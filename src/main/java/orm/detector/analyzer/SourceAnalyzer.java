package main.java.orm.detector.analyzer;

import java.io.File;

import main.java.orm.detector.report.ReportElement;

public class SourceAnalyzer 
{
	
	private String rootDirectory;
	
	public SourceAnalyzer(String rootDirectory)
	{
		this.rootDirectory = rootDirectory;
	}
	
	public void analyze()
	{
		//List all files
		scanFiles(new File(this.rootDirectory).listFiles());
	}
	
	private void scanFiles(File[] files) 
	{
		//Go through every file
	    for (File file : files) 
	    {
	    	//Ignore directories
	        if (file.isDirectory()) 
	        {
	            System.out.println("Directory: " + file.getName());
	            scanFiles(file.listFiles()); // Calls self, recursively
	        } 
	        else 
	        {
	        	//Read java files into a string buffer
	            System.out.println("File: Name: " + file.getName() + " Absolute Path: " +file.getAbsolutePath()+ " Path: "+file.getPath());
	            System.out.println(FileReader.readFile(file.getAbsolutePath()));
	            String contentBuffer = FileReader.readFile(file.getAbsolutePath());
	            
	            // For the files that have source code content (*.java files)
	            if(contentBuffer != "")
	            {
	            	//Create a Report Element
	            	ReportElement repEl = new ReportElement();
	            	
	            	//Store file absolute path
	            	repEl.setFilePath(file.getAbsolutePath());
	            	
	            	//Store file content as string
	            	repEl.setFileContent(contentBuffer);
	            	
	            	//Store file name
	            	repEl.setFileName(file.getName());
	            	
	            	//Store public class name
	            	repEl.setClassName((file.getName()).replace(".java",""));
	            	
	            	// Read line-by-line and apply filters
	            	String[] lines = contentBuffer.split(System.getProperty("line.separator"));
	            	
	            	int linenr = 0;
	            	for(String line : lines)
	            	{
	            		//read line by line
	            		System.out.println("-> Line "+(++linenr)+" "+line);
	            		
	            		//Check if line is a method declaration in that class
	            		//if(line.matches(regex))
	            		//{
	            		//	
	            		//}
	            		
	            	}
	            	
	            	//Store the total number of lines of code
	            	repEl.setLinesOfCode(linenr);
	            	
	            	
	            	
	            	linenr = 0; //reset line number
	            	
	            	System.out.println("### "+repEl);
	            }
	            
	            //Collect the file content as buffer string
	        }
	    }
	}
}
