package org.bladerunnerjs.plugin.proxy;

import org.bladerunnerjs.plugin.BundlerTagHandlerPlugin;

public class VirtualProxyBundlerTagHandlerPlugin extends VirtualProxyTagHandlerPlugin implements BundlerTagHandlerPlugin {
	private BundlerTagHandlerPlugin bundlerTagHandlerPlugin;
	
	public VirtualProxyBundlerTagHandlerPlugin(BundlerTagHandlerPlugin bundlerTagHandlerPlugin) {
		super(bundlerTagHandlerPlugin);
		this.bundlerTagHandlerPlugin = bundlerTagHandlerPlugin;
	}
	
	@Override
	public String getMimeType() {
		return bundlerTagHandlerPlugin.getMimeType();
	}
}
