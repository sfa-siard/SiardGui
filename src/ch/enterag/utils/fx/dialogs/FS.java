/*======================================================================
FS implements convenience calls to the static methods in FC.  
Application : JavaFX Utilities
Description : FS implements convenience calls to the static methods in FC
  getting the relevant strings from an language-dependent FxBundle. 
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland
Created    : 21.12.2015, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.fx.dialogs;

import java.io.*;
import java.util.*;
import javafx.stage.*;

import ch.enterag.utils.fx.*;

/*====================================================================*/
/** FS implements convenience calls to the static methods in FC
  getting the relevant strings from an language-dependent FxBundle.
  Use system property ch.enterag.utils.fx.dialogs.FS.useNative to
  force usage of native dialogs.
 * @author Hartwig Thomas
 *
 */
public abstract class FS
{
  public static final String sUSE_NATIVE_PROPERTY = "ch.enterag.utils.fx.dialogs.FS.useNative";
  
  /*------------------------------------------------------------------*/
  /** convenience method with standard strings from bundle and no hidden files.
   * @param stageOwner owner stage or null.
   * @param sTitle title of selector dialog.
   * @param sMessage message in selector dialog.
   * @param fb bundle for Ok, Cancel, folder name label.
   * @param fileInitialFolder initial folder - must exist.
   * @return selected existing folder or null, if dialog was canceled.
   * @throws FileNotFoundException, if the initial folder does not exist.
   */
  public static File chooseExistingFolder(Stage stageOwner,
      String sTitle, String sMessage, FxBundle fb,
      File fileInitialFolder)
    throws FileNotFoundException
  {
    File fileFolder = null;
    if (Boolean.valueOf(System.getProperty(sUSE_NATIVE_PROPERTY)))
    {
      DirectoryChooser dc = new DirectoryChooser();
      dc.setTitle(sTitle);
      dc.setInitialDirectory(fileInitialFolder);
      fileFolder = dc.showDialog(stageOwner);
    }
    else
      fileFolder = FC.chooseExistingFolder(stageOwner,sTitle,sMessage,
          fb.getOk(),fb.getCancel(),fb.getPathLabel(),fb.getFolderLabel(),
          fileInitialFolder,false);
    return fileFolder;
  } /* chooseExistingFolder */
  
  /*------------------------------------------------------------------*/
  /** convenience method with standard strings from bundle and no hidden files.
   * @param stageOwner owner stage or null.
   * @param sTitle title of selector dialog.
   * @param sMessage message in selector dialog.
   * @param fb bundle for Ok, Cancel, folder name label.
   * @param fileInitialFolder initial folder - must not necessarily exist.
   * @return selected new folder (may not exist yet) or null, if dialog was canceled.
   * @throws IOException if the initial folder is not a folder.
   */
  public static File chooseNewFolder(Stage stageOwner,
      String sTitle, String sMessage, FxBundle fb,
      File fileInitialFolder)
    throws IOException
  {
    File fileFolder = null;
    if (Boolean.valueOf(System.getProperty(sUSE_NATIVE_PROPERTY)))
    {
      DirectoryChooser dc = new DirectoryChooser();
      dc.setTitle(sTitle);
      /* if the initial folder does not exist, create it */
      int iCreated = 0;
      for (File folder = fileInitialFolder; !folder.exists(); folder = folder.getParentFile())
        iCreated++;
      fileInitialFolder.mkdirs();
      dc.setInitialDirectory(fileInitialFolder);
      fileFolder = dc.showDialog(stageOwner);
      /* remove the created folders again */
      for (File folder = fileInitialFolder; iCreated > 0; folder = folder.getParentFile())
      {
        fileInitialFolder.delete();
        iCreated--;
      }
    }
    else
      fileFolder = FC.chooseNewFolder(stageOwner,sTitle,sMessage,
        fb.getOk(),fb.getCancel(),fb.getPathLabel(),fb.getFolderLabel(),
        fileInitialFolder,false);
    return fileFolder;
  } /* chooseNewFolder */
  
