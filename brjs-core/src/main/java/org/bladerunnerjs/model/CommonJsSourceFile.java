package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.exception.ModelOperationException;


public class CommonJsSourceFile implements SourceFile {
	private boolean recalculateDependencies;
	private WatchingAssetFile assetFile;
	private List<String> requirePaths;
	private List<String> aliasNames;
	private final String requirePath;
	private final Resources resources;
	private final SourceLocation sourceLocation;
	private List<SourceFile> dependentSourceFiles;
	private List<AliasDefinition> aliases;
	
	public CommonJsSourceFile(String requirePath, File sourceFile, Resources resources, SourceLocation sourceLocation) {
		this.requirePath = requirePath;
		this.resources = resources;
		this.sourceLocation = sourceLocation;
		assetFile = new WatchingAssetFile(sourceFile);
		assetFile.addObserver(new Observer());
	}
	
	@Override
	public void onSourceLocationsUpdated(List<SourceLocation> sourceLocations) {
		dependentSourceFiles = new ArrayList<>();
		aliases = new ArrayList<>();
		
		for(String requirePath : requirePaths) {
			dependentSourceFiles.add(sourceLocation.sourceFile(requirePath));
		}
		
		for(@SuppressWarnings("unused") String aliasName : aliasNames) {
			// TODO: how do I get the AliasDefinition instance?
		}
	}
	
	@Override
	public List<SourceFile> getDependentSourceFiles() throws ModelOperationException {
		if (recalculateDependencies) {
			recalculateDependencies();
		}
		
		return dependentSourceFiles;
	}
	
	@Override
	public List<AliasDefinition> getAliases() throws ModelOperationException {
		if (recalculateDependencies) {
			recalculateDependencies();
		}
		
		return aliases;
	}
	
	@Override
	public boolean containsClassReferences() throws ModelOperationException {
		return false;
	}
	
	@Override
	public Reader getReader() {
		return assetFile.getReader();
	}
	
	@Override
	public void addObserver(AssetFileObserver observer) {
		assetFile.addObserver(observer);
	}
	
	@Override
	public String getRequirePath() {
		return requirePath;
	}
	
	@Override
	public Resources getResources() {
		return resources;
	}
	
	@Override
	public List<SourceFile> getOrderDependentSourceFiles() throws ModelOperationException {
		return new ArrayList<>();
	}
	
	private void recalculateDependencies() throws ModelOperationException {
		requirePaths = new ArrayList<>();
		aliasNames = new ArrayList<>();
		
		try {
			StringWriter stringWriter = new StringWriter();
			IOUtils.copy(assetFile.getReader(), stringWriter);
			
			Matcher m = Pattern.compile("(require|br\\.alias|caplin\\.alias)\\([\"']([^)]+)[\"']\\)").matcher(stringWriter.toString());
			
			while(m.find()) {
				boolean isRequirePath = m.group(1).startsWith("require");
				String methodArgument = m.group(2);
				
				if(isRequirePath) {
					requirePaths.add(methodArgument);
				}
				else {
					aliasNames.add(methodArgument);
				}
			}
		}
		catch(IOException e) {
			throw new ModelOperationException(e);
		}
		
		recalculateDependencies = false;
	}
	
	private class Observer implements AssetFileObserver {
		@Override
		public void onAssetFileModified() {
			recalculateDependencies = true;
		}
	}
}