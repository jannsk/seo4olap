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
        <title>${result.metadataTitle}</title>
        <meta name="description" content="${result.metadataDescription}">
        <meta name="keywords" content="${result.metadataKeywords}">
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
		
        <div class="container" id="own-result" itemscope itemtype="http://schema.org/Dataset">
			<div class="row" style="min-height: 300px">
				<div class="col-md-12">
					<h1 itemprop="name">${result.queryTitle}</h1>
				</div>
	    		<div class="col-md-8" itemprop="mainEntity" itemscope itemtype="http://schema.org/Table">
		        	<table class="table table-hover" >
						<c:forEach items="${result.table}" var="row" varStatus="rowStatus">
							<c:choose> 
							  <c:when test="${rowStatus.first}">
							    <tr class="info">
									<c:forEach items="${row}" var="entry">
										<th>${entry}</th>						
									</c:forEach>
								</tr>
							  </c:when>
							  <c:otherwise>
							    <tr>
									<c:forEach items="${row}" var="entry">
										<td>${entry}</td>						
									</c:forEach>
								</tr>
							  </c:otherwise>
							</c:choose>
						</c:forEach>
					</table>	
		      	</div>
				<div class="col-md-4">
		        	<div class="panel panel-default">
					  	<div class="panel-heading">
					    	<h3 class="panel-title">Active filters</h3>
					  	</div>
					  	<div class="panel-body">
				    		<table>
				    			<c:forEach items="${result.filters}" var="filter">
									<tr>
					    				<td>${filter}</td>
					    			</tr>
								</c:forEach>
				    		</table>
					  	</div>
					</div>
				</div>
		   	</div>
		   	<div class="row">
		  		<div class="col-md-4">
		  			<h3>Change View</h3>
		  			<c:choose> 
					  	<c:when test="${not empty result.changeViewLinks}">
				        	<div class="list-group">
				        		<c:forEach items="${result.changeViewLinks}" var="link">
									<a class="list-group-item" href="${link.url}">${link.text}</a>
								</c:forEach>
				        	</div>
					  	</c:when>
					  	<c:otherwise>
					    	<ul class="list-group">
								<li class="list-group-item">No change possible</li>
							</ul>
					  	</c:otherwise>
					</c:choose>
		      	</div>
		      	<div class="col-md-4">
		  			<h3>Add Filter</h3>
		  			<c:choose> 
					  	<c:when test="${not empty result.filterLinks}">
				        	<div class="list-group">
				        		<c:forEach items="${result.filterLinks}" var="link">
									<a class="list-group-item" href="${link.url}">${link.text}</a>
								</c:forEach>
				        	</div>
					  	</c:when>
					  	<c:otherwise>
					    	<ul class="list-group">
								<li class="list-group-item">No filter available</li>
							</ul>
					  	</c:otherwise>
					</c:choose>
		      	</div>
		      	<div class="col-md-4">
		      		<h3 style="visibility:hidden">---</h3>
					<div class="panel panel-default">
					  	<div class="panel-heading">
					    	<h3 class="panel-title" style="display:inline-block">About the Dataset</h3>
					    	<c:if test="${not empty result.overviewLink}">
								<a href="${result.overviewLink.url}" class=""><button class="pull-right btn btn-default btn-xs">Overview</button></a>
					  		</c:if>
					  	</div>
					  	<div class="panel-body">
					    	<p itemprop="description">${result.datasetDescription}</p>
					    	<meta itemprop="keywords" content="${result.metadataKeywords}" />
				    		<table>
				    			<tr>
				    				<td>Source:</td>
				    				<td><a itemprop="isBasedOnUrl" href="${result.sourceLink.url }">${result.sourceLink.text}</a></td>
				    			</tr>
				    			<tr>
				    				<td>License:</td>
				    				<td><a href="${result.licenceLink.url }">${result.licenceLink.text}</a></td>
				    			</tr>
				    		</table>
					  	</div>
					</div>
		      	</div>
		    </div>
    	</div>    
		
		<c:if test="${runtimeContext.debugMode}">
			<div class="container">
				<div class="row">
					<h2 class="col-md-12">DebugInformation</h2>
				    <div class="col-md-6">
				        <h3>OlapRequest</h3>
				        
			        	<h4>datasetUri:</h4>
			        	<ul class="list-group">
							<li class="list-group-item">${result.debugInformation.datasetUri}</li>
						</ul>
						
			            <h4>members2dice:</h4>
						<ul class="list-group">
							<c:forEach items="${result.debugInformation.members2dice}" var="member">
								<li class="list-group-item">${member}</li>
							</c:forEach>
						</ul>
						
						<h4>measures2project</h4>
						<ul class="list-group">
							<c:forEach items="${result.debugInformation.measures2project}" var="member">
								<li class="list-group-item">${member}</li>
							</c:forEach>
						</ul>
						
						<h4>dimensions2keep:</h4>
						<ul class="list-group">
							<c:forEach items="${result.debugInformation.dimensions2keep}" var="member">
								<li class="list-group-item">${member}</li>
							</c:forEach>
						</ul>
					</div>
					<div class="col-md-6">
						<h3>ResultGeneration</h3>
						
			        	<h4>dicedMembers:</h4>
						<ul class="list-group">
							<c:forEach items="${result.debugInformation.dicedMembers}" var="member">
								<li class="list-group-item">${member}</li>
							</c:forEach>
						</ul>
						
			        	<h4>freeDimensions:</h4> 
						<ul class="list-group">
							<c:forEach items="${result.debugInformation.freeDimensions}" var="member">
								<li class="list-group-item">${member}</li>
							</c:forEach>
						</ul>
						
			        	<h4>projectedMeasures:</h4> 
						<ul class="list-group">
							<c:forEach items="${result.debugInformation.projectedMeasures}" var="member">
								<li class="list-group-item">${member}</li>
							</c:forEach>
						</ul>
				      </div>
				</div>
				<div class="row">
					<div class="col-md-12">
				        <h3>Metadata</h3>
				        <p>
				        	MetadataTitle: ${result.metadataTitle}<br>
				        	MetatadataDescription: ${result.metadataDescription}<br>
				        	MetadataKeywords: ${result.metadataKeywords}<br>
				        </p>
					</div>
				</div>
			</div>
		</c:if>
		
    	<%@include file='/templates/footer.html'%>
    	<%@include file='/templates/scripts-bottom.html'%>
    	<%@include file='/templates/scripts-tracking.html'%>
    </body>
</html>
