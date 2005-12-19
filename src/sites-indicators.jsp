<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Sites indicators' function - search page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<%
  WebContentManagement cm = SessionManager.getWebContent();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="<%=SessionManager.getCurrentLanguage()%>" xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=SessionManager.getCurrentLanguage()%>">
<head>
  <title>
    <%=application.getInitParameter("PAGE_TITLE")%>
    <%=cm.cms("sites_indicators_page_title")%>
  </title>
  <jsp:include page="header-page.jsp" />
</head>
  <body>
    <div id="outline">
    <div id="alignment">
    <div id="content">
    <jsp:include page="header-dynamic.jsp">
      <jsp:param name="location" value="home_location#index.jsp,sites_location#sites.jsp,sites_indicators_location" />
      <jsp:param name="mapLink" value="show" />
    </jsp:include>
    <%=cm.cmsText("sites-indicators_01")%>
    <jsp:include page="footer.jsp">
      <jsp:param name="page_name" value="sites-indicators.jsp" />
    </jsp:include>
    </div>
    </div>
    </div>
  </body>
</html>