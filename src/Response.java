/**
 * User: scp8008
 * Date: 1/25/13
 * Time: 4:45 PM
 */

import java.net.Socket;

public class Response {
    private String responseData;
    private String HTTPMessage;
    private int HTTPCode;

    public Response(String responseData, int HTTPCode, String HTTPMessage){
        this.responseData = responseData;
        this.HTTPCode = HTTPCode;
        this.HTTPMessage = HTTPMessage;
    }

    private String constructHeader(){
        StringBuilder header = new StringBuilder();
        header.append("HTTP/1.0 " + HTTPCode + " " + HTTPMessage + "\r\n");
        //header.append("Host: " + socketResource.getInetAddress() + "\r\n");
        header.append("Content-Type: text/HTML\r\n");
        //header.append("Server: " + socketResource.getLocalAddress() + "\r\n");
        header.append("\r\n");
        return header.toString();
    }

    public String toString(){
        StringBuilder message = new StringBuilder();
        message.append(this.constructHeader());
        message.append(this.responseData);
        message.append("\r\n");
        return message.toString();
    }

}
