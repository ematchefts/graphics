
package cedarkartteamd.managers;

import com.jme3.app.SimpleApplication;
import com.jme3.input.InputManager;

/**
 *
 * @author Paul Marshall
 */
public class ControllerManager implements ICedarKartManager {

    public static final float db = 0.2f;
    private static final float k1 = 0.05f;
    private static final float k2 = 1.5f;
    
    private InputManager inputManager;
    
    public ControllerManager(InputManager inputManager) {
        this.inputManager = inputManager;
        this.inputManager.deleteMapping((SimpleApplication.INPUT_MAPPING_EXIT));
    }
    
    public static float shape(float value) {
        return value - db * (k1 + (k2 * Math.abs(value - db)));
    }
    
    @Override
    public void start() {}
    
    public int availableJoysticks() {
        int count = 0;
        
        if (inputManager.getJoysticks() != null) {
            count = inputManager.getJoysticks().length;
        }
        
        return count;
    }
    
    public void reset() {
        // Keybindings
        this.inputManager.deleteMapping("Left");
        this.inputManager.deleteMapping("Alt Left");
        this.inputManager.deleteMapping("Right");
        this.inputManager.deleteMapping("Alt Right");
        this.inputManager.deleteMapping("Up");
        this.inputManager.deleteMapping("Alt Up");
        this.inputManager.deleteMapping("Down");
        this.inputManager.deleteMapping("Alt Down");
        this.inputManager.deleteMapping("Brake");
        this.inputManager.deleteMapping("Space");
        this.inputManager.deleteMapping("Turbo");
        this.inputManager.deleteMapping("Reset");
        this.inputManager.deleteMapping("Detach");
        this.inputManager.deleteMapping("Location");
        this.inputManager.deleteMapping("Pause");
        
        // Joystick
        this.inputManager.deleteMapping("LS Left");
        this.inputManager.deleteMapping("LS Right");
        this.inputManager.deleteMapping("Btn A");
        this.inputManager.deleteMapping("Btn B");
        this.inputManager.deleteMapping("Btn X");
        this.inputManager.deleteMapping("Btn Start");
        this.inputManager.deleteMapping("Btn LS");
    }
}
