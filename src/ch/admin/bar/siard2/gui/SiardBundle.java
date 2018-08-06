/*====================================================================== 
SiardBundle implements the multilingual string pool for the SIARD GUI. 
Application: SIARD GUI
Description: SiardBundle implements the multilingual string pool for 
             the SIARD GUI.
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 05.05.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui;

import java.io.*;
import java.text.*;
import ch.enterag.utils.*;
import ch.enterag.utils.fx.*;

/*====================================================================*/
/** SiardBundle implements the multilingual string pool for the SIARD GUI.
 @author Hartwig Thomas
 */
public class SiardBundle extends FxBundle
{
  /** singleton */
  private static SiardBundle _sb = null;

  /*------------------------------------------------------------------*/
  /** constructor
   */
  private SiardBundle()
  {
    super(SiardBundle.class,"res/Siard");
  } /* constructor SiardBundle */
  
  /*------------------------------------------------------------------*/
  /** factory
   @return SiardBundle instance.
   */
  public static SiardBundle getSiardBundle()
  {
    if (_sb == null)
      _sb = new SiardBundle();
    return _sb;
  } /* getInstance */

  /*------------------------------------------------------------------*/
  /** a property that will be used as a format, must have duplicated apostrophes.
   * @param sKey property key
   * @return property value with duplicated apostrophes.
   */
  public String getPropertyFormat(String sKey)
  {
    String sProperty = getProperty(sKey);
    return sProperty.replaceAll("'", "''");
  } /* getProperty */
  
  public String[] getMainLanguages() { return getProperty("main.languages").split(","); }
  public String getLanguage(String sLanguage) { return getProperty("language."+sLanguage); }
  public String getMenuFile() { return getProperty("menu.file"); }
  public String getMenuFileDownload() { return getProperty("menu.file.download"); }
  public String getMenuFileDownloadMru() { return getProperty("menu.file.downloadmru"); }
  public String getMenuFileOpen() { return getProperty("menu.file.open"); }
  public String getMenuFileOpenMru() { return getProperty("menu.file.openmru"); }
  public String getMenuFileSave() { return getProperty("menu.file.save"); }
  public String getMenuFileDisplayMetaData() { return getProperty("menu.file.displaymd"); }
  public String getMenuFileAugmentMetaData() { return getProperty("menu.file.augmentmd"); }
  public String getMenuFileUpload() { return getProperty("menu.file.upload"); }
  public String getMenuFileUploadMru() { return getProperty("menu.file.uploadmru"); }
  public String getMenuFileClose() { return getProperty("menu.file.close"); }
  public String getMenuFileExit() { return getProperty("menu.file.exit"); }
  public String getMenuEdit() { return getProperty("menu.edit"); }
  public String getMenuEditCopyAll() { return getProperty("menu.edit.copyall"); }
  public String getMenuEditCopyRow() { return getProperty("menu.edit.copyrow"); }
  public String getMenuEditExportTable() { return getProperty("menu.edit.exporttable"); }
  public String getMenuEditFind() { return getProperty("menu.edit.find"); }
  public String getMenuEditFindNext() { return getProperty("menu.edit.findnext"); }
  public String getMenuEditSearch() { return getProperty("menu.edit.search"); }
  public String getMenuEditSearchNext() { return getProperty("menu.edit.searchnext"); }
  public String getMenuTools() { return getProperty("menu.tools"); }
  public String getMenuToolsInstall() { return getProperty("menu.tools.install"); }
  public String getMenuToolsUninstall() { return getProperty("menu.tools.uninstall"); }
  public String getMenuToolsLanguage() { return getProperty("menu.tools.language"); }
  public String getMenuToolsIntegrity() { return getProperty("menu.tools.integrity"); }
  public String getMenuToolsOptions() { return getProperty("menu.tools.options"); }
  public String getMenuHelp() { return getProperty("menu.help"); }
  public String getMenuHelpHelp() { return getProperty("menu.help.help"); }
  public String getMenuHelpInfo() { return getProperty("menu.help.info"); }
  
  public String getInfoTitle() { return getProperty("info.title"); }
  public String getInfoSubject() { return getProperty("info.subject"); }
  public String getInfoDescription() { return getProperty("info.description"); }
  public String getInfoCreatorsLabel() { return getProperty("info.creators.label"); }
  public String getInfoContributorsLabel() { return getProperty("info.contributors.label"); }
  public String getInfoProvenancesLabel() { return getProperty("info.provenances.label"); }
  
