package ch.enterag.utils.lang;

import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.*;
import org.junit.*;

import ch.admin.bar.siard2.gui.actions.InstallUninstallHandler;
import ch.enterag.utils.io.SpecialFolder;

public class ScriptTester
{

  private void runScript(File file)
  {
    /* execution directory of script */
    File folderScript = SpecialFolder.getJarFromClass(InstallUninstallHandler.class,false);
    folderScript = new File(folderScript.getParentFile().getAbsolutePath()+File.separator+file.getName()).getParentFile();
    System.out.println("Script folder: "+folderScript.getAbsolutePath());
    /* PATH */
    String sPath = System.getenv("PATH");
    String[] asPath = sPath.split(";");
    Arrays.sort(asPath);
    System.out.println("PATH before:");
    for (int i = 0; i < asPath.length; i++)
      System.out.println("  "+asPath[i].trim());
    Execute ex = Execute.execute(file.getAbsolutePath());
    System.out.println("Result: "+String.valueOf(ex.getResult()));
    System.out.println("Error: "+ex.getStdErr());
    System.out.println("Output: "+ex.getStdOut());
  } /* runScript */
  
  @Test
  public void testDesktopCmd()
  {
    File fileShortcut = new File("C:\\Users\\"+System.getProperty("user.name")+"\\Desktop\\SiardGui.lnk");
    if (fileShortcut.exists())
      fileShortcut.delete();
    /* run the desktop command */
    runScript(new File("bin/desktop.cmd"));
    assertTrue("Shortcut has not been created!",fileShortcut.exists());
  }

  @Test
  public void testUndesktopCmd()
  {
    File fileShortcut = new File("C:\\Users\\"+System.getProperty("user.name")+"\\Desktop\\SiardGui.lnk");
    runScript(new File("bin/undesktop.cmd"));
    assertTrue("Shortcut has not been deleted!",!fileShortcut.exists());
  }

}
