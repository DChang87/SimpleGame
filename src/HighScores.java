import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


class HighScores {
	public HighScores(){
		readFile();
		openNewFile();
	}
	private static int [] topScores = new int[10];
	private static String[] topNames = new String[10];
	static FileWriter outfile;
	public static void readFile(){
		Scanner infile = null;
		try{
			infile = new Scanner(new File("HighScore.txt"));
		}
		catch(IOException ex){
			System.out.println("System cannot find HighScore.txt");
		}
		for (int i=0;i<10;i++){
			topScores[i]=infile.nextInt();
			topNames[i]=infile.next();
		}
	}
	public static void openNewFile(){
		try{
			outfile = new FileWriter(new File("HighScore.txt"));
		}
		catch (IOException ex){
			System.out.println("System cannot create HighScore.txt");
		}
		
	}
	public static int placement(int score){
		int pos=-1;
		//returns the index: 0-9
		for (int i=0;i<10;i++){
			if (topScores[i]<=score){
				return i;
			}
		}
		return pos;
	}
	public static int returnScore(int index){
		return (topScores[index]);
	}
	public static String returnName(int index){
		return topNames[index];
	}
	public static void gameFinished(int score,String name){
		int pos = placement(score);
		if (pos!=-1){
			for (int i=0;i<pos;i++){
				tryWriting(topScores[i]+" "+topNames[i]+"\n");
			}
			tryWriting(score+" "+name+"\n");
			for (int i=pos+1;i<10;i++){
				tryWriting(topScores[i]+" "+topNames[i]+"\n");
			}
		}
		try {
			outfile.close();
		} 
		catch (IOException e) {
			System.out.println("Cannot close HighScore.txt");
		}
		readFile();
	}
	public static void tryWriting(String s){
		try{
			outfile.write(s);
		}
		catch(IOException ex){
			System.out.println("System could not successfully load the outfile");
		}
	}
}
