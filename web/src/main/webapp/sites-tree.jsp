<%--
  - Author(s)   : The EUNIS Database Team.
  - Date        :
  - Copyright   : (c) 2002-2006 EEA - European Environment Agency.
  - Description : Sites tree
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<%
  request.setCharacterEncoding( "UTF-8");  
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement"%>
<%@ page import="ro.finsiel.eunis.search.Utilities"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.sql.DriverManager"%>
<%@ page import="ro.finsiel.eunis.utilities.SQLUtilities"%>
<%@ page import="ro.finsiel.eunis.search.sites.SitesSearchUtility"%>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<%
  WebContentManagement cm = SessionManager.getWebContent();
  String eeaHome = application.getInitParameter( "EEA_HOME" );
  String btrail = "eea#" + eeaHome + ",home#index.jsp,sites#sites.jsp,sites_tree_browser";
%>
<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cmsPhrase("Sites tree browser") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" helpLink="help.jsp" pageTitle="${title}" btrail="<%= btrail%>">
    <stripes:layout-component name="head">
    </stripes:layout-component>
    <stripes:layout-component name="contents">
        <a name="documentContent"></a>
        <h1>
          <%=cm.cmsPhrase("Sites browser")%>
        </h1>

<!-- MAIN CONTENT -->
            <p class="documentDescription">
            <%=cm.cmsPhrase("Sites grouped by description")%>
            </p>
          <%
            String idSource = Utilities.formatString( request.getParameter( "idSource" ), "" );
            String idDesignation = Utilities.formatString( request.getParameter( "idDesignation" ), "" );
            String idGeoscope = Utilities.formatString( request.getParameter( "idGeoscope" ), "" );

            SQLUtilities sqlc = new SQLUtilities();
            sqlc.Init();

            String strSQL = "";

            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            PreparedStatement ps2 = null;
            ResultSet rs2 = null;
            PreparedStatement ps3 = null;
            ResultSet rs3 = null;

            strSQL = "SELECT ID_DESIGNATION, chm62edt_designations.ID_GEOSCOPE, DESCRIPTION, AREA_NAME_EN";
            strSQL = strSQL + " FROM chm62edt_designations, chm62edt_country";
            strSQL = strSQL + " WHERE chm62edt_designations.ID_GEOSCOPE = chm62edt_country.ID_GEOSCOPE";
            strSQL = strSQL + " AND LENGTH(DESCRIPTION)>0";
            strSQL = strSQL + " ORDER BY DESCRIPTION ASC, AREA_NAME_EN ASC";

            try
            {
              con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

              ps = con.prepareStatement( strSQL );
              rs = ps.executeQuery();
          %>
              <ul>
          <%
                while ( rs.next() )
                {
          %>
                <li>
                <a href="designations/<%=rs.getString("ID_GEOSCOPE")%>:<%=rs.getString("ID_DESIGNATION")%>#position"><%=rs.getString("DESCRIPTION")%></a> <%=rs.getString("AREA_NAME_EN")%> (<%=rs.getString("ID_DESIGNATION")%>)
                </li>
<%
                }
%>
              </ul>
          <%
            }
            catch ( Exception e )
            {

              return;
            } finally {
              SQLUtilities.closeAll(con, ps, rs);
            }

          %>
<!-- END MAIN CONTENT -->

    </stripes:layout-component>
</stripes:layout-render>
