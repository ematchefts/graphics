package cedarkartteamd.managers;

import cedarkartteamd.Counter;
import cedarkartteamd.managers.SettingsManager.Level;
import cedarkartteamd.managers.SettingsManager.Weather;
import com.jme3.asset.AssetManager;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.LightNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shader.VarType;
import com.jme3.terrain.geomipmap.TerrainQuad;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author nabond
 */
public class PlacementManager implements ICedarKartManager {

    // Standard Manangers
    private final AssetManager assetManager;
    // Custom Manangers
    private final WorldManager worldMan;
    private final PhysicsManager physicsMan;
    private final SettingsManager settingsMan;
    private final Node rootNode;
    private final Counter nextPlayerLoc = new Counter();
    private final ArrayList<Vector3f> playerLocs = new ArrayList<>();
    private final ArrayList<Float> playerRots = new ArrayList<>();
    //trophy stuff
    private boolean directionUp = true;
    private float trophyHeight;

    public PlacementManager(AssetManager assetManager, Node rootNode,
            WorldManager worldMan, SettingsManager settingsMan) {
        this.assetManager = assetManager;
        this.worldMan = worldMan;
        this.physicsMan = worldMan.getPhysicsManager();
        this.settingsMan = settingsMan;
        this.rootNode = rootNode;
        
        if (this.playerLocs.size() != this.playerRots.size()) {
            throw new UnsupportedOperationException("There must be the same"
                    + " nummber of player locations as player rotations.  Check"
                    + " PlacementManager.initPlayerLocs() and"
                    + " PlacementManager.initPlayerRots().");
        }
    }

    public void start(TerrainQuad terrain) {
        //setWorldBox(terrain);
        addPlacements(createBuildingPlacements(), true);
        addPlacements(createObjectPlacements(), true);

        if (settingsMan.getWeather().equals(Weather.NIGHT)) {
            addLamps(createLampEmptyPlacements(), createLampGlowPlacements(),
                    true, terrain, settingsMan.advancedLighting());
        } else {
            addMoveablePlacements(createLampPostPlacements(), true, terrain);
        }

        if (settingsMan.getWeather().equals(Weather.WINTER)) {
            addMoveablePlacements(createWinterTreePlacements(), true, terrain);
            addPlacements(createWinterDetailPlacements(), true);
        } else {
            addMoveablePlacements(createTreePlacements(), true, terrain);
        }

        addAutoHeightPlacements(createBicyclePlacements(), true, terrain);

        addMoveablePlacements(createVehiclePlacements(), true, terrain);
        addMoveablePlacements(createBenchPlacements(), true, terrain);
        addAutoHeightPlacements(createDetailedModelPlacements(), true, terrain); // water tower and bee origin not at base so can't auto-height
        addMoveablePlacements(createMoveablePlacements(), true, terrain);

        initPlayerLocs(terrain);
        initPlayerRots();
    }

