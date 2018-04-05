/*======================================================================
User properties contains the user's properties.
Application : Siard2
Description : User properties contains the user's properties in a property
              file.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 10.05.2017, Hartwig Thomas, Enter AG, Zurich
======================================================================*/

package ch.admin.bar.siard2.gui;

import java.io.*;
import java.util.*;
import ch.enterag.utils.io.*;
import ch.enterag.utils.lang.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.cmd.*;

/*====================================================================*/
/** User properties contains the user's properties in a property
    file. Properties that are not defined, are returned as null.
 @author Hartwig Thomas
 */
@SuppressWarnings("serial")
public class UserProperties extends Properties
{
  /*===================================================================
  (private) members
  ====================================================================*/
	/** singleton */
	private static UserProperties _up = null;
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(UserProperties.class.getName());
  /** properties file */
  private File _file = null;
  public File getFile() { return _file; }
  /** file name */
  // private String _sFilename = null;
  // public String getFilename() { return _sFilename; }
  /** application name */
  private String _sApplicationName = null;
  public String getApplicationName() { return _sApplicationName; }

  /*------------------------------------------------------------------*/
  /** sets the file name based on the application name.
   * @param sApplicationName name of application.
   */
  private void setApplicationName(String sApplicationName)
  {
    _sApplicationName = sApplicationName;
    _file = new File(SpecialFolder.getUserJavaSettings() + File.separator+sApplicationName+".properties");
  } /* setApplicationName */
  
  /*------------------------------------------------------------------*/
  /** constructor sets the file name to &lt;user.home&gt;/&lt;application&gt;/user.properties
   * @param sApplicationName name of the application to be used for the
   *                         application directory.
  */
  private UserProperties(String sApplicationName)
  {
  	super();
  	setApplicationName(sApplicationName.toLowerCase().replace(' ','_'));
  } /* constructor UserProperties */
  
  /*------------------------------------------------------------------*/
  /** returns singleton UserProperties.
  @return singleton instance of UserProperties.
  */
  public static UserProperties getUserProperties()
  {
    if (_up == null)
      _up = new UserProperties(SiardGui.getApplication()+" "+Archive.sMETA_DATA_VERSION);
    return _up;
  } /* getInstance */
  
  /*====================================================================
  public methods
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** loads singleton UserProperties from &lt;user.home&gt;/.java/SiardEdit/user.properties
  and returns success of loading operation.
  @return true, if successfully loaded.
  */
  public boolean load()
  {
  	_il.enter();
  	boolean bSuccess = false;
    /* try to open file with user properties */
  	try
  	{
      _il.event("Loading preferences from "+_file.getAbsolutePath());
  	  FileInputStream fis = new FileInputStream(_file);
  	  _up.load(fis);
  	  fis.close();
  	  bSuccess = true;
  	}
  	catch(FileNotFoundException fnfe)
  	{ 
  		_il.exception(fnfe);
    	_il.event("file "+_file.getAbsolutePath()+" does not exist!");
 		}
  	catch(IOException ie) { _il.exception(ie); }
  	_il.exit(String.valueOf(bSuccess));
  	return bSuccess;
  } /* load */
  
  /*------------------------------------------------------------------*/
  /** deletes the UserProperties file.
  @return true, if successfully deleted.
  */
  public boolean delete()
  {
  	_il.enter();
  	boolean bSuccess = false;
  	if (_file != null)
  	{
      _file.delete();
  		if (!_file.exists())
  		{
  		  _file = null;
  		  bSuccess = true;
  		}
  	}
  	else
  		bSuccess = true;
  	_il.exit(String.valueOf(bSuccess));
  	return bSuccess;
  } /* delete */
  
  /*------------------------------------------------------------------*/
  /** stores singleton UserProperties.
  @return true, if successfully stored.
  */
  public boolean store()
  {
  	_il.enter();
  	boolean bSuccess = false;
  	if (_file != null)
  	{
      /* try to store user properties in file */
    	try
    	{
        _il.event("Storing preferences to "+_file.getAbsolutePath());
    	  FileOutputStream fos = new FileOutputStream(_file);
    	  store(fos,null);
    	  fos.close();
    	  bSuccess = true;
    	}
    	catch(FileNotFoundException fnfe)
    	{ _il.exception(fnfe); }
    	catch(IOException ie)
    	{ _il.exception(ie); }
  	}
  	else
  		bSuccess = true;
  	_il.exit();
  	return bSuccess;
  } /* store */
  
