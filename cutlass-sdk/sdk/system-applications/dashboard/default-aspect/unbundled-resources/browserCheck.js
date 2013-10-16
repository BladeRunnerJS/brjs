$(function(){
	
	fIsSupportedBrowser = function()
	{
		var mMinimalBrowser = {
				msie : 9,
				chrome : 5,
				firefox : 4,
				safari : 5
		};
		
		for( var sBrowser in mMinimalBrowser )
		{
			if( $.browser[ sBrowser ] && $.browser.versionNumber <  mMinimalBrowser[ sBrowser ]  )
			{
				return false;	
			}
		}
		
		return true;
	}
	
	var sWarningHtml = '';
	
	sWarningHtml +=	'<a href="http://browsehappy.com/" target="_blank" class="browser-warning noBg">' +
						'<div class="minimumBrowserVersions" data-bind="html:browserVersionsHtml"></div>' +
					'</a>' +
					'<p>' +
						'<div class="browserSupportText">The BladeRunner dashboard is not compatible with this browser.</div>' +
						'<div class="browserSupportText">Please upgrade to a recent version of Chrome, Firefox or use IE9.</div>' +
					'</p>';
					
	if( !fIsSupportedBrowser() )
	{
		var oModal = new WolfSimpleBox();
		oModal.setContent( sWarningHtml );
		oModal.setClosable( false );
		oModal.show();
	}
});