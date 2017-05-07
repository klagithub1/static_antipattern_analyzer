package main.java.orm.detector.analyzer.bcelvisitor;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ElementValuePair;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.ReturnInstruction;

import main.java.orm.detector.logger.Logger;
import main.java.orm.detector.persistence.SQLDriver;

public class FieldVisitor extends EmptyVisitor
{
	JavaClass visitedClass;
	private int classId;
	private FieldGen fg;
	
	private int getterMethodId;
	private int setterMethodId;
	
	private static final int NO_RECORD_RETURNED=0;
	
	public FieldVisitor(FieldGen f, JavaClass jc) 
	{
		visitedClass = jc;
	    fg = f;
	}
	
	public void start(int classId)
	{
		this.classId = classId;
		
		getterMethodId = processGetterMethodId();
		setterMethodId = processSetterMethodId();
		
		SQLDriver.insertAttributeRecord(visitedClass.getClassName() + ":" + fg.getName(),fg.getType().toString(), Integer.toString(classId), Integer.toString(getterMethodId), Integer.toString(setterMethodId), "0");
		processFieldAnnotations();
	}
	
	private int processGetterMethodId()
	{
		Method[] methods = visitedClass.getMethods();
		for(Method method:methods)
		{
			if((method.getName()).equalsIgnoreCase("get"+(fg.getField().getName())))
			{
				//Found a method that looks like the getter method, check if it exists in db
				int returnedId = SQLDriver.getMethodIdByName((visitedClass.getClassName() + ":" + method.getName()));
				if(returnedId > NO_RECORD_RETURNED )
				{
					Logger.log("Found Getter method: "+method.getName());
					return returnedId;
				}
			}
		}
		return NO_RECORD_RETURNED;
	}
	
	private int processSetterMethodId()
	{
		Method[] methods = visitedClass.getMethods();
		for(Method method:methods)
		{
			if((method.getName()).equalsIgnoreCase("set"+(fg.getField().getName())))
			{
				//Found a method that looks like the getter method, check if it exists in db
				int returnedId = SQLDriver.getMethodIdByName((visitedClass.getClassName() + ":" + method.getName()));
				if(returnedId > NO_RECORD_RETURNED )
				{
					Logger.log("Found Setter method: "+method.getName());
					return returnedId;
				}
			}
		}
		return NO_RECORD_RETURNED;
	}
	
	private void markTaintedGetterSetter()
	{
		if(getterMethodId > 0)
		{
			SQLDriver.updateMethodTaintedStatusById(getterMethodId);
		}
		
		if(setterMethodId > 0)
		{
			SQLDriver.updateMethodTaintedStatusById(setterMethodId);
		}
	}
	
	private boolean isSuspectedAnnotation(String annotationName)
	{
		if((annotationName.isEmpty()) ||(annotationName==null))
		{
			return false;
		}
		
		if(annotationName.matches(".*Column;"))
		{
			return true;
		}
		else if(annotationName.matches(".*ManyToOne;"))
		{
			return true;
		}
		else if(annotationName.matches(".*OneToOne;"))
		{
			return true;
		}
		else if(annotationName.matches(".*ManyToMany;"))
		{
			return true;
		}
		else if(annotationName.matches(".*OneToOne;"))
		{
			return true;
		}
		else if(annotationName.matches(".*JoinColumn;"))
		{
			return true;
		}
		else if(annotationName.matches(".*Id;"))
		{
			return true;
		}
		
		return false;
	}
	
	private void processFieldAnnotations()
	{
		AnnotationEntry[] annotationEntries = fg.getField().getAnnotationEntries();
		
		for(AnnotationEntry annotation : annotationEntries)
		{
			//Insert Metadata of this attribute to the database
			
			SQLDriver.insertMetadataRecord(annotation.getAnnotationType(), Integer.toString(classId), "0", Integer.toString(SQLDriver.getTopAttributeId()));
			
			System.out.println("INFO: Processed metadata: "+annotation.getAnnotationType());
			Logger.log("INFO: Processed metadata: "+ annotation.getAnnotationType());
			
			if(isSuspectedAnnotation(annotation.getAnnotationType()))
			{
				//Mark this attribute as tainted also
				//SQLDriver.updateAttributeTaintedStatusById(SQLDriver.getTopAttributeId());
				
				//Mark its getters and setters (if any) as tainted
				markTaintedGetterSetter();
			}
			
			ElementValuePair[] evpair = annotation.getElementValuePairs();
			for(ElementValuePair pair : evpair)
			{
				//Insert Key and Value Pairs of this attribute to the database
				SQLDriver.insertMetadataKVPairRecord(pair.getNameString(), ((pair.getValue()).toString()).replaceAll("'", ""), Integer.toString(SQLDriver.getTopMetadataId()));
				
				//System.out.println("---- Processed metadata \"key-value\" pair: "+" key: "+pair.getNameString()+" value: "+(pair.getValue()).toString());
				Logger.log("INFO: Processed metadata \"key-value\" pair: "+" key: "+pair.getNameString()+" value: "+(pair.getValue()).toString());
			}
		}
	}
}
