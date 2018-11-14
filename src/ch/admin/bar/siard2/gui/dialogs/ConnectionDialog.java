/*======================================================================
ConnectionDialog for entering data to connect to a database. 
Application : Siard2
Description : ConnectionDialog for entering data to connect to a 
              database. 
Platform    : Java 8, JavaFX 8   
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 27.06.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.dialogs;

import java.io.*;
import java.util.*;

import javafx.beans.value.*;
import javafx.event.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.fx.dialogs.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.cmd.*;
import ch.admin.bar.siard2.gui.*;

/*====================================================================*/
/** ConnectionDialog for entering data to connect to a database.
 * Abstract base class for DownloadConnectionDialog and UploadConnectionDialog.
 * @author Hartwig Thomas
 */
public abstract class ConnectionDialog
  extends Stage 
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(ConnectionDialog.class.getName());
  // "padding" inside the screen */
  protected static final double dSCREEN_PADDING = 10.0;
  // padding inside the dialog's VBox
  protected static final double dOUTER_PADDING = 10.0;
  // padding inside
  protected static final double dINNER_PADDING = 10.0;
  // vertical spacing of elements
  protected static final double dVSPACING = 10.0;
  // horizontal spacing of elements
  protected static final double dHSPACING = 10.0;
  // width of JDBC URL input box
  protected static final double dWIDTH_URL = FxSizes.getScreenBounds().getWidth()/2.0;
  
  // properties
  protected String _sConnectionUrl = null;
  protected String _sDbUser = null;
  /** result will be 1 for default, 0 otherwise */
  public static final int iRESULT_CANCELED = 0;
  public static final int iRESULT_SUCCESS = 1;
  protected int _iResult = iRESULT_CANCELED;
  public int getResult() { return _iResult; }

  /** default button (= Enter) */
  protected Button _btnDefault = null;
  /** cancel button (= Escape) */
  protected Button _btnCancel = null;
  /** error message */
  protected TextField _tfError = null;
  /** VBox for parameters */
  protected VBox _vboxParameters = null;
  protected ComboBox<String> _cbDbScheme = null;
  /** hbox for database host */
  protected HBox _hboxDbHost = null;
  /** text field for database host */
  protected TextField _tfDbHost = null;
  /** text field for database name */
  protected TextField _tfDbName = null;
  /** hbox for database folder */
  protected HBox _hboxDbFolder = null;
  /** text field for folder name */
  protected PastingTextField _tfDbFolder = null;
  // protected Label _lblDbFolder = null;
  /** button for triggering folder selection */
  protected Button _btnDbFolder = null;
  /** map from scheme title to scheme */
  protected Map<String,String> _mapSchemes = new HashMap<String,String>();
  /** list of sample URL labels */
  protected Map<String,Label> _mapSampleUrls = new HashMap<String,Label>();
  /** text field for connection URL */
  protected TextField _tfConnectionUrl = null;
  public String getConnectionUrl() { return _iResult == 1? _tfConnectionUrl.getText(): null; }
  /** text field for DbUser */
  protected TextField _tfDbUser = null;
  public String getDbUser() { return _iResult == 1? _tfDbUser.getText(): null; }
  /** password field for password */
  protected PasswordField _pfDbPassword = null;
  public String getDbPassword() { return _iResult == 1? _pfDbPassword.getText(): null; }
  /** check box for meta data only download */
  protected CheckBox _cbMetaDataOnly = null;
  public boolean isMetaDataOnly() { return _iResult == 1? _cbMetaDataOnly.isSelected(): false; }
  /** check box for overwrite */
  private CheckBox _cbOverwrite = null;
  public boolean isOverwrite() { return (_iResult == 1) && (_cbOverwrite != null)? _cbOverwrite.isSelected(): false; }
  /** check box for views as tables */
  private CheckBox _cbViewsAsTables = null;
  public boolean isViewsAsTables() { return (_iResult == 1) && (_cbViewsAsTables != null)? _cbViewsAsTables.isSelected(): false; }

  /*====================================================================*/
  /** DragEventHandler handles dropping folders on DbFolder field.
   */
  private class DragEventHandler
    implements EventHandler<DragEvent>
  {
    @Override
    public void handle(DragEvent event)
    {
      boolean bDropCompleted = false;
      Dragboard db = event.getDragboard();
      if (db.hasFiles())
      {
        List<File> listFiles = db.getFiles();
        if (listFiles.size() == 1)
        {
          File file = db.getFiles().get(0);
          if (file.isDirectory())
          {
            if (event.getEventType() == DragEvent.DRAG_OVER)
              event.acceptTransferModes(TransferMode.COPY);
            else if (event.getEventType() == DragEvent.DRAG_DROPPED)
            {
              PastingTextField ptf = (PastingTextField)event.getSource();
              ptf.changeText(file.getAbsolutePath());
              bDropCompleted = true;
            }
          }
        }
      }
      if (event.getEventType() == DragEvent.DRAG_DROPPED)
        event.setDropCompleted(bDropCompleted);
    } // handle
  } // class
  private DragEventHandler _deh = new DragEventHandler();

  protected void persist()
  {
    UserProperties up = UserProperties.getUserProperties();
    String sDbScheme = _mapSchemes.get(getSelectedTitle());
    up.setDatabaseScheme(sDbScheme);
    String sDbHost = _tfDbHost.getText();
    if ((sDbHost != null) && (sDbHost.length() > 0))
      up.setDatabaseHost(sDbHost);
    String sDbName = _tfDbName.getText();
    up.setDatabaseName(sDbName);
    _sDbUser = _tfDbUser.getText();
    up.setDatabaseUser(_sDbUser);
  } /* persist */
  
  protected String validate()
  {
    String sError = null;
    String sDbUser = _tfDbUser.getText();
    if ((sDbUser == null) || (sDbUser.length() == 0))
    {
      sError = SiardBundle.getSiardBundle().getConnectionErrorUser();
      _tfDbUser.requestFocus();
    }
    return sError;
  } /* validate */
  
  /*====================================================================*/
  /** ActionEventHandler handles all action events.
   */
  private class ActionEventHandler
    implements EventHandler<ActionEvent>
  {
    /*------------------------------------------------------------------*/
    /** handle the clicking of the default or cancel button.
     */
    @Override
    public void handle(ActionEvent ae)
    {
      if (ae.getSource() == _btnCancel)
        close();
      else if (ae.getSource() == _btnDefault)
      {
        String sError = validate();
        if (sError == null)
        {
          persist();
          _iResult = iRESULT_SUCCESS;
          close();
        }
        else
          _tfError.setText(sError);
      }
      else if (ae.getSource() == _btnDbFolder)
      {
        UserProperties up = UserProperties.getUserProperties();
        SiardBundle sb = SiardBundle.getSiardBundle();
        String sDbFolder = _tfDbFolder.getText();
        File fileDbFolder = new File(sDbFolder);
        try
        {
          fileDbFolder = FS.chooseExistingFolder(ConnectionDialog.this, 
            sb.getConnectionDbFolderTitle(), sb.getConnectionDbFolderMessage(), sb, 
            fileDbFolder);
          if (fileDbFolder != null)
          {
            up.setDatabaseFolder(fileDbFolder);
            _tfDbFolder.setText(fileDbFolder.getAbsolutePath());
          }
        }
        catch(FileNotFoundException fnfe) { _il.exception(fnfe); }
      }
    } /* handle */
  }
  private ActionEventHandler _aeh = new ActionEventHandler();
  
  /*====================================================================*/
  /** StringChangeListener handles all change events.
   */
  private class StringChangeListener
    implements ChangeListener<String>
  {
    /*------------------------------------------------------------------*/
    /** if the scheme choice changed, redisplay Host/Folder and JDBC URL.
     * @param ovs observable value.
     * @param sOld old string.
     * @param sNew new string.
     */
    @Override
    public void changed(ObservableValue<? extends String> ovs,
        String sOld, String sNew)
    {
      SiardConnection sc = SiardConnection.getSiardConnection();
      String sScheme = _mapSchemes.get(getSelectedTitle());
      if (ovs == _cbDbScheme.getSelectionModel().selectedItemProperty())
      {
        if (sc.isLocal(sScheme))
        {
          {
            if (_vboxParameters.getChildren().contains(_hboxDbHost))
              _vboxParameters.getChildren().remove(_hboxDbHost);
            if (!_vboxParameters.getChildren().contains(_hboxDbFolder))
              _vboxParameters.getChildren().add(1, _hboxDbFolder);
          }
        }
        else
        {
          if (!_vboxParameters.getChildren().contains(_hboxDbHost))
            _vboxParameters.getChildren().add(1,_hboxDbHost);
          if (_vboxParameters.getChildren().contains(_hboxDbFolder))
            _vboxParameters.getChildren().remove(_hboxDbFolder);
        }
        if (sScheme.equals(UserProperties.sORACLE_DATABASE_SCHEME))
          _tfDbName.setText(UserProperties.sORACLE_DATABASE_NAME);
      }
      if (ovs == _tfDbUser)
      {
        if (sScheme.equals(UserProperties.sACCESS_DATABASE_SCHEME))
        {
          String sDbUser = _tfDbUser.getText();
          if (sDbUser.length() == 0)
            _tfDbUser.setText(UserProperties.sACCESS_DATABASE_USER);
        }
      }
      if ((ovs == _cbDbScheme.getSelectionModel().selectedItemProperty()) ||
          (ovs == _tfDbHost.textProperty()) ||
          (ovs == _tfDbFolder.textProperty()) ||
          (ovs == _tfDbName.textProperty()))
      {
        if (_tfConnectionUrl != null)
        {
          String sSampleUrl = sc.getSampleUrl(sScheme, _tfDbHost.getText(), _tfDbFolder.getText(), _tfDbName.getText());
          _tfConnectionUrl.setText(sSampleUrl);
        }
      }
      if (_tfError != null)
        _tfError.setText("");
    } /* changed */
  } /* class */
  protected StringChangeListener _scl = new StringChangeListener();
  
  /*====================================================================*/
  /** PastingTextField implements non-editable field which can receive
   * paste events and serve as a drop target.
   */
  private class PastingTextField
    extends TextField
  {
    private boolean bChangeable = false;
    PastingTextField(String s)
    {
      super(s);
    }
    @Override
    public void replaceText(int start, int end, String text) 
    {
      if (bChangeable)
        super.replaceText(start, end, text);
    }
    public void changeText(String s)
    {
      bChangeable = true;
      setText(s);
      bChangeable = false;
    }
    @Override
    public void paste()
    {
      Clipboard cb = Clipboard.getSystemClipboard();
      String sPasted = cb.getString();
      File file = new File(sPasted);
      // if we have a file, it gets priority over the string
      List<File> listFiles = cb.getFiles();
      if (listFiles.size() == 1)
        file = listFiles.get(0);
      if (file.isDirectory())
        changeText(file.getAbsolutePath());
    }
  } /* class */ 
  /*====================================================================*/
  
  /*------------------------------------------------------------------*/
  private String getSelectedTitle()
  {
    String sTitle = _cbDbScheme.getSelectionModel().getSelectedItem();
    if (sTitle == null)
    {
      UserProperties up = UserProperties.getUserProperties();
      String sScheme = up.getDatabaseScheme();
      for (Iterator<String> iterScheme = _mapSchemes.keySet().iterator(); iterScheme.hasNext(); )
      {
        String sTitleTry = iterScheme.next();
        if (_mapSchemes.get(sTitleTry).equals(sScheme))
          sTitle = sTitleTry;
      }
      _cbDbScheme.getSelectionModel().clearSelection();
      _cbDbScheme.getSelectionModel().select(sTitle);
    }
    return sTitle;
  } /* getSelectedTitle */
  
  /*------------------------------------------------------------------*/
  /** compute the maximum pref width of the given labels and set their
   * min and pref widths to that value.
   * @param aLabels labels.
   * @return maximum pref width.
   */
  protected double getMaxLabelPrefWidth(Label... aLabel)
  {
    double dMaxPrefWidth = 0.0;
    for (int iLabel = 0; iLabel < aLabel.length; iLabel++)
    {
      Label lbl = aLabel[iLabel];
      if (lbl != null)
      {
        double dPrefWidth = lbl.getPrefWidth();
        if (dMaxPrefWidth < dPrefWidth)
          dMaxPrefWidth = dPrefWidth;
      }
    }   
    for (int iLabel = 0; iLabel < aLabel.length; iLabel++)
    {
      Label lbl = aLabel[iLabel];
      if (lbl != null)
      {
        lbl.setMinWidth(dMaxPrefWidth);
        lbl.setPrefWidth(dMaxPrefWidth);
      }
    }   
    return dMaxPrefWidth;
  } /* getMaxLabelPrefWidth */

  /*------------------------------------------------------------------*/
  /** compute the maximum min width of the given Panes.
   * @param apane Panes.
   * @return maximum min width.
   */
  protected double getMaxPaneMinWidth(Pane... apane)
  {
    double dMaxMinWidth = 0.0;
    for (int iPane = 0; iPane < apane.length; iPane++)
    {
      Pane pane = apane[iPane];
      if (pane != null)
      {
        double dMinWidth = pane.getMinWidth();
        if (dMaxMinWidth < dMinWidth)
          dMaxMinWidth = dMinWidth;
      }
    }
    return dMaxMinWidth;
  } /* getMaxPaneMinWidth */
  
  /*------------------------------------------------------------------*/
  /** create a label for the given node.
   * @param sLabel label text
   * @param nodeFor node associated with label.
   * @return label.
   */
  protected Label createLabel(String sLabel, Node nodeFor)
  {
    Label lbl = new Label(sLabel);
    lbl.setPrefWidth(FxSizes.getNodeWidth(lbl));
    lbl.setLabelFor(nodeFor);
    return lbl;
  } /* createLabel */
  
  /*------------------------------------------------------------------*/
  /** create a horizontal box with label and text
   * @param lbl Label.
   * @param ptfText field for text
   * @param btn button for selector
   * @return horizontal box.
   */
  protected HBox createHBox(Label lbl, PastingTextField ptfText, Button btn)
  {
    HBox hbox = new HBox();
    lbl.setAlignment(Pos.BASELINE_RIGHT);
    hbox.setPadding(new Insets(dINNER_PADDING));
    hbox.setSpacing(dHSPACING);
    hbox.setAlignment(Pos.TOP_LEFT);
    hbox.getChildren().add(lbl);
    hbox.getChildren().add(ptfText);
    HBox.getHgrow(ptfText);
    hbox.getChildren().add(btn);
    hbox.setMinWidth(lbl.getPrefWidth() + dHSPACING + 
        ptfText.getPrefWidth() + dHSPACING +
        btn.getPrefWidth());
    return hbox;
  } /* createHBox */
  
  /*------------------------------------------------------------------*/
  /** create a horizontal box with label and text
   * @param lbl Label.
   * @param node TextField/Checkbox
   * @return horizontal box.
   */
  protected HBox createHBox(Label lbl, Node node)
  {
    HBox hbox = new HBox();
    lbl.setAlignment(Pos.BASELINE_RIGHT);
    hbox.setPadding(new Insets(dINNER_PADDING));
    hbox.setSpacing(dHSPACING);
    hbox.setAlignment(Pos.TOP_LEFT);
    hbox.getChildren().add(lbl);
    hbox.getChildren().add(node);
    double dWidth = 0.0;
    if (node instanceof TextField) // PasswordField is a TextField
      dWidth = ((TextField)node).getPrefWidth();
    else if (node instanceof CheckBox)
      dWidth = ((CheckBox)node).getWidth();
    hbox.setMinWidth(lbl.getPrefWidth() + dHSPACING + dWidth);
    return hbox;
  } /* createHBox */
  
  /*------------------------------------------------------------------*/
  /** create the VBox containing the parameters database server and
   * database name.
   * @return parameters VBox.
   */
  private VBox createVBoxParameters()
  {
    UserProperties up = UserProperties.getUserProperties();
    SiardBundle sb = SiardBundle.getSiardBundle();
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(dINNER_PADDING));
    vbox.setSpacing(dVSPACING);
    vbox.setAlignment(Pos.TOP_LEFT);
    
    ObservableList<String> olScheme = FXCollections.observableArrayList();
    SiardConnection sc = SiardConnection.getSiardConnection();
    String[] asScheme = sc.getSchemes();
    for (int iScheme = 0; iScheme < asScheme.length; iScheme++)
    {
      String sScheme = asScheme[iScheme];
      String sTitle = sc.getTitle(sScheme);
      olScheme.add(sTitle);
      _mapSchemes.put(sTitle, sScheme);
    }

    _cbDbScheme = new ComboBox<String>(olScheme);
    _cbDbScheme.setTooltip(new Tooltip(sb.getConnectionDbSchemeTooltip()));
    _cbDbScheme.getSelectionModel().selectedItemProperty().addListener(_scl);
    HBox.setHgrow(_cbDbScheme, Priority.ALWAYS);
    Label lblDbSchemeLabel = createLabel(sb.getConnectionDbSchemeLabel(),_cbDbScheme);
    /* select the element for the connection URL in combo box */
    
    _tfDbHost = new TextField(up.getDatabaseHost());
    _tfDbHost.textProperty().addListener(_scl);
    HBox.setHgrow(_tfDbHost, Priority.ALWAYS);
    Label lblDbHostLabel = createLabel(sb.getConnectionDbHostLabel(),_tfDbHost);
    
    _tfDbFolder = new PastingTextField(up.getDatabaseFolder().getAbsolutePath());
    _tfDbFolder.textProperty().addListener(_scl);
    HBox.setHgrow(_tfDbFolder, Priority.ALWAYS);
    _tfDbFolder.setOnDragOver(_deh);
    _tfDbFolder.setOnDragDropped(_deh);
    
    _btnDbFolder = new Button(sb.getConnectionDbFolderButton());
    _btnDbFolder.setOnAction(_aeh);
    _btnDbFolder.setAlignment(Pos.BASELINE_RIGHT);
    _btnDbFolder.setMinWidth(FxSizes.getNodeWidth(_btnDbFolder));
    Label lblDbFolderLabel = createLabel(sb.getConnectionDbFolderLabel(),_tfDbFolder);

    _tfDbName = new TextField(up.getDatabaseName());
    _tfDbName.textProperty().addListener(_scl);
    HBox.setHgrow(_tfDbName, Priority.ALWAYS);
    Label lblDbNameLabel = createLabel(sb.getConnectionDbNameLabel(),_tfDbName);
    
    getMaxLabelPrefWidth(lblDbSchemeLabel,lblDbHostLabel,lblDbNameLabel,lblDbFolderLabel);
    
    HBox hboxDbScheme = createHBox(lblDbSchemeLabel, _cbDbScheme);
    vbox.getChildren().add(hboxDbScheme);
    _hboxDbHost = createHBox(lblDbHostLabel, _tfDbHost);
    vbox.getChildren().add(_hboxDbHost);
    _hboxDbFolder = createHBox(lblDbFolderLabel, _tfDbFolder, _btnDbFolder);
    vbox.getChildren().add(_hboxDbFolder);
    HBox hboxDbName = createHBox(lblDbNameLabel, _tfDbName);
    vbox.getChildren().add(hboxDbName);
    
    double dMinWidth = getMaxPaneMinWidth(hboxDbScheme,_hboxDbHost,_hboxDbFolder,hboxDbName);
    vbox.setMinWidth(dMinWidth);
    return vbox;
  } /* createVBoxParameters */
  
  /*------------------------------------------------------------------*/
  /** create a text field element for the connection URL.
   * @return text field element for connection URL.
   */
  private TextField createTextFieldConnectionUrl()
  {
    String sScheme = _mapSchemes.get(getSelectedTitle());
    SiardConnection sc = SiardConnection.getSiardConnection();
    String sSampleUrl = sc.getSampleUrl(sScheme, _tfDbHost.getText(), _tfDbFolder.getText(), _tfDbName.getText());
    TextField tf = new TextField(sSampleUrl);
    tf.setPrefWidth(dWIDTH_URL);
    if (_sConnectionUrl != null)
      tf.setText(_sConnectionUrl);
    return tf;
  } /* createTextFieldConnectionUrl */
  
  /*------------------------------------------------------------------*/
  /** create the HBox with the error message, Cancel and OK button.
   * @return HBox with the error message, Cancel and OK button.
   */
  private HBox createHBoxButtons()
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    /* ok button */
    _btnDefault = new Button(sb.getOk());
    _btnDefault.setDefaultButton(true);
    _btnDefault.setOnAction(_aeh);
    double dMinWidth = _btnDefault.getLayoutBounds().getWidth();
    /* cancel button */
    _btnCancel = new Button(sb.getCancel());
    _btnCancel.setCancelButton(true);
    _btnCancel.setOnAction(_aeh);
    dMinWidth += dHSPACING + _btnCancel.getLayoutBounds().getWidth();
    _tfError = new TextField("");
    _tfError.setBackground(Background.EMPTY);
    _tfError.setStyle(FxStyles.sSTYLE_ERROR);
    HBox.setHgrow(_tfError, Priority.ALWAYS);
    /* HBox for buttons */
    HBox hboxButton = new HBox();
    hboxButton.setPadding(new Insets(dINNER_PADDING));
    hboxButton.setSpacing(dHSPACING);
    // hboxButton.setAlignment(Pos.TOP_LEFT);
    hboxButton.getChildren().add(_tfError);
    hboxButton.getChildren().add(_btnDefault);
    hboxButton.getChildren().add(_btnCancel);
    HBox.setMargin(_tfError, new Insets(dOUTER_PADDING));
    HBox.setMargin(_btnDefault, new Insets(dOUTER_PADDING));
    HBox.setMargin(_btnCancel, new Insets(dOUTER_PADDING));
    hboxButton.setMinWidth(dMinWidth);
    return hboxButton;
  } /* createHBoxButton */

  /*------------------------------------------------------------------*/
  /** create the VBox for the connection URL.
   * @return VBox with connection URL.
   */
  private VBox createVBoxConnectionUrl()
  {
    /* VBox for connection URL */
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(dOUTER_PADDING));
    vbox.setSpacing(dVSPACING/4.0);
    vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    vbox.setAlignment(Pos.TOP_LEFT);
    SiardBundle sb = SiardBundle.getSiardBundle();
    _tfConnectionUrl = createTextFieldConnectionUrl();
    _tfConnectionUrl.setTooltip(new Tooltip(sb.getConnectionUrlTooltip()));
    _tfConnectionUrl.textProperty().addListener(_scl);
    double dMinWidth = _tfConnectionUrl.getPrefWidth();
    Label lblConnectionUrl = createLabel(sb.getConnectionUrlLabel(),_tfConnectionUrl);
    if (dMinWidth < lblConnectionUrl.getPrefWidth())
      dMinWidth = lblConnectionUrl.getPrefWidth();
    vbox.getChildren().add(lblConnectionUrl);
    vbox.getChildren().add(_tfConnectionUrl);
    vbox.setMinWidth(dMinWidth);
    return vbox;
  } /* createVBoxConnectionUrl */

  /*------------------------------------------------------------------*/
  /** create the VBox with the connection parameters.
   * @param sLoadMetaDataOnlyLabel label for meta data only check box.
   * @param sLoadMetaDataOnlyTooltip tool tip for meta data only check box.
   * @param sLoadOverwriteLabel label for overwrite check box.
   * @param sLoadOverwriteTooltip tool tip for overwrite check box.
   * @param sLoadViewsAsTablesLabel label for views as tables check box.
   * @param sLoadViewsAsTablesTooltip tool tip for views as tables check box.
   * @return VBox with the connection parameters.
   */
  protected VBox createVBoxConnectionParameters(
      String sLoadMetaDataOnlyLabel, String sLoadMetaDataOnlyTooltip,
      String sLoadOverwriteLabel, String sLoadOverwriteTooltip,
      String sLoadViewsAsTablesLabel, String sLoadViewsAsTablesTooltip)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    UserProperties up = UserProperties.getUserProperties();
    _tfDbUser = new TextField();
    if (_sDbUser != null)
      _tfDbUser.setText(_sDbUser);
    else
      _tfDbUser.setText(up.getDatabaseUser());
    _tfDbUser.setTooltip(new Tooltip(sb.getConnectionDbUserTooltip()));
    _tfDbUser.textProperty().addListener(_scl);
    Label lblDbUser = createLabel(sb.getConnectionDbUserLabel()+":",_tfDbUser);
    _pfDbPassword = new PasswordField();
    _pfDbPassword.setTooltip(new Tooltip(sb.getConnectionDbPasswordTooltip()));
    Label lblDbPassword = createLabel(sb.getConnectionDbPasswordLabel()+":",_pfDbPassword);
    _cbMetaDataOnly = new CheckBox();
    _cbMetaDataOnly.setTooltip(new Tooltip(sLoadMetaDataOnlyTooltip));
    _cbMetaDataOnly.setAllowIndeterminate(false);
    Label lblMetaDataOnly = createLabel(sLoadMetaDataOnlyLabel+":",_cbMetaDataOnly);
    Label lblOverwrite = null;
    if (sLoadOverwriteLabel != null)
    {
      _cbOverwrite = new CheckBox();
      _cbOverwrite.setTooltip(new Tooltip(sLoadOverwriteTooltip));
      _cbOverwrite.setAllowIndeterminate(false);
      lblOverwrite  = createLabel(sLoadOverwriteLabel+":",_cbOverwrite);
    }
    Label lblViewsAsTables = null;
    if (sLoadViewsAsTablesLabel != null)
    {
      _cbViewsAsTables = new CheckBox();
      _cbViewsAsTables.setTooltip(new Tooltip(sLoadViewsAsTablesTooltip));
      _cbViewsAsTables.setAllowIndeterminate(false);
      lblViewsAsTables  = createLabel(sLoadViewsAsTablesLabel+":",_cbViewsAsTables);
    }

    double dLabelWidth = this.getMaxLabelPrefWidth(lblDbUser,lblDbPassword,lblMetaDataOnly,lblOverwrite,lblViewsAsTables);

    double dTextWidth = dWIDTH_URL - dLabelWidth - dHSPACING;
    _tfDbUser.setPrefWidth(dTextWidth);
    _pfDbPassword.setPrefWidth(dTextWidth);
    
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(dINNER_PADDING));
    vbox.setSpacing(dVSPACING);
    vbox.setAlignment(Pos.TOP_LEFT);
    
    HBox hboxDbUser = createHBox(lblDbUser,_tfDbUser);
    vbox.getChildren().add(hboxDbUser);
    
    HBox hboxDbPassword = createHBox(lblDbPassword,_pfDbPassword);
    vbox.getChildren().add(hboxDbPassword);
    
    HBox hboxMetaDataOnly = createHBox(lblMetaDataOnly,_cbMetaDataOnly);
    vbox.getChildren().add(hboxMetaDataOnly);

    HBox hboxOverwrite = null;
    if (sLoadOverwriteLabel != null)
    {
      hboxOverwrite = createHBox(lblOverwrite,_cbOverwrite);
      vbox.getChildren().add(hboxOverwrite);
    }
    
    HBox hboxViewsAsTables = null;
    if (sLoadViewsAsTablesLabel != null)
    {
      hboxViewsAsTables = createHBox(lblViewsAsTables,_cbViewsAsTables);
      vbox.getChildren().add(hboxViewsAsTables);
    }
    double dMinWidth = getMaxPaneMinWidth(hboxDbUser,hboxDbPassword,hboxMetaDataOnly,hboxOverwrite,hboxViewsAsTables);
    vbox.setMinWidth(dMinWidth);
    return vbox;
  } /* createVBoxConnectionParameters */
  
  /*------------------------------------------------------------------*/
  /** create the main VBox of the dialog
   * @param sLoadMetaDataOnlyLabel label for meta data only check box.
   * @param sLoadMetaDataOnlyTooltip tool tip for meta data only check box.
   * @param sLoadOverwriteLabel label for overwrite check box.
   * @param sLoadOverwriteTooltip tool tip for overwrite check box.
   * @param sLoadViewsAsTablesLabel label for views as tables check box.
   * @param sLoadViewsAsTablesTooltip tool tip for views as tables check box.
   * @return main VBox
   */
  private VBox createVBoxDialog(
      String sLoadMetaDataOnlyLabel, String sLoadMetaDataOnlyTooltip,
      String sLoadOverwriteLabel, String sLoadOverwriteTooltip,
      String sLoadViewsAsTablesLabel, String sLoadViewsAsTablesTooltip)
  {
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(dOUTER_PADDING));
    vbox.setSpacing(dVSPACING);
    vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    
    _vboxParameters = createVBoxParameters();
    vbox.getChildren().add(_vboxParameters);
    
    vbox.getChildren().add(new Separator());

    VBox vboxConnectionUrl = createVBoxConnectionUrl();
    vbox.getChildren().add(vboxConnectionUrl);
    
    VBox vboxConnectionParameters = createVBoxConnectionParameters(
        sLoadMetaDataOnlyLabel, sLoadMetaDataOnlyTooltip, 
        sLoadOverwriteLabel, sLoadOverwriteTooltip,
        sLoadViewsAsTablesLabel, sLoadViewsAsTablesTooltip);
    vbox.getChildren().add(vboxConnectionParameters);
    
    vbox.getChildren().add(new Separator());
    
    HBox hboxButton = createHBoxButtons();
    vbox.getChildren().add(hboxButton);
    
    double dMinWidth = getMaxPaneMinWidth(_vboxParameters,vboxConnectionUrl,vboxConnectionParameters,hboxButton);
    vbox.setMinWidth(dMinWidth);
    return vbox;
  } /* createVBoxDialog */
  
  /*------------------------------------------------------------------*/
  /** display the connection dialog.
   * @param stageOwner owner window.
   * @param sConnectionUrl initial value for connection (JDBC) URL or null.
   * @param sDbUser initial value for user of database or null.
   * @param sTitle title of the dialog.
   * @param sLoadMetaDataOnlyLabel label for meta data only check box.
   * @param sLoadMetaDataOnlyTooltip tool tip for meta data only check box.
   * @param sLoadOverwriteLabel label for overwrite check box.
   * @param sLoadOverwriteTooltip tool tip for overwrite check box.
   * @param sLoadViewsAsTablesLabel label for views as tables check box.
   * @param sLoadViewsAsTablesTooltip tool tip for views as tables check box.
   */
  protected ConnectionDialog(Stage stageOwner, String sConnectionUrl, String sDbUser,
    String sTitle, String sLoadMetaDataOnlyLabel, String sLoadMetaDataOnlyTooltip,
    String sLoadOverwriteLabel, String sLoadOverwriteTooltip,
    String sLoadViewsAsTablesLabel, String sLoadViewsAsTablesTooltip)
  {
    super();
    _sConnectionUrl = sConnectionUrl;
    _sDbUser = sDbUser;
    double dMinWidth = FxSizes.getTextWidth(sTitle)+FxSizes.getCloseWidth()+dHSPACING;
    VBox vboxDialog = createVBoxDialog(sLoadMetaDataOnlyLabel, sLoadMetaDataOnlyTooltip,
        sLoadOverwriteLabel, sLoadOverwriteTooltip, sLoadViewsAsTablesLabel, sLoadViewsAsTablesTooltip);
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
    /* title */
    setTitle(sTitle);
    /* style */
    initStyle(StageStyle.UTILITY);
    /* init owner */
    initOwner(stageOwner);
    /* modality */
    initModality(Modality.APPLICATION_MODAL);
  } /* constructor DownloadConnectionDialog */
  
} /* class ConnectionDialog */
