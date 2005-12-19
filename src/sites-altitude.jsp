<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : "Sites altitude" function - search page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.search.Utilities,
                  java.util.Vector,
                  ro.finsiel.eunis.WebContentManagement"%>
<%@ page import="ro.finsiel.eunis.utilities.Accesibility"%>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session"/>
<%
  WebContentManagement cm = SessionManager.getWebContent();

  // Request parameters
  String country = (request.getParameter("country")==null?"":request.getParameter("country"));
  String altitude1 = (request.getParameter("altitude1")==null?"":request.getParameter("altitude1"));
  String altitude2 = (request.getParameter("altitude2")==null?"":request.getParameter("altitude2"));
  String altitude21 = (request.getParameter("altitude21")==null?"":request.getParameter("altitude21"));
  String altitude22 = (request.getParameter("altitude22")==null?"":request.getParameter("altitude22"));
  String altitude31 = (request.getParameter("altitude31")==null?"":request.getParameter("altitude31"));
  String altitude32 = (request.getParameter("altitude32")==null?"":request.getParameter("altitude32"));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="<%=SessionManager.getCurrentLanguage()%>" xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=SessionManager.getCurrentLanguage()%>">
  <head>
    <jsp:include page="header-page.jsp" />
    <script language="JavaScript" type="text/javascript" src="script/sites-altitude.js"></script>
    <script language="JavaScript" type="text/javascript" src="script/save-criteria.js"></script>
    <script language="JavaScript" type="text/javascript" src="script/sites-altitude-save-criteria.js"></script>
    <script language="JavaScript" type="text/javascript">
      <!--
       var countryListString = "<%=Utilities.getCountryListString()%>";
      //-->
    </script>
    <title>
      <%=application.getInitParameter("PAGE_TITLE")%>
      <%=cm.cms("sites_altitude_title")%>
    </title>
  </head>
  <body>
    <div id="outline">
    <div id="alignment">
    <div id="content">
      <jsp:include page="header-dynamic.jsp">
        <jsp:param name="location" value="home_location#index.jsp,sites_location#sites.jsp,sites_altitude_location"/>
        <jsp:param name="helpLink" value="sites-help.jsp"/>
        <jsp:param name="mapLink" value="show"/>
      </jsp:include>
      <form name="eunis" method="get" onsubmit="return validateForm();" action="sites-altitude-result.jsp">
        <input type="hidden" name="source" value="sitename" />
        <h1>
          <%=cm.cmsText("sites_altitude_01")%>
        </h1>
        <%=cm.cmsText("sites_altitude_20")%>
        <br />
        <br />
        <div class="grey_rectangle">
          <strong>
            <%=cm.cmsText("search_will_provide_following_information")%>
          </strong>
          <br />
          <input id="showSourceDB" name="showSourceDB" type="checkbox" value="true" checked="checked" title="<%=cm.cms("sites_altitude_03")%>" />
          <label for="showSourceDB"><%=cm.cmsText("sites_altitude_03")%></label>
          <%=cm.cmsTitle("sites_altitude_03")%>

          <input id="showCountry" name="showCountry" type="checkbox" value="true" checked="checked" title="<%=cm.cms("sites_altitude_04")%>" />
          <label for="showCountry"><%=cm.cmsText("sites_altitude_04")%></label>
          <%=cm.cmsTitle("sites_altitude_04")%>

          <input id="showName" name="showName" type="checkbox" value="true" checked="checked" disabled="disabled" title="<%=cm.cms("sites_altitude_06")%>" />
          <label for="showName"><%=cm.cmsText("sites_altitude_06")%></label>
          <%=cm.cmsTitle("sites_altitude_06")%>

          <input id="showDesignationTypes" name="showDesignationTypes" type="checkbox" value="true" checked="checked" title="<%=cm.cms("sites_altitude_05")%>" />
          <label for="showDesignationTypes"><%=cm.cmsText("sites_altitude_05")%></label>
          <%=cm.cmsTitle("sites_altitude_05")%>

          <input id="showCoordinates" name="showCoordinates" type="checkbox" value="true" checked="checked" title="<%=cm.cms("sites_altitude_07")%>" />
          <label for="showCoordinates"><%=cm.cmsText("sites_altitude_07")%></label>
          <%=cm.cmsTitle("sites_altitude_07")%>

          <input id="showAltitude" name="showAltitude" type="checkbox" value="true" checked="checked" title="<%=cm.cms("sites_altitude_08")%>" />
          <label for="showAltitude"><%=cm.cmsText("sites_altitude_08")%></label>
          <%=cm.cmsTitle("sites_altitude_08")%>
        </div>
        <img align="middle" alt="<%=cm.cms("field_included")%>" title="<%=cm.cms("field_included")%>" src="images/mini/field_included.gif" width="11" height="12" />
        <%=cm.cmsAlt("field_included")%>
        <strong>
          <%=cm.cmsText("sites_altitude_10")%>
        </strong>
        <label for="relOp" class="noshow"><%=cm.cms("operator")%></label>
        <select id="relOp" name="relOp" onchange="MM_jumpMenuAlt('parent',this,0)" class="inputTextField" title="<%=cm.cms("operator")%>">
