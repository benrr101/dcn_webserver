////////////////////////////////////////////////////////////////////////////
// Stream Gobbler
// @descrip This class gobbles up streams.
// @source  http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html?page=4
////////////////////////////////////////////////////////////////////////////

import java.io.*;
class StreamGobbler extends Thread
{
    InputStream is;
    String body;

    StreamGobbler(InputStream is)
    {
        this.is = is;
    }

    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ( (line = br.readLine()) != null)
                body += line;
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public String getBody() { return body; }
}