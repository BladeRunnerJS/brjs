/**
 * This is a static class that never needs to be instantiated.
 * 
 * @class
 * Utility class that provides methods for array manipulation.
 * 
 * @memberOf br.util
 */
br.util.ArrayUtility = {}

/**
 * Return <code>true</code> if the input array contains the input value,
 * <code>false</code> otherwise.
 *
 * @param {Array} pArray The Array to test for the presence of the input variant.  May not be null or undefined.
 * @param {Variant} vValueToFind Variant whose presence in the input array is to be tested.  Works with null, undefined and NaN.
 * @type boolean
 * @return <code>true</code> if the specified value was found in the array,
 *		 <code>false</code> otherwise.
 */
br.util.ArrayUtility.inArray = function(pArray, vValueToFind)
{
	if (!Array.isArray(pArray)) throw new br.util.Error("TypeError", "ArrayUtility.inArray: IllegalArgument, ("+pArray+") is not an array.");

	// this is only true for NaN.
	if (vValueToFind !== vValueToFind) {
		for (var i = 0; i < pArray.length; ++i) {
			if (pArray[i] !== pArray[i]) {
				return true;
			}
		}
	}

	return pArray.indexOf(vValueToFind) >= 0;
};

/**
 * Removes the first example of the specified item in the specified array, or does nothing if the item is not found.
 * 
 * <p>Since this method uses the built-in <code>indexOf</code> which does work with <code>NaN</code>, it will
 * not work if called with NaN and will throw an ArgumentError.  If you need to remove <code>NaN</code>, you'll
 * have to loop over the array and then splice it out.</p>
 *
 * @param {Array} pArray The Array from which to remove the specified item.  May not be null or undefined.
 * @param {Variant} vValueToRemove The Variant to be removed from the specified array.  May not be NaN.
 * @return {Array} The input array with the specified item removed, or the original array if the item was not found.
 */
br.util.ArrayUtility.removeItem = function(pArray, vValueToRemove)
{
	if (vValueToRemove !== vValueToRemove)  throw new br.util.Error("ArgumentError", "ArrayUtility.removeItem: IllegalArgument, this method cannot remove NaN from an array.");
	var nItemPosition = pArray.indexOf(vValueToRemove);
	
	if(nItemPosition >= 0)
	{
		pArray.splice(nItemPosition, 1);
	}
	
	return pArray;
};
