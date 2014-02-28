package org.bladerunnerjs.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.utility.StringLengthComparator;
import org.bladerunnerjs.utility.TemplateUtility;


public class BRJSNodeHelper {
	public static void populate(BRJSNode node) throws InvalidNameException, ModelUpdateException {
		Map<String, String> transformations = new TreeMap<>(new StringLengthComparator());
		populate(node, transformations);
	}
	
	public static void populate(BRJSNode node, Map<String, String> overrideTransformations) 
			throws InvalidNameException, ModelUpdateException {
		node.create();
		
		try {
			Map<String, String> transformations = getNodeTransformations(node);
			for(Entry<String, String> override : overrideTransformations.entrySet()) {
				transformations.put(override.getKey(), override.getValue());
			}
			
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
