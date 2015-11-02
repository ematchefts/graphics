
package cedarkartteamd.network;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Paul Marshall
 */
@Serializable
public class GameStateMessage extends AbstractMessage {
    
    private String state;
    
    public GameStateMessage() {
        this.state = null;
    }
    
    public GameStateMessage(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
