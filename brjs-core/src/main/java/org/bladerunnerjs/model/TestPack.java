package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.utility.NameValidator;
import org.bladerunnerjs.utility.filemodification.NodeFileModifiedChecker;


public class TestPack extends AbstractBundlableNode implements NamedNode
{
	private final NodeItem<DirNode> tests = new NodeItem<>(DirNode.class, "tests");
	private final NodeItem<DirNode> testSource = new NodeItem<>(DirNode.class, "src-test");
	private AliasesFile aliasesFile;
	private String name;
	
	private NodeFileModifiedChecker testSourceModulesFileModifiedChecker = new NodeFileModifiedChecker(this);
	private NodeFileModifiedChecker sourceModulesFileModifiedChecker = new NodeFileModifiedChecker(this);
	private List<SourceModule> testSourceModules = null;
	private List<SourceModule> sourceModules = null;
	
	public TestPack(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		
		// TODO: we should never call registerInitializedNode() from a non-final class
		registerInitializedNode();
	}
	
	public static NodeMap<TestPack> createNodeSet(RootNode rootNode)
	{
		return new NodeMap<>(rootNode, TestPack.class, "", null);
	}
	
	@Override
	public List<LinkedAsset> getSeedFiles() 
	{
		return new ArrayList<LinkedAsset>( assetLocation("tests").seedResources("js") );
	}
	
	@Override
	public java.util.List<LinkedAsset> seedFiles() {
		return getSeedFiles();
	};
	
	@Override
	public String requirePrefix()
	{
		return "";
	}
	
	@Override
	public String namespace() {
		return ((AssetContainer) parentNode().parentNode()).namespace(); //TOOD: refactor this
	}
	
	@Override
	public boolean isNamespaceEnforced() {
		return false;
	}
	
	@Override
	public List<AssetContainer> assetContainers()
	{
		List<AssetContainer> assetContainers = new ArrayList<AssetContainer>();
		
		BRJS brjs = root();
		
		Node testScopeNode = parentNode().parentNode();
		
		assetContainers.add(this);
		assetContainers.addAll( this.app().jsLibs() );
		
		if (testScopeNode instanceof Blade)
		{
			Blade blade = (Blade) testScopeNode;
			assetContainers.add( blade );
			assetContainers.add( (Bladeset)blade.parentNode() );
		}
		if (testScopeNode instanceof Bladeset)
		{
			Bladeset bladeset = (Bladeset) testScopeNode;
			assetContainers.add( bladeset );
		}
		if (testScopeNode instanceof Aspect)
		{			
			App app = this.app();
			Aspect aspect = (Aspect) testScopeNode;
			
			assetContainers.add( aspect );
			
			List<Bladeset> bladesets = app.bladesets();
			List<Blade> blades = new ArrayList<Blade>();
			for (Bladeset bladeset : bladesets)
			{
				blades.addAll( bladeset.blades() );
			}
			
			assetContainers.addAll( bladesets );
			assetContainers.addAll( blades );
		}
		if (testScopeNode instanceof Workbench)
		{
			Workbench workbench = (Workbench) testScopeNode;
			assetContainers.add( brjs.locateAncestorNodeOfClass(workbench, Blade.class) );
			assetContainers.add( brjs.locateAncestorNodeOfClass(workbench, Bladeset.class) );
			
			App app = this.app();
			assetContainers.add( app.aspect("default") );
			
		}
		
		//TODO: do we need to add support for 'sdk' level
		
		return assetContainers;
	}
	
	@Override
	public List<SourceModule> sourceModules() {
		if(sourceModulesFileModifiedChecker.hasChangedSinceLastCheck() || (sourceModules == null)) {
			sourceModules = new ArrayList<SourceModule>();
			
			for(AssetPlugin assetPlugin : (root()).plugins().assetProducers()) {
				for (AssetLocation assetLocation : assetLocations())
				{
					if ( !isTestAssetLocation(assetLocation) )
					{
						sourceModules.addAll(assetPlugin.getSourceModules(assetLocation));
					}
				}
			}
		}
		
		return sourceModules;
	}
	
	public List<SourceModule> testSourceModules() {
		if(testSourceModulesFileModifiedChecker.hasChangedSinceLastCheck() || (testSourceModules == null)) {
			testSourceModules = new ArrayList<SourceModule>();
			
			for(AssetPlugin assetPlugin : (root()).plugins().assetProducers()) {
				for (AssetLocation assetLocation : assetLocations())
				{
					if ( isTestAssetLocation(assetLocation) )
					{
						testSourceModules.addAll(assetPlugin.getTestSourceModules(assetLocation));
					}
				}
			}
		}
		
		return testSourceModules;
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public boolean isValidName()
	{
		return NameValidator.isValidDirectoryName(name);
	}
	
	@Override
	public void assertValidName() throws InvalidNameException
	{
		NameValidator.assertValidDirectoryName(this);
	}
	
	@Override
	public String getTemplateName()
	{
		return parentNode().parentNode().getClass().getSimpleName().toLowerCase() + "-" + name;
	}
	
	public AliasesFile aliasesFile()
	{
		if(aliasesFile == null) {
			aliasesFile = new AliasesFile(dir(), "resources/aliases.xml", this);
		}
		
		return aliasesFile;
	}
	
	public DirNode testSource()
	{
		return item(testSource);
	}
	
	public DirNode tests()
	{
		return item(tests);
	}
	
	
	private boolean isTestAssetLocation(AssetLocation assetLocation)
	{
		return assetLocation instanceof TestAssetLocation || assetLocation instanceof ChildTestAssetLocation;
	}
	
}
