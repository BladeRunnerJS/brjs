/**
 * @module br/presenter/util/ObjectGraphCrawler
 */

br.presenter.util.ObjectGraphCrawler = function() {
};

br.presenter.util.ObjectGraphCrawler.prototype.getValueFromObject = function(oRoot, vParts, sInitialParts)
{
	if(!sInitialParts) 
	{
		if (vParts instanceof Array) 
			sInitialParts = vParts.join(".");
		else
			sInitialParts = vParts;
	}
		
	var pParts = this._getPartsArray(vParts);
	var vPathElement = pParts.shift();
	var vNode = oRoot[vPathElement];

	if(!vNode) 
	{
//		var fieldName =  this._capitaliseFirstLetter(vPathElement);
		var sGetterName = "get" + this._capitaliseFirstLetter(vPathElement);

		if(oRoot[sGetterName])
		{
			vNode = oRoot[sGetterName]();
		}
		else if(oRoot.getFieldValue && oRoot.getFieldValue(vPathElement) !== undefined){
			vNode = oRoot.getFieldValue(vPathElement);
		}
		else
		{
			throw new br.Errors.InvalidParametersError('The method "' + sGetterName + '" or object "' +  vPathElement + '" could not be found when searching for property "' + sInitialParts + '"');
		}
	}
	
	if(pParts.length === 0)
	{
		return vNode;
	}
	else
	{
		if(vNode)
		{
			return this.getValueFromObject(vNode, pParts, sInitialParts);
		}
		else
		{
			throw new br.Errors.InvalidParametersError('The method "' +  sGetterName + '" returned ' + vNode + ' when searching for property "' + sInitialParts + '"');
		}
	}
};

br.presenter.util.ObjectGraphCrawler.prototype.setValueForObject = function(oRoot, vParts, newValue)
{
	var oNode = undefined;
	var pParts = this._getPartsArray(vParts);
	var sLastElement = pParts.pop();
	var sSetterName = "set" + this._capitaliseFirstLetter(sLastElement);
	oNode = this._getObjectNodeOnWhichToSetValue(oRoot, pParts);

	if(oNode[sLastElement])
	{
		oNode[sLastElement] = newValue;
	}
	else
	{
		if(oNode[sSetterName])
		{
			oNode[sSetterName](newValue);
		}
		else if(oNode["setFieldValue"]){
			oNode.setFieldValue(sLastElement, newValue);
		}
		else
		{
			throw new br.Errors.InvalidParametersError('The method "' + sSetterName  + '" does not exist');
		}	
	}
};

br.presenter.util.ObjectGraphCrawler.prototype._getPartsArray = function(vParts) {
	if(vParts.split)
	{
		return vParts.split('.');
	}
	return vParts.slice(0);
};

br.presenter.util.ObjectGraphCrawler.prototype._capitaliseFirstLetter = function(sValue) {
	return sValue.charAt(0).toUpperCase() + sValue.slice(1);
};

br.presenter.util.ObjectGraphCrawler.prototype._getObjectNodeOnWhichToSetValue = function(oRoot, pParts) {
	return pParts.length > 0 ? this.getValueFromObject(oRoot, pParts) : oRoot;
};
