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
        <title>Your-Domain.org title</title>
        <meta name="description" content="Open-Statistics offers you a diversity of different statistical governmental datasets to explore.">
        <meta name="viewport" content="width=device-width, initial-scale=1">
		<c:set var="ctx" value="${pageContext.request.contextPath}"/>

        <link rel="stylesheet" href="${ctx}/css/bootstrap.min.css">
        <link rel="stylesheet" href="${ctx}/css/bootstrap-theme.min.css">
        <link rel="stylesheet" href="${ctx}/css/main.css">
        <style>
            body {
                padding-top: 50px;
                padding-bottom: 20px;
            }
        </style>
    </head>
    <body>
        <!--[if lt IE 8]>
            <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
        <![endif]-->
        <!-- Main jumbotron for a primary marketing message or call to action -->
	    <div class="jumbotron">
	      <div class="container">
	        <h1>Start exploring now!</h1>
	        <p>
				Welcome to Your-Domain. <br>
				
			</p>
	      </div>
	    </div>
        
		<%@include file='/templates/navbar.html'%>
		
        <div class="container">		 
    		<c:if test="${not empty index.datasets}">
    			<div id="datasets" class="row">
	    			<h2 class="col-md-12">Newest Datasets</h2>
		        	<c:forEach items="${index.datasets}" var="dataset">
		        		<div class="col-md-12 dataset">
							<h4><a href="${dataset.link.url}">${dataset.title}</a></h4>
				        	<p>${dataset.description}</p>
			        	</div>
					</c:forEach>
				</div>
        	</c:if>
      	</div> 
      	
		<%@include file='/templates/footer.html'%>
    	<%@include file='/templates/scripts-bottom.html'%>
    	<%@include file='/templates/scripts-tracking.html'%>
    </body>
</html>
