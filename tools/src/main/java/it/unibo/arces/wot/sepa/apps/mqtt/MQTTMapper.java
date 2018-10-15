package it.unibo.arces.wot.sepa.apps.mqtt;

import java.io.IOException;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.security.SEPASecurityManager;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermLiteral;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;
import it.unibo.arces.wot.sepa.pattern.JSAP;
import it.unibo.arces.wot.sepa.pattern.Producer;

public class MQTTMapper extends Producer {
	private static final Logger logger = LogManager.getLogger();
	
	private static SEPASecurityManager sm = null;
	
	public static void main(String[] args) throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, IOException, MqttException {
		if (args.length != 1) {
			logger.error("Please provide the jsap file as argument");
			System.exit(-1);
		}
		
		JSAP app = new JSAP(args[0]);
		
		if (app.isSecure()) sm = new SEPASecurityManager("sepa.jks", "sepa2017", "sepa2017",app.getAuthenticationProperties());
		
		MQTTMapper client = new MQTTMapper(app,sm);
		client.init();
		client.close();
	}
	
	public MQTTMapper(JSAP jsap,SEPASecurityManager sm) throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException, IOException {
		super(jsap, "ADD_OBSERVATION",sm);
	}
	
	public void init() throws SEPASecurityException, IOException, SEPAPropertiesException {
		logger.info("Parse semantic mappings");
		JsonObject mappings = getApplicationProfile().getExtendedData().get("semantic-mappings")
				.getAsJsonObject();

		logger.info("Add observations");
		for (Entry<String, JsonElement> mapping : mappings.entrySet()) addObservation(mapping);	
	}
	
	private void addObservation(Entry<String, JsonElement> mapping) throws SEPASecurityException, IOException, SEPAPropertiesException {
		String topic = mapping.getKey();

		String observation = mapping.getValue().getAsJsonObject().get("observation").getAsString();
		String unit = mapping.getValue().getAsJsonObject().get("unit").getAsString();
		String location = mapping.getValue().getAsJsonObject().get("location").getAsString();
		String comment = mapping.getValue().getAsJsonObject().get("comment").getAsString();
		String label = mapping.getValue().getAsJsonObject().get("label").getAsString();

		setUpdateBindingValue("observation",new RDFTermURI(observation));
		setUpdateBindingValue("comment", new RDFTermLiteral(comment));
		setUpdateBindingValue("label",new RDFTermLiteral(label));
		setUpdateBindingValue("location",new RDFTermURI(location));
		setUpdateBindingValue("unit",new RDFTermURI(unit));
		setUpdateBindingValue("topic",new RDFTermLiteral(topic));
		update();
	}

}
