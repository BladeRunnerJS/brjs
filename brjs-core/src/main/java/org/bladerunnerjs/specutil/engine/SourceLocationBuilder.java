package org.bladerunnerjs.specutil.engine;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.AbstractSourceLocation;


public abstract class SourceLocationBuilder<N extends AbstractSourceLocation> extends NodeBuilder<N>
{
	
	private AbstractSourceLocation node;
	
	public SourceLocationBuilder(SpecTest specTest, N node)
	{
		super(specTest, node);
		this.node = node;
	}

	public BuilderChainer hasClass(String className) throws Exception
	{
		String classBody = className + " = function() {\n};\n";
		FileUtils.write(node.src().file(className.replaceAll("\\.", "/") + ".js"), classBody);
		
		return builderChainer;
	}

	public BuilderChainer hasClasses(String... classNames) throws Exception
	{
		for(String className : classNames) {
			hasClass(className);
		}
		
		return builderChainer;
	}

	public BuilderChainer classRefersTo(String sourceClass, String destClass) throws Exception
	{
		FileUtils.write(node.src().file(sourceClass.replaceAll("\\.", "/") + ".js"), destClass);
		
		return builderChainer;
	}

	public BuilderChainer hasBeenPopulated() throws Exception
	{
		node.populate();
		return builderChainer;
	}

}
