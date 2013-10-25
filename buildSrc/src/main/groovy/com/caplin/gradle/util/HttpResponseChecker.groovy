
package com.caplin.gradle.util

import org.apache.http.client.HttpClient
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient


class HttpResponseChecker
{

	private static final int DEFAULT_MAX_TRIES = 20
	private static final int DEFAULT_SLEEP_TIME = 3000
    private static final int TIMEOUT_RESPONSE_CODE = -1;

	private int maxTries = -1
	private int sleepTime = -1
	private String url = ""
	private boolean printOutput

	HttpResponseChecker(String url)
	{
		this(url, DEFAULT_MAX_TRIES, DEFAULT_SLEEP_TIME, true)
	}

	HttpResponseChecker(String url, boolean printOutput)
	{
		this(url, DEFAULT_MAX_TRIES, DEFAULT_SLEEP_TIME, printOutput)
	}

	HttpResponseChecker(String url, int maxTries, int sleepTime, boolean printOutput)
	{
		this.maxTries = maxTries;
		this.sleepTime = sleepTime;
		this.url = url;
		this.printOutput = printOutput
	}

	boolean checkForResponseCode(int responseCode)
	{
		HttpClient httpclient = new DefaultHttpClient();
		boolean hasConnected = false
        boolean connectionTimeout = false
		int tryCount = 0;

		while (!hasConnected && tryCount < maxTries)
		{
			sleep(sleepTime)
			tryCount++
			try
			{
				if (printOutput)
				{
					if (responseCode > -1)
                    {
                        println "Attempting connection to ${url}"
                    }
                    else
                    {
                        println "Ensuring connection timeout to ${url}"
                    }
				}
				HttpResponse response = httpclient.execute(new HttpGet(url));
				int statusCode = response.getStatusLine().getStatusCode()
				if (statusCode == responseCode)
				{
					hasConnected = true
				} else
				{
					hasConnected = false
				}
			} catch (Exception ex)
			{
				hasConnected = false
                connectionTimeout = true
                if (responseCode == TIMEOUT_RESPONSE_CODE)
                {
                    return connectionTimeout;
                }
			}
		}
        if (responseCode == TIMEOUT_RESPONSE_CODE)
        {
            return connectionTimeout;
        }
        else
        {
		    return hasConnected
        }
	}

    boolean confirmNoResponse()
    {
        return checkForResponseCode(TIMEOUT_RESPONSE_CODE)
    }

}