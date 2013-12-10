package org.bladerunnerjs.plugin.proxy;

import org.bladerunnerjs.plugin.BundlerContentPlugin;

public class VirtualProxyBundlerContentPlugin extends VirtualProxyContentPlugin implements BundlerContentPlugin {
	private BundlerContentPlugin bundlerContentPlugin;
	
	public VirtualProxyBundlerContentPlugin(BundlerContentPlugin bundlerContentPlugin) {
		super(bundlerContentPlugin);
		this.bundlerContentPlugin = bundlerContentPlugin;
	}
	
	@Override
	public String getMimeType() {
		return bundlerContentPlugin.getMimeType();
	}
}
