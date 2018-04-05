package ch.admin.bar.siard2.gui.dialogs;

import java.io.*;
import javafx.application.*;
import javafx.stage.*;
import ch.enterag.utils.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.primary.*;

public class MetaDataDialogTester
extends Application
{
  /** return codes */
  private static final int iRETURN_OK = 0;
  private static final int iRETURN_ERROR = 8;
  
  private Stage _stage = null;

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
    Archive archive = ArchiveImpl.newInstance();
    archive.open(fileArchive);
    File fileXsl = new File("etc/metadata.xsl");
    MetaDataDialog.showMetaDataDialog(_stage,archive,fileXsl);
    _stage.close();
  } /* start */

}
