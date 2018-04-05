package ch.admin.bar.siard2.gui.tasks;

import javafx.concurrent.*;
import javafx.event.*;
import ch.enterag.utils.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.gui.*;

public class SearchTask
extends Task<Cell>
{
  private DU _du = null;
  private Table _table = null;
  public Table getTable() { return _table; }

  /*------------------------------------------------------------------*/
  /** actual searching on the worker thread
   */
  @Override
  protected Cell call() throws Exception
  {
    Cell cell = _table.findNext(_du);
    return cell;
  } /* call */

  /*------------------------------------------------------------------*/
  /** constructor
   * @param table table to be searched.
   * @param ehFinished event handler for finished event.
   */
  private SearchTask(Table table,
    EventHandler<WorkerStateEvent> ehFinished)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    _du = DU.getInstance(sb.getLanguage(),sb.getDateFormat());
    _table = table;
    setOnSucceeded(ehFinished);
    setOnFailed(ehFinished);
    setOnCancelled(ehFinished);
  } /* constructor SearchTask */

  /*------------------------------------------------------------------*/
  /** executes the search in a background thread and fires event, 
   * when finished.
   * @param table table to be searched.
   * @param ehFinished event handler for finished event.
   */
  public static void searchTask(
    Table table,
    EventHandler<WorkerStateEvent> ehFinished)
  {
    SearchTask st = new SearchTask(table,ehFinished);
    Thread thread = new Thread(st);
    thread.setDaemon(true);
    thread.start();
  } /* uninstallTask */
  
} /* class SearchTask */
