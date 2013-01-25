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

    // CONSTRUCTOR /////////////////////////////////////////////////////////
    public static ConfigManager getInstance() {
        return ourInstance;
    }

    // METHODS /////////////////////////////////////////////////////////////
    private ConfigManager() {

    }

}
