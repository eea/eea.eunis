<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : "Sites neighborhood" function - Display sites which are in specified range of a selected site.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.search.Utilities,
                 ro.finsiel.eunis.search.sites.SitesSearchUtility,
                 java.util.List,
                 ro.finsiel.eunis.WebContentManagement,
                 ro.finsiel.eunis.jrfTables.Chm62edtSitesPersist,
                 ro.finsiel.eunis.jrfTables.Chm62edtSitesDomain,
                 ro.finsiel.eunis.search.sites.neighborhood.NeighborhoodPaginator,
                 ro.finsiel.eunis.jrfTables.sites.neighborhood.NeighborhoodDomain,
                 java.util.Vector,
                 ro.finsiel.eunis.search.AbstractSortCriteria,
                 ro.finsiel.eunis.search.sites.neighborhood.NeighborhoodDetailSortCriteria,
                 ro.finsiel.eunis.search.CountryUtil"%>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session"/>
<jsp:useBean id="formBean" class="ro.finsiel.eunis.search.sites.neighborhood.NeighborhoodBean" scope="page">
  <jsp:setProperty name="formBean" property="*"/>
</jsp:useBean>
<%
  WebContentManagement cm = SessionManager.getWebContent();
  String idSite = request.getParameter("idsite");
  String countryCode = request.getParameter("countryCode");
  countryCode = CountryUtil.areaNameEn2ISO2L(countryCode);// Translate it into CODE (ISO)
  float radius = Utilities.checkedStringToFloat(request.getParameter("radius"), 0.0F);
  int currentPage = Utilities.checkedStringToInt(request.getParameter("currentPage"), 0);
  if (currentPage < 0) currentPage = 0;
  int pageSize = Utilities.checkedStringToInt(request.getParameter("pageSize"), 10);
  List sites;

  Vector<String> hiddenParams = new Vector<String>();
  hiddenParams.addElement("sort");
  hiddenParams.addElement("ascendency");
  hiddenParams.addElement("criteriaSearch");
  hiddenParams.addElement("pageSize");

  StringBuffer toFORMParam = Utilities.writeFormParameter("idsite", idSite);
  toFORMParam.append(Utilities.writeFormParameter("radius", radius));
  toFORMParam.append(Utilities.writeFormParameter("countryCode", countryCode));
  toFORMParam.append(formBean.toFORMParam(hiddenParams));

  // Initialize URL and FORM parameters, passed to links and forms from page.
  // Also add this page specific fields to the URL/FORM fields written on links/forms.
  String toURLParam = "idsite=" + idSite;
  toURLParam += Utilities.writeURLParameter("radius", radius);
  toURLParam += Utilities.writeURLParameter("pageSize", pageSize);
  toURLParam += Utilities.writeURLParameter("countryCode", countryCode);
  toURLParam += formBean.toURLParam(hiddenParams);

  // Try to find the site with ID passed on the request.
  Chm62edtSitesPersist mainSite;
  sites = new Chm62edtSitesDomain().findWhere("ID_SITE='" + idSite + "'");
  String pageName = "sites-neighborhood-detail.jsp";
  int guid = 0;
  // This is the paginator for the results.
  NeighborhoodPaginator paginator;
  float originX;
  float originY;
  mainSite = (Chm62edtSitesPersist)sites.get(0);
  // Compute circle's center
  originX = Utilities.checkedStringToFloat(mainSite.getLongitude(), 0);
  originY = Utilities.checkedStringToFloat(mainSite.getLatitude(), 0);
  paginator = new NeighborhoodPaginator(new NeighborhoodDomain(mainSite.getIdSite(), radius, originX, originY, formBean.toSortCriteriaDetailsPage()));

  paginator.setPageSize( pageSize );
  int pagesCount = 0;
  try
  {
    pagesCount = paginator.countPages();
    sites = paginator.getPage(currentPage);
  } catch (Exception e) {

  }
  String eeaHome = application.getInitParameter( "EEA_HOME" );
  String location = "eea#" + eeaHome + ",home#index.jsp,sites#sites.jsp,sites_neighborhood_location#sites-neighborhood.jsp,sites_neighborhood_detail_location";
  String btrail = "eea#" + eeaHome + ",home#index.jsp,sites#sites.jsp,sites_neighborhood_location#sites-neighborhood.jsp,sites_neighborhood_detail_location";
  if (sites.isEmpty())
  {
%>
      <jsp:forward page="emptyresults.jsp">
        <jsp:param name="location" value="<%=location%>" />
      </jsp:forward>
<%
  }
%>

