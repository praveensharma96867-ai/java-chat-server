package com.example.javainteractivechat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Optional;

public class ChatClientGUI extends Application {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    private TextArea chatArea;
    private TextField inputField;
    private Button sendButton;
    private ListView<String> userList;

    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String username;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Java Interactive Chat");

        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Welcome");
        dialog.setHeaderText("Choose your Chat Identity");
        dialog.setContentText("Enter your username:");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().trim().isEmpty()) {
            username = result.get().trim();
        } else {
            System.exit(0);
        }

        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);

        inputField = new TextField();
        inputField.setPromptText("Type a message here...");

        sendButton = new Button("Send");
        sendButton.setPrefWidth(80);

        userList = new ListView<>();
        userList.setPrefWidth(120);
        Label userListLabel = new Label("Online Users");
        VBox sidebar = new VBox(5, userListLabel, userList);
        sidebar.setPadding(new Insets(10));

        HBox bottomRow = new HBox(10, inputField, sendButton);
        HBox.setHgrow(inputField, javafx.scene.layout.Priority.ALWAYS);
        bottomRow.setPadding(new Insets(10));

        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(chatArea);
        mainLayout.setRight(sidebar);
        mainLayout.setBottom(bottomRow);

        sendButton.setOnAction(e -> sendMessage());
        inputField.setOnAction(e -> sendMessage());

        connectToServer();

        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(e -> {
            if (out != null) out.println("/exit");
            System.exit(0);
        });

        primaryStage.show();
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(username);

            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        String finalMessage = message;
                        Platform.runLater(() -> {
                            if (finalMessage.startsWith("USERLIST_UPDATE:")) {
                                updateOnlineUsers(finalMessage);
                            } else {
                                chatArea.appendText(finalMessage + "\n");
                            }
                        });
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> chatArea.appendText("Connection lost to server.\n"));
                }
            }).start();

        } catch (IOException e) {
            chatArea.appendText("Could not connect to server. Is it running?\n");
            inputField.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            out.println(text);
            chatArea.appendText("[You]: " + text + "\n");
            inputField.clear();
        }
    }

    private void updateOnlineUsers(String serverPayload) {
        userList.getItems().clear();
        String namesRaw = serverPayload.replace("USERLIST_UPDATE:", "");
        if (!namesRaw.isEmpty()) {
            String[] names = namesRaw.split(",");
            userList.getItems().addAll(names);
        }
    }
}