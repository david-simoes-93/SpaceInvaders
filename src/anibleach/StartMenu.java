/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package anibleach;

/**
 *
 * @author asus
 */
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class StartMenu {
    /** The fonts to draw to the screen */
    private UnicodeFont font, font2;
    TrueTypeFont font3;
    
    String [] scores = new String[4];
    
    int levelAt=0;
    static int width=800, height=600;
    
    int [] finals=new int [2];
    int mouseAt=-1;
    String [] mouseAtInfo=new String [7];
    boolean soundOn=true;
    /**
     * Start the test
     */
    public int [] start() {
        
        loadListFromFile();
        initGL();       // init OpenGL
        initText();
        initSound(soundOn);
        Display.setTitle("Space Invaders");
        
        int stat=0;
        
        // Frame loop
        while (!Display.isCloseRequested()&& stat==0) {
            render();         // Draw image
            renderText();
            
            stat=checkMouse();
            
            Display.update();   // show actual image
            Display.sync(60);   // cap fps to 60fps
        }
        
        if(stat!=0){
            finals[0]=stat;
            return finals;
        }
        
        
        finals[0]=0;
        return finals;
    }
    
    public void loadListFromFile(){
        ObjectInputStream input;
        
        try {
            input = new ObjectInputStream(new FileInputStream("scores.dat"));
            scores = (String[]) input.readObject();
            input.close();
        } catch (ClassNotFoundException | IOException ex) {
            scores[3]=scores[2]=scores[1]=scores[0]="  Top Scores\n"
                    + "1. 0\n"
                    + "2. 0\n"
                    + "3. 0\n";
            //scores[3]="Level\nUnavailable";
        }
        
    }
    
    public void saveListToFile(){
        ObjectOutputStream output;
        
        try {
            output = new ObjectOutputStream(new FileOutputStream("scores.dat"));
            output.writeObject(scores);
            output.close();
        } catch (IOException ex) {
            System.out.println("Error writing "+ex);
        }
    }
    
    public int [] continueMenu(int level, int Score) {
        loadListFromFile();
        
        if(level!=0){
            addScore(level-2, Score); }
        initText();
        
        int stat=0;
        // Frame loop
        while (!Display.isCloseRequested()&& stat==0) {
            render();         // Draw image
            renderText();
            
            stat=checkMouse();
            
            Display.update();   // show actual image
            Display.sync(60);   // cap fps to 60fps
        }
        saveListToFile();
        if(stat>=0){
            finals[0]=stat;
            return finals;
        }
        
        finals[0]=0;
        return finals;
    }
    
    public void addScore(int level, int score){
        int tempScore;
        
        String [] temp=scores[level].split("[.\n]");
        
        for(int i=2; i<7; i+=2){
            tempScore=Integer.parseInt(temp[i].substring(1));
            
            if(score > tempScore){
                temp[i]=" "+Integer.toString(score);
                score = tempScore;
            }
        }
        scores[level]="Top Scores\n1."+temp[2]+"\n2."+temp[4]+"\n3."+temp[6]+'\n';
    }
    
    
    /* Initialize Sound */
    public void initSound(boolean sound){
        try {
            Audio wavEffect = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("song.wav"));

            //The first two arguments are pitch and gain, the boolean is whether to loop the content
            if(sound){  wavEffect.playAsMusic(1.0f, 0.1f, true); }
            else {      wavEffect.playAsMusic(1.0f, 0.0f, true); }
            
        } catch (Exception ex) {
            System.out.println("Error with audio "+ex);
        }
    }
    
    public int checkMouse(){
        int x = Mouse.getX();
        int y = 600-Mouse.getY();
        
        if (Mouse.isButtonDown(0)) {
            if(x>=140 && x<=240 && y>=500 && y<=550){ setDisplayMode(800, 600, !Display.isFullscreen()); }   // FullScreen
            else if(x>=20 && x<=120 && y>=500 && y<=550){ return -1; }          // Exit
            else if(x>=100 && x<=300 && y>=200 && y<=250){ return 1; }          // Level 1
            else if(x>=100 && x<=300 && y>=270 && y<=320){ return 2; }          // Level 2
            else if(x>=100 && x<=300 && y>=340 && y<=390){ return 3; }          // Level 3
            else if(x>=100 && x<=300 && y>=410 && y<=460){ return 4; }          // Level 3
            else if(x>=260 && x<=360 && y>=500 && y<=550){ soundOn=!soundOn; initSound(soundOn); }  // Sound
        }
        
        if(x>=100 && x<=300 && y>=200 && y<=250){ levelAt=0; mouseAt=1; }       // Level 1
        else if(x>=100 && x<=300 && y>=270 && y<=320){ levelAt=1; mouseAt=2; }  // Level 2
        else if(x>=100 && x<=300 && y>=340 && y<=390){ levelAt=2; mouseAt=3; }  // Level 3
        else if(x>=100 && x<=300 && y>=410 && y<=460){ levelAt=3; mouseAt=4; }  // Level 4
        else if(x>=20 && x<=120 && y>=500 && y<=550){ mouseAt=0; }              // Exit
        else if(x>=140 && x<=240 && y>=500 && y<=550){ mouseAt=5; }             // FullScreen
        else if(x>=260 && x<=360 && y>=500 && y<=550){ mouseAt=6; }             // Mute
        else { mouseAt=-1; }
        
        return 0;
    }
    
    
    /**
     * Set the display mode to be used
     *
     * @param width The width of the display required
     * @param height The height of the display required
     * @param fullscreen True if we want fullscreen mode
     */
    public void setDisplayMode(int width, int height, boolean fullscreen) {
        
        // return if requested DisplayMode is already set
        if ((Display.getDisplayMode().getWidth() == width) &&
                (Display.getDisplayMode().getHeight() == height) &&
                (Display.isFullscreen() == fullscreen)) {
            return;
        }
        
        try {
            DisplayMode targetDisplayMode = null;
            
            if (fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;
                
                for (int i=0;i<modes.length;i++) {
                    DisplayMode current = modes[i];
                    
                    if ((current.getWidth() == width) && (current.getHeight() == height)) {
                        if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
                            if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
                                targetDisplayMode = current;
                                freq = targetDisplayMode.getFrequency();
                            }
                        }
                        
                        // if we've found a match for bpp and frequence against the
                        // original display mode then it's probably best to go for this one
                        // since it's most likely compatible with the monitor
                        if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
                                (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
                            targetDisplayMode = current;
                            break;
                        }
                    }
                }
            } else {
                targetDisplayMode = new DisplayMode(width,height);
            }
            
            if (targetDisplayMode == null) {
                System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
                return;
            }
            
            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);
        } catch (LWJGLException e) {
            System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
        }
    }
    
    
    
    
    
    
    /**
     * Initialise the GL display
     *
     * @param width The width of the display
     * @param height The height of the display
     */
    private void initGL() {
        try {
            Display.setDisplayMode(new DisplayMode(width,height));
            Display.create();
            Display.setVSyncEnabled(true);
        } catch (LWJGLException e) {
            System.out.println("Error at OpenGL "+e);
            System.exit(0);
        }
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClearDepth(1);
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        GL11.glViewport(0,0,width,height);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, width, height, 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        
    }
    
    /**
     * Initialise resources
     */
    public void initText() {
        // load font from file
        try {
            InputStream inputStream	= ResourceLoader.getResourceAsStream("boah.ttf");
            Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            
            awtFont2 = awtFont2.deriveFont(120f);
            font = new UnicodeFont(awtFont2);
            font.addAsciiGlyphs();
            font.getEffects().add(new ColorEffect(java.awt.Color.green));
            font.loadGlyphs();
            
            Font awtFont = new Font("Arial", Font.BOLD, 40);
            awtFont2 = awtFont.deriveFont(40f);
            font2 = new UnicodeFont(awtFont2);
            font2.addAsciiGlyphs();
            font2.getEffects().add(new ColorEffect(java.awt.Color.yellow));
            font2.loadGlyphs();
            
            //Font awtFont = new Font("Arial", Font.BOLD, 40);
            font3 = new TrueTypeFont(awtFont, true);
            
        } catch (FontFormatException | IOException | SlickException e) {
            System.out.println("Error with fonts "+e);
        }
        
        mouseAtInfo[0]="Exit the Game";
        mouseAtInfo[1]="Play level 1!";
        mouseAtInfo[2]="Play level 2!";
        mouseAtInfo[3]="Play level 3!";
        mouseAtInfo[4]="Play level 4!";
        mouseAtInfo[5]="Toggle Fullscreen";
        mouseAtInfo[6]="Toggle Sound";
    }
    
    /**
     * Game loop render
     */
    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        
        // Level 1
        GL11.glColor3f(1f, 0f, 0f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(100, 200);
        GL11.glVertex2f(300, 200);
        GL11.glVertex2f(300, 250);
        GL11.glVertex2f(100, 250);
        GL11.glEnd();
        
        // Level 2
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(100, 270);
        GL11.glVertex2f(300, 270);
        GL11.glVertex2f(300, 320);
        GL11.glVertex2f(100, 320);
        GL11.glEnd();
        
        // Level 3
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(100, 340);
        GL11.glVertex2f(300, 340);
        GL11.glVertex2f(300, 390);
        GL11.glVertex2f(100, 390);
        GL11.glEnd();
        
        // Level 4
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(100, 410);
        GL11.glVertex2f(300, 410);
        GL11.glVertex2f(300, 460);
        GL11.glVertex2f(100, 460);
        GL11.glEnd();
        
        //GL11.glColor3f(0.5f, 0.5f, 0.5f);
        
        // Exit
        GL11.glColor3f(0.1f, .1f, 1f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(20, 500);
        GL11.glVertex2f(120, 500);
        GL11.glVertex2f(120, 550);
        GL11.glVertex2f(20, 550);
        GL11.glEnd();
        
        // FullScreen
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(140, 500);
        GL11.glVertex2f(240, 500);
        GL11.glVertex2f(240, 550);
        GL11.glVertex2f(140, 550);
        GL11.glEnd();
        
        // Sound
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(260, 500);
        GL11.glVertex2f(360, 500);
        GL11.glVertex2f(360, 550);
        GL11.glVertex2f(260, 550);
        GL11.glEnd();
    }
    
    public void renderText(){
        font.drawString(100, 50, "SPACE INVADERS");
        
        font2.drawString(120, 200, "LEVEL 1");
        font2.drawString(120, 270, "LEVEL 2");
        font2.drawString(120, 340, "LEVEL 3");
        font2.drawString(120, 410, "LEVEL 4");
        
        font2.drawString(40, 500, "Exit");
        font2.drawString(160, 500, "FS");
        font2.drawString(265, 500, "Mute");
        
        font2.drawString(350, 200, scores[levelAt]);
        
        if(mouseAt>=0){
            font3.drawString(400, 490, mouseAtInfo[mouseAt], Color.red);
        }
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        
    }
}
