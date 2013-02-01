import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import sun.misc.Regexp;

import java.io.StringReader;
import java.util.Scanner;
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

    // CONSTRUCTOR /////////////////////////////////////////////////////////

    public Request(String requestString) throws RequestException{
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

    public Response processRequest() {
        return null;
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
