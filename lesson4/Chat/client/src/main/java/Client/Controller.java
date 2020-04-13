package Client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public TextArea chat;
    @FXML
    public Button registrationBtn;
    @FXML
    private TextField message;
    @FXML
    private TextField password;
    @FXML
    private TextField loginField;
    @FXML
    private HBox messagePanel;
    @FXML
    private HBox authorizationPanel;
    @FXML
    private ListView<String> clientList;

    //СОКЕТ
    private Socket client;

    //ВХОДНОЙ И ВЫХОДНОЙ ПОТОК
    private DataInputStream in;
    private DataOutputStream out;

    private final String CHAT_NAME = "Чат";

    //ПЕРЕМЕННАЯ ОТВЕЧАЮЩАЯ ЗА УСПЕШНУЮ АВТОРИЗАЦИЮ
    private boolean authorizationDone;

    //ПЕРМЕННАЯ ДЛЯ ХРАНЕНИЯ НИКНЕЙМА
    private String nickname;

    //ПЕРЕМЕННАЯ ДЛЯ ХРАНЕНИЯ ЛОГИНА
    private String login;

    //ПОЛЕ ОТВЕЧАЮЩЕЕ ЗА ОКНО РЕГИСТРАЦИИ
    private Stage regStage;


    //МЕТОД ОТВЕЧАЮЩИЙ ЗА ВИЗУАЛЬНЫЕ ИЗМЕНЕНИЯ ОКНА В СЛУЧАЕ УСПЕШНОЙ АВТОРИЗАЦИИ
    public void setAuthorizationDone(boolean authorizationDone) {
        this.authorizationDone = authorizationDone;
        authorizationPanel.setVisible(!authorizationDone);
        authorizationPanel.setManaged(!authorizationDone);
        messagePanel.setVisible(authorizationDone);
        messagePanel.setManaged(authorizationDone);
        clientList.setVisible(authorizationDone);
        clientList.setManaged(authorizationDone);
        registrationBtn.setVisible(!authorizationDone);
        registrationBtn.setManaged(!authorizationDone);

        //ВЫСТАВЛЕНИЕ ЗНАЧЕНИЙ ПО-УМОЛЧАНИЮ ДЛЯ НИКНЕЙМА И НАЗВАНИЯ ОКНА
        if (!authorizationDone) {
            nickname = "";
            setTitle(CHAT_NAME);
            History.stop();
        }

        //ОЧИСТКА ЧАТА ОТ СООБЕЩНИЙ ДО ИЛИ ПОСЛЕ АВТОРИЗАЦИИ
        chat.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //ИЗНАЧАЛЬНО АВТОРИЗАЦИЯ НЕ ПРОЙДЕНА
        authorizationDone = false;

        //ОБРАБОТКА НАЖАТИЯ НА КРЕСТИК
        Platform.runLater(() -> {
            Stage stage = (Stage) chat.getScene().getWindow();
            stage.setOnCloseRequest(windowEvent -> {
                if (client != null && !client.isClosed()) {
                    try {
                        out.writeUTF("/end");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }


    //МЕТОД ОТВЕЧАЮЩИЙ ЗА ПОДКЛЮЧЕНИЕ КЛИЕНТА К СЕРВЕРУ
    public void connect() {
        try {
            //ИНИЦИАЛИЗИРУЕМ СОКЕТ, ВХОДНОЙ И ВЫХОДНОЙ ПОТОК
            //АЙПИ АДРЕС, ПОРТ И НАЗВАНИЕ ОКНА КЛИЕНТА ПО-УМОЛЧАНИЮ
            String IP = "localhost";
            int PORT = 7777;
            client = new Socket(IP, PORT);
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());

            //ПОТОК ОТВЕЧАЮЩИЙ ЗА ОБМЕН ДАННЫМИ МЕЖДУ СЕРВЕРОМ И КЛИЕНТОМ
            new Thread(() -> {
                //ЦИКЛ АВТОРИЗАЦИИ
                try {
                    while (true) {
                        //ПОЛУЧЕНИЕ СООБЩЕНИЯ ОТ СЕРВЕРА
                        String authorization = in.readUTF();

                        //ПРИСУЖДАЕМ ПОЛЬЗОВАТЕЛЮ НИК И ВЫСТАВЛЯЕМ ЗНАЧЕНИЕ АВТОРИЗАЦИИ НА УСПЕШНОЕ
                        if (authorization.startsWith("/authorizationOK")) {
                            nickname = authorization.split(" ")[1];
                            setAuthorizationDone(true);

                            //ВЫВОДИМ ИСТОРИЮ СООБЩЕНИЙ
                            chat.clear();
                            chat.appendText(History.showHistory(login));
                            //НАЧИНАЕМ ЗАПИСЬ ИСТОРИИ
                            History.start(login);

                            break;
                        }
                        chat.appendText(authorization + "\n");
                    }

                    //МЕНЯЕМ НАЗВАНИЕ ОКНА НА УНИКАЛЬНОЕ ПОЛЬЗОВАТЕЛЬСКОЕ
                    setTitle(CHAT_NAME + ". " + nickname);

                    //ЦИКЛ РАБОТЫ
                    while (true) {
                        //ПОЛУЧАЕМ СООБЩЕНИЕ ОТ СЕРВЕРА
                        String message = in.readUTF();

                        //ОБРАБОТКА СИСТЕМНЫХ СООБЩЕНИЙ
                        if (message.startsWith("/")) {
                            //ВОЗМОЖНОСТЬ ВЫХОДА ДЛЯ КЛИЕНТА
                            if (message.equals("/end")) {
                                break;
                            }
                            //ПОЛУЧЕНИЕ СПИСКА КЛИЕНТОВ
                            if (message.startsWith("/clientlist ")) {
                                String[] token = message.split(" ");
                                Platform.runLater(() -> {
                                    clientList.getItems().clear();
                                    for (int i = 1; i < token.length; i++) {
                                        clientList.getItems().add(token[i]);
                                    }
                                });
                            }
                            if (message.startsWith("/yourNickIs")){
                                nickname = message.split(" ")[1];
                                setTitle(CHAT_NAME + ". " + nickname);
                            }
                        } else {
                            //ВЫВОД СООБЕЩНИЯ ПОЛЬЗОВАТЕЛЯ В ЧАТ
                            chat.appendText(message + "\n");
                            History.writeLine(message);
                        }
                    }
                } catch (EOFException e) {
                    System.out.println("Клиент отключен");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setAuthorizationDone(false);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //МЕТОД ОТВЕЧАЮЩИЙ ЗА ОТПРАВКУ СООБЩЕНИЯ ПРИ НАЖАТИИ НА КНОПКУ ИЛИ НА ENTER
    public void sendMessage() {
        try {
            //СЧИТЫВАЕМ СООБЩЕНИЯ С ПОЛЯ ВВОДА И ОТПРАВЛЯЕМ НА СЕРВЕР
            out.writeUTF(message.getText());
            message.clear();
            message.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //МЕТОД ОТВЕЧАЮЩИЙ ЗА ПОПЫТКИ АВТОРИЗАЦИИ
    public void tryAuthorization() {
        if (client == null || client.isClosed()) {
            connect();
        }
        try {
            out.writeUTF("/authorization " + loginField.getText().trim() + " " + password.getText().trim());
            login = loginField.getText();
            loginField.clear();
            password.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //МЕТОД ОТВЕЧАЮЩИЙ ЗА ИЗМЕНЕНИЕ НАЗВАНИЯ ОКНА
    public void setTitle(String title) {
        Platform.runLater(() -> ((Stage) chat.getScene().getWindow()).setTitle(title));
    }

    public void listSendPrivate() {
        message.setText("/w " + clientList.getSelectionModel().getSelectedItem() + " ");
        message.requestFocus();
    }

    public void setAuthorizationPanel(HBox authorizationPanel) {
        this.authorizationPanel = authorizationPanel;
    }

    public Stage createRegWindow() {
        Stage stage = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/registration.fxml"));
            Parent root = fxmlLoader.load();
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);

            //ССЫЛКА НА ПОЛЕ КОНТРОЛЛЕР ВНУТРИ КЛАССА РЕГКОНТРОЛЛЕР
            RegistrationController registrationController = fxmlLoader.getController();
            registrationController.controller = this;

            stage.setTitle("Регистрация");
            stage.setScene(new Scene(root, 400, 800));
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stage;
    }

    public void registrationOpen() {
        if (regStage == null){
            regStage = createRegWindow();
        }
        regStage.show();
    }

    public void tryRegistration(String login, String password, String nickname){
        String message = String.format("/reg %s %s %s", login, password, nickname);
        if (client == null || client.isClosed()) {
            connect();
        }
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
