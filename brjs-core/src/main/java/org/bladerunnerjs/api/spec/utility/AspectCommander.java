package org.bladerunnerjs.api.spec.utility;

import java.io.IOException;
import java.io.StringWriter;

import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.spec.engine.BundlableNodeCommander;
import org.bladerunnerjs.api.spec.engine.Command;
import org.bladerunnerjs.api.spec.engine.CommanderChainer;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.utility.EncodedFileUtil;
import org.bladerunnerjs.utility.NoTagHandlerFoundException;
import org.bladerunnerjs.utility.TagPluginUtility;

public class AspectCommander extends BundlableNodeCommander<Aspect> {
	private final Aspect aspect;
	private AspectBuilder aspectBuilder;
	
	public AspectCommander(SpecTest modelTest, Aspect aspect)
	{
		super(modelTest, aspect);
		this.aspect = aspect;
		this.aspectBuilder = new AspectBuilder(modelTest, aspect);
	}
	
	public BundleInfoCommander getBundleInfo() throws Exception {
		return new BundleInfoCommander((aspect.getBundleSet()));
	}
	
	public CommanderChainer indexPageLoadedInDev(final StringBuffer pageResponse, final String locale) throws ConfigException, IOException, ModelOperationException, NoTagHandlerFoundException {
		call(new Command() {
			public void call() throws Exception {
				pageLoaded(pageResponse, new Locale(locale), RequestMode.Dev);
			}
		});
		
		return commanderChainer;
	}

	public CommanderChainer indexPageLoadedInProd(final StringBuffer pageResponse, final String locale) throws ConfigException, IOException, ModelOperationException, NoTagHandlerFoundException {
		call(new Command() {
			public void call() throws Exception {
				pageLoaded(pageResponse, new Locale(locale), RequestMode.Prod);
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer retrievesAlias(final String aliasName) throws Exception {
		call(new Command() {
			public void call() throws Exception {
				aspect.aliasesFile().getAlias(aliasName);
			}
		});
		
		return commanderChainer;
	}
	
	private void pageLoaded(StringBuffer pageResponse, Locale locale, RequestMode opMode) throws ConfigException, IOException, ModelOperationException, NoTagHandlerFoundException, RequirePathException {
		StringWriter writer = new StringWriter();	
		
		String version = (opMode == RequestMode.Dev) ? aspect.root().getAppVersionGenerator().getDevVersion() : aspect.root().getAppVersionGenerator().getProdVersion();
		
		TagPluginUtility.filterContent(fileUtil.readFileToString(aspect.file("index.html")), aspect.getBundleSet(), writer, opMode, locale, version);
		
		pageResponse.append(writer.toString());
	}
	
	// --- Proxy to AspectBuilder to re-use code ---
	public CommanderChainer indexPageHasContent(String content) throws Exception
	{
		aspectBuilder.indexPageHasContent(content);
		
		return commanderChainer;
	}
	
	public CommanderChainer indexPageRefersTo(String classNames) throws Exception
	{
		aspectBuilder.indexPageRefersTo(classNames);
		
		return commanderChainer;
	}
	
	public CommanderChainer indexPageRequires(JsLib thirdpartyLib) throws Exception
	{
		aspectBuilder.indexPageRequires(thirdpartyLib);
		
		return commanderChainer;
	}
	
	public CommanderChainer indexPageRequires(String requirePath) throws Exception
	{
		aspectBuilder.indexPageRequires(requirePath);
		
		return commanderChainer;
	}
	
	public CommanderChainer classRefersTo(String sourceClass, String... referencedClasses) throws Exception
	{
		aspectBuilder.classDependsOn(sourceClass, referencedClasses);
		
		return commanderChainer;
	}
	
	public CommanderChainer classDependsOn(String dependentClass, String referencedClass) throws Exception
	{
		aspectBuilder.classExtends(dependentClass, referencedClass);
		
		return commanderChainer;
	}
	
	public CommanderChainer classDependsOnThirdpartyLib(String sourceClass, JsLib thirdpartyLib) throws Exception
	{
		aspectBuilder.classDependsOnThirdpartyLib(sourceClass, thirdpartyLib);
		
		return commanderChainer;
	}
	
	public CommanderChainer classRequires(String sourceClass, String dependencyClass) throws Exception
	{
		aspectBuilder.classRequires(sourceClass, dependencyClass);
		
		return commanderChainer;
	}
	
	public CommanderChainer classRequires(String sourceClass, JsLib thirdpartyLib) throws Exception
	{
		aspectBuilder.classRequiresThirdpartyLib(sourceClass, thirdpartyLib);
		
		return commanderChainer;
	}

	public CommanderChainer resourceFileRefersTo(String resourceFileName, String... classNames) throws Exception 
	{
		aspectBuilder.resourceFileRefersTo(resourceFileName, classNames);
		
		return commanderChainer;
	}

	public CommanderChainer resourceFileContains(String resourceFileName, String contents) throws Exception 
	{
		aspectBuilder.containsResourceFileWithContents(resourceFileName, contents);
		
		return commanderChainer;
	}

	public CommanderChainer indexPageRefersToWithoutNotifyingFileRegistry(String content) throws IOException
	{
		new EncodedFileUtil(aspect.root(), specTest.getActiveCharacterEncoding()).write( aspect.file("index.html"), content, false);

		return commanderChainer;
	}
	
}
