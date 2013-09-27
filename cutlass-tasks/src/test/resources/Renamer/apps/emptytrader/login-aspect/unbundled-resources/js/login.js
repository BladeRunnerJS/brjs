emptycorp = {};
emptycorp.emptytrader = {};
emptycorp.emptytrader.login = function(form)
{
	var username = form.getElementsByClassName('username')[0].value;
	var locale = form.getElementsByClassName('locale')[0].value;
	emptycorp.emptytrader.CookieUtils.setCookie("CAPLIN.LOCALE",locale,1);
};