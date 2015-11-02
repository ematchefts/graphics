
package cedarkartteamd.network;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Paul Marshall
 */
@Serializable
public class InitMessage extends AbstractMessage {
    
    private int seed;
    private String weather;
    
    public InitMessage() {
        this.seed = -1;
        this.weather = "SUNRISE";
    }
    
    public InitMessage(int seed, String weather) {
        this.seed = seed;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
 }
