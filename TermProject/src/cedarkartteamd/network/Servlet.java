
package cedarkartteamd.network;

import cedarkartteamd.managers.GameManager.State;
import cedarkartteamd.managers.Powerup;
import cedarkartteamd.managers.SettingsManager.Weather;
import com.jme3.network.ConnectionListener;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Paul Marshall
 */
public class Servlet {
    
    private static final Logger LOG =  Logger.getLogger(Servlet.class.getName());
    
    private int seed = (new Random()).nextInt();
    private Random random = new Random(seed);
    private Server server = null;
    private ArrayList<ActorInit> actors = new ArrayList<>();
    private Weather map;
    
    public Servlet(int port) {
        
        Serializer.registerClasses(ActorUpdateMessage.class, ActorInitMessage.class,
                    ActorDestroyMessage.class, PowerupMessage.class,
                    GameStateMessage.class, TrophyMessage.class,
                    InitMessage.class);
        
        try {
            this.server = Network.createServer(port);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Unable to create server.", ex);
        }
        
        if (this.server != null) {
            this.server.addMessageListener(new ServerListener(),
                    ActorUpdateMessage.class, ActorInitMessage.class,
                    ActorDestroyMessage.class, PowerupMessage.class,
                    GameStateMessage.class, TrophyMessage.class,
                    ActorInit.class, ActorUpdate.class, Powerup.class);
            this.server.addConnectionListener(new ClientListener());
        }
    }
    
    public void start(Weather map) {
        this.map = map;
        if (this.server != null && !this.server.isRunning()) {
            this.server.start();
            System.out.println("[SERVER] Server started.");
        }
    }
    
    public void stop() {
        if (this.server != null && this.server.isRunning()) {
            this.server.close();
        }
    }
    
    public void forceState(State state) {
        server.broadcast(new GameStateMessage(state.name()));
    }
    
    private class ServerListener implements MessageListener<HostedConnection> {

        @Override
        public void messageReceived(HostedConnection source, Message m) {
            if (m instanceof ActorUpdateMessage) {
                ActorUpdateMessage message = (ActorUpdateMessage) m;
                server.broadcast(Filters.notEqualTo(source), message);
            } else if (m instanceof ActorInitMessage) {
                ActorInitMessage message = (ActorInitMessage) m;
                if (message.getInit().getId() < 0) {
                    message.getInit().setId(source.getId());
                }
                server.broadcast(Filters.notEqualTo(source), message);
                for (ActorInit init: actors) {
                    source.send(new ActorInitMessage(init));
                }
                actors.add(message.getInit());
            } else if (m instanceof GameStateMessage) {
                GameStateMessage message = (GameStateMessage) m;
                server.broadcast(Filters.notEqualTo(source), message);
            } else if (m instanceof PowerupMessage) {
                PowerupMessage message = (PowerupMessage) m;
                System.out.println("[SERVER] Powerup message from " + source.getId());
                int target = message.getPowerup().getTarget();
                if (target < 0) {
                    server.broadcast(Filters.notEqualTo(source), message);
                } else {
                    server.getConnection(target).send(message);
                }
            } else if (m instanceof TrophyMessage) {
                TrophyMessage message = (TrophyMessage) m;
                message.setNext(random.nextInt());
                server.broadcast(Filters.notEqualTo(source), message);
                System.out.println("[SERVER] Trophy message from " + source.getId());
            }
        }
    }
    
    private void removeConnection(HostedConnection conn) {
        ActorDestroyMessage message = new ActorDestroyMessage(conn.getId());
        server.broadcast(message);
        for (ActorInit init : actors) {
            if (init.getId() == conn.getId()) {
                actors.remove(init);
                break;
            }
        }
    }
    
    private class ClientListener implements ConnectionListener {

        @Override
        public void connectionAdded(Server server, HostedConnection conn) {
            conn.send(new InitMessage(seed, map.name()));
        }

        @Override
        public void connectionRemoved(Server server, HostedConnection conn) {
            removeConnection(conn);
        }
    }
}
