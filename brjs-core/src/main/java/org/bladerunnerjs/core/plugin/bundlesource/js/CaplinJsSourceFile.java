package org.bladerunnerjs.core.plugin.bundlesource.js;

import java.io.Reader;
import java.util.List;

import org.bladerunnerjs.model.AliasDefinition;
import org.bladerunnerjs.model.AssetFileObserver;
import org.bladerunnerjs.model.Resources;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.SourceLocation;
import org.bladerunnerjs.model.exception.ModelOperationException;

public class CaplinJsSourceFile implements SourceFile {
	@Override
	public void onSourceLocationsUpdated(List<SourceLocation> sourceLocations) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<SourceFile> getDependentSourceFiles() throws ModelOperationException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<AliasDefinition> getAliases() throws ModelOperationException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean containsClassReferences() throws ModelOperationException {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Reader getReader() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void addObserver(AssetFileObserver observer) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public String getRequirePath() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Resources getResources() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<SourceFile> getOrderDependentSourceFiles() throws ModelOperationException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getClassName() {
		// TODO Auto-generated method stub
		return null;
	}
}
