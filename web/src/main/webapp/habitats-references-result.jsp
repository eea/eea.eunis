<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Pick references, show habitats' function - results page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>

<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement,
                 ro.finsiel.eunis.jrfTables.habitats.habitatsByReferences.RefDomain,
                 ro.finsiel.eunis.jrfTables.habitats.habitatsByReferences.RefPersist,
                 ro.finsiel.eunis.search.AbstractPaginator,
                 ro.finsiel.eunis.search.AbstractSearchCriteria,
                 ro.finsiel.eunis.search.AbstractSortCriteria,
                 ro.finsiel.eunis.search.Utilities,
                 ro.finsiel.eunis.search.habitats.habitatsByReferences.ReferencesBean,
                 ro.finsiel.eunis.search.habitats.habitatsByReferences.ReferencesPaginator,
                 ro.finsiel.eunis.search.habitats.habitatsByReferences.ReferencesSearchCriteria,
                 ro.finsiel.eunis.search.habitats.habitatsByReferences.ReferencesSortCriteria,
                 java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Vector" %>
<%@ page import="eionet.eunis.util.JstlFunctions" %>
<jsp:useBean id="formBean" class="ro.finsiel.eunis.search.habitats.habitatsByReferences.ReferencesBean" scope="request">
  <jsp:setProperty name="formBean" property="*" />
</jsp:useBean>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<%
  // Prepare the search in results (fix)
  if (null != formBean.getRemoveFilterIndex())
  {
    formBean.prepareFilterCriterias();
  }
  // Requets parameters
  boolean showLevel = Utilities.checkedStringToBoolean(formBean.getShowLevel(), ReferencesBean.HIDE);
  boolean showCode = Utilities.checkedStringToBoolean(formBean.getShowCode(), ReferencesBean.HIDE);
  //boolean showScientificName = Utilities.checkedStringToBoolean(formBean.getShowScientificName(), ReferencesBean.HIDE);
  boolean showVernacularName = Utilities.checkedStringToBoolean(formBean.getShowVernacularName(), ReferencesBean.HIDE);

  Integer database = Utilities.checkedStringToInt(formBean.getDatabase(), RefDomain.SEARCH_EUNIS);
  Integer source = Utilities.checkedStringToInt(formBean.getSource(), RefDomain.SOURCE);
  // Initialization
  int currentPage = Utilities.checkedStringToInt(formBean.getCurrentPage(), 0);

  // The main paginator
  ReferencesPaginator paginator = new ReferencesPaginator(new RefDomain(formBean.toSearchCriteria(),
    formBean.toSortCriteria(),
    database, source));
  //SpeciesPaginator paginator = new SpeciesPaginator(new InternationalThreatStatusDomain(formBean.toSearc);
  //paginator = new ReferencesPaginator(new InternationalThreatStatusDomain(formBean.toSearchCriteria()));
  paginator.setSortCriteria(formBean.toSortCriteria());
  paginator.setPageSize(Utilities.checkedStringToInt(formBean.getPageSize(), AbstractPaginator.DEFAULT_PAGE_SIZE));
  currentPage = paginator.setCurrentPage(currentPage);// Compute *REAL* current page (adjusted if user messes up)
  int resultsCount = paginator.countResults();
  final String pageName = "habitats-references-result.jsp";
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

  String tsvLink = "javascript:openTSVDownload('reports/habitats/tsv-habitats-references.jsp?" + formBean.toURLParam(reportFields) + "')";
  WebContentManagement cm = SessionManager.getWebContent();
  String eeaHome = application.getInitParameter( "EEA_HOME" );
  String location = "eea#" + eeaHome + ",home#index.jsp,habitat_types#habitats.jsp,pick_habitat_type_show_references#habitats-references.jsp,results";
  if (results.isEmpty())
  {
    boolean fromRefine = formBean.getCriteriaSearch() != null && formBean.getCriteriaSearch().length > 0;
%>
  <jsp:forward page="emptyresults.jsp">
    <jsp:param name="location" value="<%=location%>" />
    <jsp:param name="fromRefine" value="<%=fromRefine%>" />
  </jsp:forward>
<%
   }
%>
<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cms("habitats_references-result_title") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" helpLink="habitats-help.jsp" pageTitle="${title}" downloadLink="<%= tsvLink%>" btrail="<%= location%>">
    <stripes:layout-component name="head">
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/eea_search.css">
      <script language="JavaScript" src="<%=request.getContextPath()%>/script/species-result.js" type="text/javascript"></script>
      <script language="JavaScript" type="text/javascript">
      //<![CDATA[
        function MM_openBrWindow(theURL,winName,features) { //v2.0
          window.open(theURL,winName,features);
        }

          function openPopup(theURL,winName,features) {
            window.open(theURL,winName,features);
        }
      //]]>
      </script>
    </stripes:layout-component>
    <stripes:layout-component name="contents">
                <a name="documentContent"></a>
                <h1>
                  <%=cm.cmsPhrase("Books, articles")%>
                </h1>
