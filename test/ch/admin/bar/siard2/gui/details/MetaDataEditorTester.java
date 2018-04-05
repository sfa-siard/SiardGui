package ch.admin.bar.siard2.gui.details;

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
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.primary.*;

public class MetaDataEditorTester
  extends Application
  implements EventHandler<ActionEvent>
{
  /** return codes */
  private static final int iRETURN_OK = 0;
  private static final int iRETURN_ERROR = 8;

  private Archive _archive = null;
  private Stage _stage = null;
  private MetaDataEditor _mde = null;
  private double _dEditorWidth = -1.0;
  private double _dEditorHeight = -1.0;
  private Button _btnMetaData = null;
  private Button _btnMetaSchema = null;
  private Button _btnMetaUser = null;
  private Button _btnMetaPrivilege = null;
  private Button _btnMetaType = null;
  private Button _btnMetaTable = null;
  private Button _btnMetaView = null;
  private Button _btnMetaRoutine = null;
  private Button _btnMetaAttribute = null;
  private Button _btnMetaColumn = null;
  private Button _btnMetaUniqueKey = null;
  private Button _btnMetaForeignKey = null;
  private Button _btnMetaParameter = null;

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
    File fileArchive = new File("testfiles/sample.siard");
    _archive = ArchiveImpl.newInstance();
    _archive.open(fileArchive);
    stage.initStyle(StageStyle.UTILITY);
    VBox vbox = new VBox(); 
    vbox.setPadding(new Insets(0.0));
    vbox.setSpacing(10);
    vbox.setAlignment(Pos.TOP_LEFT);
    vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    _mde = new MetaDataEditor();
    _mde.setMetaData(_archive.getMetaData());
    _dEditorWidth = _mde.getMinWidth();
    _dEditorHeight = _mde.getMinHeight();
    ScrollPane sp = new ScrollPane();
    sp.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    sp.setContent(_mde);
    sp.setPrefViewportWidth(_dEditorWidth+2);
    sp.setPrefViewportHeight(_dEditorHeight+32);
    double dWidth = sp.getPrefViewportWidth();
    double dHeight = sp.getPrefViewportHeight();
    sp.prefViewportWidthProperty().bind(vbox.widthProperty());
    sp.prefViewportHeightProperty().bind(vbox.heightProperty());
    vbox.getChildren().add(sp);

    _btnMetaData = new Button("MetaData");
    _btnMetaData.setDefaultButton(true);
    _btnMetaData.setOnAction(this);
    _btnMetaSchema = new Button("MetaSchema");
    _btnMetaSchema.setOnAction(this);
    _btnMetaUser = new Button("MetaUser");
    _btnMetaUser.setOnAction(this);
    _btnMetaPrivilege = new Button("MetaPrivilege");
    _btnMetaPrivilege.setOnAction(this);
    HBox hbox = new HBox();
    hbox.setPadding(new Insets(10));
    hbox.setSpacing(10);
    hbox.setAlignment(Pos.TOP_LEFT);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    hbox.getChildren().add(_btnMetaData);
    hbox.getChildren().add(_btnMetaSchema);
    hbox.getChildren().add(_btnMetaUser);
    hbox.getChildren().add(_btnMetaPrivilege);
    if (dWidth < FxSizes.getNodeWidth(hbox))
      dWidth = FxSizes.getNodeWidth(hbox);
    dHeight += FxSizes.getNodeHeight(hbox);
    vbox.getChildren().add(hbox);
    
    _btnMetaType = new Button("MetaType");
    _btnMetaType.setOnAction(this);
    _btnMetaTable = new Button("MetaTable");
    _btnMetaTable.setOnAction(this);
    _btnMetaView = new Button("MetaView");
    _btnMetaView.setOnAction(this);
    _btnMetaRoutine = new Button("MetaRoutine");
    _btnMetaRoutine.setOnAction(this);
    hbox = new HBox();
    hbox.setPadding(new Insets(10));
    hbox.setSpacing(10);
    hbox.setAlignment(Pos.TOP_LEFT);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    hbox.getChildren().add(_btnMetaType);
    hbox.getChildren().add(_btnMetaTable);
    hbox.getChildren().add(_btnMetaView);
    hbox.getChildren().add(_btnMetaRoutine);
    if (dWidth < FxSizes.getNodeWidth(hbox))
      dWidth = FxSizes.getNodeWidth(hbox);
    dHeight += FxSizes.getNodeHeight(hbox);
    vbox.getChildren().add(hbox);
    
    _btnMetaAttribute = new Button("MetaAttribute");
    _btnMetaAttribute.setOnAction(this);
    _btnMetaColumn = new Button("MetaColumn");
    _btnMetaColumn.setOnAction(this);
    _btnMetaUniqueKey = new Button("MetaUniqueKey");
    _btnMetaUniqueKey.setOnAction(this);
    _btnMetaForeignKey = new Button("MetaForeignKey");
    _btnMetaForeignKey.setOnAction(this);
    _btnMetaParameter = new Button("MetaParameter");
    _btnMetaParameter.setOnAction(this);
    hbox = new HBox();
    hbox.setPadding(new Insets(10));
    hbox.setSpacing(10);
    hbox.setAlignment(Pos.TOP_LEFT);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    hbox.getChildren().add(_btnMetaAttribute);
    hbox.getChildren().add(_btnMetaColumn);
    hbox.getChildren().add(_btnMetaUniqueKey);
    hbox.getChildren().add(_btnMetaForeignKey);
    hbox.getChildren().add(_btnMetaParameter);
    if (dWidth < FxSizes.getNodeWidth(hbox))
      dWidth = FxSizes.getNodeWidth(hbox);
    dHeight += FxSizes.getNodeHeight(hbox);
    vbox.getChildren().add(hbox);
    
    vbox.setMinWidth(dWidth);
    vbox.setMinHeight(dHeight);
    Scene scene = new Scene(vbox,vbox.getMinWidth()+10.0,vbox.getMinHeight()+10.0);
    stage.setScene(scene);
    stage.toFront();
    stage.show();
  }

  @Override
  public void handle(ActionEvent ae)
  {
    if (ae.getSource() == _btnMetaData)
      _mde.setMetaData(_archive.getMetaData());
    else if (ae.getSource() == _btnMetaSchema)
      _mde.setMetaData(_archive.getMetaData().getMetaSchema(0));
    else if (ae.getSource() == _btnMetaUser)
      _mde.setMetaData(_archive.getMetaData().getMetaUser(0));
    else if (ae.getSource() == _btnMetaPrivilege)
      _mde.setMetaData(_archive.getMetaData().getMetaPrivilege(0));
    else if (ae.getSource() == _btnMetaType)
      _mde.setMetaData(_archive.getMetaData().getMetaSchema(0).getMetaType(0));
    else if (ae.getSource() == _btnMetaTable)
      _mde.setMetaData(_archive.getMetaData().getMetaSchema(0).getMetaTable(0));
    else if (ae.getSource() == _btnMetaView)
      _mde.setMetaData(_archive.getMetaData().getMetaSchema(0).getMetaView(1));
    else if (ae.getSource() == _btnMetaRoutine)
      _mde.setMetaData(_archive.getMetaData().getMetaSchema(0).getMetaRoutine(1));
    else if (ae.getSource() == _btnMetaAttribute)
      _mde.setMetaData(_archive.getMetaData().getMetaSchema(0).getMetaType(2).getMetaAttribute(1));
    else if (ae.getSource() == _btnMetaColumn)
      _mde.setMetaData(_archive.getMetaData().getMetaSchema(0).getMetaTable(1).getMetaColumn(3));
    else if (ae.getSource() == _btnMetaUniqueKey)
      _mde.setMetaData(_archive.getMetaData().getMetaSchema(0).getMetaTable(1).getMetaPrimaryKey());
    else if (ae.getSource() == _btnMetaForeignKey)
      _mde.setMetaData(_archive.getMetaData().getMetaSchema(0).getMetaTable(1).getMetaForeignKey(0));
    else if (ae.getSource() == _btnMetaParameter)
      _mde.setMetaData(_archive.getMetaData().getMetaSchema(0).getMetaRoutine(1).getMetaParameter(0));
    double dEditorWidth = _mde.getMinWidth();
    double dEditorHeight = _mde.getMinHeight();
    _stage.setWidth(_stage.getWidth()-_dEditorWidth+dEditorWidth);
    _stage.setHeight(_stage.getHeight()-_dEditorHeight+dEditorHeight);
    _dEditorWidth = dEditorWidth;
    _dEditorHeight = dEditorHeight;
  }

}
