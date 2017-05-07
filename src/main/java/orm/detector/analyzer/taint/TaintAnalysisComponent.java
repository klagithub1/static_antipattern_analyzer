package main.java.orm.detector.analyzer.taint;

import org.apache.bcel.classfile.Method;

import main.java.orm.detector.persistence.SQLDriver;

public class TaintAnalysisComponent 
{
	private static final int NOT_TAINTED = 0;
	private static final int TAINTED = 1;
	
	public int checkTainted(String methodName)
	{
		if( hasSuspectedAnnotation(methodName) )
		{
			return TAINTED;
		}
		else if( hasTargetEdges(methodName) )
		{
			String[] targetEdges = getTargetEdges(methodName);
			
			if(targetEdges.length > 0)
			{
				for(String targetMethod : targetEdges )
				{
					if((targetMethod == null) || (targetMethod.isEmpty()))
					{
						continue;
					}
					checkTainted(targetMethod);
				}
			}
		}
		
		return NOT_TAINTED;
	}
	
	private boolean hasSuspectedAnnotation(String methodName)
	{
		//Retrieve this method's id
		int method_id = SQLDriver.getMethodIdByName(methodName);
		
		if(method_id <= 0)
		{
			return false; //Actually throw an error because system is inconsistent if this happen
		}
		
		//Check all metadata(annotation) that belong to this method
		String[] metadataList = (SQLDriver.getMetadataListforMethod(method_id )).split(",");
		
		if(metadataList == null)
		{
			return false;
		}
		
		//Filter out suspected metadata(annotation) and k-vpairs to make decision
		for(String metadata : metadataList)
		{
			if((metadata.isEmpty()) ||(metadata==null))
			{
				continue;
			}
			
			if(metadata.matches(".*Column;"))
			{
				return true;
			}
			else if(metadata.matches(".*ManyToOne;"))
			{
				return true;
			}
			else if(metadata.matches(".*JoinColumn;"))
			{
				return true;
			}
			else if(metadata.matches(".*Id;"))
			{
				return true;
			}
			
			//TODO what are the rules??
		}
		return false;
	}
	
	private boolean hasTargetEdges(String methodName)
	{
		return ((SQLDriver.methodInTableCallGraph(methodName) > 0)?true:false);
	}
	
	private String[] getTargetEdges(String methodName)
	{
		return (SQLDriver.getTargetEdgeNamesForMethod(methodName)).split(",");
	}
}
