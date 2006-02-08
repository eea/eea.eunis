<%--
  - Author(s)   : The EUNIS Database Team.
  - Date        :
  - Copyright   : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Species National threat status' function - search page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="java.util.List,
                 ro.finsiel.eunis.jrfTables.Chm62edtGroupspeciesPersist,
                 ro.finsiel.eunis.jrfTables.Chm62edtGroupspeciesDomain,
                 java.util.Vector,
                 ro.finsiel.eunis.search.species.national.NationalThreatStatusForGroupSpecies,
                 ro.finsiel.eunis.jrfTables.species.national.NationalThreatStatusPersist,
                 ro.finsiel.eunis.search.Utilities,
                 ro.finsiel.eunis.WebContentManagement"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<html lang="<%=SessionManager.getCurrentLanguage()%>" xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=SessionManager.getCurrentLanguage()%>">
  <head>
    <jsp:include page="header-page.jsp" />
    <script language="JavaScript" src="script/species-country.js" type="text/javascript"></script>
    <%
      WebContentManagement cm = SessionManager.getWebContent();
    %>
    <script language="JavaScript" type="text/javascript">
    <!--
        function onLoadFunction() {
            <%
              if (SessionManager.isAuthenticated()&&SessionManager.isSave_search_criteria_RIGHT())
              {
            %>
                 document.eunis.saveCriteria.style.display = 'none';
            <%
              }
            %>

        document.eunis.action = 'species-threat-national-result.jsp';
     }

     <%
              if (SessionManager.isAuthenticated()&&SessionManager.isSave_search_criteria_RIGHT())
              {
      %>
        function checkSaveCriteria() {
        <%
           if(request.getParameter("idCountry") != null)
           {
        %>
        var country = '<%=request.getParameter("idCountry")%>';
        <%
           } else
           {
        %>
        var country = null;
        <%
           }
           if(request.getParameter("countryName") != null)
           {
        %>
        var countryName = '<%=request.getParameter("countryName")%>';
        <%
           } else
           {
        %>
        var countryName = null;
        <%
           }
           if(request.getParameter("idGroup") != null)
           {
        %>
        var group = '<%=request.getParameter("idGroup")%>';
        <%
           } else
           {
        %>
        var group = null;
        <%
           }
           if(request.getParameter("groupName") != null)
           {
        %>
        var groupName = '<%=request.getParameter("groupName")%>';
        <%
           } else
           {
        %>
        var groupName = null;
        <%
           }
        %>
        if(country != null && countryName != null && group != null && groupName != null)
        {
              var c = document.createElement("input");
              c.type= "hidden";
              c.name = "idCountry";
              c.value = country;
              document.eunis.appendChild( c );

              var cn = document.createElement("input");
              cn.type= "hidden";
              cn.name = "countryName";
              cn.value = countryName;
              document.eunis.appendChild( cn );

              var g = document.createElement("input");
              g.type= "hidden";
              g.name = "idGroup";
              g.value = group;
              document.eunis.appendChild( g );

              var gn = document.createElement("input");
              gn.type= "hidden";
              gn.name = "groupName";
              gn.value = groupName;
              document.eunis.appendChild( gn );

              document.eunis.saveCriteria.checked=true;
              document.eunis.action = 'species-threat-national.jsp';
              alert('<%=cm.cms("save_alert")%>');
              document.eunis.submit();
        } else
        {
              if(group != null && groupName != null)
              {
                 alert('<%=cm.cms("species_threat-national_22")%>');
                  var g = document.createElement("input");
                  g.type= "hidden";
                  g.name = "idGroup";
                  g.value = group;
                  document.eunis.appendChild( g );

                  var gn = document.createElement("input");
                  gn.type= "hidden";
                  gn.name = "groupName";
                  gn.value = groupName;
                  document.eunis.appendChild( gn );
              }
              else alert('<%=cm.cms("species_threat-national_23")%>');
              document.eunis.action = 'species-threat-national.jsp';
              document.eunis.submit();

        }
        }
            <%
            }
            %>

   //-->
    </script>
    <title>
      <%=application.getInitParameter("PAGE_TITLE")%>
      <%=cm.cms("species_threat-national_title")%>
    </title>
  </head>
