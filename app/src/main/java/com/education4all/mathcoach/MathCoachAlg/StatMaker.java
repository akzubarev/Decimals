package MathCoachAlg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class StatMaker {
	public ArrayList<MathCoachAlg.Tour> tours;
	public String statsPath = "./stats.stf"; //
	
	public StatMaker() {
		tours = new ArrayList<MathCoachAlg.Tour>();
		
	}
	
	
	public void saveStats(MathCoachAlg.Tour myTour) throws IOException {
		File statsFile = new File(statsPath);
		if (!statsFile.exists()) {
			statsFile.createNewFile();
		}
		FileWriter fw = new FileWriter(statsFile, true);		
 		PrintWriter pw = new PrintWriter(fw, true);
 		ArrayList<String> lines = myTour.serialize();
 		for (int i = 0; i < lines.size(); ++i) {
 			pw.println(lines.get(i));
 		}
 		pw.close();
	}
	
	public void loadStats() throws IOException {
		tours.clear();
		File statsFile = new File(statsPath);
		if (statsFile.exists()) {
			BufferedReader fileReader = new BufferedReader(new FileReader(statsFile));
			Tour myTour = new Tour();
			ArrayList<String> lines = new ArrayList<String>();
			for (String line = fileReader.readLine(); line != null; line = fileReader.readLine()) {
				lines.add(line);
				if (line.indexOf(Tour.tourEndStr) != -1) {
					myTour = new Tour(lines);
					tours.add(myTour);
					lines.clear();
				}
			}
			fileReader.close();
		}
	}

}
