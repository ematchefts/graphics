/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cedarkartteamd.managers;

import cedarkartteamd.managers.VehicleManager.Vehicle;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;

/**
 *
 * @author nabond
 */
public class SettingsManager implements ICedarKartManager {

    public static enum Weather {
        SUNRISE {
            @Override public Vector3f direction() {
                return new Vector3f(-0.680414f, -0.272166f, -0.680414f);
            }
        },
        NIGHT {
            @Override public Vector3f direction() {
                return new Vector3f(0.30231458f, -0.3619007f, 0.8818356f);
            }
        },
        WINTER {
            @Override public Vector3f direction() {
                return new Vector3f(0.36782876f, -0.8136427f, 0.4502089f);
            }
        },
        APOCOLYPSE {
            @Override public Vector3f direction() {
                return new Vector3f(0.6728581f, -0.29458696f, 0.6785871f);
            }
        };
        
        public abstract Vector3f direction();
    };
    
    public static enum Level {
        LOW {
            @Override public boolean bloom() { return false; }
            @Override public boolean dof() { return false; }
            @Override public boolean fxaa() { return false; }
            @Override public boolean advancedLighting() { return false; }
            @Override public boolean shadows() { return false; }
            @Override public boolean ssao() { return false; }
        },
        MEDIUM {
            @Override public boolean bloom() { return true; }
            @Override public boolean dof() { return false; }
            @Override public boolean fxaa() { return true; }
            @Override public boolean advancedLighting() { return false; }
            @Override public boolean shadows() { return false; }
            @Override public boolean ssao() { return false; }
        },
        HIGH {
            @Override public boolean bloom() { return true; }
            @Override public boolean dof() { return true; }
            @Override public boolean fxaa() { return true; }
            @Override public boolean advancedLighting() { return false; }
            @Override public boolean shadows() { return true; }
            @Override public boolean ssao() { return false; }
        },
        ULTRA {
            @Override public boolean bloom() { return true; }
            @Override public boolean dof() { return true; }
            @Override public boolean fxaa() { return true; }
            @Override public boolean advancedLighting() { return true; }
            @Override public boolean shadows() { return true; }
            @Override public boolean ssao() { return true; }
        };
        
        public abstract boolean bloom();
        public abstract boolean dof();
        public abstract boolean fxaa();
        public abstract boolean shadows();
        public abstract boolean advancedLighting();
        public abstract boolean ssao();
    }
    
    public static final Weather DEFAULT_WEATHER = Weather.SUNRISE;
    public static final Vehicle DEFAULT_VEHICLE = Vehicle.GOLF_CART;
    
    private String username;
    private Weather weather;
    private Vehicle vehicle;
    private int controller;
    private int winningScore;
    private AppSettings settings;
    
    private boolean advancedLighting = false;       // !! PERFORMANCE WARNING !!
    private boolean shadows = true;
    private boolean bloom = true;
    private boolean dof = true;
    private boolean ssao = false;
    private boolean fxaa = true;

    SettingsManager(AppSettings settings) {
        weather = DEFAULT_WEATHER;
        vehicle = DEFAULT_VEHICLE;
        controller = -1;
        winningScore = 5;
        this.settings = settings;
    }
    
    @Override
    public void start() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public Vehicle getVehicle() {
        return this.vehicle;
    }
    
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    
    public int getController() {
        return this.controller;
    }
    
    public void setController(int controller) {
        this.controller = controller;
    }

    public boolean advancedLighting() {
        return advancedLighting;
    }

    public void setAdvancedLighting(boolean advancedLighting) {
        this.advancedLighting = advancedLighting;
    }

    public boolean shadows() {
        return shadows;
    }

    public void setShadows(boolean shadows) {
        this.shadows = shadows;
    }

    public boolean bloom() {
        return bloom;
    }

    public void setBloom(boolean bloom) {
        this.bloom = bloom;
    }

    public boolean dof() {
        return dof;
    }

    public void setDof(boolean dof) {
        this.dof = dof;
    }

    public boolean ssao() {
        return ssao;
    }

    public void setSsao(boolean ssao) {
        this.ssao = ssao;
    }

    public boolean fxaa() {
        return fxaa;
    }

    public void setFxaa(boolean fxaa) {
        this.fxaa = fxaa;
    }
    
    public void setLevel(String level) {
        setLevel(Level.valueOf(level));
    }
    
    public void setLevel(Level level) {
        this.advancedLighting = level.advancedLighting();
        this.shadows = level.shadows();
        this.bloom = level.bloom();
        this.dof = level.dof();
        this.fxaa = level.fxaa();
        this.ssao = level.ssao();
    }
    
    public Level getLevel() {
        if (this.ssao || this.advancedLighting) {
            return Level.ULTRA;
        } else if (this.dof || this.shadows) {
            return Level.HIGH;
        } else if (this.bloom || this.fxaa) {
            return Level.MEDIUM;
        } else {
            return Level.LOW;
        }
    }
    
    public int getHeight(){
        return settings.getHeight();
    }
    public int getWidth(){
        return settings.getWidth();
    }
    
    public void setWinningScore(int score) {
        this.winningScore = score;
    }
    
    public int getWinningScore() {
        return this.winningScore;
    }
}
