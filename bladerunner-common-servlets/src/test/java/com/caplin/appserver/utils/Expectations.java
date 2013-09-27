/**
 * 
 */
package com.caplin.appserver.utils;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class Expectations extends org.jmock.Expectations
{
	FilterConfig one(FilterConfig mock)
	{
		return super.one(mock);
	}
	
	HttpServletRequest one(HttpServletRequest mock)
	{
		return super.one(mock);
	}
	
	HttpServletResponse one(HttpServletResponse mock)
	{
		return super.one(mock);
	}
	
	FilterChain one(FilterChain mock)
	{
		return super.one(mock);
	}
	
	RequestDispatcher one(RequestDispatcher mock)
	{
		return super.one(mock);
	}
	
	ServletConfig one(ServletConfig mock)
	{
		return super.one(mock);
	}
	
	ServletContext one(ServletContext mock)
	{
		return super.one(mock);
	}
}