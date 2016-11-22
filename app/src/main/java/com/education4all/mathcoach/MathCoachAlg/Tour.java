package MathCoachAlg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import MathCoachAlg.Task;

public class Tour {
	public  ArrayList<Task> tourTasks;
    public     int totalTasks;
    public     int rightTasks;
    public     long tourTime;
    public    long tourDateTime;
    public    static String tourEndStr = "tourEnd"; 
    
    public Tour() {
	    tourTasks = new ArrayList<Task>(0);
    	totalTasks = 0;
    	rightTasks = 0;
    	tourDateTime = System.currentTimeMillis();
    	
    }

    public static String DepictTour(String line) {
        int found = line.indexOf(';');
        line = line.substring(found + 1);
        found = line.indexOf(';');
        long l_tourDateTime = Long.parseLong(line.substring(0, found));
        line = line.substring(found + 1);
        found = line.indexOf(';');
        int l_totalTasks = Integer.parseInt(line.substring(0, found)); // lineVect.size() - 2
        line = line.substring(found + 1);
        found = line.indexOf(';');
        int l_rightTasks = Integer.parseInt(line.substring(0, found)); // ����� ��������� � ����� (����� ��?)
        line = line.substring(found + 1);
        found = line.indexOf(';');
        long l_tourTime = Long.parseLong(line.substring(0, found));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l_tourDateTime);

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM',' HH:mm ");
		String str = sdf.format(calendar.getTime()) + ". ";
		str += ((l_tourTime / 60 == 0) ? "1" : Long.toString(l_tourTime / 60)) + " мин. " + "\n";
		str += "Решено заданий: " + Integer.toString(l_rightTasks) + " из " + Integer.toString(l_totalTasks);
		return str;
    }
    
	public Tour(ArrayList<String> lineVect) {
	    tourTasks = new ArrayList<Task>(0);
	    String line = lineVect.get(0);
	    Task myTask;
	    int found = line.indexOf(';');
	    line = line.substring(found + 1);
	    found = line.indexOf(';');
	    tourDateTime = Long.parseLong(line.substring(0, found));
	    line = line.substring(found + 1);
	    found = line.indexOf(';');
	    totalTasks = Integer.parseInt(line.substring(0, found)); // lineVect.size() - 2
	    line = line.substring(found + 1);
	    found = line.indexOf(';');
	    rightTasks = Integer.parseInt(line.substring(0, found)); // ����� ��������� � ����� (����� ��?)
	    line = line.substring(found + 1);
	    found = line.indexOf(';');
	    tourTime = Long.parseLong(line.substring(0, found));
	    for (int i = 1; i < lineVect.size() - 1; ++i) {
	        line = lineVect.get(i);
	        myTask = new Task(line);
	        tourTasks.add(myTask);
	    }
	}    
    	
	public ArrayList<String> serialize() {
	    String line;
	    ArrayList<String> result = new ArrayList<String>();
	    line = "tourStart;" + Long.toString(tourDateTime) + ';' + Integer.toString(totalTasks) + ';' +
	    		Integer.toString(rightTasks) + ';' + Long.toString(tourTime) + ';';
	    result.add(line);
	    for (int i = 0; i < tourTasks.size(); ++i) {
	        result.add(tourTasks.get(i).serialize());
	    }
	    line = tourEndStr;
	    result.add(line);
	    return result;
	}

	public void deSerialize(ArrayList<String> lineVect) {
	    tourTasks = new ArrayList<Task>(0);
	    String line = lineVect.get(0);
	    Task myTask;
	    int found = line.indexOf(';');
	    line = line.substring(found + 1);
	    found = line.indexOf(';');
	    tourDateTime = Long.parseLong(line.substring(0, found));
	    line = line.substring(found + 1);
	    found = line.indexOf(';');
	    totalTasks = Integer.parseInt(line.substring(0, found)); // lineVect.size() - 2
	    line = line.substring(found + 1);
	    found = line.indexOf(';');
	    rightTasks = Integer.parseInt(line.substring(0, found)); // ����� ��������� � ����� (����� ��?)
	    line = line.substring(found + 1);
	    found = line.indexOf(';');
	    tourTime = Long.parseLong(line.substring(0, found));
	    for (int i = 1; i < lineVect.size() - 1; ++i) {
	        line = lineVect.get(i);
	        myTask = new Task(line);
	        tourTasks.add(myTask);
	    }
	}

}
