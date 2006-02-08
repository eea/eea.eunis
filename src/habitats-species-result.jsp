<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Pick species, show habitat types' function - results page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement,
                 ro.finsiel.eunis.jrfTables.habitats.species.ScientificNameDomain,
                 ro.finsiel.eunis.jrfTables.habitats.species.ScientificNamePersist,
                 ro.finsiel.eunis.search.AbstractPaginator,
                 ro.finsiel.eunis.search.AbstractSearchCriteria,
                 ro.finsiel.eunis.search.AbstractSortCriteria,
                 ro.finsiel.eunis.search.Utilities,
                 ro.finsiel.eunis.search.habitats.species.SpeciesBean,
                 ro.finsiel.eunis.search.habitats.species.SpeciesPaginator,
                 ro.finsiel.eunis.search.habitats.species.SpeciesSearchCriteria,
                 ro.finsiel.eunis.search.habitats.species.SpeciesSortCriteria,
                 ro.finsiel.eunis.utilities.TableColumns,
                 java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Vector" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="<%=SessionManager.getCurrentLanguage()%>" xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=SessionManager.getCurrentLanguage()%>">
<head>
  <jsp:include page="header-page.jsp" />
  <script language="JavaScript" src="script/species-result.js" type="text/javascript"></script>
  <jsp:useBean id="formBean" class="ro.finsiel.eunis.search.habitats.species.SpeciesBean" scope="request">
    <jsp:setProperty name="formBean" property="*" />
  </jsp:useBean>
  <script language="JavaScript" type="text/javascript">
  <!--
    function MM_openBrWindow(theURL,winName,features) { //v2.0
      window.open(theURL,winName,features);
    }
  //-->
  </script>
  <%
    // Prepare the search in results (fix)
    if(null != formBean.getRemoveFilterIndex()) {
      formBean.prepareFilterCriterias();
    }
    // Request parameters.
    boolean showLevel = Utilities.checkedStringToBoolean(formBean.getShowLevel(), SpeciesBean.HIDE);
    boolean showCode = Utilities.checkedStringToBoolean(formBean.getShowCode(), SpeciesBean.HIDE);
    boolean showScientificName = Utilities.checkedStringToBoolean(formBean.getShowScientificName(), true);
    boolean showVernacularName = Utilities.checkedStringToBoolean(formBean.getShowVernacularName(), SpeciesBean.HIDE);

    int currentPage = Utilities.checkedStringToInt(formBean.getCurrentPage(), 0);
    Integer database = Utilities.checkedStringToInt(formBean.getDatabase(), ScientificNameDomain.SEARCH_EUNIS);
    Integer searchAttribute = Utilities.checkedStringToInt(formBean.getSearchAttribute(), SpeciesSearchCriteria.SEARCH_SCIENTIFIC_NAME);
    // The main paginator
    SpeciesPaginator paginator = new SpeciesPaginator(new ScientificNameDomain(formBean.toSearchCriteria(),
                                                                               formBean.toSortCriteria(),
                                                                               database,
                                                                               SessionManager.getShowEUNISInvalidatedSpecies(),
                                                                               searchAttribute));
    // Initialisation
    paginator.setSortCriteria(formBean.toSortCriteria());
    paginator.setPageSize(Utilities.checkedStringToInt(formBean.getPageSize(), AbstractPaginator.DEFAULT_PAGE_SIZE));
    currentPage = paginator.setCurrentPage(currentPage);// Compute *REAL* current page (adjusted if user messes up)
    int resultsCount = paginator.countResults();
    final String pageName = "habitats-species-result.jsp";
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

    String tsvLink = "javascript:openTSVDownload('reports/habitats/tsv-habitats-species.jsp?" + formBean.toURLParam(reportFields) + "')";
    WebContentManagement cm = SessionManager.getWebContent();
%>
  <title>
    <%=application.getInitParameter("PAGE_TITLE")%>
    <%=cm.cms("habitats_species-result_title")%>
  </title>
</head>

<body>
  <div id="outline">
  <div id="alignment">
  <div id="content">
