
package cedarkartteamd.managers;

/**
 *
 * @author Jengel
 */

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.BulletAppState.ThreadingType;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;
import com.jme3.scene.Node;
import com.jme3.renderer.ViewPort;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.renderer.Camera;
import com.jme3.input.FlyByCamera;
import com.jme3.math.Vector3f;

public class PhysicsManager implements ICedarKartManager {
    
    // The game controllers

    // Standard Managers    
    private final AssetManager assetManager;
    private final InputManager inputManager;

    private final Node rootNode;
    private final ViewPort viewPort;
    private Camera camera;
    private FlyByCamera flyCam;
    private BulletAppState bulletAppState;
    private final AppStateManager appStateManager;
    
    private float activeSpeed = 0.0f;
    
    public PhysicsManager(Node rootNode, AssetManager assetManager, 
                          InputManager inputManager, Camera camera, 
                          FlyByCamera flyCam, ViewPort viewPort, 
                          AppStateManager stateManager) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.inputManager = inputManager;
        this.camera = camera;
        this.flyCam = flyCam;
        this.viewPort = viewPort;
        this.appStateManager = stateManager;
        this.bulletAppState = new BulletAppState();
        this.bulletAppState.setThreadingType(ThreadingType.PARALLEL);
    }
    
    @Override
    public void start() {
        this.appStateManager.attach(bulletAppState);
    }
    
    public void reset() {
        this.bulletAppState.setEnabled(false);
        this.appStateManager.detach(this.bulletAppState);
        this.bulletAppState = new BulletAppState();
        this.bulletAppState.setThreadingType(ThreadingType.PARALLEL);
        this.activeSpeed = this.bulletAppState.getSpeed();
        this.appStateManager.attach(this.bulletAppState);
    }
    
    public void pause() {
        this.bulletAppState.setSpeed(0.0f);
    }
    
    public void resume() {
        this.bulletAppState.setSpeed(this.activeSpeed);
    }

    /**
     * Add Physics for Objects that will be imovable and static in the game.
     * @param object 
     */
    public void addMeshPhysics(Spatial object){
        CollisionShape sceneShape;
        RigidBodyControl object_phy;
        sceneShape = CollisionShapeFactory.createMeshShape(object);
        object_phy = new RigidBodyControl(sceneShape,0f);
        object.addControl(object_phy);
        bulletAppState.getPhysicsSpace().add(object_phy);
    }
    
    public void addMeshFrictionPhysics(Spatial object, float friction) {
        CollisionShape sceneShape;
        RigidBodyControl object_phy;
        sceneShape = CollisionShapeFactory.createMeshShape(object);
        object_phy = new RigidBodyControl(sceneShape,0f);
        object_phy.setFriction(friction);
        object.addControl(object_phy);
        bulletAppState.getPhysicsSpace().add(object_phy);
    }
    
   /**
     * Addd simple physics to complex objects
     * @param object Object needed bounding
     * @param mass Weight of the object. Note 0 is immovable
     * @param dimensions Dimentions of collision box
     */
    public void addBoxPhysics(Spatial object, float mass, Vector3f dimensions){
        RigidBodyControl object_phy;
        BoxCollisionShape box = new BoxCollisionShape(dimensions);
       
        object_phy = new RigidBodyControl(box,mass);
        object.addControl(object_phy);
        
        bulletAppState.getPhysicsSpace().add(object_phy);
    }
    
    public void addSpherePhysics(Spatial object, float mass, float radius){
        RigidBodyControl object_phy;
        SphereCollisionShape sphere = new SphereCollisionShape(radius);
       
        object_phy = new RigidBodyControl(sphere,mass);
        object.addControl(object_phy);
        
        bulletAppState.getPhysicsSpace().add(object_phy);
    }
    
    
    public void addNodeBoxPhysics(Node object, float mass, Vector3f dimensions){
        RigidBodyControl object_phy;
        BoxCollisionShape box = new BoxCollisionShape(dimensions);
       
        object_phy = new RigidBodyControl(box,mass);
        object.addControl(object_phy);
        
        bulletAppState.getPhysicsSpace().add(object_phy);
    }
    
    /**
     * Easy to call physics debugger
     * @param isTrue 
     */
    public void debugPhysics(Boolean isTrue) {
        if(isTrue) {
            bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        } else {
            bulletAppState.getPhysicsSpace().disableDebug();
        }
    }

    BulletAppState getBulletAppState() {
        return bulletAppState;
    }
}
