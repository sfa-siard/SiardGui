/*====================================================================== 
UninstallerTask executes the SIARD Suite uninstallation on a "worker" thread. 
Application : JavaFX Utilities
Description: UninstallerTask executes the SIARD Suite uninstallation on 
             a "worker" thread.
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland
Created    : 09.06.2017, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.gui.tasks;

import java.io.*;

import javafx.concurrent.*;
import javafx.event.*;
import ch.enterag.utils.*;
import ch.enterag.utils.desktop.*;
import ch.enterag.utils.io.*;
import ch.enterag.utils.logging.IndentLogger;
import ch.admin.bar.siard2.gui.*;
import ch.admin.bar.siard2.gui.actions.*;

/*====================================================================*/
/** UninstallerTask executes the SIARD Suite uninstallation on a "worker" 
 * thread.
 * @author Hartwig Thomas
 */
public class UninstallerTask
extends Task<Boolean>
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(UninstallerTask.class.getName());
  private boolean _bRemoveSettings = false;
  private boolean _bFilesRemoved = false;
  public boolean areFilesRemoved() { return _bFilesRemoved; }
  private int _iDesktopResult = 0;
  public int getDesktopResult() { return _iDesktopResult; }
  private String _sDesktopError = null;
  public String getDesktopError() { return _sDesktopError; }
  
  /*------------------------------------------------------------------*/
  /** actual installation running on worker thread.
   */
  @Override
  protected Boolean call() throws Exception
  {
    _il.enter();
    boolean bUninstalled = true;
    UserProperties up = UserProperties.getUserProperties();
    File folderInstallation = up.getInstalledPath(null);
    /* platform-dependent uninstallation */
    _il.event("Remove Desktop Link "+InstallUninstallHandler.sDESKTOP_NAME);
    try { Desktop.newInstance().deleteLink(InstallUninstallHandler.sDESKTOP_NAME); }
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
    /* folder of running instance */ 
    File folderSource = SpecialFolder.getJarFromClass(InstallUninstallHandler.class,false);
    if (folderSource.isFile())
      folderSource = folderSource.getParentFile();
    /* remove files */
    _il.event("Delete Files from "+folderInstallation.getAbsolutePath());
    if (!folderInstallation.getAbsolutePath().equals(folderSource.getAbsolutePath()))
      _bFilesRemoved = FU.deleteFiles(folderInstallation);
    /* remove settings */
    if (_bRemoveSettings)
      bUninstalled = up.delete();
    _il.exit(String.valueOf(bUninstalled));
    return bUninstalled;
  } /* call */

  /*------------------------------------------------------------------*/
  /** constructor.
   * @param ehFinished event handler for finished event.
   */
  private UninstallerTask(boolean bRemoveSettings, EventHandler<WorkerStateEvent> ehFinished)
  {
    super();
    _bRemoveSettings = bRemoveSettings;
    setOnSucceeded(ehFinished);
    setOnFailed(ehFinished);
    setOnCancelled(ehFinished);
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** executes the uninstallation in a background thread and fires event, 
   * when finished.
   * @param bRemoveSettings true, if also settings (user properties) are
   *   to be deleted.
   * @param ehFinished event handler for finished event.
   */
  public static void uninstallTask(boolean bRemoveSettings,
    EventHandler<WorkerStateEvent> ehFinished)
  {
    _il.enter(String.valueOf(bRemoveSettings));
    UninstallerTask ut = new UninstallerTask(bRemoveSettings,ehFinished);
    Thread thread = new Thread(ut);
    thread.setDaemon(true);
    thread.start();
    _il.exit();
  } /* uninstallTask */
  
} /* UninstallTask */
