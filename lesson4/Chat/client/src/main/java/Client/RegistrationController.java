package Client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistrationController {

    Controller controller;

    @FXML
    public Button applyBtn;
    @FXML
    public TextField nicknameField;
    @FXML
    public PasswordField passwordField1;
    @FXML
    public PasswordField passwordField2;
    @FXML
    public TextField loginField;

    public void applyReg() {
        String login = loginField.getText().trim();
        String password1 = passwordField1.getText().trim();
        String password2 = passwordField2.getText().trim();
        String nickname = nicknameField.getText().trim();

        //ПРОВЕРКА КОРРЕКТНОСТИ ПАРОЛЕЙ
        if (!password1.equals(password2)){
            controller.chat.setText("Пароли не совпадают" + "\n");
            passwordField1.clear();
            passwordField2.clear();
        }
        //ПРОВЕРКА НА СОДЕРЖАНИЕ ПРОБЕЛОВ В ЛОГИНЕ ИЛИ ПАРОЛЕ
        else if (login.contains(" ")){
            controller.chat.setText("Логин не может содержать пробелы" + "\n");
        }
        else if (password1.contains(" ") || password2.contains(" ")){
            controller.chat.setText("Пароль не может содержать пробелы" + "\n");
        }
        //ПАРОЛЬ НЕ МОЖЕТ БЫТЬ МЕНЬШЕ 6 СИМВОЛОВ
        else if (password1.length() < 6){
            controller.chat.setText("Пароль не может быть меньше 6 символов" + "\n");
        }
        //ЕСЛИ ВСЁ ВПОРЯДКЕ ДАЕМ ДОСТУП К РЕГИСТРАЦИИ
        else {
            controller.tryRegistration(login,password1,nickname);
        }
        //ЗАКРЫВАЕМ ОКНО РЕГИСТРАЦИИ
        ((Stage) applyBtn.getScene().getWindow()).close();
    }
}
