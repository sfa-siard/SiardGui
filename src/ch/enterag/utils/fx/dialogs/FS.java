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
 * @author Hartwig Thomas
 *
 */
public abstract class FS
{
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
    return FC.chooseExistingFolder(stageOwner,sTitle,sMessage,
        fb.getOk(),fb.getCancel(),fb.getPathLabel(),fb.getFolderLabel(),
        fileInitialFolder,false);
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
    return FC.chooseNewFolder(stageOwner,sTitle,sMessage,
        fb.getOk(),fb.getCancel(),fb.getPathLabel(),fb.getFolderLabel(),
        fileInitialFolder,false);
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
    List<FC.ExtensionFilter> listExtensions = null;
    if (sExtension != null)
    {
      listExtensions = new ArrayList<FC.ExtensionFilter>();
      listExtensions.add(FC.ExtensionFilter.newExtensionFilter(fb.getFiles(sExtension), sExtension));
      listExtensions.add(FC.ExtensionFilter.newExtensionFilter(fb.getAllFiles(), null));
    }
    return FC.chooseExistingFile(stageOwner,sTitle,sMessage,
        fb.getOk(),fb.getCancel(),fb.getPathLabel(),fb.getFileLabel(),
        fileInitialFile,false,listExtensions);
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
    return FC.chooseNewFile(stageOwner,sTitle,sMessage,
        fb.getOk(),fb.getCancel(),fb.getPathLabel(),fb.getFileLabel(),
        fileInitialFile,false,listExtensions, 
        sOverwriteQuery, sYes, sNo);
  } /* chooseNewFile */
  
} /* class FS */
