
package cedarkartteamd.managers;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * Contains all the placement info for a building
 * @author David Riggleman
 */
public class Placement {
    
    public static final Float BUILDING_SCALE = 1f;
    public static final Float BASE_MASS = 1f;
    public static final Float LOCATION_SCALE = 1f;
    public static final Float TRANSLATION_X = 0f;
    public static final Float TRANSLATION_Y = 0f;
    public static final Float TRANSLATION_Z = 0f;
    
    String name;
    String filePath;
    float locationX, locationY, locationZ;
    float scale;
    float rotationX, rotationY, rotationZ, rotationW;
    float mass;
    float hitboxX, hitboxY, hitboxZ;
    float fiction = -1.0f;
    
    Placement(String name, String filePath, float locationX, float locationY,
            float locationZ, float scale, float rotationX, float rotationY,
            float rotationZ, float rotationW, float mass, float hitboxX,
            float hitboxY, float hitboxZ){
        this.name = name;
        this.filePath = filePath;
        this.locationX = locationX;
        this.locationY = locationY;
        this.locationZ = locationZ;
        this.scale = scale;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        this.rotationW = rotationW;
        this.mass = mass;
        this.hitboxX = hitboxX;
        this.hitboxY = hitboxY;
        this.hitboxZ = hitboxZ;
    }

    public Vector3f getLocalTranslation(){
        float x = locationX * LOCATION_SCALE + TRANSLATION_X;
        float y = locationY + TRANSLATION_Y;
        float z = locationZ * LOCATION_SCALE + TRANSLATION_Z;
        
        return new Vector3f(x, y, z);
    }
    
    public Vector3f getHitbox(){
        float x = hitboxX;
        float y = hitboxY;
        float z = hitboxZ;
        
        return new Vector3f(x, y, z);
    }
    
    public float getScale(){
        return scale * BUILDING_SCALE;
    }
    
    public float getMass(){
        return mass * BASE_MASS;
    }
    
    public String getName(){
        return name;
    }
    
    public Quaternion getRotation(){
        Quaternion rotation = new Quaternion(rotationX, rotationY, rotationZ, rotationW); 
        return rotation;
    }
}
