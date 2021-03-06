<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : In this page are saved easy search criteria .
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@page import="java.util.Vector,
                ro.finsiel.eunis.search.*,
                ro.finsiel.eunis.search.save_criteria.GroupsFromRequest,
                ro.finsiel.eunis.search.save_criteria.SaveSearchCriteria,
                ro.finsiel.eunis.WebContentManagement"%>
<%@ page import="java.util.Enumeration"%>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
  request.setCharacterEncoding( "UTF-8" );
%>
<html lang="<%=SessionManager.getCurrentLanguage()%>" xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=SessionManager.getCurrentLanguage()%>">
  <head>
    <jsp:include page="header-page.jsp" />
<%
  WebContentManagement cm = SessionManager.getWebContent();
%>
    <title>
      <%=cm.cmsPhrase("Save criteria")%>
    </title>
    <script language="JavaScript" src="<%=request.getContextPath()%>/script/header.js" type="text/javascript"></script>
    <script language="JavaScript" type="text/javascript">
      //<![CDATA[
        function closeWindow(where,exp)
        {
          window.opener.location.href=where+'?expandSearchCriteria='+exp;
          self.close();
        }
      //]]>
    </script>
  </head>
<%

  // Number of the form elements used in this save search criteria
  int number = (request.getParameter("numberCriteria") == null ? 0 : Utilities.checkedStringToInt(request.getParameter("numberCriteria"),0));

  // Save operation was made with success or not
  boolean saveWithSuccess = false;

  // If it was selected the save button.
  if (request.getParameter("saveCriteria") != null
        && request.getParameter("saveCriteria").equals("true")
        && request.getParameter("Reset2")==null)
  {
    // Get parameters from request
    GroupsFromRequest requestParser = new GroupsFromRequest(request);

    String description = (request.getParameter("description") == null ? "" : request.getParameter("description"));
    // pageName - in witch jsp page the save is done.
    String pageName = (request.getParameter("pageName") == null ? "" : request.getParameter("pageName"));

    // values of this constants from specific class Domain
    Vector database = new Vector();
    database.add((request.getParameter("database1") == null ? "" : request.getParameter("database1"))); //eunis
    database.add((request.getParameter("database2") == null ? "" : request.getParameter("database2"))); //annex
    database.add((request.getParameter("database3") == null ? "" : request.getParameter("database3"))); //both


    // Save search criteria
    SaveSearchCriteria sal = new SaveSearchCriteria(database,
                                                  number,
                                                  SessionManager.getUsername(),
                                                  description,
                                                  pageName,
                                                  requestParser.getAttributes(),
                                                  requestParser.getFormFieldAttributes(),
                                                  requestParser.getFormFieldOperators(),
                                                  requestParser.getBooleans(),
                                                  requestParser.getOperators(),
                                                  requestParser.getFirstValues(),
                                                  requestParser.getLastValues()
    );
    saveWithSuccess = sal.SaveCriterias();
}

