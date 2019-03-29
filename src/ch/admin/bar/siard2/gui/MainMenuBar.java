/*====================================================================== 
MainMenuBar implements menu bar of the SIARD GUI. 
Application: SIARD 2
Description: MainMenuBar implements menu bar of the SIARD GUI.
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 10.05.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui;

import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.input.*;

import ch.enterag.utils.StopWatch;
import ch.enterag.utils.fx.*;
import ch.admin.bar.siard2.api.*;

/*====================================================================*/
/** MainMenuBar implements menu bar of the CultLibEditor.
 * It also implements its own "controller" by implementing the 
 * corresponding EventHandler.
 * @author Hartwig Thomas
 */
public class MainMenuBar 
  extends MenuBar 
  implements EventHandler<ActionEvent>
{
  /** singleton */
  private static MainMenuBar _mmb = null;
  /** menus and menu items */
  private Menu _menuFile = null;
  private MenuItem _miDownload = null;
  private Menu _menuDownloadMru = null;
  private MenuItem _miOpen = null;
  private Menu _menuOpenMru = null;
  private MenuItem _miSave = null;
  private MenuItem _miDisplayMetaData = null;
  private MenuItem _miAugmentMetaData = null; // import meta data
  private MenuItem _miUpload = null;
  private Menu _menuUploadMru= null;
  private MenuItem _miClose = null;
  private MenuItem _miExit = null;
  private Menu _menuEdit = null;
  private MenuItem _miCopyAll = null;
  private MenuItem _miCopyRow = null;
  private MenuItem _miExportTable = null;
  private MenuItem _miFind = null; // meta data
  private MenuItem _miFindNext = null; // meta data
  private MenuItem _miSearch = null; // primary data
  private MenuItem _miSearchNext = null; // primary data
  private Menu _menuTools = null;
  private MenuItem _miInstall = null;
  private MenuItem _miUninstall = null;
  private Menu _menuLanguage = null;
  private MenuItem _miIntegrity = null;
  private MenuItem _miOptions = null;
  private Menu _menuHelp = null;
  private MenuItem _miHelp = null;
  private MenuItem _miInfo = null;

  public StopWatch _swRestrict = StopWatch.getInstance();
  
  /*==================================================================*/
  private class ToggleChangeListener
    implements ChangeListener<Toggle>
  {
    /*------------------------------------------------------------------*/
    /** {@link javafx.beans.value.ChangeListener<Toggle>#changed changed}
     * handles toggling of language radio menu items */
    @Override
    public void changed(ObservableValue<? extends Toggle> ovValue,
      Toggle tOld, Toggle tNew)
    {
      if (tNew != null)
      {
        RadioMenuItem rmiLanguage = (RadioMenuItem)tNew;
        String sLanguage = (String)rmiLanguage.getUserData();
        SiardGui.getSiardGui().setLanguage(sLanguage);
      }
    } /* changed */
  }
  private ToggleChangeListener _tcl = new ToggleChangeListener();

  /*==================================================================*/
  /** this class fixes a bug in the disable display ...
   */
  private class DisableChangeListener
    implements ChangeListener<Boolean>
  {
    /*------------------------------------------------------------------*/
    /** {@link javafx.beans.value.ChangeListener<Boolean>#changed changed}
     * handles change of disable state */
    @Override
    public void changed(ObservableValue<? extends Boolean> ovValue,
      Boolean bOld, Boolean bNew)
    {
      BooleanProperty bp = (BooleanProperty)ovValue;
      MenuItem mi = (MenuItem)bp.getBean();
      if (bNew.booleanValue())
        mi.setStyle(FxStyles.sSTYLE_DISABLED_OPACITY);
      else
        mi.setStyle(FxStyles.sSTYLE_ENABLED_OPACITY);
    } /* changed */
  }
  private DisableChangeListener _dcl = new DisableChangeListener(); 
  
  /*------------------------------------------------------------------*/
  /** {@link javafx.event.EventHandler<ActionEvent>#handle handle}
   * handles selection of menu items */
  @Override
  public void handle(ActionEvent ae)
  {
    SiardGui sg = SiardGui.getSiardGui();
    MenuItem mi = (MenuItem)ae.getSource(); 
    if (mi == _miDownload)
      sg.download();
    else if (mi.getParentMenu() == _menuDownloadMru)
    {
      MruConnection.Connection connMru = (MruConnection.Connection)mi.getUserData();
      sg.download(connMru._sUrl, connMru._sDbUser);
    }
    else if (mi == _miOpen)
      sg.openArchive();
    else if (mi.getParentMenu() == _menuOpenMru)
      sg.openArchive((String)mi.getUserData());
    else if (mi == _miSave)
      sg.save();
    else if (mi == _miDisplayMetaData)
      sg.displayMetaData();
    else if (mi == _miAugmentMetaData)
      sg.augmentMetaData();
    else if (mi == _miUpload)
      sg.upload();
    else if (mi.getParentMenu() == _menuUploadMru)
    {
      MruConnection.Connection connMru = (MruConnection.Connection)mi.getUserData();
      sg.upload(connMru._sUrl, connMru._sDbUser);
    }
    else if (mi == _miClose)
      sg.closeArchive();
    else if (mi == _miExit)
      sg.exit();
    else if (mi == _miCopyAll)
      sg.copyAll();
    else if (mi == _miCopyRow)
      sg.copyRow();
    else if (mi == _miExportTable)
      sg.exportTable();
    else if (mi == _miFind)
      sg.find();
    else if (mi == _miFindNext)
      sg.findNext();
    else if (mi == _miSearch)
      sg.search();
    else if (mi == _miSearchNext)
      sg.searchNext();
    else if (mi == _miInstall)
      sg.install();
    else if (mi == _miUninstall)
      sg.uninstall(false);
    // language is handled by change listener
    else if (mi == _miIntegrity)
      sg.integrity();
    else if (mi == _miOptions)
      sg.options();
    else if (mi == _miHelp)
      sg.help();
    else if (mi == _miInfo)
      sg.info(); // display Info dialog
  } /* handle */

  /*------------------------------------------------------------------*/
  /** restrict enables and disables menu items.
   */
  public void restrict()
  {
    _swRestrict.start();
    boolean bAvailable = false;
    boolean bChanged = false;
    boolean bValid = false;
    Archive archive = SiardGui.getSiardGui().getArchive();
    MainPane mp = MainPane.getMainPane();
    if (archive != null)
    {
      bAvailable = true;
      if (archive.isValid())
        bValid = true;
      if ((!archive.isMetaDataUnchanged()) || mp.isChanged())
        bChanged = true;
    }
    _miDownload.setDisable(bChanged && bValid);
    boolean bDisableMruDownloads = _miDownload.isDisable() || 
      (MruConnection.getMruConnection(true).getMruConnections() == 0);
    _menuDownloadMru.setDisable(bDisableMruDownloads);
    _miUpload.setDisable((!bAvailable) || (!bValid));
    boolean bDisableMruUploads = _miUpload.isDisable() || 
      (MruConnection.getMruConnection(false).getMruConnections() == 0);
    _menuUploadMru.setDisable(bDisableMruUploads);
    _miOpen.setDisable(bAvailable && bChanged);  
    boolean bDisableMruFiles = _miOpen.isDisable() ||
      (MruFile.getMruFile().getMruFiles() == 0);
    _menuOpenMru.setDisable(bDisableMruFiles);
    _miSave.setDisable((!bAvailable) || (!bChanged) || (!bValid));
    _miDisplayMetaData.setDisable(!bAvailable);
    _miAugmentMetaData.setDisable(false);
    _miClose.setDisable(!bAvailable);
    Table table = mp.getSelectedTable();
    _miCopyAll.setDisable(mp.getDisplayedTableView() == null);
    _miCopyRow.setDisable(mp.getSelectedTableRow() < 0);
    _miExportTable.setDisable(table == null);
    _miFind.setDisable(!bAvailable);
    _miFindNext.setDisable((!bAvailable) || (!archive.getMetaData().canFindNext()));
    _miSearch.setDisable(table == null);
    _miSearchNext.setDisable((table == null) || (!table.canFindNext()));
    // for install/uninstall we need to look at locations and versions
    String sInstalledVersion = UserProperties.getUserProperties().getInstalledVersion(null);
    _miInstall.setDisable(SiardGui.compareVersion(sInstalledVersion) <= 0);
    _miUninstall.setDisable(sInstalledVersion == null);
    _miIntegrity.setDisable((!bAvailable) || (archive.getMetaData().getMessageDigest().size() == 0));
    _miOptions.setDisable(false);
    _miHelp.setDisable(false);
    _miInfo.setDisable(false);
    _swRestrict.stop();
  } /* restrict */
  
  /*------------------------------------------------------------------*/
  /** set all language-dependent texts in menu.
   */
  void refreshLanguage()
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    _menuFile.setText(sb.getMenuFile());
    _miDownload.setText(sb.getMenuFileDownload());
    _menuDownloadMru.setText(sb.getMenuFileDownloadMru());
    _miUpload.setText(sb.getMenuFileUpload());
    _menuUploadMru.setText(sb.getMenuFileUploadMru());
    _miOpen.setText(sb.getMenuFileOpen());
    _menuOpenMru.setText(sb.getMenuFileOpenMru());
    _miSave.setText(sb.getMenuFileSave());
    _miDisplayMetaData.setText(sb.getMenuFileDisplayMetaData());
    _miAugmentMetaData.setText(sb.getMenuFileAugmentMetaData());
    _miClose.setText(sb.getMenuFileClose());
    _miExit.setText(sb.getMenuFileExit());
    _menuEdit.setText(sb.getMenuEdit());
    _miCopyAll.setText(sb.getMenuEditCopyAll());
    _miCopyRow.setText(sb.getMenuEditCopyRow());
    _miExportTable.setText(sb.getMenuEditExportTable());
    _miFind.setText(sb.getMenuEditFind());
    _miFindNext.setText(sb.getMenuEditFindNext());
    _miSearch.setText(sb.getMenuEditSearch());
    _miSearchNext.setText(sb.getMenuEditSearchNext());
    _menuTools.setText(sb.getMenuTools());
    _miInstall.setText(sb.getMenuToolsInstall());
    _miUninstall.setText(sb.getMenuToolsUninstall());
    _menuLanguage.setText(sb.getMenuToolsLanguage());
    for (int iLanguage = 0; iLanguage < _menuLanguage.getItems().size(); iLanguage++)
    {
      RadioMenuItem rmiLanguage = (RadioMenuItem)_menuLanguage.getItems().get(iLanguage);
      String sLanguage = (String)rmiLanguage.getUserData();
      rmiLanguage.setText(sb.getLanguage(sLanguage));
    }
    _miIntegrity.setText(sb.getMenuToolsIntegrity());
    _miOptions.setText(sb.getMenuToolsOptions());
    _menuHelp.setText(sb.getMenuHelp());
    _miHelp.setText(sb.getMenuHelpHelp());
    _miInfo.setText(sb.getMenuHelpInfo());
  } /* refreshLanguage */
  
  /*------------------------------------------------------------------*/
  /** replace the MRU connection sub menu.
   * @param bDownload true, for MRU download connections; false for 
   *   MRU upload connections. 
   */
  public void setConnectionMru(boolean bDownload)
  {
    MruConnection mc = MruConnection.getMruConnection(bDownload);
    Menu menu = null;
    if (bDownload)
      menu = _menuDownloadMru;
    else
      menu = _menuUploadMru;
    menu.getItems().clear();
    for (int iMru = 0; iMru < mc.getMruConnections(); iMru++)
    {
      MenuItem mi = new MenuItem();
      mi.setText(mc.getMruConnectionUrl(iMru)+"|"+mc.getMruConnectionDbUser(iMru));
      mi.setUserData(mc.getMruConnection(iMru));
      mi.setOnAction(this);
      menu.getItems().add(mi);
    }
  } /* setConnectionMru */
  
  /*------------------------------------------------------------------*/
  /** replace the MRU file sub menu.
   */
  public void setFileMru()
  {
    MruFile mf = MruFile.getMruFile();
    Menu menu = _menuOpenMru;
    menu.getItems().clear();
    for (int iMru = 0; iMru < mf.getMruFiles(); iMru++)
    {
      MenuItem mi = new MenuItem();
      mi.setText(mf.getMruFile(iMru));
      mi.setUserData(mf.getMruFile(iMru));
      mi.setOnAction(this);
      menu.getItems().add(mi);
    }
  } /* setFileMru */
  
  private MenuItem createMenuItem()
  {
    MenuItem mi = new MenuItem();
    mi.disableProperty().addListener(_dcl);
    mi.setOnAction(this);
    return mi;
  } /* createMenuItem */
  
  /*------------------------------------------------------------------*/
  /** constructor
   */
  private MainMenuBar()
  {
    super();
    _menuFile = new Menu();
    
    _miDownload = createMenuItem();
    _menuFile.getItems().add(_miDownload);
    
    _menuDownloadMru = new Menu();
    setConnectionMru(true);
    _menuFile.getItems().add(_menuDownloadMru);
    
    _miUpload = createMenuItem();
    _menuFile.getItems().add(_miUpload);
    
    _menuUploadMru = new Menu();
    setConnectionMru(false);
    _menuFile.getItems().add(_menuUploadMru);
    
    _menuFile.getItems().add(new SeparatorMenuItem());
    
    _miOpen = createMenuItem();
    _menuFile.getItems().add(_miOpen);
    
    _menuOpenMru = new Menu();
    setFileMru();
    _menuFile.getItems().add(_menuOpenMru);
    
    _miSave = createMenuItem();
    /* Ctrl-S for save */
    _miSave.setAccelerator(new KeyCodeCombination(KeyCode.S,KeyCombination.CONTROL_DOWN));    
    _menuFile.getItems().add(_miSave);

    _miClose = createMenuItem();
    _menuFile.getItems().add(_miClose);

    _menuFile.getItems().add(new SeparatorMenuItem());
    
    _miDisplayMetaData = createMenuItem();
    _menuFile.getItems().add(_miDisplayMetaData);
    
    _miAugmentMetaData = createMenuItem();
    _menuFile.getItems().add(_miAugmentMetaData);
    
    _menuFile.getItems().add(new SeparatorMenuItem());
    
    _miExit = createMenuItem();
    /* Ctrl-X for exit */
    _miExit.setAccelerator(new KeyCodeCombination(KeyCode.X,KeyCombination.CONTROL_DOWN));
    _menuFile.getItems().add(_miExit);
    
    getMenus().add(_menuFile);
    
    _menuEdit = new Menu();
    
    _miCopyAll = createMenuItem();
    /* Ctrl-C for copy all */
    _miCopyAll.setAccelerator(new KeyCodeCombination(KeyCode.C,KeyCombination.CONTROL_DOWN));
    _menuEdit.getItems().add(_miCopyAll);
    
    _miCopyRow = createMenuItem();
    /* Shift-Ctrl-C for copy row */
    _miCopyRow.setAccelerator(new KeyCodeCombination(KeyCode.C,KeyCombination.CONTROL_DOWN,KeyCombination.SHIFT_DOWN));
    _menuEdit.getItems().add(_miCopyRow);
    
    _miExportTable = createMenuItem();
    _menuEdit.getItems().add(_miExportTable);
    
    _menuEdit.getItems().add(new SeparatorMenuItem());
    
    _miFind = createMenuItem();
    /* Shift-Ctrl-F for find */
    _miFind.setAccelerator(new KeyCodeCombination(KeyCode.F3,KeyCombination.CONTROL_DOWN,KeyCombination.SHIFT_DOWN));
    _menuEdit.getItems().add(_miFind);
    
    _miFindNext = createMenuItem();
    /* Shift-F3 for find next */
    _miFindNext.setAccelerator(new KeyCodeCombination(KeyCode.F3,KeyCombination.SHIFT_DOWN));
    _menuEdit.getItems().add(_miFindNext);
    
    _miSearch = createMenuItem();
    /* Ctrl-F for search */
    _miSearch.setAccelerator(new KeyCodeCombination(KeyCode.F,KeyCombination.CONTROL_DOWN));
    _menuEdit.getItems().add(_miSearch);
    
    _miSearchNext = createMenuItem();
    /* F3 for search next */ 
    _miSearchNext.setAccelerator(new KeyCodeCombination(KeyCode.F3));
    _menuEdit.getItems().add(_miSearchNext);
    
    getMenus().add(_menuEdit);
    
    _menuTools = new Menu();
    
    _miInstall = createMenuItem();
    _menuTools.getItems().add(_miInstall);

    _miUninstall = createMenuItem();
    _menuTools.getItems().add(_miUninstall);

    _menuTools.getItems().add(new SeparatorMenuItem());
    
    _menuLanguage = new Menu();
    SiardBundle sb = SiardBundle.getSiardBundle();
    String[] asLanguage = sb.getMainLanguages();
    ToggleGroup tgLanguage = new ToggleGroup();
    for (int iLanguage = 0; iLanguage < asLanguage.length; iLanguage++)
    {
      String sLanguage = asLanguage[iLanguage];
      RadioMenuItem rmiLanguage = new RadioMenuItem(sLanguage);
      rmiLanguage.setToggleGroup(tgLanguage);
      rmiLanguage.setUserData(sLanguage);
      boolean bSelected = false;
      if (sLanguage.equals(sb.getLanguage()))
        bSelected = true;
      rmiLanguage.setSelected(bSelected);
      _menuLanguage.getItems().add(rmiLanguage);
    }
    tgLanguage.selectedToggleProperty().addListener(_tcl);
    _menuTools.getItems().add(_menuLanguage);
    
    _menuTools.getItems().add(new SeparatorMenuItem());
    
    _miIntegrity = createMenuItem();
    _menuTools.getItems().add(_miIntegrity);

    _miOptions = createMenuItem();
    _menuTools.getItems().add(_miOptions);

    getMenus().add(_menuTools);
    
    _menuHelp = new Menu();

    _miHelp = createMenuItem();
    _menuHelp.getItems().add(_miHelp);
    
    _menuHelp.getItems().add(new SeparatorMenuItem());
    
    _miInfo = createMenuItem();
    _menuHelp.getItems().add(_miInfo);
    
    getMenus().add(_menuHelp);
    /* set all language-dependent strings */
    refreshLanguage();
  } /* constructor MainMenuBar */
  
  /*------------------------------------------------------------------*/
  /** factory
   * @return freshly created menu bar.
   */
  public static MainMenuBar getMainMenuBar()
  {
    if (_mmb == null)
      _mmb = new MainMenuBar();
    return _mmb;
  } /* getMenuInstance */

} /* class MainMenuBar */
