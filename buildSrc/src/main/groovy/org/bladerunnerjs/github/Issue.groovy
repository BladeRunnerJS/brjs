package org.bladerunnerjs.github


class Issue
{

	String url
	int id
	String title
	
	public Issue(String url, int id, String title)
	{
		this.url = url
		this.id = id
		this.title = title
	}
	
	public String toString()
	{
		return "${title} (${url})"
	}
	
}
