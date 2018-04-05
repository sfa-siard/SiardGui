package ch.enterag.utils;

import java.io.*;
import org.junit.*;

public class FileTester
{

  @Test
  public void testRoots()
  {
    File[] afile = File.listRoots();
    for (int iRoot = 0; iRoot < afile.length; iRoot++)
    {
      File fileRoot = afile[iRoot];
      System.out.println("Root: "+fileRoot.getAbsolutePath());
      File[] afileTop = fileRoot.listFiles();
      if (afileTop != null)
      {
        
        for (int iFile = 0; iFile < afileTop.length; iFile++)
        {
          File fileTop = afileTop[iFile];
          if (!fileTop.isHidden())
            System.out.println("  "+fileTop.getAbsolutePath());
        }
      }
    }
  }
  
  @Test
  public void testRoot()
  {
    File fileRoot = new File("/");
    System.out.println("Name: \""+fileRoot.getName()+"\"");
    System.out.println("Path: \""+fileRoot.getPath()+"\"");
    System.out.println("toString: \""+fileRoot.toString()+"\"");
    System.out.println("Absolute: \""+fileRoot.getAbsolutePath()+"\"");
    File fileOther = new File("/C:");
    System.out.println("Name: \""+fileOther.getName()+"\"");
    System.out.println("Path: \""+fileOther.getPath()+"\"");
    System.out.println("toString: \""+fileOther.toString()+"\"");
    System.out.println("Absolute: \""+fileOther.getAbsolutePath()+"\"");
  }
  
  @Test
  public void testNew()
  {
    File fileNew = new File("D:\\Temp\\gaga\\gugus.txt\\");
    System.out.println(fileNew.getAbsolutePath()+": "+fileNew.isFile()+" "+fileNew.isDirectory());
  }
  
}
