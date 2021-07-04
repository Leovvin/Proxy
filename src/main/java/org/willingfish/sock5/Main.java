package org.willingfish.sock5;

import lombok.extern.slf4j.Slf4j;
import org.willingfish.sock5.env.Config;
import org.willingfish.sock5.env.EnvironmentFactory;

@Slf4j
public class Main {
    public static void main(String[] args){
        Config config = null;
        try {
            config = EnvironmentFactory.getConfig();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return;
        }
        if (config == null){
            log.error("cant load environment");
            return;
        }

        log.info(config.getPass());
    }
}
