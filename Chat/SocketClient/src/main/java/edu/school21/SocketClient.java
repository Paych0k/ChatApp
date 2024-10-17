package edu.school21;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketClient {
    private static String SERVER_IP;

    public static void main(String[] args) {
        int serverPort = 8081;

        loadServerConfig();

        for (String arg : args) {
            if (arg.startsWith("--server-port=")) {
                serverPort = Integer.parseInt(arg.substring("--server-port=".length()));
            }
        }

        clientConnect(serverPort);
    }

    private static void loadServerConfig() {
        Properties properties = new Properties();
        try (InputStream input = SocketClient.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new FileNotFoundException("Файл config.properties не найден в classpath");
            }
            properties.load(input);
            SERVER_IP = properties.getProperty("server.ip");
            if (SERVER_IP == null || SERVER_IP.isEmpty()) {
                throw new IllegalArgumentException("Не удалось найти значение server.ip в файле config.properties");
            }
        } catch (IOException e) {
            System.err.println("Не удалось загрузить конфигурацию: " + e.getMessage());
            SERVER_IP = "127.0.0.1";
        }
    }

    private static void clientConnect(int serverPort) {
        try (Socket socket = new Socket(SERVER_IP, serverPort);
             PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            multiLineReader(serverIn);

            System.out.print("> ");
            String userInput = stdIn.readLine();
            serverOut.println(userInput);

            String response = serverIn.readLine();
            System.out.println(response);

            if ("1".equalsIgnoreCase(userInput) || "2".equalsIgnoreCase(userInput)) {
                response = handleSignUpOrSignIn(serverOut, serverIn, stdIn);
                System.out.println(response);
            }

            if ("1".equalsIgnoreCase(userInput) && "Successful!".equalsIgnoreCase(response)) {
                handleMessaging(serverOut, serverIn, stdIn);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void multiLineReader(BufferedReader serverIn) throws IOException {
        String line;
        while ((line = serverIn.readLine()) != null) {
            if (line.equals("---END---")) {
                break;
            }
            System.out.println(line);
        }
    }

    private static String handleSignUpOrSignIn(PrintWriter serverOut, BufferedReader serverIn, BufferedReader stdIn) throws Exception {
        String userInput = stdIn.readLine();
        serverOut.println(userInput);
        System.out.println(serverIn.readLine());

        String userInput2 = stdIn.readLine();
        serverOut.println(userInput2);
        return serverIn.readLine();
    }

    private static void handleChatRoom(PrintWriter serverOut, BufferedReader serverIn, BufferedReader stdIn) throws Exception {
        multiLineReader(serverIn);
        System.out.print("> ");
        String userInput = stdIn.readLine();
        serverOut.println(userInput);

        if ("2".equals(userInput)) {
            multiLineReader(serverIn);
            System.out.print("> ");
            String roomNumber = stdIn.readLine();
            serverOut.println(roomNumber);

            String serverResponse = serverIn.readLine();
            System.out.println(serverResponse);

            if (serverResponse.startsWith("Joined room:")) {
                System.out.println("Start messaging");
            } else {
                System.out.println("Failed to join room.");
            }
        } else if ("1".equals(userInput)) {
            String serverResponse = serverIn.readLine();
            System.out.println(serverResponse);

            String roomName = stdIn.readLine();
            serverOut.println(roomName);

            serverResponse = serverIn.readLine();
            System.out.println(serverResponse);
        } else if ("3".equals(userInput)) {
            System.out.println("Exiting...");
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void handleMessaging(PrintWriter serverOut, BufferedReader serverIn, BufferedReader stdIn) throws Exception {
        handleChatRoom(serverOut, serverIn, stdIn);

        multiLineReader(serverIn);

        System.out.println("Start messaging");
        AtomicBoolean isConnectionClosed = new AtomicBoolean(false);
        Thread serverListener = new Thread(() -> {
            try {
                String input;
                while ((input = serverIn.readLine()) != null) {
                    System.out.println(input);
                }
            } catch (IOException e) {
                isConnectionClosed.set(true);
            }
        });
        serverListener.start();

        String userInput;
        while ((userInput = stdIn.readLine()) != null) {
            if ("exit".equalsIgnoreCase(userInput) || isConnectionClosed.get()) {
                System.out.println("Disconnecting from the server...");
                break;
            }
            serverOut.println(userInput);
        }

        serverListener.join();
    }
}
