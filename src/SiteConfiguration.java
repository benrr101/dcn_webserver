import java.io.File;
import java.io.FileNotFoundException;

/**
 * The site configuration class. It stores information about the site
 * configuration. Information is to be populated via the ConfigManager
 * @author  Benjamin Russell (brr1922@rit.edu)
 */
public class SiteConfiguration {

    // CONSTANTS ///////////////////////////////////////////////////////////
    public static final String ANY_HOST = "*";

    public static final int PORT_MIN_PORT = 1024;
    public static final int PORT_MAX_PORT = 49151;
    public static final int PORT_WELL_KNOWN = 80;

    // MEMBER VARIABLES ////////////////////////////////////////////////////

    /**
     * The host request that the site will service. Can be * for any host,
     * provided the connection is on the port.
     */
    private String host;

    /**
     * The port that the site is listening on
     */
    private int port;

    /**
     * Path to the root of the site's files for serving
     */
    private String root;

    // METHODS /////////////////////////////////////////////////////////////

    public byte[] getPage(String url) throws FileNotFoundException {
        // Swap / with the system-specific path separator
        url = url.replace('/', File.separatorChar);

        // Build the path to the page
        String path = root + url;

        // Create a file object for the path
        File file = new File(path);
        if(!file.exists()) {
            throw new FileNotFoundException("Path does not exist");
        }

        // @TODO: Decide on CGI stuff here

        return null;
    }

    // GETTERS /////////////////////////////////////////////////////////////

    /**
     * Generates a string representation of the site configuration
     * @return  String representation of the site configuration
     */
    public String toString() {
        String result = "Root: " + root + "\n";
        result       += "Host: " + host + "\n";
        result       += "Port: " + port + "\n";
        return result;
    }

    public int getPort() { return this.port; }
    public String getHost() { return this.host; }

    // SETTERS /////////////////////////////////////////////////////////////
    public void setHost(String host) { this.host = host; }
    public void setRoot(String root) { this.root = root; }

    /**
     * Sets the port of the site configuration
     * @param port  The port number that the site should listen on
     */
    public void setPort(int port) {
        // Verify that the port is in a legal range
        if((port > PORT_MAX_PORT || port < PORT_MIN_PORT) && port != PORT_WELL_KNOWN) {
            throw new ConfigurationException("Cannot set port to " + port + ". Port out of safe range.");
        }
        this.port = port;
    }



}
