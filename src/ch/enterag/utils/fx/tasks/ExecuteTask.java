/*====================================================================== 
ExecuteTask executes a command on a "worker" thread. 
Application : JavaFX Utilities
Description: ExecuteTask executes a command on a "worker" thread.
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland
Created    : 09.06.2017, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.fx.tasks;

import javafx.concurrent.*;
import javafx.event.EventHandler;

import ch.enterag.utils.lang.*;

/*====================================================================*/
/** ExecuteTask executes a command on a "worker" thread.
 * @author Hartwig
 */
public class ExecuteTask
  extends Task<Integer>
{
  private final String _sCommand;
  private final String[] _asCommand;
  public String getCommand() { return _sCommand != null?_sCommand:_asCommand[0]; }
  private Execute _ex = null;
  public String getOutput() { return _ex.getStdOut(); } 
  public String getError() { return _ex.getStdErr(); }
  
  /*------------------------------------------------------------------*/
  /** constructor.
   * @param asCommand external command and arguments to be executed.
   * @param ehFinished event handler for finished event.
   */
  private ExecuteTask(String[] asCommand, EventHandler<WorkerStateEvent> ehFinished)
  {
    super();
    setOnSucceeded(ehFinished);
    _sCommand = null;
    _asCommand = asCommand;
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** constructor.
   * @param sCommand external command to be executed.
   * @param sbOut receives the stdout of the command's execution
   * @param sbErr receives the stderr of the command's execution
   * @param ehFinished event handler for finished event.
   */
  private ExecuteTask(String sCommand, EventHandler<WorkerStateEvent> ehFinished)
  {
    super();
    setOnSucceeded(ehFinished);
    setOnFailed(ehFinished);
    setOnCancelled(ehFinished);
    _sCommand = sCommand;
    _asCommand = null;
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** task execution.
   */
  @Override
  protected Integer call() 
  {
    Integer iResult = null;
    if (_sCommand == null)
      _ex = Execute.execute(_asCommand);
    else
      _ex = Execute.execute(_sCommand);
    iResult = _ex.getResult();
    return iResult;
  } /* call */

  /*------------------------------------------------------------------*/
  /** executes the command in a background thread and fires event, when finished.
   * @param asCommand external command and arguments to be executed.
   * @param ehFinished event handler for finished event.
   */
  public static void executeTask(String[] asCommand, EventHandler<WorkerStateEvent> ehFinished)
  {
    ExecuteTask st = new ExecuteTask(asCommand, ehFinished);
    Thread thread = new Thread(st);
    thread.setDaemon(true);
    thread.start();
  } /* executeTask */
  
  /*------------------------------------------------------------------*/
  /** executes the command in a background thread and fires event, when finished.
   * @param sCommand external command to be executed.
   * @param ehFinished event handler for finished event.
   */
  public static void executeTask(String sCommand, EventHandler<WorkerStateEvent> ehFinished)
  {
    ExecuteTask st = new ExecuteTask(sCommand, ehFinished);
    Thread thread = new Thread(st);
    thread.setDaemon(true);
    thread.start();
  } /* executeTask */
  
} /* class ExecutorTask */
