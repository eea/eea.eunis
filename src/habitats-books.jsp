<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Pick habitats, show references' function - search page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement,
                 ro.finsiel.eunis.jrfTables.habitats.references.HabitatsBooksDomain,
                 ro.finsiel.eunis.search.Utilities,
                 ro.finsiel.eunis.search.habitats.references.ReferencesSearchCriteria,
                 java.util.Vector" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="<%=SessionManager.getCurrentLanguage()%>" xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=SessionManager.getCurrentLanguage()%>">
<head>
  <jsp:include page="header-page.jsp" />
  <script language="JavaScript" src="script/habitats-books.js" type="text/javascript"></script>
  <script language="JavaScript" src="script/save-criteria.js" type="text/javascript"></script>
  <script language="JavaScript" src="script/overlib.js" type="text/javascript"></script>
  <%
    WebContentManagement cm = SessionManager.getWebContent();
  %>
  <title>
    <%=application.getInitParameter("PAGE_TITLE")%>
    <%=cm.cms("habitats_books_title")%>
  </title>
</head>

<body>
  <div id="outline">
  <div id="alignment">
  <div id="content">
<jsp:include page="header-dynamic.jsp">
  <jsp:param name="location" value="home_location#index.jsp,habitats_location#habitats.jsp,pick_habitat_show_references_location" />
  <jsp:param name="helpLink" value="habitats-help.jsp" />
</jsp:include>
<div id="overDiv" style="z-index: 1000; visibility: hidden; position: absolute"></div>
<form name="eunis" method="get" onsubmit="javascript: return validateForm();" action="habitats-books-result.jsp">
<input type="hidden" name="typeForm" value="<%=ReferencesSearchCriteria.CRITERIA_SCIENTIFIC%>" />
<table summary="Main content" width="100%" border="0">
<tr>
  <td>
    <table width="100%" border="0" summary="layout">
        <tr>
          <td>
            <h1>
              <%=cm.cmsText("habitats_books_01")%>
            </h1>
            <%=cm.cmsText("habitats_books_20")%>
            <br />
            <br />
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td bgcolor="#EEEEEE">
                  <strong>
                    <%=cm.cmsText("habitats_books_02")%>
                  </strong>
                </td>
              </tr>
              <tr>
                <td bgcolor="#EEEEEE">
                  <input type="checkbox" name="showAuthor" id="showAuthor" value="true" checked="checked" disabled="disabled" />
                  <label for="showAuthor"><%=cm.cmsText("habitats_books_03")%></label>
                  &nbsp;
                  <input type="checkbox" name="showDate" id="showDate" value="true" checked="checked" disabled="disabled" />
                  <label for="showDate"><%=cm.cmsText("habitats_books_04")%></label>
                  &nbsp;
                  <input type="checkbox" name="showTitle" id="showTitle" value="true" checked="checked" disabled="disabled" />
                  <label for="showTitle"><%=cm.cmsText("habitats_books_05")%></label>
                  &nbsp;
                  <input type="checkbox" name="showEditor" id="showEditor" value="true" checked="checked" disabled="disabled" />
                  <label for="showEditor"><%=cm.cmsText("habitats_books_06")%></label>
                  &nbsp;
                  <input type="checkbox" name="showPublisher" id="showPublisher" value="true" checked="checked" disabled="disabled" />
                  <label for="showPublisher"><%=cm.cmsText("habitats_books_07")%></label>
                  &nbsp;
                  <input type="checkbox" name="showSourceType" id="showSourceType" value="true" checked="checked" disabled="disabled" />
                  <label for="showSourceType"><%=cm.cmsText("habitats_books_source")%></label>
                  &nbsp;
                  <input type="checkbox" name="showHabitatTypes" id="showHabitatTypes" value="true" checked="checked" disabled="disabled" />
                  <label for="showHabitatTypes"><%=cm.cmsText("habitats_books_habitats")%></label>
                  &nbsp;
                </td>
              </tr>
            </table>
            <br />
          </td>
        </tr>
        <tr>
          <td>
            <img alt="Mandatory" src="images/mini/field_mandatory.gif" align="middle" />
            <label for="scientificName"><strong><%=cm.cmsText("habitats_books_08")%></strong></label>
            <label for="relationOp" class="noshow"><%=cm.cms("operator")%></label>
            <select name="relationOp" id="relationOp" class="inputTextField" title="Operator">
              <option value="<%=Utilities.OPERATOR_IS%>"><%=cm.cms("habitats_books_09")%></option>
              <option value="<%=Utilities.OPERATOR_CONTAINS%>"><%=cm.cms("habitats_books_10")%></option>
              <option value="<%=Utilities.OPERATOR_STARTS%>" selected="selected"><%=cm.cms("habitats_books_11")%></option>
            </select>
            <%=cm.cmsLabel("operator")%>
            <%=cm.cmsInput("habitats_books_09")%>
            <%=cm.cmsInput("habitats_books_10")%>
            <%=cm.cmsInput("habitats_books_11")%>
            <label for="scientificName" class="noshow"><%=cm.cms("list_of_values")%></label>
            <input  align="middle" size="32" name="scientificName" id="scientificName" value="" class="inputTextField" title="Name" />
            <%=cm.cmsLabel("list_of_values")%>
            <a title="<%=cm.cms("list_of_values")%>" href="javascript:openHelper('habitats-books-choice.jsp?')"><img align="middle" height="18" alt="<%=cm.cms("habitats_books_12")%>" src="images/helper/helper.gif" width="11" border="0" /></a>
            <%=cm.cmsTitle("habitats_books_12")%>
            &nbsp;&nbsp;&nbsp;&nbsp;
          </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr>
          <td bgcolor="#EEEEEE">
            <%=cm.cmsText("habitats_books_13")%>:&nbsp;
            <input type="radio" name="database" id="database1" value="<%=HabitatsBooksDomain.SEARCH_EUNIS%>" checked="checked" title="<%=cm.cms("search_eunis")%>" />
            <%=cm.cmsTitle("search_eunis")%>
            <label for="database1"><%=cm.cmsText("habitats_books_14")%></label>
            &nbsp;&nbsp;
            <input type="radio" name="database" id="database2" value="<%=HabitatsBooksDomain.SEARCH_ANNEX_I%>" title="<%=cm.cms("search_annex1")%>" />
            <%=cm.cmsTitle("search_annex1")%>
            <label for="database2"><%=cm.cmsText("habitats_books_15")%></label>
            &nbsp;&nbsp;
            <input type="radio" name="database" id="database3" value="<%=HabitatsBooksDomain.SEARCH_BOTH%>" title="<%=cm.cms("search_both")%>" />
            <%=cm.cmsTitle("search_both")%>
            <label for="database3"><%=cm.cmsText("habitats_books_16")%></label>
          </td>
        </tr>
        <tr>
          <td align="right">
            <br />
            <input type="reset" title="Reset fields" value="<%=cm.cms("habitats_books_17")%>" name="Reset" id="Reset" class="inputTextField" />
            <%=cm.cmsTitle("reset_values")%>
            <%=cm.cmsInput("habitats_books_17")%>
            <input type="submit" title="<%=cm.cms("search_btn")%>" value="<%=cm.cms("habitats_books_18")%>" name="submit2" id="submit2" class="inputTextField" />
            <%=cm.cmsTitle("search_btn")%>
            <%=cm.cmsInput("habitats_books_18")%>
          </td>
        </tr>
    </table>
  </td>
