package org.bladerunnerjs.api.plugin.base;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.TagHandlerPlugin;
import org.bladerunnerjs.model.RequestMode;


/**
 * A specialization of {@link AbstractPlugin} for developers that need to implement {@link TagHandlerPlugin}.
 */
public abstract class AbstractTagHandlerPlugin extends AbstractPlugin implements TagHandlerPlugin {
	
	public List<String> getDependentContentPluginRequestPrefixes() {
		return Collections.emptyList();
	}
	
	@Override
	public List<String> getGeneratedContentPaths(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale) throws MalformedTokenException, ContentProcessingException {
		return Collections.emptyList();
	}
	
}
