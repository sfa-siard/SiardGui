package ch.admin.bar.siard2.gui.dialogs;

import java.io.*;
import javafx.application.*;
import javafx.stage.*;

import ch.enterag.utils.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.primary.*;

public class ValueDialogTester
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
    int iTable = 1;
    long lRow = 0;
    int iColumn = 2;
    Cell cell = archive.getSchema(0).
      getTable(iTable).
      getRecordExtract().getRecordExtract((int)lRow).getRecord().
      getCell(iColumn);
    ValueDialog.displayValue(null, cell, archive.getMaxInlineSize());
    stage.close();
  } /* start */
  
} /* ValueDialogTester */
