package org.bladerunnerjs.plugin.bundlers.commonjs;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.memoization.Getter;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.AmbiguousRequirePathException;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.api.utility.RequirePathUtility;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.SourceModulePatch;
import org.bladerunnerjs.utility.UnicodeReader;

import com.Ostermiller.util.ConcatReader;

public class DefaultCommonJsSourceModule implements CommonJsSourceModule {
	private MemoizedFile assetFile;

	private SourceModulePatch patch;

	private MemoizedValue<ComputedValue> computedValue;
	private List<String> requirePaths = new ArrayList<>();

	private String primaryRequirePath;

	private AssetContainer assetContainer;

	private List<Asset> implicitDependencies;

	public DefaultCommonJsSourceModule(AssetContainer assetContainer, String requirePrefix, MemoizedFile assetFile, List<Asset> implicitDependencies) {
		this.assetFile = assetFile;
		this.assetContainer = assetContainer;
		this.implicitDependencies = implicitDependencies;

		primaryRequirePath = calculateRequirePath(requirePrefix, assetFile);
		requirePaths.add(primaryRequirePath);

		patch = SourceModulePatch.getPatchForRequirePath(assetContainer, primaryRequirePath);
		computedValue = new MemoizedValue<>(getAssetPath()+" - computedValue", assetContainer.root(), assetFile, patch.getPatchFile());
	}

	@Override
	public void addImplicitDependencies(List<Asset> implicitDependencies) {
		this.implicitDependencies.addAll(implicitDependencies);
	}

	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		List<Asset> dependendAssets = new ArrayList<>();

		dependendAssets.addAll( getPreExportDefineTimeDependentAssets(bundlableNode) );
		dependendAssets.addAll( getPostExportDefineTimeDependentAssets(bundlableNode) );
		dependendAssets.addAll( getUseTimeDependentAssets(bundlableNode) );
		dependendAssets.addAll(implicitDependencies);

		return dependendAssets;
	}

	@Override
	public List<String> getRequirePaths() {
		return requirePaths;
	}

	public Reader getUnalteredContentReader() throws IOException {
		try
		{
			String defaultFileCharacterEncoding = assetContainer.root().bladerunnerConf().getDefaultFileCharacterEncoding();
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
		return primaryRequirePath;
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
	public MemoizedFile file() {
		return assetFile;
	}

	@Override
	public String getAssetName() {
		return assetFile.getName();
	}

	@Override
	public String getAssetPath() {
		return assetContainer.app().dir().getRelativePath(assetFile);
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
			return bundlableNode.assets( this, new ArrayList<>(requirePaths) );
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
		public Set<String> preExportDefineTimeRequirePaths = new LinkedHashSet<>();
		public Set<String> postExportDefineTimeRequirePaths = new LinkedHashSet<>();
		public Set<String> useTimeRequirePaths = new LinkedHashSet<>();
		public List<String> aliases = new ArrayList<>();
	}

	@Override
	public AssetContainer assetContainer()
	{
		return assetContainer;
	}

	@Override
	public boolean isScopeEnforced() {
		return true;
	}

	@Override
	public boolean isRequirable()
	{
		return true;
	}

	public static String calculateRequirePath(String requirePrefix, MemoizedFile assetFile)
	{
		return requirePrefix+"/"+assetFile.requirePathName();
	}

}