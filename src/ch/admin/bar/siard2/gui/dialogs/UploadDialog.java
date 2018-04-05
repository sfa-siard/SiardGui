/*======================================================================
UploadDialog for uploading a database from a SIARD archive. 
Application : Siard2
Description : UploadDialog for uploading a database from a SIARD archive. 
Platform    : Java 7, JavaFX 2.2   
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 29.06.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.dialogs;

import java.sql.*;
import javafx.concurrent.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import ch.enterag.utils.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.cmd.*;
import ch.admin.bar.siard2.gui.*;
import ch.admin.bar.siard2.gui.tasks.*;

/*====================================================================*/
/** UploadDialog for uploading a database from a SIARD archive.
 @author Hartwig Thomas
 */
public class UploadDialog
  extends LoadDialog
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(UploadDialog.class.getName());
  // upload task
  private UploadTask _ut = null;

  /*==================================================================*/
  private class WorkerStateEventHandler
    implements EventHandler<WorkerStateEvent>
  {
    @Override
    public void handle(WorkerStateEvent wse)
    {
      _btnCancel.setDisable(true);
      UploadTask ut = (UploadTask)wse.getSource();
      SiardBundle sb = SiardBundle.getSiardBundle();
      String sMessage = null;
      if (wse.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED)
      {
        sMessage = sb.getUploadSuccessMessage();
        _bSuccess = true;
      }
      else if (wse.getEventType() == WorkerStateEvent.WORKER_STATE_CANCELLED)
        sMessage = sb.getUploadCanceledMessage();
      else if (wse.getEventType() == WorkerStateEvent.WORKER_STATE_FAILED)
        sMessage = sb.getUploadFailureMessage(ut.getException());
      _tfMessage.setText(sMessage);
      if (!_bSuccess)
      {
        try { ut.getConnection().rollback(); }
        catch(SQLException se) { System.err.println(EU.getExceptionMessage(se)); }
      }
      _btnDefault.setDisable(false);
    }
  }
  private WorkerStateEventHandler _wseh = new WorkerStateEventHandler();
  
  /*==================================================================*/
  private class ActionEventHandler
    implements EventHandler<ActionEvent>
  {
    @Override
    public void handle(ActionEvent ae)
    {
      if (ae.getSource() == _btnCancel)
      {
        _ut.cancel();
        _btnCancel.setDisable(true);
      }
      else if (ae.getSource() == _btnDefault)
        close();
    }
  } /* ActionEventHandler */
  private ActionEventHandler _aeh = new ActionEventHandler();

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  protected EventHandler<ActionEvent> getActionEventHandler()
  {
    if (_aeh == null)
      _aeh = new ActionEventHandler();
    return _aeh;
  }; /* getActionEventHandler */
  
  /*------------------------------------------------------------------*/
  /** create the parameters VBox at the top of the dialog
   * @param archive SIARD archive to be uploaded.
   * @param conn target database connection.
   * @param bMetaDataOnly true, if only schema is to be created.
   * @return new parameters VBox at the top of the dialog.
   */
  @Override
  protected VBox createVBoxParameters(Connection conn, Archive archive, 
    boolean bMetaDataOnly, boolean bViewsAsLabels)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(dINNER_PADDING));
    vbox.setSpacing(dVSPACING);
    double dLabelWidth = 0.0;
    
    Label lblArchiveLabel = createLabelWithColon(sb.getUploadArchiveLabel());
    lblArchiveLabel.setPrefWidth(FxSizes.getTextWidth(lblArchiveLabel.getText()));
    if (dLabelWidth < lblArchiveLabel.getPrefWidth())
      dLabelWidth = lblArchiveLabel.getPrefWidth();
    
    Label lblConnectionLabel = createLabelWithColon(sb.getUploadConnectionLabel());
    if (dLabelWidth < lblConnectionLabel.getPrefWidth())
      dLabelWidth = lblConnectionLabel.getPrefWidth();
    
    Label lblMetaDataOnlyLabel = createLabelWithColon(sb.getUploadExtentLabel());
    if (dLabelWidth < lblMetaDataOnlyLabel.getPrefWidth())
      dLabelWidth = lblMetaDataOnlyLabel.getPrefWidth();
    
    lblArchiveLabel.setPrefWidth(dLabelWidth);
    lblConnectionLabel.setPrefWidth(dLabelWidth);
    lblMetaDataOnlyLabel.setPrefWidth(dLabelWidth);
    
    double dMaxWidth = FxSizes.getScreenBounds().getWidth()/2.0;
    Label lblArchive = createLabel(archive.getFile().getAbsolutePath(),dMaxWidth);
    String sConnectionUrl = "";
    try { sConnectionUrl = conn.getMetaData().getURL(); }
    catch(SQLException se) {}
    Label lblConnection = createLabel(sConnectionUrl,dMaxWidth);
    Label lblMetaDataOnly = createLabel(bMetaDataOnly?
      sb.getUploadExtentMetaDataOnly():
      sb.getUploadExtentFullDatabase(),dMaxWidth);

    double dMinWidth = 0.0;

    HBox hboxArchive = createParameterHBox(lblArchiveLabel,lblArchive);
    if (dMinWidth < hboxArchive.getMinWidth())
      dMinWidth = hboxArchive.getMinWidth();
    vbox.getChildren().add(hboxArchive);
    
    HBox hboxConnection = createParameterHBox(lblConnectionLabel,lblConnection);
    if (dMinWidth < hboxConnection.getMinWidth())
      dMinWidth = hboxConnection.getMinWidth();
    vbox.getChildren().add(hboxConnection);
    
    HBox hboxMetaDataOnly = createParameterHBox(lblMetaDataOnlyLabel,lblMetaDataOnly);
    if (dMinWidth < hboxMetaDataOnly.getMinWidth())
      dMinWidth = hboxMetaDataOnly.getMinWidth();
    vbox.getChildren().add(hboxMetaDataOnly);
    vbox.setMinWidth(dMinWidth);
    return vbox;
  } /* createVBoxParameters */
  
  /*------------------------------------------------------------------*/
  /** display the upload dialog.
   * @param stageOwner owner window.
   * @param archive SIARD archive to be uploaded.
   * @param conn database connection.
   * @param bMetaDataOnly true, if only the schema is to be created.
   * @param mdtd MetaDataToDb instance.
   */
  private UploadDialog(Stage stageOwner, Archive archive, Connection conn, 
    boolean bMetaDataOnly, MetaDataToDb mdtd)
  {
    super(stageOwner, conn, archive, bMetaDataOnly, false, 
      SiardBundle.getSiardBundle().getUploadTitle());
    MetaData md = archive.getMetaData();
    if (!mdtd.supportsUdts())
    {
      int iTypesInSiard = 0;
      for (int iSchema = 0; iSchema < md.getMetaSchemas(); iSchema++)
      {
        MetaSchema ms = md.getMetaSchema(iSchema);
        iTypesInSiard = iTypesInSiard + ms.getMetaTypes();
      }
      if (iTypesInSiard > 0)
        _tfMessage.setText(SiardBundle.getSiardBundle().getUploadUnsupportedUdtsMessage());
    }
    _ut = UploadTask.startUploadTask(archive, conn, bMetaDataOnly, mdtd, _pb.progressProperty(), _wseh);
  } /* constructor UploadDialog */
  
  /*------------------------------------------------------------------*/
  /** show upload dialog and start uploading.
   * @param stageOwner owner window.
   * @param archive SIARD archive to be uploaded.
   * @param conn database connection.
   * @param bMetaDataOnly true, if only meta data are to be uploaded.
   * @return upload result.
   */
  public static UploadDialog showUploadDialog(Stage stageOwner,
    Archive archive, Connection conn, boolean bMetaDataOnly, MetaDataToDb mdtd)
  {
    _il.enter(conn, archive,String.valueOf(bMetaDataOnly));
    UploadDialog ud = null;
    ud = new UploadDialog(stageOwner, archive, conn, bMetaDataOnly, mdtd);
    ud.showAndWait();
    System.setOut(ud._psOut);
    System.setErr(ud._psErr);
    _il.exit(ud);
    return ud;
  } /* showUploadDialog */

} /* UploadDialog */
