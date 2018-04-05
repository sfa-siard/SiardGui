/*======================================================================
MetaDataDialog applies given XSL to metadata and displays it in a browser.
Application : Siard2
Description : MetaDataDialog applies given XSL to metadata and displays 
              it in a browser.
Platform    : Java 7, JavaFX 2.2   
------------------------------------------------------------------------
Copyright  : 2017, Enter AG, Rüti ZH, Switzerland
Created    : 05.07.2017, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.gui.dialogs;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.fx.dialogs.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.gui.*;
import ch.admin.bar.siard2.gui.actions.MetaDataAction;
import ch.admin.bar.siard2.gui.browser.*;

/*====================================================================*/
/** MetaDataDialog applies given XSL to metadata and displays it in a browser.
 * @author Hartwig Thomas
 */
public class MetaDataDialog
  extends Stage 
  implements EventHandler<ActionEvent>
{
  /* counteract Oracle idiocy which in xmlparserv2 overrides standard XML
   * processing with buggy Oracle XML processing. */
  static
  {
    System.setProperty("javax.xml.parsers.DocumentBuilderFactory","com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
    System.setProperty("javax.xml.parsers.SAXParserFactory","com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
    System.setProperty("javax.xml.transform.TransformerFactory","com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
    System.setProperty("org.w3c.dom.DOMImplementationSourceList","com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");
  }
  // padding inside the dialog's browser region
  private static final double dOUTER_PADDING = 10.0;
  // padding inside
  private static final double dINNER_PADDING = 0.0;
  // vertical spacing of elements
  private static final double dVSPACING = 10.0;
  // horizontal spacing of elements
  private static final double dHSPACING = 10.0;
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(MetaDataDialog.class.getName());
  /** archive */
  private Archive _archive = null;
  /** XML */
  private String _sMetaDataXml = null;
  /** HTML */
  private String _sMetaDataHtml = null;
  /** save XML button */
  private Button _btnSaveXml = null;
  /** save HTML button */
  private Button _btnSaveHtml = null;
  /** OK button */
  private Button _btnDefault = null;

  /*------------------------------------------------------------------*/
  /** handles button events.
   * @param ae action event.
   */
  @Override
  public void handle(ActionEvent ae)
  {
    if (ae.getSource() == _btnSaveXml)
      MetaDataAction.newMetaDataAction().saveMetaDataXml(this, _archive, _sMetaDataXml);
    else if (ae.getSource() == _btnSaveHtml)
      MetaDataAction.newMetaDataAction().saveMetaDataHtml(this, _archive,_sMetaDataHtml);
    else
      close();
  } /* handle */
  
  /*------------------------------------------------------------------*/
  /** create the HBox with the OK button.
   * @return HBox with OK button.
   */
  private HBox createHBoxButtons()
  {
    HBox hbox = new HBox();
    hbox.setPadding(new Insets(dINNER_PADDING));
    hbox.setSpacing(dHSPACING);
    hbox.setAlignment(Pos.TOP_RIGHT);
    SiardBundle sb = SiardBundle.getSiardBundle();
    /* OK button */
    _btnDefault = new Button(sb.getOk());
    HBox.setMargin(_btnDefault, new Insets(dOUTER_PADDING));
    _btnDefault.setDefaultButton(true);
    _btnDefault.setOnAction(this);
    hbox.getChildren().add(_btnDefault);
    double dMinWidth = _btnDefault.getLayoutBounds().getWidth();
    /* save XML button */
    _btnSaveXml = new Button(sb.getMetaDataSaveXmlText());
    HBox.setMargin(_btnSaveXml, new Insets(dOUTER_PADDING));
    _btnSaveXml.setOnAction(this);
    hbox.getChildren().add(_btnSaveXml);
    dMinWidth += dHSPACING + _btnSaveXml.getLayoutBounds().getWidth();
    /* save HTML button */
    _btnSaveHtml = new Button(sb.getMetaDataSaveHtmlText());
    HBox.setMargin(_btnSaveHtml, new Insets(dOUTER_PADDING));
    _btnSaveHtml.setOnAction(this);
    hbox.getChildren().add(_btnSaveHtml);
    dMinWidth += dHSPACING + _btnSaveHtml.getLayoutBounds().getWidth();
    /* HBox for buttons */
    hbox.setMinWidth(dMinWidth);
    return hbox;
  } /* createHBoxButtons */

  /*------------------------------------------------------------------*/
  /** create a browser region and display meta data HTML in it.
   * @return browser region.
   */
  private BrowserRegion createBrowserRegion()
  {
    StringReader rdrHtml = new StringReader(_sMetaDataHtml);
    BrowserRegion br = BrowserRegion.newBrowserRegion(rdrHtml);
    br.setPrefSize(0.7*FxSizes.getScreenBounds().getWidth(),
                   0.7*FxSizes.getScreenBounds().getHeight());
    br.setPadding(new Insets(dOUTER_PADDING));
    br.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    return br;
  } /* createBrowserRegion */
  
  /*------------------------------------------------------------------*/
  /** create the VBox for the dialog containing a browser region
   * for the meta data HTML and the buttons.
   * @return
   */
  private VBox createVBoxDialog()
  {
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(dOUTER_PADDING));
    vbox.setSpacing(dVSPACING);
    vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    double dMinWidth = 0.0;
    /* browser region with HTML */
    BrowserRegion br = createBrowserRegion();
    if (dMinWidth < br.getPrefWidth())
      dMinWidth = br.getPrefWidth();
    vbox.getChildren().add(br);
    /* HBox with save XML, save HTML and OK button */
    HBox hbox = createHBoxButtons();
    if (dMinWidth < hbox.getMinWidth())
      dMinWidth = hbox.getMinWidth();
    /* three buttons: save XML, save HTML, Cancel */
    vbox.getChildren().add(hbox);
    vbox.setMinWidth(dMinWidth);
    return vbox;
  } /* createVBoxDialog */
  
  /*------------------------------------------------------------------*/
  /** constructor transforms metadata to HTML using XSL and displays
   * it in a browser window.
   * @param stageOwner owner window.
   * @param sMetaDataXml meta data XML.
   * @param sMetaDataHtml meta data transformed to HTML.
   */
  private MetaDataDialog(Stage stageOwner, Archive archive, String sMetaDataXml, String sMetaDataHtml)
  {
    super();
    _sMetaDataXml = sMetaDataXml;
    _sMetaDataHtml = sMetaDataHtml;
    _archive = archive;
    SiardBundle sb = SiardBundle.getSiardBundle();
    VBox vbox = createVBoxDialog();
    /* scene */
    Scene scene = new Scene(vbox);
    setScene(scene);
    /* title */
    setTitle(sb.getMetaDataDisplayTitle());
    /* style */
    initStyle(StageStyle.UTILITY);
    /* owner */
    initOwner(stageOwner);
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** transform metadata XML to metadata HTML given an XSL
   * @param fileXsl XSL file.
   */
  private static String transformMetaData(File fileXsl, String sMetaDataXml)
    throws TransformerConfigurationException, TransformerException
  {
    _il.enter(fileXsl);
    /* create transformer for XSL */
    StreamSource ssXsl = new StreamSource(fileXsl);
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer xformer = tf.newTransformer(ssXsl);
    /* create input stream for XML */
    StringReader sr = new StringReader(sMetaDataXml);
    StreamSource ssXml = new StreamSource(sr);
    /* create output stream for HTML */
    StringWriter sw = new StringWriter();
    StreamResult srHtml = new StreamResult(sw);
    /* transform */
    xformer.transform(ssXml, srHtml);
    _il.exit();
    /* return result */
    return sw.toString();
  } /* transformMetaData */

  /*------------------------------------------------------------------*/
  /** factory transforms meta data XML to HTML and displays browser window
   * showing it.
   * @param stageOwner owner window.
   * @param fileXsl XSL file to be used for transformation.
   */
  public static void showMetaDataDialog(Stage stageOwner, Archive archive, File fileXsl)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    try
    {
      /* export metadata.xml from sample.siard */
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      archive.exportMetaData(baos);
      baos.close();
      String sMetaDataXml = new String(baos.toByteArray());
      /* transform meta data to HTML */
      String sMetaDataHtml = transformMetaData(fileXsl,sMetaDataXml);
      MetaDataDialog mdd = new MetaDataDialog(stageOwner, archive, sMetaDataXml, sMetaDataHtml); 
      mdd.showAndWait(); // until it is closed 
    }
    catch(TransformerConfigurationException tce)
    {
      MB.show(stageOwner,
        sb.getMetaDataErrorTitle(),
        sb.getMetaDataErrorTransformerConfigurationMessage(tce),
        sb.getOk(), null);
    }
    catch(IOException ie)
    {
      MB.show(stageOwner,
        sb.getMetaDataErrorTitle(),
        sb.getMetaDataErrorIoMessage(ie),
        sb.getOk(), null);
    }
    catch(TransformerException te)
    {
      MB.show(stageOwner,
        sb.getMetaDataErrorTitle(),
        sb.getMetaDataErrorTransformerMessage(te),
        sb.getOk(), null);
    }
  } /* factory */

} /* MetaDataDialog */
