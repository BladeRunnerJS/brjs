package org.bladerunnerjs.plugin.bundlers.namespacedjs;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.model.AugmentedContentSourceModule;
import org.bladerunnerjs.utility.reader.AssetReaderFactory;
import org.bladerunnerjs.utility.reader.CharBufferPool;
import org.bladerunnerjs.utility.reader.JsCodeBlockStrippingDependenciesReader;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;

import com.google.common.base.Predicate;

public class NamespacedJsUseTimeDependenciesReader extends Reader {
	
	private Reader namespacedJsUseTimeDependenciesReader;

	public NamespacedJsUseTimeDependenciesReader(Asset asset) throws IOException
	{
		CharBufferPool pool = asset.assetLocation().root().getCharBufferPool();
		Predicate<Integer> insideCodeBlockPredicate = new JsCodeBlockStrippingDependenciesReader.MoreThanPredicate(0);
		Reader sourceReader = null;
		
		if(asset instanceof AugmentedContentSourceModule) {
			AugmentedContentSourceModule source = (AugmentedContentSourceModule) asset;
			sourceReader = source.getUnalteredContentReader();
		}
		else {
			sourceReader = asset.getReader();
		}
		
		Reader commentStrippingReader = new JsCommentStrippingReader(sourceReader, false, pool);
		namespacedJsUseTimeDependenciesReader = new JsCodeBlockStrippingDependenciesReader(commentStrippingReader , pool, insideCodeBlockPredicate);
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		return namespacedJsUseTimeDependenciesReader.read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException
	{
		namespacedJsUseTimeDependenciesReader.close();
	}
	
	static class Factory implements AssetReaderFactory {
		
		private Asset asset;

		public Factory(Asset asset)
		{
			this.asset = asset;
		}
		
		@Override
		public Reader createReader() throws IOException {
			return new NamespacedJsUseTimeDependenciesReader(asset);
		}
	}
}
