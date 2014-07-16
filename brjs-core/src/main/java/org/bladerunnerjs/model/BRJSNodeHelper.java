package org.bladerunnerjs.model;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.utility.StringLengthComparator;
import org.bladerunnerjs.utility.TemplateUtility;


public class BRJSNodeHelper {
	public static void populate(BRJSNode node) throws InvalidNameException, ModelUpdateException {
		populate(node, false);
	}
	
	public static void populate(BRJSNode node, boolean allowNonEmptyDirectories) throws InvalidNameException, ModelUpdateException {
		populate(node, new HashMap<String,String>(), allowNonEmptyDirectories);
	}
	
	public static void populate(BRJSNode node, Map<String, String> overrideTransformations, boolean allowNonEmptyDirectories) throws InvalidNameException, ModelUpdateException {
		if(!allowNonEmptyDirectories) {
			node.create();
		}
		
		try {
			Map<String, String> transformations = getNodeTransformations(node);
			transformations.putAll( overrideTransformations );
			
			TemplateUtility.installTemplate(node, node.getTemplateName(), transformations, allowNonEmptyDirectories);
		}
		catch(TemplateInstallationException e) {
			throw new ModelUpdateException(e);
		}

		node.ready();
	}
	
	public static String getTemplateName(BRJSNode node) {
		return node.getClass().getSimpleName().toLowerCase();
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
