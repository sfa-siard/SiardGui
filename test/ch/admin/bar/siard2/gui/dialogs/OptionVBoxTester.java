package ch.admin.bar.siard2.gui.dialogs;

import java.io.*;
import ch.enterag.utils.fx.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;

@SuppressWarnings("unused")
public class OptionVBoxTester
extends Application
{
  /** return codes */
  private static final int iRETURN_OK = 0;
  private static final int iRETURN_ERROR = 8;
  
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    int iReturn = iRETURN_ERROR;
    try
    {
      launch(args);
      iReturn = iRETURN_OK;
    }
    catch (Exception e) { System.err.println(e.getClass().getName()+": "+e.getMessage()); }
    System.exit(iReturn);
  }

  @Override
  public void start(Stage stage) throws Exception
  {
    stage.initStyle(StageStyle.UTILITY);
    VBox vbox = new VBox();
    vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_YELLOW);
    vbox.setPadding(new Insets(0.0));
    /***
    OptionVBox<String> ovb = new OptionVBox<String>("Long, long, long, long, long, long, long, long, long, long, long, long Explanation\nmultiline",
      "Label", 200,String.class,"Initial Text");
    OptionVBox<File> ovb = new OptionVBox<File>("Long, long, long, long, long, long, long, long, long, long, long, long Explanation\nmultiline",
        "Label", 200,File.class,new File("testfiles"));
    OptionVBox<Integer> ovb = new OptionVBox<Integer>(stage,"Long, long, long, long, long, long, long, long, long, long, long, long Explanation\nmultiline",
        "Label", 200,Integer.class,Integer.valueOf(5));
    ***/
    OptionVBox<Boolean> ovb = new OptionVBox<Boolean>(stage,"Long, long, long, long, long, long, long, long, long, long, long, long Explanation\nmultiline",
      "Label", 200,Boolean.class,Boolean.valueOf(false));
    VBox.setMargin(ovb, new Insets(0.0));
    vbox.getChildren().add(ovb);
    // System.out.println(FxSizes.getNodeWidth(vbox)+"x"+FxSizes.getNodeHeight(vbox));
    vbox.setMinWidth(ovb.getMinWidth()+
      vbox.getPadding().getLeft()+vbox.getPadding().getRight());
    vbox.setMinHeight(ovb.getMinHeight()+
      vbox.getPadding().getTop()+vbox.getPadding().getBottom());
    vbox.setPrefWidth(vbox.getMinWidth());
    vbox.setPrefHeight(vbox.getMinHeight());
    /* the +10 is necessary because of a bug if JavaFX! */
    Scene scene = new Scene(vbox,vbox.getMinWidth()+10.0,vbox.getMinHeight()+10.0);
    stage.setScene(scene);
    /* if resizable is set to false, the size increases magically by 10! */
    stage.setResizable(true);
    stage.toFront();
    stage.show();
  } /* start */


} /* class OptionVBoxTester */
