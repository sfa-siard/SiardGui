/*======================================================================
UploadConnectionDialog for entering data to connect to a database. 
Application : Siard2
Description : UploadConnectionDialog for entering data to connect to a 
              database for upload. 
Platform    : Java 7, JavaFX 2.2   
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 29.06.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.dialogs;

import java.util.*;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.gui.*;

/*====================================================================*/
/** UploadConnectionDialog for entering data to connect to a database
 * for upload.
 * @author Hartwig Thomas
 */
public class UploadConnectionDialog
  extends ConnectionDialog
{
  private static SiardBundle _sb = SiardBundle.getSiardBundle();
  private Archive _archive = null;
  /** schema mappings */
  private Map<Label,TextField> _mapSchemas = null;
  public Map<String,String> getSchemasMap()
  {
    Map<String,String> mapSchemas = new HashMap<String,String>();
    for (Iterator<Label> iterSchema = _mapSchemas.keySet().iterator(); iterSchema.hasNext(); )
    {
      Label lbl = iterSchema.next();
      String sSchema = lbl.getText();
      TextField tf = _mapSchemas.get(lbl);
      String sMapped = tf.getText();
      mapSchemas.put(sSchema, sMapped);
    }
    return mapSchemas;
  } /* getSchemasMap */
  
  @Override
  protected String validate()
  {
    String sError = super.validate();
    for (Iterator<Label> iterSchema = _mapSchemas.keySet().iterator(); (sError == null) && iterSchema.hasNext(); )
    {
      Label lbl = iterSchema.next();
      TextField tf = _mapSchemas.get(lbl);
      String sMapped = tf.getText();
      if (sMapped.length() == 0)
      {
        sError = SiardBundle.getSiardBundle().getUploadConnectionErrorSchema();
        tf.requestFocus();
      }
    }
    return sError;
  }
  /*------------------------------------------------------------------*/
  /** create a VBox for the schema mapping.
   * @return VBox for the schema mapping. 
   */
  private VBox createVBoxSchemas()
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(dINNER_PADDING));
    vbox.setSpacing(dVSPACING);
    vbox.setAlignment(Pos.TOP_LEFT);
    double dMinWidth = 0.0;
    Label lblTitle = createLabel(sb.getConnectionUploadSchemasLabel(),vbox);
    if (dMinWidth < lblTitle.getPrefWidth())
      dMinWidth = lblTitle.getPrefWidth();
    vbox.getChildren().add(lblTitle);
    double dLabelWidth = 0;
    _mapSchemas = new HashMap<Label,TextField>();
    MetaData md = _archive.getMetaData(); 
    for (int iSchema = 0; iSchema < md.getMetaSchemas(); iSchema++)
    {
      String sSchema = md.getMetaSchema(iSchema).getName();
      TextField tfSchema = new TextField(sSchema);
      tfSchema.textProperty().addListener(_scl);
      Label lblSchema = createLabel(sSchema,tfSchema);
      if (dLabelWidth < lblSchema.getPrefWidth())
        dLabelWidth = lblSchema.getPrefWidth();
      _mapSchemas.put(lblSchema, tfSchema);
    }
    double dTextWidth = dWIDTH_URL - dLabelWidth - dHSPACING;
    for (Iterator<Label> iterLabel = _mapSchemas.keySet().iterator(); iterLabel.hasNext(); )
    {
      Label lbl = iterLabel.next();
      lbl.setPrefWidth(dLabelWidth);
      TextField tf = _mapSchemas.get(lbl);
      tf.setPrefWidth(dTextWidth);
      HBox hbox = createHBox(lbl,tf);
      if (dMinWidth < hbox.getMinWidth())
        dMinWidth = hbox.getMinWidth();
      vbox.getChildren().add(hbox);
    }
    vbox.setMinWidth(dMinWidth);
    return vbox;
  } /* createVBoxSchemas */
  
  /*------------------------------------------------------------------*/
  /** display the upload connection dialog.
   * @param stageOwner owner window.
   * @param sConnectionUrl initial value for connection (JDBC) URL or null.
   * @param sDbUser initial value for user of database opr null.
   * @param archive SIARD archive to be uploaded.
   */
  private UploadConnectionDialog(
    Stage stageOwner, String sConnectionUrl, String sDbUser, Archive archive)
  {
    super(stageOwner,sConnectionUrl,sDbUser,
        _sb.getConnectionUploadTitle(),
        _sb.getConnectionUploadMetaDataOnlyLabel(),
        _sb.getConnectionUploadMetaDataOnlyTooltip(),
        _sb.getConnectionUploadOverwriteLabel(),
        _sb.getConnectionUploadOverwriteTooltip(),
        null,null);
    _archive = archive;
    /* add the schemas from the meta data to the dialog */
    VBox vboxDialog = (VBox)getScene().getRoot();
    double dMinWidth = vboxDialog.getMinWidth();
    VBox vboxSchemas = createVBoxSchemas();
    if (dMinWidth < vboxSchemas.getMinWidth())
      dMinWidth = vboxSchemas.getMinWidth();
    /* add schemas before buttons */
    vboxDialog.getChildren().add(vboxDialog.getChildren().size()-1,vboxSchemas);
    vboxDialog.getChildren().add(vboxDialog.getChildren().size()-1,new Separator());
    vboxDialog.setMinWidth(dMinWidth);
  } /* constructor UpConnectionDialog */
  
  /*------------------------------------------------------------------*/
  /** show upload connection dialog and save entered values.
   * @param stageOwner owner window.
   * @param sConnectionUrl initial value for connection (JDBC) URL or null.
   * @param sDbUser initial value for user of database or null.
   * @param archive SIARD archive to be uploaded.
   * @return UpConnectionDialog instance with results.
   *   if getResult() is 1 then getConnectionUrl(), getDbUser(), 
   *   getDbPassword(), isMetaDataOnly(), isOverwrite(), and getSchemasMap()
   *   have the values entered.
   */
  public static UploadConnectionDialog showUploadConnectionDialog(
    Stage stageOwner, String sConnectionUrl, String sDbUser, Archive archive)
  {
    UploadConnectionDialog ucd = new UploadConnectionDialog(
      stageOwner, sConnectionUrl, sDbUser, archive);
    ucd.showAndWait();
    return ucd;
  } /* showUploadConnectionDialog */

} /* class UploadConnectionDialog */
