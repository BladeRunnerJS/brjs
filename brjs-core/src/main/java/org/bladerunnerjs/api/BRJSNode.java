package org.bladerunnerjs.api;

import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.model.engine.Node;

/**
 * A BRJSNode is a generic term for a folder under BRJS. Among others, BRJSNodes encompass AssetContainers, Apps, TemplateGroups or Themes.
 */

public interface BRJSNode extends Node {
	
	/**
	 * The method returns a {@link BRJS} object that represents the root of the model. 
	 * 
	 * @return a BRJS object that represents the root of the model.
	 */
	BRJS root();
	
	/**
	 * Any BRJSNode may be populated according to a pre-existing template. The method will create the files and folders existing in the 
	 * specified template, including their content, within the BRJSNode. This method is to help with consistency among your entities
	 * and may be used, for example, to adhere to certain programming or modular styles. 
	 * 
	 * @param templateGroup a String that  represents the name by which the templates therein will be referenced when creating new nodes. 
	 * The templateGroup may contain templates for various BRJS entities, and the one corresponding to your current BRJSNode type 
	 * will be retrieved automatically.
	 * 
	 * @throws InvalidNameException if the node may not be created due to an invalid name associated to it
	 * @throws TemplateInstallationException if the template has not been found
	 * @throws ModelUpdateException for other non-BRJS-specific issues
	 */
	void populate(String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException;
	
	/**
	 * The method returns the name of the template, that is, the entity type and not the template group (which represents a collection
	 * of templates).
	 * 
	 * @return a String representing the name of the template
	 */
	String getTemplateName();
	
	/**
	 * Template transformation are used to replace aliases for namings that are automatically replaced when creating an node. They may be 
	 * added to any template and must be prefix by `@` to indicate that the token will be replaced. The method applies these replacements
	 * to the BRJSNode.
	 * 
	 * @param transformations a Map of Strings to Strings where the key is the token and the value is the String it will be replaced with
	 * 
	 * * @see <a href="http://bladerunnerjs.org/docs/use/custom_templates/">Custom Templates</a>
	 * @throws ModelUpdateException for any exceptions thrown while calculating node template transformations
	 * @throws ModelUpdateException for non-BRJS-specific exceptions
	 */
	void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException;
}
