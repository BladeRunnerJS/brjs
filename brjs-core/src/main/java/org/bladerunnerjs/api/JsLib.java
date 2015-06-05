package org.bladerunnerjs.api;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.engine.NamedNode;

/**
 * JavaScript libraries to be used within your BRJS applications.
 */

public interface JsLib extends AssetContainer, NamedNode, TestableNode {
	
	/**
	 * The method will create the files and folders existing in the specified template, including their content, within the JsLib. 
	 * This method is to help with consistency among your entities and may be used, for example, to adhere to certain programming
	 *  or modular styles. 
	 * 
	 * @param libNamespace the chosen namespace for the library
	 * @param templateGroup a String that  represents the name by which the templates therein will be referenced when creating new nodes. 
	 * The templateGroup may contain templates for various BRJS entities, and the one corresponding to your current entity type 
	 * will be retrieved automatically.
	 * 
	 * @throws InvalidNameException if the node may not be created due to an invalid name associated to it
	 * @throws TemplateInstallationException if the template has not been found
	 * @throws ModelUpdateException for other non-BRJS-specific issues
	 */
	void populate(String libNamespace, String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException;
}