  /*------------------------------------------------------------------*/
  /** convenience method with standard strings from bundle and no hidden files.
   * @param stageOwner owner stage or null.
   * @param sTitle title of selector dialog.
   * @param sMessage message in selector dialog.
   * @param fb bundle for Ok, Cancel, file name label.
   * @param fileInitialFile initial file - must exist and have one of the 
   *   accepted extensions - may have a wild card file name (e.g. *.ext or *.* or *).
   * @param sExtension accepted extension (without period) or null
   *   with descriptive text in fb under "&lt;extension&gt;.files".
   * @return selected existing file conforming to one of the extensions in the map.
   * @throws FileNotFoundException if the initial file does not exist or
   *   match one of the extensions.
   */
  public static File chooseExistingFile(Stage stageOwner,
      String sTitle, String sMessage, FxBundle fb,
      File fileInitialFile, String sExtension)
    throws FileNotFoundException
  {
    File file = null;
    if (Boolean.valueOf(System.getProperty(sUSE_NATIVE_PROPERTY)))
    {
      FileChooser fc = new FileChooser();
      fc.setTitle(sTitle);
      fc.setInitialDirectory(fileInitialFile.getParentFile());
      fc.setInitialFileName(fileInitialFile.getName());
      fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(fb.getFiles(sExtension), "*."+sExtension));
      fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(fb.getAllFiles(), "*"));
      file = fc.showOpenDialog(stageOwner);
    }
    else
    {
      List<FC.ExtensionFilter> listExtensions = null;
      if (sExtension != null)
      {
        listExtensions = new ArrayList<FC.ExtensionFilter>();
        listExtensions.add(FC.ExtensionFilter.newExtensionFilter(fb.getFiles(sExtension), sExtension));
        listExtensions.add(FC.ExtensionFilter.newExtensionFilter(fb.getAllFiles(), null));
      }
      file = FC.chooseExistingFile(stageOwner,sTitle,sMessage,
          fb.getOk(),fb.getCancel(),fb.getPathLabel(),fb.getFileLabel(),
          fileInitialFile,false,listExtensions);
    }
    return file;
  } /* chooseExistingFile */
  
  /*------------------------------------------------------------------*/
  /** convenience method with standard strings from bundle and no hidden files.
   * @param stageOwner owner stage or null.
   * @param sTitle title of selector dialog.
   * @param sMessage message in selector dialog.
   * @param fb bundle for Ok, Cancel, file name label.
   * @param fileInitialFile - may not exist but must have one of the 
   *   accepted extensions  - may have a wild card file name (e.g. *.ext or *.* or *).
   * @param sExtension accepted extension (without period) or null
   *   with descriptive text in fb under "&lt;extension&gt;.files".
   * @param bOverwriteQuery true, if an overwrite query is to be displayed
   *   if the selected file exists.
   * @return selected new file conforming to one of the extensions in the map.
   * @throws IOException if the initial file is not a file.
   */
  public static File chooseNewFile(Stage stageOwner,
      String sTitle, String sMessage, FxBundle fb,
      File fileInitialFile, String sExtension, boolean bOverwriteQuery)
    throws IOException
  {
    File file = null;
    if (Boolean.valueOf(System.getProperty(sUSE_NATIVE_PROPERTY)))
    {
      FileChooser fc = new FileChooser();
      fc.setTitle(sTitle);
      fc.setInitialDirectory(fileInitialFile.getParentFile());
      fc.setInitialFileName(fileInitialFile.getName());
      fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(fb.getFiles(sExtension), sExtension));
      fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(fb.getAllFiles(), "*.*"));
      file = fc.showSaveDialog(stageOwner);
    }
    else
    {
      List<FC.ExtensionFilter> listExtensions = null;
      if (sExtension != null)
      {
        listExtensions = new ArrayList<FC.ExtensionFilter>();
        listExtensions.add(FC.ExtensionFilter.newExtensionFilter(fb.getFiles(sExtension), sExtension));
        listExtensions.add(FC.ExtensionFilter.newExtensionFilter(fb.getAllFiles(), null));
      }
      String sOverwriteQuery = null;
      String sYes = null;
      String sNo = null;
      if (bOverwriteQuery)
      {
        sOverwriteQuery = fb.getOverwriteQuery();
        sYes = fb.getYes();
        sNo = fb.getNo();
      }
      file = FC.chooseNewFile(stageOwner,sTitle,sMessage,
          fb.getOk(),fb.getCancel(),fb.getPathLabel(),fb.getFileLabel(),
          fileInitialFile,false,listExtensions, 
          sOverwriteQuery, sYes, sNo);
    }
    return file;
  } /* chooseNewFile */
  
} /* class FS */
