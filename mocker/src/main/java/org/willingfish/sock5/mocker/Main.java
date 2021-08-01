package org.willingfish.sock5.mocker;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Sender sender = new Sender();
        sender.send("ss");
    }
}
