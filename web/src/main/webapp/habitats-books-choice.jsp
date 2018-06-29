<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Pick habitats, show references' function - Popup for list of values in search page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement,
                ro.finsiel.eunis.jrfTables.habitats.references.HabitatsBooksDomain,
                ro.finsiel.eunis.jrfTables.habitats.references.HabitatsBooksPersist,
                ro.finsiel.eunis.search.Utilities" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="<%=SessionManager.getCurrentLanguage()%>" xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=SessionManager.getCurrentLanguage()%>">
<head>
  <jsp:include page="header-page.jsp" />
  <%
    WebContentManagement cm = SessionManager.getWebContent();
  %>
  <title>
    <%=cm.cmsPhrase("List of values")%>
  </title>
  <script language="JavaScript" type="text/javascript">
  //<![CDATA[
   function setLine(val) {
     window.opener.document.eunis.scientificName.value=val;
     window.close();
   }
  //]]>
  </script>
</head>
<jsp:useBean id="formBean" class="ro.finsiel.eunis.search.habitats.references.ReferencesBean" scope="request">
  <jsp:setProperty name="formBean" property="*"/>
</jsp:useBean>
<%
  // Request parameter
  Integer database = Utilities.checkedStringToInt(request.getParameter("database"), HabitatsBooksDomain.SEARCH_EUNIS);

  HabitatsBooksDomain habitatsBooks = new HabitatsBooksDomain(formBean.toSearchCriteria(), database);
  // Set the result list
  List results = habitatsBooks.getHabitatsWithReferences(true);
%>
<body>
<%
  if(results != null && results.size() > 0) {
    out.print(Utilities.getTextMaxLimitForPopup(cm, results.size()));
  }
%>
<%
  // On can display the title of this popup, if exist results
  if(results != null && results.size() > 0) {
%>
<h2><%=cm.cmsPhrase("List of values for:")%></h2>
<u><%=cm.cmsPhrase("Habitat type name")%></u>
<em><%=Utilities.ReturnStringRelatioOp(Utilities.checkedStringToInt(formBean.getRelationOp(), Utilities.OPERATOR_CONTAINS))%></em>
<strong><%=StringEscapeUtils.escapeXml(formBean.getScientificName())%></strong>
<br />
<br />
<%
  }
  // If exist results, on dispayed it
  if(results != null && results.size() > 0) {
%>
<div id="tab">
  <table summary="<%=cm.cmsPhrase("List of values")%>" border="1" cellpadding="2" cellspacing="0" style="border-collapse: collapse" width="100%">
    <tr>
      <th>
        <%=cm.cmsPhrase("Search results")%>
      </th>
    </tr>
    <%  for(int i = 0; i < results.size(); i++) {
      HabitatsBooksPersist n = (HabitatsBooksPersist) results.get(i);
    %>
    <tr>
      <td bgcolor="<%=(0 == (i % 2)) ? "#EEEEEE" : "#FFFFFF"%>">
        <a title="<%=cm.cmsPhrase("Click link to select the value")%>" href="javascript:setLine('<%=Utilities.treatURLSpecialCharacters(n.getScientificName())%>');"><%=n.getScientificName()%></a>
      </td>
    </tr>
    <%
      }
    %>
  </table>
</div>
<%
} else {
%>
<strong>
  <%=cm.cmsPhrase("No results were found.")%>
</strong>
<br />
<br />
<%
  }
%>
<br />
<form action="">
  <input title="<%=cm.cmsPhrase("Close window")%>" type="button" value="<%=cm.cmsPhrase("Close")%>" onclick="javascript:window.close()" name="button" id="button" class="standardButton" />
</form>
</body>
</html>
