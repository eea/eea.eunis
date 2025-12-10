<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Pick species, show references' function - Popup for list of values in search page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@page import="java.util.List,
                ro.finsiel.eunis.search.Utilities,
                ro.finsiel.eunis.WebContentManagement"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="ro.finsiel.eunis.jrfTables.species.references.SpeciesBooksDomain"%>
<%@page import="ro.finsiel.eunis.jrfTables.species.references.SpeciesBooksPersist"%><%@ page import="java.util.ArrayList"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
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
      function setLine(val)
      {
        window.opener.document.eunis.scientificName.value=val;
        window.close();
      }
    //]]>
    </script>
  </head>
  <%// Get form parameters here%>
  <jsp:useBean id="formBean" class="ro.finsiel.eunis.search.species.references.ReferencesBean" scope="request">
    <jsp:setProperty name="formBean" property="*"/>
  </jsp:useBean>
  <%
    // List of species scientific names
    SpeciesBooksDomain domain = new SpeciesBooksDomain(formBean.toSearchCriteria(),SessionManager.getShowEUNISInvalidatedSpecies());
    // ID_DC value is NULL here
    List results = new ArrayList();
    try
    {
      results = domain.getSpeciesForAReference(null);
    } catch(Exception e) { }
  %>
  <body style="background-color:#ffffff">
  <%     
      if (results != null && results.size() > 0)
      {
        out.print(Utilities.getTextMaxLimitForPopup(cm,results.size()));
      }
    %>
<%
  // On can display the title of this popup, if exist results
  if (results != null && results.size() > 0) {
%>
<%=cm.cmsPhrase("List of values for")%>:
<u><%=cm.cmsPhrase("Species scientific name")%></u>
<em><%=Utilities.ReturnStringRelatioOp(Utilities.checkedStringToInt(formBean.getRelationOp(), Utilities.OPERATOR_CONTAINS))%></em>
<strong><%=StringEscapeUtils.escapeXmlformBean.getScientificName())%></strong>
<br />
<br />
<%
  }
  if (results != null && results.size() > 0)
    {
  %>
      <div id="tab">
      <table summary="<%=cm.cms("list_species")%>" border="1" cellpadding="2" cellspacing="0" style="border-collapse: collapse; border-color:#111111" width="100%">
      <%
        // Display results
        for(int i = 0; i < results.size(); i++)
        {
          SpeciesBooksPersist specie = (SpeciesBooksPersist) results.get(i);
      %>
          <tr>
            <td style="background-color:<%=(0 == (i % 2)) ? "#EEEEEE" : "#FFFFFF"%>">
              <a title="<%=cm.cms("species_books-choice_06_Title")%>" href="javascript:setLine('<%=Utilities.treatURLSpecialCharacters(specie.getName())%>');"><%=specie.getName()%></a>
              <%=cm.cmsTitle("species_books-choice_06_Title")%>
            </td>
          </tr>
      <%
        }
      %>
      </table>
      </div>
      <%
         //out.print(Utilities.getTextMaxLimitForPopup((results == null ? 0 : results.size())));
        } else
          {
      %>
          <strong><%=cm.cmsPhrase("No results were found")%>.</strong>
          <br />
      <%
          }
      %>
    <br />
    <form action="">
      <input id="button1" title="<%=cm.cmsPhrase("Close window")%>" type="button" value="<%=cm.cmsPhrase("Close")%>" onclick="javascript:window.close()" name="button" class="standardButton" />
    </form>
<%=cm.br()%>
<%=cm.cmsMsg("list_species")%>
<%=cm.br()%>
  </body>
</html>
