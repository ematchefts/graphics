
package cedarkartteamd.managers;

import cedarkartteamd.managers.SettingsManager.Weather;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LightControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author Paul Marshall
 */
public class VehicleManager implements ICedarKartManager {
    
    private final AssetManager assetManager;
    private final PlacementManager placements;
    
    public static enum Vehicle {
        GOLF_CART {
            @Override public String toString() { return "Golf Cart"; }
            @Override public String path() {
                return "Models/vehicles/golfCart/golfCart.mesh.xml";
            }
            @Override public float scale() {
                return 0.075f;
            }
            @Override public float acceleration() {
                return 1000.0f;
            }
            @Override public Vector3f jumpForce() {
                return  new Vector3f(0, 3000, 0);
            }
        },
        ATOM_2 {
            @Override public String toString() { return "Ariel Atom 2"; }
            @Override public String path() {
                return "Models/vehicles/atom2/atom2.j3o";
            }
            @Override public float scale() {
                return 1.0f;
            }
            @Override public float acceleration() {
                return 400.0f;
            }
            @Override public Vector3f jumpForce() {
                return  new Vector3f(0, 1000, 0);
            }
        },
        VIPER {
            @Override public String toString() { return "Dodge Viper"; }
            @Override public String path() {
                return "Models/vehicles/viper/viper_low.j3o";
            }
            @Override public float scale() {
                return 1.0f;
            }
            @Override public float acceleration() {
                return 5000.0f;
            }
            @Override public Vector3f jumpForce() {
                return  new Vector3f(0, 10000, 0);
            }
        },
        LANDSPEEDER {
            @Override public String toString() { return "Landspeeder"; }
            @Override public String path() {
                return "Models/vehicles/landspeeder/landspeeder.mesh.xml";
            }
            @Override public float scale() {
                return 0.05f;
            }
            @Override public float acceleration() {
                return 1000.0f;
            }
            @Override public Vector3f jumpForce() {
                return  new Vector3f(0, 3000, 0);
            }
        };
        
        public abstract String path();
        public abstract float scale();
        public abstract float acceleration();
        public abstract Vector3f jumpForce();
    }
    
    public class VehicleSet {
        private final Node vehicleNode;
        private final VehicleControl vehicle;
        
        public VehicleSet(Node vehicleNode, VehicleControl vehicle) {
            this.vehicleNode = vehicleNode;
            this.vehicle = vehicle;
        }

        public Node getVehicleNode() {
            return vehicleNode;
        }

        public VehicleControl getVehicle() {
            return vehicle;
        }
    }
    
    public VehicleManager(AssetManager assetManager, PlacementManager placements) {
        this.assetManager = assetManager;
        this.placements = placements;
    }

    @Override
    public void start() {}
    
    public VehicleSet makeCart(Vehicle vehicle, Spatial player, Node rootNode, SettingsManager.Weather weather) {
        switch (vehicle) {
            case GOLF_CART:
                return makeGolfCart(player, rootNode, weather);
            case ATOM_2:
                return makeAtom2(player);
            case VIPER:
                return makeViper(player);
            case LANDSPEEDER:
                return makeLandspeeder(player);
            default:
                return null;
        }
    }
    
