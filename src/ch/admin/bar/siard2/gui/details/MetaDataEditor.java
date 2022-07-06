/*======================================================================
The editor portion of the details pane for meta data.  
Application: SIARD GUI
Description: The editor portion of the details pane for meta data. 
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 25.07.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.details;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.sql.Types;
import java.util.*;

import javafx.beans.value.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import ch.enterag.utils.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.fx.dialogs.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.gui.*;

/*====================================================================*/
/** The editor portion of the details pane for meta data.
 * @author Hartwig Thomas
 */
public class MetaDataEditor
  extends VBox
  implements EventHandler<ActionEvent>, ChangeListener<String>
{
  // padding inside the dialog's VBox
  private static final double dOUTER_PADDING = 0.0;
  // padding inside
  private static final double dINNER_PADDING = 0.0;
  // vertical spacing of elements
  private static final double dVSPACING = 0.0;
  // horizontal spacing of elements
  private static final double dHSPACING = 10.0;
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(MetaDataEditor.class.getName());
  private SiardBundle _sb = SiardBundle.getSiardBundle();
  private DU _du = DU.getInstance(_sb.getLanguage(), _sb.getDateFormat());
  private Object _oMetaData = null;
  private boolean _bEditable = false;
  private boolean _bChanged = false;
  public boolean isChanged() { return _bChanged; }
  private double _dLabelWidth = -1.0;
  private String _sLabelLanguage = null;
  private Button _btnApply = null;
  private Button _btnReset = null;
  
  /*------------------------------------------------------------------*/
  /** change event from text input control.
   * @param ov observable value.
   */
  @Override
  public void changed(ObservableValue<? extends String> ov,
    String sOld, String sNew)
  {
    _bChanged = true;
    _btnApply.setDisable(false);
    _btnReset.setDisable(false);
    MainMenuBar.getMainMenuBar().restrict();
  } /* changed */

  /*------------------------------------------------------------------*/
  /** return the maximum label width for all labels in the current language.
   * @return label width
   */
  private double getLabelWidth()
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    if (!sb.getLanguage().equals(_sLabelLanguage))
    {
      double dLabelWidth = -1.0;
      Set<String> setProp = sb.stringPropertyNames();
      for (Iterator<String> iterProperty = setProp.iterator(); iterProperty.hasNext(); )
      {
        String sKey = iterProperty.next();
        if (sKey.startsWith("label."))
        {
          String sValue = (String)sb.getProperty(sKey);
          if (dLabelWidth < FxSizes.getTextWidth(sValue))
            dLabelWidth = FxSizes.getTextWidth(sValue);
        }
      }
      _dLabelWidth = dLabelWidth;
      _sLabelLanguage = sb.getLanguage();
    }
    return _dLabelWidth;
  } /* getLabelWidth */
  
  /*------------------------------------------------------------------*/
  /** set the value of the named property of the current meta data object.
   * @param sProperty property name.
   * @param sValue value.
   */
  private void setProperty(String sProperty, String sValue)
  {
    /* find method "set"+sProperty of _oMetaData object and invoke it. */
    String sMethodName = "set"+sProperty;
    try
    {
      Method methodSet = null;
      if (!sProperty.equals("LobFolder"))
      {
        methodSet = _oMetaData.getClass().getDeclaredMethod(sMethodName, String.class);
        methodSet.invoke(_oMetaData, sValue);
      }
      else 
      {
        URI uriValue = null;
        if (sValue != null)
          uriValue = URI.create(sValue);
        methodSet = _oMetaData.getClass().getDeclaredMethod(sMethodName, URI.class);
        methodSet.invoke(_oMetaData, uriValue);
      }
    }
    catch(IllegalAccessException iae) { _il.exception(iae); }
    catch (InvocationTargetException ite) {_il.exception(ite); }
    catch (NoSuchMethodException nsme) { throw new IllegalArgumentException("Method \""+sMethodName+"\" of class "+_oMetaData.getClass().getSimpleName()+" not found!",nsme); }
  } /* setProperty */

  /*------------------------------------------------------------------*/
  /** return the value of the named property of the meta data object.
   * @param sProperty name of property.
   * @return value as a string.
   */
  private String getProperty(String sProperty)
  {
    String sValue = null;
    if (sProperty.equals("LobFolder"))
    {
      URI uri = null;
      if (_oMetaData instanceof MetaColumn)
        uri = ((MetaColumn)_oMetaData).getLobFolder();
      else if (_oMetaData instanceof MetaField)
        uri = ((MetaField)_oMetaData).getLobFolder();
      else if (_oMetaData instanceof MetaData)
        uri = ((MetaData)_oMetaData).getLobFolder();
      if (uri != null)
        sValue = uri.toString();
    }
    else if (sProperty.equals("ArchivalDate") && (_oMetaData instanceof MetaData))
      sValue = _du.fromGregorianCalendar((GregorianCalendar)((MetaData)_oMetaData).getArchivalDate());
    else if (sProperty.equals("Instantiable") && (_oMetaData instanceof MetaType))
      sValue = String.valueOf(((MetaType)_oMetaData).isInstantiable());
    else if (sProperty.equals("Final") && (_oMetaData instanceof MetaType))
      sValue = String.valueOf(((MetaType)_oMetaData).isInstantiable());
    else if (sProperty.equals("Rows"))
    {
      if (_oMetaData instanceof MetaTable)
        sValue = String.valueOf(((MetaTable)_oMetaData).getRows());
      else if (_oMetaData instanceof MetaView)
        sValue = String.valueOf(((MetaView)_oMetaData).getRows());
    }
    else if (sProperty.equals("Position"))
    {
      if (_oMetaData instanceof MetaColumn)
        sValue = String.valueOf(((MetaColumn)_oMetaData).getPosition());
      else if (_oMetaData instanceof MetaField)
        sValue = String.valueOf(((MetaField)_oMetaData).getPosition());
      else if (_oMetaData instanceof MetaAttribute)
        sValue = String.valueOf(((MetaAttribute)_oMetaData).getPosition());
      else if (_oMetaData instanceof MetaParameter)
        sValue = String.valueOf(((MetaParameter)_oMetaData).getPosition());
    }
    else if (sProperty.equals("Nullable"))
    {
      if (_oMetaData instanceof MetaColumn)
        sValue = String.valueOf(((MetaColumn)_oMetaData).isNullable());
      else if (_oMetaData instanceof MetaAttribute)
        sValue = String.valueOf(((MetaAttribute)_oMetaData).isNullable());
    }
    else if (sProperty.equals("Cardinality"))
    {
      int iCardinality = -1;
      if (_oMetaData instanceof MetaColumn)
      {
        try { iCardinality = ((MetaColumn)_oMetaData).getCardinality(); }
        catch(IOException ie) { _il.exception(ie); }
      }
      else if (_oMetaData instanceof MetaAttribute)
        iCardinality = ((MetaAttribute)_oMetaData).getCardinality();
      else if (_oMetaData instanceof MetaParameter)
        iCardinality = ((MetaParameter)_oMetaData).getCardinality();
      else if (_oMetaData instanceof MetaField)
      {
        try { iCardinality = ((MetaField)_oMetaData).getCardinality(); }
        catch(IOException ie) { _il.exception(ie); }
      }
      if (iCardinality >= 0)
        sValue = String.valueOf(iCardinality);
    }
    else if (sProperty.equals("Columns"))
    {
      if (_oMetaData instanceof MetaUniqueKey)
        sValue = ((MetaUniqueKey)_oMetaData).getColumnsString();
      else if (_oMetaData instanceof MetaForeignKey)
        sValue = ((MetaForeignKey)_oMetaData).getColumnsString();
    }
    else if (sProperty.equals("References") && (_oMetaData instanceof MetaForeignKey))
      sValue = ((MetaForeignKey)_oMetaData).getReferencesString();
    else if (sProperty.equals("Table") && (_oMetaData instanceof RecordExtract))
    {
      RecordExtract re = (RecordExtract)_oMetaData;
      sValue = re.getTable().getMetaTable().getName();
    }
    else if (sProperty.equals("Data") && (_oMetaData instanceof RecordExtract))
    {
      if (SiardGui.getSiardGui().getArchive().isValid())
      {
        RecordExtract re = (RecordExtract)_oMetaData;
        RecordExtract reLastChild = null; 
        try { reLastChild = re.getRecordExtract(re.getRecordExtracts()-1); }
        catch(IOException ie) { _il.exception(ie); }
        sValue = String.valueOf(re.getOffset());
        if (re.getRecordExtracts() > 1)
          sValue = sValue + "-" +String.valueOf(reLastChild.getOffset());
        if (re.getDelta() > 1)
          sValue = sValue + " ("+String.valueOf(reLastChild.getDelta())+")";
      }
      else
        sValue = SiardBundle.getSiardBundle().getTableNoPrimaryData();
    }
    else
    {
      /* find method "get"+sProperty or "is"+sProperty of _oMetaData object and invoke it */
      String sMethodName = "get"+sProperty;
      try
      {
        Method methodGet = _oMetaData.getClass().getDeclaredMethod(sMethodName); 
        Object oValue = methodGet.invoke(_oMetaData);
        if (oValue != null)
          sValue = oValue.toString();
      }
      catch (IllegalAccessException iae) { _il.exception(iae); }
      catch (InvocationTargetException ite) {_il.exception(ite); }
      catch (NoSuchMethodException nsme) { throw new IllegalArgumentException("Method \""+sMethodName+"\" of class "+_oMetaData.getClass().getSimpleName()+" not found!",nsme); }
    }
    return sValue;
  } /* getProperty */
  
  /*------------------------------------------------------------------*/
  /** handle button events.
   */
  @Override
  public void handle(ActionEvent ae)
  {
    if (ae.getSource() == _btnApply)
      apply();
    else if (_bChanged)
      reset();
  } /* handle */

  /*------------------------------------------------------------------*/
  /** add a property to the editor.
   * @param clsMetaData meta data interface class.
   * @param sProperty name of property.
   * @param bEditable true, if property is editable.
   * @param bMultiline true, if property is multi-line.
   * @param bMandatory true, if property must not be empty.
   */
  private PropertyHBox displayProperty(Class<?> clsMetaData, String sProperty, 
    boolean bEditable, boolean bMultiline, boolean bMandatory)
  {
    String sLabelKey = ("label."+clsMetaData.getSimpleName()+"."+sProperty).toLowerCase();
    String sLabel = _sb.getProperty(sLabelKey);
    PropertyHBox phbox = new PropertyHBox(sProperty, sLabel, getLabelWidth(),
      getProperty(sProperty), bEditable,bMultiline,bMandatory);
    getChildren().add(phbox);
    if (bEditable)
    {
      phbox.addListener(this);
      _bEditable = true;
    }
    return phbox;
  } /* displayProperty */

  /*------------------------------------------------------------------*/
  /** display the apply and reset buttons.
   */
  private HBox displayButtons(double dPropertyWidth)
  {
    HBox hbox = new HBox();
    hbox.setPadding(new Insets(dINNER_PADDING));
    hbox.setSpacing(dHSPACING);
    
    _btnReset = new Button(_sb.getEditMetaDataReset());
    _btnReset.setOnAction(this);
    _btnReset.setDisable(true);
    hbox.getChildren().add(_btnReset);

    _btnApply = new Button(_sb.getEditMetaDataApply());
    _btnApply.setDefaultButton(true);
    _btnApply.setOnAction(this);
    _btnApply.setDisable(true);
    hbox.getChildren().add(_btnApply);
    
    HBox.setMargin(_btnApply, new Insets(dOUTER_PADDING));
    HBox.setMargin(_btnReset, new Insets(dOUTER_PADDING));
    if (dPropertyWidth < FxSizes.getNodeWidth(hbox))
      dPropertyWidth = FxSizes.getNodeWidth(hbox);
    hbox.setMaxWidth(dPropertyWidth);
    hbox.setAlignment(Pos.TOP_RIGHT);
    getChildren().add(hbox);
    return hbox;
  } /* displayButtons */
  
  /*------------------------------------------------------------------*/
  /** apply edited changes to meta data.
   */
  private void apply()
  {
    if (_bChanged)
    {
      for (Iterator<Node> iterNode = getChildren().iterator(); iterNode.hasNext(); )
      {
        Node node = iterNode.next();
        if (node instanceof PropertyHBox)
        {
          PropertyHBox phbox = (PropertyHBox)node;
          if (phbox.isEditable())
            setProperty(phbox.getProperty(),phbox.getValue());
        }
      }
      ActionEvent ae = new ActionEvent(_oMetaData,this);
      fireEvent(ae); // this notifies listeners for changes in meta data 
      _bChanged = false;
      MainMenuBar.getMainMenuBar().restrict();
    }
  } /* apply */

  /*------------------------------------------------------------------*/
  /** display meta data editor for current object in the current language.
   * N.B.: Must be called after a change of language.
   */
  public void reset()
  {
    _du = DU.getInstance(_sb.getLanguage(),_sb.getDateFormat());
    getChildren().clear();
    _bEditable = false;
    if (_oMetaData instanceof MetaData)
    {
      MetaData md = (MetaData)_oMetaData;
      displayProperty(MetaData.class,"Version",false,false,false);
      displayProperty(MetaData.class,"DbName",true,false,true);
      displayProperty(MetaData.class,"Description",true,true,false);
      displayProperty(MetaData.class,"Archiver",true,false,false);
      displayProperty(MetaData.class,"ArchiverContact",true,true,false);
      displayProperty(MetaData.class,"DataOwner",true,false,true);
      displayProperty(MetaData.class,"DataOriginTimespan",true,false,true);
      displayProperty(MetaData.class,"LobFolder",true,false,false);
      displayProperty(MetaData.class,"ProducerApplication",false,false,false);
      displayProperty(MetaData.class,"ArchivalDate",false,false,false);
      displayProperty(MetaData.class,"ClientMachine",false,false,false);
      displayProperty(MetaData.class,"DatabaseProduct",false,false,false);
      displayProperty(MetaData.class,"Connection",false,false,false);
      displayProperty(MetaData.class,"DatabaseUser",false,false,false);
    }
    else if (_oMetaData instanceof MetaSchema)
    {
      displayProperty(MetaSchema.class,"Name",false,false,false);
      displayProperty(MetaSchema.class,"Description",true,true,false);
    }
    else if (_oMetaData instanceof MetaUser)
    {
      displayProperty(MetaUser.class,"Name",false,false,false);
      displayProperty(MetaUser.class,"Description",true,true,false);
    }
    else if (_oMetaData instanceof MetaRole)
    {
      displayProperty(MetaRole.class,"Name",false,false,false);
      displayProperty(MetaRole.class,"Admin",false,false,false);
      displayProperty(MetaRole.class,"Description",true,true,false);
    }
    else if (_oMetaData instanceof MetaPrivilege)
    {
      displayProperty(MetaPrivilege.class,"Type",false,false,false);
      displayProperty(MetaPrivilege.class,"Object",false,false,false);
      displayProperty(MetaPrivilege.class,"Grantor",false,false,false);
      displayProperty(MetaPrivilege.class,"Grantee",false,false,false);
      displayProperty(MetaPrivilege.class,"Option",false,false,false);
      displayProperty(MetaPrivilege.class,"Description",true,true,false);
    }
    else if (_oMetaData instanceof MetaType)
    {
      displayProperty(MetaType.class,"Name",false,false,false);
      displayProperty(MetaType.class,"Category",false,false,false);
      displayProperty(MetaType.class,"Instantiable",false,false,false);
      displayProperty(MetaType.class,"Final",false,false,false);
      displayProperty(MetaType.class,"Base",false,false,false);
      displayProperty(MetaType.class,"Description",true,true,false);
    }
    else if (_oMetaData instanceof MetaTable)
    {
      displayProperty(MetaTable.class,"Name",false,false,false);
      displayProperty(MetaTable.class,"Rows",false,false,false);
      displayProperty(MetaTable.class,"Description",true,true,false);
    }
    else if (_oMetaData instanceof MetaView)
    {
      displayProperty(MetaView.class,"Name",false,false,false);
      displayProperty(MetaView.class,"QueryOriginal",false,true,false);
      displayProperty(MetaView.class,"Query",true,true,false);
      displayProperty(MetaView.class,"Rows",false,false,false);
      displayProperty(MetaView.class,"Description",true,true,false);
    }
    else if (_oMetaData instanceof MetaRoutine)
    {
      displayProperty(MetaRoutine.class,"Name",false,false,false);
      displayProperty(MetaRoutine.class,"SpecificName",false,false,false);
      displayProperty(MetaRoutine.class,"Source",false,true,false);
      displayProperty(MetaRoutine.class,"Body",true,true,false);
      displayProperty(MetaRoutine.class,"Characteristic",false,false,false);
      displayProperty(MetaRoutine.class,"ReturnType",false,false,false);
      displayProperty(MetaRoutine.class,"Description",true,true,false);
    }
    else if (_oMetaData instanceof MetaAttribute)
    {
      displayProperty(MetaAttribute.class,"Name",false,false,false);
      displayProperty(MetaAttribute.class,"Position",false,false,false);
      displayProperty(MetaAttribute.class,"Type",false,false,false);
      displayProperty(MetaAttribute.class,"TypeSchema",false,false,false);
      displayProperty(MetaAttribute.class,"TypeName",false,false,false);
      displayProperty(MetaAttribute.class,"TypeOriginal",false,false,false);
      displayProperty(MetaAttribute.class,"Nullable",false,false,false);
      displayProperty(MetaAttribute.class,"DefaultValue",false,false,false);
      displayProperty(MetaAttribute.class,"Cardinality",false,false,false);
      displayProperty(MetaAttribute.class,"Description",true,true,false);
    }
    else if (_oMetaData instanceof MetaColumn)
    {
      MetaColumn mc = (MetaColumn)_oMetaData;
      displayProperty(MetaColumn.class,"Name",false,false,false);
      displayProperty(MetaColumn.class,"Position",false,false,false);
      displayProperty(MetaColumn.class,"LobFolder",true,false,false);
      displayProperty(MetaColumn.class,"MimeType",true,false,false);
      displayProperty(MetaColumn.class,"Type",false,false,false);
      displayProperty(MetaColumn.class,"TypeSchema",false,false,false);
      displayProperty(MetaColumn.class,"TypeName",false,false,false);
      displayProperty(MetaColumn.class,"TypeOriginal",false,false,false);
      displayProperty(MetaColumn.class,"Nullable",false,false,false);
      displayProperty(MetaColumn.class,"DefaultValue",false,false,false);
      displayProperty(MetaColumn.class,"Cardinality",false,false,false);
      displayProperty(MetaColumn.class,"Description",true,true,false);
    }
    else if (_oMetaData instanceof MetaUniqueKey)
    {
      displayProperty(MetaUniqueKey.class,"Name",false,false,false);
      displayProperty(MetaUniqueKey.class,"Columns",false,false,false);
      displayProperty(MetaUniqueKey.class,"Description",true,true,false);
    }
    else if (_oMetaData instanceof MetaForeignKey)
    {
      displayProperty(MetaForeignKey.class,"Name",false,false,false);
      displayProperty(MetaForeignKey.class,"Columns",false,false,false);
      displayProperty(MetaForeignKey.class,"ReferencedSchema",false,false,false);
      displayProperty(MetaForeignKey.class,"ReferencedTable",false,false,false);
      displayProperty(MetaForeignKey.class,"References",false,false,false);
      displayProperty(MetaForeignKey.class,"MatchType",false,false,false);
      displayProperty(MetaForeignKey.class,"DeleteAction",false,false,false);
      displayProperty(MetaForeignKey.class,"UpdateAction",false,false,false);
      displayProperty(MetaForeignKey.class,"Description",true,true,false);
    }
    else if (_oMetaData instanceof MetaParameter)
    {
      displayProperty(MetaParameter.class,"Name",false,false,false);
      displayProperty(MetaParameter.class,"Position",false,false,false);
      displayProperty(MetaParameter.class,"Mode",false,false,false);
      displayProperty(MetaParameter.class,"Type",false,false,false);
      displayProperty(MetaParameter.class,"TypeSchema",false,false,false);
      displayProperty(MetaParameter.class,"TypeName",false,false,false);
      displayProperty(MetaParameter.class,"TypeOriginal",false,false,false);
      displayProperty(MetaParameter.class,"Cardinality",false,false,false);
      displayProperty(MetaParameter.class,"Description",true,true,false);
    }
    else if (_oMetaData instanceof MetaField)
    {
      MetaField mf = (MetaField)_oMetaData;
      displayProperty(MetaField.class,"Name",false,false,false);
      displayProperty(MetaField.class,"Position",false,false,false);
      boolean bEditable = false;
      try
      {
        boolean bInvalid = !mf.getAncestorMetaColumn().getParentMetaTable().getParentMetaSchema().getParentMetaData().getArchive().isValid();
        if (bInvalid)
        {
          int iPreType = mf.getPreType();
          boolean bLob = 
             (iPreType == Types.BINARY) ||
             (iPreType == Types.VARBINARY) ||
             (iPreType == Types.BLOB) ||
             (iPreType == Types.CHAR) ||
             (iPreType == Types.VARCHAR) ||
             (iPreType == Types.CLOB) ||
             (iPreType == Types.NCHAR) ||
             (iPreType == Types.NVARCHAR) ||
             (iPreType == Types.NCLOB) ||
             (iPreType == Types.SQLXML);
          if (bLob)
            bEditable = true;
        }
      }
      catch(IOException ie) {}
      displayProperty(MetaField.class,"LobFolder",bEditable,false,false);
      displayProperty(MetaField.class,"MimeType",bEditable,false,false);
      displayProperty(MetaField.class,"Type",false,false,false);
      displayProperty(MetaField.class,"TypeSchema",false,false,false);
      displayProperty(MetaField.class,"TypeName",false,false,false);
      displayProperty(MetaField.class,"TypeOriginal",false,false,false);
      displayProperty(MetaField.class,"Cardinality",false,false,false);
      displayProperty(MetaField.class,"Description",true,true,false);
    }
    else if (_oMetaData instanceof RecordExtract)
    {
      displayProperty(RecordExtract.class,"Table",false,false,false);
      displayProperty(RecordExtract.class,"Data",false,false,false);
    }
    double dPropertyWidth = 0.0;
    for (Iterator<Node> iterProperty = getChildren().iterator(); iterProperty.hasNext(); )
    {
      PropertyHBox phbox = (PropertyHBox)iterProperty.next();
      if (dPropertyWidth < phbox.getMinWidth())
        dPropertyWidth = phbox.getMinWidth();
    }
    if (_bEditable)
    {
      displayButtons(dPropertyWidth);
      _bChanged = false;
      MainMenuBar.getMainMenuBar().restrict();
    }
    setMinWidth(computeMinWidth(getMinHeight()));
    setMinHeight(computeMinHeight(getMinWidth()));
  } /* reset */

  /*------------------------------------------------------------------*/
  /** select a substring of the value of a property.
   * @param iSelectProperty property index.
   * @param iStartOffset start offset of selected range.
   * @param iEndOffset end offset of selected range.
   */
  public void selectRange(int iSelectProperty, int iStartOffset, int iEndOffset)
  {
    for (int iProperty = 0; iProperty < getChildren().size(); iProperty++)
    {
      Node node = getChildren().get(iProperty);
      if (node instanceof PropertyHBox)
      {
        PropertyHBox phbox = (PropertyHBox)node;
        if (iProperty == iSelectProperty)
          phbox.selectRange(iStartOffset, iEndOffset);
        else
          phbox.deselect();
      }
    }
  } /* selectRange */
  
  /*------------------------------------------------------------------*/
  /** change meta data editor to edit a new meta data object.
   * @param oMetaData new meta data object.
   */
  public void setMetaData(Object oMetaData)
  {
    if ((_oMetaData != null) && _bChanged)
    {
      if (MB.show(SiardGui.getSiardGui().getStage(), 
          _sb.getEditMetaDataTitle(), 
          _sb.getEditMetaDataQuery(), 
          _sb.getYes(), 
          _sb.getNo()) == 1)
        apply();
    }
    _oMetaData = oMetaData;
    reset();
  } /* setMetaData */

  /*------------------------------------------------------------------*/
  /** convenience method for registering an event handler to be notified
   * when the meta data have changed.
   * @param ehavMetaDataChange event handler for change of meta data.
   */
  public void setOnMetaDataChangeAction(EventHandler<ActionEvent> ehavMetaDataChange)
  {
    addEventHandler(ActionEvent.ACTION, ehavMetaDataChange);
  } /* setOnMetaDataChangeAction */
  
  /*------------------------------------------------------------------*/
  /** constructor for a few general visual aspects.
   */
  public MetaDataEditor()
  {
    super();
    setPadding(new Insets(dOUTER_PADDING));
    setSpacing(dVSPACING);
    setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    PropertyHBox phbox = new PropertyHBox("Property", "Label", getLabelWidth(),
      "Property", true, true, false);
    setMinWidth(phbox.getMinWidth());
    setMinHeight(0.68*getMinWidth()+dVSPACING);
  } /* constructor MetaDataEditor */

} /* class MetaDataEditor */
