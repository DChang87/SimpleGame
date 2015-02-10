import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Miner extends JFrame implements ActionListener {
	//classes
	Timer myTimer;
	GamePanel game;
	Timer gameTimer;
	HighScores hs;
	StartScreen ss;
	
	public static final int GAME=1,MENU=0; //reference values for deciding which panel to repaint, depending on which panel the user is on
	public static int state = MENU; //default state is at MENU
	public Miner(){
		super("Gold Miner");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,640);
		setLayout(new BorderLayout());
		//load the classes
		ss = new StartScreen(this);
		game = new GamePanel(this);
		hs = new HighScores();
		
		//set the locations and the sizes of the objects
		ss.setLocation(0,0);
		ss.setSize(800,640);
		add(ss);
		game.setLocation(0,0);
		game.setSize(800,640);
		add(game);
		
		//load the timers
		gameTimer = new Timer(1000,this); //gameTimer is used to record the amount of time left in the game since it updates esvery 1 second
		myTimer = new Timer(20,this); //myTimer is used to record the time for general movements in the game
		
		setResizable(false);
		setVisible(true);
	}
	public void start(){
		//start the timers
		myTimer.start();
    	gameTimer.start();
    }
	public void actionPerformed(ActionEvent evt){
		Object source = evt.getSource();
		if (state==MENU){
			//if the user is using the menu, repaint the menu
			ss.repaint();
		}
		else if (state==GAME){
			//if the user is playing the game, update the game and repaint it
			if (source==myTimer){
				game.move(); //move the objects
				game.moveMan(); //move the man
				game.throwDynamite(); //check the throwing of the dynamite
			}
			if (source==gameTimer){
				game.changeTime();
			}
			game.repaint();
		}
	}
	public static void main(String[] args){
		new Miner();
	}
}
