package org.bladerunnerjs.plugin.plugins.bundlers.appmeta;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.utility.AppMetadataUtility;


public class BaseTagHandlePlugin extends AbstractTagHandlerPlugin
{

	@Override
	public String getTagName()
	{
		return "base.tag";
	}

	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException
	{
		writeTagContent( writer, AppMetadataUtility.getPathRelativeToIndexPage("") );
	}

	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException
	{
		writeTagContent( writer, AppMetadataUtility.getPathRelativeToIndexPage("") );
	}
	
	public void writeTagContent(Writer writer, String href) throws IOException
	{
		writer.write("<base href=\""+href+"\"/>");
		// from https://stackoverflow.com/a/13373180/2634854
		writer.write("<!--[if IE]><script type=\"text/javascript\">\n"+
					"// Fix IE ignoring relative base tags.\n"+
					"(function() {\n"+
					"	var baseTag = document.getElementsByTagName('base')[0];\n"+
					"	baseTag.href = baseTag.href;\n"+
					"})();\n"+
					"</script><![endif]-->");
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}

}
