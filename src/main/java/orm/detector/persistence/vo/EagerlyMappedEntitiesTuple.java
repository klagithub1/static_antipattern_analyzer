package main.java.orm.detector.persistence.vo;

public class EagerlyMappedEntitiesTuple 
{
	private String mappedEntityName;
	private String mapperEntityName;
	private String mappedEntityId;
	private String mapperEntityId;
	
	public EagerlyMappedEntitiesTuple( String mappedEntityName, String mapperEntityName, String mappedEntityId, String mapperEntityId )
	{
		this.mappedEntityName = mappedEntityName;
		this.mapperEntityName = mapperEntityName;
		this.mappedEntityId = mappedEntityId;
		this.mapperEntityId = mapperEntityId;
	}
	
	public String getMapperEntityId() 
	{
		return mapperEntityId;
	}
	
	public void setMapperEntityId(String mapperEntityId) 
	{
		this.mapperEntityId = mapperEntityId;
	}
	
	public String getMappedEntityId() 
	{
		return mappedEntityId;
	}
	
	public void setMappedEntityId(String mappedEntityId) 
	{
		this.mappedEntityId = mappedEntityId;
	}
	
	public String getMapperEntityName() 
	{
		return mapperEntityName;
	}
	
	public void setMapperEntityName(String mapperEntityName)
	{
		this.mapperEntityName = mapperEntityName;
	}
	
	public String getMappedEntityName() 
	{
		return mappedEntityName;
	}
	
	public void setMappedEntityName(String mappedEntityName) 
	{
		this.mappedEntityName = mappedEntityName;
	}
}
