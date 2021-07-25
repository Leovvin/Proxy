package org.willingfish.sock5.common;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.willingfish.sock5.common.IServer;


@Slf4j
public class Listener implements ApplicationListener<ContextStartedEvent> {
    @Setter
    IServer server ;
    @Override
    public void onApplicationEvent(ContextStartedEvent event) {
        log.info("start server");
        try {
            server.start();
        } catch (Exception e) {
            log.error("server start failed.",e);
        }
    }
}
