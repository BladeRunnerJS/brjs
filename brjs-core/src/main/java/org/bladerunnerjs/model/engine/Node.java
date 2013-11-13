package org.bladerunnerjs.model.engine;

import java.io.File;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.core.plugin.Event;
import org.bladerunnerjs.core.plugin.EventObserver;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.utility.ObserverList;


public interface Node {
	RootNode root();
	Node parentNode();
	File dir();
	File file(String filePath);
	boolean dirExists();
	boolean containsFile(String filePath);
	void create() throws InvalidNameException, ModelUpdateException;
	void ready();
	void delete() throws ModelUpdateException;
	File storageDir(String pluginName);
	File storageFile(String pluginName, String filePath);
	NodeProperties nodeProperties(String pluginName);
	void addObserver(EventObserver observer);
	void addObserver(Class<? extends Event> eventType, EventObserver observer);
	void notifyObservers(Event event, Node notifyForNode);
	ObserverList getObservers();
	void discoverAllChildren();
}
