package ch.admin.bar.siard2.gui.browser;

import java.io.*;
import javafx.geometry.*;
import javafx.scene.layout.*;
import javafx.scene.web.*;

import ch.enterag.utils.EU;
import ch.enterag.utils.fx.*;

public class BrowserRegion
  extends Region
{
  private static final int iBUFSIZ = 8192;
  private WebView _wv = new WebView();
  
  private BrowserRegion(String sUrl)
  {
    super();
    _wv.getEngine().load(sUrl);
    getChildren().add(_wv);
  } /* constructor BrowserRegion */
  
  private BrowserRegion(Reader rdrHtml)
  {
    super();
    char[] cbuf = new char[iBUFSIZ];
    try
    {
      StringWriter sw = new StringWriter();
      for (int iRead = rdrHtml.read(cbuf); iRead != -1; iRead = rdrHtml.read(cbuf))
        sw.write(cbuf,0,iRead);
      rdrHtml.close();
      sw.close();
      _wv.getEngine().loadContent(sw.getBuffer().toString());
      getChildren().add(_wv);
    }
    catch(IOException ie) { System.err.println(EU.getExceptionMessage(ie));  }
  } /* constructor BrowserRegion */

  @Override 
  protected void layoutChildren() 
  {
    double w = getWidth();
    double h = getHeight();
    this.layoutInArea(_wv,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
  } /* layoutChildren */

  @Override 
  protected double computePrefWidth(double dHeight) 
  {
    return FxSizes.getScreenBounds().getWidth()/2.0;
  } /* computePrefWidth */

  @Override 
  protected double computePrefHeight(double dWidth) 
  {
    return FxSizes.getScreenBounds().getHeight()/2.0;
  } /* computePrefHeight */
  
  public static BrowserRegion newBrowserRegion(String sUrl)
  {
    return new BrowserRegion(sUrl);
  } /* factory newBrowserRegion */
  
  public static BrowserRegion newBrowserRegion(Reader rdrHtml)
  {
    return new BrowserRegion(rdrHtml);
  }
  
} /* BrowserRegion */
