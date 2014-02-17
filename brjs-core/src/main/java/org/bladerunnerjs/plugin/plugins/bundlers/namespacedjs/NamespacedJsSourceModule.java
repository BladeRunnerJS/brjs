package org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.FullyQualifiedLinkedAsset;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.SourceModulePatch;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.utility.FileModifiedChecker;
import org.bladerunnerjs.utility.RelativePathUtility;

import com.Ostermiller.util.ConcatReader;

public class NamespacedJsSourceModule implements SourceModule {
	private static final Pattern extendPattern = Pattern.compile("(caplin|br\\.Core)\\.(extend|implement|inherit)\\([^,]+,\\s*([^)]+)\\)");
	
	private List<SourceModule> orderDependentSourceModules;
	private FileModifiedChecker fileModifiedChecker;
	private LinkedAsset linkedAsset;
	private AssetLocation assetLocation;
	private String requirePath;
	private String className;

	private SourceModulePatch patch;
	
	@Override
	public void initialize(AssetLocation assetLocation, File dir, String assetName) throws AssetFileInstantationException
	{
		try {
			File assetFile = new File(dir, assetName);
			
			this.assetLocation = assetLocation;
			requirePath = assetLocation.requirePrefix() + "/" + RelativePathUtility.get(assetLocation.dir(), assetFile).replaceAll("\\.js$", "");
			className = requirePath.replaceAll("/", ".");
			fileModifiedChecker = new FileModifiedChecker(assetFile);
			linkedAsset = new FullyQualifiedLinkedAsset();
			linkedAsset.initialize(assetLocation, dir, assetName);
		}
		catch(RequirePathException e) {
			throw new AssetFileInstantationException(e);
		}
	}
	
	@Override
 	public List<SourceModule> getDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		return linkedAsset.getDependentSourceModules(bundlableNode);
	}
	
	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		return linkedAsset.getAliasNames();
	}
	
	@Override
	public Reader getReader() throws IOException {
		
		String defineBlock = "\ndefine('%s', function(require, exports, module) {" +
							 	"module.exports = %s;" +
							 " });";
		String formattedDefineBlock = String.format(defineBlock, requirePath, className);
		Reader[] readers = new Reader[] { linkedAsset.getReader(), patch.getReader(), new StringReader(formattedDefineBlock) };
		return new ConcatReader( readers );
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
		return false;
	}
	
	@Override
	public List<SourceModule> getOrderDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			recalculateDependencies(bundlableNode);
		}
		
		return orderDependentSourceModules;
	}
	
	@Override
	public File dir()
	{
		return linkedAsset.dir();
	}
	
	@Override
	public String getAssetName() {
		return linkedAsset.getAssetName();
	}
	
	@Override
	public String getAssetPath() {
		return linkedAsset.getAssetPath();
	}
	
	@Override
	public AssetLocation getAssetLocation()
	{
		return assetLocation;
	}
	
	private void recalculateDependencies(BundlableNode bundlableNode) throws ModelOperationException {
		try(Reader reader = linkedAsset.getReader()) {
			orderDependentSourceModules = new ArrayList<>();
			
			StringWriter stringWriter = new StringWriter();
			IOUtils.copy(reader, stringWriter);
			Matcher matcher = extendPattern.matcher(stringWriter.toString());
			
			while (matcher.find()) {
				String referencedClass = matcher.group(3);
				String requirePath = referencedClass.replaceAll("\\.", "/");
				
				try {
					orderDependentSourceModules.add(bundlableNode.getSourceModule(requirePath));
				}
				catch(UnresolvableRequirePathException e) {
					// TODO: log the fact that the thing being extended was not found to be a fully qualified class name (probably a variable name), and so is being ignored for the purposes of bundling.
				}
			}
		}
		catch(IOException | RequirePathException e) {
			throw new ModelOperationException(e);
		}
	}

	@Override
	public void addPatch(SourceModulePatch patch)
	{
		this.patch = patch;
	}
}
