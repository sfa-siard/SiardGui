/*====================================================================== 
MainPanel implements the main panel of the SIARD GUI. 
Application: SIARD 2
Description: MainPanel implements the main panel of the SIARD GUI.
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland
Created    : 10.05.2017, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.gui;

import java.io.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.fx.controls.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.gui.tv.*;
import ch.admin.bar.siard2.gui.details.*;

/*====================================================================*/
/** MainPanel implements the main panel of the SIARD GUI.
 @author Hartwig Thomas
 */
public class MainPane extends BorderPane
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(MainPane.class.getName());
  /** padding inside main pane */
  private static final double dPADDING_EX = 0.0;
  /** margin inside main pane */
  private static final double dMARGIN_EM = 0.0;
  /** minimum width of main pane */
  private static final double dMIN_WIDTH_EM = 50.0;
  /** minimum height of main pane */
  private static final double dMIN_HEIGHT_EX = 20.0;
  /** initial divider position */
  private static final double dDIVIDER_POSITION = 0.2;
  /** singleton ... */
  private static MainPane _mp = null;
  /** menu bar */
  private MainMenuBar _mmb = null;
  /** tree view */
  private ArchiveTreeView _atv = null;
  /** details vbox */
  private DetailsScrollPane _dsp = null;
  /** status field */
  private Label _lblStatus = null;
  /** split pane */
  private SplitPane _sp = null;

  /**-----------------------------------------------------------------*/
  /** setArchive displays archive.
   */
  public void setArchive()
  {
    _atv.setArchive(SiardGui.getSiardGui().getArchive());
    _sp.setDividerPosition(0,dDIVIDER_POSITION);
  } /* setArchive */

  /*------------------------------------------------------------------*/
  /** constructor
   */
  private MainPane()
  {
    super();
    setPadding(new Insets(FxSizes.fromExes(dPADDING_EX)));
    setMinWidth(FxSizes.fromEms(dMIN_WIDTH_EM));
    setMinHeight(FxSizes.fromExes(dMIN_HEIGHT_EX));
    _mmb = MainMenuBar.getMainMenuBar();
    setTop(_mmb);
    _sp = new SplitPane();
    _atv = new ArchiveTreeView();
    _dsp = new DetailsScrollPane();
    _sp.getItems().addAll(_atv,_dsp);
    _dsp.prefViewportWidthProperty().bind(widthProperty());
    _dsp.prefViewportHeightProperty().bind(heightProperty());
    setCenter(_sp);
    _sp.setDividerPosition(0,0.0);
    _lblStatus = new Label("");
    _lblStatus.prefWidthProperty().bind(widthProperty());
    _lblStatus.setStyle(FxStyles.sSTYLE_MESSAGE);
    setBottom(_lblStatus);
    setMargin(_mmb, new Insets(FxSizes.fromEms(dMARGIN_EM)));
    setMargin(_sp, new Insets(FxSizes.fromEms(dMARGIN_EM)));
    setMargin(_lblStatus, new Insets(FxSizes.fromEms(dMARGIN_EM)));
    setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
  } /* constructor MainPane */
  
  /*------------------------------------------------------------------*/
  /** refreshLanguage redisplays the pane in the given language.
   */
  public void refreshLanguage()
  {
    _mmb.refreshLanguage();
    _dsp.reset();
  } /* refreshLanguage */
  
  /*------------------------------------------------------------------*/
  /** setModal displays status in status line and disables most controls.
   * @param sStatus null, if modality is to be terminated. 
   */
  public void setModal(String sStatus)
  {
    if (sStatus != null)
    {
      _lblStatus.setText(sStatus);
      {
        _mmb.setDisable(true);
        _atv.setDisable(true);
      }
    }
    else
    {
      _atv.setDisable(false);
      _mmb.setDisable(false);
      _lblStatus.setText("");
    }
  } /* setStatus */
  
  /*------------------------------------------------------------------*/
  /** show the meta data in the details pane.
   * @param oMetaData meta data or record extract.
   * @param clsTableData class of table data to show.
   */
  public void showMetaData(Object oMetaData,Class<?>clsTableData)
  {
    if (_dsp != null)
      _dsp.setMetaData(oMetaData,clsTableData);
  } /* showMetaData */
  
  /*------------------------------------------------------------------*/
  /** collapse currently selected record extract and its parents up to 
   * the rows entry of the table tree item.
   */
  public void collapseToRows()
  {
    if (_atv != null)
      _atv.collapseToRows();
  } /* collapseToRows */

  /*------------------------------------------------------------------*/
  /** return the currently selected table in the tree view.
   * @return the currently selected table in the tree view.
   */
  public Table getSelectedTable()
  {
    Table table = null;
    if (_atv != null)
    {
      Object oMetaData = _atv.getSelectedObject();
      if (oMetaData instanceof MetaTable)
        table = ((MetaTable)oMetaData).getTable();
      if (oMetaData instanceof RecordExtract[])
      {
        RecordExtract[] are = (RecordExtract[])oMetaData;
        if (are.length > 0)
          table = are[0].getTable();
      }
      if (oMetaData instanceof RecordExtract)
        table = ((RecordExtract)oMetaData).getTable();
    }
    return table;
  } /* getSelectedTable */
  
  /*------------------------------------------------------------------*/
  /** return the currently displayed table view in the details pane.
   * @return the currently displayed table view in the details pane.
   */
  public ObjectListTableView getDisplayedTableView()
  {
    return _dsp.getTableView();
  } /* getDisplayedTableView */
  
  /*------------------------------------------------------------------*/
  /** are there any edited changes not yet applied to meta data?
   * @return true, if meta data details were edited but not yet applied 
   * to meta data.
   */
  public boolean isChanged()
  {
    return _dsp.isChanged();
  } /* isChanged */
  
  /*------------------------------------------------------------------*/
  /** return the currently selected row on the details pane.
   * @return the currently selected row on the details pane 
   *   or -1 if nothing is selected. 
   */
  public int getSelectedTableRow()
  {
    int iSelectedRow = -1;
    ObjectListTableView oltv = getDisplayedTableView();
    if (oltv != null)
      iSelectedRow = oltv.getSelectionModel().getSelectedIndex();
    return iSelectedRow;
  } /* getSelectedTableRow */
  
  /*------------------------------------------------------------------*/
  /** select the meta data containing the search string.
   * @param ms MetaSearch meta data object.
   */
  public void selectFound(MetaSearch ms)
  {
    /* select the tree item with this object and scroll to it */
    _atv.select(ms);
    /* this should have displayed it on the details pane. */
    try
    {
      _dsp.getMetaDataEditor().selectRange(ms.getFoundElement(), 
        ms.getFoundOffset(), ms.getFoundOffset()+ms.getFindString().length());
    }
    catch(IOException ie) { _il.exception(ie); }
  } /* selectFound */
  
  /*------------------------------------------------------------------*/
  /** select the cell in the primary data record set containing the 
   * search string.
   * @param table selected table with search result.
   */
  public void selectSearched(Table table)
  {
    /* select the tree item of the record set containing the found cell
     * and scroll to it */
    _atv.select(table);
    /* this should have displayed the record set on the details pane. */
    RecordExtractTableView retv = (RecordExtractTableView)_dsp.getTableView();
    RecordExtract re = retv.getRecordExtract();
    int iRecord = (int)(table.getFoundRow()-re.getOffset());
    TextField tf = retv.selectRange(iRecord,table.getFoundPosition(),
      table.getFoundOffset(),table.getFindString().length());
    _dsp.scrollToCenter(tf);
  } /* selectSearched */

  /*------------------------------------------------------------------*/
  /** deselect last search result.
   */
  public void deselectSearched()
  {
    if (_dsp.getTableView() instanceof RecordExtractTableView)
    {
      RecordExtractTableView retv = (RecordExtractTableView)_dsp.getTableView();
      retv.deselect();
    }
  } /* deselectSearched */

  /*------------------------------------------------------------------*/
  /** factory
   * @return singleton main pane instance.
   */
  public static MainPane getMainPane()
  {
    if (_mp == null)
      _mp = new MainPane();
    return _mp;
  } /* getInstance */

} /* MainPane */