<%
  String isSaveCriteriaChecked = ((request.getParameter("saveCriteria") == null ?
                                 "" :
                                 request.getParameter("saveCriteria")).equalsIgnoreCase("true") ?
                                 "checked=\"checked\"" :
                                 "");
  // REQUEST PARAMETER(S) (not necessarily required)
  // group - group where we do the search(id group)
  // groupName - The name of the group...
  String group = request.getParameter("idGroup");
  String country = request.getParameter("idCountry");
  String groupName = request.getParameter("groupName");
  String countryName = request.getParameter("countryName");
  final boolean anyGroupSelected = (null != groupName) ? groupName.equalsIgnoreCase("any") : false;


  boolean showGroup = false;
  boolean showCountry = false;
  boolean showStatus = false;
  boolean showVernacularNames = false;

   if(request.getParameter("firstTime") == null)
  {
      showGroup = true;
      showCountry = true;
      showStatus = true;
      showVernacularNames = true;

  } else
  {
      showGroup = Utilities.checkedStringToBoolean(request.getParameter("showGroup"), false);
      showCountry = Utilities.checkedStringToBoolean(request.getParameter("showCountry"), false);
      showStatus = Utilities.checkedStringToBoolean(request.getParameter("showStatus"), false);
      showVernacularNames = Utilities.checkedStringToBoolean(request.getParameter("showVernacularNames"), false);
  }

  String chekedGroup = (showGroup == true ? "checked=\"checked\"" : "");
  String chekedCountry = (showCountry == true ? "checked=\"checked\"" : "");
  String chekedStatus = (showStatus == true ? "checked=\"checked\"" : "");
  String chekedVernacular = (showVernacularNames == true ? "checked=\"checked\"" : "");

