package org.bladerunnerjs.utility;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.api.Asset;

public class RequirePathUtility {
	
	private static final Pattern matcherPattern = Pattern.compile("(require|br\\.Core\\.alias|caplin\\.alias|getAlias|getService)\\([ ]*[\"']([^)]+)[\"'][ ]*\\)");

	public static String getPrimaryRequirePath(Asset asset) {
		List<String> requirePaths = asset.getRequirePaths();
		
		return (requirePaths.size() > 0) ? requirePaths.get(0) : null;
	}
	
	public static void addRequirePathsFromReader(Reader reader, Set<String> dependencies, List<String> aliases) throws IOException {
		StringWriter stringWriter = new StringWriter();
		IOUtils.copy(reader, stringWriter);
		
		Matcher m = matcherPattern.matcher(stringWriter.toString());
		while (m.find()) {
			String methodArgument = m.group(2);
			
			if (m.group(1).startsWith("require")) {
				String requirePath = methodArgument;
				dependencies.add(requirePath);
			}
			else if (m.group(1).startsWith("getService")){
				String serviceAliasName = methodArgument;
				dependencies.add("service!" + serviceAliasName);
			}
			else {
				String aliasName = methodArgument;
				aliases.add(aliasName);
			}
		}
	}
}
