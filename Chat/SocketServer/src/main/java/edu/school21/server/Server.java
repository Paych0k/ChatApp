package edu.school21.server;

import edu.school21.models.ChatRoom;
import edu.school21.models.Message;
import edu.school21.models.User;
import edu.school21.services.ChatRoomService;
import edu.school21.services.MessageService;
import edu.school21.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;


public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static final List<ClientSession> usersChats = new CopyOnWriteArrayList<>();
    public static final String ENDMESSAGE = "---END---";

    private final UserService userService;
    private final MessageService messageService;
    private final ChatRoomService chatRoomService;

    public Server(UserService userService, MessageService messageService, ChatRoomService chatRoomService) {
        this.userService = userService;
        this.messageService = messageService;
        this.chatRoomService = chatRoomService;
    }

    public void startConnection(int port) {
        LOGGER.info("Starting server on port {}", port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!LocalDateTime.now().equals(LocalDateTime.of(2030, 12, 31, 23, 59))) {
                Socket socket = serverSocket.accept();
                LOGGER.info("Server accepted connection from {}", socket.getRemoteSocketAddress());
                new Thread(() -> {
                    try {
                        handleClient(socket);
                    } catch (SocketException e) {
                        LOGGER.error("Socket exception", e);
                    }
                }).start();
            }
        } catch (SocketException e) {
            LOGGER.error("Error occurred while running the server: ", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleClient(Socket socket) throws SocketException {
        ClientSession session = null;
        try {
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
            session = new ClientSession(serverOut, serverIn);

            String supMessage = """
                    Hello from Server!
                    1. signIn
                    2. signUp
                    3. exit
                    """;
            serverOut.print(supMessage);
            serverOut.println(ENDMESSAGE);
            serverOut.flush();

            String message = serverIn.readLine();

            if ("1".equals(message)) {
                handleSignIn(session);
            } else if ("2".equals(message)) {
                handleSignUp(session);
            } else if ("3".equals(message)) {
                System.out.println("You have left the chat.");
                socket.close();
            } else {
                serverOut.println("Invalid action");
                socket.close();
            }
        } catch (IOException e) {
            LOGGER.error("Error handling client: {}", e.getMessage());
        } finally {
            if (session != null) {
                usersChats.remove(session);
            }
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.error("Error closing socket: ", e);
            }
        }
    }

    private void handleSignUp(ClientSession session) throws IOException {
        PrintWriter serverOut = session.getServerOut();
        BufferedReader serverIn = session.getServerIn();

        serverOut.println("Enter username:");
        serverOut.flush();

        String username = serverIn.readLine();

        serverOut.println("Enter password:");
        serverOut.flush();

        String password = serverIn.readLine();

        Optional<User> user = userService.registerUser(username, password);

        if (user.isPresent()) {
            serverOut.println("Successful!");
            session.setUser(user.get());
        } else {
            serverOut.println("Username already exists");
            LOGGER.warn("Attempt to register existing username: {}", username);
        }
        serverOut.flush();
    }

    private void handleSignIn(ClientSession session) throws IOException {
        session.getServerOut().println("Enter username:");
        session.getServerOut().flush();
        String username = session.getServerIn().readLine();

        session.getServerOut().println("Enter password:");
        session.getServerOut().flush();
        String password = session.getServerIn().readLine();

        Optional<User> user = userService.loginUser(username, password);

        if (user.isPresent()) {
            session.setUser(user.get());
            usersChats.add(session);
            session.getServerOut().println("Successful!");
            session.getServerOut().flush();

            LOGGER.info("User {} signed in successfully", username);

            boolean isOk = chatRoomHandler(session);
            if(isOk)
                chatHandler(session);
            else{

            }
        } else {
            session.getServerOut().println("Username or password is incorrect");
            session.getServerOut().flush();
            LOGGER.warn("Failed sign in attempt for username: {}", username);
        }
    }


    private boolean chatRoomHandler(ClientSession session) throws IOException {
        PrintWriter serverOut = session.getServerOut();
        BufferedReader serverIn = session.getServerIn();

        String chatRoomMessage = """
                1.\tCreate room
                2.\tChoose room
                3.\tExit
                """;
        serverOut.print(chatRoomMessage);
        serverOut.println(ENDMESSAGE);
        serverOut.flush();
        String input = serverIn.readLine();
        if ("1".equals(input)) {
            createRoom(session);
        } else if ("2".equals(input)) {
            chooseRoom(session);
        } else if ("3".equals(input)) {
            return false;
        }
        return true;
    }


    private void createRoom(ClientSession session) throws IOException {
        session.getServerOut().println("Enter room name: ");
        session.getServerOut().flush();
        String roomName = session.getServerIn().readLine();

        chatRoomService.createChatRoom(roomName, session.getUser().getId());
        session.getServerOut().println("Room " + roomName + " created.");
        session.getServerOut().flush();
    }

    private void chooseRoom(ClientSession session) throws IOException {
        PrintWriter serverOut = session.getServerOut();
        BufferedReader serverIn = session.getServerIn();

        List<ChatRoom> chatRooms = chatRoomService.getAllChatRooms();
        if (chatRooms.isEmpty()) {
            serverOut.println("No rooms available.");
            serverOut.println(ENDMESSAGE);
            serverOut.flush();
            return;
        }

        serverOut.println("Available rooms:");
        for (int i = 0; i < chatRooms.size(); i++) {
            serverOut.println((i + 1) + ". " + chatRooms.get(i).getName());
        }
        serverOut.println("Enter room number:");
        serverOut.println(ENDMESSAGE);
        serverOut.flush();

        String input = serverIn.readLine();

        if (input == null || input.trim().isEmpty()) {
            serverOut.println("Invalid input. Please enter a valid room number.");
            serverOut.flush();
            return;
        }

        try {
            int roomNumber = Integer.parseInt(input.trim());
            if (roomNumber < 1 || roomNumber > chatRooms.size()) {
                serverOut.println("Invalid room number.");
                serverOut.flush();
                return;
            }

            ChatRoom selectedRoom = chatRooms.get(roomNumber - 1);
            session.setCurrentChatRoom(selectedRoom);
            chatRoomService.addParticipant(session.getUser(), selectedRoom);

            serverOut.println("Joined room: " + selectedRoom.getName());
            serverOut.println(ENDMESSAGE);
            serverOut.flush();

            List<Message> recentMessages = messageService.getRecentMessages(selectedRoom.getId(), 30);
            for (Message msg : recentMessages) {
                String username = userService.getUserById(msg.getUsernameId())
                        .map(User::getUsername)
                        .orElse("NoName");
                serverOut.println(username + ": " + msg.getText());
            }
            serverOut.println(ENDMESSAGE);
            serverOut.flush();
        } catch (NumberFormatException e) {
            serverOut.println("Invalid input. Please enter a valid room number.");
            serverOut.flush();
        }
    }


    private void chatHandler(ClientSession session) throws IOException {
        BufferedReader serverIn = session.getServerIn();
        User user = session.getUser();
        String username = user.getUsername();
        ChatRoom currentRoom = session.getCurrentChatRoom();

        try {
            String inputMessage;
            while ((inputMessage = serverIn.readLine()) != null && !inputMessage.equalsIgnoreCase("exit")) {
                if (inputMessage.trim().isEmpty()) continue;

                broadcastMessage(username + ": " + inputMessage, currentRoom);
                messageService.saveMessage(inputMessage, user.getId(), currentRoom.getId(), LocalDateTime.now());
                usersChats.removeIf(s -> s.getServerOut().checkError());
            }
        } finally {
            usersChats.remove(session);
            broadcastMessage(username + " left the chat", currentRoom);
        }
    }


    private void broadcastMessage(String message, ChatRoom chatRoom) {
        for (ClientSession session : usersChats) {
            if (session.getCurrentChatRoom() != null && session.getCurrentChatRoom().getId().equals(chatRoom.getId())) {
                PrintWriter writer = session.getServerOut();
                writer.println(message);
                writer.flush();
            }
        }
    }
}
