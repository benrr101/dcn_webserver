/**
 * User: scp8008
 * Date: 1/25/13
 * Time: 4:43 PM
 */

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Date;


public class PortListener extends Thread{

    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter writer;

    private int portNumber;
    private RequestException re;
    private String server;
    public PortListener(int portNumber){
        this.server = "DCN2WebServer-Java";
        this.portNumber = portNumber;
        try {
            //create a new socket to listen on
            this.serverSocket = new ServerSocket(portNumber);

        } catch (IOException e) {
            System.err.println("IOException in PortListener: " + e.getMessage());
        }


    }
    public void run(){
        for(;;){
            try {
                //accept connection
                socket = serverSocket.accept();

                BufferedReader netListener = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //BufferedWriter writer = new BufferedWriter(new PrintWriter(socket.getOutputStream()));
                this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

                String requestString = "";
                String line = "";
                while((line = netListener.readLine()) != null){
                    if(line.isEmpty())
                        break;
                    requestString += line + "\r\n";
                }
                Request req = new Request(requestString);
                Response res = req.process();
                //System.out.println(res);
                writer.println(res);
                writer.flush();
                //writer.close();
                socket.close();

            } catch (IOException e) {
                //todo: log this instead of printing to stderr
                System.err.println("IOException in PortListener: " + e);
            } catch (RequestException re) {
                //todo: log this instead of printing to stderr
                SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-5"));
                System.err.println(gmtDateFormat.format(new Date()) + " RequestException in PortListener: " + re.getMessage());
                try {
                    this.re = re;
                    writer.println("HTTP/1.0 " + re.getCode() + " Not Implemented");
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


    }


}
