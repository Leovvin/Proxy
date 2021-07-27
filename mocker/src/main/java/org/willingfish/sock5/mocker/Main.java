package org.willingfish.sock5.mocker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Slf4j
public class Main {

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext application = new ClassPathXmlApplicationContext("beans.xml");
        Mocker mocker = application.getBean(Mocker.class);
        mocker.send("hello 1234567890123456");
        System.out.println("send message success");

        mocker.send("world 1234567890123456");
        System.out.println("send message success");

        mocker.send("this 1234567890123456");
        System.out.println("send message success");

        Thread.sleep(10000);
    }
}
