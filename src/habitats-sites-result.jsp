<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Pick sites, show habitats' function - results page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement,
                 ro.finsiel.eunis.jrfTables.habitats.sites.HabitatsSitesDomain,
                 ro.finsiel.eunis.jrfTables.habitats.sites.HabitatsSitesPersist,
                 ro.finsiel.eunis.search.AbstractPaginator,
                 ro.finsiel.eunis.search.AbstractSearchCriteria,
                 ro.finsiel.eunis.search.AbstractSortCriteria,
                 ro.finsiel.eunis.search.Utilities,
                 ro.finsiel.eunis.search.habitats.sites.SitesBean,
                 ro.finsiel.eunis.search.habitats.sites.SitesPaginator,
                 ro.finsiel.eunis.search.habitats.sites.SitesSearchCriteria,
                 ro.finsiel.eunis.search.habitats.sites.SitesSortCriteria,
                 ro.finsiel.eunis.utilities.SQLUtilities,
                 java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Vector" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="<%=SessionManager.getCurrentLanguage()%>" xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=SessionManager.getCurrentLanguage()%>">
<head>
  <jsp:include page="header-page.jsp" />
  <jsp:useBean id="formBean" class="ro.finsiel.eunis.search.habitats.sites.SitesBean" scope="request">
    <jsp:setProperty name="formBean" property="*" />
  </jsp:useBean>
  <script language="JavaScript" src="script/species-result.js" type="text/javascript"></script>
  <script language="JavaScript" type="text/javascript">
  <!--
  function MM_openBrWindow(theURL,winName,features) { //v2.0
    window.open(theURL,winName,features);
  }
  //-->
  </script>
  <%
    if (null != formBean.getRemoveFilterIndex()) {
      formBean.prepareFilterCriterias();
    }
    boolean showLevel = Utilities.checkedStringToBoolean(formBean.getShowLevel(), SitesBean.HIDE);
    boolean showCode = Utilities.checkedStringToBoolean(formBean.getShowCode(), SitesBean.HIDE);
    boolean showScientificName = Utilities.checkedStringToBoolean(formBean.getShowScientificName(), true);
    //boolean showVernacularName = Utilities.checkedStringToBoolean(formBean.getShowVernacularName(), SitesBean.HIDE);
    boolean showVernacularName = false;

//    System.out.println("showVernacularName = " + showVernacularName);
    int currentPage = Utilities.checkedStringToInt(formBean.getCurrentPage(), 0);
    // The main paginator
//    Integer database = Utilities.checkedStringToInt(formBean.getDatabase(), HabitatsSitesDomain.SEARCH_EUNIS);
    Integer database = HabitatsSitesDomain.SEARCH_BOTH;
    Integer searchAttribute = Utilities.checkedStringToInt(formBean.getSearchAttribute(), SitesSearchCriteria.SEARCH_NAME);
    boolean[] source_db = {true, true, true, true, true, true, true, true};

    SitesPaginator paginator = new SitesPaginator(new HabitatsSitesDomain(formBean.toSearchCriteria(),
      formBean.toSortCriteria(),
      searchAttribute,
      database,
      source_db));

    paginator.setSortCriteria(formBean.toSortCriteria());
    paginator.setPageSize(Utilities.checkedStringToInt(formBean.getPageSize(), AbstractPaginator.DEFAULT_PAGE_SIZE));
    currentPage = paginator.setCurrentPage(currentPage);// Compute *REAL* current page (adjusted if user messes up)
    int resultsCount = paginator.countResults();
    final String pageName = "habitats-sites-result.jsp";
    int pagesCount = paginator.countPages();// This is used in @page include...
    int guid = 0;// This is used in @page include...
    // Now extract the results for the current page.
    List results = paginator.getPage(currentPage);
    // Set number criteria for the search result
    int noCriteria = (null == formBean.getCriteriaSearch() ? 0 : formBean.getCriteriaSearch().length);

    // Prepare parameters for tsvlink
    Vector reportFields = new Vector();
    reportFields.addElement("sort");
    reportFields.addElement("ascendency");
    reportFields.addElement("criteriaSearch");
    reportFields.addElement("oper");
    reportFields.addElement("criteriaType");

    String tsvLink = "javascript:openTSVDownload('reports/habitats/tsv-habitats-sites.jsp?" + formBean.toURLParam(reportFields) + "')";
    WebContentManagement cm = SessionManager.getWebContent();
