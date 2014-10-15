package org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AugmentedContentSourceModule;
import org.bladerunnerjs.utility.reader.AssetReaderFactory;
import org.bladerunnerjs.utility.reader.CharBufferPool;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;

public class NamespacaedJsUseTimeDependenciesReader extends Reader {
	
	private Reader namespacaedJsUseTimeDependenciesReader;

	public NamespacaedJsUseTimeDependenciesReader(Asset asset) throws IOException
	{
		CharBufferPool pool = asset.assetLocation().root().getCharBufferPool();
		Reader reader = null;
		if(asset instanceof AugmentedContentSourceModule){
			AugmentedContentSourceModule source = (AugmentedContentSourceModule)asset;
			reader = source.getUnalteredContentReader();
		}else{
			reader = asset.getReader();
		}
		namespacaedJsUseTimeDependenciesReader = new JsCommentStrippingReader(reader, false, pool);
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		return namespacaedJsUseTimeDependenciesReader.read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException
	{
		namespacaedJsUseTimeDependenciesReader.close();
	}
	
	static class Factory implements AssetReaderFactory {
		
		private Asset asset;

		public Factory(Asset asset)
		{
			this.asset = asset;
		}
		
		@Override
		public Reader createReader() throws IOException {
			return new NamespacaedJsUseTimeDependenciesReader(asset);
		}
	}	
	
}
