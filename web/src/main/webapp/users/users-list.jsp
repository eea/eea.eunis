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
                 ro.finsiel.eunis.jrfTables.users.UserDomain,
                 ro.finsiel.eunis.jrfTables.users.UserPersist,
                 ro.finsiel.eunis.search.Utilities,
                 java.util.Vector,
                 ro.finsiel.eunis.search.users.UsersUtility,
                 ro.finsiel.eunis.jrfTables.users.RolesPersist"%>
<%@ page import="ro.finsiel.eunis.WebContentManagement"%>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session"/>
<%
  // Web content manager used in this page.
     WebContentManagement cm = SessionManager.getWebContent();

  // If user is authentificated  and he has this right
  if(SessionManager.isAuthenticated() && SessionManager.isUser_management_RIGHT())
{
%>
    <h2><%=cm.cmsText("users_list_02")%></h2>
    <br />
<%
try {
// Users names list
List usersList = new UserDomain().findCustom("SELECT USERNAME,FIRST_NAME,LAST_NAME,LANG,EMAIL," +
              " THEME_INDEX,DATE_FORMAT(login_date,'%d %b %Y %H:%i:%s')" +
              " FROM eunis_users ORDER BY USERNAME");
if(usersList != null && usersList.size() > 0)
  {
%>
<table summary="<%=cm.cms("list_users")%>" width="100%" border="1" cellspacing="0" cellpadding="0">
<tr  class="dusers" style="text-align:left;font-weight :bold;">
  <td>
    <%=cm.cmsText("user_name")%>
  </td>
  <td>
    <%=cm.cmsText("first_name")%>
  </td>
  <td>
    <%=cm.cmsText("last_name")%>
  </td>
  <td style="text-align:center">
    <%=cm.cmsText("users_list_07")%>
  </td>
  <td>
    <%=cm.cmsText("roles")%>
  </td>
</tr>
<%
  //String bgColor = "";
  String classValue = "";
  for(int i=0;i<usersList.size();i++)
  {
    UserPersist objUser = (UserPersist) usersList.get(i);
    //bgColor = (0 == (i % 2)) ? "#EEEEEE" : "#FFFFFF";
    classValue = (0 == (i % 2)) ? "eusers" : "fusers";
    String objUserName = (objUser.getUsername()==null?"":objUser.getUsername());
%>
<tr>
  <td style="text-align:left" class="<%=classValue%>">
  <%
    if (!Utilities.formatString(objUser.getUsername(),"&nbsp;").equalsIgnoreCase("&nbsp;"))
    {
  %>
     <a href="users.jsp?userName=<%=objUser.getUsername()%>&amp;tab=1" title="<%=cm.cms("edit_user")%>"><%=Utilities.formatString(objUser.getUsername(),"&nbsp;")%></a>
     <%=cm.cmsTitle("edit_user")%>
  <%
    } else
    {
      out.print(Utilities.formatString(objUser.getUsername(),"&nbsp;"));
    }
  %>
  </td>
  <td class="<%=classValue%>">
    <%=Utilities.formatString(objUser.getFirstName(),"&nbsp;")%>
  </td>
  <td class="<%=classValue%>">
    <%=Utilities.formatString(objUser.getLastName(),"&nbsp;")%>
  </td>
  <td style="text-align:center" class="<%=classValue%>">
    <%=(objUser.getLoginDate() == null ? "" : (objUser.getLoginDate().trim().equalsIgnoreCase("null") ? "" : objUser.getLoginDate()))%>&nbsp;
  </td>
  <td class="<%=classValue%>" style="vertical-align:top">
  <%
    // Roles names for a user name
    Vector userRoles = UsersUtility.getUsersRoles(objUserName);
    if(userRoles != null && userRoles.size() > 0)
       {
       for(int j=0;j<userRoles.size();j++)
        {
    %>
           <div  onmouseover="return showtooltip('<%=UsersUtility.getRolesRightsAsString((String)userRoles.get(j))%>')" onmouseout="hidetooltip()">
              <%=Utilities.formatString((String)userRoles.get(j),"&nbsp;")%>
           </div>
    <%
       }
    } else
    {
%>&nbsp;
<%
    }
%>
  </td>
</tr>
<%
  }
%>
</table>
<%
  }
}catch(Exception e){ }
%>
<%
  } else
  {
%>
<strong><%=cm.cmsText("not_authenticated_no_rights")%></strong>
<br />
<%
  }
%>

<%=cm.br()%>
<%=cm.cmsMsg("list_users")%>
<%=cm.br()%>