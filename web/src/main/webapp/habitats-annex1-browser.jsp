<%--
  - Author(s)   : The EUNIS Database Team.
  - Date        :
  - Copyright   : (c) 2002-2006 EEA - European Environment Agency.
  - Description : Annex I habitat types tree
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
  String btrail = "eea#" + eeaHome + ",home#index.jsp,habitat_types#habitats.jsp,habitats_annex1_tree_location";
%>
<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cms("habitats_annex1-browser_title") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" pageTitle="${title}" btrail="<%= btrail%>">
    <stripes:layout-component name="head">
        <link rel="StyleSheet" href="css/eunistree.css" type="text/css" />
    </stripes:layout-component>
    <stripes:layout-component name="contents">
        <a name="documentContent"></a>
<!-- MAIN CONTENT -->
        <h1>
          <%=cm.cmsPhrase("Habitat Annex I Directive hierarchical view: (higher levels are for grouping only)")%>
        </h1>

        <br/>
          <%
            String expand = Utilities.formatString( request.getParameter( "expand" ), "" );

            SQLUtilities sqlc = new SQLUtilities();
            sqlc.Init();

            String strSQL = "";

            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            PreparedStatement ps2 = null;
            ResultSet rs2 = null;
            PreparedStatement ps4 = null;
            ResultSet rs4 = null;
            PreparedStatement ps5 = null;
            ResultSet rs5 = null;

            try
            {
              con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

              //we display root nodes
              strSQL = "SELECT ID_HABITAT, SCIENTIFIC_NAME, CODE_2000";
              strSQL = strSQL + " FROM chm62edt_habitat";
              strSQL = strSQL + " WHERE LENGTH(CODE_2000)=1";
              strSQL = strSQL + " AND CODE_2000<>'-'";
              strSQL = strSQL + " ORDER BY CODE_2000 ASC";

              ps = con.prepareStatement( strSQL );
              rs = ps.executeQuery();
              
              String hide = cm.cmsPhrase("Hide sublevel habitat types");
              String show = cm.cmsPhrase("Show sublevel habitat types");

          %>
              <ul class="eunistree">
          <%
              while(rs.next())
              {
          %>
                <li>
                  <% if(Utilities.expandContains(expand,rs.getString("CODE_2000").substring(0,1))){ %>
	                <a title="<%=hide%>" id="level_<%=rs.getString("CODE_2000")%>" href="habitats-annex1-browser.jsp?expand=<%=Utilities.removeFromExpanded(expand,rs.getString("CODE_2000").substring(0,1))%>#level_<%=rs.getString("CODE_2000")%>"><img src="images/img_minus.gif" alt="<%=hide%>"/></a>
                  <% } else { %>
                    <a title="<%=show%>" id="level_<%=rs.getString("CODE_2000")%>" href="habitats-annex1-browser.jsp?expand=<%=Utilities.addToExpanded(expand,rs.getString("CODE_2000").substring(0,1))%>#level_<%=rs.getString("CODE_2000")%>"><img src="images/img_plus.gif" alt="<%=show%>"/></a>
                  <% } %>
                      <%=rs.getString("CODE_2000")%> : <%=rs.getString("SCIENTIFIC_NAME")%>
                    <br/>
                  <%
                  //we begin to display the tree
	              if(expand.length()>0 && Utilities.expandContains(expand,rs.getString("CODE_2000").substring(0,1))) {
	                strSQL = "SELECT ID_HABITAT, SCIENTIFIC_NAME, CODE_2000";
	                strSQL = strSQL + " FROM chm62edt_habitat";
	                strSQL = strSQL + " WHERE CODE_2000 LIKE '"+rs.getString("CODE_2000").substring(0,1)+"%00'";
	                strSQL = strSQL + " ORDER BY CODE_2000 ASC";
	
	                ps2 = con.prepareStatement( strSQL );
	                rs2 = ps2.executeQuery();
	
	%>
	                <ul class="eunistree">
	<%
	                while(rs2.next())
	                {
	%>
	                  <li>
	<%
	                  if(sqlc.Annex1HabitatHasChilds(rs2.getString("CODE_2000").substring(0,rs2.getString("CODE_2000").length()-2),rs2.getString("CODE_2000"))) {
	%>					<% if(Utilities.expandContains(expand,rs2.getString("CODE_2000").substring(0,2))){ %>
		                  <a title="<%=hide%>" id="level_<%=rs2.getString("CODE_2000")%>" href="habitats-annex1-browser.jsp?expand=<%=Utilities.removeFromExpanded(expand,rs2.getString("CODE_2000").substring(0,2))%>#level_<%=rs2.getString("CODE_2000")%>"><img src="images/img_minus.gif" alt="<%=hide%>"/></a>
	                    <% } else { %>
	                      <a title="<%=show%>" id="level_<%=rs2.getString("CODE_2000")%>" href="habitats-annex1-browser.jsp?expand=<%=Utilities.addToExpanded(expand,rs2.getString("CODE_2000").substring(0,2))%>#level_<%=rs2.getString("CODE_2000")%>"><img src="images/img_plus.gif" alt="<%=show%>"/></a>
	                    <% } %>
                            <%=rs2.getString("CODE_2000")%> : <%=rs2.getString("SCIENTIFIC_NAME")%>
                          <br/>
	<%
	                  } else {
	%>
	                    <img src="images/img_bullet.gif" alt="<%=rs2.getString("SCIENTIFIC_NAME")%>"/>&nbsp;<a title="<%=rs2.getString("SCIENTIFIC_NAME")%>" href="habitats/<%=rs2.getString("ID_HABITAT")%>"><%=rs2.getString("CODE_2000")%> : <%=rs2.getString("SCIENTIFIC_NAME")%></a><br/>
	<%
	                  }
	
	                   if(expand.length()>0 && Utilities.expandContains(expand,rs2.getString("CODE_2000").substring(0,2))) {
	                     strSQL = "SELECT ID_HABITAT, SCIENTIFIC_NAME, CODE_2000";
	                     strSQL = strSQL + " FROM chm62edt_habitat";
	                     strSQL = strSQL + " WHERE ( CODE_2000 LIKE '"+rs2.getString("CODE_2000").substring(0,2)+"%0'";
	                     strSQL = strSQL + " or CODE_2000 LIKE '"+rs2.getString("CODE_2000").substring(0,2)+"%A' )";
	                     strSQL = strSQL + " AND CODE_2000 NOT LIKE '"+rs2.getString("CODE_2000").substring(0,2)+"%00'";
						   // orders by code_2000 but first by the last letter of the code (09110, 09120... 091Z0, A91AA ...)
                           // the last letter is not the fourth level, it is used as a continuation of the third level
                           // the order should be 9110, 9120 ... 91Y0, 91Z0, 91AA, 91BA, 91CA
                           // https://taskman.eionet.europa.eu/issues/27925
	                     strSQL = strSQL + " ORDER BY concat(substr(CODE_2000, 4,1), CODE_2000) ASC";
	
	                     ps4 = con.prepareStatement( strSQL );
	                     rs4 = ps4.executeQuery();
	
	%>
	                     <ul class="eunistree">
	<%
	                     while(rs4.next())
	                     {
	%>
	                       <li>

	                         <img src="images/img_bullet.gif" alt="<%=rs4.getString("SCIENTIFIC_NAME")%>"/>&nbsp;<a title="<%=rs4.getString("SCIENTIFIC_NAME")%>" href="habitats/<%=rs4.getString("ID_HABITAT")%>"><%=rs4.getString("CODE_2000")%> : <%=rs4.getString("SCIENTIFIC_NAME")%></a><br/>

	                       </li>
	<%
	                     }
	%>
	                     </ul>
	<%
	
	                     rs4.close();
	                     ps4.close();
	                   }
	%>
	              </li>
	<%
	                }
	%>
	            </ul>
	<%
	
	                rs2.close();
	                ps2.close();
	              }
                  %>
                </li>
          <%
              }
          %>
              </ul>
              <%=cm.cmsTitle("Show sublevel habitat types")%>
              <%=cm.br()%>
              <%=cm.cmsTitle("Hide sublevel habitat types")%>
          <%

              rs.close();
              ps.close();

              out.println("<br/><br/>");

              

              con.close();
            }
            catch ( Exception e )
            {
              e.printStackTrace();
              return;
            }

%>
                <br/>
<!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>