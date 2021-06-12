package com.ifmo.jjd.lib;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by User on 08.06.2021.
 */
public class Connection implements AutoCloseable {
    private final Socket socket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private String sender;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }

    public String getSender() {
        return sender;
    }

    public void sendMessage(Message message) throws IOException {
        message.setDateTime();
        output.writeObject(message);
        output.flush();
    }

    public Message readMessage() throws IOException, ClassNotFoundException {
        Message message = (Message) input.readObject();
        sender = message.getSender();
        return message;
    }

    @Override
    public void close() throws Exception {
        input.close();
        output.close();
        socket.close();
    }
}