<%
  String selected = "";
  String no = Utilities.formatString( request.getParameter( "no" ), "" );
  if ( no.equalsIgnoreCase( "" ) || no.equalsIgnoreCase( "1" ) )
  {
    selected = "selected=\"selected\"";
  }
%>
          <option value="sites-altitude.jsp?between=no&amp;no=1&amp;between2=<%=request.getParameter("between2")%>&amp;no2=<%=request.getParameter("no2")%>&amp;between3=<%=request.getParameter("between3")%>&amp;no3=<%=request.getParameter("no3")%>" <%=selected%>>
            <%=cm.cms("sites_altitude_11")%>
          </option>
<%
  selected = "";
  if ( no.equalsIgnoreCase( "2" ) )
  {
    selected = "selected=\"selected\"";
  }
%>
          <option value="sites-altitude.jsp?between=yes&amp;no=2&amp;between2=<%=request.getParameter("between2")%>&amp;no2=<%=request.getParameter("no2")%>&amp;between3=<%=request.getParameter("between3")%>&amp;no3=<%=request.getParameter("no3")%>" <%=selected%>>
            <%=cm.cms("sites_altitude_12")%>
          </option>
<%
  selected = "";
  if ( no.equalsIgnoreCase( "3" ) )
  {
    selected = "selected=\"selected\"";
  }
%>
          <option value="sites-altitude.jsp?between=no&amp;no=3&amp;between2=<%=request.getParameter("between2")%>&amp;no2=<%=request.getParameter("no2")%>&amp;between3=<%=request.getParameter("between3")%>&amp;no3=<%=request.getParameter("no3")%>" <%=selected%>>
            <%=cm.cms("sites_altitude_13")%>
          </option>
<%
  selected = "";
  if ( no.equalsIgnoreCase( "4" ) )
  {
    selected = "selected=\"selected\"";
  }
%>
          <option value="sites-altitude.jsp?between=no&amp;no=4&amp;between2=<%=request.getParameter("between2")%>&amp;no2=<%=request.getParameter("no2")%>&amp;between3=<%=request.getParameter("between3")%>&amp;no3=<%=request.getParameter("no3")%>" <%=selected%>>
            <%=cm.cms("sites_altitude_14")%>
          </option>
        </select>
        <%=cm.cmsLabel("operator")%>
        <%=cm.cmsTitle("operator")%>
        <%=cm.cmsInput("sites_altitude_11")%>
        <%=cm.cmsInput("sites_altitude_12")%>
        <%=cm.cmsInput("sites_altitude_13")%>
        <%=cm.cmsInput("sites_altitude_14")%>
