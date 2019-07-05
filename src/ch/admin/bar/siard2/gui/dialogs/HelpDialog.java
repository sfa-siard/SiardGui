/*======================================================================
HelpDialog displays HTML containing help information. 
Application : Siard2
Description : HelpDialog displays HTML containing help information.
              See http://docs.oracle.com/javafx/2/webview/jfxpub-webview.htm 
Platform    : Java 7, JavaFX 2.2   
------------------------------------------------------------------------
Copyright  : 2017, Enter AG, Rüti ZH, Switzerland
Created    : 12.01.2017, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.gui.dialogs;

import java.io.*;
import java.net.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.stage.*;

import ch.admin.bar.siard2.gui.*;
import ch.admin.bar.siard2.gui.browser.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.fx.dialogs.*;
import ch.enterag.utils.io.*;
import ch.enterag.utils.logging.*;

/*====================================================================*/
/** HelpDialog displays HTML containing help information. 
 * @author Hartwig Thomas
 */
public class HelpDialog
  extends ScrollableDialog 
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(HelpDialog.class.getName());

  /*------------------------------------------------------------------*/
  /** constructor for web browser dialog
   * @param stageOwner owner window.
   * @param sUrl URL to be displayed in dialog.
   */
  private HelpDialog(Stage stageOwner, String sUrl)
  {
    super(stageOwner,SiardBundle.getSiardBundle().getHelpTitle());
    /* browser region */
    BrowserRegion br = BrowserRegion.newBrowserRegion(sUrl);
    br.setPrefSize(0.7*FxSizes.getScreenBounds().getWidth(),
                   0.7*FxSizes.getScreenBounds().getHeight());
    br.setPadding(new Insets(dOUTER_PADDING));
    br.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    /* scene */
    Scene scene = new Scene(br);
    setScene(scene);
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** show the modal help dialog.
   * @param stageOwner owner window.
   * @param sPath path to HTML to be displayed relative to manual root.
   */
  public static void showHelpDialog(Stage stageOwner, String sPath)
  {
    /* find the manual in either "doc/manual/" (Eclipse) or "../doc/manual/" (JAR) */
    File fileManual = SpecialFolder.getJarFromClass(HelpDialog.class, false);
    if (fileManual.isFile())
      fileManual = fileManual.getParentFile();
    String sManual = fileManual.getParentFile().getAbsolutePath()+
        File.separator+"doc"+File.separator+"manual" +  
        File.separator + SiardBundle.getSiardBundle().getLanguage();  
    fileManual = new File(sManual+File.separator+sPath);
    if (!fileManual.exists())
      fileManual = new File(sManual+File.separator+sPath);
    if (fileManual.exists())
    {
      try 
      { 
        HelpDialog hd = new HelpDialog(stageOwner,fileManual.toURI().toURL().toString()); 
        hd.showAndWait(); // until it is closed 
      }
      catch(MalformedURLException mfue) { _il.exception(mfue); }
    }
    else
    {
      SiardBundle sb = SiardBundle.getSiardBundle();
      MB.show(SiardGui.getSiardGui().getStage(),
        sb.getHelpErrorTitle(), 
        sb.getHelpErrorMessage(fileManual), 
        sb.getOk(), null);
    }
  } /* showHelpDialog */
  
} /* HelpDialog */
