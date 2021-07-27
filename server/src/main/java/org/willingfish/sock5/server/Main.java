package org.willingfish.sock5.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args){
        ClassPathXmlApplicationContext application = new ClassPathXmlApplicationContext("beans.xml");
        application.start();
    }
}
