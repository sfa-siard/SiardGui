package ch.enterag.utils.fx.controls;

import java.io.*;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import ch.enterag.utils.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.lang.*;

public class ConsolePrintStreamTester 
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
  private Button _btnAdd = null;
  private PrintStream _ps = null;
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
    ScrollPane sp = new ScrollPane();
    sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);    
    TextArea ta = new TextArea();
    ta.setEditable(false);
    ta.setWrapText(false);
    ta.setText(RandomTestUtils.getRandomText(1642));
    System.out.println("Initial length: "+String.valueOf(ta.getText().length()));
    ta.setPrefColumnCount(80);
    ta.setPrefRowCount(50);
    _ps = ConsolePrintStream.newConsolePrintStream(ta); 
    sp.setContent(ta);
    sp.setPrefViewportWidth(ta.getPrefColumnCount()*FxSizes.getEm());
    sp.setPrefViewportHeight(ta.getPrefRowCount()*FxSizes.getEx());
    sp.setFitToWidth(true);
    sp.setFitToHeight(true);
    BorderPane.setMargin(sp, new Insets(dMARGIN));
    bp.setCenter(sp);
    _btnAdd = new Button("Add");
    _btnAdd.setOnAction(this);
    bp.setBottom(_btnAdd);
    stage.show();
  } /* start */

  
  @Override
  public void handle(ActionEvent ae)
  {
    _ps.println(RandomTestUtils.getRandomString());
    System.out.println("New length: "+String.valueOf(((ConsolePrintStream)_ps).getTextArea().getLength()));
    
  }

}
