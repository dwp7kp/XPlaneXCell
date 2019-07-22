
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Simdata_Format {
	
	static Scanner sc;
	static FileWriter fw;
	
	static final String XPLANE_DATA_LOC = "C:\\Users\\dwprohas\\eclipse-workspace\\LBFD\\data\\";
	static final String XPLANE_DATA_NAME = "Data(3).txt";
	
	static final String SIM_DATA_LOC = "C:\\Users\\dwprohas\\Documents\\tmp\\InternDataXLS\\";
	static final String SIM_DATA_NAME = "SlowPitchDoublet.csv";
	
	static final String OUTPUT_LOC = "C:\\Users\\dwprohas\\eclipse-workspace\\LBFD\\data\\";
	
	static final int NUM_LETTERS = 1184;
	static final int NUM_COL = 74;

	// Run Commands:
	// C:\Program Files\Java\jdk-12.0.1\bin"\javac Simdata_Format.java
	// C:\Program Files\Java\jdk-12.0.1\bin"\java Simdata_Format
	
	// TODO: 
	
	public static void main(String [] args) {

		String [][] xplaneData = parseData(XPLANE_DATA_LOC + XPLANE_DATA_NAME, NUM_LETTERS, NUM_COL, "|");
		String[][] simData = parseCSV(SIM_DATA_LOC + SIM_DATA_NAME, 33, 33, ",");
		
		String fileName = "testOutput.csv";
		int[] order = {-1, 1001, 1002, 10, -1, 5, 0, -2};
		
		try { writeCSV(OUTPUT_LOC, fileName, xplaneData, simData, order); } 
			catch (IOException e) { e.printStackTrace(); }
		
		printIndexFile(xplaneData, simData);
		//resetFile(XPLANE_DATA_LOC, "Data.txt");
		
		
	}
	
	public static String[][] parseData(String filePath, int num_letters, int num_col, String delim) {
		System.out.print("Parsing Data: " + filePath + " with " + num_letters + " letters and " + num_col + " columns...");
		
		File f = new File(filePath);
		String [][] data = null;
		Integer dataLines = 0;
		
		try {
			// Find headers
			sc = new Scanner(f);

			String [] temp = new String [0];
			String s = "";
			int trash = 0;
			int length = 0;
			
			for (int i = 0; sc.hasNext(); i++) {
				if (temp.length != num_letters && sc.hasNextLine()) {
					trash = i;
					System.out.println("FOUND!  " + i);
				}
				s = sc.nextLine();
				temp = s.split(delim);
				length++;
			}
			//System.out.println(s);
			
			
			// Calculate data lines
			while (sc.hasNextLine()) {
				sc.nextLine();
				dataLines++;
			}
			
			// Accounts for the later addition of column headers
			dataLines++;
			trash--;
			
			
			// Create data array and go to data in new file
			data = new String [length-trash][num_col];
			dataLines = length-trash;
			sc = new Scanner(f);
			for (int i = 0; i < trash; i++)
				sc.nextLine();
			
			// Parse data
			for (int i = 0; i < dataLines; i++) {
				for (int j = 0; j < num_col; j++) {
					if (sc.hasNext()) {
						data[i][j] = sc.next();
						try {
							if (data[i][j].contains(",")) 
								data[i][j] = removeChar(data[i][j],",");
						} catch (Exception e) {}
					}
					if (sc.hasNext())
						sc.next();
				}
			}
			
			System.out.println(" Done!");
		}
		catch(Exception e) { e.printStackTrace(); }
		finally { sc.close(); }
		
		return data;
	}
	public static String[][] parseCSV(String filePath, int num_letters, int num_col, String delim) {
		System.out.print("Parsing CSV: " + filePath + " with " + num_letters + " letters and " + num_col + " columns...");
		
		File f = new File(filePath);
		String [][] data = null;
		Integer dataLines = 0;
		
		try {
			// Find headers
			sc = new Scanner(f);
			int trash = 0;
			String [] temp = new String [0];
			String s;
			
			
			
			while (temp.length != num_letters && sc.hasNextLine()) {
				s = sc.nextLine();
				temp = s.split(delim);
				trash++;
			}
			
			// Calculate data lines
			while (sc.hasNextLine()) {
				sc.nextLine();
				dataLines++;
			}
			
			// Accounts for the later addition of column headers
			dataLines++;
			trash--;
			
			// Create data array and go to data in new file
			data = new String [dataLines][num_col];
			sc = new Scanner(f);
			for (int i = 0; i < trash; i++)
				sc.nextLine();
			
			// Parse data
			for (int i = 0; i < dataLines; i++) {
				data[i] = sc.nextLine().split(delim);
				//System.out.println(Arrays.asList(data[i]).toString());
			}
			
			System.out.println(" Done!");
		}
		catch(Exception e) { e.printStackTrace(); }
		finally { sc.close(); }
		
		return data;
	}
	
	public static void writeCSV(String filePath, String fileName, String[][] xplaneData, String[][] simData, int[] order) throws IOException {
			
		System.out.print("Printing to " + filePath + fileName + "... \n 	Using column order [");
		for (int i : order)
			System.out.print(i + ", ");
		System.out.print("] = [");
		for (int i : order)
			if (i == -1) {
				System.out.print("(empty), ");
			} else if (i == -2) {
				System.out.print("(calclulated_time),");
			} else if (i > 1000) {
				try { System.out.print(simData[0][i%1000] + ", "); }
				catch (Exception e) {}
			} else {
				System.out.print(xplaneData[0][i] + ", ");
			}
		System.out.print("]");
		fw = new FileWriter(filePath + fileName);
		
		int max = Math.max(xplaneData.length, simData.length);
		
		for (int i = 0; i < max; i++) {
			String toWrite = "";
			for (int j : order) {
				if (j == -1) {
					// j=-1 is blank
					toWrite += ",";
				}
				else if (j == -2) {
					if (i == 0)
						toWrite += "Calculated_Time, ";
					else if (i == 1)
						toWrite += "0,";
					else if (i >= 2)
						try {
							toWrite +=  Double.parseDouble(xplaneData[i][0])-Double.parseDouble(xplaneData[1][0]) + ",";
						}
						catch (Exception e ) {}
				}
				else if (j > 1000) {
					// j>1000 is simData
					try {
						toWrite += simData[i][j%1000] + ",";
					} catch (Exception e){
						toWrite += "null,";
						//System.out.println("oops! " + e);
					}
				} else {
					// j<1000 is xplaneData
					try {
						toWrite += xplaneData[i][j] + ",";
					} catch (Exception e) {
						toWrite += "null,";
						//e.printStackTrace();
						//System.out.println("oops! " + e);
					}
				}
			}
			toWrite += "\n";
			//System.out.print(toWrite);
			fw.write(toWrite);
		}
		System.out.println(" Done!");
		
	}
	
	public static String removeChar(String str, String s) {
		return charRemoveAt(str,str.indexOf(s));
	}
    public static String charRemoveAt(String str, int p) {  
        return str.substring(0, p) + str.substring(p + 1);  
     }
    public static void resetFile(String filePath, String fileName) {
    	System.out.print("Trying to delete file " + fileName + " at location " + filePath + "... ");
    	File f = new File(filePath + fileName);
    	Boolean b = f.delete();
    	System.out.println(b);
    	
    	if (b) {
    		System.out.print("Refreshing file... ");
    		
    		try {
				fw = new FileWriter(filePath + fileName);
				fw.write("");
			} catch (IOException e) { e.printStackTrace(); }
    		
    		System.out.println("Done!");
    	}
    	
    	
    }
    public static void printIndexFile(String[][] xplaneData, String[][] simData) {
    	System.out.print("Printing Index File to " + OUTPUT_LOC + "... ");
    	File f = new File(OUTPUT_LOC + "CellReference.txt");
    	try {
			FileWriter fw = new FileWriter(f);
			
			fw.write("Special Characters \n");
			fw.write("------------------\n");
			fw.write("-1 Empty Col | -2 Calc Time \n");
			fw.write("\n");
			
	    	fw.write("X-Plane Data | Sim Data \n");
	    	fw.write("-----------------------\n");
			String toWrite;
			for (int i = 0; i < Math.max(xplaneData[0].length, simData[0].length); i++) {
				toWrite = "";
				toWrite += i + " ";
				try { toWrite += xplaneData[0][i]; }
				catch (Exception e) { toWrite += "null"; }
				toWrite += " | " + (1000+i) + " ";
				try { toWrite += simData[0][i]; }
				catch (Exception e) { toWrite += "null"; }
				toWrite += "\n";
				//System.out.println(toWrite);
	    		fw.write(toWrite);
	    	}
			fw.close();
		} catch (IOException e) { e.printStackTrace(); }
    	
    	System.out.println("Done!");
    }
}