%>
  <title>
    <%=application.getInitParameter("PAGE_TITLE")%>
    <%=cm.cms("habitats_sites-result_title")%>
  </title>
</head>

<body>
  <div id="outline">
  <div id="alignment">
  <div id="content">
<jsp:include page="header-dynamic.jsp">
  <jsp:param name="location" value="home_location#index.jsp,sites_location#sites.jsp,habitats_location#habitats-sites.jsp,results_location" />
  <jsp:param name="helpLink" value="sites-help.jsp" />
  <jsp:param name="downloadLink" value="<%=tsvLink%>" />
</jsp:include>
<table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
<td>
<h1><%=cm.cmsText("habitats_sites-result_01")%></h1>
<table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
  <%
    SitesSearchCriteria mainCriteria = (SitesSearchCriteria) formBean.getMainSearchCriteria();
    String criteriaStr = cm.cms("you_searched");
    if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_EUNIS)) {
      criteriaStr += " <strong> EUNIS </strong>";
    }
    if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_ANNEX_I)) {
      criteriaStr += " <strong> ANNEX I </strong>";
    }
    if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_BOTH)) {
      criteriaStr += " " + cm.cms("both_eunis_and_annex1");
    }
    criteriaStr += " " + cm.cms("habitats_within_sites") + " <strong>" + mainCriteria.toHumanString() + "</strong>";
  %>
  <tr>
    <td width="100%">
      <%=criteriaStr%>
    </td>
  </tr>
</table>
 <%
  if (results.isEmpty())
  {
     boolean fromRefine = false;
     if(formBean != null && formBean.getCriteriaSearch() != null && formBean.getCriteriaSearch().length > 0)
       fromRefine = true;
%>
     <jsp:include page="noresults.jsp" >
       <jsp:param name="fromRefine" value="<%=fromRefine%>" />
     </jsp:include>
<%
       return;
   }
%>
<%=cm.cmsText("habitats_sites-result_02")%>&nbsp;<strong><%=resultsCount%></strong>
<%
  // Prepare parameters for pagesize.jsp
  Vector pageSizeFormFields = new Vector();       /*  These fields are used by pagesize.jsp, included below.    */
  pageSizeFormFields.addElement("sort");          /*  *NOTE* I didn't add currentPage & pageSize since pageSize */
  pageSizeFormFields.addElement("ascendency");    /*   is overriden & also pageSize is set to default           */
  pageSizeFormFields.addElement("criteriaSearch");/*   to page '0' aka first page. */
  pageSizeFormFields.addElement("oper");
  pageSizeFormFields.addElement("criteriaType");
  pageSizeFormFields.addElement("expand");
%>
<jsp:include page="pagesize.jsp">
  <jsp:param name="guid" value="<%=guid%>" />
  <jsp:param name="pageName" value="<%=pageName%>" />
  <jsp:param name="pageSize" value="<%=formBean.getPageSize()%>" />
  <jsp:param name="toFORMParam" value="<%=formBean.toFORMParam(pageSizeFormFields)%>" />
</jsp:include>
<%
  // Prepare the form parameters.
  Vector filterSearch = new Vector();
  filterSearch.addElement("sort");
  filterSearch.addElement("ascendency");
  filterSearch.addElement("criteriaSearch");
  filterSearch.addElement("oper");
  filterSearch.addElement("criteriaType");
  filterSearch.addElement("pageSize");
  filterSearch.addElement("expand");
%>
<br />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td bgcolor="#EEEEEE">
    <strong>
      <%=cm.cmsText("habitats_sites-result_03")%>
    </strong>
  </td>
