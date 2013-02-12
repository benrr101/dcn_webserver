/**
 * A request exception is an exception that occurs when there is a problem with
 * an HTTP request that is being processed. It contains enough information to
 * create an error log message and to generate an error message.
 * @author Benjamin Russell (brr1922@rit.edu)
 */
public class RequestException extends RuntimeException{
    // MEMBER VARIABLES ////////////////////////////////////////////////////
    /**
     * The response code to return to the client
     */
    private int code;

    /**
     * A friendlier message to the user
     */
    private String friendlyMessage;

    // CONSTRUCTOR /////////////////////////////////////////////////////////
    public RequestException(int code, String message, String friendlyMessage) {
        super(message);
        this.code = code;
        this.friendlyMessage = friendlyMessage;
    }

    // GETTERS /////////////////////////////////////////////////////////////
    public int getCode() { return code; }
    public String getFriendlyMessage() { return this.friendlyMessage; }
}
