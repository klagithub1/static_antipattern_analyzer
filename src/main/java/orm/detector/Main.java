package main.java.orm.detector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import main.java.orm.detector.analyzer.SourceAnalyzer;
import main.java.orm.detector.analyzer.taint.TaintAnalysisComponent;
import main.java.orm.detector.antipattern.EDAntipatternDetector;
import main.java.orm.detector.extractor.ASTDataExtractor;
import main.java.orm.detector.extractor.BCELDataExtractor;
import main.java.orm.detector.extractor.ESTDataExtractor;
import main.java.orm.detector.logger.Logger;
import main.java.orm.detector.persistence.SQLDriver;
import main.java.orm.detector.persistence.vo.EagerlyMappedEntitiesTuple;

public class Main 
{
	// Enter the full path to the .properties file here
	public static String PROPERTIES_FILES_PATH="/Users/blocalbox/dev/soen691/config/config.properties";
	
	public static void main(String... args) 
	{
		// Load Project Properties and Run Application
		run( loadConfig() );
	}
	
	public static Properties loadConfig()
	{
		System.out.println("===================================================");
		System.out.println("INFO: Fetching configurations from config file.");
		System.out.println("===================================================");
		// Load Project Properties as key-values from a configuration file
		Properties props = new Properties(); 
		try 
		{
			props.load(new FileInputStream(PROPERTIES_FILES_PATH));
		} 
		catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		
		return props;
	}
	
	public static void run( Properties properties )
	{
		//Define the Logger's path, file needs to be created first and filesystem permissions have to be in place.
		Logger.PATH = properties.getProperty("logger_path");
		
		System.out.println("===================================================");
		System.out.println("INFO: Program Started at: "+(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
		Logger.log("INFO: Program Started at: "+(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
		System.out.println("===================================================");
		
		//Set db properties
		SQLDriver.setDbName(properties.getProperty("report_database_path"),properties.getProperty("report_database_name"));
		
		//Create Report Database and tables (for the first time, flag has to be set)
		if ( (properties.getProperty("generate_report_db")).equalsIgnoreCase("true") )
		{
			//TODO check if db exists already before deleting it :(, else will throw error
			System.out.println("===================================================");
			System.out.println("INFO: Creating and initialising database structure.");
			Logger.log("INFO: Creating and initialising database structure.");
			System.out.println("===================================================");
			try 
			{
			    Files.delete( (FileSystems.getDefault().getPath(properties.getProperty("report_database_path"), properties.getProperty("report_database_name"))));
			} 
			catch (NoSuchFileException x) 
			{
			    System.err.format("%s: no such" + " file or directory%n", properties.getProperty("report_database_path")+properties.getProperty("report_database_name"));
			} 
			catch (DirectoryNotEmptyException x) 
			{
			    System.err.format("%s not empty%n", properties.getProperty("report_database_path")+properties.getProperty("report_database_name"));
			} 
			catch (IOException x) 
			{
			    System.err.println(x);
			}
			
			SQLDriver.createInitialDatabase(properties.getProperty("report_database_path"),properties.getProperty("report_database_name"));
			
			//Initialize and Run Analysis
			System.out.println("===================================================");
			System.out.println("INFO: Running Source Code Static Analysis and Data Extraction");
			Logger.log("INFO: Running Source Code Static Analysis and Data Extraction");
			System.out.println("===================================================");
			(new BCELDataExtractor()).process(properties.getProperty("jar_path"));
			
		}
		
		System.out.println("===================================================");
		System.out.println("INFO: Static Analysis, Checking for Name Mis-match");
		Logger.log("INFO: Static Analysis, Checking for Name Mis-match");
		System.out.println("===================================================");
		
		//Uncomment this to run the AST parser
		//(new ASTDataExtractor()).process(properties.getProperty("release_path"));
		
		//Uncomment this to run the EST Parser and check for Name Mismatch Antipatterns and Nested Transactions
		(new ESTDataExtractor(properties.getProperty("sql_schema_path"))).process(properties.getProperty("release_path"));
		
		//Excessive Data Antipattern Detector, check for Excessive Data
		System.out.println("===================================================");
		System.out.println("INFO: Checking for Excessive Data");
		Logger.log("INFO: Checking for Excessive Data");
		System.out.println("===================================================");
		
		//Uncomment this to run the excessive data antipattern analysis
		//(new EDAntipatternDetector()).run();
		
		System.out.println("===================================================");
		System.out.println("INFO: Program Successfully Ended at: "+(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
		Logger.log("INFO: Program Successfully Ended at: "+(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
		System.out.println("===================================================");
	}
}
