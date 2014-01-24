import Person = require('./Person');

class Team {
	private name: string;
	private people: Person[] = [];
	
	constructor(name: string) {
		this.name = name;
	}
	
	addPLayer(person: Person): void {
		this.people.push(person);
	}
}
export = Team;