<%
  // request.getParameter("between") is true if operator for Mean Altitude is "between"
  // no = 1 if operator is "is"
  // no = 2 if operator is "between"
  // no = 3 if operator is "greater than"
  // no = 4 if operator is "smaller than"
  if (request.getParameter("between")!=null && request.getParameter("between").equalsIgnoreCase("yes"))
  {
%>
        <label for="altitude1" class="noshow"><%=cm.cms("sites_altitude_minmean")%></label>
        <input id="altitude1" name="altitude1" value="<%=altitude1%>" size="5" class="inputTextField" title="<%=cm.cms("sites_altitude_minmean")%>" />
        <%=cm.cmsLabel("sites_altitude_minmean")%>
        <%=cm.cmsTitle("sites_altitude_minmean")%>
        and
        <label for="altitude2" class="noshow"><%=cm.cms("sites_altitude_maxnmean")%></label>
        <input id="altitude2" name="altitude2" value="<%=altitude2%>" size="5" class="inputTextField" title="<%=cm.cms("sites_altitude_maxnmean")%>" />
        <%=cm.cmsLabel("sites_altitude_maxnmean")%>
        <%=cm.cmsTitle("sites_altitude_maxnmean")%>
<%
  }
  else
  {
%>
        <label for="altitude1" class="noshow"><%=cm.cms("sites_altitude_mean")%></label>
        <input id="altitude1" name="altitude1" value="<%=altitude1%>" size="5" class="inputTextField" title="<%=cm.cms("sites_altitude_mean")%>" />
        <%=cm.cmsLabel("sites_altitude_mean")%>
        <%=cm.cmsTitle("sites_altitude_mean")%>
<%
  }
  Integer valAl=Utilities.checkedStringToInt(request.getParameter("no"),new Integer(1));
  // Set value for relationOp hidden field
  Integer valAltitude = Utilities.OPERATOR_IS;
  if(valAl.compareTo(new Integer(2))==0) valAltitude = Utilities.OPERATOR_BETWEEN;
  if(valAl.compareTo(new Integer(3))==0) valAltitude = Utilities.OPERATOR_GREATER_OR_EQUAL;
  if(valAl.compareTo(new Integer(4))==0) valAltitude = Utilities.OPERATOR_SMALLER_OR_EQUAL;
%>
        <br />
        <img align="middle" alt="<%=cm.cms("field_included")%>" title="<%=cm.cms("field_included")%>" src="images/mini/field_included.gif" width="11" height="12" />
        <%=cm.cmsTitle("field_included")%>
        <%=cm.cmsAlt("field_included")%>
        <strong>
          <%=cm.cmsText("sites_altitude_16")%>
        </strong>
<%
  selected = "";
  String no2 = Utilities.formatString( request.getParameter( "no2" ), "" );
  if ( no2.equalsIgnoreCase( "" ) || no2.equalsIgnoreCase( "1" ) )
  {
    selected = "selected=\"selected\"";
  }
%>
        <label for="relOp2" class="noshow"><%=cm.cms("operator")%></label>
        <select id="relOp2" name="relOp2" onchange="MM_jumpMenuAlt('parent',this,0)" class="inputTextField" title="<%=cm.cms("operator")%>">
          <option value="sites-altitude.jsp?between2=no&amp;no2=1&amp;between=<%=request.getParameter("between")%>&amp;no=<%=request.getParameter("no")%>&amp;between3=<%=request.getParameter("between3")%>&amp;no3=<%=request.getParameter("no3")%>" <%=selected%>>
            <%=cm.cms("sites_altitude_11")%>
          </option>
<%
  selected = "";
  if ( no2.equalsIgnoreCase( "2" ) )
  {
    selected = "selected=\"selected\"";
  }
%>
          <option value="sites-altitude.jsp?between2=yes&amp;no2=2&amp;between=<%=request.getParameter("between")%>&amp;no=<%=request.getParameter("no")%>&amp;between3=<%=request.getParameter("between3")%>&amp;no3=<%=request.getParameter("no3")%>" <%=selected%>>
            <%=cm.cms("sites_altitude_12")%>
          </option>
<%
  selected = "";
  if ( no2.equalsIgnoreCase( "3" ) )
  {
    selected = "selected=\"selected\"";
  }
%>
          <option value="sites-altitude.jsp?between2=no&amp;no2=3&amp;between=<%=request.getParameter("between")%>&amp;no=<%=request.getParameter("no")%>&amp;between3=<%=request.getParameter("between3")%>&amp;no3=<%=request.getParameter("no3")%>" <%=selected%>>
            <%=cm.cms("sites_altitude_13")%>
          </option>
<%
  selected = "";
  if ( no2.equalsIgnoreCase( "4" ) )
  {
    selected = "selected=\"selected\"";
  }
