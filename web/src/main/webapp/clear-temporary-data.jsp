<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Clear temporary data from MySQL' function
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="java.sql.Connection,
                 java.sql.PreparedStatement,
                 java.sql.DriverManager,
                 java.sql.ResultSet,
                 ro.finsiel.eunis.WebContentManagement" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />

<%
  WebContentManagement cm = SessionManager.getWebContent();
  String datacount = "0";
  String eeaHome = application.getInitParameter( "EEA_HOME" );
  String btrail = "eea#" + eeaHome + ",home#index.jsp,services#services.jsp,clear_temporary_data_btn";
%>

<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cmsPhrase("Clear temporary data") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" pageTitle="${title}" btrail="<%= btrail%>">
    <stripes:layout-component name="head">

    </stripes:layout-component>
    <stripes:layout-component name="contents">
                <a name="documentContent"></a>

<!-- MAIN CONTENT -->
<%
  // Check if user has Admin right
  if(!SessionManager.isAdmin())
  {
%>
                <h1>
                  <%=cm.cmsPhrase( "Clear temporary data" )%>
                </h1>
		<div class="error-msg">
                <%=cm.cmsPhrase("You do not have Administrator rights, which are required to perform this operation.<br />Please login as Administrator first.")%>
		</div>
                <%=cm.cmsMsg("clear_temporary_data_btn")%>
<%
  }
  else
  {
%>
                <%=cm.cmsText("generic_clear-temporary-data_02")%>
                <br />
                <br />
                <form name="clearlog" method="post" action="clear-temporary-data.jsp">
                  <input type="submit" value="<%=cm.cmsPhrase("Clear temporary data")%>" title="<%=cm.cmsPhrase("Clear temporary data")%>" id="submit" name="submit" class="submitSearchButton" />
                </form>
<%
    if(request.getParameter("submit") != null)
    {
      if(request.getParameter("submit").equalsIgnoreCase(cm.cmsPhrase("Clear temporary data")))
      {
        boolean result;
        try
        {
          // Delete all session data
          result = ro.finsiel.eunis.search.Utilities.ClearAllSessionsData();
          if(!result)
          {
%>
                <%=cm.cmsPhrase("The temporary data could not be deleted.")%>
                <br />
<%
          }
          else
          {
%>
                <%=cm.cmsPhrase("The temporary data has been deleted.")%>
                <br />
<%
          }
        }
        catch(Exception e)
        {

          return;
        }
      }
    }
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    try
    {
      con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

      String SQL;
      // Find total number of rows containing temporary data detected in database
      SQL = "SELECT COUNT(*) FROM eunis_advanced_search_results ";
      SQL += " UNION ";
      SQL += "SELECT COUNT(*) FROM eunis_advanced_search_results ";
      SQL += " UNION ";
      SQL += "SELECT COUNT(*) FROM eunis_advanced_search_temp ";
      SQL += " UNION ";
      SQL += "SELECT COUNT(*) FROM eunis_advanced_search_criteria ";
      SQL += " UNION ";
      SQL += "SELECT COUNT(*) FROM eunis_advanced_search_criteria_temp ";
      SQL += " UNION ";
      SQL += "SELECT COUNT(*) FROM eunis_combined_search_results ";
      SQL += " UNION ";
      SQL += "SELECT COUNT(*) FROM eunis_combined_search_results ";
      SQL += " UNION ";
      SQL += "SELECT COUNT(*) FROM eunis_combined_search_temp ";
      SQL += " UNION ";
      SQL += "SELECT COUNT(*) FROM eunis_combined_search_criteria ";
      SQL += " UNION ";
      SQL += "SELECT COUNT(*) FROM eunis_combined_search_criteria_temp ";

      ps = con.prepareStatement(SQL);
      rs = ps.executeQuery();
      if(rs.next())
      {
        datacount = rs.getString(1);
      }
      rs.close();
      ps.close();
      con.close();
    }
    catch(Exception e)
    {

      return;
    }
%>
                <br />
                <%=cm.cmsPhrase("Total number of rows containing temporary data detected in the database:")%>&nbsp;<strong><%=datacount%></strong>
                <br />
                <%=cm.cmsMsg("clear_temporary_data_btn")%>
<%
  }
%>
<!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>