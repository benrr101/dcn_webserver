/**
 * User: scp8008
 * Date: 1/25/13
 * Time: 4:45 PM
 */

public class Response {
    private boolean headOnly = false;
    protected byte[] responseData;
    protected String HTTPMessage;
    protected int HTTPCode;

    protected String contentType;

    protected Response() {}

    public Response(byte[] responseData, int HTTPCode, String HTTPMessage){
        this.responseData = responseData;
        this.HTTPCode = HTTPCode;
        this.HTTPMessage = HTTPMessage;
    }

    protected String constructHeader(){
        StringBuilder header = new StringBuilder();
        header.append("HTTP/1.0 " + HTTPCode + " " + HTTPMessage + "\r\n");
        header.append("Content-Type: " + this.contentType + "\r\n");
        header.append("Content-Length: " + this.responseData.length + "\r\n");
        header.append("\r\n");
        return header.toString();
    }

    public String toString(){
        StringBuilder message = new StringBuilder();
        message.append(this.constructHeader());
        message.append(new String(this.responseData));
        message.append("\r\n");
        return message.toString();
    }

    public byte[] getBytes() {
        // Concatenate the header and content
        byte[] header = constructHeader().getBytes();
        if(headOnly) {
            return header;
        }

        // We're printing the entire content;
        byte[] concat = new byte[header.length + responseData.length];
        System.arraycopy(header, 0, concat, 0, header.length);
        System.arraycopy(responseData, 0, concat, header.length, responseData.length);

        return concat;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setHeadOnly(boolean val) {
        headOnly = val;
    }
}