%>
          <option value="sites-altitude.jsp?between2=no&amp;no2=4&amp;between=<%=request.getParameter("between")%>&amp;no=<%=request.getParameter("no")%>&amp;between3=<%=request.getParameter("between3")%>&amp;no3=<%=request.getParameter("no3")%>" <%=selected%>>
            <%=cm.cms("sites_altitude_14")%>
          </option>
        </select>
        <%=cm.cmsLabel("operator")%>
        <%=cm.cmsTitle("operator")%>
        <%=cm.cmsInput("sites_altitude_11")%>
        <%=cm.cmsInput("sites_altitude_12")%>
        <%=cm.cmsInput("sites_altitude_13")%>
        <%=cm.cmsInput("sites_altitude_14")%>
<%
  // request.getParameter("between2") is true if operator for Minimum Altitude is "between"
  // no2 = 1 if operator is "is"
  // no2 = 2 if operator is "between"
  // no2 = 3 if operator is "greater than"
  // no2 = 4 if operator is "smaller than"
  if (request.getParameter("between2")!=null && request.getParameter("between2").equalsIgnoreCase("yes"))
  {
%>
        <label for="altitude21" class="noshow"><%=cm.cms("sites_altitude_minmin")%></label>
        <input id="altitude21" name="altitude21" value="<%=altitude21%>" size="5" class="inputTextField" title="<%=cm.cms("sites_altitude_minmin")%>" />
        <%=cm.cmsLabel("sites_altitude_minmin")%>
        <%=cm.cmsTitle("sites_altitude_minmin")%>
        and
        <label for="altitude22" class="noshow"><%=cm.cms("sites_altitude_maxmin")%></label>
        <input id="altitude22" name="altitude22" value="<%=altitude22%>" size="5" class="inputTextField" title="<%=cm.cms("sites_altitude_maxmin")%>" />
        <%=cm.cmsLabel("sites_altitude_maxmin")%>
        <%=cm.cmsTitle("sites_altitude_maxmin")%>
<%
  }
  else
  {
%>
        <label for="altitude21" class="noshow"><%=cm.cms("sites_altitude_min")%></label>
        <input id="altitude21" name="altitude21" value="<%=altitude21%>" size="5" class="inputTextField" title="<%=cm.cms("sites_altitude_min")%>" />
        <%=cm.cmsLabel("sites_altitude_min")%>
        <%=cm.cmsTitle("sites_altitude_min")%>
<%
  }
  Integer valAl2=Utilities.checkedStringToInt(request.getParameter("no2"),new Integer(1));
  // Set value for relationOp2 hidden field
  Integer valAltitude2 = Utilities.OPERATOR_IS;
  if(valAl2.compareTo(new Integer(2))==0) valAltitude2 = Utilities.OPERATOR_BETWEEN;
  if(valAl2.compareTo(new Integer(3))==0) valAltitude2 = Utilities.OPERATOR_GREATER_OR_EQUAL;
  if(valAl2.compareTo(new Integer(4))==0) valAltitude2 = Utilities.OPERATOR_SMALLER_OR_EQUAL;
%>
        <br />
        <img align="middle" alt="<%=cm.cms("field_included")%>" title="<%=cm.cms("field_included")%>" src="images/mini/field_included.gif" width="11" height="12" />
        <%=cm.cmsTitle("field_included")%>
        <%=cm.cmsAlt("field_included")%>
        <strong>
          <%=cm.cmsText("sites_altitude_17")%>
        </strong>
<%
  selected = "";
  String no3 = Utilities.formatString( request.getParameter( "no3" ), "" );
  if ( no3.equalsIgnoreCase( "" ) || no3.equalsIgnoreCase( "1" ) )
  {
    selected = "selected=\"selected\"";
  }
%>
        <label for="relOp3" class="noshow"><%=cm.cms("operator")%></label>
        <select id="relOp3" name="relOp3" onchange="MM_jumpMenuAlt('parent',this,0)" class="inputTextField" title="<%=cm.cms("operator")%>">
          <option value="sites-altitude.jsp?between3=no&amp;no3=1&amp;between=<%=request.getParameter("between")%>&amp;no=<%=request.getParameter("no")%>&amp;between2=<%=request.getParameter("between2")%>&amp;no2=<%=request.getParameter("no2")%>" <%=selected%>>
            <%=cm.cms("sites_altitude_11")%>
          </option>
<%
  selected = "";
  if ( no3.equalsIgnoreCase( "2" ) )
  {
    selected = "selected=\"selected\"";
  }
