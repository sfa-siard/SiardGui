/*======================================================================
Creates the table views for SIARD meta data.
Application: SIARD GUI
Description: Creates the table views for SIARD meta data. 
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 25.07.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.gui.details;

import java.io.*;
import java.util.*;

import ch.enterag.utils.fx.controls.ObjectListTableView;
import ch.enterag.sqlparser.identifier.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.gui.*;

/*====================================================================*/
/** Creates the table views for SIARD meta data.
 * @author Hartwig Thomas
 */
public abstract class MetaDataTableFactory
{
  /*------------------------------------------------------------------*/
  /** create the table view for the schemas of the SIARD archive.
   * @param md meta data of SIARD archive.
   * @return TableView listing the schemas of the SIARD archive.
   */
  public static ObjectListTableView newMetaSchemasTableView(MetaData md)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    List<String> listHeaders = Arrays.asList(
      sb.getProperty("header.metaschemas.row"),
      sb.getProperty("header.metaschemas.name"),
      sb.getProperty("header.metaschemas.tables"));
    ObjectListTableView oltv = new ObjectListTableView(listHeaders);
    /* table data */
    for (int iSchema = 0; iSchema < md.getMetaSchemas(); iSchema++)
    {
      MetaSchema ms = md.getMetaSchema(iSchema);
      List<Object> listRow = Arrays.asList((Object)iSchema, 
        ms.getName(), ms.getMetaTables());
      oltv.getItems().add(listRow);
    }
    return oltv;
  } /* newMetaSchemasTableView */

  /*------------------------------------------------------------------*/
  /** create the table view for the users of the SIARD archive.
   * @param md meta data of SIARD archive.
   * @return TableView listing the users of the SIARD archive.
   */
  public static ObjectListTableView newMetaUsersTableView(MetaData md)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    List<String> listHeaders = Arrays.asList(
      sb.getProperty("header.metausers.row"),
      sb.getProperty("header.metausers.name"));
    ObjectListTableView oltv = new ObjectListTableView(listHeaders);
    /* table data */
    for (int iUser = 0; iUser < md.getMetaUsers(); iUser++)
    {
      MetaUser mu = md.getMetaUser(iUser);
      List<Object> listRow = Arrays.asList((Object)iUser, 
        mu.getName());
      oltv.getItems().add(listRow);
    }
    return oltv;
  } /* newMetaUsersTableView */
  
  /*------------------------------------------------------------------*/
  /** create the table view for the roles of the SIARD archive.
   * @param md meta data of SIARD archive.
   * @return TableView listing the roles of the SIARD archive.
   */
  public static ObjectListTableView newMetaRolesTableView(MetaData md)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    List<String> listHeaders = Arrays.asList(
      sb.getProperty("header.metaroles.row"),
      sb.getProperty("header.metaroles.name"),
      sb.getProperty("header.metaroles.admin"));
    ObjectListTableView oltv = new ObjectListTableView(listHeaders);
    /* table data */
    for (int iRole = 0; iRole < md.getMetaRoles(); iRole++)
    {
      MetaRole mr = md.getMetaRole(iRole);
      List<Object> listRow = Arrays.asList((Object)iRole, 
        mr.getName(),mr.getAdmin());
      oltv.getItems().add(listRow);
    }
    return oltv;
  } /* newMetaRolesTableView */
  
  /*------------------------------------------------------------------*/
  /** create the table view for the privileges of the SIARD archive.
   * @param md meta data of SIARD archive.
   * @return TableView listing the privileges of the SIARD archive.
   */
  public static ObjectListTableView newMetaPrivilegesTableView(MetaData md)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    List<String> listHeaders = Arrays.asList(
      sb.getProperty("header.metaprivileges.row"),
      sb.getProperty("header.metaprivileges.type"),
      sb.getProperty("header.metaprivileges.object"),
      sb.getProperty("header.metaprivileges.grantor"),
      sb.getProperty("header.metaprivileges.grantee"));
    ObjectListTableView oltv = new ObjectListTableView(listHeaders);
    /* table data */
    for (int iPrivilege = 0; iPrivilege < md.getMetaPrivileges(); iPrivilege++)
    {
      MetaPrivilege mp = md.getMetaPrivilege(iPrivilege);
      List<Object> listRow = Arrays.asList((Object)iPrivilege,
        mp.getType(),mp.getObject(),mp.getGrantor(),mp.getGrantee());
      oltv.getItems().add(listRow);
    }
    return oltv;
  } /* newMetaPrivilegesTableView */
  
  /*------------------------------------------------------------------*/
  /** create the table view for the types of a schema.
   * @param ms schema meta data.
   * @return TableView listing the types of the schema.
   */
  public static ObjectListTableView newMetaTypesTableView(MetaSchema ms)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    List<String> listHeaders = Arrays.asList(
      sb.getProperty("header.metatypes.row"),
      sb.getProperty("header.metatypes.name"),
      sb.getProperty("header.metatypes.category"),
      sb.getProperty("header.metatypes.attributes"));
    ObjectListTableView oltv = new ObjectListTableView(listHeaders);
    /* table data */
    for (int iType = 0; iType < ms.getMetaTypes(); iType++)
    {
      MetaType mt = ms.getMetaType(iType);
      List<Object> listRow = Arrays.asList((Object)iType,
        mt.getName(),mt.getCategory(),mt.getMetaAttributes());
      oltv.getItems().add(listRow);
    }
    return oltv;
  } /* newMetaTypesTableView */

  /*------------------------------------------------------------------*/
  /** create the table view for the tables of a schema.
   * @param ms schema meta data.
   * @return TableView listing the tables of the schema.
   */
  public static ObjectListTableView newMetaTablesTableView(MetaSchema ms)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    List<String> listHeaders = Arrays.asList(
      sb.getProperty("header.metatables.row"),
      sb.getProperty("header.metatables.name"),
      sb.getProperty("header.metatables.columns"),
      sb.getProperty("header.metatables.records"));
    ObjectListTableView oltv = new ObjectListTableView(listHeaders);
    /* table data */
    for (int iTable = 0; iTable < ms.getMetaTables(); iTable++)
    {
      MetaTable mt = ms.getMetaTable(iTable);
      List<Object> listRow = Arrays.asList((Object)iTable,
        mt.getName(),mt.getMetaColumns(),mt.getRows());
      oltv.getItems().add(listRow);
    }
    return oltv;
  } /* newMetaTablesTableView */

  /*------------------------------------------------------------------*/
  /** create the table view for the views of a schema.
   * @param ms schema meta data.
   * @return TableView listing the views of the schema.
   */
  public static ObjectListTableView newMetaViewsTableView(MetaSchema ms)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    List<String> listHeaders = Arrays.asList(
      sb.getProperty("header.metaviews.row"),
      sb.getProperty("header.metaviews.name"),
      sb.getProperty("header.metaviews.columns"),
      sb.getProperty("header.metaviews.records"));
    ObjectListTableView oltv = new ObjectListTableView(listHeaders);
    /* table data */
    for (int iView = 0; iView < ms.getMetaViews(); iView++)
    {
      MetaView mv = ms.getMetaView(iView);
      List<Object> listRow = Arrays.asList((Object)iView,
        mv.getName(),mv.getMetaColumns(),mv.getRows());
      oltv.getItems().add(listRow);
    }
    return oltv;
  } /* newMetaViewsTableView */

  /*------------------------------------------------------------------*/
  /** create the table view for the routines of a schema.
   * @param ms schema meta data.
   * @return TableView listing the routines of the schema.
   */
  public static ObjectListTableView newMetaRoutinesTableView(MetaSchema ms)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    List<String> listHeaders = Arrays.asList(
      sb.getProperty("header.metaroutines.row"),
      sb.getProperty("header.metaroutines.name"),
      sb.getProperty("header.metaroutines.specificname"),
      sb.getProperty("header.metaroutines.characteristic"),
      sb.getProperty("header.metaroutines.returntype"),
      sb.getProperty("header.metaroutines.parameters"));
    ObjectListTableView oltv = new ObjectListTableView(listHeaders);
    /* table data */
    for (int iRoutine = 0; iRoutine < ms.getMetaRoutines(); iRoutine++)
    {
      MetaRoutine mr = ms.getMetaRoutine(iRoutine);
      List<Object> listRow = Arrays.asList((Object)iRoutine,
        mr.getName(),mr.getSpecificName(),mr.getCharacteristic(),mr.getReturnType(),mr.getMetaParameters());
      oltv.getItems().add(listRow);
    }
    return oltv;
  } /* newMetaRoutinesTableView */

  /*------------------------------------------------------------------*/
  /** create the table view for the attributes of a type.
   * @param mt type meta data.
   * @return TableView listing the attributes of the type.
   */
  public static ObjectListTableView newMetaAttributesTableView(MetaType mt)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    List<String> listHeaders = Arrays.asList(
      sb.getProperty("header.metaattributes.position"),
      sb.getProperty("header.metaattributes.name"),
      sb.getProperty("header.metaattributes.type"),
      sb.getProperty("header.metaattributes.cardinality"));
    ObjectListTableView oltv = new ObjectListTableView(listHeaders);
    /* table data */
    for (int iAttribute = 0; iAttribute < mt.getMetaAttributes(); iAttribute++)
    {
      MetaAttribute ma = mt.getMetaAttribute(iAttribute);
      String sType = ma.getType();
      if (sType == null)
      {
        QualifiedId qiType = new QualifiedId(null,ma.getTypeSchema(),ma.getTypeName());
        sType = qiType.format();
      }
      List<Object> listRow = Arrays.asList((Object)ma.getPosition(),
        ma.getName(),sType,ma.getCardinality() > 0?ma.getCardinality():null);
      oltv.getItems().add(listRow);
    }
    return oltv;
  } /* newMetaAttributesTableView */

  /*------------------------------------------------------------------*/
  /** create the table view for the columns of a table.
   * @param mt table meta data.
   * @return TableView listing the columns of the table.
   */
  public static ObjectListTableView newMetaColumnsTableView(MetaTable mt)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    List<String> listHeaders = Arrays.asList(
      sb.getProperty("header.metacolumns.position"),
      sb.getProperty("header.metacolumns.name"),
      sb.getProperty("header.metacolumns.type"),
      sb.getProperty("header.metacolumns.cardinality"));
    ObjectListTableView oltv = new ObjectListTableView(listHeaders);
    /* table data */
    try
    {
      for (int iColumn = 0; iColumn < mt.getMetaColumns(); iColumn++)
      {
        MetaColumn mc = mt.getMetaColumn(iColumn);
        String sType = null;
        sType = mc.getType();
        if (sType == null)
        {
          QualifiedId qiType = new QualifiedId(null,mc.getTypeSchema(),mc.getTypeName());
          sType = qiType.format();
        }
        List<Object> listRow = Arrays.asList((Object)mc.getPosition(),
          mc.getName(),sType,mc.getCardinality() > 0?mc.getCardinality():null);
        oltv.getItems().add(listRow);
      }
    }
    catch(IOException ie) { }
    return oltv;
  } /* newMetaColumnsTableView */

  /*------------------------------------------------------------------*/
  /** create the table view for the columns of a view.
   * @param mv view meta data.
   * @return TableView listing the columns of the view.
   */
  public static ObjectListTableView newMetaColumnsTableView(MetaView mv)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    List<String> listHeaders = Arrays.asList(
      sb.getProperty("header.metacolumns.position"),
      sb.getProperty("header.metacolumns.name"),
      sb.getProperty("header.metacolumns.type"),
      sb.getProperty("header.metacolumns.cardinality"));
    ObjectListTableView oltv = new ObjectListTableView(listHeaders);
    /* table data */
    try
    {
      for (int iColumn = 0; iColumn < mv.getMetaColumns(); iColumn++)
      {
        MetaColumn mc = mv.getMetaColumn(iColumn);
        String sType = null;
        sType = mc.getType();
        if (sType == null)
        {
          QualifiedId qiType = new QualifiedId(null,mc.getTypeSchema(),mc.getTypeName());
          sType = qiType.format();
        }
        List<Object> listRow = Arrays.asList((Object)mc.getPosition(),
          mc.getName(),sType,mc.getCardinality() > 0?mc.getCardinality():null);
        oltv.getItems().add(listRow);
      }
    }
    catch(IOException ie) { }
    return oltv;
  } /* newMetaColumnsTableView */

  /*------------------------------------------------------------------*/
  /** create the table view for the candidate keys of a table.
   * @param mt table meta data.
   * @return TableView listing the candidate keys of the table.
   */
  public static ObjectListTableView newMetaCandidateKeysTableView(MetaTable mt)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    List<String> listHeaders = Arrays.asList(
      sb.getProperty("header.metacandidatekeys.row"),
      sb.getProperty("header.metacandidatekeys.name"),
      sb.getProperty("header.metacandidatekeys.columns"));
    ObjectListTableView oltv = new ObjectListTableView(listHeaders);
    /* table data */
    for (int iCandidateKey = 0; iCandidateKey < mt.getMetaCandidateKeys(); iCandidateKey++)
    {
      MetaUniqueKey muk = mt.getMetaCandidateKey(iCandidateKey);
      String sColumns = "";
      for (int i = 0; i < muk.getColumns(); i++)
      {
        if (i > 0)
          sColumns = sColumns + ",";
        sColumns = sColumns + muk.getColumn(i);
      }
      List<Object> listRow = Arrays.asList((Object)iCandidateKey,
        muk.getName(),sColumns);
      oltv.getItems().add(listRow);
    }
    return oltv;
  } /* newMetaCandidateKeysTableView */
  
  /*------------------------------------------------------------------*/
  /** create the table view for the foreign keys of a table.
   * @param mt table meta data.
   * @return TableView listing the foreign keys of the table.
   */
  public static ObjectListTableView newMetaForeignKeysTableView(MetaTable mt)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    List<String> listHeaders = Arrays.asList(
      sb.getProperty("header.metaforeignkeys.row"),
      sb.getProperty("header.metaforeignkeys.name"),
      sb.getProperty("header.metaforeignkeys.columns"),
      sb.getProperty("header.metaforeignkeys.referencedschema"),
      sb.getProperty("header.metaforeignkeys.referencedtable"),
      sb.getProperty("header.metaforeignkeys.referencedcolumns"));
    ObjectListTableView oltv = new ObjectListTableView(listHeaders);
    /* table data */
    for (int iForeignKey = 0; iForeignKey < mt.getMetaForeignKeys(); iForeignKey++)
    {
      MetaForeignKey mfk = mt.getMetaForeignKey(iForeignKey);
      String sColumns = "";
      String sReferencedColumns = "";
      for (int i = 0; i < mfk.getReferences(); i++)
      {
        if (i > 0)
        {
          sColumns = sColumns + ",";
          sReferencedColumns = sReferencedColumns + ",";
        }
        sColumns = sColumns + mfk.getColumn(i);
        sReferencedColumns = sReferencedColumns + mfk.getReferenced(i);
      }
      List<Object> listRow = Arrays.asList((Object)iForeignKey,
        mfk.getName(),sColumns,mfk.getReferencedSchema(),mfk.getReferencedTable(),sReferencedColumns);
      oltv.getItems().add(listRow);
    }
    return oltv;
  } /* newMetaForeignKeysTableView */
  
  /*------------------------------------------------------------------*/
  /** create the table view for the parameters of a routine.
   * @param mr routine meta data.
   * @return TableView listing the parameters of the routine.
   */
  public static ObjectListTableView newMetaParametersTableView(MetaRoutine mr)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    List<String> listHeaders = Arrays.asList(
      sb.getProperty("header.metaparameters.position"),
      sb.getProperty("header.metaparameters.name"),
      sb.getProperty("header.metaparameters.mode"),
      sb.getProperty("header.metaparameters.type"),
      sb.getProperty("header.metaparameters.cardinality"));
    ObjectListTableView oltv = new ObjectListTableView(listHeaders);
    /* table data */
    for (int iParameter = 0; iParameter < mr.getMetaParameters(); iParameter++)
    {
      MetaParameter mp = mr.getMetaParameter(iParameter);
      String sType = mp.getType(); 
      if (sType == null)
      {
        QualifiedId qiType = new QualifiedId(null,mp.getTypeSchema(),mp.getTypeName());
        sType = qiType.format();
      }
      List<Object> listRow = Arrays.asList((Object)mp.getPosition(),
        mp.getName(), mp.getMode(), sType, mp.getCardinality() > 0?mp.getCardinality():null);
      oltv.getItems().add(listRow);
    }
    return oltv;
  } /* newMetaParametersTableView */

  /*------------------------------------------------------------------*/
  /** create the table view for the fields of a column.
   * @param mc column meta data.
   * @return TableView listing the fields of the column.
   */
  public static ObjectListTableView newMetaFieldsTableView(MetaColumn mc)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    List<String> listHeaders = Arrays.asList(
      sb.getProperty("header.metafields.position"),
      sb.getProperty("header.metafields.name"),
      sb.getProperty("header.metafields.type"),
      sb.getProperty("header.metafields.cardinality"));
    ObjectListTableView oltv = new ObjectListTableView(listHeaders);
    /* table data */
    try
    {
      for (int iField = 0; iField < mc.getMetaFields(); iField++)
      {
        MetaField mf = mc.getMetaField(iField);
        String sType = null;
        sType = mf.getType();
        if (sType == null)
        {
          QualifiedId qiType = new QualifiedId(null,mf.getTypeSchema(),mf.getTypeName());
          sType = qiType.format();
        }
        List<Object> listRow = Arrays.asList((Object)mf.getPosition(),
          mf.getName(),sType,mf.getCardinality() > 0?mf.getCardinality():null);
        oltv.getItems().add(listRow);
      }
    }
    catch(IOException ie) { }
    return oltv;
  } /* newMetaFieldsTableView */

  /*------------------------------------------------------------------*/
  /** create the table view for the fields of a field
   * @param mf field meta data.
   * @return TableView listing the fields of the field.
   */
  public static ObjectListTableView newMetaFieldsTableView(MetaField mf)
  {
    SiardBundle sb = SiardBundle.getSiardBundle();
    List<String> listHeaders = Arrays.asList(
      sb.getProperty("header.metafields.position"),
      sb.getProperty("header.metafields.name"),
      sb.getProperty("header.metafields.type"),
      sb.getProperty("header.metafields.cardinality"));
    ObjectListTableView oltv = new ObjectListTableView(listHeaders);
    /* table data */
    try
    {
      for (int iField = 0; iField < mf.getMetaFields(); iField++)
      {
        MetaField mfChild = mf.getMetaField(iField);
        String sType = null;
        sType = mfChild.getType();
        if (sType == null)
        {
          QualifiedId qiType = new QualifiedId(null,mfChild.getTypeSchema(),mfChild.getTypeName());
          sType = qiType.format();
        }
        List<Object> listRow = Arrays.asList((Object)mf.getPosition(),
          mfChild.getName(),sType,mfChild.getCardinality() > 0?mfChild.getCardinality():null);
        oltv.getItems().add(listRow);
      }
    }
    catch(IOException ie) { }
    return oltv;
  } /* newMetaFieldsTableView */

} /* MetaDataTableFactory */
