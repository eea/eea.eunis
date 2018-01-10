<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Habitats module' function - display links to all habitat searches.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement,
                ro.finsiel.eunis.jrfTables.habitats.names.NamesDomain,
                ro.finsiel.eunis.search.Utilities" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />

  <%
    WebContentManagement cm = SessionManager.getWebContent();
    String eeaHome = application.getInitParameter( "EEA_HOME" );
    String btrail = "eea#" + eeaHome + ",home#index.jsp,habitat_types";
  %>


<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cms("habitat_type_search") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" pageTitle="${title}" btrail="<%= btrail%>">
    <stripes:layout-component name="head">
    </stripes:layout-component>
    <stripes:layout-component name="contents">

        <a name="documentContent"></a>
        <h1 class="documentFirstHeading">
        <%=cm.cmsPhrase("Habitat types search")%>
        </h1>
<!-- MAIN CONTENT -->
        <div class="documentDescription">
        <%=cm.cmsPhrase("Access information about the EUNIS habitat classification and the EU Habitats Directive Annex I habitat types")%>
        </div>
                <div id="qs" align="center" style="padding-left : 10px; width : 90%; vertical-align : middle;">
                  <form name="quick_search" action="habitats-names-result.jsp" method="post" onsubmit="javascript:if(trim(document.quick_search.searchString.value) == '' || trim(document.quick_search.searchString.value) == 'Enter habitat name here...') {alert('Before searching, please type a few letters from habitat name.');return false;} else return true; ">
                    <input type="hidden" name="showLevel" value="true" />
                    <input type="hidden" name="showCode" value="true" />
                    <input type="hidden" name="showScientificName" value="true" />
                    <input type="hidden" name="showVernacularName" value="true" />
                    <input type="hidden" name="showOtherInfo" value="true" />
                    <input type="hidden" name="database" value="<%=NamesDomain.SEARCH_BOTH%>" />
                    <input type="hidden" name="useScientific" value="true" />
                    <input type="hidden" name="useVernacular" value="true" />
                    <input type="hidden" name="fuzzySearch" value="true" />
                    <input type="hidden" name="relationOp" value="<%=Utilities.OPERATOR_STARTS%>" />
                    <label for="searchString"><%=cm.cmsPhrase("Search habitat types by code or names starting with:")%></label>
                    <input id="searchString" type="text"
                           size="30" name="searchString"
                           value="<%=cm.cms("enter_habitat_name_here")%>"
                           onfocus="if(this.value=='<%=cm.cms("enter_habitat_name_here")%>')this.value='';"
                           onblur="if(this.value=='')this.value='<%=cm.cms("enter_habitat_name_here")%>';" />
                    <%=cm.cmsTitle("habitat_to_search_for")%>
                    <%=cm.cmsInput("enter_habitat_name_here")%>
                    <input type="submit"
                           value="<%=cm.cmsPhrase("Search")%>"
                           name="Submit"
                           id="Submit"
                           class="submitSearchButton"
                           title="Search" />
                    <a href="fuzzy-search-help.jsp" title="<%=cm.cms("help_fuzzy_search")%>"><img alt="<%=cm.cms("help_fuzzy_search")%>" title="<%=cm.cms("help_fuzzy_search")%>" src="images/mini/help.jpg" border="0" style="vertical-align:middle" /></a>
                    <%=cm.cmsTitle("help_fuzzy_search")%>
                  </form>
                <br />
                </div>
                
        <div class="eea-accordion-panels non-exclusive">

        <div class="eea-accordion-panel" style="clear: both;">
        <h2 class="notoc eea-icon-right-container"><%= cm.cmsPhrase("Predefined searches") %></h2>
        <div class="pane">
        <h4><%=cm.cmsPhrase("A set of predefined functions to search the database") %></h4>
        <table class="fullwidth">
            <colgroup>
                <col style="width: 350px; white-space: nowrap;">
                <col>
            </colgroup>
                  <tbody>
                    <tr>
                      <td>
                        <img alt="<%=cm.cms("names_and_descriptions")%>" src="images/mini/bulletb.gif" width="6" height="6" style="vertical-align:middle" /><%=cm.cmsTitle("names_and_descriptions")%>
                        <a href="habitats-names.jsp"><strong><%=cm.cmsPhrase("Names and descriptions")%></strong></a>
                        <%=cm.cmsTitle("habitats_main_namesDesc")%>
                      </td>
                      <td>
                        <%=cm.cmsPhrase("Search habitat types by name or description")%>
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <img alt="<%=cm.cms("legal_instruments")%>" src="images/mini/bulletb.gif" width="6" height="6" style="vertical-align:middle" /><%=cm.cmsTitle("legal_instruments")%>
                        <a href="habitats-legal.jsp"><strong><%=cm.cmsPhrase("Legal instruments")%></strong></a>
                        <%=cm.cmsTitle("habitats_main_legalDesc")%>
                      </td>
                      <td>
                        <%=cm.cmsPhrase("Search EUNIS habitat types under legal designation at European level")%>
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <img alt="<%=cm.cms("habitats_main_key")%>" src="images/mini/bulletb.gif" width="6" height="6" style="vertical-align:middle" /><%=cm.cmsTitle("habitats_main_key")%>
                        <a href="habitats-key.jsp"><strong><%=cm.cmsPhrase("Key navigation")%></strong></a>
                        <%=cm.cmsTitle("habitats_main_keyDesc")%>
                      </td>
                      <td>
                        <%=cm.cmsPhrase("Identify a habitat type following questions and graphical schemas")%>
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <img alt="<%=cm.cmsPhrase("EUNIS habitat types hierarchical view")%>" src="images/mini/bulletb.gif" width="6" height="6" style="vertical-align:middle" /><%=cm.cmsTitle("habitats_main_EUNIShierarchy")%>
                        <a href="habitats-code-browser.jsp"><strong><%=cm.cmsPhrase("EUNIS habitat types hierarchical view")%></strong></a>
                      </td>
                      <td>
                        <%=cm.cmsPhrase("Visualisation of EUNIS habitat types classification")%>
                      </td>
                    </tr>
                    <tr>
                        <td>
                            <img alt="<%=cm.cmsPhrase("EUNIS habitat types hierarchical view, REVISED")%>" src="images/mini/bulletb.gif" width="6" height="6" style="vertical-align:middle" /><%=cm.cmsTitle("EUNIS habitat types hierarchical view, REVISED")%>
                            <a href="habitats-code-browser-2017.jsp"><strong><%=cm.cmsPhrase("EUNIS habitat types hierarchical view, REVISED")%></strong></a>
                        </td>
                        <td>
                            <%=cm.cmsPhrase("Visualization of revised groups of EUNIS habitat type classification")%>
                        </td>
                    </tr>
                    <tr>
                      <td>
                        <img alt="<%=cm.cmsPhrase("ANNEX I habitat types hierarchical view")%>" src="images/mini/bulletb.gif" width="6" height="6" style="vertical-align:middle" /><%=cm.cmsTitle("habitats_main_ANNEXhierarchy")%>
                        <a href="habitats-annex1-browser.jsp"><strong><%=cm.cmsPhrase("ANNEX I habitat types hierarchical view")%></strong></a>
                      </td>
                      <td>
                        <%=cm.cmsPhrase("Visualisation of Habitats Directive Annex I habitat types (Natura 2000 network)")%>
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <img alt="<%=cm.cmsPhrase("Resolution 4 habitat types hierarchical view")%>" src="images/mini/bulletb.gif" width="6" height="6" style="vertical-align:middle" /><%=cm.cmsTitle("habitats_main_ANNEXhierarchy")%>
                        <a href="habitats-emerald-browser.jsp"><strong><%=cm.cmsPhrase("Resolution 4 habitat types hierarchical view")%></strong></a>
                      </td>
                      <td>
                        <%=cm.cmsPhrase("Visualisation of Bern Convention Resolution 4 habitat types (Emerald network)")%>
                      </td>
                    </tr>
                  </tbody>
                </table>
            </div>
            </div>
            <div class="eea-accordion-panel" style="clear: both;">
            <h2 class="notoc eea-icon-right-container"><%= cm.cmsPhrase("Advanced search") %></h2>
            <div class="pane">
                <h4><%=cm.cmsPhrase("Search habitat type information using more complex filtering capabilities") %></h4>
                <table class="fullwidth">
                <colgroup>
                    <col style="width: 350px; white-space: nowrap;">
                    <col>
                </colgroup>
                    <tbody>
                      <tr>
                        <td>
                          <img alt="" src="images/mini/bulletb.gif" width="6" height="6" style="vertical-align:middle" />
                          <a href="habitats-advanced.jsp?natureobject=Habitat"><strong><%=cm.cmsPhrase("Advanced Search")%></strong></a>
                          <%=cm.cmsTitle("habitats_main_advSearchSearchDesc")%>
                        </td>
                        <td>
                          <%=cm.cmsPhrase("Search habitat types information using more complex filtering capabilities")%>
                        </td>
                      </tr>
                      <tr>
                        <td>
                          <img alt="<%=cm.cms("how_to_use_advanced_search")%>" src="images/mini/bulletb.gif" width="6" height="6" style="vertical-align:middle" /><%=cm.cmsTitle("how_to_use_advanced_search")%>
                          <a href="advanced-help.jsp"><strong><%=cm.cmsPhrase("How to use Advanced search")%></strong></a>
                          <%=cm.cmsTitle("habitats_main_advSearchHelpDesc")%>
                        </td>
                        <td>
                          <%=cm.cmsPhrase("Help on habitat types <strong>Advanced Search</strong>")%>
                        </td>
                      </tr>
                    </tbody>
                  </table>
            </div>
            </div>
            <div class="eea-accordion-panel" style="clear: both;">
                <h2 class="notoc eea-icon-right-container"><%= cm.cmsPhrase("Downloads") %></h2>
                <div class="pane">
                    <table summary="layout" class="listing fullwidth">
                        <tbody>
                        <tr>
                            <td>
                                <a href="http://www.eea.europa.eu/data-and-maps/data/eunis-habitat-classification">
                                    <%=cm.cmsPhrase("EUNIS habitat classification")%>
                                </a>
                            </td>
                            <td>
                                <%=cm.cmsPhrase("The classification covers all types of habitat types from natural to artificial, from terrestrial to freshwater and marine.")%>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <a href="http://www.eea.europa.eu/data-and-maps/data/article-17-database-habitats-directive-92-43-eec">
                                    <%=cm.cmsPhrase("Conservation status of habitat types and species")%>
                                </a>
                            </td>
                            <td>
                                <%=cm.cmsPhrase("All EU Member States are requested by the Habitats Directive to monitor habitat types considered to be of Community interest")%>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <a href="http://www.eea.europa.eu/data-and-maps/data/natura-5">
                                    <%=cm.cmsPhrase("Natura 2000 data")%>
                                </a>
                            </td>
                            <td>
                                <%=cm.cmsPhrase("Natura 2000 is the European Union network of protected sites")%>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
      </div>


<!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>
