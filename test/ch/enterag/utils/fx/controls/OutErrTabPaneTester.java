package ch.enterag.utils.fx.controls;

import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import ch.enterag.utils.*;
import ch.enterag.utils.lang.*;

public class OutErrTabPaneTester
  extends Application
  implements EventHandler<ActionEvent>
{
  /** return codes */
  private static final int iRETURN_OK = 0;
  private static final int iRETURN_ERROR = 8;

  /** margin inside main pane */
  private static final double dMARGIN = 10.0;
  /** light gray style */
  private static final String sSTYLE_BACKGROUND_LIGHTGREY = "-fx-background-color: lightgrey";
  private Stage _stage = null;
  private Button _btnOut = null;
  private Button _btnErr = null;
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    int iReturn = iRETURN_ERROR;
    try
    {
      // Problem: if there are two classes extending Application, we must load the ancillary one first
      try { Class.forName("ch.admin.bar.siard2.gui.SiardGui"); }
      catch(ClassNotFoundException cnfe) { System.err.println(EU.getExceptionMessage(cnfe)); }
      launch(args);
      iReturn = iRETURN_OK;
    }
    catch (Exception e) { System.err.println(e.getClass().getName()+": "+e.getMessage()); }
    System.exit(iReturn);
  }

  @Override
  public void start(Stage stage) throws Exception
  {
    _stage = stage;
    _stage.setTitle(ConsolePrintStreamTester.class.getSimpleName());
    BorderPane bp = new BorderPane();
    bp.setStyle(sSTYLE_BACKGROUND_LIGHTGREY);
    Scene scene = new Scene(bp);
    stage.setScene(scene);
    OutErrTabPane oetp = new OutErrTabPane();
    BorderPane.setMargin(oetp, new Insets(dMARGIN));
    bp.setCenter(oetp);
    _btnOut = new Button("Out");
    _btnOut.setOnAction(this);
    _btnErr = new Button("Err");
    _btnErr.setOnAction(this);
    HBox hbox = new HBox();
    hbox.getChildren().add(_btnOut);
    hbox.getChildren().add(_btnErr);
    bp.setBottom(hbox);
    stage.show();
  } /* start */

  
  @Override
  public void handle(ActionEvent ae)
  {
    if (ae.getSource() == _btnOut)
      System.out.println(RandomTestUtils.getRandomString());
    else if (ae.getSource() == _btnErr)
      System.err.println(RandomTestUtils.getRandomString());
  }

}
