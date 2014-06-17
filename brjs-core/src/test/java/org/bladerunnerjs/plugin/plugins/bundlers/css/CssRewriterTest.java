package org.bladerunnerjs.plugin.plugins.bundlers.css;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/* Note: this tests the CSSRewriter indirectly by using the CSSContentPlugin */
public class CssRewriterTest extends SpecTest
{
	private JsLib userLib;
	private Aspect aspect;
	private StringBuffer response;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
		
		App app = brjs.app("app1");
		aspect = app.aspect("default");
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
	
	@Ignore
	@Test
	public void backgroundImageWithImageUrlAndAdditonalStyling() throws Exception
	{
		given(userLib).hasCommonJsPackageStyle().
			and(userLib).containsFileWithContents("style.css", "background: url(close.png) no-repeat center center;").
			and(userLib).containsFile("close.png").
			and(userLib).containsFileWithContents("thirdparty-lib.manifest", "js: Class1.js \ncss: style.css \nexports: userLib").
			and(aspect).indexPageHasContent("require('userLib/Class1');");
		when(aspect).requestReceived("css/common/bundle.css", response);
		then(response).containsText("background: url(../../cssresource/aspect_default/theme_common/close.png) no-repeat center center").
			and(exceptions).verifyNoOutstandingExceptions();
	}
}