%>
  <body onload="onLoadFunction()">
  <div id="outline">
  <div id="alignment">
  <div id="content">
    <jsp:include page="header-dynamic.jsp">
      <jsp:param name="location" value="home_location#index.jsp,species_location#species.jsp,national_threat_status_location" />
      <jsp:param name="helpLink" value="species-help.jsp" />
    </jsp:include>
    <h1>
        <%=cm.cmsText("species_threat-national_01")%>
    </h1>
    <table summary="layout" width="100%" border="0">
      <tr>
        <td>
          <%=cm.cmsText("species_threat-national_21")%>
          <br />
          <br />
          <form name="eunis" method="post" action="species-threat-national-result.jsp">
          <table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td style="background-color:#EEEEEE">
                <strong>
                  <%=cm.cmsText("species_threat-national_02")%>
                </strong>
              </td>
            </tr>
              <tr>
                <td style="background-color:#EEEEEE">                
                  <input title="<%=cm.cms("group")%>" id="checkbox1" type="checkbox" name="showGroup" value="true" <%=chekedGroup%> />
                    <label for="checkbox1"><%=cm.cmsText("species_threat-national_03")%></label>
                    <%=cm.cmsTitle("group")%>
                  <input title="<%=cm.cms("country")%>" id="checkbox5" type="checkbox" name="showCountry" value="true" <%=chekedCountry%> />
                    <label for="checkbox5"><%=cm.cmsText("species_threat-national_country")%></label>
                    <%=cm.cmsTitle("country")%>
                  <input title="<%=cm.cms("status")%>" id="checkbox6" type="checkbox" name="showStatus" value="true" <%=chekedStatus%> />
                    <label for="checkbox6"><%=cm.cmsText("species_threat-national_status")%></label>
                    <%=cm.cmsTitle("status")%>
                  <input title="<%=cm.cms("scientific_name")%>" id="checkbox2" type="checkbox" name="true" value="true" disabled="disabled" checked="checked" />
                    <label for="checkbox2"><%=cm.cmsText("species_threat-national_06")%></label>
                    <%=cm.cmsTitle("scientific_name")%>
                  <input title="<%=cm.cms("vernacular_name")%>" id="checkbox3" type="checkbox" name="showVernacularNames" value="true" <%=chekedVernacular%> />
                    <label for="checkbox3"><%=cm.cmsText("species_threat-national_07")%></label>
                    <%=cm.cmsTitle("vernacular_name")%>
                </td>
              </tr>
              <tr>
                <td style="background-color:#FFFFFF">&nbsp;</td>
              </tr>
          </table>
          <br />
          <table summary="layout" cellspacing="1" cellpadding="0" border="0" width="100%" style="text-align:left">
            <tr>
              <td style="vertical-align:middle">
                <img width="11" height="12" style="vertical-align:middle" alt="<%=cm.cms("species_threat-national_11")%>" title="<%=cm.cms("species_threat-national_11")%>" src="images/mini/field_mandatory.gif" />&nbsp;
                <%=cm.cmsAlt("species_threat-national_11")%>
                <%
                  if (null == group)
                  {
                    // If group is null then display the group selection texbox
                %>
                  <label for="select1" class="noshow"><%=cm.cms("group_name")%></label>
                  <select id="select1" title="<%=cm.cms("group_name")%>" name="Group" onchange="MM_jumpMenuInternational('parent',this,0)" class="inputTextField">
                    <option value="species-threat-national.jsp"><%=cm.cms("species_threat-national_12")%></option>
                    <option value="species-threat-national.jsp?idGroup=-1&amp;groupName=any"
                            title="Species threat national status"><%=cm.cms("species_threat-national_13")%>
                    </option>
                    <% // Species groups list
                       List groups = new Chm62edtGroupspeciesDomain().findWhereDistinct("");
                       if (groups != null && groups.size() > 0)
                       {
                         for (int i = 0;i<groups.size();i++)
                         {
                            Chm62edtGroupspeciesPersist aGroup = (Chm62edtGroupspeciesPersist)groups.get(i);%>
                            <option value="species-threat-national.jsp?idGroup=<%=aGroup.getIdGroupspecies()%>&amp;groupName=<%=(aGroup.getCommonName() != null ? aGroup.getCommonName().replaceAll("&","&amp;") : "")%>">
                              <%=(aGroup.getCommonName() != null ? aGroup.getCommonName().replaceAll("&","&amp;") : "")%>
                            </option>
                    <%
                         }
                       }
                    %>
                  </select>
                  <%=cm.cmsLabel("group_name")%>
                  <%=cm.cmsTitle("group_name")%>
                <%
                  } else {
                    // or else put out the selected group and let the user select the country name.
                    if (null == country)
                    {
                      if (anyGroupSelected)
                      {
                %>
                        <strong>
                          <%=cm.cmsText("species_threat-national_13")%>
                        </strong>
                        &nbsp;
                        <strong>
                          <%=cm.cmsText("species_threat-national_15")%>
                        </strong>
                        &nbsp;
                        <%=cm.cmsText("species_threat-national_14")%>
                  <%
                      }
                      else
                      {
                        String groupNameDispayed = (-1 == groupName.lastIndexOf("Mosses") ? groupName : "Mosses &amp; Liverworts");
                    %>
                        <strong>
                          <%=groupNameDispayed%>
                        </strong>
                        &nbsp;
                        <strong>
                          <%=cm.cmsText("species_threat-national_15")%>
                        </strong>
                        &nbsp;
                        <%=cm.cmsText("species_threat-national_14")%>
                    <%
                      }
                    %>
                      <label for="select2" class="noshow"><%=cm.cms("country")%></label>
                      <select id="select2" title="<%=cm.cms("country")%>" name="Contry" onchange="MM_jumpMenuInternational('parent',this,0)"
                              class="inputTextField">
                        <option value="species-threat-national.jsp?idGroup=<%=group%>&amp;groupName=<%=groupName%>" selected="selected">
                          <%=cm.cms("species_threat-national_16")%>
                        </option>
                        <%
                        NationalThreatStatusForGroupSpecies a = new NationalThreatStatusForGroupSpecies(group,"-1");
                        a.setCountriesForAnyGroup();
                        a.setCountriesForAGroup();
                        Vector CountriesForAnyGroup = a.getCountriesForAnyGroup();
                        Vector CountriesForAGroup = a.getCountriesForAGroup();
                        if (!anyGroupSelected)
                        {
                          if (CountriesForAGroup != null && CountriesForAGroup.size() > 0)
                          {
                            for (int i = 0;i<CountriesForAGroup.size();i++)
                            {
                        %>
                              <option value="species-threat-national.jsp?groupName=<%=groupName%>&amp;idGroup=<%=group%>&amp;countryName=<%=((NationalThreatStatusPersist)CountriesForAGroup.get(i)).getCountry()%>&amp;idCountry=<%=((NationalThreatStatusPersist)CountriesForAGroup.get(i)).getIdCountry()%>">
                                <%=((NationalThreatStatusPersist)CountriesForAGroup.get(i)).getCountry()%>
                              </option>
                      <%
                            }
                          }
                        } else {
                          if (CountriesForAnyGroup != null && CountriesForAnyGroup.size() > 0)
                          {
                            for (int i=0;i<CountriesForAnyGroup.size();i++)
                            {
                      %>
                              <option value="species-threat-national.jsp?groupName=<%=groupName%>&amp;idGroup=<%=group%>&amp;countryName=<%=((NationalThreatStatusPersist)CountriesForAnyGroup.get(i)).getCountry()%>&amp;idCountry=<%=((NationalThreatStatusPersist)CountriesForAnyGroup.get(i)).getIdCountry()%>">
                                <%=((NationalThreatStatusPersist)CountriesForAnyGroup.get(i)).getCountry()%>
                              </option>
                     <%
                            }
                          }
                        }
                     %>
                      </select>
                      <%=cm.cmsLabel("country")%>
                      <%=cm.cmsTitle("country")%>
                  <%
                    }
                    else
                    {
                      if (anyGroupSelected)
                      {
                %>
                        <strong>
                          <%=cm.cmsText("species_threat-national_13")%>
                        </strong>
                        &nbsp;
                        <strong>
                          <%=cm.cmsText("species_threat-national_15")%>
                        </strong>
                        &nbsp;
                        <%=cm.cmsText("species_threat-national_14")%>
                  <%
                    } else {
                      String groupNameDispayed = (-1 == groupName.lastIndexOf("Mosses") ? groupName : "Mosses &amp; Liverworts");
                    %>
                        <strong>
                          <%=groupNameDispayed%>
                        </strong>
                        &nbsp;
                        <strong>
                          <%=cm.cmsText("species_threat-national_15")%>
                        </strong>
                        &nbsp;
                        <%=cm.cmsText("species_threat-national_14")%>
                    <%
                    }
                    %>
                      &nbsp;
                      <strong>
                        <%=countryName%>
                      </strong>
                      &nbsp;
                      <strong>
                        <%=cm.cmsText("species_threat-national_15")%>
                      </strong>
                      &nbsp;
                      <%=cm.cmsText("species_threat-national_17")%>
                      <label for="select3" class="noshow"><%=cm.cms("status")%></label>
                      <select id="select3" title="<%=cm.cms("status")%>" name="Status" onchange="MM_jumpMenuInternational('parent',this,0)"
                              class="inputTextField">
                        <option value="species-threat-national.jsp?idGroup=<%=group%>&amp;groupName=<%=groupName%>&amp;idCountry=<%=country%>&amp;countryName=<%=countryName%>" selected="selected">
                          <%=cm.cms("species_threat-national_18")%>
                        </option>
                        <option value="species-threat-national-result.jsp?idCountry=<%=country%>&amp;countryName=<%=countryName%>&amp;idGroup=<%=group%>&amp;groupName=<%=groupName%>&amp;idConservation=-1&amp;statusName=any">
                          <%=cm.cms("species_threat-national_19")%>
                        </option>
                        <%
                          NationalThreatStatusForGroupSpecies a = new NationalThreatStatusForGroupSpecies(group,country);
                          a.setThreatStatusForAnyGroupAndACountry();
                          a.setThreatStatusForAGroupAndACountry();
                          Vector ThreatStatusForAnyGroupAndACountry = a.getThreatStatusForAnyGroupAndACountry();
                          Vector ThreatStatusForAGroupAndACountry = a.getThreatStatusForAGroupAndACountry();
                          if (!anyGroupSelected)
                          {
                            if (ThreatStatusForAGroupAndACountry != null && ThreatStatusForAGroupAndACountry.size() > 0)
                            {
                              for (int i = 0;i<ThreatStatusForAGroupAndACountry.size();i++)
                              {
                        %>
                                <option value="species-threat-national-result.jsp?idCountry=<%=country%>&amp;countryName=<%=countryName%>&amp;groupName=<%=groupName%>&amp;idGroup=<%=group%>&amp;statusName=<%=((NationalThreatStatusPersist)ThreatStatusForAGroupAndACountry.get(i)).getDefAbrev()%>&amp;idConservation=<%=((NationalThreatStatusPersist)ThreatStatusForAGroupAndACountry.get(i)).getIdCons()%>">
                                  <%=((NationalThreatStatusPersist)ThreatStatusForAGroupAndACountry.get(i)).getDefAbrev()%>
                                </option>
                        <%
                              }
                            }
                          }
                          else
                          {
                            if (ThreatStatusForAnyGroupAndACountry != null && ThreatStatusForAnyGroupAndACountry.size() > 0)
                            {
                              for (int i=0;i<ThreatStatusForAnyGroupAndACountry.size();i++)
                              {
                        %>
                                <option value="species-threat-national-result.jsp?idCountry=<%=country%>&amp;countryName=<%=countryName%>&amp;groupName=<%=groupName%>&amp;idGroup=<%=group%>&amp;statusName=<%=((NationalThreatStatusPersist)ThreatStatusForAnyGroupAndACountry.get(i)).getDefAbrev()%>&amp;idConservation=<%=((NationalThreatStatusPersist)ThreatStatusForAnyGroupAndACountry.get(i)).getIdCons()%>">
                                  <%=((NationalThreatStatusPersist)ThreatStatusForAnyGroupAndACountry.get(i)).getDefAbrev()%>
                                </option>
                        <%
                              }
                            }
                          }
                        %>
                          </select>
                          <%=cm.cmsLabel("status")%>
                          <%=cm.cmsTitle("status")%>
                        <%
                        }
                  }
