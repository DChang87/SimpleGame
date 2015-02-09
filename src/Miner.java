import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Miner extends JFrame implements ActionListener {
	Timer myTimer;
	GamePanel game;
	Timer gameTimer;
	public Miner(){
		super("Gold Miner");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,640);
		setLayout(new BorderLayout());
		game = new GamePanel(this);
		game.setLocation(0,0);
		game.setSize(800,640);
		add(game);
		gameTimer = new Timer(1000,this);
		myTimer = new Timer(20,this);
		setResizable(false);
		setVisible(true);
	}
	public void start(){
    	myTimer.start();
    	gameTimer.start();
    }
	public void actionPerformed(ActionEvent evt){
		Object source = evt.getSource();
		
		if (source==myTimer){
			game.move();
			game.throwDynamite();
		}
		if (source==gameTimer){
			game.changeTime();
		}
		game.repaint();
	}
	public static void main(String[] args){
		new Miner();
	}
}