  /*------------------------------------------------------------------*/
  /** get string property.
   * @param sKey property key.
   * @param sDefault default value.
   * @return property value.
   */
  private String getString(String sKey, String sDefault)
  {
    String sValue = getProperty(sKey);
    if (sValue == null)
      sValue = sDefault;
    return sValue;
  } /* getString */
  /*------------------------------------------------------------------*/
  /** set string property.
   * @param sKey property key.
   * @param sValue property value.
   */
  private void setString(String sKey, String sValue)
  {
    if (sValue != null)
      setProperty(sKey, sValue);
    else
      remove(sKey);
  } /* setString */
  
  /*------------------------------------------------------------------*/
  /** get boolean property.
   * @param sKey property key.
   * @param bDefault default value.
   * @return property value.
   */
  private boolean getBoolean(String sKey, boolean bDefault)
  {
    boolean bValue = bDefault;
    String sValue = getProperty(sKey);
    if (sValue != null)
      bValue = Boolean.parseBoolean(sValue);
    return bValue;
  } /* getBoolean */
  /*------------------------------------------------------------------*/
  /** set boolean property.
   * @param sKey property key.
   * @param bValue property value.
   */
  private void setBoolean(String sKey, boolean bValue)
  {
    setProperty(sKey,Boolean.toString(bValue));
  } /* setBoolean */
  
  /*------------------------------------------------------------------*/
  /** get double property.
   * @param sKey property key.
   * @param dDefault default value.
   * @return property value.
   */
  private double getDouble(String sKey, double dDefault)
  {
    double dValue = dDefault;
    String sValue = getProperty(sKey);
    if (sValue != null)
      dValue = Double.parseDouble(sValue);
    return dValue;
  } /* getDouble */
  /*------------------------------------------------------------------*/
  /** set double property.
   * @param sKey property key.
   * @param dValue property value.
   */
  private void setDouble(String sKey, double dValue)
  {
    setProperty(sKey,Double.toString(dValue));
  } /* setDouble */
  
  /*------------------------------------------------------------------*/
  /** get int property.
   * @param sKey property key.
   * @param iDefault default value.
   * @return property value.
   */
  private int getInt(String sKey, int iDefault)
  {
    int iValue = iDefault;
    String sValue = getProperty(sKey);
    if (sValue != null)
      iValue = Integer.parseInt(sValue);
    return iValue;
  } /* getInt */
  /*------------------------------------------------------------------*/
  /** set int property.
   * @param sKey property key.
   * @param iValue property value.
   */
  private void setInt(String sKey, int iValue)
  {
    setProperty(sKey,Integer.toString(iValue));
  } /* setInt */
  
  /*------------------------------------------------------------------*/
  /** get file property.
   * @param sKey property key.
   * @param fileDefault default value.
   * @return property value.
   */
  private File getFile(String sKey, File fileDefault)
  {
    File fileValue = fileDefault;
    String sValue = getProperty(sKey);
    if (sValue != null)
      fileValue = new File(sValue);;
    return fileValue;
  } /* getFile */
  /*------------------------------------------------------------------*/
  /** set file property.
   * @param sKey property key.
   * @param fileValue property value.
   */
  private void setFile(String sKey, File fileValue)
  {
    if (fileValue != null)
      setProperty(sKey, fileValue.getAbsolutePath());
    else
      remove(sKey);
  } /* setFile */
  
  /*====================================================================
  convenience methods for accessing all fields
  ====================================================================*/
  /* properties */
  private static final String sUI_LANGUAGE_KEY = "ui.language";
  public String getUiLanguage(String sDefault) { return getString(sUI_LANGUAGE_KEY,sDefault);  }
  public void setUiLanguage(String sLanguage) { setString(sUI_LANGUAGE_KEY,sLanguage); }
  
  private static final String sSTAGE_MAXIMIZED_KEY = "stage.maximized";
  public boolean getStageMaximized(boolean bDefault) { return getBoolean(sSTAGE_MAXIMIZED_KEY, bDefault); }
  public void setStageMaximized(boolean bStageMaximized) { setBoolean(sSTAGE_MAXIMIZED_KEY, bStageMaximized); }

  private static final String sSTAGE_MINIMIZED_KEY = "stage.minimized";
  public boolean getStageMinimized(boolean bDefault) { return getBoolean(sSTAGE_MINIMIZED_KEY, bDefault); }
  public void setStageMinimized(boolean bStageMinimized) { setBoolean(sSTAGE_MINIMIZED_KEY, bStageMinimized); }
  
