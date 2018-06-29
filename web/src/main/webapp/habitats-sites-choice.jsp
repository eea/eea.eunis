<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Pick sites, show habitats' function - Popup for list of values in search page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement, ro.finsiel.eunis.jrfTables.habitats.sites.HabitatsSitesDomain, ro.finsiel.eunis.search.Utilities,
                ro.finsiel.eunis.search.habitats.sites.SitesSearchCriteria,
                java.util.List,
                java.util.Vector" %>
<%@ page import="ro.finsiel.eunis.search.SourceDb" %>
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
  <%// Get form parameters here%>
  <jsp:useBean id="formBean" class="ro.finsiel.eunis.search.habitats.sites.SitesBean" scope="request">
    <jsp:setProperty name="formBean" property="*"/>
  </jsp:useBean>
  <script language="JavaScript" type="text/javascript">
  //<![CDATA[
    function setLine(val)
    {
      window.opener.document.criteria.scientificName.value=val;
      window.close();
    }
  //]]>
  </script>
  <%
    Integer relationOp = Utilities.checkedStringToInt(formBean.getRelationOp(), Utilities.OPERATOR_CONTAINS);
    Integer searchAttribute = Utilities.checkedStringToInt(formBean.getSearchAttribute(), SitesSearchCriteria.SEARCH_NAME);
    List results = new Vector();
      SourceDb sourceDb = SourceDb.allDatabases();
    Integer database = Utilities.checkedStringToInt(formBean.getDatabase(), HabitatsSitesDomain.SEARCH_EUNIS);
    // List of values (in accordance with searchAttribute)
    results = new HabitatsSitesDomain().findPopupLOV(new SitesSearchCriteria(searchAttribute,
                                                                             formBean.getScientificName(),
                                                                             relationOp),
                                                     sourceDb,
                                                     searchAttribute,
                                                     database);
  %>
</head>

<body>
<%
  if(results != null && results.size() > 0) {
    out.print(Utilities.getTextMaxLimitForPopup(cm, (results == null ? 0 : results.size())));
  }
%>
<%
  if(!results.isEmpty()) {
    SitesSearchCriteria sitesSearch = new SitesSearchCriteria(searchAttribute, formBean.getScientificName(), relationOp);
%>
<h2><%=cm.cmsPhrase("List of values for:")%></h2>
<u><%=sitesSearch.getHumanMappings().get(searchAttribute)%></u>
<%
  if(null != formBean.getScientificName() && null != relationOp) {
%>
<em><%=Utilities.ReturnStringRelatioOp(relationOp)%></em>
<strong><%=StringEscapeUtils.escapeXml(formBean.getScientificName())%></strong>
<%
  }
%>
<br />
<br />

<div id="tab">
  <table summary="<%=cm.cmsPhrase("List of values")%>" border="1" cellpadding="2" cellspacing="0" style="border-collapse: collapse" width="100%">
    <%
      // Display results.
      String rowBgColor = "";
      String value = "";
      for(int i = 0; i < results.size(); i++) {
        rowBgColor = (0 == (i % 2)) ? "#FFFFFF" : "#EEEEEE";
        value = (String) results.get(i);
    %>
    <tr bgcolor="<%=rowBgColor%>">
      <td>
        <a title="<%=cm.cmsPhrase("Click link to select the value")%>" href="javascript:setLine('<%=Utilities.treatURLSpecialCharacters(value)%>');"><%=value%></a>
      </td>
    </tr>
    <%
      }
    %>
  </table>
</div>
  <%--   <%=Utilities.getTextMaxLimitForPopup((results == null ? 0 : results.size()))%>--%>
  <%
  } else {
  %>
  <strong>
    <%=cm.cmsPhrase("No results were found.")%>
  </strong>
  <br />
  <%
    }
  %>
  <br />

  <form action="">
    <input title="<%=cm.cmsPhrase("Close window")%>" type="button" value="<%=cm.cmsPhrase("Close")%>" onclick="javascript:window.close()" id="button" name="button" class="standardButton" />
  </form>
</body>
</html>
