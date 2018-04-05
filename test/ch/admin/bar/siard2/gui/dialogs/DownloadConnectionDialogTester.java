package ch.admin.bar.siard2.gui.dialogs;

import javafx.application.*;
import javafx.stage.*;
import ch.enterag.utils.*;

public class DownloadConnectionDialogTester 
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
    DownloadConnectionDialog dcd = DownloadConnectionDialog.showDownloadConnectionDialog(
      _stage, null, null);
    System.out.println("Result: "+String.valueOf(dcd.getResult()));
    if (dcd.getResult() == 1)
    {
      System.out.println("JDBC URL: "+dcd.getConnectionUrl());
      System.out.println("DB User: "+dcd.getDbUser());
      System.out.println("DB Password: "+dcd.getDbPassword());
      System.out.println("Meta Data Only: "+String.valueOf(dcd.isMetaDataOnly()));
    }
    _stage.close();
  } /* start */

} /* class DownloadConnectionDialogTester */
