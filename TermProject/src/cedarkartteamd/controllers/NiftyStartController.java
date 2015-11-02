
package cedarkartteamd.controllers;

import cedarkartteamd.Main;
import cedarkartteamd.managers.SettingsManager;
import cedarkartteamd.managers.SettingsManager.Level;
import cedarkartteamd.managers.SettingsManager.Weather;
import cedarkartteamd.managers.VehicleManager.Vehicle;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Paul Marshall
 */
public class NiftyStartController implements ScreenController {

    private Nifty nifty;
    private Main app;
    private SettingsBuffer settings;
    
    public NiftyStartController(Main app) {
        this.app = app;
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
    }

    @Override
    public void onStartScreen() {
        this.app.getFlyByCamera().setDragToRotate(true);
    }

    @Override
    public void onEndScreen() {
        this.app.getFlyByCamera().setDragToRotate(false);
    }
    
    public void nop() {}
    
    public void main() {
        this.app.getGameMan().resetWorld();
    }
    
    public void start() {
        this.app.getGameMan().setMultiplayer(false);
        this.app.getGameMan().startGame();
    }
    
    public void start_mp() {
        this.app.getGameMan().setMultiplayer(true);
        
        if (!this.app.getGameMan().isHost()) {
            TextField hostField = this.nifty.getCurrentScreen().findNiftyControl("address", TextField.class);
            this.app.getGameMan().setHost(hostField.getDisplayedText());
        } else {
            this.app.getGameMan().setHost("127.0.0.1");
        }
        
        this.app.getGameMan().connect();
    }
    
    public void trigger_start() {
        this.app.getGameMan().triggerGame();
    }
    
    public void pause(boolean me) {
        if (me) {
            this.nifty.gotoScreen("pause");
        } else {
            this.nifty.gotoScreen("mp_pause");
        }
    }
    
    public void resume() {
        this.app.getGameMan().resumeGame();
    }
    
    public void quit() {
        this.app.getGameMan().quitGame();
    }

    public void load(String target) {
        if (this.nifty.getCurrentScreen().getScreenId().equals("start")
                && target.startsWith("settings")) {
            SettingsManager man = this.app.getGameMan().getSettings();
            this.settings = new SettingsBuffer();
            this.settings.level = man.getLevel();
            this.settings.controller = man.getController();
        }
        
        if (target.equals("singleplayer_map")) {
            switch (this.app.getGameMan().getSettings().getWeather()) {
                case WINTER:
                    target += "_winter";
                    break;
                case SUNRISE:
                    target += "_sunrise";
                    break;
                case NIGHT:
                    target += "_night";
                    break;
                case APOCOLYPSE:
                    target += "_apoc";
                    break;
            }
        } else if (target.equals("singleplayer_vehicle")) {
            switch (this.app.getGameMan().getSettings().getVehicle()) {
                case GOLF_CART:
                    target += "_golfkart";
                    break;
                case ATOM_2:
                    target += "_atom2";
                    break;
                case LANDSPEEDER:
                    target += "_landspeeder";
                    break;
                case VIPER:
                    target += "_viper";
                    break;
            }
        } else if (target.equals("multiplayer_map")) {
            switch (this.app.getGameMan().getSettings().getWeather()) {
                case WINTER:
                    target += "_winter";
                    break;
                case SUNRISE:
                    target += "_sunrise";
                    break;
                case NIGHT:
                    target += "_night";
                    break;
                case APOCOLYPSE:
                    target += "_apoc";
                    break;
            }
        } else if (target.equals("multiplayer_vehicle")) {
            switch (this.app.getGameMan().getSettings().getVehicle()) {
                case GOLF_CART:
                    target += "_golfkart";
                    break;
                case ATOM_2:
                    target += "_atom2";
                    break;
                case LANDSPEEDER:
                    target += "_landspeeder";
                    break;
                case VIPER:
                    target += "_viper";
                    break;
            }
        } else if (target.equals("multiplayer_network")) {
            if (this.app.getGameMan().isHost()) {
                target += "_host";
            } else {
                target += "_client";
            }
        } else if (target.equals("settings_graphics")) {
            switch (this.settings.level) {
                case LOW:
                    target += "_low";
                    break;
                case MEDIUM:
                    target += "_med";
                    break;
                case HIGH:
                    target += "_high";
                    break;
                case ULTRA:
                    target += "_ultra";
                    break;
            }
        } else if (target.equals("settings_control")) {
            if (this.settings.controller < 0) {
                if (this.app.getGameMan().getControls().availableJoysticks() > 0) {
                    target += "_keyboard_gp";
                } else {
                    target += "_keyboard_ngp";
                }
            } else {
                target += "_gamepad";
            }
        } else if (target.equals("end")) {
            if (this.app.getGameMan().isMultiplayer()) {
                if (this.app.getGameMan().won()) {
                    target += "_mp_won";
                } else {
                    target += "_mp_lost";
                }
            } else {
                target += "_game";
            }
        }
        
        this.nifty.gotoScreen(target);
    }
    
    public void exit() {
        System.exit(0);
    }
    
    public void setMap(String weather, String target) {
        this.app.getGameMan().getSettings().setWeather(Weather.valueOf(weather));
        load(target);
    }
    
    public void setCar(String car, String target) {
        this.app.getGameMan().getSettings().setVehicle(Vehicle.valueOf(car));
        load(target);
    }
    
    public void clearHost() {
        this.app.getGameMan().host(false);
        load("multiplayer_network");
    }
    
    public void setHost() {
        this.app.getGameMan().host(true);
        load("multiplayer_network");
    }
    
    public void setKB() {
        this.settings.controller = -1;
        load("settings_control");
    }
    
    public void setGP() {
        this.settings.controller = 0;
        load("settings_control");
    }
    
    public void setGraphics(String level) {
        this.settings.level = Level.valueOf(level);
        load("settings_graphics");
    }
    
    public void save() {
        SettingsManager man = this.app.getGameMan().getSettings();
        man.setLevel(this.settings.level);
        man.setController(this.settings.controller);
        
        load("start");
    }
}
