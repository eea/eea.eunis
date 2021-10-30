<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Habitats names and descriptions' function - results page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement,
                 ro.finsiel.eunis.jrfTables.Chm62edtSoundexDomain,
                 ro.finsiel.eunis.jrfTables.Chm62edtSoundexPersist,
                 ro.finsiel.eunis.jrfTables.habitats.names.NamesDomain,
                 ro.finsiel.eunis.jrfTables.habitats.names.NamesPersist,
                 ro.finsiel.eunis.search.AbstractPaginator,
                 ro.finsiel.eunis.search.AbstractSearchCriteria,
                 ro.finsiel.eunis.search.AbstractSortCriteria,
                 ro.finsiel.eunis.search.Utilities,
                 ro.finsiel.eunis.search.habitats.names.NameBean,
                 ro.finsiel.eunis.search.habitats.names.NamePaginator,
                 ro.finsiel.eunis.search.habitats.names.NameSearchCriteria" %>
<%@ page import="ro.finsiel.eunis.search.habitats.names.NameSortCriteria" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Vector" %>
<%@ page import="eionet.eunis.util.JstlFunctions" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<jsp:useBean id="formBean" class="ro.finsiel.eunis.search.habitats.names.NameBean" scope="page">
  <jsp:setProperty name="formBean" property="*" />
