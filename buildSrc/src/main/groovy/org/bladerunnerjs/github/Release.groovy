package org.bladerunnerjs.github


class Release
{

	String url
	String upload_url
	int id
	String name
	String tagVersion
	
	public Release(String url, String upload_url, int id, String name, String tagVersion)
	{
		this.url = url
		this.upload_url = upload_url
		this.id = id
		this.name = name
		this.tagVersion = tagVersion
	}
	
}
