import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;

import javax.swing.*;

import java.awt.image.*; 
import java.io.*; 

import javax.imageio.*; 
import javax.swing.JPanel;

import java.util.*;

class GamePanel extends JPanel implements KeyListener{
	private final int RIGHT=1,LEFT=-1,NODIRECTION=0;
	public static int endx,endy;
	private int direction=LEFT,startx,starty;
	private double angle=20;
	private boolean[] keys;
	//old down = 3, up = -0.5
	private double down=5,up=-1.5,shootdire=down,length=50;
	private boolean shooting;
	public boolean ready=false;
	private Miner mainFrame;
	//obj
	private Image man = new ImageIcon("man.png").getImage();
	private static int current_level=1;
	private int manPos=0;
	private ArrayList<Integer> goals=new ArrayList<Integer>();
	private int numDynamites;
	private static Image dynamitePic = new ImageIcon("dynamite.png").getImage();
	public static Image TNT_Image = new ImageIcon("tnt.png").getImage();
	private static Image boomPic = new ImageIcon("boom.png").getImage();
	public static ArrayList<ArrayList<Integer>> explode_nextRound = new ArrayList<ArrayList<Integer>>(); //all of the TNT that should be exploded
	public static ArrayList<ArrayList<Integer>> explode_thisRound = new ArrayList<ArrayList<Integer>>();
	private Image background;
	private int TNT_timer=0;
	private int manDirection=NODIRECTION;
	private int boom_timer = -1;
	private int objcaught=-1;
	private int totals;
	private int totalval=0;
	public Objects obj = new Objects();
	public GamePanel(Miner m){
		totals=obj.returnTimes();
		numDynamites=3;
		keys = new boolean[65535];
		goals.add(1);
		background = new ImageIcon("goldminer2.jpg").getImage();
		mainFrame=m;
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
		startx=130+manPos;
	}
	public void move(){
		//swing
		obj.movePigs(objcaught);
		double maxlength=550;
		if (angle>=160){
			direction=LEFT;
		}
		else if (angle<=20){
			direction=RIGHT;
		}
		//shoot
		if (length>=maxlength){
			shootdire=up;
		}
		else if (shootdire!= up && length<maxlength){
			shootdire=down;
		}
		//calling old TNT's and checking for new exploding ones that the user reached
		for (int i=0;i<obj.numTNT;i++){
			
			if (obj.returnTNT_pos(i,0)<=endx && obj.returnTNT_pos(i,0)+TNT_Image.getWidth(null)>=endx && obj.returnTNT_pos(i,1)<=endy && obj.returnTNT_pos(i,1)+TNT_Image.getHeight(null)>=endy){
				explode_thisRound.add(obj.returnTNT_pos2(i));
				
			}
		}
		for (int i=0;i<explode_thisRound.size();i++){
			//get the co-ords at explode_thisRound, find the index at TNT_pos and call checkTNT on it to add more things to explode_nextRound;
			for (int k=0;k<obj.returnTNT_posSize();k++){
				if (obj.returnTNT_pos2(k)==explode_thisRound.get(i)){
					obj.checkTNT(k);
				}
			}
		}
		
		if (objcaught!=-1){
			//this means that a piece of obj has been caught
			obj.move(objcaught,endx,endy);
			shootdire=obj.returnSpeed(objcaught);
			if (length==50){
				totalval+=obj.returnIntData(objcaught,obj.VAL);
				obj.removeVal(objcaught);
				objcaught=-1;
			}
		}
		else{
			if (shootdire==down){
				objcaught = obj.catchobj();
			}
		}
		if (shooting){
			length=Math.min(length+shootdire,maxlength);
			length=Math.max(length+shootdire,50);
			if (length==50){
				shooting=false;
				shootdire=down;
			}
		}
		else{
			shooting = keys[KeyEvent.VK_SPACE];
			angle +=direction;
		}
	}
	
	
	public void throwDynamite(){
		if (keys[KeyEvent.VK_D] && numDynamites>0){
			if (objcaught!=-1){
				obj.removeVal(objcaught);
				numDynamites-=1;
				objcaught=-1;
			}
		shootdire = up;
		}
	}
	public void timesUp(){
		JOptionPane.showMessageDialog (null, "Time is up for level"+current_level);
		newLevel();
	}
	public void checkIfLevelIsDone(){
		if (obj.returnNumObj()==0){
			JOptionPane.showMessageDialog (null, "You have grabbed everything for level"+current_level);
			newLevel();
		}
	}
	public void newLevel(){
		current_level+=1;
		if (current_level>5){
			gameFinished();
		}
		else{
			//mainFrame.state = mainFrame.SHOP;
			//setFocusable(false);
			//mainFrame.shop.requestFocus();
			obj.clearEverything();
			obj.loadMyStuff(current_level);
			totals = obj.returnTimes();
			length=50;
		}
		
	}
	public void gameFinished(){
		
	}
	public void changeTime(){
		
		if (totals==0){
			timesUp();
		}
		else{
			checkIfLevelIsDone();
		}
		totals--;
		if (boom_timer!=-1){
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
	public void paintComponent(Graphics g){
		g.drawImage(background,0,0,this);  
		g.setColor(Color.black);
		
		if (manDirection==RIGHT && manPos!=450){
			//endx=Math.max(0,endx-10);
			angle=Math.min(160, angle+0.7);
		}
		else if (manDirection==LEFT && manPos!=0){
			angle=Math.max(0, angle-0.7);
			//endx=Math.min(800, endx+10);
		}
		endx=startx+(int)(length*Math.cos(Math.toRadians(angle)));
		endy=starty+(int)(length*(Math.sin(Math.toRadians(angle))));
		g.setColor(Color.yellow);
		Graphics2D g2d = (Graphics2D)g;
		for (int i=0;i<numDynamites;i++){
			g.drawImage(dynamitePic,460+i*35,85,this);
		}
		g.drawImage(man,100+manPos,43,this);
		g.drawLine(startx,starty,endx,endy);
		for (int i=0;i<obj.numobj;i++){
			g.drawImage(obj.returnSprites(i),obj.returnIntData(i,obj.X),obj.returnIntData(i,obj.Y),this);
		}
		for (int i=0;i<obj.numTNT;i++){
			g.drawImage(TNT_Image,obj.returnTNT_pos(i,0),obj.returnTNT_pos(i,1),this);
		}
		g.setColor(Color.black);
		//drawing strings
		Font font = new Font("Calisto MT", Font.PLAIN, 20);
		g.setFont(font);
		g.drawString("Time: "+totals, 680, 55);
		g.drawString("Goal: "+obj.returnGoals(),20,90);
		g.drawString("Level: "+current_level,680,90);
		g.drawString("Money: "+totalval, 20, 55);
		for (int i=0;i<explode_thisRound.size();i++){
			if (boom_timer<1){
				g.drawImage(boomPic,explode_thisRound.get(i).get(0)+TNT_Image.getWidth(null)/2-boomPic.getWidth(null)/2,explode_thisRound.get(i).get(1)+TNT_Image.getHeight(null)/2-boomPic.getHeight(null)/2,this);
				if (boom_timer==-1){
					boom_timer=0;
					shootdire=up;
				}
			}
			else{
				obj.removeTNT_pos(explode_thisRound.get(i));
				obj.numTNT-=1;
				
				boom_timer=-1; //-1 indicates that it will not be timed until the timer is back at 0
			}
		}
		if (boom_timer==-1){
			//maybe delete this line:
			explode_thisRound.clear();
		}
		if (30==TNT_timer){
			explode_thisRound.clear();
			//if it is time, draw the exploding TNT's and replace them with the ones that will be exploded next time the timer is at 2
			for (int i=0;i<explode_nextRound.size();i++){
				explode_thisRound.add(explode_nextRound.get(i));
			}
			explode_nextRound.clear();
			TNT_timer=0;
		}
		TNT_timer++;
	}
}
