<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : Common header
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.session.ThemeWrapper,
                 ro.finsiel.eunis.session.ThemeManager"%>
<%@ page import="ro.finsiel.eunis.jrfTables.EunisISOLanguagesPersist"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.StringTokenizer"%>
<%@ page import="ro.finsiel.eunis.WebContentManagement"%>
<%@ page import="java.util.List"%>
<jsp:include page="meta-tags.jsp"/>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session"/>

<jsp:include page="required-head.jsp" />
<script type="text/javascript" language="JavaScript" src="https://cdn.ravenjs.com/3.20.0/raven.min.js"></script>

<script type="text/javascript" language="JavaScript" src="<%=request.getContextPath()%>/script/msg-<%=SessionManager.getCurrentLanguage()%>.js"></script>
<script type="text/javascript" language="JavaScript" src="<%=request.getContextPath()%>/script/header.js"></script>
<script type="text/javascript" language="JavaScript" src="<%=request.getContextPath()%>/script/utils.js"></script>
<%
  WebContentManagement cm = SessionManager.getWebContent();
  List translatedLanguages = cm.getTranslatedLanguages();
  if( !SessionManager.isLanguageDetected() )
  {
    String acceptLanguage = request.getHeader( "accept-language" );
    if( acceptLanguage != null )
    {
      boolean found = false;
      ArrayList acceptedLanguages = new ArrayList();
      StringTokenizer tok = new StringTokenizer( acceptLanguage, "," );
      while( tok.hasMoreElements() )
      {
        acceptedLanguages.add( tok.nextElement() );
      }
      if( acceptedLanguages.size() > 0 )
      {
        for(int i = 0; i < acceptedLanguages.size(); i++)
        {
          for( int j = 0; j < translatedLanguages.size(); j++ )
          {
            EunisISOLanguagesPersist language = ( EunisISOLanguagesPersist ) translatedLanguages.get(j);
            if( acceptedLanguages.get( i ).toString().indexOf( language.getCode() ) >= 0 )
            {
              SessionManager.setCurrentLanguage( language.getCode() );
              SessionManager.setLanguageDetected( true );
              found = true;
              break;
            }
          }
          if( found ) break;
        }
      }
    }
  }
  // Select stylesheet
  ThemeWrapper currentTheme = SessionManager.getThemeManager().getCurrentTheme();
  if ( currentTheme.equals( ThemeManager.FRESH_ORANGE ) )
  {
%>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/css_fresh_orange.css" />
<%
  }
  else if ( currentTheme.equals( ThemeManager.NATURE_GREEN ) )
  {
%>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/css_nature_green.css" />
<%
  }
  else if ( currentTheme.equals( ThemeManager.CHERRY ) )
  {
%>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/css_cherry.css" />
<%
  }
  else if ( currentTheme.equals( ThemeManager.BLACKWHITE ) )
  {
%>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/css_black_white.css" />
<%
  }
  else
  {
%>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/css_sky_blue.css" />
<%
  }
%>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/print.css" media="print" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/eea_tabs.css" />

