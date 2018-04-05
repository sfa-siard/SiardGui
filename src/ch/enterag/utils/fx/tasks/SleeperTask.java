/*====================================================================== 
SleeperTask runs sleep on a "worker" thread. 
Application : JavaFX Utilities
Description: SleeperTask runs sleep on a "worker" thread.
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland
Created    : 12.05.2017, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.fx.tasks;

import javafx.concurrent.*;
import javafx.event.*;

/*====================================================================*/
/** SleeperTask runs sleep on a "worker" thread.
 * @author Hartwig
 */
public class SleeperTask 
  extends Task<Void>
{
  private final int _iSleepMs;
  public int getSleepMs() { return _iSleepMs; }
  
  /*------------------------------------------------------------------*/
  /** constructor.
   * @param iSleepMs milliseconds to sleep.
   * @param ehFinished event handler for finished event.
   */
  private SleeperTask(int iSleepMs, EventHandler<WorkerStateEvent> ehFinished)
  {
    super();
    setOnSucceeded(ehFinished);
    _iSleepMs = iSleepMs;
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** task execution.
   */
  @Override
  protected Void call() 
  {
    try { Thread.sleep(_iSleepMs); }
    catch (InterruptedException ie) {}
    return null;
  } /* call */

  /*------------------------------------------------------------------*/
  /** runs sleep in a background thread and fires event, when finished.
   * @param iSleepMs milliseconds to sleep.
   * @param ehFinished event handler for finished event.
   */
  public static void runSleeperTask(int iSleepMs, EventHandler<WorkerStateEvent> ehFinished)
  {
    SleeperTask st = new SleeperTask(iSleepMs, ehFinished);
    Thread thread = new Thread(st);
    thread.setDaemon(true);
    thread.start();
  } /* runSleeperTask */
  
} /* class SleeperTask */
