package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {

    //СПИСОК КЛИЕНТОВ
    private Vector<ClientHandler> clients;

    //ССЫЛКА НА ИНТЕРФЕЙС АВТОРИЗАЦИИ
    private Authorization authorization;


    //ГЕТТЕР
    public Authorization getAuthorization() {
        return authorization;
    }

    public Server() {
        //СОКЕТЫ
        ServerSocket server = null;
        Socket client = null;

        //ПОРТ
        final int PORT = 7777;

        //ИНИЦИАЛИЗАЦИЯ СПИСКА КЛИЕНТОВ И АВТОРИЗАЦИИ
        clients = new Vector<>();
        if (!SQLHandler.connect()){
            throw  new RuntimeException("Не удалось подключиться к БД");
        }
        authorization = new DBAuthorization();

        try {
            //СОЗДАЕМ СЕРВЕР
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен, ожидание подключения...");

            while (true) {
                //ОЖИДАЕМ ПОДКЛЮЧЕНИЯ КЛИЕНТА
                client = server.accept();
                System.out.println("Клиент подключен: " + client.getLocalSocketAddress());

                //СОЗДАЕМ ЭКЗЕМПЛЯР КЛАССА ОТВЕЧАЮЩЕГО ЗА РАБОТУ С ПОДКЛЮЧЕННЫМ КЛИЕНТОМ
                new ClientHandler(client, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SQLHandler.disconnect();
            try {
                assert client != null;
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //МЕТОД ДЛЯ МАССОВОЙ ОТПРАВКИ СООБЩЕНИЙ
    public void broadcastMessage(String nickname ,String message ) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(nickname + ": " + message);
        }
    }

    //МЕТОД ДЛЯ ПРИВАТНОЙ ОТПРАВКИ СООБЩЕНИЙ
    public void privateMessage(ClientHandler sender, String receiver, String message){
        //ВЫСТРАИВАЕМ ФОРМАТ ВЫВОДИМОГО СООБЩЕНИЯ
        String newMessage = String.format("от [%s] : %s", sender.getNickname(),message);

        if (sender.getNickname().equals(receiver)){
            sender.sendMessage(message);
            return;
        }

        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getNickname().equals(receiver)){
                clientHandler.sendMessage(newMessage);
                sender.sendMessage(newMessage);
                return;
            }
        }

        sender.sendMessage("Получатель не найден: " + receiver);
    }

    public void clientListMessage(){
        StringBuilder stringBuilder = new StringBuilder("/clientlist ");
        for (ClientHandler clientHandler: clients) {
            stringBuilder.append(clientHandler.getNickname()).append(" ");
        }
        String message = stringBuilder.toString();
        for (ClientHandler clientHandler : clients){
            clientHandler.sendMessage(message);
        }
    }

    //МЕТОД ДОБАВЛЕНИЯ КЛИЕНТА В КЛАСС ОТВЕЧАЮЩИЙ ЗА РАБОТУ С ПОДКЛЮЧЕННЫМ КЛИЕНТОМ
    public void subscribeAdd(ClientHandler clientHandler) {
        clients.add(clientHandler);
        clientListMessage();
    }

    //МЕТОД УДАЛЕНИЯ КЛИЕНТА В КЛАСС ОТВЕЧАЮЩИЙ ЗА РАБОТУ С ПОДКЛЮЧЕННЫМ КЛИЕНТОМ
    public void subscribeDel(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        clientListMessage();
    }

    //ПРОВЕРЯЕМ ЗАШЕЛ ЛИ ПОЛЬЗОВАТЕЛЬ С КОНКРЕТНЫМ ЛОГИНОМ
    public boolean isLoginAuthorized(String login){
        for (ClientHandler clientHandler: clients) {
            if (clientHandler.getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }
}
