package org.bladerunnerjs.core.plugin.minifier;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.SequenceInputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;
import org.bladerunnerjs.model.BRJS;


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
		logger = brjs.logger(LoggerType.MINIFIER, this.getClass());
	}
	
	@Override
	public List<String> getSettingNames() {
		return settingNames;
	}
	
	/* using ClosureCompiler API in Java taken from http://blog.bolinfest.com/2009/11/calling-closure-compiler-from-java.html 
	 * 	and https://code.google.com/p/closure-compiler/wiki/FAQ#How_do_I_call_Closure_Compiler_from_the_Java_API? */
	@Override
	public void minify(String settingName, List<InputSource> inputSources, Writer writer) throws IOException {
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
		
		Compiler compiler = new Compiler( new PrintStream(errorStream) );
		CompilerOptions options = new CompilerOptions();
		
		getCompilationLevelForSettingName(settingName).setOptionsForCompilationLevel(options);
		
		/* we have to use an extern, so create a dummy one */
		SourceFile extern = SourceFile.fromCode("externs.js", "function alert(x) {}");
		SourceFile input = SourceFile.fromInputStream( "input.js", getSingleInputStreamForInputSources(inputSources) );
		
		Result result = compiler.compile(extern, input, options);
		if (result.success)
		{
			logger.debug(Messages.OUTPUT_FROM_MINIFIER, errorStream.toString());
			writer.write(compiler.toSource());
		}
		else
		{
			logger.error(Messages.ERROR_WHILE_BUNDLING_MSG, errorStream.toString());
			writer.write( String.format(Messages.ERROR_WHILE_BUNDLING_MSG, errorStream.toString()) );
		}		
	}
	
	@Override
	public void generateSourceMap(String minifierLevel, List<InputSource> inputSources, Writer writer) throws IOException {
		// TODO: implement this method
	}
	
	
	private InputStream getSingleInputStreamForInputSources(List<InputSource> inputSources)
	{
		Vector<InputStream> inputStreams = new Vector<InputStream>();
		for (InputSource inputSource : inputSources)
		{
			inputStreams.add( IOUtils.toInputStream(inputSource.getSource()) );
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
