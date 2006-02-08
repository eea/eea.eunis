<%--
  - Author(s)   : The EUNIS Database Team.
  - Date        :
  - Copyright   : (c) 2002-2005 EEA - European Environment Agency.
  - Description : Species download.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@page import="ro.finsiel.eunis.WebContentManagement"%>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="<%=SessionManager.getCurrentLanguage()%>" xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=SessionManager.getCurrentLanguage()%>">
  <head>
    <jsp:include page="header-page.jsp" />
    <%
        WebContentManagement cm = SessionManager.getWebContent();
    %>
    <title><%=application.getInitParameter("PAGE_TITLE")%><%=cm.cms("species_download_title")%></title>
  </head>
  <body>
    <div id="outline">
    <div id="alignment">
    <div id="content">
      <jsp:include page="header-dynamic.jsp">
        <jsp:param name="location" value="home_location#index.jsp,species_location#species.jsp,links_downloads_location"/>
        <jsp:param name="mapLink" value="show"/>
      </jsp:include>
      <h1>
       <%=cm.cmsText("species_download_title")%>
      </h1>
      <br />
      <%=cm.cmsText("species_download_01")%>
      <%=cm.br()%>
      <%=cm.cmsMsg("species_download_title")%>
      <%=cm.br()%>
      <jsp:include page="footer.jsp">
        <jsp:param name="page_name" value="species-download.jsp" />
      </jsp:include>
    </div>
    </div>
    </div>
  </body>
</html>