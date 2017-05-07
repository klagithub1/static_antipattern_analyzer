package main.java.orm.detector.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import main.java.orm.detector.persistence.vo.EagerlyMappedEntitiesTuple;

//TODO make this a singleton
public class SQLDriver 
{
	private static String DATABASE_NAME;
	
	public static void setDbName(String dbPath, String dbName)
	{
		DATABASE_NAME = dbPath+dbName;
	}
	
	public static void createInitialDatabase(String dbPath, String dbName)
	{
		
	    Connection c = null;
	    Statement stmt = null;
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	     
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);

	      //create callgraph table
	      stmt = c.createStatement();
	      String sql = "CREATE TABLE callgraph " +
	                   "(cg_id INTEGER PRIMARY KEY     AUTOINCREMENT NOT NULL," +
	                   " source_method_name           TEXT, " + 
	                   " source_method_type           TEXT, " + 
	                   " source_method_id             INT, " + 
	                   " target_method_name           TEXT, " + 
	                   " target_method_type           TEXT, " + 
	                   " target_method_id             INT,   " + 
	                   " target_method_executed_in_loop             INT   " + 
	                   " )"; 
	      stmt.executeUpdate(sql);
	      
	      sql = "CREATE TABLE class " +
                  "(cl_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                  " class_name           TEXT, " + 
                  " package_name           	 TEXT, " + 
                  " absolute_path           	 TEXT, " + 
                  " is_entity            INT " + 
                  " )"; 
	      stmt.executeUpdate(sql);
	    
	      sql = "CREATE TABLE method " +
                  "(me_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                  " method_name           TEXT, " + 
                  " cl_id           	 INTEGER, " + 
                  " is_tainted            INT " + 
                  " )"; 
	      stmt.executeUpdate(sql);
	      
	     sql = "CREATE TABLE attribute " +
                  "(attr_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                  " attribute_name           TEXT, " + 
                  " attribute_type           TEXT, " + 
                  " cl_id           	 INTEGER, " + 
                  " getter_me_id           INTEGER, " +
                  " setter_me_id           INTEGER, " + 
                  " is_tainted            INT " + 
                  " )"; 
	      stmt.executeUpdate(sql);
	      
	      sql = "CREATE TABLE metadata " +
                  "(met_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                  " metadata_name           TEXT, " + 
                  " cl_id           	 INTEGER, " + 
                  " me_id           INTEGER, " +
                  " attr_id           INTEGER " + 
                  " )"; 
	      stmt.executeUpdate(sql);
	      
	      sql = "CREATE TABLE metadata_kv_pair " +
                  "(mkvp_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                  " key           TEXT, " + 
                  " value           TEXT, " + 
                  " met_id           	 INTEGER " + 
                  " )"; 
	      stmt.executeUpdate(sql);
	      
	      stmt.close();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( "SQLDriver::createInitialDatabase -> "+e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Database tructure and tables created successfully.");
	}
	
