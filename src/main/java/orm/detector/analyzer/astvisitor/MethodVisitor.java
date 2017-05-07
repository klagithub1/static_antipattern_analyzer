package main.java.orm.detector.analyzer.astvisitor;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import main.java.orm.detector.logger.Logger;

public class MethodVisitor extends VoidVisitorAdapter  {
	 @Override
     public void visit(MethodDeclaration n, Object arg) {
		 
		 //com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
         // here you can access the attributes of the method.
         // this method will be called for all methods in this 
         // CompilationUnit, including inner class methods
         System.out.println("Method: " +n.getName());
         Logger.log(n.getName());
         super.visit(n, arg);
     }
	 
	 
}
