package MathCoachAlg;

import java.util.ArrayList;
import java.util.Random;


public class Task {
	public String expression;
	public int operation;
	public int complexity;
	public String answer;
	public String userAnswer;
	public long taskTime;
	public long timeTaken;
	public Random rnd;
	public final char[] operations = {'+', '−', '∙', ':'};

	public Task() {
		expression = "2 + 2";
		answer = "4";
		userAnswer = new String();
		rnd = new Random();
	}

//	    public Task(final int allowedTasks[][]) {
//	        operation = operationRandomizer(allowedTasks);
//	        switch (operation) {
//	            case 0:
//	                addition(allowedTasks);
//	            break;
//	            case 1:
//	                substraction(allowedTasks);
//	            break;
//	            case 2:
//	                multiplication(allowedTasks);
//	            break;
//	            case 3:
//	                division(allowedTasks);
//	            break;
//	        }
//	        taskTime = Calendar.getInstance().getTimeInMillis();
//		}

	public static String DepictTask(String line) {
		Task l_task = new Task(line);
//		return l_task.expression + " = " + l_task.userAnswer + "(" + l_task.answer + ") " + l_task.timeTaken + " сек. ";
		return l_task.expression + " = " + l_task.userAnswer + " " + l_task.timeTaken + " сек. ";
	}

	public static ArrayList<String> DepictTaskExtended(String line, ArrayList<String> answers ) {
		Task l_task = new Task(line);
		ArrayList<String> res = new ArrayList<String>();
		String firstPart = l_task.expression + " = ";
		String partToBeParsed =  l_task.userAnswer;
		int commaIndex;
		int colonIndex;
		String oneAnswer;
		String oneAnswerTime;
		while (partToBeParsed.length() > 0) {
			commaIndex = partToBeParsed.indexOf(',');
			colonIndex = partToBeParsed.indexOf(':');
			if (colonIndex > 0) {
				oneAnswer = partToBeParsed.substring(0, colonIndex);
				oneAnswerTime = partToBeParsed.substring(colonIndex + 1, commaIndex);
				res.add(firstPart + oneAnswer + "  (" + oneAnswerTime + " сек)");
				answers.add(oneAnswer);
			}
			partToBeParsed = partToBeParsed.substring(commaIndex + 1, partToBeParsed.length());
		}
		return res;
	}

//	public static ArrayList<String> DepictTaskExpanded(String line) {
//		Task l_task = new Task(line);
//		ArrayList<String> result = new ArrayList<String>();
//		String answers = l_task.userAnswer;
//		int coma = 0;
//		coma = answers.indexOf(',');
//		while (coma > 0) {
//			result.add(l_task.userAnswer.substring(coma - 1, coma));
//			answers = answers.substring(coma + 1);
//			coma = answers.indexOf(',');
//		}
//		return result;
//	}



	    public Task(String line) {
	        int found = line.indexOf(';');
	        expression = line.substring(0, found);
	        line = line.substring(found + 1);
	        found = line.indexOf(';');
	        operation = Integer.parseInt(line.substring(0, found));
	        line = line.substring(found + 1);
	        found = line.indexOf(';');
	        complexity = Integer.parseInt(line.substring(0, found));
	        line = line.substring(found + 1);
	        found = line.indexOf(';');
	        answer = line.substring(0, found);
	        line = line.substring(found + 1);
	        found = line.indexOf(';');
	        userAnswer = line.substring(0, found);
	        line = line.substring(found + 1);
	        found = line.indexOf(';');
	        taskTime = Long.parseLong(line.substring(0, found));
	        line = line.substring(found + 1);
	        found = line.indexOf(';');
	        timeTaken = Long.parseLong(line.substring(0, found));	    	    	
	    }
	    
