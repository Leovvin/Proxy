package org.willingfish.sock5;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static java.lang.String.format;

@Slf4j
public class Main {
    static final String TYPE_SERVER = "server";
    static final String TYPE_LOCAL = "local";
    public static void main(String[] args){
        String type = getType(args);
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(format("beans-%s.xml",type));
        context.start();
    }

    static String getType(String[] args){
        if (args.length==0){
            return TYPE_SERVER;
        }
        String type = args[0];
        if (TYPE_LOCAL.equals(type)){
            return TYPE_LOCAL;
        }else if (TYPE_SERVER.equals(type)){
            return TYPE_SERVER;
        }else {
            throw new RuntimeException();
        }
    }
}
