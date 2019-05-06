package com.seo4ajax;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.seo4ajax.tools.IOUtils;

public class SEO4AjaxFilter implements Filter {

	private static final String SITE_TOKEN_PARAM = "siteToken";

	private static final String REGEXP_BOTS_PARAM = "regexpBots";

	private static final String URL_API_PARAM = "urlApi";

	private static final int PROXY_READ_TIMEOUT = 10 * 1000;

	private static final int PROXY_CONNECT_TIMEOUT = 30 * 1000;

	private static final String USER_AGENT_HEADER = "User-Agent";
	
	private static final String ESCAPED_FRAGMENT_QUERY_PARAM = "_escaped_fragment_=";

	private static String siteToken;

	private static String regexpBots = ".*(bot|spider|pinterest|crawler|archiver|flipboardproxy|mediapartners|facebookexternalhit|quora).*";

	private static String urlApi = "https://api.seo4ajax.com/";

	@Override
	public void init(final FilterConfig config) throws ServletException {
		siteToken = config.getInitParameter(SITE_TOKEN_PARAM);
		if (siteToken == null) {
			throw new ServletException(SITE_TOKEN_PARAM + " parameter not set");
		}
		if (config.getInitParameter(REGEXP_BOTS_PARAM) != null) {
			regexpBots = config.getInitParameter(REGEXP_BOTS_PARAM);
		}
		if (config.getInitParameter(URL_API_PARAM) != null) {
			urlApi = config.getInitParameter(URL_API_PARAM);
		}
		urlApi += siteToken;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		String queryString = request.getQueryString();
		URLConnection urlConnection = null;
		boolean foundEscapedFragment = false;
		String url = urlApi + request.getRequestURI();
		if (queryString != null) {
			url += "?" + queryString;
			foundEscapedFragment = queryString.endsWith(ESCAPED_FRAGMENT_QUERY_PARAM);
		}
		if (foundEscapedFragment) {
			urlConnection = new URL(url).openConnection();
		} else {
			String userAgent = request.getHeader(USER_AGENT_HEADER);
			if (userAgent != null && userAgent.matches(regexpBots)) {
				urlConnection = new URL(url).openConnection();
			}
		}
		if (urlConnection == null) {
			chain.doFilter(servletRequest, servletResponse);
		} else {
			HttpServletResponse response = (HttpServletResponse) servletResponse;
			response.setContentType(urlConnection.getContentType());
			urlConnection.setConnectTimeout(PROXY_CONNECT_TIMEOUT);
			urlConnection.setReadTimeout(PROXY_READ_TIMEOUT);
			IOUtils.copy(urlConnection.getInputStream(), response.getOutputStream(), true);
		}
	}

}
