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
public class MruConnection
{
  /*====================================================================
  (public) constants
  ====================================================================*/
	public static final int iNUM_CONNECTIONS = 10;
	
	/*====================================================================
  (private) data members
  ====================================================================*/
	/** ensures "singleton" for download and upload each */
  private static MruConnection _mcDown = null;
  private static MruConnection _mcUp = null;
	/** a connection consists of a JDBC URL and a database user name */
	static final class Connection
	{
	  public String _sUrl = null;
	  public String _sDbUser = null;
    public String getConnection()
    {
      return _sUrl + "\t" + _sDbUser;
    }
    public Connection(String sConnection)
    {
      String[] as = sConnection.split("\\t");
      if (as.length == 2)
      {
        _sUrl = as[0];
        _sDbUser = as[1];
      }
    }
	  public Connection(String sUrl, String sDbUser)
	  {
	    _sUrl = sUrl;
	    _sDbUser = sDbUser;
	  }
	  public boolean equals(Connection conn)
	  {
	    return _sUrl.equals(conn._sUrl) && _sDbUser.equals(conn._sDbUser);
	  }
	}
	/** choice of download or upload */
	private boolean _bDownload = false;
	/** array of most recently used connections */
	private final List<Connection> _listMruConnections = new ArrayList<Connection>();
	
	/*====================================================================
  constructor
  ====================================================================*/
	private MruConnection(boolean bDownload)
	{
	  _bDownload = bDownload;
		load();
	} /* constructor MruConnection */
	
	/*====================================================================
  methods
  ====================================================================*/
	/*------------------------------------------------------------------*/
	/** get number of MRU connection entries.
	 * @return number of entries.
	 */
	public int getMruConnections()
	{
		return _listMruConnections.size();
	} /* getMruConnections */

	/** get a MRU connection entry
   * @param iIndex indicates which entry is to be used.
	 * @return connection entry.
	 */
  Connection getMruConnection(int iIndex)
  {
    Connection conn = null;
    if ((iIndex >= 0) && (iIndex < _listMruConnections.size()))
      conn = _listMruConnections.get(iIndex);
    return conn;
  } /* getMruConnection */
  
	/*------------------------------------------------------------------*/
	/** get a MRU connection entry's URL.
   * @param iIndex indicates which entry is to be used.
	 * @return connection entry's URL.
	 */
	public String getMruConnectionUrl(int iIndex)
	{
		String sMruConnectionUrl = null;
    Connection connMru = getMruConnection(iIndex);
    if (connMru != null)
      sMruConnectionUrl = connMru._sUrl; 
		return sMruConnectionUrl;
	} /* getMruConnectionUrl */
	
  /*------------------------------------------------------------------*/
  /** get a MRU connection entry's database user.
   * @param iIndex indicates which entry is to be used.
   * @return connection entry's database user.
   */
  public String getMruConnectionDbUser(int iIndex)
  {
    String sMruConnectionDbUser = null;
    Connection connMru = getMruConnection(iIndex);
    if (connMru != null)
      sMruConnectionDbUser= connMru._sDbUser; 
    return sMruConnectionDbUser;
  } /* getMruConnectionDbUser */
  
	/*------------------------------------------------------------------*/
	/** set a MRU connection entry.
   * @param sMruUrl JDBC URL of connection to be added to MRU list.
   * @param sMruDbUser database user of connection to be added to MRU list.
	 */
	public void setMruConnection(String sMruUrl, String sMruDbUser)
	{
	  Connection conn = new Connection(sMruUrl, sMruDbUser);
		if ((_listMruConnections.size() == 0) || 
		    (!conn.equals(_listMruConnections.get(0))))
		{
			int iPrevious = _listMruConnections.indexOf(conn);
			if (iPrevious > 0)
				_listMruConnections.remove(iPrevious);
			_listMruConnections.add(0, conn);
		}
	} /* setMruConnection */
	
	/*------------------------------------------------------------------*/
	/** load the list from the user properties.
	 */
	public void load()
	{
		_listMruConnections.clear();
		for (int iIndex = 0; iIndex < iNUM_CONNECTIONS; iIndex++)
		{
			String sMruConnection = UserProperties.getUserProperties().getMruConnection(_bDownload,iIndex);
			Connection conn = new Connection(sMruConnection);
			if (conn._sUrl != null)
			  _listMruConnections.add(conn);
		}
	} /* load */
	
	/*------------------------------------------------------------------*/
	/** store the list in the user properties.
	 */
	public void store()
	{
		for (int iIndex = 0; iIndex < _listMruConnections.size(); iIndex++)
		{
		  Connection conn = _listMruConnections.get(iIndex);
			UserProperties.getUserProperties().setMruConnection(_bDownload,iIndex, conn.getConnection());
		}
	} /* store */
	
	/*====================================================================
  factory
  ====================================================================*/
	public static MruConnection getMruConnection(boolean bDownload)
	{
	  MruConnection mc = null;
    if (bDownload)
      mc = _mcDown;
    else
      mc = _mcUp;
    if (mc == null)
    {
      mc = new MruConnection(bDownload);
      if (bDownload)
        _mcDown = mc;
      else
        _mcUp = mc;
    }
		return mc;
	} /* getMruConnection */

} /* MruConnection */