    private VehicleSet makeGolfCart(Spatial player, Node rootNode, SettingsManager.Weather weather) {
        float vehicleScale = 0.75f;
        
        Material wheel_mat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wheel_mat.setColor("Color", ColorRGBA.Black);
        
        //create a car collision shape
        Node vehicleNode = new Node(player.getName());
        CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(player);
        vehicleNode.attachChild(player);
        VehicleControl vehicle = new VehicleControl(carHull, 40f);
        
        // adjust vehicle locations and rotations here
        //vehicleNode.setLocalTranslation(-25f,10f,90);
        vehicleNode.setLocalTranslation(-41.63699f, 8.491901f, 102.99223f);
        vehicleNode.setLocalRotation(new Quaternion(0.017232679f, 0.056404218f, 0.0f, 1.0f));

        //setting suspension values for wheels, this can be a bit tricky
        //see also https://docs.google.com/Doc?docid=0AXVUZ5xw6XpKZGNuZG56a3FfMzU0Z2NyZnF4Zmo&hl=en
        float stiffness = 50.0f;//200=f1 car
        float compValue = .3f; //(should be lower than damp)
        float dampValue = .4f;
        vehicle.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionStiffness(stiffness);
        vehicle.setMass(900);
        vehicle.setFriction(1);
        
        //Friction
        if(weather.equals(SettingsManager.Weather.WINTER)){
            vehicle.setFrictionSlip(1.8f);
        }
        else
        {
            vehicle.setFrictionSlip(5.0f);
        }

        // Add Physics Control on Vehicle object
        vehicleNode.addControl(vehicle);
        
        //Create four wheels and add them at their locations
        Vector3f wheelDirection = new Vector3f(0, -1, 0); // was 0, -1, 0
        Vector3f wheelAxle = new Vector3f(-1, 0, 0); // was -1, 0, 0
        float radiusf = 0.2f * vehicleScale;
        float radiusb = 0.2f * vehicleScale;
        float restLength = 0.3f * vehicleScale;
        float yOff = 0.42f * vehicleScale;
        float xOff = 0.36f * vehicleScale;
        float zOffFront = -0.61f * vehicleScale;
        float zOffBack = 0.71f * vehicleScale;
        
        Cylinder wheelMeshf = new Cylinder(16, 16, radiusf, radiusf * 0.6f, true);
        Cylinder wheelMeshb = new Cylinder(16, 16, radiusb, radiusb * 0.6f, true);
        
        // Front Right Wheel
        Node node2 = new Node("wheel Front Right node");
        Geometry wheels2 = new Geometry("wheel 2", wheelMeshf);
        node2.attachChild(wheels2);
        wheels2.rotate(0, FastMath.HALF_PI, 0);
        wheels2.setMaterial(wheel_mat);
        vehicle.addWheel(node2, new Vector3f(xOff, yOff, zOffFront),
                wheelDirection, wheelAxle, restLength, radiusf, false);
        
        // Back Right Wheel
        Node node1 = new Node("wheel Back Right node");
        Geometry wheels1 = new Geometry("wheel 1", wheelMeshf);
        node1.attachChild(wheels1);
        wheels1.rotate(0, FastMath.HALF_PI, 0);
        wheels1.setMaterial(wheel_mat);
        vehicle.addWheel(node1, new Vector3f(-xOff, yOff, zOffFront),
                wheelDirection, wheelAxle, restLength, radiusf, false);

        // Front Left Wheel
        Node node3 = new Node("wheel Front Left node");
        Geometry wheels3 = new Geometry("wheel 3", wheelMeshb);
        node3.attachChild(wheels3);
        wheels3.rotate(0, FastMath.HALF_PI, 0);
        wheels3.setMaterial(wheel_mat);
        vehicle.addWheel(node3, new Vector3f(xOff, yOff, zOffBack),
                wheelDirection, wheelAxle, restLength, radiusb, true);

        // Back Left Wheel
        Node node4 = new Node("wheel Back Left node");
        Geometry wheels4 = new Geometry("wheel 4", wheelMeshb);
        node4.attachChild(wheels4);
        wheels4.rotate(0, FastMath.HALF_PI, 0);
        wheels4.setMaterial(wheel_mat);
        vehicle.addWheel(node4, new Vector3f(-xOff, yOff, zOffBack),
                wheelDirection, wheelAxle, restLength, radiusb, true);

        // add wheels to the object and then to the real world.
        vehicleNode.attachChild(node1);
        vehicleNode.attachChild(node2);
        vehicleNode.attachChild(node3);
        vehicleNode.attachChild(node4);
        
        //Make headlights if night
        if(weather == Weather.NIGHT){
            Node lNode = new Node();
            lNode.setLocalTranslation(new Vector3f(0f,0.4f,0.37f));
            lNode.setLocalRotation(new Quaternion(0,0.748f,-0.6626f,0));
            vehicleNode.attachChild(lNode);

            SpotLight sLight = new SpotLight();
            sLight.setColor(ColorRGBA.White.mult(1.5f));
            sLight.setSpotOuterAngle(.58f);
            sLight.setSpotInnerAngle(.1745f);
            sLight.setSpotRange(20f);

            Geometry lightModel = new Geometry("light", new Box(.035f,.01f,.02f));
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.White);
            mat.setColor("GlowColor", ColorRGBA.White);
            lightModel.setMaterial(mat);
            lightModel.setLocalTranslation(0.17f,-0.27f,0f);
            Geometry lightModel2 = lightModel.clone();
            lightModel2.setLocalTranslation(-0.17f,-0.27f,0f);
            lNode.attachChild(lightModel);
            lNode.attachChild(lightModel2);
            
            //sLight.setPosition(vehicle.getPhysicsLocation());
            //sLight.setDirection(new Vector3f(0,-3.14f/2f,0));
            //sLight.setDirection(vehicleNode.getLocalRotation().mult(new Vector3f(0,-1,0)));
            lNode.addControl(new LightControl(sLight));
            rootNode.addLight(sLight);
        }
        
