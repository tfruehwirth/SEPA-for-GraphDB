package it.unibo.arces.wot.sepa.api.protocol.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

import javax.websocket.DeploymentException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.client.SslEngineConfigurator;

import org.junit.BeforeClass;
import org.junit.Test;

import it.unibo.arces.wot.sepa.ConfigurationProvider;
import it.unibo.arces.wot.sepa.Sync;
import it.unibo.arces.wot.sepa.TyrusWebsocketClient;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.security.SEPASecurityManager;
import it.unibo.arces.wot.sepa.pattern.JSAP;

import static org.junit.Assert.assertFalse;

public class ITTyrusWebSocketClient {
	protected final Logger logger = LogManager.getLogger();
	protected static JSAP properties = null;
	
	protected static String url = null;
	protected final Sync sync = new Sync();
	
	protected static SEPASecurityManager sm = null;

	protected HashSet<Thread> threadPool = new HashSet<Thread>();

	@BeforeClass
	public static void init() {
		try {
			properties = new ConfigurationProvider().getJsap();
		} catch (SEPAPropertiesException | SEPASecurityException e) {
			assertFalse("Configuration not found", false);
		}
		if (properties.isSecure()) {
			int port = properties.getSubscribePort();
			if (port == -1)
				url = "wss://" + properties.getDefaultHost() + properties.getSubscribePath();
			else
				url = "wss://" + properties.getDefaultHost() + ":" + String.valueOf(port)
						+ properties.getSubscribePath();

			try {
				sm = new SEPASecurityManager();
			} catch (SEPASecurityException e) {
				assertFalse("Security exception " + e.getMessage(), false);
			}
		} else {
			int port = properties.getSubscribePort();
			if (port == -1)
				url = "ws://" + properties.getDefaultHost() + properties.getSubscribePath();
			else
				url = "ws://" + properties.getDefaultHost() + ":" + String.valueOf(port)
						+ properties.getSubscribePath();
		}
	}

	@Test//(timeout = 60000)
	public void Connect() throws URISyntaxException, SEPASecurityException, DeploymentException, IOException {
		int n = 1000;
		sync.reset();

		for (int i = 0; i < n; i++) {
			ClientManager client = ClientManager.createClient();
			if (properties.isSecure()) {
				SslEngineConfigurator config = new SslEngineConfigurator(sm.getSSLContext());
				config.setHostVerificationEnabled(false);
				client.getProperties().put(ClientProperties.SSL_ENGINE_CONFIGURATOR, config);	
			}
			client.connectToServer(new TyrusWebsocketClient(sync), new URI(url));

		}

		sync.waitEvents(n);
	}

}
