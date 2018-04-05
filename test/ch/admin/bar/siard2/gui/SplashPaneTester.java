package ch.admin.bar.siard2.gui;

import java.io.*;
import java.util.*;
import ch.enterag.utils.*;
import javafx.application.*;
import javafx.event.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.input.*;

public class SplashPaneTester 
  extends Application 
  implements EventHandler<MouseEvent>
{
  /** return codes */
  private static final int iRETURN_OK = 0;
  private static final int iRETURN_ERROR = 8;
  
  private Stage _stage = null;

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    int iReturn = iRETURN_ERROR;
    try
    {
      Application.launch(args);
      iReturn = iRETURN_OK;
    }
    catch (Exception e) { System.err.println(EU.getExceptionMessage(e)); }
    System.exit(iReturn);
  }

  @Override
  public void start(Stage stage) throws Exception
  {
    _stage = stage;
    stage.initStyle(StageStyle.UNDECORATED);
    Scene scene = new Scene(SplashPane.newSplashPane());
    stage.setScene(scene);
    // any click on the splash pane will close it.
    scene.setOnMouseClicked(this);
    stage.show();
    List<String> listUnnamed = getParameters().getUnnamed();
    if ((listUnnamed.size() > 0) && listUnnamed.get(0).equals("exit"))
    {
      File filePng = new File("doc/manual/splash.png");
      System.out.println("Saving \""+filePng.getAbsolutePath()+"\" ...");
      SplashPane.saveSnapshot(filePng);
      stage.close();
      Platform.exit();
    }
  } /* start */

  @Override
  public void handle(MouseEvent arg0)
  {
    _stage.close();
  }
  
} /* SplashPaneTester */