<jsp:include page="header-dynamic.jsp">
  <jsp:param name="location" value="home_location#index.jsp,species_location#species.jsp,habitats_location#habitats-species.jsp,results_location" />
  <jsp:param name="helpLink" value="species-help.jsp" />
  <jsp:param name="downloadLink" value="<%=tsvLink%>" />
</jsp:include>
<table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
<td>
<h1><%=cm.cmsText("habitats_species-result_01")%></h1>
<table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
  <%SpeciesSearchCriteria mainCriteria = (SpeciesSearchCriteria) formBean.getMainSearchCriteria();%>
  <tr>
    <td>
      <%=cm.cmsText("habitats_species-result_02")%>
      <strong>
        <%=Utilities.getSourceHabitat(database, ScientificNameDomain.SEARCH_ANNEX_I.intValue(), ScientificNameDomain.SEARCH_BOTH.intValue())%>
      </strong>
      <%=cm.cmsText("habitats_species-result_03")%>:
      <strong>
        <%=mainCriteria.toHumanString()%>
      </strong>.
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
<%=cm.cmsText("habitats_species-result_04")%>:&nbsp;<strong><%=resultsCount%></strong>
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
<table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td bgcolor="#EEEEEE">
      <strong>
        <%=cm.cmsText("habitats_species-result_05")%>
      </strong>
    </td>
  </tr>
  <tr>
    <td bgcolor="#EEEEEE">
      <form name="refineSearch" method="get" onsubmit="return(validateRefineForm(<%=noCriteria%>));" action="">
        <%=formBean.toFORMParam(filterSearch)%>
        <label for="criteriaType" class="noshow"><%=cm.cms("Criteria")%></label>
        <select title="<%=cm.cms("Criteria")%>" name="criteriaType" id="criteriaType" class="inputTextField">
          <%if(showCode && 0 == database.compareTo(ScientificNameDomain.SEARCH_EUNIS)) {%>
          <option value="<%=SpeciesSearchCriteria.CRITERIA_EUNIS_CODE%>"><%=cm.cms("habitats_species-result_06")%></option><%}%>
          <%if(showCode && 0 == database.compareTo(ScientificNameDomain.SEARCH_ANNEX_I)) {%>
          <option value="<%=SpeciesSearchCriteria.CRITERIA_ANNEX_CODE%>"><%=cm.cms("habitats_species-result_07")%></option><%}%>
          <%if(showCode && 0 == database.compareTo(ScientificNameDomain.SEARCH_BOTH)) {%>
          <option value="<%=SpeciesSearchCriteria.CRITERIA_EUNIS_CODE%>"><%=cm.cms("habitats_species-result_06")%></option>
          <option value="<%=SpeciesSearchCriteria.CRITERIA_ANNEX_CODE%>"><%=cm.cms("habitats_species-result_07")%></option>
          <%}%>
          <%if(showLevel && formBean.getDatabase().equalsIgnoreCase(ScientificNameDomain.SEARCH_EUNIS.toString())) {%>
          <option value="<%=SpeciesSearchCriteria.CRITERIA_LEVEL%>"><%=cm.cms("habitats_species-result_08")%></option><%}%>
          <%if(showVernacularName) {%>
          <option value="<%=SpeciesSearchCriteria.CRITERIA_NAME%>"><%=cm.cms("habitats_species-result_09")%></option><%}%>
          <%if(showScientificName) {%>
          <option value="<%=SpeciesSearchCriteria.CRITERIA_SCIENTIFIC_NAME%>" selected="selected"><%=cm.cms("habitats_species-result_10")%></option><%}%>
        </select>
        <%=cm.cmsLabel("Criteria")%>
        <%=cm.cmsInput("habitats_species-result_06")%>
        <%=cm.cmsInput("habitats_species-result_07")%>
        <%=cm.cmsInput("habitats_species-result_08")%>
        <%=cm.cmsInput("habitats_species-result_09")%>
        <%=cm.cmsInput("habitats_species-result_10")%>
        <label for="oper" class="noshow"><%=cm.cms("operator")%></label>
        <select title="<%=cm.cms("operator")%>" name="oper" id="oper" class="inputTextField">
          <option value="<%=Utilities.OPERATOR_IS%>" selected="selected"><%=cm.cms("habitats_species-result_11")%></option>
          <option value="<%=Utilities.OPERATOR_STARTS%>"><%=cm.cms("habitats_species-result_12")%></option>
          <option value="<%=Utilities.OPERATOR_CONTAINS%>"><%=cm.cms("habitats_species-result_13")%></option>
        </select>
        <%=cm.cmsLabel("operator")%>
        <%=cm.cmsInput("habitats_species-result_11")%>
        <%=cm.cmsInput("habitats_species-result_12")%>
        <%=cm.cmsInput("habitats_species-result_13")%>
        <label for="criteriaSearch" class="noshow"><%=cm.cms("search_value")%></label>
        <input title="<%=cm.cms("search_value")%>" class="inputTextField" name="criteriaSearch" id="criteriaSearch" type="text" size="30" />
        <%=cm.cmsTitle("search_value")%>
        <input title="<%=cm.cms("search")%>" class="inputTextField" type="submit" name="Submit" id="Submit" value="<%=cm.cms("habitats_species-result_14")%>" />
        <%=cm.cmsTitle("search")%>
        <%=cm.cmsInput("habitats_species-result_14")%>
      </form>
    </td>
  </tr>
    <%-- This is the code which shows the search filters --%>
    <%ro.finsiel.eunis.search.AbstractSearchCriteria[] criterias = formBean.toSearchCriteria();%>
    <%if(criterias.length > 1) {%>
  <tr>
    <td bgcolor="#EEEEEE">
      <%=cm.cmsText("habitats_species-result_15")%>:
    </td>
  </tr>
  <%}%>
  <%for(int i = criterias.length - 1; i > 0; i--) {%>
  <%AbstractSearchCriteria criteria = criterias[i];%>
  <%if(null != criteria && null != formBean.getCriteriaSearch()) {%>
  <tr>
    <td bgcolor="#CCCCCC">
      <a title="<%=cm.cms("delete_filter")%>" href="<%=pageName%>?<%=formBean.toURLParam(filterSearch)%>&amp;removeFilterIndex=<%=i%>">
        <img alt="<%=cm.cms("delete_filter")%>" src="images/mini/delete.jpg" border="0" align="middle" />
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
<jsp:include page="navigator.jsp">
  <jsp:param name="pagesCount" value="<%=pagesCount%>" />
  <jsp:param name="pageName" value="<%=pageName%>" />
  <jsp:param name="guid" value="<%=guid%>" />
  <jsp:param name="currentPage" value="<%=formBean.getCurrentPage()%>" />
  <jsp:param name="toURLParam" value="<%=formBean.toURLParam(navigatorFormFields)%>" />
  <jsp:param name="toFORMParam" value="<%=formBean.toFORMParam(navigatorFormFields)%>" />
</jsp:include>
<table summary="<%=cm.cms("search_results")%>" border="1" cellpadding="0" cellspacing="0" width="100%" style="border-collapse: collapse">
<%// Compute the sort criteria
  Vector sortURLFields = new Vector();      /* Used for sorting */
  sortURLFields.addElement("pageSize");
  sortURLFields.addElement("criteriaSearch");
  sortURLFields.addElement("oper");
  sortURLFields.addElement("criteriaType");
  sortURLFields.addElement("currentPage");
  sortURLFields.addElement("expand");
  String urlSortString = formBean.toURLParam(sortURLFields);
  AbstractSortCriteria levelCrit = formBean.lookupSortCriteria(SpeciesSortCriteria.SORT_LEVEL);
  AbstractSortCriteria eunisCodeCrit = formBean.lookupSortCriteria(SpeciesSortCriteria.SORT_EUNIS_CODE);
  AbstractSortCriteria annexCodeCrit = formBean.lookupSortCriteria(SpeciesSortCriteria.SORT_ANNEX_CODE);
  AbstractSortCriteria sciNameCrit = formBean.lookupSortCriteria(SpeciesSortCriteria.SORT_SCIENTIFIC_NAME);
  AbstractSortCriteria nameCrit = formBean.lookupSortCriteria(SpeciesSortCriteria.SORT_VERNACULAR_NAME);
%>
<tr>
  <%if(showLevel && (formBean).getDatabase().equalsIgnoreCase(ScientificNameDomain.SEARCH_EUNIS.toString())) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SpeciesSortCriteria.SORT_LEVEL%>&amp;ascendency=<%=formBean.changeAscendency(levelCrit, (null == levelCrit) ? true : false)%>"><%=Utilities.getSortImageTag(levelCrit)%><%=cm.cmsText("habitats_species-result_16")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%if(showCode) {%>
  <%if(0 == database.compareTo(ScientificNameDomain.SEARCH_BOTH)) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SpeciesSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsText("habitats_species-result_06")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SpeciesSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsText("habitats_species-result_07")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%if(0 == database.compareTo(ScientificNameDomain.SEARCH_EUNIS)) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SpeciesSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsText("habitats_species-result_06")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%if(0 == database.compareTo(ScientificNameDomain.SEARCH_ANNEX_I)) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SpeciesSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsText("habitats_species-result_07")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%}%>
  <%if(showScientificName) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SpeciesSortCriteria.SORT_SCIENTIFIC_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sciNameCrit, (null == sciNameCrit) ? true : false)%>"><%=Utilities.getSortImageTag(sciNameCrit)%><%=cm.cmsText("habitats_species-result_17")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%if(showVernacularName) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SpeciesSortCriteria.SORT_VERNACULAR_NAME%>&amp;ascendency=<%=formBean.changeAscendency(nameCrit, (null == nameCrit) ? true : false)%>"><%=Utilities.getSortImageTag(nameCrit)%><%=cm.cmsText("habitats_species-result_09")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <th class="resultHeader" style="text-align : center;">
    <%=cm.cmsText("habitats_species-result_18")%>
  </th>
