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

class Objects {
	private static ArrayList<ArrayList<Integer>> integer_data= new ArrayList<ArrayList<Integer>>(); 
	private static ArrayList<Double> speed = new ArrayList<Double>();
	private static ArrayList<Image> sprites = new ArrayList<Image>();
	private static ArrayList<ArrayList<Integer>> TNT_pos = new ArrayList<ArrayList<Integer>>();
	private static int goals;
	public static int numobj=6; //CHANGE TO 0
	public static int numTNT;
	public static int numlevels=2;
	public static final int GOLD = 1, ROCK = 2;
	private static int times;
	public static final int PIG=3, LEFT = 0, RIGHT=1,NODIRECTION=2;
	public static boolean [] pigsrunning = new boolean[2];
	private final static int NOobj=-1;
	private static Image pigleft,pigright;
	public final static int X=0,Y=1,VAL=2,DIRECTION=3,TYPE=4;
	public Objects(){
		pigleft = new ImageIcon("pig.png").getImage();
		pigright= new ImageIcon("pig2.png").getImage();
		pigsrunning[0]=false;
		pigsrunning[1]=true;
		loadMyStuff(GamePanel.returnLevel());
	}
	public static void loadMyStuff(int level){
		Scanner infile = null;
		try{
			infile = new Scanner(new File("Data"+level+".txt"));
		}
		catch(IOException ex){
			System.out.println("System cannot find Data"+level+".txt");
		}
		numobj = Integer.parseInt(infile.nextLine());
		for (int i=0;i<numobj;i++){
			integer_data.add(new ArrayList<Integer>());
			integer_data.get(i).add(infile.nextInt());
		}
		for (int i=0;i<numobj;i++){
			integer_data.get(i).add(infile.nextInt());
		}
		for (int i=0;i<numobj;i++){
			integer_data.get(i).add(infile.nextInt());
		}
		for (int i=0;i<numobj;i++){
			speed.add(infile.nextDouble());
		}
		for (int i=0;i<numobj;i++){
			sprites.add(new ImageIcon(infile.next()).getImage());
		}
		for (int i=0;i<numobj;i++){
			integer_data.get(i).add(infile.nextInt());
		}
		for (int i=0;i<numobj;i++){
			integer_data.get(i).add(infile.nextInt());
		}
		numTNT = infile.nextInt();
		for (int i=0;i<numTNT;i++){
			TNT_pos.add(new ArrayList<Integer>());
			TNT_pos.get(i).add(infile.nextInt());
		}
		for (int i=0;i<numTNT;i++){
			TNT_pos.get(i).add(infile.nextInt());
		}
		times= infile.nextInt();
		goals=infile.nextInt(); //add the goal for this level;
	}
	public static int catchobj(){
		for (int i=0;i<numobj;i++){
			if (distance(GamePanel.endy,integer_data.get(i).get(Y)+sprites.get(i).getHeight(null)/2)<=sprites.get(i).getHeight(null)/2 && distance(GamePanel.endx,integer_data.get(i).get(X)+sprites.get(i).getWidth(null)/2)<=sprites.get(i).getWidth(null)/2){
				//GamePanel.endy=integer_data.get(i).get(Y);
				//GamePanel.endx=integer_data.get(i).get(X);
				//System.out.println(GamePanel.endx+" "+GamePanel.endy);
				///set the position to the clamp to where the clamp touched the sprite
				return i; 
			}	
		}
		return NOobj;
	}
	public static int returnTimes(){
		return times;
	}
	public static void clearEverything(){
		integer_data.clear();
		speed.clear();
		sprites.clear();
		TNT_pos.clear();
	}
	public static int returnNumObj(){
		return numobj;
	}
	public static int distance(int a,int b){
		return Math.abs(b-a);
	}
	public static double distance_2(int x1,int y1,int x2,int y2){
		return Math.sqrt((Math.pow(distance(x1,x2),2)+Math.pow(distance(y1,y2),2)));
	}
	public static boolean checkCollision(int cx,int cy,int x,int y,int width,int height){
		//if any one of the corners is within the range, return true since it will be affected by the blast
		return distance_2(cx,cy,x,y)<150 || distance_2(cx,cy,x+width,y)<150 || distance_2(cx,cy,x+width,y+height)<150 || distance_2(cx,cy,x,y+height)<150;
	}
	public static void checkTNT(int check_Val){
		//record the TNT that is also exploded by the current TNT so it can explode later (so they don't ALL explode at the same time because that's not cool)
		ArrayList<ArrayList<Integer>> to_Remove = new ArrayList<ArrayList<Integer>>();		//stores the integer_value data for identification later
		for (int i=0;i<numobj;i++){
			if (checkCollision(TNT_pos.get(check_Val).get(0)+GamePanel.TNT_Image.getWidth(null)/2,TNT_pos.get(check_Val).get(1)+GamePanel.TNT_Image.getHeight(null),integer_data.get(i).get(X),integer_data.get(i).get(Y),sprites.get(i).getWidth(null),sprites.get(i).getHeight(null))){
				to_Remove.add(integer_data.get(i));
			}
		}
		for (int i=0;i<to_Remove.size();i++){
			removeVal(integer_data.indexOf(to_Remove.get(i)));
		}
		for (int i=0;i<numTNT;i++){
			if (i!=check_Val & distance_2(TNT_pos.get(check_Val).get(0),TNT_pos.get(check_Val).get(1),TNT_pos.get(i).get(0),TNT_pos.get(i).get(1))<150){
				GamePanel.explode_nextRound.add(TNT_pos.get(i));
			}
		}
		numTNT-=1;
	}
	public static void removeVal(int objcaught){
		sprites.remove(objcaught);
		speed.remove(objcaught);
		integer_data.remove(objcaught);
		numobj-=1;
	}
	public static int returnIntData(int indexA,int indexB){
		return integer_data.get(indexA).get(indexB);
	}
	public static int returnGoals(){
		return goals;
	}
	public static double returnSpeed(int index){
		return speed.get(index);
	}
	public static Image returnSprites(int index){
		return sprites.get(index);
	}
	public static int returnTNT_pos(int indexA,int indexB){
		return TNT_pos.get(indexA).get(indexB);
	}
	public static void removeTNT_pos(ArrayList<Integer> object){
		TNT_pos.remove(object);
	}
	public static ArrayList<Integer> returnTNT_pos2(int index){
		return TNT_pos.get(index);
	}
	public static int returnTNT_posSize(){
		return TNT_pos.size();
	}
	public static void move(int objcaught, int endx,int endy){
		//this method moves the obj that is caught with the clamp
		
		integer_data.get(objcaught).set(X,endx-sprites.get(objcaught).getWidth(null)/2);
		integer_data.get(objcaught).set(Y,endy-sprites.get(objcaught).getHeight(null)/2);
	}
	
	public static void movePigs(int objcaught){
		for (int i=0;i<integer_data.size();i++){
			if (integer_data.get(i).get(TYPE)==PIG && i!=objcaught){
				if (integer_data.get(i).get(DIRECTION)==LEFT){
					integer_data.get(i).set(X,integer_data.get(i).get(X)-2);
					if (integer_data.get(i).get(X)<=0){
						sprites.set(i,pigright);
						integer_data.get(i).set(DIRECTION,RIGHT);
					}
				}
				else{
					integer_data.get(i).set(X,integer_data.get(i).get(X)+2);
					if(integer_data.get(i).get(X)>=800-sprites.get(i).getWidth(null)){
						sprites.set(i, pigleft);
						integer_data.get(i).set(DIRECTION,LEFT);
					}
				}
			}
		}
	}
}

