package com.education4all.mathcoach.MathCoachAlg;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class DataStorage {
	private String settingsPath = "./settings.stf";
//	private String logPath;
//	private	String erLogPath;
	public int[][] allowedTasks;
	public double tourTime;
	public double disapTime;
	public boolean timervisible;
	
	public DataStorage() {
		allowedTasks = new int[][]{{0},{0},{0},{0}};
	}

	
	public void loadSettings() throws IOException {
		// ��������� ��������� �� �����
		File settingsFile = new File(settingsPath);
		if (settingsFile.exists()) {				
			BufferedReader fileReader = new BufferedReader(new FileReader(settingsFile));
			String line = fileReader.readLine();
			tourTime = Double.parseDouble(line);
			line = fileReader.readLine();
			disapTime = Double.parseDouble(line);
			line = fileReader.readLine();
			timervisible = Boolean.parseBoolean(line);
			ArrayList<int[]> buf = new ArrayList<int[]>();
			for (line = fileReader.readLine(); line != null; line = fileReader.readLine()) {
				buf.add(LineToArr(line));
			}
			allowedTasks = new int[buf.size()][4];
			buf.toArray(allowedTasks);
			buf.toArray(allowedTasks);
			fileReader.close();
		} else {
			//allowedTasks = new int[][]{{0},{0},{0},{0}};
			allowedTasks = new int[4][];
			allowedTasks[0] = new int[4];
			allowedTasks[0][1] =  0;
			allowedTasks[1] = new int[4];
			allowedTasks[1][1] =  0;

			disapTime = -1;
			tourTime = 1*60;
			timervisible=true;
			saveSettings();
		}
	}
	
	public void saveSettings() throws IOException {
		// ��������� ��������� � ����
		File settingsFile = new File(settingsPath);
		if (!settingsFile.exists()) {
			settingsFile.createNewFile();
		}
		FileWriter fw = new FileWriter(settingsFile);		
 		PrintWriter pw = new PrintWriter(fw, true);
 		String line = Double.toString(tourTime);
 		pw.println(line);
 		line = Double.toString(disapTime);
 		pw.println(line);
 		line = Boolean.toString(timervisible);
 		pw.println(line);
	    for (int i = 0; i < allowedTasks.length; ++i) {        
	    	line = "";
	    	for (int j = 0; j < allowedTasks[i].length; ++j) {
	    		line += Integer.toString(allowedTasks[i][j]) + ' ';
	    	}
	    	pw.println(line);
	    } 		
		pw.close();
	}
	
	public void saveErrorLog(String message) {
		// ����� �� ������
	}

	public static int[] LineToArr(String s) {		
		ArrayList<Integer> buf = new ArrayList<Integer>();
		int i = 0;
		int pos = s.indexOf(' ');
		int tmp;
		while (pos != -1) {
			tmp = Integer.parseInt(s.substring(i ,pos));
			buf.add(tmp);
			i = pos + 1;
			pos = s.indexOf(' ', i);		
		}
		
		return convertIntegers(buf);
	}
	
	public static int[] convertIntegers(ArrayList<Integer> integers)
	{
	    int[] ret = new int[integers.size()];
	    for (int i=0; i < ret.length; i++)
	    {
	        ret[i] = integers.get(i).intValue();
	    }
	    return ret;
	}
}
