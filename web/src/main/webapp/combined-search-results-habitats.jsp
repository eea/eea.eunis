<%--
  - Author(s)   : The EUNIS Database Team.
  - Date        :
  - Copyright   : (c) 2002-2005 EEA - European Environment Agency.
  - Description : Results page for 'Combined search' function when starting nature object was Habitats.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="java.util.*,
                 ro.finsiel.eunis.search.AbstractPaginator,
                 ro.finsiel.eunis.search.combined.CombinedSearchPaginator,
                 ro.finsiel.eunis.jrfTables.combined.HabitatsCombinedDomain,
                 ro.finsiel.eunis.jrfTables.combined.HabitatsCombinedPersist,
                 ro.finsiel.eunis.search.Utilities,
                 ro.finsiel.eunis.search.advanced.AdvancedSortCriteria,
                 ro.finsiel.eunis.WebContentManagement,
                 ro.finsiel.eunis.search.AbstractSortCriteria" %>
<jsp:useBean id="formBean" class="ro.finsiel.eunis.formBeans.CombinedSearchBean" scope="page">
  <jsp:setProperty name="formBean" property="*"/>
</jsp:useBean>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session"/>
<%
  WebContentManagement cm = SessionManager.getWebContent();
  String eeaHome = application.getInitParameter( "EEA_HOME" );
  String btrail = "eea#" + eeaHome + ",home#index.jsp,combined_search#combined-search.jsp,results";
%>

<%
    // Database where to search. Possible values are: Species, Habitats or Sites
    String searchedDatabase = formBean.getSearchedNatureObject();
    AbstractPaginator paginator = new CombinedSearchPaginator(new HabitatsCombinedDomain(request.getSession().getId()));
    int currentPage = Utilities.checkedStringToInt(formBean.getCurrentPage(), 0);
    paginator.setSortCriteria(formBean.toSortCriteria());
    paginator.setPageSize(Utilities.checkedStringToInt(formBean.getPageSize(), AbstractPaginator.DEFAULT_PAGE_SIZE));
    currentPage = paginator.setCurrentPage(currentPage);// Compute *REAL* current page (adjusted if user messes up)
    int resultsCount = paginator.countResults();
    final String pageName = "combined-search-results-habitats.jsp";
    int pagesCount = paginator.countPages();// This is used in @page include...
    int guid = 0;// This is used in @page include...
    // Now extract the results for the current page.
    List results = paginator.getPage(currentPage);
    Iterator it = (null != results) ? results.iterator() : new Vector().iterator();

    Vector columnsDisplayed = formBean.parseShowColumns();
    boolean showLevel = (columnsDisplayed.contains("showLevel")) ? true : false;
    boolean showEUNISCode = (columnsDisplayed.contains("showEUNISCode")) ? true : false;
    boolean showANNEXCode = (columnsDisplayed.contains("showANNEXCode")) ? true : false;
    boolean showScientificName = (columnsDisplayed.contains("showScientificName")) ? true : false;
    boolean showEnglishName = (columnsDisplayed.contains("showEnglishName")) ? true : false;
    boolean showLegalInstruments = (columnsDisplayed.contains("showLegalInstruments")) ? true : false;
    boolean showCountry = (columnsDisplayed.contains("showCountry")) ? true : false;
    boolean showRegion = (columnsDisplayed.contains("showRegion")) ? true : false;
    boolean showReferences = (columnsDisplayed.contains("showReferences")) ? true : false;
    boolean showDiagram = (columnsDisplayed.contains("showDiagram")) ? true : false;
    if(results.isEmpty())
    {
%>
<jsp:forward page="emptyresults.jsp">
    <jsp:param name="location" value="<%=btrail%>" />
</jsp:forward>
<%
    }
