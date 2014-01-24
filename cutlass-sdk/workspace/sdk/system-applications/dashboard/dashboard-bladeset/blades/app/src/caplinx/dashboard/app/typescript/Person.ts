class Person {
	private name: string;
	private age: number;
	private salary: number;

	constructor(name: string, age: number, salary: number) {
		this.name = name;
		this.age = age;
		this.salary = salary;
	}
	
	toString(): string {
		return this.name + " (" + this.age + ")" + "(" + this.salary + ")";
	}
}
export = Person;