  public String getHelpTitle() { return getProperty("help.title"); }
  public String getHelpErrorTitle() { return getProperty("help.error.title"); }
  public String getHelpErrorMessage(File fileManual) 
  { 
    return MessageFormat.format(getPropertyFormat("help.error.message"), 
      fileManual.getAbsolutePath()); 
  }
  
  public String getInstalledNoneTitle() { return getProperty("installed.none.title"); }
  public String getInstalledNoneMessage(File fileProperties, String sNewVersion) 
  {
    return MessageFormat.format(getPropertyFormat("installed.none.message"),
      fileProperties.getAbsolutePath(),sNewVersion); 
  }
  public String getInstalledOlderTitle() { return getProperty("installed.older.title"); }
  public String getInstalledOlderMessage(String sInstalledVersion, File fileProperties, 
    File fileInstalledPath, String sNewVersion) 
  { 
    return MessageFormat.format(getPropertyFormat("installed.older.message"),
      sInstalledVersion, fileProperties.getAbsolutePath(), 
      fileInstalledPath.getAbsolutePath(), sNewVersion); 
  }
  public String getInstalledNewerTitle() { return getProperty("installed.newer.title"); }
  public String getInstalledNewerMessage(String sNewVersion, File fileProperties, 
    File fileInstalledPath, String sOldVersion) 
  { 
    return MessageFormat.format(getPropertyFormat("installed.newer.message"),
      sNewVersion,fileProperties.getAbsolutePath(),fileInstalledPath.getAbsolutePath(),sOldVersion); 
  }

  public String getInstallationSelectorTitle() { return getProperty("installation.selector.title"); }
  public String getInstallationSelectorMessage() { return getProperty("installation.selector.message"); }

  public String getInstallationNotemptyTitle() { return getProperty("installation.notempty.title"); }
  public String getInstallationNotemptyMessage() { return getProperty("installation.notempty.message"); }
  
  public String getInstallingStatus(File fileInstallationFolder) 
  { 
    return MessageFormat.format(getPropertyFormat("installing.status"),
      fileInstallationFolder.getAbsolutePath()); 
  }
  public String getUninstallingStatus(File fileInstallationFolder)
  { 
    return MessageFormat.format(getPropertyFormat("uninstalling.status"),
      fileInstallationFolder.getAbsolutePath());
  }

  public String getInstallationErrorTitle() { return getProperty("installation.error.title"); }
  public String getInstallationErrorCopyMessage(File fileInstallationFolder) 
  {
    return MessageFormat.format(getPropertyFormat("installation.error.copy.message"),
      fileInstallationFolder.getAbsolutePath()); 
  }
  public String getInstallationErrorDesktopMessage(String sError)
  { 
    return MessageFormat.format(getPropertyFormat("installation.error.desktop.message"),
      sError);
  }
  public String getInstallationSuccessTitle() { return getProperty("installation.success.title"); }
  public String getInstallationSuccessMessage(File fileInstallationFolder)
  {
    return MessageFormat.format(getPropertyFormat("installation.success.message"),
      fileInstallationFolder.getAbsolutePath());
  }
  public String getInstallationSuccessDesktopMessage(File fileInstallationFolder)
  {
    return MessageFormat.format(getPropertyFormat("installation.success.desktop.message"),
      fileInstallationFolder.getAbsolutePath());
  }

  public String getUninstallationTitle() { return getProperty("uninstallation.title"); }
  public String getUninstallationCompleteQuery() { return getProperty("uninstallation.complete.query"); }
  public String getUninstallationErrorTitle() { return getProperty("uninstallation.error.title"); }
  public String getUninstallationErrorImpossibleMessage() { return getProperty("uninstallation.error.impossible.message"); }
  public String getUninstallationErrorDesktopMessage(String sError)
  {
    return MessageFormat.format(getPropertyFormat("uninstallation.error.desktop.message"),sError); 
  }
  public String getUninstallationErrorSettingsMessage(File fileProperties) 
  {
    return MessageFormat.format(getPropertyFormat("uninstallation.error.settings.message"),fileProperties.getAbsolutePath());
  }
  public String getUninstallationSuccessTitle() { return getProperty("uninstallation.success.title"); }
  public String getUninstallationSuccessFilesMessage(File fileInstallationFolder, File fileProperties) 
  {
    return MessageFormat.format(getPropertyFormat("uninstallation.success.files.message"),
      fileInstallationFolder.getAbsolutePath(), fileProperties.getAbsolutePath()); 
  }
  public String getUninstallationSuccessSettingsMessage(File fileInstallationFolder, File fileProperties)
  { 
    return MessageFormat.format(getPropertyFormat("uninstallation.success.settings.message"),
      fileInstallationFolder.getAbsolutePath(), fileProperties.getAbsolutePath());
  }

