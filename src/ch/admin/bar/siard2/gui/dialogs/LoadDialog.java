/*======================================================================
LoadDialog for up-/down-loading a database to a SIARD archive. 
Application : Siard2
Description : LoadDialog for up-/down-loading a database to a SIARD archive. 
Platform    : Java 7, JavaFX 2.2   
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 27.06.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.dialogs;

import java.io.*;
import java.sql.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.fx.Dialog;
import ch.enterag.utils.fx.controls.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.gui.*;

/*====================================================================*/
/** LoadDialog for up-/down-loading a database to a SIARD archive.
 * Abstract base class for DownloadDialog and UploadDialog.
 @author Hartwig Thomas
 */
public abstract class LoadDialog
  extends Dialog
  implements EventHandler<WindowEvent>
{
  // archive
  private Archive _archive = null;
  public Archive getArchive() { return _archive; }
  // stdout before redirection
  protected PrintStream _psOut = null;
  // stderr before redirection
  protected PrintStream _psErr = null;
  // dialog result
  protected boolean _bSuccess = false;
  public boolean wasSuccessful() { return _bSuccess; }
  // area for error message
  protected TextField _tfMessage = null;
  // OK button
  protected Button _btnDefault = null;
  // Cancel button
  protected Button _btnCancel = null;
  // progress bar
  protected ProgressBar _pb = null;

  /*------------------------------------------------------------------*/
  /** action event handler
   * handles Ok, Cancel.
   * @return handler.
   */
  /* action event handle */
  protected abstract EventHandler<ActionEvent> getActionEventHandler();

  /*------------------------------------------------------------------*/
  /** prevent closing if background action is still running.
   * @param we Windows Event from close button in title bar. 
   */
  @Override
  public void handle(WindowEvent we)
  {
    if (_btnDefault.isDisabled())
      we.consume();
  } /* handle */

  /*------------------------------------------------------------------*/
  /** create the HBox with an OK and a Cancel button.
   * @return HBox with an OK and a Cancel button.
   */
  private HBox createHBoxButtons()
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    /* ok button */
    _btnDefault = new Button(sb.getOk());
    _btnDefault.setDefaultButton(true);
    _btnDefault.setOnAction(getActionEventHandler());
    double dMinWidth = _btnDefault.getLayoutBounds().getWidth();
    /* cancel button */
    _btnCancel = new Button(sb.getCancel());
    _btnCancel.setCancelButton(true);
    _btnCancel.setOnAction(getActionEventHandler());
    dMinWidth += dHSPACING + _btnCancel.getLayoutBounds().getWidth();
    /* HBox for buttons */
    HBox hbox = new HBox();
    hbox.setPadding(new Insets(dINNER_PADDING));
    hbox.setSpacing(dHSPACING);
    hbox.setAlignment(Pos.TOP_RIGHT);
    hbox.getChildren().add(_btnDefault);
    hbox.getChildren().add(_btnCancel);
    HBox.setMargin(_btnDefault, new Insets(dOUTER_PADDING));
    HBox.setMargin(_btnCancel, new Insets(dOUTER_PADDING));
    hbox.setMinWidth(dMinWidth);
    return hbox;
  } /* createHBoxButtons */

  /*------------------------------------------------------------------*/
  /** create a label with the given text and set its preferred width
   * to the text width.
   * @param sText text for label.
   * @param dMaxWidth maximum width of label.
   * @return new label.
   */
  protected Label createLabel(String sText, double dMaxWidth)
  {
    Label lbl = new Label(sText);
    double dPreferredWidth = FxSizes.getNodeWidth(lbl);
    if (dPreferredWidth > dMaxWidth)
      dPreferredWidth = dMaxWidth;
    lbl.setMinWidth(dPreferredWidth);
    lbl.setPrefWidth(dPreferredWidth);
    lbl.setMaxWidth(dMaxWidth);
    lbl.setTextOverrun(OverrunStyle.ELLIPSIS);
    return lbl;
  } /* createLabel */
  
  /*------------------------------------------------------------------*/
  /** create a label with the given text followed by a colon,
   * set its preferred width to the text width and set its alignment to
   * right.
   * @param sText text for label.
   * @return new label.
   */
  protected Label createLabelWithColon(String sText)
  {
    Label lbl = new Label(sText+":");
    double dMinWidth = FxSizes.getNodeWidth(lbl);
    lbl.setMinWidth(dMinWidth);
    lbl.setPrefWidth(dMinWidth);
    lbl.setAlignment(Pos.BASELINE_RIGHT);
    return lbl;
  } /* createLabelWithColon */

  /*------------------------------------------------------------------*/
  /** create a HBox containing the label and the text of a parameter.
   * @param lblLabel label of the parameter.
   * @param lblText text of the parameter.
   * @return
   */
  protected HBox createParameterHBox(Label lblLabel, Label lblText)
  {
    HBox hbox = new HBox();
    hbox.setPadding(new Insets(dINNER_PADDING));
    hbox.setSpacing(dHSPACING);
    hbox.setAlignment(Pos.TOP_LEFT);
    double dMinWidth = 0.0;
    hbox.getChildren().add(lblLabel);
    dMinWidth = dMinWidth + lblLabel.getPrefWidth();
    hbox.getChildren().add(lblText);
    dMinWidth = dMinWidth + dHSPACING + lblText.getPrefWidth();
    hbox.setMinWidth(dMinWidth);
    return hbox;
  } /* createParameterHBox */
  
  /*------------------------------------------------------------------*/
  /** create the parameters VBox at the top of the dialog
   * @param conn database connection.
   * @param fileArchive SIARD archive to be written.
   * @param bMetaDataOnly true, if only meta data are to be up- or downloaded.
   * @param bViewsAsTables true, if views are to be downloaded as tables.
   * @return new parameters VBox at the top of the dialog.
   */
  protected abstract VBox createVBoxParameters(Connection conn, Archive archive, 
    boolean bMetaDataOnly, boolean bViewsAsTables);
  
  /*------------------------------------------------------------------*/
  /** create the main VBox of the dialog
   * containing title area, text area, progress bar, separator and OK and Cancel buttons.
   * @param conn database connection.
   * @param fileArchive SIARD archive to be written.
   * @param bMetaDataOnly true, if only meta data are to be up- or downloaded.
   * @param bViewsAsTables true, if views are to be downloaded as tables.
   * @return new main VBox of the dialog.
   */
  protected VBox createVBoxDialog(Connection conn, Archive archive, 
    boolean bMetaDataOnly, boolean bViewsAsTables)
  {
    /* VBox for title area, text area, progress bar, message, separator and OK and Cancel buttons */
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(dOUTER_PADDING));
    vbox.setSpacing(dVSPACING);
    vbox.setAlignment(Pos.TOP_LEFT);
    vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    double dMinWidth = 0;

    // dialog parameters 
    VBox vboxParameters = createVBoxParameters(conn, archive, 
      bMetaDataOnly,bViewsAsTables);
    if (dMinWidth < vboxParameters.getMinWidth())
      dMinWidth = vboxParameters.getMinWidth();
    vbox.getChildren().add(vboxParameters);
    
    // out/err tab pane
    OutErrTabPane oetp = new OutErrTabPane();
    if (dMinWidth < oetp.getMinWidth())
      dMinWidth = oetp.getMinWidth();
    vbox.getChildren().add(oetp);

    // progress bar
    _pb = new ProgressBar(1.0);
    _pb.setMinWidth(dMinWidth);
    vbox.getChildren().add(_pb);
    
    // message
    _tfMessage = new TextField();
    _tfMessage.setStyle(FxStyles.sSTYLE_MESSAGE);
    _tfMessage.setEditable(false);
    _tfMessage.setPrefWidth(dMinWidth);
    vbox.getChildren().add(_tfMessage);
    
    vbox.getChildren().add(new Separator());
    // cancel/ok button
    HBox hboxButtons = createHBoxButtons();
    if (dMinWidth < hboxButtons.getMinWidth())
      dMinWidth = hboxButtons.getMinWidth();
    vbox.getChildren().add(hboxButtons);
    vbox.setMinWidth(dMinWidth);
    return vbox;
  } /* createVBoxDialog */
  
  /*------------------------------------------------------------------*/
  /** display the connection dialog.
   * @param stageOwner owner window.
   * @param conn database connection.
   * @param archive SIARD archive to be written.
   * @param sTitle dialog title.
   * @param bMetaDataOnly true, if only meta data are to be up- or downloaded.
   * @param bViewsAsTables true, if views are to be downloaded as tables.
   */
  protected LoadDialog(Stage stageOwner, Connection conn, Archive archive, 
    boolean bMetaDataOnly, boolean bViewsAsTables, String sTitle)
  {
    super(stageOwner,sTitle);
    _archive = archive;
    _psOut = System.out;
    _psErr = System.err;
    setOnCloseRequest(this);
    double dMinWidth = FxSizes.getTextWidth(sTitle)+FxSizes.getCloseWidth()+dHSPACING;
    VBox vboxDialog = createVBoxDialog(conn, _archive, bMetaDataOnly,bViewsAsTables);
    if (dMinWidth < vboxDialog.getMinWidth())
      dMinWidth = vboxDialog.getMinWidth();
    /* adapt dialog width to screen */
    dMinWidth += 2*dOUTER_PADDING;
    Rectangle2D rectScreen = FxSizes.getScreenBounds();
    if (dMinWidth >= rectScreen.getWidth())
      dMinWidth = rectScreen.getWidth()-2*dSCREEN_PADDING;
    setMinWidth(dMinWidth);
    /* scene */
    Scene scene = new Scene(vboxDialog);
    setScene(scene);
    /* start download task */
    _btnDefault.setDisable(true);
    _btnCancel.setDisable(false);
  } /* constructor LoadDialog */
  
} /* LoadDialog */
