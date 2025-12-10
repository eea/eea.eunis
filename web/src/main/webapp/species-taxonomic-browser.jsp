<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : Display tree of the species taxonomy.
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

<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<%
    WebContentManagement cm = SessionManager.getWebContent();
    String eeaHome = application.getInitParameter( "EEA_HOME" );
    String btrail = "eea#" + eeaHome + ",home#index.jsp,species#species.jsp,taxonomic_classification#species-taxonomic-browser.jsp";
%>

<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cms("taxonomic_classification") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" helpLink="species-help.jsp" pageTitle="${title}" btrail="<%= btrail%>">
    <stripes:layout-component name="head">
        <link rel="StyleSheet" href="css/eunistree.css" type="text/css" />
        <script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/script/tree.js"></script>
        <script language="JavaScript" src="<%=request.getContextPath()%>/script/sortable.js" type="text/javascript"></script>
    </stripes:layout-component>
    <stripes:layout-component name="contents">
        <a name="documentContent"></a>
        <h1>
          <%=cm.cmsPhrase("Species taxonomic classification")%>
        </h1>
        <%=cm.cmsPhrase("This is not a comprehensive classification. It only reflects the species included on this information system")%>

<!-- MAIN CONTENT -->
                <%
	            String expand = Utilities.formatString( request.getParameter( "expand" ), "" );
	            String genus = Utilities.formatString( request.getParameter( "genus" ), "" );

	            SQLUtilities sqlc = new SQLUtilities();
        		sqlc.Init();

        		Connection con = null;
	            
	            try
            	{
              		con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
	            %>
	
	            	<%=Utilities.generateSpeciesTaxonomicTree("", expand, genus, true, con, sqlc, cm)%>
	            	<%=cm.cmsTitle("Show sublevels")%>
                	<%=cm.br()%>
                	<%=cm.cmsTitle("Hide sublevels")%>
	            
	            <%
            	}
            	catch ( Exception e )
            	{

              		return;
            	} finally {
                    SQLUtilities.closeAll(con, null, null);
                }
	            %>

<!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>

