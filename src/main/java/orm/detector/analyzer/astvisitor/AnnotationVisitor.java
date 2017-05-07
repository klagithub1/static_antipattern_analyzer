package main.java.orm.detector.analyzer.astvisitor;

import java.util.ArrayList;

import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import main.java.orm.detector.logger.Logger;

import main.java.orm.detector.persistence.SQLDriver;

public class AnnotationVisitor extends VoidVisitorAdapter {
		//ArrayList for Sequence names
		private static ArrayList<String> Seq = new ArrayList<String>();

		public static ArrayList<String> getSeq()
		{
			return Seq;
		}
	
		@Override
		public void visit(MarkerAnnotationExpr n, Object arg) 
		{
			 //com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
	        // here you can access the attributes of the method.
	        // this method will be called for all methods in this 
	        // CompilationUnit, including inner class methods
			String annot = n.getName().toString();
			
			//We do not care about @Override
			//if(!annot.equals("Override"))
				System.out.println("Annotation: " +annot);
	        
			//For SNMAntipatternDetector
	        if(annot.equals("SequenceGenerator"))
	        {
	        	Logger.log("Annotation is Sequence: "+n.getName());
	        	System.out.println("Annotation is Sequence: "+n.getName());
	        	//This adds the annotation to Seq not the name!! TODO
	        	Seq.add(annot);
	        }
	        //For NTAntipatternDetector
	        else if(annot.equals("Transactional"))
	        {
	        	Logger.log("Annotation is Transactional in class: "+n.getClass().toString());
	        	//Mark as tainted
	        	Object[] methods = n.getClass().getMethods();
	        	for(int i = 0; i < methods.length; i++)
	        	{
	        		//SQLDriver.updateMethodTaintedStatusByName(methods[i].toString());
	        	}
	        }
	        else
	        {
	        	if(!annot.equals("Override"))
	        		Logger.log(n.getName().toString());
	        }
	        super.visit(n, arg);
		}
}
