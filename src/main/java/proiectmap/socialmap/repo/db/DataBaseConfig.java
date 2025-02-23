package proiectmap.socialmap.repo.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataBaseConfig {
    private static Properties properties = new Properties();
    static {
        try (InputStream input = DataBaseConfig.class.getClassLoader().getResourceAsStream("db.properties")){
            if(input == null){
                throw new IOException("Unable to find db.properties");
            }
            properties.load(input);
        }
        catch(IOException e){
            e.printStackTrace();
            throw new RuntimeException("Error loading database configuration");
        }
    }

    public static String getDbUrl(){
        return properties.getProperty("db.url");
    }

    public static String getDbUser(){
        return properties.getProperty("db.user");
    }

    public static String getDbPassword(){
        return properties.getProperty("db.password");
    }
}
