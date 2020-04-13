package com.geekbrains;

import java.sql.*;

public class App {
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatementInsert;

    public static void main(String[] args) {
        try {
            connect();
            System.out.println("Подключилось");
            clearDB();
            prepareAllStatement();
            batchFillTable();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:main.db");
        statement = connection.createStatement();
    }

    public static void disconnect() {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //ОПТИМИЗАЦИЯ ЗАПРОСА
    public static void prepareAllStatement() throws SQLException {
        preparedStatementInsert = connection.prepareStatement("INSERT INTO students (name,score) VALUES (?,?);");
    }

    public static void batchFillTable() throws SQLException {
        //ОТКЛЮЧАЕМ КОММИТЫ И УСКОРЯЕМ ВНЕСЕНИЕ В БАЗУ ДАННЫХ
        connection.setAutoCommit(false);

        long a = System.currentTimeMillis();
        for (int i = 1; i <= 1000; i++) {
            preparedStatementInsert.setString(1,"Bob" + i);
            preparedStatementInsert.setInt(2, i * 3 % 100);
            preparedStatementInsert.addBatch();
        }
        preparedStatementInsert.executeBatch();
        connection.setAutoCommit(true); //ВКЛЮЧАЕМ ОБРАТНО И ДЕЛАЕМ КОММИТ ВСЕХ ДАННЫХ СРАЗУ
        System.out.println("Время заполнения таблицы: " + (System.currentTimeMillis() - a) + "ms");
    }

    //ОТКАТ
    public static void rollbackEx() throws SQLException {
        statement.executeUpdate("INSERT INTO students(name,score) VALUES ('Bob1',80);");
        //ТОЧКА СОХРАНЕНИЯ - ВЫРУБАЕТ АВТОКОММИТ
        Savepoint savepoint1 = connection.setSavepoint();
        statement.executeUpdate("INSERT INTO students(name,score) VALUES ('Bob2',80);");
        //ОТКАТ ДО ТОЧКИ СОХРАНЕНИЯ
        connection.rollback(savepoint1);
        statement.executeUpdate("INSERT INTO students(name,score) VALUES ('Bob3',80);");
        connection.setAutoCommit(true);
    }

    /*CRUD: create-read-update-delete ^_^*/
    //Create
    static void insertEx() throws SQLException {
        statement.executeUpdate("INSERT INTO students(name,score) VALUES ('John',80);");
    }
    //Update
    static void updateEx() throws SQLException {
        statement.executeUpdate("UPDATE students SET score = 100 WHERE name 'Bob'; ");
    }
    //Delete
    static void deleteEx() throws SQLException {
        statement.executeUpdate("DELETE FROM students WHERE id = 2;");
    }
    //Delete
    static void clearDB() throws SQLException {
        statement.executeUpdate("DELETE FROM students;");
    }
    //Read
    static void readDB() throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT name ,score FROM students;");
        while (resultSet.next()) {
            System.out.println(resultSet.getString("name") + " " + resultSet.getInt("score"));
        }
    }

   /* CREATE TABLE students (
            id    INTEGER PRIMARY KEY AUTOINCREMENT,
            name  TEXT,
            score INTEGER
    );*/

}
