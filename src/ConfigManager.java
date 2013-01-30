import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

/**
 * The configuration manager. It loads the site configuration files for each of
 * the sites supported by the server. This class follows the singleton pattern.
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

    public ArrayList<SiteConfiguration> getSiteConfigurations() {
        // Load the files in the configuration
        File[] siteConfigurationFiles = configFolder.listFiles();
        for(File siteFile : siteConfigurationFiles) {
            readSiteConfiguration(siteFile);
        }

        return null;
    }

    private SiteConfiguration readSiteConfiguration(File siteFile) {
        try {
            // Read in the XML for the site configuration
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xmlDoc = documentBuilder.parse(siteFile);
            xmlDoc.normalizeDocument();

            System.out.println("Found Site Configuration: " + siteFile.getName());
            System.out.println(" - ");
        } catch(Exception e) {
            System.err.println("*** Failed to parse site configuration for " + siteFile.getName());
            System.err.println("\n" + e.getMessage());
        }

        return null;
    }
}