package ch.admin.bar.siard2.gui.tv;

import java.io.*;
import javafx.beans.value.*;
import javafx.scene.control.*;
import javafx.util.*;

import ch.enterag.utils.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.fx.controls.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.gui.*;

public class ArchiveTreeView
  extends TreeView<Object>
  implements ChangeListener<TreeItem<Object>> // handles changing selection
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(ArchiveTreeView.class.getName());
  
  public StopWatch swDetails = StopWatch.getInstance();
  public StopWatch swRestrict = StopWatch.getInstance();
  
  /*------------------------------------------------------------------*/
  /** printStatus prints memory and stop watches.
   */
  public void printStatus()
  {
    Runtime rt = Runtime.getRuntime();
    System.out.println(
      "  TV: Used Memory: "+StopWatch.formatLong(rt.totalMemory() - rt.freeMemory())+
      ", Free Memory: "+StopWatch.formatLong(rt.freeMemory())+
      ", Details: "+swDetails.formatMs()+
      ", Restrict: "+swRestrict.formatMs());
  } /* printStatus */
  
  /*==================================================================*/
  private class MetaPrivilegeTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaPrivilegeTreeItem(MetaPrivilege mp)
    {
      super(mp);
    } /* constructor */
    @Override
    public void addChildren()
    {
      // privilege has no children
    } /* addChildren */
  } /* class MetaPrivilegeTreeItem */
  
  /*==================================================================*/
  private class MetaPrivilegesTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaPrivilegesTreeItem(MetaData md)
    {
      super(md);
    } /* constructor */
    @Override
    protected void addChildren()
    {
      MetaData md = (MetaData)getValue();
      for (int iPrivilege = 0; iPrivilege < md.getMetaPrivileges(); iPrivilege++)
        getChildren().add(new MetaPrivilegeTreeItem(md.getMetaPrivilege(iPrivilege)));
    } /* addChildren */
  } /* class MetaPrivilegesTreeItem */
  
  /*==================================================================*/
  private class MetaRoleTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaRoleTreeItem(MetaRole mr)
    {
      super(mr);
    } /* constructor */
    @Override
    public void addChildren()
    {
      // role has no children
    } /* addChildren */
  } /* class MetaRoleTreeItem */
  
  /*==================================================================*/
  private class MetaRolesTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaRolesTreeItem(MetaData md)
    {
      super(md);
    } /* constructor */
    @Override
    protected void addChildren()
    {
      MetaData md = (MetaData)getValue();
      for (int iRole = 0; iRole < md.getMetaRoles(); iRole++)
        getChildren().add(new MetaRoleTreeItem(md.getMetaRole(iRole)));
    } /* addChildren */
  } /* class MetaRolesTreeItem */
  
  /*==================================================================*/
  private class MetaUserTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaUserTreeItem(MetaUser mu)
    {
      super(mu);
    } /* constructor */
    @Override
    public void addChildren()
    {
      // User has no children
    } /* addChildren */
  } /* class MetaUserTreeItem */
  
  /*==================================================================*/
  private class MetaUsersTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaUsersTreeItem(MetaData md)
    {
      super(md);
    } /* constructor */
    @Override
    protected void addChildren()
    {
      MetaData md = (MetaData)getValue();
      for (int iUser = 0; iUser < md.getMetaUsers(); iUser++)
        getChildren().add(new MetaUserTreeItem(md.getMetaUser(iUser)));
    } /* addChildren */
  } /* class MetaUsersTreeItem */
  
  /*====================================================================*/
  private class MetaParameterTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaParameterTreeItem(MetaParameter mp)
    {
      super(mp);
    } /* constructor */
    @Override
    public void addChildren()
    {
      // Parameter has no children
    } /* addChildren */
  } /* class MetaParameterTreeItem */
  
  /*==================================================================*/
  private class MetaParametersTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaParametersTreeItem(MetaRoutine mr)
    {
      super(mr);
    } /* constructor */
    @Override
    protected void addChildren()
    {
      MetaRoutine mr = (MetaRoutine)getValue();
      for (int iParameter = 0; iParameter < mr.getMetaParameters(); iParameter++)
        getChildren().add(new MetaParameterTreeItem(mr.getMetaParameter(iParameter)));
    } /* addChildren */
  } /* class MetaParametersTreeItem */
  
  /*==================================================================*/
  private class MetaRoutineTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaRoutineTreeItem(MetaRoutine mr)
    {
      super(mr);
    } /* constructor */
    @Override
    public void addChildren()
    {
      MetaRoutine mr = (MetaRoutine)getValue();
      if (mr.getMetaParameters() > 0)
        getChildren().add(new MetaParametersTreeItem(mr));
    } /* addChildren */
  } /* class MetaRoutineTreeItem */
  
  /*==================================================================*/
  private class MetaRoutinesTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaRoutinesTreeItem(MetaSchema ms)
    {
      super(ms);
    } /* constructor */
    @Override
    protected void addChildren()
    {
      MetaSchema ms = (MetaSchema)getValue();
      for (int iRoutine = 0; iRoutine < ms.getMetaRoutines(); iRoutine++)
        getChildren().add(new MetaRoutineTreeItem(ms.getMetaRoutine(iRoutine)));
    } /* addChildren */
  } /* class MetaRoutinesTreeItem */
  
  /*====================================================================*/
  private class MetaColumnTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaColumnTreeItem(MetaColumn mc)
    {
      super(mc);
    } /* constructor */
    @Override
    public void addChildren()
    {
      MetaColumn mc = (MetaColumn)getValue();
      try
      {
        if (mc.getMetaFields() > 0)
          getChildren().add(new MetaFieldsTreeItem(mc));
      }
      catch(IOException ie) { _il.exception(ie); }
    } /* addChildren */
  } /* MetaColumnTreeItem */
  
  /*==================================================================*/
  private class MetaColumnsTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaColumnsTreeItem(MetaTable mt)
    {
      super(mt);
    } /* constructor */
    public MetaColumnsTreeItem(MetaView mv)
    {
      super(mv);
    } /* constructor */
    @Override
    protected void addChildren()
    {
      Object oItem = this.getValue();
      if (oItem instanceof MetaTable)
      {
        MetaTable mt = (MetaTable)oItem;
        for (int iColumn = 0; iColumn < mt.getMetaColumns(); iColumn++)
          getChildren().add(new MetaColumnTreeItem(mt.getMetaColumn(iColumn)));
      }
      else if (oItem instanceof MetaView)
      {
        MetaView mv = (MetaView)oItem;
        for (int iColumn = 0; iColumn < mv.getMetaColumns(); iColumn++)
          getChildren().add(new MetaColumnTreeItem(mv.getMetaColumn(iColumn)));
      }
    } /* addChildren */
  } /* class MetaColumnsTreeItem */

  /*====================================================================*/
  private class MetaFieldTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaFieldTreeItem(MetaField mf)
    {
      super(mf);
    } /* constructor */
    @Override
    public void addChildren()
    {
      try
      {
        MetaField mf = (MetaField)getValue();
        if (mf.getMetaFields() > 0)
          getChildren().add(new MetaFieldsTreeItem(mf));
      }
      catch(IOException ie) { _il.exception(ie); }
    } /* addChildren */
  } /* MetaColumnTreeItem */
  
  /*==================================================================*/
  private class MetaFieldsTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaFieldsTreeItem(MetaColumn mc)
    {
      super(mc);
    } /* constructor */
    public MetaFieldsTreeItem(MetaField mf)
    {
      super(mf);
    } /* constructor */
    @Override
    protected void addChildren()
    {
      try
      {
        if (getValue() instanceof MetaColumn)
        {
          MetaColumn mc = (MetaColumn)getValue();
          for (int iField = 0; iField < mc.getMetaFields(); iField++)
            getChildren().add(new MetaFieldTreeItem(mc.getMetaField(iField)));
        }
        else if (getValue() instanceof MetaField)
        {
          MetaField mf = (MetaField)getValue();
          for (int iField = 0; iField < mf.getMetaFields(); iField++)
            getChildren().add(new MetaFieldTreeItem(mf.getMetaField(iField)));
        }
      }
      catch(IOException ie) { _il.exception(ie); }
    } /* addChildren */
  } /* class MetaFieldsTreeItem */
  
  /*==================================================================*/
  private class MetaViewTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaViewTreeItem(MetaView mv)
    {
      super(mv);
    } /* constructor */
    @Override
    public void addChildren()
    {
      MetaView mv = (MetaView)getValue();
      if (mv.getMetaColumns() > 0)
        getChildren().add(new MetaColumnsTreeItem(mv));
    } /* addChildren */
  } /* class MetaViewTreeItem */
  
  /*==================================================================*/
  private class MetaViewsTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaViewsTreeItem(MetaSchema ms)
    {
      super(ms);
    } /* constructor */
    @Override
    protected void addChildren()
    {
      MetaSchema ms = (MetaSchema)getValue();
      for (int iView = 0; iView < ms.getMetaViews(); iView++)
        getChildren().add(new MetaViewTreeItem(ms.getMetaView(iView)));
    } /* addChildren */
  } /* class MetaViewsTreeItem */

  /*====================================================================*/
  private class MetaForeignKeyTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaForeignKeyTreeItem(MetaForeignKey mfk)
    {
      super(mfk);
    } /* constructor */
    @Override
    public void addChildren()
    {
      // foreign key has no children
    } /* addChildren */
  } /* class MetaForeignKeyTreeItem */
  
  /*==================================================================*/
  private class MetaForeignKeysTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaForeignKeysTreeItem(MetaTable mt)
    {
      super(mt);
    } /* constructor */
    @Override
    protected void addChildren()
    {
      MetaTable mt = (MetaTable)getValue();
      for (int iForeignKey = 0; iForeignKey < mt.getMetaForeignKeys(); iForeignKey++)
        getChildren().add(new MetaForeignKeyTreeItem(mt.getMetaForeignKey(iForeignKey)));
    } /* addChildren */
  } /* class MetaForeignKeysTreeItem */
  
  /*====================================================================*/
  private class MetaUniqueKeyTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaUniqueKeyTreeItem(MetaUniqueKey muk)
    {
      super(muk);
    } /* constructor */
    @Override
    public void addChildren()
    {
      // unique key has no children
    } /* addChildren */
  } /* class MetaUniqueKeyTreeItem */
  
  /*==================================================================*/
  private class MetaCandidateKeysTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaCandidateKeysTreeItem(MetaTable mt)
    {
      super(mt);
    } /* constructor */
    @Override
    protected void addChildren()
    {
      MetaTable mt = (MetaTable)getValue();
      for (int iCandidateKey = 0; iCandidateKey < mt.getMetaCandidateKeys(); iCandidateKey++)
        getChildren().add(new MetaUniqueKeyTreeItem(mt.getMetaCandidateKey(iCandidateKey)));
    } /* addChildren */
  } /* class MetaCandidateKeysTreeItem */
  
  /*====================================================================*/
  private class RecordExtractTreeItem
    extends DynamicTreeItem<Object>
  {
    public RecordExtractTreeItem(RecordExtract re)
    {
      super(re);
    } /* constructor RecordExtractTreeItem */
    @Override
    protected void addChildren()
    {
      try
      {
        RecordExtract re = (RecordExtract)getValue();
        if (re.getDelta() > 1)
        {
          for (int iRecordExtract = 0; iRecordExtract < re.getRecordExtracts(); iRecordExtract++)
            getChildren().add(new RecordExtractTreeItem(re.getRecordExtract(iRecordExtract)));
        }
      }
      catch(IOException ie) { _il.exception(ie); }
    } /* addChildren */
  } /* class RecordExtractTreeItem */
  
  /*==================================================================*/
  private class MetaTableTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaTableTreeItem(MetaTable mt)
    {
      super(mt);
    } /* constructor */
    @Override
    public void addChildren()
    {
      MetaTable mt = (MetaTable)getValue();
      if (mt.getRows() > 0)
      {
        try 
        {
          Table table = mt.getTable();
          RecordExtract re = table.getRecordExtract();
          getChildren().add(new RecordExtractTreeItem(re)); 
        }
        catch(IOException ie) { _il.exception(ie); }
      }
      if (mt.getMetaColumns() > 0)
        getChildren().add(new MetaColumnsTreeItem(mt));
      if (mt.getMetaPrimaryKey() != null)
        getChildren().add(new MetaUniqueKeyTreeItem(mt.getMetaPrimaryKey()));
      if (mt.getMetaCandidateKeys() > 0)
        getChildren().add(new MetaCandidateKeysTreeItem(mt));
      if (mt.getMetaForeignKeys() > 0)
        getChildren().add(new MetaForeignKeysTreeItem(mt));
    } /* addChildren */
  } /* class MetaTableTreeItem */
  
  /*==================================================================*/
  private class MetaTablesTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaTablesTreeItem(MetaSchema ms)
    {
      super(ms);
    } /* constructor */
    @Override
    protected void addChildren()
    {
      MetaSchema ms = (MetaSchema)getValue();
      for (int iTable = 0; iTable < ms.getMetaTables(); iTable++)
        getChildren().add(new MetaTableTreeItem(ms.getMetaTable(iTable)));
    } /* addChildren */
  } /* class MetaTablesTreeItem */
  
  /*==================================================================*/
  private class MetaAttributeTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaAttributeTreeItem(MetaAttribute ma)
    {
      super(ma);
    } /* constructor */
    @Override
    public void addChildren()
    {
      // Attribute has no children
    } /* addChildren */
  } /* class MetaAttributeTreeItem */
  
  /*==================================================================*/
  private class MetaAttributesTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaAttributesTreeItem(MetaType mt)
    {
      super(mt);
    } /* constructor */
    @Override
    protected void addChildren()
    {
      MetaType mt = (MetaType)getValue();
      for (int iAttribute = 0; iAttribute < mt.getMetaAttributes(); iAttribute++)
        getChildren().add(new MetaAttributeTreeItem(mt.getMetaAttribute(iAttribute)));
    } /* addChildren */
  } /* class MetaAttributesTreeItem */
  
  /*==================================================================*/
  private class MetaTypeTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaTypeTreeItem(MetaType mt)
    {
      super(mt);
    } /* constructor */
    @Override
    public void addChildren()
    {
      MetaType mt = (MetaType)getValue();
      if (mt.getMetaAttributes() > 0)
        getChildren().add(new MetaAttributesTreeItem(mt));
    } /* addChildren */
  } /* class MetaTypeTreeItem */
  
  /*==================================================================*/
  private class MetaTypesTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaTypesTreeItem(MetaSchema ms)
    {
      super(ms);
    } /* constructor */
    @Override
    protected void addChildren()
    {
      MetaSchema ms = (MetaSchema)getValue();
      for (int iType = 0; iType < ms.getMetaTypes(); iType++)
        getChildren().add(new MetaTypeTreeItem(ms.getMetaType(iType)));
    } /* addChildren */
  } /* class MetaTypesTreeItem */
  
  /*==================================================================*/
  private class MetaSchemaTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaSchemaTreeItem(MetaSchema ms)
    {
      super(ms);
    } /* constructor */
    @Override
    public void addChildren()
    {
      MetaSchema ms = (MetaSchema)getValue();
      if (ms.getMetaTypes() > 0)
        getChildren().add(new MetaTypesTreeItem(ms));
      if (ms.getMetaTables() > 0)
        getChildren().add(new MetaTablesTreeItem(ms));
      if (ms.getMetaViews() > 0)
        getChildren().add(new MetaViewsTreeItem(ms));
      if (ms.getMetaRoutines() > 0)
        getChildren().add(new MetaRoutinesTreeItem(ms));
    } /* addChildren */
  } /* class MetaSchemaTreeItem */
  
  /*==================================================================*/
  private class MetaSchemasTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaSchemasTreeItem(MetaData md)
    {
      super(md);
    } /* constructor */
    /*------------------------------------------------------------------*/
    /** add the children to this TreeItem.
     */
    @Override
    protected void addChildren()
    {
      MetaData md = (MetaData)getValue();
      for (int iSchema = 0; iSchema < md.getMetaSchemas(); iSchema++)
        getChildren().add(new MetaSchemaTreeItem(md.getMetaSchema(iSchema)));
    } /* addChildren */
  } /* class MetaSchemasTreeItem */
  
  /*==================================================================*/
  private class MetaDataTreeItem
    extends DynamicTreeItem<Object>
  {
    public MetaDataTreeItem(MetaData md)
    {
      super(md);
    } /* constructor */
    /*------------------------------------------------------------------*/
    /** add the children to this TreeItem.
     */
    @Override
    protected void addChildren()
    {
      MetaData md = (MetaData)getValue();
      if (md.getMetaSchemas() > 0)
        getChildren().add(new MetaSchemasTreeItem(md));
      if (md.getMetaUsers() > 0)
        getChildren().add(new MetaUsersTreeItem(md));
      if (md.getMetaRoles() > 0)
        getChildren().add(new MetaRolesTreeItem(md));
      if (md.getMetaPrivileges() > 0)
        getChildren().add(new MetaPrivilegesTreeItem(md));
    } /* addChildren */
  } /* MetaDataTreeItem */
  
  /*==================================================================*/
  /** tree cell for meta data.
   */
  private class ArchiveTreeCell
    extends TreeCell<Object>
  {
    public ArchiveTreeCell()
    {
      super();
    }
    @Override
    public void updateItem(Object oItem, boolean bEmpty)
    {
     super.updateItem(oItem,bEmpty);
      String sText = "";
      if (!bEmpty)
      {
        if (oItem != null)
        {
          sText = oItem.toString();
          TreeItem<Object> ti = getTreeItem();
          if (ti instanceof MetaSchemasTreeItem)
            sText = "schemas ("+String.valueOf(ti.getChildren().size())+")";
          else if (ti instanceof MetaUsersTreeItem)
            sText = "users ("+String.valueOf(ti.getChildren().size())+")";
          else if (ti instanceof MetaRolesTreeItem)
            sText = "roles ("+String.valueOf(ti.getChildren().size())+")";
          else if (ti instanceof MetaPrivilegesTreeItem)
            sText = "privileges ("+String.valueOf(ti.getChildren().size())+")";
          else if (ti instanceof MetaTypesTreeItem)
            sText = "types ("+String.valueOf(ti.getChildren().size())+")";
          else if (ti instanceof MetaTablesTreeItem)
            sText = "tables ("+String.valueOf(ti.getChildren().size())+")";
          else if (ti instanceof MetaViewsTreeItem)
            sText = "views ("+String.valueOf(ti.getChildren().size())+")";
          else if (ti instanceof MetaRoutinesTreeItem)
            sText = "routines ("+String.valueOf(ti.getChildren().size())+")";
          else if (ti instanceof MetaAttributesTreeItem)
            sText = "routines ("+String.valueOf(ti.getChildren().size())+")";
          else if (ti instanceof MetaAttributesTreeItem)
            sText = "attributes ("+String.valueOf(ti.getChildren().size())+")";
          else if (ti instanceof MetaColumnsTreeItem)
            sText = "columns ("+String.valueOf(ti.getChildren().size())+")";
          else if (ti instanceof MetaCandidateKeysTreeItem)
            sText = "candidate keys ("+String.valueOf(ti.getChildren().size())+")";
          else if (ti instanceof MetaForeignKeysTreeItem)
            sText = "foreign keys ("+String.valueOf(ti.getChildren().size())+")";
          else if (ti instanceof MetaParametersTreeItem)
            sText = "parameters ("+String.valueOf(ti.getChildren().size())+")";
          else if (ti instanceof MetaFieldsTreeItem)
            sText = "fields ("+String.valueOf(ti.getChildren().size())+")";
        }
      }
      setText(sText);
    } /* updateItem */
  } /* class ArchiveTreeCell */
  /*==================================================================*/
  
  /*------------------------------------------------------------------*/
  /** constructor creates empty tree view
   */
  public ArchiveTreeView()
  {
    super();
    setEditable(false);
    getSelectionModel().selectedItemProperty().addListener(this);
    setCellFactory(new Callback<TreeView<Object>,TreeCell<Object>>()
    {
      @Override public TreeCell<Object> call(TreeView<Object> tv)
      {
        return new ArchiveTreeCell();
      }
    });
    setPrefWidth(FxSizes.fromEms(10.0));
 } /* constructor */

  /*------------------------------------------------------------------*/
  /** display tree view for given archive
   * @param archive
   */
  public void setArchive(Archive archive)
  {
    if (archive != null)
    {
      MetaDataTreeItem mdti = new MetaDataTreeItem(archive.getMetaData());
      mdti.addChildren();
      setRoot(mdti);
      getSelectionModel().select(mdti);
      mdti.setExpanded(true);
      mdti.getChildren().get(0).setExpanded(true);
    }
    else
      setRoot(null);
  } /* setArchive */

  /*------------------------------------------------------------------*/
  /** collapse all record extracts up to rows of selected table.
   * This is called after sorting the table. Therefore the record
   * extract items needs to be replaced.
   * @param re current record extract.
   */
  public void collapseToRows()
  {
    DynamicTreeItem<Object> dti = (DynamicTreeItem<Object>)getSelectionModel().getSelectedItem();
    if (dti.getValue() instanceof RecordExtract)
    {
      for (RecordExtract re = (RecordExtract)dti.getValue(); 
        re.getParentRecordExtract() != null; 
        re = re.getParentRecordExtract())
        dti = (DynamicTreeItem<Object>)dti.getParent();
      DynamicTreeItem<Object> dtiTable = (DynamicTreeItem<Object>)dti.getParent();
      dtiTable.setExpanded(false); // collapses all children etc. and removes children of "rows"
      MetaTable mt = (MetaTable)dtiTable.getValue();
      try
      {
        RecordExtract reRows = mt.getTable().getRecordExtract();
        dti.setValue(reRows); /* these should now be sorted */
        dtiTable.setExpanded(true); /* this should now create the children of "rows" */
        getSelectionModel().select(dtiTable); /* make sure, the details are updated */
      }
      catch(IOException ie) { _il.exception(ie); }
      getSelectionModel().select(dti);
    }
  } /* collapseToRows */
  
  /*------------------------------------------------------------------*/
  /** get selected object.
   * @return selected object.
   */
  public Object getSelectedObject()
  {
    Object oSelected = null;
    MultipleSelectionModel<TreeItem<Object>> msm = getSelectionModel();
    if (msm != null)
    {
      TreeItem<Object> tiSelected = msm.getSelectedItem();
      if (tiSelected != null)
        oSelected = tiSelected.getValue();
    }
    return oSelected;
  } /* getSelectedObject */

  /*------------------------------------------------------------------*/
  /** get parent object of given meta data object.
   * @param ms meta data object.
   * @return parent object.
   */
  private MetaSearch getParent(MetaSearch ms)
  {
    MetaSearch msParent = null;
    if (ms instanceof MetaSchema)
      msParent = ((MetaSchema)ms).getParentMetaData();
    else if (ms instanceof MetaType)
      msParent = ((MetaType)ms).getParentMetaSchema();
    else if (ms instanceof MetaTable)
      msParent = ((MetaTable)ms).getParentMetaSchema();
    else if (ms instanceof MetaView)
      msParent = ((MetaView)ms).getParentMetaSchema();
    else if (ms instanceof MetaRoutine)
      msParent = ((MetaRoutine)ms).getParentMetaSchema();
    else if (ms instanceof MetaAttribute)
      msParent = ((MetaAttribute)ms).getParentMetaType();
    else if (ms instanceof MetaColumn)
    {
      MetaColumn mc = (MetaColumn)ms;
      msParent = mc.getParentMetaTable();
      if (msParent == null)
        msParent = mc.getParentMetaView();
    }
    else if (ms instanceof MetaField)
    {
      MetaField mf = (MetaField)ms;
      msParent = mf.getParentMetaColumn();
      if (msParent == null)
        msParent = mf.getParentMetaField();
    }
    else if (ms instanceof MetaUniqueKey)
      msParent = ((MetaUniqueKey) ms).getParentMetaTable();
    else if (ms instanceof MetaForeignKey)
      msParent = ((MetaForeignKey)ms).getParentMetaTable();
    else if (ms instanceof MetaParameter)
      msParent = ((MetaParameter)ms).getParentMetaRoutine();
    return msParent;
  } /* getParent */
  
  /*------------------------------------------------------------------*/
  /** locate the given meta data object in the given tree item or under it.
   * @param dti tree item.
   * @param ms meta data object.
   * @return located tree item.
   */
  private DynamicTreeItem<Object> locate(DynamicTreeItem<Object> dti, MetaSearch ms)
  {
    DynamicTreeItem<Object> dtiLocated = null;
    if (dti.getValue() != ms)
    {
      dti.setExpanded(true);
      for (MetaSearch msParent = ms; (ms != null) && (dtiLocated == null); msParent = getParent(msParent))
      {
        for (int iChild = 0; (dtiLocated == null) && (iChild < dti.getChildren().size()); iChild++)
        {
          DynamicTreeItem<Object> dtiChild = (DynamicTreeItem<Object>)dti.getChildren().get(iChild);
          if (dti.getValue() == dtiChild.getValue()) // fictitious intermediate tree item
          {
            for (int iGrandChild = 0; iGrandChild < dtiChild.getChildren().size(); iGrandChild++)
            {
              DynamicTreeItem<Object> dtiGrandChild = (DynamicTreeItem<Object>)dtiChild.getChildren().get(iGrandChild);
              if (dtiGrandChild.getValue() == msParent)
                dtiLocated = locate(dtiChild,ms);
            }
          }
          else if (dtiChild.getValue() == msParent)
            dtiLocated = locate(dtiChild,ms);
        }
      }
    }
    else
      dtiLocated = dti;
    return dtiLocated;
  } /* locate */
  
  /*------------------------------------------------------------------*/
  /** select the tree item which represents the given meta data object
   * and scroll to it.
   * @param ms meta data object.
   * @return selected tree item.
   */
  public DynamicTreeItem<Object> select(MetaSearch ms)
  {
    DynamicTreeItem<Object> dti = locate((DynamicTreeItem<Object>)getRoot(),ms);
    scrollTo(getSelectionModel().getSelectedIndex());
    setFocused(true);
    getSelectionModel().select(dti);
    return dti;
  } /* select */
  
  /*------------------------------------------------------------------*/
  /** locate the tree item with the highest resolution record extract 
   * in the given tree item or under it, which contains the given row.
   * @param dti tree item with record extract item. 
   * @param lRow row.
   * @return located tree item.
   */
  private DynamicTreeItem<Object> locate(DynamicTreeItem<Object> dti, long lRow)
  {
    DynamicTreeItem<Object> dtiLocated = null;
    RecordExtract re = (RecordExtract)dti.getValue();
    if (dti.getChildren().size() > 0)
    {
      dti.setExpanded(true);
      long lOffset = re.getOffset();
      for (int iChild = 0; (dtiLocated == null) && (iChild < dti.getChildren().size()); iChild++)
      {
        DynamicTreeItem<Object> dtiChild = (DynamicTreeItem<Object>)dti.getChildren().get(iChild);
        if ((lRow >= lOffset) && (lRow < lOffset + re.getDelta()))
          dtiLocated = locate(dtiChild,lRow);
        lOffset = lOffset + re.getDelta();
      }
    }
    else
      dtiLocated = dti;
    return dtiLocated;
  } /* locate */
  
  /*------------------------------------------------------------------*/
  /** select the tree item with the record extract which contains the 
   * found row in the given table and scroll to it.
   * @param table table with search result.
   * @return selected tree item.
   */
  public DynamicTreeItem<Object> select(Table table)
  {
    DynamicTreeItem<Object> dtiTable = locate((DynamicTreeItem<Object>)getRoot(),table.getMetaTable());
    dtiTable.setExpanded(true);
    DynamicTreeItem<Object> dtiRows = (DynamicTreeItem<Object>)dtiTable.getChildren().get(0);
    DynamicTreeItem<Object> dtiRecordExtract = locate(dtiRows,table.getFoundRow());
    scrollTo(getSelectionModel().getSelectedIndex());
    setFocused(true);
    getSelectionModel().select(dtiRecordExtract);
    return dtiRecordExtract;
  } /* select */
  
  /*------------------------------------------------------------------*/
  /** handle changed selection.
   * @param observable selected tree item that changed.
   * @param tiOld tree item selected previously.
   * @param tiNew tree item to which selection changed. 
   */
  @Override
  public void changed(ObservableValue<? extends TreeItem<Object>> observable,
    TreeItem<Object> tiOld, TreeItem<Object> tiNew)
  {
    SiardGui sg = SiardGui.getSiardGui();
    if (tiOld != null)
      ;// sg.storeMetadata(ctiOld.getValue());
    if (sg != null)
    {
      swDetails.start();
      if (tiNew != null)
      {
        Object oMetaData = tiNew.getValue();
        Class<?> clsTableData = null;
        if (tiNew instanceof MetaDataTreeItem)
          clsTableData = MetaSchema.class;
        else if (tiNew instanceof MetaSchemasTreeItem)
          clsTableData = MetaSchema.class;
        else if (tiNew instanceof MetaUsersTreeItem)
          clsTableData = MetaUser.class;
        else if (tiNew instanceof MetaRolesTreeItem)
          clsTableData = MetaRole.class;
        else if (tiNew instanceof MetaPrivilegesTreeItem)
          clsTableData = MetaPrivilege.class;
        else if (tiNew instanceof MetaSchemaTreeItem)
          clsTableData = MetaTable.class;
        else if (tiNew instanceof MetaTypesTreeItem)
          clsTableData = MetaType.class;
        else if (tiNew instanceof MetaTablesTreeItem)
          clsTableData = MetaTable.class;
        else if (tiNew instanceof MetaViewsTreeItem)
          clsTableData = MetaView.class;
        else if (tiNew instanceof MetaRoutinesTreeItem)
          clsTableData = MetaRoutine.class;
        else if (tiNew instanceof MetaTypeTreeItem)
          clsTableData = MetaAttribute.class;
        else if (tiNew instanceof MetaTableTreeItem)
          clsTableData = MetaColumn.class;
        else if (tiNew instanceof MetaViewTreeItem)
          clsTableData = MetaColumn.class;
        else if (tiNew instanceof MetaRoutineTreeItem)
          clsTableData = MetaParameter.class;
        else if (tiNew instanceof MetaAttributesTreeItem)
          clsTableData = MetaAttribute.class;
        else if (tiNew instanceof MetaColumnsTreeItem)
          clsTableData = MetaColumn.class;
        else if (tiNew instanceof MetaCandidateKeysTreeItem)
          clsTableData = MetaUniqueKey.class;
        else if (tiNew instanceof MetaForeignKeysTreeItem)
          clsTableData = MetaForeignKey.class;
        else if (tiNew instanceof MetaParametersTreeItem)
          clsTableData = MetaParameter.class;
        else if (tiNew instanceof MetaColumnTreeItem)
          clsTableData = MetaField.class;
        sg.showDetails(oMetaData,clsTableData);
      }
      else
        sg.showDetails(null,null);
      swDetails.stop();
      printStatus();
      swRestrict.start();
      MainMenuBar.getMainMenuBar().restrict();
      swRestrict.stop();
      printStatus();
    }
  } /* changed */
  
} /* ArchiveTreeView */
