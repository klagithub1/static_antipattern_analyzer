package main.java.orm.detector.report;

public class ReportElement 
{
	private String fileName;
	private String filePath;
	private String className;
	private String antiPattern;
	private String fileContent;
	private int lineNumber;
	private int linesOfCode;
	
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	public void setClassName(String className)
	{
		this.className = className;
	}
	
	public void setFileContent(String fileContent)
	{
		this.fileContent = fileContent;
	}
	
	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}
	
	public void setLinesOfCode(int linesOfCode)
	{
		this.linesOfCode = linesOfCode;
	}
	
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}
	
	public void setAntiPattern(String antiPattern)
	{
		this.antiPattern = antiPattern;
	}
	
	public String getFileName()
	{
		return this.fileName;
	}
	
	public String getClassName()
	{
		return this.className;
	}
	
	public int getLineNumber()
	{
		return this.lineNumber;
	}
	
	public int getLinesOfCode()
	{
		return this.linesOfCode;
	}
	
	public String getFilePath ()
	{
		return this.filePath;
	}
	
	public String getAntiPattern()
	{
		return this.antiPattern;
	}
	
	public String getFileContent()
	{
		return this.fileContent;
	}
	
	@Override
	public String toString()
	{
		return "[ ReportElement => {fileName: "+this.fileName+", filePath: "+this.filePath+", className: "+this.className+", linesOfCode: "+this.linesOfCode+"} ]";
	}
}
