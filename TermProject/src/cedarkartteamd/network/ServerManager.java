
package cedarkartteamd.network;

import cedarkartteamd.Main;
import cedarkartteamd.managers.GameManager;
import cedarkartteamd.managers.GameManager.State;
import cedarkartteamd.managers.SettingsManager.Weather;

/**
 *
 * @author Paul Marshall
 */
public class ServerManager {
    
    private final Main app;
    private final GameManager gameMan;
    
    private Servlet server = null;
    
    public ServerManager(Main app, GameManager gameMan) {
        this.app = app;
        this.gameMan = gameMan;
        
        this.server = new Servlet(this.gameMan.getPort());
    }
    
    public void start(Weather map) {
        this.server.start(map);
    }
    
    public void stop() {
        this.server.stop();
    }
    
    public void setState(State state) {
        this.server.forceState(state);
    }
}
