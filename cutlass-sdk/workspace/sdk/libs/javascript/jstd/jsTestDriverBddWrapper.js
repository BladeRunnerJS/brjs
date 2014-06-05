Boolean.prototype.shouldBeTrue = function(){
	assertTrue(this.valueOf());
};

Boolean.prototype.shouldBeFalse = function(){
	assertFalse(this.valueOf());
};

String.prototype.shouldBe = function(other){
	assertEquals(other, this);
};

Array.prototype.shouldBe = function(other){
	assertEquals(other, this);
};

Number.prototype.shouldBe = function(other){
	assertEquals(other,this.valueOf());
};

String.prototype.shouldNotBeNull = function(){
	assertNotNull(this);
};

Array.prototype.shouldNotBeNull = function(){
	assertNotNull(this);
};

Number.prototype.shouldNotBeNull = function(other){
	assertNotNull(this.valueOf());
};

String.prototype.shouldContain = function(value){
	(this.search(value) >= 0).shouldBeTrue();
};

Array.prototype.shouldContain = function(value){
	for(var x in this){
		if(x == value) return;
	}
	fail("Expected value not present in array");
};

String.prototype.shouldBeEmpty = function(){
	this.length.shouldBe(0);
};

String.prototype.shouldBeOfSize = function(size){
	this.length.shouldBe(size);
};

Array.prototype.shouldBeOfSize = function(size){
	this.length.shouldBe(size);
};

Array.prototype.shouldBeEmpty = function(){
	this.length.shouldBe(0);
};

// Needs some pumping to expose this assertion
//Object.prototype.shouldBe = function(other){
//	if(other != undefined)
//		assertSame(other, this);
//};
