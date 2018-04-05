/*======================================================================
ConnectionDialog for entering data to connect to a database. 
Application : Siard2
Description : ConnectionDialog for entering data to connect to a 
              database. 
Platform    : Java 7, JavaFX 2.2   
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 27.06.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.dialogs;

import java.io.*;
import java.util.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.fx.dialogs.FS;
import ch.enterag.utils.logging.IndentLogger;
import ch.admin.bar.siard2.cmd.*;
import ch.admin.bar.siard2.gui.*;

/*====================================================================*/
/** ConnectionDialog for entering data to connect to a database.
 * Abstract base class for DownloadConnectionDialog and UploadConnectionDialog.
 * @author Hartwig Thomas
 */
public abstract class ConnectionDialog
  extends Stage 
  implements EventHandler<ActionEvent>, ChangeListener<String>
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(ConnectionDialog.class.getName());
  // "padding" inside the screen */
  protected static final double dSCREEN_PADDING = 10.0;
  // padding inside the dialog's VBox
  protected static final double dOUTER_PADDING = 10.0;
  // padding inside
  protected static final double dINNER_PADDING = 0.0;
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
  /** text field for database host */
  protected TextField _tfDbHost = null;
  /** text field for database name */
  protected TextField _tfDbName = null;
  /** text field for folder name */
  protected Label _lblDbFolder = null;
  /** button for triggering folder selection */
  protected Button _btnDbFolder = null;
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

  /*------------------------------------------------------------------*/
  /** if either the dbhost or the dbname changed, recompute the samle URLs.
   * @param ovs observable value.
   * @param sOld old string.
   * @param sNew new string.
   */
  @Override
  public void changed(ObservableValue<? extends String> ovs,
      String sOld, String sNew)
  {
    String sDbHost = _tfDbHost.getText();
    String sDbName = _tfDbName.getText();
    String sDbFolder = _lblDbFolder.getText();
    if ((sDbHost != null) && (sDbHost.length() > 0) &&
        (sDbFolder != null) && (sDbFolder.length() > 0) &&
        (sDbName != null) && (sDbName.length() > 0))
    {
      SiardConnection sc = SiardConnection.getSiardConnection();
      for (Iterator<String> iterScheme = _mapSampleUrls.keySet().iterator(); iterScheme.hasNext(); )
      {
        String sScheme = iterScheme.next();
        Label lblSampleUrl = _mapSampleUrls.get(sScheme);
        double dMaxWidth = lblSampleUrl.getMaxWidth();
        String sText = sc.getSampleUrl(sScheme, sDbHost, sDbFolder, sDbName);
        lblSampleUrl.setText(sText);
        double dWidth = FxSizes.getTextWidth(sText);
        if (dWidth > dMaxWidth)
          dWidth = dMaxWidth;
        lblSampleUrl.setMinWidth(dWidth);
        lblSampleUrl.setPrefWidth(dWidth);
      }
    }
  } /* changed */
  
  /*------------------------------------------------------------------*/
  /** handle the clicking of the default or cancel button.
   */
  @Override
  public void handle(ActionEvent ae)
  {
    if ((ae.getSource() == _btnDefault) || (ae.getSource() == _btnCancel))
    {
      if (ae.getSource() == _btnDefault)
      {
        UserProperties up = UserProperties.getUserProperties();
        String sDbHost = _tfDbHost.getText();
        if ((sDbHost != null) && (sDbHost.length() == 0))
          sDbHost = null; // results in default name next time
        up.setDatabaseHost(sDbHost);
        String sDbName = _tfDbName.getText();
        if ((sDbName != null) && (sDbName.length() == 0))
          sDbName = null; // results in default name next time
        up.setDatabaseName(sDbName);
        _iResult = iRESULT_SUCCESS;
      }
      close();
    }
    else if (ae.getSource() == _btnDbFolder)
    {
      UserProperties up = UserProperties.getUserProperties();
      SiardBundle sb = SiardBundle.getSiardBundle();
      String sDbFolder = _lblDbFolder.getText();
      File fileDbFolder = new File(sDbFolder);
      try
      {
        fileDbFolder = FS.chooseExistingFolder(this, 
          sb.getConnectionDbFolderTitle(), sb.getConnectionDbFolderMessage(), sb, 
          fileDbFolder);
        if (fileDbFolder != null)
        {
          up.setDatabaseFolder(fileDbFolder);
          _lblDbFolder.setText(fileDbFolder.getAbsolutePath());
        }
      }
      catch(FileNotFoundException fnfe) { _il.exception(fnfe); }
    }
    else // one of the copy buttons
    {
      String sSampleUrl = null;
      /* last label contains sample URL */
      HBox hbox = (HBox)((Node)ae.getSource()).getParent();
      for (Iterator<Node> iterChild = hbox.getChildren().iterator(); iterChild.hasNext(); )
      {
        Node node = iterChild.next();
        if (node instanceof Label)
          sSampleUrl = ((Label)node).getText();
      }
      _tfConnectionUrl.setText(sSampleUrl);
    }
  } /* handle */
  
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
   * @param lblText label for text
   * @param btn button for selector
   * @return horizontal box.
   */
  protected HBox createHBox(Label lbl, Label lblText, Button btn)
  {
    HBox hbox = new HBox();
    lbl.setAlignment(Pos.BASELINE_RIGHT);
    hbox.setPadding(new Insets(dINNER_PADDING));
    hbox.setSpacing(dHSPACING);
    hbox.setAlignment(Pos.TOP_LEFT);
    hbox.getChildren().add(lbl);
    hbox.getChildren().add(lblText);
    HBox.getHgrow(lblText);
    hbox.getChildren().add(btn);
    hbox.setMinWidth(lbl.getPrefWidth() + dHSPACING + 
        lblText.getPrefWidth() + dHSPACING +
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
    double dLabelWidth = 0.0;
    _tfDbHost = new TextField(up.getDatabaseHost());
    _tfDbHost.textProperty().addListener(this);
    HBox.setHgrow(_tfDbHost, Priority.ALWAYS);
    Label lblDbHostLabel = createLabel(sb.getConnectionDbHostLabel(),_tfDbHost);
    if (dLabelWidth < lblDbHostLabel.getPrefWidth())
      dLabelWidth = lblDbHostLabel.getPrefWidth();
    _tfDbName = new TextField(up.getDatabaseName());
    _tfDbName.textProperty().addListener(this);
    HBox.setHgrow(_tfDbName, Priority.ALWAYS);
    Label lblDbNameLabel = createLabel(sb.getConnectionDbNameLabel(),_tfDbName);
    if (dLabelWidth < lblDbNameLabel.getPrefWidth())
      dLabelWidth = lblDbNameLabel.getPrefWidth();
    _lblDbFolder = new Label(up.getDatabaseFolder().getAbsolutePath());
    _lblDbFolder.setStyle(FxStyles.sSTYLE_BACKGROUND_WHITE);
    _lblDbFolder.setAlignment(Pos.BASELINE_LEFT);
    _lblDbFolder.textProperty().addListener(this);
    _lblDbFolder.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
    _lblDbFolder.setPrefHeight(FxSizes.getNodeHeight(_tfDbHost));
    _lblDbFolder.prefWidthProperty().bind(_tfDbHost.widthProperty());
    _lblDbFolder.setPadding(_tfDbHost.getPadding()); // has been set by getNodeHeight
    _btnDbFolder = new Button(sb.getConnectionDbFolderButton());
    _btnDbFolder.setOnAction(this);
    _btnDbFolder.setAlignment(Pos.BASELINE_RIGHT);
    _btnDbFolder.setMinWidth(FxSizes.getNodeWidth(_btnDbFolder));
    Label lblDbFolderLabel = createLabel(sb.getConnectionDbFolderLabel(),_lblDbFolder);
    if (dLabelWidth < lblDbFolderLabel.getPrefWidth())
      dLabelWidth = lblDbFolderLabel.getPrefWidth();
    
    lblDbHostLabel.setMinWidth(dLabelWidth);
    lblDbHostLabel.setPrefWidth(dLabelWidth);
    lblDbNameLabel.setMinWidth(dLabelWidth);
    lblDbNameLabel.setPrefWidth(dLabelWidth);
    lblDbFolderLabel.setMinWidth(dLabelWidth);
    lblDbFolderLabel.setPrefWidth(dLabelWidth);
    double dMinWidth = 0.0;
    HBox hboxDbHost = createHBox(lblDbHostLabel, _tfDbHost);
    vbox.getChildren().add(hboxDbHost);
    if (dMinWidth < hboxDbHost.getMinWidth())
      dMinWidth = hboxDbHost.getMinWidth();
    HBox hboxDbName = createHBox(lblDbNameLabel, _tfDbName);
    vbox.getChildren().add(hboxDbName);
    if (dMinWidth < hboxDbName.getMinWidth())
      dMinWidth = hboxDbName.getMinWidth();
    HBox hboxDbFolder = createHBox(lblDbFolderLabel, _lblDbFolder, _btnDbFolder);
    vbox.getChildren().add(hboxDbFolder);
    if (dMinWidth < hboxDbFolder.getMinWidth())
      dMinWidth = hboxDbFolder.getMinWidth();
    vbox.setMinWidth(dMinWidth);
    return vbox;
  } /* createVBoxParameters */
  
  /*------------------------------------------------------------------*/
  /** create a text field element for the connection URL.
   * @return text field element for connection URL.
   */
  private TextField createTextFieldConnectionUrl()
  {
    TextField tf = new TextField();
    tf.setPrefWidth(dWIDTH_URL);
    if (_sConnectionUrl != null)
      tf.setText(_sConnectionUrl);
    return tf;
  } /* createTextFieldConnectionUrl */
  
  /*------------------------------------------------------------------*/
  /** create the HBox with the OK button.
   * @return HBox with OK button.
   */
  private HBox createHBoxButtons()
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    /* ok button */
    _btnDefault = new Button(sb.getOk());
    _btnDefault.setDefaultButton(true);
    _btnDefault.setOnAction(this);
    double dMinWidth = _btnDefault.getLayoutBounds().getWidth();
    /* cancel button */
    _btnCancel = new Button(sb.getCancel());
    _btnCancel.setCancelButton(true);
    _btnCancel.setOnAction(this);
    dMinWidth += dHSPACING + _btnCancel.getLayoutBounds().getWidth();
    /* HBox for buttons */
    HBox hboxButton = new HBox();
    hboxButton.setPadding(new Insets(dINNER_PADDING));
    hboxButton.setSpacing(dHSPACING);
    hboxButton.setAlignment(Pos.TOP_RIGHT);
    hboxButton.getChildren().add(_btnDefault);
    hboxButton.getChildren().add(_btnCancel);
    HBox.setMargin(_btnDefault, new Insets(dOUTER_PADDING));
    HBox.setMargin(_btnCancel, new Insets(dOUTER_PADDING));
    hboxButton.setMinWidth(dMinWidth);
    return hboxButton;
  } /* createHBoxButton */

  /*------------------------------------------------------------------*/
  /** create the HBox for the scheme's title, sample URL and copy button.
   * @param sScheme JDBC URL sub scheme.
   * @param dTitleWidth largest title width.
   * @return HBox for the scheme's title, sample URL and copy button.
   */
  private HBox createHBoxScheme(String sScheme, double dTitleWidth)
  {
    HBox hbox = new HBox();
    hbox.setPadding(new Insets(dINNER_PADDING));
    hbox.setSpacing(dHSPACING);
    hbox.setAlignment(Pos.BASELINE_LEFT);
    SiardConnection sc = SiardConnection.getSiardConnection();
    SiardBundle sb = SiardBundle.getSiardBundle();
    UserProperties up = UserProperties.getUserProperties();
    double dMinWidth = 2*dINNER_PADDING;
    String sTitle = sc.getTitle(sScheme)+":";
    Label lblTitle = new Label(sTitle);
    lblTitle.setTooltip(new Tooltip(sb.getConnectionSchemeTitleTooltip()));
    Region rgIndent = new Region();
    rgIndent.setMinWidth(dHSPACING);
    dMinWidth += dHSPACING;
    hbox.getChildren().add(rgIndent);
    lblTitle.setMinWidth(dTitleWidth);
    lblTitle.setPrefWidth(dTitleWidth);
    lblTitle.setAlignment(Pos.BASELINE_RIGHT);
    hbox.getChildren().add(lblTitle);
    dMinWidth = dMinWidth + dHSPACING + lblTitle.getPrefWidth();
    String sSampleUrl = sc.getSampleUrl(sScheme,up.getDatabaseHost(),up.getDatabaseFolder().getAbsolutePath(),up.getDatabaseName());
    Label lblSampleUrl = new Label(sSampleUrl);
    lblSampleUrl.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
    lblSampleUrl.setTooltip(new Tooltip(sb.getConnectionSchemeSampleUrlTooltip(sc.getTitle(sScheme))));
    lblSampleUrl.setPrefWidth(FxSizes.getTextWidth(sSampleUrl));
    hbox.getChildren().add(lblSampleUrl);
    dMinWidth = dMinWidth + dHSPACING /* +lblSampleUrl.getPrefWidth() */;
    _mapSampleUrls.put(sScheme,lblSampleUrl);
    Region rgGrow = new Region();
    HBox.setHgrow(rgGrow, Priority.ALWAYS);
    hbox.getChildren().add(rgGrow);
    dMinWidth = dMinWidth + dHSPACING;
    Button btnScheme = new Button(sb.getConnectionSchemeCopy());
    btnScheme.setMinWidth(FxSizes.getNodeWidth(btnScheme));
    btnScheme.setTooltip(new Tooltip(sb.getConnectionSchemeCopyTooltip(sc.getSampleUrl(sScheme,"dbserver.enterag.ch","D:\\dbfolder","testdb"))));
    btnScheme.setOnAction(this);
    dMinWidth = dMinWidth + dHSPACING + FxSizes.getNodeWidth(btnScheme);
    hbox.getChildren().add(btnScheme);
    double dSampleUrlWidth = dMinWidth; 
    if (dMinWidth < dWIDTH_URL)
      dMinWidth = dWIDTH_URL;
    dSampleUrlWidth = dMinWidth - dSampleUrlWidth;
    lblSampleUrl.setMaxWidth(dSampleUrlWidth);
    hbox.setMinWidth(dMinWidth);
    hbox.setMaxWidth(dMinWidth);
    return hbox;
  } /* createHBoxScheme */
  
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
    double dMinWidth = _tfConnectionUrl.getPrefWidth();
    Label lblConnectionUrl = createLabel(sb.getConnectionUrlLabel(),_tfConnectionUrl);
    if (dMinWidth < lblConnectionUrl.getPrefWidth())
      dMinWidth = lblConnectionUrl.getPrefWidth();
    vbox.getChildren().add(lblConnectionUrl);
    SiardConnection sc = SiardConnection.getSiardConnection();
    String[] asSchemes = sc.getSchemes();
    double dTitleWidth = 0.0;
    for (int i = 0; i < asSchemes.length;i++)
    {
      double d = FxSizes.getTextWidth(sc.getTitle(asSchemes[i])+":")+2.0;
      if (dTitleWidth < d)
        dTitleWidth = d;
    }
    for (int i = 0; i < asSchemes.length; i++)
    {
      HBox hboxScheme = createHBoxScheme(asSchemes[i],dTitleWidth);
      if (dMinWidth < hboxScheme.getMinWidth())
        dMinWidth = hboxScheme.getMinWidth();
      vbox.getChildren().add(hboxScheme);
    }
    vbox.getChildren().add(_tfConnectionUrl);
    vbox.setMinWidth(dMinWidth);
    return vbox;
  } /* createVBoxConnectionUrl */

  /*------------------------------------------------------------------*/
  /** set the minimum width to the maximum preferred width of the given 
   * labels.
   * @param albl labels.
   * @return maximum preferred width.
   */
  private double setMaxLabelWidth(Label[] albl)
  {
    double dLabelWidth = 0.0;
    for (int i = 0; i < albl.length; i++)
    {
      if ((albl[i] != null) && (dLabelWidth < albl[i].getPrefWidth()))
        dLabelWidth = albl[i].getPrefWidth();
    }
    for (int i = 0; i < albl.length; i++)
    {
      if (albl[i] != null)
        albl[i].setMinWidth(dLabelWidth);
    }
    return dLabelWidth;
  } /* setMaxLabelWidth */
  
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
    _tfDbUser = new TextField();
    if (_sDbUser != null)
      _tfDbUser.setText(_sDbUser);
    _tfDbUser.setTooltip(new Tooltip(sb.getConnectionDbUserTooltip()));
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

    double dLabelWidth = setMaxLabelWidth(new Label[] {lblDbUser,lblDbPassword,lblMetaDataOnly,lblOverwrite,lblViewsAsTables});

    double dTextWidth = dWIDTH_URL - dLabelWidth - dHSPACING;
    _tfDbUser.setPrefWidth(dTextWidth);
    _pfDbPassword.setPrefWidth(dTextWidth);
    
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(dINNER_PADDING));
    vbox.setSpacing(dVSPACING);
    vbox.setAlignment(Pos.TOP_LEFT);
    double dMinWidth = 0;
    
    HBox hboxDbUser = createHBox(lblDbUser,_tfDbUser);
    if (dMinWidth < hboxDbUser.getMinWidth())
      dMinWidth = hboxDbUser.getMinWidth();
    vbox.getChildren().add(hboxDbUser);
    
    HBox hboxDbPassword = createHBox(lblDbPassword,_pfDbPassword);
    if (dMinWidth < hboxDbPassword.getMinWidth())
      dMinWidth = hboxDbPassword.getMinWidth();
    vbox.getChildren().add(hboxDbPassword);
    
    HBox hboxMetaDataOnly = createHBox(lblMetaDataOnly,_cbMetaDataOnly);
    if (dMinWidth < hboxMetaDataOnly.getMinWidth())
      dMinWidth = hboxMetaDataOnly.getMinWidth();
    vbox.getChildren().add(hboxMetaDataOnly);

    if (sLoadOverwriteLabel != null)
    {
      HBox hboxOverwrite = createHBox(lblOverwrite,_cbOverwrite);
      if (dMinWidth < hboxOverwrite.getMinWidth())
        dMinWidth = hboxOverwrite.getMinWidth();
      vbox.getChildren().add(hboxOverwrite);
    }
    
    if (sLoadViewsAsTablesLabel != null)
    {
      HBox hboxViewsAsTables = createHBox(lblViewsAsTables,_cbViewsAsTables);
      if (dMinWidth < hboxViewsAsTables.getMinWidth())
        dMinWidth = hboxViewsAsTables.getMinWidth();
      vbox.getChildren().add(hboxViewsAsTables);
    }
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
    double dMinWidth = 0;
    
    VBox vboxParameters = createVBoxParameters();
    if (dMinWidth < vboxParameters.getMinWidth())
      dMinWidth = vboxParameters.getMinWidth();
    vbox.getChildren().add(vboxParameters);
    
    vbox.getChildren().add(new Separator());

    VBox vboxConnectionUrl = createVBoxConnectionUrl();
    if (dMinWidth < vboxConnectionUrl.getMinWidth())
      dMinWidth = vboxConnectionUrl.getMinWidth();
    vbox.getChildren().add(vboxConnectionUrl);
    
    VBox vboxConnectionParameters = createVBoxConnectionParameters(
        sLoadMetaDataOnlyLabel, sLoadMetaDataOnlyTooltip, 
        sLoadOverwriteLabel, sLoadOverwriteTooltip,
        sLoadViewsAsTablesLabel, sLoadViewsAsTablesTooltip);
    if (dMinWidth < vboxConnectionParameters.getMinWidth())
      dMinWidth = vboxConnectionParameters.getMinWidth();
    vbox.getChildren().add(vboxConnectionParameters);
    
    vbox.getChildren().add(new Separator());
    
    HBox hboxButton = createHBoxButtons();
    if (dMinWidth < hboxButton.getMinWidth())
      dMinWidth = hboxButton.getMinWidth();
    vbox.getChildren().add(hboxButton);
    
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
  
} /* class DownloadConnectionDialog */
