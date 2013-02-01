/**
 * Created with IntelliJ IDEA.
 * User: Omega
 * Date: 1/31/13
 * Time: 10:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestException extends RuntimeException{
    // MEMBER VARIABLES ////////////////////////////////////////////////////
    /**
     * The response code to return to the client
     */
    private int code;

    // CONSTRUCTOR /////////////////////////////////////////////////////////
    public RequestException(int code, String message) {
        super(message);
        this.code = code;
    }

    // GETTERS /////////////////////////////////////////////////////////////
    public int getCode() {
        return code;
    }
}