</tr>
<tr>
  <td bgcolor="#EEEEEE">
    <form name="refineSearch" method="get" onsubmit="return( validateRefineForm(<%=noCriteria%>) );" action="">
      <%=formBean.toFORMParam(filterSearch)%>
      <label for="criteriaType" class="noshow"><%=cm.cms("Criteria")%></label>
      <select title="<%=cm.cms("Criteria")%>" name="criteriaType" id="criteriaType" class="inputTextField">
        <%
          if (showCode) {
        %>
        <%
          if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_BOTH)) {
        %>
        <option value="<%=SitesSearchCriteria.CRITERIA_EUNIS_CODE%>"><%=cm.cms("habitats_sites-result_04")%></option>
        <option value="<%=SitesSearchCriteria.CRITERIA_ANNEX_CODE%>"><%=cm.cms("habitats_sites-result_05")%></option>
        <%
          }
        %>
        <%
          if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_EUNIS)) {
        %>
        <option value="<%=SitesSearchCriteria.CRITERIA_EUNIS_CODE%>"><%=cm.cms("habitats_sites-result_04")%></option>
        <%
          }
        %>
        <%
          if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_ANNEX_I)) {
        %>
        <option value="<%=SitesSearchCriteria.CRITERIA_ANNEX_CODE%>"><%=cm.cms("habitats_sites-result_05")%></option>
        <%
          }
        %>
        <%
          }
        %>
        <%
          if (showLevel && database.intValue() == HabitatsSitesDomain.SEARCH_EUNIS.intValue()) {
        %>
        <option value="<%=SitesSearchCriteria.CRITERIA_LEVEL%>"><%=cm.cms("habitats_sites-result_06")%></option>
        <%
          }
        %>
        <%
          if (showVernacularName) {
        %>
        <option value="<%=SitesSearchCriteria.CRITERIA_NAME%>"><%=cm.cms("habitats_sites-result_07")%></option>
        <%
          }
        %>
        <%
          if (showScientificName) {
        %>
        <option value="<%=SitesSearchCriteria.CRITERIA_SCIENTIFIC_NAME%>" selected="selected"><%=cm.cms("habitats_sites-result_08")%></option>
        <%
          }
        %>
      </select>
      <%=cm.cmsLabel("Criteria")%>
      <%=cm.cmsInput("habitats_sites-result_04")%>
      <%=cm.cmsInput("habitats_sites-result_05")%>
      <%=cm.cmsInput("habitats_sites-result_06")%>
      <%=cm.cmsInput("habitats_sites-result_07")%>
      <%=cm.cmsInput("habitats_sites-result_08")%>
      <label for="oper" class="noshow"><%=cm.cms("operator")%></label>
      <select title="<%=cm.cms("operator")%>" name="oper" id="oper" class="inputTextField">
        <option value="<%=Utilities.OPERATOR_IS%>" selected="selected"><%=cm.cms("habitats_sites-result_09")%></option>
        <option value="<%=Utilities.OPERATOR_STARTS%>"><%=cm.cms("habitats_sites-result_10")%></option>
        <option value="<%=Utilities.OPERATOR_CONTAINS%>"><%=cm.cms("habitats_sites-result_11")%></option>
      </select>
      <%=cm.cmsLabel("operator")%>
      <%=cm.cmsInput("habitats_sites-result_09")%>
      <%=cm.cmsInput("habitats_sites-result_10")%>
      <%=cm.cmsInput("habitats_sites-result_11")%>
      <label for="criteriaSearch" class="noshow"><%=cm.cms("search_value")%></label>
      <input title="<%=cm.cms("search_value")%>" class="inputTextField" name="criteriaSearch" id="criteriaSearch" type="text" size="30" />
      <%=cm.cmsTitle("search_value")%>
      <input title="<%=cm.cms("search")%>" class="inputTextField" type="submit" name="Submit" id="Submit" value="<%=cm.cms("habitats_sites-result_12")%>" />
      <%=cm.cmsTitle("search")%>
      <%=cm.cmsInput("habitats_sites-result_12")%>
    </form>
  </td>
</tr>
<%-- This is the code which shows the search filters --%>
<%ro.finsiel.eunis.search.AbstractSearchCriteria[] criterias = formBean.toSearchCriteria();%>
<%
  if (criterias.length > 1) {
%>
<tr>
  <td bgcolor="#EEEEEE">
    <%=cm.cmsText("habitats_sites-result_13")%>:
  </td>

</tr>
<%
  }
