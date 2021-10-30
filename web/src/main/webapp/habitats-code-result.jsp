<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Habitats code' function - results page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement,
                 ro.finsiel.eunis.jrfTables.habitats.code.CodeDomain,
                 ro.finsiel.eunis.jrfTables.habitats.code.CodePersist,
                 ro.finsiel.eunis.search.AbstractPaginator,
                 ro.finsiel.eunis.search.AbstractSearchCriteria,
                 ro.finsiel.eunis.search.AbstractSortCriteria,
                 ro.finsiel.eunis.search.Utilities,
                 ro.finsiel.eunis.search.habitats.HabitatsSearchUtility,
                 ro.finsiel.eunis.search.habitats.code.*,
                 java.util.Iterator" %>
<%@ page import="java.util.List" %><%@ page import="java.util.Vector" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<jsp:useBean id="formBean" class="ro.finsiel.eunis.search.habitats.code.CodeBean" scope="page">
  <jsp:setProperty name="formBean" property="*" />
</jsp:useBean>
<%
  WebContentManagement cm = SessionManager.getWebContent();
  if (null != formBean.getRemoveFilterIndex()) {
    formBean.prepareFilterCriterias();
  }
  boolean showExpanded = Utilities.checkedStringToBoolean(request.getParameter("expanded"),false);
  boolean showLevel = Utilities.checkedStringToBoolean(formBean.getShowLevel(), CodeBean.HIDE);
  boolean showCode = Utilities.checkedStringToBoolean(formBean.getShowCode(), CodeBean.HIDE);
  boolean showScientificName = Utilities.checkedStringToBoolean(formBean.getShowScientificName(), CodeBean.HIDE);
  boolean showOtherCodes = Utilities.checkedStringToBoolean(formBean.getShowOtherCodes(), CodeBean.HIDE);
  String searchedClassification = formBean.getClassificationCode();
  //boolean showEnglishName = Utilities.checkedStringToBoolean(formBean.getShowEnglishName(), CodeBean.HIDE);
  // Set number criteria for the search result
  int noCriteria = (null == formBean.getCriteriaSearch() ? 0 : formBean.getCriteriaSearch().length);

  Integer relationOp = Utilities.checkedStringToInt(formBean.getRelationOp(), Utilities.OPERATOR_CONTAINS);

  int currentPage = Utilities.checkedStringToInt(formBean.getCurrentPage(), 0);
  Integer database = Utilities.checkedStringToInt(formBean.getDatabase(), CodeDomain.SEARCH_EUNIS);
  // The main paginator
  CodePaginator paginator = new CodePaginator(new CodeDomain(formBean.toSearchCriteria(), formBean.toSortCriteria(), database));
  paginator.setSortCriteria(formBean.toSortCriteria());
  paginator.setPageSize(Utilities.checkedStringToInt(formBean.getPageSize(), AbstractPaginator.DEFAULT_PAGE_SIZE));
  currentPage = paginator.setCurrentPage(currentPage);// Compute *REAL* current page (adjusted if user messes up)
  int resultsCount = paginator.countResults();
  final String pageName = "habitats-code-result.jsp";
  int pagesCount = paginator.countPages();// This is used in @page include...
  int guid = 0;// This is used in @page include...
  // Now extract the results for the current page.
  List results = paginator.getPage(currentPage);

  // Prepare parameters for tsvlink
  Vector reportFields = new Vector();
  reportFields.addElement("sort");
  reportFields.addElement("ascendency");
  reportFields.addElement("criteriaSearch");
  reportFields.addElement("criteriaSearch");
  reportFields.addElement("oper");
  reportFields.addElement("criteriaType");

  String tsvLink = "javascript:openTSVDownload('reports/habitats/tsv-habitats-code.jsp?" + formBean.toURLParam(reportFields) + "')";
  String eeaHome = application.getInitParameter( "EEA_HOME" );
  String location = "eea#" + eeaHome + ",home#index.jsp,habitat_types#habitats.jsp,code_column#habitats-code.jsp,results";
  if (results.isEmpty())
  {
    boolean fromRefine = false;
    if(formBean != null && formBean.getCriteriaSearch() != null && formBean.getCriteriaSearch().length > 0)
    {
      fromRefine = true;
    }
 %>
  <jsp:forward page="emptyresults.jsp">
    <jsp:param name="location" value="<%=location%>" />
    <jsp:param name="fromRefine" value="<%=fromRefine%>" />
  </jsp:forward>
<%
  }
