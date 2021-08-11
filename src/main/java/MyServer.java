
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Непосредственно сервер
 */
public class MyServer {

    private List<ClientHandler> clients;
    private AuthService authService;

    public List<ClientHandler> getClients() {
        return clients;
    }

    public MyServer() {
        try (ServerSocket server = new ServerSocket(ChatConstants.PORT)) {
            authService = new DataBaseAuthService();
            authService.start();
            clients = new ArrayList<>();
            while (true) {
                System.out.println("Server is waiting for connection");
                Socket socket = server.accept(); //получение клиента (ожидаем подключение клиента)
                System.out.println("Client connected");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isNickBusy(String nick) {
        return clients.stream().anyMatch(client -> client.getName().equals(nick));
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClients();
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClients();
    }

    /**
     * Отправляет сообщения всем пользователям
     */
    public synchronized void broadcastMessage(String message) {
        clients.forEach(client -> client.sendMsg(message));
    }

    /**
     * Отправляет сообщение только одному пользователю
     */
    public synchronized void broadcastMessageToOne(String name, String message) {
        String[] parts = message.split("\\s+");
        List<String> list = new ArrayList<>(Arrays.asList(parts));
        String address = list.get(2);
        list.remove(1);
        list.remove(1);
        String messageBack = String.join(" ", list);
        clients.stream()
                .filter(clients -> clients.getName().equals(address) || clients.getName().equals(name))
                .forEach(client -> client.sendMsg(messageBack));
    }

    public synchronized void broadcastMessageToClients(String message, List<String> nicknames) {
        clients.stream().filter(c -> nicknames.contains(c.getName()))
                .forEach(c -> c.sendMsg(message));
    }

    public synchronized void broadcastClients() {
        String clientsMessage = ChatConstants.CLIENTS_LIST + " " +
                " " +
                clients.stream()
                        .map(c -> c.getName())
                        .collect(Collectors.joining(" "));
        clients.forEach(c -> c.sendMsg(clientsMessage));
    }


}
