/*======================================================================
Desktop implements the creation of desktop links.
Application : Utilities
Description : Desktop implements the creation of desktop links.
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland, 2018
Created    : 31.01.2018, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.desktop;

import java.io.*;
import java.util.*;

import ch.admin.bar.siard2.gui.tasks.InstallerTask;
import ch.enterag.utils.io.SpecialFolder;
import ch.enterag.utils.lang.*;
import ch.enterag.utils.logging.IndentLogger;

/*====================================================================*/
/** Desktop implements the creation of desktop links.
 * The abstract class defines a factory and the basic interface.
 * @author Hartwig
 */
public abstract class Desktop
{
  /** logger */  
  protected static IndentLogger _il = IndentLogger.getIndentLogger(InstallerTask.class.getName());
  
  /* These must be set by derived classes! */
  protected static char cCOMMAND_LINE_ESCAPE;
  protected static String sLINK_EXTENSION;
  
  /*------------------------------------------------------------------*/
  /** executable to be run when activating the desktop link.
   * @return executable.
   */
  public abstract File getExecutable();

  /*------------------------------------------------------------------*/
  /** command line arguments to be used when running the executable.
   * @return command line arguments.
   */
  public abstract List<String> getArguments();

  /*------------------------------------------------------------------*/
  /** working directory for execution.
   * @return working directory.
   */
  public abstract File getWorkingDir();

  /*------------------------------------------------------------------*/
  /** icon displayed on the desktop for link.
   * @return icon file.
   */
  public abstract File getIcon();

  /*------------------------------------------------------------------*/
  /** description of desktop link.
   * @return description.
   */
  public abstract String getDescription();

  private File _fileFolder = null;
  /*------------------------------------------------------------------*/
  /** get folder for link file. 
   * @return folder for the link file (null for default).
   */
  public File getFolder() { return _fileFolder; }
  /*------------------------------------------------------------------*/
  /** fileFolder must be set before reading or writing a desktop entry,
   * if the link should be written to another location than the 
   * default location.
   * @param fileFolder folder for the link file.
   */
  public void setFolder(File fileFolder) { _fileFolder = fileFolder; }

  private File _fileLink = null;
  /*------------------------------------------------------------------*/
  /** get link file
   * @return link file.
   */
  public File getLink() { return _fileLink; }
  /*------------------------------------------------------------------*/
  /** set link file.
   * @param fileLink link file
   */
  private void setLink(File fileLink) { _fileLink = fileLink; }
  /*------------------------------------------------------------------*/
  /** link file from link name.
   * @param sLinkName
   * @return link file from default desktop location or folder property
   *   with platform-dependent default extension.
   */
  private File getLink(String sLinkName)
  {
    File fileFolder = getFolder();
    if (fileFolder == null)
      fileFolder = new File(SpecialFolder.getDesktopFolder());
    setLink(new File(fileFolder.getAbsolutePath()+File.separator+sLinkName+sLINK_EXTENSION));
    _il.event("Link: "+getLink());
    return getLink();
  } /* getLink */
  
  /*------------------------------------------------------------------*/
  /** Constructor
   */
  public Desktop()
  {
  } /* Desktop */

  /*------------------------------------------------------------------*/
  /** factory
   * @return new Desktop instance.
   * @throws RuntimeException if neither Windows nor LINUX.
   */
  public static Desktop newInstance()
  {
    Desktop dt = null;
    if (Execute.isOsWindows())
      dt = new DesktopWindows();
    else if (Execute.isOsLinux())
      dt =new DesktopLinux();
    else
      throw new RuntimeException("No desktop link on this OS!");
    return dt;
  } /* newInstance */
  
