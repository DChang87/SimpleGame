import java.awt.*;
import java.awt.event.*;
import java.awt.Font;
import java.awt.Image;
import java.applet.*;
import javax.swing.*;
import javax.swing.JPanel;
import java.util.*;

class GamePanel extends JPanel implements KeyListener{
	//Audio:
	private AudioClip back;
	public static AudioClip pull,pigsound,dynamitesound,TNTsound;
	
	//Images:
	private Image man = new ImageIcon("man.png").getImage();
	private Image scoreboard;
	private static Image dynamitePic = new ImageIcon("dynamite.png").getImage();
	public static Image TNT_Image = new ImageIcon("tnt.png").getImage();
	private static Image boomPic = new ImageIcon("boom.png").getImage();
	private Image background;
	
	//classes:
	public Objects obj = new Objects();
	private Miner mainFrame;
	
	//explosion arraylists:
	public static ArrayList<ArrayList<Integer>> explode_nextRound = new ArrayList<ArrayList<Integer>>(); //all of the TNT that should be exploded
	public static ArrayList<ArrayList<Integer>> explode_thisRound = new ArrayList<ArrayList<Integer>>();
	
	//miscellaneous variables to set up the game
	private final int RIGHT=1,LEFT=-1,NODIRECTION=0;
	public static int endx,endy; //the end point of the claw
	private int direction=LEFT,startx,starty; //direction of the swing is default at LEFT. startx, starty is the coordinate where the string for the claw begins
	private double angle=20; //angle of the swing
	private boolean[] keys; 
	private double down=5,up=-1.5,shootdire=down,length=50; //default values of the claw. the speed to go down and up are 5 and -1.5.
	//shooting direction is down, the length of the string for the claw is at 50
	private boolean shooting; //indicates if the claw is shooting or not
	public boolean ready=false;
	private static int current_level=5;
	private int manPos=0; //the position of the man and his platform
	private int numDynamites;
	private int TNT_timer=0; //the counter for setting off TNTs
	private int manDirection=NODIRECTION; //the direction of the man
	private int boom_timer = -1; //this is used to figure out the time an which the boom effect takes place. -1 means that the counter is not in use
	private int objcaught=-1; //the current object that is caught (currently at -1, which means no object is caught)
	private int totals; //total number of seconds
	private int totalval=0; //total value of all of the grabs
	private double maxlength=550; //maximum length of the string
	private final int max_level=5;
	
	public GamePanel(Miner m){
		totals=obj.returnTimes();
		numDynamites=3; //user gets 3 dynamites 
		//loading audio: 
		back = Applet.newAudioClip(getClass().getResource("back.wav"));
		pigsound = Applet.newAudioClip(getClass().getResource("pigoink.wav"));
		pull = Applet.newAudioClip(getClass().getResource("pull.wav"));
		dynamitesound = Applet.newAudioClip(getClass().getResource("dynamitesound.wav"));
		TNTsound = Applet.newAudioClip(getClass().getResource("TNTsound.wav"));
		back.loop();
		
		//loading image
		scoreboard = new ImageIcon("scoreboard.jpg").getImage();
		background = new ImageIcon("goldminer2.jpg").getImage();
		
		//miscellaneous set-up
		mainFrame=m;
		keys = new boolean[65535];
		startx=30;
		starty=90;
		setSize(800,640);
		addKeyListener(this);

	}
	
