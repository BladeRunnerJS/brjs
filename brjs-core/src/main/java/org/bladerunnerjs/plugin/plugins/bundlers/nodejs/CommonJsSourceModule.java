package org.bladerunnerjs.plugin.plugins.bundlers.nodejs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
import org.bladerunnerjs.model.BladerunnerConf;
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
	
	private SourceModulePatch patch;
	
	private MemoizedValue<ComputedValue> computedValue;
	private String relativeRequirePath;
	private String requirePath;
	
	public CommonJsSourceModule(File assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		this.assetLocation = assetLocation;
		this.assetFile = assetFile;
		relativeRequirePath = RelativePathUtility.get(assetLocation.dir(), assetFile).replaceAll("\\.js$", "");
		requirePath = assetLocation.requirePrefix() + "/" + relativeRequirePath; //TODO: calculate this every time, currently we get a  BRJSMemoizationFileAccessException if we do that
		patch = SourceModulePatch.getPatchForRequirePath(assetLocation, getRequirePath());
		computedValue = new MemoizedValue<>("NodeJsSourceModule.computedValue", assetLocation.root(), assetFile, patch.getPatchFile(), BladerunnerConf.getConfigFilePath(assetLocation.root()));
	}
	
	@Override
	public List<SourceModule> getDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		SourceModuleResolver sourceModuleResolver = getSourceModuleResolver(bundlableNode);
		
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
		try
		{
			String defaultFileCharacterEncoding = assetLocation.root().bladerunnerConf().getDefaultFileCharacterEncoding();
			return new ConcatReader( new Reader[] {
					new BufferedReader(new UnicodeReader(assetFile, defaultFileCharacterEncoding)),
					patch.getReader(),
			});
		}
		catch (ConfigException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public Reader getReader() throws IOException {
		return new ConcatReader(new Reader[] {
			new StringReader( String.format(NODEJS_DEFINE_BLOCK_HEADER, getRequirePath()) ),
			getUnalteredContentReader(),
			new StringReader( NODEJS_DEFINE_BLOCK_FOOTER )
		});
	}
	
	@Override
	public String getRequirePath() {
		return requirePath;
	}
	
	@Override
	public boolean isEncapsulatedModule() {
		return true;
	}
	
	@Override
	public List<SourceModule> getOrderDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		return Collections.emptyList();
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
		return RelativePathUtility.get(assetLocation.assetContainer().app().dir(), assetFile);
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
		return AssetLocationUtility.getAllDependentAssetLocations(assetLocation);
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
	
	
	private SourceModuleResolver getSourceModuleResolver(BundlableNode bundlableNode) {
		App app = assetLocation.assetContainer().app();
		return new SourceModuleResolver(bundlableNode, assetLocation, getRequirePath(), app.dir(), app.root().libsDir());
	}
}