<%
    String tsvLink ="";
    int maxSitesPerMap = 0;
    String ids = "";

    if (sites.size() > 0)
    {
        maxSitesPerMap = Utilities.checkedStringToInt( application.getInitParameter( "MAX_SITES_PER_MAP" ), 2000 );
        NeighborhoodPaginator mapPaginator = new NeighborhoodPaginator(new NeighborhoodDomain(mainSite.getIdSite(), radius, originX, originY, formBean.toSortCriteriaDetailsPage()));
        try
        {
            mapPaginator.setPageSize( mapPaginator.countResults() );
            List mapSites = mapPaginator.getPage(0);
            if ( mapSites.size() < maxSitesPerMap )
            {
                int i;
                for (i = 0; i < mapSites.size(); i++)
                {
                    Chm62edtSitesPersist site = (Chm62edtSitesPersist)mapSites.get(i);
                    ids += "'" + site.getIdSite() + "'";
                    if ( i < mapSites.size() - 1 ) ids += ",";
                }
            }
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
        }
        Vector reportFields = new Vector();
        reportFields.addElement("sort");
        reportFields.addElement("ascendency");
        reportFields.addElement("criteriaSearch");
        reportFields.addElement("criteriaSearch");
        reportFields.addElement("oper");
        reportFields.addElement("criteriaType");
        tsvLink = "javascript:openTSVDownload('reports/sites/tsv-sites-neighborhood.jsp?" + formBean.toURLParam(reportFields) + "&idsite=" + idSite + "&radius=" + radius +  "')";
    }
%>
<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cms("sites_neighborhood-detail_title") %>'/>