</tr>
</table>
</form>

<%
  // Save search criteria
  if (SessionManager.isAuthenticated() && SessionManager.isSave_search_criteria_RIGHT()) {
%>
  <br />
    &nbsp;
  <script type="text/javascript" language="JavaScript">
  <!--
  // values of this constants from specific class Domain
  var source1='';
  var source2='';
  var database1='<%=HabitatsBooksDomain.SEARCH_EUNIS%>';
  var database2='<%=HabitatsBooksDomain.SEARCH_ANNEX_I%>';
  var database3='<%=HabitatsBooksDomain.SEARCH_BOTH%>';
  //-->
  </script>
<script language="JavaScript" src="script/habitats-books-save-criteria.js" type="text/javascript"></script>
    <%=cm.cmsText("habitats_books_19")%>:
    <a title="<%=cm.cms("save_criteria")%>" href="javascript:composeParameterListForSaveCriteria('<%=request.getParameter("expandSearchCriteria")%>',validateForm(),'habitats-books.jsp','2','eunis',attributesNames,formFieldAttributes,operators,formFieldOperators,booleans,'save-criteria-search.jsp');"><img alt="<%=cm.cms("save_criteria")%>" border="0" src="images/save.jpg" width="21" height="19" align="middle" /></a>
    <%=cm.cmsTitle("save_criteria")%>
<%
  // Set Vector for URL string
  Vector show = new Vector();
  String pageName = "habitats-books.jsp";
  String pageNameResult = "habitats-books-result.jsp?" + Utilities.writeURLCriteriaSave(show);
  // Expand or not save criterias list
  String expandSearchCriteria = (request.getParameter("expandSearchCriteria") == null ? "no" : request.getParameter("expandSearchCriteria"));
%>
    <jsp:include page="show-criteria-search.jsp">
      <jsp:param name="pageName" value="<%=pageName%>" />
      <jsp:param name="pageNameResult" value="<%=pageNameResult%>" />
      <jsp:param name="expandSearchCriteria" value="<%=expandSearchCriteria%>" />
    </jsp:include>
<%
  }
%>
<%=cm.br()%>
<%=cm.cmsMsg("habitats_books_title")%>
<%=cm.br()%>
<jsp:include page="footer.jsp">
  <jsp:param name="page_name" value="habitats-books.jsp" />
</jsp:include>
  </div>
  </div>
  </div>
</body>
</html>