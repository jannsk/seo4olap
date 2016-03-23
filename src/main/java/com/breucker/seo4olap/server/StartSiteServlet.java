package com.breucker.seo4olap.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.breucker.seo4olap.main.IndexBean;
import com.breucker.seo4olap.main.RequestHandler;


@SuppressWarnings("serial")
public class StartSiteServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		resp.setContentType("text/html");
		
		RequestHandler requestHandler = new RequestHandler();
		IndexBean indexBean = requestHandler.getIndexBean(false);
		
		req.setAttribute("runtimeContext", requestHandler.getRuntimeContext(false));
		req.setAttribute("index", indexBean);
		req.getRequestDispatcher("/jsps/index.jsp").forward(req, resp);
	}
}
