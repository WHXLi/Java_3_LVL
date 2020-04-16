package Server;

public interface Authorization {
    String getNicknameByLoginAndPassword(String login, String password);

    boolean registration(String login, String password, String nickname);

    boolean changeNickname(String oldNickname, String newNickname);

}
