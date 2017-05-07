package main.java.orm.detector.extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import main.java.orm.detector.analyzer.astvisitor.ClassVisitor;
import main.java.orm.detector.analyzer.astvisitor.MethodVisitor;
import main.java.orm.detector.analyzer.astvisitor.AnnotationVisitor;
import main.java.orm.detector.logger.Logger;

import com.github.javaparser.JavaParser; //Parse Java source code and creates Abstract Syntax Trees.
import com.github.javaparser.ast.CompilationUnit;

public class ASTDataExtractor
{	
	public ASTDataExtractor() 
	{
		//Blank...
	}
	
	private void scanFiles(File[] files) 
	{
		//Go through every file
	    for (File file : files) 
	    {
	    	//Ignore directories
	        if (file.isDirectory()) 
	        {
	            //System.out.println("Directory: " + file.getName());
	            scanFiles(file.listFiles()); // Calls self, recursively
	        } 
	        else 
	        {
	        	if( !(file.getName().toLowerCase().contains(".java")) )
	    		{
	    			//TODO ... deal with non-java files, ignore them ...
	        		//System.out.println("ASTDataExtractor::scanFiles() => not a jave file");
	    		}
	        	else //Deal wiht java files
	        	{
	        		//String content = null;
		    		FileInputStream fin = null;
		    		
		    		try 
		    		{
		    			Logger.log("Checking: " + file.getAbsolutePath());
		    			System.out.println("*** Checking: " + file.getAbsolutePath());
		    			fin = new FileInputStream(file.getAbsolutePath());
		    			
		    			parse(fin);
		    		} 
		    		catch (IOException e) 
		    		{
		    			System.err.println(e);
		    		}
	        	}
	        }
	    }
	}
	
	public void process(String releasepath)
	{
		//get a list of all files in the root directory
		this.scanFiles(new File(releasepath).listFiles());
	}
	
	private void parse(FileInputStream fin)
	{
		//Visit its methods
		 CompilationUnit cu = null;
	        try 
	        {
	            // parse the file
	            cu = JavaParser.parse(fin);
	            
	            // visit and print the methods names
	            ClassVisitor cv = new ClassVisitor();
	            MethodVisitor mv = new MethodVisitor();
	            AnnotationVisitor av = new AnnotationVisitor();
	            
	            cv.visit(cu,null);
	            mv.visit(cu, null);
	            av.visit(cu,null);
	        }
	        catch(Exception e)
	        {
	        	//TODO ??
	        	//System.err.println("*****ERROR"+e.getMessage());
	        }
	        finally 
	        {
	            try 
	            {
					fin.close();
				} 
	            catch (IOException e) 
	            {
					e.printStackTrace();
				}
	        }
	}
	
	// Simple visitor implementation for visiting methodDeclaration nodes.
	/*public void visit(MethodDeclaration n, Object arg)
	{
		//Access the attributes of the method
		//This method will be called for all methods in this
		// CompilationUnit, include inner class methods
		System.out.println(n.getName());
		super.visit(n, arg);
	}*/
}
