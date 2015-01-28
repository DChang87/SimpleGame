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
	private final static int NOobj=-1;
	public static int numobj=4;
	public Objects(){
		addThings();
	}
	public void addThings(){
		sprites.add(new ImageIcon("gold2.png").getImage());
		x_val.add(100);
		y_val.add(200);
		val.add(50);
		speed.add(-1.0);
		sprites.add(new ImageIcon("gold1.png").getImage());
		x_val.add(300);
		y_val.add(400);
		speed.add(-0.25);
		val.add(200);
		sprites.add(new ImageIcon("rock1.png").getImage());
		x_val.add(200);
		y_val.add(300);
		speed.add(-0.25);
		val.add(10);
		sprites.add(new ImageIcon("rock2.png").getImage());
		x_val.add(600);
		y_val.add(300);
		speed.add(-0.5);
		val.add(5);
	}
	public static int catchobj(int endx, int endy){
		for (int i=0;i<numobj;i++){
			if (distance(endy,y_val.get(i)+sprites.get(i).getHeight(null)/2)<=sprites.get(i).getHeight(null)/2 && distance(endx,x_val.get(i)+sprites.get(i).getWidth(null)/2)<=sprites.get(i).getWidth(null)/2){
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
	}
	public static void move(int objcaught, int endx,int endy){
		//this method moves the obj that is caught with the clamp
		//System.out.println("moveobj");
		x_val.set(objcaught,endx-sprites.get(objcaught).getWidth(null)/2);
		y_val.set(objcaught,endy-sprites.get(objcaught).getHeight(null)/2);
	}
}

