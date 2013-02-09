import java.io.*;
import java.util.HashMap;

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

    /**
     * A list of error codes and the files to use for their handler
     */
    private HashMap<Integer, String> errorHandlers = new HashMap<Integer, String>();

    // METHODS /////////////////////////////////////////////////////////////

    /**
     * Retrieves the file that was requested
     * @param   url     The url that requested the page, stripped of its host
     * @return  A byte array of the file that was requested.
     * @throws  IOException     FileNotFoundException thrown if the file does
     *                          not exist. IOException thrown if reading the
     *                          file failed somehow.
     */
    public byte[] getPage(String url) throws IOException {
        // Swap / with the system-specific path separator
        url = url.replace('/', File.separatorChar);

        // If the path does not start with a separator, add one
        if(!url.startsWith(File.separator)) {
            url = File.separator + url;
        }

        // Build the path to the page
        String path = root + url;

        // Create a file object for the path
        File file = new File(path);
        if(!file.exists()) {
            throw new FileNotFoundException("Path does not exist: " + path);
        }

        // @TODO: Decide on CGI stuff here

        // Build a stream for reading bytes from the file
        try {
            byte[] fileData = new byte[(int)file.length()];
            DataInputStream dis = new DataInputStream((new FileInputStream(file)));
            dis.readFully(fileData);
            dis.close();
            return fileData;
        } catch(IOException e) {
            throw e;
        }
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

    public String getErrorHandlerPath(int code) {
        return errorHandlers.get(code);
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

    /**
     * Register an error handler
     * @param code  The code to return the page on
     * @param path  The path to return when the error code happens
     */
    public void addErrorHandler(int code, String path) {
        // Store the error handler
        this.errorHandlers.put(code, path);
    }
}
