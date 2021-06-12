package com.ifmo.jjd.server;

import com.ifmo.jjd.lib.Settings;

/**
 * Created by User on 08.06.2021.
 */
public class ServerApp {
    public static void main(String[] args) {
        new Server(Settings.PORT).start();
    }
}
