live.examples.presenter.Dependencies;

live.examples.presenter.LiveExamples = function()
{
	live.examples.presenter.EVENT_HUB = new live.examples.presenter.EventEmitter();
	this.m_oLayout = new live.examples.presenter.Layout();
	this.m_oModel = new live.examples.presenter.Model();
	this.m_oController = new live.examples.presenter.Controller( this.m_oModel, this.m_oLayout );
	this.initNamespace();

	live.examples.presenter.EVENT_HUB.bind( "ModelReady", this.init, this );
	window.onhashchange = this.loadFromHash.bind( this );
};

live.examples.presenter.LiveExamples.prototype.init = function()
{
	setTimeout(function(){
		if( document.location.hash )
		{
			this.loadFromHash();
		}
		else
		{
			this.m_oController.loadContent( "about" );
		}
	}.bind(this), 500);
};

live.examples.presenter.LiveExamples.prototype.loadFromHash = function()
{
	var sKey = document.location.hash.substr(1);
	var pSections = sKey.split('/');
	sKey = pSections[pSections.length-1];
	if( live.examples.presenter.HASH !== sKey )
	{
		this.m_oController.loadContent( sKey );
	}
};

live.examples.presenter.LiveExamples.prototype.loadIntro = function()
{
	var self = this;
	$.get( live.examples.presenter.Config.INTRO_URL, function( sHtml ){
		self.m_oLayout.showTutorial( sHtml );
	});
};

live.examples.presenter.LiveExamples.prototype.initNamespace = function()
{
	novobank={example:{}};
	jQuery.tmpl.tag.ko_code = { open: (this.q < 3 ? "_" : "__") + ".push($1 || '');" };
};

