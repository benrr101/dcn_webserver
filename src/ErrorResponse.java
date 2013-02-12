/**
 * This class represents a response that is an error state. It automatically
 * generates its own error pages.
 * @author Benjamin Russell (brr1922@rit.edu)
 */
public class ErrorResponse extends Response {

    /**
     * A message suitable for framing in the system's error log
     */
    private String logMessage;

    /**
     * Constructs an error response using the error code, message and a
     * friendlier error message.
     * @param code              The HTTP error code
     * @param message           The message to associate with the error code
     * @param verboseMessage    The friendly error message
     */
    public ErrorResponse(int code, String message, String verboseMessage, String uri) {
        // Store code and message
        super(null, code, message);

        // Build the error page
        this.responseData = generateErrorPage(code, message, verboseMessage);

        // Set the log error message
        this.logMessage = " Error " + code + " on request for " + uri + ": " + message;

        // Set the content type
        contentType = "text/html";
    }

    /**
     * Generates an HTML error page based on the error code and the message
     * of the error
     * @param code The code of the error
     * @param message   The message of the error
     * @param verboseMessage    The user friendly error message
     * @return  A byte array of the html for the error page
     */
    private byte[] generateErrorPage(int code, String message, String verboseMessage) {
        // @TODO: Future improvement, use a XSLT
        StringBuilder build = new StringBuilder();
        build.append("<html><body><title>");
        build.append(message);
        build.append("</title><body><h1>Error ");
        build.append(code);
        build.append(": ");
        build.append(message);
        build.append("</h1><p>");
        build.append(verboseMessage);
        build.append("</p><hr>");
        build.append("<p style='font-style:italic'>DCN2 Web Server/Java ");
        build.append(System.getProperty("java.version"));
        build.append("/");
        build.append(System.getProperty("os.name"));
        build.append("</p></body></html>");
        return build.toString().getBytes();
    }

    public String getLogMessage() { return logMessage; }
}
