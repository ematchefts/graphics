package cedarkartteamd.managers;

import cedarkartteamd.Counter;
import cedarkartteamd.managers.Powerup.Type;
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Gregory
 */
public class TrophyManager implements ICedarKartManager {

    private final AssetManager assetManager;
    private final PlayerManager playerMan;
    private final SettingsManager settingsMan;
    private final Node rootNode;
    private final GameManager gameMan;
    private final WorldManager worldMan;
    private final HUDManager HUDMan;
    private final PlacementManager placementMan;
    private final SoundManager soundMan;
    private final GUIManager GUIMan;
    private final long POWERUP_RESPAWN_TIME = 17000;
    ArrayList<Placement> placements = new ArrayList<>();
    ArrayList<Placement> placement = new ArrayList<>();
    private int playerScore = 0;
    private float distance;
    Random generator = new Random();
    ArrayList<Placement> powerPlacements = new ArrayList<>();
    ArrayList<Placement> usedPowerPlacements = new ArrayList<>();
    ArrayList<Long> powerResetStartTime = new ArrayList<>();
    private final Counter count = new Counter();


    public TrophyManager(AssetManager assetManager, Node rootNode, GameManager gameMan,
            WorldManager worldMan, SettingsManager settingsMan, HUDManager HUDMan,
            SoundManager soundMan, GUIManager GUIMan) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        this.gameMan = gameMan;
        this.worldMan = worldMan;
        this.settingsMan = settingsMan;
        this.HUDMan = HUDMan;
        this.placementMan = worldMan.getPlacementManager();
        this.playerMan = worldMan.getPlayerManager();
        this.soundMan = soundMan;
        this.GUIMan = GUIMan;
    }

    @Override
    public void start() {
        placements = placementMan.createTrophyPlacements();
        powerPlacements = placementMan.createPowerUpPlacements();
        placementMan.addTrophyPlacements(powerPlacements, false);
    }
    
    public void gameStart() {
        placeTrophy();
    }

    public void update() {
        //trophies
        placementMan.rotateObjects(placement);
        placementMan.bounceObjects(placement, (float)count.next());

        float playerX = playerMan.getLocation().x;
        float playerY = playerMan.getLocation().y;
        float playerZ = playerMan.getLocation().z;
        distance = (float) (FastMath.sqr(playerX - placement.get(0).locationX) + FastMath.sqr(playerZ - placement.get(0).locationZ));
        if (distance < 1f) {
            soundMan.playTrophySound();
            playerScore++;
            HUDMan.setScore(playerScore);
            captureTrophy();
            this.gameMan.sendCapture();
            if (playerScore == settingsMan.getWinningScore()) {
                this.gameMan.endGame();
                reset();
            }
        }

        //powerups
        float powerDist;
        float powerX;
        float powerY;
        float powerZ;
        Placement currPlacement;
        for (int i = 0; i < powerPlacements.size(); i++) {
            currPlacement = powerPlacements.get(i);
            powerX = currPlacement.locationX;
            powerY = currPlacement.locationY;
            powerZ = currPlacement.locationZ;
            powerDist = (float) (FastMath.sqr(playerX - powerX) + FastMath.sqr(playerY + 0.5f - powerY) + FastMath.sqr(playerZ - powerZ));
            //if you get close enough to the powerup box...
            if (powerDist < 1f) {
                soundMan.playPowerupSound();
                capturePowerup(i);
                int type = FastMath.nextRandomInt(1, 4);
                if (!this.gameMan.isMultiplayer()) {
                    switch(type) {
                        case 1:
                            playerMan.setBees(true);
                            break;
                        case 2:
                            playerMan.enableTray();
                            break;
                        case 3:
                            playerMan.impulsePowerup();
                            break;
                        case 4:
                            playerMan.randomizeLocation();
                            break;
                        case 5:
                            playerMan.setGravity(false);
                            break;
                    }
                } else {
                    Type pType = Type.values()[type - 1];
                    if (pType == Type.BEES || pType == Type.IMPULSE
                            || pType == Type.GRAVITY || pType == Type.RANDOMIZER) {
                        this.gameMan.sendPowerup(new Powerup(i, -1, pType));
                    }
                }
            }
        }

        long currTime = System.currentTimeMillis();
        for (int i = 0; i < usedPowerPlacements.size(); i++) {
            if (currTime - powerResetStartTime.get(i) >= POWERUP_RESPAWN_TIME) {
                currPlacement = usedPowerPlacements.get(i);
                ArrayList<Placement> addPlacement = new ArrayList<>();
                addPlacement.add(currPlacement);
                powerPlacements.add(currPlacement);
                powerResetStartTime.remove(i);
                placementMan.addTrophyPlacements(addPlacement, false);
                usedPowerPlacements.remove(i);

                playerMan.setBees(false);
//                playerMan.setGravity(true);
            }
        }
    }

    public void reset(){
        
            playerScore = 0;
            placementMan.removeObjects(placement);
            placement.remove(0);
        
            Placement currPlacement;
        for (int i = 0; i < usedPowerPlacements.size(); i++) {
            currPlacement = powerPlacements.get(i);
            ArrayList<Placement> addPlacement = new ArrayList<>();
            addPlacement.add(currPlacement);
            usedPowerPlacements.remove(currPlacement);
            powerResetStartTime.remove(i);
            placementMan.addTrophyPlacements(addPlacement,false);
            powerPlacements.add(currPlacement);
        }
    }

    public int getScore() {
        return this.playerScore;
    }
    
    public void setSeed(int seed) {
        this.generator.setSeed(seed);
    }
    
    public int captureTrophy() {
        placementMan.removeObjects(placement);
        placement.remove(0);
        return placeTrophy();
    }
    
    public void captureTrophy(int next) {
        placementMan.removeObjects(placement);
        placement.remove(0);
        
        if (placements.isEmpty()) {
            placements = placementMan.createTrophyPlacements();
        }
        
        next %= placements.size();
        placement.add(placements.remove(next));
        placementMan.addTrophyPlacements(placement, false);
        HUDMan.updateTrophyPosition(placement.get(0).locationX,
                placement.get(0).locationZ);
    }
    
    public void capturePowerup(int id) {
        if (id >= powerPlacements.size()) {
            System.err.println("[POWERUP] id is out of range.  Powerup may not be properly removed.");
            return;
        }
        
        Placement currPlacement = powerPlacements.get(id);
        ArrayList<Placement> removePlacement = new ArrayList<>();
        removePlacement.add(currPlacement);
        usedPowerPlacements.add(currPlacement);
        powerResetStartTime.add(System.currentTimeMillis());
        placementMan.removeObjects(removePlacement);
        powerPlacements.remove(id);
    }

    private int placeTrophy() {
        if (placements.isEmpty()) {
            placements = placementMan.createTrophyPlacements();
        }

        int temp = generator.nextInt(placements.size());
        placement.add(placements.remove(temp));
        placementMan.addTrophyPlacements(placement, false);
        HUDMan.updateTrophyPosition(placement.get(0).locationX,
        placement.get(0).locationZ);
        return temp;
    }
}
