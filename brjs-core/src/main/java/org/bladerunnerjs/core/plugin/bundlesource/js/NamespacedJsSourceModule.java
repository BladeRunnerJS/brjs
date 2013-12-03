package org.bladerunnerjs.core.plugin.bundlesource.js;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.FullyQualifiedLinkedAsset;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ModelOperationException;

import com.Ostermiller.util.ConcatReader;

public class NamespacedJsSourceModule implements SourceModule {
	private LinkedAsset assetFile;
	private AssetLocation assetLocation;
	private String requirePath;
	
	@Override
	public void initializeUnderlyingObjects(AssetLocation assetLocation, File file)
	{
		this.assetLocation = assetLocation;
		this.requirePath = assetLocation.getAssetContainer().file("src").toURI().relativize(file.toURI()).getPath().replaceAll("\\.js$", "");
		assetFile = new FullyQualifiedLinkedAsset();
		assetFile.initializeUnderlyingObjects(assetLocation, file);
	}
	
	@Override
 	public List<SourceModule> getDependentSourceModules() throws ModelOperationException {
		List<SourceModule> dependentSourceModules = assetFile.getDependentSourceModules();
		dependentSourceModules.removeAll(getOrderDependentSourceModules());
		dependentSourceModules.remove(this);
		
		return dependentSourceModules;
	}
	
	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		return assetFile.getAliasNames();
	}
	
	@Override
	public Reader getReader() throws FileNotFoundException {
		return new ConcatReader(assetFile.getReader(), new StringReader(globalizeNonCaplinJsClasses()));
	}
	
	@Override
	public String getRequirePath() {
		return requirePath;
	}
	
	@Override
	public List<SourceModule> getOrderDependentSourceModules() throws ModelOperationException {
		// TODO: scan the source file for caplin.extend(), caplin.implement(), br.extend() & br.implement()
		return new ArrayList<>();
	}
	
	@Override
	public File getUnderlyingFile() {
		return assetFile.getUnderlyingFile();
	}
	
	@Override
	public String getAssetName() {
		return assetFile.getAssetName();
	}
	
	@Override
	public String getAssetPath() {
		return assetFile.getAssetPath();
	}
	
	@Override
	public AssetLocation getAssetLocation()
	{
		return assetLocation;
	}
	
	public String getClassName() {
		return getRequirePath().replaceAll("/", ".");
	}
	
	private String globalizeNonCaplinJsClasses() {
		StringBuffer stringBuffer = new StringBuffer();
		
		try {
			for(SourceModule dependentSourceModules : getDependentSourceModules()) {
				if(!(dependentSourceModules instanceof NamespacedJsSourceModule)) {
					String moduleNamespace = dependentSourceModules.getRequirePath().replaceAll("/", ".");
					stringBuffer.append(moduleNamespace + " = require('" + dependentSourceModules.getRequirePath()  + "');\n");
				}
			}
		}
		catch(ModelOperationException e) {
			throw new RuntimeException(e);
		}
		
		return stringBuffer.toString();
	}
}
