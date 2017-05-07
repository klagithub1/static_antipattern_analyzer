package main.java.orm.detector.analyzer.bcelvisitor;

import java.util.ArrayList;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ElementValuePair;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.GotoInstruction;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReturnInstruction;

import main.java.orm.detector.analyzer.taint.TaintAnalysisComponent;
import main.java.orm.detector.logger.Logger;
import main.java.orm.detector.persistence.SQLDriver;

@SuppressWarnings("deprecation")
public class MethodVisitor extends EmptyVisitor 
{
    @SuppressWarnings("unused")
	private int classId;
    
    private int methodId;
    private MethodGen mg;
    private ConstantPoolGen cp;
    private boolean ifInstructionDetectedFlag;
    private boolean gotoInstructionDetectedFlag;
    JavaClass visitedClass;
    
    ArrayList<String> buffer;
    
    @SuppressWarnings("unused")
	private String format;

    public MethodVisitor(MethodGen m, JavaClass jc) 
    {
    	this.buffer = new ArrayList<String>();
    	this.ifInstructionDetectedFlag= false;
        this.gotoInstructionDetectedFlag= false;
        
        visitedClass = jc;
        mg = m;
        cp = mg.getConstantPool();
    }

    public void start(int classId)
    {
    	this.classId = classId;
    	
        if (mg.isAbstract() || mg.isNative())
        {
            return;
        }
        
        //Insert method in Db
        SQLDriver.insertMethodRecord(visitedClass.getClassName() + ":" + mg.getName(), Integer.toString(classId), "0");
     
        //Save its id
        methodId = SQLDriver.getTopMethodId();
        
        //Loop through its instructions
        //System.out.println("--------START INSTRUCTION LOOP---------");
        
        for (InstructionHandle ih = mg.getInstructionList().getStart(); ih != null; ih = ih.getNext()) 
        {
            Instruction i = ih.getInstruction();
            
            if (!visitInstruction(i))
            {
            	 i.accept(this);
            }  
        }
        
//        if(ifInstructionDetectedFlag && gotoInstructionDetectedFlag)
//        {
//        	for(String entry : buffer)
//        	{
//        		String[] piece = entry.split("_");
//        		SQLDriver.insertCallGraphRecord("",piece[0], piece[1], piece[2] ,piece[3],"1");
//        	}
//        }
//        else
//        {
        	for(String entry : buffer)
        	{
        		String[] piece = entry.split("_");
        		SQLDriver.insertCallGraphRecord("",piece[0], piece[1], piece[2] ,piece[3],"0");
        	}
//        }
        
        //Reset Loop Flags
//        this.ifInstructionDetectedFlag=false;
//        this.gotoInstructionDetectedFlag=false;
        this.buffer.clear();
        
        //System.out.println("--------END INSTRUCTION LOOP---------");
        
        //Process Annotations
        processMethodAnnotations();
        
        //Check if it has a suspected annotation
//       if( this.hasSuspectedAnnotation(visitedClass.getClassName() + ":" + mg.getName()))
//       {
//    	   SQLDriver.updateMethodTaintedStatusByName(visitedClass.getClassName() + ":" + mg.getName());
//    	   Logger.log("Method: "+(visitedClass.getClassName() + ":" + mg.getName())+" is tainted!");
//       }
    }
    
    private void processMethodAnnotations()
    {
    	AnnotationEntry[] annotationEntries = mg.getMethod().getAnnotationEntries();
    	
    	for(AnnotationEntry annotation : annotationEntries)
    	{
    		//Insert Metadata of this attribute to the database
    		
    		SQLDriver.insertMetadataRecord(annotation.getAnnotationType(), "0", Integer.toString(SQLDriver.getTopMethodId()), "0");
    		ElementValuePair[] evpair = annotation.getElementValuePairs();
    		
    		for(ElementValuePair pair : evpair)
    		{
    			//Insert Key and Value Pairs of this attribute to the database
    			SQLDriver.insertMetadataKVPairRecord(pair.getNameString(), ((pair.getValue()).toString()).replaceAll("'", ""), Integer.toString(SQLDriver.getTopMetadataId()));
    			
    			System.out.println("INFO: Processed metadata \"key-value\" pair: "+" key: "+pair.getNameString()+" value: "+(pair.getValue()).toString());
    			Logger.log("INFO: Processed metadata \"key-value\" pair: "+" key: "+pair.getNameString()+" value: "+(pair.getValue()).toString());
    		}
    	}
    }
    
//    private boolean hasSuspectedAnnotation(String methodName)
//	{
//		//Retrieve this method's id
//		int method_id = SQLDriver.getMethodIdByName(methodName);
//		
//		if(method_id <= 0)
//		{
//			return false; //Actually throw an error because system is inconsistent if this happen
//		}
//		
//		//Check all metadata(annotation) that belong to this method
//		String[] metadataList = (SQLDriver.getMetadataListforMethod(method_id )).split(",");
//		
//		if(metadataList == null)
//		{
//			return false;
//		}
//		
//		//Filter out suspected metadata(annotation) and k-vpairs to make decision
//		for(String metadata : metadataList)
//		{
//			if((metadata.isEmpty()) ||(metadata==null))
//			{
//				continue;
//			}
//			
//			if(metadata.matches(".*Column;"))
//			{
//				return true;
//			}
//			else if(metadata.matches(".*ManyToOne;"))
//			{
//				return true;
//			}
//			else if(metadata.matches(".*JoinColumn;"))
//			{
//				return true;
//			}
//			else if(metadata.matches(".*Id;"))
//			{
//				return true;
//			}
//			
//			//TODO what are the rules??
//		}
//		return false;
//	}

    private boolean visitInstruction(Instruction i) 
    {
    	//KLAJDI: Disable Loop detection for now as it is irrelevant here
//    	if(i instanceof org.apache.bcel.generic.IfInstruction) 
//    	{
//    		this.ifInstructionDetectedFlag = true;
//    	}
//    	
//    	if(i instanceof GotoInstruction)
//    	{
//    		this.gotoInstructionDetectedFlag = true;
//    	}
    	
        return ((InstructionConstants.INSTRUCTIONS[i.getOpcode()] != null) && !(i instanceof ConstantPushInstruction) && !(i instanceof ReturnInstruction));
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) 
    {
    	this.buffer.add((visitedClass.getClassName() + ":" + mg.getName())+"_"+Integer.toString(methodId)+"_M_"+((i.getReferenceType(cp).toString())+":"+i.getMethodName(cp)));
    }
   
    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE i) 
    {
    	this.buffer.add((visitedClass.getClassName() + ":" + mg.getName())+"_"+Integer.toString(methodId)+"_I_"+((i.getReferenceType(cp).toString())+":"+i.getMethodName(cp)));
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL i) 
    {
    	this.buffer.add((visitedClass.getClassName() + ":" + mg.getName())+"_"+Integer.toString(methodId)+"_O_"+((i.getReferenceType(cp).toString())+":"+i.getMethodName(cp)));
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC i) 
    {
    	this.buffer.add((visitedClass.getClassName() + ":" + mg.getName())+"_"+Integer.toString(methodId)+"_S_"+((i.getReferenceType(cp).toString())+":"+i.getMethodName(cp)));
    }
}
