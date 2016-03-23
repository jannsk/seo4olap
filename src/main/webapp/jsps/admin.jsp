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
        <title>SEO4OLAP</title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width, initial-scale=1">
		<c:set var="ctx" value="${pageContext.request.contextPath}"/>

        <link rel="stylesheet" href="${ctx}/css/bootstrap.min.css">
        <style>
            body {
                padding-top: 50px;
                padding-bottom: 20px;
            }
        </style>
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
		    <!-- Example row of columns -->
		    <div class="row">
		      <div class="col-md-9">
		      	<h2>Query Form</h2>
		      	<div>
			      	<form action="" method="post">
			      		
					  <div class="form-group">
					    <label for="dsId">DatasetId</label>
					    <input type="text" name="dsid"  id="dsId" class="form-control" value="${sparqlBean.dsId}"></input>
					    
					  </div>
					  <div class="form-group">
					  	<label for="query">Sparql-Query</label>
				  		<c:choose> 
						  <c:when test="${empty sparqlBean.query}">
						    <textarea name="query" id="query" class="form-control" rows="10">
PREFIX dcterms: &lt;http://purl.org/dc/terms/&gt; 
PREFIX rdfs: &lt;http://www.w3.org/2000/01/rdf-schema#&gt; 
PREFIX rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; 
PREFIX dc: &lt;http://purl.org/dc/elements/1.1/&gt; 
PREFIX sdmx-measure: &lt;http://purl.org/linked-data/sdmx/2009/measure#&gt; 
PREFIX qb: &lt;http://purl.org/linked-data/cube#&gt; 
PREFIX xkos: &lt;http://purl.org/linked-data/xkos#&gt; 
PREFIX owl: &lt;http://www.w3.org/2002/07/owl#&gt; 
PREFIX skos: &lt;http://www.w3.org/2004/02/skos/core#&gt; 
SELECT DISTINCT 
WHERE {

} 
							</textarea>
						  </c:when>
						  <c:otherwise>
						   	<textarea name="query" id="query" class="form-control" rows="10">${sparqlBean.query}</textarea>
						  </c:otherwise>
						</c:choose>
					  
					  
					  </div>
					  <button type="submit" class="btn btn-default" formaction="/admin/sparql">Run Query</button>
					  <button type="submit" class="btn btn-default" formaction="/admin/rdf">Get Dataset</button>
					  <button type="submit" class="btn btn-default" formaction="/admin/metadata">Show Metadata</button>
					</form>
		       	</div>
		       	<h2>Query Result</h2>
		      	${sparqlBean.result }
		      </div>
		      <div class="col-md-3">
		     		<h2>Setup</h2>
					<h4>Set Debug</h4>
					<p>
						If debug is on, debugInformation is displayed on result pages.
					</p>
					<form action="/admin/setup" method="post">
						<input style="display:none" name="task" value="setupDebug"/>
					 	<label class="radio-inline">
						  <input type="radio" name="debug" id="inlineRadio1" value="on"> On
						</label>
						<label class="radio-inline">
						  <input type="radio" name="debug" id="inlineRadio2" value="off"> Off
						</label>
					  <button style="margin-left:15px" type="submit" class="btn btn-default">Set Debug</button>
					</form>  
					<hr>
					<h4>Reset stored Data</h4>
					<p>
						Might be necessary after Configuration Update, e.g. SiteMap, index...
					</p>
					<form action="/admin/setup" method="post">
						<input style="display:none" name="task" value="resetStoredData"/>
					  <button type="submit" class="btn btn-default">Reset</button>
					</form> 
					<hr>
					<h4>Setup LabelMap</h4>
					<p>
						You can recompute the LabelMap of any Dataset. This might be necessary if the Configuration 
						has changed.<br>
						Set DatasetId to 'all' in order to recompute all Datasets.
					</p>
					<form action="/admin/setup" method="post">
						<input style="display:none" name="task" value="setupLabelMap"/>
					 	<div class="form-group">
						    <label for="dsId">DatasetId</label>
						    <input type="text" name="dsid"  id="dsId" class="form-control"></input>
						</div>
					  <button type="submit" class="btn btn-default">Recompute LabelMap</button>
					</form> 
					<hr>
					<h4>Setup Database</h4>
					<p>
						You can populate all Queries from the Sitemap into the Database.<br>
						Set DatasetId to 'all' in order to do this for all Datasets.
					</p>
					<form action="/admin/setup" method="post">
						<input style="display:none" name="task" value="setupDatabase"/>
					 	<div class="form-group">
						    <label for="dsId">DatasetId</label>
						    <input type="text" name="dsid"  id="dsId" class="form-control"></input>
						</div>
					  <button type="submit" class="btn btn-default">Load Queries</button>
					</form> 
			</div>
		    </div>
      	</div>
		
      	<%@include file='/templates/footer.html'%>
    	<%@include file='/templates/scripts-bottom.html'%>
    </body>
</html>
