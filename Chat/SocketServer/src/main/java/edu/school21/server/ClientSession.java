package edu.school21.server;

import edu.school21.models.ChatRoom;
import edu.school21.models.User;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class ClientSession {
    private User user;
    private PrintWriter serverOut;
    private BufferedReader serverIn;
    private ChatRoom chatroom;

    public ClientSession(PrintWriter serverOut, BufferedReader serverIn) {
        this.serverOut = serverOut;
        this.serverIn = serverIn;
        this.chatroom = new ChatRoom();
    }

    public ClientSession(User user, PrintWriter serverOut, BufferedReader serverIn) {
        this.user = user;
        this.serverOut = serverOut;
        this.serverIn = serverIn;
        this.chatroom = new ChatRoom();
    }


    public ChatRoom getCurrentChatRoom() {
        return chatroom;
    }

    public void setCurrentChatRoom(ChatRoom chatroom) {
        this.chatroom = chatroom;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PrintWriter getServerOut() {
        return serverOut;
    }

    public void setServerOut(PrintWriter serverOut) {
        this.serverOut = serverOut;
    }

    public BufferedReader getServerIn() {
        return serverIn;
    }

    public void setServerIn(BufferedReader serverIn) {
        this.serverIn = serverIn;
    }
}