  public String getConnectionDownloadTitle() { return getProperty("connection.download.title"); }
  public String getConnectionUploadTitle() { return getProperty("connection.upload.title"); }
  public String getConnectionDbSchemeLabel() { return getProperty("connection.dbscheme.label"); }
  public String getConnectionDbSchemeTooltip() { return getProperty("connection.dbscheme.tooltip"); }
  public String getConnectionDbHostLabel() { return getProperty("connection.dbhost.label"); }
  public String getConnectionDbNameLabel() { return getProperty("connection.dbname.label"); }
  public String getConnectionDbFolderLabel() { return getProperty("connection.dbfolder.label"); }
  public String getConnectionDbFolderTitle() { return getProperty("connection.dbfolder.title"); }
  public String getConnectionDbFolderMessage() { return getProperty("connection.dbfolder.message"); }
  public String getConnectionDbFolderButton() { return getProperty("connection.dbfolder.button"); }
  public String getConnectionUrlLabel() { return getProperty("connection.url.label"); }
  public String getConnectionUrlTooltip() { return getProperty("connection.url.tooltip"); }
  public String getConnectionSchemeTitleTooltip() { return getProperty("connection.scheme.title.tooltip"); }
  public String getConnectionSchemeSampleUrlTooltip(String sConnectionTitle)
  { 
    return MessageFormat.format(getPropertyFormat("connection.scheme.sampleurl.tooltip"),sConnectionTitle);
  }
  public String getConnectionSchemeCopy() { return getProperty("connection.scheme.copy"); }
  public String getConnectionSchemeCopyTooltip(String sSampleUrl)
  { 
    return MessageFormat.format(getPropertyFormat("connection.scheme.copy.tooltip"),sSampleUrl);
  }
  public String getConnectionDbUserLabel() { return getProperty("connection.dbuser.label"); }
  public String getConnectionDbUserTooltip() { return getProperty("connection.dbuser.tooltip"); }
  public String getConnectionDbPasswordLabel() { return getProperty("connection.dbpassword.label"); }
  public String getConnectionDbPasswordTooltip() { return getProperty("connection.dbpassword.tooltip"); }

  public String getConnectionDownloadMetaDataOnlyLabel() { return getProperty("connection.download.metadataonly.label"); }
  public String getConnectionDownloadMetaDataOnlyTooltip() { return getProperty("connection.download.metadataonly.tooltip"); }
  public String getConnectionDownloadViewsAsTablesLabel() { return getProperty("connection.download.viewsastables.label"); }
  public String getConnectionDownloadViewsAsTablesTooltip() { return getProperty("connection.download.viewsastables.tooltip"); }
  public String getConnectionUploadMetaDataOnlyLabel() { return getProperty("connection.upload.metadataonly.label"); }
  public String getConnectionUploadMetaDataOnlyTooltip() { return getProperty("connection.upload.metadataonly.tooltip"); }
  public String getConnectionUploadOverwriteLabel() { return getProperty("connection.upload.overwrite.label"); }
  public String getConnectionUploadOverwriteTooltip() { return getProperty("connection.upload.overwrite.tooltip"); }
  public String getConnectionUploadSchemasLabel() { return getProperty("connection.upload.schemas.label"); }

  public String getConnectionErrorTitle() { return getProperty("connection.error.title"); }
  public String getConnectionErrorDriverMessage(String sError)
  { 
    return MessageFormat.format(getPropertyFormat("connection.error.driver.message"),sError); 
  }
  public String getConnectionErrorConnectMessage(String sUrl, Exception e) 
  { 
    return MessageFormat.format(getPropertyFormat("connection.error.connect.message"),
      sUrl, EU.getExceptionMessage(e)); 
  }

  public String getUploadConnectionErrorTitle() { return getProperty("upload.connection.error.title"); }
  public String getUploadConnectionErrorSchemaMessage() { return getProperty("upload.connection.error.schema.message"); }
  
