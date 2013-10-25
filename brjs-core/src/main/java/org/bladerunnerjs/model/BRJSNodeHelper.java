package org.bladerunnerjs.model;

import java.util.Map;
import java.util.TreeMap;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.model.utility.StringLengthComparator;
import org.bladerunnerjs.model.utility.TemplateUtility;


public class BRJSNodeHelper {
	public static void populate(BRJSNode node) throws InvalidNameException, ModelUpdateException {
		node.create();
		
		try {
			Map<String, String> transformations = getNodeTransformations(node);
			
			TemplateUtility.installTemplate(node, node.getTemplateName(), transformations);
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
