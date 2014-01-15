package org.bladerunnerjs.plugin;

import java.io.IOException;
import java.io.Writer;
import java.util.List;


/**
 * Minifier plug-ins allow alternate minifier implementations to be made available for JavaScript & CSS minification.
 * 
 * <p>Minifier plug-ins may or may not choose to support source-maps, with the following levels of support being possible:</p>
 * 
 * <dl>
 *   <dt>No source-map support</dt>
 *   <dd>Minifiers do not themselves support source-maps, and do not interfere with instances of {@link ContentPlugin} that are capable of generating source-maps.</dd>
 *   
 *   <dt>Single-level source-map support</dt>
 *   <dd>Minifiers support source-map generation for the minified content they generate, but neither preserve nor interfere with source-maps that can be generated from instances of
 *   {@link ContentPlugin} that are capable of generating source-maps.</dd>
 *   
 *   <dt>Multi-level source-map support</dt>
 *   <dd>Minifiers support source-map generation for the minified content while preserving any source-maps generated from instances of
 *   {@link ContentPlugin} that are capable of generating source-maps.</dd>
 * </dl>
 * 
 * <p><b>Note:</b> This interface is <i>beta</i>, and likely to change.</p>
 */
public interface MinifierPlugin extends Plugin {
	List<String> getSettingNames();
	void minify(String settingName, List<InputSource> inputSources, Writer writer) throws IOException;
	void generateSourceMap(String minifierLevel, List<InputSource> inputSources, Writer writer) throws IOException;
}
