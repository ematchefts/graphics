
package cedarkartteamd.network;

import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import java.io.Serializable;

/**
 *
 * @author Paul Marshall
 */
@com.jme3.network.serializing.Serializable
public class ActorUpdate implements Serializable {
    
    private Vector3f location;
    private Matrix3f rotation;
    private Vector3f velocity;
    private Vector3f angular;
    private int id;
    
    public ActorUpdate() {
        this(-1);
    }
    
    public ActorUpdate(int id) {
        this(id, Vector3f.ZERO, Matrix3f.ZERO, Vector3f.ZERO, Vector3f.ZERO);
    }
    
    public ActorUpdate(int id, Vector3f location, Matrix3f rotation,
            Vector3f velocity, Vector3f angular) {
        this.id = id;
        this.location = location;
        this.rotation = rotation;
        this.velocity = velocity;
        this.angular = angular;
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }

    public void setRotation(Matrix3f rotation) {
        this.rotation = rotation;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public void setAngular(Vector3f angular) {
        this.angular = angular;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vector3f getLocation() {
        return location;
    }

    public Matrix3f getRotation() {
        return rotation;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public Vector3f getAngular() {
        return angular;
    }

    public int getId() {
        return id;
    }
}
