package it.unibo.arces.wot.sepa.engine.scheduling.updateprocessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.response.QueryResponse;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import it.unibo.arces.wot.sepa.commons.sparql.BindingsResults;
import it.unibo.arces.wot.sepa.engine.processing.QueryProcessor;
import it.unibo.arces.wot.sepa.engine.scheduling.InternalQueryRequest;
import it.unibo.arces.wot.sepa.engine.scheduling.InternalUpdateRequest;
import it.unibo.arces.wot.sepa.engine.scheduling.updateprocessing.epspec.EpSpecFactory;
import it.unibo.arces.wot.sepa.engine.scheduling.updateprocessing.epspec.IEndPointSpecification;


public class AsksAsSelectGraphAsVar implements IAsk{
	
	/*
	 * Example:
	 * 
			SELECT ?g ?s ?p ?o {
					VALUES (?g ?s ?p ?o) {
						  (<prova3><s> <p> <o>)
						  (<prova3><s1>	<P> <o1>)
						  (<prova2><s2> <P> <o2>)
					}
					GRAPH ?g {  
						?s ?p ?o.
					}			
			}

	 */

	private String removedAsksAsSelect=null;
	private String addedAsksAsSelect=null;
	private ArrayList<UpdateExtractedData> ueds = new ArrayList<UpdateExtractedData> ();
	private InternalUpdateRequest req;
	private QueryProcessor processor;
	
	public AsksAsSelectGraphAsVar(ArrayList<UpdateExtractedData> ueds,InternalUpdateRequest req, QueryProcessor processor) throws SEPABindingsException {
		this.ueds=ueds;
		this.req=req;
		this.processor=processor;
		this.init();
		
	}
	
	protected void init() throws SEPABindingsException {
		String added="";
		String removed="";
		for (UpdateExtractedData updateConstruct : ueds) {
			if(updateConstruct.needDelete()) {
				String deleteGraph= updateConstruct.getRemovedGraph();
				for (Bindings bind : updateConstruct.getRemoved().getBindings()) {
					removed+=incapsulate(deleteGraph,bind);
				}
			}
			if(updateConstruct.needInsert()) {
				String addedGraph= updateConstruct.getAddedGraph();
				for (Bindings bind : updateConstruct.getAdded().getBindings()) {
					added+=incapsulate(addedGraph,bind);
				}
			}
		}
		if(removed.length()>0) {
			removedAsksAsSelect=generateSelect(removed);
		}
		if(added.length()>0) {
			addedAsksAsSelect=generateSelect(added);
		}
	}

	public String getAsksAsSelect(HashMap<String,ArrayList<Bindings>> allTriple) throws SEPABindingsException {
		IEndPointSpecification eps = EpSpecFactory.getInstance();
		String select = "SELECT ?"+eps.g()
		+" ?"+eps.s()
		+" ?"+eps.p()
		+" ?"+eps.o()
		+" {"
		
		+" VALUES (?"+eps.g()
		+" ?"+eps.s()
		+" ?"+eps.p()
		+" ?"+eps.o()+") { \n";		
		for (String graph : allTriple.keySet()) {
			for (Bindings bind : allTriple.get(graph)) {
				String t =  TripleConverter.tripleToString(bind);
				select+="(<"+graph+">"+t+")\n";
			}
		}
		select+="}";
		
		select+=" GRAPH ?"+eps.g()
		+" { ?"+eps.s()
		+" ?"+eps.p()
		+" ?"+eps.o()
		+" }";		
	
		
		select+="}";
		return select;
	}
	
	protected String generateSelect(String values) {
		IEndPointSpecification eps = EpSpecFactory.getInstance();
		return "SELECT ?"+eps.g()
				+" ?"+eps.s()
				+" ?"+eps.p()
				+" ?"+eps.o()
				+" { "
				
				+" VALUES (?"+eps.g()
				+" ?"+eps.s()
				+" ?"+eps.p()
				+" ?"+eps.o()+") { \n" + values+ "}"
				
				+" GRAPH ?"+eps.g()
				+" { ?"+eps.s()
				+" ?"+eps.p()
				+" ?"+eps.o()
				+" }"
						
				
				+"}";
	}
	
