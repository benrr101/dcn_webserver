/**
 * User: scp8008
 * Date: 1/25/13
 * Time: 4:43 PM
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Date;


public class PortListener extends Thread{

    private ServerSocket serverSocket;
    private Socket socket;
    private DataOutputStream output;
    private HashMap<String, SiteConfiguration> configs = new HashMap<String, SiteConfiguration>();

    private int portNumber;


    public PortListener(SiteConfiguration conf){
        try {
            this.portNumber = conf.getPort();
            //create a new socket to listen on
            configs.put(conf.getHost(), conf);
            this.serverSocket = new ServerSocket(conf.getPort());

        } catch (IOException e) {
            System.err.println("IOException creating PortListener: " + e.getMessage());
        }
    }


    public boolean addSiteConfiguration(SiteConfiguration conf){
        configs.put(conf.getHost(), conf);
        //if successful
        return true;
    }

    public void run(){
        for(;;){
            try {
                //accept connection
                socket = serverSocket.accept();

                BufferedReader netListener = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new DataOutputStream(socket.getOutputStream());

                //get string in
                String requestString = "";
                String line;
                while((line = netListener.readLine()) != null){
                    if(line.isEmpty())
                        break;
                    requestString += line + "\r\n";
                }

                //generate a request
                Request req = new Request(requestString, socket.getInetAddress().toString());

                // Grab the site to process the request with
                SiteConfiguration site;
                if(configs.get(req.getHost()) == null) {
                    // Use the * host
                    site = configs.get(SiteConfiguration.ANY_HOST);
                } else {
                    // Use the specified host
                    site = configs.get(req.getHost());
                }
                req.setSiteConfiguration(site);

                //process request
                Response res = req.process();
                if(res instanceof ErrorResponse) {
                    logError(((ErrorResponse) res).getLogMessage());
                }

                //write response to browser
                write(res.getBytes());

                //close socket
                socket.close();

            } catch (IOException e) {
                logError("IOException in PortListener: " + e.getMessage());
            } finally {
                //cleanup, if needed
                try{
                    socket.close();
                }catch (IOException e){
                    logError("IOException in PortListener: " + e.getMessage());
                }
            }
        }


    }//run

    /**
     * Output the packet to the client, auto flushes
     * @param toSend        The bytes to write to the client
     * @throws IOException  Thrown if the underlying data stream encounters an error
     */
    private void write(byte[] toSend) throws IOException {
        // Write and flush
        output.write(toSend);
        output.flush();
    }

    private void logError(String message) {
        // Log it to std error
        SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        gmtDateFormat.setTimeZone(TimeZone.getDefault());
        System.err.println(gmtDateFormat.format(new Date()) + "Port: " + portNumber + " " + message);
    }
}
