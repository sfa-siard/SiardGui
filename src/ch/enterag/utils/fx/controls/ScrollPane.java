package ch.enterag.utils.fx.controls;

import javafx.geometry.*;
import javafx.scene.*;
import ch.enterag.utils.fx.*;

public class ScrollPane
  extends javafx.scene.control.ScrollPane
{
  /*------------------------------------------------------------------*/
  /** scroll horizontally until the given X coordinate becomes visible.
   * @param dX x coordinate in content.
   */
  public void makeVisibleX(double dX)
  {
    double dViewportWidth = getViewportBounds().getWidth();
    double dContentWidth = getContent().getLayoutBounds().getWidth();
    if (dContentWidth > dViewportWidth)
    {
      double dMaxLeft = dContentWidth - dViewportWidth;
      /* value between 0.0 and 1.0 */
      double dMu = (getHvalue()-getHmin())/(getHmax()-getHmin());
      /* current left position of viewport */
      double dLeft = dMu*dMaxLeft;
      if (dX < dLeft)
      {
        dMu = dX/dMaxLeft;
        setHvalue((1.0-dMu)*getHmin()+dMu*getHmax());
      }
      else if (dX-dViewportWidth > dLeft)
      {
        dMu = (dX-dViewportWidth)/dMaxLeft;
        setHvalue((1.0-dMu)*getHmin()+dMu*getHmax());
      }
    }
  } /* makeVisibleX */
  
  /*------------------------------------------------------------------*/
  /** scroll vertically until the given Y coordinate becomes visible.
   * @param dY y coordinate in content.
   */
  public void makeVisibleY(double dY)
  {
    double dViewportHeight = getViewportBounds().getHeight();
    double dContentHeight = getContent().getLayoutBounds().getHeight();
    if (dContentHeight > dViewportHeight)
    {
      double dMaxTop = dContentHeight - dViewportHeight;
      /* value between 0.0 and 1.0 */
      double dMu = (getVvalue()-getVmin())/(getVmax()-getVmin());
      /* current left position of viewport */
      double dTop = dMu*dMaxTop;
      if (dY < dTop)
      {
        dMu = dY/dMaxTop;
        setVvalue((1.0-dMu)*getVmin()+dMu*getVmax());
      }
      else if (dY-dViewportHeight > dTop)
      {
        dMu = (dY-dViewportHeight)/dMaxTop;
        setVvalue((1.0-dMu)*getVmin()+dMu*getVmax());
      }
    }
  } /* makeVisibleY */
  
  /*------------------------------------------------------------------*/
  /** scroll until the given node becomes visible if needed as well as
   * possible.
   * @param node node.
   */
  public void makeVisible(Node node)
  {
    double dViewportWidth = getViewportBounds().getWidth();
    Bounds b = FxSizes.localToAncestor(node, getContent(), node.getLayoutBounds());
    if ((b.getMaxX() - b.getMinX()) < dViewportWidth)
    {
      // scroll both edges to visibility
      makeVisibleX(b.getMinX());
      makeVisibleX(b.getMaxX());
    }
    else // node wider than viewport
    {
      // scroll center to visibility
      makeVisibleX(0.5*(b.getMinX()+b.getMaxX()));
    }
    double dViewportHeight = getViewportBounds().getHeight();
    if ((b.getMaxY() - b.getMinY()) < dViewportHeight)
    {
      // scroll both edges to visibility
      makeVisibleY(b.getMinY());
      makeVisibleY(b.getMaxY());
    }
    else
    {
      // scroll center to visibility
      makeVisibleY(0.5*(b.getMinY()+b.getMaxY()));
    }
  } /* makeVisible */
  
  /*------------------------------------------------------------------*/
  /** scroll horizontally to put the given x coordinate to the center
   * of the viewport as close as possible.
   * @param dX x coordinate in content.
   */
  public void scrollToCenterX(double dX)
  {
    double dViewportWidth = getViewportBounds().getWidth();
    double dContentWidth = getContent().getLayoutBounds().getWidth();
    if (dContentWidth > dViewportWidth)
    {
      double dMaxLeft = dContentWidth - dViewportWidth;
      double dLeft = dX - 0.5*dViewportWidth;
      double dMu = Math.min(Math.max(dLeft/dMaxLeft,0.0),1.0);
      setHvalue((1.0-dMu)*getHmin()+dMu*getHmax());
    }
  } /* scrollToCenterX */
  
  /*------------------------------------------------------------------*/
  /** scroll vertically to put the given y coordinate to the center of 
   * the viewport as close as possible.
   * @param dY y coordinate in content.
   */
  public void scrollToCenterY(double dY)
  {
    double dViewportHeight = getViewportBounds().getHeight();
    double dContentHeight = getContent().getLayoutBounds().getHeight();
    if (dContentHeight > dViewportHeight)
    {
      double dMaxTop = dContentHeight - dViewportHeight;
      double dTop = dY - 0.5*dViewportHeight;
      double dMu = Math.min(Math.max(dTop/dMaxTop,0.0),1.0);
      setVvalue((1.0-dMu)*getVmin()+dMu*getVmax());
    }
  } /* scrollToCenterY */
  
  /*------------------------------------------------------------------*/
  /** scroll until the (center of) the given node is in the center of 
   * the viewport as close as possible.
   * @param node node to be centered.
   */
  public void scrollToCenter(Node node)
  {
    Bounds b = FxSizes.localToAncestor(node, getContent(), node.getLayoutBounds());
    scrollToCenterX(0.5*(b.getMinX()+b.getMaxX()));
    scrollToCenterY(0.5*(b.getMinY()+b.getMaxY()));
  } /* scrollToCenter */
  
} /* ScrollPane */
