package org.willingfish.socks.common;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;


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
