package org.bladerunnerjs.plugin.bundlers.commonjs;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.utility.reader.CharBufferPool;
import org.bladerunnerjs.utility.reader.JsCodeBlockStrippingDependenciesReader;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;

import com.google.common.base.Predicate;


public class CommonJsUseTimeDependenciesReader extends Reader
{

	private Reader useTimeDependencesReader;

	public CommonJsUseTimeDependenciesReader(CommonJsSourceModule sourceModule) throws IOException {
		CharBufferPool pool = sourceModule.assetLocation().root().getCharBufferPool();
		Predicate<Integer> insideCodeBlockPredicate = new JsCodeBlockStrippingDependenciesReader.MoreThanPredicate(0);
		
		Reader sourceReader = sourceModule.getUnalteredContentReader();
		Reader commentStrippingReader = new JsCommentStrippingReader(sourceReader, false, pool);
		useTimeDependencesReader = new JsCodeBlockStrippingDependenciesReader(commentStrippingReader , pool, insideCodeBlockPredicate);
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		return useTimeDependencesReader.read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException
	{
		useTimeDependencesReader.close();
	}
}