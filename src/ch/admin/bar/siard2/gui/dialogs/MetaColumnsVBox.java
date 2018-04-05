/*======================================================================
VBox with list view with check boxes for column names.
Application : Siard2
Description : VBox with list view with check boxes for column names. 
Platform    : Java 7, JavaFX 2.2   
------------------------------------------------------------------------
Copyright  : 2017, Enter AG, Rüti ZH, Switzerland
Created    : 31.08.2017, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.gui.dialogs;

import java.io.*;
import java.sql.*;
import java.util.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import javafx.util.*;
import javafx.util.Pair;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.identifier.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.*;

/*====================================================================*/
/** VBox with list view with check boxes for column names.
 * @author Hartwig Thomas
 */
public class MetaColumnsVBox
  extends VBox
  implements EventHandler<ActionEvent>
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(MetaColumnsVBox.class.getName());
  // padding inside
  private static final double dINNER_PADDING = 0.0;
  // horizontal spacing of elements
  private static final double dHSPACING = 10.0;
  // vertical spacing of elements
  private static final double dVSPACING = 10.0;
  // width of list view im ems 
  private static final int iLISTVIEW_COLUMNS = 30;
  // width of list view im exes 
  private static final int iLISTVIEW_ROWS = 20;
  
  /* Button select all */
  private Button _btnAll = null;
  /* Button select none */
  private Button _btnNone = null;
  /* ListView with columns */
  private MetaColumnsListView _mclv = null;
  public MetaColumnsListView getMetaColumnsListView() { return _mclv; }

  /*------------------------------------------------------------------*/
  /** a column can be selected for search if it has a simple type.
   * @param mc column meta data.
   * @return true, if column can be selected for search.
   */
  private boolean isSelectable(MetaColumn mc)
  {
    boolean bSelectable = true;
    try
    {
      if ((mc.getMetaType() != null) && 
          (mc.getMetaType().getCategoryType() == CategoryType.UDT))
        bSelectable = false;
      else if (mc.getCardinality() > 0)
        bSelectable = false;
      else
      {
        int iType = mc.getPreType();
        if ((iType == Types.CLOB) ||
            (iType == Types.NCLOB) ||
            (iType == Types.SQLXML) ||
            (iType == Types.BLOB) ||
            (iType == Types.BINARY) ||
            (iType == Types.VARBINARY))
          bSelectable = false;
      }
    }
    catch(IOException ie) { _il.exception(ie); }
    return bSelectable;
  } /* isSelectable */
  
  /*------------------------------------------------------------------*/
  /** handle the pressing of one of the buttons.
   * @param ae action event.
   */
  @Override
  public void handle(ActionEvent ae)
  {
    for (int iColumn = 0; iColumn < _mclv.getItems().size(); iColumn++)
    {
      Pair<MetaColumn,BooleanProperty> pmb = _mclv.getItems().get(iColumn);
      if (ae.getSource() == _btnAll)
      {
        /* select all selectable columns */
        if (isSelectable(pmb.getKey()))
          pmb.getValue().set(true);
      }
      else if (ae.getSource() == _btnNone)
        /* deselect all columns */
        pmb.getValue().set(false);
    }
  } /* handle */
  
  /*------------------------------------------------------------------*/
  /** create a HBox with All and None button.
   * @param sAll text on Select All button.
   * @param sNone text on Select None button.
   * @return HBox.
   */
  private HBox createAllNoneButtonsHBox(String sAll, String sNone)
  {
    HBox hbox = new HBox();
    hbox.setSpacing(dHSPACING);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    hbox.setAlignment(Pos.BASELINE_RIGHT);
    /* buttons and their width */
    _btnAll = new Button(sAll);
    _btnAll.setOnAction(this);
    hbox.getChildren().add(_btnAll);
    _btnNone = new Button(sNone);
    _btnNone.setOnAction(this);
    hbox.getChildren().add(_btnNone);
    return hbox;
  } /* createAllNoneButtonsHBox */

  /*==================================================================*/
  /** check box list cell for pairs of column meta data and selected
   * property.
   */
  private class MetaColumnCheckBoxListCell
    extends CheckBoxListCell<Pair<MetaColumn,BooleanProperty>>
  {
    public MetaColumnCheckBoxListCell(
      Callback<Pair<MetaColumn,BooleanProperty>, ObservableValue<Boolean>> getSelectedProperty)
    {
      super(getSelectedProperty);
    } /* constructor with selected property */
    @Override
    public void updateItem(Pair<MetaColumn,BooleanProperty> pmb, boolean bEmpty)
    {
      super.updateItem(pmb,bEmpty);
      String sText = null;
      if (!bEmpty)
      {
        sText = "";
        if (pmb != null)
        {
          MetaColumn mc = pmb.getKey();
          setDisable(!isSelectable(mc));
          sText = mc.getName();
          try
          {
            String sType = mc.getType();
            if (mc.getTypeName() != null)
            {
              QualifiedId qiType = new QualifiedId(null,mc.getTypeSchema(),mc.getTypeName());
              sType = qiType.format();
            }
            sText = sText + " ("+sType+")";
          }
          catch(IOException ie) { _il.exception(ie); }
        }
      }
      setText(sText);
    } /* updateItem */
    
  } /* class MetaColumnCheckBoxListCell */
  
  /*==================================================================*/
  /** list view with pairs of meta column and selected property.
   */
  private class MetaColumnsListView
    extends ListView<Pair<MetaColumn,BooleanProperty>>
  {
    public MetaColumnsListView(MetaTable mt)
    {
      super();
      setCellFactory(new Callback<ListView<Pair<MetaColumn,BooleanProperty>>,ListCell<Pair<MetaColumn,BooleanProperty>>>()
        {
          /** create a list cell for a list view */
          @Override
          public ListCell<Pair<MetaColumn, BooleanProperty>> call(
              ListView<Pair<MetaColumn, BooleanProperty>> lv)
          {
            return new MetaColumnCheckBoxListCell(new Callback<Pair<MetaColumn,BooleanProperty>, ObservableValue<Boolean>>() 
              {
                /** return the boolean observable of the pair */
                @Override
                public ObservableValue<Boolean> call(
                    Pair<MetaColumn, BooleanProperty> pmb)
                {
                  return pmb.getValue();
                } /* call */
              });
          } /* call */
        });
      ObservableList<Pair<MetaColumn,BooleanProperty>> items = FXCollections.observableArrayList();
      setItems(items); // empty
      for (int iColumn = 0; iColumn < mt.getMetaColumns(); iColumn++)
      {
        MetaColumn mc = mt.getMetaColumn(iColumn);
        getItems().add(
          new Pair<MetaColumn,BooleanProperty>(mc,
            new SimpleBooleanProperty(isSelectable(mc))));
      }
      setMinWidth(FxSizes.fromEms(iLISTVIEW_COLUMNS));
      setMinHeight(FxSizes.fromExes(iLISTVIEW_ROWS));
    } /* constructor */
  } /* class MetaColumnsListView */
  /*==================================================================*/
  
  /** constructor
   * @param sExplanation explanatory text above list.
   * @param mt table meta data for columns.
   * @param sAll text for select all button.
   * @param sNone text for select none button.
   */
  public MetaColumnsVBox(String sExplanation, MetaTable mt, String sAll, String sNone)
  {
    setPadding(new Insets(dINNER_PADDING));
    setSpacing(dVSPACING);
    setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    getChildren().add(new Label(sExplanation));
    _mclv = new MetaColumnsListView(mt);
    getChildren().add(_mclv);
    getChildren().add(createAllNoneButtonsHBox(sAll,sNone));
    setMinWidth(FxSizes.getNodeWidth(this));
    setMinHeight(FxSizes.getNodeHeight(this));
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** add a change listener to the selection check boxes.
   * @param clb change listener.
   */
  public void addSelectionChangeListener(ChangeListener<Boolean> clb)
  {
    for (int iColumn = 0; iColumn < _mclv.getItems().size(); iColumn++)
      _mclv.getItems().get(iColumn).getValue().addListener(clb);
  } /* addSelectionChangeListener */
  
  /*------------------------------------------------------------------*/
  /** return the list of selected columns.
   * @return list of selected columns meta data.
   */
  public List<MetaColumn> getSelection()
  {
    List<MetaColumn> listSelected = new ArrayList<MetaColumn>();
    for (int iColumn = 0; iColumn < _mclv.getItems().size(); iColumn++)
    {
      Pair<MetaColumn,BooleanProperty> pmb = _mclv.getItems().get(iColumn);
      if (pmb.getValue().get())
        listSelected.add(pmb.getKey());
    }
    return listSelected;
  } /* getSelection */
  
} /* class MetaColumnsVBox */
