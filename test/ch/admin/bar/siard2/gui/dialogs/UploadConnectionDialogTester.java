package ch.admin.bar.siard2.gui.dialogs;

import java.io.*;
import java.util.*;
import javafx.application.*;
import javafx.stage.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.primary.*;
import ch.enterag.utils.*;

public class UploadConnectionDialogTester 
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
    Archive archive = ArchiveImpl.newInstance();
    File fileArchive = new File("testfiles/sample.siard");
    archive.open(fileArchive);
    UploadConnectionDialog ucd = UploadConnectionDialog.showUploadConnectionDialog(_stage,null, null, archive);
    System.out.println("Result: "+String.valueOf(ucd.getResult()));
    if (ucd.getResult() == UploadConnectionDialog.iRESULT_SUCCESS)
    {
      System.out.println("JDBC URL: "+ucd.getConnectionUrl());
      System.out.println("DB User: "+ucd.getDbUser());
      System.out.println("DB Password: "+ucd.getDbPassword());
      System.out.println("Meta Data Only: "+String.valueOf(ucd.isMetaDataOnly()));
      System.out.println("Overwrite: "+String.valueOf(ucd.isOverwrite()));
      Map<String,String> mapSchemas = ucd.getSchemasMap();
      System.out.println("Schema mapping:");
      for (Iterator<String> iterSchema = mapSchemas.keySet().iterator(); iterSchema.hasNext(); )
      {
        String sSchema = iterSchema.next();
        System.out.println("  "+sSchema+": "+mapSchemas.get(sSchema));
      }
    }
    archive.close();
    _stage.close();
  } /* start */

} /* class UploadConnectionDialogTester */
