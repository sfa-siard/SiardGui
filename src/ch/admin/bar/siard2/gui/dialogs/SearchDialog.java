/*======================================================================
SearchDialog asks for a string to find in the table data.
Application : Siard2
Description : SearchDialog asks for a string to find in the table data. 
Platform    : Java 7, JavaFX 2.2   
------------------------------------------------------------------------
Copyright  : 2017, Enter AG, Rüti ZH, Switzerland
Created    : 31.08.2017, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.gui.dialogs;

import java.util.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import ch.enterag.utils.fx.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.gui.*;

/*====================================================================*/
/** SearchDialog asks for a string to find in the table data.
 * @author Hartwig Thomas
 */
public class SearchDialog
  extends Stage 
  implements EventHandler<ActionEvent>
{
  private static final double dINNER_PADDING = 10.0;
  private static final double dVSPACING = 10.0;
  private static final double dHSPACING = 10.0;
  private static final int iTEXT_COLUMNS = 32;
  private boolean _bCanceled = true; // just closing with the close button is canceling
  public boolean isCanceled() { return _bCanceled; }
  
  private TextField _tfFindString = null;
  public String getFindString() 
  { 
    return _tfFindString.getText();
  }
  private CheckBox _cbMatchCase = null;
  public boolean mustMatchCase() { return _cbMatchCase.isSelected(); }
  private MetaColumnsVBox _mcvb = null;
  public List<MetaColumn> getSelection() { return _mcvb.getSelection(); }
  private Button _btnOk = null;
  private Button _btnCancel = null;

  private void disableOk()
  {
    _btnOk.setDisable((getFindString().length() == 0) || (getSelection().size() == 0));
  } /* disableOk */
  
  /*==================================================================*/
  private class BooleanChangeListener
    implements ChangeListener<Boolean>
  {
    /*------------------------------------------------------------------*/
    /** react to change in selection of columns.
     */
    @Override
    public void changed(ObservableValue<? extends Boolean> ovb,
        Boolean bSelectedOld, Boolean bSelectedNew)
    {
      disableOk();
    } /* changed */
  } /* class BooleanChangeListener */
  private BooleanChangeListener _bcl = new BooleanChangeListener();
  
  /*==================================================================*/
  private class StringChangeListener
    implements ChangeListener<String>
  {
    /** react to change in find string.
     */
    @Override
    public void changed(ObservableValue<? extends String> ovs,
        String sOld, String sNew)
    {
      disableOk();
    } /* changed */
  } /* class StringChangeListener */
  private StringChangeListener _scl = new StringChangeListener();
  
  /*------------------------------------------------------------------*/
  /** handle pressed button.
   * @param ae action event.
   */
  @Override
  public void handle(ActionEvent ae)
  {
    if (ae.getSource() == _btnOk)
      _bCanceled = false;
    close();
  } /* handle */
  
  /*------------------------------------------------------------------*/
  /** create a HBox with OK and Cancel button.
   * @param sOk text on OK button.
   * @param sCancel text on Cancel button.
   * @return HBox.
   */
  private HBox createButtonsHBox(String sOk, String sCancel)
  {
    HBox hbox = new HBox();
    hbox.setSpacing(dHSPACING);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    hbox.setAlignment(Pos.BASELINE_RIGHT);
    /* buttons and their width */
    _btnOk = new Button(sOk);
    _btnOk.setDefaultButton(true);
    _btnOk.setOnAction(this);
    hbox.getChildren().add(_btnOk);
    _btnCancel = new Button(sCancel);
    _btnCancel.setCancelButton(true); // associate it with ESC key
    _btnCancel.setOnAction(this);
    hbox.getChildren().add(_btnCancel);
    return hbox;
  } /* createButtonsHBox */

  /*------------------------------------------------------------------*/
  /** create the columns VBox
   * @param sb string pool.
   * @param table table to be searched.
   * @return columns VBox with all/none button.
   */
  private VBox createColumnsVBox(SiardBundle sb, MetaTable mt)
  {
    _mcvb = new MetaColumnsVBox(sb.getSearchExplanation(),mt,sb.getSearchSelectAll(),sb.getSearchSelectNone());
    _mcvb.addSelectionChangeListener(_bcl);
    return _mcvb;
  } /* createColumnsVBox */
  
  /*------------------------------------------------------------------*/
  /** create HBox with label and check box for match case.
   * @param sMatchCaseLabel label.
   * @param dLabelWidth width of label.
   * @param bMatchCase initial value of match case.
   * @return HBox with label and check box.
   */
  private HBox createMatchCaseHBox(String sMatchCaseLabel, double dLabelWidth,
    boolean bMatchCase)
  {
    HBox hbox = new HBox();
    hbox.setSpacing(dHSPACING);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    _cbMatchCase = new CheckBox();
    _cbMatchCase.setSelected(bMatchCase);
    Label lblMatchCase = new Label(sMatchCaseLabel);
    lblMatchCase.setLabelFor(_cbMatchCase);
    lblMatchCase.setAlignment(Pos.BASELINE_RIGHT);
    hbox.getChildren().add(lblMatchCase);
    hbox.getChildren().add(_cbMatchCase);
    return hbox;
  } /* createMatchCaseHBox */

  /*------------------------------------------------------------------*/
  /** create HBox with label and text field for find string.
   * @param sFindStringLabel label.
   * @param dLabelWidth width of label.
   * @param sFindString initial value of find string.
   * @return HBox with label and text field.
   */
  private HBox createFindStringHBox(String sFindStringLabel, double dLabelWidth, 
    String sFindString)
  {
    HBox hbox = new HBox();
    hbox.setSpacing(dHSPACING);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    _tfFindString = new TextField();
    if (sFindString != null)
      _tfFindString.setText(sFindString);
    _tfFindString.setMinWidth(FxSizes.fromEms(iTEXT_COLUMNS));
    _tfFindString.textProperty().addListener(_scl);
    Label lblFindString = new Label(sFindStringLabel);
    lblFindString.setMinWidth(dLabelWidth);
    lblFindString.setLabelFor(_tfFindString);
    lblFindString.setAlignment(Pos.BASELINE_RIGHT);
    hbox.getChildren().add(lblFindString);
    hbox.getChildren().add(_tfFindString);
    return hbox;
  } /* createFindStringHBox */
  
  /*------------------------------------------------------------------*/
  /** create the main VBox of the dialog.
   * @param sb string pool.
   * @param mt meta data of table to be searched.
   * @param sFindString initial find string.
   * @param bMatchCase initial match case value.
   * @return VBox.
   */
  private VBox createVBox(SiardBundle sb, MetaTable mt, String sFindString, boolean bMatchCase)
  {
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(dINNER_PADDING));
    vbox.setSpacing(dVSPACING);
    vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    String sFindStringLabel = sb.getFindStringLabel();
    String sMatchCaseLabel = sb.getFindMatchCaseLabel();
    double dLabelWidth = FxSizes.getTextWidth(sFindStringLabel);
    if (dLabelWidth < FxSizes.getTextWidth(sMatchCaseLabel))
      dLabelWidth = FxSizes.getTextWidth(sMatchCaseLabel);
    vbox.getChildren().add(createFindStringHBox(sFindStringLabel, dLabelWidth, sFindString));
    vbox.getChildren().add(createMatchCaseHBox(sMatchCaseLabel, dLabelWidth,bMatchCase));
    vbox.getChildren().add(new Separator());
    vbox.getChildren().add(this.createColumnsVBox(sb,mt));
    vbox.getChildren().add(new Separator());
    vbox.getChildren().add(createButtonsHBox(sb.getOk(),sb.getCancel()));
    vbox.setMinWidth(FxSizes.getNodeWidth(vbox));
    vbox.setMinHeight(FxSizes.getNodeHeight(vbox));
    return vbox;
  } /* createVBox */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param stageOwner owner window.
   * @param mt meta data of table to be searched.
   * @param sFindString initial find string value.
   * @param bMatchCase initial match case value.
   */
  private SearchDialog(Stage stageOwner, MetaTable mt, String sFindString, boolean bMatchCase)
  {
    super();
    SiardBundle sb = SiardBundle.getSiardBundle();
    VBox vbox = createVBox(sb, mt, sFindString, bMatchCase);
    /* scene */
    Scene scene = new Scene(vbox, vbox.getMinWidth()+10.0, vbox.getMinHeight()+10.0);
    setScene(scene);
    /* title */
    setTitle(sb.getSearchTitle(mt.getName()));
    /* style */
    initStyle(StageStyle.UTILITY);
    /* init owner */
    initOwner(stageOwner);
    /* modality */
    initModality(Modality.APPLICATION_MODAL);
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** shows search dialog.
   * @param stageOwner owner window.
   * @param mt meta data of table to be searched.
   * @param sFindString initial find string value.
   * @param bMatchCase initial match case value.
   * @return search dialog with result.
   */
  public static SearchDialog showSearchDialog(Stage stageOwner, 
    MetaTable mt, String sFindString, boolean bMatchCase)
  {
    SearchDialog sd = new SearchDialog(stageOwner,mt,sFindString,bMatchCase);
    sd.showAndWait();
    return sd;
  } /* showSearchDialog */

} /* SearchDialog */
