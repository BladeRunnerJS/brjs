package org.bladerunnerjs.model;

import java.io.File;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.utility.NameValidator;

//TODO: this needs a better name, JsLib was taken for the interface name
public class BrjsJsLib extends AbstractAssetContainer implements JsLib
{
	private final NodeItem<SourceAssetLocation> src = new NodeItem<>(SourceAssetLocation.class, "src");
	private final NodeItem<DeepAssetLocation> resources = new NodeItem<>(DeepAssetLocation.class, "resources");
	private String name;
	private JsLibConf libConf;
	private Node parent;
	
	public BrjsJsLib(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, dir);
		init(rootNode, parent, dir);
		this.name = name;
		this.parent = parent;
	}
	
	public BrjsJsLib(RootNode rootNode, Node parent, File dir)
	{
		// TODO: can we avoid having to have a null name for a NamedNode that is available as a single item through the model
		this(rootNode, parent, dir, null);
	}
	
	public static NodeItem<BrjsJsLib> createSdkNodeItem()
	{
		return new NodeItem<>(BrjsJsLib.class, "sdk/libs/javascript/caplin");
	}
	
	public static NodeMap<BrjsJsLib> createAppNodeSet()
	{
		NodeMap<BrjsJsLib> appNodeSet = new NodeMap<>(BrjsJsLib.class, "libs", null);
		
		return appNodeSet;
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
		try {
			transformations.put("libns", libConf().getLibNamespace());
		}
		catch(ConfigException e) {
			throw new ModelUpdateException(e);
		}
	}
	
	@Override
	public String getName()
	{
		if (name != null)
		{
			return name;			
		}
		return dir().getName();
	}
	
	@Override
	public boolean isValidName()
	{
		return NameValidator.isValidDirectoryName(name);
	}
	
	@Override
	public void assertValidName() throws InvalidNameException
	{
		NameValidator.assertValidDirectoryName(this);
	}
	
	@Override
	public Node parentNode()
	{
		return parent;
	}
	
	@Override
	public App getApp()
	{
		if (parent == root())
		{
			return root().systemApp("SDK");			
		}
		return super.getApp();
	}
	
	public SourceAssetLocation src()
	{
		return item(src);
	}
	
	public AssetLocation resources()
	{
		return item(resources);
	}
	
	public JsLibConf libConf() throws ConfigException
	{
		if(libConf == null) {
			libConf = new JsLibConf(this);
		}
		
		return libConf ;
	}
	
	@Override
	public void populate(String libNamespace) throws InvalidNameException, ModelUpdateException
	{
		NameValidator.assertValidRootPackageName(this, libNamespace);
		
		try {
			libConf().setLibNamespace(libNamespace);
			populate();
			libConf().write();
		}
		catch (ConfigException e) {
			if(e.getCause() instanceof InvalidNameException) {
				throw (InvalidNameException) e.getCause();
			}
			else {
				throw new ModelUpdateException(e);
			}
		}
	}
	
	@Override
	public String namespace() {
		return getName();
	}
	

	@Override
	public String toString()
	{
		return super.toString()+" - "+getName();
	}
	
	
	@Override
	public String getTemplateName()
	{
		return JsLib.class.getSimpleName();
	}
}