  public String getArchiveFileTitle() { return getProperty("archive.file.title"); }
  public String getArchiveFileMessage() { return getProperty("archive.file.message"); }
  public String getDownloadingStatus(String sUrl, File fileArchive)
  { 
    return MessageFormat.format(getPropertyFormat("downloading.status"),
      sUrl,fileArchive.getAbsolutePath());
  }
  public String getDownloadTitle() { return getProperty("download.title"); }
  public String getDownloadSuccessMessage() { return getProperty("download.success.message"); }
  public String getDownloadCanceledMessage() { return getProperty("download.canceled.message"); }
  public String getDownloadFailureMessage(Throwable t)
  {
    return MessageFormat.format(getPropertyFormat("download.failure.message"),
      EU.getThrowableMessage(t));
  }
  public String getDownloadErrorTitle() { return getProperty("download.error.title"); }
  public String getDownloadErrorCreateMessage(File fileArchive, Exception e)
  {
    return MessageFormat.format(getPropertyFormat("download.error.create.message"), 
      fileArchive.getAbsolutePath(), EU.getExceptionMessage(e));
  }
  public String getDownloadErrorInvalidMessage(File fileArchive)
  {
    return MessageFormat.format(getPropertyFormat("download.error.invalid.message"), 
      fileArchive.getAbsolutePath());
  }
  public String getDownloadConnectionLabel() { return getProperty("download.connection.label"); }
  public String getDownloadArchiveLabel() { return getProperty("download.archive.label"); }
  public String getDownloadExtentLabel() { return getProperty("download.extent.label"); }
  public String getDownloadExtentMetaDataOnly() { return getProperty("download.extent.metadataonly"); }
  public String getDownloadExtentFullDatabase() { return getProperty("download.extent.fulldatabase"); }
  public String getDownloadViewsLabel() { return getProperty("download.views.label"); }
  public String getDownloadViewsAsTables() { return getProperty("download.views.astables"); }
  public String getDownloadViewsAsViews() { return getProperty("download.views.asviews"); }

  public String getUploadingStatus(File fileArchive, String sUrl)
  {
    return MessageFormat.format(getPropertyFormat("uploading.status"),fileArchive.getAbsolutePath(),sUrl);
  }
  public String getUploadTitle() { return getProperty("upload.title"); }
  public String getUploadUnsupportedUdtsMessage() { return getProperty("upload.unsupported.udts.message"); }
  public String getUploadSuccessMessage() { return getProperty("upload.success.message"); }
  public String getUploadCanceledMessage() { return getProperty("upload.canceled.message"); }
  public String getUploadFailureMessage(Throwable t)
  {
    return MessageFormat.format(getPropertyFormat("upload.failure.message"),EU.getThrowableMessage(t));
  }
  public String getUploadErrorTitle() { return getProperty("upload.error.title"); }
  public String getUploadErrorOverwriteMessage(String sUrl)
  {
    return MessageFormat.format(getPropertyFormat("upload.error.overwrite.message"), sUrl); 
  }
  public String getUploadErrorIoMessage(File fileArchive, Exception e)
  { 
    return MessageFormat.format(getPropertyFormat("upload.error.io.message"),
      fileArchive.getAbsolutePath(), EU.getExceptionMessage(e)); 
  }
  public String getUploadErrorSqlMessage(String sUrl, Exception e)
  {
    return MessageFormat.format(getPropertyFormat("upload.error.sql.message"),
      sUrl,EU.getExceptionMessage(e)); 
  }
  public String getUploadArchiveLabel() { return getProperty("upload.connection.label"); }
  public String getUploadConnectionLabel() { return getProperty("upload.connection.label"); }
  public String getUploadExtentLabel() { return getProperty("upload.extent.label"); }
  public String getUploadExtentMetaDataOnly() { return getProperty("upload.extent.metadataonly"); }
  public String getUploadExtentFullDatabase() { return getProperty("upload.extent.fulldatabase"); }
  
  public String getOpeningStatus(File fileArchive)
  {
    return MessageFormat.format(getPropertyFormat("opening.status"),
      fileArchive.getAbsolutePath()); 
  }
  public String getOpenArchiveTitle() { return getProperty("open.archive.title"); }
  public String getOpenArchiveMessage() { return getProperty("open.archive.message"); }
  public String getOpenErrorTitle() { return getProperty("open.error.title"); }
  public String getOpenErrorUnsavedMessage(File fileArchive)
  {
    return MessageFormat.format(getPropertyFormat("open.error.unsaved.message"),
      fileArchive.getAbsolutePath()); 
  }
  public String getOpenErrorMessage(File fileArchive, Exception e)
  {
    return MessageFormat.format(getPropertyFormat("open.error.message"),
      fileArchive.getAbsolutePath(), EU.getExceptionMessage(e)); 
  }
  public String getOpenErrorInvalidMessage(File fileArchive)
  {
    return MessageFormat.format(getPropertyFormat("open.error.invalid.message"),
      fileArchive.getAbsolutePath()); 
  }

