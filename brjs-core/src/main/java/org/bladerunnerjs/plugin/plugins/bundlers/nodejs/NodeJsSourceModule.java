package org.bladerunnerjs.plugin.plugins.bundlers.nodejs;

import java.io.BufferedReader;
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
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
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

import com.Ostermiller.util.ConcatReader;

public class NodeJsSourceModule implements SourceModule {

	public static final String NODEJS_DEFINE_BLOCK_HEADER = "define('%s', function(require, exports, module) {\n";
	public static final String NODEJS_DEFINE_BLOCK_FOOTER = "\n});\n";

	private static final Pattern matcherPattern = Pattern.compile("(require|br\\.Core\\.alias|caplin\\.alias)\\([ ]*[\"']([^)]+)[\"'][ ]*\\)");
	
	private File assetFile;
	private Set<String> requirePaths;
	private List<String> aliasNames;
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
			assetPath = RelativePathUtility.get(assetLocation.getAssetContainer().getApp().dir(), assetFile);
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
		Set<SourceModule> dependentSourceModules = new HashSet<>();
		
		try {
			if (fileModifiedChecker.fileModifiedSinceLastCheck() || patchFileModifiedChecker.fileModifiedSinceLastCheck()) {
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
		aliasNames = new ArrayList<>();
		
		try(Reader fileReader = getReader()) {
			StringWriter stringWriter = new StringWriter();
			IOUtils.copy(fileReader, stringWriter);
			
			Matcher m = matcherPattern.matcher(stringWriter.toString());
			
			while(m.find()) {
				boolean isRequirePath = m.group(1).startsWith("require");
				String methodArgument = m.group(2);
				
				if(isRequirePath) {
					String requirePath = methodArgument;
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

	@Override
	public void addPatch(SourceModulePatch patch)
	{
		this.patch = patch;
		patchFileModifiedChecker = new FileModifiedChecker(patch.getPatchFile());
	}
}