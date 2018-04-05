package ch.admin.bar.siard2.gui.dialogs;

import java.io.File;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.EU;
import javafx.application.*;
import javafx.stage.Stage;

public class SearchDialogTester
extends Application
{
  /** return codes */
  private static final int iRETURN_OK = 0;
  private static final int iRETURN_ERROR = 8;
  
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
    Archive archive = ArchiveImpl.newInstance();
    archive.open(fileArchive);
    MetaTable mt = archive.getMetaData().getMetaSchema(0).getMetaTable(0);
    SearchDialog sd = SearchDialog.showSearchDialog(stage,mt,"findstr",true);
    System.out.println("Canceled: "+String.valueOf(sd.isCanceled()));
    if (!sd.isCanceled())
    {
      System.out.println("Find string: "+sd.getFindString());
      System.out.println("Match case: "+sd.mustMatchCase());
      System.out.println("Selected columns: "+sd.getSelection());
    }
    stage.close();
  } /* start */
  

}
