/**
 * User: scp8008
 * Date: 1/25/13
 * Time: 4:43 PM
 */

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Date;


public class PortListener extends Thread{

    private ServerSocket serverSocket;
    private Socket socket;
    private DataOutputStream output;
    //not sure if deals with * host nicely
    private HashMap<String, SiteConfiguration> configs = new HashMap<String, SiteConfiguration>();

    private int portNumber;
    private RequestException re;
    private String server;


    public PortListener(SiteConfiguration conf){
        this.server = "DCN2WebServer-Java";
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
                String line = "";
                while((line = netListener.readLine()) != null){
                    if(line.isEmpty())
                        break;
                    requestString += line + "\r\n";
                }


                //generate a request
                Request req = generateRequest(requestString);

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

                //write response to browser
                write(res.getBytes());

                //close socket
                socket.close();

            } catch (IOException e) {
                //todo: log this instead of printing to stderr
                System.err.println("IOException in PortListener: " + e);
            } catch (RequestException re) {
                //todo: log this instead of printing to stderr
                SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-5"));
                System.err.println(gmtDateFormat.format(new Date()) +
                        " RequestException in PortListener on port " + this.portNumber + ": " + re.getMessage());
                try {
                    this.re = re;
                    output.write(new String("HTTP/1.0 " + re.getCode() + " " + re.getMessage() + "\r\n").getBytes());
                    output.write(new String("Server: " + server + "\r\n").getBytes());
                    output.write(new String("Error " + re.getCode() + " " + re.getMessage() + "\r\n").getBytes());
                    output.write(new String("\r\n").getBytes());
                    output.flush();
                } catch (Exception e) {
                    System.err.println("HOLY MASSIVE ERRORS BATMAN! " + e);
                }
            } finally {
                //cleanup, if needed
                try{
                    socket.close();
                }catch (IOException e){
                    System.err.println(e.getMessage());
                }
            }
        }


    }//run

    //todo: finish restructuring and move exception handling
    private Request generateRequest(String requestString) throws RequestException{
        Request req = new Request(requestString);
        return req;
    }

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

    public int getPortNumber(){
        return this.portNumber;
    }
}
