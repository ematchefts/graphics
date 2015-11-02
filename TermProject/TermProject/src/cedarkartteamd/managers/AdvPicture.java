/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cedarkartteamd.managers;

import com.jme3.ui.Picture;

/**
 *
 * @author Gregory
 */
public class AdvPicture extends Picture {

    float height;
    float width;
    float xPos;
    float yPos;

    public AdvPicture() {
        super();
    }

    public AdvPicture(java.lang.String name) {
        super(name);
    }

    public AdvPicture(java.lang.String name, boolean flipY) {
        super(name, flipY);
    }

    @Override
    public void setHeight(float height) {
        this.height = height;
        super.setHeight(height);
    }

    @Override
    public void setWidth(float width) {
        this.width = width;
        super.setWidth(width);
    }

    @Override
    public void setPosition(float x, float y) {
        xPos = x;
        yPos = y;
        super.setPosition(x, y);
    }

    public float getX() {
        return xPos;
    }

    public float getY() {
        return yPos;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }
    
}
