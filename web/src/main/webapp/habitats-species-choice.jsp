<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Pick species, show habitat types' function - Popup for list of values in search page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement,
                ro.finsiel.eunis.jrfTables.habitats.species.ScientificNameDomain, ro.finsiel.eunis.search.Utilities,
                ro.finsiel.eunis.search.habitats.species.SpeciesSearchCriteria,
                java.util.List,
                java.util.Vector" %>
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
  <jsp:useBean id="formBean" class="ro.finsiel.eunis.search.habitats.species.SpeciesBean" scope="request">
    <jsp:setProperty name="formBean" property="*"/>
  </jsp:useBean>
  <script language="JavaScript" type="text/javascript">
  //<![CDATA[
   function setLine(val)
   {
     window.opener.document.eunis.scientificName.value=val;
     window.close();
   }
 //]]>
  </script>
  <%
    Integer relationOp = Utilities.checkedStringToInt(formBean.getRelationOp(), Utilities.OPERATOR_CONTAINS);
    Integer database = Utilities.checkedStringToInt(formBean.getDatabase(), ScientificNameDomain.SEARCH_EUNIS);
    Integer searchAttribute = Utilities.checkedStringToInt(formBean.getSearchAttribute(), SpeciesSearchCriteria.SEARCH_SCIENTIFIC_NAME);
    List results = new Vector();
    // List of values (in accordance with searchAttribute)
    results = new ScientificNameDomain().findPopupLOV(new SpeciesSearchCriteria(searchAttribute,
                                                                                formBean.getScientificName(),
                                                                                relationOp),
                                                      database,
                                                      SessionManager.getShowEUNISInvalidatedSpecies(),
                                                      searchAttribute);
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
      SpeciesSearchCriteria speciesSearch = new SpeciesSearchCriteria(searchAttribute, formBean.getScientificName(), relationOp);
  %>
  <h2>List of values for:</h2>
  <u><%=speciesSearch.getHumanMappings().get(searchAttribute)%></u>
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
        String rowBgColor = "";
        String value = "";
        // Display results.
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
  <%
    if(searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_GROUP.intValue()) {
      out.print(Utilities.getTextWarningForPopup((results == null ? 0 : results.size())));
    } else {
      //out.print(Utilities.getTextMaxLimitForPopup((results == null ? 0 : results.size())));
    }
  } else {
  %>
  <strong>
    <%=cm.cmsPhrase("No results were found")%>
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
