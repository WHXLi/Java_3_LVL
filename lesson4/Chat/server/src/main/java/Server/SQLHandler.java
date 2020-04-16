package Server;

import java.sql.*;

public class SQLHandler {
    private static Connection connection;
    private static PreparedStatement psGetNickname;
    private static PreparedStatement psRegistration;
    private static PreparedStatement psChangeNickname;

    //ПОДКЛЮЧАЕМСЯ
    public static boolean connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");
            prepareAllStatements();
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //ОТКЛЮЧАЕМСЯ
    public static void disconnect(){
        try {
            psRegistration.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            psGetNickname.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            psChangeNickname.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //ПОДГОТОВКА ЗАПРОСОВ
    private static void prepareAllStatements() throws SQLException {
        psGetNickname = connection.prepareStatement("SELECT nickname FROM users WHERE login = ? AND password = ?;");
        psRegistration = connection.prepareStatement("INSERT INTO users (login,password,nickname) VALUES (?,?,?);");
        psChangeNickname = connection.prepareStatement("UPDATE users SET nickname = ? WHERE nickname = ?;");
    }

    //ПОЛУЧАЕМ НИК
    public  static  String getNicknameByLoginAndPassword(String login,String password){
        String nick = null;
        try {
            psGetNickname.setString(1,login);
            psGetNickname.setString(2,password);
            ResultSet resultSet = psGetNickname.executeQuery();
            if (resultSet.next()){
                nick = resultSet.getString(1);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nick;
    }

    //РЕГИСТРАЦИЯ
    public static boolean registration (String login, String password, String nickname){
        try {
            psRegistration.setString(1,login);
            psRegistration.setString(2,password);
            psRegistration.setString(3,nickname);
            psRegistration.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //СМЕНА НИКА
    public static boolean changeNickname (String oldNickname, String newNickname){
        try {
            psChangeNickname.setString(1,newNickname);
            psChangeNickname.setString(2,oldNickname);
            psChangeNickname.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
