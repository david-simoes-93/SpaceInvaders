/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package anibleach;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author asus
 */
public class SpaceInvaders {
    
    // positions
    float [] ship = {400, 500};
    float [] laser = new float[2];
    float [][] enemies = new float[20][2];

    // game state info
    int enemiesLeft=20;
    boolean laserOn=false;
    boolean goingLeft=false;
    
    // game won/lost
    boolean gameOver=false;
    boolean gameWon=false;
    
    // score info
    long startingTime;
    int shots=50;
    int [] finals=new int[2];
    long endingTime=0;
    // Main
    public int [] start() {
        AniBleach.getDelta();     // call once before loop to initialise lastFrame
        startingTime = AniBleach.lastFPS = AniBleach.getTime(); // call before loop to initialise fps timer
        
        // Define enemies positions
        for(int i=0; i<20; i++){
            if(i%10==0){    enemies[i][0]=85; }
            else{           enemies[i][0]=enemies[i-1][0]+70; }
            
            if(i>=10){      enemies[i][1]=100;  }
            else{           enemies[i][1]=200;}
        }
        
        // Frame loop
        int delta;
        while (!Display.isCloseRequested()) {
            delta = AniBleach.getDelta();
            if(update(delta)==0){       // Update positions
                finals[0]=-1;           // back to menu, ignore score
                return finals;
            }
            renderGL();             // Draw image
            
            if(gameOver){           // If Win/Lose, end the game
                if(gameWon){
                    if(endingTime==0){ 
                        endingTime=AniBleach.getTime();
                        finals[0]=-2;
                        finals[1]=score();
                    }
                    if(AniBleach.showMessage("You scored "+finals[1]+"!", endingTime, 2000)){ return finals; }
                }
                else{ 
                    if(endingTime==0){ 
                        endingTime=AniBleach.getTime();
                        finals[0]=-1;
                    } 
                    if(AniBleach.showMessage("You lost", endingTime, 2000)){ return finals; }
                }
            }
            
            Display.update();   // show actual image
            Display.sync(60);   // cap fps to 60fps
        }

        finals[0]=0;
        return finals;
    }
    
    // Check if laser intersects with enemy
    public boolean intersect(float [] pos){
        
        if(laserOn){     // If there's a laser and any of it's corners is inside an enemy
            if( ( (laser[0]+10>pos[0]-20 && laser[0]+10<pos[0]+20) || (laser[0]-10<pos[0]+20 && laser[0]-10>pos[0]-20))){
                if((laser[1]+20>pos[1]-20 && laser[1]+20<pos[1]+20) || (laser[1]-20>pos[1]-20 && laser[1]-20<pos[1]+20)){
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public int score(){
        long timeLeft= 50-((AniBleach.getTime()-startingTime)/1000);
        if(timeLeft<=0){ timeLeft=1; }
        
        return shots*10*(int)timeLeft; // multiply by time?
    }
    
    // Update position info
    public int update(int delta) {
        if(enemiesLeft==0){     // If no enemies, WIN
            gameWon=true;
            gameOver=true;
        }
        
        float lowest=-130;
        
        for(int i=0; i<20; i++){            // Find if enemies should be going left or right
            if(enemies[i]!=null){
                if(enemies[i][0]<50){       goingLeft=false; }
                else if(enemies[i][0]>750){ goingLeft=true; }
            }
        }
        
        for(int i=0; i<20; i++){            // Update enemies' position, if they exist
            if(enemies[i]!=null){
                if(goingLeft){  enemies[i][0] -= 0.1f * delta; }
                else{           enemies[i][0] += 0.1f * delta; }
                
                enemies[i][1] += 0.005f * delta;
                
                if(lowest<enemies[i][1]){ lowest=enemies[i][1]; }
                
                if(intersect(enemies[i])){  // If laser found one, kill it
                    enemies[i]=null;
                    laserOn=false;
                    enemiesLeft--;
                }
            }
        }
        
        if(laserOn){ laser[1] -= 0.35f * delta; }
        
        // Move Ship
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)){     ship[0] -= 0.35f * delta; }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){    ship[0] += 0.35f * delta; }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)){       ship[1] -= 0.35f * delta; }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)){     ship[1] += 0.35f * delta; }
        
        // Check for a single key press
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.getEventKey() == Keyboard.KEY_SPACE) { // Space - fire laser
                    if(!laserOn){
                        if(shots>1){ shots--; }
                        laserOn=true;
                        laser[0] = ship[0];
                        laser[1] = ship[1];
                    }
                }
                
                if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE){ return 0; }
            }
        }
        
        // keep ship on the screen and below enemies
        if (ship[0] < 0){ ship[0] = 0; }
        if (ship[0] > 800){ ship[0] = 800; }
        if (ship[1]-130 < lowest){ ship[1] = lowest+130; }
        if (ship[1] > 600){ ship[1] = 600; }
        
        // If enemies overrun ship, DEFEAT
        if(lowest>530){ gameOver=true; }
        
        // Kill laser if it's offscreen
        if(laser[1] < -20){ laserOn=false; }
        
        //updateFPS(); // update FPS Counter
        return 1;
    }
    
    
    // Render Graphics
    public void renderGL() {
        // Clear The Screen And The Depth Buffer
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glColor3f(0.5f, 0.5f, 1.0f);
        
        GL11.glBegin(GL11.GL_POLYGON);
        GL11.glVertex2f(ship[0] - 20, ship[1] - 20);
        GL11.glVertex2f(ship[0] - 40, ship[1] + 20);
        GL11.glVertex2f(ship[0] + 40, ship[1] + 20);
        GL11.glVertex2f(ship[0] + 20, ship[1] - 20);
        GL11.glVertex2f(ship[0]     , ship[1] - 50);
        GL11.glEnd();
        
        //draw enemies
        GL11.glColor3f(0.5f, 1f, 0.5f);
        for(int i=0; i<20; i++){
            if(enemies[i]!=null){
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2f(enemies[i][0] - 20, enemies[i][1] + 20);
                GL11.glVertex2f(enemies[i][0] - 20, enemies[i][1] - 20);
                GL11.glVertex2f(enemies[i][0] + 20, enemies[i][1] - 20);
                GL11.glVertex2f(enemies[i][0] + 20, enemies[i][1] + 20);
                GL11.glEnd();
            }
        }
        
        GL11.glColor3f(1f, 0.5f, 0.5f);
        if(laserOn){
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(laser[0] - 10, laser[1] + 20);
            GL11.glVertex2f(laser[0] - 10, laser[1] - 20);
            GL11.glVertex2f(laser[0] + 10, laser[1] - 20);
            GL11.glVertex2f(laser[0] + 10, laser[1] + 20);
            GL11.glEnd();
        }
    }
}
