package org.bladerunnerjs.utility;

public class NamespaceUtility {
	public static String convertToNamespace(String requirePath) {
		return requirePath.replace('/', '.');
	}
}
