package ch.admin.bar.siard2.gui.tasks;

import javafx.concurrent.*;
import javafx.event.*;
import ch.admin.bar.siard2.api.*;

public class SortTask
  extends Task<Void>
{
  private Table _table = null;
  private boolean _bAscending = false;
  private int _iSortColumn = -1;
  
  /*------------------------------------------------------------------*/
  /** actual sorting on worker thread.
   */
  @Override
  protected Void call() throws Exception
  {
    _table.sort(_bAscending, _iSortColumn, null);
    return null;
  } /* call */

  /*------------------------------------------------------------------*/
  /** constructor
   * @param table table to be sorted.
   * @param bAscending sort direction. 
   * @param iSortColumn column for table to be sorted by.
   * @param ehFinished event handler for finished event.
   */
  private SortTask(Table table, boolean bAscending, int iSortColumn,
    EventHandler<WorkerStateEvent> ehFinished)
  {
    super();
    _table = table;
    _bAscending = bAscending;
    _iSortColumn = iSortColumn;
    setOnSucceeded(ehFinished);
    setOnFailed(ehFinished);
    setOnCancelled(ehFinished);
  } /* constructor SortTask */
  
  /*------------------------------------------------------------------*/
  /** executes the sort in a background thread and fires event, 
   * when finished.
   * @param table table to be sorted.
   * @param bAscending sort direction. 
   * @param iSortColumn column for table to be sorted by.
   * @param ehFinished event handler for finished event.
   */
  public static void sortTask(
    Table table, boolean bAscending, int iSortColumn,
    EventHandler<WorkerStateEvent> ehFinished)
  {
    SortTask st = new SortTask(table,bAscending,iSortColumn,ehFinished);
    Thread thread = new Thread(st);
    thread.setDaemon(true);
    thread.start();
  } /* uninstallTask */
  
} /* class SortTask */
