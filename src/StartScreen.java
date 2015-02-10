import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
public class StartScreen extends JPanel implements MouseMotionListener, MouseListener{
	private static int mouseX,mouseY;
	private Image[] startImages = new Image[3];
	private Image[] helpImages = new Image[3];
	private Image background = new ImageIcon("StartScreen.jpg").getImage();
	private boolean down=false;
	Miner mainFrame;
	public StartScreen(Miner m){
		System.out.println("START");
		for (int i=1;i<=3;i++){
			startImages[i-1]=new ImageIcon("start"+i+".png").getImage();
		}
		mainFrame=m;
		addMouseMotionListener(this);
		addMouseListener(this);
		setSize(800,640);
	}
    public void addNotify() {
    	System.out.println("addnotify");
    	super.addNotify();
    	requestFocus();
    }
    
    
    // ------------ MouseListener ------------------------------------------
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {
    	down=false;
    }    
    public void mouseClicked(MouseEvent e){
    }  
    	 
    public void mousePressed(MouseEvent e){
		down=true;
	}
    	
    // ---------- MouseMotionListener ------------------------------------------
    public void mouseDragged(MouseEvent e){}
    public void mouseMoved(MouseEvent e){
    	mouseX=e.getX();
    	mouseY = e.getY();
    }
    
    public static boolean collide(int x,int y,int width, int height){
    	return x<=mouseX && mouseX<=x+width && y<=mouseY && mouseY<=y+height;
    }
    public static int startX=75,startY=170;
    public void paintComponent(Graphics g){
    	g.drawImage(background,0,0,this);
    	if (down && collide(startX,startY,startImages[0].getWidth(null),startImages[0].getHeight(null))){
    		g.drawImage(startImages[2],startX,startY,this);
    		mainFrame.state=mainFrame.GAME;
    		setFocusable(false);
    		mainFrame.game.requestFocus();
    	}
    	else if (collide(startX,startY,startImages[0].getWidth(null),startImages[0].getHeight(null))){
    		g.drawImage(startImages[1],startX,startY,this);
    	}
    	else{
    		g.drawImage(startImages[0],startX,startY,this);
    	}
    }
	    
}
