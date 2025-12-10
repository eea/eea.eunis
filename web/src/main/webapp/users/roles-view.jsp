<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : Part of user management
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="java.util.List,
                 ro.finsiel.eunis.jrfTables.users.RolesDomain,
                 ro.finsiel.eunis.jrfTables.users.RightsDomain,
                 ro.finsiel.eunis.jrfTables.users.RightsPersist,
                 ro.finsiel.eunis.jrfTables.users.RolesPersist,
                 ro.finsiel.eunis.search.users.UsersUtility,
                 java.util.Enumeration"%>
<%@ page import="ro.finsiel.eunis.WebContentManagement"%>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session"/>
<%
    // Web content manager used in this page.
      WebContentManagement cm = SessionManager.getWebContent();

  // If user is authentificated and has this right
  if(SessionManager.isAuthenticated() && SessionManager.isRole_management_RIGHT())
{
%>
<h2>
   <%=cm.cmsText("roles_view_02")%>
</h2>
<br />
<%

// Request parameters
String tab = (request.getParameter("tab")==null?"view_roles":request.getParameter("tab"));

%>
<form name="eunis" method="post" action="roles.jsp">
 <input type="hidden" name="tab" value="<%=tab%>" />
 <input type="hidden" name="operation" value="" />
 <input type="hidden" name="deleteWho" value="" />
<%
try {
// Roles list
List roles = new RolesDomain().findOrderBy("A.ROLENAME");
// Rights list
List rights = new RightsDomain().findOrderBy("A.RIGHTNAME");
if(roles != null && roles.size() > 0)
{
%>
  <table summary="layout" width="100%" border="1" style="border-collapse: collapse">
  <tr>
    <th id="Name_column_header" style="text-align:left">
      <%=cm.cmsText("righ_name")%>
    </th>
  <%
     for(int i=0;i<roles.size();i++)
     {
      RolesPersist role = (RolesPersist)roles.get(i);
      String tooltip = (role.getDescription() != null && !role.getDescription().trim().equalsIgnoreCase("")  ? "  title=\""+(role.getDescription())+"\" ":"");
  %>
    <th id="<%=role.getRoleName()%>_column_header" style="text-align:center" <%=tooltip%>>
      <a href="roles.jsp?tab=1&amp;roleName=<%=role.getRoleName()%>" title="<%=cm.cms("edit_role")%>">
        <%=UsersUtility.getNameNice(role.getRoleName())%>
      </a>
      <%=cm.cmsTitle("edit_role")%>
    </th>
  <%
     }
  %>
  </tr>
  <%
     if(rights != null && rights.size() > 0)
     {
       for(int j=0;j<rights.size();j++)
       {
         RightsPersist right = (RightsPersist)rights.get(j);
         String tooltip = (right.getDescription() != null && !right.getDescription().trim().equalsIgnoreCase("")  ? " title=\""+(right.getDescription())+"\" " : "");
  %>
  <tr>
    <th id="<%=right.getRightName()%>_row_header" style="text-align:left" <%=tooltip%>>
      <a href="roles.jsp?tab=3&amp;rightName=<%=right.getRightName()%>" title="<%=cm.cms("edit_right")%>">
       <%=UsersUtility.getNameNice(right.getRightName())%>
      </a>
      <%=cm.cmsTitle("edit_right")%>
    </th>
  <%
     for(int i=0;i<roles.size();i++)
     {
        RolesPersist role = (RolesPersist)roles.get(i);
    %>
    <td headers="<%=role.getRoleName()%>_right <%=right.getRightName()%>" style="text-align:center">
       <input id="<%=role.getRoleName()%>_rightxxx_<%=right.getRightName()%>" name="<%=role.getRoleName()%>_rightxxx_<%=right.getRightName()%>" type="checkbox" value="checkbox" <%=(UsersUtility.ObjectIsInVector(UsersUtility.getRolesRightsName(role.getRoleName()),right.getRightName())?"checked=\"checked\"":"")%> disabled="disabled" />
    </td>
  <%
    }
  %>
  </tr>
  <%
    }
     }
  %>
  </table>
<%
}
} catch(Exception e){ }
%>
</form>
<%
} else
  {
%>
  <strong><%=cm.cmsText("not_authenticated_no_rights_1")%></strong>
<br />
<%
  }
%>