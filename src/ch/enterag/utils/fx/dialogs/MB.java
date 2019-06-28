/*======================================================================
MB is a trivial dialog for displaying a message and one or two buttons. 
Version     : $Id: MessageBox.java 607 2016-02-23 12:18:01Z hartwig $
Application : JavaFX Utilities.
Description : MB is a trivial dialog for displaying a message and one or two buttons. 
Platform    : Java 7, JavaFX 2.2   
------------------------------------------------------------------------
Copyright  : 2015, Enter AG, Rüti ZH, Switzerland
Created    : 22.12.2015, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.fx.dialogs;

import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.fx.Dialog;

/*====================================================================*/
/** A trivial dialog for displaying a message and one or two buttons. 
 * @author Hartwig Thomas
 */
public class MB 
  extends Dialog 
  implements EventHandler<ActionEvent>
{
  // "padding" inside the screen */
  private static final double dSCREEN_PADDING = 10.0;
  // outer padding
  private static final double dOUTER_PADDING = 10.0;
  // inner padding
  private static final double dINNER_PADDING = 0.0;
  // vertical spacing of elements
  private static final double dVSPACING = 10.0;
  // horizontal spacing of elements
  private static final double dHSPACING = 10.0;
  
  /** default button (= Enter) could be the OK or Yes button */
  private Button _btnDefault = null;
  /** cancel button (= Escape) could be the Cancel or No button */
  private Button _btnCancel = null;
  /** result will be 1 for default, 0 otherwise */
  public static final int iRESULT_CANCELED = 0;
  public static final int iRESULT_SUCCESS = 1;
  private int _iResult = iRESULT_CANCELED;
  private int getResult() { return _iResult; }

  /*------------------------------------------------------------------*/
  /** remember which button was pressed and close (hide) the window.
   */
  @Override
  public void handle(ActionEvent ae)
  {
    if (ae.getSource() == _btnDefault)
      _iResult = iRESULT_SUCCESS;
    close();
  } /* handle */

  /*------------------------------------------------------------------*/
  /** a message box is a modal dialog which contains a prompt and
   * some buttons.
   * @param stageOwner owner window.
   * @param sTitle title of the message box.
   * @param sPrompt prompt text.
   * @param sButtonDefault displayed on first (default) button.
   * @param sButtonCancel displayed on second (cancel) button or null, 
   *                      if only one button is to be displayed.
   */
  private MB(Stage stageOwner, String sTitle, String sPrompt, String sButtonDefault, String sButtonCancel)
  {
    super(stageOwner,sTitle);
    double dMinWidth = FxSizes.getTextWidth(sTitle)+FxSizes.getCloseWidth()+dHSPACING;
    
    /* buttons and their width */
    _btnDefault = new Button(sButtonDefault);
    _btnDefault.setDefaultButton(true);
    _btnDefault.setOnAction(this);
    double dButtonWidth = _btnDefault.getLayoutBounds().getWidth();
    if (sButtonCancel != null)
    {
      _btnCancel = new Button(sButtonCancel);
      _btnCancel.setCancelButton(true); // associate it with ESC key
      _btnCancel.setOnAction(this);
      dButtonWidth += dHSPACING + _btnCancel.getLayoutBounds().getWidth();
    }
    if (dMinWidth < dButtonWidth)
      dMinWidth = dButtonWidth;
    /* HBox for buttons */
    HBox hbox = new HBox();
    hbox.setPadding(new Insets(dINNER_PADDING));
    hbox.setSpacing(dHSPACING); // between Cancel and OK
    hbox.setAlignment(Pos.TOP_RIGHT);
    hbox.getChildren().add(_btnDefault);
    HBox.setMargin(_btnDefault, new Insets(dOUTER_PADDING));
    if (_btnCancel != null)
    {
      hbox.getChildren().add(_btnCancel);
      HBox.setMargin(_btnCancel, new Insets(dOUTER_PADDING));
    }
    
    /* width of title and text */
    Text txtPrompt = new Text(sPrompt);
    if (txtPrompt.getLayoutBounds().getWidth() > FxSizes.getScreenBounds().getWidth()/2.0)
      txtPrompt.setWrappingWidth(FxSizes.getScreenBounds().getWidth()/2.0);
    else
      txtPrompt.setWrappingWidth(txtPrompt.getLayoutBounds().getWidth());
      
    if (dMinWidth < txtPrompt.getWrappingWidth())
      dMinWidth = txtPrompt.getWrappingWidth();

    dMinWidth += 2*dOUTER_PADDING;
    Rectangle2D rectScreen = Screen.getPrimary().getVisualBounds();
    if (dMinWidth >= rectScreen.getWidth())
      dMinWidth = rectScreen.getWidth()-2*dSCREEN_PADDING;

    /* VBox for prompt and buttons */
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(dOUTER_PADDING));
    vbox.setSpacing(dVSPACING);
    vbox.setMinWidth(dMinWidth);
    vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    vbox.getChildren().add(txtPrompt);
    VBox.setMargin(txtPrompt, new Insets(dOUTER_PADDING,dOUTER_PADDING,dOUTER_PADDING,0));
    vbox.getChildren().add(hbox);
    
    /* scene */
    Scene scene = new Scene(vbox);
    setScene(scene);
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** show the modal message box and return the result.
   * @param stageOwner owner window.
   * @param sTitle title of the message box.
   * @param sPrompt prompt text.
   * @param sButtonDefault displayed on first (default) button.
   * @param sButtonCancel displayed on second (cancel) button or null, 
   *                      if only one button is to be displayed.
   * @return 1, if the default button was pressed, 0 otherwise.
   */
  public static int show(Stage stageOwner, 
    String sTitle, String sPrompt, String sButtonDefault, String sButtonCancel)
  {
    MB mb = new MB(stageOwner, sTitle, sPrompt, sButtonDefault, sButtonCancel);
    Cursor cursor = mb.getScene().getCursor();
    mb.getScene().setCursor(Cursor.DEFAULT);
    mb.showAndWait(); // until it is closed 
    mb.getScene().setCursor(cursor);
    return mb.getResult();
  } /* show */
  
} /* class MB */
