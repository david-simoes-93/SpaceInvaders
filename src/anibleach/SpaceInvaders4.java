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
public class SpaceInvaders4 {
    final int NumberOfEnemies=32;
    final int enemiesPerRow=8;
    final int NumberOfVulnerables=2;
    // positions
    float [] ship = {400, 500};
    float [] laser1 = new float[2];
    float [] laser2 = new float[2];
    float [][] enemies = new float[NumberOfEnemies][2];

    // game state info
    int enemiesLeft=NumberOfEnemies;
    boolean laser1On=false;
    boolean laser2On=false;
    boolean goingLeft=false;
    boolean [] vulnerable = new boolean[NumberOfEnemies];
    
    // game won/lost
    boolean gameOver=false;
    boolean gameWon=false;
    
    // score info
    long startingTime;
    int shots=100;
    int [] finals=new int[2];
    long endingTime=0;
    // Main
    public int [] start() {
        AniBleach.getDelta();     // call once before loop to initialise lastFrame
        startingTime = AniBleach.lastFPS = AniBleach.getTime(); // call before loop to initialise fps timer
        
        // Define enemies positions
        int enemiesY=0;
        for(int i=0; i<NumberOfEnemies; i++){
            if(i%enemiesPerRow==0){  
                enemiesY+=100;
                enemies[i][0]=155; 
            }
            else{
                enemies[i][0]=enemies[i-1][0]+70; 
            }
            
            enemies[i][1]=enemiesY;
        }
        
        int vulnie;
        for(int i=0; i<NumberOfVulnerables; i++){
            vulnie=(int)(Math.random()*enemiesPerRow);
            System.out.println(vulnie);
            vulnerable[NumberOfEnemies-vulnie-1]=true;
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
                        finals[0]=-5;
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
    public boolean intersect(float [] pos, boolean laserOn, float [] laser){
        
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
        
        return shots*10*(int)timeLeft; 
    }
    
    // Update position info
    public int update(int delta) {
        if(enemiesLeft==0){     // If no enemies, WIN
            gameWon=true;
            gameOver=true;
        }
        
        float lowest=-130;
        
        for(int i=0; i<NumberOfEnemies; i++){            // Find if enemies should be going left or right
            if(enemies[i]!=null){
                if(enemies[i][0]<50){       goingLeft=false; }
                else if(enemies[i][0]>750){ goingLeft=true; }
            }
        }
        
        for(int i=0; i<NumberOfEnemies; i++){            // Update enemies' position, if they exist
            if(enemies[i]!=null){
                if(goingLeft){  enemies[i][0] -= 0.2f * delta; }
                else{           enemies[i][0] += 0.2f * delta; }
                
                enemies[i][1] += 0.01f * delta;
                
                if(lowest<enemies[i][1]){ lowest=enemies[i][1]; }
                
                int rows=NumberOfEnemies/enemiesPerRow;
                if(vulnerable[i]){
                    if(intersect(enemies[i], laser1On, laser1)){  // If left laser found one, kill it
                        enemies[i]=null;
                        laser1On=false;
                        enemiesLeft--;
                        
                        //set enemies to vulnerable
                        if((i-1)/enemiesPerRow == i/enemiesPerRow && i-1>=0){ vulnerable[i-1]=true; }
                        if((i+1)/enemiesPerRow == i/enemiesPerRow){ vulnerable[i+1]=true; }
                        if(i-enemiesPerRow>=0){
                            vulnerable[i-enemiesPerRow]=true;
                            if((i-1)/enemiesPerRow == i/enemiesPerRow){ vulnerable[i-1-enemiesPerRow]=true; }
                            if((i+1)/enemiesPerRow == i/enemiesPerRow){ vulnerable[i+1-enemiesPerRow]=true; }
                        }
                        if(i+enemiesPerRow<NumberOfEnemies){
                            vulnerable[i+enemiesPerRow]=true;
                            if((i-1)/enemiesPerRow == i/enemiesPerRow){ vulnerable[i-1+enemiesPerRow]=true; }
                            if((i+1)/enemiesPerRow == i/enemiesPerRow){ vulnerable[i+1+enemiesPerRow]=true; }
                        }
                    }
                    else if(intersect(enemies[i], laser2On, laser2)){  // else if right laser found one, kill it
                        enemies[i]=null;
                        laser2On=false;
                        enemiesLeft--;
                        
                        //set enemies to vulnerable
                        if((i-1)/enemiesPerRow == i/enemiesPerRow && i-1>=0){ vulnerable[i-1]=true; }
                        if((i+1)/enemiesPerRow == i/enemiesPerRow){ vulnerable[i+1]=true; }
                        if(i-enemiesPerRow>=0){
                            vulnerable[i-enemiesPerRow]=true;
                            if((i-1)/enemiesPerRow == i/enemiesPerRow){ vulnerable[i-1-enemiesPerRow]=true; }
                            if((i+1)/enemiesPerRow == i/enemiesPerRow){ vulnerable[i+1-enemiesPerRow]=true; }
                        }
                        if(i+enemiesPerRow<NumberOfEnemies){
                            vulnerable[i+enemiesPerRow]=true;
                            if((i-1)/enemiesPerRow == i/enemiesPerRow){ vulnerable[i-1+enemiesPerRow]=true; }
                            if((i+1)/enemiesPerRow == i/enemiesPerRow){ vulnerable[i+1+enemiesPerRow]=true; }
                        }
                    }
                }
            }
        }
        
        if(laser1On){ 
            laser1[0] -= 0.1f * delta;
            laser1[1] -= 0.35f * delta; 
        }
        if(laser2On){ 
            laser2[0] += 0.1f * delta;
            laser2[1] -= 0.35f * delta; 
        }
        
        // Move Ship
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)){     ship[0] += 0.35f * delta; }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){    ship[0] -= 0.35f * delta; }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)){       ship[1] += 0.35f * delta; }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)){     ship[1] -= 0.35f * delta; }
        
        // Check for a single key press
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.getEventKey() == Keyboard.KEY_SPACE) { // Space - fire laser
                    if(!laser1On){
                        if(shots>1){ shots--; }
                        laser1On=true;
                        laser1[0] = ship[0];
                        laser1[1] = ship[1];
                    }
                    if(!laser2On){
                        if(shots>1){ shots--; }
                        laser2On=true;
                        laser2[0] = ship[0];
                        laser2[1] = ship[1];
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
        if(laser1[1] < -20 || laser1[0]<10 || laser1[0]>810){ laser1On=false; }
        if(laser2[1] < -20 || laser2[0]<10 || laser2[0]>810){ laser2On=false; }
        
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
        for(int i=0; i<NumberOfEnemies; i++){
            if(enemies[i]!=null){
                if(vulnerable[i]){
                    GL11.glColor3f(0.5f, 1f, 0.5f);
                }
                else{
                    GL11.glColor3f(0.5f, 0.5f, 0.5f);
                }
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2f(enemies[i][0] - 20, enemies[i][1] + 20);
                GL11.glVertex2f(enemies[i][0] - 20, enemies[i][1] - 20);
                GL11.glVertex2f(enemies[i][0] + 20, enemies[i][1] - 20);
                GL11.glVertex2f(enemies[i][0] + 20, enemies[i][1] + 20);
                GL11.glEnd();
            }
        }
        
        GL11.glColor3f(1f, 0.5f, 0.5f);
        if(laser1On){
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(laser1[0] - 10, laser1[1] + 20);
            GL11.glVertex2f(laser1[0] - 10, laser1[1] - 20);
            GL11.glVertex2f(laser1[0] + 10, laser1[1] - 20);
            GL11.glVertex2f(laser1[0] + 10, laser1[1] + 20);
            GL11.glEnd();
        }
        
        if(laser2On){
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(laser2[0] - 10, laser2[1] + 20);
            GL11.glVertex2f(laser2[0] - 10, laser2[1] - 20);
            GL11.glVertex2f(laser2[0] + 10, laser2[1] - 20);
            GL11.glVertex2f(laser2[0] + 10, laser2[1] + 20);
            GL11.glEnd();
        }
    }
}
