package main.java.orm.detector.antipattern;

import java.util.ArrayList;

import main.java.orm.detector.persistence.SQLDriver;

public class AntipatternDetector 
{
	public static void run()
	{
		//if current  = previous skip
		
		
		ArrayList<String> source_targetEntry= new ArrayList<String>();
		
		source_targetEntry = SQLDriver.getLoopCallGraphEntries();
		
		if(!source_targetEntry.isEmpty())
		{
			for(int i = 0; i < source_targetEntry.size(); i ++)
			{
				String[] piece = (source_targetEntry.get(i)).split("_");
				
				String source = piece[0];
				String target = piece[1];
				
				if(SQLDriver.checkMethodTaintedStatus(source) || SQLDriver.checkMethodTaintedStatus(target) )
				{
					System.out.println("("+(i+1)+")"+"METHOD: "+target+" is antipattern! with source: "+source);
				}
			}
		}
		
	}
}
