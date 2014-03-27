package org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.nodejs.NodeJsContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;
import org.json.simple.JSONObject;


public class NamespacedJsContentPlugin extends AbstractContentPlugin
{

	public static final String GLOBALIZE_EXTRA_CLASSES_REQUEST = "globalize-extra-classes-request";
	public static final String PACKAGE_DEFINITIONS_REQUEST = "package-definitions-request";
	public static final String SINGLE_MODULE_REQUEST = "single-module-request";
	public static final String BUNDLE_REQUEST = "bundle-request";

	public static final String JS_STYLE = "namespaced-js";

	private ContentPathParser contentPathParser;
	private List<String> prodRequestPaths = new ArrayList<>();
	private BRJS brjs;

	{
		try
		{
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			contentPathParserBuilder.accepts("namespaced-js/bundle.js").as(BUNDLE_REQUEST).and("namespaced-js/module/<module>.js").as(SINGLE_MODULE_REQUEST).and("namespaced-js/package-definitions.js").as(PACKAGE_DEFINITIONS_REQUEST).and("namespaced-js/globalize-extra-classes.js").as(GLOBALIZE_EXTRA_CLASSES_REQUEST).where("module").hasForm(ContentPathParserBuilder.PATH_TOKEN);

			contentPathParser = contentPathParserBuilder.build();
			prodRequestPaths.add(contentPathParser.createRequest(BUNDLE_REQUEST));
		}
		catch (MalformedTokenException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
	}

	@Override
	public String getRequestPrefix()
	{
		return "namespaced-js";
	}

	@Override
	public String getGroupName()
	{
		return "text/javascript";
	}
	
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return Arrays.asList(NodeJsContentPlugin.class.getCanonicalName());
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return new ArrayList<>();
	}
	
	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		List<String> requestPaths = new ArrayList<>();

		try
		{
			requestPaths.add(contentPathParser.createRequest(PACKAGE_DEFINITIONS_REQUEST));
			for (SourceModule sourceModule : bundleSet.getSourceModules())
			{
				if (sourceModule instanceof NamespacedJsSourceModule)
				{
					requestPaths.add(contentPathParser.createRequest(SINGLE_MODULE_REQUEST, sourceModule.getRequirePath()));
				}
			}
			requestPaths.add(contentPathParser.createRequest(GLOBALIZE_EXTRA_CLASSES_REQUEST));
		}
		catch (MalformedTokenException e)
		{
			throw new ContentProcessingException(e);
		}

