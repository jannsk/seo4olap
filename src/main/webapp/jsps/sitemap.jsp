<!doctype html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang=""> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8" lang=""> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9" lang=""> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang=""> <!--<![endif]-->
    <head>
    	<%@ page pageEncoding="UTF-8" %>
    	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title>Your-Domain.org - Sitemap</title>
        <meta name="description" content="Open-Statistics.org - Sitemap">
        <meta name="viewport" content="width=device-width, initial-scale=1">
		<c:set var="ctx" value="${pageContext.request.contextPath}"/>

        <link rel="stylesheet" href="${ctx}/css/bootstrap.min.css">
        <link rel="stylesheet" href="${ctx}/css/bootstrap-theme.min.css">
        <link rel="stylesheet" href="${ctx}/css/main.css">
    </head>
    <body>
        <!--[if lt IE 8]>
            <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
        <![endif]-->
		<%@include file='/templates/navbar.html'%>
        <div class="container">
		    <div class="row">
		      	<div class="col-md-12">
		        	<h1 style="margin-top: 0px">Sitemap</h1>
		        </div>
	        	<c:forEach items="${sitemaps}" var="sitemap">
	        		<div style="margin-bottom: 20px"  class="col-md-12">
						<h3>Dataset: ${sitemap.title}</h3>
						<ul>
							<c:forEach items="${sitemap.links}" var="link">
								<li><a href="${link.url}">${link.text}</a></li>
							</c:forEach>
						</ul>
					</div>
				</c:forEach>
		    </div>
    	</div>     
    	
		<%@include file='/templates/footer.html'%>
    	<%@include file='/templates/scripts-bottom.html'%>
    	<%@include file='/templates/scripts-tracking.html'%>
    </body>
</html>
