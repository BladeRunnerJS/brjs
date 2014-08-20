/**
 * @module br/presenter/SerializablePresentationModel
 */

/**
 * @interface
 * @class
 * @alias module:br/presenter/SerializablePresentationModel
 * 
 * @classdesc
 * Interface implemented by presentation models in order to provide a serialized form of the data they contain.
 */
br.presenter.SerializablePresentationModel = function()
{
};

/**
 * This method provides the serialized form of the presentation model.
 * Keep in mind that the implementor should be the one providing the deserialization implementation.
 * 
 * @return Serialized presentation model.
 * @type String
 */
br.presenter.SerializablePresentationModel.prototype.serialize = function()
{
	throw new br.Errors.CustomError(br.Errors.UNIMPLEMENTED_INTERFACE,
			"br.presenter.SerializablePresentationModel.serialize() has not been implemented.");
};

/**
 * Presentation models can implement this method in order to provide a deserialization mechanism.
 * 
 * @param {String} sData Serialized presentation model.
 */
br.presenter.SerializablePresentationModel.prototype.deserialize = function(sData)
{
	throw new br.Errors.CustomError(br.Errors.UNIMPLEMENTED_INTERFACE,
			"br.presenter.SerializablePresentationModel.deserialize() has not been implemented.");
};
