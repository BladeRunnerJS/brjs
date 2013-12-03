package org.bladerunnerjs.core.plugin.bundlesource.js;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.model.utility.FileModifiedChecker;

public class NodeJsSourceModule implements SourceModule {
	private File assetFile;
	private List<String> requirePaths;
	private List<String> aliasNames;
	private AssetLocation assetLocation;
	private List<String> aliases;
	private FileModifiedChecker fileModifiedChecker;
	private String requirePath;
	
	@Override
	public void initializeUnderlyingObjects(AssetLocation assetLocation, File file)
	{
		this.assetLocation = assetLocation;
		assetFile = file;
		// TODO: this requirePath should use assetLocation.getAssetContainer().requirePrefix() and not require the 'src' directory to contain the entire namespace to be repeated
		this.requirePath = assetLocation.getAssetContainer().file("src").toURI().relativize(assetFile.toURI()).getPath().replaceAll("\\.js$", "");
		fileModifiedChecker = new FileModifiedChecker(assetFile);
	}
	
	@Override
	public List<SourceModule> getDependentSourceModules() throws ModelOperationException {
		List<SourceModule> dependentSourceFiles = new ArrayList<>();
		
		try {
			if (fileModifiedChecker.fileModifiedSinceLastCheck()) {
				recalculateDependencies();
			}
			
			Map<String, SourceModule> sourceFileMap = new HashMap<String, SourceModule>();
			
			for (AssetContainer assetContainer : assetLocation.getAssetContainer().getApp().getAllAssetContainers())
			{
				for (SourceModule sourceFile : assetContainer.sourceFiles())
				{
					sourceFileMap.put(sourceFile.getRequirePath(), sourceFile);
				}
			}
			
			for(String requirePath : requirePaths) {
				SourceModule sourceFile = sourceFileMap.get(requirePath);
				
				if(sourceFile == null) {
					throw new UnresolvableRequirePathException(this.requirePath, requirePath);
				}
				
				dependentSourceFiles.add(sourceFile);
			}
		}
		catch(UnresolvableRequirePathException e) {
			throw new ModelOperationException(e);
		}
		
		return dependentSourceFiles;
	}
	
	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		if (fileModifiedChecker.fileModifiedSinceLastCheck()) {
			recalculateDependencies();
		}
		
		return aliases;
	}
	
	@Override
	public Reader getReader() throws FileNotFoundException {
		return new BufferedReader( new FileReader(assetFile) );
	}
	
	@Override
	public String getRequirePath() {
		return requirePath;
	}
	
	@Override
	public List<SourceModule> getOrderDependentSourceModules() throws ModelOperationException {
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
	
	private void recalculateDependencies() throws ModelOperationException {
		requirePaths = new ArrayList<>();
		aliasNames = new ArrayList<>();
		
		try {
			StringWriter stringWriter = new StringWriter();
			IOUtils.copy(getReader(), stringWriter);
			
			Matcher m = Pattern.compile("(require|br\\.alias|caplin\\.alias)\\([\"']([^)]+)[\"']\\)").matcher(stringWriter.toString());
			
			while(m.find()) {
				boolean isRequirePath = m.group(1).startsWith("require");
				String methodArgument = m.group(2);
				
				if(isRequirePath) {
					String requirePath = methodArgument;
					if (requirePath.startsWith("./"))
					{
						String thisRequirePathRoot = StringUtils.substringBeforeLast(getRequirePath(), "/");
						requirePath = thisRequirePathRoot + requirePath.replaceFirst("^./", "/");
					}
					requirePaths.add(requirePath);
				}
				else {
					aliasNames.add(methodArgument);
				}
			}
			
			aliases = new ArrayList<>();
			for(@SuppressWarnings("unused") String aliasName : aliasNames) {
				// TODO: how do I get the AliasDefinition instance?
				// aliases.add( .... )
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