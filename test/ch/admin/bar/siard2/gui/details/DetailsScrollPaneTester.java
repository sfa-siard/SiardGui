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

public class DetailsScrollPaneTester
extends Application
  implements EventHandler<ActionEvent>
{
  /** return codes */
  private static final int iRETURN_OK = 0;
  private static final int iRETURN_ERROR = 8;
  
  private Archive _archive = null;
  private Stage _stage = null;
  private DetailsScrollPane _dsp = null;
  private double _dDetailsWidth = -1.0;
  private double _dDetailsHeight = -1.0;
  private Button _btnMetaData = null;
  private Button _btnMetaSchemas = null;
  private Button _btnMetaUsers = null;
  private Button _btnMetaPrivileges = null;
  private Button _btnMetaSchema = null;
  private Button _btnMetaUser = null;
  private Button _btnMetaPrivilege = null;
  private Button _btnMetaTypes = null;
  private Button _btnMetaTables = null;
  private Button _btnMetaViews = null;
  private Button _btnMetaRoutines = null;
  private Button _btnMetaType = null;
  private Button _btnMetaTable = null;
  private Button _btnMetaView = null;
  private Button _btnMetaRoutine = null;
  private Button _btnMetaAttributes = null;
  private Button _btnMetaColumns = null;
  private Button _btnMetaUniqueKeys = null;
  private Button _btnMetaForeignKeys = null;
  private Button _btnMetaParameters = null;
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
    vbox.setPadding(new Insets(10));
    vbox.setSpacing(10);
    vbox.setAlignment(Pos.TOP_LEFT);
    vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    _dsp = new DetailsScrollPane();
    _dsp.setMetaData(_archive.getMetaData(),MetaSchema.class);
    _dsp.setStyle(FxStyles.sSTYLE_BACKGROUND_YELLOW);
    _dDetailsWidth = ((Region)_dsp.getContent()).getMinWidth();
    _dDetailsHeight = ((Region)_dsp.getContent()).getMinHeight();
    _dsp.setPrefViewportWidth(_dDetailsWidth+2);
    _dsp.setPrefViewportHeight(_dDetailsHeight+62);
    double dWidth = _dsp.getPrefViewportWidth();
    double dHeight = _dsp.getPrefViewportHeight();
    _dsp.prefViewportWidthProperty().bind(vbox.widthProperty());
    _dsp.prefViewportHeightProperty().bind(vbox.heightProperty());
    vbox.getChildren().add(_dsp);
    
    _btnMetaData = new Button("MetaData");
    _btnMetaData.setDefaultButton(true);
    _btnMetaData.setOnAction(this);
    _btnMetaSchemas = new Button("MetaSchemas");
    _btnMetaSchemas.setOnAction(this);
    _btnMetaUsers = new Button("MetaUsers");
    _btnMetaUsers.setOnAction(this);
    _btnMetaPrivileges = new Button("MetaPrivileges");
    _btnMetaPrivileges.setOnAction(this);
    HBox hbox = new HBox();
    hbox.setPadding(new Insets(10));
    hbox.setSpacing(10);
    hbox.setAlignment(Pos.TOP_LEFT);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    hbox.getChildren().add(_btnMetaData);
    hbox.getChildren().add(_btnMetaSchemas);
    hbox.getChildren().add(_btnMetaUsers);
    hbox.getChildren().add(_btnMetaPrivileges);
    if (dWidth < FxSizes.getNodeWidth(hbox))
      dWidth = FxSizes.getNodeWidth(hbox);
    dHeight += FxSizes.getNodeHeight(hbox);
    vbox.getChildren().add(hbox);
    
    _btnMetaSchema = new Button("MetaSchema");
    _btnMetaSchema.setOnAction(this);
    _btnMetaUser = new Button("MetaUser");
    _btnMetaUser.setOnAction(this);
    _btnMetaPrivilege = new Button("MetaPrivilege");
    _btnMetaPrivilege.setOnAction(this);
    hbox = new HBox();
    hbox.setPadding(new Insets(10));
    hbox.setSpacing(10);
    hbox.setAlignment(Pos.TOP_LEFT);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    hbox.getChildren().add(_btnMetaSchema);
    hbox.getChildren().add(_btnMetaUser);
    hbox.getChildren().add(_btnMetaPrivilege);
    if (dWidth < FxSizes.getNodeWidth(hbox))
      dWidth = FxSizes.getNodeWidth(hbox);
    dHeight += FxSizes.getNodeHeight(hbox);
    vbox.getChildren().add(hbox);
    
    _btnMetaTypes = new Button("MetaTypes");
    _btnMetaTypes.setOnAction(this);
    _btnMetaTables = new Button("MetaTables");
    _btnMetaTables.setOnAction(this);
    _btnMetaViews = new Button("MetaViews");
    _btnMetaViews.setOnAction(this);
    _btnMetaRoutines = new Button("MetaRoutines");
    _btnMetaRoutines.setOnAction(this);
    hbox = new HBox();
    hbox.setPadding(new Insets(10));
    hbox.setSpacing(10);
    hbox.setAlignment(Pos.TOP_LEFT);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    hbox.getChildren().add(_btnMetaTypes);
    hbox.getChildren().add(_btnMetaTables);
    hbox.getChildren().add(_btnMetaViews);
    hbox.getChildren().add(_btnMetaRoutines);
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
    
    _btnMetaAttributes = new Button("MetaAttributes");
    _btnMetaAttributes.setOnAction(this);
    _btnMetaColumns = new Button("MetaColumns");
    _btnMetaColumns.setOnAction(this);
    _btnMetaUniqueKeys = new Button("MetaUniqueKeys");
    _btnMetaUniqueKeys.setOnAction(this);
    _btnMetaForeignKeys = new Button("MetaForeignKeys");
    _btnMetaForeignKeys.setOnAction(this);
    _btnMetaParameters = new Button("MetaParameters");
    _btnMetaParameters.setOnAction(this);
    hbox = new HBox();
    hbox.setPadding(new Insets(10));
    hbox.setSpacing(10);
    hbox.setAlignment(Pos.TOP_LEFT);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    hbox.getChildren().add(_btnMetaAttributes);
    hbox.getChildren().add(_btnMetaColumns);
    hbox.getChildren().add(_btnMetaUniqueKeys);
    hbox.getChildren().add(_btnMetaForeignKeys);
    hbox.getChildren().add(_btnMetaParameters);
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
    
    vbox.setMinWidth(dWidth+vbox.getPadding().getLeft()+vbox.getPadding().getRight());
    vbox.setMinHeight(dHeight+vbox.getPadding().getTop()+vbox.getPadding().getBottom());
    Scene scene = new Scene(vbox,vbox.getMinWidth()+10,vbox.getMinHeight()+10);
    stage.setScene(scene);
    stage.toFront();
    stage.show();
  } /* start */

  @Override
  public void handle(ActionEvent ae)
  {
    MetaData md = _archive.getMetaData();
    MetaSchema ms = md.getMetaSchema(0);
    MetaType mty = ms.getMetaType(2);
    MetaTable mt = ms.getMetaTable(1);
    MetaRoutine mr = ms.getMetaRoutine(1);
    if (ae.getSource() == _btnMetaData)
      _dsp.setMetaData(md,MetaSchema.class);
    else if (ae.getSource() == _btnMetaSchemas)
      _dsp.setMetaData(md,MetaSchema.class);
    else if (ae.getSource() == _btnMetaUsers)
      _dsp.setMetaData(md,MetaUser.class);
    else if (ae.getSource() == _btnMetaPrivileges)
      _dsp.setMetaData(md,MetaPrivilege.class);
    else if (ae.getSource() == _btnMetaSchema)
      _dsp.setMetaData(ms,MetaTable.class);
    else if (ae.getSource() == _btnMetaUser)
      _dsp.setMetaData(md.getMetaUser(0),null);
    else if (ae.getSource() == _btnMetaPrivilege)
      _dsp.setMetaData(md.getMetaPrivilege(0),null);
    else if (ae.getSource() == _btnMetaTypes)
      _dsp.setMetaData(ms,MetaType.class);
    else if (ae.getSource() == _btnMetaTables)
      _dsp.setMetaData(ms,MetaTable.class);
    else if (ae.getSource() == _btnMetaViews)
      _dsp.setMetaData(ms,MetaView.class);
    else if (ae.getSource() == _btnMetaRoutines)
      _dsp.setMetaData(ms,MetaRoutine.class);
    else if (ae.getSource() == _btnMetaType)
      _dsp.setMetaData(ms.getMetaType(0),MetaAttribute.class);
    else if (ae.getSource() == _btnMetaTable)
      _dsp.setMetaData(ms.getMetaTable(0),MetaColumn.class);
    else if (ae.getSource() == _btnMetaView)
      _dsp.setMetaData(ms.getMetaView(1),MetaColumn.class);
    else if (ae.getSource() == _btnMetaRoutine)
      _dsp.setMetaData(ms.getMetaRoutine(1),MetaParameter.class);
    else if (ae.getSource() == _btnMetaAttributes)
      _dsp.setMetaData(mty,MetaAttribute.class);
    else if (ae.getSource() == _btnMetaColumns)
      _dsp.setMetaData(mt,MetaColumn.class);
    else if (ae.getSource() == _btnMetaUniqueKeys)
      _dsp.setMetaData(ms.getMetaTable(0),MetaUniqueKey.class);
    else if (ae.getSource() == _btnMetaForeignKeys)
      _dsp.setMetaData(mt,MetaForeignKey.class);
    else if (ae.getSource() == _btnMetaParameters)
      _dsp.setMetaData(mr,MetaParameter.class);
    else if (ae.getSource() == _btnMetaAttribute)
      _dsp.setMetaData(mty.getMetaAttribute(1),null);
    else if (ae.getSource() == _btnMetaColumn)
      _dsp.setMetaData(mt.getMetaColumn(3),null);
    else if (ae.getSource() == _btnMetaUniqueKey)
      _dsp.setMetaData(mt.getMetaPrimaryKey(),null);
    else if (ae.getSource() == _btnMetaForeignKey)
      _dsp.setMetaData(mt.getMetaForeignKey(0),null);
    else if (ae.getSource() == _btnMetaParameter)
      _dsp.setMetaData(mr.getMetaParameter(0),null);
    double dDetailsWidth = ((Region)_dsp.getContent()).getMinWidth();
    double dDetailsHeight = ((Region)_dsp.getContent()).getMinHeight();
    _stage.setWidth(_stage.getWidth()-_dDetailsWidth+dDetailsWidth);
    _stage.setHeight(_stage.getHeight()-_dDetailsHeight+dDetailsHeight);
    _dDetailsWidth = dDetailsWidth;
    _dDetailsHeight = dDetailsHeight;
  }
  
}
