package cedarkartteamd.managers;

//import CedarKart.UserSettings.Weather;
import cedarkartteamd.managers.SettingsManager.Weather;
import cedarkartteamd.network.ActorInit;
import cedarkartteamd.network.ActorUpdate;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;
import java.util.HashMap;


//public class WorldManager implements AnimEventListener, ActionListener, ICedarKartManager {
public class WorldManager implements AnimEventListener, ICedarKartManager {
    
    // cedarvillegse5.png - in progress heightmap fix
    public static final String AlphaMap = "Textures/Terrain/splat/bigAlpha2.png";
    public static final String WORLD_HEIGHTMAP = "Textures/Terrain/splat/cedarvillegse7.png";
    public static final float WORLD_OFFSET = 2.41f;
    public static final float WORLD_Z_SCALE = 0.2f;
    public static final float WORLD_SCALE = 1f;
    
    // Constants
    private final float grassScale = 128;
    private final float sidewalkScale = 128;
    private final float roadScale = 128;
    private final float lakeScale = 128;
    
    // Standard Managers
    private AppStateManager stateManager;
    private final InputManager inputManager;
    private final AssetManager assetManager;
    private GameManager gameMan;
    
    // Custom Managers
    private final PlayerManager playerMan;
    private final PhysicsManager physicsMan;
    private final VehicleManager vehicleMan;
    private final ControllerManager controlMan;
    private final SettingsManager settingsMan;
    private final SoundManager soundMan;
    private final PlacementManager placementMan;
    
    // for multiplayer
    private final HashMap<Integer, PlayerManager> players = new HashMap<>();
    
    // The game controllers
    private final Node rootNode;
    private final ViewPort viewPort;
    private Camera camera;
    private FlyByCamera flyCam;
    
    // Hashmap containing all spatials as defined in Placements
    private HashMap<String, Spatial> spatials = new HashMap<>();

    // Location of finish
    private Vector3f finish;

    // Some bools
    private boolean worldBuilt = false;
    
    // Lights
    Light aLight;
    DirectionalLight dLight;
    
    // Water
    private WaterFilter water;
    
    // Filters
    private FilterPostProcessor fpp = null;
    
    TerrainQuad terrain;

    public WorldManager(Node rootNode, AssetManager assetManager,
                        InputManager inputManager, Camera camera,
                        FlyByCamera flyCam, ViewPort viewPort,
                        AppStateManager stateManager, 
                        SettingsManager settingsMan, GameManager gameMan,
                        SoundManager soundMan) { // , SimpleGUI gui) {

        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.inputManager = inputManager;
        this.camera = camera;
        this.flyCam = flyCam;
        this.viewPort = viewPort;
        this.stateManager = stateManager;
        this.settingsMan = settingsMan;
        this.gameMan = gameMan;
        this.soundMan = soundMan;
        // this.gui = gui;
        
        controlMan = new ControllerManager(inputManager);
        
        physicsMan = new PhysicsManager(rootNode, assetManager, inputManager, camera, flyCam, viewPort, stateManager);
        // raceMan = new RaceManager();
        // soundMan = new SoundManager(rootNode, assetManager);
        fpp = new FilterPostProcessor(assetManager);
        
        placementMan = new PlacementManager(assetManager, rootNode, this, settingsMan);
        vehicleMan = new VehicleManager(assetManager, placementMan);
        playerMan = new PlayerManager(rootNode, assetManager, inputManager, camera,
                flyCam, viewPort, gameMan, placementMan, physicsMan, vehicleMan,
                controlMan, settingsMan, soundMan, null);
    }
    
    public void makeWorld() {
        if (!this.worldBuilt) {
            createLake(settingsMan.getWeather());    // water
            createTerrain(settingsMan.getWeather()); // earth
            createLight(settingsMan.getWeather());   // fire
            createSky(settingsMan.getWeather());     // air :)

            physicsMan.start();
            physicsMan.addMeshPhysics(terrain);

            placementMan.start(terrain);

            if (settingsMan.bloom()) {
                BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
                fpp.addFilter(bloom);
            }

            if (settingsMan.dof()) {
                DepthOfFieldFilter dof = new DepthOfFieldFilter();
                dof.setFocusDistance(250.0f);
                dof.setFocusRange(2500.0f);
                fpp.addFilter(dof);
            }

            if (settingsMan.fxaa()) {
                FXAAFilter fxaa = new FXAAFilter();
                fpp.addFilter(fxaa);
            }

            if (settingsMan.ssao()) {
                SSAOFilter ssao = new SSAOFilter();
                fpp.addFilter(ssao);
            }
            
            /*if (settingsMan.shadows()) {
                DirectionalLightShadowFilter dls = new DirectionalLightShadowFilter(assetManager, 1024, 3);
                dls.setLight(dLight);
                fpp.addFilter(dls);
            }*/
            
            /*if (settingsMan.advancedLighting()) {
                PointLightShadowFilter pls = new PointLightShadowFilter(assetManager, 1024);
                SpotLightShadowFilter sls = new SpotLightShadowFilter(assetManager, 1024);
                fpp.addFilter(pls);
                fpp.addFilter(sls);
            }*/

            viewPort.addProcessor(fpp); 

            this.worldBuilt = true;
        }
    }
    
