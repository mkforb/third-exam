package com.ifmo.jjd.server;

import com.ifmo.jjd.lib.Connection;
import com.ifmo.jjd.lib.Message;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by User on 08.06.2021.
 */
public class Server {
    private final int port;
    private final List<Connection> connections = new ArrayList<>();
    private final ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<>(10);

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен");
            // Поток, который будет читать очередь и рассылать сообщения клиентам
            new Thread(new Reader(connections, queue), "ReaderThread").start();
            while (true) {
                Socket newClient = serverSocket.accept();
                Connection connection = new Connection(newClient);
                connections.add(connection);
                new Thread(new Writer(connection)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Writer implements Runnable {
        private final Connection connection;

        public Writer(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " started");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Message message = connection.readMessage();
                    System.out.println(Thread.currentThread().getName() + ": " + message);
                    if (!message.getText().isEmpty()) {
                        queue.put(message);
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                    // Клиент отключился?
                    connections.remove(connection);
                    Thread.currentThread().interrupt();
                } catch (EOFException e) {
                    e.printStackTrace();
                    // End of stream
                    connections.remove(connection);
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    connections.remove(connection);
                    Thread.currentThread().interrupt();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + " stopped");
        }
    }

    private static class Reader implements Runnable {
        private final List<Connection> connections;
        private final ArrayBlockingQueue<Message> queue;

        public Reader(List<Connection> connections, ArrayBlockingQueue<Message> queue) {
            this.connections = connections;
            this.queue = queue;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " started");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Message message = queue.take();
                    System.out.println(Thread.currentThread().getName() + ": " + message);
                    // Отправить сообщение
                    for (Connection connection : connections) {
                        if (connection.getSender().equals(message.getSender())) continue;
                        connection.sendMessage(message);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
