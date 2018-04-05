/*======================================================================
FxSizes implements all kinds of size computations.
Application : JavaFX Utilities
Description : FxSizes implements all kinds of size computations, conversions 
              from absolute units (mm) and from font-related units (em, ex) 
              to pixels.  
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland
Created    : 21.12.2015, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.fx;

import javafx.scene.*;
import javafx.geometry.*;
import javafx.scene.text.*;
import javafx.stage.*;

/*====================================================================*/
/** FxSizes implements all kinds of size computations, conversions 
 * from absolute units (mm) and from font-related units (em, ex) to pixels.
 * @author Hartwig Thomas
 */
public abstract class FxSizes
{
  private static final double dMM_PER_INCH = 25.4;
  private static final double dDOTS_PER_INCH = Screen.getPrimary().getDpi();
  private static final Rectangle2D r2SCREEN = Screen.getPrimary().getVisualBounds();
  public static Rectangle2D getScreenBounds() { return r2SCREEN; }
  // width of the close button
  private static final double dCLOSE_WIDTH = 10.0;
  public static double getCloseWidth() { return dCLOSE_WIDTH; }

  /*------------------------------------------------------------------*/
  /** number of pixels for the width of a node.
   * @param node (control, region) to be measured.
   * @return width of node.
   */
  public static double getNodeWidth(Node node)
  {
    Group group = null;
    if (node.getScene() == null)
    {
      group = new Group(node);
      new Scene(group);
    }
    node.snapshot(null, null);
    double dWidth = Math.ceil(node.getLayoutBounds().getWidth());
    if (group != null)
      group.getChildren().remove(node);
    return dWidth;
  } /* getNodeWidth */
  
  /*------------------------------------------------------------------*/
  /** number of pixels for the height of a node.
   * @param node (control, region) to be measured.
   * @return height of node.
   */
  public static double getNodeHeight(Node node)
  {
    Group group = null;
    if (node.getScene() == null)
    {
      group = new Group(node);
      new Scene(group);
    }
    node.snapshot(null,null);
    double dHeight = Math.ceil(node.getLayoutBounds().getHeight());
    if (group != null)
      group.getChildren().remove(node);
    return dHeight;
  } /* getNodeHeight */
  
  /*------------------------------------------------------------------*/
  /** number of pixels for width of given text in given font.
   * @param sText text to be measured.
   * @param font font to be used for measurement.
   * @param dWrappingWidth width for text wrapping.
   * @return pixels for width of given text.
   */
  public static double getTextWidth(String sText, Font font, double dWrappingWidth)
  {
    Text text = new Text(sText);
    text.setFont(font);
    text.setWrappingWidth(dWrappingWidth);
    return Math.ceil(text.getLayoutBounds().getWidth());
  } /* getTextWidth */
  
  /*------------------------------------------------------------------*/
  /** number of pixels for width of given text in given font.
   * @param sText text to be measured.
   * @param font font to be used for measurement.
   * @return pixels for width of given text.
   */
  public static double getTextWidth(String sText, Font font)
  {
    Text text = new Text(sText);
    text.setFont(font);
    return Math.ceil(text.getLayoutBounds().getWidth());
  } /* getTextWidth */
  
  /*------------------------------------------------------------------*/
  /** number of pixels for width of given text in default font.
   * @param sText text to be measured.
   * @param dWrappingWidth width for text wrapping.
   * @return pixels for width of given text.
   */
  public static double getTextWidth(String sText, double dWrappingWidth)
  {
    return getTextWidth(sText,Font.getDefault(),dWrappingWidth);
  } /* getTextWidth */
  
  /*------------------------------------------------------------------*/
  /** number of pixels for width of given text in default font.
   * @param sText text to be measured.
   * @return pixels for width of given text.
   */
  public static double getTextWidth(String sText)
  {
    return getTextWidth(sText,Font.getDefault());
  } /* getTextWidth */
  
  /*------------------------------------------------------------------*/
  /** number of pixels for height of given text in given font.
   * @param sText text to be measured.
   * @param font font to be used for measurement.
   * @param dWrappingWidth width for text wrapping.
   * @return pixels for height of given text.
   */
  public static double getTextHeight(String sText, Font font, double dWrappingWidth)
  {
    Text text = new Text(sText);
    text.setFont(font);
    text.setWrappingWidth(dWrappingWidth);
    return Math.ceil(text.getLayoutBounds().getHeight());
  } /* getTextHeight */
  
