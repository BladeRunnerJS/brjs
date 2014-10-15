package org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.model.AugmentedContentSourceModule;
import org.bladerunnerjs.utility.reader.AssetReaderFactory;
import org.bladerunnerjs.utility.reader.CharBufferPool;
import org.bladerunnerjs.utility.reader.JsCodeBlockStrippingDependenciesReader;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;
import org.bladerunnerjs.utility.reader.JsStringStrippingReader;

public class NamespacedJsDefineTimeDependenciesReader extends Reader {
	
	private Reader namespacedJsDefineTimeDependenciesReader;
	
	public NamespacedJsDefineTimeDependenciesReader(AugmentedContentSourceModule sourceModule) throws IOException
	{
		CharBufferPool pool = sourceModule.assetLocation().root().getCharBufferPool();
		Reader commentStrippingReader = new JsCommentStrippingReader(sourceModule.getUnalteredContentReader(), false, pool);
		Reader commentStrippingAndStringStrippingReader = new JsStringStrippingReader(commentStrippingReader, pool);
		namespacedJsDefineTimeDependenciesReader = new JsCodeBlockStrippingDependenciesReader(commentStrippingAndStringStrippingReader, pool);
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		return namespacedJsDefineTimeDependenciesReader.read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException
	{
		namespacedJsDefineTimeDependenciesReader.close();
	}
	
	static class Factory implements AssetReaderFactory {
		
		private AugmentedContentSourceModule sourceModule;

		public Factory(AugmentedContentSourceModule sourceModule)
		{
			this.sourceModule = sourceModule;
		}
		
		@Override
		public Reader createReader() throws IOException {
			return new NamespacedJsDefineTimeDependenciesReader(sourceModule);
		}
	}
	
}