</jsp:useBean>
<%
  // Prepare the search in results (fix)
  if (null != formBean.getRemoveFilterIndex()) {
    formBean.prepareFilterCriterias();
  }
  // Request parameters.
  boolean showLevel = Utilities.checkedStringToBoolean(formBean.getShowLevel(), NameBean.HIDE);
  boolean showCode = Utilities.checkedStringToBoolean(formBean.getShowCode(), NameBean.HIDE);
  boolean showScientificName = Utilities.checkedStringToBoolean(formBean.getShowScientificName(), NameBean.HIDE);
  boolean showVernacularName = Utilities.checkedStringToBoolean(formBean.getShowVernacularName(), NameBean.HIDE);
  boolean newName = Utilities.checkedStringToBoolean(formBean.getNewName(), false);
  boolean noSoundex = Utilities.checkedStringToBoolean( formBean.getNoSoundex(), false );
  boolean fuzzySearch = Utilities.checkedStringToBoolean(formBean.getFuzzySearch(), false);
  // Set number criteria for the search result
  int noCriteria = (null == formBean.getCriteriaSearch() ? 0 : formBean.getCriteriaSearch().length);
  int currentPage = Utilities.checkedStringToInt(formBean.getCurrentPage(), 0);
  Integer database = Utilities.checkedStringToInt(formBean.getDatabase(), NamesDomain.SEARCH_EUNIS);
  // The main paginator
  NamePaginator paginator = new NamePaginator(new NamesDomain(formBean.toSearchCriteria(), formBean.getMainSearchCriteriasExtra(), formBean.toSortCriteria(), database, fuzzySearch));
  // Initialization
  paginator.setSortCriteria(formBean.toSortCriteria());
  paginator.setPageSize(Utilities.checkedStringToInt(formBean.getPageSize(), AbstractPaginator.DEFAULT_PAGE_SIZE));
  currentPage = paginator.setCurrentPage(currentPage);// Compute *REAL* current page (adjusted if user messes up)  
  final String pageName = "habitats-names-result.jsp";
  int pagesCount = paginator.countPages();// This is used in @page include...
  int guid = 0;// This is used in @page include...
  // Now extract the results for the current page.
  List results = paginator.getPage(currentPage);
  int resultsCount = paginator.countResults();
  // Prepare parameters for tsvlink
  Vector reportFields = new Vector();
  reportFields.addElement("sort");
  reportFields.addElement("ascendency");
  reportFields.addElement("criteriaSearch");
  reportFields.addElement("oper");
  reportFields.addElement("criteriaType");

  String tsvLink = "javascript:openTSVDownload('reports/habitats/tsv-habitats-names.jsp?" + formBean.toURLParam(reportFields) + "')";

  if (results.isEmpty() && !newName  && !noSoundex) {
    String sname = formBean.getSearchString();
    List list = new Vector();
    String cnstSoundex = new String(ro.finsiel.eunis.utilities.SQLUtilities.smartSoundex);
    cnstSoundex = cnstSoundex.replaceAll("<name>", sname.replaceAll("'", "''"));
    cnstSoundex = cnstSoundex.replaceAll("<object_type>", "HABITAT");
    list = new Chm62edtSoundexDomain().findCustom(cnstSoundex);
    if (list != null && list.size() > 0) {
      Chm62edtSoundexPersist t = (Chm62edtSoundexPersist) list.get(0);
      String soundexName = t.getName();
      try {
        String URL = "habitats-names-result.jsp?showScientificName=true&deleteIndex=-1&sort=3&ascendency=1&showLevel=true&showCode=true&relationOp=4&noSoundex=false&searchString=" + soundexName + "&database=" + database + "&useScientific=true&useVernacular=true&newName=true&oldName=" + sname + "&fuzzySearch="+formBean.getFuzzySearch();
        response.sendRedirect(URL);
        return;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  String eeaHome = application.getInitParameter( "EEA_HOME" );
  String location = "eea#" + eeaHome + ",home#index.jsp,habitat_types#habitats.jsp,names#habitats-names.jsp,results";
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
<%
  WebContentManagement cm = SessionManager.getWebContent();
%>
<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cms("habitats_names-result_title") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" helpLink="habitats-help.jsp" pageTitle="${title}" downloadLink="<%= tsvLink%>" btrail="<%= location%>">
    <stripes:layout-component name="head">
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/eea_search.css">
      <script language="JavaScript" src="<%=request.getContextPath()%>/script/habitats-result.js" type="text/javascript"></script>
      <script language="JavaScript" type="text/javascript">
      //<![CDATA[
        function openPopup(theURL,winName,features) { //v2.0
          window.open(theURL,winName,features);
        }
      //]]>
      </script>
    </stripes:layout-component>
    <stripes:layout-component name="contents">
        <a name="documentContent"></a>
        <h1>
        <%=cm.cmsPhrase("Names and Descriptions")%>
        </h1>
<!-- MAIN CONTENT -->
                <table width="100%" summary="layout" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td>
                      <%AbstractSearchCriteria mainCriteria = formBean.getMainSearchCriteria();%>
                      <table width="100%" summary="layout" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                          <td>
                            <%
                            if (!results.isEmpty()) {
                              if (newName) {
                                String searchDescription = "";
                                if(!formBean.getOldName().equalsIgnoreCase(formBean.getSearchString())) {
                                  searchDescription = cm.cms("no_match_was_found_for") + " <strong>" + StringEscapeUtils.escapeXml(formBean.getOldName()) + "</strong>.&nbsp;";
                                  searchDescription += cm.cms("the_closest_phonetic_match") + " <strong>" + StringEscapeUtils.escapeXml(formBean.getSearchString()) + "</strong>";
                                } else {
                                  searchDescription += cm.cms("the_closest_phonetic_match") + " <strong>" + StringEscapeUtils.escapeXml(formBean.getSearchString()) + "</strong>";
                                }
                            %>
                            <%=searchDescription%>
                            <%=cm.cmsMsg("no_match_was_found_for")%>
                            <%=cm.cmsMsg("the_closest_phonetic_match")%>
                            <%
                            } else {
                            %>
                            <%=cm.cmsPhrase("You searched for")%>
                            <strong>
                              <%=Utilities.getSourceHabitat(database, NamesDomain.SEARCH_ANNEX_I.intValue(), NamesDomain.SEARCH_BOTH.intValue())%>
                            </strong>
                            <%=cm.cmsPhrase("habitat types for which")%>
                            <strong>
                              <%=mainCriteria.toHumanString()%>
                            </strong>
                            <%
                              }
                            }
                            %>
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
                        <jsp:param name="guid" value="<%=guid + 1%>" />
                        <jsp:param name="pageName" value="<%=pageName%>" />
                        <jsp:param name="pageSize" value="<%=formBean.getPageSize()%>" />
                        <jsp:param name="toFORMParam" value="<%=formBean.toFORMParam(pageSizeFormFields)%>" />
                      </jsp:include>
                      <br />
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
                            <form name="resultSearch" method="post" onsubmit="return(checkHabitats(<%=noCriteria%>));" action="habitats-names-result.jsp">
                            <input type="hidden" name="noSoundex" value="true" />
                              <%=formBean.toFORMParam(filterSearch)%>
                              <label for="criteriaType" class="noshow"><%=cm.cmsPhrase("Criteria")%></label>
                              <select name="criteriaType" id="criteriaType" title="<%=cm.cmsPhrase("Criteria")%>">
                                <%if (showCode && 0 == database.compareTo(NamesDomain.SEARCH_EUNIS)) {%>
                                <option value="<%=NameSearchCriteria.CRITERIA_CODE_EUNIS%>"><%=cm.cms("eunis_code")%></option>
                                <%}%>
                                <%if (showCode && 0 == database.compareTo(NamesDomain.SEARCH_ANNEX_I)) {%>
                                <option value="<%=NameSearchCriteria.CRITERIA_CODE_ANNEX%>"><%=cm.cms("annex_code")%></option>
                                <%}%>
                                <%if (showCode && 0 == database.compareTo(NamesDomain.SEARCH_BOTH)) {%>
                                <option value="<%=NameSearchCriteria.CRITERIA_CODE_EUNIS%>"><%=cm.cms("eunis_code")%></option>
                                <option value="<%=NameSearchCriteria.CRITERIA_CODE_ANNEX%>"><%=cm.cms("annex_code")%></option>
                                <%}%>
                                <%if (showLevel && 0 == database.compareTo(NamesDomain.SEARCH_EUNIS)) {%>
                                <option value="<%=NameSearchCriteria.CRITERIA_LEVEL%>"><%=cm.cms("generic_index_07")%></option><%}%>
                                <%if (showVernacularName) {%>
                                <option value="<%=NameSearchCriteria.CRITERIA_NAME%>"><%=cm.cms("habitat_type_english_name")%></option><%}%>
                                <%if (showScientificName) {%>
                                <option value="<%=NameSearchCriteria.CRITERIA_SCIENTIFIC_NAME%>" selected="selected"><%=cm.cms("habitat_type_name")%></option><%}%>
                              </select>
                              <%=cm.cmsInput("eunis_code")%>
                              <%=cm.cmsInput("annex_code")%>
                              <%=cm.cmsInput("generic_index_07")%>
                              <%=cm.cmsInput("habitat_type_english_name")%>
                              <%=cm.cmsInput("habitat_type_name")%>
                              <select title="<%=cm.cmsPhrase("Operator")%>" name="oper" id="oper">
                                <option value="<%=Utilities.OPERATOR_IS%>" selected="selected"><%=cm.cmsPhrase("is")%></option>
                                <option value="<%=Utilities.OPERATOR_STARTS%>"><%=cm.cmsPhrase("starts with")%></option>
                                <option value="<%=Utilities.OPERATOR_CONTAINS%>"><%=cm.cmsPhrase("contains")%></option>
                              </select>
                              <label for="criteriaSearch" class="noshow"><%=cm.cmsPhrase("Filter value")%></label>
                              <input alt="<%=cm.cmsPhrase("Filter value")%>" title="<%=cm.cmsPhrase("Filter value")%>" name="criteriaSearch" id="criteriaSearch" type="text" size="30" />
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
                            <a title="<%=cm.cms("delete_filter")%>" href="<%= pageName%>?<%=formBean.toURLParam(filterSearch)%>&amp;removeFilterIndex=<%=i%>">
                              <img alt="<%=cm.cms("delete_filter")%>" title="<%=cm.cms("delete_filter")%>" src="images/mini/delete.jpg" border="0" style="vertical-align:middle" />
                            </a>
                            <%=cm.cms("delete_filter")%>&nbsp;&nbsp;
                            <strong class="linkDarkBg"><%= i + ". " + criteria.toHumanString()%></strong>
                          </td>
                        </tr>
                        <%
                            }
                          }
                        %>
                        <% // Prepare parameters for navigator.jsp
                          Vector navigatorFormFields = new Vector();  /*  The following fields are used by paginator.jsp, included below.      */
                          navigatorFormFields.addElement("pageSize"); /* NOTE* that I didn't add here currentPage since it is overriden in the */
                          navigatorFormFields.addElement("sort");     /* <form name='..."> in the navigator.jsp!                               */
                          navigatorFormFields.addElement("ascendency");
                          navigatorFormFields.addElement("criteriaSearch");
                          navigatorFormFields.addElement("oper");
                          navigatorFormFields.addElement("criteriaType");
                          navigatorFormFields.addElement("expand");
                        %>
                        <tr>
                          <td>
                            <br />
                            <br />
                          </td>
                        </tr>
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
                        <tr>
                          <td>
                            <%// Compute the sort criteria
                              Vector sortURLFields = new Vector();      /* Used for sorting */
                              sortURLFields.addElement("pageSize");
                              sortURLFields.addElement("criteriaSearch");
                              sortURLFields.addElement("oper");
                              sortURLFields.addElement("criteriaType");
                              sortURLFields.addElement("currentPage");
                              sortURLFields.addElement("expand");
                              String urlSortString = formBean.toURLParam(sortURLFields);
                              AbstractSortCriteria levelCrit = formBean.lookupSortCriteria(NameSortCriteria.SORT_LEVEL);
                              AbstractSortCriteria eunisCodeCrit = formBean.lookupSortCriteria(NameSortCriteria.SORT_EUNIS_CODE);
                              AbstractSortCriteria annexCodeCrit = formBean.lookupSortCriteria(NameSortCriteria.SORT_ANNEX_CODE);
                              AbstractSortCriteria sciNameCrit = formBean.lookupSortCriteria(NameSortCriteria.SORT_SCIENTIFIC_NAME);
                              AbstractSortCriteria nameCrit = formBean.lookupSortCriteria(NameSortCriteria.SORT_VERNACULAR_NAME);
                            %>
                            <table class="sortable listing" width="100%" summary="<%=cm.cmsPhrase("Search results")%>">
                              <thead>
                                <tr>
                            <%
                              if (showLevel && 0 == database.compareTo(NamesDomain.SEARCH_EUNIS))
                              {
                            %>
                                  <th class="nosort" scope="col">
                                    <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_LEVEL%>&amp;ascendency=<%=formBean.changeAscendency(levelCrit, (null == levelCrit))%>"><%=Utilities.getSortImageTag(levelCrit)%><%=cm.cmsPhrase("Level")%></a>
                                  </th>
                            <%
                              }
                            %>
                            <%
                              if (showCode)
                              {
                            %>
                            <%
                                if (0 == database.compareTo(NamesDomain.SEARCH_BOTH))
                                {
                            %>
                                  <th class="nosort" scope="col">
                                    <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit))%>">
                                      <%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsPhrase("EUNIS Code")%>
                                    </a>
                                  </th>
                                  <th class="nosort" scope="col">
                                    <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit))%>">
                                      <%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsPhrase("ANNEX I Code")%>
                                    </a>
                                  </th>
                              <%
                                }
                              %>
                            <%
                                if (0 == database.compareTo(NamesDomain.SEARCH_EUNIS))
                                {
                            %>
                                  <th class="nosort" scope="col">
                                    <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit))%>">
                                      <%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsPhrase("EUNIS Code")%>
                                    </a>
                                  </th>
                            <%
                                }
                            %>
                            <%
                                if (0 == database.compareTo(NamesDomain.SEARCH_ANNEX_I))
                                {
                            %>
                                  <th class="nosort" scope="col">
                                    <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit))%>">
                                      <%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsPhrase("ANNEX I Code")%>
                                    </a>
                                  </th>
                            <%
                                }
                            %>
                            <%
                              }
                            %>
                            <%
                              if (showScientificName)
                              {
                            %>
                                  <th class="nosort" scope="col">
                                    <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_SCIENTIFIC_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sciNameCrit, (null == sciNameCrit))%>">
                                      <%=Utilities.getSortImageTag(sciNameCrit)%><%=cm.cmsPhrase("Habitat type name")%>
                                    </a>
                                  </th>
                            <%
                              }
                            %>
                            <%
                              if (showVernacularName)
                              {
                            %>
                                  <th class="nosort" scope="col">
                                    <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_VERNACULAR_NAME%>&amp;ascendency=<%=formBean.changeAscendency(nameCrit, (null == nameCrit))%>">
                                      <%=Utilities.getSortImageTag(nameCrit)%><%=cm.cmsPhrase("Habitat type english name")%>
                                    </a>
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
                                NamesPersist habitat = (NamesPersist) it.next();
                                int level = habitat.getHabLevel().intValue();
                                boolean isEUNIS = Utilities.isHabitatEunis(habitat.getHabitatType());
                                String eunisCode = habitat.getEunisHabitatCode();
                                String annexCode = habitat.getCode2000();
                                if (isEUNIS) {
                                  annexCode = "";
                                } else {
                                  eunisCode = "";
                                }
                            %>
                                <tr>
                            <%
                                  if (showLevel && 0 == database.compareTo(NamesDomain.SEARCH_EUNIS))
                                  {

                            %>
                                  <td style="white-space : nowrap;">
                                    <%for (int iter = 0; iter < level; iter++) {%>
                                    <img alt="" src="images/mini/lev_blank.gif" /><%}%><%=level%>
                                  </td>
                            <%
                                  }
                            %>
                            <%
                                if (showCode)
                                {
                            %>
                            <%
                                  if (0 == database.compareTo(NamesDomain.SEARCH_BOTH))
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
                                  if (0 == database.compareTo(NamesDomain.SEARCH_EUNIS))
                                  {
                            %>
                                  <td>
                                    <%=eunisCode%>
                                  </td>
                            <%
                                  }
                            %>
                            <%
                                  if (0 == database.compareTo(NamesDomain.SEARCH_ANNEX_I))
                                  {
                            %>
                                  <td>
                                    <%=annexCode%>
                                  </td>
                            <%
                                  }
                            %>
                            <%
                                }
                            %>
                            <%
                                if (showScientificName)
                                {
                            %>
                                  <td>
                                    <a href="habitats/<%=habitat.getIdHabitat()%>"><%=JstlFunctions.bracketsToItalics(habitat.getScientificName())%></a>
                                  </td>
                            <%
                                }
                            %>
                            <%
                                if (showVernacularName)
                                {
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
                              if (showLevel && 0 == database.compareTo(NamesDomain.SEARCH_EUNIS))
                              {
                            %>
                                  <th class="nosort" scope="col">
                                    <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_LEVEL%>&amp;ascendency=<%=formBean.changeAscendency(levelCrit, (null == levelCrit))%>"><%=Utilities.getSortImageTag(levelCrit)%><%=cm.cmsPhrase("Level")%></a>
                                  </th>
                            <%
                              }
                            %>
                            <%
                              if (showCode)
                              {
                            %>
                            <%
                                if (0 == database.compareTo(NamesDomain.SEARCH_BOTH))
                                {
                            %>
                                  <th class="nosort" scope="col">
                                    <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit))%>">
                                      <%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsPhrase("EUNIS Code")%>
                                    </a>
                                  </th>
                                  <th class="nosort" scope="col">
                                    <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit))%>">
                                      <%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsPhrase("ANNEX I Code")%>
                                    </a>
                                  </th>
                            <%
                                }
                            %>
                            <%
                                if (0 == database.compareTo(NamesDomain.SEARCH_EUNIS))
                                {
                            %>
                                  <th class="nosort" scope="col">
                                    <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_EUNIS_CODE%>&amp;ascendency=<%=formBean.changeAscendency(eunisCodeCrit, (null == eunisCodeCrit))%>">
                                      <%=Utilities.getSortImageTag(eunisCodeCrit)%><%=cm.cmsPhrase("EUNIS Code")%>
                                    </a>
                                  </th>
                            <%
                                }
                            %>
                            <%
                                if (0 == database.compareTo(NamesDomain.SEARCH_ANNEX_I))
                                {
                            %>
                                  <th class="nosort" scope="col">
                                    <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_ANNEX_CODE%>&amp;ascendency=<%=formBean.changeAscendency(annexCodeCrit, (null == annexCodeCrit))%>">
                                      <%=Utilities.getSortImageTag(annexCodeCrit)%><%=cm.cmsPhrase("ANNEX I Code")%>
                                    </a>
                                  </th>
                            <%
                                }
                            %>
                            <%
                              }
                            %>
                            <%
                              if (showScientificName)
                              {
                            %>
                                  <th class="nosort" scope="col">
                                    <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_SCIENTIFIC_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sciNameCrit, (null == sciNameCrit))%>">
                                      <%=Utilities.getSortImageTag(sciNameCrit)%><%=cm.cmsPhrase("Habitat type name")%>
                                    </a>
                                  </th>
                            <%
                              }
                            %>
                            <%
                              if (showVernacularName)
                              {
                            %>
                                  <th class="nosort" scope="col">
                                    <a title="<%=cm.cmsPhrase("Sort results on this column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_VERNACULAR_NAME%>&amp;ascendency=<%=formBean.changeAscendency(nameCrit, (null == nameCrit))%>">
                                      <%=Utilities.getSortImageTag(nameCrit)%><%=cm.cmsPhrase("Habitat type english name")%>
                                    </a>
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
                    </td>
                  </tr>
                </table>
                <%=cm.br()%>
                <%=cm.cmsMsg("habitats_names-result_title")%>
                <%=cm.br()%>
<!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>