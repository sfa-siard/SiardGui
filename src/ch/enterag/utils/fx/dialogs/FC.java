/*======================================================================
FC replaces the rather deficient "native" FileChooser and DirectoryChooser. 
Application : JavaFX Utilities
Description : FC replaces the rather deficient "native" FileChooser and 
  DirectoryChooser by an all-JAVA implementation which can be localized,
  because all strings are parametrized. 
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland
Created    : 21.12.2015, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.fx.dialogs;

import java.io.*;
import java.text.*;
import java.util.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.fx.controls.*;

/*====================================================================*/
/** FC replaces the rather deficient "native" FileChooser and 
  DirectoryChooser by an all-JAVA implementation which can be localized,
  because all strings are parametrized.
 * @author Hartwig Thomas
 */
public class FC
  extends ScrollableDialog 
  implements EventHandler<ActionEvent>
{
  private static final double dINNER_PADDING = 10.0;
  private static final double dVSPACING = 10.0;
  private static final double dHSPACING = 10.0;
  private static final int iTREE_WIDTH = 16;
  private static final int iTREE_HEIGHT = 16;
  private static final int iLIST_WIDTH = 16;
  private static final int  iLIST_HEIGHT = 16;
  private boolean _bExisting = false;
  private boolean _bFolder = false;
  private boolean _bIncludeHidden = false;
  private String _sOverwriteQuery = null;
  private String _sYes = null;
  private String _sNo = null;
  private VBox _vbox = null;
  private TreeView<File> _tv = null;
  private ListView<File> _lv = null;
  private Label _lblPath = null;
  private TextField _tfName = null;
  private ComboBox<ExtensionFilter> _cbExtensions = null;
  private Button _btnOk = null;
  private Button _btnCancel = null;
  private File _fileResult = null;
  private File getResult() { return _fileResult; }

  /*------------------------------------------------------------------*/
  /** Handle the fact, that subfolders or files unter hidden folders
   * may not be hidden.
   * @param file file to be examined.
   * @return true, if it is hidden by a parent.
   */
  private static boolean isHidden(File file)
  {
    boolean bHidden = false;
    for (; 
        (!bHidden) && (file != null); 
        file = file.getParentFile())
    {
      if (file.exists() && file.isHidden())
        bHidden = true;
    }
    return bHidden;
  } /* isHidden */
  
  /*------------------------------------------------------------------*/
  /** Handle the fact, that file roots in Windows incorrectly are not 
   * marked as directories.
   * @param file file to be examined.
   * @return true, if it is a folder.
   */
  private static boolean isFolder(File file)
  {
    boolean bIsFolder = true;
    if ((file != null) && (file.getName().length() > 0))
      bIsFolder = file.isDirectory();
    return bIsFolder;
  } /* isFolder */
  
  /*------------------------------------------------------------------*/
  /** create the label for displaying the message.
   * @return message label.
   */
  private Label createMessage(String sMessage)
  {
    Label lbl = new Label(sMessage);
    lbl.setWrapText(true);
    lbl.maxWidthProperty().bind(_vbox.widthProperty());
    lbl.minWidthProperty().bind(_vbox.widthProperty());
    return lbl;
  } /* createMessage */
  
  /*==================================================================*/
  /** selection change in TreeView displays new content in ListView and
   * changes Path.
   */
  private class FileTreeItemListener
    implements ChangeListener<TreeItem<File>> // handles changing selection
  {
    @Override
    public void changed(ObservableValue<? extends TreeItem<File>> ovfti,
        TreeItem<File> ftiOld, TreeItem<File> ftiNew)
    {
      ExtensionFilter ef = null;
      if (_cbExtensions != null)
        ef = _cbExtensions.getSelectionModel().getSelectedItem();
      fillList(ftiNew.getValue(), ef);
      File file = ftiNew.getValue();
      if (_lblPath != null)
      {
        if (file != null)
          _lblPath.setText(file.getAbsolutePath());
        else
          _lblPath.setText("/");
      }
    }
  } /* class FileTreeItemListener */
  /*==================================================================*/
  private FileTreeItemListener _ftil = new FileTreeItemListener();

  /*------------------------------------------------------------------*/
  /** determine, whether a file needs to be hidden.
   * Handle the fact, that file roots in Windows incorrectly are marked
   * as hidden.
   * @param file file to be examined.
   * @return true, if it must be hidden.
   */
  private boolean mustBeHidden(File file)
  {
    boolean bMustBeHidden = false;
    if (!_bIncludeHidden)
    {
      if (file.getName().length() != 0)
        bMustBeHidden = file.isHidden();
    }
    return bMustBeHidden;
  } /* mustBeHidden */

  /*------------------------------------------------------------------*/
  /** compute the list of not-to-be-hidden child folders from a parent 
   * folder.
   * @param fileParent parentFolder
   * @return list of not-to-be-hidden child folders.
   */
  private List<File> getChildFolders(File fileParent)
  {
    List<File> listChildFolders = new ArrayList<File>();
    File[] afile = null;
    if (fileParent == null)
    {
      afile = File.listRoots();
      // LINUX has only "/" as root
      // Windows as A:\, C:\, ... as roots
      if ((afile.length == 1) && (afile[0].getAbsolutePath().equals("/")))
        afile = afile[0].listFiles();
    }
    else
      afile = fileParent.listFiles();
    if (afile != null)
    {
      for (int iFile = 0; iFile < afile.length; iFile++)
      {
        File fileChild = afile[iFile];
        if ((!mustBeHidden(fileChild)) &&
            isFolder(fileChild))
          listChildFolders.add(fileChild);
      }
    }
    return listChildFolders;
  } /* getChildFolders */
  
  /*==================================================================*/
  /** class FileTreeItem implements a dynamic tree item with folders as items.
   */
  private class FileTreeItem
    extends DynamicTreeItem<File>
  {
    public FileTreeItem(File file)
    {
      super(file);
      setExpanded(false);
    } /* constructor */
    @Override
    protected void addChildren()
    {
      List<File> listFolders = getChildFolders((File)getValue());
      for (Iterator<File> iterFolder = listFolders.iterator(); iterFolder.hasNext(); )
      {
        File fileChild = iterFolder.next();
        // for existing folder selection only parents of selectable folders
        // need to be included in tree.
        if ((!_bFolder) || (!_bExisting) || (getChildFolders(fileChild).size() > 0))
          getChildren().add(new FileTreeItem(fileChild));
      }
    }
  } /* class FileTreeItem */
  /*==================================================================*/

  /*------------------------------------------------------------------*/
  /** expand the parent file tree item to the parent of the initial 
   * file recursively.
   * @param fileParent parent of initial file.
   * @param ftiParent parent file tree item.
   */
  private FileTreeItem expandInitial(File fileParent, FileTreeItem ftiParent)
  {
    FileTreeItem ftiExpanded = ftiParent;
    if ((ftiParent != null) && (fileParent != null))
    {
      ftiExpanded = ftiParent;
      ftiParent.setExpanded(true);
      for (int iChild = 0; iChild < ftiParent.getChildren().size(); iChild++)
      {
        FileTreeItem ftiChild = (FileTreeItem)ftiParent.getChildren().get(iChild);
        File file = ftiChild.getValue();
        if (fileParent.getAbsolutePath().startsWith(file.getAbsolutePath()))
          ftiExpanded = expandInitial(fileParent,ftiChild);
      }
    }
    return ftiExpanded;
  } /* expandInitial */

  /*==================================================================*/
  /** file tree cell displays the file items.
   */
  private class FileTreeCell
    extends TreeCell<File>
  {
    public FileTreeCell()
    {
      super();
    }
    @Override
    public void updateItem(File fileItem, boolean bEmpty)
    {
      super.updateItem(fileItem, bEmpty); // is needed for disclosure triangles
      String sName = "";
      if (!bEmpty)
      {
        sName = "/";
        if (fileItem != null)
        {
          sName = fileItem.getName();
          if (sName.length() == 0)
            sName = fileItem.getPath();
        }
      }
      setText(sName);
    } /* updateItem */
  } /* class FileTreeCell */
  /*==================================================================*/
  
  /*------------------------------------------------------------------*/
  /** create the tree view and open it to the last existing parent of 
   * the initial file/folder.
   * @param fileInitial initial file/folder.
   * @return created tree view.
   */
  private TreeView<File> createTreeView(File fileInitial)
  {
    _tv = new TreeView<File>();
    _tv.setEditable(false);
    _tv.setCellFactory(new Callback<TreeView<File>,TreeCell<File>>()
      {
        @Override public TreeCell<File> call(TreeView<File> tv)
        {
          return new FileTreeCell();
        }
      });
    _tv.getSelectionModel().selectedItemProperty().addListener(_ftil);
    _tv.setMinWidth(FxSizes.fromEms(iTREE_WIDTH));
    _tv.setMinHeight(FxSizes.fromEms(iTREE_HEIGHT));
    FileTreeItem ftiRoot = new FileTreeItem(null);
    ftiRoot.addChildren();
    _tv.setRoot(ftiRoot);
    FileTreeItem ftiParent = ftiRoot;
    if (fileInitial != null)
      ftiParent = expandInitial(fileInitial.getParentFile(),ftiRoot);
    _tv.getSelectionModel().select(ftiParent);
    _tv.scrollTo(_tv.getSelectionModel().getSelectedIndex());
    return _tv;
  } /* createTreeView */
  
  /*==================================================================*/
  /** selection change in ListView displays new content in text field.
   */
  private class FileListener
    implements ChangeListener<File> // handles changing selection
  {
    @Override
    public void changed(ObservableValue<? extends File> ovfile,
        File fileOld, File fileNew)
    {
      if (_tfName != null)
      {
        if (fileNew != null)
        {
          String sName = fileNew.getName();
          if (sName.length() == 0)
            sName = fileNew.getPath();
          _tfName.setText(sName);
        }
        else
          _tfName.setText("");
      }
    } /* changed */
  } /* class FileListener */
  /*==================================================================*/
  private FileListener _fl = new FileListener();

  /*------------------------------------------------------------------*/
  /** fill the list view with the files/folders under the parent folder.
   * @param fileParent parent folder
   */
  private void fillList(File fileParent, ExtensionFilter ef)
  {
    if (_lv != null)
    {
      _lv.setUserData(fileParent);
      _lv.getItems().clear();
      File[] afile = null;
      if (fileParent == null)
        afile = File.listRoots();
      else
        afile = fileParent.listFiles();
      if (afile != null)
      {
        for (int iFile = 0; iFile < afile.length; iFile++)
        {
          File file = afile[iFile];
          if (!mustBeHidden(file))
          {
            if (_bFolder && isFolder(file))
              _lv.getItems().add(file);
            else if ((!_bFolder) && (file.isFile()))
            {
              if ((ef == null) ||
                  (ef.getExtension() == null) ||
                file.getName().endsWith("."+ef.getExtension()))
                _lv.getItems().add(file);
            }
          }
        }
      }
    }
  } /* fillList */

  /*------------------------------------------------------------------*/
  /** get the extension filter in the list, that matches the initial file.
   * @param fileInitial initial file.
   * @param listExtensions list of extension filters.
   * @return matching filter or null.
   */
  private ExtensionFilter getExtensionFilter(File fileInitial, List<ExtensionFilter> listExtensions)
  {
    ExtensionFilter ef = null;
    if ((fileInitial != null) && (listExtensions != null))
    {
      for (int iExtension = 0; (ef == null) && (iExtension < listExtensions.size()); iExtension++)
      {
        ExtensionFilter efTry = listExtensions.get(iExtension);
        if (efTry.getExtension() != null)
        {
          if (fileInitial.getName().endsWith("."+efTry.getExtension()))
            ef = efTry;
        }
        else
          ef = efTry;
      }
    }
    return ef;
  } /* getExtensionFilter */
  
  /*==================================================================*/
  /** FileListCell displays a file in the list view.
   */
  private class FileListCell
    extends ListCell<File>
  {
    public FileListCell()
    {
      super();
    }
    @Override
    public void updateItem(File fileItem, boolean bEmpty)
    {
      super.updateItem(fileItem, bEmpty);
      String sName = "";
      if (!bEmpty)
      {
        sName = fileItem.getName();
        if (sName.length() == 0)
          sName = fileItem.getPath();
      }
      setText(sName);
    }
  } /* FileListCell */
  /*==================================================================*/
  
  /*------------------------------------------------------------------*/
  /** create the list view containing the choosable files.
   * @param fileInitial initial file.
   * @return list view.
   */
  private ListView<File> createListView(File fileInitial, List<ExtensionFilter> listExtensions)
  {
    _lv = new ListView<File>();
    ObservableList<File> data = FXCollections.observableArrayList();
    _lv.setItems(data);
    _lv.setEditable(false);
    _lv.setCellFactory(new Callback<ListView<File>,ListCell<File>>()
      {
        @Override public ListCell<File> call(ListView<File> lv)
        {
          return new FileListCell();
        }
      });
    _lv.getSelectionModel().selectedItemProperty().addListener(_fl);
    _lv.setMinWidth(FxSizes.fromEms(iLIST_WIDTH));
    _lv.setMinHeight(FxSizes.fromEms(iLIST_HEIGHT));
    if (fileInitial != null)
    {
      fillList(fileInitial.getParentFile(),getExtensionFilter(fileInitial,listExtensions));
      _lv.getSelectionModel().select(fileInitial);
      _lv.scrollTo(fileInitial);
    }
    return _lv;
  } /* createListView */
  
  /*------------------------------------------------------------------*/
  /** create a HBox with the folder tree and the list of names.
   * @param fileInitial initial file/folder.
   * @return HBox.
   */
  private HBox createTreeAndList(File fileInitial, List<ExtensionFilter> listExtensions)
  {
    HBox hbox = new HBox();
    hbox.setSpacing(dHSPACING);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    _tv = createTreeView(fileInitial);
    hbox.getChildren().add(_tv);
    _lv = createListView(fileInitial, listExtensions);
    hbox.getChildren().add(_lv);
    return hbox;
  } /* createTreeAndList */
  
  /*==================================================================*/
  /** selection change in ComboBox displays new content in list view
   */
  private class ExtensionFilterListener
    implements ChangeListener<ExtensionFilter> // handles changing selection
  {
    @Override
    public void changed(ObservableValue<? extends ExtensionFilter> ovef,
        ExtensionFilter efOld, ExtensionFilter efNew)
    {
      if (_lv != null)
        fillList((File)_lv.getUserData(),efNew);
    } /* changed */
  } /* class ExtensionFilterListener */
  /*==================================================================*/
  private ExtensionFilterListener _efl = new ExtensionFilterListener();
  
  /*==================================================================*/
  private class StringListener
    implements ChangeListener<String>
  {
    @Override
    public void changed(ObservableValue<? extends String> ovs,
        String sOld, String sNew)
    {
      if (_btnOk != null)
      {
        boolean bDisable = true;
        if (sNew.length() > 0)
        {
          // if the root is selected and the text does not start with a 
          // root folder then disable
          if (_tv.getSelectionModel().getSelectedItem().getValue() == null)
          {
            File[] afile = File.listRoots();
            for (int iFile = 0; iFile < afile.length; iFile++)
            {
              if (sNew.startsWith(afile[iFile].getAbsolutePath()))
                bDisable = false;
            }
          }
          else
            bDisable = false;
        }
        _btnOk.setDisable(bDisable);
      }
    } /* changed */
  } /* class StringListener */
  /*==================================================================*/
  private StringListener _sl = new StringListener();

  /*------------------------------------------------------------------*/
  /** create the HBox indicating the currently selected folder.
   * @param fileInitial initial file/folder.
   * @param sPathLabel text for label for path.
   * @return
   */
  private HBox createPath(File fileInitial, String sPathLabel)
  {
    HBox hbox = new HBox();
    hbox.setSpacing(dHSPACING);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    String sName = "";
    if (fileInitial != null)
      sName = fileInitial.getParentFile().getAbsolutePath();
    _lblPath = new Label(sName);
    _lblPath.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
    HBox.setHgrow(_lblPath, Priority.ALWAYS);
    Label lblPath = new Label(sPathLabel);
    lblPath.setMinWidth(FxSizes.getTextWidth(sPathLabel));
    lblPath.setLabelFor(_lblPath);
    hbox.getChildren().add(lblPath);
    hbox.getChildren().add(_lblPath);
    return hbox;
  } /* createPath */
  
  /*------------------------------------------------------------------*/
  /** create the HBox containing the text field for the name and the
   * combo box for the filter.
   * @param sNameLabel text for label for text field.
   * @param fileInitial initial file/folder.
   * @param listExtensions list of extension filters.
   * @return resulting HBox.
   */
  private HBox createNameAndFilter(String sNameLabel, File fileInitial, List<ExtensionFilter> listExtensions)
  {
    HBox hbox = new HBox();
    hbox.setSpacing(dHSPACING);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    String sName = "";
    if ((fileInitial != null) && (!fileInitial.getName().startsWith("*")))
    {
      sName = fileInitial.getName();
      if (sName.length() == 0)
        sName = fileInitial.getPath();
    }
    _tfName = new TextField(sName);
    _tfName.setEditable(!_bExisting);
    _tfName.textProperty().addListener(_sl);
    HBox.setHgrow(_tfName, Priority.ALWAYS);
    Label lblName = new Label(sNameLabel);
    lblName.setMinWidth(FxSizes.getTextWidth(sNameLabel));
    lblName.setLabelFor(_tfName);
    hbox.getChildren().add(lblName);
    hbox.getChildren().add(_tfName);
    if (listExtensions != null)
    {
      _cbExtensions = new ComboBox<ExtensionFilter>();
      ObservableList<ExtensionFilter> items = FXCollections.observableArrayList();
      _cbExtensions.setItems(items);
      for (int iExtension = 0; iExtension < listExtensions.size(); iExtension++)
        _cbExtensions.getItems().add(listExtensions.get(iExtension));
      if (fileInitial != null)
        _cbExtensions.getSelectionModel().select(getExtensionFilter(fileInitial,listExtensions));
      else
        _cbExtensions.getSelectionModel().select(0);
      _cbExtensions.getSelectionModel().selectedItemProperty().addListener(_efl);
      hbox.getChildren().add(_cbExtensions);
    }
    return hbox;
  } /* createNameAndFilter */

  /*------------------------------------------------------------------*/
  /** create a HBox with OK and Cancel button.
   * @param sOk text on OK button.
   * @param sCancel text on Cancel button.
   * @return HBox.
   */
  private HBox createButtons(File fileInitial, String sOk, String sCancel)
  {
    HBox hbox = new HBox();
    hbox.setSpacing(dHSPACING);
    hbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    hbox.setAlignment(Pos.BASELINE_RIGHT);
    /* buttons and their width */
    _btnOk = new Button(sOk);
    _btnOk.setDefaultButton(true);
    _btnOk.setOnAction(this);
    _btnOk.setDisable((fileInitial == null) || (fileInitial.getName().startsWith("*"))); // disable it, if text is empty
    hbox.getChildren().add(_btnOk);
    _btnCancel = new Button(sCancel);
    _btnCancel.setCancelButton(true); // associate it with ESC key
    _btnCancel.setOnAction(this);
    hbox.getChildren().add(_btnCancel);
    return hbox;
  } /* createButtons */
  
  /*------------------------------------------------------------------*/
  /** main VBox consists of message area,
   * tree and list area.
   * file/folder name text box and filter combo box.
   * OK, Cancel buttons.
   * @param sMessage message to be displayed.
   * @param fileInitial initial file/folder name.
   * @param sPathLabel text for label for path.
   * @param sNameLabel label for name text field.
   * @param listExtensions list of extension filters. 
   * @param sOk text on OK button.
   * @param sCancel text on cancel button.
   * @return main VBox.
   */
  private VBox createVBox(String sMessage, 
      File fileInitial,
      String sPathLabel, String sNameLabel, 
      List<ExtensionFilter> listExtensions,
      String sOk,String sCancel)
  {
    _vbox = new VBox();
    _vbox.setPadding(new Insets(dINNER_PADDING));
    _vbox.setSpacing(dVSPACING);
    _vbox.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    if (sMessage != null)
      _vbox.getChildren().add(createMessage(sMessage));
    _vbox.getChildren().add(createTreeAndList(fileInitial,listExtensions));
    _vbox.getChildren().add(createPath(fileInitial,sPathLabel));
    _vbox.getChildren().add(createNameAndFilter(sNameLabel,fileInitial,listExtensions));
    _vbox.getChildren().add(createButtons(fileInitial,sOk,sCancel));
    _vbox.setMinWidth(FxSizes.getNodeWidth(_vbox));
    _vbox.setMinHeight(FxSizes.getNodeHeight(_vbox));
    return _vbox;
  } /* createVBox */
  
  /*------------------------------------------------------------------*/
  /** constructor creates the dialog.
   * @param stageOwner owner stage or null.
   * @param bExisting true, if only existing file/folder is to be selected.
   * @param bFolder true for folder selection, false for file selection.
   * @param sTitle title of dialog.
   * @param sMessage message in dialog.
   * @param sOk text on OK button.
   * @param sCancel text on cancel button.
   * @param sPathLabel text for label for path.
   * @param sNameLabel text for label for name. 
   * @param fileInitial initial file/folder.
   * @param bIncludeHidden true, if hidden files are to be included.
   * @param listExtensions lists accepted extensions 
   *   (without period, null for all) and their descriptive texts, 
   *   or null, if no filtering is to be applied.
   * @param sOverwriteQuery text to display, if the chosen new file already
   *    exists. While this is answered in the negative, the selector stays
   *    active. If this is null, no question is displayed.
   * @param sYes text on yes button of the overwrite query.
   * @param sNo text on the no button of the overwrite query.
   */
  private FC(Stage stageOwner, boolean bExisting, boolean bFolder, 
      String sTitle, String sMessage, String sOk, String sCancel, String sPathLabel, String sNameLabel,
      File fileInitial, boolean bIncludeHidden, List<ExtensionFilter> listExtensions,
      String sOverwriteQuery, String sYes, String sNo)
  {
    super(stageOwner,sTitle);
    _bExisting = bExisting;
    _bFolder = bFolder;
    _bIncludeHidden = bIncludeHidden;
    fileInitial = fileInitial.getAbsoluteFile();
    if (isHidden(fileInitial))
      _bIncludeHidden = true;
    if ((sOverwriteQuery != null) && (sOverwriteQuery.length() > 0))
    {
      _sOverwriteQuery = sOverwriteQuery;
      _sYes = sYes;
      _sNo = sNo;
    }
    if (!fileInitial.getParentFile().exists())
      fileInitial.getParentFile().mkdirs();
    VBox vbox = createVBox(sMessage,
      fileInitial,
      sPathLabel, sNameLabel,
      listExtensions,
      sOk,sCancel);
    Scene scene = new Scene(vbox, vbox.getMinWidth()+10.0, vbox.getMinHeight()+10.0);
    setScene(scene);
  } /* constructor FS */

  /*------------------------------------------------------------------*/
  /** handle the pressing of a button.
   * @param ae action event identifying the button.
   */
  @Override
  public void handle(ActionEvent ae)
  {
    boolean bClose = true;
    if (ae.getSource() == _btnOk)
    {
      String sName = _tfName.getText();
      if (_cbExtensions != null)
      {
        ExtensionFilter ef = _cbExtensions.getSelectionModel().getSelectedItem();
        String sExtension = ef.getExtension();
        if (sExtension != null)
        {
          if (!sName.endsWith("."+sExtension))
            sName = sName + "."+sExtension;
        }
      }
      File fileParent = _tv.getSelectionModel().getSelectedItem().getValue();
      if (fileParent != null)
        sName = fileParent.getAbsolutePath()+File.separator+sName;
      _fileResult = new File(sName);
      if ((_sOverwriteQuery != null) && (_fileResult != null) && _fileResult.exists())
      {
        int iResult = MB.show(this, getTitle(), 
            MessageFormat.format(_sOverwriteQuery,_fileResult.getAbsolutePath()), 
            _sYes, _sNo); 
        if (iResult != 1)
        {
          _fileResult = null;
          bClose = false;
        }
      }
    }
    if (bClose)
      close();
  } /* handle */

  /*==================================================================*/
  /** ExtensionFilter describes a single extension together with
   * a descriptive text to be displayed in the extension combo box. 
   */
  public static class ExtensionFilter
  {
    private String _sText;
    public String getText() { return _sText; }
    private String _sExtension;
    public String getExtension() { return _sExtension; }
    private ExtensionFilter(String sText, String sExtension)
    {
      _sText = sText;
      _sExtension = sExtension;
    } /* constructor */
    public static ExtensionFilter newExtensionFilter(String sText, String sExtension)
    {
      return new ExtensionFilter(sText,sExtension);
    } /* factory */
    @Override
    public String toString()
    {
      String s = _sText + " (*";
      if (_sExtension != null)
        s = s + "."+_sExtension;
      s = s + ")";
      return s;
    } /* toString */
  } /* class ExtensionFilter */
  /*==================================================================*/
  
  /*------------------------------------------------------------------*/
  /** choose an existing folder.
   * @param stageOwner owner stage or null.
   * @param sTitle title of selector dialog.
   * @param sMessage message in selector dialog.
   * @param sOk text on OK button.
   * @param sCancel text on cancel button.
   * @param sPathLabel text for label for path.
   * @param sFolderLabel text for label for folder name. 
   * @param fileInitialFolder initial folder - must exist.
   *   If it or a parent of it is hidden, hidden folders are displayed
   *   irrespective of the parameter bIncludeHidden.
   *   If its parent does not exist, it is created.
   * @param bIncludeHidden true, if hidden files are to be included.
   * @return selected existing folder or null, if dialog was canceled.
   * @throws FileNotFoundException, if the initial folder does not exist.
   */
  public static File chooseExistingFolder(Stage stageOwner, 
      String sTitle, String sMessage, String sOk, String sCancel, String sPathLabel, String sFolderLabel, 
      File fileInitialFolder,boolean bIncludeHidden)
    throws FileNotFoundException
  {
    File fileResultFolder = null;
    boolean bOk = true;
    if (fileInitialFolder != null)
    {
      if (fileInitialFolder.exists())
      {
        if (!isFolder(fileInitialFolder))
          bOk = false;
      }
      else
        bOk = false;
    }
    if (bOk)
    {
      FC fs = new FC(stageOwner,true,true,sTitle,sMessage,sOk,sCancel,sPathLabel,sFolderLabel,
        fileInitialFolder,bIncludeHidden,null,null,null,null);
        fs.showAndWait();
      fileResultFolder = fs.getResult();
    }
    else 
      throw new FileNotFoundException("Initial folder \""+fileInitialFolder.getAbsolutePath()+"\" does not exist!");
    return fileResultFolder;
  } /* chooseExistingFolder */

  /*------------------------------------------------------------------*/
  /** choose a new folder.
   * @param stageOwner owner stage or null.
   * @param sTitle title of selector dialog.
   * @param sMessage message in selector dialog.
   * @param sOk text on OK button.
   * @param sCancel text on cancel button.
   * @param sPathLabel text for label for path.
   * @param sFolderLabel text for label for name. 
   * @param fileInitialFolder initial folder - must not necessarily exist.
   *   If it or a parent of it is hidden, hidden folders are displayed
   *   irrespective of the parameter bIncludeHidden.
   *   If its parent does not exist, it is created.
   * @param bIncludeHidden true, if hidden files are to be included.
   * @return selected new folder (may not exist yet) or null, if dialog was canceled.
   * @throws IOException if the initial folder is not a folder.
   */
  public static File chooseNewFolder(Stage stageOwner, 
      String sTitle, String sMessage, String sOk, String sCancel, String sPathLabel, String sFolderLabel,
      File fileInitialFolder, boolean bIncludeHidden)
          throws IOException
  {
    File fileResultFolder = null;
    boolean bOk = true;
    if (fileInitialFolder != null)
    {
      if (fileInitialFolder.exists())
      {
        if (!isFolder(fileInitialFolder))
          bOk = false;
      }
    }
    if (bOk)
    {
      FC fs = new FC(stageOwner,false,true,sTitle,sMessage,sOk,sCancel,sPathLabel,sFolderLabel,
        fileInitialFolder,bIncludeHidden,null,null,null,null);
      fs.showAndWait();
      fileResultFolder = fs.getResult();
    }
    else
      throw new IOException("Initial folder \""+fileInitialFolder.getAbsolutePath()+"\" is not a folder!");
    return fileResultFolder;
  } /* chooseNewFolder */

  /*------------------------------------------------------------------*/
  /** determine whether the given file matches one of the given extensions.
   * @param file file.
   * @param listExtensions extension list.
   * @return true, if file matches an extension.
   */
  private static boolean matchesExtensions(File file, List<ExtensionFilter> listExtensions)
  {
    boolean bMatch = false;
    if (listExtensions != null)
    {
      String sName = file.getName();
      for (int iExtension = 0; (!bMatch) && (iExtension < listExtensions.size()); iExtension++)
      {
        ExtensionFilter ef = listExtensions.get(iExtension);
        String sExtension = ef.getExtension();
        if (sExtension != null)
        {
          if (sName.endsWith("."+sExtension))
            bMatch = true;
        }
        else
          bMatch = true;
      }
    }
    else
      bMatch = true;
    return bMatch;
  } /* matchesExtensions */
  
  /*------------------------------------------------------------------*/
  /** choose an existing file (for open).
   * @param stageOwner owner stage or null.
   * @param sTitle title of selector dialog.
   * @param sMessage message in selector dialog.
   * @param sOk text on OK button.
   * @param sCancel text on cancel button.
   * @param sPathLabel text for label for path.
   * @param sFileLabel text for label for file name. 
   * @param fileInitialFile initial file - must exist and have one of the 
   *   accepted extensions - may have a wild card file name (e.g. *.ext or *.* or *).
   *   If it or a parent of it is hidden, hidden files are displayed
   *   irrespective of the parameter bIncludeHidden.
   *   If its parent does not exist, it is created.
   * @param bIncludeHidden true, if hidden files are to be included.
   * @param listExtensions lists accepted extensions 
   *   (without period, null for all) and their descriptive texts, 
   *   or null, if no filtering is to be applied.
   * @return selected existing file conforming to one of the extensions in the map.
   * @throws FileNotFoundException if the initial file does not exist or
   *   match one of the extensions.
   */
  public static File chooseExistingFile(Stage stageOwner, 
      String sTitle, String sMessage, String sOk, String sCancel, String sPathLabel, String sFileLabel,
      File fileInitialFile, boolean bIncludeHidden, List<ExtensionFilter> listExtensions)
    throws FileNotFoundException
  {
    File fileResultFile = null;
    boolean bOk = true;
    if (fileInitialFile != null)
    {
      if (matchesExtensions(fileInitialFile,listExtensions))
      {
        if (fileInitialFile.exists())
        {
          if (!fileInitialFile.isFile())
            bOk = false;
        }
        else
        {
          if (!fileInitialFile.getName().startsWith("*"))
            bOk = false;
        }
      }
    }
    if (bOk)
    {
      FC fs = new FC(stageOwner,true,false,sTitle,sMessage,sOk,sCancel,sPathLabel,sFileLabel,
        fileInitialFile,bIncludeHidden,listExtensions, null, null,null);
      fs.showAndWait();
      fileResultFile = fs.getResult();
    }
    else
      throw new FileNotFoundException("Initial file \""+fileInitialFile.getAbsolutePath()+"\" does not exist or does not match extensions!");
    return fileResultFile;
  } /* chooseExistingFile */

  /*------------------------------------------------------------------*/
  /** choose a new file (for save).
   * @param stageOwner owner stage or null.
   * @param sTitle title of selector dialog.
   * @param sMessage message in selector dialog.
   * @param sOk text on OK button.
   * @param sCancel text on cancel button.
   * @param sPathLabel text for label for path.
   * @param sFileLabel text for label for file name.
   * @param fileInitialFile - may not exist but must have one of the 
   *   accepted extensions  - may have a wild card file name (e.g. *.ext or *.* or *).
   *   If it or a parent of it is hidden, hidden files are displayed
   *   irrespective of the parameter bIncludeHidden.
   *   If its parent does not exist, it is created.
   * @param bIncludeHidden true, if hidden files are to be included.
   * @param listExtensions lists accepted extensions 
   *   (without period, null for all) and their descriptive texts, 
   *   or null, if no filtering is to be applied.
   * @param sOverwriteQuery text to display, if the chosen new file already
   *    exists. While this is answered in the negative, the selector stays
   *    active. If this is null, no question is displayed. If the query contains
   *    "{0}" it is replaced by the chosen file name.
   * @param sYes text on yes button of the overwrite query.
   * @param sNo text on the no button of the overwrite query.
   * @return selected new file conforming to one of the extensions in the map.
   * @throws IOException if the initial file is not a file or does not
   *   match one of the extensions.
   */
  public static File chooseNewFile(Stage stageOwner, 
    String sTitle, String sMessage, String sOk, String sCancel, String sPathLabel, String sFileLabel,
    File fileInitialFile, boolean bIncludeHidden, 
    List<ExtensionFilter> listExtensions, 
    String sOverwriteQuery, String sYes, String sNo)
    throws IOException
  {
    File fileResultFile = null;
    boolean bOk = true;
    if (fileInitialFile != null)
    {
      if (matchesExtensions(fileInitialFile,listExtensions))
      {
        if (fileInitialFile.exists())
        {
          if (!fileInitialFile.isFile())
            bOk = false;
        }
      }
      else
        bOk = false;
    }
    if (bOk)
    {
      FC fs = new FC(stageOwner,false,false,sTitle,sMessage,sOk,sCancel,sPathLabel,sFileLabel,
        fileInitialFile,bIncludeHidden,listExtensions, sOverwriteQuery, sYes, sNo);
      fs.showAndWait();
      fileResultFile = fs.getResult();
    }
    else
      throw new IOException("Initial file \""+fileInitialFile.getAbsolutePath()+"\" is not a file or does not match extensions!");
    return fileResultFile;
  } /* chooseNewFile */

} /* class FS */
