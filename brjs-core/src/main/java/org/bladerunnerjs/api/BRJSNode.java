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
	 * @return BRJS a BRJS object that represents the root of the model.
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
	 * @exception InvalidNameException if the node may not be created due to an invalid name
	 */
	void populate(String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException;
	
	String getTemplateName();
	void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException;
}
