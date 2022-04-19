/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.arces.wot.sepa.engine.acl.storage;

import it.unibo.arces.wot.sepa.engine.acl.SEPAAcl.UserData;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.jena.acl.DatasetACL;
import org.apache.jena.query.Dataset;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lorenzo
 */
public class ACLStorageFactoryTest {
    
     private final String initQuery = "PREFIX sepaACL: <http://acl.sepa.com/>"   +System.lineSeparator() +   
                            "PREFIX mp: <http://mysparql.com/> " + System.lineSeparator() + 
                            "INSERT DATA { GRAPH sepaACL:acl { " + System.lineSeparator() + 
                            "   sepaACL:monger" + System.lineSeparator() + 		
                            "       sepaACL:userName    \"monger\" ;" + System.lineSeparator() + 
                            "       sepaACL:memberOf	\"group1\" ;" + System.lineSeparator() + 
                            "       sepaACL:accessInformation 	[" + System.lineSeparator() + 
                            "           sepaACL:graphName   	mp:graph1;" + System.lineSeparator() + 
                            "           sepaACL:allowedRight	sepaACL:update;" + System.lineSeparator() + 
                            "           sepaACL:allowedRight	sepaACL:query" + System.lineSeparator() + 
                            "       ]." + System.lineSeparator() + 
                            "   sepaACL:gonger" + System.lineSeparator() + 
                            "       sepaACL:userName    \"gonger\" ;" + System.lineSeparator() + 
                            "       sepaACL:memberOf	\"group2\" ;" + System.lineSeparator() +              
                            "       sepaACL:accessInformation 	[" + System.lineSeparator() + 
                            "           sepaACL:graphName   	mp:graph2;" + System.lineSeparator() + 
                            "           sepaACL:allowedRight	sepaACL:update;" + System.lineSeparator() + 
                            "           sepaACL:allowedRight	sepaACL:insertData;" + System.lineSeparator() + 
                            "           sepaACL:allowedRight	sepaACL:deleteData;" + System.lineSeparator() + 
                            "           sepaACL:allowedRight	sepaACL:query" + System.lineSeparator() + 
                            "   ]" + System.lineSeparator() + 
                            "}}" + System.lineSeparator();
     
     private final String initGroupsQuery = "PREFIX sepaACL: <http://acl.sepa.com/>" + System.lineSeparator() + 
                            "PREFIX sepaACLGroups: <http://groups.acl.sepa.com/>" + System.lineSeparator() + 
                            "PREFIX mp: <http://mysparql.com/>" + System.lineSeparator() + 
                            "" + System.lineSeparator() + 
                            "INSERT DATA { GRAPH sepaACL:aclGroups {" + System.lineSeparator() + 
                            "    sepaACLGroups:group1		 " + System.lineSeparator() + 
                            "    sepaACL:groupName    \"group1\" ; " + System.lineSeparator() + 
                            "    sepaACL:accessInformation 	[" + System.lineSeparator() + 
                            "   sepaACL:graphName   	mp:graph1;" + System.lineSeparator() + 
                            "   sepaACL:allowedRight	sepaACL:update;" + System.lineSeparator() + 
                            "   sepaACL:allowedRight	sepaACL:query  " + System.lineSeparator() +       
                            "]." + System.lineSeparator() + 
                            "" + System.lineSeparator() + 
                            "" + System.lineSeparator() + 
                            "   sepaACLGroups:group2" + System.lineSeparator() + 
                            "   sepaACL:groupName    \"group2\" ; " + System.lineSeparator() + 
                            "   sepaACL:accessInformation 	[" + System.lineSeparator() + 
                            "   sepaACL:graphName   	mp:graph2;" + System.lineSeparator() + 
                            "   sepaACL:allowedRight	sepaACL:update;" + System.lineSeparator() + 
                            "   sepaACL:allowedRight	sepaACL:query     " + System.lineSeparator() + 
                            "]" + System.lineSeparator() + 
                            "" + System.lineSeparator() + 
                            "" + System.lineSeparator() + 
                            "}}";


     
    
    private final String USER1= "monger";
    private final String USER2= "gonger";
    private final String USER3= "deaduser";
    private final String GROUP1= "group1";
    private final String GROUP2= "group2";
    private final String GROUP3= "deadgroup";
    
    private final String NEWUSER = "newUser";
    private final String NEWGRAPH = "newGraph";
    private final String NEWGROUP = "newGroup";
    
        
    private final String GRAPH1 = "mp:graph1";
    private final String GRAPH2 = "mp:graph2";
    
    public ACLStorageFactoryTest() {
    }

    /**
     * Test of newInstance method, of class ACLStorageFactory.
     */
    @Test 
    public void testNewInstanceDatasetTDB2() throws Exception {
        final Map<String,Object> paramMap = new TreeMap<>();
        final String dsName = "./run/aclStorageTdb2";
        
        try {
            Files.createDirectory(Path.of("./run"));
        } catch(Exception e ) {
            
        }
        
        paramMap.put(ACLStorageDataset.PARAM_DATASETPERSISTENCY, ACLStorageDataset.PARAM_DATASETPERSISTENCY_VALUE_TDB2);
        paramMap.put(ACLStorageDataset.PARAM_DATASETPATH,dsName);
        
        initACLDataset(dsName);

        //testNewInstanceDataset(paramMap);
    }
    
