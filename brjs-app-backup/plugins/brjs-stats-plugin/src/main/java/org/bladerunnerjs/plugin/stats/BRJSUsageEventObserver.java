package org.bladerunnerjs.plugin.stats;

import io.keen.client.java.JavaKeenClientBuilder;
import io.keen.client.java.KeenCallback;
import io.keen.client.java.KeenClient;
import io.keen.client.java.KeenProject;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.plugin.Event;
import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.api.plugin.base.AbstractModelObserverPlugin;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.events.BundleSetCreatedEvent;
import org.bladerunnerjs.model.events.CommandExecutedEvent;
import org.bladerunnerjs.model.events.NewInstallEvent;


public class BRJSUsageEventObserver extends AbstractModelObserverPlugin implements EventObserver
{
	private KeenClient keenClient;
	private volatile int outstandingKeenEvents = 0;
	private Logger logger;
	private static String KEENIO_PROJECT_ID = "54aeab2e90e4bd761ee37832";
	private static String KEENIO_APP_KEY = "7b8d1bd48df22db797f8b7b9ec61063cd0cd16de04db4bba62119f1d2ecbeff4bb0e66468c2c3948e70074cfe6ec93a429d65177193935f7cfad1ce11ec7023619ff03185aa1bc005520ece21fc942ce819afcf948556541e16ee131d32ca6cdf48ca5aa73834346adcc6e06fed163d6";
	// only include 1 key here for atleast *some* security - since this is only a write key and its only for rough stats its not *too* much of an issue. OK well maybe it is //TODO: figure out how to add this in at compile time
	
	public BRJSUsageEventObserver() {
		this.keenClient = new JavaKeenClientBuilder().build();
		KeenClient.initialize(keenClient);
	}
	
	BRJSUsageEventObserver(KeenClient keenClient) {
		this.keenClient = keenClient;
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		brjs.addObserver(this);
		logger = brjs.logger(this.getClass()); 
		KeenProject project = new KeenProject(KEENIO_PROJECT_ID, KEENIO_APP_KEY, KEENIO_APP_KEY);
		keenClient.setDefaultProject(project);
		keenClient.setGlobalProperties( getGlobalKeenIOProps(brjs) );
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	int waitForKeenEventsCount = 0;
		    	while (outstandingKeenEvents > 0 && waitForKeenEventsCount < 10) {
		    		try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e)
					{
					}
		    		waitForKeenEventsCount++;
		    	}
		    }
		});
	}
	
	@Override
	public void onEventEmitted(Event event, Node node)
	{
		if (node instanceof BRJS && node != null) {
			try
			{
				if (!((BRJS) node).bladerunnerConf().getAllowAnonymousStats()) {
					return;
				}
			}
			catch (Exception e)
			{
				return; // assume we don't want to track if there's an exception
			}
		}
		
		String eventType;
		Map<String,Object> eventData = new LinkedHashMap<String,Object>();
		
		if (event instanceof BundleSetCreatedEvent) {
			eventType = "bundlesets";
			BundleSetCreatedEvent bundleSetCreatedEvent = (BundleSetCreatedEvent) event;
			
			BundleSet bundleset = bundleSetCreatedEvent.getBundleSet();
			App bundlesetApp = bundleset.bundlableNode().app();
			String appName = bundlesetApp.getName();
			if (bundleset.bundlableNode().root().systemApp(appName) == bundlesetApp) {
				return;
			}
			
			eventData.put("total_count", bundleset.assets().size());
			eventData.put("execution_duration", bundleSetCreatedEvent.getCreationDuration());
			eventData.put("bundlable_node_type", bundleset.bundlableNode().getClass().getSimpleName());
		}
		else if (event instanceof CommandExecutedEvent) {
			eventType = "commands";
			CommandExecutedEvent commandExecutedEvent = (CommandExecutedEvent) event;
			eventData.put("command_name", commandExecutedEvent.getCommandId());
        }
		else if (event instanceof NewInstallEvent) {        	
			eventType = "installs";
			eventData.put("os_arch", System.getProperty("os.name"));
			eventData.put("os_name", System.getProperty("os.name"));
			eventData.put("os_version", System.getProperty("os.version"));
			eventData.put("java_vendor", System.getProperty("java.vendor"));
			eventData.put("java_version", System.getProperty("java.version"));
    	} else {
    		return;
    	}
		
		eventData.put("timestamp", System.currentTimeMillis());
		
		logger.debug("recording '%s' event with the payload: %s", eventType, eventData);
		outstandingKeenEvents++;
		keenClient.addEventAsync(null, eventType, eventData, null, new KeenCallback()
		{
			@Override
			public void onSuccess()
			{
				outstandingKeenEvents--;
			}
			@Override
			public void onFailure(Exception arg0)
			{
				outstandingKeenEvents--;
			}
		});
	}
	
	private Map<String, Object> getGlobalKeenIOProps(BRJS brjs) {
		Map<String,Object> versionInfo = new LinkedHashMap<>();
		versionInfo.put("brjs_version", brjs.versionInfo().getVersionNumber());
		versionInfo.put("toolkit_version", brjs.root().versionInfo().getVersionNumber());
		versionInfo.put("toolkit_name", "BladeRunnerJS");
		for (JsLib lib : brjs.sdkLibs()) {
			if (lib.getName().toLowerCase().startsWith("ct-")) {
				versionInfo.put("toolkit_name", "CT");
				break;
			}
		}
		return versionInfo;
	}
	
	
}
