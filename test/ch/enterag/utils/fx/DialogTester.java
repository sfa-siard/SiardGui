package ch.enterag.utils.fx;

import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class DialogTester extends Application
{
  public class TestDialog extends Dialog implements EventHandler<ActionEvent>
  {
    private boolean _bSmall = true;
    /** exit button (= Escape) */
    private Button _btnExit = null;
    /** change size button (= Enter) */
    private Button _btnSize = null;
    /** container of lines and button */
    private VBox _vbox = null;
    
    /** lines for test */
    private int _iLines = 2;
    
    private void changeSize()
    {
      _bSmall = !_bSmall;
      if (_bSmall)
      {
        /* delete last lines */
        for (int iLine = 2*_iLines-1; iLine >= _iLines; iLine--)
          _vbox.getChildren().remove(iLine);
        _btnSize.setText("increase");
      }
      else
      {
        /* insert lines */
        for (int iLine = _iLines; iLine < 2*_iLines; iLine++)
        {
          Text txt = new Text("Line "+String.valueOf(iLine));
          _vbox.getChildren().add(iLine, txt);
          VBox.setMargin(txt, new Insets(dOUTER_PADDING,dOUTER_PADDING,dOUTER_PADDING,0));
        }
        _btnSize.setText("decrease");
      }
    } /* changeSize */
    
    @Override
    public void handle(ActionEvent ae)
    {
      if (ae.getSource() == _btnExit)
        close();
      else
        changeSize();
    } /* handle */
    
    /* test dialog has many lines and two buttons: one for quitting and one for changing size */
    public TestDialog(Stage stageOwner, String sTitle)
    {
      super(stageOwner, sTitle);
      /* change size button */
      _btnSize = new Button("increase");
      _btnSize.setDefaultButton(true); // associate it with Enter key
      _btnSize.setOnAction(this);
      /* exit button */
      _btnExit = new Button("exit");
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
      _vbox = new VBox();
      _vbox.setPadding(new Insets(dOUTER_PADDING));
      _vbox.setSpacing(dVSPACING);
      _vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
      for (int iLine = 0; iLine < _iLines; iLine++)
      {
        Text txt = new Text("Line "+String.valueOf(iLine));
        _vbox.getChildren().add(txt);
        VBox.setMargin(txt, new Insets(dOUTER_PADDING,dOUTER_PADDING,dOUTER_PADDING,0));
      }
      _vbox.getChildren().add(hbox);
      /* scene */
      Scene scene = new Scene(_vbox);
      setScene(scene);
    } /* constructor TestDialog */

  }
  @Override
  public void start(Stage stage) throws Exception
  {
    TestDialog td = new TestDialog(stage,"DialogTester");
    td.showAndWait();
    stage.close();
  } /* start */
}
