/*====================================================================== 
FindAction handles finding strings in the meta data. 
Application: SIARD GUI
Description: FindAction handles finding strings in the meta data. 
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 16.08.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.actions;

import java.io.*;

import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.gui.*;
import ch.admin.bar.siard2.gui.dialogs.*;

/*====================================================================*/
/** FindAction handles finding strings in the meta data.
 * @author Hartwig Thomas
 */
public class FindAction
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(FindAction.class.getName());
  
  /*------------------------------------------------------------------*/
  /** constructor
   */
  private FindAction()
  {
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** factory
   * @return new instance of FindAction.
   */
  public static FindAction newFindAction()
  {
    FindAction fa = new FindAction();
    return fa;
  } /* factory */

  /*------------------------------------------------------------------*/
  /** prepare finding a string in the meta data.
   */
  public boolean find()
  {
    SiardGui sg = SiardGui.getSiardGui();
    UserProperties up = UserProperties.getUserProperties();
    FindDialog fd = FindDialog.showFindDialog(sg.getStage(),
      up.getFindString(),
      up.getFindMatchCase());
    if (!fd.isCanceled())
    {
      String sFindString = fd.getFindString();
      boolean bMatchCase = fd.mustMatchCase();
      up.setFindString(sFindString);
      up.setFindMatchCase(bMatchCase);
      try { sg.getArchive().getMetaData().find(sFindString, bMatchCase); }
      catch(IOException ie) { _il.exception(ie); }
    }
    return !fd.isCanceled();
  } /* find */
  
} /* class FindAction */
