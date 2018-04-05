/*======================================================================
FxStyles implements some JavaFX styles used all over. 
Application : JavaFX Utilities
Description : FxStyles implements some JavaFX styles used all over. 
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland
Created    : 12.05.2017, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.fx;

public abstract class FxStyles
{
  /** light gray background style */
  public static final String sSTYLE_BACKGROUND_LIGHTGREY = "-fx-background-color: lightgrey;";
  
  /** white background style */
  public static final String sSTYLE_BACKGROUND_WHITE = "-fx-background-color: white;";
  
  /** transparent background style */
  public static final String sSTYLE_BACKGROUND_TRANSPARENT = "-fx-background-color: transparent;";
  
  /** yellow background style (useful for debugging) */
  public static final String sSTYLE_BACKGROUND_YELLOW = "-fx-background-color: yellow;";

  /** style for status/result/error message */
  public static final String sSTYLE_MESSAGE =
      "-fx-padding:0.2em;"+
      "-fx-font-weight: bold;"+
      "-fx-text-fill: rgb(100%,75%,25%);" +
  		"-fx-background-color: dimgrey;";
  /* red text style */

  /** style for stderr output */
  public static final String sSTYLE_ERROR = "-fx-text-fill: red;";
  
  /** opacity for disabled elements */
  public static final String sSTYLE_DISABLED_OPACITY = "-fx-opacity: 0.8;";
  
  /** solid border style */
  public static final String sSTYLE_SOLID_BORDER = "-fx-border-style: solid; -fx-border-width: 1px; -fx-border-color: black;";

  /** slight right-left padding for table cells */
  // top, right, bottom, left
  public static final String sSTYLE_TABLECELL_PADDING = 
    "-fx-padding: 1 2 1 2;";
} /* FxStyles */
