package it.unibo.arces.wot.sepa.pattern;

import it.unibo.arces.wot.sepa.ITAggregator;
import it.unibo.arces.wot.sepa.ITConsumer;
import it.unibo.arces.wot.sepa.ITGenericClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.unibo.arces.wot.sepa.ConfigurationProvider;
import it.unibo.arces.wot.sepa.api.ISubscriptionHandler;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.security.ClientSecurityManager;
import it.unibo.arces.wot.sepa.commons.response.ErrorResponse;
import it.unibo.arces.wot.sepa.commons.response.Notification;
import it.unibo.arces.wot.sepa.commons.response.Response;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.HashMap;

public class ITPattern implements ISubscriptionHandler{

	protected final Logger logger = LogManager.getLogger();
	
	protected static JSAP app = null;
	protected static ClientSecurityManager sm = null;

	protected static ITConsumer consumerAll;
	protected static Producer randomProducer;
	protected static ITAggregator randomAggregator;
	protected static ITConsumer consumerRandom1;
	
	protected static ITGenericClient genericClient;
	protected static HashMap<String,String> subscriptions = new HashMap<>();
	
	@BeforeClass
	public static void init() throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException {
		try {
			app = new ConfigurationProvider().getJsap();
		} catch (SEPAPropertiesException | SEPASecurityException e) {
			assertFalse("Configuration not found", false);
		}

		if (app.isSecure()) {
			sm = new ConfigurationProvider().buildSecurityManager();
			Response ret = sm.register("SEPATest");
			ret = sm.refreshToken();
			assertFalse(ret.isError());
		}
	}
	
	@Before
	public void beginTest() throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException {
		consumerAll = new ITConsumer(app, "ALL", sm);
		randomProducer = new Producer(app, "RANDOM", sm);
		randomAggregator = new ITAggregator(app, "RANDOM", "RANDOM1", sm);
		consumerRandom1 = new ITConsumer(app, "RANDOM1", sm);
		genericClient = new ITGenericClient(app, sm, this);
	}

	@After
	public void afterTest() throws IOException {
		consumerAll.close();
		randomProducer.close();
		randomAggregator.close();
		consumerRandom1.close();
	}
	
	@Test(timeout = 40000)
	public void subscribe() throws InterruptedException, SEPASecurityException, IOException, SEPAPropertiesException,
			SEPAProtocolException, SEPABindingsException {
		consumerAll.subscribe();
	}

	@Test(timeout = 20000)
	public void produce() throws InterruptedException, SEPASecurityException, IOException, SEPAPropertiesException,
			SEPAProtocolException, SEPABindingsException {
		Response ret = randomProducer.update();
		
		assertFalse(ret.isError());
	}

	@Test(timeout = 40000)
	public void subscribeAndResults() throws InterruptedException, SEPASecurityException, IOException,
			SEPAPropertiesException, SEPAProtocolException, SEPABindingsException {
		consumerAll.subscribe();
		consumerAll.waitNotification();
	}

	@Test(timeout = 20000)
	public void notification() throws InterruptedException, SEPASecurityException, IOException, SEPAPropertiesException,
			SEPAProtocolException, SEPABindingsException {
		consumerAll.subscribe();
		consumerAll.waitNotification();

		randomProducer.update();

		consumerAll.waitNotification();
	}

	@Test(timeout = 40000)
	public void aggregation() throws InterruptedException, SEPASecurityException, IOException, SEPAPropertiesException,
			SEPAProtocolException, SEPABindingsException {
		consumerRandom1.subscribe();
		consumerRandom1.waitNotification();

		randomAggregator.subscribe();
		randomAggregator.waitNotification();

		randomProducer.update();

		randomAggregator.waitNotification();
		consumerRandom1.waitNotification();
	}
	
	@Test(timeout =  20000)
	public void genericClientSingleSubscribe() {
		try {
			genericClient.subscribe("ALL", null, 1000,"first");
			
			if (genericClient.getSubscriptionsCount() != 1) {
				synchronized(this) {
					wait(1000);
				}
				assertFalse("Failed to subscribe",genericClient.getSubscriptionsCount()!=1);
			}
			
			genericClient.update("RANDOM", null, 1000);
			
			if (genericClient.getNotificationsCount() != 2) {
				synchronized(this) {
					wait(1000);
				}
				assertFalse("Failed to notify",genericClient.getNotificationsCount()!=2);
			}
			
			genericClient.unsubscribe(subscriptions.get("first"), 1000);
			
			if (genericClient.getSubscriptionsCount() != 0) {
				synchronized(this) {
					wait(1000);
				}
				assertFalse("Failed to unsubscribe",genericClient.getSubscriptionsCount()!=0);
			}
		} catch (SEPAProtocolException | SEPASecurityException | SEPAPropertiesException | SEPABindingsException
				| InterruptedException | IOException e) {
			e.printStackTrace();
			assertFalse(e.getMessage(),true);
		}
	}
	
	@Test(timeout =  20000)
	public void genericClientDoubleSubscribe() {
		try {
			genericClient.subscribe("RANDOM", null, 1000,"first");
			genericClient.subscribe("RANDOM1", null, 1000,"second");
			
			if (genericClient.getSubscriptionsCount() != 2) {
				synchronized(this) {
					wait(1000);
				}
				assertFalse("Failed to subscribe",genericClient.getSubscriptionsCount()!=2);
			}
			
			genericClient.update("RANDOM", null, 1000);
			genericClient.update("RANDOM1", null, 1000);
			
			if (genericClient.getNotificationsCount() != 4) {
				synchronized(this) {
					wait(1000);
				}
				assertFalse("Failed to notify",genericClient.getNotificationsCount()!=2);
			}
			
			genericClient.unsubscribe(subscriptions.get("first"), 1000);
			genericClient.unsubscribe(subscriptions.get("second"), 1000);
					
			if (genericClient.getSubscriptionsCount() != 0) {
				synchronized(this) {
					wait(1000);
				}
				assertFalse("Failed to unsubscribe",genericClient.getSubscriptionsCount()!=0);
			}
		} catch (SEPAProtocolException | SEPASecurityException | SEPAPropertiesException | SEPABindingsException
				| InterruptedException | IOException e) {
			e.printStackTrace();
			assertFalse(e.getMessage(),true);
		}
	}

	@Override
	public void onSemanticEvent(Notification notify) {
		logger.debug(notify);
		genericClient.setOnSemanticEvent(notify.getSpuid());
	}

	@Override
	public void onBrokenConnection() {
		logger.debug("onBrokenConnection");
	}

	@Override
	public void onError(ErrorResponse errorResponse) {
		logger.debug(errorResponse);
	}

	@Override
	public void onSubscribe(String spuid, String alias) {
		logger.debug("onSubscribe "+spuid+" "+alias);
		subscriptions.put(alias, spuid);
		genericClient.setOnSubscribe(spuid,alias);
	}

	@Override
	public void onUnsubscribe(String spuid) {
		logger.debug("onUnsubscribe "+spuid);
		genericClient.setOnUnsubscribe(spuid);
	}
}
