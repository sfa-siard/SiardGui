package ch.admin.bar.siard2.gui.dialogs;

import ch.enterag.utils.EU;
import javafx.application.*;
import javafx.stage.Stage;

public class OptionDialogTester
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
    OptionDialog od = OptionDialog.showOptionDialog(stage);
    System.out.println("Canceled: "+String.valueOf(od.isCanceled()));
  } /* start */
  
} /* OptionDialogTester */
