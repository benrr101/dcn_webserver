import java.io.File;

/**
 * The configuration manager. It loads the site configuration files for each of
 * the sites supported by the server. This class follows the
 * @author Benjamin Russell (brr1922@rit.edu)
 */
public class ConfigManager {
    // CONSTANTS ///////////////////////////////////////////////////////////
    private static final String siteConfigFolder = "siteConfigs";

    // MEMBER VARIABLES ////////////////////////////////////////////////////
    private static ConfigManager ourInstance = new ConfigManager();

    private File configFolder;

    // CONSTRUCTOR /////////////////////////////////////////////////////////
    private ConfigManager() {
        // Open the folder of configurations
        configFolder = new File(siteConfigFolder);
        if(!configFolder.exists()) {
            throw new ConfigurationException("Site configurations folder does not exist");
        }
        if(!configFolder.isDirectory()) {
            throw new ConfigurationException("Site configurations folder is not a directory");
        }
    }

    // METHODS /////////////////////////////////////////////////////////////
    public static ConfigManager getInstance() {
        return ourInstance;
    }

    public void getSiteConfigurations() {
        // Load the files in the configuration
        File[] siteConfigurationFiles = configFolder.listFiles();
    }

}

public class ConfigurationException extends RuntimeException {
    ConfigurationException(String message) {
        super(message);
    }
}