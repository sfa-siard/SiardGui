/*======================================================================
The ObjectListTableView has lists of objects as records.   
Application: SIARD GUI
Description: The ObjectListTableView has lists of objects as records. 
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 10.08.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/

package ch.enterag.utils.fx.controls;

import java.math.*;
import java.text.*;
import java.util.*;
import javax.xml.datatype.Duration;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.util.*;
import javafx.util.Pair;

import ch.enterag.utils.*;
import ch.enterag.utils.fx.*;
import ch.enterag.sqlparser.*;

/*====================================================================*/
/** a table view with a list of objects per row.
 * - displays and aligns objects according to their type.
 * - generates an action event ("show cell") for double clicks or enter
 * keys on a cell. The target of the action event is an ObjectTableCell
 * with information about row and column index.
 *  
 * @author Hartwig Thomas
 */
public class ObjectListTableView
 extends TableViewWithVisibleRowCount<List<Object>>
{
  private DU _du = DU.getInstance(Locale.getDefault().getLanguage(), (new SimpleDateFormat()).toPattern());
  private int _iMaxVisibleRows = 10; // default value
  private int _iColumnId = 0;
  private int _iCellId = 0;
  // for real flexibility we need the table cells arranged as a traditional matrix.
  private List<List<ObjectTableCell>> _llTableCells = new ArrayList<List<ObjectTableCell>>();
  public ObjectTableCell getTableCell(int iRow, int iColumn)
  {
    ObjectTableCell otc = null;
    if (_llTableCells.get(iRow) != null)
      otc = _llTableCells.get(iRow).get(iColumn);
    return otc;
  } /* getTableCell */
  /* Selection change listener permits tracing of selection. */
  @SuppressWarnings("rawtypes")
  private ListChangeListener<TablePosition> _lclSel = new ListChangeListener<TablePosition>()
  {
    @Override
    public void onChanged(ListChangeListener.Change<? extends TablePosition> change)
    {
      while (change.next())
      {
        if (change.wasRemoved())
        {
          if (change.getRemovedSize() == 1)
          {
            TablePosition tp = change.getRemoved().get(0);
            ObjectTableCell otc = getTableCell(tp.getRow(),tp.getColumn());
            otc.deselect();
            // System.out.println("--- Removed "+String.valueOf(tp.getRow())+","+String.valueOf(tp.getColumn()));
          }
          else
            System.err.println("!!! Unexpected removed size for single selection!");
        }
        if (change.wasAdded())
        {
          if (change.getAddedSize() == 1)
          {
            TablePosition tp = change.getAddedSubList().get(0);
            ObjectTableCell otc = getTableCell(tp.getRow(),tp.getColumn());
            otc.setFocus();
            // System.out.println("--- Added "+String.valueOf(tp.getRow())+","+String.valueOf(tp.getColumn()));
          }
          else
            System.err.println("!!! Unexpected added size for single selection!");
        }
        if (change.wasPermutated())
        {
          System.err.println("!!! Unexpected selection change!");
        }
        if (change.wasUpdated())
        {
          System.err.println("!!! Unexpected selection change!");
        }
      }
    }
  };
  
  /*------------------------------------------------------------------*/
  /** select a range in the given table cell
   * @param otc table cell.
   * @param iStartOffset start offset of range. 
   * @param iEndOffset end offset of range.
   * @return selected text field in table cell.
   */
  private TextField select(ObjectTableCell otc, int iStartOffset, int iEndOffset)
  {
    // System.out.println(">>> select");
    TextField tfSelected = null;
    // System.out.println("Size before deselect: "+String.valueOf(getSelectionModel().getSelectedCells().size()));
    for (int iSelected = 0; iSelected < getSelectionModel().getSelectedCells().size(); iSelected++)
    {
      @SuppressWarnings("rawtypes")
      TablePosition tp = getSelectionModel().getSelectedCells().get(iSelected);
      // System.out.println("deselect["+String.valueOf(iSelected)+"]: "+String.valueOf(tp.getRow()+","+tp.getColumn()));
      getTableCell(tp.getRow(),tp.getColumn()).deselect();
      // System.out.println("Size after single deselect: "+String.valueOf(getSelectionModel().getSelectedCells().size()));
      // getSelectionModel().clearSelection(tp.getRow(), tp.getTableColumn());
    }
    getSelectionModel().clearSelection();
    // System.out.println("Size after deselect: "+String.valueOf(getSelectionModel().getSelectedCells().size()));
    if (otc != null)
    {
      otc.setFocus();
      getSelectionModel().select(otc.getRow(),otc.getTableColumn());
      // System.out.println("select "+String.valueOf(otc.getRow())+","+String.valueOf(otc.getColumn()));
      tfSelected = otc.selectRange(iStartOffset, iEndOffset);
    }
    // System.out.println("<<< select");
    return tfSelected;
  } /* select */

  /*------------------------------------------------------------------*/
  /** convert an item into a string.
   * @param oItem item
   * @return string representation of item.
   */
  private String getDisplayText(Object oItem)
  {
    String sText = "";
    if (oItem != null)
    {
      sText = oItem.toString();
      if (oItem instanceof byte[])
        sText = "0x"+BU.toHex((byte[])oItem);
      else if ((oItem instanceof Integer) ||
        (oItem instanceof Short) ||
        (oItem instanceof Long) ||
        (oItem instanceof Float) ||
        (oItem instanceof Double) ||
        (oItem instanceof BigInteger) ||
        (oItem instanceof BigDecimal))
      {
        if (oItem instanceof BigDecimal)
          sText = ((BigDecimal)oItem).toPlainString();
      }
      else if ((oItem instanceof java.sql.Timestamp) ||
        (oItem instanceof java.sql.Time) ||
        (oItem instanceof java.sql.Date) ||
        (oItem instanceof java.util.Date) ||
        (oItem instanceof java.util.GregorianCalendar))
      {
        if (oItem instanceof java.sql.Timestamp)
          sText = _du.fromSqlTimestamp((java.sql.Timestamp)oItem);
        else if (oItem instanceof java.sql.Time)
          sText = _du.fromSqlTime((java.sql.Time)oItem);
        else if (oItem instanceof java.sql.Date)
          sText = _du.fromSqlDate((java.sql.Date)oItem);
        else if (oItem instanceof java.util.Date)
          sText = _du.fromDate((java.util.Date)oItem);
        else if (oItem instanceof java.util.GregorianCalendar)
          sText = _du.fromGregorianCalendar((java.util.GregorianCalendar)oItem);
      }
      else if (oItem instanceof Duration)
        sText = SqlLiterals.formatIntervalLiteral(Interval.fromDuration((Duration)oItem));
    }
    return sText;
  } /* getDisplayText */

  /** mouse event filter for text field */
  private EventHandler<MouseEvent> _mef = new EventHandler<MouseEvent>()
  {
    /*----------------------------------------------------------------*/
    /** mouse event on the way to text field is redirected to 
     * containing table cell.
     */
    @Override
    public void handle(MouseEvent me)
    {
      if (me.getTarget() instanceof Text)
      {
        ObjectTableCell otc = (ObjectTableCell)me.getSource();
        /* redirect it to new target */
        Event.fireEvent(otc, me);
        /* prevent the event from reaching the text field */
        me.consume();
      }
    } /* handle */
  };

  /** mouse event handler for table cell */
  private EventHandler<MouseEvent> _meh = new EventHandler<MouseEvent>()
  {
    /*----------------------------------------------------------------*/
    /** mouse event from table cell translates double click events to 
     * show cell events.
     */
    @Override
    public void handle(MouseEvent me)
    {
      if (me.getClickCount() > 1)
      {
        ObjectTableCell otc = (ObjectTableCell)me.getTarget();
        me.consume();
        otc.selectAll();
        Platform.runLater(new Runnable() 
        {
          ObjectTableCell _otc = null;
          public Runnable init(ObjectTableCell otc)
          {
            _otc = otc;
            return this;
          }
          @Override
          public void run()
          {
            ActionEvent ae = new ActionEvent(ObjectListTableView.this,_otc);
            _otc.fireEvent(ae);
          }
        }.init(otc));
      }
      // otherwise bubble up ...
    } /* handle */
  };
  
  @SuppressWarnings({ "rawtypes", "unused" })
  private void displaySelectionAndFocus()
  {
    for (TablePosition tpSelected : getSelectionModel().getSelectedCells())
      System.out.println("Selected "+tpSelected.getRow()+"/"+tpSelected.getColumn());
    TablePosition tpFocus = (TablePosition)getFocusModel().getFocusedCell();
    if (tpFocus != null)
      System.out.println("Focussed "+tpFocus.getRow()+"/"+tpFocus.getColumn());
    else
      System.out.println("No focus!");
  } /* displaySelectionAndFocus */
  
  /** key event handler for table cell */
  private EventHandler<KeyEvent> _keh = new EventHandler<KeyEvent>() 
  {
    /*------------------------------------------------------------------*/
    /** key event from table cell translates enter key pressed events
     * to show cell events and moves the focus of cursor keys.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void handle(KeyEvent ke)
    {
      // System.out.println("Key Event Handler");
      // displaySelectionAndFocus();
      TablePosition tpFocus = (TablePosition)ObjectListTableView.this.getFocusModel().getFocusedCell();
      if (tpFocus != null)
      {
        int iRow = tpFocus.getRow();
        int iColumn = tpFocus.getColumn();
        if (ke.getCode() == KeyCode.ENTER)
        {
          ke.consume();
          ObjectTableCell otc = ObjectListTableView.this.getTableCell(iRow,iColumn);
          otc.selectAll();
          Platform.runLater(new Runnable() 
          {
            ObjectTableCell _otc = null;
            public Runnable init(ObjectTableCell otc)
            {
              _otc = otc;
              return this;
            }
            @Override
            public void run()
            {
              ActionEvent ae = new ActionEvent(ObjectListTableView.this,_otc);
              _otc.fireEvent(ae);
            }
          }.init(otc));
        }
        else if ((ke.getCode() == KeyCode.LEFT) || (ke.getCode() == KeyCode.NUMPAD4))
        {
          ke.consume();
          if (iColumn > 1)
            ObjectListTableView.this.getTableCell(iRow,iColumn-1).setFocus();
        }
        else if ((ke.getCode() == KeyCode.RIGHT) || (ke.getCode() == KeyCode.NUMPAD6))
        {
          ke.consume();
          if (iColumn < ObjectListTableView.this.getColumns().size()-1)
            ObjectListTableView.this.getTableCell(iRow,iColumn+1).setFocus();
        }
        else if ((ke.getCode() == KeyCode.UP) || (ke.getCode() == KeyCode.NUMPAD8))
        {
          ke.consume();
          if (iColumn == 0)
            iColumn = 1;
          if (iRow > 0)
            ObjectListTableView.this.getTableCell(iRow-1,iColumn).setFocus();
        }
        else if ((ke.getCode() == KeyCode.DOWN) || (ke.getCode() == KeyCode.NUMPAD2))
        {
          ke.consume();
          if (iColumn == 0)
            iColumn = 1;
          if (iRow < ObjectListTableView.this.getItems().size()-1)
            ObjectListTableView.this.getTableCell(iRow+1,iColumn).setFocus();
        }
      }
      // otherwise bubble up ...
    } /* handle */
  };

  /*==================================================================*/
  /** ObjectTableCell displays the value.
   */
  public class ObjectTableCell
    extends TableCell<List<Object>, Object>
  {
    /*================================================================*/
    /** class for text field handles selection.
     */
    private class TableCellTextField
      extends TextField
    {
      /** constructor */
      public TableCellTextField()
      {
        super();
        setEditable(false);
      }
    }
    /*================================================================*/
    
    private TableCellTextField _tctf = null;
    private int _iRow = 0;
    public int getRow() { return _iRow; }
    private int _iColumn = 0;
    public int getColumn() { return _iColumn; }

    /*----------------------------------------------------------------*/
    /** add a new table cell to the matrix of table cells.
     */
    private void addTableCell()
    {
      if (getRow() < getItems().size())
      {
        /* the cells are not created in order. We may have to extend the list. */
        for (int i = _llTableCells.size(); i <= getRow(); i++)
          _llTableCells.add(new ArrayList<ObjectTableCell>());
        List<ObjectTableCell> listColumnCells = _llTableCells.get(getRow());
        /* we may also have to extend the list of cells in the column */
        for (int i = listColumnCells.size(); i <= getColumn(); i++)
          listColumnCells.add(null);
        listColumnCells.set(getColumn(), this);
        // System.out.println("Added TableCell "+getId()+" on "+getRow()+"/"+getColumn()+" minheight: "+getMinHeight());
        // System.out.println("setTableHeight");
        // setTableHeight();
      }
      else
        setVisible(false);
    } /* addTableCell */
    
    /*----------------------------------------------------------------*/
    /** constructor registers mouse and key event handlers.
     */
    public ObjectTableCell()
    {
      super();
      setId("cell"+_iCellId);
      _iCellId++;
      /* determine height of cell and recompute table height, when layout appears */
      if ((getLayoutBounds() != null) && (getLayoutBounds().getHeight() > 0))
      {
        setMinHeight(getLayoutBounds().getHeight());
        setTableHeight();
      }
      else
      {
        layoutBoundsProperty().addListener(new ChangeListener<Bounds>() 
        {
          @Override
          public void changed(ObservableValue<? extends Bounds> ovb,
              Bounds bndsOld, Bounds bndsNew)
          {
            ObjectTableCell otc = ObjectTableCell.this;
            if ((otc.getLayoutBounds() != null) && (otc.getLayoutBounds().getHeight() > 0))
            {
              setMinHeight(otc.getLayoutBounds().getHeight());
              setTableHeight();
            }
          }
        });
      }
      /* filter stuff going to the text field */
      addEventFilter(MouseEvent.MOUSE_PRESSED, _mef);
      /* handle mouse events */
      setOnMousePressed(_meh);
      setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      _tctf = new TableCellTextField();
    } /* constructor ObjectTableCell */

    /*----------------------------------------------------------------*/
    /* remove  mouse and keyboard listener from columns 0
     * and color it gray.
     * @return background color style for column.
     */
    private String tweakSelectionHandlers(Object oItem)
    {
      String sStyle = "";
      if ((getColumn() == 0) && 
          ((oItem instanceof Integer) || (oItem instanceof Long)))
      {
        sStyle = FxStyles.sSTYLE_BACKGROUND_LIGHTGREY;
        _tctf.setStyle(sStyle);
        setOnMousePressed(null);
      }
      else
        _tctf.setStyle(FxStyles.sSTYLE_BACKGROUND_TRANSPARENT);
      return sStyle;
    } /* tweakSelectionHandlers */
    
    /*----------------------------------------------------------------*/
    /** updateItem converts item to string and sets alignment.
     * Also, the row and column is determined.
     * @param oItem is a pair of a List<Object> record and an object list 
     *   table column constructed by the cell value factory below.
     * @param bEmpty true, if cell should be empty.
     */
    @Override
    public void updateItem(Object oItem, boolean bEmpty)
    {
      if (oItem != null)
      {
        @SuppressWarnings("unchecked")
        Pair<List<Object>,ObjectListTableColumn> pairItem = (Pair<List<Object>,ObjectListTableColumn>)oItem;
        List<Object> listRecord = pairItem.getKey();
        ObjectListTableColumn oltc = pairItem.getValue();
        _iRow = ObjectListTableView.this.getItems().indexOf(listRecord);
        _iColumn = oltc.getColumn();
        Object oValue = listRecord.get(_iColumn); 
        // System.out.println("update "+getId()+": "+getRow()+"/"+getColumn()+" "+oValue);
        addTableCell();
        if (!bEmpty)
        {
          String sStyle = tweakSelectionHandlers(oValue);
          sStyle = FxStyles.sSTYLE_TABLECELL_PADDING + sStyle;
          setStyle(sStyle);
          /* text and alignment */
          String sText = getDisplayText(oValue);
          Pos pos = Pos.BASELINE_LEFT;
          if (oValue != null)
          {
            if ((oValue instanceof Integer) ||
              (oValue instanceof Short) ||
              (oValue instanceof Long) ||
              (oValue instanceof Float) ||
              (oValue instanceof Double) ||
              (oValue instanceof BigInteger) ||
              (oValue instanceof BigDecimal))
            {
              pos = Pos.BASELINE_RIGHT;
            }
            else if ((oValue instanceof java.sql.Timestamp) ||
              (oValue instanceof java.sql.Time) ||
              (oValue instanceof java.sql.Date) ||
              (oValue instanceof java.util.Date) ||
              (oValue instanceof java.util.GregorianCalendar))
              pos = Pos.BASELINE_CENTER;
          }
          _tctf.setAlignment(pos);
          _tctf.setText(sText);
          setGraphic(_tctf);
          // System.out.println("Updated TableCell "+getId()+" on "+getRow()+"/"+getColumn()+" minheight: "+getMinHeight());
        }
      }
    } /* updateItem */

    /*----------------------------------------------------------------*/
    /** set the focus to this table cell. 
     * (The focus within the table view is managed by the table view.)
     */
    public void setFocus()
    {
      Platform.runLater(new Runnable() 
      {
        @Override
        public void run()
        {
          ObjectListTableView.this.setFocused(true);
          ObjectTableCell otc = ObjectTableCell.this;
          ObjectListTableView.this.getFocusModel().focus(otc.getRow(), otc.getTableColumn());
          otc._tctf.requestFocus();
        }
      });
    } /* setFocus */
    
    /*----------------------------------------------------------------*/
    /** select a range of text in the text field.
     * @param iStartOffset start offset.
     * @param iEndOffset end offset.
     * @returns text field with selected range.
     */
    public TextField selectRange(int iStartOffset, int iEndOffset)
    {
      Platform.runLater(new Runnable() 
      {
        private int _iStart = -1;
        private int _iEnd = -1;
        public Runnable init(int iStart, int iEnd)
        {
          _iStart = iStart;
          _iEnd = iEnd;
          return this;
        }
        @Override
        public void run()
        {
          ObjectTableCell otc = ObjectTableCell.this;
          otc._tctf.requestFocus();
          otc._tctf.selectRange(_iStart, _iEnd);
          ObjectListTableView.this.setFocused(true);
          ObjectListTableView.this.getFocusModel().focus(otc.getRow(), otc.getTableColumn());
        }
      }.init(iStartOffset, iEndOffset));
      return _tctf;
    } /* selectRange */

    /*----------------------------------------------------------------*/
    /** select the table cell.
     */
    public void selectAll()
    {
      Platform.runLater(new Runnable() 
      {
        @Override
        public void run()
        {
          ObjectTableCell otc = ObjectTableCell.this;
          otc._tctf.requestFocus();
          otc._tctf.selectAll();
          ObjectListTableView.this.setFocused(true);
          ObjectListTableView.this.getFocusModel().focus(otc.getRow(), otc.getTableColumn());
        }
      });
    } /* selectAll */
    
    /*----------------------------------------------------------------*/
    /** deselect the table cell.
     */
    public void deselect()
    {
      Platform.runLater(new Runnable() 
      {
        @Override
        public void run()
        {
          ObjectTableCell otc = ObjectTableCell.this;
          otc._tctf.requestFocus();
          otc._tctf.deselect();
        }
      });
    } /* deselect */
    
  } /* class ObjectTableCell */

  /*==================================================================*/
  /** TableColumn class implements access to the current column index.
   */
  public class ObjectListTableColumn
    extends TableColumn<List<Object>, Object>
  {
    /*----------------------------------------------------------------*/
    /** index of this TableColumn
     * @return index of this TableColumn
     */
    public int getColumn() 
    {
      int iColumn = 0;
      if (getTableView() != null)
      {
        iColumn = getTableView().getColumns().indexOf(this);
        if (iColumn < 0)
          iColumn = getTableView().getColumns().size();
      }
      return iColumn;
    } /* getColumn */
    
    /*----------------------------------------------------------------*/
    /** constructor installs cell factory and cell value factory
     * @param sHeader header of table column.
     */
    public ObjectListTableColumn(String sHeader)
    {
      super("");
      setGraphic(new Label(sHeader));
      setId("col"+_iColumnId);
      _iColumnId++;
      /* cell factory creates an ObjectTableCell */
      setCellFactory(new Callback<TableColumn<List<Object>,Object>, TableCell<List<Object>, Object>>()
      {
        @Override
        public TableCell<List<Object>,Object> call(TableColumn<List<Object>,Object> tc)
        {
          return new ObjectTableCell();
        } /* call */
      });
      /* cell value factory creates a pair of object list record and table column. */
      setCellValueFactory(new Callback<CellDataFeatures<List<Object>,Object>,ObservableValue<Object>>()
      {
        @Override
        public ObservableValue<Object> call(CellDataFeatures<List<Object>, Object> cdf)
        {
          ObjectListTableColumn oltc = (ObjectListTableColumn)cdf.getTableColumn();
          List<Object> listRecord = cdf.getValue();
          Object o = new Pair<List<Object>,ObjectListTableColumn>(listRecord,oltc);
          return new SimpleObjectProperty<Object>(o);
        } /* call */
      });
    } /* constructor ObjectListTableColumn */
    
  } /* class ObjectListTableColumn */
  
  /*==================================================================*/
  /*------------------------------------------------------------------*/
  /** constructor
   * @param listHeaders list of header texts.
   */
  public ObjectListTableView(List<String> listHeaders)
  {
    super();
    setCache(false);
    for (int iColumn = 0; iColumn < listHeaders.size(); iColumn++)
    {
      ObjectListTableColumn oatc = new ObjectListTableColumn(listHeaders.get(iColumn));
      getColumns().add(oatc);
    }
    setEditable(false);
    setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    getSelectionModel().setCellSelectionEnabled(true);
    getSelectionModel().getSelectedCells().addListener(_lclSel);
    addEventHandler(KeyEvent.KEY_PRESSED, _keh);
    ObservableList<List<Object>> data = FXCollections.observableArrayList();
    setItems(data);
    // List<Object> items are to be added to this empty data list ... 
    setPlaceholder(new Label("")); // for empty table
    // default: after 10 rows display scroll bar */
    setMaxVisibleRows(_iMaxVisibleRows);
  } /* constructor ObjectListTableView */
  
  /*------------------------------------------------------------------*/
  /** set maximum rows to display (scroll bar limit).
   * @param iMaxVisibleRows maximum visible rows.
   */
  public void setMaxVisibleRows(int iMaxVisibleRows)
  {
    _iMaxVisibleRows = iMaxVisibleRows;
    setTableHeight();
  } /* setMaxRows */

  /* listener for visibility of horizontal scroll bar */
  ChangeListener<Boolean> _sbvl = new ChangeListener<Boolean>() 
  {
    @Override
    public void changed(ObservableValue<? extends Boolean> ovb,
        Boolean bOldVisible, Boolean bNewVisible)
    {
      // System.out.println("Visibility of scrollbar changed to "+bNewVisible+"!");
      setTableHeight();
    }
  };

  /*------------------------------------------------------------------*/
  /** initiates computing of table height 
   * and setting of minHeight, prefHeight, maxHeight.
   * (Listen to them, for notification!)
   */
  public void setTableHeight()
  {
    if ((getItems().size() > 0) &&
        (getItems().size() == _llTableCells.size()) &&
        (getColumns().size() == _llTableCells.get(getItems().size()-1).size()))
    {
      /* number of rows to display */
      int iVisibleRows = getItems().size();
      if (iVisibleRows > _iMaxVisibleRows)
        iVisibleRows = _iMaxVisibleRows;
      visibleRowCountProperty().set(iVisibleRows);
      double dTableHeight = getPrefHeight();
      setMaxHeight(dTableHeight);
      setMinHeight(dTableHeight);
    }      
  } /* setTableHeight */

  /*------------------------------------------------------------------*/
  /** add event handler for the "show cell" double click on a table cell.
   * @param ehavShowCell show cell event handler.
   */
  public void setOnShowCellAction(EventHandler<ActionEvent> ehavShowCell)
  {
    addEventHandler(ActionEvent.ACTION, ehavShowCell);
  } /* setOnShowCellAction */

  /*------------------------------------------------------------------*/
  /** get header texts separated by tabs.
   * @return header texts separated by tabs.
   */
  private String getHeaders()
  {
    StringBuilder sb = new StringBuilder();
    for (int iColumn = 0; iColumn < getColumns().size(); iColumn++)
    {
      if (iColumn > 0)
        sb.append("\t");
      ObjectListTableColumn oltc = (ObjectListTableColumn)getColumns().get(iColumn);
      Label lbl = (Label)oltc.getGraphic();
      sb.append((lbl == null)?"":lbl.getText());
    }
    return sb.toString();
  } /* getHeaders */
  
  /*------------------------------------------------------------------*/
  /** get cell values of row with given index separated by tabs.
   * @param iIndex row.
   * @return cell values of row with given index separated by tabs.
   */
  private String getRecord(int iIndex)
  {
    StringBuilder sb = new StringBuilder();
    for (int iColumn = 0; iColumn < getColumns().size(); iColumn++)
    {
      if (iColumn > 0)
        sb.append("\t");
      sb.append(getDisplayText(getItems().get(iIndex).get(iColumn)));
    }
    return sb.toString();
  } /* getRecord */
  
  /*------------------------------------------------------------------*/
  /** put a string into the system clipboard.
   * @param s string to be copied.
   */
  private void copyString(String s)
  {
    ClipboardContent cc = new ClipboardContent();
    cc.putString(s);
    Clipboard.getSystemClipboard().setContent(cc);
  } /* copyString */

  /*------------------------------------------------------------------*/
  /** copy the header texts and the row values to the clipboard.
   * @param iIndex index of row to be copied.
   */
  public void copyRow(int iIndex)
  {
    copyString(getHeaders() + "\n" + getRecord(iIndex));
  } /* copyRow */

  /*------------------------------------------------------------------*/
  /** copy the whole table (header texts and row values) to the clipboard.
   */
  public void copyTable()
  {
    StringBuilder sb = new StringBuilder(getHeaders());
    for (int iRow = 0; iRow < getItems().size(); iRow++)
    {
      sb.append("\n");
      sb.append(getRecord(iRow));
    }
    copyString(sb.toString());
  } /* copyTable */
  
  /*------------------------------------------------------------------*/
  /** select a range in a table cell.
   * @param iRow row of cell.
   * @param iColumn column of cell.
   * @param iOffset offset of selection in cell.
   * @param iLength length of selection in cell.
   * @return text field with selected range.
   */
  public TextField selectRange(int iRow, int iColumn, int iOffset, int iLength)
  {
    /* make the desired cell visible (and thus make sure, it is updated 
     * and added to in _llTableCells) */
    scrollTo(iRow);
    scrollToColumn(getColumns().get(iColumn));
    snapshot(null,null);
    TextField tf = select(getTableCell(iRow,iColumn),iOffset,iOffset+iLength);
    return tf;
  } /* selectRange */

  /*------------------------------------------------------------------*/
  /** deselect previous selection.
   */
  public void deselect()
  {
    select(null,-1,-1);
  } /* deselect */

} /* class ObjectListTableView */
