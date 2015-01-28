import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
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
	private ArrayList<Integer> objx = new ArrayList<Integer>();
	private ArrayList<Integer> objy = new ArrayList<Integer>();
	private ArrayList<Integer> objv = new ArrayList<Integer>();
	private ArrayList<Double> objs = new ArrayList<Double>();
	private ArrayList<Image> objsprites = new ArrayList<Image>();
	//
	private int numobj=4;
	private Image background,obj1;
	private final int NOobj=-1;
	private int objcaught=-1;
	private int totals=0;
	private int totalval=0;
	private int level=1;
	public GamePanel(Miner m){
		keys = new boolean[65535];
		background = new ImageIcon("goldminer1.jpg").getImage();
		addThings();
		mainFrame=m;
		startx=400;
		starty=90;
		setSize(800,640);
		addKeyListener(this);
	}
	public void addThings(){
		objsprites.add(new ImageIcon("gold2.png").getImage());
		objx.add(100);
		objy.add(200);
		objv.add(50);
		objs.add(-1.0);
		objsprites.add(new ImageIcon("gold1.png").getImage());
		objx.add(300);
		objy.add(400);
		objs.add(-0.25);
		objv.add(200);
		objsprites.add(new ImageIcon("rock1.png").getImage());
		objx.add(200);
		objy.add(300);
		objs.add(-0.25);
		objv.add(10);
		objsprites.add(new ImageIcon("rock2.png").getImage());
		objx.add(600);
		objy.add(300);
		objs.add(-0.5);
		objv.add(5);
		goals.add(220);
	}
	public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }
	public void move(){
		//swing
		
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
		
		if (objcaught!=-1){
			//this means that a piece of obj has been caught
			moveobj();
			shootdire=objs.get(objcaught);
			if (length==50){
				System.out.println("length"+length);
				totalval+=objv.get(objcaught);
				removeVal();
				numobj--;
				objcaught=-1;
			}
		}
		else{
			objcaught = catchobj();
		}
		//System.out.println(objcaught+" objcaught");
		//move
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
	public void removeVal(){
		objx.remove(objcaught);
		objy.remove(objcaught);
		objv.remove(objcaught);
		objsprites.remove(objcaught);
	}
	public void moveobj(){
		//this method moves the obj that is caught with the clamp
		//System.out.println("moveobj");
		objx.set(objcaught,endx-objsprites.get(objcaught).getWidth(null)/2);
		objy.set(objcaught,endy-objsprites.get(objcaught).getHeight(null)/2);
	}
	public int catchobj(){
		for (int i=0;i<numobj;i++){
			if (distance(endy,objy.get(i)+objsprites.get(i).getHeight(null)/2)<=64&&distance(endx,objx.get(i)+objsprites.get(i).getWidth(null)/2)<=72){
				System.out.println(distance(endy,objy.get(i))+" "+objsprites.get(i).getHeight(null)/2);
				System.out.println(distance(endx,objx.get(i))+" "+objsprites.get(i).getWidth(null)/2);
				return i;
			}
			if (distance(endy,objy.get(i))<=objsprites.get(i).getHeight(null)/2 && distance(endx,objx.get(i))<=objsprites.get(i).getWidth(null)/2){
				return i; 
			}
			
		}
		return NOobj;
	}
	public int distance(int a,int b){
		return Math.abs(b-a);
	}
	public void changeTime(){
		totals++;
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
		endx=startx+(int)(length*Math.cos(Math.toRadians(angle)));
		endy=starty+(int)(length*(Math.sin(Math.toRadians(angle))));
		g.drawLine(startx,starty,endx,endy);
		g.setColor(Color.yellow);
		Graphics2D g2d = (Graphics2D)g;
		for (int i=0;i<numobj;i++){
			g.drawImage(objsprites.get(i),objx.get(i),objy.get(i),this);
		}
		g.setColor(Color.black);
		
		//drawing strings
		Font font = new Font("Calisto MT", Font.PLAIN, 20);
		g.setFont(font);

		
		g.drawString("Time: "+totals, 680, 55);
		g.drawString("Goal: "+goals.get(level-1),20,90);
		g.drawString("Level: "+level,680,90);
		g.drawString("Money: "+totalval, 20, 55);

	}
    
}