        return new VehicleSet(vehicleNode, vehicle);
    }
    
    private VehicleSet makeAtom2(Spatial player) {
        Node vehicleNode = new Node(player.getName());
        VehicleControl vehicle = player.getControl(VehicleControl.class);
        vehicleNode.attachChild(player);
        vehicleNode.addControl(vehicle);
        
        return new VehicleSet(vehicleNode, vehicle);
    }
    
    private VehicleSet makeViper(Spatial player) {
        Node vehicleNode = new Node(player.getName());
        VehicleControl vehicle = player.getControl(VehicleControl.class);
        vehicle.setMass(3500);
        vehicleNode.attachChild(player);
        vehicleNode.addControl(vehicle);
        
        return new VehicleSet(vehicleNode, vehicle);
    }
    
    private VehicleSet makeLandspeeder(Spatial player) {
        float vehicleScale = .75f;
        
        Material wheel_mat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wheel_mat.setColor("Color", new ColorRGBA(0.0f, 0.0f, 0.0f, 0.0f));
        // comment below to show wheels
        wheel_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        //create a car collision shape
        Node vehicleNode = new Node(player.getName());
        
        //attempt to lower the center of mass
        CompoundCollisionShape carHull = new CompoundCollisionShape();
        carHull.addChildShape(CollisionShapeFactory.createDynamicMeshShape(player), new Vector3f(0f, 0.5f, 0f));
        //CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(player);
        
        player.setLocalTranslation(0, 0.5f, 0);
        
        vehicleNode.attachChild(player);
        
        VehicleControl vehicle = new VehicleControl(carHull, 40f);
        
        // adjust vehicle locations and rotations here
        //vehicleNode.setLocalTranslation(-25f,10f,90);
        vehicleNode.setLocalTranslation(-41.63699f, 8.491901f, 102.99223f);
        vehicleNode.setLocalRotation(new Quaternion(0.017232679f, 0.056404218f, 0.0f, 1.0f));

        //setting suspension values for wheels, this can be a bit tricky
        //see also https://docs.google.com/Doc?docid=0AXVUZ5xw6XpKZGNuZG56a3FfMzU0Z2NyZnF4Zmo&hl=en
        float stiffness = 40.0f;//200=f1 car
        float compValue = .3f; //(should be lower than damp)
        float dampValue = .4f;
        vehicle.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionStiffness(stiffness);
        vehicle.setMass(900);
        vehicle.setFriction(1f);
        vehicle.setFrictionSlip(10000.0f);

        // Add Physics Control on Vehicle object
        vehicleNode.addControl(vehicle);
        
        //Create four wheels and add them at their locations
        Vector3f wheelDirection = new Vector3f(0, -1, 0); // was 0, -1, 0
        Vector3f wheelAxle = new Vector3f(-1, 0, 0); // was -1, 0, 0

        float radiusf = 0.1f * vehicleScale;
        float radiusb = 0.1f * vehicleScale;
        float restLength = 0.3f * vehicleScale;
        float yOff = -0.05f * vehicleScale + 0.5f;
        float xOff = 0.76f * vehicleScale;
        float zOffFront = -0.61f * vehicleScale;
        float zOffBack = 0.71f * vehicleScale;
		
		
        Cylinder wheelMeshf = new Cylinder(16, 16, radiusf, radiusf * 0.2f, true);
        Cylinder wheelMeshb = new Cylinder(16, 16, radiusb, radiusb * 0.2f, true);
        
        // Front Right Wheel
        Node node2 = new Node("wheel Front Right node");
        Geometry wheels2 = new Geometry("wheel 2", wheelMeshf);
        node2.attachChild(wheels2);
        wheels2.rotate(0, FastMath.HALF_PI, 0);
        wheels2.setMaterial(wheel_mat);
        wheels2.setQueueBucket(RenderQueue.Bucket.Transparent);
        vehicle.addWheel(node2, new Vector3f(xOff, yOff, zOffFront),
                wheelDirection, wheelAxle, restLength, radiusf, false);
        
        // Back Right Wheel
        Node node1 = new Node("wheel Back Right node");
        Geometry wheels1 = new Geometry("wheel 1", wheelMeshf);
        node1.attachChild(wheels1);
        wheels1.rotate(0, FastMath.HALF_PI, 0);
        wheels1.setMaterial(wheel_mat);
        wheels1.setQueueBucket(RenderQueue.Bucket.Transparent);
        vehicle.addWheel(node1, new Vector3f(-xOff, yOff, zOffFront),
                wheelDirection, wheelAxle, restLength, radiusf, false);

        // Front Left Wheel
        Node node3 = new Node("wheel Front Left node");
        Geometry wheels3 = new Geometry("wheel 3", wheelMeshb);
        node3.attachChild(wheels3);
        wheels3.rotate(0, FastMath.HALF_PI, 0);
        wheels3.setMaterial(wheel_mat);
        wheels3.setQueueBucket(RenderQueue.Bucket.Transparent);
        vehicle.addWheel(node3, new Vector3f(xOff, yOff, zOffBack),
                wheelDirection, wheelAxle, restLength, radiusb, true);

        // Back Left Wheel
        Node node4 = new Node("wheel Back Left node");
        Geometry wheels4 = new Geometry("wheel 4", wheelMeshb);
        node4.attachChild(wheels4);
        wheels4.rotate(0, FastMath.HALF_PI, 0);
        wheels4.setMaterial(wheel_mat);
        wheels4.setQueueBucket(RenderQueue.Bucket.Transparent);
        vehicle.addWheel(node4, new Vector3f(-xOff, yOff, zOffBack),
                wheelDirection, wheelAxle, restLength, radiusb, true);

        // add wheels to the object and then to the real world.
        vehicleNode.attachChild(node1);
        vehicleNode.attachChild(node2);
        vehicleNode.attachChild(node3);
        vehicleNode.attachChild(node4);
        
        return new VehicleSet(vehicleNode, vehicle);
    }
}
