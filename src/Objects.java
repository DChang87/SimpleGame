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
	public static ArrayList<ArrayList<Integer>> integer_data= new ArrayList<ArrayList<Integer>>(); 
	//public static ArrayList<Integer> integer_data.get(X) = new ArrayList<Integer>();
	//public static ArrayList<Integer> integer_data.get(Y) = new ArrayList<Integer>();
	//public static ArrayList<Integer> val = new ArrayList<Integer>();
	public static ArrayList<Double> speed = new ArrayList<Double>();
	public static ArrayList<Image> sprites = new ArrayList<Image>();
	//public static ArrayList<Integer> integer_data.get(DIRECTION) = new ArrayList<Integer>();
	//public static ArrayList<Integer> integer_data.get(TYPE) = new ArrayList<Integer>();
	public static ArrayList<ArrayList<Integer>> TNT_pos = new ArrayList<ArrayList<Integer>>();
	
	public static int numobj=6; //CHANGE TO 0
	public static int numTNT;
	public static int numlevels=2;
	public static int current_level;
	public static final int GOLD = 1, ROCK = 2;
	public static final int PIG=3, LEFT = 0, RIGHT=1,NODIRECTION=2;
	public static boolean [] pigsrunning = new boolean[2];
	private final static int NOobj=-1;
	public Objects(){
		current_level=1;
		pigleft = new ImageIcon("pig.png").getImage();
		pigright= new ImageIcon("pig2.png").getImage();
		pigsrunning[0]=false;
		pigsrunning[1]=true;
		loadMyStuff(current_level);
	}
	private static Image pigleft;
	public final static int X=0,Y=1,VAL=2,DIRECTION=3,TYPE=4;
	private static Image pigright;
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
		//System.out.println(integer_data.get(X).toString());
		///System.out.println(integer_data.get(Y).toString());
		//read in x,y position of TNT since all TNT are equal in power
	}
	public static int catchobj(int endx, int endy){
		for (int i=0;i<numobj;i++){
			if (distance(endy,integer_data.get(i).get(Y)+sprites.get(i).getHeight(null)/2)<=sprites.get(i).getHeight(null)/2 && distance(endx,integer_data.get(i).get(X)+sprites.get(i).getWidth(null)/2)<=sprites.get(i).getWidth(null)/2){
				//System.out.println(i+"OBJECTCAUGHT");
				return i; 
			}	
		}
		return NOobj;
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
			System.out.println(check_Val+"checkval");
			System.out.println(TNT_pos.get(check_Val).toString()+"TNTpos");
			if (checkCollision(TNT_pos.get(check_Val).get(0)+GamePanel.TNT_Image.getWidth(null)/2,TNT_pos.get(check_Val).get(1)+GamePanel.TNT_Image.getHeight(null),integer_data.get(i).get(X),integer_data.get(i).get(Y),sprites.get(i).getWidth(null),sprites.get(i).getHeight(null))){
				to_Remove.add(integer_data.get(i));
			}
		}
		for (int i=0;i<to_Remove.size();i++){
			removeVal(integer_data.indexOf(to_Remove.get(i)));
		}
		int x = TNT_pos.get(check_Val).get(0);
		int y = TNT_pos.get(check_Val).get(1);
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
	public static void move(int objcaught, int endx,int endy){
		//this method moves the obj that is caught with the clamp
		//System.out.println("moveobj");
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
					integer_data.get(i).set(X,integer_data.get(X).get(i)+2);
					if( integer_data.get(i).get(X)>=800-sprites.get(i).getWidth(null)){
						sprites.set(i, pigleft);
						integer_data.get(i).set(DIRECTION,LEFT);
					}
				}
			}
		}
	}
}

