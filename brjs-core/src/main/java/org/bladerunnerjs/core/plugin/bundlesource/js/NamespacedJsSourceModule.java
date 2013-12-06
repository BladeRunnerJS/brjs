package org.bladerunnerjs.core.plugin.bundlesource.js;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.FullyQualifiedLinkedAsset;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;

import com.Ostermiller.util.ConcatReader;

public class NamespacedJsSourceModule implements SourceModule {
	private static final Pattern extendPattern = Pattern.compile("(caplin|br)\\.(extend|implement)\\([^,]+,\\s*([^)]+)\\)");
	
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
 	public List<SourceModule> getDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		return assetFile.getDependentSourceModules(bundlableNode);
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
	public List<SourceModule> getOrderDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		List<SourceModule> orderDependentSourceModules = new ArrayList<>();
		
		try {
			StringWriter stringWriter = new StringWriter();
			IOUtils.copy(assetFile.getReader(), stringWriter);
			Matcher matcher = extendPattern.matcher(stringWriter.toString());
			
			while (matcher.find()) {
				String referencedClass = matcher.group(3);
				String requirePath = referencedClass.replaceAll("\\.", "/");
				
				try {
					orderDependentSourceModules.add(bundlableNode.getSourceFile(requirePath));
				}
				catch(UnresolvableRequirePathException e) {
					// TODO: log the fact that the thing being extended was not found to be a fully qualified class name (probably a variable name), and so is being ignored for the purposes of bundling.
				}
			}
		}
		catch(IOException | RequirePathException e) {
			throw new ModelOperationException(e);
		}
		
		return orderDependentSourceModules;
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
		StringBuffer stringBuffer = new StringBuffer("\n");
		
		try {
			// TODO: we need to think about the current need to provide a bundlableNode as a result of a getReader() invocation
			for(SourceModule dependentSourceModule : getDependentSourceModules(null)) {
				if(!(dependentSourceModule instanceof NamespacedJsSourceModule)) {
					String moduleNamespace = dependentSourceModule.getRequirePath().replaceAll("/", ".");
					stringBuffer.append(moduleNamespace + " = require('" + dependentSourceModule.getRequirePath()  + "');\n");
				}
			}
		}
		catch(ModelOperationException e) {
			throw new RuntimeException(e);
		}
		
		return stringBuffer.toString();
	}
}
