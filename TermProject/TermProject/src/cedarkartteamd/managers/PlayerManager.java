package cedarkartteamd.managers;

import cedarkartteamd.managers.SettingsManager.Weather;
import cedarkartteamd.managers.VehicleManager.VehicleSet;
import cedarkartteamd.managers.VehicleManager.Vehicle;
import cedarkartteamd.network.ActorInit;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.input.InputManager;
import com.jme3.scene.Spatial;
import com.jme3.scene.Node;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.Camera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.JoyAxisTrigger;
import com.jme3.input.controls.JoyButtonTrigger;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class PlayerManager implements ActionListener, AnalogListener, ICedarKartManager {

    // audio
    private AudioNode audio_engine;
    // For multiplayer:
    private final ActorInit init;
    // Standard Managers
    private AssetManager assetManager;
    private InputManager inputManager;
    // Custom Managers
    private GameManager gameMan;
    private PlacementManager placementMan;
    private PhysicsManager physicsManager;
    private SoundManager soundMan;
    private VehicleManager vehicleManager;
    private ControllerManager controls;
    private SettingsManager settingsMan;
    private Node rootNode;
    private ViewPort viewPort;
    private Camera camera;
    private FlyByCamera flyCam;
    private Spatial player;
    public ChaseCamera chaseCam;
    ParticleEmitter beeParticles;
    ParticleEmitter snowParticles = null;
    private boolean trayEnabled = false;
    Spatial tray;
    private float steeringisPressed = 0;
    private float accelerationisPressed = 0;
    private float accelerationForce;
    private float boostFactor = 2.0f;
    private boolean controllingCar = true;
    private boolean acceleratingForward = false;
    private boolean acceleratingBackward = false;
    private boolean steeringRight = false;
    private boolean steeringLeft = false;
    private Vector3f jumpForce;
    private Vector3f gravity;
    protected Node vehicleNode;
    protected VehicleControl vehicle;
    protected Vehicle vehicleType;

    public PlayerManager(Node rootNode, AssetManager assetManager,
            InputManager inputManager, Camera camera, FlyByCamera flyCam,
            ViewPort viewPort, GameManager gameMan, PlacementManager placementMan,
            PhysicsManager physicsManager, VehicleManager vehicleManager,
            ControllerManager controls, SettingsManager settingsMan,
            SoundManager soundMan, ActorInit init) {

        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.inputManager = inputManager;
        this.camera = camera;
        this.flyCam = flyCam;
        this.viewPort = viewPort;
        this.gameMan = gameMan;
        this.placementMan = placementMan;
        this.physicsManager = physicsManager;
        this.vehicleManager = vehicleManager;
        this.controls = controls;
        this.settingsMan = settingsMan;
        this.soundMan = soundMan;
        this.init = init;
    }

    /**
     * Simple create Character
     */
    @Override
    public void start() {
        if (this.init == null) {
            this.vehicleType = settingsMan.getVehicle();
        } else {
            this.vehicleType = Vehicle.valueOf(this.init.getVehicle());
        }

        player = assetManager.loadModel(this.vehicleType.path());
        player.setLocalScale(this.vehicleType.scale());
        accelerationForce = this.vehicleType.acceleration();
        jumpForce = this.vehicleType.jumpForce();

        buildVehicle(player);

        if (this.init == null) {
            setupChaseCamera();
            if (settingsMan.getWeather() == Weather.WINTER) {
                setupSnow();
            }
            //enableTray();
            setupBees();
            setupControls();
        }
    }

    /**
     * Builds the golf cart vehicle with physics
     *
     * @param player The designed object model
     */
    public void buildVehicle(Spatial player) {
        VehicleSet vSet = this.vehicleManager.makeCart(this.vehicleType, player, rootNode, settingsMan.getWeather());
        this.vehicleNode = vSet.getVehicleNode();
        this.vehicle = vSet.getVehicle();
        this.rootNode.attachChild(this.vehicleNode);
        this.physicsManager.getBulletAppState().getPhysicsSpace().add(this.vehicle);

        this.gravity = this.vehicle.getGravity();

        this.resetVehicle();
    }

    private void setupChaseCamera() {

        camera.setFrustumPerspective(45f, (float) camera.getWidth() / camera.getHeight(), 0.01f, 1000f);

        flyCam.setEnabled(false);
        flyCam.setMoveSpeed(30);
        flyCam.setDragToRotate(true);

        chaseCam = new ChaseCamera(camera, player, inputManager);

        chaseCam.setDefaultDistance(2.5f);
        chaseCam.setMaxDistance(3f);
        chaseCam.setDefaultVerticalRotation(0.2f);
        chaseCam.setTrailingSensitivity(5f);
        chaseCam.setChasingSensitivity(50f);
        chaseCam.setLookAtOffset(new Vector3f(0, 1, 0));
        chaseCam.setSmoothMotion(true);
        chaseCam.setTrailingEnabled(true);
    }

    private void setupBees() {
        beeParticles = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 3000);
        Material mat_bee = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat_bee.setTexture("Texture", assetManager.loadTexture("Models/trophies/powers/Bee3.png"));
        beeParticles.setShape(new EmitterSphereShape(Vector3f.ZERO, 2f));
        beeParticles.setMaterial(mat_bee);
        beeParticles.setImagesX(1);
        beeParticles.setImagesY(1);
        beeParticles.setParticlesPerSec(400);
        //       beeParticles.setEndColor(ColorRGBA.Yellow);
        beeParticles.setStartColor(ColorRGBA.Yellow);
        beeParticles.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, 1f, 0f)); // particles will move accordingly
        beeParticles.setStartSize(0.2f);
        beeParticles.setEndSize(0.4f);
        beeParticles.setGravity(0, 0, 0);
        beeParticles.setLowLife(1f);
        beeParticles.setHighLife(2f);
        beeParticles.getParticleInfluencer().setVelocityVariation(1f); // gives variation to the way the particles move
        beeParticles.setLocalTranslation(vehicleNode.getLocalTranslation().add(0f, 1f, 0f));
        beeParticles.setEnabled(false);
        rootNode.attachChild(beeParticles);
    }

    private void setupSnow() {
        snowParticles = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 3000);
        Material mat_snow = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat_snow.setTexture("Texture", assetManager.loadTexture("Textures/sky/snow1.png"));
        snowParticles.setShape(new EmitterSphereShape(Vector3f.ZERO, 5f));
        snowParticles.setMaterial(mat_snow);
        snowParticles.setImagesX(1);
        snowParticles.setImagesY(1);
        snowParticles.setParticlesPerSec(50);
        snowParticles.setEndColor(ColorRGBA.Gray);
        snowParticles.setStartColor(new ColorRGBA(.1f, .1f, .1f, 1f));
        snowParticles.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, -1f, 0f)); // particles will move accordingly
        snowParticles.setStartSize(0.5f);
        snowParticles.setEndSize(0.5f);
        snowParticles.setGravity(0, 0, 0);
        snowParticles.setLowLife(5f);
        snowParticles.setHighLife(5f);
        snowParticles.getParticleInfluencer().setVelocityVariation(.1f); // gives variation to the way the particles move
        snowParticles.setLocalTranslation(vehicleNode.getLocalTranslation().add(0f, 1f, 0f));
        snowParticles.setEnabled(true);
        rootNode.attachChild(snowParticles);
    }

    public void setBees(boolean enable) {
        beeParticles.setEnabled(enable);
        if (!enable) {
            beeParticles.killAllParticles();
        }
    }

    public void enableTray() {
        trayEnabled = true;
    }

    public void shootTray() {

        Vector3f vehicleDir = Vector3f.ZERO;
        vehicle.getForwardVector(vehicleDir);
        Box s = new Box(0.3f, 0.03f, 0.2f);
        Geometry geom = new Geometry("tray", s);
        geom.setLocalTranslation(vehicleNode.getLocalTranslation().add(vehicleDir.add(0f, 0.3f, 0f)));

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Brown);
//            mat.setTexture("ColorMap", assetManager.loadTexture(placement.filePath
//                    + placement.getName() + ".png"));
//            mat.setTexture("GlowMap", assetManager.loadTexture(placement.filePath
//                    + placement.getName() + "Glow.png"));
        geom.setMaterial(mat);

        rootNode.attachChild(geom);
        physicsManager.addBoxPhysics(geom, 5000, new Vector3f(0.3f, 0.03f, 0.2f));
        //physicsManager.debugPhysics(true);
        RigidBodyControl trayPhysics = (RigidBodyControl) geom.getControl(0);
        trayPhysics.setGravity(Vector3f.ZERO);
        trayPhysics.applyImpulse(vehicleDir.mult(100000), Vector3f.ZERO);
        trayEnabled = false;
    }

    public void impulsePowerup() {
        vehicle.applyImpulse(jumpForce.mult(3f), new Vector3f(FastMath.nextRandomFloat(), FastMath.nextRandomFloat(), FastMath.nextRandomFloat()));
    }

    public void setGravity(boolean state) {
        if (state) {
            this.vehicle.setGravity(this.gravity);
        } else {
            this.vehicle.setGravity(Vector3f.ZERO);
        }
    }

    public void randomizeLocation() {
        Random random = new Random(System.currentTimeMillis());
        float size = 512 * WorldManager.WORLD_SCALE;

        float x = (random.nextFloat() * size) - (size * 0.5f);
        float z = (random.nextFloat() * size) - (size * 0.5f);

        Vector3f newLoc = this.placementMan.getHeightAtLocation(x, z,
                this.gameMan.getWorld().terrain);
        newLoc.y += 1.0f;

        this.vehicle.setPhysicsLocation(newLoc);
    }

    private void setupControls() {
        int joystick = settingsMan.getController();

        if (joystick == -1) {
            //inputManager.addMapping("Tree", new KeyTrigger(KeyInput.KEY_T));
        
            inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
            inputManager.addMapping("Alt Left", new KeyTrigger(KeyInput.KEY_LEFT));
            inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
            inputManager.addMapping("Alt Right", new KeyTrigger(KeyInput.KEY_RIGHT));
            inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
            inputManager.addMapping("Alt Up", new KeyTrigger(KeyInput.KEY_UP));
            inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
            inputManager.addMapping("Alt Down", new KeyTrigger(KeyInput.KEY_DOWN));
            inputManager.addMapping("Brake", new KeyTrigger(KeyInput.KEY_B));
            inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
            inputManager.addMapping("Turbo", new KeyTrigger(KeyInput.KEY_LSHIFT));
            inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_F1));
            inputManager.addMapping("Detach", new KeyTrigger(KeyInput.KEY_F2));
            inputManager.addMapping("Location", new KeyTrigger(KeyInput.KEY_E));
            inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_ESCAPE));
            inputManager.addMapping("Powerup", new KeyTrigger(KeyInput.KEY_TAB));
            inputManager.addListener(this,"Tree", "Left", "Alt Left", "Right", "Alt Right",
                    "Up", "Alt Up", "Down", "Alt Down", "Brake", "Space", "Reset",
                    "Detach", "Turbo", "Location", "Pause", "Powerup");
        } else if (joystick < this.controls.availableJoysticks()) {
            inputManager.addMapping("LS Left", new JoyAxisTrigger(joystick, 1, true));
            inputManager.addMapping("LS Right", new JoyAxisTrigger(joystick, 1, false));
            inputManager.addMapping("Btn A", new JoyButtonTrigger(joystick, 0));
            inputManager.addMapping("Btn B", new JoyButtonTrigger(joystick, 1));
            inputManager.addMapping("Btn X", new JoyButtonTrigger(joystick, 2));
            inputManager.addMapping("Btn Y", new JoyButtonTrigger(joystick, 3)); // use stored pwrup?
            inputManager.addMapping("Btn Back", new JoyButtonTrigger(joystick, 6));
            inputManager.addMapping("Btn Start", new JoyButtonTrigger(joystick, 7));
            inputManager.addMapping("Btn LS", new JoyButtonTrigger(joystick, 8));

            inputManager.addListener(this, "LS Left", "LS Right", "Btn A",
                    "Btn B", "Btn Y", "Btn X", "Btn LS", "Btn Back", "Btn Start");
        }
    }

    @Override
    public void onAction(String action, boolean isPressed, float tpf) {
        float value = isPressed ? 1f : -1f;

        if (action.equals("Location") && !isPressed) {
            System.out.println("Location: " + camera.getLocation());
            System.out.println("Rotation: " + camera.getRotation());
            System.out.println("Direction: " + camera.getDirection());
            System.out.println("Vehicle location: " + getLocation());
            System.out.println("Vehicle rotation: " + getRotation());
        }
//         if (action.equals("Tree")&& !isPressed ) {
//            System.out.println("Location: " + camera.getLocation());
//            System.out.println("Rotation: " + camera.getRotation());
//            System.out.println("Direction: " + camera.getDirection());
//            System.out.println("Vehicle location: " + getLocation());
//            System.out.println("Vehicle rotation: " + getRotation());
//            String location = String.valueOf(camera.getLocation().x) + "f" + ", " + 
//                    /*String.valueOf(camera.getLocation().y) + "f"*/ "0f" + ", " + 
//                    String.valueOf(camera.getLocation().z) + "f";
//            
//            try{
//                Scanner sc = new Scanner( new File("C:\\Users\\rjacubec.CL-ENS242-09\\Desktop\\number.txt"));
//                String tree = sc.nextLine();
//                String code = "placements.add(new Placement(" + "\""+ tree + "\"" + ", \"Models/objects/trees/NormalTrees/pine.mesh.xml\", "+ location
//                    +", s 0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));" ; 
//                tree =  "Tree "+ String.valueOf(1 + Integer.valueOf(tree.substring(5, tree.length())));
//                PrintWriter pw = new PrintWriter(new FileOutputStream("C:\\Users\\rjacubec.CL-ENS242-09\\Desktop\\locations.txt", true));
//                pw.println(code);
//                pw.close();
//                pw = new PrintWriter(new File("C:\\Users\\rjacubec.CL-ENS242-09\\Desktop\\number.txt"));
//                pw.println(tree);
//                pw.close();
//            }
//            catch(Exception e){
//                System.out.println(e.toString());
//            }
//         }

        if ((action.equals("Left") || action.equals("Alt Left")) && controllingCar) {
            steeringisPressed += .2f * value;
            vehicle.steer(steeringisPressed);
        } else if ((action.equals("Right") || action.equals("Alt Right")) && controllingCar) {
            steeringisPressed += -.2f * value;
            vehicle.steer(steeringisPressed);
        } else if ((action.equals("Up") || action.equals("Alt Up") || action.equals("Btn A")) && controllingCar) {
            soundMan.setAccelerating(isPressed);
            accelerationisPressed += accelerationForce * value;
            vehicle.accelerate(accelerationisPressed);
            vehicle.brake(0f);
        } else if ((action.equals("Down") || action.equals("Alt Down") || action.equals("Btn B")) && controllingCar) {
            accelerationisPressed += -accelerationForce * value;

            if (vehicle.getCurrentVehicleSpeedKmHour() < 10) {
                vehicle.accelerate(accelerationisPressed);
                vehicle.brake(0f);
            } else {
                if (isPressed) {
                    vehicle.brake(50f);
                } else {
                    vehicle.brake(0f);
                }
            }
        } else if (action.equals("Brake")) {
            if (isPressed) {
                vehicle.brake(50f);
            } else {
                vehicle.brake(0f);
            }
        } else if ((action.equals("Space") || action.equals("Btn X"))) {
            if (isPressed && vehicle.getPhysicsLocation().y < 10f) {
                vehicle.applyImpulse(jumpForce, Vector3f.ZERO);
            }
        } else if ((action.equals("Reset") || action.equals("Btn Back"))) {
            if (isPressed) {
                resetVehicle();
            }
        } else if (action.equals("Detach")) {
            if (isPressed) {
                accelerationisPressed = 0f;
                vehicle.accelerate(accelerationisPressed);
                steeringisPressed = 0f;
                vehicle.steer(steeringisPressed);
                controllingCar = !controllingCar;
                flyCam.setEnabled(!flyCam.isEnabled());
                chaseCam.setEnabled(!chaseCam.isEnabled());
            }
        } else if (action.equals("Turbo") || action.equals("Btn LS")) {
            if (isPressed) {
//                soundMan.startAccelerating();
                vehicle.accelerate(accelerationForce * boostFactor);
            } else {
//                soundMan.stopAccelerating();
                vehicle.accelerate(0);
            }
        } else if (action.equals("Pause") || action.equals("Btn Start")) {
            this.gameMan.pauseGame(true);

        } else if (action.equals("Powerup") || action.equals("Btn Y")) {
            if (isPressed && trayEnabled) {
                shootTray();
            }
        }
    }
    

    @Override
    public void onAnalog(String action, float value, float tpf) {
        value = value / tpf;
        if (value >= ControllerManager.db || value <= -ControllerManager.db) {
            value = ControllerManager.shape(value);

            if (action.equals("LS Left")) {
                steeringisPressed = .2f * value;
                vehicle.steer(steeringisPressed);
            } else if (action.equals("LS Right")) {
                steeringisPressed = -.2f * value;
                vehicle.steer(steeringisPressed);
            }
        } else {
            if ((action.equals("LS Left") || action.equals("LS Right"))) {
                steeringisPressed = 0;
                vehicle.steer(steeringisPressed);
            }
        }
    }

    public double getVelocity() {
        return vehicle.getLinearVelocity().length();
    }
    private boolean newLap = false;

    public boolean isNewLap(Vector3f vector, double maxDistance) {
        double distance = vehicle.getPhysicsLocation().distance(vector);

        if (distance < maxDistance) {
            if (!newLap) {
                newLap = true;
                return true;
            }
        } else {
            newLap = false;
        }

        return false;
    }

    /**
     * Resets the Vehicle to starting position
     */
    public void initVehicle() {
        if (this.init == null) {
            vehicle.setPhysicsRotation(this.placementMan.playerRotation(this.gameMan.getId()));
            vehicle.setPhysicsLocation(this.placementMan.playerLocation(this.gameMan.getId()));
        } else {
            vehicle.setPhysicsRotation(this.placementMan.playerRotation(this.init.getId()));
            vehicle.setPhysicsLocation(this.placementMan.playerLocation(this.init.getId()));
        }
        vehicle.setLinearVelocity(Vector3f.ZERO);
        vehicle.setAngularVelocity(Vector3f.ZERO);
        vehicle.resetSuspension();
    }

    public void resetVehicle() {
        Vector3f newLoc = this.placementMan.randomResetPoint();
        vehicle.setPhysicsRotation(this.placementMan.rotationForLocation(newLoc));
        vehicle.setPhysicsLocation(newLoc);
        vehicle.setLinearVelocity(Vector3f.ZERO);
        vehicle.setAngularVelocity(Vector3f.ZERO);
        vehicle.resetSuspension();
    }

    public void update() {
        this.gameMan.sendUpdate(vehicle);
        beeParticles.setLocalTranslation(vehicleNode.getLocalTranslation().add(0f, 1f, 0f));
        if (snowParticles != null) {
            snowParticles.setLocalTranslation(vehicleNode.getLocalTranslation().add(0f, 1f, 0f));
        }
    }

    public Vector3f getLocation() {
        return vehicle.getPhysicsLocation();
    }

    public Quaternion getRotation() {
        return vehicle.getPhysicsRotation();
    }
}
