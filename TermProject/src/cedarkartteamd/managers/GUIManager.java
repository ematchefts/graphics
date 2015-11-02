
package cedarkartteamd.managers;

import cedarkartteamd.Main;
import cedarkartteamd.controllers.NiftyStartController;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;

/**
 *
 * @author Paul Marshall
 */
public class GUIManager implements ICedarKartManager {
    
    private Main app;
    
    private Nifty nifty;
    private NiftyJmeDisplay niftyDisplay;
    private NiftyStartController nsCtrl;
    
    public GUIManager(Main app) {
        this.app = app;
    }

    @Override
    public void start() {
        // Main menu:
        this.nsCtrl = new NiftyStartController(this.app);
        
        this.niftyDisplay = new NiftyJmeDisplay(this.app.getAssetManager(),
                this.app.getInputManager(), this.app.getAudioRenderer(),
                this.app.getGuiViewPort());
        
        this.nifty = niftyDisplay.getNifty();
        this.nifty.fromXml("Interface/Nifty/start.xml", "loading", this.nsCtrl);
    }
    
    public void start_overlay(boolean isHost) {
        if (isHost) {
            this.nsCtrl.load("start_mp");
        } else {
            this.nsCtrl.load("start_wait");
        }
    }
    
    public void show() {
        this.app.getGuiViewPort().addProcessor(this.niftyDisplay);
    }
    
    public void hide() {
        this.app.getGuiViewPort().removeProcessor(this.niftyDisplay);
    }
    
    public void main() {
        this.nsCtrl.load("start");
    }
    
    public void loading() {
        this.nsCtrl.load("loading");
    }
    
    public void end() {
        this.nsCtrl.load("end");
    }
    
    public void pause(boolean me) {
        this.nsCtrl.pause(me);
    }
}