<stripes:layout-render name="/stripes/common/template.jsp" helpLink="sites-help.jsp" pageTitle="${title}" downloadLink="<%= tsvLink%>"  btrail="<%= btrail%>">
    <stripes:layout-component name="head">
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/eea_search.css">

        <script type="text/javascript" language="javascript">
          //<![CDATA[
          function openlink( URL )
          {
            eval("page = window.open(URL, '', 'scrollbars=yes,toolbar=0,resizable=yes, location=0,width=450,height=280,left=490,top=0');");
          }
          //]]>
        </script>
    </stripes:layout-component>
    <stripes:layout-component name="contents">

    <%
        if (sites.size() > 0)
        {
    %>

        <a name="documentContent"></a>
          <h1>
            <%=cm.cmsPhrase("Site neighborhood")%>
          </h1>
<!-- MAIN CONTENT -->
                      <br />
                      <%=cm.cmsPhrase("Found")%>
                      <strong>
                      <%=paginator.countResults()%>
                      </strong>
                      <%=cm.cmsPhrase("site(s) in the range of aprox.")%>
                      <strong>
                        <%=radius%>
                      </strong>
                      <%=cm.cmsPhrase("km of &nbsp;site&nbsp;")%>
                      <strong>
                        <%=mainSite.getName()%>
                      </strong>
                      <br />
                <%
                  if ( sites.size() < maxSitesPerMap )
                  {
                %>
                      <br />
                      <form name="gis" action="sites-gis-tool.jsp" target="_blank" method="post">
                        <input type="hidden" name="sites" value="<%=ids%>" />
                        <input type="submit" id="showMapGIS" name="Show map" value="<%=cm.cms("show_map")%>" title="<%=cm.cms("show_map")%>" class="standardButton" />
                        <%=cm.cmsTitle("show_map")%>
                        <%=cm.cmsInput("show_map")%>
                      </form>
                <%
                  }
                %>
                      <jsp:include page="pagesize.jsp">
                        <jsp:param name="guid" value="<%=guid%>"/>
                        <jsp:param name="pageName" value="<%=pageName%>"/>
                        <jsp:param name="pageSize" value="<%=pageSize%>"/>
                        <jsp:param name="toFORMParam" value="<%=toFORMParam%>"/>
                      </jsp:include>
                      <br />
                      <jsp:include page="navigator.jsp">
                        <jsp:param name="pagesCount" value="<%=pagesCount%>"/>
                        <jsp:param name="pageName" value="<%=pageName%>"/>
                        <jsp:param name="guid" value="<%=guid%>"/>
                        <jsp:param name="currentPage" value="<%=currentPage%>"/>
                        <jsp:param name="toURLParam" value="<%=toURLParam%>"/>
                        <jsp:param name="toFORMParam" value="<%=toFORMParam%>"/>
                      </jsp:include>
                <%
                  // Compute the sort criteria
                  Vector sortURLFields = new Vector();      /* Used for sorting */
                  sortURLFields.addElement("pageSize");
                  sortURLFields.addElement("criteriaSearch");
                  String urlSortString = formBean.toURLParam(sortURLFields);
                  urlSortString += "&amp;idsite=" + idSite;
                  AbstractSortCriteria sortSourceDB = formBean.lookupSortCriteria(NeighborhoodDetailSortCriteria.SORT_SOURCE_DB);
                  AbstractSortCriteria sortName = formBean.lookupSortCriteria(NeighborhoodDetailSortCriteria.SORT_NAME);
                  AbstractSortCriteria sortSize = formBean.lookupSortCriteria(NeighborhoodDetailSortCriteria.SORT_SIZE);
                %>
                      <a name="dataTable"></a>
                      <table class="sortable listing" width="100%" summary="<%=cm.cmsPhrase("Search results")%>">
                        <thead>
                          <tr>
                            <th class="nosort" scope="col" nowrap="nowrap">
                              <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NeighborhoodDetailSortCriteria.SORT_SOURCE_DB%>&amp;ascendency=<%=formBean.changeAscendency(sortSourceDB, sortSourceDB == null )%>#dataTable"><%=Utilities.getSortImageTag(sortSourceDB)%><%=cm.cmsPhrase("Source data set")%></a>
                            </th>
                            <th class="nosort" scope="col" nowrap="nowrap">
                              <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NeighborhoodDetailSortCriteria.SORT_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sortName, sortName == null )%>#dataTable"><%=Utilities.getSortImageTag(sortName)%><%=cm.cmsPhrase("Site name")%></a>
                            </th>
                            <th class="nosort" scope="col" nowrap="nowrap" nowrap="nowrap" style="text-align : right;">
                              <%=cm.cmsPhrase("&nbsp;Distance (km)")%>
                            </th>
                            <th class="nosort" scope="col" nowrap="nowrap" style="text-align : center;">

                              <%=cm.cmsPhrase("Longitude")%>
                            </th>
                            <th class="nosort" scope="col" nowrap="nowrap" style="text-align : center;">
                              <%=cm.cmsPhrase("Latitude")%>
                            </th>
                            <th class="nosort" scope="col" align="right" nowrap="nowrap" style="text-align : right;">
                              <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NeighborhoodDetailSortCriteria.SORT_SIZE%>&amp;ascendency=<%=formBean.changeAscendency(sortSize, sortSize == null )%>#dataTable"><%=Utilities.getSortImageTag(sortSize)%><%=cm.cmsPhrase("Size(ha)")%></a>
                            </th>
                          </tr>
                        </thead>
                        <tbody>
                <%
                    for (int i = 0; i < sites.size(); i++)
                    {
                      Chm62edtSitesPersist site = (Chm62edtSitesPersist)sites.get(i);
                %>
                        <tr>
                          <td>
                            <strong>
                              <%=SitesSearchUtility.translateSourceDB(site.getSourceDB())%>
                            </strong>
                          </td>
                          <td>
                            <a href="sites/<%=site.getIdSite()%>"><%=site.getName()%></a>
                          </td>
                          <td align="right" nowrap="nowrap">
                            <%=Utilities.formatArea("" + SitesSearchUtility.distanceBetweenSites(mainSite, site), 4, 3, "&nbsp;")%>
                          </td>
                          <td align="center" nowrap="nowrap">
                            <%=SitesSearchUtility.formatLongitude(site.getLongitude())%>
                          </td>
                          <td align="center" nowrap="nowrap">
                            <%=SitesSearchUtility.formatLatitude(site.getLatitude())%>
                          </td>
                          <td align="right" nowrap="nowrap">
                            <%=Utilities.formatArea(site.getArea(), 5, 2, "&nbsp;")%>
                          </td>
                        </tr>
                <%
                    }
                %>
                        </tbody>
                        <thead>
                          <tr>
                            <th scope="col" nowrap="nowrap">
                              <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NeighborhoodDetailSortCriteria.SORT_SOURCE_DB%>&amp;ascendency=<%=formBean.changeAscendency(sortSourceDB, sortSourceDB == null )%>#dataTable"><%=Utilities.getSortImageTag(sortSourceDB)%><%=cm.cmsPhrase("Source data set")%></a>
                            </th>
                            <th scope="col" nowrap="nowrap">
                              <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NeighborhoodDetailSortCriteria.SORT_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sortName, sortName == null )%>#dataTable"><%=Utilities.getSortImageTag(sortName)%><%=cm.cmsPhrase("Site name")%></a>
                            </th>
                            <th scope="col" nowrap="nowrap" nowrap="nowrap" style="text-align : right;">
                              &nbsp;<%=cm.cmsPhrase("Distance (km)")%>
                            </th>
                            <th scope="col" nowrap="nowrap" style="text-align : center;">

                              <%=cm.cmsPhrase("Longitude")%>
                            </th>
                            <th scope="col" nowrap="nowrap" style="text-align : center;">
                              <%=cm.cmsPhrase("Latitude")%>
                            </th>
                            <th scope="col" align="right" nowrap="nowrap" style="text-align : right;">
                              <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NeighborhoodDetailSortCriteria.SORT_SIZE%>&amp;ascendency=<%=formBean.changeAscendency(sortSize, sortSize == null )%>#dataTable"><%=Utilities.getSortImageTag(sortSize)%><%=cm.cmsPhrase("Size(ha)")%></a>
                            </th>
                          </tr>
                        </thead>
                      </table>
                <%
                  }
                  else
                  {
                %>
                      <%--<jsp:include page="header-dynamic.jsp">--%>
                        <%--<jsp:param name="location" value="<%=btrail%>"/>--%>
                        <%--<jsp:param name="mapLink" value="show"/>--%>
                      <%--</jsp:include>--%>
                        <h1>
                          <%=cm.cmsPhrase("Site neighborhood")%>
                        </h1>

                        <br />
                        <%=cm.cms("sites_neighborhood_detail_nosites")%> <%=mainSite.getName()%>.
                        <br />
                        <%=cm.cms("sites_neighborhood_searchradius")%> <%=radius%> km.
                        <br />
                        <br />
                <%
                  }
                %>

                      <%=cm.cmsMsg("sites_neighborhood-detail_title")%>
<!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>