		return requestPaths;
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		return prodRequestPaths;
	}

	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws ContentProcessingException
	{
		try
		{
			if (contentPath.formName.equals(SINGLE_MODULE_REQUEST))
			{
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getBrowserCharacterEncoding()))
				{
					SourceModule jsModule = bundleSet.getBundlableNode().getSourceModule(contentPath.properties.get("module"));
					writer.write(getGlobalizedNonNamespacedDependenciesContent(jsModule, new ArrayList<SourceModule>()));
					IOUtils.copy(jsModule.getReader(), writer);
				}
			}
			else if (contentPath.formName.equals(BUNDLE_REQUEST))
			{
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getBrowserCharacterEncoding()))
				{
					StringWriter contentBuffer = new StringWriter();
					List<SourceModule> processedGlobalizedSourceModules = new ArrayList<SourceModule>();

					// do this first and buffer the content so we know which modules have been globally namespaced
					for (SourceModule sourceModule : bundleSet.getSourceModules())
					{
						if (sourceModule instanceof NamespacedJsSourceModule)
						{
							contentBuffer.write(getGlobalizedNonNamespacedDependenciesContent(sourceModule, processedGlobalizedSourceModules));
							contentBuffer.write("// " + sourceModule.getRequirePath() + "\n");
							IOUtils.copy(sourceModule.getReader(), contentBuffer);
							contentBuffer.write("\n\n");
						}
					}

					// call globalizeExtraClasses here so it pushes more classes onto processedGlobalizedSourceModules so we create the package structure for these classes
					String globalizedClasses = getExtraGlobalizedClassesContent(bundleSet, processedGlobalizedSourceModules);

					Map<String, Map<String, ?>> packageStructure = createPackageStructureForCaplinJsClasses(bundleSet, processedGlobalizedSourceModules, writer);
					writePackageStructure(packageStructure, writer);
					writer.write("\n");

					writer.write(contentBuffer.toString());

					writer.write("\n");
					writer.write(globalizedClasses);
				}
			}
			else if (contentPath.formName.equals(PACKAGE_DEFINITIONS_REQUEST))
			{
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getBrowserCharacterEncoding()))
				{
					List<SourceModule> processedGlobalizedSourceModules = calculateGlobalizedClasses(bundleSet);

					// call globalizeExtraClasses so it pushes more classes onto processedGlobalizedSourceModules so we create the package structure for these classes
					getExtraGlobalizedClassesContent(bundleSet, processedGlobalizedSourceModules);
					Map<String, Map<String, ?>> packageStructure = createPackageStructureForCaplinJsClasses(bundleSet, processedGlobalizedSourceModules, writer);
					writePackageStructure(packageStructure, writer);
				}
			}
			else if (contentPath.formName.equals(GLOBALIZE_EXTRA_CLASSES_REQUEST))
			{
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getBrowserCharacterEncoding()))
				{
					List<SourceModule> processedGlobalizedSourceModules = calculateGlobalizedClasses(bundleSet);
					writer.write(getExtraGlobalizedClassesContent(bundleSet, processedGlobalizedSourceModules));
				}
			}
			else
			{
				throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
			}
		}
		catch (ModelOperationException | ConfigException | IOException | RequirePathException e)
		{
			throw new ContentProcessingException(e);
		}
	}

	private List<SourceModule> calculateGlobalizedClasses(BundleSet bundleSet) throws ModelOperationException, RequirePathException
	{
		List<SourceModule> processedGlobalizedSourceModules = new ArrayList<SourceModule>();
		for (SourceModule sourceModule : bundleSet.getSourceModules())
		{
			if (sourceModule instanceof NamespacedJsSourceModule)
			{
				getGlobalizedNonNamespacedDependenciesContent(sourceModule, processedGlobalizedSourceModules);
			}
		}
		return processedGlobalizedSourceModules;
	}

	private Map<String, Map<String, ?>> createPackageStructureForCaplinJsClasses(BundleSet bundleSet, List<SourceModule> globalizedModules, Writer writer)
	{
		Map<String, Map<String, ?>> packageStructure = new LinkedHashMap<>();

		for (SourceModule sourceModule : bundleSet.getSourceModules())
		{
			if (sourceModule instanceof NamespacedJsSourceModule)
			{
				List<String> packageList = Arrays.asList(sourceModule.getClassname().split("\\."));
				addPackageToStructure(packageStructure, packageList.subList(0, packageList.size() - 1));
			}
		}

		for (SourceModule sourceModule : globalizedModules)
		{
			String namespacedName = sourceModule.getRequirePath().replace('/', '.');
			namespacedName = (namespacedName.startsWith(".")) ? StringUtils.substringAfter(namespacedName, ".") : namespacedName;
			List<String> packageList = Arrays.asList(namespacedName.split("\\."));
			addPackageToStructure(packageStructure, packageList.subList(0, packageList.size() - 1));
		}

		return packageStructure;
	}

	@SuppressWarnings("unchecked")
	private void addPackageToStructure(Map<String, Map<String, ?>> packageStructure, List<String> packageList)
	{
		Map<String, Map<String, ?>> currentPackage = packageStructure;

		for (String packageName : packageList)
		{
			Map<String, Map<String, ?>> nextPackage;

			if (currentPackage.containsKey(packageName))
			{
				nextPackage = (Map<String, Map<String, ?>>) currentPackage.get(packageName);
			}
			else
			{
				nextPackage = new LinkedHashMap<String, Map<String, ?>>();
				currentPackage.put(packageName, nextPackage);
			}

			currentPackage = nextPackage;
		}
	}

	private void writePackageStructure(Map<String, Map<String, ?>> packageStructure, Writer writer) throws IOException
	{
		if (packageStructure.size() > 0)
		{
			writer.write(
				"// package definition block\n" +
				"function mergePackageBlock(context, packageBlock) {\n" +
				"	for(packageName in packageBlock) {\n" +
				"		if(!context[packageName]) {\n" +
				"			context[packageName] = packageBlock[packageName];\n" +
				"		}\n" +
				"		else {\n" +
				"			mergePackageBlock(context[packageName], packageBlock[packageName]);\n" +
				"		}\n" +
				"	}\n" +
				"}\n");
			writer.write("mergePackageBlock(window, ");
			JSONObject.writeJSONString(packageStructure, writer);
			writer.write(");\n");
			writer.flush();
		}
	}

	private String getGlobalizedNonNamespacedDependenciesContent(SourceModule sourceModule, List<SourceModule> globalizedModules) throws ModelOperationException, RequirePathException
	{
		StringBuffer stringBuffer = new StringBuffer();

		for (SourceModule dependentSourceModule : sourceModule.getDependentSourceModules(null))
		{
			stringBuffer.append( getGlobalizedNonNamespaceSourceModuleContent(dependentSourceModule, globalizedModules) );
		}

		return stringBuffer.toString();
	}

	private String getGlobalizedNonNamespaceSourceModuleContent(SourceModule dependentSourceModule, List<SourceModule> globalizedModules)
	{
		if (dependentSourceModule.isEncapsulatedModule() && !globalizedModules.contains(dependentSourceModule))
		{
			globalizedModules.add(dependentSourceModule);
			return dependentSourceModule.getClassname() + " = require('" + dependentSourceModule.getRequirePath() + "');\n";
		}
		return "";
	}

	private String getExtraGlobalizedClassesContent(BundleSet bundleSet, List<SourceModule> processedGlobalizedSourceModules)
	{
		StringBuffer output = new StringBuffer();
		for (SourceModule sourceModule : bundleSet.getSourceModules())
		{
			output.append(getGlobalizedNonNamespaceSourceModuleContent(sourceModule, processedGlobalizedSourceModules));
		}
		return output.toString();
	}

}
