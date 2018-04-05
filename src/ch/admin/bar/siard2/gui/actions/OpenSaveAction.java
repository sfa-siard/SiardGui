/*====================================================================== 
Application : SIARD GUI
Description: OpenSaveAction handles open and save of a SIARD archive. 
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 30.06.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.actions;

import java.io.*;
import javafx.stage.*;
import ch.enterag.utils.fx.dialogs.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.primary.*;
import ch.admin.bar.siard2.gui.*;

/*====================================================================*/
/** OpenSaveAction handles open and save of a SIARD archive.
 * @author Hartwig Thomas
 */
public class OpenSaveAction
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(OpenSaveAction.class.getName());

  /*------------------------------------------------------------------*/
  /** constructor */
  private OpenSaveAction()
  {
  } /* constructor OpenSaveAction */
  
  /*------------------------------------------------------------------*/
  /** factory */
  public static OpenSaveAction newOpenSaveAction()
  {
    return new OpenSaveAction();
  } /* newOpenSaveAction */
  
  /*------------------------------------------------------------------*/
  /** if sFile is null or does not exist, display file selector,
   * otherwise open the file.
   * @param sFile SIARD archive file to be opened.
   */
  public void open(String sFile)
  {
    _il.enter(sFile);
    SiardBundle sb = SiardBundle.getSiardBundle();
    Stage stage = SiardGui.getSiardGui().getStage();
    File fileArchive = null;
    if (sFile != null)
    {
      fileArchive = new File(sFile);
      if (!fileArchive.exists())
        fileArchive = null;
    }
    if (fileArchive == null)
    {
      fileArchive = new File(SiardGui.getDefaultDataDirectory().getAbsolutePath()+File.separator+"*."+Archive.sSIARD_DEFAULT_EXTENSION);
      if (MruFile.getMruFile().getMruFiles() > 0)
        fileArchive = (new File(MruFile.getMruFile().getMruFile(0)));
      try
      {
        fileArchive = FS.chooseExistingFile(stage,
            sb.getOpenArchiveTitle(), sb.getOpenArchiveMessage(), sb, 
            fileArchive, Archive.sSIARD_DEFAULT_EXTENSION);
      }
      catch(FileNotFoundException fnfe) { _il.exception(fnfe); }
    }
    if (fileArchive != null)
    {
      SiardGui.getSiardGui().startAction(sb.getOpeningStatus(fileArchive));
      Archive archive = ArchiveImpl.newInstance();
      try
      {
        archive.open(fileArchive);
        if (archive.isValid())
        {
          MruFile mf = MruFile.getMruFile();
          mf.setMruFile(archive.getFile().getAbsolutePath());
          MainMenuBar.getMainMenuBar().setFileMru();
          SiardGui.getSiardGui().setArchive(archive);
        }
        else
        {
          MB.show(stage, 
            sb.getOpenErrorTitle(), 
            sb.getOpenErrorInvalidMessage(fileArchive),
            sb.getOk(),null);
        }
      }
      catch(IOException ie)
      {
        MB.show(stage,
          sb.getOpenErrorTitle(),
          sb.getOpenErrorMessage(fileArchive,ie),
          sb.getOk(), null);
      }
      SiardGui.getSiardGui().terminateAction();
    }
    _il.exit(sFile);
  } /* open */

  /*------------------------------------------------------------------*/
  /** save the SIARD archive
   */
  public void save()
  {
    _il.enter();
    SiardBundle sb = SiardBundle.getSiardBundle();
    Archive archive = SiardGui.getSiardGui().getArchive();
    // force apply/reset on unsaved changes to meta data
    MainPane.getMainPane().refreshLanguage();
    SiardGui.getSiardGui().startAction(sb.getSavingStatus(archive.getFile()));
    try 
    { archive.saveMetaData(); }
    catch(IOException ie)
    {
      MB.show(SiardGui.getSiardGui().getStage(),
        sb.getSaveErrorTitle(), sb.getSaveErrorMessage(archive.getFile(),ie),
        sb.getOk(), null);
    }
    SiardGui.getSiardGui().terminateAction();
    _il.exit();
  }
} /* class OpenSaveAction */
