import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

class HighScores {
	//this class takes care of loading the highscores and updating them
	public HighScores(){
		readFile(); //read the old highscores file
	}
	private static int[] newTopScores = new int[10]; //the soon-to-be updated version of the top scores
	private static String[] newTopNames = new String[10]; //soon to be updated version of the top names (corresponding to the scores)
	private static boolean doneLoading=false; //if it is done loading everything
	private static int [] topScores = new int[10]; //the current top scores
	private static String[] topNames = new String[10]; //current top names
	static FileWriter outfile; //the file that will be written on
	public static void readFile(){
		Scanner infile = null;
		try{
			infile = new Scanner(new File("HighScore.txt"));
		}
		catch(IOException ex){
			System.out.println("System cannot find HighScore.txt");
		}
		for (int i=0;i<10;i++){
			//read the data and store them
			topScores[i]=infile.nextInt();
			topNames[i]=infile.next();
		}
	}
	public static void openNewFile(){
		//this method opens a new file to write on
		try{
			outfile = new FileWriter(new File("HighScore.txt"));
		}
		catch (IOException ex){
			System.out.println("System cannot create HighScore.txt");
		}
		
	}
	public static int placement(int score){
		int pos=-1;
		//returns the index: 0-9 of where the user places on the score board compared to the top 10 scores
		for (int i=0;i<10;i++){
			if (topScores[i]<=score){
				return i;
			}
		}
		return pos;
	}
	
	//return things
	public static int returnScore(int index){
		return newTopScores[index];
	}
	public static String returnName(int index){
		return newTopNames[index];
	}
	public static boolean returnLoading(){
		return doneLoading;
	}

	public static void gameFinished(int score,String name){
		//this method is called when the game is finished
		//it opens the new file to write on. stores the new values to the newTopScores/newTopNames arraylists to print on screen
		//writes the values on the new file to be accessed later
		openNewFile();
		int pos = placement(score);
		if (pos!=-1){
			//keep all of the scores before the user's score
			//add on ther user's score
			//take all of the scores after the user (Except the last person since the last person would be the 11th, not top 10)
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
		//this method was created so there was no need to code-out the try and catch everytime something was added to the outfile
		//this method tries to write the string on the outfile
		try{
			outfile.write(s);
		}
		catch(IOException ex){
			System.out.println("System could not successfully load the outfile");
		}
	}
}
