package org.willingfish.sock5;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.willingfish.sock5.serv.Server;

@Slf4j
public class Main {
    public static void main(String[] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        context.start();
    }
}
