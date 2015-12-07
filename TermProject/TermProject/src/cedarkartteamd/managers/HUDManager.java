package cedarkartteamd.managers;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import java.text.DecimalFormat;

public class HUDManager implements ICedarKartManager {

    // Game Managers
    private PlayerManager playerMan;
    private final Node guiNode;
    private final Node rootNode;
    private final AssetManager assetManager;
    private SettingsManager settingsMan;
    private Camera camera;
    private FlyByCamera flyCam;
    private Picture miniMapPlayer;
    private Picture miniMapTrophy;
    private AdvPicture miniMapImage;
    private Picture speedometer;
    private Picture needle;
    private Node miniMap;
    private Picture background;
    private Picture instructionsBackground;
    private BitmapText hudText;
    private int terrainSize = 513;
    private Node markerNode;
    private Node trophyNode;
    private BitmapText currTrophy;
    private BitmapText instructions;
    private BitmapText gameTimes;
    private BitmapFont font;
    private long start;
    private long diff;
    private final float mmReduce = 1.8f;

    static enum GUIMode {

        Settings, Race
    };

    HUDManager(Node rootNode, Node guiNode, AssetManager assetManager,
            Camera camera, FlyByCamera flyCam, SettingsManager settingsMan) {
        this.guiNode = guiNode;
        this.assetManager = assetManager;
        this.camera = camera;
        this.flyCam = flyCam;
        this.settingsMan = settingsMan;
        this.rootNode = rootNode;

        font = assetManager.loadFont("Interface/Fonts/Default.fnt");

    }

    @Override
    public void start() {
        initSpeedometer();
        initMinimap();
        initBackgroundOverlay();
        initOverlay();
        initInstructionsBackground();
        initInstructions();
        //Win();
    }

    public void setPlayerMan(PlayerManager playerMan) {
        this.playerMan = playerMan;
    }

    public void initMinimap() {
        //I choose to have the minimap all be on one node so that it is easy
        //to move the whole thing
        miniMap = new Node();
        //The guiNode is defaultly there and is where you can add things to the
        //hud
        guiNode.attachChild(miniMap);

        //setting up the map itself
        //I am just using the texturing pic as the minimap
        miniMapImage = new AdvPicture("Mini Map image", true);
        miniMapImage.setImage(assetManager, "Interface/Game/Minimap.png", true);
        miniMapImage.setWidth(terrainSize / mmReduce);
        miniMapImage.setHeight(terrainSize / mmReduce);
        miniMap.attachChild(miniMapImage);

        //setting maker for the player
        miniMapPlayer = new Picture("mini Map Marker");
        //the true is for y inverting the image
        miniMapPlayer.setImage(assetManager, "Interface/Game/RedArrow.png", true);
        miniMapPlayer.setWidth(7);
        miniMapPlayer.setHeight(7);

        miniMapTrophy = new Picture("mini Map Marker");
        //the true is for y inverting the image
        miniMapTrophy.setImage(assetManager, "Interface/Game/book.png", true);
        miniMapTrophy.setWidth(15);
        miniMapTrophy.setHeight(15);
        //this is so it can be rotated around the center of the image rather
        //then around the bottom left corner
        markerNode = new Node();
        markerNode.attachChild(miniMapPlayer);
        trophyNode = new Node();
        trophyNode.attachChild(miniMapTrophy);

        //first 2 dimentions are for position on screen
        //3rd is for what is for forground and background
        //this translation puts the marker in the middle of the miniMeNode
        //which means rotations of the miniMeNode will make the marker spin
        // about its center
        miniMapPlayer.setLocalTranslation(-3.5f, -3.5f, 1);
        miniMapTrophy.setLocalTranslation(-5f, -5f, 1);

        miniMap.attachChild(markerNode);
        miniMap.attachChild(trophyNode);
        //posistions are with the lower left hand corner being (0,0)
        miniMap.setLocalTranslation(-10f, 0, 0);
    }

    public void initSpeedometer() {
        speedometer = new Picture("Speedometer");
        speedometer.setPosition(settingsMan.getWidth() - 220, 0);
        speedometer.setWidth(220);
        speedometer.setHeight(140);
        speedometer.setImage(assetManager, "Interface/Game/speedometer-50.png", true);

        needle = new Picture("Speedometer");
        needle.setPosition(settingsMan.getWidth() - 115, 30);
        needle.setWidth(6);
        needle.setHeight(94);
        needle.setImage(assetManager, "Interface/Game/speedometer-needle.png", true);
        setSpeed(50);

        guiNode.attachChild(speedometer);
        guiNode.attachChild(needle);
    }

