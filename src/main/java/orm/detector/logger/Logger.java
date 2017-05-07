package main.java.orm.detector.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger 
{
	public static String PATH="";
    public static void log(String input) 
    {
        try 
        {
            FileWriter fstream = new FileWriter(PATH, true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(input);
            out.newLine();
            out.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}