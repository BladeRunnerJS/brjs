package org.bladerunnerjs.model.engine;

import java.io.File;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.utility.ObserverList;


public interface Node {
	RootNode root();
	Node parentNode();
	File dir();
	File file(String filePath);
	File[] memoizedScopeFiles();
	String getTypeName();
	boolean dirExists();
	boolean exists();
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
