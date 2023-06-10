package org.example.props;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class PropertyReader {

    private static final String PROPERTY_FILE_NAME = "application.properties";
    private static final String PORT_PROPERTY_NAME = "api.port";

    public static Integer getConnectionPort() {
        Properties prop = getProperties();
        if (Objects.nonNull(prop)) {
            return Integer.parseInt(prop.getProperty(PORT_PROPERTY_NAME));
        } else {
            return null;
        }
    }

    // Helpers

    private static Properties getProperties() {
        try (InputStream input = PropertyReader.class.getClassLoader()
                .getResourceAsStream(PROPERTY_FILE_NAME)) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return null;
            }

            prop.load(input);

            return prop;

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
