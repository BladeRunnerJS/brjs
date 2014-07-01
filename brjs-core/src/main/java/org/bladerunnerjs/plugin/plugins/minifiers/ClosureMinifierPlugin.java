package org.bladerunnerjs.plugin.plugins.minifiers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.input.ReaderInputStream;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.InputSource;
import org.bladerunnerjs.plugin.MinifierPlugin;
import org.bladerunnerjs.plugin.base.AbstractMinifierPlugin;

import com.Ostermiller.util.ConcatReader;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;


public class ClosureMinifierPlugin extends AbstractMinifierPlugin implements MinifierPlugin {
	
	public class Messages {
		public static final String OUTPUT_FROM_MINIFIER = "Minifying complete, output from minifier was:\n%s";
		public static final String ERROR_WHILE_BUNDLING_MSG = "There was an error while minifying, the error from the minifier was:\n%s";
	}
	
	public static final String CLOSURE_WHITESPACE = "closure-whitespace";
	public static final String CLOSURE_SIMPLE = "closure-simple";
	public static final String CLOSURE_ADVANCED = "closure-advanced";
	
	private Logger logger;
	
	private List<String> settingNames = new ArrayList<>();
	
	{
		settingNames.add(CLOSURE_WHITESPACE);
		settingNames.add(CLOSURE_SIMPLE);
		settingNames.add(CLOSURE_ADVANCED);
	}
	
	@Override
	public void setBRJS(BRJS brjs) 
	{
		logger = brjs.logger(this.getClass());
	}
	
	@Override
	public List<String> getSettingNames() {
		return settingNames;
	}
	
	/* using ClosureCompiler API in Java taken from http://blog.bolinfest.com/2009/11/calling-closure-compiler-from-java.html 
	 * 	and https://code.google.com/p/closure-compiler/wiki/FAQ#How_do_I_call_Closure_Compiler_from_the_Java_API? */
	@Override
	public Reader minify(String settingName, List<InputSource> inputSources) throws ContentProcessingException {
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
		
		Compiler compiler = new Compiler( new PrintStream(errorStream) );
		CompilerOptions options = new CompilerOptions();
		
		getCompilationLevelForSettingName(settingName).setOptionsForCompilationLevel(options);
		
		/* we have to use an extern, so create a dummy one */
		SourceFile extern = SourceFile.fromCode("externs.js", "function alert(x) {}");
		SourceFile input;
		try
		{
			input = SourceFile.fromInputStream( "input.js", getSingleInputStreamForInputSources(inputSources) );
		}
		catch (IOException ex)
		{
			throw new ContentProcessingException(ex);
		}
		
		List<Reader> readers = new LinkedList<>();
		
		Result result = compiler.compile(extern, input, options);
		if (result.success)
		{
			logger.debug(Messages.OUTPUT_FROM_MINIFIER, errorStream.toString());
			readers.add( new StringReader(compiler.toSource()) );
		}
		else
		{
			logger.error(Messages.ERROR_WHILE_BUNDLING_MSG, errorStream.toString());
			readers.add( new StringReader(String.format(Messages.ERROR_WHILE_BUNDLING_MSG, errorStream.toString())) );
		}
		
		return new ConcatReader( readers.toArray(new Reader[0]) );
	}
	
	@Override
	public Reader generateSourceMap(String minifierLevel, List<InputSource> inputSources) throws ContentProcessingException {
		// TODO: implement this method
		return new StringReader("");
	}
	
	
	private InputStream getSingleInputStreamForInputSources(List<InputSource> inputSources) throws ContentProcessingException
	{
		Vector<InputStream> inputStreams = new Vector<InputStream>();
		for (InputSource inputSource : inputSources)
		{
			Reader reader = inputSource.getContentPluginReader();
			inputStreams.add( new ReaderInputStream(reader) );
		}
		return new SequenceInputStream( inputStreams.elements() );
	}
	
	private CompilationLevel getCompilationLevelForSettingName(String settingName)
	{
		if (settingName.equals(CLOSURE_WHITESPACE))
		{
			return CompilationLevel.WHITESPACE_ONLY;
		}
		if (settingName.equals(CLOSURE_SIMPLE))
		{
			return CompilationLevel.SIMPLE_OPTIMIZATIONS;
		}
		if (settingName.equals(CLOSURE_ADVANCED))
		{
			return CompilationLevel.ADVANCED_OPTIMIZATIONS;
		}
		throw new RuntimeException("Closure compile does not support the seting " + settingName);
	}
	
}