<!-- MAIN CONTENT -->
                <table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                <td>
                <table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
                  <%
                    // Create mainCriteria object for main criteria description
                    ReferencesSearchCriteria mainCriteria = (ReferencesSearchCriteria) formBean.getMainSearchCriteria();
                  %>
                  <tr>
                    <td>
                      <%=cm.cmsPhrase("You searched for")%>
                      <strong><%=Utilities.getSourceHabitat(database, RefDomain.SEARCH_ANNEX_I.intValue(), RefDomain.SEARCH_BOTH.intValue())%></strong>
                      <%=cm.cmsPhrase("habitat types for which the selected references ")%>
                      (
                      <strong>
                        <%
                          if (0 == source.compareTo(RefDomain.SOURCE)) {
                        %>
                        <%=cm.cmsPhrase("Source")%>
                        <%
                        } else {
                        %>
                        <%=cm.cmsPhrase("Other information")%>
                        <%
                          }
                        %>
                      </strong>)
                      <%
                        if (mainCriteria.toHumanString().length() > 0) {
                      %>
                      (
                      <%=cm.cmsPhrase("with")%>
                      <strong>
                        <%=mainCriteria.toHumanString()%>
                      </strong>
                      )
                      <%
                        }
                      %>
                      <%=cm.cmsPhrase("are recorded in the database")%>
                    </td>
                  </tr>
                </table>
                <%=cm.cmsPhrase("Results found")%>:&nbsp;<strong><%=resultsCount%></strong>
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
                        <%=cm.cmsPhrase("Refine your search")%>
                      </strong>
                    </td>
                  </tr>
                  <tr>
                    <td bgcolor="#EEEEEE">
                      <form name="refineSearch" method="get" onsubmit="return(validateRefineForm(<%=noCriteria%>));" action="">
                        <%=formBean.toFORMParam(filterSearch)%>
                        <label for="criteriaType" class="noshow"><%=cm.cmsPhrase("Criteria")%></label>
                        <select title="<%=cm.cmsPhrase("Criteria")%>" name="criteriaType" id="criteriaType">
                          <%
                            if (showCode && 0 == database.compareTo(RefDomain.SEARCH_EUNIS)) {
                          %>
                          <option value="<%=ReferencesSearchCriteria.CRITERIA_CODE_EUNIS%>"><%=cm.cms("eunis_code")%></option>
                          <%
                            }
                            if (showCode && 0 == database.compareTo(RefDomain.SEARCH_ANNEX_I)) {
                          %>
                          <option value="<%=ReferencesSearchCriteria.CRITERIA_CODE_ANNEX%>"><%=cm.cms("annex_code")%></option>
                          <%
                            }
                            if (showCode && 0 == database.compareTo(RefDomain.SEARCH_BOTH)) {
                          %>
                          <option value="<%=ReferencesSearchCriteria.CRITERIA_CODE_EUNIS%>"><%=cm.cms("eunis_code")%></option>
                          <option value="<%=ReferencesSearchCriteria.CRITERIA_CODE_ANNEX%>"><%=cm.cms("annex_code")%></option>
                          <%
                            }
                            if (showLevel && formBean.getDatabase().equalsIgnoreCase(RefDomain.SEARCH_EUNIS.toString())) {
                          %>
                          <option value="<%=ReferencesSearchCriteria.CRITERIA_LEVEL%>"><%=cm.cms("generic_index_07")%></option>
                          <%
                            }
                            if (showVernacularName) {
                          %>
                          <option value="<%=ReferencesSearchCriteria.CRITERIA_NAME%>"><%=cm.cms("english_name")%></option>
                          <%
                            }
                          %>
                          <option value="<%=ReferencesSearchCriteria.CRITERIA_SCIENTIFIC_NAME%>" selected="selected"><%=cm.cms("habitat_type_name")%></option>
                        </select>
                        <%=cm.cmsInput("eunis_code")%>
                        <%=cm.cmsInput("annex_code")%>
                        <%=cm.cmsInput("generic_index_07")%>
                        <%=cm.cmsInput("english_name")%>
                        <%=cm.cmsInput("habitat_type_name")%>
                        <select title="<%=cm.cmsPhrase("Operator")%>" name="oper" id="oper">
                          <option value="<%=Utilities.OPERATOR_IS%>" selected="selected"><%=cm.cmsPhrase("is")%></option>
                          <option value="<%=Utilities.OPERATOR_STARTS%>"><%=cm.cms("starts with")%></option>
                          <option value="<%=Utilities.OPERATOR_CONTAINS%>"><%=cm.cmsPhrase("contains")%></option>
                        </select>
                        <label for="criteriaSearch" class="noshow"><%=cm.cmsPhrase("Filter value")%></label>
                        <input title="<%=cm.cmsPhrase("Filter value")%>" name="criteriaSearch" id="criteriaSearch" type="text" size="30" />
                        <input title="<%=cm.cmsPhrase("Search")%>" class="submitSearchButton" type="submit" name="Submit" id="Submit" value="<%=cm.cmsPhrase("Search")%>" />
                      </form>
                    </td>
                  </tr>
                  <%-- This is the code which shows the search filters --%>
                  <%
                    ro.finsiel.eunis.search.AbstractSearchCriteria[] criterias = formBean.toSearchCriteria();
                    if (criterias.length > 1) {
                  %>
                  <tr>
                    <td bgcolor="#EEEEEE">
                      <%=cm.cmsPhrase("Applied filters to the results")%>:
                    </td>
                  </tr>
                  <%
                    }
                    for (int i = criterias.length - 1; i > 0; i--) {
                      AbstractSearchCriteria criteria = criterias[i];
                      if (null != criteria && null != formBean.getCriteriaSearch()) {
                  %>
                  <tr>
                    <td bgcolor="#CCCCCC">
                      <a title="<%=cm.cms("delete_filter")%>" href="<%= pageName%>?<%=formBean.toURLParam(filterSearch)%>&amp;removeFilterIndex=<%=i%>"><img alt="<%=cm.cms("delete_filter")%>" src="images/mini/delete.jpg" border="0" style="vertical-align:middle" /></a>
                      <%=cm.cmsTitle("delete_filter")%>&nbsp;&nbsp;
                      <strong class="linkDarkBg"><%= i + ". " + criteria.toHumanString()%></strong>
                    </td>
                  </tr>
                  <%
                      }
                    }
                  %>
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
                </td>
                </tr>
                <tr>
                <td>
                <%
                  // Compute the sort criteria
                  Vector sortURLFields = new Vector();      /* Used for sorting */
                  sortURLFields.addElement("pageSize");
                  sortURLFields.addElement("criteriaSearch");
                  sortURLFields.addElement("oper");
                  sortURLFields.addElement("criteriaType");
                  sortURLFields.addElement("currentPage");
                  sortURLFields.addElement("expand");
                  String urlSortString = formBean.toURLParam(sortURLFields);
                  AbstractSortCriteria levelCrit = formBean.lookupSortCriteria(ReferencesSortCriteria.SORT_LEVEL);
                  AbstractSortCriteria eunisCodeCrit = formBean.lookupSortCriteria(ReferencesSortCriteria.SORT_EUNIS_CODE);
                  AbstractSortCriteria annexCodeCrit = formBean.lookupSortCriteria(ReferencesSortCriteria.SORT_ANNEX_CODE);
                  AbstractSortCriteria sciNameCrit = formBean.lookupSortCriteria(ReferencesSortCriteria.SORT_SCIENTIFIC_NAME);
                  AbstractSortCriteria nameCrit = formBean.lookupSortCriteria(ReferencesSortCriteria.SORT_VERNACULAR_NAME);
                %>
                <table class="sortable listing" width="100%" summary="<%=cm.cmsPhrase("Search results")%>">
                  <thead>
                    <tr>
                  <%
                    if (showLevel && formBean.getDatabase().equalsIgnoreCase(RefDomain.SEARCH_EUNIS.toString())) {
                  %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_LEVEL%>&amp;ascendency=<%=formBean.changeAscendency(levelCrit, (null == levelCrit))%>"><%=Utilities.getSortImageTag(levelCrit)%><%=cm.cmsPhrase("Level")%></a>
                      </th>
                  <%
                    }
                    if (showCode)
                    {
                      if (0 == database.compareTo(RefDomain.SEARCH_BOTH))
                      {
                  %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit))%>"><%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsPhrase("EUNIS Code")%></a>
                      </th>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit))%>"><%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsPhrase("ANNEX I Code")%></a>
                      </th>
                  <%
                    }
                    if (0 == database.compareTo(RefDomain.SEARCH_EUNIS)) {
                  %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit))%>"><%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsPhrase("EUNIS Code")%></a>
                      </th>
                  <%
                    }
                    if (0 == database.compareTo(RefDomain.SEARCH_ANNEX_I)) {
                  %>
                      <th class="nosort" title="<%=cm.cmsPhrase("Sort results on this column")%>" scope="col">
                        <a href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit))%>"><%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsPhrase("ANNEX I Code")%></a>
                      </th>
                  <%
                      }
                    }
                  %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_SCIENTIFIC_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sciNameCrit, (null == sciNameCrit))%>"><%=Utilities.getSortImageTag(sciNameCrit)%><%=cm.cmsPhrase("Habitat type name")%></a>
                      </th>
                  <%
                    if (showVernacularName) {
                  %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_VERNACULAR_NAME%>&amp;ascendency=<%=formBean.changeAscendency(nameCrit, (null == nameCrit))%>"><%=Utilities.getSortImageTag(nameCrit)%><%=cm.cmsPhrase("English name")%></a>
                      </th>
                  <%
                    }
                  %>
                    </tr>
                  </thead>
                  <tbody>
                <%
                  int i = 0;
                  Iterator it = results.iterator();
                  // Display the result list
                  while (it.hasNext()) {
                    RefPersist habitat = (RefPersist) it.next();
                    String eunisCode = habitat.getEunisCode();
                    //String annexCode = habitat.getAnnex1Code();
                    String annexCode = habitat.getCode2000();
                    int level = habitat.getLevel().intValue();
                    boolean isEUNIS = Utilities.isHabitatEunis(habitat.getHabitatType());
                    if (isEUNIS) {
                      annexCode = "";
                    } else {
                      eunisCode = "";
                    }
                %>
                    <tr>
                  <%
                    if (showLevel && formBean.getDatabase().equalsIgnoreCase(RefDomain.SEARCH_EUNIS.toString())) {
                  %>
                      <td style="white-space : nowrap;">
                    <%
                      for (int iter = 0; iter < level; iter++) {
                    %>
                        <img alt="" src="images/mini/lev_blank.gif" />
                    <%
                      }
                    %>
                    <%=habitat.getLevel()%>
                      </td>
                  <%
                    }
                    if (showCode) {
                      if (0 == database.compareTo(RefDomain.SEARCH_BOTH)) {
                  %>
                      <td>
                        <%=eunisCode%>
                      </td>
                      <td>
                        <%=annexCode%>
                      </td>
                  <%
                    }
                    if (0 == database.compareTo(RefDomain.SEARCH_EUNIS)) {
                  %>
                      <td>
                        <%=eunisCode%>
                      </td>
                  <%
                    }
                    if (0 == database.compareTo(RefDomain.SEARCH_ANNEX_I)) {
                  %>
                      <td>
                        <%=annexCode%>
                      </td>
                  <%
                      }
                    }
                  %>
                      <td>
                        <a href="habitats/<%=habitat.getIdHabitat()%>"><%=JstlFunctions.bracketsToItalics(habitat.getScName())%></a>
                      </td>
                  <%
                    if (showVernacularName) {
                  %>
                      <td>
                        <%=habitat.getDescription()%>
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
                    if (showLevel && formBean.getDatabase().equalsIgnoreCase(RefDomain.SEARCH_EUNIS.toString())) {
                  %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_LEVEL%>&amp;ascendency=<%=formBean.changeAscendency(levelCrit, (null == levelCrit))%>"><%=Utilities.getSortImageTag(levelCrit)%><%=cm.cmsPhrase("Level")%></a>
                      </th>
                  <%
                    }
                    if (showCode)
                    {
                      if (0 == database.compareTo(RefDomain.SEARCH_BOTH))
                      {
                  %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit))%>"><%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsPhrase("EUNIS Code")%></a>
                      </th>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit))%>"><%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsPhrase("ANNEX I Code")%></a>
                      </th>
                  <%
                    }
                    if (0 == database.compareTo(RefDomain.SEARCH_EUNIS)) {
                  %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit))%>"><%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsPhrase("EUNIS Code")%></a>
                      </th>
                  <%
                    }
                    if (0 == database.compareTo(RefDomain.SEARCH_ANNEX_I)) {
                  %>
                      <th class="nosort" title="<%=cm.cmsPhrase("Sort results on this column")%>" scope="col">
                        <a href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit))%>"><%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsPhrase("ANNEX I Code")%></a>
                      </th>
                  <%
                      }
                    }
                  %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_SCIENTIFIC_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sciNameCrit, (null == sciNameCrit))%>"><%=Utilities.getSortImageTag(sciNameCrit)%><%=cm.cmsPhrase("Habitat type name")%></a>
                      </th>
                  <%
                    if (showVernacularName) {
                  %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_VERNACULAR_NAME%>&amp;ascendency=<%=formBean.changeAscendency(nameCrit, (null == nameCrit))%>"><%=Utilities.getSortImageTag(nameCrit)%><%=cm.cmsPhrase("English name")%></a>
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
                <%=cm.br()%>
                <%=cm.cmsMsg("habitats_references-result_title")%>
                <%=cm.br()%>
<!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>