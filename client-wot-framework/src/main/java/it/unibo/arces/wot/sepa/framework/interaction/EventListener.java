package it.unibo.arces.wot.sepa.framework.interaction;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import it.unibo.arces.wot.sepa.commons.sparql.ARBindingsResults;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import it.unibo.arces.wot.sepa.commons.sparql.BindingsResults;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;
import it.unibo.arces.wot.sepa.framework.Event;
import it.unibo.arces.wot.sepa.pattern.ApplicationProfile;
import it.unibo.arces.wot.sepa.pattern.Consumer;

public abstract class EventListener {
	private ApplicationProfile app;
	private HashMap<String,HashMap<String,ThingEventListener>> thingEventListener;
	private HashMap<String,AllEventListener> allEventListener;
	
	public abstract void onEvent(Set<Event> events);
	
	public void startListeningForEvent(String eventURI) throws InvalidKeyException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, KeyStoreException, CertificateException, IOException, URISyntaxException, InterruptedException {
		if (allEventListener.containsKey(eventURI)) return;
		AllEventListener listener = new AllEventListener();
		allEventListener.put(eventURI, listener);
		Bindings bindings = new Bindings();
		bindings.addBinding("event", new RDFTermURI(eventURI));
		allEventListener.get(eventURI).subscribe(bindings);
	}
	
	public void stopListeningForEvent(String eventURI) throws InvalidKeyException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, KeyStoreException, CertificateException, IOException, URISyntaxException, InterruptedException {
		if (!allEventListener.containsKey(eventURI)) return;
		allEventListener.get(eventURI).unsubscribe();
		allEventListener.remove(eventURI);
	}
	
	public void startListeningForEvent(String eventURI,String thingURI) throws InvalidKeyException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, KeyStoreException, CertificateException, IOException, URISyntaxException, InterruptedException {
		if (thingEventListener.containsKey(thingURI)) {
			HashMap<String,ThingEventListener> thingEvents = thingEventListener.get(thingURI);
			if (thingEvents.containsKey(eventURI)) return;
			ThingEventListener listener = new ThingEventListener();
			thingEvents.put(eventURI, listener);
			Bindings bindings = new Bindings();
			bindings.addBinding("event", new RDFTermURI(eventURI));
			bindings.addBinding("thing", new RDFTermURI(thingURI));
			thingEvents.get(eventURI).subscribe(bindings);	
		}
		else {
			HashMap<String,ThingEventListener> thingEvents = new HashMap<String,ThingEventListener>();
			ThingEventListener listener = new ThingEventListener();
			thingEvents.put(eventURI, listener);
			Bindings bindings = new Bindings();
			bindings.addBinding("event", new RDFTermURI(eventURI));
			bindings.addBinding("thing", new RDFTermURI(thingURI));
			thingEventListener.put(thingURI, thingEvents);
			thingEvents.get(eventURI).subscribe(bindings);	
		}
	}
	
	public void stopListeningForEvent(String eventURI,String thingURI) throws InvalidKeyException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, KeyStoreException, CertificateException, IOException, URISyntaxException, InterruptedException {
		if (!thingEventListener.containsKey(thingURI)) return;
		if (!thingEventListener.get(thingURI).containsKey(eventURI)) return;
		thingEventListener.get(thingURI).get(eventURI).unsubscribe();
		thingEventListener.get(thingURI).remove(eventURI);
	}
	
	class AllEventListener extends Consumer {
		public AllEventListener()
				throws IllegalArgumentException, UnrecoverableKeyException, KeyManagementException, KeyStoreException,
				NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, URISyntaxException, InvalidKeyException, NoSuchElementException, NullPointerException, ClassCastException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
			super(app, "SUBSCRIBE_TO_EVENT");
		}

		@Override
		public void onResults(ARBindingsResults results) {
			
		}

		//Variables: ?thing ?instance ?timeStamp ?eOutput ?outputValue
		@Override
		public void onAddedResults(BindingsResults results) {
			
		}

		@Override
		public void onRemovedResults(BindingsResults results) {
			
		}

		@Override
		public void onSubscribe(BindingsResults results) {
			onAddedResults(results);
			
		}

		@Override
		public void onUnsubscribe() {
			
		}	
	}
	
	class ThingEventListener extends Consumer {

		public ThingEventListener()
				throws IllegalArgumentException, UnrecoverableKeyException, KeyManagementException, KeyStoreException,
				NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, URISyntaxException, InvalidKeyException, NoSuchElementException, NullPointerException, ClassCastException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
			super(app, "SUBSCRIBE_TO_THING_EVENT");
		}

		@Override
		public void onResults(ARBindingsResults results) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAddedResults(BindingsResults results) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRemovedResults(BindingsResults results) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSubscribe(BindingsResults results) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onUnsubscribe() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public EventListener() throws InvalidKeyException, FileNotFoundException, NoSuchElementException, IllegalArgumentException, NullPointerException, ClassCastException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		app = new ApplicationProfile("td.jsap");
	}

}