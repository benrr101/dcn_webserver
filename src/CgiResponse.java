import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class to specially handle cgi responses.
 * @author Benjamin Russell (brr1922@rit.edu)
 */
public class CgiResponse extends Response {

    HashMap<String, String> headers = new HashMap<String, String>();

    public CgiResponse(String body) {
        // Process the header of the
        Scanner reader = new Scanner(body);
        while(reader.hasNextLine()) {
            // Break out on a blank line
            String line = reader.nextLine();
            if(line.equals("\r\n") || line.isEmpty()) {
                break;
            }

            StringTokenizer tok = new StringTokenizer(line, ":");
            Pattern p = Pattern.compile(".*: .*");

            // Special case, we want the status
            if(line.toLowerCase().startsWith("status:")) {
                tok.nextToken();
                Pattern p2 = Pattern.compile("(\\d+) (.*)");
                Matcher m = p2.matcher(tok.nextToken());
                this.HTTPCode = Integer.parseInt(m.group(1));
                this.HTTPMessage = m.group(2);
            } else if(line.toLowerCase().startsWith("content-type")) {
                tok.nextToken();
                this.contentType = tok.nextToken().trim();
            } else if(!p.matcher(line).matches()) {
                continue;
            } else {
                // Stow away the header info
                headers.put(tok.nextToken(), tok.nextToken());
            }
        }

        // Now everything left is the content
        StringBuilder content = new StringBuilder();
        while(reader.hasNextLine()) {
            content.append(reader.nextLine());
        }

        // Store the content in the data field
        responseData = content.toString().getBytes();
        if(HTTPCode == 0) {
            HTTPCode = 200;
            HTTPMessage = "OK";
        }
        if(contentType == null) {
            contentType = "text/html";
        }
    }

    @Override
    public String constructHeader() {
        StringBuilder header = new StringBuilder();
        header.append("HTTP/1.0 " + HTTPCode + " " + HTTPMessage + "\r\n");
        header.append("Content-Type: " + this.contentType + "\r\n");
        header.append("Content-Length: " + this.responseData.length + "\r\n");
        for(Map.Entry<String, String> entry : headers.entrySet()) {
            header.append(entry.getKey());
            header.append(entry.getValue());
            header.append("\r\n");
        }

        header.append("\r\n");
        return header.toString();
    }
}
