package Client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class History {
    //ЗАПИСЬ ТЕКСТОВЫХ ВХОДНЫХ ДАННЫХ
    private static PrintWriter out;

    private static String getHistoryFileNameByLogin (String login) { return "history/history_" + login + ".txt"; }

    public static void start(String login){
        try {
            out = new PrintWriter(new FileOutputStream(getHistoryFileNameByLogin(login),true), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void stop(){
        if (out != null){
            out.close();
        }
    }

    public static void writeLine(String message){ out.println(message); }

    public static String showHistory(String login){
        int historySize = 100;
        if (!Files.exists(Paths.get(getHistoryFileNameByLogin(login)))){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            List<String> history = Files.readAllLines(Paths.get(getHistoryFileNameByLogin(login)));
            int startPosition = 0;
            if (history.size() > historySize){
                startPosition = historySize;
            }
            for (int i = startPosition; i < history.size(); i++) {
                stringBuilder.append(history.get(i)).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
