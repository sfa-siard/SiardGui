package ch.enterag.utils.fx.dialogs;

import java.io.*;
import javafx.application.*;
import javafx.stage.*;
import ch.enterag.utils.*;
import ch.admin.bar.siard2.gui.*;

public class FsTester
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
    /***/
    try
    {
      System.setProperty(FS.sUSE_NATIVE_PROPERTY, "true");
      File folderInitial = new File("C:\\Users\\Hartwig\\AppData\\Local\\siard_suite_2.1\\");
      File folderChosen = FS.chooseExistingFolder(stage, 
          "Title", "Purpose of chosen folder", SiardBundle.getSiardBundle(),
          folderInitial);
      if (folderChosen != null)
        System.out.println("Existing folder chosen: "+folderChosen.getAbsolutePath());
      else
        System.out.println("Existing folder canceled");
    }
    catch(FileNotFoundException fnfe) { System.out.println(EU.getExceptionMessage(fnfe)); } 
    /***/
    /***
    try
    {
      System.setProperty(FS.sUSE_NATIVE_PROPERTY, "true");
      File folderInitial = new File("D:\\Temp\\gaga\\");
      File folderChosen = FS.chooseNewFolder(stage, 
          "Title", "Purpose of chosen folder", SiardBundle.getSiardBundle(),
          folderInitial);
      if (folderChosen != null)
        System.out.println("New folder chosen: "+folderChosen.getAbsolutePath());
      else
        System.out.println("New folder canceled");
    }
    catch(IOException ie) { System.out.println(EU.getExceptionMessage(ie)); }
    ***/
    /***
    try
    {
      System.setProperty(FS.sUSE_NATIVE_PROPERTY, "true");
      File fileInitial = new File("C:\\Users\\Hartwig\\AppData\\Roaming\\SIARD Suite 2.1\\*.siard");
      System.out.println("Initial file: "+fileInitial.getAbsolutePath());
      File fileChosen = FS.chooseExistingFile(stage, 
          "Title", "Purpose of chosen folder", SiardBundle.getSiardBundle(),
          fileInitial,"siard");
      if (fileChosen != null)
        System.out.println("Existing file chosen: "+fileChosen.getAbsolutePath());
      else
        System.out.println("Existing file canceled");
    }
    catch(FileNotFoundException fnfe) { System.out.println(EU.getExceptionMessage(fnfe)); }
    ***/
    /***
    try
    {
      System.setProperty(FS.sUSE_NATIVE_PROPERTY, "true");
      File fileInitial = new File("D:\\Temp\\guguseli.html");
      System.out.println("Initial file: "+fileInitial.getAbsolutePath());
      File fileChosen = FS.chooseNewFile(stage, 
          "Title", "Purpose of chosen folder", SiardBundle.getSiardBundle(),
          fileInitial,"html",false);
      if (fileChosen != null)
        System.out.println("New file chosen: "+fileChosen.getAbsolutePath());
      else
        System.out.println("New file canceled");
    }
    catch(IOException ie) { System.out.println(EU.getExceptionMessage(ie)); }
    ***/
    
    stage.close();
  } /* start */

} /* FsTester */