%>
          <option value="sites-altitude.jsp?between3=yes&amp;no3=2&amp;between=<%=request.getParameter("between")%>&amp;no=<%=request.getParameter("no")%>&amp;between2=<%=request.getParameter("between2")%>&amp;no2=<%=request.getParameter("no2")%>" <%=selected%>>
            <%=cm.cms("sites_altitude_12")%>
          </option>
<%
  selected = "";
  if ( no3.equalsIgnoreCase( "3" ) )
  {
    selected = "selected=\"selected\"";
  }
%>
          <option value="sites-altitude.jsp?between3=no&amp;no3=3&amp;between=<%=request.getParameter("between")%>&amp;no=<%=request.getParameter("no")%>&amp;between2=<%=request.getParameter("between2")%>&amp;no2=<%=request.getParameter("no2")%>" <%=selected%>>
            <%=cm.cms("sites_altitude_13")%>
          </option>
<%
  selected = "";
  if ( no3.equalsIgnoreCase( "4" ) )
  {
    selected = "selected=\"selected\"";
  }
%>
          <option value="sites-altitude.jsp?between3=no&amp;no3=4&amp;between=<%=request.getParameter("between")%>&amp;no=<%=request.getParameter("no")%>&amp;between2=<%=request.getParameter("between2")%>&amp;no2=<%=request.getParameter("no2")%>" <%=selected%>>
            <%=cm.cms("sites_altitude_14")%>
          </option>
        </select>
        <%=cm.cmsLabel("operator")%>
        <%=cm.cmsTitle("operator")%>
        <%=cm.cmsInput("sites_altitude_11")%>
        <%=cm.cmsInput("sites_altitude_12")%>
        <%=cm.cmsInput("sites_altitude_13")%>
        <%=cm.cmsInput("sites_altitude_14")%>

<%
  // request.getParameter("between3") is true if operator for Maximum Altitude is "between"
  // no3 = 1 if operator is "is"
  // no3 = 2 if operator is "between"
  // no3 = 3 if operator is "greater than"
  // no3 = 4 if operator is "smaller than"
  if (request.getParameter("between3")!=null && request.getParameter("between3").equalsIgnoreCase("yes"))
  {
%>
        <label for="altitude31" class="noshow"><%=cm.cms("sites_altitude_minmax")%></label>
        <input id="altitude31" name="altitude31" value="<%=altitude31%>" size="5" class="inputTextField" title="<%=cm.cms("sites_altitude_minmax")%>" />
        <%=cm.cmsLabel("sites_altitude_minmax")%>
        <%=cm.cmsTitle("sites_altitude_minmax")%>
        and
        <label for="altitude32" class="noshow"><%=cm.cms("sites_altitude_maxmax")%></label>
        <input id="altitude32" name="altitude32" value="<%=altitude32%>" size="5" class="inputTextField" title="<%=cm.cms("sites_altitude_maxmax")%>" />
        <%=cm.cmsLabel("sites_altitude_maxmax")%>
        <%=cm.cmsTitle("sites_altitude_maxmax")%>
<%
  }
  else
  {
%>
        <label for="altitude31" class="noshow"><%=cm.cms("sites_altitude_max")%></label>
        <input id="altitude31" name="altitude31" value="<%=altitude31%>" size="5" class="inputTextField" title="<%=cm.cms("sites_altitude_max")%>" />
        <%=cm.cmsLabel("sites_altitude_max")%>
        <%=cm.cmsTitle("sites_altitude_max")%>
<%
  }
  Integer valAl3=Utilities.checkedStringToInt(request.getParameter("no3"),new Integer(1));
  // Set value for relationOp3 hidden field
  Integer valAltitude3 = Utilities.OPERATOR_IS;
  if(valAl3.compareTo(new Integer(2))==0) valAltitude3 = Utilities.OPERATOR_BETWEEN;
  if(valAl3.compareTo(new Integer(3))==0) valAltitude3 = Utilities.OPERATOR_GREATER_OR_EQUAL;
  if(valAl3.compareTo(new Integer(4))==0) valAltitude3 = Utilities.OPERATOR_SMALLER_OR_EQUAL;