  /*------------------------------------------------------------------*/
  /** number of pixels for height of given text in given font.
   * @param sText text to be measured.
   * @param font font to be used for measurement.
   * @return pixels for height of given text.
   */
  public static double getTextHeight(String sText, Font font)
  {
    Text text = new Text(sText);
    text.setFont(font);
    return Math.ceil(text.getLayoutBounds().getHeight());
  } /* getTextHeight */
  
  /*------------------------------------------------------------------*/
  /** number of pixels for height of given text in default font.
   * @param sText text to be measured.
   * @param dWrappingWidth width for text wrapping.
   * @return pixels for height of given text.
   */
  public static double getTextHeight(String sText, double dWrappingWidth)
  {
    return getTextHeight(sText,Font.getDefault(),dWrappingWidth);
  } /* getTextHeight */
  
  /*------------------------------------------------------------------*/
  /** number of pixels for height of given text in default font.
   * @param sText text to be measured.
   * @return pixels for height of given text.
   */
  public static double getTextHeight(String sText)
  {
    return getTextHeight(sText,Font.getDefault());
  } /* getTextHeight */
  
  /*------------------------------------------------------------------*/
  /** number of pixels for width of "m" in given font.
   * @param font font to be used for measurement.
   * @return pixels for width of "m".
   */
  public static double getEm(Font font)
  {
    return getTextWidth("m",font);
  } /* getEm */
  
  /*------------------------------------------------------------------*/
  /** number of pixels for width of "m" in default font.
   * @return pixels for width of "m".
   */
  public static double getEm()
  {
    return getEm(Font.getDefault());
  } /* getEm */
  
  /*------------------------------------------------------------------*/
  /** number of pixels for height of "x" in given font.
   * @param font font to be used for measurement.
   * @return pixels for height of "x".
   */
  public static double getEx(Font font)
  {
    return getTextHeight("x",font);
  } /* getEx */
  
  /*------------------------------------------------------------------*/
  /** number of pixels for height of "x" in default font.
   * @return pixels for height of "x".
   */
  public static double getEx()
  {
    return getEx(Font.getDefault());
  } /* getEx */
  
  /*------------------------------------------------------------------*/
  /** convert mm to pixels
   * @param dMm number of milli meters.
   * @return pixels.
   */
  public static double fromMm(double dMm)
  {
    return dMm*dDOTS_PER_INCH/dMM_PER_INCH;
  } /* fromMm */

  /*------------------------------------------------------------------*/
  /** convert exes to pixels
   * @param dExes number of exes ("x" heights) of given font.
   * @param font font to be used for measurement.
   * @return pixels.
   */
  public static double fromExes(double dExes, Font font)
  {
    return dExes*getEx(font);
  } /* fromExes */
  
  /*------------------------------------------------------------------*/
  /** convert exes to pixels
   * @param dExes number of exes ("x" heights) of default font.
   * @return pixels.
   */
  public static double fromExes(double dExes)
  {
    return dExes*getEx();
  } /* fromExes */

  /*------------------------------------------------------------------*/
  /** convert ems to pixels
   * @param dEms number of ems ("m" widths) of given font.
   * @param font font to be used for measurement.
   * @return pixels.
   */
  public static double fromEms(double dEms, Font font)
  {
    return dEms*getEm(font);
  } /* fromEms */
  
  /*------------------------------------------------------------------*/
  /** convert ems to pixels
   * @param dEms number of ems ("m" widths) of default font.
   * @return pixels.
   */
  public static double fromEms(double dEms)
  {
    return dEms*getEm();
  } /* fromEms */

  /*----------------------------------------------------------------.-*/
  /** compute a point in node coordinates relative to an ancestor.
   * @param node node
   * @param ancestor ancestor
   * @param x local offset in node.
   * @param y local offset in node.
   * @return point relative to ancestor.
   */
  public static Point2D localToAncestor(Node node, Node ancestor, double x, double y)
  {
    // For simplicity I suppose that the n node is on the path of the parent
    Point2D p = new Point2D(x, y);
    for (Node cn = node; cn != ancestor;cn = cn.getParent())
      p = cn.localToParent(p);
    return p;
  } /* localtoAncestor */

  /*------------------------------------------------------------------*/
  /** compute bounds in node coordinates relative to ancestor. 
   * @param node node
   * @param ancestor ancestor
   * @param bounds bound in node
   * @return bounds relative to ancestor.
   */
  public static Bounds localToAncestor(Node node, Node ancestor, Bounds bounds)
  {
    Point2D p = localToAncestor(node, ancestor, bounds.getMinX(), bounds.getMinY());
    return new BoundingBox(p.getX(), p.getY(), bounds.getWidth(), bounds.getHeight());
  } /* localToAncestor */
  
} /* class FxSizes */
