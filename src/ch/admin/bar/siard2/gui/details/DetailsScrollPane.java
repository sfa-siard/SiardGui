/*======================================================================
The details scroll pane contains the meta data editor and the meta data table.  
Application: SIARD GUI
Description: The details scroll pane contains the meta data editor and the meta data table. 
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 10.08.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.details;

import java.io.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.layout.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.gui.SiardGui;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.fx.controls.*;

/*====================================================================*/
/** The details scroll pane contains the meta data editor and the meta data table.
 * @author Hartwig
 */
public class DetailsScrollPane
  extends ScrollPane
  implements EventHandler<ActionEvent>
{
  private VBox _vbox = null;
  private Object _oMetaData = null;
  private Class<?> _clsTableData = null;
  private MetaDataEditor _mde = new MetaDataEditor();
  public MetaDataEditor getMetaDataEditor() { return _mde; }
  private ObjectListTableView _oltv = null;
  public ObjectListTableView getTableView() { return _oltv; }
  // padding inside
  private static final double dINNER_PADDING = 10.0;
  // vertical spacing of elements
  private static final double dVSPACING = 10.0;
  
  /*------------------------------------------------------------------*/
  /** set new meta data to display in the details scroll pane.
   * @param oMetaData new meta data.
   * @param clsTable class of table data.
   */
  public void setMetaData(Object oMetaData, Class<?> clsTableData)
  {
    _oMetaData = oMetaData;
    _clsTableData = clsTableData;
    reset();
  } /* setMetaData */

  /*------------------------------------------------------------------*/
  /** redisplay the current meta data in the current language.
   */
  public void reset() 
  {
    _vbox.getChildren().remove(_oltv);
    _vbox.minHeightProperty().unbind();
    _oltv = null;
    if (_oMetaData instanceof MetaData)
    {
      MetaData md = (MetaData)_oMetaData;
      _mde.setMetaData(md);
      if (_clsTableData.equals(MetaSchema.class))
        _oltv = MetaDataTableFactory.newMetaSchemasTableView(md);
      else if (_clsTableData.equals(MetaUser.class))
        _oltv = MetaDataTableFactory.newMetaUsersTableView(md);
      else if (_clsTableData.equals(MetaRole.class))
        _oltv = MetaDataTableFactory.newMetaRolesTableView(md);
      else if (_clsTableData.equals(MetaPrivilege.class))
        _oltv = MetaDataTableFactory.newMetaPrivilegesTableView(md);
    }
    else if (_oMetaData instanceof MetaSchema)
    {
      MetaSchema ms = (MetaSchema)_oMetaData;
      _mde.setMetaData(ms);
      if (_clsTableData.equals(MetaType.class))
        _oltv = MetaDataTableFactory.newMetaTypesTableView(ms);
      else if (_clsTableData.equals(MetaTable.class))
        _oltv = MetaDataTableFactory.newMetaTablesTableView(ms);
      else if (_clsTableData.equals(MetaView.class))
        _oltv = MetaDataTableFactory.newMetaViewsTableView(ms);
      else if (_clsTableData.equals(MetaRoutine.class))
        _oltv = MetaDataTableFactory.newMetaRoutinesTableView(ms);
    }
    else if (_oMetaData instanceof MetaType)
    {
      MetaType mt = (MetaType)_oMetaData;
      _mde.setMetaData(mt);
      _oltv = MetaDataTableFactory.newMetaAttributesTableView(mt);
    }
    else if (_oMetaData instanceof MetaTable)
    {
      MetaTable mt = (MetaTable)_oMetaData;
      _mde.setMetaData(mt);
      if (_clsTableData.equals(MetaColumn.class))
        _oltv = MetaDataTableFactory.newMetaColumnsTableView(mt);
      else if (_clsTableData.equals(MetaUniqueKey.class))
        _oltv = MetaDataTableFactory.newMetaCandidateKeysTableView(mt);
      else if (_clsTableData.equals(MetaForeignKey.class))
        _oltv = MetaDataTableFactory.newMetaForeignKeysTableView(mt);
    }
    else if (_oMetaData instanceof MetaView)
    {
      MetaView mv = (MetaView)_oMetaData;
      _mde.setMetaData(mv);
      _oltv = MetaDataTableFactory.newMetaColumnsTableView(mv);
    }
    else if (_oMetaData instanceof MetaRoutine)
    {
      MetaRoutine mr = (MetaRoutine)_oMetaData;
      _mde.setMetaData(mr);
      _oltv = MetaDataTableFactory.newMetaParametersTableView(mr);
    }
    else if (_oMetaData instanceof MetaColumn)
    {
      MetaColumn mc = (MetaColumn)_oMetaData;
      _mde.setMetaData(mc);
      try
      {
        if (mc.getMetaFields() > 0)
          _oltv = MetaDataTableFactory.newMetaFieldsTableView(mc);
      }
      catch(IOException ie) { }
    }
    else if (_oMetaData instanceof MetaField)
    {
      MetaField mf = (MetaField)_oMetaData;
      _mde.setMetaData(mf);
      try
      {
        if (mf.getMetaFields() > 0)
          _oltv = MetaDataTableFactory.newMetaFieldsTableView(mf);
      }
      catch(IOException ie) { }
    }
    else if ((_oMetaData instanceof RecordExtract))
    {
      RecordExtract re = (RecordExtract)_oMetaData;
      _mde.setMetaData(re);
      try { _oltv = new RecordExtractTableView(re); }
      catch(IOException ie) { /* meta data only */ }
    }
    else
      _mde.setMetaData(_oMetaData);
    setFocused(true);
    double dMinWidth = 2*dINNER_PADDING;
    double dMinHeight = 2*dINNER_PADDING;
    if (_mde != null)
    {
      if (!_vbox.getChildren().contains(_mde))
        _vbox.getChildren().add(_mde);
      dMinWidth += _mde.getMinWidth();
      dMinHeight += _mde.getMinHeight();
      if (_oltv == null)
        VBox.setVgrow(_mde, Priority.ALWAYS);
      else
        VBox.setVgrow(_mde, Priority.NEVER);
    }
    if (_oltv != null)
    {
      _vbox.getChildren().add(_oltv);
      if (_mde != null)
        dMinHeight += dVSPACING;
      _vbox.snapshot(null, null);
      dMinHeight += _oltv.getMinHeight();
      _vbox.setMinWidth(dMinWidth);
      _vbox.setMinHeight(dMinHeight);
      VBox.setVgrow(_oltv, Priority.ALWAYS);
    }
  } /* reset */

  /*------------------------------------------------------------------*/
  /** constructor
   * empty VBox.
   */
  public DetailsScrollPane()
  {
    super();
    _mde.setOnMetaDataChangeAction(this);
    setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    _vbox = new VBox();
    _vbox.setPadding(new Insets(dINNER_PADDING));
    _vbox.setSpacing(dVSPACING);
    _vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    setContent(_vbox);
    setFitToWidth(true);
    setFitToHeight(true);
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** handle meta data change action for meta data editor.
   * @param ae action event.
   */
  @Override
  public void handle(ActionEvent ae)
  {
    SiardGui.getSiardGui().setTitle();
  } /* handle */
  
  /*------------------------------------------------------------------*/
  /** are there any edited changes not yet applied to meta data?
   * @return true, if meta data details were edited but not yet applied 
   * to meta data.
   */
  public boolean isChanged()
  {
    return _mde.isChanged();
  } /* isChanged */

} /* DetailsScrollPane */
