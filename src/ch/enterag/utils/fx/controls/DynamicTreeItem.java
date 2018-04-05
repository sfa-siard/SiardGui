/*======================================================================
DynamicTreeItem is the base class adding and removing TreeItems dynamically.  
Application: SIARD GUI
Description: DynamicTreeItem is the base class adding and removing TreeItems dynamically. 
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 20.07.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.enterag.utils.fx.controls;

import java.util.*;
import javafx.beans.value.*;
import javafx.scene.control.*;

/*====================================================================*/
/** DynamicTreeItem is the base class adding and removing TreeItems dynamically.
 * @author Hartwig Thomas
 */
public abstract class DynamicTreeItem<T>
  extends TreeItem<T>
  implements ChangeListener<Boolean>
{
  /*------------------------------------------------------------------*/
  /** adds the children to this TreeItem.
   * N.B.: Must manually be called at least for root once!
   */
  protected abstract void addChildren();
  
  /*------------------------------------------------------------------*/
  /** adds the grand children to this TreeItem.
   */
  private void addGrandChildren()
  {
    for (Iterator<TreeItem<T>> iterChildren = getChildren().iterator(); iterChildren.hasNext(); )
    {
      DynamicTreeItem<T> dti = (DynamicTreeItem<T>)iterChildren.next();
      dti.addChildren();
    }
  } /* addGrandChildren */

  /*------------------------------------------------------------------*/
  /** removes the children of this TreeItem.
   */
  private void removeChildren()
  {
    getChildren().clear();
  } /* removeChildren */
  
  /*------------------------------------------------------------------*/
  /** collapses children and removes the grand children of this tree item.
   */
  private void removeGrandChildren()
  {
    for (Iterator<TreeItem<T>> iterChildren = getChildren().iterator(); iterChildren.hasNext(); )
    {
      DynamicTreeItem<T> dti = (DynamicTreeItem<T>)iterChildren.next();
      dti.setExpanded(false);
      dti.removeChildren();
    }
  } /* removeGrandChildren */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * listens for changes of the expandedProperty. 
   * @param value value object of the TreeItem (o.toString() is displayed)
   */
  public DynamicTreeItem(T value)
  {
    super(value);
    expandedProperty().addListener(this);
  } /* constructor MetaDataTreeItem */

  /*------------------------------------------------------------------*/
  /** listener for expanded property */
  @Override
  public void changed(ObservableValue<? extends Boolean> expandedProperty,
    Boolean bOldValue, Boolean bNewValue)
  {
    if (!bOldValue.booleanValue())
    {
      if (bNewValue.booleanValue())
        addGrandChildren();
    }
    else
    {
      if (!bNewValue.booleanValue())
        removeGrandChildren();
    }
  } /* changed */

} /* class DynamicTreeItem */
