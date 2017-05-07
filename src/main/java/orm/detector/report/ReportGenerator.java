package main.java.orm.detector.report;
import java.util.UUID;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.json.*;
import javax.json.stream.JsonGenerator;

public class ReportGenerator 
{
	private static String REPORT_FILE_NAME = "report";
	
	private String reportFile;
	
	public ReportGenerator(String reportFilePath)
	{
		this.setReportFile(reportFilePath);
	}
	
	public void generateReport()
	{
		StringWriter swr = new StringWriter();
		JsonGenerator generator = null;
		try {
			generator = Json.createGenerator(new FileWriter("hello.json"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//BufferedWriter bc = new BufferedWriter(new FileWriter());
		
		generator
	     .writeStartObject()
	         .write("firstName", "John")
	         .write("lastName", "Smith")
	         .write("age", 25)
	         .writeStartObject("address")
	             .write("streetAddress", "21 2nd Street")
	             .write("city", "New York")
	             .write("state", "NY")
	             .write("postalCode", "10021")
	         .writeEnd()
	         .writeStartArray("phoneNumber")
	             .writeStartObject()
	                 .write("type", "home")
	                 .write("number", "212 555-1234")
	             .writeEnd()
	             .writeStartObject()
	                 .write("type", "fax")
	                 .write("number", "646 555-4567")
	             .writeEnd()
	         .writeEnd()
	     .writeEnd();
		
		 //System.out.println("!!!!!!!!!!!Employee JSON String\n"+swr.);
	 generator.close();
	 
	
		
		
		JsonObjectBuilder empBuilder = Json.createObjectBuilder();
		
		//TODO this is just a dummy for now
		empBuilder.add("id", "12345").add("dev", 2039232);
		
		JsonObject empJsonObject = empBuilder.build();
		
		System.out.println("Employee JSON String\n"+empJsonObject);
		
		//write to file
		OutputStream os = null;
		try 
		{
			os = new FileOutputStream(this.reportFile);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		JsonWriter jsonWriter = Json.createWriter(os);
		jsonWriter.writeObject(empJsonObject);
		jsonWriter.close();
	}
	
	public void setReportFile(String reportFilePath)
	{
		this.reportFile = reportFilePath + ReportGenerator.REPORT_FILE_NAME +"."+((UUID.randomUUID()).toString()) +".json";
	}
}
