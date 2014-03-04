package com.caplin.cutlass.bundler.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StreamedSource;

import org.apache.commons.io.IOUtils;

import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import com.caplin.cutlass.bundler.io.BundlerFileReaderFactory;
import com.caplin.cutlass.exception.NamespaceException;
import com.caplin.cutlass.structure.CutlassDirectoryLocator;
import com.caplin.cutlass.structure.RequirePrefixCalculator;
import com.caplin.cutlass.structure.ScopeLevel;

public class HtmlFileProcessor
{	
	private Map<String, File> identifiers;
	
	public HtmlFileProcessor()
	{
		identifiers = new HashMap<String, File>();
	}
	
	public void bundleHtml(File htmlFile, Writer writer) throws ContentFileProcessingException 
	{
		try
		{
			validateSourceHtml(htmlFile);
			writeToBundle(htmlFile, writer);
		}
		catch (ContentProcessingException e)
		{
			throw new ContentFileProcessingException(e, htmlFile);
		}
		catch (Exception e)
		{
			throw new ContentFileProcessingException(htmlFile, e, "Error bundling HTML.");
		}
	}
	
	private void writeToBundle(File htmlFile, Writer writer) throws FileNotFoundException, IOException
	{
		try(Reader bundlerFileReader = BundlerFileReaderFactory.getBundlerFileReader(htmlFile))
		{
			writer.append("\n<!-- File: " + htmlFile.getName() + " -->\n");
			IOUtils.copy(bundlerFileReader, writer);
			writer.flush();
		}
	}
	
	private void validateSourceHtml(File htmlFile) throws IOException, ContentFileProcessingException, NamespaceException
	{
		StartTag startTag = getStartTag(htmlFile);
		String namespace = RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(htmlFile);
		String identifier = startTag.getAttributeValue("id");
		
		if(identifier == null)
		{
			throw new ContentFileProcessingException(htmlFile, "HTML template found without an identifier: " +
				startTag.toString()+". Expected root element with namespaced ID of '" + namespace + "'.");
		}
		
		if(!identifier.startsWith(namespace))
		{
			throw new ContentFileProcessingException(htmlFile, "The identifier '" +
				identifier + "' is not correctly namespaced.\nNamespace '" + namespace + "*' was expected.");
		}
		
		ScopeLevel htmlFileScope = CutlassDirectoryLocator.getScope(htmlFile);
		if(htmlFileScope == ScopeLevel.BLADE_SCOPE || htmlFileScope == ScopeLevel.BLADESET_SCOPE)
		{
			File htmlFileWithTheSameIdentifier = identifiers.get(identifier);
			if(htmlFileWithTheSameIdentifier != null)
			{
				throw new ContentFileProcessingException(htmlFile, "HTML template found with a duplicate identifier: " +
						identifier + ". The same identifier is used for the file:\n'" 
						+ htmlFileWithTheSameIdentifier.getAbsolutePath()
						+ "'.");
			}
			identifiers.put(identifier, htmlFile);
		}
	}
	
	private StartTag getStartTag(File htmlFile) throws IOException
	{
		try(Reader bundlerFileReader = BundlerFileReaderFactory.getBundlerFileReader(htmlFile))
		{
			StreamedSource streamedSource = new StreamedSource(bundlerFileReader);
			StartTag startTag = null;
			
			try
			{
				for(Segment nextSegment : streamedSource)
				{
					if(nextSegment instanceof StartTag)
					{
						startTag = (StartTag) nextSegment;
						break;
					}
				}
			}
			finally
			{
				streamedSource.close();
			}
			
			return startTag;
		}
	}
}
