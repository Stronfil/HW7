package server;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server extends Application {
    private ServerSocket server;
    private final List<ClientHandler> clients;
    private final AuthService authService;

    public Server() {
        clients = new CopyOnWriteArrayList<>();
        authService = new SimpleAuthService();

        try {
            int PORT = 7373;
            server = new ServerSocket(PORT);
            System.out.println("Server started");

            while (true) {
                Socket socket = server.accept();
                System.out.println("Client connected");
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    public void broadcastMsg(ClientHandler clientHandler, String msg){
        String message = String.format("[ %s ]: %s", clientHandler.getNickname(), msg);
        for (ClientHandler c : clients) {
            c.sendMsg(message);
        }
    }
    public void privateMsg(ClientHandler sender,String receiver, String msg){
        String message = String.format("[ %s ] to [ %s ]: %s", sender.getNickname(),receiver, msg);
        for (ClientHandler c : clients) {
            if(c.getNickname().equals(receiver)) {
                c.sendMsg(message);
                if(!c.equals(sender)){
                    sender.sendMsg(message);
                }
                return;
            }

        }
        sender.sendMsg(String.format("User %s not found ", receiver ));
    }

    void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
    }

    void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
    }

    public AuthService getAuthService() {
        return authService;
    }
}