%>
<%
  for (int i = criterias.length - 1; i > 0; i--) {
%>
<%AbstractSearchCriteria criteria = criterias[i];%>
<%if (null != criteria && null != formBean.getCriteriaSearch()) {%>
<tr>
  <td bgcolor="#CCCCCC">
    <a title="<%=cm.cms("delete_filter")%>" href="<%= pageName%>?<%=formBean.toURLParam(filterSearch)%>&amp;removeFilterIndex=<%=i%>">
      <img title="<%=cm.cms("delete_filter")%>" alt="<%=cm.cms("delete_filter")%>" src="images/mini/delete.jpg" border="0" align="middle" />
    </a>
    <%=cm.cmsTitle("delete_filter")%>&nbsp;&nbsp;
    <strong class="linkDarkBg"><%= i + ". " + criteria.toHumanString()%></strong>
  </td>
</tr>
<%}%>
<%}%>
</table>
<%
  // Prepare parameters for navigator.jsp
  Vector navigatorFormFields = new Vector();  /*  The following fields are used by paginator.jsp, included below.      */
  navigatorFormFields.addElement("pageSize"); /* NOTE* that I didn't add here currentPage since it is overriden in the */
  navigatorFormFields.addElement("sort");     /* <form name='..."> in the navigator.jsp!                               */
  navigatorFormFields.addElement("ascendency");
  navigatorFormFields.addElement("criteriaSearch");
  navigatorFormFields.addElement("oper");
  navigatorFormFields.addElement("criteriaType");
  navigatorFormFields.addElement("expand");
%>
<table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <jsp:include page="navigator.jsp">
        <jsp:param name="pagesCount" value="<%=pagesCount%>" />
        <jsp:param name="pageName" value="<%=pageName%>" />
        <jsp:param name="guid" value="<%=guid%>" />
        <jsp:param name="currentPage" value="<%=formBean.getCurrentPage()%>" />
        <jsp:param name="toURLParam" value="<%=formBean.toURLParam(navigatorFormFields)%>" />
        <jsp:param name="toFORMParam" value="<%=formBean.toFORMParam(navigatorFormFields)%>" />
      </jsp:include>
    </td>
  </tr>
</table>
<table summary="<%=cm.cms("search_results")%>" width="100%" border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse">
<%// Compute the sort criteria
  Vector sortURLFields = new Vector();      /* Used for sorting */
  sortURLFields.addElement("pageSize");
  sortURLFields.addElement("criteriaSearch");
  sortURLFields.addElement("oper");
  sortURLFields.addElement("criteriaType");
  sortURLFields.addElement("currentPage");
  sortURLFields.addElement("expand");
  String urlSortString = formBean.toURLParam(sortURLFields);
  AbstractSortCriteria levelCrit = formBean.lookupSortCriteria(SitesSortCriteria.SORT_LEVEL);
  AbstractSortCriteria eunisCodeCrit = formBean.lookupSortCriteria(SitesSortCriteria.SORT_EUNIS_CODE);
  AbstractSortCriteria annexCodeCrit = formBean.lookupSortCriteria(SitesSortCriteria.SORT_ANNEX_CODE);
  AbstractSortCriteria sciNameCrit = formBean.lookupSortCriteria(SitesSortCriteria.SORT_SCIENTIFIC_NAME);
  AbstractSortCriteria nameCrit = formBean.lookupSortCriteria(SitesSortCriteria.SORT_VERNACULAR_NAME);
