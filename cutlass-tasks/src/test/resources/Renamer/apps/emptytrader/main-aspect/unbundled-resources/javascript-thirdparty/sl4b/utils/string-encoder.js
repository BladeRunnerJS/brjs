var SL4B_StringEncoder=function(){this.m_oCharactersToEncodeRegExp=/[<>&"]/g;this.m_mDecodedToEncodedValueMap={"<":"&lt;", ">":"&gt;", "&":"&amp;", "\"":"&quot;"};};
if(false){function SL4B_StringEncoder(){}
}SL4B_StringEncoder.prototype.encodeValue = function(A){if(this.m_oCharactersToEncodeRegExp.test(A)){return A.replace(this.m_oCharactersToEncodeRegExp,this._encodeCharacter);
}return A;
};
SL4B_StringEncoder.prototype._encodeCharacter = function(A){return SL4B_StringEncoder.m_mDecodedToEncodedValueMap[A];
};
SL4B_StringEncoder=new SL4B_StringEncoder();