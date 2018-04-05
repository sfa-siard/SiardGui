package ch.admin.bar.siard2.gui;

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javafx.embed.swing.*;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import ch.enterag.utils.*;

public class SplashPane 
  extends Pane
{
  private static final String sSPLASH_IMAGE_RESOURCE = "res/splash.png";
  /*------------------------------------------------------------------*/
  /** constructor
   */
  private SplashPane()
  {
    super();
    ImageView ivSplash = new ImageView(new Image(SplashPane.class.getResourceAsStream(sSPLASH_IMAGE_RESOURCE)));
    getChildren().add(ivSplash);
    double dImageWidth = ivSplash.getBoundsInParent().getWidth();
    double dImageHeight = ivSplash.getBoundsInParent().getHeight();
    Label lblMain = new Label(SiardGui.getApplication()+" "+SiardGui.getVersion());
    double dDefaultSize = Font.getDefault().getSize();
    lblMain.setFont(Font.font(null, FontWeight.SEMI_BOLD, 2.3*dDefaultSize));
    lblMain.setLayoutX(0.37*dImageWidth);
    lblMain.setLayoutY(0.09*dImageHeight);
    getChildren().add(lblMain);
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** factory
   * @return new splash pane.
   */
  public static SplashPane newSplashPane()
  {
    return new SplashPane();
  } /* newSplashPane */

  /*------------------------------------------------------------------*/
  /** save a snapshot of the splash pane as a PNG file.
   * @param filePng PNG file to write.
   */
  public static void saveSnapshot(File filePng)
  {
    SplashPane sp = newSplashPane();
    new Scene(sp);
    WritableImage wi = sp.snapshot(null, null);
    RenderedImage ri = SwingFXUtils.fromFXImage(wi, null);
    try { ImageIO.write(ri, "png", filePng); }
    catch(IOException ie) { System.err.println(EU.getExceptionMessage(ie)); }
  } /* saveSnapshot */
  
} /* class SplashPane */