	protected String incapsulate(String graph,Bindings bind ) throws SEPABindingsException {
		return "(<"+graph+">"+TripleConverter.tripleToString(bind)+")\n";
	}
	


	
	public BindingsResults getBindingsForRemoved() throws SEPASecurityException, IOException  {
	
		InternalQueryRequest askquery=new InternalQueryRequest(
				removedAsksAsSelect,
				req.getDefaultGraphUri(),
				req.getNamedGraphUri(),
				req.getClientAuthorization()
				);
		
		return ((QueryResponse)processor.process(askquery)).getBindingsResults();
						
	}
	
	
	public BindingsResults getBindingsForAdded() throws SEPASecurityException, IOException  {
		InternalQueryRequest askquery=new InternalQueryRequest(
				addedAsksAsSelect,
				req.getDefaultGraphUri(),
				req.getNamedGraphUri(),
				req.getClientAuthorization()
				);
		
		return ((QueryResponse)processor.process(askquery)).getBindingsResults();	
		
	}
	
	
	protected HashMap<String,BindingsResults> getReorganizedBindingsForAdded() throws SEPABindingsException, SEPASecurityException, IOException  {
		IEndPointSpecification eps = EpSpecFactory.getInstance();
		HashMap<String,BindingsResults>  list = new HashMap<String,BindingsResults>();
		
		ArrayList<String> vars = eps.vars();
		
		if(needAskSelectForAdded()) {
//			TestMetric t= new TestMetric("");
//			t.start();
			BindingsResults result = getBindingsForAdded();
//			t.stop();
//			System.out.println("real call: "+t.getInterval());
			for (Bindings bind : result.getBindings()) {
				String graph = bind.getValue(eps.g());
				Bindings triple = new Bindings();
				triple.addBinding(eps.s(), bind.getRDFTerm(eps.s()));
				triple.addBinding(eps.p(), bind.getRDFTerm(eps.p()));
				triple.addBinding(eps.o(), bind.getRDFTerm(eps.o()));
				
				if(list.containsKey(graph)) {
					list.get(graph).add(triple);
				}else {
					ArrayList<Bindings> bindList = new ArrayList<Bindings>();
					bindList.add(triple);
					list.put(graph,new BindingsResults(vars, bindList));	
				}
			}
		}
		return list;		
		
	}
	
	
	protected  HashMap<String,BindingsResults> getReorganizedBindingsForRemoved() throws SEPABindingsException, SEPASecurityException, IOException  {
		IEndPointSpecification eps = EpSpecFactory.getInstance();
		HashMap<String,BindingsResults>  list = new HashMap<String,BindingsResults>();
		
		ArrayList<String> vars = eps.vars();
		
		if(needAskSelectForRemoved()) {
			BindingsResults result = getBindingsForRemoved();
			for (Bindings bind : result.getBindings()) {
				String graph = bind.getValue(eps.g());
				Bindings triple = new Bindings();
				triple.addBinding(eps.s(), bind.getRDFTerm(eps.s()));
				triple.addBinding(eps.p(), bind.getRDFTerm(eps.p()));
				triple.addBinding(eps.o(), bind.getRDFTerm(eps.o()));
				
				if(list.containsKey(graph)) {
					list.get(graph).add(triple);
				}else {
					ArrayList<Bindings> bindList = new ArrayList<Bindings>();
					bindList.add(triple);
					list.put(graph,new BindingsResults(vars, bindList));	
				}
			}
		}
		return list;		
		
	}
	
	

	public ArrayList<UpdateExtractedData> filter() throws SEPABindingsException, SEPASecurityException, IOException {

		HashMap<String,BindingsResults> alredyExist  = this.getReorganizedBindingsForAdded();
		HashMap<String,BindingsResults> realRemoved  = this.getReorganizedBindingsForRemoved();
		for (UpdateExtractedData constructs : ueds) {
			
			String graph = constructs.getAddedGraph();
			
			if(constructs.needInsert() && alredyExist.containsKey(graph) ){
				constructs.removeBingingFromAddedList(alredyExist.get(graph)); 
				alredyExist.remove(graph);
			}	
			
			graph = constructs.getRemovedGraph();
			
			if(constructs.needDelete()) {
				if(realRemoved.containsKey(graph)) {
					constructs.setRemoved(realRemoved.get(graph));				
					realRemoved.remove(graph);
				}else {
					constructs.clearRemoved();
				}
			}
			
			
		}
		
		
		return ueds;
	}
	
	
	//----------------------------------------------------------------------SETTERS and GETTERS
	public String getRemovedAsksAsSelect() {
		return removedAsksAsSelect;
	}

	public String getAddedAsksAsSelect() {
		return addedAsksAsSelect;
	}
	
	public boolean needAskSelectForAdded() {
		return addedAsksAsSelect!=null;
	}
	
	public boolean needAskSelectForRemoved() {
		return removedAsksAsSelect!=null;
	}

	
}