  public String getSavingStatus(File fileArchive)
  {
    return MessageFormat.format(getPropertyFormat("saving.status"),fileArchive.getAbsolutePath()); 
  }
  public String getSaveErrorTitle() { return getProperty("save.error.title"); }
  public String getSaveErrorMessage(File fileArchive, Exception e)
  {
    return MessageFormat.format(getPropertyFormat("save.error.message"),
      fileArchive.getAbsolutePath(), EU.getExceptionMessage(e)); 
  }
  
  public String getCloseTitle() { return getProperty("close.title"); }
  public String getCloseSaveQuestion(File fileArchive)
  {
    return MessageFormat.format(getPropertyFormat("close.save.question"),
      fileArchive.getAbsolutePath()); 
  }
  public String getCloseExportMetaDataQuestion() { return getProperty("close.export.metadata.question"); }
  public String getCloseErrorTitle() { return getProperty("close.error.title"); }
  public String getCloseErrorMessage(File fileArchive, Exception e)
  { 
    return MessageFormat.format(getPropertyFormat("close.error.message"),
      fileArchive.getAbsolutePath(), EU.getExceptionMessage(e));
  }

  public String getMetaDataErrorTitle() { return getProperty("metadata.error.title"); }
  public String getMetaDataErrorTransformerConfigurationMessage(Exception e)
  {
    return MessageFormat.format(getPropertyFormat("metadata.error.transformer.configuration.message"),
      EU.getExceptionMessage(e));
  }
  public String getMetaDataErrorIoMessage(Exception e)
  { 
    return MessageFormat.format(getPropertyFormat("metadata.error.io.message"),
      EU.getExceptionMessage(e));
  }
  public String getMetaDataErrorTransformerMessage(Exception e)
  { 
    return MessageFormat.format(getPropertyFormat("metadata.error.transformer.message"),
      EU.getExceptionMessage(e)); 
  }
  public String getMetaDataDisplayTitle() { return getProperty("metadata.display.title"); }
  public String getMetaDataSaveXmlText() { return getProperty("metadata.save.xml.text"); }
  public String getMetaDataSaveHtmlText() { return getProperty("metadata.save.html.text"); }
  public String getMetaDataErrorSaveMessage(File fileMetaData, Exception e)
  {
    return MessageFormat.format(getPropertyFormat("metadata.error.save.message"),
      fileMetaData.getAbsolutePath(), EU.getExceptionMessage(e));
  }
  public String getMetaDataSaveTitle() { return getProperty("metadata.save.title"); }
  public String getMetaDataSaveMessage() { return getProperty("metadata.save.message"); }
  public String getMetaDataAugmentTitle()  { return getProperty("metadata.augment.title"); }
  public String getMetaDataAugmentMessage() { return getProperty("metadata.augment.message"); }
  public String getMetaDataErrorAugmentMessage(File fileMetaData, Exception e) 
  {
    return MessageFormat.format(getPropertyFormat("metadata.error.augment.message"),
      fileMetaData.getAbsolutePath(), EU.getExceptionMessage(e)); 
  }
  
  public String getEditMetaDataApply() { return getProperty("edit.metadata.apply"); }
  public String getEditMetaDataReset() { return getProperty("edit.metadata.reset"); }
  public String getEditMetaDataTitle() { return getProperty("edit.metadata.title"); }
  public String getEditMetaDataQuery() { return getProperty("edit.metadata.query"); }
  public String getEditMetaDataMandatory() { return getProperty("edit.metadata.mandatory"); }
  
  public String getValueDialogTitle(String sTableLocation)
  { 
    return MessageFormat.format(getPropertyFormat("value.dialog.title"),sTableLocation);
  }
  public String getValueDialogClose() { return getProperty("value.dialog.close"); }

  public String getTableNoPrimaryData() { return getProperty("table.no.primary.data"); }
  public String getTableSorting(String sTable)
  { 
    return MessageFormat.format(getPropertyFormat("table.sorting"),sTable); 
  }
  public String getTableFileTitle() { return getProperty("table.file.title"); }
  public String getTableFileMessage() { return getProperty("table.file.message"); }
  public String getTableExportStatus(String sTable, File fileExport)
  {
    return MessageFormat.format(getPropertyFormat("table.export.status"),
      sTable, fileExport.getAbsolutePath()); 
  }
  public String getTableErrorTitle() { return getProperty("table.error.title"); }
  public String getTableErrorExportMessage(String sTable, File fileExport, Exception e)
  {
    return MessageFormat.format(getPropertyFormat("table.error.export.message"),
      sTable, fileExport.getAbsolutePath(), EU.getExceptionMessage(e));
  }
  
