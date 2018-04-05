package ch.admin.bar.siard2.gui.tv;

import java.io.*;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.layout.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import ch.enterag.utils.*;
import ch.enterag.utils.fx.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.primary.*;

public class ArchiveTreeViewTester
  extends Application
  implements EventHandler<ActionEvent>
{
  /** return codes */
  private static final int iRETURN_OK = 0;
  private static final int iRETURN_ERROR = 8;
  
  @SuppressWarnings("unused")
  private Stage _stage = null;
  private ArchiveTreeView _atv = null;
  private Button _butSample = null;
  private Button _butSakila = null;

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
    stage.initStyle(StageStyle.UTILITY);
    VBox vbox = new VBox(); 
    vbox.setPadding(new Insets(10));
    vbox.setSpacing(10);
    vbox.setAlignment(Pos.TOP_LEFT);
    vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    // Scrollpane is not needed, because TreeView is already scrolling!!!
    // ScrollPane sp = new ScrollPane();
    // sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    // sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    _atv = new ArchiveTreeView();
    _atv.setStyle(FxStyles.sSTYLE_BACKGROUND_YELLOW);
    vbox.setPrefWidth(FxSizes.fromEms(20.0));
    vbox.setPrefHeight(FxSizes.fromExes(15.0));
    // sp.setContent(_atv);
    // sp.prefViewportWidthProperty().bind(vbox.widthProperty());
    // sp.prefViewportHeightProperty().bind(vbox.heightProperty());
    vbox.getChildren().add(_atv);
    _butSample = new Button("Sample");
    _butSample.setDefaultButton(true);
    _butSample.setOnAction(this);
    _butSakila = new Button("Sakila");
    _butSakila.setOnAction(this);
    HBox hbox = new HBox();
    hbox.setPadding(new Insets(10));
    hbox.setSpacing(10);
    hbox.setAlignment(Pos.TOP_LEFT);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    hbox.getChildren().add(_butSample);
    hbox.getChildren().add(_butSakila);
    vbox.getChildren().add(hbox);
    Scene scene = new Scene(vbox);
    stage.setScene(scene);
    stage.show();
  } /* start */

  @Override
  public void handle(ActionEvent ae)
  {
    try
    {
      File fileArchive = new File("testfiles/sample.siard");
      if (ae.getSource() == _butSakila)
        fileArchive = new File("testfiles/sfdbsakila.siard");
      Archive archive = ArchiveImpl.newInstance();
      archive.open(fileArchive);
      _atv.setArchive(archive);
    }
    catch(IOException ie) { System.err.println(EU.getExceptionMessage(ie)); }
  }

}
