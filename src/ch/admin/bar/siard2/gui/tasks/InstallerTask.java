/*====================================================================== 
InstallerTask executes the SIARD Suite installation on a "worker" thread. 
Application : JavaFX Utilities
Description: InstallerTask executes the SIARD Suite installation on a 
             "worker" thread.
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland
Created    : 09.06.2017, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.gui.tasks;

import java.io.*;
import java.util.*;
import javafx.concurrent.*;
import javafx.event.*;
import ch.enterag.utils.*;
import ch.enterag.utils.desktop.*;
import ch.enterag.utils.io.*;
import ch.enterag.utils.lang.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.gui.actions.*;

/*====================================================================*/
/** InstallerTask executes the SIARD Suite installation on a "worker" thread.
 * @author Hartwig Thomas
 */
public class InstallerTask
  extends Task<Boolean>
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(InstallerTask.class.getName());
  private final File _folderSource;
  private final File _folderInstallation;
  public File getInstallationFolder() { return _folderInstallation; }
  private int _iDesktopResult = 0;
  public int getDesktopResult() { return _iDesktopResult; }
  private String _sDesktopError = null;
  public String getDesktopError() { return _sDesktopError; }

  /*------------------------------------------------------------------*/
  /** copy the contents of a folder to another folder, overwriting
   * files and folders with the same name.
   * @param folderSource
   * @param folderTarget
   * @return true, if all files and folders could be copied.
   */
  private boolean copyFolder(File folderSource, File folderTarget)
  {
    _il.enter(folderSource,folderTarget);
    boolean bCopied = true;
    if (!folderTarget.exists())
      bCopied = folderTarget.mkdirs();
    File[] afile = folderSource.listFiles();
    for (int i = 0; bCopied && (i < afile.length); i++)
    {
      File fileSource = afile[i];
      File fileTarget = new File(folderTarget.getAbsolutePath() + File.separator + fileSource.getName());
      if (fileSource.isDirectory())
        bCopied = copyFolder(fileSource,fileTarget);
      else
      {
        try { FU.copy(fileSource, fileTarget); }
        catch(IOException ie) { bCopied = false; }
      }
    }
    _il.exit(String.valueOf(bCopied));
    return bCopied;
  } /* copyFolder */

  /*------------------------------------------------------------------*/
  /** make shell script executable.
   * @param folderInstallation location of shell script.
   * @param sShellScript name of shell script.
   */
  private boolean setExecutable(File folderInstallation, String sShellScript)
  {
    File file = new File(folderInstallation.getAbsolutePath()+File.separator+sShellScript);
    return file.setExecutable(true);
  } /* setExecutable */

  /*------------------------------------------------------------------*/
  /** actual installation running on worker thread.
   */
  @Override
  protected Boolean call() throws Exception
  {
    _il.enter();
    /* copy all files from folderSource to folderInstallation */
    boolean bInJar = false;
    if (SpecialFolder.getMainJar().isFile())
      bInJar = true;
    boolean bInstalled = true;
    if (bInJar)
      bInstalled = copyFolder(_folderSource,_folderInstallation);
    else
      _il.severe("If run from JAR the SIARD Suite would now be copied from \""+_folderSource.getAbsolutePath()+"\" to \""+_folderInstallation.getAbsolutePath()+"\"");
    if (bInstalled)
    {
      _il.event("Files copied from "+_folderSource.getAbsolutePath()+" to "+_folderInstallation.getAbsolutePath());
      /* make .sh files executable */
      setExecutable(_folderInstallation,"siardgui.sh");
      setExecutable(_folderInstallation,"siardfromdb.sh");
      setExecutable(_folderInstallation,"siardtodb.sh");
      _il.event(".sh files were set executable");
      /* desktop */
      String sJavaExecutable = "java";
      if (Execute.isOsWindows())
        sJavaExecutable = "javaw.exe";
      String sJavaHome = System.getProperty("java.home");
      File fileExecutable = new File(sJavaHome + File.separator + "bin"+File.separator+sJavaExecutable);
      String sAppFolder = _folderInstallation.getAbsolutePath();
      List<String> listArguments = new ArrayList<String>(Arrays.asList(new String[] {
          "-Xmx1024m",
          "-Dsun.awt.disablegrab=true",
          "-Djava.util.logging.config.file="+sAppFolder+File.separator+"etc"+File.separator+"logging.properties",
          "-jar",
          sAppFolder+File.separator+"lib"+File.separator+"siardgui.jar"
        }));
      File fileIcon = new File(sAppFolder+File.separator+"siardgui.ico");
      String sDescription = "SiardGui for viewing and modifying archived data from relational databases";
      try
      {
        _il.event("Creating Link "+InstallUninstallHandler.sDESKTOP_NAME);
        _il.event(" Executable: "+fileExecutable.getAbsolutePath());
        _il.event(" Arguments: "+Desktop.formatArguments(listArguments));
        _il.event(" Working Directory: "+_folderInstallation.getAbsolutePath());
        _il.event(" Icon: "+fileIcon.getAbsolutePath());
        _il.event(" Description: "+sDescription);
        Desktop dt = Desktop.newInstance();
        _il.event(" Desktop Instance: "+String.valueOf(dt));
        dt.createLink(InstallUninstallHandler.sDESKTOP_NAME, 
          fileExecutable, listArguments, _folderInstallation, fileIcon, sDescription);
        _il.event("Link created");
      }
      catch(IOException ie) 
      {
        _il.exception(ie);
        _sDesktopError = EU.getExceptionMessage(ie);
        _iDesktopResult = -1;
      }
      catch(RuntimeException re)
      {
        _il.exception(re);
        _sDesktopError = EU.getExceptionMessage(re);
        _iDesktopResult = -2;
      }
      catch(Exception e)
      {
        _il.exception(e);
        _sDesktopError = EU.getExceptionMessage(e);
        _iDesktopResult = -3;
      }
    }
    _il.exit(String.valueOf(bInstalled));
    return Boolean.valueOf(bInstalled);
  } /* call */

  /*------------------------------------------------------------------*/
  /** constructor.
   * @param folderSource source folder for SIARD Suite files.
   * @param folderInstallation targete folder for copy of SIARD Suite files.
   * @param ehFinished event handler for finished event.
   */
  private InstallerTask(File folderSource, File folderInstallation,
    EventHandler<WorkerStateEvent> ehFinished)
  {
    super();
    setOnSucceeded(ehFinished);
    setOnFailed(ehFinished);
    setOnCancelled(ehFinished);
    _folderSource = folderSource;
    _folderInstallation = folderInstallation;
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** executes the installation in a background thread and fires event, 
   * when finished.
   * @param folderSource source folder for SIARD Suite files.
   * @param folderInstallation targete folder for copy of SIARD Suite files.
   * @param ehFinished event handler for finished event.
   */
  public static void installTask(File folderSource, File folderInstallation,
    EventHandler<WorkerStateEvent> ehFinished)
  {
    _il.enter(folderSource,folderInstallation);
    InstallerTask it = new InstallerTask(folderSource, folderInstallation, 
      ehFinished);
    Thread thread = new Thread(it);
    thread.setDaemon(true);
    thread.start();
    _il.exit();
  } /* installTask */
  
} /* InstallerTask */
