package ch.enterag.utils.lang;

import java.util.*;

public abstract class RandomTestUtils
{
  public static String sLINE_SEPARATOR = System.getProperty("line.separator");

  private static Random _random = new Random(3); // seeding makes debugging reproducible
  
  private static String getSymbols()
  {
    StringBuilder sbSymbols = new StringBuilder();
    sbSymbols.append('\t');
    for (int i = 32; i < 127; i++)
      sbSymbols.append((char)i);
    sbSymbols.append("üÜöÖäÄ");
    return sbSymbols.toString();
  }
  private static final String sSYMBOLS = getSymbols();
  
  public static String getRandomString(int iLength)
  {
    StringBuilder sbRandom = new StringBuilder();
    for (int i = 0; i < iLength; i++)
    {
      int iCode = (int)Math.floor(sSYMBOLS.length()*_random.nextDouble());
      sbRandom.append(sSYMBOLS.charAt(iCode));
    }
    return sbRandom.toString();
  }
  
  public static String getRandomString()
  {
    int iLength = (int)Math.floor(120.0*_random.nextDouble());
    return getRandomString(iLength);
  } /* getRandomString */
  
  public static String getRandomText(int iRows)
  {
    StringBuilder sbRandomText = new StringBuilder();
    for (int i = 0; i < iRows; i++)
    {
      sbRandomText.append(getRandomString());
      sbRandomText.append(sLINE_SEPARATOR);
    }
    return sbRandomText.toString();
  } /* getRandomText */

} /* RandomTestUtils */
