package org.bladerunnerjs.plugin.bundlers.commonjs;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.memoization.Getter;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.AmbiguousRequirePathException;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocationUtility;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.SourceModulePatch;
import org.bladerunnerjs.utility.RequirePathUtility;
import org.bladerunnerjs.utility.UnicodeReader;

import com.Ostermiller.util.ConcatReader;

public class DefaultCommonJsSourceModule implements CommonJsSourceModule {
	private MemoizedFile assetFile;
	private AssetLocation assetLocation;
	
	private SourceModulePatch patch;
	
	private MemoizedValue<ComputedValue> computedValue;
	private List<String> requirePaths = new ArrayList<>();

	public DefaultCommonJsSourceModule(MemoizedFile assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		this.assetLocation = assetLocation;
		this.assetFile = assetLocation.root().getMemoizedFile(assetFile);
		
		String requirePath = assetLocation.requirePrefix() + "/" + assetLocation.dir().getRelativePath(assetFile).replaceAll("\\.js$", "");
		requirePaths.add(requirePath);
		
		patch = SourceModulePatch.getPatchForRequirePath(assetLocation, getPrimaryRequirePath());
		computedValue = new MemoizedValue<>(getAssetPath()+" - computedValue", assetLocation.root(), assetFile, patch.getPatchFile());
	}
	
	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		List<Asset> dependendAssets = new ArrayList<>();
		dependendAssets.addAll( getPreExportDefineTimeDependentAssets(bundlableNode) );
		dependendAssets.addAll( getPostExportDefineTimeDependentAssets(bundlableNode) );
		dependendAssets.addAll( getUseTimeDependentAssets(bundlableNode) );
		return dependendAssets;
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
		return RequirePathUtility.getPrimaryRequirePath(this);
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
	public List<Asset> getPreExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return getSourceModulesForRequirePaths( bundlableNode, getComputedValue().preExportDefineTimeRequirePaths );
	}
	
	@Override
	public List<Asset> getPostExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return getSourceModulesForRequirePaths( bundlableNode, getComputedValue().postExportDefineTimeRequirePaths );
	}
	
	@Override
	public List<Asset> getUseTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return getSourceModulesForRequirePaths( bundlableNode, getComputedValue().useTimeRequirePaths );
	}
	
	@Override
	public MemoizedFile dir() {
		return assetFile.getParentFile();
	}
	
	@Override
	public String getAssetName() {
		return assetFile.getName();
	}
	
	@Override
	public String getAssetPath() {
		return assetLocation.assetContainer().app().dir().getRelativePath(assetFile);
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
		DefaultCommonJsSourceModule sourceModule = this;
		return computedValue.value(new Getter<ModelOperationException>() {
			@Override
			public Object get() throws ModelOperationException {
				ComputedValue computedValue = new ComputedValue();
				
				try {
					try(Reader reader = new CommonJsPreExportDefineTimeDependenciesReader(sourceModule)) 
					{
						RequirePathUtility.addRequirePathsFromReader(reader, computedValue.preExportDefineTimeRequirePaths, computedValue.aliases);
					}
					
					try(Reader reader = new CommonJsPostExportDefineTimeDependenciesReader(sourceModule)) 
					{
						RequirePathUtility.addRequirePathsFromReader(reader, computedValue.postExportDefineTimeRequirePaths, computedValue.aliases);
					}

					try(Reader reader = new CommonJsUseTimeDependenciesReader(sourceModule)) 
					{
						RequirePathUtility.addRequirePathsFromReader(reader, computedValue.useTimeRequirePaths, computedValue.aliases);
					}
				}
				catch(IOException e) {
					throw new ModelOperationException(e);
				}
				
				return computedValue;
			}
		});
	}

	private List<Asset> getSourceModulesForRequirePaths(BundlableNode bundlableNode, Set<String> requirePaths) throws ModelOperationException {
		try {
			return bundlableNode.getLinkedAssets( assetLocation, new ArrayList<>(requirePaths) );
		}
		catch (AmbiguousRequirePathException e) {
			e.setSourceRequirePath(getPrimaryRequirePath());
			throw new ModelOperationException(e);
		}
		catch (UnresolvableRequirePathException e) {
			e.setSourceRequirePath(getPrimaryRequirePath());
			throw new ModelOperationException(e);
		}
		catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
	}
	
	private class ComputedValue {
		public Set<String> preExportDefineTimeRequirePaths = new HashSet<>();
		public Set<String> postExportDefineTimeRequirePaths = new HashSet<>();
		public Set<String> useTimeRequirePaths = new HashSet<>();
		public List<String> aliases = new ArrayList<>();
	}
	
}