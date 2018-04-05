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
import ch.enterag.utils.fx.controls.ObjectListTableView;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.primary.*;


public class MetaDataTableTester
  extends Application
  implements EventHandler<ActionEvent>
{
  /** return codes */
  private static final int iRETURN_OK = 0;
  private static final int iRETURN_ERROR = 8;
  
  private VBox _vbox = null;
  private Archive _archive = null;

  private Button _btnMetaSchemas = null;
  private Button _btnMetaUsers = null;
  private Button _btnMetaRoles = null;
  private Button _btnMetaPrivileges = null;
  private Button _btnMetaTypes = null;
  private Button _btnMetaTables = null;
  private Button _btnMetaViews = null;
  private Button _btnMetaRoutines = null;
  private Button _btnMetaAttributes = null;
  private Button _btnMetaTableColumns = null;
  private Button _btnMetaViewColumns = null;
  private Button _btnMetaCandidateKeys = null;
  private Button _btnMetaForeignKeys = null;
  private Button _btnMetaParameters = null;
  
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
    File fileArchive = new File("testfiles/sample.siard");
    _archive = ArchiveImpl.newInstance();
    _archive.open(fileArchive);
    stage.initStyle(StageStyle.UTILITY);
    _vbox = new VBox(); 
    _vbox.setPadding(new Insets(10));
    _vbox.setSpacing(10);
    _vbox.setAlignment(Pos.TOP_LEFT);
    _vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    _btnMetaSchemas = new Button("MetaSchemas");
    _btnMetaSchemas.setDefaultButton(true);
    _btnMetaSchemas.setOnAction(this);
    _btnMetaUsers = new Button("MetaUsers");
    _btnMetaUsers.setOnAction(this);
    _btnMetaRoles = new Button("MetaRoles");
    _btnMetaRoles.setOnAction(this);
    _btnMetaPrivileges = new Button("MetaPrivileges");
    _btnMetaPrivileges.setOnAction(this);
    HBox hbox = new HBox();
    hbox.setPadding(new Insets(10));
    hbox.setSpacing(10);
    hbox.setAlignment(Pos.TOP_LEFT);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    hbox.getChildren().add(_btnMetaSchemas);
    hbox.getChildren().add(_btnMetaUsers);
    hbox.getChildren().add(_btnMetaRoles);
    hbox.getChildren().add(_btnMetaPrivileges);
    _vbox.getChildren().add(hbox);
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
    _vbox.getChildren().add(hbox);
    _btnMetaAttributes = new Button("MetaAttributes");
    _btnMetaAttributes.setOnAction(this);
    _btnMetaTableColumns = new Button("MetaTableColumns");
    _btnMetaTableColumns.setOnAction(this);
    _btnMetaViewColumns = new Button("MetaViewColumns");
    _btnMetaViewColumns.setOnAction(this);
    _btnMetaCandidateKeys = new Button("MetaCandidateKeys");
    _btnMetaCandidateKeys.setOnAction(this);
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
    hbox.getChildren().add(_btnMetaTableColumns);
    hbox.getChildren().add(_btnMetaViewColumns);
    hbox.getChildren().add(_btnMetaCandidateKeys);
    hbox.getChildren().add(_btnMetaForeignKeys);
    hbox.getChildren().add(_btnMetaParameters);
    _vbox.getChildren().add(hbox);
    _vbox.setMinWidth(FxSizes.fromEms(40));
    _vbox.setMinHeight(FxSizes.fromExes(40));
    Scene scene = new Scene(_vbox);
    stage.setScene(scene);
    stage.show();
  } /* start */

  @Override
  public void handle(ActionEvent ae)
  {
    Node node = _vbox.getChildren().get(0);
    if (node instanceof TableView)
      _vbox.getChildren().remove(0);
    if (ae.getSource() == _btnMetaSchemas)
    {
      ObjectListTableView oltvMetaSchemas = 
        MetaDataTableFactory.newMetaSchemasTableView(_archive.getMetaData());
      _vbox.getChildren().add(0, oltvMetaSchemas);
    }
    if (ae.getSource() == _btnMetaUsers)
    {
      ObjectListTableView oltvMetaUsers = 
        MetaDataTableFactory.newMetaUsersTableView(_archive.getMetaData());
      _vbox.getChildren().add(0, oltvMetaUsers);
    }
    if (ae.getSource() == _btnMetaRoles)
    {
      ObjectListTableView oltvMetaRoles = 
        MetaDataTableFactory.newMetaRolesTableView(_archive.getMetaData());
      _vbox.getChildren().add(0, oltvMetaRoles);
    }
    if (ae.getSource() == _btnMetaPrivileges)
    {
      ObjectListTableView oltvMetaPrivileges = 
        MetaDataTableFactory.newMetaPrivilegesTableView(_archive.getMetaData());
      _vbox.getChildren().add(0, oltvMetaPrivileges);
    }
    else if (ae.getSource() == _btnMetaTypes)
    {
      ObjectListTableView oltvMetaTypes =
        MetaDataTableFactory.newMetaTypesTableView(_archive.getMetaData().getMetaSchema(0));
      _vbox.getChildren().add(0, oltvMetaTypes);
    }
    else if (ae.getSource() == _btnMetaTables)
    {
      ObjectListTableView oltvMetaTables =
        MetaDataTableFactory.newMetaTablesTableView(_archive.getMetaData().getMetaSchema(0));
      _vbox.getChildren().add(0, oltvMetaTables);
    }
    else if (ae.getSource() == _btnMetaViews)
    {
      ObjectListTableView oltvMetaViews =
        MetaDataTableFactory.newMetaViewsTableView(_archive.getMetaData().getMetaSchema(0));
      _vbox.getChildren().add(0, oltvMetaViews);
    }
    else if (ae.getSource() == _btnMetaRoutines)
    {
      ObjectListTableView oltvMetaRoutines =
        MetaDataTableFactory.newMetaRoutinesTableView(_archive.getMetaData().getMetaSchema(0));
      _vbox.getChildren().add(0, oltvMetaRoutines);
    }
    else if (ae.getSource() == _btnMetaAttributes)
    {
      ObjectListTableView oltvMetaAttributes =
        MetaDataTableFactory.newMetaAttributesTableView(_archive.getMetaData().getMetaSchema(0).getMetaType(2));
      _vbox.getChildren().add(0,oltvMetaAttributes);
    }
    else if (ae.getSource() == _btnMetaTableColumns)
    {
      ObjectListTableView oltvMetaTableColumns =
        MetaDataTableFactory.newMetaColumnsTableView(_archive.getMetaData().getMetaSchema(0).getMetaTable(0));
      _vbox.getChildren().add(0, oltvMetaTableColumns);
    }
    else if (ae.getSource() == _btnMetaViewColumns)
    {
      ObjectListTableView oltvMetaViewColumns =
        MetaDataTableFactory.newMetaColumnsTableView(_archive.getMetaData().getMetaSchema(0).getMetaView(1));
      _vbox.getChildren().add(0, oltvMetaViewColumns);
    }
    else if (ae.getSource() == _btnMetaCandidateKeys)
    {
      ObjectListTableView oltvMetaCandidateKeys =
        MetaDataTableFactory.newMetaCandidateKeysTableView(_archive.getMetaData().getMetaSchema(0).getMetaTable(0));
      _vbox.getChildren().add(0, oltvMetaCandidateKeys);
    }
    else if (ae.getSource() == _btnMetaForeignKeys)
    {
      ObjectListTableView oltvMetaForeignKeys =
        MetaDataTableFactory.newMetaForeignKeysTableView(_archive.getMetaData().getMetaSchema(0).getMetaTable(1));
      _vbox.getChildren().add(0, oltvMetaForeignKeys);
    }
    else if (ae.getSource() == _btnMetaParameters)
    {
      ObjectListTableView oltvMetaParameters =
        MetaDataTableFactory.newMetaParametersTableView(_archive.getMetaData().getMetaSchema(0).getMetaRoutine(1));
      _vbox.getChildren().add(0, oltvMetaParameters);
    }
  }
  
}
