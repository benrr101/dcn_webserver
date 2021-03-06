import java.io.*;
import java.net.URLConnection;
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
     * Name of the file in each directory to use a path handler
     */
    private String indexHandler = null;

    /**
     * A list of error codes and the files to use for their handler
     */
    private HashMap<Integer, String> errorHandlers = new HashMap<Integer, String>();

    /**
     * A list of file extensions and what executable to use to open them
     */
    private HashMap<String, String> cgiHandlers = new HashMap<String, String>();

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
        // Get the file to display
        File file = getFileFromUrl(url);

        // Build a stream for reading bytes from the file
        byte[] fileData = new byte[(int)file.length()];
        DataInputStream dis = new DataInputStream((new FileInputStream(file)));
        dis.readFully(fileData);
        dis.close();
        return fileData;
    }

    /**
     * Retrieves the mime-type of the file based on the URL.
     * @param   url     The URL that was requested
     * @return  The mime-type of the file
     * @throws  IOException Returned when something blew up.
     */
    public String getPageContentType(String url) throws IOException {
        // Get the file to get info about
        File file = getFileFromUrl(url);

        // Peek into the file and guess the file type
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        String contentType = URLConnection.guessContentTypeFromStream(is);

        // Close up the stream
        is.close();

        return contentType;
    }

    // PRIVATE METHODS /////////////////////////////////////////////////////

    /**
     * Retrieves the file object based on the url requested. This will automatically
     * hijack directory requests and replace them with the index handler, if it is
     * defined. If it isn't file not found will be returned.
     * @param url   The url of the file requested by the client
     * @return  a File object representation of the requested file
     * @throws FileNotFoundException    Thrown if the file does not exist
     */
    private File getFileFromUrl(String url) throws FileNotFoundException {
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

        // If the file is a directory, get the site's index handler
        if(file.isDirectory() && indexHandler != null) {
            file = getFileFromUrl(url + indexHandler);
        } else if(file.isDirectory()) {
            throw new FileNotFoundException("Directory index not specified: " + path);
        }

        return file;
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

    public String getCgiHandler(String extension) {
        return cgiHandlers.get(extension);
    }

    public int getPort() { return this.port; }
    public String getHost() { return this.host; }
    public String getRoot() { return this.root; }

    // SETTERS /////////////////////////////////////////////////////////////
    public void setHost(String host) { this.host = host; }
    public void setRoot(String root) { this.root = root; }
    public void setIndexHandler(String indexHandler) { this.indexHandler = indexHandler; }

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

    /**
     * Register the extension handler
     * @param extension     The extension to use to handle the file
     * @param executable    The executable to call for the file
     */
    public void addCgiHandler(String extension, String executable) {
        // Store the cgi handler
        this.cgiHandlers.put(extension, executable);
    }
}
