/*======================================================================
The editor HBox of a single property of a meta data object.  
Application: SIARD GUI
Description: The editor BHox of a single property of a meta data object. 
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 25.07.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.details;

import java.util.*;

import javafx.beans.*;
import javafx.beans.value.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import ch.enterag.utils.fx.*;
import ch.enterag.utils.fx.dialogs.*;
import ch.admin.bar.siard2.gui.*;

/*====================================================================*/
/** The editor BHox of a single property of a meta data object.
 * @author Hartwig Thomas
 */
public class PropertyHBox
  extends HBox
  implements ObservableValue<String>
{
  // padding inside
  private static final double dINNER_PADDING = 0.0;
  // horizontal spacing of elements
  private static final double dHSPACING = 10.0;
  // columns for text field
  private static int iCOLUMNS = 40;
  // rows for text area
  private static int iROWS = 5;
  /** invalidation listener belongs to being observable */  
  private Set<InvalidationListener> _setIvl = new HashSet<InvalidationListener>(); 
  @Override public void addListener(InvalidationListener il) { _setIvl.add(il); }
  @Override public void removeListener(InvalidationListener il) { _setIvl.remove(il); }
  /** change listener belongs to being observable */
  private Set<ChangeListener<? super String>> _setCl = new HashSet<ChangeListener<? super String>>(); 
  @Override public void addListener(ChangeListener<? super String> cl) { _setCl.add(cl); }
  @Override public void removeListener(ChangeListener<? super String> cl) { _setCl.remove(cl); }

  /** the label control */
  private Label _lbl = null;
  /** the text field or text area */
  private TextInputControl _tic = null;
  
  private String _sProperty = null;
  public String getProperty() { return _sProperty; }
  private boolean _bEditable = false;
  public boolean isEditable() { return _bEditable; }
  private boolean _bMandatory = false;
  
  /*==================================================================*/
  private class TextChangeListener implements ChangeListener<String>
  {
    /*------------------------------------------------------------------*/
    /** Reacts to a change in the textProperty of _tf or _ta: 
     * Passes notification on to all ChangeListeners of this observable.
     * @param ov observable value (text property of _tf or _ta).
     * @param sOld old value.
     * @param sNew new value.
     */
    @Override
    public void changed(ObservableValue<? extends String> ov,
        String sOld, String sNew)
    {
      if (_bMandatory && ((sNew == null) || (sNew.trim().length() == 0)))
      {
        SiardBundle sb = SiardBundle.getSiardBundle();
        MB.show((Stage)getScene().getWindow(),
          sb.getEditMetaDataTitle(),
          sb.getEditMetaDataMandatory(),
          sb.getOk(), null);
        _tic.setText(sOld);
      }
      else
      {
        // "OuterName.this" refers to the outer object from within a nested class object
        for (ChangeListener<? super String> cl : _setCl)
          cl.changed(PropertyHBox.this, sOld, sNew);
      }
    } /* changed */
  } /* class TextChangeListener */
  /*==================================================================*/
  private TextChangeListener _tcl = new TextChangeListener();

  /*------------------------------------------------------------------*/
  /** constructor
   * @param sProperty property name.
   * @param sLabel label text.
   * @param dLabelWidth width of label text.
   * @param sValue property value.
   * @param bEditable true if value is editable.
   * @param bMultiline true, if multi-line textarea is needed.
   * @param bMandatory true, if text must not be empty.
   */
  public PropertyHBox(String sProperty, String sLabel, double dLabelWidth,
    String sValue, boolean bEditable, boolean bMultiline, boolean bMandatory)
  {
    super();
    setPadding(new Insets(dINNER_PADDING));
    setSpacing(dHSPACING);
    _sProperty = sProperty;
    _bEditable = bEditable;
    TextField tf = new TextField();
    tf.setText(sValue);
    tf.setPrefColumnCount(iCOLUMNS);
    double dTextHeight = FxSizes.getNodeHeight(tf);
    tf.setMinHeight(dTextHeight);
    if (!bMultiline)
      _tic = tf;
    else
    {
      TextArea ta = new TextArea();
      ta.setText(sValue);
      ta.setWrapText(true);
      ta.setPrefColumnCount(iCOLUMNS);
      if (_bEditable)
        ta.setPrefRowCount(iROWS);
      else
      {
        int iRows = (int)Math.ceil(FxSizes.getTextHeight(sValue)/FxSizes.getTextHeight("X"));
        if (iRows > iROWS)
          iRows = iROWS;
        ta.setPrefRowCount(iRows);
      }
      _tic = ta;
    }
    _tic.setEditable(_bEditable);
    _tic.setFocusTraversable(_bEditable);
    if (_bEditable)
      _tic.textProperty().addListener(_tcl);
    else
      _tic.setStyle(FxStyles.sSTYLE_BACKGROUND_LIGHTGREY);
    _bMandatory = bMandatory;
    if (_bMandatory)
      sLabel = sLabel + "*";
    _lbl = new Label(sLabel);
    _lbl.setLabelFor(_tic);
    _lbl.setAlignment(Pos.BASELINE_RIGHT);
    _lbl.setMinWidth(dLabelWidth);
    _lbl.setMinHeight(dTextHeight);
    getChildren().add(_lbl);
    getChildren().add(_tic);
    setMinWidth(computeMinWidth(getHeight()));
    setMinHeight(computeMinHeight(getWidth()));
  } /* constructor PropertyHBox */

  /*------------------------------------------------------------------*/
  /** the edited value of the property.
   */
  @Override
  public String getValue()
  {
    String sValue = _tic.getText();
    return sValue;
  } /* getValue */

  /*------------------------------------------------------------------*/
  /** select a substring of the value.
   * @param iStartOffset start of selection.
   * @param iEndOffset end of selection.
   */
  public void selectRange(int iStartOffset, int iEndOffset)
  {
    _tic.requestFocus();
    _tic.selectRange(iStartOffset, iEndOffset);
  } /* selectRange */

  /*------------------------------------------------------------------*/
  /** clear any selection.
   */
  public void deselect()
  {
    _tic.deselect();
  } /* deselect */
  
} /* PropertyHBox */
