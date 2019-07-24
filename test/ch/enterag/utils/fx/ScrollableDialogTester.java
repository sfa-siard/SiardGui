package ch.enterag.utils.fx;

import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class ScrollableDialogTester extends Application
{
  public static final int iLINES = 12;
  
  public class TestScrollableDialog 
    extends ScrollableDialog 
    implements EventHandler<ActionEvent>
  {
    /* _bExit becomes true, when exit button is pressed */
    private boolean _bExit = false;
    public boolean isExit() { return _bExit; }
    /* _bSize becomes true, when size button is pressed */ 
    private boolean _bSize = false;
    public boolean isSize() { return _bSize; }
    /** exit button (= Escape) */
    private Button _btnExit = null;
    /** change size button (= Enter) */
    private Button _btnSize = null;
    
    @Override
    public void handle(ActionEvent ae)
    {
      if (ae.getSource() == _btnExit)
        _bExit = true;
      else
        _bSize = true;
      close();
    } /* handle */
    
    /* test dialog has many lines and two buttons: one for quitting and one for changing size */
    public TestScrollableDialog(Stage stageOwner, String sTitle, int iLines)
    {
      super(stageOwner, sTitle);
      /* change size button */
      _btnSize = new Button("Size");
      _btnSize.setDefaultButton(true); // associate it with Enter key
      _btnSize.setOnAction(this);
      /* exit button */
      _btnExit = new Button("Exit");
      _btnExit.setCancelButton(true); // associate with ESC key
      _btnExit.setOnAction(this);
      /* HBox for buttons */
      HBox hbox = new HBox();
      hbox.setPadding(new Insets(dINNER_PADDING));
      hbox.setSpacing(dHSPACING); // between increase and exit
      hbox.setAlignment(Pos.TOP_RIGHT);
      hbox.getChildren().add(_btnSize);
      HBox.setMargin(_btnSize, new Insets(dOUTER_PADDING));
      hbox.getChildren().add(_btnExit);
      HBox.setMargin(_btnExit, new Insets(dOUTER_PADDING));
      /* VBox for lines and buttons */
      VBox vbox = new VBox();
      vbox.setPadding(new Insets(dOUTER_PADDING));
      vbox.setSpacing(dVSPACING);
      vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
      for (int iLine = 0; iLine < iLines; iLine++)
      {
        Text txt = new Text("Line "+String.valueOf(iLine));
        vbox.getChildren().add(txt);
        VBox.setMargin(txt, new Insets(dOUTER_PADDING,dOUTER_PADDING,dOUTER_PADDING,0));
      }
      vbox.getChildren().add(hbox);
      /* scene */
      Scene scene = new Scene(vbox);
      setScene(scene);
    } /* constructor TestDialog */

  }
  @Override
  public void start(Stage stage) throws Exception
  {
    TestScrollableDialog tsd = null;
    int iLines = iLINES;
    for (boolean bExit = false; !bExit; bExit = tsd.isExit())
    {
      tsd = new TestScrollableDialog(stage,"DialogTester",iLines);
      tsd.showAndWait();
      if (tsd.isSize())
        iLines = (iLines == iLINES)? 2*iLINES : iLINES;
    }
    stage.close();
  } /* start */
  
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    launch(args);
  }
  
}