  private static final String sSTAGE_X_KEY = "stage.x";
  public double getStageX(double dDefault) { return getDouble(sSTAGE_X_KEY,dDefault); }
  public void setStageX(double dStageX) { setDouble(sSTAGE_X_KEY, dStageX); }
  
  private static final String sSTAGE_Y_KEY = "stage.y";
  public double getStageY(double dDefault) { return getDouble(sSTAGE_Y_KEY,dDefault); }
  public void setStageY(double dStageY) { setDouble(sSTAGE_Y_KEY, dStageY); }

  private static final String sSTAGE_WIDTH_KEY = "stage.width";
  public double getStageWidth(double dDefault) { return getDouble(sSTAGE_WIDTH_KEY,dDefault); }
  public void setStageWidth(double dStageWidth) { setDouble(sSTAGE_WIDTH_KEY, dStageWidth); }
  
  private static final String sSTAGE_HEIGHT_KEY = "stage.height";
  public double getStageHeight(double dDefault) { return getDouble(sSTAGE_HEIGHT_KEY,dDefault); }
  public void setStageHeight(double dStageHeight) { setDouble(sSTAGE_HEIGHT_KEY, dStageHeight); }
  
  private static final String sINSTALLED_PATH_KEY = "installed.path";
  public File getInstalledPath(File fileDefault) { return getFile(sINSTALLED_PATH_KEY,fileDefault); }
  public void setInstalledPath(File fileInstalledPath) { setFile(sINSTALLED_PATH_KEY,fileInstalledPath); }
  
  private static final String sINSTALLED_VERSION_KEY = "installed.version";
  public String getInstalledVersion(String sDefault) { return getString(sINSTALLED_VERSION_KEY,sDefault); }
  public void setInstalledVersion(String sInstalledVersion) { setString(sINSTALLED_VERSION_KEY,sInstalledVersion); }
  
  private static final String sFIND_STRING_KEY = "find.string";
  public String getFindString() { return getString(sFIND_STRING_KEY, null); }
  public void setFindString(String sFindString) { setString(sFIND_STRING_KEY, sFindString); }
  
  private static final String sFIND_MATCH_CASE_KEY = "find.match.case";
  public boolean getFindMatchCase() { return getBoolean(sFIND_MATCH_CASE_KEY, false); }
  public void setFindMatchCase(boolean bMatchCase) { setBoolean(sFIND_MATCH_CASE_KEY, bMatchCase); }
  
  private static final String sSEARCH_STRING_KEY = "search.string";
  public String getSearchString() { return getString(sSEARCH_STRING_KEY, null); }
  public void setSearchString(String sSearchString) { setString(sSEARCH_STRING_KEY, sSearchString); }
  
  private static final String sSEARCH_MATCH_CASE_KEY = "search.match.case";
  public boolean getSearchMatchCase() { return getBoolean(sSEARCH_MATCH_CASE_KEY, false); }
  public void setSearchMatchCase(boolean bMatchCase) { setBoolean(sSEARCH_MATCH_CASE_KEY, bMatchCase); }
  
  private static final String sDATABASE_HOST_KEY = "database.host";
  private static final String sDATABASE_HOST = "dbserver.enterag.ch";
  public String getDatabaseHost() { return getString(sDATABASE_HOST_KEY,sDATABASE_HOST); }
  public void setDatabaseHost(String sDatabaseHost) { setString(sDATABASE_HOST_KEY, sDatabaseHost); }

  private static final String sDATABASE_NAME_KEY = "database.name";
  private static final String sDATABASE_NAME = "testdb";
  public String getDatabaseName() { return getString(sDATABASE_NAME_KEY,sDATABASE_NAME); }
  public void setDatabaseName(String sDatabaseName) { setString(sDATABASE_NAME_KEY, sDatabaseName); }
  
  private static final String sDATABASE_FOLDER_KEY = "database.folder";
  private static final String sDATABASE_FOLDER = "db";
  public File getDatabaseFolder()
  {
    _il.enter();
    File fileDbFolder = new File(SiardGui.getDefaultDataDirectory().getAbsolutePath() + 
        File.separator+sDATABASE_FOLDER);
    fileDbFolder = getFile(sDATABASE_FOLDER_KEY, fileDbFolder);
    fileDbFolder.mkdirs();
    _il.exit(fileDbFolder);
    return fileDbFolder;
  } /* getDatabaseFolder */
  public void setDatabaseFolder(File fileDbFolder)
  {
    setFile(sDATABASE_FOLDER_KEY, fileDbFolder);
  } /* setDatabaseFolder */

