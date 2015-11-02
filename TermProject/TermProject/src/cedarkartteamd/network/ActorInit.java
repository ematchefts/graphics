
package cedarkartteamd.network;

import java.io.Serializable;

/**
 *
 * @author Paul Marshall
 */
@com.jme3.network.serializing.Serializable
public class ActorInit implements Serializable {
    
    private int id;
    private String vehicle;
    
    public ActorInit() {
        this.id = -1;
        this.vehicle = null;
    }
    
    public ActorInit(int id, String vehicle) {
        this.id = id;
        this.vehicle = vehicle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }
    
}
