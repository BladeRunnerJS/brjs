caplin.thirdparty( 'caplin-br' );

( function() {

  var br = require( 'br' );

  function ExamplePresentationModel() {
    var Property = br.presenter.property.Property;
    
    this.message = new Property( 'Hello World!' );
  };
  br.extend( ExamplePresentationModel, br.presenter.PresentationModel );

  ExamplePresentationModel.prototype.buttonClicked = function() {
    console.log( 'button clicked' );
  }

  @appns.@bladeset.@blade.ExamplePresentationModel = ExamplePresentationModel;

} )();
