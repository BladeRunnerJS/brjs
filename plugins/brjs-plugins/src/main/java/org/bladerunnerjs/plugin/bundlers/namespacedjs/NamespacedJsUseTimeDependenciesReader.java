package org.bladerunnerjs.plugin.bundlers.namespacedjs;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.utility.reader.AssetReaderFactory;
import org.bladerunnerjs.utility.reader.JsCodeBlockStrippingDependenciesReader;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;

import com.google.common.base.Predicate;

public class NamespacedJsUseTimeDependenciesReader extends Reader {
	
	private Reader namespacedJsUseTimeDependenciesReader;

	public NamespacedJsUseTimeDependenciesReader(NamespacedJsSourceModule asset) throws IOException
	{
		Predicate<Integer> insideCodeBlockPredicate = new JsCodeBlockStrippingDependenciesReader.MoreThanPredicate(0);
		Reader sourceReader = asset.getUnalteredContentReader();
		
		Reader commentStrippingReader = new JsCommentStrippingReader(asset.assetContainer().root(), sourceReader, false);
		namespacedJsUseTimeDependenciesReader = new JsCodeBlockStrippingDependenciesReader(asset.assetContainer().root(), commentStrippingReader, insideCodeBlockPredicate);
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
		
		private NamespacedJsSourceModule asset;

		public Factory(NamespacedJsSourceModule asset)
		{
			this.asset = asset;
		}
		
		@Override
		public Reader createReader() throws IOException {
			return new NamespacedJsUseTimeDependenciesReader(asset);
		}
	}
}
