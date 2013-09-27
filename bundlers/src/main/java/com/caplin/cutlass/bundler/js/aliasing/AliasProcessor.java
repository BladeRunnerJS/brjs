package com.caplin.cutlass.bundler.js.aliasing;

import static com.caplin.cutlass.structure.CutlassDirectoryLocator.getScope;

import java.io.File;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLStreamReader2;

import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import com.caplin.cutlass.exception.NamespaceException;
import com.caplin.cutlass.structure.NamespaceCalculator;
import com.caplin.cutlass.structure.ScopeLevel;

public class AliasProcessor
{
	private AliasRegistry aliasRegistry;
	private File aliasesFile;
	private AliasingStreamReader streamReader;
	private Set<String> validClasses;
	
	public AliasProcessor(AliasRegistry aliasRegistry, XMLStreamReader2 streamReader, File aliasesFile, Set<String> validClasses)
	{
		this.aliasesFile = aliasesFile;
		this.streamReader = new AliasingStreamReader(streamReader);
		this.aliasRegistry = aliasRegistry;
		this.validClasses = validClasses;
	}
	
	public void processAliasDefinitionsFile() throws BundlerFileProcessingException, XMLStreamException, NamespaceException
	{
		
		ScopeLevel requestLevel = getScope( aliasesFile );
		String packageNamespace = NamespaceCalculator.getPackageNamespaceForBladeLevelResources( aliasesFile );
		
		AliasContext context = new AliasContext(requestLevel, packageNamespace, aliasRegistry, validClasses);
		
		while(streamReader.hasNext())
		{
			AliasingNode node = streamReader.getNextNode();
			node.setContext(context);
			try
			{
				node.register();
			}
			catch (BundlerProcessingException e)
			{
				throw new BundlerFileProcessingException(aliasesFile, streamReader.getLineNumber(), streamReader.getColumnNumber(), e.getMessage());
			}
		}
	}
	
	public void processAliasesFile() throws BundlerFileProcessingException, XMLStreamException, NamespaceException
	{
		
		ScopeLevel requestLevel = getScope( aliasesFile );
		String packageNamespace = NamespaceCalculator.getPackageNamespaceForBladeLevelResources( aliasesFile );
		
		AliasContext context = new AliasContext(requestLevel, packageNamespace, aliasRegistry, validClasses);
		
		while(streamReader.hasNext())
		{
			AliasingNode node = streamReader.getNextNode();
			node.setContext(context);
			try
			{
				node.use();
			}
			catch (BundlerProcessingException e)
			{
				throw new BundlerFileProcessingException(aliasesFile, streamReader.getLineNumber(), streamReader.getColumnNumber(), e.getMessage());
			}
		}
	}
	
}
