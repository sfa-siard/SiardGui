/*======================================================================
InfoDialog displays meta data of the program. 
Application : Siard2
Description : InfoDialog displays meta data of the program. 
Platform    : Java 7, JavaFX 2.2   
------------------------------------------------------------------------
Copyright  : 2017, Enter AG, Rüti ZH, Switzerland
Created    : 12.01.2017, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.gui.dialogs;

import java.text.*;
import java.util.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import ch.enterag.utils.fx.*;
import ch.admin.bar.siard2.gui.*;

/*====================================================================*/
/** InfoDialog displays meta data of the program. 
 * @author Hartwig Thomas
 */
public class InfoDialog
  extends ScrollableDialog 
  implements EventHandler<ActionEvent>
{
  /*------------------------------------------------------------------*/
  /** close dialog when the OK button is pressed.
   */
  @Override
  public void handle(ActionEvent ae)
  {
    close();
  } /* handle */

  /*------------------------------------------------------------------*/
  /** create the VBox containing the title information.
   * @return VBox with title information.
   */
  private VBox createVBoxTitle()
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    /* texts in title area */
    double dMinWidth = 0.0;
    Text txtTitle = new Text(SiardGui.getApplication()+" "+SiardGui.getVersion());
    double dTextWidth = FxSizes.getTextWidth(txtTitle.getText());
    if (dMinWidth < dTextWidth)
      dMinWidth = dTextWidth;
    SimpleDateFormat sdf = new SimpleDateFormat(sb.getDateFormat());
    Text txtCopyright = new Text("©"+sdf.format(SiardGui.getPublicationDate())+" "+SiardGui.getCopyright());
    dTextWidth = FxSizes.getTextWidth(txtCopyright.getText());
    if (dMinWidth < dTextWidth)
      dMinWidth = dTextWidth;
    Text txtSubject = new Text(sb.getInfoSubject());
    dTextWidth = FxSizes.getTextWidth(txtSubject.getText());
    if (dMinWidth < dTextWidth)
      dMinWidth = dTextWidth;
    TextArea taDescription = new TextArea();
    taDescription.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY+FxStyles.sSTYLE_SOLID_BORDER);
    taDescription.setEditable(false);
    taDescription.setWrapText(true);
    taDescription.setText(sb.getInfoDescription());
    taDescription.setPrefRowCount(6);
    /* VBox for title area */
    VBox vboxTitle = new VBox();
    vboxTitle.setPadding(new Insets(dINNER_PADDING));
    vboxTitle.setSpacing(dHSPACING);
    vboxTitle.setAlignment(Pos.TOP_CENTER);
    vboxTitle.getChildren().add(txtTitle);
    vboxTitle.getChildren().add(txtCopyright);
    vboxTitle.getChildren().add(txtSubject);
    vboxTitle.getChildren().add(taDescription);
    vboxTitle.setMinWidth(dMinWidth);
    return vboxTitle;
  } /* createVBoxTitle */

  /*------------------------------------------------------------------*/
  /** create a Label with the given text.
   * @param sLabel label text.
   * @param txt associated text control.
   * @return Label.
   */
  private Label createLabelCredits(String sLabel, Text txt)
  {
    Label lblCredits = new Label(sLabel+":");
    lblCredits.setPrefWidth(FxSizes.getTextWidth(lblCredits.getText()));
    lblCredits.setAlignment(Pos.BASELINE_RIGHT);
    lblCredits.setLabelFor(txt);
    return lblCredits;
  } /* createLabel */
  
  /*------------------------------------------------------------------*/
  /** create a Text containing the credits in the list.
   * @param listCredits list of credits.
   * @return Text with credits.
   */
  private Text createTextCredits(List<String> listCredits)
  {
    Text txtCredits = null;
    if ((listCredits != null) && (listCredits.size() > 0))
    {
      StringBuilder sbCredits = new StringBuilder();
      for (int iCredit = 0; iCredit < listCredits.size(); iCredit++)
      {
        String sCredit = listCredits.get(iCredit);
        if (iCredit != 0)
          sbCredits.append(",\n");
        sbCredits.append(sCredit);
      }
      txtCredits = new Text(sbCredits.toString());
    }
    return txtCredits;
  } /* createTextCredits */

  /*------------------------------------------------------------------*/
  /** create a HBox containing the credits.
   * @param lblCredits label for credits.
   * @param txtCredits text of credits.
   * @return HBox with credits.
   */
  private HBox createHBoxCredits(Label lblCredits, Text txtCredits)
  {
    HBox hboxCredits = null;
    if (txtCredits != null)
    {
      hboxCredits = new HBox();
      hboxCredits.setPadding(new Insets(dINNER_PADDING));
      hboxCredits.setSpacing(dHSPACING);
      hboxCredits.setAlignment(Pos.TOP_LEFT);
      hboxCredits.getChildren().add(lblCredits);
      hboxCredits.getChildren().add(txtCredits);
      hboxCredits.setMinWidth(lblCredits.getPrefWidth() + FxSizes.getTextWidth(txtCredits.getText()));
    }
    return hboxCredits;
  } /* createHBoxCredits */
  
  /*------------------------------------------------------------------*/
  /** create the VBox containing the credits.
   * @return VBox with credits.
   */
  private VBox createVBoxCredits()
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    /* VBox for credits */
    VBox vboxCredits = new VBox();
    vboxCredits.setPadding(new Insets(dINNER_PADDING));
    vboxCredits.setSpacing(dHSPACING);
    vboxCredits.setAlignment(Pos.TOP_LEFT);
    double dLabelWidth = 0.0;
    Text txtCreators = createTextCredits(SiardGui.getCreators());
    Label lblCreators = null;
    if (txtCreators != null)
    {
      lblCreators = createLabelCredits(sb.getInfoCreatorsLabel(),txtCreators);
      if (dLabelWidth < lblCreators.getPrefWidth())
        dLabelWidth = lblCreators.getPrefWidth();
    }
    Text txtContributors = createTextCredits(SiardGui.getContributors());
    Label lblContributors = null;
    if (txtContributors != null)
    {
      lblContributors = createLabelCredits(sb.getInfoContributorsLabel(),txtContributors);
      if (dLabelWidth < lblContributors.getPrefWidth())
        dLabelWidth = lblContributors.getPrefWidth();
    }
    Text txtProvenances = createTextCredits(SiardGui.getProvenances());
    Label lblProvenances = null;
    if (txtProvenances != null)
    {
      lblProvenances = createLabelCredits(sb.getInfoProvenancesLabel(),txtProvenances);
      if (dLabelWidth < lblProvenances.getPrefWidth())
        dLabelWidth = lblProvenances.getPrefWidth();
    }
    if (lblCreators != null)
      lblCreators.setPrefWidth(dLabelWidth);
    if (lblContributors != null)
      lblContributors.setPrefWidth(dLabelWidth);
    if (lblProvenances != null)
      lblProvenances.setPrefWidth(dLabelWidth);
    
    double dMinWidth = 0.0;
    HBox hboxCreators = createHBoxCredits(lblCreators, txtCreators);
    if (hboxCreators != null)
    {
      vboxCredits.getChildren().add(hboxCreators);
      if (dMinWidth < hboxCreators.getMinWidth())
        dMinWidth = hboxCreators.getMinWidth();
    }
    HBox hboxContributors = createHBoxCredits(lblContributors, txtContributors);
    if (hboxContributors != null)
    {
      vboxCredits.getChildren().add(hboxContributors);
      if (dMinWidth < hboxContributors.getMinWidth())
        dMinWidth = hboxContributors.getMinWidth();
    }
    HBox hboxProvenances = createHBoxCredits(lblProvenances, txtProvenances);
    if (hboxProvenances != null)
    {
      vboxCredits.getChildren().add(hboxProvenances);
      if (dMinWidth < hboxProvenances.getMinWidth())
        dMinWidth = hboxProvenances.getMinWidth();
    }
    vboxCredits.setMinWidth(dMinWidth);
    return vboxCredits;
  } /* createVBoxCredits */

  /*------------------------------------------------------------------*/
  /** create the HBox with the OK button.
   * @return HBox with OK button.
   */
  private HBox createHBoxButton()
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    /* default button */
    Button btnDefault = new Button(sb.getOk());
    btnDefault.setDefaultButton(true);
    btnDefault.setOnAction(this);
    /* HBox for button */
    HBox hboxButton = new HBox();
    hboxButton.setPadding(new Insets(dINNER_PADDING));
    hboxButton.setSpacing(dHSPACING);
    hboxButton.setAlignment(Pos.TOP_RIGHT);
    hboxButton.getChildren().add(btnDefault);
    hboxButton.setMinWidth(FxSizes.getTextWidth(sb.getOk()));
    return hboxButton;
  } /* createHBoxButton */
  
  /*------------------------------------------------------------------*/
  /** create the main VBox of the dialog
   * @return main VBox
   */
  private VBox createVBoxDialog()
  {
    /* VBox for title area, separator, credits area, separator and OK button */
    VBox vboxDialog = new VBox();
    vboxDialog.setPadding(new Insets(dOUTER_PADDING));
    vboxDialog.setSpacing(dVSPACING);
    vboxDialog.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    double dMinWidth = 0;
    VBox vboxTitle = createVBoxTitle();
    if (dMinWidth < vboxTitle.getMinWidth())
      dMinWidth = vboxTitle.getMinWidth();
    vboxDialog.getChildren().add(vboxTitle);
    vboxDialog.getChildren().add(new Separator());
    VBox vboxCredits = createVBoxCredits();
    if (dMinWidth < vboxCredits.getMinWidth())
      dMinWidth = vboxCredits.getMinWidth();
    vboxDialog.getChildren().add(vboxCredits);
    vboxDialog.getChildren().add(new Separator());
    HBox hboxButton = createHBoxButton();
    if (dMinWidth < hboxButton.getMinWidth())
      dMinWidth = hboxButton.getMinWidth();
    vboxDialog.getChildren().add(hboxButton);
    vboxDialog.setMinWidth(dMinWidth);
    return vboxDialog;
  } /* createVBoxDialog */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param stageOwner owner window.
   */
  private InfoDialog(Stage stageOwner)
  {
    super(stageOwner,SiardBundle.getSiardBundle().getInfoTitle());
    double dMinWidth = FxSizes.getTextWidth(SiardBundle.getSiardBundle().getInfoTitle())+FxSizes.getCloseWidth()+dHSPACING;
    VBox vboxDialog = createVBoxDialog();
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
  } /* constructor InfoDialog */
  
  /*------------------------------------------------------------------*/
  /** show the modal info dialog.
   * @param stageOwner owner window.
   */
  public static void showInfoDialog(Stage stageOwner)
  {
    InfoDialog id = new InfoDialog(stageOwner); 
    id.showAndWait(); // until it is closed 
  } /* showInfoDialog */
  
} /* class InfoDialog */
