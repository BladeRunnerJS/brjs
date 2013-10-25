package com.caplin.cutlass.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.Servlet;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class RestApiServletTestUtils
{
	
	public static Server createServer(String contextPath, int port, Servlet servlet, File resourceBase)
	{
		Server server = new Server(port);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath(contextPath);
		server.setHandler(context);
		context.addServlet(new ServletHolder(servlet),"/*");
		if (resourceBase != null) 
		{
			context.setResourceBase(resourceBase.getPath());
		}
		return server;
 	}
	
	public static HttpResponse makeRequest(HttpClient client, String method, String url) throws IOException
	{
		return makeRequest(client, method, url, "");
	}
	
	public static HttpResponse makeRequest(HttpClient client, String method, String url, String body) throws IOException
	{
		HttpResponse response = null;
		if (method.equals("GET"))
		{
			HttpGet request = new HttpGet(url);			
			response = client.execute(request);
		} else if (method.equals("POST"))
		{
			HttpPost request = new HttpPost(url);
			request.setEntity(new StringEntity(body));
			response = client.execute(request);
		}
		return response;
	}

	public static String getResponseTextFromResponse(HttpResponse response) throws IOException
	{
		StringBuilder responseText = new StringBuilder();
		InputStream in = response.getEntity().getContent();
		InputStreamReader is = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(is);
		String read = br.readLine();
		while(read != null) {
			responseText.append(read);
			read = br.readLine();
		}
		return responseText.toString();
	}
	
}