  private static final String sTEXT_EDITOR_KEY = "installed.texteditor";
  private static final File fileWINDOWS_EDITOR = new File("notepad.exe");
  private static final File fileLINUX_EDITOR = new File("gedit");
  private static final File fileTEXT_EDITOR = new File("texteditor"); 
  public File getTextEditor()
  {
  	_il.enter();
  	File fileTextEditor = getFile(sTEXT_EDITOR_KEY,fileTEXT_EDITOR);
    if (Execute.isOsWindows())
      fileTextEditor = getFile(sTEXT_EDITOR_KEY,fileWINDOWS_EDITOR);
  	else if (Execute.isOsLinux())
      fileTextEditor = getFile(sTEXT_EDITOR_KEY,fileLINUX_EDITOR);
  	_il.exit(fileTextEditor);
  	return  fileTextEditor;
 	} /* getTextEditor */
  public void setTextEditor(File fileTextEditor) { setFile(sTEXT_EDITOR_KEY,fileTextEditor); }
  
  private static final String sBIN_EDITOR_KEY = "installed.bineditor";
  private static final String sHXD_FILE = "hxd"+File.separator+"HxD.exe";
  private static final File fileLINUX_BINEDITOR = new File("ghex");
  private static final File fileBIN_EDITOR = new File("bineditor"); 
  public File getBinEditor()
  {
  	_il.enter();
    File fileBinEditor = getFile(sBIN_EDITOR_KEY,fileBIN_EDITOR);
    if (Execute.isOsWindows())
    {
      File fileHxd = SpecialFolder.getMainJar();
      if (fileHxd.isFile())
        fileHxd = fileHxd.getParentFile();
      fileHxd = new File(fileHxd.getParentFile().getAbsolutePath() +
        File.separator + sHXD_FILE);
      fileBinEditor = getFile(sBIN_EDITOR_KEY,fileHxd);
    }
    else if (Execute.isOsLinux())
      fileBinEditor = getFile(sBIN_EDITOR_KEY,fileLINUX_BINEDITOR);
  	_il.exit(fileBinEditor);
  	return  fileBinEditor;
 	}
  public void setBinEditor(File fileBinEditor) { setFile(sBIN_EDITOR_KEY,fileBinEditor); }
  
  private static final String sSPLASH_MS_KEY = "splash.ms";
  public int getSplashMs(int iDefault) { return getInt(sSPLASH_MS_KEY,iDefault); }
  public void setSplashMs(int iSplashMs) { setInt(sSPLASH_MS_KEY, iSplashMs); }
  
  private static final String sLOGIN_TIMEOUT_SECONDS_KEY ="login.timeout.seconds";
  public int getLoginTimeoutSeconds() { return getInt(sLOGIN_TIMEOUT_SECONDS_KEY,SiardConnection.iDEFAULT_LOGIN_TIMEOUT_SECONDS); }
  public void setLoginTimeoutSeconds(int iLoginTimeoutSeconds) { setInt(sLOGIN_TIMEOUT_SECONDS_KEY,iLoginTimeoutSeconds); }

  private static final String sQUERY_TIMEOUT_SECONDS_KEY ="query.timeout.seconds";
  public int getQueryTimeoutSeconds() { return getInt(sQUERY_TIMEOUT_SECONDS_KEY,SiardConnection.iDEFAULT_QUERY_TIMEOUT_SECONDS); }
  public void setQueryTimeoutSeconds(int iQueryTimeoutSeconds) { setInt(sQUERY_TIMEOUT_SECONDS_KEY,iQueryTimeoutSeconds); }

  private static final String sCOLUMN_WIDTH_KEY = "column.width";
  private static final int iCOLUMN_WIDTH = 10;
  public int getColumnWidth() { return getInt(sCOLUMN_WIDTH_KEY,iCOLUMN_WIDTH); }
  public void setColumnWidth(int iColumnWidth) { setInt(sCOLUMN_WIDTH_KEY,iColumnWidth); }
  
  private static final String sCONFIG_FOLDER_KEY = "config.folder";
  private static final String sCONFIG_FOLDER = "etc";
  public File getConfigFolder()
  {
    _il.enter();
    File fileConfigFolder = SpecialFolder.getMainJar();
    if (fileConfigFolder.isFile())
      fileConfigFolder = fileConfigFolder.getParentFile();
    fileConfigFolder = new File(fileConfigFolder.getParentFile().getAbsolutePath() +
      File.separator + sCONFIG_FOLDER);
    fileConfigFolder = getFile(sCONFIG_FOLDER_KEY,fileConfigFolder);
    _il.exit(fileConfigFolder);
    return fileConfigFolder;
  } /* getConfigFolder */
  