%>
<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cms("generic_combined-search-results-habitats_title") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" pageTitle="${title}" btrail="<%= btrail%>">
    <stripes:layout-component name="head">
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/eea_search.css">
        <script language="JavaScript" src="<%=request.getContextPath()%>/script/species-result.js" type="text/javascript"></script>
    </stripes:layout-component>
    <stripes:layout-component name="contents">

        <a name="documentContent"></a>
		<h1><%=cm.cmsPhrase("Habitat types combined search results")%></h1>

<!-- MAIN CONTENT -->
                <table summary="layout" width="100%" border=0 cellspacing="0" cellpadding="0">
                  <tr>
                    <td colspan="3">
                      <%=cm.cmsPhrase("You searched for habitat types<br />")%>
                      <table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                          <td>
                            <%=cm.cmsPhrase("You searched habitat types using combined search criteria")%>
                          </td>
                        </tr>
                        <tr>
                        <td bgcolor="#FFFFFF">
                          <%=SessionManager.getCombinednatureobject1()%>
                          <%=cm.cmsPhrase(" combination used:&nbsp;")%>
                          <%=SessionManager.getCombinedexplainedcriteria1()%>
                          <%=cm.cmsPhrase(", where:")%>
                        </td>
                      </tr>
                      <tr>
                        <td bgcolor="#FFFFFF">
                          <%=SessionManager.getCombinedlistcriteria1() != null ? SessionManager.getCombinedlistcriteria1() : "&nbsp;"%>
                        </td>
                      </tr>
                <%
                  if(SessionManager.getCombinednatureobject2() != null)
                  {
                %>
                      <tr>
                        <td bgcolor="#FFFFFF">
                          <%=SessionManager.getCombinednatureobject2()%>
                          <%=cm.cmsPhrase(" combination used:&nbsp;")%>
                          <%=SessionManager.getCombinedexplainedcriteria2()%>
                          <%=cm.cmsPhrase(", where:")%>
                        </td>
                      </tr>
                      <tr>
                        <td bgcolor="#FFFFFF">
                          <%=SessionManager.getCombinedlistcriteria2() != null ? SessionManager.getCombinedlistcriteria2() : "&nbsp;"%>
                        </td>
                      </tr>
                <%
                    if(SessionManager.getCombinednatureobject2().equalsIgnoreCase("Sites"))
                    {
                %>
                      <tr>
                        <td bgcolor="#FFFFFF">
                          <%=cm.cmsPhrase("Source data sets:")%>
                          <%=SessionManager.getSourcedb()%>
                        </td>
                      </tr>
                <%
                    }
                  }
                %>
                <%
                  if(SessionManager.getCombinednatureobject3() != null)
                  {
                %>
                      <tr>
                        <td bgcolor="#FFFFFF">
                          <%=SessionManager.getCombinednatureobject3()%>
                          <%=cm.cmsPhrase(" combination used:&nbsp;")%>
                          <%=SessionManager.getCombinedexplainedcriteria3()%>
                          <%=cm.cmsPhrase(", where:")%>
                        </td>
                      </tr>
                      <tr>
                        <td bgcolor="#FFFFFF">
                          <%=SessionManager.getCombinedlistcriteria3() != null ? SessionManager.getCombinedlistcriteria3() : "&nbsp;"%>
                        </td>
                      </tr>
                <%
                    if(SessionManager.getCombinednatureobject3().equalsIgnoreCase("Sites"))
                    {
                %>
                      <tr>
                        <td bgcolor="#FFFFFF">
                          <%=cm.cmsPhrase("Source data sets:")%>
                          <%=SessionManager.getSourcedb()%>
                        </td>
                      </tr>
                <%
                    }
                  }
                %>
                <%
                  if(SessionManager.getCombinedcombinationtype() != null)
                  {
                %>
                      <tr>
                        <td bgcolor="#FFFFFF">
                          <%=cm.cmsPhrase("Combination type: ")%>
                          <%=SessionManager.getCombinedcombinationtype()%>
                        </td>
                      </tr>
                <%
                } else {
                  if(SessionManager.getCombinednatureobject2() != null)
                  {
                %>
                      <tr>
                        <td bgcolor="#FFFFFF">
                          <%=cm.cmsPhrase("Combination type: ")%>
                          <%=SessionManager.getCombinednatureobject1()%>
                          <%=cm.cmsPhrase(" related to ")%>
                          <%=SessionManager.getCombinednatureobject2()%>
                        </td>
                      </tr>
                <%
                  }
                  if(SessionManager.getCombinednatureobject3() != null)
                  {
                %>
                      <tr>
                        <td bgcolor="#FFFFFF">
                          <%=cm.cmsPhrase("Combination type: ")%>
                          <%=SessionManager.getCombinednatureobject1()%>
                          <%=cm.cmsPhrase(" related to ")%>
                          <%=SessionManager.getCombinednatureobject3()%>
                        </td>
                      </tr>
                <%
                  }
                }
                %>
                    </table>

                    <%=cm.cmsPhrase("Results found")%>
                    <strong>
                      <%=resultsCount%>
                    </strong>
                    <br />
                <%
                  // Prepare parameters for pagesize.jsp
                  Vector pageSizeFormFields = new Vector();       /*  These fields are used by pagesize.jsp, included below.    */
                  pageSizeFormFields.addElement("sort");          /*  *NOTE* I didn't add currentPage & pageSize since pageSize */
                  pageSizeFormFields.addElement("ascendency");    /*   is overriden & also pageSize is set to default           */
                  pageSizeFormFields.addElement("criteriaSearch");/*   to page '0' aka first page. */
                %>
                <jsp:include page="pagesize.jsp">
                  <jsp:param name="guid" value="<%=guid + 1%>"/>
                  <jsp:param name="pageName" value="<%=pageName%>"/>
                  <jsp:param name="pageSize" value="<%=formBean.getPageSize()%>"/>
                  <jsp:param name="toFORMParam" value="<%=formBean.toFORMParam(pageSizeFormFields)%>"/>
                </jsp:include>
                <%
                  // Prepare the form parameters.
                  Vector filterSearch = new Vector();
                  filterSearch.addElement("sort");
                  filterSearch.addElement("ascendency");
                  filterSearch.addElement("criteriaSearch");
                  filterSearch.addElement("pageSize");
                %>
                    <br />
                <%
                  Vector navigatorFormFields = new Vector();  /*  The following fields are used by paginator.jsp, included below.      */
                  navigatorFormFields.addElement("pageSize"); /* NOTE* that I didn't add here currentPage since it is overriden in the */
                  navigatorFormFields.addElement("sort");     /* <form name='..."> in the navigator.jsp!                               */
                  navigatorFormFields.addElement("ascendency");
                  navigatorFormFields.addElement("criteriaSearch");
                %>
                <jsp:include page="navigator.jsp">
                  <jsp:param name="pagesCount" value="<%=pagesCount%>"/>
                  <jsp:param name="pageName" value="<%=pageName%>"/>
                  <jsp:param name="guid" value="<%=guid%>"/>
                  <jsp:param name="currentPage" value="<%=formBean.getCurrentPage()%>"/>
                  <jsp:param name="toURLParam" value="<%=formBean.toURLParam(navigatorFormFields)%>"/>
                  <jsp:param name="toFORMParam" value="<%=formBean.toFORMParam(navigatorFormFields)%>"/>
                </jsp:include>
                <%// Compute the sort criteria
                Vector sortURLFields = new Vector();      /* Used for sorting */
                sortURLFields.addElement("pageSize");
                String urlSortString = formBean.toURLParam(sortURLFields);
                AbstractSortCriteria sortLevel = formBean.lookupSortCriteria(AdvancedSortCriteria.SORT_LEVEL);
                AbstractSortCriteria sortEunisCode = formBean.lookupSortCriteria(AdvancedSortCriteria.SORT_EUNIS_CODE);
                AbstractSortCriteria sortAnnexCode = formBean.lookupSortCriteria(AdvancedSortCriteria.SORT_ANNEX_CODE);
                AbstractSortCriteria sortScientificName = formBean.lookupSortCriteria(AdvancedSortCriteria.SORT_SCIENTIFIC_NAME);
                AbstractSortCriteria sortEnglishName = formBean.lookupSortCriteria(AdvancedSortCriteria.SORT_ENGLISH_NAME);
                // Expand/Collapse common names
                Vector expand = new Vector();
                expand.addElement("sort");
                expand.addElement("ascendency");
                expand.addElement("pageSize");
                expand.addElement("currentPage");
                String expandURL = formBean.toURLParam(expand);
              %>
                    <table class="sortable listing" width="100%" summary="<%=cm.cmsPhrase("Search results")%>">
                      <thead>
                        <tr>
                    <%
                      if(showLevel)
                      {
                    %>
                          <th class="nosort" scope="col">
                            <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=AdvancedSortCriteria.SORT_LEVEL%>&amp;ascendency=<%=formBean.changeAscendency(sortLevel, (null == sortLevel) ? true : false)%>"><%=Utilities.getSortImageTag(sortLevel)%><%=cm.cmsPhrase("Level")%></a>
                          </th>
                    <%
                      }
                    %>
                    <%
                      if(showEUNISCode)
                      {
                    %>
                          <th class="nosort" scope="col">
                            <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=AdvancedSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(sortEunisCode, (null == sortEunisCode) ? true : false)%>"><%=Utilities.getSortImageTag(sortEunisCode)%><%=cm.cmsPhrase("EUNIS Code")%></a>
                          </th>
                    <%
                      }
                    %>
                    <%
                      if(showANNEXCode)
                      {
                    %>
                          <th class="nosort" scope="col">
                            <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=AdvancedSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(sortAnnexCode, (null == sortAnnexCode) ? true : false)%>"><%=Utilities.getSortImageTag(sortAnnexCode)%><%=cm.cmsPhrase("ANNEX I Code")%></a>
                          </th>
                    <%
                      }
                    %>
                    <%
                      if(showScientificName)
                      {
                    %>
                          <th class="nosort" scope="col">
                            <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=AdvancedSortCriteria.SORT_SCIENTIFIC_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sortScientificName, (null == sortScientificName) ? true : false)%>"><%=Utilities.getSortImageTag(sortScientificName)%><%=cm.cmsPhrase("Name")%></a>
                          </th>
                    <%
                      }
                    %>
                    <%
                      if(showEnglishName)
                      {
                    %>
                          <th class="nosort" scope="col">
                            <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=AdvancedSortCriteria.SORT_ENGLISH_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sortEnglishName, (null == sortEnglishName) ? true : false)%>"><%=Utilities.getSortImageTag(sortEnglishName)%><%=cm.cmsPhrase("English name")%></a>
                          </th>
                    <%
                      }
                    %>
                        </tr>
                      </thead>
                      <tbody>
                    <%
                      int i = 0;
                      while ( it.hasNext() )
                      {
                        HabitatsCombinedPersist habitat = ( HabitatsCombinedPersist )it.next();
                        String cssClass = i++ % 2 == 0 ? " class=\"zebraeven\"" : "";
                        int level = habitat.getHabLevel().intValue();
                    %>
                        <tr<%=cssClass%>>
                    <%
                        if(showLevel)
                        {
                    %>
                          <td style="white-space: nowrap;">
                    <%
                                for(int iter = 0; iter < level; iter++)
                                {
                    %>
                            <img alt="" src="images/mini/lev_blank.gif">
                    <%
                                  }
                    %>
                                <%=level%>
                          </td>
                    <%
                        }
                    %>
                    <%
                        if(showEUNISCode)
                        {
                    %>
                          <td>
                            <%=(Utilities.isHabitatEunis(habitat.getHabitatType())) ? habitat.getEunisHabitatCode() : "&nbsp;"%>
                          </td>
                    <%
                        }
                    %>
                    <%
                      if(showANNEXCode)
                      {
                    %>
                          <td>
                            <%=(Utilities.isHabitatAnnex1(habitat.getHabitatType())) ? habitat.getCodeAnnex1() : "&nbsp;"%>
                          </td>
                    <%
                      }
                    %>
                    <%
                      if(showScientificName)
                      {
                    %>
                          <td>
                            <a href="habitats/<%=habitat.getIdHabitat()%>"><%=habitat.getScientificName()%></a>
                          </td>
                    <%
                      }
                    %>
                    <%
                      if(showEnglishName)
                      {
                    %>
                          <td>
                            <a href="habitats/<%=habitat.getIdHabitat()%>"><%=habitat.getDescription()%></a>
                          </td>
                    <%
                       }
                    %>
                        </tr>
                    <%
                    }
                    %>
                      </tbody>
                      <thead>
                        <tr>
                    <%
                      if(showLevel)
                      {
                    %>
                          <th class="nosort" scope="col">
                            <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=AdvancedSortCriteria.SORT_LEVEL%>&amp;ascendency=<%=formBean.changeAscendency(sortLevel, (null == sortLevel) ? true : false)%>"><%=Utilities.getSortImageTag(sortLevel)%><%=cm.cmsPhrase("Level")%></a>
                          </th>
                    <%
                      }
                    %>
                    <%
                      if(showEUNISCode)
                      {
                    %>
                          <th class="nosort" scope="col">
                            <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=AdvancedSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(sortEunisCode, (null == sortEunisCode) ? true : false)%>"><%=Utilities.getSortImageTag(sortEunisCode)%><%=cm.cmsPhrase("EUNIS Code")%></a>
                          </th>
                    <%
                      }
                    %>
                    <%
                      if(showANNEXCode)
                      {
                    %>
                          <th class="nosort" scope="col">
                            <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=AdvancedSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(sortAnnexCode, (null == sortAnnexCode) ? true : false)%>"><%=Utilities.getSortImageTag(sortAnnexCode)%><%=cm.cmsPhrase("ANNEX I Code")%></a>
                          </th>
                    <%
                      }
                    %>
                    <%
                      if(showScientificName)
                      {
                    %>
                          <th class="nosort" scope="col">
                            <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=AdvancedSortCriteria.SORT_SCIENTIFIC_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sortScientificName, (null == sortScientificName) ? true : false)%>"><%=Utilities.getSortImageTag(sortScientificName)%><%=cm.cmsPhrase("Name")%></a>
                          </th>
                    <%
                      }
                    %>
                    <%
                      if(showEnglishName)
                      {
                    %>
                          <th class="nosort" scope="col">
                            <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=AdvancedSortCriteria.SORT_ENGLISH_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sortEnglishName, (null == sortEnglishName) ? true : false)%>"><%=Utilities.getSortImageTag(sortEnglishName)%><%=cm.cmsPhrase("English name")%></a>
                          </th>
                    <%
                      }
                    %>
                        </tr>
                      </thead>
                    </table>
                  </td>
                </tr>
                <tr>
                  <td>
                    <jsp:include page="navigator.jsp">
                      <jsp:param name="pagesCount" value="<%=pagesCount%>"/>
                      <jsp:param name="pageName" value="<%=pageName%>"/>
                      <jsp:param name="guid" value="<%=guid + 1%>"/>
                      <jsp:param name="currentPage" value="<%=formBean.getCurrentPage()%>"/>
                      <jsp:param name="toURLParam" value="<%=formBean.toURLParam(navigatorFormFields)%>"/>
                      <jsp:param name="toFORMParam" value="<%=formBean.toFORMParam(navigatorFormFields)%>"/>
                    </jsp:include>
                  </td>
                </tr>
              </table>
                <%=cm.cmsMsg("generic_combined-search-results-habitats_title")%>
                <%=cm.br()%>
<!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>