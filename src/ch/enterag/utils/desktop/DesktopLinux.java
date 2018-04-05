/*======================================================================
DesktopLinux implements the creation of desktop links for LINUX.
Application : Utilities
Description : DesktopLINUX implements the creation of desktop links for LINUX.
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland, 2018
Created    : 31.01.2018, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.desktop;

import java.io.*;
import java.util.*;
import org.ini4j.*;

/*====================================================================*/
/** DesktopLinux implements the creation of desktop links for Linux.
 * @author Hartwig Thomas
 */
public class DesktopLinux
  extends Desktop
{
  private Ini _ini = null;
  private static final String sDESKTOP_ENTRY = "Desktop Entry";
  private static final String sEXEC_KEY = "Exec";
  private static final String sPATH_KEY = "Path";
  private static final String sICON_KEY = "Icon";
  private static final String sCOMMENT_KEY = "Comment";
  
  /*------------------------------------------------------------------*/
  /** executable to be run when activating the desktop link.
   * @return executable.
   */
  public File getExecutable()
  {
    File fileExecutable = null;
    if (_ini != null)
    {
      String sExec = _ini.get(sDESKTOP_ENTRY,sEXEC_KEY,String.class);
      if (sExec != null)
        fileExecutable = new File(parseArguments(sExec).get(0));
    }
    return fileExecutable;
  } /* getExecutable */
  
  /*------------------------------------------------------------------*/
  /** command line arguments to be used when running the executable.
   * @return command line arguments.
   */
  public List<String> getArguments()
  {
    List<String> listArguments = new ArrayList<String>();
    if (_ini != null)
    {
      String sExec = _ini.get(sDESKTOP_ENTRY,sEXEC_KEY,String.class);
      if (sExec != null)
      {
        listArguments = parseArguments(sExec);
        listArguments.remove(0);
      }
    }
    return listArguments;
  } /* getArguments */
  
  /*------------------------------------------------------------------*/
  /** working directory for execution.
   * @return working directory.
   */
  public File getWorkingDir()
  {
    File fileWorkingDir = null;
    if (_ini != null)
    {
      String sWorkingDir = _ini.get(sDESKTOP_ENTRY,sPATH_KEY,String.class);
      if (sWorkingDir != null)
        fileWorkingDir = new File(sWorkingDir);
    }
    return fileWorkingDir;
  } /* getWorkingDir */
  
  /*------------------------------------------------------------------*/
  /** icon displayed on the desktop for link.
   * @return icon file.
   */
  public File getIcon()
  {
    File fileIcon = null;
    if (_ini != null)
    {
      String sIcon = _ini.get(sDESKTOP_ENTRY, sICON_KEY,String.class);
      if (sIcon != null)
        fileIcon = new File(sIcon);
    }
    return fileIcon; 
  } /* getIcon */

  /*------------------------------------------------------------------*/
  /** description of desktop link.
   * @return description.
   */
  public String getDescription()
  {
    String sDescription = null;
    if (_ini != null)
      sDescription = _ini.get(sDESKTOP_ENTRY,sCOMMENT_KEY); 
    return sDescription;
  } /* getDescription */

  /*------------------------------------------------------------------*/
  /** Constructor
   */
  public DesktopLinux()
  {
    super();
    cCOMMAND_LINE_ESCAPE = '\\';
    sLINK_EXTENSION = ".desktop";
  } /* Desktop */

  /*------------------------------------------------------------------*/
  /** factory
   * @return new Desktop instance.
   */
  public static DesktopLinux newInstance()
  {
    return new DesktopLinux();
  } /* newInstance */
  
  /*------------------------------------------------------------------*/
  /** write the desktop link.
   * @param fileLink desktop link file.
   * @param fileExecutable executable to be run, when link is activated 
   *   (must not be null!).
   * @param listArguments list of arguments to be used for running the 
   *   executable (must not be null! Use empty list for no arguments!)
   * @param fileWorkingDir working directory for running the executable
   *   (may be null).
   * @param fileIcon file name for icon to be displayed (may be null).
   * @param sDescription description of link (may be null).
   */
  protected void writeLink(File fileLink, 
    File fileExecutable, List<String> listArguments, File fileWorkingDir,
    File fileIcon, String sDescription)
    throws IOException
  {
    _ini = new Ini();
    _ini.putComment(null, "#!/usr/bin/env xdg-open");
    _ini.put(sDESKTOP_ENTRY, "Version", "1.0");
    _ini.put(sDESKTOP_ENTRY, "Encoding", "UTF-8");
    _ini.put(sDESKTOP_ENTRY, "Type", "Application");
    _ini.put(sDESKTOP_ENTRY, "Category", "Utility");
    String sName = fileLink.getName();
    if (sName.endsWith(sLINK_EXTENSION))
      sName = sName.substring(0,sName.length()-sLINK_EXTENSION.length());
    _ini.put(sDESKTOP_ENTRY, "Name", sName);
    List<String> listExec = new ArrayList<String>(listArguments);
    listExec.add(0, fileExecutable.getAbsolutePath());
    String sExec = formatArguments(listExec);
    _ini.put(sDESKTOP_ENTRY, sEXEC_KEY, sExec);
    if (fileWorkingDir != null)
      _ini.put(sDESKTOP_ENTRY, sPATH_KEY, fileWorkingDir.getAbsolutePath());
    if (fileIcon != null)
      _ini.put(sDESKTOP_ENTRY, sICON_KEY, fileIcon.getAbsolutePath());
    if (sDescription != null)
      _ini.put(sDESKTOP_ENTRY,  sCOMMENT_KEY, sDescription);
    _ini.store(fileLink);
    fileLink.setExecutable(true);
  } /* writeLink */
  
  /*------------------------------------------------------------------*/
  /** read and parse the desktop link.
   * @param fileLink desktop link file.
   */
  public void readLink(File fileLink)
    throws IOException
  {
    _ini = new Ini(fileLink);
    _ini.load();
  } /* readLink */

} /* class Desktop */
