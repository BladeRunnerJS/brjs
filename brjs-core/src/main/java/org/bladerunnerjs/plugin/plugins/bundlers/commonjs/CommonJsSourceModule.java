package org.bladerunnerjs.plugin.plugins.bundlers.commonjs;

import java.io.File;
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
import org.bladerunnerjs.memoization.Getter;
import org.bladerunnerjs.memoization.LegacyMemoizedValue;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.AssetLocationUtility;
import org.bladerunnerjs.model.AugmentedContentSourceModule;
import org.bladerunnerjs.model.BladerunnerConf;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.SourceModulePatch;
import org.bladerunnerjs.model.exception.AmbiguousRequirePathException;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.utility.PrimaryRequirePathUtility;
import org.bladerunnerjs.utility.RelativePathUtility;
import org.bladerunnerjs.utility.UnicodeReader;
import org.bladerunnerjs.utility.reader.CharBufferPool;
import org.bladerunnerjs.utility.reader.JsCodeBlockStrippingDependenciesReader;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;
import org.bladerunnerjs.utility.reader.JsPostModuleExportsStrippingReader;

import com.Ostermiller.util.ConcatReader;

public class CommonJsSourceModule implements AugmentedContentSourceModule {

	public static final String COMMONJS_DEFINE_BLOCK_HEADER = "define('%s', function(require, exports, module) {\n";
	public static final String COMMONJS_DEFINE_BLOCK_FOOTER = "\n});\n";

	private static final Pattern matcherPattern = Pattern.compile("(require|br\\.Core\\.alias|caplin\\.alias|getAlias|getService)\\([ ]*[\"']([^)]+)[\"'][ ]*\\)");
	
	private File assetFile;
	private AssetLocation assetLocation;
	
	private SourceModulePatch patch;
	
	private LegacyMemoizedValue<ComputedValue> computedValue;
	private List<String> requirePaths = new ArrayList<>();
	public static final String JS_STYLE = "common-js";
	
	public CommonJsSourceModule(File assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		this.assetLocation = assetLocation;
		this.assetFile = assetFile;
		
		String requirePath = assetLocation.requirePrefix() + "/" + RelativePathUtility.get(assetLocation.root().getFileInfoAccessor(), assetLocation.dir(), assetFile).replaceAll("\\.js$", "");
		requirePaths.add(requirePath);
		
		patch = SourceModulePatch.getPatchForRequirePath(assetLocation, getPrimaryRequirePath());
		computedValue = new LegacyMemoizedValue<>(getAssetPath()+" - computedValue", assetLocation.root(), assetFile, patch.getPatchFile(), BladerunnerConf.getConfigFilePath(assetLocation.root()));
	}
	
	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		try {
			return bundlableNode.getLinkedAssets(assetLocation, requirePaths());
		}
		catch (AmbiguousRequirePathException | UnresolvableRequirePathException e) {
		    e.setSourceRequirePath(getPrimaryRequirePath());
		    throw new ModelOperationException(e);
		}
		catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
	}
	
	@Override
	public List<String> getRequirePaths() {
		return requirePaths;
	}
	
	
	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		return getComputedValue().aliases;
	}
	
	@Override
	public Reader getUnalteredContentReader() throws IOException {
		try
		{
			String defaultFileCharacterEncoding = assetLocation.root().bladerunnerConf().getDefaultFileCharacterEncoding();
			Reader assetReader = new UnicodeReader(assetFile, defaultFileCharacterEncoding);
			if (patch.patchAvailable()){
				return new ConcatReader( new Reader[] { assetReader, patch.getReader() });
			} else {
				return assetReader;
			}
		}
		catch (ConfigException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public Reader getReader() throws IOException {
		return new ConcatReader(new Reader[] {
			new StringReader( String.format(COMMONJS_DEFINE_BLOCK_HEADER, getPrimaryRequirePath()) ),
			getUnalteredContentReader(),
			new StringReader( COMMONJS_DEFINE_BLOCK_FOOTER )
		});
	}
	
	@Override
	public String getPrimaryRequirePath() {
		return PrimaryRequirePathUtility.getPrimaryRequirePath(this);
	}
	
	@Override
	public boolean isEncapsulatedModule() {
		return true;
	}
	
	@Override
	public boolean isGlobalisedModule() {
		return false;
	}
	
	@Override
	public List<SourceModule> getOrderDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		List<SourceModule> result = new ArrayList<SourceModule>();
		try {
			List<Asset> assets = bundlableNode.getLinkedAssets(assetLocation, orderDependentRequirePaths());
			for(Asset asset : assets) {
				if(asset instanceof SourceModule) {
					result.add((SourceModule)asset);
				}
			}
		}
		catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
		return result;
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
		return RelativePathUtility.get(assetLocation.root().getFileInfoAccessor(), assetLocation.assetContainer().app().dir(), assetFile);
	}
	
	private List<String> orderDependentRequirePaths() throws ModelOperationException {
		return new ArrayList<>( getComputedValue().orderDependentRequirePaths );
	}
	
	private List<String> requirePaths() throws ModelOperationException {
		return new ArrayList<>( getComputedValue().requirePaths );
	}

	@Override
	public AssetLocation assetLocation()
	{
		return assetLocation;
	}
	
	@Override
	public List<AssetLocation> assetLocations() {
		return AssetLocationUtility.getAllDependentAssetLocations(assetLocation);
	}
	
	private ComputedValue getComputedValue() throws ModelOperationException {
		return computedValue.value(new Getter<ModelOperationException>() {
			@Override
			public Object get() throws ModelOperationException {
				ComputedValue computedValue = new ComputedValue();
				CharBufferPool pool = assetLocation.root().getCharBufferPool();
				
				try {
					try(Reader reader = new JsCommentStrippingReader(getUnalteredContentReader(), false, pool)) {
						addToComputedValue(computedValue, reader, false);
					}
					
					try(Reader reader = new JsPostModuleExportsStrippingReader(new JsCodeBlockStrippingDependenciesReader(new JsCommentStrippingReader(getUnalteredContentReader(), false, pool), pool), pool)) {
						addToComputedValue(computedValue, reader, true);
					}
				}
				catch(IOException e) {
					throw new ModelOperationException(e);
				}
				
				return computedValue;
			}
		});
	}
	
	private void addToComputedValue(ComputedValue computedValue, Reader reader, boolean defineTimeDependencies) throws IOException {
		StringWriter stringWriter = new StringWriter();
		IOUtils.copy(reader, stringWriter);
		
		Matcher m = matcherPattern.matcher(stringWriter.toString());
		while (m.find()) {
			String methodArgument = m.group(2);
			
			if (m.group(1).startsWith("require")) {
				String requirePath = methodArgument;
				
				if(defineTimeDependencies) {
					computedValue.orderDependentRequirePaths.add(requirePath);
				}
				else {
					computedValue.requirePaths.add(requirePath);
				}
			}
			else if(!defineTimeDependencies) {
				if (m.group(1).startsWith("getService")){
					String serviceAliasName = methodArgument;
					//TODO: this is a big hack, remove the "SERVICE!" part and the same in BundleSetBuilder
					computedValue.aliases.add("SERVICE!"+serviceAliasName);
				}
				else {
					computedValue.aliases.add(methodArgument);
				}
			}
		}
	}

	private class ComputedValue {
		public Set<String> orderDependentRequirePaths = new HashSet<>();
		public Set<String> requirePaths = new HashSet<>();
		public List<String> aliases = new ArrayList<>();
	}
	
}