import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


class HighScores {
	public HighScores(){
		readFile();
	}
	private static int[] newTopScores = new int[10];
	private static String[] newTopNames = new String[10];
	
	private static boolean doneLoading=false;
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
			System.out.println(topScores[i]);
			topNames[i]=infile.next();
		}
	}
	public static boolean returnLoading(){
		return doneLoading;
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
		return newTopScores[index];
	}
	public static String returnName(int index){
		return newTopNames[index];
	}
	public static void gameFinished(int score,String name){
		openNewFile();
		int pos = placement(score);
		if (pos!=-1){
			for (int i=0;i<pos;i++){
				tryWriting(topScores[i]+" "+topNames[i]+"\n");
				newTopScores[i]=topScores[i];
				newTopNames[i]=topNames[i];
			}
			tryWriting(score+" "+name+"\n");
			newTopScores[pos]=score;
			newTopNames[pos]=name;
			for (int i=0;i<9-pos;i++){
				tryWriting(topScores[pos+i]+" "+topNames[pos+i]+"\n");
				System.out.println(pos+1+i+"POS");
				newTopScores[pos+1+i]=topScores[pos+i];
				newTopNames[pos+1+i]=topNames[pos+i];
			}
		}
		try {
			outfile.close();
		} 
		catch (IOException e) {
			System.out.println("Cannot close HighScore.txt");
		}
		doneLoading=true;
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
