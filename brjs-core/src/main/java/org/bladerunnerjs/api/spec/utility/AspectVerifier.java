package org.bladerunnerjs.api.spec.utility;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BladerunnerConf;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.aliasing.AliasDefinition;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.spec.engine.VerifierChainer;
import org.bladerunnerjs.model.StaticContentAccessor;

import com.google.common.base.Joiner;


public class AspectVerifier extends BundlableNodeVerifier<Aspect> {
	private Aspect aspect;
	private AssetContainerVerifier assetContainerVerifier;
	
	public AspectVerifier(SpecTest modelTest, Aspect aspect) {
		super(modelTest, aspect);
		this.aspect = aspect;
		assetContainerVerifier = new AssetContainerVerifier(aspect);
	}
	
	public VerifierChainer hasAlias(String aliasName, String classRef, String interfaceRef) throws Exception {
		AliasDefinition alias = aspect.aliasesFile().getAlias(aliasName);
		
		assertEquals("Class not as expected for alias '" + aliasName + "'", classRef, alias.getClassName());
		assertEquals("Interface not as expected for alias '" + aliasName + "'", interfaceRef, alias.getInterfaceName());
		
		return verifierChainer;
	}
	
	public VerifierChainer hasAlias(String aliasName, String classRef) throws Exception {
		hasAlias(aliasName, classRef, null);
		
		return verifierChainer;
	}
	
	public VerifierChainer hasSourceModules(String... sourceModules) throws Exception {
		assetContainerVerifier.hasSourceModules(sourceModules);
		
		return verifierChainer;
	}
	
	public VerifierChainer hasAssetLocations(String... assetLocations) throws Exception {
		assetContainerVerifier.hasAssetLocations(assetLocations);
		
		return verifierChainer;
	}
	
	public VerifierChainer sourceModuleHasAssetLocation(String sourceModulePath, String assetLocationPath) throws Exception {
		SourceModule sourceModule = (SourceModule)aspect.getLinkedAsset(sourceModulePath);
		AssetLocation assetLocation = aspect.assetLocation(assetLocationPath);
		
		assertEquals("Source module '" + sourceModulePath + "' did not have the asset location '" + assetLocationPath + "'.", assetLocation.dir().getPath(), sourceModule.assetLocation().dir().getPath());
		
		return verifierChainer;
	}
	
	public VerifierChainer assetLocationHasNoDependencies(String assetLocation) {
		assetContainerVerifier.assetLocationHasNoDependencies(assetLocation);
		
		return verifierChainer;
	}
	
	public VerifierChainer assetLocationHasDependencies(String assetLocation, String... assetLocationDependencies) {
		assetContainerVerifier.assetLocationHasDependencies(assetLocation, assetLocationDependencies);
		
		return verifierChainer;
	}
	
	public VerifierChainer classHasPreExportDependencies(String requirePath, String... expectedRequirePaths) throws Exception {
		SourceModule sourceModule = (SourceModule) aspect.linkedAsset(requirePath);
		List<String> actualRequirePaths = requirePaths(sourceModule.getPreExportDefineTimeDependentAssets(aspect));
		
		assertEquals(Joiner.on(", ").join(expectedRequirePaths), Joiner.on(", ").join(actualRequirePaths));
		
		return verifierChainer;
	}
	
	public VerifierChainer classHasPostExportDependencies(String requirePath, String... expectedRequirePaths) throws Exception {
		SourceModule sourceModule = (SourceModule) aspect.linkedAsset(requirePath);
		List<String> actualRequirePaths = requirePaths(sourceModule.getPostExportDefineTimeDependentAssets(aspect));
		
		assertEquals(Joiner.on(", ").join(expectedRequirePaths), Joiner.on(", ").join(actualRequirePaths));
		
		return verifierChainer;
	}
	
	public VerifierChainer classHasUseTimeDependencies(String requirePath, String... expectedRequirePaths) throws Exception {
		SourceModule sourceModule = (SourceModule) aspect.linkedAsset(requirePath);
		List<String> actualRequirePaths = requirePaths(sourceModule.getUseTimeDependentAssets(aspect));
		
		assertEquals(Joiner.on(", ").join(expectedRequirePaths), Joiner.on(", ").join(actualRequirePaths));
		
		return verifierChainer;
	}

	private List<String> requirePaths(List<Asset> assets) {
		List<String> requirePaths = new ArrayList<>();
		
		for(Asset asset : assets) {
			SourceModule sourceModule = (SourceModule) asset;
			requirePaths.add(sourceModule.getRequirePaths().get(0));
		}
		
		return requirePaths;
	}

	public VerifierChainer devResponseContains(String requestPath, String expectedContent, StringBuffer response) throws IOException, MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		ResponseContent responseContent = aspect.handleLogicalRequest(requestPath, new StaticContentAccessor(aspect.app()), aspect.root().getAppVersionGenerator().getDevVersion());        		
		ByteArrayOutputStream pluginContent = new ByteArrayOutputStream();
		responseContent.write(pluginContent);
		if (!pluginContent.toString().contains(expectedContent)) {
			assertEquals(expectedContent, pluginContent.toString());
		}
		response.append(pluginContent.toString(BladerunnerConf.OUTPUT_ENCODING));
		
		return verifierChainer;
	}
	
	public VerifierChainer devResponseEventuallyContains(String requestPath, String content, StringBuffer response) {
		Throwable failure = null;
		for (int i = 0; i < 50 ; i++) {
			try {
				devResponseContains(requestPath, content, response);
				return verifierChainer;
			}
			catch (Throwable e) {
				failure = e;
				try {
					Thread.sleep(2000);
				} catch (Exception ex) {
					// ignore
				}
			}
		}
		if (failure == null) {
			fail("Didn't get expected response");
		}
		throw new RuntimeException(failure);
	}
}
