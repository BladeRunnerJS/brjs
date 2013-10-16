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


public class FullyQualifiedSourceFile implements SourceFile {
	private final FullyQualifiedLinkedAssetFile linkedAssetFile;
	private final String requirePath;
	private final Resources resources;
	private boolean recalculateDependencies = true;
	private List<SourceFile> orderDependentSourceFiles;
	private final SourceLocation sourceLocation;
	
	public FullyQualifiedSourceFile(String requirePath, File sourceFile, Resources resources, SourceLocation sourceLocation) {
		this.resources = resources;
		this.requirePath = requirePath;
		this.sourceLocation = sourceLocation;
		linkedAssetFile = new FullyQualifiedLinkedAssetFile(sourceFile);
		linkedAssetFile.addObserver(new Observer());
	}
	
	@Override
	public void onSourceLocationsUpdated(List<SourceLocation> sourceLocations) {
		recalculateDependencies = true;
		linkedAssetFile.onSourceLocationsUpdated(sourceLocations);
	}
	
	@Override
	public List<SourceFile> getDependentSourceFiles() throws ModelOperationException {
		return linkedAssetFile.getDependentSourceFiles();
	}
	
	@Override
	public List<AliasDefinition> getAliases() throws ModelOperationException {
		return linkedAssetFile.getAliases();
	}
	
	@Override
	public boolean containsClassReferences() throws ModelOperationException {
		return linkedAssetFile.containsClassReferences();
	}
	
	@Override
	public Reader getReader() {
		return linkedAssetFile.getReader();
	}
	
	@Override
	public void addObserver(AssetFileObserver observer) {
		linkedAssetFile.addObserver(observer);
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
		if(recalculateDependencies) {
			recalculateDependencies();
		}
		
		return orderDependentSourceFiles;
	}
	
	private void recalculateDependencies() throws ModelOperationException {
		orderDependentSourceFiles = new ArrayList<>();
		
		try {
			StringWriter stringWriter = new StringWriter();
			IOUtils.copy(linkedAssetFile.getReader(), stringWriter);
			
			Matcher m = Pattern.compile("caplin\\.(extend|implement|thirdparty)\\(([^)]+)\\)").matcher(stringWriter.toString());
			
			while(m.find()) {
				String[] params = m.group(2).split("\\s*,\\s*");
				String sourceIdentifier = params[params.length - 1];
				String requirePath = sourceIdentifier.replaceAll("\\.", "/").replaceAll("('|\")", "");
				
				orderDependentSourceFiles.add(sourceLocation.sourceFile(requirePath));
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