</tr>
<%
  int i = 0;
  Iterator it = results.iterator();
  while(it.hasNext()) {
    ScientificNamePersist habitat = (ScientificNamePersist) it.next();
    String bgColor = (0 == (i++ % 2)) ? "#FFFFFF" : "#EEEEEE";
    int idHabitat = Utilities.checkedStringToInt(habitat.getIdHabitat(), -1);
    boolean isEUNIS = idHabitat <= 10000;
    String eunisCode = habitat.getEunisHabitatCode();
    //String annexCode = habitat.getCodeAnnex1();
    String annexCode = habitat.getCode2000();
    if(isEUNIS) {
      annexCode = "";
    } else {
      eunisCode = "";
    }
%>
<tr>
  <%
    if(showLevel && formBean.getDatabase().equalsIgnoreCase(ScientificNameDomain.SEARCH_EUNIS.toString())) {
      int level = habitat.getHabLevel().intValue();
  %>
  <td class="resultCell" style="background-color : <%=bgColor%>; white-space : nowrap;">
    <%for(int iter = 0; iter < level; iter++) {%>
    <img alt="" src="images/mini/lev_blank.gif" /><%}%><%=habitat.getHabLevel()%>
  </td>
  <%
    }
    if(showCode) {
      if(0 == database.compareTo(ScientificNameDomain.SEARCH_BOTH)) {
  %>
  <td class="resultCell" style="background-color : <%=bgColor%>;">
    <%=eunisCode%>
  </td>
  <td class="resultCell" style="background-color : <%=bgColor%>;">
    <%=annexCode%>
  </td>
  <%
    }
    if(0 == database.compareTo(ScientificNameDomain.SEARCH_EUNIS)) {
  %>
  <td class="resultCell" style="background-color : <%=bgColor%>;">
    <%=eunisCode%>
  </td>
  <%
    }
    if(0 == database.compareTo(ScientificNameDomain.SEARCH_ANNEX_I)) {
  %>
  <td class="resultCell" style="background-color : <%=bgColor%>;">
    <%=annexCode%>
  </td>
  <%
      }
    }
    if(showScientificName) {
  %>
  <td class="resultCell" style="background-color : <%=bgColor%>;">
    <a title="<%=cm.cms("open_habitat_factsheet")%>" href="habitats-factsheet.jsp?idHabitat=<%=habitat.getIdHabitat()%>"><%=habitat.getScientificName()%></a>
    <%=cm.cmsTitle("open_habitat_factsheet")%>
  </td>
  <%
    }
    if(showVernacularName) {
  %>
  <td class="resultCell" style="background-color : <%=bgColor%>;">
    <%=habitat.getDescription()%>
  </td>
  <%
    }
  %>
  <td>
    <%
      Integer relationOp = Utilities.checkedStringToInt(formBean.getRelationOp(), Utilities.OPERATOR_CONTAINS);
      Integer idNatureObject = habitat.getIdNatureObject();
      // List of species attributes.
      List resultsSpecies = new ScientificNameDomain().findSpeciesFromHabitat(new SpeciesSearchCriteria(searchAttribute,
                                                                                                        formBean.getScientificName(),
                                                                                                        relationOp),
                                                                              database,
                                                                              SessionManager.getShowEUNISInvalidatedSpecies(),
                                                                              idNatureObject,
                                                                              searchAttribute);

      if(resultsSpecies != null && resultsSpecies.size() > 0) {
    %>
    <table summary="<%=cm.cms("species")%>" border="1" cellspacing="1" cellpadding="1" style="border-collapse: collapse">
      <%
        for(int ii = 0; ii < resultsSpecies.size(); ii++) {
          TableColumns tableColumns = (TableColumns) resultsSpecies.get(ii);
          String scientificName = (String) tableColumns.getColumnsValues().get(0);
          Integer idSpecies = (Integer) tableColumns.getColumnsValues().get(1);
          Integer idSpeciesLink = (Integer) tableColumns.getColumnsValues().get(2);
      %>
      <tr>
        <td>
          <a title="<%=cm.cms("open_species_factsheet")%>" href="species-factsheet.jsp?idSpecies=<%=idSpecies%>&amp;idSpeciesLink=<%=idSpeciesLink%>"><%=scientificName%></a>
          <%=cm.cmsTitle("open_species_factsheet")%>
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
  <%if(showLevel && (formBean).getDatabase().equalsIgnoreCase(ScientificNameDomain.SEARCH_EUNIS.toString())) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SpeciesSortCriteria.SORT_LEVEL%>&amp;ascendency=<%=formBean.changeAscendency(levelCrit, (null == levelCrit) ? true : false)%>"><%=Utilities.getSortImageTag(levelCrit)%><%=cm.cmsText("habitats_species-result_16")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%if(showCode) {%>
  <%if(0 == database.compareTo(ScientificNameDomain.SEARCH_BOTH)) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SpeciesSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsText("habitats_species-result_06")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SpeciesSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsText("habitats_species-result_07")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%if(0 == database.compareTo(ScientificNameDomain.SEARCH_EUNIS)) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SpeciesSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsText("habitats_species-result_06")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%if(0 == database.compareTo(ScientificNameDomain.SEARCH_ANNEX_I)) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SpeciesSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsText("habitats_species-result_07")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%}%>
  <%if(showScientificName) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SpeciesSortCriteria.SORT_SCIENTIFIC_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sciNameCrit, (null == sciNameCrit) ? true : false)%>"><%=Utilities.getSortImageTag(sciNameCrit)%><%=cm.cmsText("habitats_species-result_17")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <%if(showVernacularName) {%>
  <th class="resultHeader">
    <a title="<%=cm.cms("sort_results_on_this_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=SpeciesSortCriteria.SORT_VERNACULAR_NAME%>&amp;ascendency=<%=formBean.changeAscendency(nameCrit, (null == nameCrit) ? true : false)%>"><%=Utilities.getSortImageTag(nameCrit)%><%=cm.cmsText("habitats_species-result_09")%></a>
    <%=cm.cmsTitle("sort_results_on_this_column")%>
  </th>
  <%}%>
  <th class="resultHeader" style="text-align : center;">
    <%=cm.cmsText("habitats_species-result_18")%>
  </th>
</tr>
</table>
</td>
</tr>
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
<%=cm.cmsMsg("habitats_species-result_title")%>
<%=cm.br()%>
<%=cm.cmsTitle("species")%>
<jsp:include page="footer.jsp">
  <jsp:param name="page_name" value="habitats-species-result.jsp" />
</jsp:include>
    </div>
    </div>
    </div>
  </body>
</html>