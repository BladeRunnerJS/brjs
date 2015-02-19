package org.bladerunnerjs.model;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.utility.StringLengthComparator;
import org.bladerunnerjs.utility.TemplateUtility;


public class BRJSNodeHelper {
	public static void populate(BRJSNode node) throws InvalidNameException, ModelUpdateException, TemplateInstallationException {
		populate(node, "default", false);
	}
	
	public static void populate(BRJSNode node, String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException {
		populate(node, templateGroup, false);
	}
	
	public static void populate(BRJSNode node, String templateGroup, boolean allowNonEmptyDirectories) throws InvalidNameException, ModelUpdateException, TemplateInstallationException {
		populate(node, templateGroup, new HashMap<String,String>(), allowNonEmptyDirectories);
	}
	
	public static void populate(BRJSNode node, String templateGroup, Map<String, String> overrideTransformations, boolean allowNonEmptyDirectories) throws InvalidNameException, ModelUpdateException, TemplateInstallationException {
		if(!allowNonEmptyDirectories) {
			node.create();
		}
		try {
			Map<String, String> transformations = getNodeTransformations(node);
			transformations.putAll( overrideTransformations );
			
			TemplateUtility.installTemplate(node, templateGroup, node.getTemplateName(), transformations, allowNonEmptyDirectories);
		}
		catch(TemplateInstallationException e) {
			throw new TemplateInstallationException(e.getMessage());
		}
		node.incrementChildFileVersions();
		node.ready();
	}
	
	public static String getTemplateName(BRJSNode node) {
		return node.getTypeName().toLowerCase();
	}
	
	private static Map<String, String> getNodeTransformations(BRJSNode node) throws ModelUpdateException {
		Map<String, String> transformations = new TreeMap<>(new StringLengthComparator());
		
		do {
			node.addTemplateTransformations(transformations);
			node = (BRJSNode) node.parentNode();
		} while(node != null);
		
		return transformations;
	}
}
