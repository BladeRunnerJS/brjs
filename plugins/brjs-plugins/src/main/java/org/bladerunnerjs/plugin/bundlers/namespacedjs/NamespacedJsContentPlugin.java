package org.bladerunnerjs.plugin.bundlers.namespacedjs;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.CharResponseContent;
import org.bladerunnerjs.api.plugin.CompositeContentPlugin;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.TestAssetLocation;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.plugin.bundlers.commonjs.CommonJsContentPlugin;
import org.bladerunnerjs.plugin.bundlers.commonjs.CommonJsSourceModule;
import org.bladerunnerjs.plugin.utility.InstanceFinder;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

import com.Ostermiller.util.ConcatReader;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class NamespacedJsContentPlugin extends AbstractContentPlugin implements CompositeContentPlugin
{

	public static final String GLOBALIZE_EXTRA_CLASSES_REQUEST = "globalize-extra-classes-request";
	public static final String PACKAGE_DEFINITIONS_REQUEST = "package-definitions-request";
	public static final String SINGLE_MODULE_REQUEST = "single-module-request";
	public static final String BUNDLE_REQUEST = "bundle-request";

	private ContentPathParser contentPathParser;
	private List<String> prodRequestPaths = new ArrayList<>();
	private BRJS brjs;

	{
		try
		{
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			contentPathParserBuilder
				.accepts("namespaced-js/bundle.js").as(BUNDLE_REQUEST)
					.and("namespaced-js/module/<module>.js").as(SINGLE_MODULE_REQUEST)
					.and("namespaced-js/package-definitions.js").as(PACKAGE_DEFINITIONS_REQUEST)
					.and("namespaced-js/globalize-extra-classes.js").as(GLOBALIZE_EXTRA_CLASSES_REQUEST)
				.where("module").hasForm(ContentPathParserBuilder.PATH_TOKEN);

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
	public String getCompositeGroupName()
	{
		return "text/javascript";
	}
	
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return Arrays.asList(CommonJsContentPlugin.class.getCanonicalName());
	}
	
	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException
	{
		List<String> requestPaths = new ArrayList<>();
		
		if (requestMode == RequestMode.Prod) {
			return (InstanceFinder.containsInstance(bundleSet.getSourceModules(), NamespacedJsSourceModule.class)) ? prodRequestPaths : Collections.emptyList();
		}
		
		if(InstanceFinder.containsInstance(bundleSet.getSourceModules(), NamespacedJsSourceModule.class)) {
			try
			{
				requestPaths.add(contentPathParser.createRequest(PACKAGE_DEFINITIONS_REQUEST));
				for (SourceModule sourceModule : bundleSet.getSourceModules())
				{
					if (sourceModule instanceof NamespacedJsSourceModule)
					{
						requestPaths.add(contentPathParser.createRequest(SINGLE_MODULE_REQUEST, sourceModule.getPrimaryRequirePath()));
					}
				}
				requestPaths.add(contentPathParser.createRequest(GLOBALIZE_EXTRA_CLASSES_REQUEST));
			}
			catch (MalformedTokenException e)
			{
				throw new ContentProcessingException(e);
			}
		}

		return requestPaths;
	}

	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor output, String version) throws MalformedRequestException, ContentProcessingException
	{
		try
		{
			ParsedContentPath parsedContentPath = contentPathParser.parse(contentPath);
			
			if (parsedContentPath.formName.equals(SINGLE_MODULE_REQUEST))
			{
				SourceModule jsModule =  (SourceModule)bundleSet.getBundlableNode().getLinkedAsset(parsedContentPath.properties.get("module"));
				return new CharResponseContent(brjs, jsModule.getReader());
			}

			else if (parsedContentPath.formName.equals(BUNDLE_REQUEST))
			{
				List<Reader> readerList = new ArrayList<Reader>();
				
				StringBuffer contentBuffer = new StringBuffer();
				for (SourceModule sourceModule : bundleSet.getSourceModules())
				{
					if (sourceModule instanceof NamespacedJsSourceModule)
					{
						contentBuffer.append("// " + sourceModule.getPrimaryRequirePath() + "\n");
						Reader reader = sourceModule.getReader();
						readerList.add(reader);
						contentBuffer.append("\n\n");
					}
				}
				
				List<SourceModule> processedGlobalizedSourceModules = new ArrayList<SourceModule>();
				String globalizedClasses = getGlobalizedClassesContent(bundleSet, processedGlobalizedSourceModules);
				Map<String, Map<String, ?>> packageStructure = createPackageStructureForNamespacedJsClasses(bundleSet, processedGlobalizedSourceModules);
				Reader structureRreader = getPackageStructureReader(packageStructure);
				if(structureRreader != null){
					readerList.add(structureRreader);
				}
				readerList.add(new StringReader("\n"));
								
				String content = contentBuffer.toString();
				readerList.add(new StringReader(content));
				readerList.add(new StringReader("\n"));
				readerList.add(new StringReader(globalizedClasses));				
				
				return new CharResponseContent( brjs, readerList );
			}
			else if (parsedContentPath.formName.equals(PACKAGE_DEFINITIONS_REQUEST))
			{
				// call globalizeExtraClasses here so it pushes more classes onto processedGlobalizedSourceModules so we create the package structure for these classes
				List<SourceModule> processedGlobalizedSourceModules = new ArrayList<SourceModule>();
				getGlobalizedClassesContent(bundleSet, processedGlobalizedSourceModules);
				Map<String, Map<String, ?>> packageStructure = createPackageStructureForNamespacedJsClasses(bundleSet, processedGlobalizedSourceModules);
				return new CharResponseContent(brjs, getPackageStructureReader(packageStructure) );
			}
			else if (parsedContentPath.formName.equals(GLOBALIZE_EXTRA_CLASSES_REQUEST))
			{
				// call globalizeExtraClasses here so it pushes more classes onto processedGlobalizedSourceModules so we create the package structure for these classes
				List<SourceModule> processedGlobalizedSourceModules = new ArrayList<SourceModule>();
				return new CharResponseContent(brjs, getGlobalizedClassesContent(bundleSet, processedGlobalizedSourceModules));
			}
			else
			{
				throw new ContentProcessingException("unknown request form '" + parsedContentPath.formName + "'.");
			}
		}
		catch ( IOException | RequirePathException e)
		{
			throw new ContentProcessingException(e);
		}
	}

	private Map<String, Map<String, ?>> createPackageStructureForNamespacedJsClasses(BundleSet bundleSet, List<SourceModule> globalizedModules)
	{
		Map<String, Map<String, ?>> packageStructure = new LinkedHashMap<>();

		for (SourceModule sourceModule : bundleSet.getSourceModules())
		{
			if ((sourceModule instanceof NamespacedJsSourceModule) && !(sourceModule.assetLocation() instanceof TestAssetLocation))
			{
				List<String> packageList = Arrays.asList(sourceModule.getPrimaryRequirePath().split("/"));
				addPackageToStructure(packageStructure, packageList.subList(0, packageList.size() - 1));
			}
		}

		for (SourceModule sourceModule : globalizedModules)
		{
			String namespacedName = sourceModule.getPrimaryRequirePath().replace('/', '.');
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

	private Reader getPackageStructureReader(Map<String, Map<String, ?>> packageStructure) 
	{
		if (packageStructure.size() > 0)
		{
			Gson gson = new GsonBuilder().create();
			return new ConcatReader(new Reader[]{
				new StringReader("// package definition block\n"),	
				new StringReader("mergePackageBlock(window, "),	
				new StringReader(gson.toJson(packageStructure)),
				new StringReader(");\n")	
			});
		}
		return new StringReader("");
	}

	private String getGlobalizedNonNamespaceSourceModuleContent(SourceModule dependentSourceModule, List<SourceModule> globalizedModules)
	{
		if (dependentSourceModule.isEncapsulatedModule() && !globalizedModules.contains(dependentSourceModule))
		{
			globalizedModules.add(dependentSourceModule);
			String requirePath = dependentSourceModule.getPrimaryRequirePath();
			
			if(!requirePath.contains("!")) {
				String sourceModuleClassName = requirePath.replace('/', '.').replace('-', '_');
				
				return "\t" + sourceModuleClassName + " = require('" + requirePath + "');\n";
			}
		}
		return "";
	}

	private String getGlobalizedClassesContent(BundleSet bundleSet, List<SourceModule> processedGlobalizedSourceModules)
	{		
		StringBuffer output = new StringBuffer();
		Predicate<SourceModule> sourceModuleFilter = Predicates.or(new IsSourceModuleTypePredicate(NamespacedJsSourceModule.class), new IsSourceModuleTypePredicate(CommonJsSourceModule.class));
		
		for(SourceModule sourceModule : Collections2.filter(bundleSet.getSourceModules(), sourceModuleFilter)) {
			output.append(getGlobalizedNonNamespaceSourceModuleContent(sourceModule, processedGlobalizedSourceModules));
		}
		
		String globalizedSourceModules = output.toString();
		
		return (globalizedSourceModules.length() == 0) ? "" : "function globalizeSourceModules() {\n" + globalizedSourceModules + "}\nglobalizeSourceModules();\n";
	}
	
	
	
	private class IsSourceModuleTypePredicate implements Predicate<SourceModule> {
		private Class<? extends SourceModule> type;
		public IsSourceModuleTypePredicate(Class<? extends SourceModule> type) {
			this.type = type;
		}
		@Override
		public boolean apply(SourceModule input)
		{
			return ( type.isAssignableFrom(input.getClass()) && !(input.assetLocation() instanceof TestAssetLocation) );
		}
	}
}
