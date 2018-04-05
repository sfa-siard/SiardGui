/*====================================================================== 
ExportAction handles HTML export of a SIARD table. 
Application : SIARD GUI
Description: ExportAction handles HTML export of a SIARD table. 
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 16.08.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.actions;

import java.io.*;
import java.net.*;
import javafx.stage.*;
import ch.enterag.utils.*;
import ch.enterag.utils.fx.dialogs.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.gui.*;

/*====================================================================*/
/** ExportAction handles HTML export of a SIARD table.
 * @author Hartwig Thomas
 */
public class ExportAction
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(ExportAction.class.getName());
  public static final String sHTML_EXTENSION = "html";
  Table _table = null;

  /*------------------------------------------------------------------*/
  /** constructor
   * @param table table to be exported.
   */
  private ExportAction(Table table)
  {
    _table = table;
  } /* constructor ExportAction */
  
  /*------------------------------------------------------------------*/
  /** factory
   * @param table table to be exported.
   * @return new instance of ExportAction.
   */
  public static ExportAction newExportAction(Table table)
  {
    ExportAction ea = new ExportAction(table);
    return ea;
  } /* factory newExportAction */
  
  /*------------------------------------------------------------------*/
  /** export of SIARD table.
   * - ask user for file name
   * - get lobs folder from user properties.
   * - write table to HTML file.
   */
  public void exportAsHtml()
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    File fileExport = null;
    /* select file for saving the table */
    try
    {
      File fileInitialDirectory = SiardGui.getDefaultDataDirectory();
      String sInitialFilename = URLEncoder.encode(_table.getMetaTable().getName(),SU.sUTF8_CHARSET_NAME).replace("*", "_");
      fileExport = new File(fileInitialDirectory.getAbsolutePath()+File.separator+sInitialFilename+"."+sHTML_EXTENSION);
      Stage stage = SiardGui.getSiardGui().getStage();
      try
      {
        fileExport = FS.chooseNewFile(stage, 
          sb.getTableFileTitle(), sb.getTableFileMessage(), 
          sb, fileExport, sHTML_EXTENSION,true);
      }
      catch (IOException ie) { _il.exception(ie); }
      if (fileExport != null)
      {
        SiardGui.getSiardGui().startAction(
          sb.getTableExportStatus(_table.getMetaTable().getName(),fileExport));
        try
        {
          File folderLobs = UserProperties.getUserProperties().getLobsFolder();
          FileOutputStream fos = new FileOutputStream(fileExport);
          _table.exportAsHtml(fos, folderLobs);
          fos.close();
        }
        catch(IOException ie)
        {
          MB.show(SiardGui.getSiardGui().getStage(),
            sb.getTableErrorTitle(), 
            sb.getTableErrorExportMessage(_table.getMetaTable().getName(),fileExport,ie), 
            sb.getOk(), null);
        }
        SiardGui.getSiardGui().terminateAction();
      }
    }
    catch(UnsupportedEncodingException uee) { _il.exception(uee); }
  } /* exportAsHtml */
  
} /* class ExportAction */
