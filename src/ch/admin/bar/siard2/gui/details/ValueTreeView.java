/*======================================================================
ValueTreeView represents a UDT oder array value.
Application : Siard2
Description : ValueTreeView represents a UDT oder array value.  
Platform    : Java 7, JavaFX 2.2   
------------------------------------------------------------------------
Copyright  : 2017, Enter AG, Rüti ZH, Switzerland
Created    : 14.08.2017, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.gui.details;

import java.io.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.util.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siard2.api.generated.*;
import ch.admin.bar.siard2.gui.dialogs.*;
import ch.enterag.utils.EU;
import ch.enterag.utils.fx.*;

/*--------------------------------------------------------------------*/
/** ValueTreeView represents a UDT oder array value.
 * @author Hartwig Thomas
 */
public class ValueTreeView
  extends TreeView<String>
  implements Callback<TreeView<String>,TreeCell<String>>
{
  private String _sLocation = null;
  private double _dWidth = 0.0;
  private int _iCells = 0;
  private double _dCellHeight = 0.0;
  
  /*==================================================================*/
  private class MouseEventHandler
    implements EventHandler<MouseEvent>
  {
    /*----------------------------------------------------------------*/
    /** mouse event from table cell translates double click events to 
     * LOB cell events.
     */
    @Override
    public void handle(MouseEvent me)
    {
      if (me.getClickCount() > 1)
      {
        ValueTreeCell vtc = (ValueTreeCell)me.getSource();
        ActionEvent ae = new ActionEvent(ValueTreeView.this,vtc);
        vtc.fireEvent(ae);
      }
    } /* handle */
  } /* MouseEventHandler */
  private MouseEventHandler _meh = new MouseEventHandler();
  
  /*==================================================================*/
  private class KeyEventHandler
    implements EventHandler<KeyEvent>
  {
    /*------------------------------------------------------------------*/
    /** key event from table cell translates enter key pressed events
     * to LOB cell events.
     */
    @Override
    public void handle(KeyEvent ke)
    {
      if (ke.getCode() == KeyCode.ENTER)
      {
        ValueTreeCell vtc = (ValueTreeCell)ke.getSource();
        ActionEvent ae = new ActionEvent(ValueTreeView.this,vtc);
        vtc.fireEvent(ae);
      }
    } /* handle */
  } /* KeyEventHandler */
  private KeyEventHandler _keh = new KeyEventHandler();

  /*==================================================================*/
  /** Tree cell for database value.
   * installs keyboard and mouse action handlers, if the tree cell
   * represents a LOB.
   */
  public class ValueTreeCell
    extends TreeCell<String>
    implements ChangeListener<TreeItem<String>>
  {
    /*----------------------------------------------------------------*/
    /** value data of associated tree item.
     * @return value data of associated tree item.
     */
    public Value getDataValue()
    {
      Value value = null;
      if (getTreeItem() instanceof ValueTreeItem)
        value = ((ValueTreeItem)getTreeItem()).getDataValue();
      return value;
    } /* getDataValue */
    
    /*----------------------------------------------------------------*/
    /** maximum inline size of associated tree item.
     * @return maximum inline size of associated tree item.
     */
    public int getMaxInlineSize()
    {
      int iMaxInlineSize = -1;
      if (getTreeItem() instanceof ValueTreeItem)
        iMaxInlineSize = ((ValueTreeItem)getTreeItem()).getMaxInlineSize();
      return iMaxInlineSize;
    } /* getMaxInlineSize */
    
    /*----------------------------------------------------------------*/
    /** get a displayable location indication of the content in the table.
     * @return location indication.
     */
    public String getLocation()
    {
      String sLocation = null;
      MetaValue mv = getDataValue().getMetaValue();
      while(mv instanceof MetaField)
      {
        if (sLocation != null)
          sLocation = String.valueOf(mv.getName())+"."+sLocation;
        else
          sLocation = String.valueOf(mv.getName());
        MetaField mf = (MetaField)mv;
        mv = mf.getParentMetaField() == null? mf.getParentMetaColumn(): mf.getParentMetaField();
      }
      sLocation = _sLocation+"."+sLocation;
      return sLocation;
    } /* getLocation */
    
    /*----------------------------------------------------------------*/
    /** constructor captures changes in TreeItem.
     */
    public ValueTreeCell()
    {
      super();
      treeItemProperty().addListener(this);
    } /* constructor */
    
    /*----------------------------------------------------------------*/
    /** on change to TreeItem install listeners for mouse and keyboard,
     * if it is a value.
     */
    @Override
    public void changed(
      ObservableValue<? extends TreeItem<String>> ovti,
      TreeItem<String> tiOld, TreeItem<String> tiNew)
    {
      if (tiNew instanceof ValueTreeItem)
      {
        ValueTreeItem vti = (ValueTreeItem)tiNew;
        try
        {
          MetaValue mv = vti.getDataValue().getMetaValue();
          MetaType mt = mv.getMetaType();
          if ((vti.isMetaNode()) ||
            ((mt != null) && (mt.getCategoryType() == CategoryType.UDT)) ||
            (mv.getCardinality() > 0))
            setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
          else
          {
            setOnKeyPressed(_keh);
            setOnMousePressed(_meh);
          }
        }
        catch(IOException ie) { System.err.println(EU.getExceptionMessage(ie)); }
      }
    } /* changed */
    
    /*----------------------------------------------------------------*/
    /** update tree cell with new item.
     */
    @Override
    public void updateItem(String sItem, boolean bEmpty) 
    {
      super.updateItem(sItem, bEmpty);
      String sText = null;
      if (!bEmpty)
        sText = sItem == null ? "" : sItem;
      setText(sText);
      setMinWidth(FxSizes.getNodeWidth(this));
      setMinHeight(FxSizes.getNodeHeight(this));
      if (_dWidth < getMinWidth())
        _dWidth = getMinWidth();
      _dCellHeight = getMinHeight();
    } /* updateItem */

    /*----------------------------------------------------------------*/
    /** clear the selection
     */
    public void deselect()
    {
      getSelectionModel().clearSelection();
    } /* deselect */
    
  } /* class ValueTreeCell */
  
  /*==================================================================*/
  /** Tree item for database value.
   */
  private class ValueTreeItem
    extends TreeItem<String>
  {
    private boolean _bMetaNode = false;
    public boolean isMetaNode() { return _bMetaNode; }
    
    private Value _value = null;
    public Value getDataValue() { return _value; }
    
    private int _iMaxInlineSize = -1;
    public int getMaxInlineSize() { return _iMaxInlineSize; }

    /*------------------------------------------------------------------*/
    /** get the text for a tree item from the value.
     * @param mv meta data for value.
     * @param value value.
     * @param iMaxInlineSize maximum displayable size.
     * @return tree item.
     * @throws IOException if an I/O exception occurred.
     */
    public String getText(Value value, int iMaxInlineSize)
      throws IOException
    {
      String sText = null;
      MetaValue mv = value.getMetaValue();
      MetaType mt = mv.getMetaType();
      if (((mt != null) && (mt.getCategoryType() == CategoryType.UDT)) ||
        (mv.getCardinality() > 0))
      {
        sText = mv.getType();
        if (mt != null)
          sText = mt.getName();
      }
      else
      {
        sText = ValueDialog.getText(value,iMaxInlineSize);
        if (sText == null)
        {
          if (value.getCharLength() > iMaxInlineSize)
            sText = "{clob: "+String.valueOf(value.getCharLength())+"}";
          else if (value.getByteLength() > iMaxInlineSize/2)
            sText = "{blob: "+String.valueOf(value.getByteLength())+"}";
        }
      }
      return sText;
    } /* getText */

    /*----------------------------------------------------------------*/
    /** constructor stores relevant data value and builds tree recursively.
     * @param value data value
     * @param iMaxInlineSize maximum inline size to be displayed.
     * @param bMetaNode true, if the name of the meta data is to be
     *   displayed.
     * @throws IOException if an I/O error occurred.
     */
    public ValueTreeItem(Value value, int iMaxInlineSize, boolean bMetaNode)
      throws IOException
    {
      super();
      _bMetaNode = bMetaNode;
      _value = value;
      _iMaxInlineSize = iMaxInlineSize;
      _iCells++;
      MetaValue mv = value.getMetaValue();
      String sText = mv.getName();
      if (!bMetaNode)
        sText = getText(value, iMaxInlineSize);
      setValue(sText);
      setExpanded(true);
      /** if it is a meta node, then it has a single child (same but non-Meta */
      if (bMetaNode)
      {
        ValueTreeItem vti = new ValueTreeItem(value,iMaxInlineSize,false);
        getChildren().add(vti);
      }
      else
      {
        MetaType mt = mv.getMetaType();
        if (mv.getCardinality() >= 0)
        {
          for (int iElement = 0; iElement < value.getElements(); iElement++)
          {
            Value valueElement = value.getElement(iElement);
            ValueTreeItem vtiElement = new ValueTreeItem(valueElement,iMaxInlineSize,true);
            getChildren().add(vtiElement);
          }
        }
        else if ((mt != null) && (mt.getCategoryType() == CategoryType.UDT))
        {
          for (int iAttribute = 0; iAttribute < value.getAttributes(); iAttribute++)
          {
            Value valueAttribute = value.getAttribute(iAttribute);
            ValueTreeItem vtiAttribute = new ValueTreeItem(valueAttribute,iMaxInlineSize,true);
            getChildren().add(vtiAttribute);
          }
        }
      }
    }
  } /* class ValueTreeItem */
  
  /*------------------------------------------------------------------*/
  /** cell factory call
   */
  @Override
  public TreeCell<String> call(TreeView<String> tv)
  {
    return new ValueTreeCell();
  } /* TreeCell */
  
  /*------------------------------------------------------------------*/
  /** constructor creates the tree view for displaying arrays or UDTs.
   * @param cell cell data.
   */
  public ValueTreeView(String sLocation, Cell cell, int iMaxInlineSize)
    throws IOException
  {
    super();
    setRoot(new ValueTreeItem(cell,iMaxInlineSize,false));
    _sLocation = sLocation;
    setEditable(false);
    setCellFactory(this);
    /* snapshop in getNodeWidth() forces creation of TreeCells
     * and thus computation of _dWidth and _dCellHeigth */
    FxSizes.getNodeWidth(this);
    double dHeight = _iCells*_dCellHeight;
    setPrefWidth(_dWidth+20.0);
    setPrefHeight(dHeight+2.0);
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** add event handler for the "show cell" double click on a table value.
   * @param ehavShowValue show value event handler.
   */
  public void setOnShowValue(EventHandler<ActionEvent> ehavShowValue)
  {
    addEventHandler(ActionEvent.ACTION, ehavShowValue);
  } /* setOnShowValue */
  
} /* class ValueTreeView */
