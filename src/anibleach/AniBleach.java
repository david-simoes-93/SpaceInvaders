/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package anibleach;

import java.awt.Font;
import org.lwjgl.Sys;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

/**
 *
 * @author asus
 */


public class AniBleach {
    
    static long lastFPS;
    static long lastFrame;
    static int fps;
    
    
    public static void main(String[] argv) {
        StartMenu menu = new StartMenu();
        int [] level=menu.start();

        while(level[0]!=0){
            if(level[0]==1){
                SpaceInvaders level1 = new SpaceInvaders();
                level=level1.start();
            }
            else if(level[0]==2){
                SpaceInvaders2 level2 = new SpaceInvaders2();
                level=level2.start();
            }
            else if(level[0]==3){
                SpaceInvaders3 level3 = new SpaceInvaders3();
                level=level3.start();
            }
            else if(level[0]==4){
                SpaceInvaders4 level4 = new SpaceInvaders4();
                level=level4.start();
            }
            else if(level[0]<0){
                if(level[0]==-1){
                    level=menu.continueMenu(0, 0); }                 // if lost level
                else{
                    level=menu.continueMenu(-level[0], level[1]); }  // if won
            }
        }
        try{
            Display.destroy();
            AL.destroy();
        }catch(Exception ex){
            System.out.println("Ending"+ex);
        }
    }
    
    
    // calculate milliseconds passed since last frame
    public static int getDelta() {
        long time = getTime();
        int delta = (int) (time - lastFrame);
        lastFrame = time;
        
        return delta;
    }
    
    // return the system time in milliseconds
    public static long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }
    
    // set FPS in title
    public static void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            Display.setTitle("FPS: " + fps);
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
    }
    
    // Shows a message (blocks everything else) for "ms" milliseconds
    public static boolean showMessage(String mess, long endingTime, int ms){
        Font awtFont = new Font("Arial", Font.BOLD, 40);
        TrueTypeFont font3 = new TrueTypeFont(awtFont, true);
        font3.drawString(300, 300, mess, Color.yellow);
        
        if(getTime()-endingTime>ms){ return true; }
        
        return false;
    }
}



/*
        public boolean touchingEnemies(int x, int y){
            
            int polySides=0;
            int   i, j=polySides-1;
            boolean  oddNodes=false;
            
            int [] polyY={10,10,10};
            int [] polyX={10,15,20};
            
            for (i=0; i<polySides; i++) {
                if ((polyY[i]< y && polyY[j]>=y ||   polyY[j]< y && polyY[i]>=y) &&  (polyX[i]<=x || polyX[j]<=x)) {
                    oddNodes^=(polyX[i]+(y-polyY[i])/(polyY[j]-polyY[i])*(polyX[j]-polyX[i])<x); 
                }
                j=i; 
            }

        return oddNodes; 
        
        }*/
        
        // End Level
        /*public void winGame(boolean win){
            Texture texture;
            
            try {
                if(win){
                    texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("win.png"));
                }
                else{
                    texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("fail.png"));
                }
                

                Color.white.bind();
                texture.bind();
                
                GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0,0);
			GL11.glVertex2f(100,100);
			GL11.glTexCoord2f(1,0);
			GL11.glVertex2f(100+texture.getTextureWidth(),100);
			GL11.glTexCoord2f(1,1);
			GL11.glVertex2f(100+texture.getTextureWidth(),100+texture.getTextureHeight());
			GL11.glTexCoord2f(0,1);
			GL11.glVertex2f(100,100+texture.getTextureHeight());
		GL11.glEnd();
            } catch (IOException ex) {
                //Display.destroy();
            }
        }*/

