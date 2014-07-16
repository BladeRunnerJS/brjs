package org.bladerunnerjs.plugin.plugins.bundlers.css;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


/* Note: this tests the CSSRewriter indirectly by using the CSSContentPlugin */
public class CssRewriterTest extends SpecTest
{
	private JsLib userLib;
	private Aspect aspect;
	private StringBuffer response;
	private Bladeset bladeset;
	private Blade blade;
	private Workbench workbench;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
		
		App app = brjs.app("app1");
		aspect = app.aspect("default");
		bladeset = app.bladeset("bs");
		blade = bladeset.blade("b1");
		workbench = blade.workbench();
		response = new StringBuffer();
		userLib = app.jsLib("userLib");
		
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
	}
	
	@Test
	public void onlyTheUrlIsRewritten_SurroundingQuotesAreUntouched() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('image.png');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('../../cssresource/aspect_default/theme_common/image.png');");
	}
	
	@Test
	public void urlsAreCorrectlyRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('image.png');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('../../cssresource/aspect_default/theme_common/image.png');");
	}
	
	@Test
	public void urlCanBeInCapitals() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:URL('image.png');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:URL('../../cssresource/aspect_default/theme_common/image.png');");
		
	}
	
	@Test
	public void pathCanBeInDoubleQuotes() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:URL(\"image.png\");");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:URL(\"../../cssresource/aspect_default/theme_common/image.png\");");
	}
	
	@Test
	public void spacesCanBeBeforeAndAfterUrl() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:URL(    'image.png'    );");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:URL(    '../../cssresource/aspect_default/theme_common/image.png'    );");
	}
	
	@Test
	public void spacesCanBeAfterBackground() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:   URL( 'image.png' );");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:   URL( '../../cssresource/aspect_default/theme_common/image.png' );");
	}
	
	@Test
	public void theQuotesInURLAreOptional() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:URL(image.png);");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:URL(../../cssresource/aspect_default/theme_common/image.png);");
	}
	
	@Test
	public void relativeUrlsAreCorrectlyRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('./image.png');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('../../cssresource/aspect_default/theme_common/image.png');");
	}
	
	@Test
	public void relativeUrlsToParentDirectoriesAreCorrectlyRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/dir1/dir2/dir3/style.css", "background:url('../../../image.png');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('../../cssresource/aspect_default/theme_common/image.png');");
		
	}
	
	@Test
	public void urlsToChildDirectoriesAreCorrectlyRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('dir1/dir2/dir3/image.png');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('../../cssresource/aspect_default/theme_common/dir1/dir2/dir3/image.png');");
	}
	
	@Test
	public void relativeUrlsToChildDirectoriesAreCorrectlyRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('./dir1/dir2/dir3/image.png');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('../../cssresource/aspect_default/theme_common/dir1/dir2/dir3/image.png');");
	}
	
	@Test
	public void urlsWithArbitraryExtensionsAreRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('./dir1/dir2/dir3/image.some-ext');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('../../cssresource/aspect_default/theme_common/dir1/dir2/dir3/image.some-ext');");
	}
	
	@Test
	public void queryStringsAreKeptInTheRewrittenUrl() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('./image.png?arg=value');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('../../cssresource/aspect_default/theme_common/image.png?arg=value');");
	}
	
	@Test
	public void anchorStringsAreKeptInTheRewrittenUrl() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('./image.png#arg');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('../../cssresource/aspect_default/theme_common/image.png#arg');");
	}
	
	@Test
	public void anchorAndQueryStringsAreKeptInTheRewrittenUrl() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('./image.png?arg=value#arg');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('../../cssresource/aspect_default/theme_common/image.png?arg=value#arg');");
	}
	
	@Test
	public void specialCharactersInFilenamesAreEncoded() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('./some image.png');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('../../cssresource/aspect_default/theme_common/some%20image.png');");
	}
	
	@Test
	public void regexSpecialCharactersInFilenamesAreValid() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", 
				"background:url('./some$image.png');\n"+"background:url('./another(image.png');\n");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('../../cssresource/aspect_default/theme_common/some$image.png');")
			.and(response).containsText("background:url('../../cssresource/aspect_default/theme_common/another(image.png');");
	}
	 
	@Test
	public void poundSignCharactersInFilenamesAreCorrectlyEncoded() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('./some£image.png');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('../../cssresource/aspect_default/theme_common/some%C2%A3image.png');");
	}
	
	@Test
	public void absoluteUrlsAreNotRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('/some/absolute/url/image.png');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('/some/absolute/url/image.png');");
	}
	
	@Test
	public void urlsWithAProtocolAreNotRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('http://my-domain.com/some/absolute/url/image.png');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('http://my-domain.com/some/absolute/url/image.png');");
	}
	
	@Test
	public void protocolRelativeUrlsAreNotRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('//my-domain.com/some/absolute/url/image.png');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('//my-domain.com/some/absolute/url/image.png');");
	}
	
	@Test
	public void dataUrisAreNotRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('data:image/gif;base64,R0lGODlhyAAiALM...DfD0QAADs=');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('data:image/gif;base64,R0lGODlhyAAiALM...DfD0QAADs=');");
	}
	
	@Test
	public void dataUrisWithADifferentEncodingAreNotRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('data:image/png;base64,iadsadasidsads...DfD0QAADs=');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('data:image/png;base64,iadsadasidsads...DfD0QAADs=');");
	}
	
	@Test
	public void urlsWithAnUnknownProtocolAreNotRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background:url('myProtocol://my-domain.com/some/absolute/url/image.png');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('myProtocol://my-domain.com/some/absolute/url/image.png');");
	}
	
	@Test
	public void backgroundRgbValuesAreNotRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background: rgba( 0,0,0,0.5 );");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background: rgba( 0,0,0,0.5 );");
	}
	
	@Test
	public void backgroundImageWithMultipleImagesUrlsIsRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background-image: url(flower.png), url(ball.png), url(grass.png);");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background-image: " +
				"url(../../cssresource/aspect_default/theme_common/flower.png), " +
				"url(../../cssresource/aspect_default/theme_common/ball.png), " +
				"url(../../cssresource/aspect_default/theme_common/grass.png);");
	}
	
	@Test
	public void backgroundImageWithImageUrlAndAdditonalStylingIsRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background-image: url(flower.png) no-repeat center center;");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background-image: url(../../cssresource/aspect_default/theme_common/flower.png) no-repeat center center;");
	}
	
	@Test
	public void imagesInThirdpartyLibrariesAreRewritten() throws Exception
	{
		given(userLib).containsFileWithContents("style.css", "background: url(images/image.png);")
			.and(userLib).containsFileWithContents("thirdparty-lib.manifest", "js: Class1.js \ncss: style.css \nexports: userLib")
			.and(aspect).indexPageHasContent("require('userLib');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background: url(../../cssresource/lib_userLib/images/image.png);").
			and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void imagesInBRLibrariesAreRewritten() throws Exception
	{
		given(userLib).containsFileWithContents("br-lib.conf","requirePrefix: lib")
			.and(userLib).hasClass("lib/Class1")
    		.and(userLib).containsFileWithContents("resources/style.css", "background: url(images/image.png);")
    		.and(aspect).indexPageHasContent("require('lib/Class1');");
    	when(aspect).requestReceived("css/common/bundle.css", response);
    	then(response).containsText("background: url(../../cssresource/lib_userLib/resources/images/image.png);").
    		and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void imagesCanLiveInSrcCode() throws Exception
	{
		given(aspect).containsFileWithContents("resources/style.css", "background:url('../src/pk1/pkg2/image.png');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background:url('../../cssresource/aspect_default_resource/src/pk1/pkg2/image.png');");
	}
	
	@Test
	public void imagesInAspectResourcesAreRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background-image: url(../../resources/images/flower.png);");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background-image: url(../../cssresource/aspect_default_resource/resources/images/flower.png);");
	}
	
	@Test
	public void imagesInBladesetResourcesAreRewritten() throws Exception
	{
		given(bladeset).containsFileWithContents("themes/common/style.css", "background-image: url(../../resources/images/flower.png);")
			.and(bladeset).hasClass("bs/Class1")
			.and(aspect).indexPageHasContent("require('bs/Class1');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background-image: url(../../cssresource/bladeset_bs_resource/resources/images/flower.png);");
	}
	
	@Test
	public void imagesInBladeResourcesAreRewritten() throws Exception
	{
		given(blade).containsFileWithContents("themes/common/style.css", "background-image: url(../../resources/images/flower.png);")
			.and(blade).hasClass("bs/b1/Class1")
			.and(aspect).indexPageHasContent("require('bs/b1/Class1');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background-image: url(../../cssresource/bladeset_bs/blade_b1_resource/resources/images/flower.png);");
	}
	
	@Test
	public void imagesInBladeWorkbenchResourcesAreRewritten() throws Exception
	{
		given(workbench).containsFileWithContents("resources/style.css", "background-image: url(images/flower.png);")
			.and(blade).hasClass("bs/b1/Class1")
			.and(workbench).indexPageHasContent("require('bs/b1/Class1');");
		when(workbench).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background-image: url(../../cssresource/bladeset_bs/blade_b1/workbench_resource/resources/images/flower.png);");
	}
	
	@Test
	public void inlineSVGIsntRewritten() throws Exception
	{
		given(aspect).containsFileWithContents("themes/common/style.css", "background: url('data:image/svg+xml;base64,PD94bWwgd...IiB5Mj0iMTAwJSI);");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background: url('data:image/svg+xml;base64,PD94bWwgd...IiB5Mj0iMTAwJSI);");
	}
	
}
