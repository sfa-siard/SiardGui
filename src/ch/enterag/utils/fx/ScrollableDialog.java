/*======================================================================
Base class for simple dialogs. 
Application : JavaFX Utilities.
Description : Dialog is a base class for simple dialogs. 
Platform    : Java 8, JavaFX 8   
------------------------------------------------------------------------
Copyright  : 2015, Enter AG, Rüti ZH, Switzerland
Created    : 22.12.2015, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.fx;

import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

public class ScrollableDialog
  extends Dialog
{

  protected ScrollableDialog(Stage stageOwner, String sTitle)
  {
    super(stageOwner, sTitle);
    /* after scene has been finalized, put everything on a scrollpane */
    setOnShowing(new EventHandler<WindowEvent>() 
    {
      @Override
      public void handle(WindowEvent event)
      {
        // create a ScrollPane as the Root of the Scene
        ScrollPane sp = null;
        Node nodeMain = ScrollableDialog.this.getScene().getRoot();
        if (nodeMain instanceof ScrollPane)
        {
          sp = (ScrollPane)nodeMain;
          nodeMain = sp.getContent();
        }
        else
        {
          sp = new ScrollPane(nodeMain);
          ScrollableDialog.this.getScene().setRoot(sp);
          sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
          sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
          sp.setFitToHeight(true);
          sp.setFitToWidth(true);
        }
        double dContentWidth = FxSizes.getNodeWidth(nodeMain);
        double dContentHeight = FxSizes.getNodeHeight(nodeMain);
        /* set scroll pane dimensions */
        sp.setPrefViewportWidth(dContentWidth);
        sp.setPrefViewportHeight(dContentHeight);
      }
    });
    /* when stage extent becomes available, modify it */
    setOnShown(new EventHandler<WindowEvent>() 
    {
      @Override
      public void handle(WindowEvent event)
      {
        Node nodeMain = ScrollableDialog.this.getScene().getRoot();
        ScrollPane sp = (ScrollPane)nodeMain;
        nodeMain = sp.getContent();
        double dScreenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        double dScreenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double dStageWidth = ScrollableDialog.this.getWidth();
        double dStageHeight = ScrollableDialog.this.getHeight();
        /* set stage and scroll pane dimensions */
        if (dScreenWidth < dStageWidth)
        {
          ScrollableDialog.this.setWidth(dScreenWidth);
          ScrollableDialog.this.setX(0);
        }
        if (dScreenHeight < dStageHeight)
        {
          ScrollableDialog.this.setHeight(dScreenHeight);
          ScrollableDialog.this.setY(0);
        }
      }
    });
  } /* constructor */

}
