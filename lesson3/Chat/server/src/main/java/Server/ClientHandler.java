package Server;

import Server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientHandler {

    //ССЫЛКИ НА СОКЕТЫ
    private Server server;
    private Socket client;

    //ВХОДНОЙ И ВЫХОДНОЙ ПОТОКИ
    private DataInputStream in;
    private DataOutputStream out;

    //ПЕРЕМЕННЫЕ ДЛЯ ХРАНЕНИЯ НИКНЕЙМА И ЛОГИНА
    private String nickname;
    private String login;

    public String getNickname() {
        return nickname;
    }

    public String getLogin() {
        return login;
    }

    //КОНСТРУКТОР
    public ClientHandler(Socket client, Server server) {
        try {
            this.client = client;
            this.server = server;
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());

            //ПОТОК ОТВЕЧАЮЩИЙ ЗА ОБМЕН ДАННЫМИ МЕЖДУ СЕРВЕРОМ И КЛИЕНТОМ
            new Thread(() -> {
                try {
                    //ТАЙМАУТ 120 СЕКУНД НА ПОПЫТКУ ВХОДА
                    client.setSoTimeout(120000);
                    //ЦИКЛ АВТОРИЗАЦИИ
                    while (true) {
                        //ПОЛУЧАЕМ СООБЩЕНИЕ ОТ СЕРВЕРА
                        String authorization = in.readUTF();

                        //ВОЗМОЖНОСТЬ ВЫХОДА ДЛЯ КЛИЕНТА
                        if (authorization.equals("/end")) {
                            throw new RuntimeException("Клиент отключен");
                        }

                        //РЕГИСТРАЦИЯ
                        if (authorization.startsWith("/reg ")) {
                            String[] token = authorization.split(" ");
                            boolean apply = server.getAuthorization().registration(token[1], token[2], token[3]);
                            if (apply) {
                                sendMessage("\n" + "Регистрация прошла успешно!");
                            } else sendMessage("Логин или Никнейм уже занят");
                        }

                        //СЧИТЫВАЕМ ИНФОРМАЦИЮ ИЗ ПОПЫТКИ ВХОДА
                        if (authorization.startsWith("/authorization ")) {
                            //РАЗБИВАЕМ СТРОКУ ЧЕРЕЗ ПРОБЕЛ
                            String[] token = authorization.split(" ");
                            //ПОЛУЧАЕМ НИКНЕЙМ ЧЕРЕЗ ЛОГИН И ПАРОЛЬ ПОЛЬЗОВАТЕЛЯ
                            String newNickname = server.getAuthorization().getNicknameByLoginAndPassword(token[1], token[2]);

                            login = token[1];

                            //В СЛУЧАЕ УСПЕШНОЙ АВТОРИЗАЦИИ ПРИСУЖДАЕМ ПОЛЬЗОВАТЕЛЮ НИКНЕЙМ
                            if (newNickname != null) {
                                if (!server.isLoginAuthorized(login)) {
                                    sendMessage("/authorizationOK " + newNickname);
                                    nickname = newNickname;
                                    server.subscribeAdd(this);
                                    System.out.println("Клиент " + nickname + " авторизовался");
                                    break;
                                } else {
                                    sendMessage("Пользователь в сети");
                                    System.out.println("Ошибка входа");
                                }
                            } else {
                                sendMessage("Неверный логин / пароль" + "\n");
                                System.out.println("Ошибка входа");
                            }
                        }
                    }

                    //ЦИКЛ РАБОТЫ
                    client.setSoTimeout(0);
                    while (true) {
                        //ПОЛУЧАЕМ СООБЩЕНИЕ ОТ СЕРВЕРА
                        String message = in.readUTF();

                        //ОБРАБОТКА СИСТЕМНЫХ СООБЩЕНИЙ
                        if (message.startsWith("/")) {
                            //ВОЗМОЖНОСТЬ ВЫХОДА ДЛЯ КЛИЕНТА
                            if (message.equals("/end")) {
                                out.writeUTF("/end");
                                break;
                            }
                            //ВОЗМОЖНОСТЬ ОТПРАВКИ ПРИВАТНОГО СООБЩЕНИЯ (НЕ РАБОТАЕТ)
                            if (message.startsWith("/w ")) {
                                //ОГРАНИЧИВАЕМ СПЛИТ ДО 3 ПРОБЕЛОВ
                                String[] token = message.split(" ", 3);
                                //ФИКСИМ ВОЗМОЖНУЮ ОШИБКУ
                                if (token.length == 3) {
                                    server.privateMessage(this, token[1], token[2]);
                                }
                            }
                            //ВОЗМОЖНОСТЬ СМЕНЫ НИКА
                            if (message.startsWith("/changeNick")){
                                String[] token = message.split(" ", 2);
                                if (token[1].contains(" ")){
                                    sendMessage("Ник не может содержать пробелов");
                                    continue;
                                }
                                if (server.getAuthorization().changeNickname(this.nickname,token[1])){
                                    sendMessage("/yourNickIs " + token[1]);
                                    sendMessage("Ваш ник изменен на " + token[1]);
                                    this.nickname = token[1];
                                    server.clientListMessage();
                                }else sendMessage("Не удалось изменить ник. " + token[1] + " уже существует");
                            }
                        } else {
                            //ВЫВОДИМ СООБЩЕНИЕ ПОЛЬЗОВАТЕЛЯ В ЧАТ
                            server.broadcastMessage(nickname, message);
                        }
                    }
                }catch (SocketTimeoutException e){
                    System.out.println("Клиент бездействовал продолжительное время, отключение");
                    sendMessage("/end");
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //В СЛУЧАЕ ВЫХОДА КЛИЕНТА, ОН УДАЛЯЕТСЯ ИЗ СПИСКА ОБРАБАТЫВАЕМЫХ КЛИЕНТОВ
                    server.subscribeDel(this);
                    System.out.println(nickname + " отключен");
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //МЕТОД ОТВЕЧАЮЩИЙ ЗА ОТПРАВКУ СООБЩЕНИЙ НА СЕРВЕР
    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
