/*======================================================================
FxBundle implements a few basic general constants needed by JavaFX. 
Application : JavaFX Utilities
Description : FxBundle implements a few basic general constants needed by JavaFX. 
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland
Created    : 21.12.2015, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.fx;

import ch.enterag.utils.resource.*;

/*====================================================================*/
/** FxBundle implements a few basic general constants needed by JavaFX.
 * @author Hartwig Thomas
 */
public class FxBundle extends Bundle
{
  /*------------------------------------------------------------------*/
  /**  {@link ch.enterag.utils.resource.Bundle#Bundle(Class,String) Bundle}
   */
  protected FxBundle(Class<?> clsResource, String sResource)
  {
    super(clsResource, sResource);
  } /* constructor FxBundle */
  
  /*------------------------------------------------------------------*/
  /** factory creates an FxBundle instance.
   * @param clsResource class relative to which the resources are to be loaded.
   * @param sResource path name from which resources are loaded without 
   * language postfix (e.g. "_en") and without implicit ".properties" extension.
   * If no resource with the given language postfix exists, the resource 
   * without any postfix is used, which must be available.
   * All ".properties" files must be stored with UTF-8 encoding.  
   * @return new FxBundle instance.
   */
  public static FxBundle getFxBundle(Class<?> clsResource, String sResource)
  {
    return new FxBundle(clsResource, sResource);
  } /* getFxBundle */
  
  public String getOk() { return getProperty("fx.ok"); }
  public String getCancel() { return getProperty("fx.cancel"); }
  public String getYes() { return getProperty("fx.yes"); }
  public String getNo() { return getProperty("fx.no"); }
  public String getPathLabel() { return getProperty("fx.path.label"); }
  public String getFileLabel() { return getProperty("fx.file.label"); }
  public String getFolderLabel() { return getProperty("fx.folder.label"); }
  public String getDateFormat() { return getProperty("fx.date.format"); }
  public String getAllFiles() { return getProperty("fx.all.files"); }
  public String getFiles(String sExtension) { return getProperty("fx."+sExtension+".files"); }
  public String getOverwriteQuery() { return getProperty("fx.overwrite.query"); }

} /* class FxBundle */
