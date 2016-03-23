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
        <title>${dataset.title}</title>
        <meta name="description" content="Overview of dataset ${dataset.title}. ${dataset.description}">
        <meta name="viewport" content="width=device-width, initial-scale=1">
		<c:set var="ctx" value="${pageContext.request.contextPath}"/>

        <link rel="stylesheet" href="${ctx}/css/bootstrap.min.css">
        <link rel="stylesheet" href="${ctx}/css/bootstrap-theme.min.css">
        <link rel="stylesheet" href="${ctx}/css/main.css">

        <script src="${ctx}/js/vendor/modernizr-2.8.3.min.js"></script>
    </head>
    <body>
        <!--[if lt IE 8]>
            <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
        <![endif]-->
		<%@include file='/templates/navbar.html'%>
        <div class="container">
			<div class="row">
		    	<h1 class="col-md-12" style="margin: 0px 0px 20px 0px">${dataset.title}</h1>
		    </div>
	  	 	<div class="row">
	        	<h4 class="col-md-12">Explore the Dataset</h4>
	        	<div class="col-md-8">
	        		<c:if test="${not empty dataset.links}">
		        		<div class="list-group">
				        	<c:forEach items="${dataset.links}" var="link">
								<a class="list-group-item" href="${link.url}">${link.text}</a>
							</c:forEach>
						</div>
					</c:if>
				</div>
				<div class="col-md-4" id="own-dataset-panel">
					<div class="panel panel-default">
					  	<div class="panel-heading">
					    	<h3 class="panel-title">About the Dataset</h3>
					  	</div>
					  	<div class="panel-body">
					    	<p>${dataset.description}</p>
				    		<table>
				    			<tr>
				    				<td>Source:</td>
				    				<td><a href="${dataset.source.url}">${dataset.source.text}</a></td>
				    			</tr>
				    			<tr>
				    				<td>Licence:</td>
				    				<td><a href="${dataset.licence.url }">${dataset.licence.text}</a></td>
				    			</tr>
				    		</table>
					  	</div>
					</div>
				</div>	
        	</div>
    	</div>
    
		<%@include file='/templates/footer.html'%>
    	<%@include file='/templates/scripts-bottom.html'%>
    	<%@include file='/templates/scripts-tracking.html'%>
    </body>
</html>
