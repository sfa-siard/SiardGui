package ch.admin.bar.siard2.gui.dialogs;

import java.io.*;
import java.sql.*;
import java.util.*;
import javafx.application.*;
import javafx.stage.Stage;
import ch.enterag.utils.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.primary.*;
import ch.admin.bar.siard2.cmd.*;

public class UploadDialogTester
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
      File fileArchive = new File("testfiles/sample.siard");
      try
      {
        Archive archive = ArchiveImpl.newInstance();
        archive.open(fileArchive);
        Map<String,String> mapSchemas = new HashMap<String,String>();
        mapSchemas.put("SampleSchema", "TESTUSER");
        MetaDataToDb mdtd = MetaDataToDb.newInstance(conn.getMetaData(), archive.getMetaData(), mapSchemas);
        UploadDialog ud = UploadDialog.showUploadDialog(_stage,archive,conn,false,mdtd);
        System.out.println("Success: "+String.valueOf(ud.wasSuccessful()));
      }
      catch(IOException ie) { System.err.println(EU.getExceptionMessage(ie)); }
    }
    else
      System.err.println("URL "+sUrl+" could not be loaded!");
    _stage.close();
  } /* start */

}