    @Test 
    public void testNewInstanceDatasetTDB1() throws Exception {
        final Map<String,Object> paramMap = new TreeMap<>();
        final String dsName = "./run/aclStorageTdb1";
        paramMap.put(ACLStorageDataset.PARAM_DATASETPERSISTENCY, ACLStorageDataset.PARAM_DATASETPERSISTENCY_VALUE_TDB1);
        paramMap.put(ACLStorageDataset.PARAM_DATASETPATH,dsName);
        try {
            Files.createDirectory(Path.of("./run"));
        } catch(Exception e ) {
            
        }
        
        initACLDataset(dsName);
        
        //testNewInstanceDataset(paramMap);
    }
    
    @Test 
    public void testNewInstanceDatasetMemory() throws Exception {
        final Map<String,Object> paramMap = new TreeMap<>();
        
        testNewInstanceDataset(paramMap,true);
    }

    private void testNewInstanceDataset(final Map<String,Object> paramMap,boolean fMem ) throws Exception {
        System.out.println(this.getClass().getName() + "testNewInstanceDataset()");
        final ACLStorage obj  = ACLStorageFactory.newInstance(ACLStorage.ACLStorageId.asiDataset, paramMap);
        assertNotEquals(null, obj);
        
        if (obj instanceof ACLStorageDataset && fMem) {
            //do a force load
            try {
                final Field f = ACLStorageDataset.class.getDeclaredField("storageDataset");
                f.setAccessible(true);
                final  Dataset ds = ( Dataset) f.get(obj);
                LocalDatasetActions.insertData(ds, initQuery);
                LocalDatasetActions.insertData(ds, initGroupsQuery);
                
            } catch(Exception e ) {
                System.err.println(e);
                fail("Unable to access datset field");
                return;
            }
            
        }
        testNewInstance(obj);

    }
    
    @Test
    public void testNewInstanceJSon() throws Exception {
        System.out.println(this.getClass().getName() + "testNewInstanceJSon()");
    }
    