    public void initBackgroundOverlay() {
        background = new Picture("Overlay");
        background.setPosition(settingsMan.getWidth() - 180, settingsMan.getHeight() - 100);
        background.setWidth(180);
        background.setHeight(100);
        background.setImage(assetManager, "Interface/Game/overlay.png", true);
        guiNode.attachChild(background);
    }

    public void initOverlay() {
        currTrophy = new BitmapText(font);
        currTrophy.setColor(ColorRGBA.White);
        currTrophy.setSize(font.getPreferredSize() * 1.5f);
        currTrophy.setText("Supplies: 0");
        currTrophy.setLocalTranslation(settingsMan.getWidth() - 140, settingsMan.getHeight() - 10, 0);

        gameTimes = new BitmapText(font);
        gameTimes.setColor(ColorRGBA.White);
        gameTimes.setText("Not Started");
        gameTimes.setLocalTranslation(settingsMan.getWidth() - 137, settingsMan.getHeight() - 45, 0);

        guiNode.attachChild(currTrophy);
        guiNode.attachChild(gameTimes);
    }
    
    public void initInstructionsBackground(){
        instructionsBackground = new Picture("Overlay");
        instructionsBackground.setPosition(settingsMan.getWidth() - 800, settingsMan.getHeight() - 400);
        instructionsBackground.setWidth(600);
        instructionsBackground.setHeight(150);
        instructionsBackground.setImage(assetManager, "Interface/Game/overlay.png", true);
        guiNode.attachChild(instructionsBackground);
    }
    
    public void initInstructions() {
        instructions = new BitmapText(font);
        instructions.setColor(ColorRGBA.White);
        instructions.setSize(font.getPreferredSize() * 1.5f);
        instructions.setText("Collect her school supplies as quickly as possible");
        instructions.setLocalTranslation(settingsMan.getWidth() - 750, settingsMan.getHeight() - 300, 0);

        guiNode.attachChild(instructions);
    }

    void display(GUIMode guiMode) {
    }

    public void update(boolean running) {
        //update minimap
        updatePlayerPosition();
        //update Speedometer
        setSpeed(playerMan.getVelocity());
        //update time
        if (running) {
            setTime();
        }
    }

    private void updatePlayerPosition() {
        //translation
        //making the the player marker moves on the minimap correctly
        Vector3f vehiclePos = playerMan.getLocation();
        float x, y;
        x = ((vehiclePos.x + terrainSize) / (terrainSize)) * miniMapImage.width;
        y = ((-vehiclePos.z + terrainSize) / (terrainSize)) * miniMapImage.height;
        markerNode.setLocalTranslation(x - terrainSize / (mmReduce * 2), y - terrainSize / (mmReduce * 2) + 12, 0);

        //rotation
        //makes the marker point in the same direction of the camera

        Quaternion rotation = playerMan.getRotation();
        float[] angles = {0f, 0f, 0f};
        rotation.toAngles(angles);

        //atan2 has to be used to make it be used for full rotation
        //rotation.fromAngleAxis((float) (Math.atan2(rotation.getZ(), rotation.getX()) + Math.PI / 4), new Vector3f(0, 0, 1));
        rotation.fromAngleAxis(angles[1] - FastMath.PI, new Vector3f(0, 0, 1));
        markerNode.setLocalRotation(rotation);
    }

    public void updateTrophyPosition(float X, float Z) {
        float x, y;
        x = ((X + terrainSize) / (terrainSize)) * miniMapImage.width;
        y = ((-Z + terrainSize) / (terrainSize)) * miniMapImage.height;
        trophyNode.setLocalTranslation(x - terrainSize / (mmReduce * 2), y - terrainSize / (mmReduce * 2) + 12, 0);
    }

    public void setSpeed(double mph) {
        int angle = (int) (mph * 108) / 80 - 108;
        needle.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.PI * angle / 180, new Vector3f(0, 0, 1)));
    }

    public void setScore(int trophies) {
        currTrophy.setText("Supplies " + trophies);
    }

    public void setStartTime() {
        this.start = System.currentTimeMillis();
    }

    public void setTime() {
        long end = System.currentTimeMillis();
        diff = (end - start) / 1000;
        DecimalFormat nf2 = new DecimalFormat("#00");
        gameTimes.setText("Time: " + (int) (diff / 60) + ":" + nf2.format(diff % 60));
        if ((diff % 60) == 4){
            instructionsBackground.removeFromParent();
            instructions.removeFromParent();
        }
           
    }
    
    public void removeHUD() {
        markerNode.removeFromParent();
        trophyNode.removeFromParent();
        miniMap.removeFromParent();
        needle.removeFromParent();
        speedometer.removeFromParent();
        background.removeFromParent();
        currTrophy.removeFromParent();
        gameTimes.removeFromParent();
    }
}
