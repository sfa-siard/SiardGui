/*======================================================================
DownloadTask for downloading a database to a SIARD archive on a Worker thread. 
Application : Siard2
Description : DownloadTask for downloading a database to a SIARD archive 
              on a Worker thread. 
Platform    : Java 7, JavaFX 2.2   
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 27.06.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.tasks;

import java.sql.*;
import javafx.beans.property.*;
import javafx.concurrent.*;
import javafx.event.*;
import ch.enterag.utils.background.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.cmd.*;
import ch.admin.bar.siard2.gui.UserProperties;

/*====================================================================*/
/** DownloadTask for downloading a database to a SIARD archive on a 
 * Worker thread. 
 * @author Hartwig Thomas
 */
public class DownloadTask
  extends Task<Void>
  implements Progress
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(DownloadTask.class.getName());
  private Connection _conn = null;
  public Connection getConnection() { return _conn; }
  private Archive _archive = null;
  public Archive getArchive() { return _archive; }
  private boolean _bMetaDataOnly = false;
  public boolean isMetaDataOnly() { return _bMetaDataOnly; }
  private boolean _bViewsAsTables = false;
  public boolean isViewsAsTables() { return _bViewsAsTables; }

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
  /** actual download running on worker thread.
   */
  @Override
  protected Void call() throws Exception
  {
    _il.enter();
    _conn.setAutoCommit(false);
    UserProperties up = UserProperties.getUserProperties();
    MetaDataFromDb mdfd = MetaDataFromDb.newInstance(_conn.getMetaData(), _archive.getMetaData());
    mdfd.setQueryTimeout(up.getQueryTimeoutSeconds());
    mdfd.download(_bViewsAsTables, this);
    if (!_bMetaDataOnly)
    {
      updateProgress(0,100);
      PrimaryDataFromDb pdfd = PrimaryDataFromDb.newInstance(_conn, _archive);
      pdfd.setQueryTimeout(up.getQueryTimeoutSeconds());
      pdfd.download(this);
    }
    _il.exit();
    return null;
  } /* call */

  /*------------------------------------------------------------------*/
  /** constructor.
   * @param conn connection to database to be downloaded.
   * @param archive empty created archive to be filled.
   * @param bMetaDataOnly true, if only meta data are to be downloaded.
   * @param bViewsAsTables true, if all views are to be downloaded as tables.
   * @param dp progress property to be bound to task's progress.
   * @param eh event handler for all worker events.
   */
  private DownloadTask(Connection conn, Archive archive, 
    boolean bMetaDataOnly, boolean bViewsAsTables, 
    DoubleProperty dp, EventHandler<WorkerStateEvent> eh)
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
    _bViewsAsTables = bViewsAsTables;
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** executes the download in a background thread and fires event, 
   * when finished.
   * @param conn connection to database to be downloaded.
   * @param archive empty created archive to be filled.
   * @param bMetaDataOnly true, if only meta data are to be downloaded.
   * @param bViewsAsTables true, if all views are to be downloaded as tables.
   * @param dp progress property to be bound to task's progress.
   * @param eh event handler for all worker events.
   * @returns instance of started task (for canceling ...)
   */
  public static DownloadTask startDownloadTask(Connection conn, Archive archive,
    boolean bMetaDataOnly, boolean bViewsAsTables, 
    DoubleProperty dp, EventHandler<WorkerStateEvent> eh)
  {
    _il.enter();
    DownloadTask dt = new DownloadTask(conn, archive, bMetaDataOnly, bViewsAsTables, dp, eh);
    Thread thread = new Thread(dt);
    thread.setDaemon(true);
    thread.start();
    _il.exit(dt);
    return dt;
  } /* startDownloadTask */

} /* DownloadTask */
