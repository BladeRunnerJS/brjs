package org.bladerunnerjs.plugin.bundlesource.js;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
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
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.utility.FileModifiedChecker;

import com.Ostermiller.util.ConcatReader;

public class NodeJsSourceModule implements SourceModule {
	private File assetFile;
	private List<String> requirePaths;
	private List<String> aliasNames;
	private AssetLocation assetLocation;
	private List<String> aliases;
	private FileModifiedChecker fileModifiedChecker;
	private String requirePath;
	private String className;
	
	@Override
	public void initializeUnderlyingObjects(AssetLocation assetLocation, File file)
	{
		String relativeRequirePath = assetLocation.getAssetContainer().file("src").toURI().relativize(file.toURI()).getPath().replaceAll("\\.js$", "");
		
		this.assetLocation = assetLocation;
		assetFile = file;
		requirePath = /* assetLocation.getAssetContainer().requirePrefix() + */ "/" + relativeRequirePath;
		className = relativeRequirePath.replaceAll("/", ".");
		fileModifiedChecker = new FileModifiedChecker(assetFile);
	}
	
	@Override
	public List<SourceModule> getDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		List<SourceModule> dependentSourceModules = new ArrayList<>();
		
		try {
			if (fileModifiedChecker.fileModifiedSinceLastCheck()) {
				recalculateDependencies();
			}
			
			Map<String, SourceModule> sourceModuleMap = new HashMap<String, SourceModule>();
			
			for (AssetContainer assetContainer : assetLocation.getAssetContainer().getApp().getAllAssetContainers())
			{
				for (SourceModule sourceModule : assetContainer.sourceModules())
				{
					sourceModuleMap.put(sourceModule.getRequirePath(), sourceModule);
				}
			}
			
			for(String requirePath : requirePaths) {
				SourceModule sourceModule = sourceModuleMap.get(requirePath);
				
				if(sourceModule == null) {
					throw new UnresolvableRequirePathException(requirePath, this.requirePath);
				}
				
				dependentSourceModules.add(sourceModule);
			}
		}
		catch(UnresolvableRequirePathException e) {
			throw new ModelOperationException(e);
		}
		
		return dependentSourceModules;
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
		return new ConcatReader(new Reader[] {
			new StringReader("define('" + requirePath + "', function(require, exports, module) {\n"),
			new BufferedReader(new FileReader(assetFile)),
			new StringReader("});\n")
		});
	}
	
	@Override
	public String getRequirePath() {
		return requirePath;
	}
	
	@Override
	public String getClassName() {
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
	
	private void recalculateDependencies() throws ModelOperationException {
		requirePaths = new ArrayList<>();
		aliasNames = new ArrayList<>();
		
		try {
			StringWriter stringWriter = new StringWriter();
			IOUtils.copy(getReader(), stringWriter);
			
			Matcher m = Pattern.compile("(require|br\\.alias|caplin\\.alias)\\([ ]*[\"']([^)]+)[\"']\\)").matcher(stringWriter.toString());
			
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