package main.java.orm.detector.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileReader 
{
	/**
	 * Read a file's content into a string buffer.
	 **/
	public static String readFile(String fileName)
	{
		//if( !(fileName.toLowerCase().contains(".java")) )
		//{
			//return ""; //Return empty string if the file is not a java file
		//}
		
		String content = null;
		FileInputStream fin = null;
		int length;
		byte buf[] = null;
		
		try 
		{
			length = (int)(new File(fileName).length());
			fin = new FileInputStream(fileName);
			buf = new byte[length];
			fin.read(buf);
			fin.close();
			
			// Assign file contents to a string variable
			content = new String(buf);
		} 
		catch (IOException e) 
		{
			System.err.println(e);
		}
		//Filters
		//content = content.replaceAll("<script>.*</script>","");
		
		return content;
	}
	
	/*public static int getFileSize(String fileName)
	{
		if( !(fileName.toLowerCase().contains(".java")) )
		{
			return -500; //Return error
		}
		
		String fileLocation =fileName;
		int length = (int)(new File(fileLocation).length());
		return length;
	}*/

	//private static String stripComments(String content)
	//{
	//	return content.replaceAll("/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/","");
	//}
}
