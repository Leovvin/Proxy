package org.willingfish.sock5.env;

import io.netty.util.internal.StringUtil;

import java.lang.reflect.Field;
import java.util.function.Function;

public class EnvironmentFactory {
    static Config config = null;

    public static synchronized Config getConfig() throws InstantiationException, IllegalAccessException {
        if (config ==null){
            Class<Config> clazz = Config.class;
            config = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields){
                String name = field.getName();
                if (StringUtil.isNullOrEmpty(System.getenv(name))){
                    continue;
                }
                String value = System.getenv(name);
                if (!field.isAccessible()){
                    field.setAccessible(true);
                }
                Class type = field.getType();
                if (type.equals(String.class)){
                    field.set(config,value);
                }else if (type.equals(Integer.class)||type.equals(int.class)){
                    field.set(config,convertToInt.apply(value));
                }
            }
        }
        return config;
    }

    static Function<String,Integer> convertToInt = s -> Integer.parseInt(s);

}