	    public void generate(final int[][] allowedTasks) {
            if (areTasks(allowedTasks)) {
                operation = operationRandomizer(allowedTasks);
                switch (operation) {
                    case 0:
                        addition(allowedTasks);
                        break;
                    case 1:
                        substraction(allowedTasks);
                        break;
                    case 2:
                        multiplication(allowedTasks);
                        break;
                    case 3:
                        division(allowedTasks);
                        break;
                }
                taskTime = System.currentTimeMillis();
            }
		}

        public boolean areTasks(int[][] p_allowedTasks) {
            for (int i = 0; i < p_allowedTasks.length; ++i) {
                if (p_allowedTasks[i].length > 0) {
                    return true;
                }
            }
            return false;
        }


	    public String serialize() {
	        return expression + ";" + Integer.toString(operation) + ";" + Integer.toString(complexity) + ";" +
	                answer + ";" + userAnswer + ";" + Long.toString(taskTime) + ";" +
	                Long.toString(timeTaken) + ";";	    	
		}
	    
	    public void deSerialize(String line) {
	        int found = line.indexOf(';');
	        expression = line.substring(0, found);
	        line = line.substring(found + 1);
	        found = line.indexOf(';');
	        operation = Integer.parseInt(line.substring(0, found));
	        line = line.substring(found + 1);
	        found = line.indexOf(';');
	        complexity = Integer.parseInt(line.substring(0, found));
	        line = line.substring(found + 1);
	        found = line.indexOf(';');
	        answer = line.substring(0, found);
	        line = line.substring(found + 1);
	        found = line.indexOf(';');
	        userAnswer = line.substring(0, found);
	        line = line.substring(found + 1);
	        found = line.indexOf(';');
	        taskTime = Integer.parseInt(line.substring(0, found));
	        line = line.substring(found + 1);
	        found = line.indexOf(';');
	        timeTaken = Integer.parseInt(line.substring(0, found));
	    }
	    
	    public char getOperation(int n) {
	    	return operations[n];	    	
	    }
	    
