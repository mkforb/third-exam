package com.ifmo.jjd.client;

import com.ifmo.jjd.lib.Settings;

/**
 * Created by User on 08.06.2021.
 */
public class ClientApp {
    public static void main(String[] args) {
        new Client(Settings.HOST, Settings.PORT).start();
    }
}
