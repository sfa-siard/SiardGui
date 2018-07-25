/*====================================================================== 
InstallUninstallHandler handles installing and uninstalling of SIARD Suite. 
Application : SIARD GUI
Description: InstallUninstallHandler handles installing and uninstalling of SIARD Suite. 
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland
Created    : 09.06.2017, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.gui.actions;

import java.io.*;
import javafx.concurrent.*;
import javafx.event.*;
import javafx.stage.*;
import ch.enterag.utils.fx.dialogs.*;
import ch.enterag.utils.io.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.gui.*;
import ch.admin.bar.siard2.gui.tasks.*;

/*====================================================================*/
/** InstallUninstallHandler handles installing and uninstalling of SIARD Suite.
 * @author Hartwig Thomas
 */
public class InstallUninstallHandler
  implements EventHandler<WorkerStateEvent>
{
  private static InstallUninstallHandler _si = null;
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(InstallUninstallHandler.class.getName());
  public static final String sDESKTOP_NAME = "SiardGui";
  /* problem: file system is not cleaned up immediately after uninstall */
  @SuppressWarnings("unused")
  private static final long lCONTINUE_MILLIS = 100;
  private boolean _bContinueInstall = false;
  private boolean _bQuiet = false;

  /*------------------------------------------------------------------*/
  /** constructor */
  private InstallUninstallHandler()
  {
  } /* constructor InstallUninstallHandler */
  
  /*------------------------------------------------------------------*/
  /** factory */
  public static InstallUninstallHandler getInstallUninstallHandler()
  {
    if (_si == null)
      _si = new InstallUninstallHandler();
    return _si;
  } /* getInstallUninstallHandler */

  /*------------------------------------------------------------------*/
  /** handle the result of the installer task.
   * @param it installer task.
   */
  private void handleInstallerResult(InstallerTask it)
  {
    _il.enter(it);
    SiardBundle sb = SiardBundle.getSiardBundle();
    UserProperties up = UserProperties.getUserProperties();
    boolean bInstalled = it.getValue().booleanValue();
    if (bInstalled)
    {
      _il.event("Installation succeeded");
      String sMessage = sb.getInstallationSuccessMessage(it.getInstallationFolder());
      if (it.getDesktopResult() != 0)
        MB.show(SiardGui.getSiardGui().getStage(),
          sb.getInstallationErrorTitle(), 
          sb.getInstallationErrorDesktopMessage(it.getDesktopError()), 
          sb.getOk(), null);
      else
        sMessage = sb.getInstallationSuccessDesktopMessage(it.getInstallationFolder());
      MB.show(SiardGui.getSiardGui().getStage(),
        sb.getInstallationSuccessTitle(), sMessage, 
        sb.getOk(), null);
      _il.event("Setting installation folder to "+it.getInstallationFolder().getAbsolutePath()+" ...");
      up.setInstalledPath(it.getInstallationFolder());
      _il.event("Setting installation version "+SiardGui.getVersion()+" ...");
      up.setInstalledVersion(SiardGui.getVersion());
      _il.event("Storing installation ...");
      up.store();
      _il.event("Stored "+up.getInstalledVersion(null)+" ("+up.getInstalledPath(null)+") to "+ up.getFile().getAbsolutePath());
      SiardGui.getSiardGui().exit();
    }
    else
    {
      _il.event("Installation failed!");
      MB.show(SiardGui.getSiardGui().getStage(),
        sb.getInstallationErrorTitle(),
        sb.getInstallationErrorCopyMessage(it.getInstallationFolder()), 
        sb.getOk(), null);
    }
    _il.exit();
  } /* handleInstallerResult */
  
  /*------------------------------------------------------------------*/
  /** handle the result of the uninstaller task.
   * @param ut uninstaller task.
   */
  private void handleUninstallerResult(UninstallerTask ut)
  {
    _il.enter(ut);
    SiardBundle sb = SiardBundle.getSiardBundle();
    UserProperties up = UserProperties.getUserProperties();
    boolean bUninstalled = ut.getValue().booleanValue();
    _il.event(bUninstalled?"Uninstallation succeeded":"Uninstallation failed!");
    if (bUninstalled)
    {
      if (ut.getDesktopResult() != 0)
        MB.show(SiardGui.getSiardGui().getStage(),
          sb.getUninstallationErrorTitle(),
          sb.getUninstallationErrorDesktopMessage(ut.getDesktopError()),
          sb.getOk(),null);
      if (!_bQuiet)
      {
        String sMessage = sb.getUninstallationSuccessSettingsMessage(up.getInstalledPath(null), up.getFile());
        if (ut.areFilesRemoved()) // settings and files removed
          sMessage = sb.getUninstallationSuccessFilesMessage(up.getInstalledPath(null), up.getFile());
        MB.show(SiardGui.getSiardGui().getStage(),
          sb.getUninstallationSuccessTitle(), sMessage, 
          sb.getOk(), null);
      }
      up.setInstalledVersion(null);
      _il.event("Storing uninstallation ...");
      up.store();
      if (_bContinueInstall)
        install();
      else
        MainMenuBar.getMainMenuBar().restrict();
    }
    else
    {
      MB.show(SiardGui.getSiardGui().getStage(),
        sb.getUninstallationErrorTitle(),
        sb.getUninstallationErrorSettingsMessage(up.getFile()), 
        sb.getOk(), null);
    }
    _il.exit();
  } /* handleUninstallerResult */
  
  /*------------------------------------------------------------------*/
  /** handle the result of the InstallerTask or UninstallerTask.
   * @param wse event from ExecuteTask.
   */
  @Override
  public void handle(WorkerStateEvent wse)
  {
    _il.enter(wse);
    SiardGui.getSiardGui().terminateAction();
    if (wse.getSource() instanceof InstallerTask)
      handleInstallerResult((InstallerTask)wse.getSource());
    else if (wse.getSource() instanceof UninstallerTask)
      handleUninstallerResult((UninstallerTask)wse.getSource());
    _il.exit();
  } /* handle */
  
  /*------------------------------------------------------------------*/
  /** install the currently executing instance
   */
  public void install()
  {
    _il.enter(String.valueOf(_bContinueInstall));
    _bContinueInstall = false;
    UserProperties up = UserProperties.getUserProperties();
    SiardBundle sb = SiardBundle.getSiardBundle();
    Stage stage = SiardGui.getSiardGui().getStage();
    /* folder of running instance */ 
    File folderSource = SpecialFolder.getMainJar();
    if (folderSource.isFile())
      folderSource = folderSource.getParentFile().getParentFile();
    else
      folderSource = folderSource.getParentFile();
    _il.event("folderSource: "+folderSource.getAbsolutePath());
    /* get initial value for from previously installed version */
    File folderInstallation = up.getInstalledPath(null);
    if (up.getInstalledVersion(null) == null)
    {
      /* if completely new, then set it to application folder in local home */
      if (folderInstallation == null)
      {
        _il.event("New installation!");
        folderInstallation = new File(SpecialFolder.getUserLocalHome(up.getApplicationName()));
      }
      _il.event("Initial installation folder: "+folderInstallation.getAbsolutePath());
      /* now select folder where SIARD Suite is to be installed. */
      try 
      {
        /* do not use native directory chooser here */
        boolean bNative = Boolean.valueOf(System.getProperty(FS.sUSE_NATIVE_PROPERTY));
        System.setProperty(FS.sUSE_NATIVE_PROPERTY, String.valueOf(false));
        do
        {
          folderInstallation = FS.chooseNewFolder(stage, 
              sb.getInstallationSelectorTitle(), sb.getInstallationSelectorMessage(), 
              sb, folderInstallation);
        } while ((folderInstallation != null) && 
          folderInstallation.exists() && 
          (folderInstallation.listFiles().length > 0) &&
          (MB.show(stage,
            sb.getInstallationNotemptyTitle(),
            sb.getInstallationNotemptyMessage(), sb.getOk(), null) == 1)
          );
        System.setProperty(FS.sUSE_NATIVE_PROPERTY, String.valueOf(bNative));
        /* install */
        _il.event("Selected installation folder: "+((folderInstallation == null)? "null" : folderInstallation.getAbsolutePath()));
        if (folderInstallation != null) // else DirectorySelector was cancelled
        {
          SiardGui.getSiardGui().startAction(sb.getInstallingStatus(folderInstallation));
          _il.event("Starting InstallerTask from "+
            "\""+folderSource.getAbsolutePath()+"\" to "+
            "\""+folderInstallation.getAbsolutePath()+"\".");
          InstallerTask.installTask(folderSource, folderInstallation, this);
        }
        else
          _il.event("Selection of installation folder cancelled!");
      }
      catch(IOException ie) { _il.exception(ie); }
    }
    else
    {
      _bContinueInstall = true;
      SiardGui.getSiardGui().uninstall(true);
    }
    _il.exit();
  } /* install */
  
  /*------------------------------------------------------------------*/
  /** uninstall the currently installed version of SIARD Suite.
   * @param bQuiet true, if questions are to be asked and no success is 
   *   to be displayed.
   */
  public void uninstall(boolean bQuiet)
  {
    _il.enter();
    _bQuiet = bQuiet;
    SiardBundle sb = SiardBundle.getSiardBundle();
    boolean bRemoveSettings = false;
    if (!bQuiet)
    {
      bRemoveSettings = (MB.show(SiardGui.getSiardGui().getStage(),
        sb.getUninstallationTitle(), sb.getUninstallationCompleteQuery(), 
        sb.getYes(), sb.getNo()) == 1);
    }
    File folderInstallation = UserProperties.getUserProperties().getInstalledPath(null);
    if (folderInstallation != null)
    {
      SiardGui.getSiardGui().startAction(sb.getUninstallingStatus(folderInstallation));
      _il.event("Starting UninstallerTask");
      UninstallerTask.uninstallTask(bRemoveSettings,this);
    }
    else
    {
      MB.show(SiardGui.getSiardGui().getStage(),
        sb.getUninstallationErrorTitle(),
        sb.getUninstallationErrorImpossibleMessage(), sb.getOk(), null);
    }
    _il.exit();
  } /* uninstall */

} /* class InstallUninstallHandler */
