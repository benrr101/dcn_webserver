/**
 * User: scp8008
 * Date: 1/25/13
 * Time: 4:46 PM
 */

import java.util.ArrayList;
public class DCN2WebServer {

    public static void main(String[] argv) {
        ConfigManager configManager = ConfigManager.getInstance();
        ArrayList<SiteConfiguration> configurations = configManager.getSiteConfigurations();
        ArrayList<PortListener> listeners = new ArrayList<PortListener>();

        //create port listeners from Site Configurations
        for(SiteConfiguration conf : configurations){
            PortListener temp = new PortListener(conf.getPort());
            listeners.add(temp);
            //start the PortListener
            temp.start();
        }
    }

}
