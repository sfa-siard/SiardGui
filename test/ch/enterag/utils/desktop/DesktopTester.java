package ch.enterag.utils.desktop;

import java.io.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;
import ch.enterag.utils.io.SpecialFolder;
import ch.enterag.utils.lang.Execute;

public class DesktopTester
{
  private void reportLink(Desktop dt)
  {
    System.out.println("Link: "+dt.getLink().getAbsolutePath());
    System.out.println("  Executable: "+dt.getExecutable().getPath());
    System.out.println("  Arguments: "+Desktop.formatArguments(dt.getArguments()));
    for (Iterator<String> iterArgument = dt.getArguments().iterator(); iterArgument.hasNext(); )
      System.out.println("    "+iterArgument.next());
    System.out.println("  Working Directory: "+(dt.getWorkingDir() == null?"[No WorkingDir]":dt.getWorkingDir().getAbsolutePath()));
    System.out.println("  Icon: "+(dt.getIcon() == null?"[No Icon]":dt.getIcon().getAbsolutePath()));
    System.out.println("  Description: "+dt.getDescription());
    System.out.println();
  }

  @Test
  public void testCreateLinkSiardGui()
  {
    try
    {
      Desktop dt = Desktop.newInstance();
      String sJavaExecutable = "java.exe";
      if (Execute.isOsWindows())
        sJavaExecutable = "javaw.exe";
      String sJavaHome = System.getProperty("java.home");
      File fileExecutable = new File(sJavaHome + File.separator + "bin"+File.separator+sJavaExecutable);
      String sAppFolder = SpecialFolder.getUserLocalHome("siard_suite_2.1");
      List<String> listArguments = new ArrayList<String>(Arrays.asList(new String[] {
        "-Dsun.awt.disablegrab=true",
        "-Djava.util.logging.config.file="+sAppFolder+File.separator+"etc"+File.separator+"logging.properties",
        "-jar",
        sAppFolder+File.separator+"lib"+File.separator+"siardgui.jar"
      }));
      File fileIcon = new File(sAppFolder+File.separator+"siardgui.ico");
      File fileWorkingDir = new File(sAppFolder);
      String sDescription = "SiardGui for viewing and modifying archived data from relational databases";
      dt.createLink("SiardGui", fileExecutable, listArguments, fileWorkingDir, fileIcon, sDescription);
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* testCreateLinkSiardGui */
  
  @Test
  public void testParseLinkSiardGui()
  {
    try
    {
      Desktop dt = Desktop.newInstance();
      dt.parseLink("SiardGui");
      reportLink(dt);
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* testParseLinkSiardGui */

  @Test
  public void testParseAllLinks()
  {
    try
    {
      Desktop dt = Desktop.newInstance();
      File fileLinkFolder = new File(SpecialFolder.getDesktopFolder());
      File[] afileLink = fileLinkFolder.listFiles();
      for (int iLink = 0; iLink < afileLink.length; iLink++)
      {
        File fileLink = afileLink[iLink];
        if (fileLink.isFile())
        {
          String sName = fileLink.getName();
          if (sName.endsWith(Desktop.sLINK_EXTENSION))
          {
            sName = sName.substring(0,sName.length()-Desktop.sLINK_EXTENSION.length());
            dt.parseLink(sName);
            if (dt.getExecutable() != null)
              reportLink(dt);
            else
              System.err.println("Link "+sName+" could not be parsed!\n");
          }
        }
      }
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* testParseAllLinks */

}
