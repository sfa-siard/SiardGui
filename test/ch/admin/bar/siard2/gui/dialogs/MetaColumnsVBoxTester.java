package ch.admin.bar.siard2.gui.dialogs;

import java.io.File;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.primary.*;
import javafx.application.*;
import javafx.beans.value.*;
import javafx.scene.*;
import javafx.stage.*;

public class MetaColumnsVBoxTester
  extends Application
  implements ChangeListener<Boolean>
{
  /** return codes */
  private static final int iRETURN_OK = 0;
  private static final int iRETURN_ERROR = 8;
  private MetaColumnsVBox _mcvbox = null;
  
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    int iReturn = iRETURN_ERROR;
    try
    {
      launch(args);
      iReturn = iRETURN_OK;
    }
    catch (Exception e) { System.err.println(e.getClass().getName()+": "+e.getMessage()); }
    System.exit(iReturn);
  }

  @Override
  public void start(Stage stage) throws Exception
  {
    stage.initStyle(StageStyle.UTILITY);
    File fileArchive = new File("testfiles/sample.siard");
    Archive archive = ArchiveImpl.newInstance();
    archive.open(fileArchive);
    MetaTable mt = archive.getMetaData().getMetaSchema(0).getMetaTable(0);
    _mcvbox = new MetaColumnsVBox("Explanation",mt,"SelectAll","SelectNone");
    _mcvbox.addSelectionChangeListener(this);
    Scene scene = new Scene(_mcvbox,_mcvbox.getMinWidth()+10.0,_mcvbox.getMinHeight()+10.0);
    stage.setScene(scene);
    /* if resizable is set to false, the size increases magically by 10! */
    stage.setResizable(true);
    stage.toFront();
    stage.show();
  } /* start */

  @Override
  public void changed(ObservableValue<? extends Boolean> ovb0,
      Boolean bOld, Boolean bNew)
  {
    System.out.println("Change!");
    if (!bNew)
    {
      if (_mcvbox.getSelection().size() == 0)
      {
        System.out.println("Empty!");
      }
    }
  }

} /* class MetaColumnsVBoxTester */
