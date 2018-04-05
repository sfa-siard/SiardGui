/*======================================================================
DesktopWindows implements the creation of desktop links for Windows.
Application : Utilities
Description : DesktopWindows implements the creation of desktop links for Windows.
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland, 2018
Created    : 31.01.2018, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.desktop;

import java.io.*;
import java.util.*;

import ch.enterag.utils.EU;
import mslinks.*;

/*====================================================================*/
/** DesktopWindows implements the creation of desktop links for Windows.
 * See: https://msdn.microsoft.com/en-us/library/dd871305.aspx
 * @author Hartwig Thomas
 */
public class DesktopWindows
  extends Desktop
{
  private ShellLink _sl = null;
  
  /*------------------------------------------------------------------*/
  /** executable to be run when activating the desktop link.
   * @return executable.
   */
  public File getExecutable()
  {
    File fileExecutable = null;
    if (_sl != null)
    {
      String sTarget = _sl.resolveTarget();
      if (!sTarget.equals("<unknown>"))
        fileExecutable = new File(sTarget);
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
    if (_sl != null)
    {
      String sArguments = _sl.getCMDArgs();
      if (sArguments != null)
        listArguments = parseArguments(sArguments);
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
    if (_sl != null)
    {
      String sWorkingDir = _sl.getWorkingDir();
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
    if (_sl != null)
    {
      String sIcon = _sl.getIconLocation();
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
    if (_sl != null)
      sDescription = _sl.getName();
    return sDescription;
  } /* getDescription */
  
  
  /*------------------------------------------------------------------*/
  /** Constructor
   */
  public DesktopWindows()
  {
    super();
    cCOMMAND_LINE_ESCAPE = '^';
    sLINK_EXTENSION = ".lnk";
  } /* Desktop */

  /*------------------------------------------------------------------*/
  /** factory
   * @return new Desktop instance.
   */
  public static DesktopWindows newInstance()
  {
    return new DesktopWindows();
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
  protected void writeLink(File fileLink, File fileExecutable,
    List<String> listArguments, File fileWorkingDir,
    File fileIcon, String sDescription)
    throws IOException
  {
    _il.enter(fileLink);
    _sl = ShellLink.createLink(fileExecutable.getAbsolutePath());
    if (listArguments.size() > 0)
      _sl.setCMDArgs(formatArguments(listArguments));
    if (fileWorkingDir != null)
      _sl.setWorkingDir(fileWorkingDir.getAbsolutePath());
    if (fileIcon != null)
      _sl.setIconLocation(fileIcon.getAbsolutePath());
    if (sDescription != null)
      _sl.setName(sDescription);
    _sl.saveTo(fileLink.getAbsolutePath());
    _il.exit();
  } /* writeLink */
  
  /*------------------------------------------------------------------*/
  /** read and parse the desktop link.
   * @param fileLink desktop link file.
   */
  public void readLink(File fileLink)
    throws IOException
  {
    _il.enter(fileLink);
    try { _sl = new ShellLink(fileLink); }
    catch(ShellLinkException sle) { throw new IOException(EU.getExceptionMessage(sle)); }
    _il.exit();
  } /* readLink */

} /* class Desktop */
