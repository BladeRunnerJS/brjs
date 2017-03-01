package org.bladerunnerjs.model.engine;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.plugin.Event;
import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.utility.ObserverList;


public interface Node {
	RootNode root();
	Node parentNode();
	MemoizedFile dir();
	MemoizedFile file(String filePath);
	MemoizedFile[] memoizedScopeFiles();
	String getTypeName();
	boolean dirExists();
	boolean exists();
	boolean containsFile(String filePath);
	void create() throws InvalidNameException, ModelUpdateException;
	void ready();
	void delete() throws ModelUpdateException;
	MemoizedFile storageDir(String pluginName);
	MemoizedFile storageFile(String pluginName, String filePath);
	NodeProperties nodeProperties(String pluginName);
	void addObserver(EventObserver observer);
	void addObserver(Class<? extends Event> eventType, EventObserver observer);
	void notifyObservers(Event event, Node notifyForNode);
	ObserverList getObservers();
	void discoverAllChildren();
	void incrementFileVersion();
	void incrementChildFileVersions();
}
