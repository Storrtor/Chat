import java.util.List;

/**
 * Простейшая реализация сервиса аутентификации, которая работает на встроенном спике
 */
public class BaseAuthService implements AuthService {
    private class Entry {
        private final String nick;
        private final String login;
        private final String pass;

        public Entry(String nick, String login, String pass) {
            this.nick = nick;
            this.login = login;
            this.pass = pass;
        }
    }

    private List<Entry> entries;

    public BaseAuthService() {
        entries = List.of(
                new Entry("nick1", "login1", "pass1"),
                new Entry("nick2", "login2", "pass2"),
                new Entry("nick3", "login3", "pass3")
        );
    }

    @Override
    public void start() {
        System.out.println(this.getClass().getName() + " server started");
    }

    @Override
    public void stop() {
        System.out.println(this.getClass().getName() + " server stopped");
    }

    @Override
    public String getNickByLoginAndPass(String login, String pass) {
        return entries.stream()
                .filter(entry -> entry.login.equals(login) && entry.pass.equals(pass))
                .map(entry -> entry.nick)
                .findFirst().orElse(null);
    }


    @Override
    public void updateNick(String newName, String oldName) {

    }

    @Override
    public boolean isNickBusy(String s) {
        return false;
    }

}
