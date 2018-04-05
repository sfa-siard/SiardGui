/*======================================================================
ConsolePrintStream implements a TextArea as an output "console". 
Application : Siard2
Description : ConsolePrintStream implements a TextArea as an output "console". 
Platform    : Java 7, JavaFX 2.2   
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 27.06.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.enterag.utils.fx.controls;

import java.io.*;
import java.text.*;
import java.util.*;
import javafx.application.*;
import javafx.scene.control.*;

/*====================================================================*/
/** ConsolePrintStream implements a TextArea as an output "console".
 * @author Hartwig Thomas
 */
public class ConsolePrintStream
  extends PrintStream
{
  private static String sLINE_SEPARATOR = System.getProperty("line.separator");
  private int _iMaxCharacters = 100000; /* 0.2 MB (1 char = 2 bytes) */
  public int getMaxCharacters() { return _iMaxCharacters; }
  public void setMaxCharacters(int iMaxCharacters) { _iMaxCharacters = iMaxCharacters; }
  
  /** TextArea control */
  private TextArea _ta = null;
  public TextArea getTextArea() { return _ta; }
  public void setTextArea(TextArea ta) { _ta = ta; }
  
  private StringBuilder _sb = new StringBuilder();
  /*==================================================================*/
  private class runAppend 
    implements Runnable
  {
    public runAppend() {}
    
    /*----------------------------------------------------------------*/
    /** delete everything before and including the next new line.
     * @param s string to be pruned.
     * @return pruned string.
     */
    private void prune()
    {
      int i = -1;
      for (i = 0; (i < _ta.getText().length()) && (_ta.getText().charAt(i) != '\n'); i++) {}
      _ta.deleteText(0,i+1);
    } /* prune */
    
    /*----------------------------------------------------------------*/
    /** run appends the accumulated text in the StringBuilder to the
     * TextArea control.
     */
    public void run()
    {
      String sAppend = null;
      synchronized(_sb)
      {
        sAppend = _sb.toString();
        _sb.setLength(0);
      }
      while (_ta.getText().length() + sAppend.length() > getMaxCharacters())
        prune();
      _ta.appendText(sAppend);
      _ta.end();
      _ta.requestFocus();
    }
  } /* runAppend */
  /*==================================================================*/
  /** no reason, why it cannot be used more than once ... */
  private runAppend _ra = new runAppend();

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PrintStream append(char c)
  {
    synchronized(_sb) { _sb.append(c); }
    Platform.runLater(_ra);
    return this;
  } /* append */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PrintStream append(CharSequence cs)
  {
    synchronized(_sb) { _sb.append(cs); }
    Platform.runLater(_ra);
    return this;
  } /* append */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PrintStream append(CharSequence cs, int iStart, int iEnd)
  {
    synchronized(_sb) { _sb.append(cs, iStart, iEnd); }
    Platform.runLater(_ra);
    return this;
  } /* append */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PrintStream format(Locale loc, String sFormat, Object... args)
  {
    /* we ignore the locale which only influences some of the pattern syntax */
    return format(sFormat, args);
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PrintStream format(String sFormat, Object... args)
  {
    synchronized(_sb) { _sb.append(MessageFormat.format(sFormat, args)); }
    Platform.runLater(_ra);
    return this;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PrintStream printf(Locale loc, String sFormat, Object... args)
  {
    return format(loc, sFormat, args);
  } /* printf */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PrintStream printf(String sFormat, Object... args)
  {
    return format(sFormat, args);
  } /* printf */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void print(boolean b)
  {
    synchronized(_sb) { _sb.append(b); }
    Platform.runLater(_ra);
  } /* print */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void print(char c)
  {
    synchronized(_sb) { _sb.append(c); }
    Platform.runLater(_ra);
  } /* print */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void print(char[] cbuf)
  {
    synchronized(_sb) { _sb.append(cbuf); }
    Platform.runLater(_ra);
  } /* print */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void print(double d)
  {
    synchronized(_sb) { _sb.append(d); }
    Platform.runLater(_ra);
  } /* print */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void print(float f)
  {
    synchronized(_sb) { _sb.append(f); }
    Platform.runLater(_ra);
  } /* print */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void print(int i)
  {
    synchronized(_sb) { _sb.append(i); }
    Platform.runLater(_ra);
  } /* print */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void print(long l)
  {
    synchronized(_sb) { _sb.append(l); }
    Platform.runLater(_ra);
  } /* print */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void print(Object o)
  {
    synchronized(_sb) { _sb.append(o); }
    Platform.runLater(_ra);
  } /* print */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void print(String s)
  {
    synchronized(_sb) { _sb.append(s); }
    Platform.runLater(_ra);
  } /* print */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void println()
  {
    synchronized(_sb) { _sb.append(sLINE_SEPARATOR); }
    Platform.runLater(_ra);
  } /* println */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void println(boolean b)
  {
    synchronized(_sb) { _sb.append(b); _sb.append(sLINE_SEPARATOR); }
    Platform.runLater(_ra);
  } /* println */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void println(char c)
  {
    synchronized(_sb) { _sb.append(c); _sb.append(sLINE_SEPARATOR); }
    Platform.runLater(_ra);
  } /* println */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void println(char[] cbuf)
  {
    synchronized(_sb) { _sb.append(cbuf); _sb.append(sLINE_SEPARATOR); }
    Platform.runLater(_ra);
  } /* println */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void println(double d)
  {
    synchronized(_sb) { _sb.append(d); _sb.append(sLINE_SEPARATOR); }
    Platform.runLater(_ra);
  } /* println */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void println(float f)
  {
    synchronized(_sb) { _sb.append(f); _sb.append(sLINE_SEPARATOR); }
    Platform.runLater(_ra);
  } /* println */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void println(int i)
  {
    synchronized(_sb) { _sb.append(i); _sb.append(sLINE_SEPARATOR); }
    Platform.runLater(_ra);
  } /* println */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void println(long l)
  {
    synchronized(_sb) { _sb.append(l); _sb.append(sLINE_SEPARATOR); }
    Platform.runLater(_ra);
  } /* println */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void println(Object o)
  {
    synchronized(_sb) { _sb.append(o); _sb.append(sLINE_SEPARATOR); }
    Platform.runLater(_ra);
  } /* println */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void println(String s)
  {
    synchronized(_sb) { _sb.append(s); _sb.append(sLINE_SEPARATOR); }
    Platform.runLater(_ra);
  } /* println */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void write(int b)
  {
    throw new IllegalArgumentException("write() must not be called on ConsolePrintStream!");
  } /* write */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void write(byte[] buf)
  {
    throw new IllegalArgumentException("write() must not be called on ConsolePrintStream!");
  } /* write */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void write(byte[] buf, int iOffset, int iLength)
  {
    throw new IllegalArgumentException("write() must not be called on ConsolePrintStream!");
  } /* write */
  
  /*------------------------------------------------------------------*/
  /** constructor
   */
  private ConsolePrintStream(TextArea ta)
  {
    super(new ByteArrayOutputStream());
    _ta = ta;
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** factory
   * @return new ConsolePrintStream instance.
   */
  public static ConsolePrintStream newConsolePrintStream(TextArea ta)
  {
    return new ConsolePrintStream(ta);
  } /* newConsolePrintStream */
  
} /* ConsolePrintStream */
