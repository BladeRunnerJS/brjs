package org.bladerunnerjs.plugin.plugins.bundlers.nodejs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.memoization.Getter;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.AssetLocationUtility;
import org.bladerunnerjs.model.AugmentedContentSourceModule;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.SourceModulePatch;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.utility.RelativePathUtility;
import org.bladerunnerjs.utility.SourceModuleResolver;
import org.bladerunnerjs.utility.UnicodeReader;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;

import com.Ostermiller.util.ConcatReader;

public class CommonJsSourceModule implements AugmentedContentSourceModule {

	public static final String NODEJS_DEFINE_BLOCK_HEADER = "define('%s', function(require, exports, module) {\n";
	public static final String NODEJS_DEFINE_BLOCK_FOOTER = "\n});\n";

	private static final Pattern matcherPattern = Pattern.compile("(require|br\\.Core\\.alias|caplin\\.alias|getAlias|getService)\\([ ]*[\"']([^)]+)[\"'][ ]*\\)");
	
	private File assetFile;
	private AssetLocation assetLocation;
	private String requirePath;
	private String className;
	private String assetPath;

	private String defaultFileCharacterEncoding;

	private SourceModulePatch patch;
	
	private MemoizedValue<ComputedValue> computedValue;
	private MemoizedValue<List<AssetLocation>> assetLocationsList;
	private final Map<BundlableNode, SourceModuleResolver> sourceModuleResolvers = new HashMap<>();
	
	public CommonJsSourceModule(File assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		try {
			this.assetLocation = assetLocation;
			this.assetFile = assetFile;
			assetPath = RelativePathUtility.get(assetLocation.assetContainer().app().dir(), assetFile);
			requirePath = assetLocation.requirePrefix() + "/" + RelativePathUtility.get(assetLocation.dir(), assetFile).replaceAll("\\.js$", "");
			className = requirePath.replaceAll("/", ".");
			defaultFileCharacterEncoding = assetLocation.root().bladerunnerConf().getDefaultFileCharacterEncoding();
			patch = SourceModulePatch.getPatchForRequirePath(assetLocation, getRequirePath());
			computedValue = new MemoizedValue<>("NodeJsSourceModule.computedValue", assetLocation.root(), assetFile, patch.getPatchFile(), assetLocation.root().conf().file("bladerunner.conf"));
			assetLocationsList = new MemoizedValue<>("NodeJsSourceModule.assetLocations", assetLocation.root(), assetLocation.assetContainer().dir());
		}
		catch(ConfigException e) {
			throw new AssetFileInstantationException(e);
		}
	}
	
	@Override
	public List<SourceModule> getDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		if(!sourceModuleResolvers.containsKey(bundlableNode)) {
			App app = assetLocation.assetContainer().app();
			sourceModuleResolvers.put(bundlableNode, new SourceModuleResolver(bundlableNode, assetLocation, assetPath, false, app.dir(), app.root().libsDir()));
		}
		SourceModuleResolver sourceModuleResolver = sourceModuleResolvers.get(bundlableNode);
		
		try {
			return sourceModuleResolver.getSourceModules(requirePaths());
		}
		catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
	}
	
	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		return getComputedValue().aliases;
	}
	
	@Override
	public Reader getUnalteredContentReader() throws IOException {
		return new ConcatReader( new Reader[] {
				new BufferedReader(new UnicodeReader(assetFile, defaultFileCharacterEncoding)),
				patch.getReader(),
		});
	}
	
	@Override
	public Reader getReader() throws IOException {
		return new ConcatReader(new Reader[] {
			new StringReader( String.format(NODEJS_DEFINE_BLOCK_HEADER, requirePath) ),
			getUnalteredContentReader(),
			new StringReader( NODEJS_DEFINE_BLOCK_FOOTER )
		});
	}
	
	@Override
	public String getRequirePath() {
		return requirePath;
	}
	
	@Override
	public String getClassname() {
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
	public File dir() {
		return assetFile.getParentFile();
	}
	
	@Override
	public String getAssetName() {
		return assetFile.getName();
	}
	
	@Override
	public String getAssetPath() {
		return assetPath;
	}
	
	private Set<String> requirePaths() throws ModelOperationException {
		return getComputedValue().requirePaths;
	}

	@Override
	public AssetLocation assetLocation()
	{
		return assetLocation;
	}
	
	@Override
	public List<AssetLocation> assetLocations() {
		return assetLocationsList.value(() -> {
			return AssetLocationUtility.getAllDependentAssetLocations(assetLocation);
		});
	}
	
	private ComputedValue getComputedValue() throws ModelOperationException {
		return computedValue.value(new Getter<ModelOperationException>() {
			@Override
			public Object get() throws ModelOperationException {
				ComputedValue computedValue = new ComputedValue();
				
				try(Reader fileReader = new JsCommentStrippingReader(getReader(), false)) {
					StringWriter stringWriter = new StringWriter();
					IOUtils.copy(fileReader, stringWriter);
					
					Matcher m = matcherPattern.matcher(stringWriter.toString());
					while (m.find()) {
						String methodArgument = m.group(2);
						
						if (m.group(1).startsWith("require")) {
							String requirePath = methodArgument;
							computedValue.requirePaths.add(requirePath);
						}
						else if (m.group(1).startsWith("getService")){
							String serviceAliasName = methodArgument;
							//TODO: this is a big hack, remove the "SERVICE!" part and the same in BundleSetBuilder
							computedValue.aliases.add("SERVICE!"+serviceAliasName);
						}
						else {
							computedValue.aliases.add(methodArgument);
						}
					}
				}
				catch(IOException e) {
					throw new ModelOperationException(e);
				}
				
				return computedValue;
			}
		});
	}
	
	private class ComputedValue {
		public Set<String> requirePaths = new HashSet<>();
		public List<String> aliases = new ArrayList<>();
	}
}