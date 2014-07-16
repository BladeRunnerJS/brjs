brjs.dashboard.app.model.form.FileField = function(sAccepts)
{
	this.accepts = new br.presenter.property.WritableProperty(sAccepts);
	this.fileSelected = new br.presenter.property.Property(false);
	this.enabled = new br.presenter.property.WritableProperty(true);
	this.fileName = new br.presenter.property.WritableProperty("");
	this.fileInputVisible = new br.presenter.property.WritableProperty( true );
	this.fileInfoVisible = new br.presenter.property.WritableProperty( false );
};
br.Core.extend(brjs.dashboard.app.model.form.FileField, br.presenter.node.PresentationNode);

brjs.dashboard.app.model.form.FileField.prototype.onChange = function(oViewModel, oEvent)
{
	this.m_eFileInput = oEvent.currentTarget;

	this._setFileName();
	

	if(this.m_eFileInput.value)
	{
		this.fileSelected._$setInternalValue(true);
		this.fileInputVisible.setValue( false );
		this.fileInfoVisible.setValue( true );
	}
	else
	{
		this.fileSelected._$setInternalValue(false);
		this.fileInputVisible.setValue( true );
		this.fileInfoVisible.setValue( false );
	}
};

brjs.dashboard.app.model.form.FileField.prototype.getFileInput = function()
{
	return this.m_eFileInput;
};

brjs.dashboard.app.model.form.FileField.prototype.chooseDifferentFile = function()
{
	this.fileSelected._$setInternalValue(false);
	this.fileInputVisible.setValue( true );
	this.fileInfoVisible.setValue( false );
};

brjs.dashboard.app.model.form.FileField.prototype._setFileName = function()
{
	var sFileName = "";

	if( this.m_eFileInput.files && this.m_eFileInput.files.length === 1 )
	{
		sFileName =  this.m_eFileInput.files[0].name;
	}
	else
	{
		pFileName = this.m_eFileInput.value.split( "/" );
		sFileName = pFileName[ pFileName.length - 1 ];
	}

	this.fileName.setValue( sFileName );
};
