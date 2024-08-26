/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aspectedit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mark
 */
public class Config {

    private static final String CONFIG_FILE_LOC = "aspectedit.config";

    private static Config instance;

    public static Config getInstance() {
        if(instance == null) {
            instance = new Config();
        }

        return instance;
    }

    private Properties props;

    private Config() {
        
        props = new Properties();
        
        File propertiesFile = new File(CONFIG_FILE_LOC);
        
        if(propertiesFile.exists()) {
            try {
                props.loadFromXML(new FileInputStream(propertiesFile));
                
            } catch (InvalidPropertiesFormatException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.WARNING, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.WARNING, null, ex);
            }
        }
        
    }


    public String getProperty(String property) {
        return props.getProperty(property);
    }


    public void setProperty(String property, String value) {
        props.setProperty(property, value);
    }


    public void write() {
        File file = new File(CONFIG_FILE_LOC);
        try {
            props.storeToXML(new FileOutputStream(file), null);

        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
