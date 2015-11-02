package cedarkartteamd;

import cedarkartteamd.managers.GameManager;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {
    // Constants
    public GameManager gameMan;

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setUseJoysticks(true);
        settings.setResolution(1024, 768);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void initialize() {
        super.initialize();
        setDisplayStatView(false);
    }

    @Override
    public void simpleInitApp() {
        this.gameMan = new GameManager(this, rootNode, guiNode,
                assetManager, inputManager, cam, flyCam, viewPort, stateManager,
                settings);
        gameMan.start();
    }

    @Override
    public void update() {
        super.update();
        gameMan.update();
    }

    @Override
    public void destroy() {
        gameMan.destroy();
        super.destroy();
    }

    public GameManager getGameMan() {
        return gameMan;
    }
}
