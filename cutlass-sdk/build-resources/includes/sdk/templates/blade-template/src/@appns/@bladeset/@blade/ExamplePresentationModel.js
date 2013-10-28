caplin.thirdparty( 'caplin-br' );

( function() {

  var br = require( 'br' );

  function ExamplePresentationModel() {
    this.message = new br.presenter.property.Property( 'Hello World!' );
  };
  br.extend( ExamplePresentationModel, br.presenter.PresentationModel );

  ExamplePresentationModel.prototype.buttonClicked = function() {
    console.log( 'button clicked' );
  }

  @appns.@bladeset.@blade.ExamplePresentationModel = ExamplePresentationModel;

} )();
