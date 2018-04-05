/*======================================================================
Most recently used list of files from user properties.
Application : Siard2
Description : Most recently used list of files.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2008, 2017
Created    : 10.05.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui;

import java.util.*;
import ch.enterag.utils.*;

/*====================================================================*/
/** Most recently used list of files from user properties.
 @author Hartwig Thomas
 */
public class MruFile
{
  /*====================================================================
  (public) constants
  ====================================================================*/
	public static final int iNUM_FILES = 10;
	
	/*====================================================================
  (private) data members
  ====================================================================*/
	/** ensures singleton */
	private static MruFile _mf = null;
	/** array of most recently used files */
	private final List<String> _listMruFiles = new ArrayList<String>();
	
	/*====================================================================
  constructor
  ====================================================================*/
	private MruFile()
	{
		load();
	} /* constructor MruFile */
	
	/*====================================================================
  methods
  ====================================================================*/
	/*------------------------------------------------------------------*/
	/** get number of MRU file entries.
	 * @return number of entries.
	 */
	public int getMruFiles()
	{
		return _listMruFiles.size();
	} /* getMruFiles */
	
	/*------------------------------------------------------------------*/
	/** get a MRU file entry.
	 * @param iIndex indicates which entry is to be returned.
	 * @return file path.
	 */
	public String getMruFile(int iIndex)
	{
		String sMruFile = null;
		if ((iIndex >= 0) && (iIndex < _listMruFiles.size()))
		  sMruFile = _listMruFiles.get(iIndex);
		return sMruFile;
	} /* getMruFile */
	
	/*------------------------------------------------------------------*/
	/** set a MRU file entry.
	 * @param sMruFile file path to be added to MRU list.
	 */
	public void setMruFile(String sMruFile)
	{
		if ((_listMruFiles.size() == 0) || (!sMruFile.equals(_listMruFiles.get(0))))
		{
			int iPrevious = _listMruFiles.indexOf(sMruFile);
			if (iPrevious > 0)
				_listMruFiles.remove(iPrevious);
			_listMruFiles.add(0, sMruFile);
		}
	} /* setMruFile */
	
	/*------------------------------------------------------------------*/
	/** load the list form the user properties.
	 */
	public void load()
	{
		_listMruFiles.clear();
		for (int iIndex = 0; iIndex < iNUM_FILES; iIndex++)
		{
			String sMruFile = UserProperties.getUserProperties().getMruFile(iIndex);
			if (SU.isNotWhite(sMruFile))
				_listMruFiles.add(sMruFile);
		}
	} /* load */
	
	/*------------------------------------------------------------------*/
	/** store the list in the user properties.
	 */
	public void store()
	{
		for (int iIndex = 0; iIndex < _listMruFiles.size(); iIndex++)
		{
			String sMruFile = _listMruFiles.get(iIndex);
			UserProperties.getUserProperties().setMruFile(iIndex, sMruFile);
		}
	} /* store */
	
	/*====================================================================
  factory
  ====================================================================*/
	public static MruFile getMruFile()
	{
		if (_mf == null)
			_mf = new MruFile();
		return _mf;
	} /* getMruFile */

} /* MruFile */
