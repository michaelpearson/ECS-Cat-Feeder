package catfeeder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class Configuration {
    private static JSONObject configuration;

    static {
        try {
            String configFile = System.getProperty("config-file");
            configuration = (JSONObject)new JSONParser().parse(new FileReader(configFile));
        } catch (IOException | ParseException e) {
            System.out.println("Error reading configuration file; " + e.getMessage());
            throw new RuntimeException("Error reading configuration file; " + e.getMessage());
        }
    }
    public static String getConfigurationString(String namespace, String key) {
        return (String)((JSONObject)configuration.get(namespace)).get(key);
    }
}
