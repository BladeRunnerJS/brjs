package com.caplin.cutlass.structure.model.path;

import java.io.File;
import java.util.List;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.structure.model.SdkModel;
import com.caplin.cutlass.structure.model.node.AppNode;

public class AppPath extends AbstractPath
{
	public static AppPath locateAncestorPath(File path)
	{
		AppNode appNode = SdkModel.getAppNode(path);
		return (appNode == null) ? new AppPath((File) null) : appNode.getPath();
	}
	
	public AppPath(File path)
	{
		super(path);
	}
	
	public BladesetPath bladesetPath(String bladesetName)
	{
		return new BladesetPath(new File(path, bladesetName + "-bladeset"));
	}
	
	public AspectPath aspectPath(String aspectName)
	{
		return new AspectPath(new File(path, aspectName + "-aspect"));
	}
	
	public ThirdpartyLibsPath thirdpartyLibsPath()
	{
		return new ThirdpartyLibsPath(new File(path, "thirdparty-libraries"));
	}
	
	public UserLibsPath userLibsPath()
	{
		return new UserLibsPath(new File(path, "libs"));
	}
	
	public TestIntegrationSrcPath testIntegrationSrcPath()
	{
		return new TestIntegrationSrcPath(new File(path, "test-integration-src"));
	}
	
	// TODO: experimental method
	public void calculateChildNodes()
	{
		getAspects();
		getBladesets();
	}
	
	// TODO: experimental method
	public List<AspectPath> getAspects()
	{
		return PathAccessor.getPaths(path, CutlassConfig.ASPECT_SUFFIX, AspectPath.class);
	}
	
	// TODO: experimental method
	public List<BladesetPath> getBladesets()
	{
		return PathAccessor.getPaths(path, CutlassConfig.BLADESET_SUFFIX, BladesetPath.class);
	}
}
