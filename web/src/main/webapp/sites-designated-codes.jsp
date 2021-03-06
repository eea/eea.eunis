<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : "Sites by designation types" function - search page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.search.Utilities,
                 java.util.Vector,
                 ro.finsiel.eunis.WebContentManagement,
                 ro.finsiel.eunis.utilities.Accesibility"%>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session"/>
<%
  WebContentManagement cm = SessionManager.getWebContent();
  String eeaHome = application.getInitParameter( "EEA_HOME" );
  String btrail = "eea#" + eeaHome + ",home#index.jsp,Sites#sites.jsp,sites_designated_codes_location";
%>
<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cms("site_by_designation_codes") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" helpLink="species-help.jsp" pageTitle="${title}" btrail="<%= btrail%>">
    <stripes:layout-component name="head">
        <link rel="stylesheet" type="text/css" href="/css/eea_search.css">
        <script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/script/sites-designated-codes.js"></script>
        <script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/script/save-criteria.js"></script>
        <script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/script/sites-designated-codes-save-criteria.js"></script>
    </stripes:layout-component>
    <stripes:layout-component name="contents">
        <a name="documentContent"></a>
        <h1>
          <%=cm.cmsPhrase("Pick designation types, show sites")%>
        </h1>
<!-- MAIN CONTENT -->
                <form name="eunis" method="get" onsubmit="javascript: return validateForm();" action="sites-designated-codes-result.jsp">
                <input type="hidden" name="source" value="sitedesignatedname" />
                <p>
                <%=cm.cmsPhrase("Search sites by legal instruments<br />(ex.: designations with <strong>forest</strong> in their name, <strong>A</strong> as category, from all source data sets)")%>
                </p>
                  <fieldset class="large">
                  <legend><%=cm.cmsPhrase("Search in")%></legend>
                  <jsp:include page="sites-search-common.jsp" />
                  </fieldset>

                  <fieldset class="large">
                  <legend><%=cm.cmsPhrase("Search what")%></legend>

                <img style="vertical-align:middle" alt="<%=Accesibility.getText( "generic.criteria.mandatory")%>" title="<%=Accesibility.getText( "generic.criteria.mandatory")%>" src="images/mini/field_mandatory.gif" width="11" height="12" />
                <label for="relationOp"><%=cm.cmsPhrase("Original/English/French Designation name")%></label>
                <select id="relationOp" name="relationOp" title="<%=cm.cmsPhrase("Operator")%>">
                  <option value="<%=Utilities.OPERATOR_IS%>">
                    <%=cm.cmsPhrase("is")%>
                  </option>
                  <option value="<%=Utilities.OPERATOR_CONTAINS%>">
                    <%=cm.cmsPhrase("contains")%>
                  </option>
                  <option value="<%=Utilities.OPERATOR_STARTS%>" selected="selected">
                    <%=cm.cmsPhrase("starts with")%>
                  </option>
                </select>

                <label for="searchString" class="noshow"><%=cm.cms("designation_name")%></label>
                <input id="searchString" name="searchString" value="" size="32" title="<%=cm.cms("designation_name")%>" />
                <%=cm.cmsLabel("designation_name")%>
                <%=cm.cmsTitle("designation_name")%>
                <a title="<%=cm.cms("helper")%>" href="javascript:openHelper('sites-designations-choice.jsp','yes')"><img src="images/helper/helper.gif" alt="<%=cm.cms("helper")%>" title="<%=cm.cms("helper")%>" width="11" height="18" border="0" style="vertical-align:middle" /></a>&nbsp;
                <%=cm.cmsTitle("helper")%>
                <%=cm.cmsAlt("helper")%>
                <br />
                <img style="vertical-align:middle" alt="<%=cm.cms("field_optional")%>" title="<%=cm.cms("field_optional")%>" src="images/mini/field_optional.gif" width="11" height="12" />
                <%=cm.cmsAlt("field_optional")%>
                <label for="category">
                    <%=cm.cmsPhrase("Designation category")%>
                </label>
                <select id="category" name="category" title="Designation category">
                    <option value="A"><%=cm.cms("sites_designations_cata")%></option>
                    <option value="B"><%=cm.cms("sites_designations_catb")%></option>
                    <option value="C"><%=cm.cms("sites_designations_catc")%></option>
                    <option value="any" selected="selected">
                      <%=cm.cms("any")%>
                    </option>
                </select>
                <%=cm.cmsInput("sites_designations_cata")%>
                <%=cm.cmsInput("sites_designations_catb")%>
                <%=cm.cmsInput("sites_designations_catc")%>
                  </fieldset>

                  <fieldset class="large">
                    <legend><%=cm.cmsPhrase("Output fields")%></legend>
                  <strong>
                    <%=cm.cmsPhrase("Search will provide the following information (checked fields will be displayed), as provided in the original database:")%>
                  </strong>
                  <br />
                  <input id="showSourceDB" name="showSourceDB" type="checkbox" value="true" checked="checked" title="<%=cm.cms("source_data_set_2")%>" />
                  <label for="showSourceDB"><%=cm.cmsPhrase("Source data set&nbsp;")%></label>
                  <%=cm.cmsTitle("source_data_set_2")%>

                  <input id="showCountry" name="showCountry" type="checkbox" value="true" checked="checked" title="<%=cm.cms("country_1")%>" />
                  <label for="showCountry"><%=cm.cmsPhrase("Country &nbsp;")%></label>
                  <%=cm.cmsTitle("country_1")%>

                  <input id="showName" name="showName" type="checkbox" disabled="disabled" value="true" checked="checked" title="<%=cm.cms("site_name_1")%>" />
                  <label for="showName"><%=cm.cmsPhrase("Site name &nbsp;")%></label>
                  <%=cm.cmsTitle("site_name_1")%>

                  <input id="showDesignationTypes" name="showDesignationTypes" type="checkbox" value="true" checked="checked" title="<%=cm.cms("sites_designated-codes_05")%>" />
                  <label for="showDesignationTypes"><%=cm.cmsPhrase("Designation type category&nbsp;")%></label>
                  <%=cm.cmsTitle("sites_designated-codes_05")%>

                  <input id="showCoordinates" name="showCoordinates" type="checkbox" value="true" checked="checked" title="<%=cm.cms("coordinates_1")%>" />
                  <label for="showCoordinates"><%=cm.cmsPhrase("Coordinates &nbsp;")%></label>
                  <%=cm.cmsTitle("coordinates_1")%>

                  <input id="showSize" name="showSize" type="checkbox" value="true" checked="checked" title="<%=cm.cms("size_1")%>" />
                  <label for="showSize"><%=cm.cmsPhrase("Size &nbsp;")%></label>
                  <%=cm.cmsTitle("size_1")%>

                  <input id="showDesignationYear" name="showDesignationYear" type="checkbox" value="true" checked="checked" disabled="disabled" title="<%=cm.cms("designation_year")%>" />
                  <label for="showDesignationYear"><%=cm.cmsPhrase("Designation year")%></label>
                  <%=cm.cmsTitle("designation_year")%>
                </fieldset>

                <div class="submit_buttons">
                  <input id="reset" name="Reset" type="reset" value="<%=cm.cmsPhrase("Reset")%>" class="standardButton" title="<%=cm.cmsPhrase("Reset values")%>" />

                  <input id="submit2" name="submit2" type="submit" class="submitSearchButton" value="<%=cm.cmsPhrase("Search")%>" title="<%=cm.cmsPhrase("Search")%>" />
                </div>
              </form>
          <%
            // Save search criteria
            if (SessionManager.isAuthenticated()&&SessionManager.isSave_search_criteria_RIGHT())
            {
              // Set Vector for URL string
              Vector show = new Vector();
              show.addElement("showName");
              show.addElement("showSourceDB");
              show.addElement("showDesignationYear");
              show.addElement("showCountry");
              show.addElement("showDesignationTypes");
              show.addElement("showCoordinates");
              show.addElement("showSize");
              String pageName = "sites-designated-codes.jsp";
              String pageNameResult = "sites-designated-codes-result.jsp?"+Utilities.writeURLCriteriaSave(show);
              // Expand or not save criterias list
              String expandSearchCriteria = (request.getParameter("expandSearchCriteria")==null?"no":request.getParameter("expandSearchCriteria"));
          %>
              <%=cm.cmsPhrase("Save your criteria:")%>
              <a title="<%=cm.cmsPhrase("Save")%>" href="javascript:composeParameterListForSaveCriteria('<%=request.getParameter("expandSearchCriteria")%>',validateForm(),'sites-designated-codes.jsp','3','eunis',attributesNames,formFieldAttributes,operators,formFieldOperators,booleans,'save-criteria-search.jsp');"><img border="0" alt="<%=cm.cmsPhrase("Save")%>" title="<%=cm.cmsPhrase("Save")%>" src="images/save.jpg" width="21" height="19" style="vertical-align:middle" /></a>
              <jsp:include page="show-criteria-search.jsp">
                <jsp:param name="pageName" value="<%=pageName%>" />
                <jsp:param name="pageNameResult" value="<%=pageNameResult%>" />
                <jsp:param name="expandSearchCriteria" value="<%=expandSearchCriteria%>" />
              </jsp:include>
          <%
            }
          %>

                <%=cm.cmsMsg("site_by_designation_codes")%>
<!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>