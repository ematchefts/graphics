package cedarkartteamd.managers;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.scene.Node;

public class SoundManager implements ICedarKartManager {
    
    // audio nodes
    private AudioNode audioEngine;
    protected AudioNode trophyCollectedSound;
    protected AudioNode powerupCollectedSound;
    protected AudioNode backgroundSound;
    
    // Standard Managers
    private AssetManager assetManager;
    
    // Custom Managers
    private SettingsManager settingsMan;

    private Node rootNode;
    private boolean isAccelerating;
    private final float VOLUME = 1.0f;
    
    public SoundManager(Node rootNode, AssetManager assetManager, SettingsManager settingsMan) {

        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.settingsMan = settingsMan;
    }
    
    @Override
    public void start() {
        audioEngine = new AudioNode(assetManager, "Sounds/Engine/running_engine.wav", false);
        audioEngine.setPositional(true);
        audioEngine.setLooping(true);
        audioEngine.setPitch(0.5f);
        audioEngine.setVolume(0.9f);
        //vehicleNode.attachChild(audioEngine);
        //audioEngine.play();
        
        backgroundSound = new AudioNode(assetManager, "Sounds/Background/BackgroundSound.wav", false);
        backgroundSound.setPositional(false);
        backgroundSound.setLooping(true);
        backgroundSound.setVolume(0.18f);
        rootNode.attachChild(backgroundSound);
        backgroundSound.play();
        
        trophyCollectedSound = new AudioNode(assetManager, "Sounds/CollectionSounds/TrophyCollectedSound.wav", false);
        trophyCollectedSound.setPositional(false);
        trophyCollectedSound.setLooping(false);
        trophyCollectedSound.setVolume(1.0f);
        rootNode.attachChild(trophyCollectedSound);
        
        powerupCollectedSound = new AudioNode(assetManager, "Sounds/CollectionSounds/PowerupCollectedSound.wav", false);
        powerupCollectedSound.setPositional(false);
        powerupCollectedSound.setLooping(false);
        powerupCollectedSound.setVolume(1.0f);
        rootNode.attachChild(powerupCollectedSound);
    }

    public void setAccelerating(boolean isPressed) {
        isAccelerating = isPressed;
    }
    
    public void attachVehicleSound(Node vehicleNode){
        vehicleNode.attachChild(audioEngine);
        audioEngine.play();
    }
    
    public void stopEngineSound(){
        audioEngine.stop();
    }

    public void updateEngineSound(VehicleControl vehicle) {
        
        float pitch = vehicle.getCurrentVehicleSpeedKmHour()/100 + 0.5f;
        if(isAccelerating){
            if(pitch >= 0.5f && pitch <= 2.0f){
                audioEngine.setPitch(pitch);
            }
            else if(pitch > 2.0f){
                audioEngine.setPitch(2.0f);
            }
            else{
                audioEngine.setPitch(0.5f);
            }
            audioEngine.setVolume(1.0f);
            audioEngine.play();
        }
        else{
            audioEngine.setPitch(.5f);
            audioEngine.setVolume(.9f);
        }
    }

    public void playTrophySound(){
        trophyCollectedSound.play();
    }
    
    public void playPowerupSound(){
        powerupCollectedSound.play();
    }
}
