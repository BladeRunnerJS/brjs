package org.bladerunnerjs.plugin.base;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.TagHandlerPlugin;


/**
 * A specialization of {@link AbstractPlugin} for developers that need to implement {@link TagHandlerPlugin}.
 */
public abstract class AbstractTagHandlerPlugin extends AbstractPlugin implements TagHandlerPlugin {
	
	public List<String> getDependentContentPluginRequestPrefixes() {
		return Collections.emptyList();
	}
	
	public List<String> getGeneratedRequests(RequestMode requestMode, Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, String version) throws MalformedTokenException, ContentProcessingException {
		return Collections.emptyList();
	}
	
}
