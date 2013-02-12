import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a HTTP request.
 */
public class Request {

    // CONSTANTS ///////////////////////////////////////////////////////////

    /**
     * Valid request methods
     */
    enum RequestMethod {
        POST,
        GET,
        HEAD
    }

    public static final int MAX_HTTP_MAJOR = 1;
    public static final int MAX_HTTP_MINOR = 0;

    // MEMBER VARIABLES ////////////////////////////////////////////////////
    private String requestBody;

    private int majorVersion;
    private int minorVersion;
    private RequestMethod requestMethod;
    private String requestUri;
    private String host;

    private HashMap<String, String> getVariables;
    private HashMap<String, String> postVariables;

    private SiteConfiguration siteConfiguration = null;

    // CONSTRUCTOR /////////////////////////////////////////////////////////

    /**
     * Creates a request object based on the text of the HTTP request.
     * Validates the protocol version, and the request method.
     * @param requestString     The text of the HTTP request
     * @throws RequestException Thrown if the request is via an unsupported
     *                          protocol version, the method is unsupported, or
     *                          if the method line is malformed.
     */
    public Request(String requestString){
        // Store the request
        this.requestBody = requestString;

        // Create storage for the put and get variables
        this.getVariables = new HashMap<String, String>();
        this.postVariables = new HashMap<String, String>();
    }

    // METHODS /////////////////////////////////////////////////////////////

    /**
     * Processes the request and generates are response for output
     * @return  A response that can easily be outputted to the socket
     */
    public Response process() {
        // Process the header
        try {
            processHeader();
        } catch(RequestException e) {
            return new ErrorResponse(e.getCode(), e.getMessage(), e.getFriendlyMessage(), requestUri);
        }

        // Make sure that the request has a configuration to use for processing
        if(this.siteConfiguration == null) {
            throw new NullPointerException("Cannot process request if site configuration is undefined.");
        }

        // Extract the URI based on the style of URI we received
        String uri = "";
        if(requestUri.startsWith("http://")) {
            StringTokenizer tok = new StringTokenizer(requestUri, "/", true);
            tok.nextToken();
            tok.nextToken();
            tok.nextToken();
            tok.nextToken();
            while(tok.hasMoreTokens()) {
                uri += tok.nextToken();
            }
        } else if(requestUri.startsWith("/")) {
            uri = requestUri;
        } else {
            // It's an invalid URI
            return processError(400, "Bad Request", "The request does not match the format specified by HTTP/1.0");
        }

        // Break off the page to load
        StringTokenizer tok = new StringTokenizer(uri, "?");
        if(tok.countTokens() > 2) {
            // There's > 1 ? in the uri. that's invalid
            return processError(400, "Bad Request", "The request does not match the format specified by HTTP/1.0");
        }
        String page = tok.nextToken();

        // Break off get parameters
        if(tok.hasMoreTokens()) {
            String getParams = tok.nextToken();
            processGetParameters(getParams);
        }

        // Extract the page to load from the request uri
        try {
            byte[] q = siteConfiguration.getPage(page);
            // @TODO: Support HEAD requests

            // Create a response based on the file bytes
            Response r = new Response(q, 200, "OK");
            r.setContentType(siteConfiguration.getPageContentType(page));
            return r;

        } catch(FileNotFoundException e) {
            return processError(404, "File Not Found", "The requested page could not be found on this server.");
        } catch(IOException e) {
            return processError(500, "Internal Server Error", "An internal server error has occurred.");
        }
    }

    // PRIVATE METHODS /////////////////////////////////////////////////////

    /**
     * Processes the header of the request internally.
     * @throws RequestException Thrown if there is a problem with the request
     */
    private void processHeader() throws RequestException {
        // Create a string reader
        Scanner requestReader = new Scanner(this.requestBody);

        // Make sure we at least have one line
        if(!requestReader.hasNextLine()) {
            throw new RequestException(501, "Incomplete Method", "The HTTP header provided is incomplete.");
        }

        // First line must be the method, request URI, and protocol version
        Pattern p = Pattern.compile("^(.*) (.*) HTTP/([0-9])\\.([0-9])$");
        Matcher m = p.matcher(requestReader.nextLine());
        if(!m.matches()) {
            throw new RequestException(501, "Incomplete Method", "The HTTP header provided is incomplete.");
        }

        // Store the method
        try {
            requestMethod = RequestMethod.valueOf(m.group(1));
        } catch(IllegalArgumentException e) {
            throw new RequestException(501, "Unsupported Method", "The HTTP request method is unsupported.");
        }

        // Store the URI
        requestUri = m.group(2);

        // Store the protocol
        majorVersion = Integer.parseInt(m.group(3));
        minorVersion = Integer.parseInt(m.group(4));
        if(majorVersion > MAX_HTTP_MAJOR || (majorVersion == MAX_HTTP_MAJOR && minorVersion > MAX_HTTP_MINOR)) {
            throw new RequestException(505, "HTTP Version Not Supported", "This HTTP protocol version is not supported");
        }
    }

    /**
     * Processes GET style parameters from the uri and stores them in the
     * internal get variables
     * @param   query     The query portion of the URI
     */
    private void processGetParameters(String query) {
        // Split up the query string based on &'s, x=y
        StringTokenizer getTok = new StringTokenizer(query, "&");
        Pattern p = Pattern.compile("(.*)=(.*)");

        // Iterate over the parameters and create a new one for each parameter given
        while(getTok.hasMoreTokens()) {
            // Check to see if it matches the format
            Matcher m = p.matcher(getTok.nextToken());
            if(!m.matches()) {
                continue;
            }

            // It matches the proper format. Now process it.
            this.getVariables.put(m.group(1), m.group(2));
        }
    }

    /**
     * Generates an error page for it the error. If an error handler for
     * the error is defined in the site configuration, the page for the error is
     * retrieved. If that page does not exist, the default error page is returned.
     * @param code          The HTTP code for the error that occurred.
     * @param message       The HTTP message for the error that occurred.
     * @param friendlyError A friendlier error message to be included with the error page
     * @return  An error response
     */
    private Response processError(int code, String message, String friendlyError) {
        // Grab the page for the error code, if it exists
        String path = siteConfiguration.getErrorHandlerPath(code);
        if(path == null) {
            return new ErrorResponse(code, message, friendlyError, requestUri);
        } else {
            try {
                // Return the error handler page
                return new Response(siteConfiguration.getPage(path), code, message);
            } catch(Exception e) {
                friendlyError += "<br/>An additional error 404 was incurred while attempting to ";
                friendlyError += "find the appropriate page for this error";
                return new ErrorResponse(code, message, friendlyError, requestUri);
            }
        }
    }

    // SETTERS /////////////////////////////////////////////////////////////

    /**
     * Sets the site configuration
     * @param config    The site configuration the request will be processed with
     * @throws NullPointerException Thrown if the configuration passed in is null
     */
    public void setSiteConfiguration(SiteConfiguration config) throws NullPointerException {
        // Verify that the site configuration is valid
        if(config == null) {
            throw new NullPointerException("Site configuration cannot be null");
        }

        this.siteConfiguration = config;
    }

    // GETTERS /////////////////////////////////////////////////////////////
    public String getHost() {
        return host;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Protocol Version: ");
        b.append(majorVersion);
        b.append(".");
        b.append(minorVersion);
        b.append("\n");
        b.append("Method: ");
        b.append(requestMethod);
        b.append("\n");
        b.append("URI: ");
        b.append(requestUri);
        b.append("\n");

        return b.toString();
    }
}
