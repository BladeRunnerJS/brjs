package org.bladerunnerjs.plugin.plugins.bundlers.typescript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.utility.FileModifiedChecker;
import org.bladerunnerjs.utility.RelativePathUtility;

import com.Ostermiller.util.ConcatReader;

public class TypeScriptSourceModule implements SourceModule {
	private static Pattern matcherPattern = Pattern.compile("(require)\\([ ]*[\"']([^)]+)[\"'][ ]*\\)");
	
	private File assetFile;
	private File transpiledAssetFile;
	private Set<String> requirePaths;
	private AssetLocation assetLocation;
	private FileModifiedChecker fileModifiedChecker;
	private String requirePath;
	private String className;
	
	@Override
	public void initialize(AssetLocation assetLocation, File assetFile) throws AssetFileInstantationException
	{
		try {
			App app = assetLocation.getAssetContainer().getApp();
			
			this.assetLocation = assetLocation;
			this.assetFile = assetFile;
			requirePath = assetLocation.requirePrefix() + "/" + RelativePathUtility.get(assetLocation.dir(), assetFile).replaceAll("\\.ts$", "");
			transpiledAssetFile = app.storageFile("typescript", requirePath + ".js");
			className = requirePath.replaceAll("/", ".");
			fileModifiedChecker = new FileModifiedChecker(assetFile);
		}
		catch(RequirePathException e) {
			throw new AssetFileInstantationException(e);
		}
	}
	
	@Override
	public List<SourceModule> getDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		Set<SourceModule> dependentSourceModules = new HashSet<>();
		
		try {
			if (fileModifiedChecker.fileModifiedSinceLastCheck()) {
				recalculateDependencies();
			}
			
			for(String requirePath : requirePaths) {
				SourceModule sourceModule = assetLocation.getSourceModuleWithRequirePath(requirePath);
				
				if(sourceModule == null) {
					throw new UnresolvableRequirePathException(requirePath, this.requirePath);
				}
				
				dependentSourceModules.add(sourceModule);
			}
		}
		catch(RequirePathException e) {
			throw new ModelOperationException(e);
		}
		
		return new ArrayList<SourceModule>( dependentSourceModules );
	}
	
	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		return new ArrayList<>();
	}
	
	@Override
	public Reader getReader() throws FileNotFoundException {
		return new ConcatReader(new Reader[] {
			new StringReader("define('" + requirePath + "', function(require, exports, module) {\n"),
			new BufferedReader(new FileReader(transpiledAssetFile)),
			new StringReader("\n});\n")
		});
	}
	
	@Override
	public String getRequirePath() {
		return requirePath;
	}
	
	@Override
	public String getNamespacedName() {
		return className;
	}
	
	@Override
	public boolean isEncapsulatedModule() {
		return true;
	}
	
	@Override
	public List<SourceModule> getOrderDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		return new ArrayList<>();
	}
	
	@Override
	public File getUnderlyingFile() {
		return assetFile;
	}
	
	@Override
	public String getAssetName() {
		return assetFile.getName();
	}
	
	@Override
	public String getAssetPath() {
		return assetFile.getPath();
	}
	
	public File getTranspiledAssetFile() {
		return transpiledAssetFile;
	}
	
	private void recalculateDependencies() throws ModelOperationException {
		requirePaths = new HashSet<>();
		
		try(Reader reader = new BufferedReader(new FileReader(assetFile))) {
			StringWriter stringWriter = new StringWriter();
			IOUtils.copy(reader, stringWriter);
			
			Matcher m = matcherPattern.matcher(stringWriter.toString());
			
			while(m.find()) {
				String methodArgument = m.group(2);
				
				String requirePath = methodArgument;
				requirePaths.add(requirePath);
			}
		}
		catch(IOException e) {
			throw new ModelOperationException(e);
		}
	}

	@Override
	public AssetLocation getAssetLocation()
	{
		return assetLocation;
	}
}