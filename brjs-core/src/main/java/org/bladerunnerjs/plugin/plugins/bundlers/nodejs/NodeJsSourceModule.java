package org.bladerunnerjs.plugin.plugins.bundlers.nodejs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.AssetLocationUtility;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.SourceModulePatch;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.utility.FileModifiedChecker;
import org.bladerunnerjs.utility.RelativePathUtility;
import org.bladerunnerjs.utility.UnicodeReader;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;

import com.Ostermiller.util.ConcatReader;

public class NodeJsSourceModule implements SourceModule {

	public static final String NODEJS_DEFINE_BLOCK_HEADER = "define('%s', function(require, exports, module) {\n";
	public static final String NODEJS_DEFINE_BLOCK_FOOTER = "\n});\n";

	private static final Pattern matcherPattern = Pattern.compile("(require|br\\.Core\\.alias|caplin\\.alias|getAlias|getService)\\([ ]*[\"']([^)]+)[\"'][ ]*\\)");
	
	private File assetFile;
	private Set<String> requirePaths;
	private AssetLocation assetLocation;
	private List<String> aliases;
	private FileModifiedChecker fileModifiedChecker;
	private String requirePath;
	private String className;
	private String assetPath;

	private String defaultFileCharacterEncoding;

	private SourceModulePatch patch;
	private FileModifiedChecker patchFileModifiedChecker;
	
	@Override
	public void initialize(AssetLocation assetLocation, File dir, String assetName) throws AssetFileInstantationException
	{
		try {
			this.assetLocation = assetLocation;
			this.assetFile = new File(dir, assetName);
			assetPath = RelativePathUtility.get(assetLocation.assetContainer().app().dir(), assetFile);
			requirePath = assetLocation.requirePrefix() + "/" + RelativePathUtility.get(assetLocation.dir(), assetFile).replaceAll("\\.js$", "");
			className = requirePath.replaceAll("/", ".");
			fileModifiedChecker = new FileModifiedChecker(assetFile);
			defaultFileCharacterEncoding = assetLocation.root().bladerunnerConf().getDefaultFileCharacterEncoding();
		}
		catch(RequirePathException | ConfigException e) {
			throw new AssetFileInstantationException(e);
		}
	}
	
	@Override
	public List<SourceModule> getDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		Set<SourceModule> dependentSourceModules = new LinkedHashSet<>();
		
		try {
			if (fileModifiedChecker.fileModifiedSinceLastCheck() || patchFileModifiedChecker.fileModifiedSinceLastCheck()) {
				recalculateDependencies();
			}
			
			for(String requirePath : requirePaths) {
				SourceModule sourceModule = assetLocation.sourceModule(requirePath);
				
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
		if (fileModifiedChecker.fileModifiedSinceLastCheck() || patchFileModifiedChecker.fileModifiedSinceLastCheck()) {
			recalculateDependencies();
		}
		
		return aliases;
	}
	
	@Override
	public Reader getReader() throws IOException {
		return new ConcatReader(new Reader[] {
			new StringReader( String.format(NODEJS_DEFINE_BLOCK_HEADER, requirePath) ),
			new BufferedReader(new UnicodeReader(assetFile, defaultFileCharacterEncoding)),
			patch.getReader(),
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
	
	private void recalculateDependencies() throws ModelOperationException {
		requirePaths = new HashSet<>();
		aliases = new ArrayList<>();
		
		try(Reader fileReader = new JsCommentStrippingReader(getReader(), false)) {
			StringWriter stringWriter = new StringWriter();
			IOUtils.copy(fileReader, stringWriter);
			
			Matcher m = matcherPattern.matcher(stringWriter.toString());
			while (m.find()) {
				String methodArgument = m.group(2);
				
				if (m.group(1).startsWith("require")) {
					String requirePath = methodArgument;
					requirePaths.add(requirePath);
				}
				else if (m.group(1).startsWith("getService")){
					String serviceAliasName = methodArgument;
					//TODO: this is a big hack, remove the "SERVICE!" part and the same in BundleSetBuilder
					aliases.add("SERVICE!"+serviceAliasName);
				}
				else {
					aliases.add(methodArgument);
				}
			}	
		}
		catch(IOException e) {
			throw new ModelOperationException(e);
		}
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
	
	@Override
	public void addPatch(SourceModulePatch patch)
	{
		this.patch = patch;
		patchFileModifiedChecker = new FileModifiedChecker(patch.getPatchFile());
	}
}