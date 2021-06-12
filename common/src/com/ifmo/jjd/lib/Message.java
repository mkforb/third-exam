package com.ifmo.jjd.lib;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by User on 08.06.2021.
 */
public class Message implements Serializable {
    private final String sender;
    private final String text;
    private LocalDateTime dateTime;

    public Message(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime() {
        this.dateTime = LocalDateTime.now();
    }

    public String getDateTimeFormatted() {
        return dateTime.format(Settings.DTF);
    }

    public static Message getInstance(String sender, String text) {
        return new Message(sender, text);
    }

    @Override
    public String toString() {
        return dateTime.format(Settings.DTF) + ": " + sender + ": " + text;
    }
}