    public void remakeWorld() {
        if (this.worldBuilt) {
            this.worldBuilt = false;
            
            this.players.clear();
            
            // reset filters (includes water)
            viewPort.clearProcessors();
            fpp.removeAllFilters();
            
            this.physicsMan.reset();
            
            // remove world:
            this.rootNode.detachAllChildren();
            
            // remove lights
            this.rootNode.removeLight(aLight);
            this.rootNode.removeLight(dLight);
        }

        makeWorld();
    }
    
    @Override
    public void start() {
        if (!this.worldBuilt) {
            makeWorld();
        }
        
        playerMan.start();
        
        gameMan.setSetupComplete(true);
    }
    
    public Spatial getSpatialByName(String name){
        return spatials.get(name); 
    }

//    public void addBoundingBoxes(){
//         Vector3f dimensions = new Vector3f(0.35f, 1.2f, 0.35f);
//         physicsMan.addBoxPhysics(spatials.get("Yellow Jacket"), 75, dimensions);
//        
//         dimensions = new Vector3f( 0.7f, 6.7f, 0.7f);
//         physicsMan.addBoxPhysics(spatials.get("Water Tower"), 0, dimensions);
//         
//        
//         //Add collition shape to cars
//         dimensions = new Vector3f( 0.45f, .42f, 1.25f);
//         for (int i = 1; i <= 121; i ++) {
//              physicsMan.addBoxPhysics(spatials.get("Car " + i), 800, dimensions);
//         }
//         
//         //Add colliusion shape to vans
//         dimensions = new Vector3f( 0.45f, .42f, 1.2f);
//         for (int i = 1; i <= 23; i ++) {
//              physicsMan.addBoxPhysics(spatials.get("Van " + i), 800, dimensions);
//         }
//  
//        /* Use for debugging bounding box
//        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        waterTowerBox.setMaterial(mat);
//        rootNode.attachChild(waterTowerBox);
//        */
//   
//    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }
    
    public void addPlayer(ActorInit init) {
        PlayerManager newPlayer = new PlayerManager(rootNode, assetManager,
                inputManager, camera, flyCam, viewPort, gameMan, placementMan,
                physicsMan, vehicleMan, controlMan, settingsMan, soundMan, init);
        
        newPlayer.start();
        soundMan.attachVehicleSound(newPlayer.vehicleNode);
        
        this.players.put(init.getId(), newPlayer);
    }
    
    public void removePlayer(int id) {
        PlayerManager oldPlayer = this.players.remove(id);

        if (oldPlayer != null) {
            oldPlayer.vehicleNode.removeFromParent();
            this.physicsMan.getBulletAppState().getPhysicsSpace().remove(oldPlayer.vehicle);
        }
    }
    
    public void updatePlayer(ActorUpdate update) {
        PlayerManager player = this.players.get(update.getId());
        
        if (player != null) {
            player.vehicle.setPhysicsLocation(update.getLocation());
            player.vehicle.setPhysicsRotation(update.getRotation());
            player.vehicle.setLinearVelocity(update.getVelocity());
            player.vehicle.setAngularVelocity(update.getAngular());
        }
    }
    
