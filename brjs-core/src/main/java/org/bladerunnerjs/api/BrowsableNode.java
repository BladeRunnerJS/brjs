package org.bladerunnerjs.api;

import java.io.Writer;

import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.model.RequestMode;

/**
 * A BrowsableNode is a location that contains an index page, which would be a {@link Aspect}, {@link BladeWorkbench} or {@link BladesetWorkbench}.
 */
public interface BrowsableNode extends BundlableNode {
	
	/**
	 * The method replaces the tag handlers from your index pages with the appropriate content as indicated, such as the required
	 * CSS bundle, for example.
	 * 
	 * @param indexPage a String object representing the content of the index page
	 * @param locale a Locale object representing the locale used
	 * @param version a String object representing the version used
	 * @param writer a Writer object used as a stream for the newly updated content
	 * @param requestMode a RequestMode object showing whether the request is made in DEV (development) or PROD (production) mode
	 */
	public void filterIndexPage(String indexPage, Locale locale, String version, Writer writer, RequestMode requestMode) throws ModelOperationException;
}
