package org.bladerunnerjs.github


class Issue
{

	String url
	int id
	String title
	List<String> labels
	
	public Issue(String url, int id, String title, List<String> labels)
	{
		this.url = url
		this.id = id
		this.title = title
		this.labels = labels
	}
	
	public String toString()
	{
		return "${title} [${url}](${url})"
	}
	
}
