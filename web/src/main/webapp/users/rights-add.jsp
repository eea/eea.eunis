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
                 ro.finsiel.eunis.WebContentManagement,
                 ro.finsiel.eunis.jrfTables.users.RolesDomain,
                 ro.finsiel.eunis.jrfTables.users.RolesPersist,
                 ro.finsiel.eunis.jrfTables.users.UserDomain,
                 ro.finsiel.eunis.search.users.UsersUtility,
                 java.util.Vector,
                 ro.finsiel.eunis.jrfTables.users.UserPersist,
                 ro.finsiel.eunis.jrfTables.users.RightsDomain,
                 ro.finsiel.eunis.jrfTables.users.RightsPersist"%>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session"/>
<%
    // Web content manager used in this page.
    WebContentManagement cm = SessionManager.getWebContent();

 //If user is authentificated and has this right
  if(SessionManager.isAuthenticated() && SessionManager.isRole_management_RIGHT())
{
  // Request parameters
  String users_operation = (request.getParameter("users_operation") == null ? "edit_rights" : request.getParameter("users_operation"));

  String tab = (request.getParameter("tab")==null?"view_roles":request.getParameter("tab"));

  try
  {
   // All rights list
   List ListRights = new RightsDomain().findAll();
   if(ListRights != null && ListRights.size() > 0)
   {
%>
    <script type="text/javascript" language="JavaScript">
       <!--
       var rights_list = new Array(<%=ListRights.size()%>);
      //-->
    </script>
<%
     for (int i=0;i<ListRights.size();i++)
      {
%>
          <script type="text/javascript" language="JavaScript">
             <!--
               rights_list[<%=i%>]='<%=((RightsPersist)ListRights.get(i)).getRightName()%>';
             //-->
         </script>
<%
      }
  } else
   {
%>
          <script type="text/javascript" language="JavaScript">
             <!--
               var rights_list = new Array(0);
             //-->
          </script>
<%
    }
  }catch(Exception e){ }
%>

<script type="text/javascript" language="JavaScript">
      <!--

      function RightExist(){
      rightName = escape(trim(document.eunis.rightName.value));
      rightName = rightName.replace(' ','_');
      exist=false;
      for(i=0;i<rights_list.length;i++) if (rightName.toLowerCase() == rights_list[i].toLowerCase()) exist = true;
      if(exist) {
                   alert("<%=cm.cms("rights_add_01")%>");
                   document.eunis.rightName.value = "";
                }
      return exist;
      }

        function validateFormAdd() {
       if (document.eunis.rightName == null || trim(document.eunis.rightName.value)=='')
            {
             alert("<%=cm.cms("rights_add_02")%>");
             return false;
            }

        if(RightExist()) return false;

       return true;
        }

           function validateFormEdit() {
            if (document.eunis.rightName == null
            || trim(document.eunis.rightName.value) == ''
            || trim(document.eunis.rightName.value) == 'selectRightName')
            {
             alert("<%=cm.cms("rights_add_16")%>");
             return false;
            }

           return true;
           }

         function deleteRight(){
         if (document.eunis.rightName != null
         && trim(document.eunis.rightName.value) != ''
         && trim(document.eunis.rightName.value) != 'selectRightName')
         {
         rightName = escape(trim(document.eunis.rightName.value));
         document.location="roles.jsp?operation=delete&rightName="+rightName+"&tab=<%=tab%>";
         }
      }
      //-->
</script>
<%

// Request parameters
String rightName = (request.getParameter("rightName") == null ? "" : request.getParameter("rightName"));
String message = "";
String operation = request.getParameter("operation");

String onSubmit = "";
// User choose 'add_rights'
if(users_operation.equalsIgnoreCase("add_rights"))
{
  // Set onSubmit string
  onSubmit = "return validateFormAdd();";
  if (operation != null)
  {
      // if user choose to add a new right
      if(operation.equalsIgnoreCase("submit"))
      {
        // if rightName is a valid right name
        if(request.getParameter("rightName") != null && !request.getParameter("rightName").equalsIgnoreCase(""))
        {
          String description = (request.getParameter("description") == null ? "" : request.getParameter("description"));
          // Replace all " " with "_"
          String goodRightName = request.getParameter("rightName").trim().replaceAll(" ","_");
          // add right
           boolean addRightSuccess = UsersUtility.addRights(SessionManager.getUsername(),goodRightName,description);

          if(addRightSuccess) message = cm.cms("rights_add_04");
          else message = "<span color=\"red\">"+cm.cms("rights_add_05")+"</span>";

        } else message = "<span color=\"red\">"+cm.cms("rights_add_05")+"</span>";
      }
  }
}

  // User choose 'edit_rights'
  if(users_operation.equalsIgnoreCase("edit_rights"))
  {
    // Set onSubmit string
    onSubmit = "return validateFormEdit();";
    if (operation!=null)
    {
        // if user choose to update a right
        if(operation.equalsIgnoreCase("submit"))
        {
          // if rightName is a valid right name
          if(request.getParameter("rightName") != null
             && !request.getParameter("rightName").trim().equalsIgnoreCase("")
             && !request.getParameter("rightName").equalsIgnoreCase("selectRightName"))
             {
               String description = (request.getParameter("description") == null ? "" : request.getParameter("description"));
               // Edit right
               boolean editWithSuccess = UsersUtility.editRights(SessionManager.getUsername(),rightName,description);
               if(editWithSuccess) message = cm.cms("rights_add_06");
               else message = "<span color=\"red\">"+cm.cms("rights_add_07")+"</span>";

             } else message = "<span color=\"red\">"+cm.cms("rights_add_07")+"</span>";
        }

      // if user choose to delete a right
      if(operation.equalsIgnoreCase("delete"))
       {
       // if rightName is a valid right name
       if(request.getParameter("rightName") != null
          && !request.getParameter("rightName").trim().equalsIgnoreCase("")
          && !request.getParameter("rightName").equalsIgnoreCase("selectRightName")
          && UsersUtility.existRightName(request.getParameter("rightName")) )
       {
           // Delete right
           boolean deleteWithSucces = UsersUtility.deleteRights(rightName);
           if(deleteWithSucces) message = cm.cms("rights_add_08");
           else message = "<span color=\"red\">"+cm.cms("rights_add_09")+"</span>";
       }  else message = "<span color=\"red\">"+cm.cms("rights_add_09")+"</span>";
      }
    }
  }


String description = "";
// if user choose 'edit_rights', fill the description field
if(users_operation.equalsIgnoreCase("edit_rights"))
{
  description = (UsersUtility.getRightsObject(rightName) == null ? "" : (UsersUtility.getRightsObject(rightName)).getDescription());
}
%>
     <h2>
        <%=(users_operation.equalsIgnoreCase("add_rights")?cm.cms("add"):cm.cms("edit"))%> rights
     </h2>
     <br />

<form name="eunis" method="post" action="roles.jsp" onsubmit="<%=onSubmit%>">
<input type="hidden" name="tab" value="<%=tab%>" />
<input type="hidden" name="operation" value="" />
<table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0" style="border-collapse:collapse" class="tableBorder">
<tr><td>&nbsp;</td></tr>
<tr>
  <td>
    <table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
    <%
    if(users_operation.equalsIgnoreCase("edit_rights"))
    {
    %>
    <tr>
      <td>
      &nbsp;&nbsp;<label for="rightName1"><%=cm.cmsText("rights_add_11")%></label>
      </td>
      <td>
       <select id="rightName1" name="rightName" style="border-width:1px" onchange="MM_jumpMenuRights('parent',this,0,'<%=tab%>')"  title="<%=cm.cms("rights_add_12")%>">
        <option value="selectRightName" selected="selected"><%=cm.cms("rights_add_11")%></option>
        <%
           try
           {
           // Rights list
           List rights = new RightsDomain().findOrderBy("A.RIGHTNAME");
            if(rights != null && rights.size() > 0)
            {
              for(int i=0;i<rights.size();i++)
              {
       %>
       <option value="<%=((RightsPersist)rights.get(i)).getRightName()%>" <%=(rightName.equalsIgnoreCase(((RightsPersist)rights.get(i)).getRightName())?"selected=\"selected\"":"")%>><%=UsersUtility.getNameNice(((RightsPersist)rights.get(i)).getRightName())%></option>
       <%
              }
            }
           } catch(Exception e){ }
        %>
       </select>
       <%=cm.cmsTitle("rights_add_12")%>
       &nbsp;&nbsp;&nbsp;
       <a href="javascript:deleteRight();"><%=cm.cmsText("rights_add_13")%></a>
       <%=cm.cmsTitle("rights_add_13")%>
      </td>
    </tr>
   <%
    } else
    {
   %>
     <tr>
       <td>
       &nbsp;&nbsp;<label for="rightName2"><%=cm.cmsText("righ_name")%></label>
       </td>
       <td>
         <input title="<%=cm.cms("righ_name")%>" alt="<%=cm.cms("righ_name")%>" type="text" id="rightName2" name="rightName" size="50" value="" onchange="RightExist();" />
         <%=cm.cmsTitle("righ_name")%>
       </td>
     </tr>
   <%
    }
   %>
      <tr>
        <td colspan="2">
          &nbsp;
        </td>
      </tr>
      <tr>
        <td>
        &nbsp;&nbsp;<label for="description"><%=cm.cmsText("description")%></label>
        </td>
        <td>
          <textarea title="<%=cm.cms("description")%>" id="description" name="description" cols="70" rows="5"><%=description%></textarea>
          <%=cm.cmsTitle("description")%>
        </td>
      </tr>
    </table>
    <p>&nbsp;</p>
  </td>
</tr>
<tr>
  <td style="text-align:left">
    &nbsp;&nbsp;
    <input id="input1" type="submit" value="<%=(users_operation.equalsIgnoreCase("add_rights")?cm.cms("add_right"):cm.cms("update_data"))%>" name="submit" onclick="document.eunis.operation.value='submit';" title="<%=cm.cms("submit")%>" />
    <%=cm.cmsTitle("submit")%>
    <%=cm.cmsInput("add_right")%>
    <%=cm.cmsInput("update_data")%>
    &nbsp;&nbsp;
    <input id="input2" type="reset" value="<%=cm.cmsPhrase("Reset")%>" name="Reset" title="<%=cm.cmsPhrase("Reset")%>" />
  </td>
</tr>
<tr>
  <td>
    &nbsp;
  </td>
</tr>
<tr>
  <td>
    <hr noshade="noshade" size="1" />
  </td>
  <td>
    <hr noshade="noshade" size="1" />
  </td>
</tr>
<tr>
  <td>
    Message: <%=message%>
  </td>
</tr>
<tr>
  <td>
    &nbsp;
  </td>
</tr>
</table>
</form>
<br />

<%
  } else
  {
%>
<strong><%=cm.cmsText("not_authenticated_no_rights_1")%></strong>
<br />
<%
  }
%>

<%=cm.br()%>
<%=cm.cmsMsg("rights_add_01")%>
<%=cm.br()%>
<%=cm.cmsMsg("rights_add_02")%>
<%=cm.br()%>
<%=cm.cmsMsg("rights_add_04")%>
<%=cm.br()%>
<%=cm.cmsMsg("rights_add_05")%>
<%=cm.br()%>
<%=cm.cmsMsg("rights_add_06")%>
<%=cm.br()%>
<%=cm.cmsMsg("rights_add_07")%>
<%=cm.br()%>
<%=cm.cmsMsg("rights_add_08")%>
<%=cm.br()%>
<%=cm.cmsMsg("rights_add_09")%>
<%=cm.br()%>
<%=cm.cmsMsg("add")%>
<%=cm.br()%>
<%=cm.cmsMsg("edit")%>
<%=cm.br()%>
<%=cm.cmsMsg("rights_add_16")%>
<%=cm.br()%>
