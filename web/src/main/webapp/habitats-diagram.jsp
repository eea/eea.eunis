<%--
  - Author(s)   : The EUNIS Database Team.
  - Date        :
  - Copyright   : (c) 2002-2005 EEA - European Environment Agency.
  - Description : Diagrams images for habitats.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement,
                 ro.finsiel.eunis.jrfTables.Chm62edtHabitatDomain,
                 ro.finsiel.eunis.jrfTables.Chm62edtHabitatPersist,
                 ro.finsiel.eunis.search.Utilities,
                 java.util.List" %>

<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="<%=SessionManager.getCurrentLanguage()%>" xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=SessionManager.getCurrentLanguage()%>">
<head>
  <jsp:include page="header-page.jsp" />
  <%
    WebContentManagement cm = SessionManager.getWebContent();
  %>
  <title>
    <%=application.getInitParameter("PAGE_TITLE")%>
    <%=cm.cms("habitats_diagram_title")%>
  </title>
  <%
    String eunisCode = request.getParameter("habCode");
    //System.out.println("eunisCode = " + eunisCode);
    boolean isEUNIS = false;
    int level = 0;
    Chm62edtHabitatPersist habitat = null;
    // If eunisCode is a valid code
    if (eunisCode != null && !eunisCode.equalsIgnoreCase("null") && !eunisCode.trim().equalsIgnoreCase("")) {
      //System.out.println("OK");
      // Find habitat with this code (eunisCode)
      List list = new Chm62edtHabitatDomain().findWhere("EUNIS_HABITAT_CODE='" + eunisCode + "'");
      //System.out.println("list.size() = " + list.size());
      if (list.size() > 0) {
        habitat = (Chm62edtHabitatPersist) list.get(0);
      }
      if (null != habitat) {
        //isEunis = (0 == Utilities.EUNIS_HABITAT.compareTo(Utilities.getHabitatType(habitat.getCode2000()))) ? true : false;
        int idHabitat = Utilities.checkedStringToInt(habitat.getIdHabitat(), -1);
        //System.out.println("idHabitat = " + idHabitat);
        isEUNIS =Utilities.isHabitatEunis(habitat.getHabitatType());
        level = habitat.getHabLevel().intValue();
        //System.out.println("level = " + level);
      }
    } else {
      //System.out.println("This is level 1 or error...");
    }
  %>
</head>

<body>
<%
  if (isEUNIS) {
%>
<%
  if (level > 0 && level < 3) {
%>
<img alt="<%=cm.cms("habitat_type_diagram")%>" border="0" src="images/diagrams/level<%=habitat.getEunisHabitatCode()%>.gif" />
<%=cm.cmsTitle("habitat_type_diagram")%>
<br />
<%
} else {
%>
<br />
<br />
<%=cm.cmsPhrase("Requested habitat type")%>: <%=habitat.getScientificName()%>(
<strong><%=eunisCode%></strong>)
-
<%=cm.cmsPhrase("Level")%>:<strong><%=habitat.getHabLevel()%></strong>
<br />
<%=cm.cmsPhrase("Diagrams exist for habitat type level <strong>1</strong> or <strong>2</strong>")%>.
<br />
<table summary="layout" width="500" border="0" cellpadding="0" cellspacing="0" bgcolor="#EEEEEE">
  <%
    if (habitat.getEunisHabitatCode().length() > 2) {
  %>
  <tr>
    <td width="417">
      &nbsp;<%=cm.cmsPhrase("Link to level 1 parent habitat type")%>:
    </td>
    <td width="40">
      &nbsp;<%=habitat.getEunisHabitatCode().substring(0, 1)%>
    </td>
    <td width="43">
      &nbsp;
      <a title="<%=cm.cms("open_diagram")%>" href="habitats-diagram.jsp?habCode=<%=habitat.getEunisHabitatCode().substring(0, 1)%>"><img alt="Open diagram" src="images/mini/diagram_in.png" style="vertical-align:middle" width="20" height="20" border="0" /></a>
      <%=cm.cmsTitle("open_diagram")%>
      <br />
    </td>
  </tr>
  <%
    }
  %>
  <%    if (habitat.getEunisHabitatCode().length() > 3) {
  %>
  <tr>
    <td width="417">
      &nbsp;<%=cm.cmsPhrase("Link to level 2 parent habitat type")%>:
    </td>
    <td width="40">
      &nbsp;<%=habitat.getEunisHabitatCode().substring(0, 2)%>
    </td>
    <td width="43">
      &nbsp;
      <a title="<%=cm.cms("open_diagram")%>" href="habitats-diagram.jsp?habCode=<%=habitat.getEunisHabitatCode().substring(0, 2)%>"><img alt="<%=cm.cms("open_diagram")%>" src="images/mini/diagram_in.png" style="vertical-align:middle" width="20" height="20" border="0" /></a>
      <%=cm.cmsTitle("open_diagram")%>
      <br />
    </td>
  </tr>
  <%
    }
  %>
</table>
<%
  }
%>
<%
} else {
%>
<img alt="<%=cm.cms("first_level_diagram")%>" border="0" src="images/diagrams/level1.gif" /><%=cm.cmsTitle("first_level_diagram")%>
<br />
<%
  }
%>
  <%=cm.br()%>
  <%=cm.cmsMsg("habitats_diagram_title")%>
  <%=cm.br()%>
</body>
</html>
