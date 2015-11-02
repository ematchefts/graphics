
package cedarkartteamd.managers;

import cedarkartteamd.Main;
import cedarkartteamd.managers.SettingsManager.Weather;
import cedarkartteamd.network.ActorUpdate;
import cedarkartteamd.network.ClientManager;
import cedarkartteamd.network.ServerManager;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

/**
 *
 * @author nabond
 */
public class GameManager implements ICedarKartManager {
    
    // Networking:
    private boolean isHost = false;
    private String host = null;
    private int port = 31337;

    private Main app;
    // Custom Managers
    private final ServerManager serverMan;
    private ClientManager clientMan;
    private final WorldManager worldMan;
    private final SettingsManager settingsMan;
    private final GUIManager GUIMan;
    private final HUDManager HUDMan;
    private final TrophyManager trophyMan;
    private final SoundManager soundMan;
    private boolean setupComplete = false;
    private boolean running = false;
    private boolean multiplayer = false;
    
    private Camera camera;
    
    public enum State {
        START,
        PAUSE,
        RESUME,
        END;
    }

    public GameManager(Main app, Node rootNode, Node guiNode,
            AssetManager assetManager, InputManager inputManager, Camera camera,
            FlyByCamera flyCam, ViewPort viewPort,
            AppStateManager stateManager, AppSettings settings) {
        this.app = app;

        this.serverMan = new ServerManager(app, this);
        this.settingsMan = new SettingsManager(settings);

        this.GUIMan = new GUIManager(this.app);
        this.GUIMan.start();

        this.HUDMan = new HUDManager(rootNode, guiNode, assetManager,camera,
                flyCam, this.settingsMan);
        
        this.soundMan = new SoundManager(rootNode, assetManager, settingsMan);
        
        this.worldMan = new WorldManager(rootNode, assetManager, inputManager,
                camera, flyCam, viewPort, stateManager, this.settingsMan, this, soundMan);
        
        this.trophyMan = new TrophyManager(assetManager, rootNode, this, worldMan,
                settingsMan, HUDMan, soundMan, GUIMan);
        
        this.camera = camera;
    }

    @Override
    public void start() {
        this.GUIMan.show();

        // Start the camera with an initial location and rotation for the menu:
        this.camera.setLocation(new Vector3f(-25.528904f, 9.666285f, 88.33059f));
        this.camera.setRotation(new Quaternion(-0.036314048f, 0.5483832f, 0.023846028f, 0.83509797f));
        
        this.worldMan.makeWorld();
        this.GUIMan.main();
        this.soundMan.start();
    }
    
    public void destroy() {
        if (this.clientMan != null) {
            this.clientMan.disconnect();
        }
        if (this.serverMan != null) {
            this.serverMan.stop();
        }
    }
    
    public int getId() {
        if (this.clientMan != null) {
            return this.clientMan.getId();
        } else {
            return 0;
        }
    }
    
    public SettingsManager getSettings() {
        return this.settingsMan;
    }
    
    public ControllerManager getControls() {
        return this.worldMan.getControllerManager();
    }
    
    public TrophyManager getTrophies() {
        return this.trophyMan;
    }
    