    private ArrayList<Placement> createObjectPlacements() {
        ArrayList<Placement> placements = new ArrayList<>();
        placements.add(new Placement("DMC Bridge", "Models/objects/bridge_dmc_tex/bridge_dmc_tex.mesh.xml", 43.5f, 7.2f, 71.5f, 2.8f, 0f, 0.41f, 0f, 0.9260f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("ENS Bridge", "Models/objects/bridge/bridge.obj", -22.3f, 7.1f, 140.3f, 0.40f, 0f, 0.5120f, 0f, 0.8590f, 0f, 0f, 0f, 0f));//-.3
        placements.add(new Placement("Spillway", "Models/objects/Spillway/spillway.mesh.xml", -22.2f, 6.47f, 140.2f, 1.8f, 0f, 1.5f, 0f, .38f, 0f, 0f, 0f, 0f));//-.2
        //placements.add(new Placement("Finish", "Models/objects/finishLine/finishLine.obj", -28.0f, 6.5f, 92.5f, 1f, 0.0000f, 0.8499f, 0.0000f, 0.5270f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("SSS Clock", "Models/objects/sscClock/sscClock.obj", -38.1f, 7.0f, 104.1f, 0.15f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Creation 1", "Models/objects/creation/creation.obj", -32.0f, 7.3f, 101.0f, 0.13f, 0.0000f, -0.5378f, 0.0000f, 0.8431f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Creation 2", "Models/objects/creation/creation.obj", -31.5f, 7.3f, 103.0f, 0.13f, 0.0000f, -0.7249f, 0.0000f, 0.6888f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Creation 3", "Models/objects/creation/creation.obj", -32.0f, 7.3f, 105.0f, 0.13f, 0.0000f, -0.8188f, 0.0000f, 0.5741f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Creation 4", "Models/objects/creation/creation.obj", -33.0f, 7.3f, 107.0f, 0.13f, 0.0000f, 0.8749f, 0.0000f, -0.4843f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Creation 5", "Models/objects/creation/creation.obj", -34.5f, 7.3f, 108.5f, 0.13f, 0.0000f, 0.9477f, 0.0000f, -0.3193f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Creation 6", "Models/objects/creation/creation.obj", -36.5f, 7.3f, 110.0f, 0.13f, 0.0000f, 0.9689f, 0.0000f, -0.2475f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Creation 7", "Models/objects/creation/creation.obj", -38.5f, 7.3f, 111.0f, 0.13f, 0.0000f, 0.9929f, 0.0000f, -0.1192f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Rock", "Models/objects/rock/rock.obj", -22.0f, 7.3f, 93.0f, 2.7f, 0.0000f, 0.8499f, 0.0000f, 0.5270f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lake Wall 1", "Models/objects/lakeWall/lakeWall.obj", -9.59998f, 4.5f, 92.7f, 0.53f, 0.0000f, 0.2532f, 0.0000f, 0.9674f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lake Wall 2", "Models/objects/lakeWall/lakeWall.obj", -16.25f, 4.5f, 96.45f, 0.53f, 0.0000f, 0.2532f, 0.0000f, 0.9674f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lake Wall 3", "Models/objects/lakeWall/lakeWall.obj", 9.7f, 4.5f, 115.3f, 0.53f, 0.0000f, 0.5191f, 0.0000f, 0.8547f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lake Wall 4", "Models/objects/lakeWall/lakeWall.obj", 6.18f, 4.5f, 122.08f, 0.53f, 0.0000f, 0.5191f, 0.0000f, 0.8547f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("BTS Statue", "Models/objects/btsStatue/btsStatue.obj", 54.5f, 7.8f, 83.1f, 0.55f, 0.0000f, 0.8663f, 0.0000f, -0.4995f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Flag Pole", "Models/objects/flagpole/flagpole.mesh.xml", 13.3f, 6.8f, 116.7f, 2f, 0f, -0.35f, 0f, -0.75f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Bent", "Models/objects/bent/bent.mesh.xml", 7.29f, 7.8f, 152.85f, .8f, 0f, 0.8660f, 0f, -.5f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Bike Rack 3", "Models/objects/bicycles/bikerack/bikerack.obj", -52.949947f, 9.6f, 72.150024f, 0.19f, 0.0f, 0.75544006f, 0.0f, 0.6552179f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Bike Rack 4", "Models/objects/bicycles/bikerack/bikerack.obj", -53.49994f, 9.6f, 70.1499f, 0.19f, 0.0f, 0.75544006f, 0.0f, 0.6552179f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Bike Rack 1", "Models/objects/bicycles/bikerack/bikerack.obj", 10.6f, 8.15f, 163.4f, 0.19f, 0.0000f, 0.0911f, 0.0000f, 0.9958f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Bike Rack 2", "Models/objects/bicycles/bikerack/bikerack.obj", 12.8f, 8.15f, 163.0f, 0.19f, 0.0000f, 0.0911f, 0.0000f, 0.9958f, 0f, 0f, 0f, 0f));

        return placements;
    }

    private ArrayList<Placement> createWinterDetailPlacements() {
        ArrayList<Placement> placements = new ArrayList<>();

        if (this.settingsMan.getWeather() == Weather.WINTER) {
            Placement ice = new Placement("Ice", "Models/objects/ice/ice.mesh.xml", 10f, 6f, 90f, 1.25f, 0.0f, 0.0f, 0.0f, 0.0f, 0f, 0f, 0f, 0f);
            ice.fiction = 0.01f;
            placements.add(ice);
        }

        return placements;
    }

    private ArrayList<Placement> createDetailedModelPlacements() {
        ArrayList<Placement> placements = new ArrayList<>();
        placements.add(new Placement("Water Tower", "Models/objects/tower/tower.mesh.xml", -40.5f, 0f, -38f, 1.5f, 0f, 4f, 0f, 0f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Opened Eyes", "Models/objects/openedEyes/statue.mesh.xml", -10.8f, 0f, 89.5f, 1f, 0f, -0.1918f, 0f, 0f, 0f, 0f, 0f, 0f));

        return placements;
    }

    private ArrayList<Placement> createMoveablePlacements() {
        ArrayList<Placement> placements = new ArrayList<>();
        placements.add(new Placement("Yellow Jacket", "Models/people/jacket/jacket.mesh.xml", -30.0f, .8f, 92f, 1.5f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));

        return placements;
    }

    private ArrayList<Placement> createLampPostPlacements() {
        ArrayList<Placement> placements = new ArrayList<>();

        placements.add(new Placement("Lamppost 1", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 21.1f, 1.435f, 23.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 2", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 9.9f, 1.435f, 18.8f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 3", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 2.7f, 1.435f, 30.7f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 4", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", -14.2f, 1.435f, 33.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 5", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 32.0f, 1.435f, 20.5f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 6", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 40.6f, 1.435f, 143.7f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 7", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 41.2f, 1.435f, 102.8f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 8", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 31.8f, 1.435f, 109.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 9", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 21.3f, 1.435f, 114.7f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 10", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 13.3f, 1.435f, 123.7f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 11", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 9.9f, 1.435f, 134.9f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 12", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 9.7f, 1.435f, 146.5f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 13", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 34.7f, 1.435f, -55.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 14", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 36.5f, 1.435f, -45.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 15", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 38.9f, 1.435f, -33.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 16", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 40.7f, 1.435f, -23.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 17", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 43.1f, 1.435f, -11.8f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 18", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 45.7f, 1.435f, 1.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 19", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 47.9f, 1.435f, 12.4f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 20", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 49.7f, 1.435f, 21.8f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 21", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 47.0f, 1.435f, 112.6f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 22", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 50.4f, 1.435f, 128.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 23", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 55.8f, 1.435f, 139.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 24", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 57.6f, 1.435f, 148.4f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 25", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 59.5f, 1.435f, 158.4f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 26", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 61.5f, 1.435f, 169.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 27", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 63.5f, 1.435f, 177.8f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 28", "Models/objects/lamppost_v2/lamppost_v2.mesh.xml", 65.5f, 1.435f, 189.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 29", "Models/objects/lamppost_v2_banner/lamppost_v2_banner.mesh.xml", -43.8f, 1.435f, 115.3f, 2.2f, 0.0000f, -0.1680f, 0.0000f, 0.9858f, 5000f, .2f, 1.43f, .2f));//+.5
        placements.add(new Placement("Lamppost 30", "Models/objects/lamppost_v2_banner/lamppost_v2_banner.mesh.xml", -46.6f, 1.435f, 123.1f, 2.2f, 0.0000f, -0.1680f, 0.0000f, 0.9858f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 31", "Models/objects/lamppost_v2_banner/lamppost_v2_banner.mesh.xml", -43.2f, 1.435f, 130.9f, 2.2f, 0.0000f, 0.5028f, 0.0000f, 0.8644f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 32", "Models/objects/lamppost_v2_banner/lamppost_v2_banner.mesh.xml", -36.0f, 1.435f, 134.7f, 2.2f, 0.0000f, 0.5028f, 0.0000f, 0.8644f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 33", "Models/objects/lamppost_v2_banner/lamppost_v2_banner.mesh.xml", -29.0f, 1.435f, 138.5f, 2.2f, 0.0000f, 0.5028f, 0.0000f, 0.8644f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 34", "Models/objects/lamppost_v2_banner/lamppost_v2_banner.mesh.xml", -17.6f, 1.435f, 144.5f, 2.2f, 0.0000f, 0.5028f, 0.0000f, 0.8644f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 35", "Models/objects/lamppost_v2_banner/lamppost_v2_banner.mesh.xml", -10.8f, 1.435f, 148.1f, 2.2f, 0.0000f, 0.5028f, 0.0000f, 0.8644f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 36", "Models/objects/lamppost_v2_banner/lamppost_v2_banner.mesh.xml", -3.8f, 1.435f, 151.7f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        //hill lights
        placements.add(new Placement("Lamppost 37","Models/objects/lamppost_v2_banner/lamppost_v2_banner.mesh.xml", 22.00449f, 1.435f, 233.24387f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 38","Models/objects/lamppost_v2_banner/lamppost_v2_banner.mesh.xml", 18.82552f, 1.435f, 233.94736f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 39", "Models/objects/lamppost_v2_banner/lamppost_v2_banner.mesh.xml", 13.139362f, 1.435f, 235.2057f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 40", "Models/objects/lamppost_v2_banner/lamppost_v2_banner.mesh.xml", 6.625704f,1.435f, 236.64716f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 41", "Models/objects/lamppost_v2_banner/lamppost_v2_banner.mesh.xml", 2.0005705f, 1.435f, 237.65288f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 42", "Models/objects/lamppost_v2_banner/lamppost_v2_banner.mesh.xml", -0.9674078f, 1.435f, 238.31314f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 43", "Models/objects/lamppost_v2_banner/lamppost_v2_banner.mesh.xml", -9.907354f, 1.435f, 240.74576f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 44", "Models/objects/lamppost_v2_banner/lamppost_v2_banner.mesh.xml", -6.6681237f, 1.435f, 239.46796f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        
        return placements;
    }

    private ArrayList<Placement> createLampEmptyPlacements() {
        ArrayList<Placement> placements = new ArrayList<>();

        placements.add(new Placement("Lamppost 1", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 21.1f, 1.435f, 23.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 2", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 9.9f, 1.435f, 18.8f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 3", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 2.7f, 1.435f, 30.7f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 4", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", -14.2f, 1.435f, 33.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 5", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 32.0f, 1.435f, 20.5f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 6", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 40.6f, 1.435f, 143.7f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 7", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 41.2f, 1.435f, 102.8f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 8", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 31.8f, 1.435f, 109.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 9", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 21.3f, 1.435f, 114.7f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 10", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 13.3f, 1.435f, 123.7f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 11", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 9.9f, 1.435f, 134.9f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 12", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 9.7f, 1.435f, 146.5f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 13", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 34.7f, 1.435f, -55.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 14", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 36.5f, 1.435f, -45.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 15", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 38.9f, 1.435f, -33.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 16", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 40.7f, 1.435f, -23.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 17", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 43.1f, 1.435f, -11.8f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 18", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 45.7f, 1.435f, 1.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 19", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 47.9f, 1.435f, 12.4f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 20", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 49.7f, 1.435f, 21.8f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 21", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 47.0f, 1.435f, 112.6f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 22", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 50.4f, 1.435f, 128.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 23", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 55.8f, 1.435f, 139.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 24", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 57.6f, 1.435f, 148.4f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 25", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 59.5f, 1.435f, 158.4f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 26", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 61.5f, 1.435f, 169.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 27", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 63.5f, 1.435f, 177.8f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 28", "Models/objects/lamppost_v2/lamppost_v2_noglass.mesh.xml", 65.5f, 1.435f, 189.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 29", "Models/objects/lamppost_v2_banner/lamppost_v2_banner_noglass.mesh.xml", -43.8f, 1.435f, 115.3f, 2.2f, 0.0000f, -0.1680f, 0.0000f, 0.9858f, 5000f, .2f, 1.43f, .2f));//+.5
        placements.add(new Placement("Lamppost 30", "Models/objects/lamppost_v2_banner/lamppost_v2_banner_noglass.mesh.xml", -46.6f, 1.435f, 123.1f, 2.2f, 0.0000f, -0.1680f, 0.0000f, 0.9858f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 31", "Models/objects/lamppost_v2_banner/lamppost_v2_banner_noglass.mesh.xml", -43.2f, 1.435f, 130.9f, 2.2f, 0.0000f, 0.5028f, 0.0000f, 0.8644f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 32", "Models/objects/lamppost_v2_banner/lamppost_v2_banner_noglass.mesh.xml", -36.0f, 1.435f, 134.7f, 2.2f, 0.0000f, 0.5028f, 0.0000f, 0.8644f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 33", "Models/objects/lamppost_v2_banner/lamppost_v2_banner_noglass.mesh.xml", -29.0f, 1.435f, 138.5f, 2.2f, 0.0000f, 0.5028f, 0.0000f, 0.8644f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 34", "Models/objects/lamppost_v2_banner/lamppost_v2_banner_noglass.mesh.xml", -17.6f, 1.435f, 144.5f, 2.2f, 0.0000f, 0.5028f, 0.0000f, 0.8644f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 35", "Models/objects/lamppost_v2_banner/lamppost_v2_banner_noglass.mesh.xml", -10.8f, 1.435f, 148.1f, 2.2f, 0.0000f, 0.5028f, 0.0000f, 0.8644f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 36", "Models/objects/lamppost_v2_banner/lamppost_v2_banner_noglass.mesh.xml", -3.8f, 1.435f, 151.7f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        //hill lights
        placements.add(new Placement("Lamppost 37", "Models/objects/lamppost_v2_banner/lamppost_v2_banner_noglass.mesh.xml", 22.00449f, 1.435f, 233.24387f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 38", "Models/objects/lamppost_v2_banner/lamppost_v2_banner_noglass.mesh.xml", 18.82552f, 1.435f, 233.94736f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 39", "Models/objects/lamppost_v2_banner/lamppost_v2_banner_noglass.mesh.xml", 13.139362f, 1.435f, 235.2057f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 40", "Models/objects/lamppost_v2_banner/lamppost_v2_banner_noglass.mesh.xml", 6.625704f,1.435f, 236.64716f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 41", "Models/objects/lamppost_v2_banner/lamppost_v2_banner_noglass.mesh.xml", 2.0005705f, 1.435f, 237.65288f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 42", "Models/objects/lamppost_v2_banner/lamppost_v2_banner_noglass.mesh.xml", -0.9674078f, 1.435f, 238.31314f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 43", "Models/objects/lamppost_v2_banner/lamppost_v2_banner_noglass.mesh.xml", -9.907354f, 1.435f, 240.74576f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        placements.add(new Placement("Lamppost 44", "Models/objects/lamppost_v2_banner/lamppost_v2_banner_noglass.mesh.xml", -6.6681237f, 1.435f, 239.46796f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 5000f, .2f, 1.43f, .2f));
        
        
        
        return placements;
    }

    private ArrayList<Placement> createLampGlowPlacements() {
        ArrayList<Placement> placements = new ArrayList<>();

        placements.add(new Placement("Lampglow 1", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 21.1f, 9.47f, 23.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 2", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 9.9f, 9.47f, 18.8f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 3", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 2.7f, 8.27f, 30.7f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 4", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", -14.2f, 8.47f, 33.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 5", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 32.0f, 9.37f, 20.5f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 6", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 40.6f, 7.77f, 143.7f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 7", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 41.2f, 6.77f, 102.8f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 8", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 31.8f, 6.77f, 109.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 9", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 21.3f, 6.77f, 114.7f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 10", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 13.3f, 6.77f, 123.7f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 11", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 9.9f, 6.77f, 134.9f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 12", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 9.7f, 7.57f, 146.5f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 13", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 34.7f, 9.37f, -55.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 14", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 36.5f, 9.37f, -45.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 15", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 38.9f, 9.37f, -33.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 16", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 40.7f, 9.37f, -23.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 17", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 43.1f, 9.37f, -11.8f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 18", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 45.7f, 9.37f, 1.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 19", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 47.9f, 9.37f, 12.4f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 20", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 49.7f, 9.37f, 21.8f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 21", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 47.0f, 7.57f, 112.6f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 22", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 50.4f, 7.57f, 128.2f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 23", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 55.8f, 7.57f, 139.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 24", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 57.6f, 7.57f, 148.4f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 25", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 59.5f, 7.57f, 158.4f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 26", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 61.5f, 7.57f, 169.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 27", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 63.5f, 7.57f, 177.8f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 28", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 65.5f, 7.57f, 189.0f, 2.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 29", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", -43.8f, 7.27f, 115.3f, 2.2f, 0.0000f, -0.1680f, 0.0000f, 0.9858f, 0f, 0f, 0f, 0f));//+.5
        placements.add(new Placement("Lampglow 30", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", -46.6f, 7.27f, 123.1f, 2.2f, 0.0000f, -0.1680f, 0.0000f, 0.9858f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 31", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", -43.2f, 7.27f, 130.9f, 2.2f, 0.0000f, 0.5028f, 0.0000f, 0.8644f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 32", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", -36.0f, 7.27f, 134.7f, 2.2f, 0.0000f, 0.5028f, 0.0000f, 0.8644f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 33", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", -29.0f, 7.27f, 138.5f, 2.2f, 0.0000f, 0.5028f, 0.0000f, 0.8644f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 34", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", -17.6f, 7.27f, 144.5f, 2.2f, 0.0000f, 0.5028f, 0.0000f, 0.8644f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 35", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", -10.8f, 7.27f, 148.1f, 2.2f, 0.0000f, 0.5028f, 0.0000f, 0.8644f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 36", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", -3.8f, 7.27f, 151.7f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 0f, 0f, 0f, 0f));
        //hill lights
        placements.add(new Placement("Lampglow 37", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 22.00449f, 7.27f, 233.24387f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 38", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 18.82552f, 7.27f, 233.94736f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 39", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 13.139362f, 7.27f, 235.2057f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 40", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 6.625704f,7.27f, 236.64716f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 41", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", 2.0005705f, 7.27f, 237.65288f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 42", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", -0.9674078f, 7.27f, 238.31314f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 43", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", -9.907354f, 7.27f, 240.74576f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lampglow 44", "Models/objects/lamppost_v2/lamppost_v2glowpart.j3o", -6.6681237f, 7.27f, 239.46796f, 2.2f, 0.0000f, 0.4695f, 0.0000f, 0.8829f, 0f, 0f, 0f, 0f));
        
        return placements;
    }

    private ArrayList<Placement> createBicyclePlacements() {
        ArrayList<Placement> placements = new ArrayList<>();

        placements.add(new Placement("Silver Bike 1", "Models/objects/bicycles/bike_silver/bike_silver.j3o", 13.5f, .369f, 162.6f, 0.13f, 0.0000f, 0.0707f, 0.0000f, 0.9975f, 150f, .5f, .369f, .1f));
        placements.add(new Placement("Green Bike 1", "Models/objects/bicycles/bike_green/bike_green.j3o", 12.8f, .369f, 162.8f, 0.13f, 0.0000f, 0.0707f, 0.0000f, 0.9975f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Blue Bike 1", "Models/objects/bicycles/bike_blue/bike_blue.j3o", 11.5f, .369f, 163.0f, 0.13f, 0.0000f, 0.0707f, 0.0000f, 0.9975f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Red Bike 1", "Models/objects/bicycles/bike_red/bike_red.j3o", 10.5f, .369f, 163.2f, 0.13f, 0.0000f, 0.0707f, 0.0000f, 0.9975f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Silver Bike 2", "Models/objects/bicycles/bike_silver/bike_silver.j3o", -53.049946f, .369f, 69.44986f, 0.13f, 0.0f, -0.60267645f, 0.0f, 0.7979857f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Green Bike 2", "Models/objects/bicycles/bike_green/bike_green.j3o", -53.049946f, .369f, 70.1f, 0.13f, 0.0f, -0.60267645f, 0.0f, 0.7979857f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Blue Bike 2", "Models/objects/bicycles/bike_blue/bike_blue.j3o", -52.549953f, .369f, 71.50f, 0.13f, 0.0f, -0.60267645f, 0.0f, 0.7979857f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Red Bike 2", "Models/objects/bicycles/bike_red/bike_red.j3o", -52.55f, .369f, 72.1f, 0.13f, 0.0f, -0.60267645f, 0.0f, 0.7979857f, 0f, 0f, 0f, 0f));

        return placements;
    }

    private ArrayList<Placement> createVehiclePlacements() {
        ArrayList<Placement> placements = new ArrayList<>();

        placements.add(new Placement("Car 1", "Models/vehicles/car_red/car.j3o", 93.00098f, 0.40f, 121.599915f, 0.3f, 0.0f, 0.081282794f, 0.0f, 0.99669105f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 2", "Models/vehicles/car_yellow/car.j3o", 95.35112f, 0.40f, 121.14989f, 0.3f, 0.0f, 0.081282794f, 0.0f, 0.99669105f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 3", "Models/vehicles/car_silver/car.j3o", 97.75127f, 0.40f, 120.59985f, 0.3f, 0.0f, 0.081282794f, 0.0f, 0.99669105f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 4", "Models/vehicles/car_blue/car.j3o", 90.90085f, 0.40f, 121.99994f, 0.3f, 0.0f, 0.081282794f, 0.0f, 0.99669105f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 5", "Models/vehicles/car_red/car.j3o", 84.50046f, 0.40f, 90.74803f, 0.3f, 0.0f, -0.6406502f, 0.0f, 0.7678329f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 6", "Models/vehicles/car_silver/car.j3o", 84.800476f, 0.40f, 92.74815f, 0.3f, 0.0f, -0.6406502f, 0.0f, 0.7678329f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 7", "Models/vehicles/car_blue/car.j3o", 85.2505f, 0.40f, 94.748276f, 0.3f, 0.0f, 0.758697f, 0.0f, 0.65144366f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 8", "Models/vehicles/car_yellow/car.j3o", 77.900055f, 0.40f, 96.29837f, 0.3f, 0.0f, -0.6325408f, 0.0f, 0.774527f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 9", "Models/vehicles/car_red/car.j3o", 77.45003f, 0.40f, 94.64827f, 0.3f, 0.0f, 0.7657362f, 0.0f, 0.64315486f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 10", "Models/vehicles/car_silver/car.j3o", 77.20001f, 0.40f, 92.598145f, 0.3f, 0.0f, -0.6322133f, 0.0f, 0.77479434f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 11", "Models/vehicles/car_blue/car.j3o", 68.64949f, 0.40f, 85.84773f, 0.3f, 0.0f, -0.6322133f, 0.0f, 0.77479434f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 12", "Models/vehicles/car_yellow/car.j3o", 69.29953f, 0.40f, 90.147995f, 0.3f, 0.0f, -0.6322133f, 0.0f, 0.77479434f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 13", "Models/vehicles/car_blue/car.j3o", 69.64955f, 0.40f, 91.8481f, 0.3f, 0.0f, -0.6322133f, 0.0f, 0.77479434f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 14", "Models/vehicles/car_red/car.j3o", 70.14958f, 0.40f, 93.248184f, 0.3f, 0.0f, -0.6322133f, 0.0f, 0.77479434f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 15", "Models/vehicles/car_silver/car.j3o", 70.54961f, 0.40f, 94.84828f, 0.3f, 0.0f, 0.7660074f, 0.0f, 0.6428317f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 16", "Models/vehicles/car_silver/car.j3o", 70.4996f, 0.40f, 96.198364f, 0.3f, 0.0f, -0.6319599f, 0.0f, 0.7750011f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 17", "Models/vehicles/car_red/car.j3o", 71.64967f, 0.40f, 101.89871f, 0.3f, 0.0f, -0.6319599f, 0.0f, 0.7750011f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 18", "Models/vehicles/car_blue/car.j3o", 15.500019f, 0.40f, 154.59946f, 0.3f, 0.0f, -0.6069951f, 0.0f, 0.7947055f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 19", "Models/vehicles/car_yellow/car.j3o", 16.10002f, 0.40f, 156.9996f, 0.3f, 0.0f, -0.6069951f, 0.0f, 0.7947055f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 20", "Models/vehicles/car_silver/car.j3o", 14.450015f, 0.40f, 150.99924f, 0.3f, 0.0f, -0.6069951f, 0.0f, 0.7947055f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 21", "Models/vehicles/car_blue/car.j3o", 22.249926f, 0.40f, 150.99924f, 0.3f, 0.0f, 0.77807915f, 0.0f, 0.6281663f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 22", "Models/vehicles/car_red/car.j3o", 22.899916f, 0.40f, 153.44939f, 0.3f, 0.0f, 0.77807915f, 0.0f, 0.6281663f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 23", "Models/vehicles/car_silver/car.j3o", 24.59989f, 0.40f, 160.84984f, 0.3f, 0.0f, 0.77807915f, 0.0f, 0.6281663f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 24", "Models/vehicles/car_yellow/car.j3o", 25.749872f, 0.40f, 166.45018f, 0.3f, 0.0f, 0.77807915f, 0.0f, 0.6281663f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 25", "Models/vehicles/car_silver/car.j3o", 14.250014f, 0.40f, 200.20224f, 0.3f, 0.0f, 0.78401124f, 0.0f, 0.62074673f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 26", "Models/vehicles/car_blue/car.j3o", 15.000017f, 0.40f, 202.60239f, 0.3f, 0.0f, 0.78401124f, 0.0f, 0.62074673f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 27", "Models/vehicles/car_silver/car.j3o", 16.350016f, 0.40f, 208.70276f, 0.3f, 0.0f, 0.78401124f, 0.0f, 0.62074673f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 28", "Models/vehicles/car_yellow/car.j3o", 8.249991f, 0.40f, 211.1029f, 0.3f, 0.0f, 0.78401124f, 0.0f, 0.62074673f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 29", "Models/vehicles/car_blue/car.j3o", 5.3499804f, 0.40f, 201.90234f, 0.3f, 0.0f, 0.78401124f, 0.0f, 0.62074673f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 30", "Models/vehicles/car_silver/car.j3o", 8.7999935f, 0.40f, 203.20242f, 0.3f, 0.0f, -0.6082859f, 0.0f, 0.793718f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 31", "Models/vehicles/car_silver/car.j3o", 10.049998f, 0.40f, 208.80276f, 0.3f, 0.0f, -0.6082859f, 0.0f, 0.793718f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 32", "Models/vehicles/car_yellow/car.j3o", -25.199455f, 0.40f, 55.59943f, 0.3f, 0.0f, -0.66401756f, 0.0f, 0.747717f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 33", "Models/vehicles/car_red/car.j3o", -24.699463f, 0.40f, 59.849365f, 0.3f, 0.0f, -0.66401756f, 0.0f, 0.747717f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 34", "Models/vehicles/car_blue/car.j3o", -23.499481f, 0.40f, 72.84984f, 0.3f, 0.0f, -0.66401756f, 0.0f, 0.747717f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 35", "Models/vehicles/car_silver/car.j3o", -23.499481f, 0.40f, 75.14998f, 0.3f, 0.0f, -0.66401756f, 0.0f, 0.747717f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 36", "Models/vehicles/car_yellow/car.j3o", -17.04958f, 0.40f, 71.74977f, 0.3f, 0.0f, 0.74199444f, 0.0f, 0.67040604f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 37", "Models/vehicles/car_red/car.j3o", -17.04958f, 0.40f, 68.699585f, 0.3f, 0.0f, 0.74199444f, 0.0f, 0.67040604f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 38", "Models/vehicles/car_silver/car.j3o", -17.04958f, 0.40f, 67.14949f, 0.3f, 0.0f, 0.74199444f, 0.0f, 0.67040604f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 39", "Models/vehicles/car_blue/car.j3o", -54.44901f, 0.40f, 55.199436f, 0.3f, 0.0f, 0.9995413f, 0.0f, -0.030285282f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 40", "Models/vehicles/car_silver/car.j3o", -56.198982f, 0.40f, 55.199436f, 0.3f, 0.0f, 0.9995413f, 0.0f, -0.030285282f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 41", "Models/vehicles/car_red/car.j3o", -57.348965f, 0.40f, 55.749428f, 0.3f, 0.0f, 0.9995413f, 0.0f, -0.030285282f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 42", "Models/vehicles/car_silver/car.j3o", -59.798927f, 0.40f, 55.949425f, 0.3f, 0.0f, 0.9995413f, 0.0f, -0.030285282f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 43", "Models/vehicles/car_blue/car.j3o", -65.59896f, 0.40f, 56.19942f, 0.3f, 0.0f, 0.9995413f, 0.0f, -0.030285282f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 44", "Models/vehicles/car_yellow/car.j3o", -69.39919f, 0.40f, 56.399418f, 0.3f, 0.0f, 0.9995413f, 0.0f, -0.030285282f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 45", "Models/vehicles/car_silver/car.j3o", -69.39919f, 0.40f, 52.199482f, 0.3f, 0.0f, 0.007805344f, 0.0f, 0.99996954f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 46", "Models/vehicles/car_red/car.j3o", -67.44907f, 0.40f, 51.59949f, 0.3f, 0.0f, 0.007805344f, 0.0f, 0.99996954f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 47", "Models/vehicles/car_blue/car.j3o", -64.39889f, 0.40f, 51.449493f, 0.3f, 0.0f, 0.007805344f, 0.0f, 0.99996954f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 48", "Models/vehicles/car_silver/car.j3o", -59.048943f, 0.40f, 50.44951f, 0.3f, 0.0f, 0.007805344f, 0.0f, 0.99996954f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 49", "Models/vehicles/car_yellow/car.j3o", -56.198986f, 0.40f, 50.44951f, 0.3f, 0.0f, 0.007805344f, 0.0f, 0.99996954f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 50", "Models/vehicles/car_blue/car.j3o", -53.799023f, 0.40f, 50.44951f, 0.3f, 0.0f, 0.007805344f, 0.0f, 0.99996954f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 51", "Models/vehicles/car_red/car.j3o", -55.548996f, 0.40f, 43.69961f, 0.3f, 0.0f, 0.9997784f, 0.0f, -0.021052048f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 52", "Models/vehicles/car_silver/car.j3o", -59.998928f, 0.40f, 39.499676f, 0.3f, 0.0f, 0.033375807f, 0.0f, 0.9994429f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 53", "Models/vehicles/car_yellow/car.j3o", -53.19903f, 0.40f, 38.54969f, 0.3f, 0.0f, 0.033375807f, 0.0f, 0.9994429f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 54", "Models/vehicles/car_silver/car.j3o", -16.84958f, 0.40f, -24.899918f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 55", "Models/vehicles/car_blue/car.j3o", -19.149546f, 0.40f, -22.899948f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 56", "Models/vehicles/car_red/car.j3o", -30.69937f, 0.40f, -40.149685f, 0.3f, 0.0f, -0.6498111f, 0.0f, 0.7600958f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 57", "Models/vehicles/car_silver/car.j3o", -30.199377f, 0.40f, -38.249714f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 58", "Models/vehicles/car_yellow/car.j3o", -29.249392f, 0.40f, -35.099762f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 59", "Models/vehicles/car_blue/car.j3o", -28.549402f, 0.40f, -32.899796f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 60", "Models/vehicles/car_silver/car.j3o", -28.349405f, 0.40f, -31.199821f, 0.3f, 0.0f, -0.6299521f, 0.0f, 0.776634f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 61", "Models/vehicles/car_red/car.j3o", -26.549433f, 0.40f, -25.099915f, 0.3f, 0.0f, -0.6427529f, 0.0f, 0.7660736f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 62", "Models/vehicles/car_silver/car.j3o", -14.299587f, 0.40f, -42.199654f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 63", "Models/vehicles/car_blue/car.j3o", -13.299583f, 0.40f, -37.999718f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 64", "Models/vehicles/car_yellow/car.j3o", -12.34958f, 0.40f, -33.599785f, 0.3f, 0.0f, -0.6425602f, 0.0f, 0.766235f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 65", "Models/vehicles/car_red/car.j3o", -11.499577f, 0.40f, -30.549831f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 66", "Models/vehicles/car_silver/car.j3o", -11.899578f, 0.40f, -32.249805f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 67", "Models/vehicles/car_blue/car.j3o", -9.6495695f, 0.40f, -23.199944f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 68", "Models/vehicles/car_silver/car.j3o", -21.149515f, 0.40f, -42.39965f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 69", "Models/vehicles/car_yellow/car.j3o", -23.949472f, 0.40f, -41.84966f, 0.3f, 0.0f, -0.6427529f, 0.0f, 0.7660736f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 70", "Models/vehicles/car_red/car.j3o", -23.549479f, 0.40f, -40.249683f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 71", "Models/vehicles/car_blue/car.j3o", -20.84952f, 0.40f, -41.04967f, 0.3f, 0.0f, -0.6239391f, 0.0f, 0.78147304f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 72", "Models/vehicles/car_silver/car.j3o", -22.999487f, 0.40f, -38.649708f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 73", "Models/vehicles/car_silver/car.j3o", -22.0995f, 0.40f, -34.199776f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 74", "Models/vehicles/car_red/car.j3o", -19.049547f, 0.40f, -33.19979f, 0.3f, 0.0f, -0.68375653f, 0.0f, 0.7297102f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 75", "Models/vehicles/car_blue/car.j3o", -18.599554f, 0.40f, -30.849827f, 0.3f, 0.0f, -0.6391657f, 0.0f, 0.76906914f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 76", "Models/vehicles/car_yellow/car.j3o", -17.799566f, 0.40f, -28.949856f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 77", "Models/vehicles/car_red/car.j3o", -21.649508f, 0.40f, -31.099823f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 78", "Models/vehicles/car_silver/car.j3o", 9.000465f, 0.40f, -57.849415f, 0.3f, 0.0f, 0.088500366f, 0.0f, 0.99607617f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 79", "Models/vehicles/car_silver/car.j3o", 7.050458f, 0.40f, -51.49951f, 0.3f, 0.0f, 0.9948778f, 0.0f, -0.10108441f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 80", "Models/vehicles/car_blue/car.j3o", 7.100458f, 0.40f, -57.249424f, 0.3f, 0.0f, 0.088500366f, 0.0f, 0.99607617f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 81", "Models/vehicles/car_red/car.j3o", 1.0004492f, 0.40f, -55.999443f, 0.3f, 0.0f, 0.088500366f, 0.0f, 0.99607617f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 82", "Models/vehicles/car_yellow/car.j3o", 1.5504487f, 0.40f, -50.19953f, 0.3f, 0.0f, 0.11171105f, 0.0f, 0.99374074f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 83", "Models/vehicles/car_silver/car.j3o", 4.450448f, 0.40f, -56.74943f, 0.3f, 0.0f, 0.9995333f, 0.0f, -0.030547919f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 84", "Models/vehicles/car_blue/car.j3o", -0.349551f, 0.40f, -49.799538f, 0.3f, 0.0f, 0.088500366f, 0.0f, 0.99607617f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 85", "Models/vehicles/car_red/car.j3o", -4.7495513f, 0.40f, -54.79946f, 0.3f, 0.0f, 0.9933848f, 0.0f, -0.11483362f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 86", "Models/vehicles/car_silver/car.j3o", -7.6495624f, 0.40f, -47.949566f, 0.3f, 0.0f, 0.99277574f, 0.0f, -0.11998499f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 87", "Models/vehicles/car_yellow/car.j3o", -14.599588f, 0.40f, -43.999626f, 0.3f, 0.0f, 0.7700068f, 0.0f, 0.6380357f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 88", "Models/vehicles/car_red/car.j3o", 37.450165f, 0.40f, 8.550006f, 0.3f, 0.0f, 0.088500366f, 0.0f, 0.99607617f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 89", "Models/vehicles/car_blue/car.j3o", 34.200214f, 0.40f, 9.350009f, 0.3f, 0.0f, 0.088500366f, 0.0f, 0.99607617f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 90", "Models/vehicles/car_silver/car.j3o", 31.550255f, 0.40f, 9.900011f, 0.3f, 0.0f, 0.088500366f, 0.0f, 0.99607617f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 91", "Models/vehicles/car_silver/car.j3o", 24.65036f, 0.40f, -3.2000067f, 0.3f, 0.0f, 0.9953092f, 0.0f, -0.09674505f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 92", "Models/vehicles/car_blue/car.j3o", 33.15023f, 0.40f, -5.250011f, 0.3f, 0.0f, 0.9953092f, 0.0f, -0.09674505f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 93", "Models/vehicles/car_red/car.j3o", 35.600193f, 0.40f, -5.500012f, 0.3f, 0.0f, 0.9953092f, 0.0f, -0.09674505f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 94", "Models/vehicles/car_yellow/car.j3o", 54.6999f, 0.40f, -20.599981f, 0.3f, 0.0f, 0.089309126f, 0.0f, 0.996004f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 95", "Models/vehicles/car_silver/car.j3o", 57.699856f, 0.40f, -21.049974f, 0.3f, 0.0f, 0.089309126f, 0.0f, 0.996004f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 96", "Models/vehicles/car_red/car.j3o", 64.59979f, 0.40f, -22.249956f, 0.3f, 0.0f, 0.089309126f, 0.0f, 0.996004f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 97", "Models/vehicles/car_blue/car.j3o", 66.54991f, 0.40f, -22.64995f, 0.3f, 0.0f, 0.089309126f, 0.0f, 0.996004f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 98", "Models/vehicles/car_silver/car.j3o", 61.849792f, 0.40f, -29.79984f, 0.3f, 0.0f, 0.99545777f, 0.0f, -0.09520385f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 99", "Models/vehicles/car_yellow/car.j3o", 58.84984f, 0.40f, -28.59986f, 0.3f, 0.0f, 0.99545777f, 0.0f, -0.09520385f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 100", "Models/vehicles/car_red/car.j3o", 56.74987f, 0.40f, -33.599785f, 0.3f, 0.0f, 0.08787441f, 0.0f, 0.99613154f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 101", "Models/vehicles/car_blue/car.j3o", 62.249786f, 0.40f, -34.949764f, 0.3f, 0.0f, 0.08787441f, 0.0f, 0.99613154f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 102", "Models/vehicles/car_silver/car.j3o", 61.849792f, 0.40f, -43.249638f, 0.3f, 0.0f, 0.9946239f, 0.0f, -0.10355363f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 103", "Models/vehicles/car_red/car.j3o", 55.199894f, 0.40f, -42.149654f, 0.3f, 0.0f, 0.9946239f, 0.0f, -0.10355363f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 104", "Models/vehicles/car_silver/car.j3o", 50.699963f, 0.40f, -41.69966f, 0.3f, 0.0f, 0.9946239f, 0.0f, -0.10355363f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 105", "Models/vehicles/car_yellow/car.j3o", 31.050262f, 0.40f, -28.349863f, 0.3f, 0.0f, -0.6232441f, 0.0f, 0.78202736f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 106", "Models/vehicles/car_red/car.j3o", 30.000278f, 0.40f, -31.699812f, 0.3f, 0.0f, -0.6232441f, 0.0f, 0.78202736f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 107", "Models/vehicles/car_blue/car.j3o", 28.900295f, 0.40f, -36.699738f, 0.3f, 0.0f, -0.6232441f, 0.0f, 0.78202736f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 108", "Models/vehicles/car_silver/car.j3o", 27.90031f, 0.40f, -41.399666f, 0.3f, 0.0f, -0.6232441f, 0.0f, 0.78202736f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 109", "Models/vehicles/car_red/car.j3o", 32.700237f, 0.40f, -47.299576f, 0.3f, 0.0f, 0.7825137f, 0.0f, 0.6226336f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 110", "Models/vehicles/car_yellow/car.j3o", 33.30023f, 0.40f, -44.149624f, 0.3f, 0.0f, 0.7825137f, 0.0f, 0.6226336f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 111", "Models/vehicles/car_blue/car.j3o", 35.500195f, 0.40f, -33.099792f, 0.3f, 0.0f, 0.7825137f, 0.0f, 0.6226336f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 112", "Models/vehicles/car_silver/car.j3o", 36.650177f, 0.40f, -27.549877f, 0.3f, 0.0f, 0.7825137f, 0.0f, 0.6226336f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 113", "Models/vehicles/car_red/car.j3o", 46.750023f, 0.40f, -76.65009f, 0.3f, 0.0f, 0.77987f, 0.0f, 0.62594163f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 114", "Models/vehicles/car_yellow/car.j3o", 47.400013f, 0.40f, -73.94993f, 0.3f, 0.0f, 0.77987f, 0.0f, 0.62594163f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 115", "Models/vehicles/car_blue/car.j3o", 48.399998f, 0.40f, -68.799614f, 0.3f, 0.0f, 0.77987f, 0.0f, 0.62594163f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 116", "Models/vehicles/car_red/car.j3o", 48.89999f, 0.40f, -66.29946f, 0.3f, 0.0f, 0.77987f, 0.0f, 0.62594163f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 117", "Models/vehicles/car_silver/car.j3o", 24.150368f, 0.40f, -62.749344f, 0.3f, 0.0f, -0.61958385f, 0.0f, 0.7849305f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 118", "Models/vehicles/car_blue/car.j3o", 23.550377f, 0.40f, -65.49941f, 0.3f, 0.0f, -0.61958385f, 0.0f, 0.7849305f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 119", "Models/vehicles/car_yellow/car.j3o", 23.000385f, 0.40f, -67.499535f, 0.3f, 0.0f, -0.61958385f, 0.0f, 0.7849305f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 120", "Models/vehicles/car_red/car.j3o", 22.250397f, 0.40f, -71.19976f, 0.3f, 0.0f, -0.61958385f, 0.0f, 0.7849305f, 0f, 0.45f, 0.40f, 1.26f));
        placements.add(new Placement("Car 121", "Models/vehicles/car_blue/car.j3o", 22.0004f, 0.40f, -73.09988f, 0.3f, 0.0f, -0.61958385f, 0.0f, 0.7849305f, 0f, 0.45f, 0.40f, 1.26f));

        placements.add(new Placement("Van 2", "Models/vehicles/van_silver/van.j3o", 23.399908f, 0.42f, 155.09949f, 0.3f, 0.0f, 0.77807915f, 0.0f, 0.6281663f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 1", "Models/vehicles/van_yellow/van.j3o", 15.000017f, 0.42f, 152.54933f, 0.3f, 0.0f, 0.78401124f, 0.0f, 0.62074673f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 20", "Models/vehicles/van_yellow/van.j3o", 27.400318f, 0.42f, -44.44962f, 0.3f, 0.0f, -0.6232441f, 0.0f, 0.78202736f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 21", "Models/vehicles/van_blue/van.j3o", 31.700253f, 0.42f, -50.099533f, 0.3f, 0.0f, 0.7825137f, 0.0f, 0.6226336f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 23", "Models/vehicles/van_silver/van.j3o", 36.200184f, 0.42f, -29.899841f, 0.3f, 0.0f, 0.7825137f, 0.0f, 0.6226336f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 22", "Models/vehicles/van_red/van.j3o", 34.750206f, 0.42f, -36.899734f, 0.3f, 0.0f, 0.7825137f, 0.0f, 0.6226336f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 19", "Models/vehicles/van_silver/van.j3o", 29.350288f, 0.42f, -34.299774f, 0.3f, 0.0f, -0.6232441f, 0.0f, 0.78202736f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 18", "Models/vehicles/van_red/van.j3o", 53.799915f, 0.42f, -42.149654f, 0.3f, 0.0f, 0.9946239f, 0.0f, -0.10355363f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 17", "Models/vehicles/van_blue/van.j3o", 58.549843f, 0.42f, -42.749645f, 0.3f, 0.0f, 0.9946239f, 0.0f, -0.10355363f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 16", "Models/vehicles/van_yellow/van.j3o", 59.149834f, 0.42f, -34.349773f, 0.3f, 0.0f, 0.08787441f, 0.0f, 0.99613154f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 15", "Models/vehicles/van_blue/van.j3o", 63.849762f, 0.42f, -30.249834f, 0.3f, 0.0f, 0.99545777f, 0.0f, -0.09520385f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 14", "Models/vehicles/van_red/van.j3o", 27.25032f, 0.42f, -3.6500063f, 0.3f, 0.0f, 0.9953092f, 0.0f, -0.09674505f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 13", "Models/vehicles/van_blue/van.j3o", 27.25032f, 0.42f, 10.300013f, 0.3f, 0.0f, 0.088500366f, 0.0f, 0.99607617f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 12", "Models/vehicles/van_blue/van.j3o", -66.199f, 0.42f, 43.69961f, 0.3f, 0.0f, 0.9997784f, 0.0f, -0.021052048f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 12", "Models/vehicles/van_yellow/van.j3o", -53.799023f, 0.42f, 43.69961f, 0.3f, 0.0f, 0.9997784f, 0.0f, -0.021052048f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 11", "Models/vehicles/van_red/van.j3o", -61.19891f, 0.42f, 51.449493f, 0.3f, 0.0f, 0.007805344f, 0.0f, 0.99996954f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 10", "Models/vehicles/van_blue/van.j3o", -62.748882f, 0.42f, 56.19942f, 0.3f, 0.0f, 0.9995413f, 0.0f, -0.030285282f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 7", "Models/vehicles/van_silver/van.j3o", 8.149991f, 0.42f, 201.00229f, 0.3f, 0.0f, -0.6082859f, 0.0f, 0.793718f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 9", "Models/vehicles/van_blue/van.j3o", -17.04958f, 0.42f, 70.04967f, 0.3f, 0.0f, 0.74199444f, 0.0f, 0.67040604f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 4", "Models/vehicles/van_yellow/van.j3o", 6.849986f, 0.42f, 208.30273f, 0.3f, 0.0f, 0.78401124f, 0.0f, 0.62074673f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 5", "Models/vehicles/car_red/car.j3o", 6.599985f, 0.42f, 206.65263f, 0.3f, 0.0f, 0.78401124f, 0.0f, 0.62074673f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 6", "Models/vehicles/van_red/van.j3o", 5.799982f, 0.42f, 204.5525f, 0.3f, 0.0f, 0.78401124f, 0.0f, 0.62074673f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 8", "Models/vehicles/van_yellow/van.j3o", -24.499466f, 0.42f, 62.999317f, 0.3f, 0.0f, -0.66401756f, 0.0f, 0.747717f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 3", "Models/vehicles/van_silver/van.j3o", 15.70002f, 0.42f, 205.40256f, 0.3f, 0.0f, 0.78401124f, 0.0f, 0.62074673f, 0f, 0.48f, 0.42f, 1.21f));
        placements.add(new Placement("Van 3", "Models/vehicles/van_blue/van.j3o", 25.149881f, 0.42f, 164.40005f, 0.3f, 0.0f, 0.77807915f, 0.0f, 0.6281663f, 0f, 0.48f, 0.42f, 1.21f));
        
        return placements;
    }

    private ArrayList<Placement> createTreePlacements() {
        ArrayList<Placement> placements = new ArrayList<>();

        placements.add(new Placement("Tree 1", "Models/objects/trees/NormalTrees/tree_w_berm.mesh.xml", 77.2f, 0f, 90.3f, 0.19f, 0.0000f, 0.7380f, 0.0000f, 0.6748f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 2", "Models/objects/trees/NormalTrees/tree_w_berm.mesh.xml", 84.1f, 0f, 89.0f, 0.19f, 0.0000f, 0.7380f, 0.0000f, 0.6748f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 3", "Models/objects/trees/NormalTrees/tree_w_berm.mesh.xml", 78.2f, 0f, 98.8f, 0.19f, 0.0000f, 0.7380f, 0.0000f, 0.6748f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 4", "Models/objects/trees/NormalTrees/tree_w_berm.mesh.xml", 85.6f, 0f, 97.1f, 0.19f, 0.0000f, 0.7380f, 0.0000f, 0.6748f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 5", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 7.2f, 0f, 157.6f, 0.30f, 0.0000f, 0.3067f, 0.0000f, 0.9518f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 6", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 28.4f, 0f, 183.5f, 0.30f, 0.0000f, 0.3067f, 0.0000f, 0.9518f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 7", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 55.5f, 0f, 174.5f, 0.30f, 0.0000f, 0.3067f, 0.0000f, 0.9518f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 8", "Models/objects/trees/NormalTrees/tree1.mesh.xml", -40.0f, 0f, 71.5f, 0.14f, 0.0000f, 0.3568f, 0.0000f, 0.9342f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 9", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 10.0f, 0f, 160.0f, 0.17f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 10", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 30.9f, 0f, -69.2f, 0.17f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 11", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 40.7f, 0f, -66.4f, 0.17f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 12", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 30.436161f, 0f, -14.662198f, 0.17f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 13", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 21.39861f, 0f, -13.0660095f, 0.17f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 14", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 48.58228f, 0f, -56.89865f, 0.17f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 15", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 16.833647f, 0f, -48.683327f, 0.17f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 16", "Models/objects/trees/NormalTrees/tree2.mesh.xml", 33.7f, 0f, -22.6f, 0.38f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 17", "Models/objects/trees/NormalTrees/tree2.mesh.xml", 53.0f, 0f, 160.2f, 0.38f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 18", "Models/objects/trees/NormalTrees/tree2.mesh.xml", 62.7f, 0f, 44.6f, 0.38f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 19", "Models/objects/trees/NormalTrees/tree2.mesh.xml", 81.8f, 0f, 44.7f, 0.46f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 20", "Models/objects/trees/NormalTrees/tree2.mesh.xml", 96.7f, 0f, 84.3f, 0.46f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 21", "Models/objects/trees/NormalTrees/tree2.mesh.xml", 60.5f, 0f, 165.0f, 0.33f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 22", "Models/objects/trees/NormalTrees/tree2.mesh.xml", 59.0f, 0f, 180.5f, 0.33f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 23", "Models/objects/trees/NormalTrees/tree2.mesh.xml", 39.0f, 0f, -74.1f, 0.33f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 24", "Models/objects/trees/NormalTrees/tree2.mesh.xml", 14.356249f, 0f, -11.822203f, 0.33f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 25", "Models/objects/trees/NormalTrees/tree2.mesh.xml", 4.3861866f, 0f, 4.1969695f, 0.33f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 26", "Models/objects/trees/NormalTrees/tree2.mesh.xml", 2.9274583f, 0f, -45.13456f, 0.33f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 27", "Models/objects/trees/NormalTrees/pine.mesh.xml", 63.8f, 0f, 174.5f, 1f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 28", "Models/objects/trees/NormalTrees/pine.mesh.xml", 30.6f, 0f, 190.9f, 1f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 29", "Models/objects/trees/NormalTrees/pine.mesh.xml", 50.4f, 0f, 144.2f, 1f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 30", "Models/objects/trees/NormalTrees/pine.mesh.xml", 57.0f, 0f, 19.2f, 0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 21", "Models/objects/trees/NormalTrees/pine.mesh.xml", -7.4f, 0f, 19.6f, 0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 32", "Models/objects/trees/NormalTrees/pine.mesh.xml", 62.892357f, 0f, -61.81623f, 0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 33", "Models/objects/trees/NormalTrees/pine.mesh.xml", 9.394054f, 0f, -46.809696f, 0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 34", "Models/objects/trees/NormalTrees/pine.mesh.xml", 7.288539f, 0f, -1.104029f, 0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 35", "Models/objects/trees/NormalTrees/pine.mesh.xml", 33f, 0f, -63.3f, 0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
placements.add(new Placement("Tree 36", "Models/objects/trees/NormalTrees/pine.mesh.xml", -28.203997f, 0f, 47.099125f, 0.6f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 37", "Models/objects/trees/NormalTrees/pine.mesh.xml", -25.453264f, 0f, 78.43215f,  0.4f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 38", "Models/objects/trees/NormalTrees/pine.mesh.xml", -25.215363f, 0f, 80.43215f,  0.9f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 39", "Models/objects/trees/NormalTrees/pine.mesh.xml", -25.79609f, 0f, 82.43215f,  0.6f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 39", "Models/objects/trees/NormalTrees/pine.mesh.xml", -25.79609f, 0f, 84.43215f,  0.4f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 39", "Models/objects/trees/NormalTrees/pine.mesh.xml", -25.79609f, 0f, 86.43215f,  0.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 40", "Models/objects/trees/NormalTrees/pine.mesh.xml", -38.336216f, 0f, 82.35618f,0.4f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 41", "Models/objects/trees/NormalTrees/pine.mesh.xml", -25.926771f, 0f, 69.39488f, 0.3f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 42", "Models/objects/trees/NormalTrees/pine.mesh.xml", -27.459005f, 0, 64.94669f, 0.5f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 43", "Models/objects/trees/NormalTrees/pine.mesh.xml", -27.149876f, 0f, 61.20359f,  0.4f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 44", "Models/objects/trees/NormalTrees/pine.mesh.xml", -27.304193f, 0f, 57.652203f, 0.6f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 45", "Models/objects/trees/NormalTrees/pine.mesh.xml", -28.203997f, 0f, 45.099125f, 0.3f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 46", "Models/objects/trees/NormalTrees/pine.mesh.xml", -28.203997f, 0f, 49.099125f, 0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 47", "Models/objects/trees/NormalTrees/pine.mesh.xml", -28.203997f, 0f, 51.099125f, 0.6f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 48", "Models/objects/trees/NormalTrees/pine.mesh.xml", -28.203997f, 0f, 53.099125f, 0.6f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 49", "Models/objects/trees/NormalTrees/pine.mesh.xml", -28.203997f, 0f, 55.099125f, 0.34f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 50", "Models/objects/trees/NormalTrees/pine.mesh.xml", -28.203997f, 0f, 59.099125f, 0.78f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 51", "Models/objects/trees/NormalTrees/pine.mesh.xml", -28.203997f, 0f, 66.099125f, 0.43f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 52", "Models/objects/trees/NormalTrees/pine.mesh.xml", -28.203997f, 0f, 68.099125f, 0.556f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 53", "Models/objects/trees/NormalTrees/tree1.mesh.xml", -0.80040777f, 0f, 86.53253f,  0.26f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 54", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 5.450004f, 0f, 83.1357f,  0.43f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 55", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 14.7508f, 0f, 78.705795f,  0.3f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 56", "Models/objects/trees/NormalTrees/tree2.mesh.xml", 23.306847f, 0f, 75.1006f,  0.3f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 57", "Models/objects/trees/NormalTrees/tree2.mesh.xml", 29.19138f, 0f, 72.848274f,  0.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 58", "Models/objects/trees/NormalTrees/pine.mesh.xml", 62.864952f, 0f, 82.957924f,  0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 59", "Models/objects/trees/NormalTrees/pine.mesh.xml", 61.720745f, 0f, 78.20403f,  0.65f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 60", "Models/objects/trees/NormalTrees/pine.mesh.xml", 60.961662f, 0f, 74.5365f,  0.5f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 61", "Models/objects/trees/NormalTrees/pine.mesh.xml", 58.23333f, 0f, 75.138306f,  0.52f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 62", "Models/objects/trees/NormalTrees/pine.mesh.xml", 58.711983f, 0f, 77.34934f,  0.87f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 63", "Models/objects/trees/NormalTrees/pine.mesh.xml", 55.401184f, 0f, 75.34773f,  0.56f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 64", "Models/objects/trees/NormalTrees/pine.mesh.xml", 56.913944f, 0f, 69.99133f,  0.45f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 65", "Models/objects/trees/NormalTrees/pine.mesh.xml", 58.30478f, 0f, 71.74782f, 0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 70", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 18.270597f, 0f, 77.13105f, .34f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 71", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 33.093697f, 0f, 71.33228f, .3f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 72", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 34.757076f, 0f, 70.64953f, .31f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 73", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 1.3602672f, 0f, 85.07595f, .36f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 74", "Models/objects/trees/NormalTrees/tree2.mesh.xml", 6.9960027f, 0f, 79.52537f, .3f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 75", "Models/objects/trees/NormalTrees/tree2.mesh.xml", 14.355156f, 0f, 71.37179f, .3f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 76", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 25.475555f, 0f, 68.57982f, .28f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 77", "Models/objects/trees/NormalTrees/tree1.mesh.xml", 29.387754f, 0f, 70.87052f, .3f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 78", "Models/objects/trees/NormalTrees/pine.mesh.xml", 55.047264f, 0f, 170.39151f, 0.75f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 79", "Models/objects/trees/NormalTrees/pine.mesh.xml", 65.10013f, 0f, 168.94363f, 0.73f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 80", "Models/objects/trees/NormalTrees/pine.mesh.xml", 66.23416f, 0f, 178.48375f,  0.71f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 81", "Models/objects/trees/NormalTrees/pine.mesh.xml", 71.97295f, 0f, 174.49896f,  0.74f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 82", "Models/objects/trees/NormalTrees/pine.mesh.xml", 69.289764f, 0f, 169.7602f,  0.74f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 83", "Models/Trees/WeepingWillow/tree.j3o", -6.062182f, 0f, 90.91095f,  0.5f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 84", "Models/Trees/WeepingWillow/tree.j3o", 19.765871f, 0f, 75.54302f,  0.5f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 85", "Models/Trees/WeepingWillow/tree.j3o", -17.6928f, 0f, 96.52544f,  0.5f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 87", "Models/objects/trees/NormalTrees/pine.mesh.xml", -42.316334f, 0f, 139.4815f,  0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 88", "Models/objects/trees/NormalTrees/pine.mesh.xml", -41.27574f, 0f, 145.90015f,  0.45f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 89", "Models/objects/trees/NormalTrees/pine.mesh.xml", -40.130177f, 0f, 151.81831f,  0.54f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 90", "Models/objects/trees/NormalTrees/pine.mesh.xml", -39.078587f, 0f, 157.62346f,  0.43f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 91", "Models/objects/trees/NormalTrees/pine.mesh.xml", -37.211777f, 0f, 166.73755f, 0.39f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 92", "Models/objects/trees/NormalTrees/pine.mesh.xml", -35.113598f, 0f, 175.34508f,  0.5f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 93", "Models/objects/trees/NormalTrees/pine.mesh.xml", -44.212116f, 0f, 130.40736f,  0.62f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 94", "Models/objects/trees/NormalTrees/tree1.mesh.xml", -41.91491f, 0f, 179.78564f, .34f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 95", "Models/objects/trees/NormalTrees/tree1.mesh.xml", -42.524734f, 0f, 178.39624f, .29f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 96", "Models/objects/trees/NormalTrees/tree2.mesh.xml", -46.75807f, 0f, 174.41728f, .25f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 97", "Models/objects/trees/NormalTrees/tree2.mesh.xml", -45.017654f, 0f, 171.5656f, .25f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 98", "Models/objects/trees/NormalTrees/tree1.mesh.xml", -43.75302f, 0f, 168.60536f, .31f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 99", "Models/objects/trees/NormalTrees/tree1.mesh.xml", -43.90448f, 0f, 166.0874f, .32f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 100", "Models/objects/trees/NormalTrees/pine.mesh.xml", -43.009567f, 0f, 164.06436f,  0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 101", "Models/objects/trees/NormalTrees/pine.mesh.xml", -46.706097f, 0f, 163.36f,  0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 102", "Models/objects/trees/NormalTrees/pine.mesh.xml", -49.6497f, 0f, 165.27925f,  0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 103", "Models/objects/trees/NormalTrees/tree2.mesh.xml", -52.35759f, 0f, 168.06946f,  .31f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 104", "Models/objects/trees/NormalTrees/tree2.mesh.xml", -54.288345f, 0f, 171.91992f, .3f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 105", "Models/objects/trees/NormalTrees/tree1.mesh.xml", -54.281372f, 0f, 175.6624f, .25f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 106", "Models/objects/trees/NormalTrees/tree1.mesh.xml", -53.044815f, 0f, 179.46721f, .29f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 107", "Models/objects/trees/NormalTrees/tree2.mesh.xml", -51.52011f, 0f, 182.43103f, .26f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 108", "Models/objects/trees/NormalTrees/pine.mesh.xml", -50.661297f, 0f, 184.10045f,  0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 109", "Models/objects/trees/NormalTrees/pine.mesh.xml", -46.53146f, 0f, 141.9691f,  0.65f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 110", "Models/objects/trees/NormalTrees/pine.mesh.xml", -45.180878f, 0f, 148.19997f, .54f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 111", "Models/objects/trees/NormalTrees/pine.mesh.xml", -44.39747f, 0f, 152.74803f, .72f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 112", "Models/objects/trees/NormalTrees/pine.mesh.xml", -43.874744f, 0f, 156.24483f, .76f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        //hill trees
        placements.add(new Placement("Tree 113", "Models/Trees/AspenAutumn/AspenAutumn.j3o", -9.335286f, 0f, 241.97876f,  0.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 114", "Models/Trees/AspenAutumn/AspenAutumn.j3o", -3.572348f, 0f, 240.59966f,  0.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 115", "Models/Trees/AspenAutumn/AspenAutumn.j3o", 5.9943175f, 0f, 238.273f,  0.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 116", "Models/Trees/AspenAutumn/AspenAutumn.j3o", 10.231564f, 0f, 237.29636f,  0.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 117", "Models/Trees/AspenAutumn/AspenAutumn.j3o", 15.5114565f, 0f, 235.94147f,  0.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 118", "Models/Trees/AspenAutumn/AspenAutumn.j3o", 21.070602f, 0f, 234.54227f,  0.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 119", "Models/Trees/AspenAutumn/AspenAutumn.j3o", 26.418161f, 0f, 233.31406f,  0.2f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        //end hill trees
       placements.add(new Placement("Tree 121", "Models/objects/trees/NormalTrees/pine.mesh.xml", 69.4682f, 0f, 143.61539f,  0.87f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
       placements.add(new Placement("Tree 122", "Models/objects/trees/NormalTrees/pine.mesh.xml", 71.9465f, 0f, 146.9894f,  0.78f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
       placements.add(new Placement("Tree 120", "Models/objects/trees/NormalTrees/pine.mesh.xml", 68.936134f, 0f, 134.38359f,  0.56f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
       
        return placements;
    }

    private ArrayList<Placement> createWinterTreePlacements() {
        ArrayList<Placement> placements = new ArrayList<>();

        placements.add(new Placement("Tree 1", "Models/objects/trees/WinterTrees/tree_w_berm.mesh.xml", 77.2f, 0f, 90.3f, 0.19f, 0.0000f, 0.7380f, 0.0000f, 0.6748f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 2", "Models/objects/trees/WinterTrees/tree_w_berm.mesh.xml", 84.1f, 0f, 89.0f, 0.19f, 0.0000f, 0.7380f, 0.0000f, 0.6748f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 3", "Models/objects/trees/WinterTrees/tree_w_berm.mesh.xml", 78.2f, 0f, 98.8f, 0.19f, 0.0000f, 0.7380f, 0.0000f, 0.6748f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 4", "Models/objects/trees/WinterTrees/tree_w_berm.mesh.xml", 85.6f, 0f, 97.1f, 0.19f, 0.0000f, 0.7380f, 0.0000f, 0.6748f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 5", "Models/objects/trees/WinterTrees/tree1.mesh.xml", 7.2f, 0f, 157.6f, 0.30f, 0.0000f, 0.3067f, 0.0000f, 0.9518f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 6", "Models/objects/trees/WinterTrees/tree1.mesh.xml", 28.4f, 0f, 183.5f, 0.30f, 0.0000f, 0.3067f, 0.0000f, 0.9518f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 7", "Models/objects/trees/WinterTrees/tree1.mesh.xml", 55.5f, 0f, 174.5f, 0.30f, 0.0000f, 0.3067f, 0.0000f, 0.9518f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 8", "Models/objects/trees/WinterTrees/tree1.mesh.xml", -40.0f, 0f, 71.5f, 0.14f, 0.0000f, 0.3568f, 0.0000f, 0.9342f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 9", "Models/objects/trees/WinterTrees/tree1.mesh.xml", 10.0f, 0f, 160.0f, 0.17f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 10", "Models/objects/trees/WinterTrees/tree1.mesh.xml", 30.9f, 0f, -69.2f, 0.17f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 11", "Models/objects/trees/WinterTrees/tree1.mesh.xml", 40.7f, 0f, -66.4f, 0.17f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 12", "Models/objects/trees/WinterTrees/tree1.mesh.xml", 30.436161f, 0f, -14.662198f, 0.17f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 13", "Models/objects/trees/WinterTrees/tree1.mesh.xml", 21.39861f, 0f, -13.0660095f, 0.17f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 14", "Models/objects/trees/WinterTrees/tree1.mesh.xml", 48.58228f, 0f, -56.89865f, 0.17f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 15", "Models/objects/trees/WinterTrees/tree1.mesh.xml", 16.833647f, 0f, -48.683327f, 0.17f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 16", "Models/objects/trees/WinterTrees/tree2.mesh.xml", 33.7f, 0f, -22.6f, 0.38f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 17", "Models/objects/trees/WinterTrees/tree2.mesh.xml", 53.0f, 0f, 160.2f, 0.38f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 18", "Models/objects/trees/WinterTrees/tree2.mesh.xml", 62.7f, 0f, 44.6f, 0.38f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 19", "Models/objects/trees/WinterTrees/tree2.mesh.xml", 81.8f, 0f, 44.7f, 0.46f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 20", "Models/objects/trees/WinterTrees/tree2.mesh.xml", 96.7f, 0f, 84.3f, 0.46f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 21", "Models/objects/trees/WinterTrees/tree2.mesh.xml", 60.5f, 0f, 165.0f, 0.33f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 22", "Models/objects/trees/WinterTrees/tree2.mesh.xml", 59.0f, 0f, 180.5f, 0.33f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 23", "Models/objects/trees/WinterTrees/tree2.mesh.xml", 39.0f, 0f, -74.1f, 0.33f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 24", "Models/objects/trees/WinterTrees/tree2.mesh.xml", 14.356249f, 0f, -11.822203f, 0.33f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 25", "Models/objects/trees/WinterTrees/tree2.mesh.xml", 4.3861866f, 0f, 4.1969695f, 0.33f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 26", "Models/objects/trees/WinterTrees/tree2.mesh.xml", 2.9274583f, 0f, -45.13456f, 0.33f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 27", "Models/objects/trees/WinterTrees/pine.mesh.xml", 63.8f, 0f, 174.5f, 1f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 28", "Models/objects/trees/WinterTrees/pine.mesh.xml", 30.6f, 0f, 190.9f, 1f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 29", "Models/objects/trees/WinterTrees/pine.mesh.xml", 50.4f, 0f, 144.2f, 1f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 30", "Models/objects/trees/WinterTrees/pine.mesh.xml", 57.0f, 0f, 19.2f, 0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 21", "Models/objects/trees/WinterTrees/pine.mesh.xml", -7.4f, 0f, 19.6f, 0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 32", "Models/objects/trees/WinterTrees/pine.mesh.xml", 62.892357f, 0f, -61.81623f, 0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 33", "Models/objects/trees/WinterTrees/pine.mesh.xml", 9.394054f, 0f, -46.809696f, 0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 34", "Models/objects/trees/WinterTrees/pine.mesh.xml", 7.288539f, 0f, -1.104029f, 0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));
        placements.add(new Placement("Tree 35", "Models/objects/trees/WinterTrees/pine.mesh.xml", 33f, 0f, -63.3f, 0.7f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 0f, .2f, 2f, .2f));

        return placements;
    }

    private ArrayList<Placement> createBenchPlacements() {
        ArrayList<Placement> placements = new ArrayList<>();

        placements.add(new Placement("Bench 1", "Models/objects/bench/bench.mesh.xml", -32.8f, 0.325f, 101.3f, 0.18f, 0.0000f, -0.5892f, 0.0000f, 0.8080f, 200f, 0.51f, 0.325f, 0.21f));
        placements.add(new Placement("Bench 2", "Models/objects/bench/bench.mesh.xml", -33.6f, 0.325f, 105.9f, 0.18f, 0.0000f, 0.8828f, 0.0000f, -0.4696f, 200f, 0.51f, 0.325f, 0.21f));
        placements.add(new Placement("Bench 3", "Models/objects/bench/bench.mesh.xml", -37.8f, 0.325f, 109.2f, 0.18f, 0.0000f, 0.9998f, 0.0000f, 0.0187f, 200f, 0.51f, 0.325f, 0.21f));
        placements.add(new Placement("Bench 4", "Models/objects/bench/bench.mesh.xml", 48.2f, 0.325f, 18.2f, 0.18f, 0.0000f, 0.7638f, 0.0000f, 0.6455f, 200f, 0.51f, 0.325f, 0.21f));
        placements.add(new Placement("Bench 5", "Models/objects/bench/bench.mesh.xml", 47.7f, 0.325f, 21.5f, 0.18f, 0.0000f, -0.1811f, 0.0000f, 0.9835f, 200f, 0.51f, 0.325f, 0.21f));
        placements.add(new Placement("Bench 6", "Models/objects/bench/bench.mesh.xml", 44.5f, 0.325f, 20.3f, 0.18f, 0.0000f, -0.2190f, 0.0000f, 0.9757f, 200f, 0.51f, 0.325f, 0.21f));
        placements.add(new Placement("Bench 7", "Models/objects/bench/bench.mesh.xml", -4.3f, 0.325f, 15.2f, 0.18f, 0.0000f, 0.505f, 0.0000f, 0.8631f, 200f, 0.51f, 0.325f, 0.21f));

        return placements;
    }

    private ArrayList<Placement> createBuildingPlacements() {
        ArrayList<Placement> placements = new ArrayList<>();
        placements.add(new Placement("WRLD", "Models/buildings/worldBox/Cube.mesh.xml", 0f, 0f, 0f, 254.5f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("DMC", "Models/buildings/dmc/dmc.mesh.xml", 11f, 7f, 50f, 1.10f, 0f, -0.0699f, 0f, 0.9976f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("SSC", "Models/buildings/ssc/ssc.mesh.xml", -55f, 5.7f, 100f, 1.75f, 0f, 0.7726f, 0f, 0.6349f, 0f, 0f, 0f, 0f));
        // BTS WAS HERE
        placements.add(new Placement("ENS", "Models/buildings/ens/ens.mesh.xml", 4f, 6.5f, 172f, 3.95f, 0f, 0.0799f, 0f, 0.9968f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("MIL", "Models/buildings/milner/milner.mesh.xml", 37f, 7.0f, 155f, 0.70f, 0f, 0.0762f, 0f, 0.9971f, 0f, 0f, 0f, 0f));
        // APPLE WAS HERE
        placements.add(new Placement("LB", "Models/buildings/library/library.mesh.xml", 27f, 3.6f, 124.5f, 0.65f, 0f, -0.6910f, 0f, 0.7229f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("X", "Models/buildings/btsstairs/btsStairs.mesh.xml", 50f, 2.65f, 95.4f, 2.50f, 0f, -0.6372f, 0f, 0.7707f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("BRO", "Models/buildings/brock/brock.mesh.xml", 32f, 10.8f, -88f, 1.50f, 0f, 0.0810f, 0f, 0.9967f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("DFH", "Models/buildings/callan/callan.mesh.xml", -36f, 7.0f, 5f, 23f, 0f, -0.0853f, 0f, 0.9964f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("FH", "Models/buildings/founders/founders.mesh.xml", 92.1f, 11.2f, 187.0f, 1.03f, 0f, 0.0086f, 0f, 1f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("LAW", "Models/buildings/lawlorPrinty/lawlorPrinty.mesh.xml", 66f, 7.2f, 7f, 0.55f, 0f, 0.9956f, 0f, -0.0935f, 0f, 0f, 0f, 0f));//
        placements.add(new Placement("PR", "Models/buildings/lawlorPrinty/lawlorPrinty.mesh.xml", 31f, 7.2f, 4f, 0.55f, 0f, 0.1085f, 0f, 0.9941f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("MX", "Models/buildings/maddox/maddox.mesh.xml", 79f, 9.2f, 73f, 1.2f, 0f, 0.0810f, 0f, 0.9967f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("MCCH", "Models/buildings/mcchesney/mcchesney.mesh.xml", 9.7f, 7.8f, -30.5f, 4.50f, 0f, 0.1118f, 0f, 0.9337f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("TYL", "Models/buildings/tyler/tyler.obj", 53.6f, 7.9f, 195.5f, 3.4f, 0f, 0.7694f, 0f, 0.6388f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("PAT", "Models/buildings/patterson/patterson.obj", 111.1f, 8.5f, 147.8f, 2.48f, 0f, 0.0693f, 0f, 0.9976f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("WIL", "Models/buildings/willets/willets.obj", 61.2f, 6.0f, -33.2f, 0.81f, 0f, -0.6327f, 0f, 0.7744f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("WH", "Models/buildings/williams/williams.obj", 80.0f, 8.5f, 162.5f, 3.27f, 0f, 0.1005f, 0f, 0.9949f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("HRS", "Models/buildings/safety/safety.obj", 81.6f, 9.4f, 171.5f, 0.52f, 0f, 0.0984f, 0f, 0.9951f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("PHB", "Models/buildings/pharmacy/PHB.mesh.xml", -60f, 7.0f, 137f, 2f, 0f, -.4f, 0f, -0.2f, 0f, 0f, 0f, 0f));//subbed 8 from x,z
        placements.add(new Placement("SoccerBall", "Models/objects/soccer/soccerball.mesh.xml", -172.11838f, 9.5f, -81.93248f, 2f, 0.027875403f, 0.68859464f, -0.02650756f, 0.72412544f, 250f, .2f, .85f, .2f));
		placements.add(new Placement("GOAL", "Models/objects/goal/Goal.mesh.xml", -170.5267f, 7.6f, -36.082657f, 2f, 0.027875403f, 0.68859464f, -0.02650756f, 0.72412544f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("GOALs", "Models/objects/goal/Goal.mesh.xml",-170.5267f, 7.6f, -152.53018f, 2f, -0.0068384954f, -0.7758777f, -0.0084116375f, 0.63079023f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lights1", "Models/objects/lights/fieldLight2.mesh.xml",-139.20836f, 4f, -55.836918f, 1f, 0.030133301f, -0.048293725f, 0.0014576054f, 0.9983775f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lights2", "Models/objects/lights/fieldLight2.mesh.xml",-139.20836f, 4f, -85.77015f, 1f, 0.030133301f, -0.048293725f, 0.0014576054f, 0.9983775f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lights3", "Models/objects/lights/fieldLight2.mesh.xml",-139.20836f, 4f, -115.10829f, 1f, 0.030133301f, -0.048293725f, 0.0014576054f, 0.9983775f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lights4", "Models/objects/lights/fieldLight2.mesh.xml",-199.64964f, 4f, -55.836918f, 1f, -0.0027182824f, 0.9956995f, -0.031026676f, -0.08724916f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lights5", "Models/objects/lights/fieldLight2.mesh.xml",-199.64964f, 4f, -85.77015f, 1f,  -0.0027182824f, 0.9956995f, -0.031026676f, -0.08724916f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("Lights6", "Models/objects/lights/fieldLight2.mesh.xml",-199.64964f, 4f, -115.10829f, 1f,  -0.0027182824f, 0.9956995f, -0.031026676f, -0.08724916f, 0f, 0f, 0f, 0f));
        
        placements.add(new Placement("HLL", "Models/buildings/thehill/thehill.mesh.xml", 14f, 11f, 243f, 1.2f, 0f, 1f, 0f, -0.1f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("EPL", "Models/buildings/epl/epl.mesh.xml", -10f, 8.0f, 188f, 1.9f, 0f, -0.65f, 0f, 0.75f, 0f, 0f, 0f, 0f));
        placements.add(new Placement("CHM", "Models/buildings/chemlabs/ChemLabs.mesh.xml", -42f, 4.9f, 207f, 0.9f, 0f, 0.85f, 0f, 0.50f, 0f, 0f, 0f, 0f));
        
		
        if (this.settingsMan.getLevel() == Level.LOW) {
            placements.add(new Placement("BTS", "Models/buildings/bts/bts.mesh.xml", 56f, 2.55f, 95f, 2.50f, 0f, -0.6372f, 0f, 0.7707f, 0f, 0f, 0f, 0f));
            placements.add(new Placement("APP", "Models/buildings/apple/apple.mesh.xml", 77f, 9.0f, 123f, 6.10f, 0f, 0.9957f, 0f, -0.0925f, 0f, 0f, 0f, 0f));
        } else if (this.settingsMan.getLevel() == Level.MEDIUM) {
            placements.add(new Placement("BTS", "Models/buildings/bts_high/bts.mesh.xml", 56f, 2.55f, 95f, 2.50f, 0f, -0.6372f, 0f, 0.7707f, 0f, 0f, 0f, 0f));
            placements.add(new Placement("APP", "Models/buildings/apple_high/apple.mesh.xml", 77f, 8.9f, 123f, 6.10f, 0f, 0.9957f, 0f, -0.0925f, 0f, 0f, 0f, 0f));
        } else {
            placements.add(new Placement("BTS", "Models/buildings/bts_high/bts.j3o", 56f, 2.55f, 95f, 2.50f, 0f, -0.6372f, 0f, 0.7707f, 0f, 0f, 0f, 0f));
            placements.add(new Placement("APP", "Models/buildings/apple_high/apple.j3o", 77f, 8.9f, 123f, 6.10f, 0f, 0.9957f, 0f, -0.0925f, 0f, 0f, 0f, 0f));
        }

        return placements;
    }

    //This one is a little different in how the Trophies are dealt with:
    //The names of the trophies should be the base names of the Texture files
    //The filepath is the path where the textures are to be found.
    public ArrayList<Placement> createTrophyPlacements() {
        ArrayList<Placement> placements = new ArrayList<>();

        placements.add(new Placement("book", "Models/trophies/supplies/", 8.58f, 9.08f, 161.36833f, .5f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("laptop", "Models/trophies/supplies/", 65.49f, 8.28f, -34.76f, .5f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("notebook", "Models/trophies/supplies/", 10.19f, 8.66f, -32.77f, .5f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("pencil", "Models/trophies/supplies/", -33.69f, 8.05f, 104.86f, .5f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("ruler", "Models/trophies/supplies/", 46.08f, 7.87f, 90.30f, .5f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("tablet", "Models/trophies/supplies/", -20.66f, 8.65f, 140.98f, .5f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("eraser", "Models/trophies/supplies/", 44.66f, 9.11f, 159.73f, .5f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("pen", "Models/trophies/supplies/", 81.49f, 9.14f, 93.52f, .5f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("book", "Models/trophies/supplies/", -37.62f, 8.80f, 1.71f, .5f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("notebook", "Models/trophies/supplies/", -59.88f, 8.10f, 53.60f, .5f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));

        return placements;
    }

    public ArrayList<Placement> createPowerUpPlacements() {
        ArrayList<Placement> placements = new ArrayList<>();

        placements.add(new Placement("Power1", "Models/trophies/powers/", 43.20f, 6.61f, 71.51f, .2f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("Power2", "Models/trophies/powers/", 12.71f, 7.38f, 116.03f, .2f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("Power3", "Models/trophies/powers/", -27.13f, 5.45f, 149.20f, .2f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("Power4", "Models/trophies/powers/", -0.30f, 8.08f, 72.66f, .2f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("Power5", "Models/trophies/powers/", 35.89f, 7.96f, 58.10f, .2f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("Power6", "Models/trophies/powers/", 72.70f, 8.37f, -4.81f, .2f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("Power7", "Models/trophies/powers/", 24.74f, 8.15f, 16.26f, .2f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("Power8", "Models/trophies/powers/", 55.23f, 7.49f, 83.42f, .2f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("Power9", "Models/trophies/powers/", 44.17f, 8.33f, 197.06f, .2f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));
        placements.add(new Placement("Power10", "Models/trophies/powers/", -54.30f, 9.82f, 72.20f, .2f, 0.0000f, 0f, 0f, 0.5270f, 250f, .2f, .85f, .2f));

        return placements;
    }
    
    private void fixAmbient(Spatial spatial) {
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (int i = 0; i < node.getQuantity(); i++) {
                Spatial child = node.getChild(i);
                fixAmbient(child);
            }
        } else if (spatial instanceof Geometry) {
            ((Geometry) spatial).getMaterial().setParam("UseMaterialColors",
                    VarType.Boolean, false);
        }
    }

    private void addPlacements(ArrayList<Placement> placements, boolean addPhysics) {
        for (Placement placement : placements) {
            Spatial spatial = (Spatial) assetManager.loadModel(placement.filePath);
            spatial.setLocalTranslation(placement.getLocalTranslation());
            spatial.setLocalScale(placement.getScale());
            spatial.setLocalRotation(placement.getRotation());
            spatial.setName(placement.getName());
            spatial.setShadowMode(ShadowMode.CastAndReceive);
            rootNode.attachChild(spatial);
            
            if (placement.filePath.endsWith(".j3o") && !(placement.name.startsWith("Tree"))) {
                fixAmbient(spatial);
            }
            
            if (addPhysics) {
                if (placement.fiction < 0.0f) {
                    physicsMan.addMeshPhysics(spatial);
                } else {
                    physicsMan.addMeshFrictionPhysics(spatial, placement.fiction);
                }
            }
            worldMan.putSpatial(placement.getName(), spatial);
        }
    }

    public void addTrophyPlacements(ArrayList<Placement> placements, boolean addPhysics) {
        for (Placement placement : placements) {
            //Spatial spatial = (Spatial) assetManager.loadModel(placement.filePath);
            Box b = new Box(1f, 1f, 1f);
            Geometry geom = new Geometry(placement.getName(), b);
            geom.setLocalTranslation(placement.getLocalTranslation());
            geom.setLocalScale(placement.getScale());
            geom.setLocalRotation(placement.getRotation());
            trophyHeight = geom.getLocalTranslation().y;
            
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            
            if (placement.getName().startsWith("Power")) {
                int id = Integer.parseInt(placement.getName().substring("Power".length()));
                id = (id % 6) + 1;
                
                mat.setTexture("ColorMap", assetManager.loadTexture(placement.filePath
                        + id + ".png"));
                mat.setTexture("GlowMap", assetManager.loadTexture(placement.filePath
                        + id + "Glow.png"));
            } else {
                mat.setTexture("ColorMap", assetManager.loadTexture(placement.filePath
                        + placement.getName() + ".png"));
                /*mat.setTexture("GlowMap", assetManager.loadTexture(placement.filePath
                        + placement.getName() + "Glow.png"));*/
            }

            geom.setMaterial(mat);

            rootNode.attachChild(geom);
            
            if (placement.filePath.endsWith(".j3o")) {
                fixAmbient(geom);
            }
            
            if (addPhysics) {
                physicsMan.addMeshPhysics(geom);
            }
            worldMan.putSpatial(placement.getName(), geom);
        }
    }

    private void addAutoHeightPlacements(ArrayList<Placement> placements,
            boolean addPhysics, TerrainQuad terrain) {
        for (Placement placement : placements) {
            Spatial spatial = (Spatial) assetManager.loadModel(placement.filePath);

            // get the height of the terrain to set the object at
            Vector3f location3d = placement.getLocalTranslation();
            Vector2f location2d = new Vector2f(location3d.x, location3d.z);
            location3d.y = terrain.getHeight(location2d) + WorldManager.WORLD_OFFSET
                    + placement.locationY;

            spatial.setLocalTranslation(location3d);
            spatial.setLocalScale(placement.getScale());
            spatial.setLocalRotation(placement.getRotation());
            spatial.setName(placement.getName());
            spatial.setShadowMode(ShadowMode.CastAndReceive);
            rootNode.attachChild(spatial);
            
            if (placement.filePath.endsWith(".j3o")) {
                fixAmbient(spatial);
            }
            
            if (addPhysics) {
                physicsMan.addMeshPhysics(spatial);
            }
            worldMan.putSpatial(placement.getName(), spatial);
        }
    }

    private void addMoveablePlacements(ArrayList<Placement> placements,
            boolean addPhysics, TerrainQuad terrain) {
        for (Placement placement : placements) {
            Spatial spatial = (Spatial) assetManager.loadModel(placement.filePath);

            // get the height of the terrain to set the object at
            Vector3f location3d = placement.getLocalTranslation();
            Vector2f location2d = new Vector2f(location3d.x, location3d.z);
            location3d.y = terrain.getHeight(location2d) + WorldManager.WORLD_OFFSET
                    + placement.locationY;

            spatial.setLocalTranslation(location3d);
            spatial.setLocalScale(placement.getScale());
            spatial.setLocalRotation(placement.getRotation());
            spatial.setName(placement.getName());
            spatial.setShadowMode(ShadowMode.CastAndReceive);
            rootNode.attachChild(spatial);
            
            if (placement.filePath.endsWith(".j3o") && !(placement.name.startsWith("Tree"))) {
                fixAmbient(spatial);
            }
            
            if (addPhysics) {
                physicsMan.addBoxPhysics(spatial, placement.getMass(), placement.getHitbox());
            }
            worldMan.putSpatial(placement.getName(), spatial);
        }
    }

    private void addLamps(ArrayList<Placement> lamps, ArrayList<Placement> lights,
            boolean addPhysics, TerrainQuad terrain, boolean addPointLights) {
        Material glowMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        glowMat.setColor("Color", ColorRGBA.White);
        glowMat.setColor("GlowColor", ColorRGBA.White);
        for (int i = 0; i < lamps.size(); i++) {
            Placement lamp = lamps.get(i);
            Placement light = lights.get(i);
            Node pivot = new Node("lampNode");

            Spatial spatial = (Spatial) assetManager.loadModel(lamp.filePath);
            Spatial lightObject = (Spatial) assetManager.loadModel(light.filePath);

            // get the height of the terrain to set the object at
            Vector3f location3d = lamp.getLocalTranslation();
            Vector2f location2d = new Vector2f(location3d.x, location3d.z);
            location3d.y = terrain.getHeight(location2d) + WorldManager.WORLD_OFFSET;


            pivot.setLocalTranslation(location3d.x, location3d.y + lamp.locationY, location3d.z);
            pivot.attachChild(spatial);
            pivot.attachChild(lightObject);

            if (addPointLights) {
                PointLight pl = new PointLight();
                pl.setRadius(7f);
                LightNode lNode = new LightNode("pointlight", pl);
                lNode.setLocalTranslation(0f, 1.5f, 0f);
                pivot.attachChild(lNode);
                rootNode.addLight(pl);
            }

            spatial.setLocalTranslation(0, 0, 0);
            spatial.setLocalScale(lamp.getScale());
            spatial.setName(lamp.getName());
            //spatial.setLocalRotation(lamp.getRotation());
            spatial.setShadowMode(ShadowMode.CastAndReceive);
            pivot.attachChild(spatial);

            lightObject.setLocalTranslation(0, -lamp.locationY, 0);
            lightObject.setLocalScale(light.getScale());
            lightObject.setName(light.getName());
            //lightObject.setLocalRotation(light.getRotation());
            pivot.attachChild(lightObject);

            //give glow to lamps if night
            lightObject.setMaterial(glowMat);

            rootNode.attachChild(pivot);

            if (lamp.filePath.endsWith(".j3o")) {
                fixAmbient(spatial);
            }

            if (addPhysics) {
                physicsMan.addNodeBoxPhysics(pivot, lamp.getMass(), lamp.getHitbox());
            }
        }
    }
    
    public Vector3f getHeightAtLocation(float x, float z, TerrainQuad terrain) {
        float y = terrain.getHeight(new Vector2f(x, z)) + WorldManager.WORLD_OFFSET;
        return new Vector3f(x, y, z);
    }

    // removes all the objects from the placement array from the game world
    public void removeObjects(ArrayList<Placement> placements) {
        for (Placement placement : placements) {
            Spatial spatial = rootNode.getChild(placement.getName());
            if (spatial != null) {
                spatial.removeFromParent();
            }
        }
    }
    
    /*
     * initPlayerLos() and initPlayerRots() work in tandem to create a series of
     * player init location and rotations.  This is designed for use with init'ing
     * the locatino of multiple players, such that each players location is
     * specified.  This could be a series of "spawn points" for a free-foam
     * style game or the start location of each player with a race type game.
     * 
     * It also allows for retreiving the start point closest to a given location.
     */

    private void initPlayerLocs(TerrainQuad terrain) {
        final float HEIGHT = 5.0f;
        this.playerLocs.clear();
        
        this.playerLocs.add(new Vector3f(-25f, HEIGHT, 90f));
        
        for (Vector3f vector: this.playerLocs) {
            vector.y += terrain.getHeight(new Vector2f(vector.x, vector.z)) + WorldManager.WORLD_OFFSET;
        }
    }
    
    public Vector3f playerLocation(int id) {
        return this.playerLocs.get(id % this.playerLocs.size());
    }
    
    public Vector3f nearestResetLocation(Vector3f currLoc) {
        Vector3f nearest = null;
        float distance = Float.MAX_VALUE;
        
        for (Vector3f vector: this.playerLocs) {
            float calcDis = (float) (FastMath.sqr(currLoc.x - vector.x)
                    + FastMath.sqr(currLoc.z - vector.z));
            if (calcDis < distance) {
                nearest = vector;
                distance = calcDis;
            }
        }
        
        return nearest;
    }
    
    public Vector3f randomResetPoint() {
        Random random = new Random(System.currentTimeMillis());
        return this.playerLocs.get(random.nextInt() % this.playerLocs.size());
    }

    private void initPlayerRots() {
        this.playerRots.clear();
        this.playerRots.add(3.4f);
    }
    
    public Quaternion playerRotation(int id) {
        Quaternion rotation = new Quaternion();
        float angle = this.playerRots.get(id % this.playerRots.size());
        rotation.fromAngleAxis(-FastMath.PI/angle, new Vector3f(0, 1, 0));
        return rotation;
    }
    
    public Quaternion rotationForLocation(Vector3f loc) {
        int index = this.playerLocs.indexOf(loc);
        Quaternion rotation = new Quaternion();
        float angle = this.playerRots.get(index);
        rotation.fromAngleAxis(-FastMath.PI/angle, new Vector3f(0, 1, 0));
        return rotation;
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void rotateObjects(ArrayList<Placement> placements) {
        for (Placement placement : placements) {
            Spatial spatial = rootNode.getChild(placement.getName());
            spatial.rotate(rotateZ());
        }
    }

    private Quaternion rotateZ() {
        Quaternion pitch = new Quaternion();
        pitch.fromAngleAxis(FastMath.PI * .2f / 180.0f, new Vector3f(0.0f,
                1.0f, 0.0f));
        return pitch;
    }

    public void bounceObjects(ArrayList<Placement> placements, float count) {
        for (Placement placement : placements) {
            Spatial spatial = rootNode.getChild(placement.getName());
            Vector3f location = spatial.getLocalTranslation();
            location.y = location.y + ((float)Math.sin(count / 300) / 1000);
            
        }
    }
    
//    public void setWorldBox(TerrainQuad terrain){
//        float size = terrain.getTerrainSize()*worldMan.WORLD_SCALE;
//        Box b = new Box(size, size, size);
//        Geometry geom = new Geometry("WorldBox", b);
//        Material mat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", new ColorRGBA(0.0f, 0.0f, 0.0f, 0.0f));
//        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//        geom.setMaterial(mat);
//        
//        rootNode.attachChild(geom);
//        physicsMan.addBoxPhysics(geom, 0, new Vector3f(size,size,size));
//        //geom.setCullHint(Spatial.CullHint.Always);
//    }
    
}
