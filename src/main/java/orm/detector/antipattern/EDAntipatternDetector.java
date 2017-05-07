package main.java.orm.detector.antipattern;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import main.java.orm.detector.logger.Logger;
import main.java.orm.detector.persistence.SQLDriver;
import main.java.orm.detector.persistence.vo.EagerlyMappedEntitiesTuple;

/**
 * Detection Approach:
 * Identify the variables that point to the database entity object by using point-to analysis.
 * Then the data flow, of the object to detect excessive data, is analyzed. If a database 
 * entity object does not use the information retrieved eagerly, it is reported as an instance 
 * of performance anti-pattern
 */
public class EDAntipatternDetector 
{
	private ArrayList<EagerlyMappedEntitiesTuple> relatedEntities = new ArrayList<EagerlyMappedEntitiesTuple>();
	private Set<String> antipatternsSet = new HashSet<String>();
	
	public void run()
	{
		System.out.println("INFO: Starting Excessive Data Antipattern Analysis...");
		Logger.log("INFO: Starting Excessive Data Antipattern Analysis...");
		
		//Go through call graph, check all methods who have a method of mapper in their target but not a method of mapped, potential antipattern detected
		ArrayList<EagerlyMappedEntitiesTuple> eagerlyMappedTuplesList = SQLDriver.getEagerlyMappedEntities();
		
		if( (eagerlyMappedTuplesList != null) && (eagerlyMappedTuplesList.size() > 0))
		{
			//For each of these pairs, perform Data flow analysis
			
			for(int i =0; i < eagerlyMappedTuplesList.size(); i++)
			{
				dataFlowAnalysis(eagerlyMappedTuplesList.get(i));
			}
		}
		
		Iterator<String> iterator = this.antipatternsSet.iterator();
		int counter = 0;
		while (iterator.hasNext())
		{
			//listOfAntipatterns = listOfAntipatterns.concat(" "+iterator.next());
			//System.out.println("$$$$$ ["+(counter+1)+"] Reported Antipattern in: "+ iterator.next());
			Logger.log("$$$$$ ["+(counter+1)+"] Reported Antipattern in: "+ iterator.next());
			counter++;
		}

		System.out.println("INFO: Ending Excessive Data Antipattern Analysis...");
		Logger.log("INFO: Ending Excessive Data Antipattern Analysis...");
	}
	
	private void dataFlowAnalysis(EagerlyMappedEntitiesTuple eagerlyMappedTuple)
	{
		System.out.println("\n\n\n");
		System.out.println("Analyzing the tuple: { [Mapper]=> "+ eagerlyMappedTuple.getMapperEntityName() + " [Mapped] => "+ eagerlyMappedTuple.getMappedEntityName()+" }");
		Logger.log("Analyzing the tuple: { [Mapper]=> "+ eagerlyMappedTuple.getMapperEntityName() + " [Mapped] => "+ eagerlyMappedTuple.getMappedEntityName()+" }");
		
		// Get all methods originating from the mapper
		ArrayList<String> sourceMethodsList = SQLDriver.getSourceMethodsOfEntity(eagerlyMappedTuple.getMapperEntityName());
		
		//Iterate through these methods
		if(sourceMethodsList.size() > 0)
		{
			//System.out.println(" A bunch of Source Methods call a method of: "+eagerlyMappedTuple.getMapperEntityName()
			//+ " , now checking if : "+ eagerlyMappedTuple.getMappedEntityName() + " is part of the targets of these source");
			
			//partial Flag: There exist methods that call the Mapper's getters/setters, these should also call the MappedBy
			for(int i=0; i < sourceMethodsList.size(); i++)
			{
				ArrayList<String> targetMethods = SQLDriver.getTargetMethodsOfSource(sourceMethodsList.get(i));
				
				//System.out.println("Checking all targets of source method: "+sourceMethodsList.get(i));
				
				//Iterate through the list target method that a source method invokes
				for(int j=0; j < targetMethods.size(); j++)
				{
					if(targetMethods.size() > 0)
					{
						String[] pieces = targetMethods.get(j).split(":");
						
						//System.out.println("Source method: "+sourceMethodsList.get(i)+ " which called a method from: "+ eagerlyMappedTuple.getMapperEntityName()+" also calls a method from: "+ pieces[0]);
						
						if(pieces[0].equals(eagerlyMappedTuple.getMappedEntityName()))
						{
							//This is the case where all is good, the target includes calls to the mapped by
							//System.out.println("since "+ pieces[0] + " matches "+ eagerlyMappedTuple.getMappedEntityName() + "it means that the EAGER link is properly used");
							//System.out.println("All good, source methods calls both mapper and mappedby in target");
						}
						else
						{
							this.antipatternsSet.add(sourceMethodsList.get(i)+" of tuple "+"{ [Mapper]=> "+ eagerlyMappedTuple.getMapperEntityName() + " [Mapped] => "+ eagerlyMappedTuple.getMappedEntityName()+" }");
							System.out.println(" *** ["+j+"] [Potential Excessive Data Antipattern Detected] in method:" + sourceMethodsList.get(i) + " of Entity class: [Entity] => "+ eagerlyMappedTuple.getMapperEntityName()+ " but does not include any invocation to the Mapped Entity [Entity] => "+eagerlyMappedTuple.getMappedEntityName()+" in its invocation targets");
							Logger.log(" *** ["+j+"] [Potential Excessive Data Antipattern Detected] in method:" + sourceMethodsList.get(i) + " of Entity class: [Entity] => "+ eagerlyMappedTuple.getMapperEntityName()+ " but does not include any invocation to the Mapped Entity [Entity] => "+eagerlyMappedTuple.getMappedEntityName()+" in its invocation targets");
						}
					}
				}
			}
		}
	}
}