    private void testLoadUsers(ACLStorage as ) throws Exception  {
        final Map<String,UserData> aclData = as.loadUsers();
        final String graph1 = GRAPH1.replace("mp:", "http://mysparql.com/");
        final String graph2 = GRAPH2.replace("mp:", "http://mysparql.com/");
        assertEquals(2, aclData.size());
        assertTrue(aclData.keySet().contains(USER1));
        assertTrue(aclData.keySet().contains(USER2));
        assertFalse(aclData.keySet().contains(USER3));
        
        assertEquals(1,aclData.get(USER1).graphACLs.size());
        assertEquals(1,aclData.get(USER2).graphACLs.size());
        
        assertTrue(aclData.get(USER1).graphACLs.containsKey(graph1));
        assertFalse(aclData.get(USER1).graphACLs.containsKey(graph2));
        assertTrue(aclData.get(USER2).graphACLs.containsKey(graph2));
        assertFalse(aclData.get(USER2).graphACLs.containsKey(graph1));
        
        
        assertTrue(aclData.get(USER1).graphACLs.get(graph1).contains(DatasetACL.aclId.aiQuery));
        assertTrue(aclData.get(USER1).graphACLs.get(graph1).contains(DatasetACL.aclId.aiUpdate));
        assertFalse(aclData.get(USER1).graphACLs.get(graph1).contains(DatasetACL.aclId.aiClear));
        assertFalse(aclData.get(USER1).graphACLs.get(graph1).contains(DatasetACL.aclId.aiCreate));
        assertFalse(aclData.get(USER1).graphACLs.get(graph1).contains(DatasetACL.aclId.aiDrop));
        assertFalse(aclData.get(USER1).graphACLs.get(graph1).contains(DatasetACL.aclId.aiInsertData));
        assertFalse(aclData.get(USER1).graphACLs.get(graph1).contains(DatasetACL.aclId.aiDeleteData));
        
        
        assertTrue(aclData.get(USER2).graphACLs.get(graph2).contains(DatasetACL.aclId.aiQuery));
        assertTrue(aclData.get(USER2).graphACLs.get(graph2).contains(DatasetACL.aclId.aiUpdate));
        assertTrue(aclData.get(USER2).graphACLs.get(graph2).contains(DatasetACL.aclId.aiInsertData));
        assertTrue(aclData.get(USER2).graphACLs.get(graph2).contains(DatasetACL.aclId.aiDeleteData));
        assertFalse(aclData.get(USER2).graphACLs.get(graph2).contains(DatasetACL.aclId.aiClear));
        assertFalse(aclData.get(USER2).graphACLs.get(graph2).contains(DatasetACL.aclId.aiCreate));
        assertFalse(aclData.get(USER2).graphACLs.get(graph2).contains(DatasetACL.aclId.aiDrop));
        
        
    }
    private void testLoadGroups(ACLStorage as ) throws Exception  {
        final Map<String,Map<String,Set<DatasetACL.aclId>>> aclData = as.loadGroups();
        
        final String graph1 = GRAPH1.replace("mp:", "http://mysparql.com/");
        final String graph2 = GRAPH2.replace("mp:", "http://mysparql.com/");
        assertEquals(2, aclData.size());
        assertTrue(aclData.keySet().contains(GROUP1));
        assertTrue(aclData.keySet().contains(GROUP2));
        assertFalse(aclData.keySet().contains(GROUP3));
        
        assertEquals(1,aclData.get(GROUP1).size());
        assertEquals(1,aclData.get(GROUP2).size());
        
        assertTrue(aclData.get(GROUP1).containsKey(graph1));
        assertFalse(aclData.get(GROUP1).containsKey(graph2));
        assertTrue(aclData.get(GROUP2).containsKey(graph2));
        assertFalse(aclData.get(GROUP2).containsKey(graph1));
        
        
        assertTrue(aclData.get(GROUP1).get(graph1).contains(DatasetACL.aclId.aiQuery));
        assertTrue(aclData.get(GROUP1).get(graph1).contains(DatasetACL.aclId.aiUpdate));
        assertFalse(aclData.get(GROUP1).get(graph1).contains(DatasetACL.aclId.aiClear));
        assertFalse(aclData.get(GROUP1).get(graph1).contains(DatasetACL.aclId.aiCreate));
        assertFalse(aclData.get(GROUP1).get(graph1).contains(DatasetACL.aclId.aiDrop));
        assertFalse(aclData.get(GROUP1).get(graph1).contains(DatasetACL.aclId.aiInsertData));
        assertFalse(aclData.get(GROUP1).get(graph1).contains(DatasetACL.aclId.aiDeleteData));
        
        
        assertTrue(aclData.get(GROUP2).get(graph2).contains(DatasetACL.aclId.aiQuery));
        assertTrue(aclData.get(GROUP2).get(graph2).contains(DatasetACL.aclId.aiUpdate));
        assertFalse(aclData.get(GROUP2).get(graph2).contains(DatasetACL.aclId.aiInsertData));
        assertFalse(aclData.get(GROUP2).get(graph2).contains(DatasetACL.aclId.aiDeleteData));
        assertFalse(aclData.get(GROUP2).get(graph2).contains(DatasetACL.aclId.aiClear));
        assertFalse(aclData.get(GROUP2).get(graph2).contains(DatasetACL.aclId.aiCreate));
        assertFalse(aclData.get(GROUP2).get(graph2).contains(DatasetACL.aclId.aiDrop));
        
        
    }
     
    private void testNewInstance(ACLStorage as) throws Exception {
        
        //check load
        testLoadUsers(as);
        testLoadGroups(as);
        //check group actions
        checkGroupActions(as);
        
        //check user actions
        checkUserActions(as);
        
        
        
        
    }
    
    private boolean checkUserMemberOfReload(ACLStorage as,String user,String group) {
        try {
            final Map<String,UserData> m = as.loadUsers();
            return m.containsKey(user) && m.get(user).memberOf.contains(group);
        } catch(ACLException e ) {
            return false;
        }
    }
    
    private boolean checkUserExistsReload(ACLStorage as,String user) {
        try {
            final Map<String,UserData> m = as.loadUsers();
            return m.containsKey(user);
        } catch(ACLException e ) {
            return false;
        }
    }
    private boolean checkGroupExistsReload(ACLStorage as,String group) {
        try {
            final Map<String,Map<String,Set<DatasetACL.aclId>>> m = as.loadGroups();
            return m.containsKey(group);
        } catch(ACLException e ) {
            return false;
        }
    }
    
    private void checkUserActions(ACLStorage as) {
        try {
            as.addUser(NEWUSER);
            assertTrue(checkUserExistsReload(as, NEWUSER));
            as.addUserToGroup(NEWUSER, NEWGROUP);
            assertTrue(checkUserMemberOfReload(as,NEWUSER,NEWGROUP));
            
            //as.addUserPermission(newUser, GRAPH1, DatasetACL.aclId.aiQuery);
            
        } catch(Exception e ) {
            fail(e.getMessage());
        }
        
        
    }
    
    private void checkGroupActions(ACLStorage as) {
        try {
            as.addGroup(NEWGROUP);
            assertTrue(checkGroupExistsReload(as, NEWUSER));
        } catch(ACLException e ) {
            fail(e.getMessage());
        }
    }
    
    private void initACLDataset(final String dsName ) throws Exception {
        //connect and clear dataset prior to testing
        Dataset ds = LocalDatasetFactory.newInstance(dsName, false);
        
        
        
        
        LocalDatasetActions.insertData(ds, initQuery);
        LocalDatasetActions.insertData(ds, initGroupsQuery);
        ds.close();
    }
    
}