/*====================================================================== 
SiardGui implements the graphical user interface of SIARD Suite 2.x 
Application: SIARD GUI
Description: SiardGui implements the graphical user interface of 
              SIARD Suite 2.x
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 05.05.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui;

import java.io.*;
import java.util.*;
import javafx.application.*;
import javafx.concurrent.*;
import javafx.geometry.*;
import javafx.event.*;
import javafx.stage.*;
import javafx.scene.*;

import ch.enterag.utils.*;
import ch.enterag.utils.configuration.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.fx.controls.*;
import ch.enterag.utils.fx.dialogs.*;
import ch.enterag.utils.fx.tasks.*;
import ch.enterag.utils.io.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.gui.actions.*;
import ch.admin.bar.siard2.gui.dialogs.*;

/*====================================================================*/
/** SiardGui implements the graphical user interface of SIARD Suite 2.x
 * as a JavaFX application.
 @author Hartwig Thomas
 */
public class SiardGui extends Application
  implements EventHandler<WindowEvent>
{
  /** singleton */
  private static SiardGui _sg = null;
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(SiardGui.class.getName());
  /** return codes */
  private static final int iRETURN_OK = 0;
  private static final int iRETURN_ERROR = 8;

  /** manifest and program properties */
  private static final ManifestAttributes MF = ManifestAttributes.getInstance(SiardGui.class);
  private static final String sAPPLICATION = "SIARD Suite";
  public static String getApplication() { return sAPPLICATION; }
  private static final String sVERSION = MF.getImplementationVersion();
  public static String getVersion() { return sVERSION; }
  private static final String sCOPYRIGHT = MF.getImplementationVendor();
  public static String getCopyright() { return sCOPYRIGHT; }
  private static final Calendar calPUBLICATION = MF.getBuiltDate();
  public static Date getPublicationDate() { return calPUBLICATION.getTime(); }
  private static final List<String> listCREATORS = Arrays.asList(new String[]{
    "Hartwig Thomas"
  });
  public static List<String> getCreators() { return listCREATORS; }
  private static final List<String> listCONTRIBUTORS = Arrays.asList(new String[]{
      "Marcel Büchler",
      "Krystyna Ohnesorge"
    });
  public static List<String> getContributors() { return listCONTRIBUTORS; }
  private static final List<String> listPROVENANCES = Arrays.asList(new String[]{
      "Enter AG, Rüti ZH, Switzerland",
      "Swiss Federal Archives, Berne, Switzerland"
    });
  public static List<String> getProvenances() { return listPROVENANCES; }
  
  /** info */
  public static ProgramInfo _pi = ProgramInfo.getProgramInfo(
    "SIARD Suite",MF.getSpecificationVersion(),
    "SiardGui",MF.getImplementationVersion(),
    "Program to download, view, upload database content and database edit meta data in a .siard file",
    "Swiss Federal Archives, Berne, Switzerland, 2007-2017");

  /** splash stage */
  private Stage _stageSplash = null;
  
  /** primary stage */
  private Stage _stage = null;
  public Stage getStage() { return _stage; }
  public void setStage(Stage stage) { _stage = stage; } // for debugging
  public Scene getScene() { return (_stage == null)? null: _stage.getScene(); }
  /** initial install */
  private boolean _bInitialInstall = false;
  /** initial file open */
  private File _fileInitialOpen = null;

  /** archive */
  private Archive _archive = null;
  public Archive getArchive() { return _archive; }
  public void setArchive(Archive archive) 
  { 
    _archive = archive;
  }
  
  /*------------------------------------------------------------------*/
  /** setTitle sets the title with file name and change indicator */
  public void setTitle()
  {
    String sTitle = getClass().getSimpleName() + " " + sVERSION;
    if (_archive != null)
    {
      sTitle = sTitle + ": " + _archive.getFile().getName();
      if (!_archive.isMetaDataUnchanged())
        sTitle = sTitle + "*";
    }
    _stage.setTitle(sTitle);
  } /* setTitle */
  
  private Cursor _cursor = Cursor.DEFAULT;
  /*------------------------------------------------------------------*/
  /** check, whether a background action is running.
   * @return true, if a background action is running.
   */
  private boolean inAction()
  {
    return getScene().getCursor().equals(Cursor.WAIT);
  } /* inAction */
  
  /*------------------------------------------------------------------*/
  /** indicate start of a modal background action.
   * @param sStatus status to be displayed in status line.
   * @return true, if no other background action is active. 
   */
  public boolean startAction(String sStatus)
  {
    boolean bStartOk = true;
    Scene scene = getScene();
    if (scene != null)
    {
      Cursor cursor = getScene().getCursor();
      if (!inAction())
      {
        _cursor = cursor;
        getScene().setCursor(Cursor.WAIT);
        MainPane.getMainPane().setModal(sStatus);
        bStartOk = true;
      }
    }
    return bStartOk;
  } /* startAction */

  /*------------------------------------------------------------------*/
  /** indicate termination of a modal background action.
   */
  public void terminateAction()
  {
    _il.enter();
    Scene scene = getScene();
    if (scene != null)
    {
      MainPane.getMainPane().setModal(null);
      scene.setCursor(_cursor);
    }
    _il.exit();
  } /* terminateAction */
  
  /*------------------------------------------------------------------*/
  /** compare version of running instance to given version.
   * @param sVersion version to compare running instance to.
   * @return 1 for running version greater than given version (or given version is null),
   *   0 for running version equals given version,
   *   -1 for running version is less than given version (not null).
   */
  static int compareVersion(String sVersion)
  {
    int iCompare = 1;
    if (sVersion != null)
    {
      iCompare = 0;
      String[] asInstalled = sVersion.split("\\.");
      String sRunningVersion = SiardGui.getVersion();
      String[] asRunning = sRunningVersion.split("\\.");
      for (int iVersionComponent = 0; 
        (iCompare == 0) && (iVersionComponent < Math.min(asInstalled.length, asRunning.length)); 
        iVersionComponent++)
        iCompare = Integer.compare(Integer.parseInt(asRunning[iVersionComponent]),Integer.parseInt(asInstalled[iVersionComponent]));
      if (iCompare == 0)
        iCompare = Integer.compare(asRunning.length,asInstalled.length);
    }
    return iCompare;
  } /* compareVersion */

  /*------------------------------------------------------------------*/
  /** default data directory of SIARD.
   * @return default data directory.
   */
  public static File getDefaultDataDirectory()
  {
    return new File(SpecialFolder.getUserDataHome(SiardGui.getApplication()+" "+Archive.sMETA_DATA_VERSION));
  } /* getDefaultDataDirectory */
  
  /*------------------------------------------------------------------*/
  /** checks whether the current instance is running from a JAR installed
   * in the given folder.
   * @param fileInstalled folder where SIARD Suite is installed.
   * @return true, if current instance is running from JAR installed
   *   in the given folder.
   */
  private static boolean isRunningFrom(File fileInstalled)
  {
    boolean bRunningFromFolder = false;
    if (fileInstalled != null)
    {
      bRunningFromFolder = true;
      File fileJar = SpecialFolder.getJarFromClass(SiardGui.class, false);
      if (fileJar.isFile()) // running from a JAR file
      {
        fileJar = fileJar.getParentFile().getParentFile();
        if (!fileJar.getAbsolutePath().equals(fileInstalled.getAbsolutePath()))
          bRunningFromFolder = false;
      }
    }
    return bRunningFromFolder;
  } /* isRunningFrom */
  
  /*------------------------------------------------------------------*/
  /** initialize language, size and state of stage from user data.
   */
  private UserProperties loadProperties()
  {
    _il.enter();
    Rectangle2D rectScreen = FxSizes.getScreenBounds();
    UserProperties up = UserProperties.getUserProperties();
    up.load();
    SiardBundle sb = SiardBundle.getSiardBundle();
    // String sDefaultLanguage = Locale.getDefault().getLanguage();
    String sDefaultLanguage = System.getProperty("user.language");
    String sLanguage = up.getUiLanguage(sDefaultLanguage);
    sb.setLanguage(up.getUiLanguage(sLanguage));
    _stage.setFullScreen(false);
    _stage.setIconified(false);
    if (up.getStageMaximized(false) || up.getStageMinimized(false))
    {
      _stage.setX(0.15*rectScreen.getWidth());
      _stage.setY(0.15*rectScreen.getHeight());
      _stage.setWidth(0.7*rectScreen.getWidth());
      _stage.setHeight(0.7*rectScreen.getHeight());
    }
    else
    {
      _stage.setX(up.getStageX(0.15*rectScreen.getWidth()));
      _stage.setY(up.getStageY(0.15*rectScreen.getHeight()));
      _stage.setWidth(up.getStageWidth(0.7*rectScreen.getWidth()));
      _stage.setHeight(up.getStageHeight(0.7*rectScreen.getHeight()));
    }
    System.setProperty(FS.sUSE_NATIVE_PROPERTY, String.valueOf(up.getFileChooserNative(false)));
    _il.exit(up);
    return up;
  } /* loadProperties */

  /*------------------------------------------------------------------*/
  /** save language, size and state of stage to user data.
   */
  private void storeProperties()
  {
    _il.enter();
    /* store MRU connections in user properties */
    MruConnection.getMruConnection(true).store();
    MruConnection.getMruConnection(false).store();
    /* store MRU files in user properties */
    MruFile.getMruFile().store();
    /* save user properties */
    UserProperties up = UserProperties.getUserProperties();
    _il.event("Properties for version "+up.getInstalledVersion(null)+" are being stored to \""+up.getFile().getAbsolutePath()+"\"");
    SiardBundle sb = SiardBundle.getSiardBundle();
    up.setUiLanguage(sb.getLanguage());
    up.setStageMaximized(_stage.isFullScreen());
    up.setStageMinimized(_stage.isIconified());
    up.setStageX(_stage.getX());
    up.setStageY(_stage.getY());
    up.setStageWidth(_stage.getWidth());
    up.setStageHeight(_stage.getHeight());
    up.store();
    _il.exit();
  } /* storeProperties */

  /*------------------------------------------------------------------*/
  /** setting the language to a different values changes the whole user
   *  interface.
   * @param sLanguage language code (ISO 639-1)
   */
  public void setLanguage(String sLanguage)
  {
    _il.enter();
    UserProperties up = UserProperties.getUserProperties();
    SiardBundle sb = SiardBundle.getSiardBundle();
    String sLanguagePrevious = up.getUiLanguage(null); 
    if (!sLanguage.equals(sLanguagePrevious))
    {
      String[] asLanguage = sb.getMainLanguages();
      for (int iLanguage = 0; iLanguage < asLanguage.length; iLanguage++)
      {
        if (asLanguage[iLanguage].equals(sLanguage))
        {
          sb.setLanguage(sLanguage);
          up.setUiLanguage(sLanguage);
        }
      }
      /* refresh main pane with new language */
      MainPane.getMainPane().refreshLanguage();
    }
    _il.exit();
  } /* setLanguage */
  
  /*------------------------------------------------------------------*/
  /** download action
   */
  public void download(String sConnectionUrl, String sDbUser)
  {
    UploadDownloadAction.newUploadDownloadAction().download(sConnectionUrl, sDbUser);
    if (_archive != null)
    {
      setTitle();
      MainMenuBar.getMainMenuBar().restrict();
      MainPane.getMainPane().setArchive();
    }
  } /* download */
  
  /*------------------------------------------------------------------*/
  /** download action
   */
  public void download()
  {
    download(null, null);
  } /* download */
  
  /*------------------------------------------------------------------*/
  /** upload action
   */
  public void upload(String sConnectionUrl, String sDbUser)
  {
    UploadDownloadAction.newUploadDownloadAction().upload(sConnectionUrl, sDbUser, _archive);
    MainMenuBar.getMainMenuBar().restrict();
  } /* upload */
  
  /*------------------------------------------------------------------*/
  /** upload action
   */
  public void upload()
  {
    upload(null,null);
  } /* upload */
  
  /*------------------------------------------------------------------*/
  /** open action
   */
  public void openArchive(String sFile)
  {
    OpenSaveAction.newOpenSaveAction().open(sFile);
    setTitle();
    MainMenuBar.getMainMenuBar().restrict();
    MainPane.getMainPane().setArchive();
  } /* openArchive */
  
  /*------------------------------------------------------------------*/
  /** open action
   */
  public void openArchive()
  {
    openArchive(null);
  } /* openArchive */
  
  /*------------------------------------------------------------------*/
  /** save action
   */
  public void save()
  {
    OpenSaveAction.newOpenSaveAction().save();
    setTitle();
    MainMenuBar.getMainMenuBar().restrict();
  } /* save */
  
  /*------------------------------------------------------------------*/
  /** display meta data action
   */
  public void displayMetaData()
  {
    // force apply/reset on unsaved changes to meta data
    MainPane.getMainPane().refreshLanguage();
    MetaDataAction.newMetaDataAction().displayMetaData();
  } /* displayMetaData */

  /*------------------------------------------------------------------*/
  /** augment meta data with content from template.
   */
  public void augmentMetaData()
  {
    // force apply/reset on unsaved changes to meta data
    MainPane.getMainPane().refreshLanguage();
    MetaDataAction.newMetaDataAction().augmentMetaData();
    setTitle();
    MainMenuBar.getMainMenuBar().restrict();
  } /* augmentMetaData */
  
  /*------------------------------------------------------------------*/
  /** close action
   */
  public void closeArchive()
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    try
    {
      // force apply/reset on unsaved changes to meta data
      MainPane.getMainPane().refreshLanguage();
      if (_archive.isValid())
      {
        if (!_archive.isMetaDataUnchanged())
        {
          int iResult = MB.show(getStage(), sb.getCloseTitle(),
            sb.getCloseSaveQuestion(_archive.getFile()),
            sb.getYes(), sb.getNo());
          if (iResult == MB.iRESULT_SUCCESS)
            _archive.saveMetaData();
        }
      }
      else
      {
        int iResult = MB.show(getStage(), sb.getCloseTitle(),
            sb.getCloseExportMetaDataQuestion(),
            sb.getYes(), sb.getNo());
          if (iResult == MB.iRESULT_SUCCESS)
          {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            _archive.exportMetaData(baos);
            baos.close();
            String sMetaDataXml = new String(baos.toByteArray());
            MetaDataAction.newMetaDataAction().saveMetaDataXml(getStage(), _archive, sMetaDataXml);
          }
      }
      _archive.close();
    }
    catch(IOException ie)
    {
      MB.show(getStage(), sb.getCloseErrorTitle(),
        sb.getCloseErrorMessage(_archive.getFile(), ie),
        sb.getOk(), null);
    }
    _archive = null;
    setTitle();
    MainMenuBar.getMainMenuBar().restrict();
    MainPane.getMainPane().setArchive();
  } /* closeArchive */
  
  /*------------------------------------------------------------------*/
  /** exit action
   */
  public void exit()
  {
    if (canClose())
      _stage.close(); // internal call generates no close request
  } /* exit */
  
  /*------------------------------------------------------------------*/
  /** copyAll action
   */
  public void copyAll()
  {
    ObjectListTableView oltv = MainPane.getMainPane().getDisplayedTableView();
    if (oltv != null)
      oltv.copyTable();
  } /* copyAll */
  
  /*------------------------------------------------------------------*/
  /** copyRow action
   */
  public void copyRow()
  {
    MainPane mp = MainPane.getMainPane();
    ObjectListTableView oltv = mp.getDisplayedTableView();
    if (oltv != null)
    {
      int iSelectedRow = mp.getSelectedTableRow();
      oltv.copyRow(iSelectedRow);
    }
  } /* copyRow */
  
  /*------------------------------------------------------------------*/
  /** exportTable action
   */
  public void exportTable()
  {
    Table table = MainPane.getMainPane().getSelectedTable();
    if (table != null)
      ExportAction.newExportAction(table).exportAsHtml();
  } /* exportTable */
  
  /*------------------------------------------------------------------*/
  /** find (meta data) action
   */
  public void find()
  {
    if (FindAction.newFindAction().find())
      findNext();
  } /* find */
  
  /*------------------------------------------------------------------*/
  /** findNext (meta data) action
   */
  public void findNext()
  {
    MetaSearch ms = null;
    SiardBundle sb = SiardBundle.getSiardBundle();
    DU du = DU.getInstance(sb.getLanguage(),sb.getDateFormat());
    String sFindString = getArchive().getMetaData().getFindString();
    try { ms = getArchive().getMetaData().findNext(du); }
    catch(IOException ie) { _il.exception(ie); }
    if (ms != null)
      MainPane.getMainPane().selectFound(ms);
    else
      MB.show(getStage(),
        sb.getFindTitle(),
        sb.getFindEndMessage(sFindString), 
        sb.getOk(), null);
    MainMenuBar.getMainMenuBar().restrict();
  } /* findNext */
  
  /*------------------------------------------------------------------*/
  /** search (current table primary data) action
   */
  public void search()
  {
    if (SearchAction.newSearchAction().search(MainPane.getMainPane().getSelectedTable()))
      searchNext();
  } /* search */
  
  /*------------------------------------------------------------------*/
  /** searchNext (table primary data) action
   */
  public void searchNext()
  {
    SearchAction.newSearchAction().searchNext(MainPane.getMainPane().getSelectedTable());
  } /* searchNext */
  
  /*------------------------------------------------------------------*/
  /** install SIARD Suite
   */
  public void install()
  {
    InstallUninstallHandler.getInstallUninstallHandler().install();
  } /* install */
  
  /*------------------------------------------------------------------*/
  /** uninstall SIARD Suite
   */
  public void uninstall(boolean bQuiet)
  {
    InstallUninstallHandler.getInstallUninstallHandler().uninstall(bQuiet);
  } /* uninstall */
  
  /*------------------------------------------------------------------*/
  /** checks integrity
   */
  public void integrity()
  {
    if (_archive != null)
    {
      try
      {
        SiardBundle sb = SiardBundle.getSiardBundle();
        String sResultMessage = null;
        if (_archive.isPrimaryDataUnchanged())
          sResultMessage = sb.getIntegrityCheckPassMessage(_archive.getFile()); 
        else
          sResultMessage = sb.getIntegrityCheckFailMessage(_archive.getFile()); 
        MB.show(getStage(),
          sb.getIntegrityCheckTitle(_archive.getFile()),
          sResultMessage, 
          sb.getOk(), null);
      }
      catch(IOException ie) { _il.exception(ie); }
    }
  } /* integrity */
  
  /*------------------------------------------------------------------*/
  /** sets options (user properties)
   */
  public void options()
  {
    OptionDialog.showOptionDialog(getStage());
  } /* options */
  
  /*------------------------------------------------------------------*/
  /** display help HTML
   */
  public void help()
  {
    HelpDialog.showHelpDialog(getStage(),"index.html");
  } /* help */
  
  /*------------------------------------------------------------------*/
  /** display info dialog
   */
  public void info()
  {
    InfoDialog.showInfoDialog(getStage());
  } /* info */
  
  /*------------------------------------------------------------------*/
  /** show object in the details pane.
   * @param oMetaData meta data or RecordExtract object.
   * @param clsTableData class of table data to show.
   */
  public void showDetails(Object oMetaData, Class<?> clsTableData)
  {
    MainPane.getMainPane().showMetaData(oMetaData,clsTableData);
  } /* showDetails */
  
  /*------------------------------------------------------------------*/
  /** collapse currently selected record extract and its parents up to 
   * the rows entry of the table tree item.
   */
  public void collapseToRows()
  {
    MainPane.getMainPane().collapseToRows();
  } /* collapseToRows */

  /*------------------------------------------------------------------*/
  /** canClose calls the handler of the CloseRequest in order to treat
   * internal close attempts like external ones.
   * @return true, if closing the main stage is OK.
   */
  private boolean canClose()
  {
    _il.enter();
    boolean bCanClose = false;
    if (!inAction())
    {
      if (_archive != null)
        closeArchive();
      // save user properties if running installed version and instance
      // or if no installed version existed.
      UserProperties up = UserProperties.getUserProperties();
      _il.event("InstalledVersion: "+up.getInstalledVersion(null));
      _il.event("InstalledPath: "+up.getInstalledPath(null));
      if ((compareVersion(up.getInstalledVersion(null)) == 0) && 
           isRunningFrom(up.getInstalledPath(null)))
        storeProperties();
      bCanClose = true;
    }
    _il.exit(String.valueOf(bCanClose));
    return bCanClose;
  } /* canClose */
  
  /*------------------------------------------------------------------*/
  /** handle close request.
   */
  @Override
  public void handle(WindowEvent we)
  {
    if (!canClose())
      we.consume();
  } /* handle */
  
  /*------------------------------------------------------------------*/
  /** make sure, all changes are saved or discarded.
   */
  @Override
  public void stop()
    throws Exception
  {
    _il.enter();
    super.stop();
    _il.exit();
  } /* stop */

  /*------------------------------------------------------------------*/
  /** compare current version with installed version and offer installation
   * if the current version is new or more recent. 
   * @return
   */
  public boolean checkInstall()
  {
    _il.enter();
    boolean bInstall = false;
    UserProperties up = UserProperties.getUserProperties();
    SiardBundle sb = SiardBundle.getSiardBundle();
    String sInstalledVersion = up.getInstalledVersion(null);
    int iResult = 0; // proceed
    int iCompare = compareVersion(sInstalledVersion);
    if (iCompare > 0)
    {
      if (sInstalledVersion == null)
      {
        // display message that there is no version installed
        iResult = MB.show(getStage(), sb.getInstalledNoneTitle(),
          sb.getInstalledNoneMessage(up.getFile(), getVersion()),
          sb.getYes(), sb.getNo());
      }
      else
      {
        // display message, that an older version is installed
        iResult = MB.show(getStage(), sb.getInstalledOlderTitle(),
          sb.getInstalledOlderMessage(sInstalledVersion, up.getFile(), up.getInstalledPath(null), getVersion()), 
          sb.getYes(),sb.getNo());
      }
    }
    else if (iCompare < 0)
    {
      // display message, that a more recent version is installed
      MB.show(getStage(), sb.getInstalledNewerTitle(),
        sb.getInstalledNewerMessage(sInstalledVersion, up.getFile(), up.getInstalledPath(null), getVersion()), 
        sb.getOk(), null);
    }
    if (iResult != 0)
      bInstall = true;
    _il.exit(String.valueOf(bInstall));
    return bInstall;
  } /* checkInstall */
  
  /*------------------------------------------------------------------*/
  /** start SIARD GUI.
   * @param stage primary stage.
   */
  @Override
  public void start(Stage stage) throws Exception
  {
    _il.enter(stage);
    _stage = stage;
    // load the user properties and display an installation note, if appropriate
    UserProperties up = loadProperties();
    _stage.setOnCloseRequest(this);
    _bInitialInstall = checkInstall();
    _stageSplash = new Stage(StageStyle.UNDECORATED);
    Scene scene = new Scene(SplashPane.newSplashPane());
    _stageSplash.setScene(scene);
    _stageSplash.toFront();
    _stageSplash.show();
    int iSplashMs = up.getSplashMs(1000);
    if (_bInitialInstall)
      iSplashMs = 0;
    SleeperTask.runSleeperTask(iSplashMs,new EventHandler<WorkerStateEvent>() 
    {
      /*----------------------------------------------------------------*/
      /** this is called after the splash panel has terminated.
       * It finishes the start() activities.
       */
      @Override
      public void handle(WorkerStateEvent wse)
      {
        _il.enter(wse);
        _stageSplash.close();
        setTitle();
        if (_bInitialInstall)
          install();
        MainMenuBar.getMainMenuBar().restrict();
        _stage.initStyle(StageStyle.DECORATED);
        Scene scene = new Scene(MainPane.getMainPane());
        scene.setCursor(Cursor.DEFAULT);
        _stage.setScene(scene);
        _stage.toFront();
        _stage.show();
        if (_fileInitialOpen != null)
          openArchive(_fileInitialOpen.getAbsolutePath());
        _il.exit();
      }
    });
    _il.exit();
  } /* start */

  /*------------------------------------------------------------------*/
  /** init is called by launcher before start.
   * initialize open directory.
   */
  @Override
  public void init()
    throws Exception
  {
    super.init();
    _il.enter();
    List<String> listParameters = getParameters().getRaw();
    if (listParameters.size() > 0)
      _fileInitialOpen = new File(listParameters.get(0));
    _sg = this;
    _il.exit();
  } /* init */

  /*------------------------------------------------------------------*/
  /** singleton access
   * @return SiardGui singleton.
   */
  public static SiardGui getSiardGui()
  {
    return _sg;
  } /* getSiardGui */
  
  /*------------------------------------------------------------------*/
  /** start JavaFX application.
   * @param args optional file name of SIARD file to be opened.
   */
  public static void main(String[] args)
  {
    int iReturn = iRETURN_ERROR;
    for (int i = 0; i < args.length; i++)
      _il.info("arg["+String.valueOf(i)+"]: "+args[i]);
    _il.systemProperties();
    _il.info("JavaFX runtime version: "+com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());
    try
    { 
      launch(args);
      iReturn = iRETURN_OK;
    }
    catch (Exception e) { System.err.println(e.getClass().getName()+": "+e.getMessage()); }
    System.exit(iReturn);
  } /* main */

} /* class SiardGui */
