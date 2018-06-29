<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Habitats legal instruments' function - results page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement,
                 ro.finsiel.eunis.formBeans.AbstractFormBean,
                 ro.finsiel.eunis.jrfTables.habitats.legal.EUNISLegalDomain,
                 ro.finsiel.eunis.jrfTables.habitats.legal.EUNISLegalPersist,
                 ro.finsiel.eunis.search.AbstractPaginator,
                 ro.finsiel.eunis.search.AbstractSearchCriteria,
                 ro.finsiel.eunis.search.AbstractSortCriteria,
                 ro.finsiel.eunis.search.Utilities,
                 ro.finsiel.eunis.search.habitats.HabitatsSearchUtility,
                 ro.finsiel.eunis.search.habitats.legal.LegalPaginator,
                 ro.finsiel.eunis.search.habitats.legal.LegalSearchCriteria,
                 ro.finsiel.eunis.search.habitats.legal.LegalSortCriteria,
                 java.util.List" %>
<%@ page import="java.util.Vector" %>
<%@ page import="eionet.eunis.util.JstlFunctions" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<jsp:useBean id="formBean" class="ro.finsiel.eunis.search.habitats.legal.LegalBean" scope="request">
  <jsp:setProperty name="formBean" property="*" />
</jsp:useBean>
<%
  // Prepare the search in results (fix)
  if (null != formBean.getRemoveFilterIndex())
  {
    formBean.prepareFilterCriterias();
  }
  // Request parameters
  boolean showCode = Utilities.checkedStringToBoolean(formBean.getShowCode(), AbstractFormBean.HIDE);
  boolean showScientificName = Utilities.checkedStringToBoolean(formBean.getShowScientificName(), AbstractFormBean.HIDE);
  boolean showLegalText = Utilities.checkedStringToBoolean(formBean.getShowLegalText(), AbstractFormBean.HIDE);
  // Set number criteria for the search result
  int noCriteria = (null == formBean.getCriteriaSearch() ? 0 : formBean.getCriteriaSearch().length);

  int currentPage = Utilities.checkedStringToInt(formBean.getCurrentPage(), 0);
  // The main paginator
  LegalPaginator paginator = new LegalPaginator(new EUNISLegalDomain(formBean.toSearchCriteria(), formBean.toSortCriteria()));
  // Initialization
  paginator.setSortCriteria(formBean.toSortCriteria());
  paginator.setPageSize(Utilities.checkedStringToInt(formBean.getPageSize(), AbstractPaginator.DEFAULT_PAGE_SIZE));
  currentPage = paginator.setCurrentPage(currentPage);// Compute *REAL* current page (adjusted if user messes up)
  int resultsCount = paginator.countResults();
  final String pageName = "habitats-legal-result.jsp";
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

  String tsvLink = "javascript:openTSVDownload('reports/habitats/tsv-habitats-legal.jsp?" + formBean.toURLParam(reportFields) + "')";
  WebContentManagement cm = SessionManager.getWebContent();
  String eeaHome = application.getInitParameter( "EEA_HOME" );
  String location = "eea#" + eeaHome + ",home#index.jsp,habitat_types#habitats.jsp,legal_instruments#habitats-legal.jsp,results";
  if (results.isEmpty())
  {
    boolean fromRefine = formBean != null && formBean.getCriteriaSearch() != null && formBean.getCriteriaSearch().length > 0;
%>
  <jsp:forward page="emptyresults.jsp">
    <jsp:param name="location" value="<%=location%>" />
    <jsp:param name="fromRefine" value="<%=fromRefine%>" />
  </jsp:forward>
<%
  }
%>

<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cms("habitats_legal-result_title") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" helpLink="habitats-help.jsp" pageTitle="${title}" downloadLink="<%= tsvLink%>" btrail="<%= location%>">
    <stripes:layout-component name="head">
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/eea_search.css">
        <script language="JavaScript" src="<%=request.getContextPath()%>/script/habitats-result.js" type="text/javascript"></script>
    </stripes:layout-component>
    <stripes:layout-component name="contents">

        <a name="documentContent"></a>
        <h1><%=cm.cmsPhrase("Legal Instruments")%></h1>
