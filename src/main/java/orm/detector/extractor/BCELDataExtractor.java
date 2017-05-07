package main.java.orm.detector.extractor;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.bcel.classfile.JavaClass;

import main.java.orm.detector.analyzer.bcelvisitor.ClassVisitor;
import main.java.orm.detector.logger.Logger;
import main.java.orm.detector.persistence.SQLDriver;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Field;

public class BCELDataExtractor 
{
	public BCELDataExtractor()
	{
		
	}
	
	public void process( String jarFilesPath )
	{
	   ClassParser cp;
       try 
       {
    	   //User defined database id's, for the sake of simplicity
    	    int databaseClassID=1;
    	   
        	File[] jarsDirectory = new File(jarFilesPath).listFiles();
        	
        	for (File jar : jarsDirectory) 
        	{
        		if( jar.isDirectory() )
        		{
        			continue; //Ignore directories
        		}
        		else if( !(jar.getName().toLowerCase().contains(".jar")) )
        		{
        			continue; //Ignore non jar files
        		}
        		else
        		{
        			if (!jar.exists()) 
	                {
	                    System.err.println("Jar file " + jar.getName() + " does not exist");
	                    continue;
	                }
        			
        			@SuppressWarnings("resource")
					Enumeration<JarEntry> entries = ( new JarFile(jar) ).entries();
        			
        			int counter = 0;
        			
        			while ( entries.hasMoreElements() ) 
	                {
	                    JarEntry entry = entries.nextElement();
	                    if (entry.isDirectory())
	                    {
	                    	continue;
	                    }
	                        
	                    if (!entry.getName().endsWith(".class"))
	                    {
	                    	continue;
	                    }
	                        
	                    cp = new ClassParser( jar.getAbsolutePath(), entry.getName() );
	                    
	                    // Go through each class
	                    
	                    //Check if class is part of org.broadleafcommerce package
	                    JavaClass javaClassInstance = cp.parse();
	                    
	                    String isEntity = "0";
	                    
	                    //Ommit the non-broadleaf classes from the calculation
	                    if(!((javaClassInstance.getPackageName()).matches("org.broadleafcommerce.*")))
	                    {
	                    	databaseClassID++;
	                    	continue;
	                    }
	                    
	                    AnnotationEntry[] annotationEntries = javaClassInstance.getAnnotationEntries();
	                    for(AnnotationEntry annotation: annotationEntries)
	                    {
	                    	if((annotation.toString()).matches(".*javax/persistence/Entity.*"))
	                    	{
	                    		Logger.log("Class: "+javaClassInstance.getClassName()+ "is an Entity");
	                    		isEntity = "1";
	                    	}
	                    }
	                    
	                    //Omit Non-Entity Classes
	                    if(Integer.parseInt(isEntity) == 0)
	                    {
	                    	Logger.log("Omitting: "+javaClassInstance.getClassName());
	                    	databaseClassID++;
	                    	continue;
	                    }
	                    
	                    //Insert Class in Database
	                    SQLDriver.insertClassRecord(javaClassInstance.getClassName(), javaClassInstance.getPackageName(), isEntity, "");
	                    
	                    //Increment ID Counter
	                    databaseClassID++;
	                    
	                    //Get top class id form database...
	                    
	                    //Visit Methods and Attributes
	                    ClassVisitor visitor = new ClassVisitor( javaClassInstance, SQLDriver.getTopClassId() );
	                    
	                    visitor.start();
	                    counter++;
	                    
	                    //System.out.println("- Processed class: "+javaClassInstance.getClassName());
	                    Logger.log("INFO: Processed class: "+javaClassInstance.getClassName());
	                }
        			
        			System.out.println("Processed "+counter+" elements in jar package: "+jar.getAbsolutePath());
        			Logger.log("Processed "+counter+" elements in jar package: "+jar.getAbsolutePath());
        		}
        	}
        } 
        catch (IOException e) 
        {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
        }
	}
}
