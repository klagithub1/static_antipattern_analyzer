package main.java.orm.detector.antipattern;

import java.util.ArrayList;

import main.java.orm.detector.persistence.SQLDriver;

//Nested Transaction Anti-pattern Detector
public class NTAntipatternDetector 
{
	public static void run()
	{
		// Record the annotations information for each method - done in data extraction
		// find all the methods which are executed in a transaction - done in data extraction
		/* This is how a transaction declaration is made:
		* @Transactional
		* Use the AST data extractor to find all instances of @Transactional
		* Then use the AST data extractor to find the properties of those
		* If the property is REQUIRES_NEW check all source and target nodes
		* If the source or a target node has @Transactional(REQUIRES_NEW)
		* then it is an anti-pattern
		*/

		ArrayList<String> source_targetEntry= new ArrayList<String>();
		
		// If there are instance
		if(!source_targetEntry.isEmpty())
		{
			for(int i = 0; i < source_targetEntry.size(); i ++)
			{
				String[] piece = (source_targetEntry.get(i)).split("_");
				
				String source = piece[0];
				String target = piece[1];
				
				if(SQLDriver.checkMethodTaintedStatus(source) && SQLDriver.checkMethodTaintedStatus(target) )
				{
					System.out.println("("+(i+1)+")"+"METHOD: "+target+" is a nested transaction antipattern! with source: "+source);
				}
			}
		}
	}
}
