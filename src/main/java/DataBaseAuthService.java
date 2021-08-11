import java.sql.*;

public class DataBaseAuthService implements AuthService {

    private static Connection connection;
    private static Statement stmt;
    private static PreparedStatement preparedStatement;

    //main тоже не использую в чатике, но для управления бд он мне нужен
//    public static void main(String[] args) {
//        DataBaseAuthService dataBaseAuthService = new DataBaseAuthService();
//        try {
//            dataBaseAuthService.connect();
//            dataBaseAuthService.read();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            dataBaseAuthService.disconnect();
//        }
//    }

    private void connect() throws SQLException {
        connection = DriverManager.getConnection(ChatConstants.DATABASE_URL);
        stmt = connection.createStatement();
    }

    private void disconnect() {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Никак не использую в чатике, но оставлю, пусть будет, для управления бд это мне нужно
    private void createTable() throws SQLException {
        String createTable = "" +
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nick VARCHAR(15), " +
                "login VARCHAR(15), " +
                "pass VARCHAR(20))";
        stmt.execute(createTable);
    }

    private void dropTable() throws SQLException {
        String dropTable = "DROP TABLE IF EXISTS users";
        stmt.execute(dropTable);
    }

    private void read() throws SQLException {
        try (ResultSet rs = stmt.executeQuery("SELECT * FROM users;")) {
            while (rs.next()) {
                System.out.println(rs.getInt(1) +
                        " " + rs.getString("nick") +
                        " " + rs.getString("login") +
                        " " + rs.getString("pass")
                );
            }
        }
    }

    private void clearTable() throws SQLException {
        String clearTable = "DELETE FROM users";
        stmt.execute(clearTable);
    }

    private void insert(String nick, String login, String pass) throws SQLException {
        String insert = "INSERT INTO users (nick, login, pass) VALUES (?, ?, ?)";
        preparedStatement = connection.prepareStatement(insert);
        preparedStatement.setString(1, nick);
        preparedStatement.setString(2, login);
        preparedStatement.setString(3, pass);
        preparedStatement.execute();
    }


    private void delete(String nick) throws SQLException {
        String delete = "DELETE FROM users WHERE nick = ?";
        preparedStatement = connection.prepareStatement(delete);
        preparedStatement.setString(1, nick);
        preparedStatement.execute();

    }

    public void updateNick(String newName, String oldName) {
        String updateNick = "UPDATE users SET nick = ? WHERE nick = ?";
        try {
            preparedStatement = connection.prepareStatement(updateNick);
            preparedStatement.setString(1, newName);
            preparedStatement.setString(1, oldName);
            preparedStatement.execute();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public boolean isNickBusy(String nick) {
        String isNickBusy = "SELECT nick FROM users WHERE nick = ?";
        try {
            preparedStatement = connection.prepareStatement(isNickBusy);
            preparedStatement.setString(1, nick);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try (ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                System.out.println(rs.getString(1));
                return true;
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return false;
    }


    private String takeNick(String login, String pass) throws SQLException {
        String takeNick = "SELECT nick FROM users WHERE login = ? AND pass = ?";
        preparedStatement = connection.prepareStatement(takeNick);
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, pass);
        try (ResultSet rs = preparedStatement.executeQuery()) {
            return rs.getString("nick");
        }
    }


    @Override
    public void start() {
        try {
            connect();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        System.out.println(this.getClass().getName() + " server started");
    }

    @Override
    public void stop() {
        System.out.println(this.getClass().getName() + " server stopped");
        disconnect();
    }

    @Override
    public String getNickByLoginAndPass(String login, String pass) throws SQLException {
        return takeNick(login, pass);
    }

}