%>
<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cms("habitats_code-result_title") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" helpLink="habitats-help.jsp" pageTitle="${title}" downloadLink="<%= tsvLink%>" btrail="<%= location%>">
    <stripes:layout-component name="head">
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/eea_search.css">
        <script language="JavaScript" src="<%=request.getContextPath()%>/script/habitats-result.js" type="text/javascript"></script>

        <script language="JavaScript" type="text/javascript">
        //<![CDATA[
            function MM_openBrWindow(theURL,winName,features) { //v2.0
                window.open(theURL,winName,features);
            }
        //]]>
        </script>
    </stripes:layout-component>
    <stripes:layout-component name="contents">
        <a name="documentContent"></a>
                <h1>
                  <%=cm.cmsPhrase("Code/Classifications")%>
                </h1>
<!-- MAIN CONTENT -->
                <table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                <td>
                <%-- Here are the main search criterias displayed to the user --%>
                <%AbstractSearchCriteria mainCriteria = formBean.getMainSearchCriteria();%>
                <table width="100%" summary="layout" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td>
                      <%=cm.cmsPhrase("You searched for")%>
                      <strong>
                        <%=Utilities.getSourceHabitat(database, CodeDomain.SEARCH_ANNEX.intValue(), CodeDomain.SEARCH_BOTH.intValue())%>
                      </strong>
                      <%=cm.cmsPhrase("habitat types related to habitat types having")%>
                      <strong>
                        <%=mainCriteria.toHumanString()%> <strong><%=cm.cmsPhrase("in the classification:")%> <%=HabitatsSearchUtility.getClassificationName(formBean.getClassificationCode())%></strong>
                      </strong>
                    </td>
                  </tr>
                </table>
                <%=cm.cmsPhrase("Results found")%>: <strong><%=resultsCount%></strong>
                <%// Prepare parameters for pagesize.jsp
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
                <%// Prepare the form parameters.
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
                <table width="100%" border="0" summary="layout" cellspacing="0" cellpadding="0">
                  <tr>
                    <td bgcolor="#EEEEEE">
                      <strong>
                        <%=cm.cmsPhrase("Refine your search")%>
                      </strong>
                    </td>
                  </tr>
                  <tr>
                    <td bgcolor="#EEEEEE">
                      <form name="resultSearch" method="get" onsubmit="return(checkHabitats(<%=noCriteria%>));" action="habitats-code-result.jsp">
                        <%=formBean.toFORMParam(filterSearch)%>
                        <label for="criteriaType" class="noshow"><%=cm.cmsPhrase("Criteria")%></label>
                        <select title="<%=cm.cmsPhrase("Criteria")%>" id="criteriaType" name="criteriaType">
                          <%
                            if (showCode && 0 == database.compareTo(CodeDomain.SEARCH_EUNIS)) {
                          %>
                          <option value="<%=CodeSearchCriteria.CRITERIA_EUNIS_CODE%>"><%=cm.cms("eunis_code")%></option>
                          <%
                            }
                          %>
                          <%
                            if (showCode && 0 == database.compareTo(CodeDomain.SEARCH_ANNEX)) {
                          %>
                          <option value="<%=CodeSearchCriteria.CRITERIA_ANNEX_CODE%>"><%=cm.cms("annex_code")%></option>
                          <%
                            }
                          %>
                          <%
                            if (showCode && 0 == database.compareTo(CodeDomain.SEARCH_BOTH)) {
                          %>
                          <option value="<%=CodeSearchCriteria.CRITERIA_EUNIS_CODE%>"><%=cm.cms("eunis_code")%></option>
                          <option value="<%=CodeSearchCriteria.CRITERIA_ANNEX_CODE%>"><%=cm.cms("annex_code")%></option>
                          <%
                            }
                          %>
                          <%
                            if (0 == database.compareTo(CodeDomain.SEARCH_EUNIS) && showLevel) {
                          %>
                          <option value="<%=CodeSearchCriteria.CRITERIA_LEVEL%>"><%=cm.cms("generic_index_07")%></option>
                          <%
                            }
                          %>
                          <%
                            if (showScientificName) {
                          %>
                          <option value="<%=CodeSearchCriteria.CRITERIA_SCIENTIFIC_NAME%>" selected="selected"><%=cm.cms("habitat_type_name")%></option>
                          <%
                            }
                          %>
                        </select>
                        <%=cm.cmsInput("eunis_code")%>
                        <%=cm.cmsInput("annex_code")%>
                        <%=cm.cmsInput("generic_index_07")%>
                        <select title="<%=cm.cmsPhrase("Operator")%>" name="oper" id="oper">
                          <option value="<%=Utilities.OPERATOR_IS%>" selected="selected"><%=cm.cmsPhrase("is")%></option>
                          <option value="<%=Utilities.OPERATOR_STARTS%>"><%=cm.cmsPhrase("starts with")%></option>
                          <option value="<%=Utilities.OPERATOR_CONTAINS%>"><%=cm.cmsPhrase("contains")%></option>
                        </select>
                        <label for="criteriaSearch" class="noshow"><%=cm.cmsPhrase("Filter value")%></label>
                        <input title="<%=cm.cmsPhrase("Filter value")%>" name="criteriaSearch" id="criteriaSearch" type="text" size="30" />
                        <input title="Search" class="submitSearchButton" type="submit" name="Submit" id="Submit" value="<%=cm.cmsPhrase("Search")%>" />
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
                      <%=cm.cmsPhrase("Applied filters to the results")%>:
                    </td>
                  </tr>
                  <%
                    }
                  %>
                  <%for (int i = criterias.length - 1; i > 0; i--) {%>
                  <%AbstractSearchCriteria criteria = criterias[i];%>
                  <%if (null != criteria && null != formBean.getCriteriaSearch()) {%>
                  <tr>
                    <td bgcolor="#CCCCCC">
                      <a title="Delete filter" href="<%= pageName%>?<%=formBean.toURLParam(filterSearch)%>&amp;removeFilterIndex=<%=i%>">
                        <img alt="Delete filter" src="images/mini/delete.jpg" border="0" style="vertical-align:middle" />
                      </a>
                      &nbsp;&nbsp;
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

                <%
// Expand/Collapse other classifications
                    Vector expand = new Vector();
                    expand.addElement("sort");
                    expand.addElement("ascendency");
                    expand.addElement("criteriaSearch");
                    expand.addElement("oper");
                    expand.addElement("criteriaType");
                    expand.addElement("pageSize");
                    expand.addElement("currentPage");
                    String expandURL = formBean.toURLParam(expand);

                  if (showOtherCodes && !showExpanded)
                  {
                %>
                   <a title="<%=cm.cms("show_habitats_codes_in_other_classifications")%>" href="<%=pageName + "?expanded=" + !showExpanded + expandURL%>"><%=cm.cmsPhrase("Display in results habitat types informations in all classifications")%></a>
                <%
                  }
                  // Compute the sort criteria
                  Vector sortURLFields = new Vector();      /* Used for sorting */
                  sortURLFields.addElement("pageSize");
                  sortURLFields.addElement("criteriaSearch");
                  sortURLFields.addElement("oper");
                  sortURLFields.addElement("criteriaType");
                  sortURLFields.addElement("currentPage");
                  sortURLFields.addElement("expand");
                  String urlSortString = formBean.toURLParam(sortURLFields);
                  AbstractSortCriteria levelCrit = formBean.lookupSortCriteria(CodeSortCriteria.SORT_LEVEL);
                  AbstractSortCriteria sciNameCrit = formBean.lookupSortCriteria(CodeSortCriteria.SORT_SCIENTIFIC_NAME);
                  AbstractSortCriteria eunisCodeCrit = formBean.lookupSortCriteria(CodeSortCriteria.SORT_EUNIS_CODE);
                  AbstractSortCriteria annexCodeCrit = formBean.lookupSortCriteria(CodeSortCriteria.SORT_ANNEX_CODE);
                %>
                <table class="sortable listing" width="100%" summary="<%=cm.cmsPhrase("Search results")%>">
                  <thead>
                    <tr>
                  <%
                    if (0 == database.compareTo(CodeDomain.SEARCH_EUNIS) && showLevel)
                    {
                  %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=CodeSortCriteria.SORT_LEVEL%>&amp;ascendency=<%=formBean.changeAscendency(levelCrit, (null == levelCrit) ? true : false)%>">
                          <%=Utilities.getSortImageTag(levelCrit)%><%=cm.cmsPhrase("Level")%>
                        </a>
                      </th>
                  <%
                    }
                  %>
                  <%
                    if (showCode)
                    {
                  %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=CodeSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit) ? true : false)%>">
                          <%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsPhrase("EUNIS Code")%>
                        </a>
                      </th>
                      <th class="nosort" scope="col" width="30">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=CodeSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit) ? true : false)%>">
                          <%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsPhrase("ANNEX I Code")%>
                        </a>
                      </th>
                  <%
                    }
                  %>
                  <%
                    if (showScientificName)
                    {
                  %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=CodeSortCriteria.SORT_SCIENTIFIC_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sciNameCrit, (null == sciNameCrit) ? true : false)%>">
                          <%=Utilities.getSortImageTag(sciNameCrit)%><%=cm.cmsPhrase("Habitat type name")%>
                        </a>
                      </th>
                  <%
                    }
                  %>
                  <%
                    if (showOtherCodes)
                    {
                  %>
                      <th class="nosort" scope="col" style="text-align : center;" width="361">
                        <%=cm.cmsPhrase("Other codes<br />(Code | Classification | Relation)")%>
                  <%
                        if(showExpanded)
                        {
                  %>
                        <br />
                        [<a title="<%=cm.cms("show_habitats_codes_in_other_classifications")%>" href="<%=pageName + "?expanded=" + !showExpanded + expandURL%>"><%=cm.cmsPhrase("Hide other classifications")%></a>]
                        <%=cm.cmsTitle("show_habitats_codes_in_other_classifications")%>
                  <%
                        }
                  %>
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
                  while (it.hasNext())
                  {
                    CodePersist habitat = (CodePersist) it.next();
                    int level = habitat.getHabLevel().intValue();
                    String eunisCode = habitat.getEunisHabitatCode();
                    //String annexCode = habitat.getCodeAnnex1();
                    String annexCode = habitat.getCode2000();
                    int idHabitat = Utilities.checkedStringToInt(habitat.getIdHabitat(), -1);
                    if (Utilities.isHabitatEunis(habitat.getHabitatType())) {
                      annexCode = "&nbsp;";
                    } else {
                      eunisCode = "&nbsp;";
                    }
                %>
                    <tr>
                  <%
                        if (0 == database.compareTo(CodeDomain.SEARCH_EUNIS) && showLevel)
                        {
                      %>
                      <td style="white-space:nowrap">
                      <%for (int iter = 0; iter < level; iter++) {%>
                        <img title="" src="images/mini/lev_blank.gif" alt="" style="vertical-align:middle" />
                      <%}%>
                      <%=level%>
                      </td>
                      <%}%>
                      <%if (showCode)
                      {
                      %>
                      <td>
                        <%=eunisCode%>
                      </td>
                      <td>
                        <%=annexCode%>
                      </td>
                    <%
                      }
                    %>
                    <%
                      if (showScientificName)
                      {
                    %>
                      <td>
                        <a href="habitats/<%=habitat.getIdHabitat()%>"><%=habitat.getScientificName()%></a>
                      </td>
                  <%}%>
                  <%
                    if (showOtherCodes) {
                      List otherClassifications = HabitatsSearchUtility.findOtherClassifications(idHabitat, relationOp, formBean.getSearchString());
                  %>
                      <td>
                        <table summary="layout" width="100%" border="1" cellspacing="0" cellpadding="0">
                      <%
                        int jj = 0;
                        for (int j = 0; j < otherClassifications.size(); j++) {
                          String bgColor1 = (0 == (jj++ % 2)) ? "#EEEEEE" : "#FFFFFF";
                          OtherClassWrapper classification = (OtherClassWrapper) otherClassifications.get(j);

                          if(!showExpanded) {
                            if(!classification.getClassificationCode().equalsIgnoreCase(searchedClassification)) {
                              continue;
                            }
                          }
                         String beginStrong = "";
                         String endStrong = "";

                         if(showExpanded && classification.getClassificationCode().equalsIgnoreCase(searchedClassification))
                         {
                            beginStrong = "<strong>";
                            endStrong = "</strong>";
                         }
                      %>
                          <tr style="background-color:<%=bgColor1%>">
                            <td>
                              <%=beginStrong%><%=classification.getCode()%><%=endStrong%>
                            </td>
                            <td>
                              <%=beginStrong%><%=classification.getClassificatioName()%><%=endStrong%>
                            </td>
                            <td>
                              <%=beginStrong%><%=classification.getRelationDecoded()%><%=endStrong%>
                            </td>
                          </tr>
                          <tr style="background-color:<%=bgColor1%>">
                            <td colspan="3" style="text-align:left">
                              <%=beginStrong%><%=Utilities.formatString(classification.getTitle(),"&nbsp;")%><%=endStrong%>
                            </td>
                          </tr>
                      <%
                        }
                      %>
                        </table>
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
                    if (0 == database.compareTo(CodeDomain.SEARCH_EUNIS) && showLevel)
                    {
                  %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=CodeSortCriteria.SORT_LEVEL%>&amp;ascendency=<%=formBean.changeAscendency(levelCrit, (null == levelCrit) ? true : false)%>">
                          <%=Utilities.getSortImageTag(levelCrit)%><%=cm.cmsPhrase("Level")%>
                        </a>
                      </th>
                  <%
                    }
                  %>
                  <%
                    if (showCode)
                    {
                  %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=CodeSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit) ? true : false)%>">
                          <%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsPhrase("EUNIS Code")%>
                        </a>
                      </th>
                      <th class="nosort" scope="col" width="30">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=CodeSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit) ? true : false)%>">
                          <%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsPhrase("ANNEX I Code")%>
                        </a>
                      </th>
                  <%
                    }
                  %>
                  <%
                    if (showScientificName)
                    {
                  %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=CodeSortCriteria.SORT_SCIENTIFIC_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sciNameCrit, (null == sciNameCrit) ? true : false)%>">
                          <%=Utilities.getSortImageTag(sciNameCrit)%><%=cm.cmsPhrase("Habitat type name")%>
                        </a>
                      </th>
                  <%
                    }
                  %>
                  <%
                    if (showOtherCodes)
                    {
                  %>
                      <th class="nosort" scope="col" style="text-align : center;" width="361">
                        <%=cm.cmsPhrase("Other codes<br />(Code | Classification | Relation)")%>
                  <%
                        if(showExpanded)
                        {
                  %>
                        <br />
                        [<a title="<%=cm.cms("show_habitats_codes_in_other_classifications")%>" href="<%=pageName + "?expanded=" + !showExpanded + expandURL%>"><%=cm.cmsPhrase("Hide other classifications")%></a>]
                        <%=cm.cmsTitle("show_habitats_codes_in_other_classifications")%>
                  <%
                        }
                  %>
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
                  <%=cm.cmsMsg("habitats_code-result_title")%>
                  <%=cm.br()%>
<!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>