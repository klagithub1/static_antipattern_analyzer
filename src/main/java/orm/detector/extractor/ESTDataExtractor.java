package main.java.orm.detector.extractor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.core.MemberValuePair;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import main.java.orm.detector.analyzer.FileReader;
import main.java.orm.detector.logger.Logger;


public class ESTDataExtractor
{	
	private static String sql_schema;
	
	public ESTDataExtractor(String sql_schema) 
	{
		ESTDataExtractor.sql_schema = sql_schema;
	}
	
	private void scanFiles(File[] files) 
	{
		//Go through every file
	    for (File file : files) 
	    {
	    	System.out.println("Processing "+file.getAbsolutePath());
	    	//Ignore directories
	        if (file.isDirectory()) 
	        {
	            System.out.println("Directory: " + file.getName());
	            scanFiles(file.listFiles()); // Calls self, recursively
	        } 
	        else 
	        {
	        	if( !(file.getName().toLowerCase().contains(".java")) )
	    		{
	    			//TODO ... deal with non-java files, ignore them ...
	        		//System.out.println("ASTDataExtractor::scanFiles() => not a jave file");
	    		}
	        	else //Deal with java files
	        	{
	        		System.out.println("INFO: Processing file: "+file.getName());
	        		try 
	        		{
						this.processJavaFile(file);
					} 
	        		catch (MalformedTreeException e) 
	        		{
						e.printStackTrace();
					} 
	        		catch (IOException e) 
	        		{
						e.printStackTrace();
					} 
	        		catch (BadLocationException e) 
	        		{
						e.printStackTrace();
					}
	        	}
	        }
	    }
	}
	
	public void process(String releasepath)
	{
		//Get a list of all files in the root directory
		this.scanFiles(new File(releasepath).listFiles());
	}
	
	public static boolean isPotentialNameMismatch(String sequenceGeneratorNameValue){
		String sql_file_content = FileReader.readFile(ESTDataExtractor.sql_schema);
		boolean antipatternFlag = true;
		//Strip Quotes
		sequenceGeneratorNameValue = sequenceGeneratorNameValue.replace("\"", "");
		
		// Read line-by-line and apply filters
    	String[] lines = sql_file_content.split(System.getProperty("line.separator"));
    	
    	int linenr = 0;
    	for(String line : lines)
    	{
    		//read line by line
    		
    		if(line.matches("(.*)"+sequenceGeneratorNameValue+"(.*)"))
    		{
    			Logger.log("[Report] The value: "+sequenceGeneratorNameValue+" has a match in the sql file, no antipattern detected for this value.");
    			
    			System.out.println("[Report] The value: "+sequenceGeneratorNameValue+" has a match in the sql file, no antipattern detected for this value.");
    			antipatternFlag = false;
    			return false;
    		}
    	}
		
		return antipatternFlag;
	}
	
	private void  processJavaFile(File file) throws IOException, MalformedTreeException, BadLocationException 
	{
	    String source = FileUtils.readFileToString(file);
	    Document document = new Document(source);
	    ASTParser parser = ASTParser.newParser(AST.JLS8);
	    parser.setSource(document.get().toCharArray());
	    CompilationUnit unit = (CompilationUnit)parser.createAST(null);
	    
	    unit.accept(
    		new ASTVisitor()
    		{
		    	public boolean visit(NormalAnnotation node) 
		    	{
		    		System.out.println("Annotation: "+ node.getTypeName().getFullyQualifiedName()+ ":" + unit.getLineNumber(node.getTypeName().getStartPosition()));
		    		
		    		if((node.getTypeName().getFullyQualifiedName()).equalsIgnoreCase("sequencegenerator"))
		    		{
		    			
		    			//Chop on ,
		    			for(Object pair : node.values())
		    			{
		    				String[] piece = pair.toString().split("=");
		    				
		    				if( piece[0] == null )
		    				{
		    					break;
		    				}
		    				
		    				if(piece[0].equalsIgnoreCase("sequencename"))
		    				{
		    					if(ESTDataExtractor.isPotentialNameMismatch(piece[1]))
		    					{
		    						Logger.log("[Report] The value: "+piece[1]+" has no match in the sql file, Name Mismatch antipattern detected for this value.");
		    		    			
		    						System.out.println("[Report] The value: "+piece[1]+" has no match in the sql file, Name Mismatch antipattern detected for this value.");
		    		    			
		    					}
		    				}
		    			}
		    		}
					return false;
		    	}
	
		        public boolean visit(VariableDeclarationFragment node) 
		        {
		           // SimpleName name = node.getName();
		            System.out.println("var.declaration"+ node.getName().getFullyQualifiedName()+ ":" + unit.getLineNumber(node.getName().getStartPosition()));
		            return false; // do not continue 
		        }
	
		        public boolean visit(MethodDeclaration method) 
		        {
		        	System.out.println("method " + method.getName().getFullyQualifiedName());
		           // System.out.println("method.return " + method.getReturnType2().toString());
		            List<SingleVariableDeclaration> params = method.parameters();
	
		            for(SingleVariableDeclaration param: params) 
		            {
		            	System.out.println("param " + param.getName().getFullyQualifiedName());
		            }
	
		           // Block methodBlock = method.getBody();
		            //String myblock = methodBlock.toString();
		           // methodVisitor(myblock);
		            return false;
		        }
		    }
	    );
	}
}
