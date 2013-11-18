package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.RootNode;

public abstract class AbstractAssetContainer extends AbstractBRJSNode implements AssetContainer {
	private final NodeItem<DirNode> src = new NodeItem<>(DirNode.class, "src");
	private final NodeItem<DirNode> resources = new NodeItem<>(DirNode.class, "resources");
	protected final AssetContainerResources assetContainerResources;
	
	final Map<File, AssetLocation> assetLocations = new HashMap<>();
	
	public AbstractAssetContainer(RootNode rootNode, File dir) {
		init(rootNode, rootNode, dir);
		
		assetContainerResources = new AssetContainerResources(this, src().dir(), resources().dir());
	}
	
	public DirNode src() {
		return item(src);
	}
	
	public DirNode resources()
	{
		return item(resources);
	}
	
	@Override
	public App getApp() {
		Node node = this.parentNode();
		
		while(!(node instanceof App)) {
			node = node.parentNode();
		}
		
		return (App) node;
	}
	
	@Override
	public List<SourceFile> sourceFiles() {
		List<SourceFile> sourceFiles = new ArrayList<SourceFile>();
			
		for(BundlerPlugin bundlerPlugin : ((BRJS) rootNode).bundlerPlugins()) {
			for (AssetLocation assetLocation : getAllAssetLocations())
			{
				sourceFiles.addAll(bundlerPlugin.getAssetFileAccessor().getSourceFiles(assetLocation));
			}
		}
		
		return sourceFiles;
	}
	
	@Override
	public SourceFile sourceFile(String requirePath) {
		for(SourceFile sourceFile : sourceFiles()) {
			if(sourceFile.getRequirePath().equals(requirePath)) {
				return sourceFile;
			}
		}
		
		return null;
	}
	
	@Override /* deep AssetLocation for resources, for each dir in src call getAssetLocation(dir) */
	public List<AssetLocation> getAllAssetLocations() {
		List<AssetLocation> allAssetLocations = new ArrayList<AssetLocation>();
		allAssetLocations.add( getAssetLocation(resources().dir(), false) );
		
		File srcDir = src().dir();
		if (srcDir.isDirectory())
		{
    		Iterator<File> fileIterator = FileUtils.iterateFilesAndDirs(srcDir, FalseFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    		while (fileIterator.hasNext())
    		{
    			File dir = fileIterator.next();
    			if (!dir.equals(dir()))
    			{
    				allAssetLocations.add( getAssetLocation(dir) );
    			}
    		}
		}
		return allAssetLocations;
	}
	
	@Override
	public AssetLocation getAssetLocation(File dir) {
		return getAssetLocation(dir, true);
	}
	
	
	
	
	public AssetLocation getAssetLocation(File dir, boolean createShallowAssetLocation) 
	{
		AssetLocation assetLocation = assetLocations.get(dir);
		if (assetLocation == null) {
			assetLocation = (createShallowAssetLocation) ? new ShallowAssetLocation(this, dir) : new DeepAssetLocation(this, dir);
			assetLocations.put(dir, assetLocation);
		}
		return assetLocation;
		
	}
}
