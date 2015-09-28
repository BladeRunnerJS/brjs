package org.bladerunnerjs.plugin.bundlers.html;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.plugin.bundlers.i18n.I18nPropertiesUtils;

public class HTMLBundlerTagHandlerPlugin extends AbstractTagHandlerPlugin {
	
	private final static Pattern I18N_TOKEN_PATTERN = Pattern.compile("@\\{(.*?)\\}");

	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public String getTagName() {
		return "html.bundle";
	}

	@Override
	public void writeTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale, Writer writer, String version) throws IOException {
		try {
			writer.write("<script>document.createElement(\"template\");</script>\n");
			writer.write("<style>template{display:none;}</style>\n");
			writer.write("<template id=\"brjs-html-templates-inline\"></template>\n");
			
			StringBuffer untranslatedContent = new StringBuffer();
			
			for(Reader reader : HTMLTemplateUtility.getReaders(bundleSet, version)) {
				untranslatedContent.append( IOUtils.toString(reader) );
			}
			
			IOUtils.write( translateContent(untranslatedContent.toString(), bundleSet, locale), writer);
		}
		catch(ContentProcessingException e) {
			throw new IOException(e);
		}
	}

	private String translateContent(String untranslatedContent, BundleSet bundleSet, Locale locale) throws ContentProcessingException
	{
		Map<String,String> propertiesMap = I18nPropertiesUtils.getI18nProperties(bundleSet, locale);
		Matcher i18nTokenMatcher = I18N_TOKEN_PATTERN.matcher(untranslatedContent);
		StringBuffer translatedContent = new StringBuffer();
		
		while (i18nTokenMatcher.find()) {
			String i18nKey = i18nTokenMatcher.group(1).toLowerCase();
			String keyReplacement = propertiesMap.get(i18nKey);
			if (keyReplacement == null) {
				throw new ContentProcessingException("Unable to find a replacement for the i18n key '"+i18nKey+"'.");
			}
			i18nTokenMatcher.appendReplacement(translatedContent, keyReplacement);
		}
		i18nTokenMatcher.appendTail(translatedContent);
		
		
		return translatedContent.toString();
	}
}
