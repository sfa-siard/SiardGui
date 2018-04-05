package ch.admin.bar.siard2.gui.dialogs;

import java.io.*;
import java.sql.*;
import javafx.application.*;
import javafx.stage.Stage;
import ch.enterag.utils.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.primary.*;
import ch.admin.bar.siard2.cmd.*;

public class DownloadDialogTester
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
    String sUrl = OracleDriver.getUrl("localhost:1521:orcl"); 
    String sUser = "TESTUSER";
    String sPassword = "testpwd";
    String sError = SiardConnection.getSiardConnection().loadDriver(sUrl);
    if (sError == null)
    {
      Connection conn = DriverManager.getConnection(sUrl, sUser, sPassword);
      File fileArchive = new File("logs/sample.siard");
      if (fileArchive.exists())
        fileArchive.delete();
      try
      {
        Archive archive = ArchiveImpl.newInstance();
        archive.create(fileArchive);
        DownloadDialog dd = DownloadDialog.showDownloadDialog(_stage,conn,archive,false,false);
        System.out.println("Success: "+String.valueOf(dd.wasSuccessful()));
        if (dd.wasSuccessful())
          dd.getArchive().close();
      }
      catch(IOException ie) { System.err.println(EU.getExceptionMessage(ie)); }
    }
    else
      System.err.println("URL "+sUrl+" could not be loaded!");
    _stage.close();
  } /* start */

}