  /*------------------------------------------------------------------*/
  /** parse arguments removing quotes.
   * @param sArguments
   * For a completely complete syntax see https://ss64.com/nt/syntax-esc.html
   */
  public static List<String> parseArguments(String sArguments)
  {
    List<String> listArguments = new ArrayList<String>();
    boolean bInQuotes = false;
    StringBuilder sbArgument = new StringBuilder();
    for (int i = 0; i < sArguments.length(); i++)
    {
      char c = sArguments.charAt(i);
      if (c == cCOMMAND_LINE_ESCAPE)
      {
        if (i < sArguments.length()-1)
        {
          i++;
          sbArgument.append(sArguments.charAt(i));
        }
      }
      else if (c == '"')
      {
        if (bInQuotes)
        {
          if (sbArgument.length() > 0)
          {
            listArguments.add(sbArgument.toString());
            sbArgument.setLength(0);
          }
        }
        bInQuotes = !bInQuotes;
      }
      else if (Character.isWhitespace(c) && !bInQuotes)
      {
        if (sbArgument.length() > 0)
        {
          listArguments.add(sbArgument.toString());
          sbArgument.setLength(0);
        }
      }
      else
        sbArgument.append(c);
    }
    if (sbArgument.length() > 0)
      listArguments.add(sbArgument.toString());
    return listArguments;
  } /* parseArguments */
  
  /*------------------------------------------------------------------*/
  /** format arguments adding quotes and escapes as needed.
   * @param sArguments
   */
  public static String formatArguments(List<String> listArguments)
  {
    StringBuilder sbArguments = new StringBuilder();
    String sCommandLineEscape = Character.toString(cCOMMAND_LINE_ESCAPE);
    for (Iterator<String> iterArgument = listArguments.iterator(); iterArgument.hasNext(); )
    {
      if (sbArguments.length() > 0)
        sbArguments.append(" ");
      String sArgument = iterArgument.next();
      sArgument = sArgument.replaceAll("\\\"", sCommandLineEscape+"\"");
      sArgument = sArgument.replaceAll("\\"+sCommandLineEscape, sCommandLineEscape+sCommandLineEscape);
      if (sArgument.contains(" ") || sArgument.contains("\t"))
        sbArguments.append("\"");
      sbArguments.append(sArgument);
      if (sArgument.contains(" ") || sArgument.contains("\t"))
        sbArguments.append("\"");
    }
    return sbArguments.toString();
  } /* formatArguments */

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
  protected abstract void writeLink(File fileLink, 
    File fileExecutable, List<String> listArguments, File fileWorkingDir,
    File fileIcon, String sDescription)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** read and parse the desktop link.
   * @param fileLink desktop link file.
   */
  protected abstract void readLink(File fileLink)
    throws IOException;

  /*------------------------------------------------------------------*/
  /** read and parse the desktop link of the given name.
   * N.B.: If the link is to be read from another location than the
   * default location, the Folder property must have been set.
   * @param sLinkName link name without extension.
   * @throws IOException if the desktop link could not be read.
   */
  public void parseLink(String sLinkName)
    throws IOException
  {
    _il.enter(sLinkName);
    readLink(getLink(sLinkName));
    _il.exit();
  } /* parseLink */
  
  /*------------------------------------------------------------------*/
  /** create and write the desktop link of the given name using
   * the properties given.
   * N.B.: If the link is to be written to another location than the
   * default location, the Folder property must have been set.
   * @param sLinkName link name without extension.
   * @param fileExecutable executable to be run, when link is activated 
   *   (must not be null!).
   * @param listArguments list of arguments to be used for running the 
   *   executable (must not be null! Use empty list for no arguments!)
   * @param fileWorkingDir working directory for running the executable
   *   (may be null).
   * @param fileIcon file name for icon to be displayed (may be null).
   * @param sDescription description of link (may be null).
   * @throws IOException if an error occurred.
   */
  public void createLink(String sLinkName, 
    File fileExecutable, List<String> listArguments, File fileWorkingDir, 
    File fileIcon, String sDescription)
    throws IOException
  {
    _il.enter(sLinkName,fileExecutable,listArguments,fileWorkingDir,fileIcon,sDescription);
    writeLink(getLink(sLinkName),fileExecutable,listArguments,fileWorkingDir,
      fileIcon, sDescription);
    _il.exit();
  } /* createLink */
  
  /*------------------------------------------------------------------*/
  /** delete a desktop link.
   * N.B.: If the link is to be deleted resides in another location than 
   * the default location, the Folder property must have been set.
   * @param sLinkName link name without extension.
   * @throws IOException if link could not be deleted.
   */
  public void deleteLink(String sLinkName)
    throws IOException
  {
    _il.enter(sLinkName);
    getLink(sLinkName).delete();
    _il.exit();
  } /* deleteLink */
  
} /* class Desktop */