String descr = (request.getParameter("description") == null ? "" : request.getParameter("description"));
%>
  <body>
      <form name="eunis" method="post" action="save-criteria-search.jsp">
        <input type="hidden" name="saveCriteria" value="true" />
        <input type="hidden" name="numberCriteria" value="<%=java.net.URLDecoder.decode( Utilities.formatString( request.getParameter("numberCriteria") ), "UTF-8" )%>" />
        <input type="hidden" name="pageName" value="<%=java.net.URLDecoder.decode( Utilities.formatString( request.getParameter("pageName") ), "UTF-8" )%>" />
        <input type="hidden" name="expandSearchCriteria" value="<%=java.net.URLDecoder.decode( Utilities.formatString( request.getParameter("expandSearchCriteria") ), "UTF-8" )%>" />
        <input type="hidden" name="database1" value="<%=java.net.URLDecoder.decode( Utilities.formatString( request.getParameter("database1") ), "UTF-8" )%>" />
        <input type="hidden" name="database2" value="<%=java.net.URLDecoder.decode( Utilities.formatString( request.getParameter("database2") ), "UTF-8" )%>" />
        <input type="hidden" name="database3" value="<%=java.net.URLDecoder.decode( Utilities.formatString( request.getParameter("database3") ), "UTF-8" )%>" />
        <%
          for (int i=0;i<number;i++)
          {
        %>
          <input type="hidden" name="_<%=i%>attributesNames" value="<%=java.net.URLDecoder.decode( Utilities.formatString( request.getParameter("_"+i+"attributesNames") ), "UTF-8" )%>" />
          <input type="hidden" name="_<%=i%>formFieldAttributes" value="<%=java.net.URLDecoder.decode( Utilities.formatString( request.getParameter("_"+i+"formFieldAttributes") ), "UTF-8" )%>" />
          <input type="hidden" name="_<%=i%>formFieldOperators" value="<%=java.net.URLDecoder.decode( Utilities.formatString( request.getParameter("_"+i+"formFieldOperators") ), "UTF-8" )%>" />
          <input type="hidden" name="_<%=i%>booleans" value="<%=java.net.URLDecoder.decode( Utilities.formatString( request.getParameter("_"+i+"booleans") ), "UTF-8" )%>" />
          <input type="hidden" name="_<%=i%>operators" value="<%=java.net.URLDecoder.decode( Utilities.formatString( request.getParameter("_"+i+"operators") ), "UTF-8" )%>" />
          <input type="hidden" name="_<%=i%>firstValues" value="<%=Utilities.treatURLSpecialCharacters( java.net.URLDecoder.decode( Utilities.formatString( request.getParameter("_"+i+"firstValues") ), "UTF-8" ) )%>" />
          <input type="hidden" name="_<%=i%>lastValues" value="<%=Utilities.treatURLSpecialCharacters( java.net.URLDecoder.decode( Utilities.formatString( request.getParameter("_"+i+"lastValues") ), "UTF-8" ) )%>" />
        <%
          }
%>
        <table width="356" border="0" cellspacing="0" cellpadding="0">
<%
          if (null==request.getParameter("description"))
          {
%>
          <tr bgcolor="#EEEEEE">
            <td>
              <strong>
                <%=cm.cmsPhrase("Save search criteria:")%>
              </strong>
            </td>
          </tr>
          <tr>
            <td>
              <%=cm.cmsPhrase("Please enter few words to describe this criteria for later reference")%>
            </td>
          </tr>
          <tr>
            <td>
              <label for="description" class="noshow"><%=cm.cms("description")%></label>
              <textarea id="description" name="description" cols="70" rows="5" style="width : 300px; height: 80px;"><%=descr%></textarea>
              <%=cm.cmsLabel("description")%>
            </td>
          </tr>
          <tr>
            <td>
              <img title="<%=cm.cms("bullet_alt")%>" alt="<%=cm.cms("bullet_alt")%>" src="images/mini/bulletb.gif" width="6" height="6" style="vertical-align:middle" />
              <%=cm.cmsAlt("bullet_alt")%>
              <%=cm.cmsTitle("bullet_alt")%>
              &nbsp;
              <strong>
                <%=cm.cmsPhrase("Remark:")%>
              </strong>
              <%=cm.cmsPhrase("By leaving this field empty, a default description (based on your selections) will be associated with this criteria.")%>
            </td>
          </tr>
<%
  }
  else
  {
    String saveOperationResult = "";
    if ( saveWithSuccess )
    {
      saveOperationResult = cm.cmsPhrase("Your search criteria was saved in database.");
    }
    else
    {
      saveOperationResult = cm.cmsPhrase("Your search criterion wasn''t saved in database. Please try again!");
    }
%>
          <tr>
            <td>
              <strong><%=saveOperationResult%></strong>
            </td>
          </tr>
<%
  }
%>
        <tr>
          <td align="right">
            <br />
<%
  if (null==request.getParameter("description"))
  {
%>
            <input type="submit" id="submit" name="Submit" title="<%=cm.cmsPhrase("Save")%>" value="<%=cm.cmsPhrase("Save")%>" class="submitSearchButton" />

            <input type="reset" id="reset" name="Reset" title="<%=cm.cmsPhrase("Reset values")%>" value="<%=cm.cmsPhrase("Reset")%>" class="standardButton" />
<%
  }
%>
            <input type="button" id="close_window" name="Close" title="<%=cm.cmsPhrase("Close window")%>" value="<%=cm.cmsPhrase("Close")%>" onclick="javascript:closeWindow('<%=request.getParameter("pageName")%>','<%=request.getParameter("expandSearchCriteria")%>')" class="standardButton" />
          </td>
        </tr>
      </table>
    </form>
    <%=cm.cmsMsg("save_criteria")%>
  </body>
</html>
