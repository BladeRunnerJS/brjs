var br = require( 'br/Core' );
var Property = require( 'br/presenter/property/Property' );
var PresentationModel = require( 'br/presenter/PresentationModel' );

function ExamplePresentationModel() {
  this.message = new Property( 'Hello World!' );
}
br.extend( ExamplePresentationModel, PresentationModel );

ExamplePresentationModel.prototype.buttonClicked = function() {
  console.log( 'button clicked' );
};

module.exports = ExamplePresentationModel;
