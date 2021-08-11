import java.sql.SQLException;

/**
 * Сервис авторизации
 */
public interface AuthService {
    /**
     * запустить сервис
     */
    void start();

    /**
     * остановить сервис
     */
    void stop();

    /**
     * получить ник
     */
    String getNickByLoginAndPass(String login, String pass) throws SQLException;

    void updateNick(String newName, String oldName);

    boolean isNickBusy(String nick);
}