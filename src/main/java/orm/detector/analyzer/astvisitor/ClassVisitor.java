package main.java.orm.detector.analyzer.astvisitor;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
//import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import main.java.orm.detector.logger.Logger;

public class ClassVisitor extends VoidVisitorAdapter  {
	
	private boolean isEntity = false;
	
	public boolean getEntity()
	{
		return this.isEntity;
	}

	 @Override
     public void visit(ClassOrInterfaceDeclaration n, Object arg) {
		 
		 //com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
         // here you can access the attributes of the method.
         // this method will be called for all methods in this 
         // CompilationUnit, including inner class methods
        
        
        //Logger.log(n.getName());
         super.visit(n, arg);
        
         System.out.println("CLASS: "+n.getName());
         Logger.log("Visiting Class : "+n.getName());
         NodeList<AnnotationExpr> nodeList = n.getAnnotations();
         
         for(int i=0; i < nodeList.size(); i++)
         {
         	//System.out.println("Annotation: "+nodeList.get(i).toString());
         	
         	if((nodeList.get(i).toString()).contains("Entity"))
         	{
         		this.isEntity  = true;
         		Logger.log("Class is entity: "+n.getName());
         	}
         }
     }
}