	public static String getTargetEdgeNamesForMethod(String methodName)
	{
		Connection c = null;
	    Statement stmt = null;
	    String resultTargetEdge = "";
	   
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT target_method_name from callgraph where source_method_name='"+methodName+"';" );
	      
	      while ( rs.next() ) 
	      {
	    	  resultTargetEdge =  rs.getString("target_method_name")+","+resultTargetEdge;
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( "SQLDriver::getTopMethodId -> "+e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
		return resultTargetEdge;
	}
	
	public static String getMetadataListforMethod(int methodId)
	{
		Connection c = null;
	    Statement stmt = null;
	    String resultList = "";
	   
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT metadata_name from metadata where me_id='"+methodId+"' ;" );
	      
	      while ( rs.next() ) 
	      {
	    	  resultList = rs.getString("metadata_name")+","+resultList;
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( "SQLDriver::getMetadataListforMethod -> "+e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
		return resultList;
	}
	
	public static int methodInTableCallGraph(String methodName)
	{
		Connection c = null;
	    Statement stmt = null;
	    int resultId = 0;
	   
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT source_method_name,source_method_id from callgraph where source_method_name='"+methodName+"' LIMIT 1;" );
	      
	      while ( rs.next() ) 
	      {
	    	  resultId = rs.getInt("source_method_id");
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( "SQLDriver::methodInTableCallGraph -> "+e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
		return resultId;
	}
	
	public static void updateMethodTaintedStatusByName(String methodName)
	{
		Connection c = null;
	    Statement stmt = null;
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);
	      
	      stmt = c.createStatement();
	      String sql = "UPDATE method SET is_tainted='1' WHERE method_name='"+methodName+"';"; 
	      stmt.executeUpdate(sql);

	      stmt.close();
	      c.commit();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( "SQLDriver::updateMethodTaintedStatusByName -> "+e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	}
	
	public static void updateMethodTaintedStatusById(int methodid)
	{
		Connection c = null;
	    Statement stmt = null;
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);
	      
	      stmt = c.createStatement();
	      String sql = "UPDATE method SET is_tainted='1' WHERE me_id='"+methodid+"';"; 
	      stmt.executeUpdate(sql);

	      stmt.close();
	      c.commit();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( "SQLDriver::updateMethodTaintedStatusById -> "+e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	}
	
	public static void insertCallGraphRecord(String sourceMethodType, String sourceMethodName, String sourceMethId, String targetMethodType, String targetMethodName, String isCalledInLoop)
	{
	    Connection c = null;
	    Statement stmt = null;
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      String sql = "INSERT INTO callgraph (source_method_name,source_method_type,source_method_id, target_method_name,target_method_type,target_method_executed_in_loop) " +
	                   "VALUES ('"+sourceMethodName+"', '"+sourceMethodType+"',  '"+sourceMethId+"','"+targetMethodName+"', '"+targetMethodType+"', '"+isCalledInLoop+"');"; 
	      stmt.executeUpdate(sql);

	      stmt.close();
	      c.commit();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( "SQLDriver::insertCallGraphRecord -> "+e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	}
	
	public static void insertClassRecord(String className, String packageName, String isEntity, String absolutePath)
	{
		Connection c = null;
	    Statement stmt = null;
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);
	      
	      stmt = c.createStatement();
	      String sql = "INSERT INTO class (class_name,package_name,absolute_path,is_entity) " +
	                   "VALUES ('"+className+"', '"+packageName+"', '"+absolutePath+"', '"+isEntity+"');"; 
	      stmt.executeUpdate(sql);

	      stmt.close();
	      c.commit();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( "SQLDriver::insertClassRecord -> "+e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	}
	
	public static void insertMethodRecord(String methodName, String classId, String isTainted)
	{
		Connection c = null;
	    Statement stmt = null;
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);
	      
	      stmt = c.createStatement();
	      String sql = "INSERT INTO method (method_name,cl_id,is_tainted) " +
	                   "VALUES ('"+methodName+"', '"+classId+"', '"+isTainted+"' );"; 
	      stmt.executeUpdate(sql);

	      stmt.close();
	      c.commit();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println(  "SQLDriver::insertMethodRecord -> "+e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	}
	
	public static int getTopMethodId()
	{
		Connection c = null;
	    Statement stmt = null;
	    int id = 0;
	   
	    try 
	    {
	      
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT me_id from method order by me_id DESC LIMIT 1;" );
	      
	      while ( rs.next() ) 
	      {
	         id = rs.getInt("me_id");
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( "SQLDriver::getTopMethodId -> "+e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
		return id;
	}
	
	public static int getMethodIdByName(String methodName)
	{
		Connection c = null;
	    Statement stmt = null;
	    int id = 0;
	   
	    try 
	    {
	      
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT me_id from method where method_name='"+methodName+"';" );
	      
	      while ( rs.next() ) 
	      {
	         id = rs.getInt("me_id");
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( "SQLDriver::getMethodIdByName -> "+e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
		return id;
	}
	
	public static void insertAttributeRecord(String attributename, String attributetype, String classId, String getterId, String setterId, String isTainted)
	{
		Connection c = null;
	    Statement stmt = null;
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);
	      
	      stmt = c.createStatement();
	      String sql = "INSERT INTO attribute (attribute_name,attribute_type,cl_id,getter_me_id,setter_me_id,is_tainted) " +
	                   "VALUES ('"+attributename+"', '"+attributetype+"', '"+classId+"', '"+getterId+"', '"+setterId+"', '"+isTainted+"');"; 
	      stmt.executeUpdate(sql);

	      stmt.close();
	      c.commit();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( "SQLDriver::insertAttributeRecord -> "+e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	}
	
	public static int getTopAttributeId()
	{
		Connection c = null;
	    Statement stmt = null;
	    int id = 0;
	   
	    try 
	    {
	      
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT attr_id from attribute order by attr_id DESC LIMIT 1;" );
	      
	      while ( rs.next() ) 
	      {
	         id = rs.getInt("attr_id");
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( "SQLDriver::getTopAttributeId -> "+e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
		return id;
	}
	
	public static void insertMetadataRecord(String metadataname, String classId, String methodId, String attributeId)
	{
		Connection c = null;
	    Statement stmt = null;
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);
	      
	      stmt = c.createStatement();
	      String sql = "INSERT INTO metadata (metadata_name,cl_id,me_id,attr_id) " +
	                   "VALUES ('"+metadataname+"', '"+classId+"', '"+methodId+"', '"+attributeId+"');"; 
	      stmt.executeUpdate(sql);

	      stmt.close();
	      c.commit();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( "SQLDriver::insertMetadataRecord -> "+e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	}
	
	public static void insertMetadataKVPairRecord(String key, String value, String metadata_id)
	{
		Connection c = null;
	    Statement stmt = null;
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);
	      
	      stmt = c.createStatement();
	      String sql = "INSERT INTO  metadata_kv_pair (key,value,met_id) " +
	                   "VALUES ('"+key+"', '"+value+"', '"+metadata_id+"');"; 
	      stmt.executeUpdate(sql);

	      stmt.close();
	      c.commit();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( "SQLDriver::insertMetadataKVPairRecord -> "+e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	}
	
	public static int getTopMetadataId()
	{
		Connection c = null;
	    Statement stmt = null;
	    int id = 0;
	   
	    try 
	    {
	      
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT met_id from metadata order by met_id DESC LIMIT 1;" );
	      
	      while ( rs.next() ) 
	      {
	         id = rs.getInt("met_id");
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println("SQLDriver::getTopMetadataId -> "+ e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
		return id;
	}
	
	public static int getTopClassId()
	{
		Connection c = null;
	    Statement stmt = null;
	    int id = 0;
	   
	    try 
	    {
	      
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT cl_id from class order by cl_id DESC LIMIT 1;" );
	      
	      while ( rs.next() ) 
	      {
	         id = rs.getInt("cl_id");
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println("SQLDriver::getTopClassId -> "+ e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
		return id;
	}

	public static void updateAttributeTaintedStatusById(int id) 
	{
		Connection c = null;
	    Statement stmt = null;
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);
	      
	      stmt = c.createStatement();
	      String sql = "UPDATE attribute SET is_tainted='1' WHERE attr_id='"+id+"';"; 
	      stmt.executeUpdate(sql);

	      stmt.close();
	      c.commit();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( "SQLDriver::updateAttributeTaintedStatusById -> "+e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	}
	
	public static boolean checkMethodTaintedStatus(String methodName) 
	{
		Connection c = null;
	    Statement stmt = null;
	    int tainted = 0;
	   
	    try 
	    {
	      
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT is_tainted FROM method WHERE method_name='"+methodName+"' ;" );
	      
	      while ( rs.next() ) 
	      {
	    	  tainted = rs.getInt("is_tainted");
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println("SQLDriver::getTopMetadataId -> "+ e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
	    if(tainted == 1)
	    {
	    	return true;
	    }
	    else
	    {
	    	return false;
	    }
	}
	
	public static ArrayList<String> getListOfUntaintedMethods()
	{
		Connection c = null;
	    Statement stmt = null;
	    ArrayList<String> unTaintedMethods = new ArrayList<String>();
	   
	    try 
	    {
	      
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT method_name FROM method WHERE is_tainted='0' ;" );
	      
	      while ( rs.next() ) 
	      {
	    	  unTaintedMethods.add(rs.getString("method_name"));
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println("SQLDriver::getListOfUntaintedMethods -> "+ e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
	    return unTaintedMethods;
	}

	public static ArrayList<String> getLoopCallGraphEntries() 
	{
		Connection c = null;
	    Statement stmt = null;
	    ArrayList<String> loopMethodEntries = new ArrayList<String>();
	   
	    try 
	    {
	      
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT source_method_name, target_method_name FROM callgraph WHERE target_method_executed_in_loop='1' ;" );
	      
	      while ( rs.next() ) 
	      {
	    	  loopMethodEntries.add(rs.getString("source_method_name")+"_"+rs.getString("target_method_name"));
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println("SQLDriver::getLoopCallGraphEntries -> "+ e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
	    return loopMethodEntries;
	}
	
	
	public static ArrayList<String> getSourceMethodsOfEntity(String entity)
	{
		Connection c = null;
	    Statement stmt = null;
	    ArrayList<String> resultEntries = new ArrayList<String>();
	  
	    try 
	    {
	      
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT source_method_name FROM callgraph WHERE target_method_name LIKE '"+entity+"%' ;" );
	      
	      while ( rs.next() ) 
	      {
	    	  resultEntries.add(rs.getString("source_method_name"));
	      }
	      
	     //Remove Duplicates due to loops internally
		 Set<String> hs = new HashSet<String>();
		 hs.addAll(resultEntries);
		 resultEntries.clear();
		 resultEntries.addAll(hs);
	      
	      rs.close();
	      stmt.close();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println("SQLDriver::getClassNameAndIdByTargetEntityMetadata -> "+ e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
	    return resultEntries;
	}
	
	public static ArrayList<String> getTargetMethodsOfSource(String sourceMethod)
	{
		Connection c = null;
	    Statement stmt = null;
	    ArrayList<String> resultEntries = new ArrayList<String>();
	  
	    try 
	    {
	      
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT target_method_name FROM callgraph WHERE source_method_name = '"+sourceMethod+"' ;" );
	      
	      while ( rs.next() ) 
	      {
	    	  resultEntries.add(rs.getString("target_method_name"));
	      }
	      
	     //Remove Duplicates due to loops internally
		 Set<String> hs = new HashSet<String>();
		 hs.addAll(resultEntries);
		 resultEntries.clear();
		 resultEntries.addAll(hs);
	      
	      rs.close();
	      stmt.close();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println("SQLDriver::getTargetMethodsOfSource -> "+ e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
	    return resultEntries;
	}
	
	/*Please note that this method shoudl eb used witha  degree of uncertainity.*/
	public static String getClassNameAndIdByTargetEntityMetadata(String targetEntity)
	{
		Connection c = null;
	    Statement stmt = null;
	    String resultEntries = "";
	    
		
		//KLAJDI: Need to truncate the L in the first position and ; in the last position and replace / with . so it becomes a class
	    
	    StringBuilder sb = new StringBuilder(targetEntity);
	    sb.deleteCharAt(targetEntity.length()-1);
	    sb.deleteCharAt(0);
	    targetEntity = sb.toString();
	    targetEntity =  targetEntity.replace("/", ".");
	    
	    try 
	    {
	      
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT cl_id, class_name FROM class WHERE class_name = '"+targetEntity+"' ORDER BY cl_id DESC LIMIT 1 ;" );
	      
	      while ( rs.next() ) 
	      {
	    	  resultEntries = rs.getString("cl_id")+"_"+rs.getString("class_name");
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println("SQLDriver::getClassNameAndIdByTargetEntityMetadata -> "+ e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
	    return resultEntries;
	}
	
	/**
	 *  //List all ID of metadata that have a fetch key with value EAGER and a key mappedBy in their kv pair list
	 *	//Loop through this list and get the matching attribute which should be an instance of an Entity class (else dismiss).	
	 *	//Insert a new mapped-mapper pair for analysis
	 */
	public static ArrayList<EagerlyMappedEntitiesTuple> getEagerlyMappedEntities() 
	{
		Connection c = null;
	    Statement stmt = null;
	    ArrayList<String> resultEntries = new ArrayList<String>();
	    ArrayList<EagerlyMappedEntitiesTuple>  eagerlyRelatedTuplesList = new ArrayList<EagerlyMappedEntitiesTuple>();

	    String sqlQuery=""
	    		+"SELECT "
	    		+"mvkp1.value AS 'mapped_entity', "
	    		+"cls1.class_name AS 'mapper_entity', "
	    		+"cls1.cl_id AS 'mapper_entity_id' "
	    		+"FROM metadata_kv_pair AS 'mvkp1'" 
	    		+"LEFT JOIN metadata met1 ON mvkp1.met_id = met1.met_id "
	    		+"LEFT JOIN attribute attr1 ON met1.attr_id = attr1.attr_id "
	    		+"LEFT JOIN class cls1 ON attr1.cl_id = cls1.cl_id "
	    		+"WHERE mvkp1.met_id IN ( select met_id from metadata " 
	    		+"WHERE metadata.metadata_name LIKE '%OneToMany%' "
	    		+"GROUP BY attr_id) and key='targetEntity' ;";
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( sqlQuery );
	      
	      while ( rs.next() ) 
	      {
	    	  resultEntries.add(rs.getString("mapper_entity_id")+"_"+rs.getString("mapper_entity")+"_"+rs.getString("mapped_entity"));
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println("SQLDriver::getEagerlyMappedEntities -> "+ e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
	     //Remove Duplicates due to loops internally 
	     //TODO-KLAJDI: this done in sql
		 //Set<String> hs = new HashSet<String>();
		 //hs.addAll(resultEntries);
		 //resultEntries.clear();
		 //resultEntries.addAll(hs);
	    
	    //Populate the list of tuples
	    for(int i=0; i < resultEntries.size(); i++)
	    {
	    	String[] piecesOfResult = resultEntries.get(i).split("_");
	    	
	    	String mappedEntityId = "";
	    	String mappedEntityName = "";
	    	
	    	if(piecesOfResult.length > 0)
	    	{
	    		String resultOfMetaDataClassMatch = SQLDriver.getClassNameAndIdByTargetEntityMetadata(piecesOfResult[2]);
	    		if(!(resultOfMetaDataClassMatch.equals("")))
	    		{
	    			String[] piecesOfMetadataMatch = resultOfMetaDataClassMatch.split("_");
	    			mappedEntityId = piecesOfMetadataMatch[0];
	    			mappedEntityName = piecesOfMetadataMatch[1];
	    		}
	    	}
	    	
	    	if((!(mappedEntityId.equals(""))))
	    	{
	    		eagerlyRelatedTuplesList.add(new EagerlyMappedEntitiesTuple(mappedEntityName, piecesOfResult[1], mappedEntityId, piecesOfResult[0]));
	    		  		
	    	}
	    }
	    System.out.println("INFO: Counted "+eagerlyRelatedTuplesList.size()+" tuples");
	    return eagerlyRelatedTuplesList;
	}
}
