package com.example.javainteractivechat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    // NEW SMART CODE
    public static void main(String[] args) {
        String portEnv = System.getenv("PORT");
        int port = (portEnv != null) ? Integer.parseInt(portEnv) : 12345;

        System.out.println("Chat Server running on port " + port + "...");
        try (ServerSocket serverSocket = new ServerSocket(port)) { // <-- It uses the dynamic port now!
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
