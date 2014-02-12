package org.bladerunnerjs.plugin.plugins.bundlers.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StreamedSource;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class HTMLContentPlugin extends AbstractContentPlugin
{
	private ContentPathParser contentPathParser;
	private Map<String, Asset> identifiers = new HashMap<String, Asset>();
	private List<String> prodRequestPaths = new ArrayList<>();
	
	private BRJS brjs;
	{
		try{
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			contentPathParserBuilder.accepts("bundle.html").as("bundle-request");
			contentPathParser = contentPathParserBuilder.build();
		
			contentPathParser = contentPathParserBuilder.build();
			prodRequestPaths.add(contentPathParser.createRequest("bundle-request"));
		}
		catch(MalformedTokenException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
	}
	
	@Override
	public String getRequestPrefix() {
		return "bundle.html";
	}

	@Override
	public String getGroupName() {
		return "text/html";
	}
	
	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		return new ArrayList<>();
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		return prodRequestPaths;
	}

	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws ContentProcessingException
	{
		identifiers = new HashMap<String, Asset>();
		Writer writer = null;
		try{
			writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding());
		}
		catch( IOException | ConfigException e) {
			throw new ContentProcessingException(e);
		}	
	
		List<Asset> htmlFiles = bundleSet.getResourceFiles("html");
		for(Asset htmlAsset : htmlFiles){
			
			try {
				validateSourceHtml(htmlAsset);
				writer.write("\n<!-- " + htmlAsset.getAssetName()  +  " -->\n");
				IOUtils.copy(htmlAsset.getReader(), writer);
				writer.flush();
			} catch (IOException | NamespaceException | RequirePathException e) {
				throw new ContentProcessingException(e, "Error while bundling asset '" + htmlAsset.getAssetPath() + "'.");
			}
		}
	}
	
	private void validateSourceHtml(Asset htmlAsset) throws IOException, ContentFileProcessingException, NamespaceException, RequirePathException
	{
		StartTag startTag = getStartTag(htmlAsset);
		
		String namespace = htmlAsset.getAssetLocation().getNamespace();
		String identifier = startTag.getAttributeValue("id");
		
		if(identifier == null)
		{
			throw new NamespaceException( "HTML template found without an identifier: '" +
					startTag.toString() + "'.  Root element should have namespaced ID of '" + namespace + ".*'");
		}
		
		if(!identifier.startsWith(namespace))
		{
			throw new NamespaceException( "The identifier '" +
					identifier + "' is not correctly namespaced.\nNamespace '" + namespace + ".*' was expected.");
		}

		Asset assetWithDuplicateId = identifiers.get(identifier);
		if(assetWithDuplicateId == null){
			identifiers.put(identifier, htmlAsset);
		}else{
			throw new NamespaceException("HTML template found with a duplicate identifier: '" +
						identifier + "'. The same identifier is used for the file:\n'" 
						+ assetWithDuplicateId.getAssetPath()
						+ "'.");
		}
	}
	
	
	private StartTag getStartTag(Asset htmlAsset) throws IOException
	{
		try(Reader reader = htmlAsset.getReader())
		{
			StreamedSource streamedSource = new StreamedSource(reader);
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
