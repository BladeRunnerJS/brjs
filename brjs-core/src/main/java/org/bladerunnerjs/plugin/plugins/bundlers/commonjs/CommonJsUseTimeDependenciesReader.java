package org.bladerunnerjs.plugin.plugins.bundlers.commonjs;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;

import org.bladerunnerjs.utility.reader.CharBufferPool;
import org.bladerunnerjs.utility.reader.JsCodeBlockStrippingDependenciesReader;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;
import org.bladerunnerjs.utility.reader.JsModuleExportsStrippingReader;

import com.google.common.base.Predicate;


public class CommonJsUseTimeDependenciesReader extends Reader
{

	private Reader useTimeDependencesReader;

	public CommonJsUseTimeDependenciesReader(CommonJsSourceModule sourceModule) throws IOException {
		super();
		CharBufferPool pool = sourceModule.assetLocation().root().getCharBufferPool();
		Predicate<Integer> insideCodeBlockPredicate = new JsCodeBlockStrippingDependenciesReader.MoreThanPredicate(0);
		
		Reader sourceReader = sourceModule.getUnalteredContentReader();
		Reader commentStrippingReader = new JsCommentStrippingReader(sourceReader, false, pool);
		useTimeDependencesReader = new JsCodeBlockStrippingDependenciesReader(commentStrippingReader , pool, insideCodeBlockPredicate, new FoundModuleExportsPredicate());
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
	
	private class FoundModuleExportsPredicate implements Predicate<String> {
		private boolean foundModuleExports = false;
		@Override
		public boolean apply(String input)
		{
			if (foundModuleExports) {
				return true;
			}
			Matcher moduleExportsMatcher = JsModuleExportsStrippingReader.MODULE_EXPORTS_REGEX_PATTERN.matcher(input);
			foundModuleExports = moduleExportsMatcher.matches();
			return foundModuleExports;
		}
		
	}
	
}