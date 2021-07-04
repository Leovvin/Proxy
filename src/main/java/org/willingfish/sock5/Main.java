package org.willingfish.sock5;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.willingfish.sock5.serv.Server;

@Slf4j
public class Main {
    public static void main(String[] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Server server = context.getBean(Server.class);
        try {
            server.start();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }
}
