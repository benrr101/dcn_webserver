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
    private PrintWriter writer;
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
                //BufferedWriter writer = new BufferedWriter(new PrintWriter(socket.getOutputStream()));
                this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));


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
                req.getHost();


                //process request
                Response res = req.process();

                //write response to browser
                write(res.toString());
                writer.flush();
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
                    writer.println("HTTP/1.0 " + re.getCode() + " " + re.getMessage());
                    writer.println("Server: " + server);
                    writer.println();
                    writer.println("Error " + re.getCode() + " " + re.getMessage());
                    writer.println();
                    writer.flush();
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

    //writes to browser
    private void write(String toSend){
        writer.println(toSend);
        //write response to browser
        writer.flush();

    }

    public int getPortNumber(){
        return this.portNumber;
    }
}
