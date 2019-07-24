/*======================================================================
OptionDialog display options for modification.
Application : Siard2
Description : OptionDialog display options for modification. 
Platform    : Java 7, JavaFX 2.2   
------------------------------------------------------------------------
Copyright  : 2017, Enter AG, Rüti ZH, Switzerland
Created    : 16.08.2017, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.gui.dialogs;

import java.io.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.fx.dialogs.FS;
import ch.admin.bar.siard2.gui.*;

/*====================================================================*/
/** OptionDialog display options for modification.
 * @author Hartwig Thomas
 */
public class OptionDialog
  extends ScrollableDialog 
  implements EventHandler<ActionEvent>
{
  // limits for column width
  private static final int iMIN_COLUMN_WIDTH = 4;
  private static final int iMAX_COLUMN_WIDTH = 80;
  
  private boolean _bCanceled = true;  // just closing with the close button is canceling
  public boolean isCanceled() { return _bCanceled; }
  private OptionVBox<Integer> _ovbLoginTimeout = null;
  private OptionVBox<Integer> _ovbQueryTimeout = null;
  private OptionVBox<Integer> _ovbColumnWidth = null;
  private OptionVBox<Boolean> _ovbFileChooserNative = null;
  private OptionVBox<File> _ovbTextEditor = null;
  private OptionVBox<File> _ovbBinaryEditor = null;
  private OptionVBox<File> _ovbXslFile = null;
  private OptionVBox<File> _ovbLobsFolder = null;
  private Button _btnDefault = null;
  private Button _btnCancel = null;

  /*------------------------------------------------------------------*/
  /** handle the action of one of the buttons.
   * @param action event.
   */
  @Override
  public void handle(ActionEvent ae)
  {
    if (ae.getSource() == _btnDefault)
    {
      UserProperties up = UserProperties.getUserProperties();
      int iLoginTimeout = _ovbLoginTimeout.getValue().intValue();
      if (iLoginTimeout < 0)
        iLoginTimeout = 0;
      up.setLoginTimeoutSeconds(iLoginTimeout);
      int iQueryTimeout = _ovbQueryTimeout.getValue().intValue();
      if (iQueryTimeout < 0)
        iQueryTimeout = 0;
      up.setQueryTimeoutSeconds(iQueryTimeout);
      int iColumnWidth = _ovbColumnWidth.getValue().intValue();
      if (iColumnWidth < iMIN_COLUMN_WIDTH)
        iColumnWidth = iMIN_COLUMN_WIDTH;
      if (iColumnWidth > iMAX_COLUMN_WIDTH)
        iColumnWidth = iMAX_COLUMN_WIDTH;
      up.setColumnWidth(iColumnWidth);
      boolean bFileChooserNative = _ovbFileChooserNative.getValue().booleanValue();
      System.setProperty(FS.sUSE_NATIVE_PROPERTY, String.valueOf(bFileChooserNative));
      File fileTextEditor = _ovbTextEditor.getValue();
      if ((fileTextEditor != null) && 
          fileTextEditor.exists() && 
          fileTextEditor.isFile())
        up.setTextEditor(fileTextEditor);
      File fileBinaryEditor = _ovbBinaryEditor.getValue();
      if ((fileBinaryEditor != null) && 
          fileBinaryEditor.exists() && 
          fileBinaryEditor.isFile())
        up.setBinEditor(fileBinaryEditor);
      File fileXslFile = _ovbXslFile.getValue();
      if ((fileXslFile != null) &&
          fileXslFile.exists() &&
          fileXslFile.isFile())
        up.setXslFile(fileXslFile);
      File fileLobsFolder = _ovbLobsFolder.getValue();
      if ((fileLobsFolder != null) && (fileLobsFolder.exists() && fileLobsFolder.isDirectory()))
        up.setLobsFolder(fileLobsFolder);
      _bCanceled = false;
    }
    close();
  } /* handle */

  /*------------------------------------------------------------------*/
  /** compute the maximum text width of the given strings.
   * @param as strings.
   * @return maximum text width.
   */
  private double getMaxWidth(String[] as)
  {
    double dWidth = 0.0;
    for (int i = 0; i < as.length; i++)
    {
      if (dWidth < FxSizes.getTextWidth(as[i]))
        dWidth = FxSizes.getTextWidth(as[i]);
    }
    return dWidth;
  } /* getMaxWidth */
  
  /*------------------------------------------------------------------*/
  /** create the HBox with the OK button.
   * @return HBox with OK button.
   */
  private HBox createHBoxButtons()
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    /* ok button */
    // label is needed for tooltip on disabled button
    _btnDefault = new Button(sb.getOk());
    _btnDefault.setDefaultButton(true);
    _btnDefault.setOnAction(this);
    if (!SiardGui.isRunningInstalled())
      _btnDefault.setTooltip(new Tooltip(sb.getOptionNotstoredTooltip()));
    // double dMinWidth = _btnDefault.getLayoutBounds().getWidth();
    /* cancel button */
    _btnCancel = new Button(sb.getCancel());
    _btnCancel.setCancelButton(true);
    _btnCancel.setOnAction(this);
    // dMinWidth += dHSPACING + _btnCancel.getLayoutBounds().getWidth();
    /* HBox for buttons */
    HBox hboxButton = new HBox();
    hboxButton.setPadding(new Insets(dINNER_PADDING));
    hboxButton.setSpacing(dHSPACING);
    hboxButton.setAlignment(Pos.TOP_RIGHT);
    hboxButton.getChildren().add(_btnDefault);
    hboxButton.getChildren().add(_btnCancel);
    HBox.setMargin(_btnDefault, new Insets(dOUTER_PADDING));
    HBox.setMargin(_btnCancel, new Insets(dOUTER_PADDING));
    // hboxButton.setMinWidth(dMinWidth);
    return hboxButton;
  } /* createHBoxButtons */

  /*------------------------------------------------------------------*/
  /** create the dialog pane containing the option editors and 
   * the ok and cancel button.
   * @return
   */
  private VBox createVBoxDialog()
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    UserProperties up = UserProperties.getUserProperties();
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(dOUTER_PADDING));
    vbox.setSpacing(dVSPACING);
    vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    
    String sLoginTimeoutLabel = sb.getOptionLoginTimeoutLabel(); 
    String sQueryTimeoutLabel = sb.getOptionQueryTimeoutLabel(); 
    String sColumnWidthLabel = sb.getOptionColumnWidthLabel(); 
    String sFileChooserNativeLabel = sb.getOptionFileChooserNativeLabel(); 
    String sTextEditorLabel = sb.getOptionTextEditorLabel(); 
    String sBinaryEditorLabel = sb.getOptionBinaryEditorLabel(); 
    String sXslFileLabel = sb.getOptionXslFileLabel(); 
    String sLobsFolderLabel = sb.getOptionLobsFolderLabel();
    double dLabelWidth = getMaxWidth(new String[] 
      {
          sLoginTimeoutLabel,
          sQueryTimeoutLabel,
          sColumnWidthLabel,
          sFileChooserNativeLabel,
          sTextEditorLabel,
          sBinaryEditorLabel,
          sXslFileLabel,
          sLobsFolderLabel,
      })+2.0;
    _ovbLoginTimeout = new OptionVBox<Integer>(this,sb.getOptionLoginTimeoutExplanation(),
      sLoginTimeoutLabel,dLabelWidth,Integer.class,Integer.valueOf(up.getLoginTimeoutSeconds()));
    vbox.getChildren().add(_ovbLoginTimeout);
    _ovbQueryTimeout = new OptionVBox<Integer>(this,sb.getOptionQueryTimeoutExplanation(),
      sQueryTimeoutLabel,dLabelWidth,Integer.class,Integer.valueOf(up.getQueryTimeoutSeconds()));
    vbox.getChildren().add(_ovbQueryTimeout);
    _ovbColumnWidth = new OptionVBox<Integer>(this,sb.getOptionColumnWidthExplanation(),
      sColumnWidthLabel,dLabelWidth,Integer.class,Integer.valueOf(up.getColumnWidth()));
    vbox.getChildren().add(_ovbColumnWidth);
    _ovbFileChooserNative = new OptionVBox<Boolean>(this,sb.getOptionFileChooserNativeExplanation(),
      sFileChooserNativeLabel,dLabelWidth,Boolean.class,Boolean.valueOf(Boolean.valueOf(System.getProperty(FS.sUSE_NATIVE_PROPERTY))));
    vbox.getChildren().add(_ovbFileChooserNative);
    _ovbTextEditor = new OptionVBox<File>(this,sb.getOptionTextEditorExplanation(),
      sTextEditorLabel,dLabelWidth,File.class,up.getTextEditor());
    vbox.getChildren().add(_ovbTextEditor);
    _ovbBinaryEditor = new OptionVBox<File>(this,sb.getOptionBinaryEditorExplanation(),
      sBinaryEditorLabel,dLabelWidth,File.class,up.getBinEditor());
    vbox.getChildren().add(_ovbBinaryEditor);
    _ovbXslFile = new OptionVBox<File>(this,sb.getOptionXslFileExplanation(),
      sXslFileLabel,dLabelWidth,File.class,up.getXslFile());
    vbox.getChildren().add(_ovbXslFile);
    _ovbLobsFolder = new OptionVBox<File>(this,sb.getOptionLobsFolderExplanation(),
      sLobsFolderLabel,dLabelWidth,File.class,up.getLobsFolder());
    vbox.getChildren().add(_ovbLobsFolder);
    vbox.getChildren().add(createHBoxButtons());
    vbox.setMinWidth(FxSizes.getNodeWidth(vbox));
    vbox.setMinHeight(FxSizes.getNodeHeight(vbox));
    return vbox;
  } /* createVBoxDialog */
  
  /*------------------------------------------------------------------*/
  /** create the option dialog.
   * @param stageOwner owner stage.
   */
  private OptionDialog(Stage stageOwner)
  {
    super(stageOwner,SiardBundle.getSiardBundle().getOptionTitle());
    VBox vbox = createVBoxDialog();
    /* scene */
    Scene scene = new Scene(vbox, vbox.getMinWidth()+10.0, vbox.getMinHeight()+10.0);
    setScene(scene);
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** shows the option dialog.
   * @param stageOwner owner window.
   * @return option dialog with results.
   */
  public static OptionDialog showOptionDialog(Stage stageOwner)
  {
    OptionDialog od = new OptionDialog(stageOwner);
    od.showAndWait();
    return od;
  } /* showOptionDialog */

} /* OptionDialog */
