package util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class PropertyLoader {


    public static ConnectionProperty loadProperties() {
        Properties properties = PropertyLoader.loadPropertiesFile("config.properties");
        ConnectionProperty connectionProperties = new ConnectionProperty();

        connectionProperties.setHost(properties.getProperty("host"));

        return connectionProperties;
    }


    public static Properties loadPropertiesFile(String filePath) {
        Properties property = new Properties();
        try (InputStream resourceAsStream = PropertyLoader.class.getClassLoader().getResourceAsStream(filePath)) {
            property.load(resourceAsStream);

        } catch (IOException e) {
            log.error("Unable to load properties file {}", filePath);
        }
        return property;
    }

}
