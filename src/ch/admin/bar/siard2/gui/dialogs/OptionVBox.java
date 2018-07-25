/*======================================================================
The editor VBox of a single option.  
Application: SIARD GUI
Description: The editor VBox of a single option. 
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 25.07.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.dialogs;

import java.io.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.fx.dialogs.*;
import ch.enterag.utils.io.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.gui.*;

/*====================================================================*/
/** The editor VBox of a single option.
 * @param T value type:
 *   File for files and folders, 
 *   Integer for integers, 
 *   String for strings.
 * @author Hartwig Thomas
 *
 */
public class OptionVBox<T>
  extends VBox
  implements EventHandler<ActionEvent>, ChangeListener<String>
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(OptionVBox.class.getName());
  // padding inside
  private static final double dINNER_PADDING = 0.0;
  // horizontal spacing of elements
  private static final double dHSPACING = 10.0;
  // vertical spacing of elements
  private static final double dVSPACING = 2.0;
  // class
  private Class<T> _cls;
  // initial file or folder
  private File _fileInitial = null;
  private Stage _stageDialog = null;
  // title for browse selector
  private String _sOptionTitle = null;
  // message for browse selector
  private String _sOptionMessage = null;
  // text field
  private TextField _tf = null;
  // check box
  private CheckBox _cb = null;
  /*------------------------------------------------------------------*/
  /** return the current value of the text field.
   * @return edited value.
   */
  @SuppressWarnings("unchecked")
  public T getValue()
  {
    T oValue = null;
    if (_cls != Boolean.class)
    {
      String sText = _tf.getText();
      if (_cls == File.class)
        oValue = (T)SpecialFolder.findInPath(sText);
      else if (_cls == Integer.class)
        oValue = (T)Integer.valueOf(Integer.parseInt(sText));
      else
        oValue = (T)sText;
    }
    else if (_cls == Boolean.class)
      oValue = (T)Boolean.valueOf(_cb.isSelected());
    return oValue;
  } /* getValue */

  /*------------------------------------------------------------------*/
  /** handle the pressing of a Browse button.
   * @param ae action event from Browse button.
   */
  @Override
  public void handle(ActionEvent ae)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    try
    {
      File file = null;
      if ((_fileInitial != null) && _fileInitial.exists() && _fileInitial.isDirectory())
      {
        file = FS.chooseExistingFolder(_stageDialog, 
          _sOptionTitle, _sOptionMessage, (FxBundle)sb, _fileInitial);
      }
      else
      {
        file = FS.chooseExistingFile(_stageDialog, 
          _sOptionTitle, _sOptionMessage, (FxBundle)sb, _fileInitial, null);
      }
      if (file != null)
        _tf.setText(file.getPath());
    }
    catch(FileNotFoundException fnfe) { _il.exception(fnfe); }
  } /* handle */
  
  /*------------------------------------------------------------------*/
  /** change listener prevents non-integer input.
   */
  @Override
  public void changed(ObservableValue<? extends String> ovs,
      String sOld, String sNew)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    try { Integer.parseInt(sNew); }
    catch(NumberFormatException nfe)
    {
      MB.show(_stageDialog, 
        _sOptionTitle, sb.getOptionNotIntegerMessage(), 
        sb.getOk(), null);
      _tf.setText(sOld);
    }
  } /* changed */
  
  /*------------------------------------------------------------------*/
  /** create a label for the given node.
   * @param sLabel label text
   * @param nodeFor node associated with label or null, for none.
   * @return label.
   */
  private Label createLabel(String sLabel, Node nodeFor)
  {
    Label lbl = new Label(sLabel);
    // lbl.setMinWidth(FxSizes.getNodeWidth(lbl));
    // lbl.setPrefWidth(lbl.getMinWidth());
    // lbl.setMinHeight(FxSizes.getNodeHeight(lbl));
    // lbl.setPrefHeight(lbl.getMinHeight());
    if (nodeFor != null)
      lbl.setLabelFor(nodeFor);
    return lbl;
  } /* createLabel */
  
  /*------------------------------------------------------------------*/
  /** create a text field.
   * @param sText initial text of text field.
   * @return text field.
   */
  private TextField createTextField(String sText)
  {
    _tf = new TextField(sText);
    return _tf;
  } /* createTextField */

  /*------------------------------------------------------------------*/
  /** create a check box
   * @param bSelected initial selection of check box.
   * @return check box.
   */
  private CheckBox createCheckBox(boolean bSelected)
  {
    _cb = new CheckBox();
    _cb.setIndeterminate(false);
    _cb.setSelected(bSelected);
    return _cb;
  } /* createCheckBox */

  /*------------------------------------------------------------------*/
  /** create a HBox containing a label of the given width and a text input field.
   * @param sLabel label text.
   * @param dLabelWidth label width.
   * @param oValue initial value of the option.
   * @return HBox containing the label and the text field.
   */
  private HBox createHBoxLabeledText(String sLabel, double dLabelWidth, T oValue)
  {
    HBox hbox = new HBox();
    hbox.setPadding(new Insets(dINNER_PADDING));
    hbox.setSpacing(dHSPACING);
    Label lbl = null;
    if (_cls != Boolean.class)
    {
      String sText = null;
      if (_cls == File.class)
        sText = ((File)oValue).getPath();
      else if (_cls == Integer.class)
        sText = String.valueOf((Integer)oValue);
      else
        sText = (String)oValue;
      TextField tf = createTextField(sText);
      if (_cls == Integer.class)
        tf.textProperty().addListener(this);
      lbl = createLabel(sLabel, tf);
      lbl.setMinHeight(FxSizes.getNodeHeight(tf));
      hbox.getChildren().add(lbl);
      hbox.getChildren().add(tf);
      HBox.setHgrow(tf, Priority.ALWAYS);
      if (_cls == File.class)
      {
        Button btnBrowse = new Button(SiardBundle.getSiardBundle().getOptionBrowse());
        btnBrowse.setOnAction(this);
        hbox.getChildren().add(btnBrowse);
      }
    }
    else
    {
      boolean bSelected = ((Boolean)oValue).booleanValue();
      CheckBox cb = createCheckBox(bSelected);
      lbl = createLabel(sLabel, cb);
      lbl.setMinHeight(FxSizes.getNodeHeight(cb));
      hbox.getChildren().add(lbl);
      hbox.getChildren().add(cb);
    }
    lbl.setMinWidth(dLabelWidth);
    lbl.setAlignment(Pos.BASELINE_RIGHT);
    return hbox;
  } /* createHBoxLabeledText */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * creates a VBox containing an explanation and a labeled text input field.
   * @param sExplanation explanation of the value.
   * @param sLabel label of the value.
   * @param dLabelWidth width of the label.
   * @param oValue initial value (File, Integer or String) - must not be null
   *   if it is a folder.
   */
  public OptionVBox(Stage stageDialog, String sExplanation, String sLabel, double dLabelWidth, 
    Class<T> cls, T oValue)
  {
    super();
    _cls = cls;
    _stageDialog = stageDialog;
    _sOptionTitle = sLabel;
    _sOptionMessage = sExplanation;
    if (cls == File.class)
      _fileInitial = (File)oValue;
    setPadding(new Insets(dINNER_PADDING));
    setSpacing(dVSPACING);
    Label lblExplanation = createLabel(sExplanation,null);
    getChildren().add(lblExplanation);
    HBox hbox = createHBoxLabeledText(sLabel,dLabelWidth,oValue);
    getChildren().add(hbox);
    getChildren().add(new Separator());
    setMinWidth(FxSizes.getNodeWidth(this));
    setPrefWidth(getMinWidth());
    setMinHeight(FxSizes.getNodeHeight(this));
    setPrefHeight(getMinHeight());
  } /* constructor */

} /* OptionsVBox */
