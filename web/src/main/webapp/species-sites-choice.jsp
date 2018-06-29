<%--
  - Author(s)   : The EUNIS Database Team.
  - Date        :
  - Copyright   : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Pick sites, show species' function - Popup for list of values in search page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@page import="java.util.List, java.util.Iterator, java.util.Vector,
                ro.finsiel.eunis.search.species.sites.SitesPaginator,
                java.util.Enumeration,
                ro.finsiel.eunis.search.Utilities,
                ro.finsiel.eunis.jrfTables.species.sites.SpeciesSitesPersist,
                ro.finsiel.eunis.search.species.sites.SitesSearchCriteria,
                ro.finsiel.eunis.jrfTables.species.sites.SpeciesSitesDomain,
                ro.finsiel.eunis.WebContentManagement"%>
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
    <jsp:useBean id="formBean" class="ro.finsiel.eunis.search.species.sites.SitesBean" scope="request">
      <jsp:setProperty name="formBean" property="*"/>
    </jsp:useBean>
  <script language="JavaScript" type="text/javascript">
  //<![CDATA[
    function setLine(val) {
      window.opener.document.criteria.scientificName.value=val;
      window.close();
    }
  //]]>
  </script>
  <%
    Integer relationOp = Utilities.checkedStringToInt(formBean.getRelationOp(), Utilities.OPERATOR_CONTAINS);
    Integer searchAttribute = Utilities.checkedStringToInt(formBean.getSearchAttribute(), SitesSearchCriteria.SEARCH_NAME);
    List results = new Vector();

    // List of values (in accordance with searchAttribute)
    results = new SpeciesSitesDomain().findPopupLOV(new SitesSearchCriteria(searchAttribute,
                                                                            formBean.getScientificName(),
                                                                            relationOp),
                                                       SourceDb.allDatabases(),
                                                       searchAttribute,
                                                       SessionManager.getShowEUNISInvalidatedSpecies());
  %>
  </head>
  <body style="background-color:#FFFFFF">
  <%
      if (results != null && results.size() > 0)
      {
        out.print(Utilities.getTextMaxLimitForPopup(cm,(results == null ? 0 : results.size())));
      }
    %>
      <%
        if (results != null && !results.isEmpty())
        {
          SitesSearchCriteria sitesSearch = new SitesSearchCriteria(searchAttribute,formBean.getScientificName(),relationOp);
      %>
          <h2><%=cm.cmsPhrase("List of values for:")%></h2>
          <u><%=sitesSearch.getHumanMappings().get(searchAttribute)%></u>
      <%
          if (null != formBean.getScientificName() && null != relationOp)
          {
      %>
          <em>
            <%=Utilities.ReturnStringRelatioOp(relationOp)%>
          </em>
          <strong>
            <%=StringEscapeUtils.escapeXml(formBean.getScientificName())%>
          </strong>
      <%
          }
      %>
        <br />
        <br />
        <div id="tab">
        <table summary="<%=cm.cmsPhrase("List of values")%>" class="datatable">
        <%
          String value = "";
          // Display results.
          for(int i =0;i < results.size(); i++)
          {
              String cssClass = i % 2 == 0 ? "" : " class=\"zebraeven\"";
              value = (String)results.get(i);
        %>
            <tr<%=cssClass%>>
              <td>
                <a title="<%=cm.cms("choose_this_value")%>" href="javascript:setLine('<%=Utilities.treatURLSpecialCharacters(value)%>');"><%=value%></a>
                <%=cm.cmsTitle("choose_this_value")%>
              </td>
            </tr>
        <%
          }
        %>
        </table>
        </div>
<%
          if (searchAttribute.intValue() == SitesSearchCriteria.SEARCH_COUNTRY.intValue()
           || searchAttribute.intValue() == SitesSearchCriteria.SEARCH_REGION.intValue())
          {
            out.print(Utilities.getTextWarningForPopup((results == null ? 0 : results.size())));
          }
          else
          {
           // out.print(Utilities.getTextMaxLimitForPopup((results == null ? 0 : results.size())));
          }
        }
        else
        {
 %>
        <strong>
          <%=cm.cmsPhrase("No results were found")%>.
        </strong>
        <br />
     <%
        }
     %>
      <br />
      <form action="">
        <input id="button" title="<%=cm.cmsPhrase("Close window")%>" type="button" value="<%=cm.cmsPhrase("Close")%>" onclick="javascript:window.close()" name="button" class="standardButton" />
      </form>
  </body>
</html>
