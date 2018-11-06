/*======================================================================
Most recently used list of connections from user properties.
Application : Siard2
Description : Most recently used list of connections.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 10.05.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui;

import java.util.*;

/*====================================================================*/
/** Most recently used list of connections from user properties.
 @author Hartwig Thomas
 */
public class SchemaMapping
{
	/*====================================================================
  (private) data members
  ====================================================================*/
	/** ensures "singleton"  */
  private static SchemaMapping _sm = null;
	/** schema mapping */
	private final Map<String,String> _mapSchemaMapping = new HashMap<String,String>();
	
	/*====================================================================
  constructor
  ====================================================================*/
	private SchemaMapping()
	{
		load();
	} /* constructor SchemaMapping */
	
	/*====================================================================
  methods
  ====================================================================*/
  /*------------------------------------------------------------------*/
	/** get the schema mapping
	 * @return mapping.
	 */
  public Map<String,String> getSchemaMapping(Set<String> setSchemas)
  {
    Map<String,String> mapSchemaMapping = new HashMap<String,String>();
    boolean bMapping = _mapSchemaMapping.keySet().equals(setSchemas);
    for (Iterator<String> iterSchema = setSchemas.iterator(); iterSchema.hasNext();)
    {
      String sSchema = iterSchema.next();
      if (bMapping)
        mapSchemaMapping.put(sSchema, _mapSchemaMapping.get(sSchema));
      else
        mapSchemaMapping.put(sSchema, sSchema);
    }
    return mapSchemaMapping;
  } /* getSchemaMapping */
  
	/*------------------------------------------------------------------*/
	/** set the schema mappings
   * @param mapSchema mapping
	 */
	public void setSchemaMapping(Map<String,String> mapSchemaMapping)
	{
	  _mapSchemaMapping.clear();
    for (Iterator<String> iterSchema = mapSchemaMapping.keySet().iterator(); iterSchema.hasNext();)
    {
      String sSchema = iterSchema.next();
      _mapSchemaMapping.put(sSchema, mapSchemaMapping.get(sSchema));
    }
	} /* setSchemaMapping */
	
	/*------------------------------------------------------------------*/
	/** load the map from the user properties.
	 */
	public void load()
	{
    UserProperties up = UserProperties.getUserProperties();
		_mapSchemaMapping.clear();
		for (int iIndex = 0; up.getSchemaMapping(iIndex) != null; iIndex++)
		{
      String sMapping = up.getSchemaMapping(iIndex);
      String[] as = sMapping.split("\\t");
      if (as.length == 2)
        _mapSchemaMapping.put(as[0],as[1]);
		}
	} /* load */
	
	/*------------------------------------------------------------------*/
	/** store the map in the user properties.
	 */
	public void store()
	{
	  // clear all previous
    UserProperties up = UserProperties.getUserProperties();
	  for (int iIndex = 0; up.getSchemaMapping(iIndex) != null; iIndex++)
      up.setSchemaMapping(iIndex,null);
	  int iIndex = 0;
	  for (Iterator<String> iterMapping = _mapSchemaMapping.keySet().iterator(); iterMapping.hasNext(); )
	  {
	    String sSchema = iterMapping.next();
	    up.setSchemaMapping(iIndex,sSchema+"\t"+_mapSchemaMapping.get(sSchema));
	    iIndex++;
	  }
	} /* store */
	
	/*====================================================================
  factory
  ====================================================================*/
	public static SchemaMapping getInstance()
	{
    if (_sm == null)
      _sm = new SchemaMapping();
		return _sm;
	} /* getInstance */

} /* SchemaMapping */
