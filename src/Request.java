import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import sun.misc.Regexp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;
import java.util.StringTokenizer;
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
    private int majorVersion;
    private int minorVersion;
    private RequestMethod requestMethod;
    private String requestUri;
    private String host;

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
    public Request(String requestString) throws RequestException {
        // Create a string reader
        Scanner requestReader = new Scanner(requestString);

        // Make sure we at least have one line
        if(!requestReader.hasNextLine()) {
            throw new RequestException(501, "Incomplete Method");
        }

        // First line must be the method, request URI, and protocol version
        Pattern p = Pattern.compile("^(.*) (.*) HTTP/([0-9])\\.([0-9])$");
        Matcher m = p.matcher(requestReader.nextLine());
        if(!m.matches()) {
            throw new RequestException(501, "Incomplete Method");
        }

        // Store the method
        try {
            requestMethod = RequestMethod.valueOf(m.group(1));
        } catch(IllegalArgumentException e) {
            throw new RequestException(501, "Unsupported Method");
        }

        // Store the URI
        requestUri = m.group(2);

        // Store the protocol
        majorVersion = Integer.parseInt(m.group(3));
        minorVersion = Integer.parseInt(m.group(4));
        if(majorVersion > MAX_HTTP_MAJOR || (majorVersion == MAX_HTTP_MAJOR && minorVersion > MAX_HTTP_MINOR)) {
            throw new RequestException(505, "HTTP Version Not Supported");
        }
    }

    // METHODS /////////////////////////////////////////////////////////////
    public Response process() {
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
            throw new RequestException(400, "Bad Request");
        }

        // Break off the page to load
        StringTokenizer tok = new StringTokenizer(uri, "?");
        if(tok.countTokens() > 2) {
            // There's > 1 ? in the uri. that's invalid
            throw new RequestException(400, "Bad Request");
        }
        String page = tok.nextToken();

        // Break off get parameters
        if(tok.hasMoreTokens()) {
            String getParams = tok.nextToken();
            // @TODO: Process these
        }

        // Extract the page to load from the request uri
        try {
            byte[] q = siteConfiguration.getPage(page);
            //@TODO: Remove debug code
            System.out.println("Printing file---");
            System.out.print(new String(q));
            System.out.println("Done---");
        } catch(FileNotFoundException e) {
            throw new RequestException(404, "File not found");
        } catch(IOException e) {
            throw new RequestException(500, "Server Error");
        }

        return null;
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
        b.append(majorVersion + "." + minorVersion + "\n");
        b.append("Method: ");
        b.append(requestMethod + "\n");
        b.append("URI: ");
        b.append(requestUri + "\n");

        return b.toString();
    }
}
