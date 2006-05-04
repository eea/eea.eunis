<%--
  - Author(s)   : The EUNIS Database Team.
  - Date        :
  - Copyright   : (c) 2002-2006 EEA - European Environment Agency.
  - Description : Annex I habitat types tree
--%>
<%@page contentType="text/html;charset=UTF-8"%>
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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<%
  WebContentManagement cm = SessionManager.getWebContent();
%>
<html lang="<%=SessionManager.getCurrentLanguage()%>" xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=SessionManager.getCurrentLanguage()%>">
  <head>
    <jsp:include page="header-page.jsp" />
    <title>
      <%=application.getInitParameter("PAGE_TITLE")%>
      <%=cm.cms("habitats_annex1-browser_title")%>
    </title>
  </head>
  <body>
    <div id="outline">
    <div id="alignment">
    <div id="content">
      <jsp:include page="header-dynamic.jsp">
        <jsp:param name="location" value="home#index.jsp,habitat_types#habitats.jsp,habitats_annex1_tree_location" />
      </jsp:include>
      <h1>
        <%=cm.cmsText("habitats_annex1-browser_01")%>
      </h1>
      <br/>
<%
  String idCode = Utilities.formatString( request.getParameter( "idCode" ), "" );

  String SQL_DRV = application.getInitParameter("JDBC_DRV");
  String SQL_URL = application.getInitParameter("JDBC_URL");
  String SQL_USR = application.getInitParameter("JDBC_USR");
  String SQL_PWD = application.getInitParameter("JDBC_PWD");

  SQLUtilities sqlc = new SQLUtilities();
  sqlc.Init(SQL_DRV,SQL_URL,SQL_USR,SQL_PWD);

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
    Class.forName( SQL_DRV );
    con = DriverManager.getConnection( SQL_URL, SQL_USR, SQL_PWD );

    //we display root nodes
    strSQL = "SELECT ID_HABITAT, SCIENTIFIC_NAME, CODE_2000";
    strSQL = strSQL + " FROM CHM62EDT_HABITAT";
    strSQL = strSQL + " WHERE LENGTH(CODE_2000)=1";
    strSQL = strSQL + " AND CODE_2000<>'-'";
    strSQL = strSQL + " ORDER BY CODE_2000 ASC";

    ps = con.prepareStatement( strSQL );
    rs = ps.executeQuery();

%>
    <ul>
<%
    while(rs.next())
    {
%>
      <li>
        <a title="<%=rs.getString("SCIENTIFIC_NAME")%>" href="habitats-annex1-tree.jsp?idCode=<%=rs.getString("CODE_2000")%>"><%=rs.getString("CODE_2000")%> : <%=rs.getString("SCIENTIFIC_NAME")%></a><br/>
      </li>
<%
    }
%>
    </ul>
