
package cedarkartteamd.network;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Paul Marshall
 */
@Serializable
public class ActorUpdateMessage extends AbstractMessage {
    
    private ActorUpdate content;
    
    public ActorUpdateMessage() {
        this.content = null;
    }
    
    public ActorUpdateMessage(ActorUpdate content) {
        this.content = content;
    }
    
    public ActorUpdate getContent() {
        return this.content;
    }
}
