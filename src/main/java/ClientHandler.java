package com.example.javainteractivechat;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            clientName = in.readLine();

            ChatServer.broadcast(">>> " + clientName + " entered the room! <<<", this);
            ChatServer.broadcastUserList();

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/exit")) {
                    break;
                }
                ChatServer.broadcast("[" + clientName + "]: " + message, this);
            }
        } catch (IOException e) {
            // Handled disconnect
        } finally {
            try {
                ChatServer.removeClient(this);
                ChatServer.broadcast("<<< " + clientName + " left the room. <<<", this);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getClientName() {
        return this.clientName;
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}