/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.arces.wot.sepa.engine.acl.storage;

/**
 *
 * @author Lorenzo
 */
public interface ACLRegistrable {
    void register(ACLStorageRegistrableParams params);
    void registerSecure(ACLStorageRegistrableParams params);
    
}