%>
        <br />
        <img align="middle" alt="<%=cm.cms("field_optional")%>" title="<%=cm.cms("field_optional")%>" src="images/mini/field_optional.gif" width="11" height="12" />
        <%=cm.cmsAlt("field_optional")%>

        <label for="country"><strong><%=cm.cmsText("sites_altitude_18")%></strong></label>
        <input id="country" name="country" type="text" size="30" value="<%=country%>" class="inputTextField" title="<%=cm.cms("sites_altitude_18")%>" />
        <%=cm.cmsLabel("sites_altitude_18")%>
        <%=cm.cmsTitle("sites_altitude_18")%>

        <a title="<%=cm.cms("helper")%>" href="javascript:chooseCountry('sites-country-choice.jsp?field=country')"><img src="images/helper/helper.gif" alt="<%=cm.cms("helper")%>" title="<%=cm.cms("helper")%>" width="11" height="18" border="0" align="middle" /></a>
        <input type="hidden" name="relationOp" value="<%=valAltitude%>" />
        <input type="hidden" name="relationOp2" value="<%=valAltitude2%>" />
        <input type="hidden" name="relationOp3" value="<%=valAltitude3%>" />
        <div class="submit_buttons">
          <label for="reset" class="noshow"><%=cm.cms("reset_btn_label")%></label>
          <input id="reset" name="Reset" type="reset" value="<%=cm.cms("reset_btn_value")%>" class="inputTextField" title="<%=cm.cms("reset_btn_title")%>" />
          <%=cm.cmsLabel("reset_btn_label")%>
          <%=cm.cmsTitle("reset_btn_title")%>
          <%=cm.cmsInput("reset_btn_value")%>

          <label for="submit2" class="noshow"><%=cm.cms("search_btn_label")%></label>
          <input id="submit2" name="submit2" type="submit" class="inputTextField" value="<%=cm.cms("search_btn_value")%>" title="<%=cm.cms("search_btn_title")%>" />
          <%=cm.cmsLabel("search_btn_label")%>
          <%=cm.cmsTitle("search_btn_title")%>
          <%=cm.cmsInput("search_btn_value")%>
        </div>
        <jsp:include page="sites-search-common.jsp" />
     </form>
<%
  // Save search criteria
  if (SessionManager.isAuthenticated()&&SessionManager.isSave_search_criteria_RIGHT())
  {
%>
        <br />
        <%=cm.cmsText("sites_altitude_savecriteria")%>:
        <a title="<%=cm.cms("save")%>" href="javascript:composeParameterListForSaveCriteria('<%=request.getParameter("expandSearchCriteria")%>',validateForm(),'sites-altitude.jsp','5','eunis',attributesNames,formFieldAttributes,operators,formFieldOperators,booleans,'save-criteria-search.jsp');"><img border="0" alt="<%=cm.cms("save")%>" title="<%=cm.cms("save")%>" src="images/save.jpg" width="21" height="19" align="middle" /></a>
        <%=cm.cmsTitle("save")%>
        <%=cm.cmsAlt("save")%>
<%
  // Set Vector for URL string
  Vector show = new Vector();
  show.addElement("showName");
  show.addElement("showSourceDB");
  show.addElement("showDesignationYear");
  show.addElement("showCountry");
  show.addElement("showDesignationTypes");
  show.addElement("showCoordinates");
  show.addElement("showAltitude");
  String pageName = "sites-altitude.jsp";
  String pageNameResult = "sites-altitude-result.jsp?"+Utilities.writeURLCriteriaSave(show);
  // Expand or not save criterias list
  String expandSearchCriteria = (request.getParameter("expandSearchCriteria")==null?"no":request.getParameter("expandSearchCriteria"));
%>
      <jsp:include page="show-criteria-search.jsp">
        <jsp:param name="pageName" value="<%=pageName%>" />
        <jsp:param name="pageNameResult" value="<%=pageNameResult%>" />
        <jsp:param name="expandSearchCriteria" value="<%=expandSearchCriteria%>" />
      </jsp:include>
<%
  }
%>
      <%=cm.cmsMsg("sites_altitude_title")%>
      <jsp:include page="footer.jsp">
        <jsp:param name="page_name" value="sites-altitude.jsp" />
      </jsp:include>
    </div>
    </div>
    </div>
  </body>
</html>