import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Miner extends JFrame implements ActionListener {
	Timer myTimer;
	GamePanel game;
	Timer gameTimer;
	StartScreen ss;
	Shop shop;
	public static final int GAME=1,MENU=0,SHOP=2;
	public static int state = MENU;
	public Miner(){
		super("Gold Miner");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,640);
		setLayout(new BorderLayout());
		ss = new StartScreen(this);
		game = new GamePanel(this);
		ss.setLocation(0,0);
		ss.setSize(800,640);
		add(ss);
		game.setLocation(0,0);
		game.setSize(800,640);
		shop = new Shop(this);
		shop.setLocation(0,0);
		shop.setSize(800,640);
		add(shop);
		add(game);
		gameTimer = new Timer(1000,this);
		myTimer = new Timer(20,this);
		setResizable(false);
		setVisible(true);
		requestFocus();
	}
	public void start(){
    	myTimer.start();
    	gameTimer.start();
    }
	public void actionPerformed(ActionEvent evt){
		/*if (ss.isFocusOwner()){
			System.out.println("fricking ss");
		}
		else if (game.isFocusOwner()){
			System.out.println("game is owner");
		}
		else if (shop.isFocusOwner()){
			System.out.println("shop owns it");
		}*/
		Object source = evt.getSource();
		if (state==MENU){
			ss.repaint();
		}
		else if (state==GAME){
			//System.out.println("gametime");
			if (source==myTimer){
				game.move();
				game.moveMan();
				game.throwDynamite();
			}
			if (source==gameTimer){
				game.changeTime();
			}
			game.repaint();
		}
		else if (state==SHOP){
			//shop.addMouseListener(shop);
			System.out.println("SHOP");
			shop.repaint();
		}
	}
	public static void main(String[] args){
		new Miner();
	}
}