%>
                 </td>
            </tr>
           <%
                if (SessionManager.isAuthenticated() && SessionManager.isSave_search_criteria_RIGHT())
                {
              %>
                  <tr>
                    <td>&nbsp; <br />
                      <input title="<%=cm.cms("save_criteria")%>" id="saveCriteria" type="checkbox" name="saveCriteria" value="true" <%=isSaveCriteriaChecked%> />
                      <%=cm.cmsTitle("save_criteria")%>
                      <label for="saveCriteria"><%=cm.cmsText("species_threat-national_10")%></label>
                      &nbsp;
                      <a title="<%=cm.cms("save_criteria")%>" href="javascript:checkSaveCriteria()"><img alt="<%=cm.cms("save_criteria")%>" border="0" src="images/save.jpg" width="21" height="19" style="vertical-align:middle" /></a><%=cm.cmsTitle("save_criteria")%>
                    </td>
                  </tr>
                  <tr><td>&nbsp;</td></tr>
              <%
                }
              %>
          </table>
          </form>
      </td>
    </tr>
    <tr>
      <td style="text-align:center">
        <strong>
          <%=cm.cmsText("species_threat-national_20")%>
        </strong>
      </td>
    </tr>
  </table>
<%
    // Expand saved searches list for this jsp page
    if (SessionManager.isAuthenticated()&&SessionManager.isSave_search_criteria_RIGHT())
    {
      // Set Vector for URL string
      Vector show = new Vector();
      show.addElement("showGroup");
      show.addElement("showFamily");
      show.addElement("showOrder");
      show.addElement("showScientificName");
      show.addElement("showVernacularNames");
      String pageName = "species-threat-national.jsp";
      String pageNameResult = "species-threat-national-result.jsp?"+Utilities.writeURLCriteriaSave(show);
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

<%=cm.br()%>
<%=cm.cmsMsg("save_alert")%>
<%=cm.br()%>
<%=cm.cmsMsg("species_threat-national_22")%>
<%=cm.br()%>
<%=cm.cmsMsg("species_threat-national_23")%>
<%=cm.br()%>
<%=cm.cmsMsg("species_threat-national_title")%>
<%=cm.br()%>
<%=cm.cmsMsg("species_threat-national_12")%>
<%=cm.br()%>
<%=cm.cmsMsg("species_threat-national_13")%>
<%=cm.br()%>
<%=cm.cmsMsg("species_threat-national_16")%>
<%=cm.br()%>
<%=cm.cmsMsg("species_threat-national_18")%>
<%=cm.br()%>
<%=cm.cmsMsg("species_threat-national_19")%>
<%=cm.br()%>

  <jsp:include page="footer.jsp">
    <jsp:param name="page_name" value="species-threat-national.jsp" />
  </jsp:include>
  </div>
  </div>
  </div>
    </body>
</html>