%>
<tr>
  <%if (showLevel && database.intValue() == HabitatsSitesDomain.SEARCH_EUNIS.intValue()) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SitesSortCriteria.SORT_LEVEL%>&amp;ascendency=<%=formBean.changeAscendency(levelCrit, (null == levelCrit) ? true : false)%>"><%=Utilities.getSortImageTag(levelCrit)%><%=cm.cmsText("habitats_sites-result_06")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%if (showCode) {%>
  <%if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_BOTH)) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SitesSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsText("habitats_sites-result_04")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SitesSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsText("habitats_sites-result_05")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_EUNIS)) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SitesSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsText("habitats_sites-result_04")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_ANNEX_I)) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SitesSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsText("habitats_sites-result_05")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%}%>
  <%if (showScientificName) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SitesSortCriteria.SORT_SCIENTIFIC_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sciNameCrit, (null == sciNameCrit) ? true : false)%>"><%=Utilities.getSortImageTag(sciNameCrit)%><%=cm.cmsText("habitats_sites-result_08")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%
    if (showVernacularName)
    {
  %>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SitesSortCriteria.SORT_VERNACULAR_NAME%>&amp;ascendency=<%=formBean.changeAscendency(nameCrit, (null == nameCrit) ? true : false)%>"><%=Utilities.getSortImageTag(nameCrit)%><%=cm.cmsText("habitats_sites-result_07")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <th class="resultHeader" width="130">
    <strong><%=cm.cmsText("habitats_sites-result_14")%>
    </strong>
  </th>
</tr>
<%
  int i = 0;
  Iterator it = results.iterator();
  int col = 0;
  while (it.hasNext()) {
    HabitatsSitesPersist habitat = (HabitatsSitesPersist) it.next();
    String bgColor = col++ % 2 == 0 ? "#EEEEEE" : "#FFFFFF";
    String eunisCode = habitat.getEunisHabitatCode();
    //String annexCode = habitat.getCodeAnnex1();
    String annexCode = habitat.getCode2000();
%>
<tr>
  <%if (showLevel && database.intValue() == HabitatsSitesDomain.SEARCH_EUNIS.intValue()) {%>
  <%int level = habitat.getHabLevel().intValue();%>
  <td class="resultCell" style="background-color : <%=bgColor%>; white-space : nowrap">
    <%for (int iter = 0; iter < level; iter++) {%>
    <img alt="" src="images/mini/lev_blank.gif" />
    <%}%><%=habitat.getHabLevel()%>
  </td>
  <%}%>
  <%if (showCode) {%>
  <%if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_BOTH)) {%>
  <td class="resultCell" style="background-color : <%=bgColor%>">
    <%=eunisCode%>
  </td>
  <td class="resultCell" style="background-color : <%=bgColor%>">
    <%=annexCode%>
  </td>
  <%}%>
  <%if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_EUNIS)) {%>
  <td class="resultCell" style="background-color : <%=bgColor%>">
    <%=eunisCode%>
  </td>
  <%}%>
  <%if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_ANNEX_I)) {%>
  <td class="resultCell" style="background-color : <%=bgColor%>">
    <%=annexCode%>
  </td>
  <%}%>
  <%}%>
  <%if (showScientificName) {%>
  <td class="resultCell" style="background-color : <%=bgColor%>">
    <a title="<%=cm.cms("open_habitat_factsheet")%>" href="habitats-factsheet.jsp?idHabitat=<%=habitat.getIdHabitat()%>"><%=habitat.getScientificName()%></a>
    <%=cm.cmsTitle("open_habitat_factsheet")%>
  </td>
  <%}%>
  <%if (showVernacularName) {%>
  <td class="resultCell" style="background-color : <%=bgColor%>">
    <%=habitat.getDescription()%>
  </td>
  <%}%>
  <td class="resultCell" style="background-color : <%=bgColor%>">
    <%
      Integer relationOp = Utilities.checkedStringToInt(formBean.getRelationOp(), Utilities.OPERATOR_CONTAINS);
      Integer idNatureObject = habitat.getIdNatureObject();
      // List of habitats from the specified site
      List resultsSites = new HabitatsSitesDomain().findSitesWithHabitats(
        new SitesSearchCriteria(searchAttribute,
          formBean.getScientificName(),
          relationOp),
        source_db,
        searchAttribute,
        idNatureObject,
        database);
      if (resultsSites != null && resultsSites.size() > 0) {
        String SQL_DRV = application.getInitParameter("JDBC_DRV");
        String SQL_URL = application.getInitParameter("JDBC_URL");
        String SQL_USR = application.getInitParameter("JDBC_USR");
        String SQL_PWD = application.getInitParameter("JDBC_PWD");

        SQLUtilities sqlc = new SQLUtilities();
        sqlc.Init(SQL_DRV, SQL_URL, SQL_USR, SQL_PWD);
    %>
    <table summary="<%=cm.cms("sites")%>" border="1" cellspacing="1" cellpadding="1" style="border-collapse: collapse">
      <%
        for (int ii = 0; ii < resultsSites.size(); ii++) {
          List l = (List) resultsSites.get(ii);
          String siteName = (String) l.get(0);
          String siteSourceDb = (String) l.get(1);
          String idSite = sqlc.ExecuteSQL("select ID_SITE FROM chm62edt_sites WHERE NAME='" + siteName.replaceAll("'", "''") + "'");
      %>
      <tr>
        <td>
          <a href="sites-factsheet.jsp?idsite=<%=idSite%>" title="<%=cm.cms("open_site_factsheet")%>"><%=siteName%></a>
          <%=cm.cmsTitle("open_site_factsheet")%>
          &nbsp;(<%=siteSourceDb%>
          )
        </td>
      </tr>
      <%
        }
      %>
    </table>
    <%
      }
    %>
  </td>
