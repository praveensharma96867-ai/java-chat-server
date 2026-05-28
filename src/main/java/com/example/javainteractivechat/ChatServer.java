package com.example.javainteractivechat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        String portEnv = System.getenv("PORT");
        int port = (portEnv != null) ? Integer.parseInt(portEnv) : 12345;

        System.out.println("Chat Server running on port " + port + "...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(socket);
                clientHandlers.add(client);
                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message, ClientHandler excludeUser) {
        for (ClientHandler client : clientHandlers) {
            if (client != excludeUser) {
                client.sendMessage(message);
            }
        }
    }

    public static void broadcastUserList() {
        StringBuilder sb = new StringBuilder("USERLIST_UPDATE:");
        for (ClientHandler client : clientHandlers) {
            if (client.getClientName() != null) {
                sb.append(client.getClientName()).append(",");
            }
        }
        String payload = sb.toString();
        for (ClientHandler client : clientHandlers) {
            client.sendMessage(payload);
        }
    }

    public static void removeClient(ClientHandler client) {
        clientHandlers.remove(client);
        broadcastUserList();
    }
}