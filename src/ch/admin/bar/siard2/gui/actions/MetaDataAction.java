/*======================================================================
MetaDataAction handles export and display of a SIARD archive. 
Application : SIARD GUI
Description: MetaDataAction handles export and display of a SIARD archive. 
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 03.07.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.actions;

import java.io.*;
import javafx.stage.*;
import ch.enterag.utils.logging.*;
import ch.enterag.utils.fx.dialogs.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.gui.*;
import ch.admin.bar.siard2.gui.dialogs.*;

/*====================================================================*/
/** MetaDataAction handles export and display of a SIARD archive.
 * @author Hartwig Thomas
 */
public class MetaDataAction
  implements FileFilter
{
  private static final String sXSL_EXTENSION = "xsl";
  private static final String sXML_EXTENSION = "xml";
  private static final String sHTML_EXTENSION = "html";
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(MetaDataAction.class.getName());

  /*------------------------------------------------------------------*/
  /** constructor */
  private MetaDataAction()
  {
  } /* constructor MetaDataAction */
  
  /*------------------------------------------------------------------*/
  /** factory */
  public static MetaDataAction newMetaDataAction()
  {
    return new MetaDataAction();
  } /* newMetaDataAction */

  /*------------------------------------------------------------------*/
  /** file filter for .xsl files. */
  @Override
  public boolean accept(File file)
  {
    return file.getName().endsWith("."+sXSL_EXTENSION);
  } /* accept */
  
  /*------------------------------------------------------------------*/
  /** search for XSL in etc opening file selector, if there is more than
   * one, and display meta data dialog using it.
   */
  public void displayMetaData()
  {
    _il.enter();
    SiardGui sg = SiardGui.getSiardGui();
    MetaDataDialog.showMetaDataDialog(sg.getStage(), sg.getArchive(), 
      UserProperties.getUserProperties().getXslFile());
    _il.exit();
  } /* displayMetaData */

  /*------------------------------------------------------------------*/
  /** augment meta data by meta data from an external meta data template.
   */
  public void augmentMetaData()
  {
    _il.enter();
    SiardBundle sb = SiardBundle.getSiardBundle();
    Stage stage = SiardGui.getSiardGui().getStage();
    File fileMetaData = new File(SiardGui.getDefaultDataDirectory().getAbsolutePath() + 
      File.separator+"*."+sXML_EXTENSION);
    try
    {
      fileMetaData = FS.chooseExistingFile(stage, 
        sb.getMetaDataAugmentTitle(), sb.getMetaDataAugmentMessage(), sb,
        fileMetaData, sXML_EXTENSION);
    }
    catch(FileNotFoundException fnfe) { _il.exception(fnfe); }
    if (fileMetaData != null)
    {
      try
      {
        FileInputStream fis = new FileInputStream(fileMetaData);
        SiardGui.getSiardGui().getArchive().importMetaDataTemplate(fis);
        fis.close();
      }
      catch(IOException ie)
      {
        MB.show(SiardGui.getSiardGui().getStage(),
          sb.getMetaDataErrorTitle(),
          sb.getMetaDataErrorAugmentMessage(fileMetaData,ie),
          sb.getOk(), null);
      }
    }
    _il.exit();
  } /* augmentMetaData */

  /*------------------------------------------------------------------*/
  /** save the given meta data string to the file system.
   * Display save file dialog for given extension and save meta data to
   * selected file.
   * @param stageOwner owner window.
   * @param archive archive.
   * @param sMetaData meta data to be saved.
   * @param sExtension file extension.
   */
  private void saveMetaData(Stage stageOwner, Archive archive, 
    String sMetaData, String sExtension)
  {
    _il.enter();
    SiardBundle sb = SiardBundle.getSiardBundle();
    Stage stage = SiardGui.getSiardGui().getStage();
    String sFileMetaData = archive.getFile().getAbsolutePath();
    // replace the SIARD extension by the given extension
    int iSep = sFileMetaData.lastIndexOf(File.separator);
    int iExt = sFileMetaData.lastIndexOf(".");
    if (iExt > iSep)
      sFileMetaData = sFileMetaData.substring(0,iExt);
    File fileMetaData = new File(sFileMetaData+"."+sExtension);
    try
    {
      fileMetaData = FS.chooseNewFile(stage, 
          sb.getMetaDataSaveTitle(), sb.getMetaDataSaveMessage(), sb, 
        fileMetaData, sExtension,true);
    }
    catch(IOException ie) { _il.exception(ie); }
    if (fileMetaData != null)
    {
      try
      {
        FileWriter fw = new FileWriter(fileMetaData);
        fw.write(sMetaData);
        fw.close();
      }
      catch(IOException ie)
      {
        MB.show(stageOwner,
          sb.getMetaDataErrorTitle(),
          sb.getMetaDataErrorSaveMessage(fileMetaData,ie),
          sb.getOk(), null);
      }
    }
    _il.exit();
  } /* saveMetaData */
  
  /*------------------------------------------------------------------*/
  /** save the given meta data XML to file
   * @param sMetaDataXml meta data XML.
   */
  public void saveMetaDataXml(Stage stageOwner, Archive archive, String sMetaDataXml)
  {
    saveMetaData(stageOwner, archive, sMetaDataXml, sXML_EXTENSION);
  } /* saveMetaDataXml */
  
  /*------------------------------------------------------------------*/
  /** save the given meta data HTML to file
   * @param sMetaDataXml meta data HTML.
   */
  public void saveMetaDataHtml(Stage stageOwner, Archive archive, String sMetaDataHtml)
  {
    saveMetaData(stageOwner, archive, sMetaDataHtml, sHTML_EXTENSION);
  } /* saveMetaDataXml */
  
} /* MetaDataAction */