	public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }
	public void moveMan(){
		//this method moves the man and his platform as the user presses left and right keys
		if (keys[KeyEvent.VK_LEFT]){
			manDirection=LEFT;
			manPos=Math.max(0, manPos-5);
		}
		else if (keys[KeyEvent.VK_RIGHT]){
			manDirection=RIGHT;
			manPos=Math.min(450,manPos+5);
		}
		else{
			manDirection=NODIRECTION;
		}
		manChangeAngle();
		startx=130+manPos; //startx differs from the position of the man since startx should be the middle of the instrument that the man is using.
	}
	public void angleAdjustment(){
		//adjusting the angles and the direction;
		//if the angle is greater than or equal to 160, change direction
		//if angle is less than or equal to 20, change direction
		if (angle>=160){
			direction=LEFT;
		}
		else if (angle<=20){
			direction=RIGHT;
		}
	}
	public void adjustShootingDirection(){
		//this method adjusts the shooting direction depending on the length of the string
		//if the length is longer than or equal to the maxlength, it is time for the clamp to move up, vice versa
		if (length>=maxlength){
			shootdire=up;
		}
		else if (shootdire!= up && length<maxlength){
			shootdire=down;
		}		
	}
	
	public void move(){
		//this method carries out the different actions involving movements on the screen
		//adjust the angle and the direction of the clamp as needed
		obj.movePigs(objcaught);
		angleAdjustment();
		adjustShootingDirection();
		
		//calling old TNT's and checking for new exploding ones that the user reached
		for (int i=0;i<obj.numTNT;i++){
			//check every TNT and if the clamp reaches it, add it to the arraylist of the ones to blow up this round
			if (obj.returnTNT_pos(i,0)<=endx && obj.returnTNT_pos(i,0)+TNT_Image.getWidth(null)>=endx && obj.returnTNT_pos(i,1)<=endy && obj.returnTNT_pos(i,1)+TNT_Image.getHeight(null)>=endy){
				explode_thisRound.add(obj.returnTNT_pos2(i));
			}
		}
		for (int i=0;i<explode_thisRound.size();i++){
			//check through all of the ones exploding this round and carry out the explosion. any TNT that is affected in the new explosion are added to the explode_nextRound arraylist
			for (int k=0;k<obj.returnTNT_posSize();k++){
				if (obj.returnTNT_pos2(k)==explode_thisRound.get(i)){
					obj.checkTNT(k);
				}
			}
		}
		
		
		if (objcaught!=-1){
			//this means that a piece of obj has been caught
			obj.move(objcaught,endx,endy); //move the object so it moves with the clamp
			shootdire=obj.returnSpeed(objcaught); //change the shooting direction so the clamp moves at the right speed
			if (length==50){
				//this means that the object has reached the top
				totalval+=obj.returnIntData(objcaught,obj.VAL); //add up the value
				obj.removeVal(objcaught); //remove the object since it was collected
				objcaught=-1; //reset the object caught variable
			}
		}
		else{
			//this means that no object is caught, therefore stop playing the pull audio since it only should be played when the man is pulling up an object
			pull.stop();
			if (shootdire==down){
				//if the shooting direction is down, check if the claw catches an object
				objcaught = obj.catchobj();
			}
		}
		if (shooting){
			//if it is shooting ,adjust the length of the string
			length=Math.min(length+shootdire,maxlength);
			length=Math.max(length+shootdire,50);
			if (length==50){
				shooting=false;
				shootdire=down;
			}
		}
		else{
			//if it is not currently shooting, check the activity of the space bar to see if the user wanted to shoot
			shooting = keys[KeyEvent.VK_SPACE];
			//when it is not shooting, the clamp should be swinging, thus adding to the angle.
			angle +=direction;
		}
	}
	
	public void throwDynamite(){
		//this method carries out the part where the man throws the Dynamite to blow something on the clamp up
		//make sure that it is a valid throw by checking the number of remaining dynamites and if something is actually caught
		//remove the object that is being blown up and set the shooting direction as up since the clamp should be moving up after the dynamite is used
		if (keys[KeyEvent.VK_D] && numDynamites>0){
			if (objcaught!=-1){
				dynamitesound.play();
				obj.removeVal(objcaught);
				numDynamites-=1;
				objcaught=-1;
			}
		shootdire = up;
		}
	}
	public void timesUp(){
		//when this method is called, it means that the time has been used up for the current level and move on to the next level
		//remove the focus so while the message dialog is showing, the user cannot control the game
		//return the focus after since the user needs to focus to play the next level
		//call the newlevel function to go to the next level
		setFocusable(false);
		JOptionPane.showMessageDialog (null, "Time is up for level"+current_level);
		newLevel();
		setFocusable(true);
		requestFocus();
	}
	public void checkIfLevelIsDone(){
		//this method does the same thing as timesUp, except is is called if the user grabbed everything already and nothing is left to grab
		if (obj.returnNumObj()==0){
			setFocusable(false);
			JOptionPane.showMessageDialog (null, "You have grabbed everything for level"+current_level);
			newLevel();
			setFocusable(true);
			requestFocus();
		}
	}
	public void newLevel(){
		//if it is only a typical transition from a level to a level, clear out everything and load the new level data
		//if there the user did not reach the goal, the game is over
		current_level+=1;
		if (mainFrame.hs.returnLoading()){
			mainFrame.hs.gameFinished(totalval, getName());
		}
		else if (totalval<obj.returnGoals()){
			JOptionPane.showMessageDialog (null, "You did not meet the goal for this level. Good luck next time.");
			mainFrame.hs.gameFinished(totalval, getName());
		}
		else{
			obj.clearEverything();
			obj.loadMyStuff(current_level);
			totals = obj.returnTimes();
			length=50;
		}
	}
	public String getName(){
		//ask for the user's name to include on the scoreboard
		String name = JOptionPane.showInputDialog("Name: ");
		return name;
	}
	public void changeTime(){
		//this method is called every second to adjust the totals and the boom_timer counters
		if (totals==0){
			timesUp();
		}
		else{
			//if time is not up, then check if the level is done
			checkIfLevelIsDone();
		}
		totals--; //totals counts down
		if (boom_timer!=-1){
			//if the boom_timer counter is being used (so NOT being -1)
			boom_timer+=1;
		}
	}
	public static int returnLevel(){
		return current_level;
	}
    // --------- Keystuff---------------
    public void keyTyped(KeyEvent e){}
    public void keyPressed(KeyEvent e){
    	keys[e.getKeyCode()]=true;
    }
    public void keyReleased(KeyEvent e){
    	keys[e.getKeyCode()]=false;
    }
    public void manChangeAngle(){
    	//if the man is moving, the angle of the string also has to change so it does not look so awkward
		if (manDirection==RIGHT && manPos<=450){
			angle=Math.min(160, angle+0.7);
		}
		else if (manDirection==LEFT && manPos<=0){
			angle=Math.max(0, angle-0.7);
		}
    }
	public void paintComponent(Graphics g){
		Font Sfont = new Font("Calisto MT", Font.BOLD, 30);
		g.setFont(Sfont);
		if (mainFrame.hs.returnLoading()){
			//if the game is over, draw the scoreboard and draw the data as strings on
			g.drawImage(scoreboard,0,0,this);
			for (int i=0;i<10;i++){
				g.drawString((i+1)+". "+mainFrame.hs.returnName(i),50,200+40*i);
				g.drawString(mainFrame.hs.returnScore(i)+"", 500, 200+40*i);
			}
		}
		else if (current_level<=max_level){
			//if game continues
			g.drawImage(background,0,0,this);  
			g.setColor(Color.black);
			endx=startx+(int)(length*Math.cos(Math.toRadians(angle))); //calculate the position of the claw by using trigs
			endy=starty+(int)(length*(Math.sin(Math.toRadians(angle))));
			for (int i=0;i<numDynamites;i++){ //draw the dynamites
				g.drawImage(dynamitePic,460+i*35,85,this);
			}
			g.drawImage(man,100+manPos,43,this); //draw the man and his platform
			g.drawLine(startx,starty,endx,endy); //draw the string
			for (int i=0;i<obj.numobj;i++){ //draw all of the objects underground (excluding TNT)
				g.drawImage(obj.returnSprites(i),obj.returnIntData(i,obj.X),obj.returnIntData(i,obj.Y),this);
			}
			for (int i=0;i<obj.numTNT;i++){ //draw the TNTs
				g.drawImage(TNT_Image,obj.returnTNT_pos(i,0),obj.returnTNT_pos(i,1),this);
			}
			
			//drawing strings
			Font font = new Font("Calisto MT", Font.PLAIN, 20);
			g.setFont(font);
			g.drawString("Time: "+totals, 680, 55);
			g.drawString("Goal: "+obj.returnGoals(),20,90);
			g.drawString("Level: "+current_level,680,90);
			g.drawString("Money: "+totalval, 20, 55);

			//draw the explosions
			for (int i=0;i<explode_thisRound.size();i++){
				if (boom_timer<1){
					//draw the explosions while the boom_timer is still under 1
					TNTsound.play();
					g.drawImage(boomPic,explode_thisRound.get(i).get(0)+TNT_Image.getWidth(null)/2-boomPic.getWidth(null)/2,explode_thisRound.get(i).get(1)+TNT_Image.getHeight(null)/2-boomPic.getHeight(null)/2,this);
					if (boom_timer==-1){
						//if the boom timer wasnt in use, make it in use by setting it as 0
						boom_timer=0;
						shootdire=up;
					}
				}
				else{
					//if the boom_timer is 1 or greater, the boom image has been drawn for enough time and remove the TNTs
					obj.removeTNT_pos(explode_thisRound.get(i));
					obj.numTNT-=1;
					boom_timer=-1; //reset the counter (-1 indicates that it will not be timed until the timer is back at 0)
				}
			}
			if (300==TNT_timer){
				explode_thisRound.clear();
				//if it is time, draw the exploding TNT's and replace them with the ones that will be exploded next time
				for (int i=0;i<explode_nextRound.size();i++){
					explode_thisRound.add(explode_nextRound.get(i));
				}
				explode_nextRound.clear();
				TNT_timer=0;
			}
			TNT_timer++;
		}
	}
}