  public String getIntegrityCheckTitle(File fileArchive)
  {
    return MessageFormat.format(getPropertyFormat("integrity.check.title"),
      fileArchive.getAbsolutePath()); 
  }
  public String getIntegrityCheckPassMessage(File fileArchive)
  { 
    return MessageFormat.format(getPropertyFormat("integrity.check.pass.message"),
      fileArchive.getAbsolutePath()); 
  }
  public String getIntegrityCheckFailMessage(File fileArchive)
  {
    return MessageFormat.format(getPropertyFormat("integrity.check.fail.message"),
      fileArchive.getAbsolutePath()); 
  }
  
  public String getFindTitle() { return getProperty("find.title"); }
  public String getFindStringLabel() { return getProperty("find.string.label"); }
  public String getFindMatchCaseLabel() { return getProperty("find.match.case.label"); }
  public String getFindEndMessage(String sFindString)
  {
    return MessageFormat.format(getPropertyFormat("find.end.message"), sFindString);
  }
  
  public String getSearchTitle(String sTableName)
  {
    return MessageFormat.format(getPropertyFormat("search.title"),sTableName); 
  }
  public String getSearchExplanation() { return getProperty("search.explanation"); }
  public String getSearchSelectAll() { return getProperty("search.select.all"); }
  public String getSearchSelectNone() { return getProperty("search.select.none"); }
  public String getSearchStatus(String sFindString, String sTableName)
  {
    return MessageFormat.format(getPropertyFormat("search.status"), sFindString, sTableName);
  }
  public String getSearchEndMessage(String sFindString, String sTableName)
  {
    return MessageFormat.format(getPropertyFormat("search.end.message"), sFindString, sTableName);
  }
  public String getSearchFailedMessage(String sFindString, String sTableName)
  {
    return MessageFormat.format(getPropertyFormat("search.failed.message"), sFindString, sTableName);
  }
  
  public String getOptionTitle() { return getProperty("option.title"); }
  public String getOptionBrowse() { return getProperty("option.browse"); }
  public String getOptionNotIntegerMessage() { return getProperty("option.not.integer.message"); }
  public String getOptionLoginTimeoutLabel() { return getProperty("option.login.timeout.label"); }
  public String getOptionLoginTimeoutExplanation() { return getProperty("option.login.timeout.explanation"); }
  public String getOptionQueryTimeoutLabel() { return getProperty("option.query.timeout.label"); }
  public String getOptionQueryTimeoutExplanation() { return getProperty("option.query.timeout.explanation"); }
  public String getOptionColumnWidthLabel() { return getProperty("option.column.width.label"); }
  public String getOptionColumnWidthExplanation() { return getProperty("option.column.width.explanation"); }
  public String getOptionFileChooserNativeLabel() { return getProperty("option.file.chooser.native.label"); }
  public String getOptionFileChooserNativeExplanation() { return getProperty("option.file.chooser.native.explanation"); }
  public String getOptionTextEditorLabel() { return getProperty("option.text.editor.label"); }
  public String getOptionTextEditorExplanation() { return getProperty("option.text.editor.explanation"); }
  public String getOptionBinaryEditorLabel() { return getProperty("option.binary.editor.label"); }
  public String getOptionBinaryEditorExplanation() { return getProperty("option.binary.editor.explanation"); }
  public String getOptionXslFileLabel() { return getProperty("option.xsl.file.label"); }
  public String getOptionXslFileExplanation() { return getProperty("option.xsl.file.explanation"); }
  public String getOptionLobsFolderLabel() { return getProperty("option.lobs.folder.label"); }
  public String getOptionLobsFolderExplanation() { return getProperty("option.lobs.folder.explanation"); }

  public String getValueDisplayErrorTitle() { return getProperty("value.display.error.title"); }
  public String getValueDisplayErrorSystem() { return getProperty("value.display.error.system"); }
  public String getValueDisplayErrorMessage(String sApplication, int iResult, String sOut, String sErr) 
  {
    return MessageFormat.format(getPropertyFormat("value.display.error.message"),
      sApplication,String.valueOf(iResult),sOut,(sErr == null)?getValueDisplayErrorSystem():sErr); 
  }
  
} /* SiardBundle */
