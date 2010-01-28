<%--
  - Author(s)   : The EUNIS Database Team.
  - Date        :
  - Copyright   : (c) 2002-2005 EEA - European Environment Agency.
  - Description : News page
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement,ro.finsiel.eunis.utilities.SQLUtilities,java.util.*"%>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="<%=SessionManager.getCurrentLanguage()%>" xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=SessionManager.getCurrentLanguage()%>">
  <head>
<%
  String domainName = application.getInitParameter( "DOMAIN_NAME" );
%>
  <base href="<%=domainName%>/"/>
    <jsp:include page="../header-page.jsp" />
<%
  WebContentManagement cm = SessionManager.getWebContent();
  String eeaHome = application.getInitParameter( "EEA_HOME" );
  String btrail = "eea#" + eeaHome + ",home#index.jsp,data import";
%>
    <title>
      Data Import
    </title>
  </head>
  <body>
    <div id="visual-portal-wrapper">
      <jsp:include page="../header.jsp" />
      <!-- The wrapper div. It contains the three columns. -->
      <div id="portal-columns" class="visualColumnHideTwo">
        <!-- start of the main and left columns -->
        <div id="visual-column-wrapper">
          <!-- start of main content block -->
          <div id="portal-column-content">
            <div id="content">
              <div class="documentContent" id="region-content">
              	<jsp:include page="../header-dynamic.jsp">
                  <jsp:param name="location" value="<%=btrail%>"/>
                </jsp:include>
                <a name="documentContent"></a>
                <div class="documentActions">
                  <h5 class="hiddenStructure"><%=cm.cms("Document Actions")%></h5><%=cm.cmsTitle( "Document Actions" )%>
                  <ul>
                    <li>
                      <a href="javascript:this.print();"><img src="http://webservices.eea.europa.eu/templates/print_icon.gif"
                            alt="<%=cm.cms("Print this page")%>"
                            title="<%=cm.cms("Print this page")%>" /></a><%=cm.cmsTitle( "Print this page" )%>
                    </li>
                    <li>
                      <a href="javascript:toggleFullScreenMode();"><img src="http://webservices.eea.europa.eu/templates/fullscreenexpand_icon.gif"
                             alt="<%=cm.cms("Toggle full screen mode")%>"
                             title="<%=cm.cms("Toggle full screen mode")%>" /></a><%=cm.cmsTitle( "Toggle full screen mode" )%>
                    </li>
                  </ul>
                </div>
<!-- MAIN CONTENT -->
                <h1>
                  Data Import
                </h1>
                <ul>
                	<li>
                		<a href="<%=domainName%>/dataimport/data-tester.jsp">Data tester</a><br/>
                		The purpose of this page is to test the XML formatted Oracle dumps from the EUNIS maintainer.
                	</li>
                	<li>
                		<a href="<%=domainName%>/dataimport/data-importer.jsp">Data importer</a><br/>
                		The purpose of this page is to import the XML formatted Oracle dumps into EUNIS database.
                	</li>
                	<li>
                		<a href="<%=domainName%>/dataimport/import-log.jsp">Background actions log</a><br/>
                		Log messages about background data import and post import scripts
                	</li>
                	<li>
                		<a href="<%=domainName%>/dataimport/data-exporter.jsp">Data exporter</a><br/>
                		The purpose of this page is to export EUNIS database table into XML formatted Oracle dump.
                	</li>
                	<li>
                		<a href="<%=domainName%>/dataimport/schema-exporter.jsp">Schema exporter</a><br/>
                		The purpose of this page is to create XML schema from EUNIS database table structure.
                	</li>
                	<li>
                		<a href="<%=domainName%>/dataimport/post-import.jsp">Post import scripts</a><br/>
                		The purpose of this page is to run database scripts after import!
                	</li>
                	<li>
                		<a href="<%=domainName%>/dataimport/matchgeospecies">Match geospecies</a><br/>
                		The purpose of this page is to import geospecies relations and match some of them manually to EUNIS species.
                	</li>
                	<li>
                		<a href="<%=domainName%>/dataimport/importpagelinks">Import page links</a><br/>
                		The purpose of this page is to import page links from geospecies database.
                	</li>
                </ul>
<!-- END MAIN CONTENT -->
              </div>
            </div>
          </div>
          <!-- end of main content block -->
          <!-- start of the left (by default at least) column -->
          <div id="portal-column-one">
            <div class="visualPadding">
              <jsp:include page="../inc_column_left.jsp">
                <jsp:param name="page_name" value="news.jsp" />
              </jsp:include>
            </div>
          </div>
          <!-- end of the left (by default at least) column -->
        </div>
        <!-- end of the main and left columns -->
        <div class="visualClear"><!-- --></div>
      </div>
      <!-- end column wrapper -->
      <jsp:include page="../footer-static.jsp" />
    </div>
  </body>
</html>
