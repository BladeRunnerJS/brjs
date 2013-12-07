package org.bladerunnerjs.model;

import java.io.File;
import java.util.Arrays;
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
import org.bladerunnerjs.utility.NameValidator;


public class TestPack extends AbstractBundlableNode implements NamedNode
{
	private final NodeItem<DirNode> tests = new NodeItem<>(DirNode.class, "tests");
	private final NodeItem<DirNode> testSource = new NodeItem<>(DirNode.class, "src-test");
	private AliasesFile aliasesFile;
	private String name;
	
	public TestPack(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, dir);
		this.name = name;
		init(rootNode, parent, dir);
	}
	
	public static NodeMap<TestPack> createNodeSet()
	{
		return new NodeMap<>(TestPack.class, "", null);
	}
	
	@Override
	public List<LinkedAsset> getSeedFiles() {
		return Arrays.asList();
	}
	
	@Override
	public String namespace() {
		return ((AssetContainer) parentNode()).namespace();
	}
	
	@Override
	public List<AssetContainer> getAssetContainers()
	{
		// TODO
		return null;
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
}
