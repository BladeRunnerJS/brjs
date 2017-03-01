package org.bladerunnerjs.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.DuplicateAssetContainerException;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.DefaultTestPack;
import org.bladerunnerjs.model.SourceResources;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.NameValidator;


public class TypedTestPack extends SourceResources implements NamedNode
{
	private static final String TEST_TYPE_DIR_FORMAT = "^test-";
	private static final Pattern TEST_TYPE_REGEX = Pattern.compile(TEST_TYPE_DIR_FORMAT);
	
	public static final String AMBIGUOUS_TESTS_DIR_WARNING = "There are multiple directories where tests could be located. Both a 'tests' directory and directories matching 'test-*' exist inside of '%s'.";
	public static final String AMBIGUOUS_TESTS_USING_TESTS_DIR = "The 'tests' directory is not empty and will be used a the test pack location. Directories matching 'test-*' should be deleted.";
	public static final String AMBIGUOUS_TESTS_USING_TESTS_HYPHEN_DIR = "The 'tests' directory is empty so 'test-*' directories will be used for the test packs. The 'tests' directory should be deleted.";
	
	private final NodeList<TestPack> technologyTestPacks = new NodeList<>(this, TestPack.class, null, "");
	private final NodeItem<DefaultTestPack> defaultTestPack = new NodeItem<>(this, DefaultTestPack.class, ".");
	private String name;
	
	public TypedTestPack(RootNode rootNode, Node parent, MemoizedFile dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
	}
	
	public static <T extends TypedTestPack> NodeList<T> createNodeSet(Node node, Class<T> nodeListClass)
	{
		boolean testsLocatedInTestsDir = false;
		boolean testsDirExists = node.file("tests").isDirectory();
		Logger logger = node.root().logger(TypedTestPack.class);
		
		if (testsDirExists) {
			testsLocatedInTestsDir = true;
			if (testHypenDirsExist(node)) {
    			logger.warn(AMBIGUOUS_TESTS_DIR_WARNING, node.root().dir().getRelativePath(node.dir()));
    			if (node.file("tests").filesAndDirs().size() > 0) {
    				logger.warn(AMBIGUOUS_TESTS_USING_TESTS_DIR);
    			} else {
    				logger.warn(AMBIGUOUS_TESTS_USING_TESTS_HYPHEN_DIR);
    				testsLocatedInTestsDir = false;
    			}
			}
		}
		
		if (testsLocatedInTestsDir) {
			return new NodeList<T>(node, nodeListClass, "tests", TEST_TYPE_DIR_FORMAT);			
		}
		return new NodeList<T>(node, nodeListClass, ".", TEST_TYPE_DIR_FORMAT);
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
	
	public List<TestPack> testTechs()
	{
		List<TestPack> testTechs = new ArrayList<>();
		if (hasSingleDefaultTestTech()) {
			testTechs.add( defaultTestTech() );
		} else {
			testTechs.addAll( technologyTestPacks.list() );
		}
		return testTechs;
	}

	public TestPack testTech(String technologyName)
	{
		if (technologyName.equals(App.DEFAULT_CONTAINER_NAME)) {	
			return defaultTestTech();
		}
		if (hasSingleDefaultTestTech()) {
			throw new DuplicateAssetContainerException("The test pack at '%s' directly contains test configuration and therefore should not contain sub test tech nodes, yet a named test tech was requested.", 
					root().dir().getRelativePath(dir()) );
		}
		return technologyTestPacks.item(technologyName);
	}
	
	public TestPack defaultTestTech()
	{
		if (!hasSingleDefaultTestTech() && !technologyTestPacks.list().isEmpty()) {
			throw new DuplicateAssetContainerException("The test pack at '%s' contains test tech nodes and therefore should not contain a 'default' test tech, yet the default test tech node was requested.", 
					root().dir().getRelativePath(dir()) );
		}
		return defaultTestPack.item();
	}
	
	private boolean hasSingleDefaultTestTech() {
		for (File file : root().getMemoizedFile(dir()).filesAndDirs()) {
			if (file.getName().equals("tests") || file.getName().endsWith(".conf")) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean testHypenDirsExist(Node node)
	{
		for (File dir : node.dir().dirs()) {
			if (TEST_TYPE_REGEX.matcher(dir.getName()).find()) {
				return true;
			}
		}
		return false;
	}
	
}
