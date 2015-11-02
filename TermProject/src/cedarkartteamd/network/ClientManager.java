
package cedarkartteamd.network;

import cedarkartteamd.Main;
import cedarkartteamd.managers.GameManager;
import cedarkartteamd.managers.GameManager.State;
import cedarkartteamd.managers.PlayerManager;
import cedarkartteamd.managers.Powerup;
import cedarkartteamd.managers.SettingsManager.Weather;
import cedarkartteamd.managers.WorldManager;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 *
 * @author Paul Marshall
 */
public class ClientManager {
    
    private final Main app;
    private final GameManager gameMan;
    private final WorldManager worldMan;
    private final PlayerManager playerMan;
    
    private Client client;
    
    public ClientManager(Main app, GameManager gameMan, WorldManager worldMan,
            PlayerManager playerMan) {
        this.app = app;
        this.gameMan = gameMan;
        this.worldMan = worldMan;
        this.playerMan = playerMan;
        
        Serializer.registerClasses(ActorUpdateMessage.class, ActorInitMessage.class,
                    ActorDestroyMessage.class, PowerupMessage.class,
                    GameStateMessage.class, TrophyMessage.class, InitMessage.class,
                    ActorInit.class, ActorUpdate.class, Powerup.class);
        
        // Start client:
        try {
            String host = gameMan.getHost();
            
            if (host == null) {
                host = "localhost";
            }
            
            this.client = Network.connectToServer(host, gameMan.getPort());
            this.client.addMessageListener(new ClientListener(),
                    ActorUpdateMessage.class, ActorInitMessage.class,
                    ActorDestroyMessage.class, PowerupMessage.class,
                    GameStateMessage.class, TrophyMessage.class,
                    InitMessage.class);
            this.client.start();
            this.client.send(init());
        } catch (IOException ex) {
            System.err.println("[CLIENT] Client could not connect to provided host.");
        }
    }
    
    private ActorInitMessage init() {
        ActorInit init = new ActorInit(this.client.getId(), this.gameMan
                .getSettings().getVehicle().name());
        return new ActorInitMessage(init);
    }
    
    public int getId() {
        if (this.client != null) {
            return this.client.getId();
        } else {
            return -1;
        }
    }
    
    public void disconnect() {
        if (this.client != null) {
            this.client.close();
        }
    }
    
    public void update(ActorUpdate update) {
        if (this.client != null) {
            this.client.send(new ActorUpdateMessage(update));
        }
    }
    
    public void pause() {
        if (this.client != null) {
            this.client.send(new GameStateMessage(State.PAUSE.name()));
        }
    }
    
    public void resume() {
        if (this.client != null) {
            this.client.send(new GameStateMessage(State.RESUME.name()));
        }
    }
    
    public void end() {
        if (this.client != null) {
            this.client.send(new GameStateMessage(State.END.name()));
        }
    }
    
    public void capture() {
        if (this.client != null) {
            this.client.send(new TrophyMessage(getId()));
        }
    }
    
    public void powerup(Powerup powerup) {
        if (this.client != null) {
            this.client.send(new PowerupMessage(powerup));
        }
    }
    
    private void updateActor(final ActorUpdate update) {
        this.app.enqueue(new Callable<ActorUpdate>() {
            @Override public ActorUpdate call() throws Exception {
                worldMan.updatePlayer(update);
                return update;
            }
        });
    }
    
    private void captureTrophy(final int id, final int next) {
        this.app.enqueue(new Callable<Integer>() {
            @Override public Integer call() throws Exception {
                gameMan.getTrophies().captureTrophy(next);
                return id;
            }
        });
    }
    
    private void applyPowerup(final Powerup powerup) {
        this.app.enqueue(new Callable<Powerup>() {
            @Override public Powerup call() throws Exception {
                gameMan.getTrophies().capturePowerup(powerup.getId());
                if (powerup.getTarget() < 0 || powerup.getTarget() == getId()) {
                    switch (powerup.getType()) {
                        case BEES:
                            playerMan.setBees(true);
                            break;
                        case TRAY:
                            playerMan.enableTray();
                            break;
                        case IMPULSE:
                            playerMan.impulsePowerup();
                            break;
                        case GRAVITY:
                            playerMan.setGravity(false);
                            break;
                        case RANDOMIZER:
                            playerMan.randomizeLocation();
                            break;
                    }
                }
                
                return powerup;
            }
        });
    }
    
    private void add(final ActorInit init) {
        this.app.enqueue(new Callable<ActorInit>() {
            @Override public ActorInit call() throws Exception {
                worldMan.addPlayer(init);
                return init;
            }
        });
    }
    
    private void remove(final int id) {
        this.app.enqueue(new Callable<Integer>() {
            @Override public Integer call() throws Exception {
                worldMan.removePlayer(id);
                return id;
            }
        });
    }
    
    private void starter() {
        this.app.enqueue(new Callable() {
            @Override public Object call() throws Exception {
                gameMan.setRunning(true);
                return null;
            }
        });
    }
    
    private void pauser() {
        this.app.enqueue(new Callable() {
            @Override public Object call() throws Exception {
                gameMan.pauseGame(false);
                return null;
            }
        });
    }
    
    private void resumer() {
        this.app.enqueue(new Callable() {
            @Override public Object call() throws Exception {
                gameMan.resumeGame();
                return null;
            }
        });
    }
    
    private void ender() {
        this.app.enqueue(new Callable() {
            @Override public Object call() throws Exception {
                gameMan.endGame();
                return null;
            }
        });
    }
    
    private void initer(final int seed, final String weather) {
        this.app.enqueue(new Callable<Integer>() {
            @Override public Integer call() throws Exception {
                gameMan.getTrophies().setSeed(seed);
                if (weather != null) {
                    gameMan.getSettings().setWeather(Weather.valueOf(weather));
                }
                gameMan.startGame();
                return seed;
            }
        });
    }
    
    private class ClientListener implements MessageListener<Client> {
        @Override public void messageReceived(Client source, Message m) {
            if (m instanceof ActorUpdateMessage) {
                updateActor(((ActorUpdateMessage) m).getContent());
            } else if (m instanceof TrophyMessage) {
                TrophyMessage trophy = (TrophyMessage) m;
                captureTrophy(trophy.getID(), trophy.getNext());
                System.out.println("[CLIENT] Trophy message received");
            } else if (m instanceof PowerupMessage) {
                applyPowerup(((PowerupMessage) m).getPowerup());
                System.out.println("[CLIENT] Powerup message received");
            } else if (m instanceof ActorInitMessage) {
                add(((ActorInitMessage) m).getInit());
            } else if (m instanceof ActorDestroyMessage) {
                remove(((ActorDestroyMessage) m).getID());
            } else if (m instanceof GameStateMessage) {
                State state = State.valueOf(((GameStateMessage) m).getState());
                switch (state) {
                    case START:
                        starter();
                        break;
                    case PAUSE:
                        pauser();
                        break;
                    case RESUME:
                        resumer();
                        break;
                    case END:
                        ender();
                        break;
                }
            } else if (m instanceof InitMessage) {
                InitMessage message = (InitMessage) m;
                initer(message.getSeed(), message.getWeather());
            }
        }
        
    }
}