<!-- MAIN CONTENT -->
                <table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                <td>
                  <table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
                    <%LegalSearchCriteria mainSearch = (LegalSearchCriteria) formBean.getMainSearchCriteria();%>
                    <%String realSearch = (null != formBean.getSearchString() && formBean.getSearchString().equalsIgnoreCase("%")) ? "" : " for which " + mainSearch.toHumanString();
                      String scientName = HabitatsSearchUtility.getHabitatLevelName(formBean.getHabitatType());%>
                    <tr>
                      <td>
                        <%=cm.cmsPhrase("You searched for <strong>EUNIS</strong> habitat types from")%>
                        '<strong><%=scientName%>'
                        <%=realSearch%>
                        <%=cm.cmsPhrase(", protected by ")%>
                        '<%=StringEscapeUtils.escapeXml(formBean.getLegalText())%>'
                        <%=cm.cmsPhrase("legal text")%>
                      </strong>
                      </td>
                    </tr>
                  </table>
                <%=cm.cmsPhrase("Results found")%>:&nbsp;<strong><%=resultsCount%></strong>
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
                <br />
                <%
                  // Prepare the form parameters.
                  Vector filterSearch = new Vector();
                  filterSearch.addElement("sort");
                  filterSearch.addElement("ascendency");
                  filterSearch.addElement("criteriaSearch");
                  filterSearch.addElement("oper");
                  filterSearch.addElement("criteriaType");
                  filterSearch.addElement("pageSize");
                  filterSearch.addElement("expand");%>
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
                      <form name="resultSearch" method="get" onsubmit="return(checkHabitats(<%=noCriteria%>));" action="">
                        <%=formBean.toFORMParam(filterSearch)%>
                        <label for="criteriaType" class="noshow"><%=cm.cmsPhrase("Criteria")%></label>
                        <select title="Criteria" name="criteriaType" id="criteriaType">
                          <%if (showCode) {%>
                          <option value="<%=LegalSearchCriteria.CRITERIA_EUNIS_CODE%>"><%=cm.cms("eunis_code")%></option><%}%>
                          <%if (showScientificName) {%>
                          <option value="<%=LegalSearchCriteria.CRITERIA_SCIENTIFIC_NAME%>" selected="selected"><%=cm.cms("habitat_type_name")%></option><%}%>
                          <%if (showLegalText) {%>
                          <option value="<%=LegalSearchCriteria.CRITERIA_LEGAL_TEXT%>"><%=cm.cms("legal_text")%></option><%}%>
                        </select>
                        <%=cm.cmsInput("eunis_code")%>
                        <%=cm.cmsInput("habitat_type_name")%>
                        <%=cm.cmsInput("legal_text")%>
                        <label for="oper" class="noshow"><%=cm.cmsPhrase("Criteria")%></label>
                        <select title="Operator" name="oper" id="oper">
                          <option value="<%=Utilities.OPERATOR_IS%>" selected="selected"><%=cm.cmsPhrase("is")%></option>
                          <option value="<%=Utilities.OPERATOR_STARTS%>"><%=cm.cmsPhrase("starts with")%></option>
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
                    AbstractSearchCriteria[] criterias = formBean.toSearchCriteria();
                    if (criterias.length > 1) {
                  %>
                  <tr>
                    <td bgcolor="#EEEEEE">
                      <%=cm.cmsPhrase("Criterias applied: (Click on item to go back to that stage)")%>
                    </td>
                  </tr>
                  <%
                    }
                    for (int i = criterias.length - 1; i > 0; i--) {
                      AbstractSearchCriteria criteria = criterias[i];
                      if (null != criteria && null != formBean.getCriteriaSearch()) {
                  %>
                  <tr>
                    <td bgcolor="#EEEEEE">
                      <a title="<%=cm.cms("delete_filter")%>" href="<%= pageName%>?<%=formBean.toURLParam(filterSearch)%>&amp;removeFilterIndex=<%=i%>">
                        <img src="images/mini/delete.jpg" alt="<%=cm.cms("delete_filter")%>" border="0" style="vertical-align:middle" />
                      </a>
                      <%=cm.cms("delete_filter")%>&nbsp;&nbsp;
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

                <%// Compute the sort criteria
                  Vector sortURLFields = new Vector();      /* Used for sorting */
                  sortURLFields.addElement("pageSize");
                  sortURLFields.addElement("criteriaSearch");
                  sortURLFields.addElement("oper");
                  sortURLFields.addElement("criteriaType");
                  sortURLFields.addElement("currentPage");
                  sortURLFields.addElement("expand");
                  String urlSortString = formBean.toURLParam(sortURLFields);
                  AbstractSortCriteria codeCrit = formBean.lookupSortCriteria(LegalSortCriteria.SORT_EUNIS_CODE);
                  AbstractSortCriteria sciNameCrit = formBean.lookupSortCriteria(LegalSortCriteria.SORT_SCIENTIFIC_NAME);
                  AbstractSortCriteria legalTxtCrit = formBean.lookupSortCriteria(LegalSortCriteria.SORT_LEGAL_INSTRUMENTS);
                  AbstractSortCriteria sortLevel = formBean.lookupSortCriteria(LegalSortCriteria.SORT_LEVEL);
                %>
                <table class="sortable listing" width="100%" summary="<%=cm.cmsPhrase("Search results")%>">
                  <thead>
                    <tr>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=LegalSortCriteria.SORT_LEVEL%>&amp;ascendency=<%=formBean.changeAscendency(sortLevel, (null == sortLevel) ? true : false)%>"><%=Utilities.getSortImageTag(sortLevel)%><%=cm.cmsPhrase("Level")%></a>
                      </th>
                      <%
                        if (showCode)
                        {
                      %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=LegalSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(codeCrit, (null == codeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(codeCrit)%><%=cm.cmsPhrase("EUNIS Code")%></a>
                      </th>
                      <%
                        }
                      %>
                      <%if (showScientificName) {%>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=LegalSortCriteria.SORT_SCIENTIFIC_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sciNameCrit, (null == sciNameCrit) ? true : false)%>"><%=Utilities.getSortImageTag(sciNameCrit)%><%=cm.cmsPhrase("Habitat type name")%></a>
                      </th>
                      <%}%>
                      <%if (showLegalText) {%>
                      <th class="nosort" scope="col">
                        <%=cm.cmsPhrase("Legal text")%>
                      </th>
                      <%}%>
                    </tr>
                  </thead>
                  <tbody>
                <%
                  for (int i = 0; i < results.size(); i++)
                  {
                    EUNISLegalPersist habitat = (EUNISLegalPersist) results.get(i);
                    int level = habitat.getHabLevel().intValue();
                %>
                    <tr>
                      <td style="white-space : nowrap;">
                <%
                    for (int iter = 0; iter < level; iter++)
                    {
                %>
                        <img alt="" src="images/mini/lev_blank.gif" />
                <%
                    }
                %>
                        <%=level%>
                      </td>
                      <%if (showCode) {%>
                      <td>
                        <%=habitat.getEunisHabitatCode()%>
                      </td>
                <%
                    }
                    if (showScientificName)
                    {
                  %>
                      <td>
                        <a href="habitats/<%=habitat.getIdHabitat()%>"><%=JstlFunctions.bracketsToItalics(habitat.getScientificName())%></a>
                      </td>
                  <%}%>
                  <%if (showLegalText) {%>
                      <td>
                        <%--<ul>--%>
                      <%
                        List legalTexts = HabitatsSearchUtility.findHabitatLegalInstrument(habitat.getIdHabitat());
                        for (int j = 0; j < legalTexts.size(); j++) {
                          EUNISLegalPersist legalText = (EUNISLegalPersist) legalTexts.get(j);
                      %>
                            <p>
                              <%=legalText.getLegalName()%>
                            </p>
                      <%
                        }
                      %>
                        <%--</ul>--%>
                      </td>
                  <%}%>
                    </tr>
                <%}%>
                  </tbody>
                  <thead>
                    <tr>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=LegalSortCriteria.SORT_LEVEL%>&amp;ascendency=<%=formBean.changeAscendency(sortLevel, (null == sortLevel) ? true : false)%>"><%=Utilities.getSortImageTag(sortLevel)%><%=cm.cmsPhrase("Level")%></a>
                      </th>
                      <%
                        if (showCode)
                        {
                      %>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=LegalSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(codeCrit, (null == codeCrit) ? true : false)%>"><%=Utilities.getSortImageTag(codeCrit)%><%=cm.cmsPhrase("EUNIS Code")%></a>
                      </th>
                      <%
                        }
                      %>
                      <%if (showScientificName) {%>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=LegalSortCriteria.SORT_SCIENTIFIC_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sciNameCrit, (null == sciNameCrit) ? true : false)%>"><%=Utilities.getSortImageTag(sciNameCrit)%><%=cm.cmsPhrase("Habitat type name")%></a>
                      </th>
                      <%}%>
                      <%if (showLegalText) {%>
                      <th class="nosort" scope="col">
                        <%=cm.cmsPhrase("Legal text")%>
                      </th>
                      <%}%>
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
                <%=cm.cmsMsg("habitats_legal-result_title")%>
                <%=cm.br()%>
                <%=cm.cmsMsg("legal_instruments")%>
                <%=cm.br()%>
<!-- END MAIN CONTENT -->

    </stripes:layout-component>
</stripes:layout-render>
