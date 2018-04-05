package ch.enterag.utils.fx.controls;

import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import ch.enterag.utils.fx.controls.ScrollPane;

public class ScrollPaneTester
  extends Application
{
  /** return codes */
  private static final int iRETURN_OK = 0;
  private static final int iRETURN_ERROR = 8;

  private static double dPADDING = 10.0;
  private static int iRow = 0;
  private static int iColumn = 0;
  
  private Button _aabtn[][] = null;
  private ScrollPane _sp = new ScrollPane();
  
  private EventHandler<KeyEvent> _kh = new EventHandler<KeyEvent>() 
  {
    @Override public void handle(KeyEvent ke)
    {
      if ((ke.getCode() == KeyCode.LEFT) || (ke.getCode() == KeyCode.NUMPAD4))
      {
        if (iColumn > 0)
          iColumn--;
        if (ke.isControlDown())
          _sp.makeVisible(_aabtn[iRow][iColumn]);
        else if (ke.isAltDown())
          _sp.setHvalue(0.0);
        else
          _sp.scrollToCenter(_aabtn[iRow][iColumn]);
      }
      else if ((ke.getCode() == KeyCode.RIGHT) || (ke.getCode() == KeyCode.NUMPAD6))
      {
        if (iColumn < 3)
          iColumn++;
        if (ke.isControlDown())
          _sp.makeVisible(_aabtn[iRow][iColumn]);
        else if (ke.isAltDown())
          _sp.setHvalue(1.0);
        else
          _sp.scrollToCenter(_aabtn[iRow][iColumn]);
      }
      else if ((ke.getCode() == KeyCode.UP) || (ke.getCode() == KeyCode.NUMPAD8))
      {
        if (iRow > 0)
          iRow--;
        if (ke.isControlDown())
          _sp.makeVisible(_aabtn[iRow][iColumn]);
        else if (ke.isAltDown())
          _sp.setVvalue(0.0);
        else
          _sp.scrollToCenter(_aabtn[iRow][iColumn]);
      }
      else if ((ke.getCode() == KeyCode.DOWN) || (ke.getCode() == KeyCode.NUMPAD2))
      {
        if (iRow < 3)
          iRow++;
        if (ke.isControlDown())
          _sp.makeVisible(_aabtn[iRow][iColumn]);
        else if (ke.isAltDown())
          _sp.setVvalue(1.0);
        else
          _sp.scrollToCenter(_aabtn[iRow][iColumn]);
      }
    }
  };
  
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
    _sp = new ScrollPane();
    _sp.setOnKeyPressed(_kh);
    _sp.setMinViewportWidth(400.0);
    _sp.setMinViewportHeight(200.0);

    _aabtn = new Button[4][4];
    for (int iRow = 0; iRow < 4; iRow++)
    {
      for (int iColumn = 0; iColumn < 4; iColumn++)
      {
        Button btn = new Button(String.valueOf(iRow)+"/"+String.valueOf(iColumn));
        btn.setMinWidth(256);
        btn.setMinHeight(128);
        _aabtn[iRow][iColumn] = btn;
      }
    }
    
    VBox vboxContent = new VBox();
    vboxContent.setStyle("-fx-background-color: lightgrey;");
    vboxContent.setPadding(new Insets(dPADDING));
    _sp.setContent(vboxContent);
    
    HBox hboxTop = new HBox();
    vboxContent.getChildren().add(hboxTop);
    
    VBox vboxTl = new VBox();
    vboxTl.setStyle("-fx-background-color: green;");
    vboxTl.setPadding(new Insets(dPADDING));
    hboxTop.getChildren().add(vboxTl);
    
    HBox hboxTl0 = new HBox();
    vboxTl.getChildren().add(hboxTl0);
    
    hboxTl0.getChildren().add(_aabtn[0][0]);
    hboxTl0.getChildren().add(_aabtn[0][1]);
    
    HBox hboxTl1 = new HBox();
    vboxTl.getChildren().add(hboxTl1);
    
    hboxTl1.getChildren().add(_aabtn[1][0]);
    hboxTl1.getChildren().add(_aabtn[1][1]);
    
    VBox vboxTr = new VBox();
    vboxTr.setStyle("-fx-background-color: orange;");
    vboxTr.setPadding(new Insets(dPADDING));
    hboxTop.getChildren().add(vboxTr);
    
    HBox hboxTr0 = new HBox();
    vboxTr.getChildren().add(hboxTr0);
    
    hboxTr0.getChildren().add(_aabtn[0][2]);
    hboxTr0.getChildren().add(_aabtn[0][3]);
    
    HBox hboxTr1 = new HBox();
    vboxTr.getChildren().add(hboxTr1);
    
    hboxTr1.getChildren().add(_aabtn[1][2]);
    hboxTr1.getChildren().add(_aabtn[1][3]);
    
    HBox hboxBottom = new HBox();
    vboxContent.getChildren().add(hboxBottom);
    
    VBox vboxBl = new VBox();
    vboxBl.setStyle("-fx-background-color: magenta;");
    vboxBl.setPadding(new Insets(dPADDING));
    hboxBottom.getChildren().add(vboxBl);
    
    HBox hboxBl0 = new HBox();
    vboxBl.getChildren().add(hboxBl0);
    
    hboxBl0.getChildren().add(_aabtn[2][0]);
    hboxBl0.getChildren().add(_aabtn[2][1]);
    
    HBox hboxBl1 = new HBox();
    vboxBl.getChildren().add(hboxBl1);
    
    hboxBl1.getChildren().add(_aabtn[3][0]);
    hboxBl1.getChildren().add(_aabtn[3][1]);
    
    VBox vboxBr = new VBox();
    vboxBr.setStyle("-fx-background-color: blue;");
    vboxBr.setPadding(new Insets(dPADDING));
    hboxBottom.getChildren().add(vboxBr);
    
    HBox hboxBr0 = new HBox();
    vboxBr.getChildren().add(hboxBr0);
    
    hboxBr0.getChildren().add(_aabtn[2][2]);
    hboxBr0.getChildren().add(_aabtn[2][3]);
    
    HBox hboxBr1 = new HBox();
    vboxBr.getChildren().add(hboxBr1);
    
    hboxBr1.getChildren().add(_aabtn[3][2]);
    hboxBr1.getChildren().add(_aabtn[3][3]);

    stage.initStyle(StageStyle.UTILITY);
    Scene scene = new Scene(_sp,_sp.getMinViewportWidth()+10,_sp.getMinViewportHeight()+10);
    stage.setScene(scene);
    stage.show();
  } /* start */

}
