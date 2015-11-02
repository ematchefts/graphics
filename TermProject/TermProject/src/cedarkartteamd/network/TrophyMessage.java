
package cedarkartteamd.network;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Paul Marshall
 */
@Serializable
public class TrophyMessage extends AbstractMessage {
    
    private int id;
    private int next;
    
    public TrophyMessage() {
        this.id = -1;
        this.next = -1;
    }
    
    public TrophyMessage(int id) {
        this.id = id;
        this.next = -1;
    }
    
    public TrophyMessage(int id, int next) {
        this.id = id;
        this.next = next;
    }
    
    public int getID() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }
}
