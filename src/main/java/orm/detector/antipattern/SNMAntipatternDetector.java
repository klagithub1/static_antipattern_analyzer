package main.java.orm.detector.antipattern;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import main.java.orm.detector.analyzer.astvisitor.AnnotationVisitor;

//Sequence Name Mismatch Anti-pattern Detector
public class SNMAntipatternDetector
{	
	public static void run()
	{
		//Scan the annotations in the source code in order to extract the sequence name in the annotation
		ArrayList<String> sequence_names = AnnotationVisitor.getSeq();
		/* This is how a sequence name declaration is made:
		* @SequenceGenerator(sequenceName="")
		* Use the AST data extractor to save the name inside the quotation marks
		* Use SQLDriver to retrieve these
		 */
		//sequence_names = SQLDriver.getSequenceNames(); //Method to get sequence names
		
		//Get the sequence names from the database to compare to
		//location of the sequence names database file:
		String sqlPath = "C:/Users/freyjaj93/workspace/soen691/releases/BroadleafCommerce-broadleaf-1.5.3-GA/quick-start/ecommerce-archetype/src/main/resources/archetype-resources/site-war/src/main/resources/sql/load_table_sequences";
		boolean antipattern = true;
		String[] arr = null;
		
		//for each sequence name seq if there are any sequence names
		if(!sequence_names.isEmpty())
		{
			for(int i = 0; i < sequence_names.size(); i ++)
			{
				//The sequence name is an anti-pattern until it has been proved otherwise.
				antipattern = true;
				
				String seq = sequence_names.get(i);
				
				//Find the corresponding sequence name in load_table_sequences.sql 
				arr = readDatabase(sqlPath);
				
				//Compare to see if there is a mismatch between seq and sql
				//Make sure words were found in database
				if(arr == null)
					System.out.println("load_table_sequences.sql EMPTY!");
				
		        for(String sql : arr)
		        {
			        //If the sql sequence name matches the java sequence name, it is not an anti-pattern
			        if(seq == sql) { 
			            antipattern = false;
			        }
		        }
		        
				//Maybe display the line of code too
				if(antipattern)
					System.out.println("Sequence name: " +seq +" is an anti-pattern!");
			}
		}		
	}
	
	private static String[] readDatabase(String sqlPath){
		// String to split lines in database
		String[] arr = null;
		Scanner scan = null;
		
		try{
			//Open Database file
			scan = new Scanner(sqlPath);
			
			//Start on the first line of the database
		    while (scan.hasNextLine()) {
		        String line = scan.nextLine();
		        arr = line.split(" ");
		    } 
		}catch (Exception e){
				System.out.println("load_table_sequences.sql NOT FOUND!!");
		} finally{
            try{
				scan.close();
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }	
		
		return arr;
	}

}