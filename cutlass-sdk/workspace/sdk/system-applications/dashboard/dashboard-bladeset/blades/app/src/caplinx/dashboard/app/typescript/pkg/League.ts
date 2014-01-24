import Team = require('../Team');

class League {
	private name: string;
	private teams: Team[] = [];
	
	constructor(name: string) {
		this.name = name;
	}
	
    getName(): string {
        return this.name;
    }
    
	addTeam(team: Team): void {
		this.teams.push(team);
	}
}
export = League;