</tr>
<%}%>
<tr>
  <%if (showLevel && database.intValue() == HabitatsSitesDomain.SEARCH_EUNIS.intValue()) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SitesSortCriteria.SORT_LEVEL%>&amp;ascendency=<%=formBean.changeAscendency(levelCrit, (null == levelCrit) ? true : false)%>"><%=Utilities.getSortImageTag(levelCrit)%><%=cm.cmsText("habitats_sites-result_06")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%if (showCode) {%>
  <%if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_BOTH)) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SitesSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsText("habitats_sites-result_04")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SitesSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsText("habitats_sites-result_05")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_EUNIS)) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SitesSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsText("habitats_sites-result_04")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_ANNEX_I)) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SitesSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsText("habitats_sites-result_05")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%}%>
  <%if (showScientificName) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SitesSortCriteria.SORT_SCIENTIFIC_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sciNameCrit, (null == sciNameCrit) ? true : false)%>"><%=Utilities.getSortImageTag(sciNameCrit)%><%=cm.cmsText("habitats_sites-result_08")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%if (showVernacularName) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SitesSortCriteria.SORT_VERNACULAR_NAME%>&amp;ascendency=<%=formBean.changeAscendency(nameCrit, (null == nameCrit) ? true : false)%>"><%=Utilities.getSortImageTag(nameCrit)%><%=cm.cmsText("habitats_sites-result_07")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <th class="resultHeader" width="130">
    <strong><%=cm.cmsText("habitats_sites-result_14")%>
    </strong>
  </th>
</tr>
</table>
</td>
</tr>
<tr>
  <td>
    <table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td>
          <jsp:include page="navigator.jsp">
            <jsp:param name="pagesCount" value="<%=pagesCount%>" />
            <jsp:param name="pageName" value="<%=pageName%>" />
            <jsp:param name="guid" value="<%=guid + 1%>" />
            <jsp:param name="currentPage" value="<%=formBean.getCurrentPage()%>" />
            <jsp:param name="toURLParam" value="<%=formBean.toURLParam(navigatorFormFields)%>" />
            <jsp:param name="toFORMParam" value="<%=formBean.toFORMParam(navigatorFormFields)%>" />
          </jsp:include>
        </td>
      </tr>
    </table>
  </td>
</tr>
</table>
<%=cm.br()%>
<%=cm.cmsMsg("habitats_sites-result_title")%>
<%=cm.br()%>
<%=cm.cmsMsg("search_results")%>
<%=cm.br()%>
<%=cm.cmsMsg("sites")%>
<%=cm.br()%>
<%=cm.cmsMsg("you_searched")%>
<%=cm.br()%>
<%=cm.cmsMsg("both_eunis_and_annex1")%>
<%=cm.br()%>
<%=cm.cmsMsg("habitats_within_sites")%>
<%=cm.br()%>
<jsp:include page="footer.jsp">
  <jsp:param name="page_name" value="habitats-sites-result.jsp" />
</jsp:include>
    </div>
    </div>
    </div>
</body>
</html>