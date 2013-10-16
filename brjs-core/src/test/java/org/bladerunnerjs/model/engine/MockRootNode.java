package org.bladerunnerjs.model.engine;

import java.io.File;

import org.bladerunnerjs.core.console.ConsoleWriter;
import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;
import org.bladerunnerjs.core.plugin.Event;
import org.bladerunnerjs.core.plugin.EventObserver;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.utility.ObserverList;


public class MockRootNode implements RootNode {

	@Override
	public Node parentNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File dir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File file(String filePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean dirExists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsFile(String filePath) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void create() throws ModelUpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete() throws ModelUpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public File storageDir(String pluginName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public File storageFile(String pluginName, String filePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void discoverAllChildren() {
		// TODO Auto-generated method stub

	}

	@Override
	public Logger logger(LoggerType type, Class<?> clazz) {
		// TODO Auto-generated method stub
		return new Logger() {
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void fatal(String message, Object... params) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void error(String message, Object... params) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void warn(String message, Object... params) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void info(String message, Object... params) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void debug(String message, Object... params) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Override
	public ConsoleWriter getConsoleWriter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConsoleWriter(ConsoleWriter consoleWriter) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isRootDir(File dir) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Node locateFirstAncestorNode(File file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <N extends Node> N locateAncestorNodeOfClass(File file,
			Class<N> nodeClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerNode(Node node) {
		// TODO Auto-generated method stub

	}

	@Override
	public Node getRegisteredNode(File childPath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addObserver(EventObserver observer)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public NodeProperties nodeProperties(String pluginName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void ready()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public ObserverList getObservers()
	{
		System.err.println("HERE");
		return null;
	}

	@Override
	public RootNode root()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addObserver(Class<? extends Event> eventType, EventObserver observer)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyObservers(Event event, Node notifyForNode)
	{
		// TODO Auto-generated method stub
		
	}
}
