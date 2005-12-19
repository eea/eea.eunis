<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : "Sites advanced map" function - Map page displaying results of search visually, on image from map server.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="java.util.List,
                 ro.finsiel.eunis.WebContentManagement,
                 java.util.ArrayList"%>
<%@ page import="ro.finsiel.eunis.jrfTables.sites.advanced.DictionaryDomain"%>
<%@ page import="ro.finsiel.eunis.search.AbstractPaginator"%>
<%@ page import="ro.finsiel.eunis.search.sites.advanced.DictionaryPaginator"%>
<%@ page import="ro.finsiel.eunis.jrfTables.sites.advanced.DictionaryPersist"%>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session"/>
<jsp:useBean id="formBean" class="ro.finsiel.eunis.search.sites.coordinates.CoordinatesBean" scope="page">
  <jsp:setProperty name="formBean" property="*"/>
</jsp:useBean>
<%
  WebContentManagement cm = SessionManager.getWebContent();
  AbstractPaginator paginator = new DictionaryPaginator(new DictionaryDomain(request.getSession().getId()));
  List sites = new ArrayList();
  try
  {
    paginator.setPageSize( paginator.countResults() );
    paginator.setCurrentPage( 0 );
    sites = paginator.getPage( 0 );
  }
  catch( Exception ex )
  {
    ex.printStackTrace();
  }
  String sitesIds = "";
  for ( int i = 0; i < sites.size(); i++ )
  {
    DictionaryPersist site = ( DictionaryPersist )sites.get( i );
    sitesIds += "'" + site.getIdSite() + "'";
    if ( i < sites.size() - 1 ) sitesIds += ",";
  }
  if ( sitesIds.equalsIgnoreCase( "" ) )
  {
    sitesIds = "none";
  }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="<%=SessionManager.getCurrentLanguage()%>" xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=SessionManager.getCurrentLanguage()%>">
  <head>
    <title>
      <%=cm.cms("sites_advanced-map_title")%>
    </title>
    <jsp:include page="header-page.jsp" />
  </head>
  <body style="margin : 0px; padding : 0px;" >
    <object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,0,0" width="740" height="552" id="fl_eunis" align="middle">
      <param name="allowScriptAccess" value="sameDomain" />
      <param name="movie" value="gis/fl_eunis.swf" />
      <param name="quality" value="high" />
      <param name="bgcolor" value="#FFFFFF" />
      <param name="FlashVars"  value="v_color=<%=SessionManager.getUserPrefs().getThemeIndex()%>&amp;v_path=<%=application.getInitParameter( "DOMAIN_NAME" )%>&amp;v_sh_sites=<%=sitesIds%>" />
      <embed src="gis/fl_eunis.swf" FLASHVARS="v_color=<%=SessionManager.getUserPrefs().getThemeIndex()%>&amp;v_path=<%=application.getInitParameter( "DOMAIN_NAME" )%>&amp;v_sh_sites=<%=sitesIds%>" quality="high" bgcolor="#FFFFFF"  width="740" height="552" name="fl_eunis" align="middle" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" />
    </object>
    <%=cm.cmsMsg("sites_advanced-map_title")%>
  </body>
</html>