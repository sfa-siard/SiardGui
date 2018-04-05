/*======================================================================
UploadTask for uploading a database from a SIARD archive on a Worker thread. 
Application : Siard2
Description : UploadTask for uploading a database from a SIARD archive 
              on a Worker thread. 
Platform    : Java 7, JavaFX 2.2   
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 29.06.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.tasks;

import java.sql.*;
import javafx.beans.property.*;
import javafx.concurrent.*;
import javafx.event.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.cmd.*;
import ch.admin.bar.siard2.gui.UserProperties;
import ch.enterag.utils.background.*;

/*====================================================================*/
/** UploadTask for uploading a database from a SIARD archive on a Worker thread.
 * @author Hartwig Thomas
 */
public class UploadTask
  extends Task<Void>
  implements Progress
{
  private Archive _archive = null;
  public Archive getArchive() { return _archive; }
  private Connection _conn = null;
  public Connection getConnection() { return _conn; }
  private boolean _bMetaDataOnly = false;
  public boolean isMetaDataOnly() { return _bMetaDataOnly; }
  private MetaDataToDb _mdtd = null;
  public MetaDataToDb getMetaDataToDb() { return _mdtd; }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean cancelRequested()
  {
    return isCancelled();
  } /* cancelRequested */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void notifyProgress(int iPercent)
  {
    updateProgress(iPercent,100);
  } /* notifyProgress */
  
  /*------------------------------------------------------------------*/
  /** actual upload running on worker thread.
   */
  @Override
  protected Void call() throws Exception
  {
    UserProperties up = UserProperties.getUserProperties();
    _conn.setAutoCommit(false);
    _mdtd.setQueryTimeout(up.getQueryTimeoutSeconds());
    _mdtd.upload(this);
    if (!_bMetaDataOnly)
    {
      updateProgress(0,100);
      PrimaryDataToDb pdtd = PrimaryDataToDb.newInstance(_conn, _archive, 
        _mdtd.getArchiveMapping(), _mdtd.supportsArrays(), _mdtd.supportsDistincts(), _mdtd.supportsUdts());
      pdtd.setQueryTimeout(up.getQueryTimeoutSeconds());
      pdtd.upload(this);
    }
    return null;
  } /* call */

  /*------------------------------------------------------------------*/
  /** constructor.
   * @param archive to be uploaded.
   * @param conn connection to database to be uploaded.
   * @param bMetaDataOnly true, if only schema is to be created.
   * @param mdtd instantiated MetaDataToDb instance ready for upload.
   * @param db progress indicator property to be updated during upload.
   * @param eh event handler for finished event.
   */
  private UploadTask(Archive archive, Connection conn, boolean bMetaDataOnly,
    MetaDataToDb mdtd, DoubleProperty dp, EventHandler<WorkerStateEvent> eh)
  {
    super();
    setOnSucceeded(eh);
    setOnFailed(eh);
    setOnCancelled(eh);
    updateProgress(0,100);
    dp.bind(progressProperty());
    _conn = conn;
    _archive = archive;
    _bMetaDataOnly = bMetaDataOnly;
    _mdtd = mdtd;
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** executes the upload in a background thread and fires event, 
   * when finished.
   * @param archive to be uploaded.
   * @param conn connection to database to be uploaded.
   * @param bMetaDataOnly true, if only schema is to be created.
   * @param mdtd instantiated MetaDataToDb instance ready for upload.
   * @param db progress indicator property to be updated during upload.
   * @param eh event handler for finished event.
   * @returns instance of started task (for canceling ...)
   */
  public static UploadTask startUploadTask(Archive archive, Connection conn,
    boolean bMetaDataOnly, MetaDataToDb mdtd, DoubleProperty dp, 
    EventHandler<WorkerStateEvent> eh)
  {
    UploadTask ut = new UploadTask(archive, conn, bMetaDataOnly, mdtd, dp, eh);
    Thread thread = new Thread(ut);
    thread.setDaemon(true);
    thread.start();
    return ut;
  } /* startUploadTask */
  
} /* UploadTask */
