package org.bladerunnerjs.model.utility;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.model.exception.name.InvalidRootPackageNameException;
import org.bladerunnerjs.model.exception.name.UnableToAutomaticallyGenerateAppNamespaceException;

public class NameValidator
{
	private static final String[] RESERVED_NAMESPACES = new String[] { "caplin", "caplinx" };
	private static final String[] RESERVED_JS_KEYWORDS = new String[] { "abstract", "as", "boolean", "break", "byte", "case", "catch", 
		"char", "class", "continue", "const", "debugger", "default", "delete", "do", "double", "else", "enum", "export", "extends", 
		"false", "final", "finally", "float", "for", "function", "goto", "if", "implements", "import", "in", "instanceof", "int", 
		"interface", "is", "long", "namespace", "native", "new", "null", "package", "private", "protected", "public", "return", "short", 
		"static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "typeof", "use", "var", "void", 
		"volatile", "while", "with" };
	private static final String VALID_DIRECTORY_NAME_REGEX = "^[a-zA-Z0-9_-]*$";
	private static final String VALID_PACKAGE_NAME_REGEX = "^([a-z][a-z0-9]*)?$";
	
	// TODO: change to accept a single directory name
	public static boolean isValidDirectoryName(String directoryName)
	{
		if (directoryName != null && !directoryName.matches(VALID_DIRECTORY_NAME_REGEX))
		{
			return false;
		}
		return true;
	}
	
	public static void assertValidDirectoryName(NamedNode node) throws InvalidDirectoryNameException {
		if(!isValidDirectoryName(node.getName())) throw new InvalidDirectoryNameException(node);
	}
	
	public static boolean isValidPackageName(String packageName)
	{
		if (!packageName.matches(VALID_PACKAGE_NAME_REGEX) || packageName.equals("")) {
			return false;
		}
		
		for (String reservedKeyword : RESERVED_JS_KEYWORDS) {
			if (packageName.equalsIgnoreCase(reservedKeyword)) {
				return false;
			}
		}
		
		return true;
	}
	
	public static void assertValidPackageName(Node node, String packageName) throws InvalidPackageNameException {
		if(!NameValidator.isValidPackageName(packageName)) throw new InvalidPackageNameException(node, packageName);
	}
	
	public static boolean isValidRootPackageName(String rootPackageName)
	{
		for (String reservedNamespace : RESERVED_NAMESPACES)
		{
			if (rootPackageName.equalsIgnoreCase(reservedNamespace))
			{
				return false;
			}
		}
		
		return isValidPackageName(rootPackageName);
	}
	
	public static void assertValidRootPackageName(Node node, String rootPackageName) throws InvalidRootPackageNameException {
		if(!NameValidator.isValidRootPackageName(rootPackageName)) throw new InvalidRootPackageNameException(node, rootPackageName);
	}
	
	public static boolean legacyIsValidDirectoryName(String... directoryNames)
	{
		for (String directoryName : directoryNames)
		{
			if (!directoryName.matches(VALID_DIRECTORY_NAME_REGEX))
			{
				return false;
			}				
		}
		return true;
	}
	
	public static boolean legacyIsValidPackageName(String... argsToValidate)
	{
		for (String arg : argsToValidate)
		{
			if (!arg.matches(VALID_PACKAGE_NAME_REGEX) || arg.equals(""))
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean legacyIsReservedNamespace(String namespace)
	{
		for (String reservedNamespace : RESERVED_NAMESPACES)
		{
			if (namespace.equalsIgnoreCase(reservedNamespace))
			{
				return true;
			}
		}
		for (String reservedNamespace : RESERVED_JS_KEYWORDS)
		{
			if (namespace.equalsIgnoreCase(reservedNamespace))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static String getReservedNamespaces()
	{
		StringBuilder message = new StringBuilder();
		
		String[] reservedList = RESERVED_NAMESPACES;
		if (reservedList.length > 0)
		{
			message.append( "Reserved namespace(s): " );
		}
		
		for (int i = 0; i < reservedList.length; i++)
		{
			message.append( "'" + reservedList[i] + "'" );
			
			if (i != (reservedList.length-1))
			{
				message.append( ", " );
			}
		}
		
		message.append("\n\n");
		
		String[] jsKeywords = RESERVED_JS_KEYWORDS;
		if (jsKeywords.length > 0)
		{
			message.append( "Banned Namespaces/JavaScript keywords: " );
		}
		
		for (int i = 0; i < jsKeywords.length; i++)
		{
			message.append( "'" + jsKeywords[i] + "'" );
			
			if (i != (jsKeywords.length-1))
			{
				message.append( ", " );
			}
		}
		
		return message.toString();
	}
	
	
	public static String generateAppNamespaceFromApp(App app) throws UnableToAutomaticallyGenerateAppNamespaceException
	{
		String appName = app.getName();
		String appNamespace = appName;
		appNamespace = appNamespace.replace("-", "");
		appNamespace = appNamespace.replace("+", "");
		appNamespace = appNamespace.replace("_", "");
		appNamespace = appNamespace.toLowerCase();
		if (isValidRootPackageName(appNamespace))
		{
			return appNamespace;
		}
		throw new UnableToAutomaticallyGenerateAppNamespaceException(app);
	}
	
}
