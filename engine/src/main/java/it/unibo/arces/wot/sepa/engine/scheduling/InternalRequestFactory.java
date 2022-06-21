/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.arces.wot.sepa.engine.scheduling;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPASparqlParsingException;
import it.unibo.arces.wot.sepa.commons.security.ClientAuthorization;
import it.unibo.arces.wot.sepa.engine.protocol.sparql11.SPARQL11ProtocolException;
import java.util.Set;

/**
 *
 * @author Lorenzo
 */
public interface InternalRequestFactory {
    public InternalQueryRequest newQueryInstance(
            String              sparql, 
            Set<String>         defaultGraphUri, 
            Set<String>         namedGraphUri,
            ClientAuthorization auth
    ) throws SEPASparqlParsingException ;
    
    public InternalQueryRequest newQueryInstance(
                String              sparql, 
                Set<String>         defaultGraphUri, 
                Set<String>         namedGraphUri,
                ClientAuthorization auth, 
                String              mediaType
        ) throws SEPASparqlParsingException ;

    public InternalUpdateRequest newUpdateInstance(
            String              sparql, 
            Set<String>         defaultGraphUri, 
            Set<String>         namedGraphUri,
            ClientAuthorization auth
    ) throws SPARQL11ProtocolException, SEPASparqlParsingException;
}