    	// ����� ������ ��������
	    private int operationRandomizer(final int[][] allowedTasks) {
	        int c = 0;
	        int i;
	        while (true) {
	            ++c;
	            i = rnd.nextInt(allowedTasks.length);
	            if (allowedTasks[i].length > 0) {
	                return i;
	            }
	        }
	    }
		// ����� ��������� �������
	    private int complexityRandomizer(final int[][] allowedTasks) {
	    	if (allowedTasks[operation].length > 0) {
	    		int r = rnd.nextInt(allowedTasks[operation].length);
	    		return allowedTasks[operation][r];
	    	} else {
	    		return -1;
	    	}
	    }
    	// +
	    private void addition(final int[][] allowedTasks) {
	        int n = 0;
	        int m = 0;
	        complexity = complexityRandomizer(allowedTasks);;
	        switch (complexity) {
	            case 0:
	                n = rnd.nextInt(9 + 1);
	                m = rnd.nextInt(9 + 1);
	            break;
	            case 1:
	                n = rnd.nextInt(99 - 10 + 1) + 10;
	                //m = rnd.nextInt(99 - 10 + 1) + 10;
					m = rnd.nextInt(99 + 1) ;
	            break;
	            case 2:
	                n = rnd.nextInt(999 - 100 + 1) + 100;	                
	                //m = rnd.nextInt(999 - 100 + 1) + 100;
					m = rnd.nextInt(999 + 1) ;
	            break;
	            case 3:
	                n = rnd.nextInt(9999 - 1000 + 1) + 1000;
	                //m = rnd.nextInt(9999 - 1000 + 1) + 1000;
					m = rnd.nextInt(9999  + 1);
	            break;
	        default:
	            ////DatasaveErrorLog("No valid complexity for addition, yet called");
	        break;
	        }
	        expression = Integer.toString(n) + operations[0] + Integer.toString(m);
	        answer = Integer.toString(n + m);
	    }
	    // :
	    private void division(final int[][] allowedTasks) {
	        int m = 0;
	        int n = 0;
	        int k = 0;
	        int l = 0;
	        complexity = complexityRandomizer(allowedTasks);;
	        switch (complexity) {
	            case 0:
	                n = rnd.nextInt(9 - 1 + 1) + 1;
	                m = rnd.nextInt(9 + 1);
	                k = n;
	                l = m;
	            break;
	            case 1:
	                n = rnd.nextInt(10 - 1 + 1) + 1;
	                m = rnd.nextInt(99 - 10 + 1) + 10;
//	                if (rnd.nextInt(2) == 1) {
//	                    int p = n;
//	                    n = m;
//	                    m = p;
//	                }
	                k = n;
	                l = m;
	            break;
				case 4:
					//На самом деле расширение второго уровня
					n = rnd.nextInt(10 - 1 + 1) + 1;
					m = rnd.nextInt(99 - 10 + 1) + 10;
					k = m;
					l = n;
				break;
	            case 2:
	                n = rnd.nextInt(10 - 1 + 1) + 1;
	                m = rnd.nextInt(999 - 100 + 1) + 100;
	                k = n;
	                l = m;
	            break;
	            case 3:
	                n = rnd.nextInt(99 - 10 + 1) + 10;
	                m = rnd.nextInt(99 - 10 + 1) + 10;
	                k = n;
	                l = m;
	            break;

	        default:
	            //DatasaveErrorLog("No valid complexity for division, yet called");
	            k = 1;
	            l = 1;
	        break;
	        }
	        expression = Integer.toString(k*l) + operations[3] + Integer.toString(k);
	        answer = Integer.toString(l);	    	
	    }
    	// -
	    private void substraction(final int[][] allowedTasks) {
	        int m = 0;
	        int n = 0;
	        complexity = complexityRandomizer(allowedTasks);;
	        switch (complexity) {
		        case 0:
		            n = rnd.nextInt(10);
		            m = rnd.nextInt(19 - n) + (n);
		        break;
		        case 1:
		            //n = rnd.nextInt(80) + 9;
					n = rnd.nextInt(89);
		            m = rnd.nextInt(89 - n) + n;
		        break;
		        case 2:
		            //n = rnd.nextInt(800) + 99;
					n = rnd.nextInt(899);
		            m = rnd.nextInt(899 - n) + n;
		        break;
		        case 3:
		            //n = rnd.nextInt(8000) + 999;
					n = rnd.nextInt(8999);
		            m = rnd.nextInt(8999 - n) + n;
		        break;
		    default:
	            //DatasaveErrorLog("No valid complexity for substraction, yet called");
	        break;
	        }
	        expression = Integer.toString(m) + operations[1] + Integer.toString(n);
	        answer = Integer.toString(m - n);
	    }
    	// *	    
	    private void multiplication(final int[][] allowedTasks) {
	        int n = 0;
	        int m = 0;
	        complexity = complexityRandomizer(allowedTasks);;
	        switch (complexity) {
	            case 0:
	                n = rnd.nextInt(8) + 2;
	                m = rnd.nextInt(8) + 2;
	            break;
	            case 1:
	                n = rnd.nextInt(8) + 2;
	                m = rnd.nextInt(90) + 10;
	                if (rnd.nextInt(2) == 1) {
	                    int k = n;
	                    n = m;
	                    m = k;
	                }
	            break;
	            case 2:
	                n = rnd.nextInt(8) + 2;
	                m = rnd.nextInt(900) + 100;
	                if (rnd.nextInt(2) == 1) {
	                    int k = n;
	                    n = m;
	                    m = k;
	                }
	            break;
	            case 3:
	                n = rnd.nextInt(90) + 10;
	                m = rnd.nextInt(90) + 10;
	            break;
	        default:
	            //DatasaveErrorLog("No valid complexity for multiplication, yet called");
	        break;
	        }
	        expression = Integer.toString(n) + operations[2] + Integer.toString(m);
	        answer = Integer.toString(n * m);	
	    }

}
