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
	public static ArrayList<Integer> x_val = new ArrayList<Integer>();
	public static ArrayList<Integer> y_val = new ArrayList<Integer>();
	public static ArrayList<Integer> val = new ArrayList<Integer>();
	public static ArrayList<Double> speed = new ArrayList<Double>();
	public static ArrayList<Image> sprites = new ArrayList<Image>();
	public static ArrayList<Integer> direction = new ArrayList<Integer>();
	public static ArrayList<Integer> type = new ArrayList<Integer>();
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
			x_val.add(infile.nextInt());
		}
		for (int i=0;i<numobj;i++){
			y_val.add(infile.nextInt());
		}
		for (int i=0;i<numobj;i++){
			val.add(infile.nextInt());
		}
		for (int i=0;i<numobj;i++){
			speed.add(infile.nextDouble());
		}
		for (int i=0;i<numobj;i++){
			sprites.add(new ImageIcon(infile.next()).getImage());
		}
		for (int i=0;i<numobj;i++){
			direction.add(infile.nextInt());
		}
		for (int i=0;i<numobj;i++){
			type.add(infile.nextInt());
		}
		numTNT = Integer.parseInt(infile.nextLine());
	}
	public static int catchobj(int endx, int endy){
		for (int i=0;i<numobj;i++){
			if (distance(endy,y_val.get(i)+sprites.get(i).getHeight(null)/2)<=sprites.get(i).getHeight(null)/2 && distance(endx,x_val.get(i)+sprites.get(i).getWidth(null)/2)<=sprites.get(i).getWidth(null)/2){
				System.out.println(i+"OBJECTCAUGHT");
				return i; 
			}	
		}
		return NOobj;
	}
	public static int distance(int a,int b){
		return Math.abs(b-a);
	}
	public static void removeVal(int objcaught){
		x_val.remove(objcaught);
		y_val.remove(objcaught);
		val.remove(objcaught);
		sprites.remove(objcaught);
		type.remove(objcaught);
		numobj-=1;
	}
	public static void move(int objcaught, int endx,int endy){
		//this method moves the obj that is caught with the clamp
		//System.out.println("moveobj");
		x_val.set(objcaught,endx-sprites.get(objcaught).getWidth(null)/2);
		y_val.set(objcaught,endy-sprites.get(objcaught).getHeight(null)/2);
	}
	
	public static void movePigs(int objcaught){
		for (int i=0;i<type.size();i++){
			if (type.get(i)==PIG && i!=objcaught){
				if (direction.get(i)==LEFT){
					x_val.set(i,x_val.get(i)-2);
					if (x_val.get(i)<=0){
						sprites.set(i,pigright);
						direction.set(i,RIGHT);
					}
				}
				else{
					x_val.set(i,x_val.get(i)+2);
					if( x_val.get(i)>=800-sprites.get(i).getWidth(null)){
						sprites.set(i, pigleft);
						direction.set(i,LEFT);
					}
				}
			}
		}
	}
}

