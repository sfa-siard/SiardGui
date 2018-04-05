/*====================================================================== 
SearchAction handles finding strings in the primary data of the selected table. 
Application: SIARD GUI
Description: SearchAction handles finding strings in the primary data of 
             the selected table. 
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 16.08.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.actions;

import java.io.*;
import java.util.concurrent.*;
import javafx.concurrent.*;
import javafx.event.*;
import ch.enterag.utils.EU;
import ch.enterag.utils.fx.dialogs.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.gui.*;
import ch.admin.bar.siard2.gui.dialogs.*;
import ch.admin.bar.siard2.gui.tasks.*;

/*====================================================================*/
/** SearchAction handles finding strings in the primary data of the 
 * selected table.
 * @author Hartwig Thomas
 */
public class SearchAction
  implements EventHandler<WorkerStateEvent>
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(SearchAction.class.getName());
  private String _sFindString = null;
  
  /*------------------------------------------------------------------*/
  /** constructor
   */
  private SearchAction()
  {
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** factory
   * @return new instance of SearchAction.
   */
  public static SearchAction newSearchAction()
  {
    SearchAction fa = new SearchAction();
    return fa;
  } /* factory */

  /*------------------------------------------------------------------*/
  /** prepare finding a string in the selected table.
   * @return true, if string can be searched.
   */
  public boolean search(Table table)
  {
    SiardGui sg = SiardGui.getSiardGui();
    UserProperties up = UserProperties.getUserProperties();
    SearchDialog sd = SearchDialog.showSearchDialog(sg.getStage(),
      table.getMetaTable(), 
      up.getSearchString(), 
      up.getSearchMatchCase());
    if (!sd.isCanceled())
    {
      String sFindString = sd.getFindString();
      boolean bMatchCase = sd.mustMatchCase();
      up.setSearchString(sFindString);
      up.setSearchMatchCase(bMatchCase);
      try { table.find(sd.getSelection(),sFindString, bMatchCase); }
      catch(IOException ie) { _il.exception(ie); }
    }
    return !sd.isCanceled();
  } /* search */

  /*------------------------------------------------------------------*/
  /** start search for next occurrence.
   * @param table table to be searched.
   */
  public void searchNext(Table table)
  {
    SiardGui sg = SiardGui.getSiardGui();
    SiardBundle sb = SiardBundle.getSiardBundle();
    _sFindString = table.getFindString();
    sg.startAction(sb.getSearchStatus(table.getFindString(),table.getMetaTable().getName()));
    SearchTask.searchTask(table, this);
  } /* searchNext */

  /*------------------------------------------------------------------*/
  /** handle end of search
   * @param wse worker state event.
   */
  @Override
  public void handle(WorkerStateEvent wse)
  {
    try
    {
      SiardGui sg = SiardGui.getSiardGui();
      SiardBundle sb = SiardBundle.getSiardBundle();
      sg.terminateAction();
      SearchTask st = (SearchTask)wse.getSource();
      String sTableName = st.getTable().getMetaTable().getName();
      if (wse.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED)
      {
        MainPane mp = MainPane.getMainPane();
        Cell cell = st.get();
        if (cell != null)
          mp.selectSearched(st.getTable());
        else
        {
          mp.deselectSearched();
          MB.show(sg.getStage(),
            sb.getSearchTitle(sTableName),
            sb.getSearchEndMessage(_sFindString,sTableName),
            sb.getOk(), null);
        }
        MainMenuBar.getMainMenuBar().restrict();
      }
      else
      {
        MB.show(sg.getStage(),
          sb.getSearchTitle(sTableName),
          sb.getSearchFailedMessage(_sFindString, sTableName), 
          sb.getOk(), null);
      }
    }
    catch(InterruptedException ie) { _il.exception(ie); }
    catch(ExecutionException ee) { System.err.println(EU.getExceptionMessage(ee)); _il.exception(ee); }
  } /* handle */
  
} /* class SearchAction */
