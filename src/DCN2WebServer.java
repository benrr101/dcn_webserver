/**
 * User: scp8008
 * Date: 1/25/13
 * Time: 4:46 PM
 */
public class DCN2WebServer {

    public static void main(String[] argv) {
        ConfigManager configManager = ConfigManager.getInstance();
        configManager.getSiteConfigurations();
    }

}
