package org.bladerunnerjs.plugin.plugins.bundlers.cssresource;

import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

public class CssResourceContentPlugin extends AbstractContentPlugin {
	private final ContentPathParser contentPathParser;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("images/aspect_<aspect>/theme_<theme>/<imagePath>_image.bundle").as("aspect-request")
				.and("images/bladeset_<bladeset>/theme_<theme>/<imagePath>_image.bundle").as("bladeset-request")
				.and("images/bladeset_<bladeset>/blade_<blade>/theme_<theme>/<imagePath>_image.bundle").as("blade-request")
				.and("images/bladeset_<bladeset>/blade_<blade>/workbench/<imagePath>_image.bundle").as("blade-workbench-request")
				.and("images/lib_<lib>/<imagePath>_image.bundle").as("lib-request")
			.where("aspect").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("bladeset").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("blade").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("lib").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("imagePath").hasForm(ContentPathParserBuilder.PATH_TOKEN)
				.and("theme").hasForm(ContentPathParserBuilder.NAME_TOKEN);
		
		contentPathParser = contentPathParserBuilder.build();
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public String getRequestPrefix() {
		return "cssresource";
	}
	
	@Override
	public String getGroupName() {
		return null;
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}
	
	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, List<String> locales) throws BundlerProcessingException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, List<String> locales) throws BundlerProcessingException {
		// TODO Auto-generated method stub
		return null;
	}
}
