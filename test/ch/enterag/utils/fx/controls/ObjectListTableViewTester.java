package ch.enterag.utils.fx.controls;

import java.io.*;
import java.util.*;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.fx.controls.ObjectListTableView.*;
import ch.enterag.utils.lang.Execute;

public class ObjectListTableViewTester
  extends Application
  implements EventHandler<ActionEvent>
{
  /** return codes */
  private static final int iRETURN_OK = 0;
  private static final int iRETURN_ERROR = 8;

  private VBox _vbox = null;
  private ObjectListTableView _oltv = null;

  private void changeSelection(KeyEvent ke, ObjectListTableView oltv, int iRow, int iColumn)
  {
    if ((ke.getCode() == KeyCode.LEFT) || (ke.getCode() == KeyCode.NUMPAD4))
    {
      // move selection to the left
      if (iColumn > 1)
      {
        // TableColumn<List<Object>,Object> tc = (TableColumn<List<Object>,Object>)oltv.getColumns().get(iColumn-1);
        // oltv.getSelectionModel().select(iRow,tc);
        oltv.selectRange(iRow, iColumn-1, 1, 3);
      }
      ke.consume();
    }
    else if ((ke.getCode() == KeyCode.RIGHT) || (ke.getCode() == KeyCode.NUMPAD6))
    {
      // move selection to the right
      if (iColumn < oltv.getColumns().size()-1)
      {
        // TableColumn<List<Object>,Object> tc = (TableColumn<List<Object>,Object>)oltv.getColumns().get(iColumn+1);
        // oltv.getSelectionModel().select(iRow,tc);
        oltv.selectRange(iRow, iColumn+1, 1, 3);
      }
      ke.consume();
    }
    else if ((ke.getCode() == KeyCode.UP) || (ke.getCode() == KeyCode.NUMPAD8))
    {
      // move selection up */
      if (iRow > 0)
      {
        // oltv.getSelectionModel().select(iRow-1,tpSelect.getTableColumn());
        oltv.selectRange(iRow-1, iColumn, 1, 3);
      }
      ke.consume();
    }
    else if ((ke.getCode() == KeyCode.DOWN) || (ke.getCode() == KeyCode.NUMPAD2))
    {
      // move selection down
      if (iRow < oltv.getItems().size()-1)
      {
        // oltv.getSelectionModel().select(iRow+1,tpSelect.getTableColumn());
        oltv.selectRange(iRow+1, iColumn, 1, 3);
      }
      ke.consume();
    }
  } /* changeSelection */
  
  private class KeyHandler
  implements EventHandler<KeyEvent>
  {
    @SuppressWarnings("unchecked")
    @Override
    public void handle(KeyEvent ke)
    {
      ObjectListTableView oltv = (ObjectListTableView)ke.getSource();
      if ((ke.getCode() == KeyCode.C) && ke.isControlDown())
        oltv.copyTable();
      TablePosition<List<Object>,Object> tpFocus = (TablePosition<List<Object>,Object>)oltv.getFocusModel().getFocusedCell();
      if (tpFocus != null)
        System.out.println("Focussed "+tpFocus.getRow()+"/"+tpFocus.getColumn());
      else
        System.out.println("No focus!");
      int iRow = 1;
      int iColumn = 1;
      if (oltv.getSelectionModel().getSelectedItems().size() > 0)
      {
        if (oltv.getSelectionModel().getSelectedItems().size() > 1)
          System.out.println("More than one cell selected!");
        TablePosition<List<Object>,Object> tpSelect = (TablePosition<List<Object>,Object>)oltv.getSelectionModel().getSelectedCells().get(0);
        iRow = tpSelect.getRow();
        iColumn = tpSelect.getColumn();
        System.out.println("Selected "+iRow+"/"+iColumn);
      }
      else
        System.out.println("Nothing selected!");
      
      changeSelection(ke,oltv,iRow,iColumn);
      
      tpFocus = (TablePosition<List<Object>,Object>)oltv.getFocusModel().getFocusedCell();
      if (tpFocus != null)
        System.out.println("After Focussed "+tpFocus.getRow()+"/"+tpFocus.getColumn());
      else
        System.out.println("After No focus!");
      iRow = 1;
      iColumn = 1;
      if (oltv.getSelectionModel().getSelectedItems().size() > 0)
      {
        if (oltv.getSelectionModel().getSelectedItems().size() > 1)
          System.out.println("After More than one cell selected!");
        TablePosition<List<Object>,Object> tpSelect = (TablePosition<List<Object>,Object>)oltv.getSelectionModel().getSelectedCells().get(0);
        iRow = tpSelect.getRow();
        iColumn = tpSelect.getColumn();
        System.out.println("After Selected "+iRow+"/"+iColumn);
      }
      else
        System.out.println("After Nothing selected!");
    }
  }
  
  private KeyHandler _kh = new KeyHandler();
  
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    int iReturn = iRETURN_ERROR;
    try
    {
      launch(args);
      iReturn = iRETURN_OK;
    }
    catch (Exception e) { System.err.println(e.getClass().getName()+": "+e.getMessage()); }
    System.exit(iReturn);
  }
  
  @Override
  public void start(Stage stage) throws Exception
  {
    stage.initStyle(StageStyle.UTILITY);
    _vbox = new VBox(); 
    _vbox.setPadding(new Insets(10));
    _vbox.setSpacing(10);
    _vbox.setAlignment(Pos.TOP_LEFT);
    _vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    _vbox.setMinWidth(FxSizes.fromEms(40));
    _vbox.setMinHeight(FxSizes.fromExes(40));
    _oltv = new ObjectListTableView(Arrays.asList("Id","Name","Date"));
    System.out.println("Created");
    for (int iColumn = 0; iColumn < _oltv.getColumns().size(); iColumn++)
    {
      ObjectListTableColumn oltc = (ObjectListTableColumn)_oltv.getColumns().get(iColumn);
      oltc.setMinWidth(FxSizes.fromEms(5.0));
      oltc.setPrefWidth(FxSizes.fromEms(10.0));
      oltc.setMaxWidth(FxSizes.fromEms(20.0));
    }
    _oltv.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    Date now = new Date();
    Date tomorrow = new Date(now.getTime()+1000*60*60*24);
    Date yesterday = new Date(now.getTime()-1000*60*60*24);
    _oltv.getItems().add(Arrays.asList((Object)35,"First",now));
    System.out.println("Row 0 added");
    _oltv.getItems().add(Arrays.asList((Object)(-35),"Second",tomorrow));
    System.out.println("Row 1 added");
    _oltv.getItems().add(Arrays.asList((Object)0,"Third",yesterday));
    System.out.println("Row 2 added");
    _oltv.getItems().add(Arrays.asList((Object)100,"Fourth",now));
    System.out.println("Row 3 added");
    _oltv.setOnShowCellAction(this);
    // _oltv.setOnKeyPressed(_kh);
    _oltv.addEventHandler(KeyEvent.KEY_PRESSED, _kh);
    _vbox.getChildren().add(_oltv);
    // setting maxX, maxY would make fewer cells visible
    Scene scene = new Scene(_vbox);
    // snapshot after inclusion in scene forces update of 
    // (only visible!!!) cells and thus computation of table height
    // (limited to max visible rows)
    _vbox.snapshot(null, null);
    // limiting the max height now, introduces vertical scroll bar (same for width)
    _oltv.setMaxHeight(0.5*_oltv.getMinHeight());
    _oltv.setMinHeight(_oltv.getMaxHeight());
    _vbox.setMinHeight(_oltv.getMinHeight()+20.0);
    System.out.println("VBox: "+_vbox.getMinWidth()+"/"+_vbox.getMinHeight());
    stage.setScene(scene);
    stage.setHeight(_vbox.getMinHeight()+35.0);
    stage.show();
    System.out.println("Stage displayed");
  } /* start */

  /*------------------------------------------------------------------*/
  /** handle show cell event.
   */
  @Override
  public void handle(ActionEvent ae)
  {
    if (ae.getTarget() instanceof ObjectListTableView.ObjectTableCell)
    {
      ObjectListTableView.ObjectTableCell otc = (ObjectListTableView.ObjectTableCell)ae.getTarget();
      System.out.println("show cell ("+String.valueOf(otc.getRow())+","+String.valueOf(otc.getColumn())+")");
      System.out.println(otc.getWidth()+"x"+otc.getHeight());
      File fileTextEditor = new File("gedit");
      String[] asCommand = new String[] {fileTextEditor.getPath()};
      Execute ex = Execute.execute(asCommand);
      if (ex.getResult() != 0)
      {
        System.err.println("Err: "+ex.getStdErr());
        System.out.println("Out: "+ex.getStdOut());
      }
      System.out.println("handled!");
    } /* handle */
    
  } /* handle */

}
