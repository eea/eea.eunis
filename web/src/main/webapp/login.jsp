<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : Login page
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement,
                 ro.finsiel.eunis.search.Utilities"%>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<%
  String eeaHome = application.getInitParameter( "EEA_HOME" );
  String btrail = "eea#" + eeaHome + ",home#index.jsp,login";
  boolean success = false;
  String username = request.getParameter( "username" );
  String cmd = Utilities.formatString( request.getParameter( "cmd" ), "" );
  String ref = request.getParameter( "ref" );
  // If cmd is login.
  if ( cmd.equalsIgnoreCase( "login" ) )
  {
    SessionManager.logout();
    try
    {
      String password = request.getParameter( "password" );
      success = ( null != username && null != password ) && SessionManager.login( username, password, request );
      SessionManager.setCurrentLanguage( SessionManager.getUserPrefs().getLang() );
    }
    catch ( Exception e )
    {

      success = false;
    }
  }
%>

<%
  WebContentManagement cm = SessionManager.getWebContent();
%>

<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cmsPhrase("Login into EUNIS Database") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" pageTitle="${title}" btrail="<%= btrail%>">
    <stripes:layout-component name="head">
    </stripes:layout-component>
    <stripes:layout-component name="contents">
        <a name="documentContent"></a>
<!-- MAIN CONTENT -->
        <h1>
          <%=cm.cmsPhrase("EUNIS Database Login")%>
        </h1>

          <%
            if ( !success )
            {
              if ( cmd.equalsIgnoreCase( "login" ) && !success )
              {
          %>
              <div class="error-msg">
                  <%=cm.cmsPhrase("Invalid login name or password")%>
              </div>
          <%
              }
          %>
                  <form name="login" method="post" action="login.jsp" style="text-align:center; width : 100%">
                    <input type="hidden" name="cmd" value="login" />
          <%
                if( ref != null )
                {
          %>
                    <input type="hidden" name="ref" value="<%=ref%>" />
          <%
                }
          %>
                    <label for="username"><%=cm.cms("login_username_label")%>:</label>
                    <input title="<%=cm.cms("login_username_title")%>" type="text" id="username" name="username" value="<%=(null != username) ? username : ""%>" />
                    <%=cm.cmsTitle("login_username_title")%>
                    <%=cm.cmsLabel("login_username_label")%>
                    <br />
                    <label for="password"><%=cm.cms("password")%>:</label>
                    <input title="<%=cm.cms("login_password_title")%>" type="password" id="password" name="password" />
                    <%=cm.cmsTitle("login_password_title")%>
                    <%=cm.cmsLabel("password")%>
                    <br />
                    <br />
                    <input class="submitSearchButton" title="<%=cm.cms("login_submit_title")%>" type="submit" id="submit" name="Submit" value="<%=cm.cms("login")%>" />
                    <%=cm.cmsTitle("login_submit_title")%>
                    <%=cm.cmsInput("login")%>
                  </form>
          <%
            }
            else
            {
          %>
             <div class="system-msg">
              <%=cm.cmsPhrase("You successfully logged in as")%>
              <strong><%=SessionManager.getUsername()%></strong>.
							<ul>
              <li><a href="services.jsp"><%=cm.cmsPhrase("Services-page")%></a></li>
							</ul>
             </div>
          <%
            }
          %>
              <%--<%=cm.cmsText("habitats_login-help_01")%>--%>
<!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>