  private static final String sLOBS_FOLDER_KEY = "lobs.folder";
  private static final String sLOBS_FOLDER = "lobs";
  public File getLobsFolder()
  {
    _il.enter();
    File fileLobsFolder = new File(SiardGui.getDefaultDataDirectory().getAbsolutePath() + 
        File.separator+sLOBS_FOLDER);
    fileLobsFolder = getFile(sLOBS_FOLDER_KEY, fileLobsFolder);
    fileLobsFolder.getParentFile().mkdirs();
    _il.exit(fileLobsFolder);
    return fileLobsFolder;
  } /* getLobsFolder */
  public void setLobsFolder(File fileLobsFolder)
  {
    setFile(sLOBS_FOLDER_KEY, fileLobsFolder);
  } /* setLobsFolder */
  
  private static final String sXSL_FILE_KEY = "xsl.file";
  private static final String sXSL_FILE = "metadata.xsl";
  public File getXslFile()
  {
    File fileXsl = new File(getConfigFolder().getAbsoluteFile()+File.separator+sXSL_FILE);
    fileXsl = getFile(sXSL_FILE_KEY, fileXsl);
    return fileXsl;
  } /* getXslFile */
  public void setXslFile(File fileXsl)
  {
    setFile(sXSL_FILE_KEY,fileXsl);
  } /* setXslFile */
  
  /*------------------------------------------------------------------*/
  /** get one file of the the list of most recently used files.
   @param iIndex Index of the connection to be returned. 
   @return file path as string or empty string if not found.
   */
  public String getMruFile(int iIndex)
  {
  	_il.enter(String.valueOf(iIndex));
  	String sFile = _up.getProperty("mru.file.entry"+iIndex, "");
  	_il.exit(sFile);
  	return sFile;
  } /* getMruFile */
  
  /*------------------------------------------------------------------*/
  /** sets one file of the the list of most recently used files.
   @param iIndex Index of the connection to be set. 
   @param sFile file path as string.
   */
  public void setMruFile(int iIndex, String sFile)
  {
  	_il.enter(String.valueOf(iIndex),sFile);
  	if (iIndex >= 0 && iIndex < (MruFile.iNUM_FILES + 1))
		  _up.setProperty("mru.file.entry"+iIndex, sFile);
  	else 
   		_il.event("Invalid index: \""+iIndex+"\"!");
  	_il.exit();
  } /* setMruFile */

  /*------------------------------------------------------------------*/
  /** get one connection of the the list of most recently used connections
   @param bDownload true for download connections, false for upload connections.
   @param iIndex Index of the connection to be returned 
   @return Connection as string of format [JDBC URL]\t[DatabaseUser] 
     or empty string if not found
   */
  public String getMruConnection(boolean bDownload, int iIndex)
  {
  	_il.enter(String.valueOf(iIndex));
    String sDirection = "up";
    if (bDownload)
      sDirection = "down";
  	String sConnection = _up.getProperty("mru.db"+sDirection+".entry"+iIndex, "");
   	/* check plausibility - does the sConnection have format [JDBC URL]\t[DatabaseUser]? */
   	StringTokenizer stConnectionData = new StringTokenizer(sConnection, "\t");
   	if (!(sConnection.length() > 0 && stConnectionData.countTokens() == 2))
   	{
   		_il.event("Invalid connection: \""+sConnection+"\"!");
   		sConnection = "";
   	}
  	_il.exit(sConnection);
  	return sConnection;
  } /* getMruConnection */
  
  /*------------------------------------------------------------------*/
  /** sets one connection of the the list of most recently used connections
   @param bDownload true for download connections, false for upload connections.
   @param iIndex Index of the connection to be set 
   @param sConnection connection ([JDBC URL]\t[DatabaseUser].
   */
  public void setMruConnection(boolean bDownload, int iIndex, String sConnection)
  {
  	_il.enter(String.valueOf(iIndex),sConnection);
    String sDirection = "up";
    if (bDownload)
      sDirection = "down";
  	if (iIndex >= 0 && iIndex < MruConnection.iNUM_CONNECTIONS+1)
  		_up.setProperty("mru.db"+sDirection+".entry"+iIndex, sConnection);
  	else 
   		_il.event("Invalid index: \""+iIndex+"\"!");
  	_il.exit();
  } /* setMruConnection */

} /* UserProperties */
