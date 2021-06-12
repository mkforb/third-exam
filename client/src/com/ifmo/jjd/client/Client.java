package com.ifmo.jjd.client;

import com.ifmo.jjd.lib.Connection;
import com.ifmo.jjd.lib.Message;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by User on 08.06.2021.
 */
public class Client {
    private final String ip;
    private final int port;
    private final Scanner scanner;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Введите имя");
        String userName = scanner.nextLine();
        String text;
        try (Connection connection = new Connection(new Socket(ip, port))) {
            new Thread(new Reader(connection), userName + "-Client").start();
            // Отправить пустое сообщение, чтобы зарегистрироваться на сервере
            connection.sendMessage(Message.getInstance(userName, ""));
            while (true) {
                System.out.println("Введите текст сообщения");
                text = scanner.nextLine();
                if (text.isEmpty()) continue;
                if (text.equals("exit")) break;
                connection.sendMessage(Message.getInstance(userName, text));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Reader implements Runnable {
        private final Connection connection;

        public Reader(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " started");
            while (true) {
                try {
                    Message message = connection.readMessage();
                    System.out.println(message);
                } catch (SocketException e) {
                    e.printStackTrace();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + " stopped");
        }
    }
}