    public WorldManager getWorld() {
        return this.worldMan;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public void host(boolean isHost) {
        this.isHost = isHost;
    }
    
    public boolean isHost() {
        return this.isHost;
    }
    
    public void startHost(Weather map) {
        this.serverMan.start(map);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public void connect() {
        this.GUIMan.show();

        if (this.isHost) {
            startHost(this.settingsMan.getWeather());
            this.GUIMan.start_overlay(true);
        } else {
            this.GUIMan.start_overlay(false);
        }
        
        this.clientMan = new ClientManager(app, this, this.worldMan, this.worldMan.getPlayerManager());
    }
    
    public void disconnect() {
        this.clientMan.disconnect();
        this.clientMan = null;
    }
    
    public void sendUpdate(VehicleControl vehicle) {
        if (this.multiplayer) {
            ActorUpdate update = new ActorUpdate(this.clientMan.getId(),
                    vehicle.getPhysicsLocation(), vehicle.getPhysicsRotationMatrix(),
                    vehicle.getLinearVelocity(), vehicle.getAngularVelocity());
            this.clientMan.update(update);
        }
    }
    
    public void sendCapture() {
        if (this.multiplayer) {
            this.clientMan.capture();
        }
    }
    
    public void sendPowerup(Powerup powerup) {
        if (this.multiplayer) {
            this.clientMan.powerup(powerup);
        }
    }

    public void startGame() {
        this.camera.setLocation(new Vector3f(0.0f, 0.0f, 10.0f));
        this.camera.setRotation(new Quaternion(0.0f, 1.0f, 0.0f, 0.0f));
        this.worldMan.remakeWorld();
        this.GUIMan.hide();
        this.HUDMan.start();
        this.HUDMan.display(HUDManager.GUIMode.Settings);
        this.HUDMan.display(HUDManager.GUIMode.Race);
        this.worldMan.start();
        this.HUDMan.setPlayerMan(this.worldMan.getPlayerManager());
        this.trophyMan.start();
        
        if (!this.multiplayer) {
            setRunning(true);
        }
    }
    
    public void triggerGame() {
        if (this.multiplayer) {
            this.serverMan.setState(State.START);
            setRunning(true);
        }
    }
    
    public void pauseGame(boolean pausedByMe) {
        if (this.running) {     // filter out repeat events
            if (this.multiplayer) {
                this.clientMan.pause();
            }
            
            this.running = false;
            this.worldMan.getPhysicsManager().pause();
            this.GUIMan.pause(pausedByMe);
            this.GUIMan.show();
        }
    }
    
    public void resumeGame() {
        if (!this.running) {
            if (this.multiplayer) {
                this.clientMan.resume();
            }
            this.worldMan.getPhysicsManager().resume();
            this.running = true;
            this.GUIMan.hide();
        }
    }
    
    public void endGame() {
        if (this.multiplayer && won()) {
            this.clientMan.end();
        }
        this.running = false;
        this.soundMan.stopEngineSound();
        this.HUDMan.removeHUD();
        this.GUIMan.end();
        this.GUIMan.show();
    }
    
    public void quitGame() {
        this.running = false;
        this.HUDMan.removeHUD();
        resetWorld();
    }
    
    public void resetWorld() {
        this.soundMan.stopEngineSound();
        this.worldMan.getControllerManager().reset();
        this.settingsMan.setWeather(SettingsManager.DEFAULT_WEATHER);
        this.settingsMan.setVehicle(SettingsManager.DEFAULT_VEHICLE);
        this.worldMan.remakeWorld();
        this.camera.setLocation(new Vector3f(-25.528904f, 9.666285f, 88.33059f));
        this.camera.setRotation(new Quaternion(-0.036314048f, 0.5483832f, 0.023846028f, 0.83509797f));
        this.GUIMan.main();
    }

    public void update() {
        if (this.setupComplete) {
            this.HUDMan.update(this.running);
            if (this.running) {
                this.trophyMan.update();
            }
            this.soundMan.updateEngineSound(this.worldMan.getPlayerManager().vehicle);
            this.worldMan.update();
        }
    }

    public void setSetupComplete(boolean isComplete) {
        this.setupComplete = isComplete;
        this.HUDMan.setStartTime();
    }
    
    public void setRunning(boolean running) {
        this.running = running;
        
        if (running) {
            this.GUIMan.hide();
            this.HUDMan.setStartTime();
            this.trophyMan.gameStart();
        }
    }
    
    public boolean isMultiplayer() {
        return multiplayer;
    }
    
    public void setMultiplayer(boolean multiplayer) {
        this.multiplayer = multiplayer;
    }
    
    public boolean won() {
        return this.trophyMan.getScore() == this.settingsMan.getWinningScore();
    }
}