    public void createTerrain(Weather weather){
        
        // TERRAIN TEXTURE material
        Material matRock = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        matRock.setBoolean("useTriPlanarMapping", false);
        matRock.setBoolean("WardIso", true);

        // ALPHA map (for splat textures)
        matRock.setTexture("AlphaMap", assetManager.loadTexture(AlphaMap));

        // HEIGHTMAP image (for the terrain heightmap)
        Texture heightMapImage = assetManager.loadTexture(WORLD_HEIGHTMAP);


        // sidewalk texture
        Texture sidewalk = assetManager.loadTexture("Textures/Terrain/splat/asphalt.jpg");
        sidewalk.setWrap(WrapMode.Repeat);
        matRock.setTexture("DiffuseMap", sidewalk);
        matRock.setFloat("DiffuseMap_0_scale", sidewalkScale);

        // grass texture
        Texture grass;
        if(weather == Weather.WINTER) {
            grass = assetManager.loadTexture("Textures/Terrain/splat/snow.jpg");
        } else {
            grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        }
        grass.setWrap(WrapMode.Repeat);
        matRock.setTexture("DiffuseMap_1", grass);
        matRock.setFloat("DiffuseMap_1_scale", grassScale);

        // road texture
        Texture road = assetManager.loadTexture("Textures/Terrain/splat/roadc.jpg");
        road.setWrap(WrapMode.Repeat);
        matRock.setTexture("DiffuseMap_2", road);
        matRock.setFloat("DiffuseMap_2_scale", roadScale);

        // lakebed texture
        Texture rock;
        if(weather == Weather.WINTER) {
            rock = assetManager.loadTexture("Textures/Terrain/splat/rockSnow.jpg");
        } else {
            rock = assetManager.loadTexture("Textures/Terrain/splat/pebble.jpg");
        }
        rock.setWrap(WrapMode.Repeat);
        matRock.setTexture("DiffuseMap_3", rock);
        matRock.setFloat("DiffuseMap_3_scale", lakeScale);

        // CREATE HEIGHTMAP
        AbstractHeightMap heightmap = null;
        try {
            //heightmap = new HillHeightMap(1025, 1000, 50, 100, (byte) 3);

            heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), WORLD_Z_SCALE);
            heightmap.load();

        } catch (Exception e) {
            e.printStackTrace();
        }

        terrain = new TerrainQuad("terrain", 65, (int)((512 * WORLD_SCALE) + 1),
                heightmap.getHeightMap());
        
        TerrainLodControl control = new TerrainLodControl(terrain, camera);
        control.setLodCalculator( new DistanceLodCalculator(65, 2.7f) );
        terrain.addControl(control);
        terrain.setMaterial(matRock);
        terrain.setModelBound(new BoundingBox());
        terrain.updateModelBound();
        terrain.setLocalTranslation(0, WORLD_OFFSET, 0);
        terrain.setLocalScale(1f, .25f, 1f);
        terrain.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(terrain);
    }
    
    public void createSky(Weather weather){
        Texture north, south, east, west, up, down;
        
        if (weather == Weather.SUNRISE) {
            north = assetManager.loadTexture("Textures/sky/ValleyLand/dvalleylandft.png");
            south = assetManager.loadTexture("Textures/sky/ValleyLand/dvalleylandbk.png");
            east = assetManager.loadTexture("Textures/sky/ValleyLand/dvalleylandlf.png");
            west = assetManager.loadTexture("Textures/sky/ValleyLand/dvalleylandrt.png");
            up = assetManager.loadTexture("Textures/sky/ValleyLand/dvalleylandup.png");
            down = assetManager.loadTexture("Textures/sky/ValleyLand/dvalleylanddn.png");
        } else if (weather == Weather.NIGHT) {
            north = assetManager.loadTexture("Textures/sky/GrimmNight/GrimmNight_lf.jpg");
            south = assetManager.loadTexture("Textures/sky/GrimmNight/GrimmNight_rt.jpg");
            east = assetManager.loadTexture("Textures/sky/GrimmNight/GrimmNight_bk.jpg");
            west = assetManager.loadTexture("Textures/sky/GrimmNight/GrimmNight_ft.jpg");
            up = assetManager.loadTexture("Textures/sky/GrimmNight/GrimmNight_up.jpg");
            down = assetManager.loadTexture("Textures/sky/GrimmNight/GrimmNight_dn.jpg");
        } else if (weather == Weather.WINTER) {
            north = assetManager.loadTexture("Textures/sky/Miramar Sky/Miramar_lf.jpg");
            south = assetManager.loadTexture("Textures/sky/Miramar Sky/Miramar_rt.jpg");
            east = assetManager.loadTexture("Textures/sky/Miramar Sky/Miramar_bk.jpg");
            west = assetManager.loadTexture("Textures/sky/Miramar Sky/Miramar_ft.jpg");
            up = assetManager.loadTexture("Textures/sky/Miramar Sky/Miramar_up.jpg");
            down = assetManager.loadTexture("Textures/sky/Miramar Sky/Miramar_dn.jpg");
        } else {
            north = assetManager.loadTexture("Textures/sky/ViolentDays Sky/ViolentDays_lf.jpg");
            south = assetManager.loadTexture("Textures/sky/ViolentDays Sky/ViolentDays_rt.jpg");
            east = assetManager.loadTexture("Textures/sky/ViolentDays Sky/ViolentDays_bk.jpg");
            west = assetManager.loadTexture("Textures/sky/ViolentDays Sky/ViolentDays_ft.jpg");
            up = assetManager.loadTexture("Textures/sky/ViolentDays Sky/ViolentDays_up.jpg");
            down = assetManager.loadTexture("Textures/sky/ViolentDays Sky/ViolentDays_dn.jpg");
        }

        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        rootNode.attachChild(sky);
    }
    
    public void createLake(Weather weather) {
        // CREATE CEDAR LAKE
        if (weather != Weather.WINTER) {
            if (weather == Weather.SUNRISE) {
                water = new WaterFilter(rootNode, weather.direction());
                water.setWaterHeight(6.25f);          
                water.setWaveScale(0.2f); // Sets the scale factor of the waves height map. The smaller the value, the bigger the waves!
                water.setMaxAmplitude(.1f); 
                water.setSpeed(0.1f); 
                water.setUseRipples(true);
                water.setNormalScale(2f); //Sets the normal scaling factors to apply to the normal map. The higher the value, the more small ripples will be visible on the waves.    
                water.setDeepWaterColor(ColorRGBA.Brown);
                water.setWaterColor(ColorRGBA.Brown.mult(1.0f));
                water.setWaterTransparency(2f);
                water.setColorExtinction(new Vector3f(10.0f, 20.0f, 30.0f));
                water.setShininess(.5f);
            } else if (weather == Weather.NIGHT) {
                water = new WaterFilter(rootNode, weather.direction());
                water.setWaterHeight(6.25f);          
                water.setWaveScale(0.2f); // Sets the scale factor of the waves height map. The smaller the value, the bigger the waves!
                water.setMaxAmplitude(.1f); 
                water.setSpeed(0.1f); 
                water.setUseRipples(true);
                water.setNormalScale(2f); //Sets the normal scaling factors to apply to the normal map. The higher the value, the more small ripples will be visible on the waves.    
                water.setDeepWaterColor(ColorRGBA.Black);
                water.setWaterColor(ColorRGBA.Black.mult(1.0f));
                water.setWaterTransparency(2f);
                water.setColorExtinction(new Vector3f(10.0f, 20.0f, 30.0f));
                water.setShininess(.5f);
            } else {
                water = new WaterFilter(rootNode, weather.direction());
                water.setWaterHeight(6.25f);          
                water.setWaveScale(0.1f); // Sets the scale factor of the waves height map. The smaller the value, the bigger the waves!
                water.setMaxAmplitude(0.3f); 
                water.setSpeed(0.1f); 
                water.setUseRipples(false);
                water.setNormalScale(2f); //Sets the normal scaling factors to apply to the normal map. The higher the value, the more small ripples will be visible on the waves.    
                water.setDeepWaterColor(ColorRGBA.Red.mult(0.5f));
                water.setWaterColor(ColorRGBA.Red.mult(0.5f));
                water.setWaterTransparency(5f);
                water.setColorExtinction(new Vector3f(10.0f, 20.0f, 30.0f));
                water.setRefractionStrength(0.7f);
                water.setLightColor(ColorRGBA.Yellow);
            }
            
            water.setCenter(new Vector3f(10f, 200f, 90f));       
            water.setRadius(60);
            water.setUseFoam(false);

            fpp.addFilter(water);    
        }
    }
    
    public void createLight(Weather weather) {
        aLight = new AmbientLight();
        dLight = new DirectionalLight();
        
        if (weather == Weather.SUNRISE) {
            aLight.setColor(ColorRGBA.White.mult(1.5f));
            dLight.setColor((ColorRGBA.Orange.add(ColorRGBA.White)).mult(0.75f));
        } else if (weather == Weather.NIGHT) {
            aLight.setColor(ColorRGBA.DarkGray.mult(1.5f));
            dLight.setColor(ColorRGBA.DarkGray.mult(2.5f));
        } else if (weather == Weather.WINTER) {
            aLight.setColor(ColorRGBA.White.add(ColorRGBA.Cyan.mult(.3f)).mult(1.3f));
            dLight.setColor(ColorRGBA.White.add(ColorRGBA.Cyan.mult(.3f)).mult(0.7f));
        } else {
            aLight.setColor(ColorRGBA.Yellow.mult(1.5f).add(ColorRGBA.Orange).mult(2f));
            dLight.setColor(ColorRGBA.White.mult(.25f));
        }
        
        dLight.setDirection(weather.direction().normalize());
        
        rootNode.addLight(aLight);
        rootNode.addLight(dLight);
    }

    public void update(){
        this.playerMan.update();
    }
    
    public PhysicsManager getPhysicsManager() {
        return physicsMan;
    }
    
    public PlayerManager getPlayerManager() {
        return playerMan;
    }
    
    public PlacementManager getPlacementManager(){
        return placementMan;
    }
    
    public ControllerManager getControllerManager() {
        return this.controlMan;
    }

    void putSpatial(String name, Spatial spatial) {
        spatials.put(name, spatial);
    }
}
