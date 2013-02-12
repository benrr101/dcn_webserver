/**
 * User: scp8008
 * Date: 1/25/13
 * Time: 4:46 PM
 */

import java.util.ArrayList;
import java.util.HashMap;

public class DCN2WebServer {

    public static void main(String[] argv) {
        ConfigManager configManager = ConfigManager.getInstance();
        ArrayList<SiteConfiguration> configurations = configManager.getSiteConfigurations();
        HashMap<Integer, PortListener> listeners = new HashMap<Integer, PortListener>();

        //create port listeners from Site Configurations
        for(SiteConfiguration conf : configurations){
            if(!listeners.containsKey(conf.getPort())){
                PortListener temp = new PortListener(conf);
                listeners.put(conf.getPort(), temp);
            }
            // @TODO: Handler multiple hosts on a single port
        }
        //start the PortListeners
        for(Integer l : listeners.keySet()){
            listeners.get(l).start();
        }
    }//main

}
