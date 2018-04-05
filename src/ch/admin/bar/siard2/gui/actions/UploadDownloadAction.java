/*====================================================================== 
UploadDownloadAction handles upload and download of a SIARD archive. 
Application : SIARD GUI
Description: UploadDownloadAction handles upload and download of a SIARD archive. 
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 23.06.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.actions;

import java.io.*;
import java.sql.*;
import javafx.stage.*;
import ch.enterag.utils.fx.dialogs.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.primary.*;
import ch.admin.bar.siard2.cmd.*;
import ch.admin.bar.siard2.gui.*;
import ch.admin.bar.siard2.gui.dialogs.*;

/*====================================================================*/
/** UploadDownloadAction handles upload and download of a SIARD archive.
 * @author Hartwig Thomas
 */
public class UploadDownloadAction
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(UploadDownloadAction.class.getName());
  
  /*------------------------------------------------------------------*/
  /** constructor */
  private UploadDownloadAction()
  {
  } /* constructor UploadDownloadAction */
  
  /*------------------------------------------------------------------*/
  /** factory */
  public static UploadDownloadAction newUploadDownloadAction()
  {
    return new UploadDownloadAction();
  } /* newUploadDownloadAction */

  /*------------------------------------------------------------------*/
  /** display connection dialog, get archive to download to, show 
   * download dialog.
   * @param sConnectionUrl connection URL from MRU or null.
   * @param sDbUser Database user from MRU or null.
   */
  public void download(String sConnectionUrl, String sDbUser)
  {
    _il.enter(sConnectionUrl,sDbUser);
    SiardBundle sb = SiardBundle.getSiardBundle();
    Stage stage = SiardGui.getSiardGui().getStage();
    /* display connection dialog (at least for password) */
    Connection conn = null;
    DownloadConnectionDialog dcd = DownloadConnectionDialog.showDownloadConnectionDialog(
      stage, sConnectionUrl, sDbUser);
    while((dcd.getResult() == DownloadConnectionDialog.iRESULT_SUCCESS) && (conn == null))
    {
      sConnectionUrl = dcd.getConnectionUrl();
      sDbUser = dcd.getDbUser();
      /* try and load appropriate driver */
      String sError = SiardConnection.getSiardConnection().loadDriver(dcd.getConnectionUrl());
      if (sError == null)
      {
        _il.event("Driver for "+dcd.getConnectionUrl()+" loaded!");
        /* test connection */
        DriverManager.setLoginTimeout(UserProperties.getUserProperties().getLoginTimeoutSeconds());
        try 
        { 
          conn = DriverManager.getConnection(dcd.getConnectionUrl(), dcd.getDbUser(), dcd.getDbPassword());
          conn.setAutoCommit(false);
        }
        catch(SQLException se)
        {
          conn = null;
          MB.show(stage,
            sb.getConnectionErrorTitle(),
            sb.getConnectionErrorConnectMessage(dcd.getConnectionUrl(), se),
            sb.getOk(), null);
        }
      }
      else
      {
        MB.show(stage,
          sb.getConnectionErrorTitle(),
          sb.getConnectionErrorDriverMessage(sError), 
          sb.getOk(), null);
      }
      if (conn == null)
        dcd = DownloadConnectionDialog.showDownloadConnectionDialog(
          stage, sConnectionUrl, sDbUser);
    }
    if (dcd.getResult() == DownloadConnectionDialog.iRESULT_SUCCESS)
    {
      _il.event("Connection established!");
      /* update MRU */
      MruConnection mc = MruConnection.getMruConnection(true);
      mc.setMruConnection(dcd.getConnectionUrl(), dcd.getDbUser());
      /* update menu bar */
      MainMenuBar.getMainMenuBar().setConnectionMru(true);
      /* get file */
      File fileArchive = null;
      try
      {
        if (!dcd.isMetaDataOnly())
        {
          fileArchive = SiardGui.getDefaultDataDirectory();
          if (MruFile.getMruFile().getMruFiles() > 0)
            fileArchive = (new File(MruFile.getMruFile().getMruFile(0))).getParentFile();
          fileArchive = new File(fileArchive.getAbsolutePath()+File.separator+"*."+Archive.sSIARD_DEFAULT_EXTENSION);
          fileArchive = FS.chooseNewFile(stage, 
              sb.getArchiveFileTitle(), sb.getArchiveFileMessage(), sb, 
            fileArchive, Archive.sSIARD_DEFAULT_EXTENSION,true);
        }
        else
        {
          /* meta data only file will be temporary */
          fileArchive = File.createTempFile("mdo",".siard");
          fileArchive.deleteOnExit();
        }
      }
      catch (IOException ie) { _il.exception(ie); }
      if (fileArchive != null)
      {
        _il.event("Archive file selected: "+fileArchive.getAbsolutePath());
        if (fileArchive.exists())
          fileArchive.delete();
        SiardGui.getSiardGui().startAction(
          sb.getDownloadingStatus(dcd.getConnectionUrl(),fileArchive));
        try
        {
          Archive archive = ArchiveImpl.newInstance();
          archive.create(fileArchive);
          /* show download dialog */
          DownloadDialog dd = DownloadDialog.showDownloadDialog(
            stage,conn,archive,dcd.isMetaDataOnly(),dcd.isViewsAsTables());
          if (dd.wasSuccessful() && (dcd.isMetaDataOnly() || dd.getArchive().isValid()))
          {
            dd.getArchive().close();
            dd.getArchive().open(fileArchive);
            SiardGui.getSiardGui().setArchive(dd.getArchive());
            if (!dcd.isMetaDataOnly())
            {
              MruFile mf = MruFile.getMruFile();
              mf.setMruFile(archive.getFile().getAbsolutePath());
              MainMenuBar.getMainMenuBar().setFileMru();
            }
          }
          else
          {
            try { dd.getArchive().close(); }
            catch(IOException ie) { }
            MB.show(stage, 
                sb.getDownloadErrorTitle(),
                sb.getDownloadErrorInvalidMessage(fileArchive),
                sb.getOk(),null);
            if (!fileArchive.delete())
              fileArchive.deleteOnExit();
          }
        }
        catch(IOException ie)
        {
          MB.show(stage,
            sb.getDownloadErrorTitle(), 
            sb.getDownloadErrorCreateMessage(fileArchive,ie), 
            sb.getOk(), null);
        }
        finally
        {
          try
          {
            if ((conn != null) && (!conn.isClosed()))
              conn.close();
          }
          catch(SQLException se) { _il.exception(se); }
        }
        SiardGui.getSiardGui().terminateAction();
      }
      else
        _il.event("No file selected!");
    }
    _il.exit();
  } /* download */
  
  /*------------------------------------------------------------------*/
  /** display connection dialog, show upload dialog.
   * @param sConnectionUrl connection URL from MRU or null.
   * @param sDbUser Database user from MRU or null.
   * @param archive SIARD archive to upload.
   */
  public void upload(String sConnectionUrl, String sDbUser, Archive archive)
  {
    _il.enter(sConnectionUrl,sDbUser);
    SiardBundle sb = SiardBundle.getSiardBundle();
    Stage stage = SiardGui.getSiardGui().getStage();
    /* display connection dialog (at least for password) */
    Connection conn = null;
    UploadConnectionDialog ucd = UploadConnectionDialog.showUploadConnectionDialog(
      stage, sConnectionUrl, sDbUser, archive);
    while((ucd.getResult() == UploadConnectionDialog.iRESULT_SUCCESS) && (conn == null))
    {
      sConnectionUrl = ucd.getConnectionUrl();
      sDbUser = ucd.getDbUser();
      /* try and load appropriate driver */
      String sError = SiardConnection.getSiardConnection().loadDriver(ucd.getConnectionUrl());
      if (sError == null)
      {
        /* test connection */
        DriverManager.setLoginTimeout(UserProperties.getUserProperties().getLoginTimeoutSeconds());
        try 
        { 
          conn = DriverManager.getConnection(ucd.getConnectionUrl(), ucd.getDbUser(), ucd.getDbPassword());
          conn.setAutoCommit(false);
        }
        catch(SQLException se)
        {
          conn = null;
          MB.show(stage,
            sb.getConnectionErrorTitle(),
            sb.getConnectionErrorConnectMessage( ucd.getConnectionUrl(), se),
            sb.getOk(), null);
        }
      }
      else
      {
        MB.show(stage, sb.getConnectionErrorTitle(),
         sb.getConnectionErrorDriverMessage(sError), 
         sb.getOk(), null);
      }
      if (conn == null)
        ucd = UploadConnectionDialog.showUploadConnectionDialog(
          stage, sConnectionUrl, sDbUser, archive);
    }
    if (ucd.getResult() == UploadConnectionDialog.iRESULT_SUCCESS)
    {
      /* update MRU */
      MruConnection mc = MruConnection.getMruConnection(false);
      mc.setMruConnection(ucd.getConnectionUrl(), ucd.getDbUser());
      /* update menu bar */
      MainMenuBar.getMainMenuBar().setConnectionMru(false);
      /* creating the MetaDataToDb takes a while. So let the user see the wait cursor ... */
      SiardGui.getSiardGui().startAction(
        sb.getUploadingStatus(archive.getFile(),ucd.getConnectionUrl()));
      try
      {
        MetaDataToDb mdtd = MetaDataToDb.newInstance(conn.getMetaData(), archive.getMetaData(), ucd.getSchemasMap());
        if (ucd.isOverwrite() || ((mdtd.tablesDroppedByUpload() == 0) && (mdtd.typesDroppedByUpload() == 0)))
          UploadDialog.showUploadDialog(stage,
            archive, conn, ucd.isMetaDataOnly(), mdtd);
        else
        {
          MB.show(stage,
            sb.getUploadErrorTitle(),
            sb.getUploadErrorOverwriteMessage(ucd.getConnectionUrl()),
            sb.getOk(), null);
        }
      }
      catch(IOException ie)
      {
        MB.show(stage,
          sb.getUploadErrorTitle(), 
          sb.getUploadErrorIoMessage(archive.getFile(),ie), 
          sb.getOk(), null);
      }
      catch(SQLException se)
      {
        MB.show(stage,
          sb.getUploadErrorTitle(), 
          sb.getUploadErrorSqlMessage(ucd.getConnectionUrl(),se), 
          sb.getOk(), null);
      }
      SiardGui.getSiardGui().terminateAction();
    }
    _il.exit();
  } /* upload */
  
} /* class UploadDownloadAction */