<%

    rs.close();
    ps.close();

    out.println("<br/><br/>");

    //we begin to display the tree

    if(idCode.length()>0) {
      strSQL = "SELECT ID_HABITAT, SCIENTIFIC_NAME, CODE_2000";
      strSQL = strSQL + " FROM CHM62EDT_HABITAT";
      strSQL = strSQL + " WHERE CODE_2000 LIKE '"+idCode.substring(0,1)+"%00'";
      strSQL = strSQL + " ORDER BY CODE_2000 ASC";

      ps2 = con.prepareStatement( strSQL );
      rs2 = ps2.executeQuery();

%>
      <ul>
<%
      while(rs2.next())
      {
        if(sqlc.Annex1HabitatHasChilds(rs2.getString("CODE_2000").substring(0,rs2.getString("CODE_2000").length()-2),rs2.getString("CODE_2000"))) {
%>
        <li>
          <a title="<%=rs2.getString("SCIENTIFIC_NAME")%>" href="habitats-annex1-tree.jsp?idCode=<%=rs2.getString("CODE_2000")%>"><%=rs2.getString("CODE_2000")%> : <%=rs2.getString("SCIENTIFIC_NAME")%></a><br/>
        </li>
<%
        } else {
%>
        <li>
          <a title="<%=rs2.getString("SCIENTIFIC_NAME")%>" href="habitats-factsheet.jsp?idHabitat=<%=rs2.getString("ID_HABITAT")%>"><%=rs2.getString("CODE_2000")%> : <%=rs2.getString("SCIENTIFIC_NAME")%></a><br/>
        </li>
<%
        }

         if(idCode.length()>=2 && idCode.indexOf(rs2.getString("CODE_2000"))>=0) {
           strSQL = "SELECT ID_HABITAT, SCIENTIFIC_NAME, CODE_2000";
           strSQL = strSQL + " FROM CHM62EDT_HABITAT";
           strSQL = strSQL + " WHERE CODE_2000 LIKE '"+idCode.substring(0,2)+"%0'";
           strSQL = strSQL + " AND CODE_2000 NOT LIKE '"+idCode.substring(0,2)+"%00'";
           strSQL = strSQL + " ORDER BY CODE_2000 ASC";

           ps4 = con.prepareStatement( strSQL );
           rs4 = ps4.executeQuery();

%>
           <ul>
<%
           while(rs4.next())
           {
             if(sqlc.Annex1HabitatHasChilds(rs4.getString("CODE_2000").substring(0,rs4.getString("CODE_2000").length()-1),rs4.getString("CODE_2000"))) {
%>
              <li>
                <a title="<%=rs4.getString("SCIENTIFIC_NAME")%>" href="habitats-annex1-tree.jsp?idCode=<%=rs4.getString("CODE_2000")%>"><%=rs4.getString("CODE_2000")%> : <%=rs4.getString("SCIENTIFIC_NAME")%></a><br/>
              </li>
<%
             } else {
%>
             <li>
               <a title="<%=rs4.getString("SCIENTIFIC_NAME")%>" href="habitats-factsheet.jsp?idHabitat=<%=rs4.getString("ID_HABITAT")%>"><%=rs4.getString("CODE_2000")%> : <%=rs4.getString("SCIENTIFIC_NAME")%></a><br/>
             </li>
<%
             }
             if(idCode.length()>=4 && idCode.indexOf(rs4.getString("CODE_2000"))>=0) {
               strSQL = "SELECT ID_HABITAT, SCIENTIFIC_NAME, CODE_2000";
               strSQL = strSQL + " FROM CHM62EDT_HABITAT";
               strSQL = strSQL + " WHERE CODE_2000 LIKE '"+idCode.substring(0,4)+"%'";
               strSQL = strSQL + " AND CODE_2000 NOT LIKE '"+idCode.substring(0,4)+"%0'";
               strSQL = strSQL + " ORDER BY CODE_2000 ASC";

               ps5 = con.prepareStatement( strSQL );
               rs5 = ps5.executeQuery();

%>
               <ul>
<%
               while(rs5.next())
               {
%>
                 <li>
                   <a title="<%=rs5.getString("SCIENTIFIC_NAME")%>" href="habitats-factsheet.jsp?idHabitat=<%=rs5.getString("ID_HABITAT")%>"><%=rs5.getString("CODE_2000")%> : <%=rs5.getString("SCIENTIFIC_NAME")%></a><br/>
                 </li>
<%
               }
%>
               </ul>
<%

               rs5.close();
               ps5.close();
             }

           }
%>
           </ul>
<%

           rs4.close();
           ps4.close();
         }
      }
%>
  </ul>
<%

      rs2.close();
      ps2.close();
    }

    con.close();
  }
  catch ( Exception e )
  {
    e.printStackTrace();
    return;
  }

%>
      <br/>
      <jsp:include page="footer.jsp">
        <jsp:param name="page_name" value="habitats-annex1-tree.jsp" />
      </jsp:include>
    </div>
    </div>
    </div>
  </body>
</html>
<%
  out.flush();
%>