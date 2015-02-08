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
	private final int RIGHT=1,LEFT=-1;
	private int endx,endy,direction=LEFT,angle=20,startx,starty;
	private boolean[] keys;
	//old down = 3, up = -0.5
	private double down=5,up=-1.5,shootdire=down,length=50;
	private boolean shooting;
	public boolean ready=false;
	private Miner mainFrame;
	//obj
	private ArrayList<Integer> goals=new ArrayList<Integer>();
	private int numDynamites;
	private static Image dynamitePic = new ImageIcon("dynamite.png").getImage();
	public static Image TNT_Image = new ImageIcon("tnt.png").getImage();
	private static Image boomPic = new ImageIcon("boom.png").getImage();
	public static ArrayList<ArrayList<Integer>> explode_nextRound = new ArrayList<ArrayList<Integer>>(); //all of the TNT that should be exploded
	public static ArrayList<ArrayList<Integer>> explode_thisRound = new ArrayList<ArrayList<Integer>>();
	private Image background;
	private int TNT_timer=0;
	private int boom_timer = -1;
	private int objcaught=-1;
	private int totals=200;
	private int totalval=0;
	public Objects obj = new Objects();
	public GamePanel(Miner m){
		numDynamites=3;
		keys = new boolean[65535];
		goals.add(1);
		background = new ImageIcon("goldminer1.jpg").getImage();
		mainFrame=m;
		startx=400;
		starty=90;
		setSize(800,640);
		addKeyListener(this);
	}
	
	public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }
	public void move(){
		//swing
		obj.movePigs(objcaught);
		double maxlength=400;
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
			//System.out.println("LOOP WITH TNT");
			if (obj.distance(obj.TNT_pos.get(i).get(0)+TNT_Image.getWidth(null)/2,endx)<=TNT_Image.getWidth(null)/2 &&obj.distance(obj.TNT_pos.get(i).get(1)+TNT_Image.getHeight(null)/2,endx)<=TNT_Image.getHeight(null)/2){
				explode_thisRound.add(obj.TNT_pos.get(i));
			}
		}
		for (int i=0;i<explode_thisRound.size();i++){
			//get the co-ords at explode_thisRound, find the index at TNT_pos and call checkTNT on it to add more things to explode_nextRound;
			System.out.println("THIS ROUND LOOP"+explode_thisRound.size());
			obj.checkTNT(obj.TNT_pos.indexOf(explode_thisRound.get(i)));
		}
		
		if (objcaught!=-1){
			//this means that a piece of obj has been caught
			obj.move(objcaught,endx,endy);
			shootdire=obj.speed.get(objcaught);
			if (length==50){
				totalval+=obj.integer_data.get(objcaught).get(obj.VAL);
				obj.removeVal(objcaught);
				objcaught=-1;
			}
		}
		else{
			if (shootdire==down){
				objcaught = obj.catchobj(endx,endy);
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

	public void changeTime(){
		//System.out.println("changetime");
		if (totals==0){
			totals=1234;
		}
		totals--;
		if (boom_timer!=-1){
			System.out.println("changetime yo");
			boom_timer+=1;
		}
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
		//System.out.println("paint"+TNT_timer);
		g.drawImage(background,0,0,this);  
		g.setColor(Color.black);
		endx=startx+(int)(length*Math.cos(Math.toRadians(angle)));
		endy=starty+(int)(length*(Math.sin(Math.toRadians(angle))));
		g.drawLine(startx,starty,endx,endy);
		g.setColor(Color.yellow);
		Graphics2D g2d = (Graphics2D)g;
		for (int i=0;i<obj.numobj;i++){
			g.drawImage(obj.sprites.get(i),obj.integer_data.get(i).get(obj.X),obj.integer_data.get(i).get(obj.Y),this);
		}
		for (int i=0;i<numDynamites;i++){
			g.drawImage(dynamitePic,460+i*35,85,this);
		}
		for (int i=0;i<obj.numTNT;i++){
			g.drawImage(TNT_Image,obj.TNT_pos.get(i).get(0),obj.TNT_pos.get(i).get(1),this);
		}
		g.setColor(Color.black);
		//g.drawOval(300+TNT_Image.getWidth(null)-150, 300+TNT_Image.getHeight(null)-150, 300, 300);
		//g.drawOval(300-100, 300-100, 200, 200);
		//g.drawOval(300+TNT_Image.getWidth(null)/2,300+TNT_Image.getHeight(null)/2,20,20);
		//g.drawOval(300+TNT_Image.getWidth(null)/2-200, 300+TNT_Image.getHeight(null)/2-200, 200, 200);
		//drawing strings
		Font font = new Font("Calisto MT", Font.PLAIN, 20);
		g.setFont(font);
		g.drawString("Time: "+totals, 680, 55);
		g.drawString("Goal: "+goals.get(obj.current_level-1),20,90);
		g.drawString("current level: "+obj.current_level,680,90);
		g.drawString("Money: "+totalval, 20, 55);
		//System.out.println("timer"+boom_timer+explode_thisRound.size());
		for (int i=0;i<explode_thisRound.size();i++){
			//System.out.println("explode?");
			if (boom_timer<1){
				g.drawImage(boomPic,explode_thisRound.get(i).get(0)+TNT_Image.getWidth(null)/2-boomPic.getWidth(null)/2,explode_thisRound.get(i).get(1)+TNT_Image.getHeight(null)/2-boomPic.getHeight(null)/2,this);
				//ystem.out.println("explode?");
				if (boom_timer==-1){
					boom_timer=0;
					shootdire=up;
				}
			}
			else{
				obj.TNT_pos.remove(explode_thisRound.get(i));
				obj.numTNT-=1;
				
				boom_timer=-1; //-1 indicates that it will not be timed until the timer is back at 0
			}
			//System.out.println("timerAFTER"+boom_timer);
		}
		if (boom_timer==-1){
			explode_thisRound.clear();
		}
		if (1000<TNT_timer && TNT_timer<=3000){
			System.out.println("TRUE");
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
