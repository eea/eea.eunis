<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Pick species, show references' function - results page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="java.util.*,
                 ro.finsiel.eunis.WebContentManagement,
                 ro.finsiel.eunis.search.species.references.ReferencesSearchCriteria,
                 ro.finsiel.eunis.search.species.references.ReferencesPaginator,
                 ro.finsiel.eunis.search.species.references.ReferencesSortCriteria,
                 ro.finsiel.eunis.jrfTables.species.references.*,
                 ro.finsiel.eunis.search.*" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<jsp:useBean id="formBean" class="ro.finsiel.eunis.search.species.references.ReferencesBean" scope="request">
    <jsp:setProperty name="formBean" property="*"/>
</jsp:useBean>
<%
  // Prepare the search in results (fix)
  if (null != formBean.getRemoveFilterIndex()) {
      formBean.prepareFilterCriterias();
  }
  // Initialization
  int currentPage = Utilities.checkedStringToInt(formBean.getCurrentPage(), 0);
  ReferencesPaginator paginator = new ReferencesPaginator(new SpeciesBooksDomain(formBean.toSearchCriteria(), SessionManager.getShowEUNISInvalidatedSpecies()));

  paginator.setSortCriteria(formBean.toSortCriteria());
  paginator.setPageSize(Utilities.checkedStringToInt(formBean.getPageSize(), AbstractPaginator.DEFAULT_PAGE_SIZE));
  currentPage = paginator.setCurrentPage(currentPage);// Compute *REAL* current page (adjusted if user messes up)
  final String pageName = "species-books-result.jsp";
  int resultsCount = 0;
  try {
      resultsCount = paginator.countResults();
  } catch (Exception e) {

  }
  int pagesCount = 0;
  try {
      pagesCount = paginator.countPages();// This is used in @page include...
  } catch (Exception e) {

  }
  int guid = 0;// This is used in @page include...
  // Now extract the results for the current page.
  List results = new ArrayList();
  try {
      results = paginator.getPage(currentPage);
  } catch (Exception e) {

  }
  // Set number criteria for the search result
  int noCriteria = (null == formBean.getCriteriaSearch() ? 0 : formBean.getCriteriaSearch().length);
  // Prepare parameters for tsv
  Vector reportFields = new Vector();
  reportFields.addElement("sort");
  reportFields.addElement("ascendency");
  reportFields.addElement("criteriaSearch");
  reportFields.addElement("oper");
  reportFields.addElement("criteriaType");
  String tsvLink = "javascript:openTSVDownload('reports/species/tsv-species-books.jsp?" + formBean.toURLParam(reportFields) + "')";
  WebContentManagement cm = SessionManager.getWebContent();
  String eeaHome = application.getInitParameter( "EEA_HOME" );
  String location = "eea#" + eeaHome + ",home#index.jsp,species#species.jsp,pick_species_show_references_location#species-books.jsp,results";
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
<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cms("species_books-result_title") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" helpLink="species-help.jsp" pageTitle="${title}" downloadLink="<%= tsvLink%>" btrail="<%= location%>">
    <stripes:layout-component name="head">
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/eea_search.css">
        <script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/script/species-result.js"></script>
    </stripes:layout-component>
    <stripes:layout-component name="contents">
                <a name="documentContent"></a>
                <h1>
                    <%=cm.cmsPhrase("Pick species, show references")%>
                </h1>

<!-- MAIN CONTENT -->
                <table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                <td colspan="3">
                    <table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
                        <%
                            // Set search description
                            StringBuffer descr = Utilities.prepareHumanString("scientific name", formBean.getScientificName(), Utilities.checkedStringToInt(formBean.getRelationOp(), Utilities.OPERATOR_CONTAINS));
                        %>
                        <tr>
                            <td>
                                <%=cm.cmsPhrase("You searched references for species with")%> <strong><%=Utilities.treatURLAmp(descr.toString())%></strong>
                            </td>
                        </tr>
                    </table>
                <%=cm.cmsPhrase("Results found")%>:
                <strong><%=resultsCount%></strong>

                <%// Prepare parameters for pagesize.jsp
                    Vector pageSizeFormFields = new Vector();       /*  These fields are used by pagesize.jsp, included below.    */
                    pageSizeFormFields.addElement("sort");          /*  *NOTE* I didn't add currentPage & pageSize since pageSize */
                    pageSizeFormFields.addElement("ascendency");    /*   is overriden & also pageSize is set to default           */
                    pageSizeFormFields.addElement("criteriaSearch");/*   to page '0' aka first page. */
                %>

                <jsp:include page="pagesize.jsp">
                    <jsp:param name="guid" value="<%=guid%>"/>
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
                <table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td style="background-color:#EEEEEE">
                            <strong>
                                <%=cm.cmsPhrase("Refine your search")%>
                            </strong>
                        </td>
                    </tr>
                    <tr>
                        <td style="background-color:#EEEEEE">
                            <form name="refineSearch" method="get" onsubmit="return(validateRefineForm(<%=noCriteria%>));" action="">
                                <%=formBean.toFORMParam(filterSearch)%>
                                <label for="select1" class="noshow"><%=cm.cmsPhrase("Criteria")%></label>
                                <select id="select1" title="<%=cm.cmsPhrase("Criteria")%>" name="criteriaType">
                                    <option value="<%=ReferencesSearchCriteria.CRITERIA_AUTHOR%>">
                                        <%=cm.cms("author")%>
                                    </option>
                                    <option value="<%=ReferencesSearchCriteria.CRITERIA_DATE%>">
                                        <%=cm.cms("date")%>
                                    </option>
                                    <option value="<%=ReferencesSearchCriteria.CRITERIA_TITLE%>" selected="selected">
                                        <%=cm.cms("title")%>
                                    </option>
                                    <option value="<%=ReferencesSearchCriteria.CRITERIA_EDITOR%>">
                                        <%=cm.cms("editor")%>
                                    </option>
                                    <option value="<%=ReferencesSearchCriteria.CRITERIA_PUBLISHER%>">
                                        <%=cm.cms("publisher")%>
                                    </option>
                                </select>
                                <select id="select2" title="<%=cm.cms("species_books-result_17_Title")%>" name="oper">
                                    <option value="<%=Utilities.OPERATOR_IS%>" selected="selected">
                                        <%=cm.cmsPhrase("is")%>
                                    </option>
                                    <option value="<%=Utilities.OPERATOR_STARTS%>">
                                        <%=cm.cmsPhrase("starts with")%>
                                    </option>
                                    <option value="<%=Utilities.OPERATOR_CONTAINS%>">
                                        <%=cm.cmsPhrase("contains")%>
                                    </option>
                                </select>
                                <%=cm.cmsTitle("species_books-result_17_Title")%>
                                <label for="criteriaSearch" class="noshow">
                                 <%=cm.cmsPhrase("Filter value")%>
                                </label>
                                <input id="criteriaSearch" title="<%=cm.cmsPhrase("Filter value")%>" alt="<%=cm.cmsPhrase("Filter value")%>" name="criteriaSearch" type="text" size="30" />
                                <input id="button1" class="standardButton" type="submit" name="Submit" title="<%=cm.cmsPhrase("Search")%>"
                                       value="<%=cm.cmsPhrase("Search")%>" />
                            </form>
                        </td>
                    </tr>
                    <%-- This is the code which shows the search filters --%>
                    <%
                        AbstractSearchCriteria[] criterias = formBean.toSearchCriteria();
                        if (criterias.length > 1)
                        {
                    %>
                    <tr>
                        <td style="background-color:#EEEEEE"><%=cm.cmsPhrase("Applied filters to the results:")%>:</td>
                    </tr>
                    <%
                        }
                        for (int i = criterias.length - 1; i > 0; i--)
                        {
                            AbstractSearchCriteria criteria = criterias[i];
                            if (null != criteria && null != formBean.getCriteriaSearch())
                            {
                    %>
                    <tr>
                        <td style="background-color:#CCCCCC;text-align:left">
                            <a title="<%=cm.cms("delete_a_criteria")%>"
                               href="<%= pageName%>?<%=formBean.toURLParam(filterSearch)%>&amp;removeFilterIndex=<%=i%>"><img
                                    alt="<%=cm.cms("delete_a_criteria")%>" src="images/mini/delete.jpg" border="0" style="vertical-align:middle"/></a>&nbsp;&nbsp;
                            <%=cm.cmsTitle("delete_a_criteria")%>
                            <%=cm.cmsAlt("delete_a_criteria")%>
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
                    sortURLFields.addElement("criteriaSearch");
                    String urlSortString = formBean.toURLParam(sortURLFields);
                    AbstractSortCriteria sortAuthor = formBean.lookupSortCriteria(ReferencesSortCriteria.SORT_AUTHOR);
                    AbstractSortCriteria sortDate = formBean.lookupSortCriteria(ReferencesSortCriteria.SORT_DATE);
                    AbstractSortCriteria sortTitle = formBean.lookupSortCriteria(ReferencesSortCriteria.SORT_TITLE);
                    AbstractSortCriteria sortEditor = formBean.lookupSortCriteria(ReferencesSortCriteria.SORT_EDITOR);
                    AbstractSortCriteria sortPublisher = formBean.lookupSortCriteria(ReferencesSortCriteria.SORT_PUBLISHER);
                    Vector speciesURLFields = new Vector();
                    speciesURLFields.addElement("criteriaSearch");
                    String search = formBean.toURLParam(speciesURLFields);
                %>
                <table class="sortable listing" width="100%" summary="<%=cm.cmsPhrase("Search results")%>">
                  <thead>
                    <tr>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cms("sort_by_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_AUTHOR%>&amp;ascendency=<%=formBean.changeAscendency(sortAuthor, (null == sortAuthor))%>"><%=Utilities.getSortImageTag(sortAuthor)%><%=cm.cmsPhrase("Author")%></a>
                        <%=cm.cmsTitle("sort_by_column")%>
                      </th>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cms("sort_by_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_DATE%>&amp;ascendency=<%=formBean.changeAscendency(sortDate, (null == sortDate))%>"><%=Utilities.getSortImageTag(sortDate)%><%=cm.cmsPhrase("Date")%></a>
                        <%=cm.cmsTitle("sort_by_column")%>
                      </th>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cms("sort_by_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_TITLE%>&amp;ascendency=<%=formBean.changeAscendency(sortTitle, (null == sortTitle))%>"><%=Utilities.getSortImageTag(sortTitle)%><%=cm.cmsPhrase("Title")%></a>
                        <%=cm.cmsTitle("sort_by_column")%>
                      </th>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cms("sort_by_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_EDITOR%>&amp;ascendency=<%=formBean.changeAscendency(sortEditor, (null == sortEditor))%>"><%=Utilities.getSortImageTag(sortEditor)%><%=cm.cmsPhrase("Editor")%></a>
                        <%=cm.cmsTitle("sort_by_column")%>
                      </th>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cms("sort_by_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_PUBLISHER%>&amp;ascendency=<%=formBean.changeAscendency(sortPublisher, (null == sortPublisher))%>"><%=Utilities.getSortImageTag(sortPublisher)%><%=cm.cmsPhrase("Publisher")%></a>
                        <%=cm.cmsTitle("sort_by_column")%>
                      </th>
                      <th class="nosort" scope="col">
                        <%=cm.cmsPhrase("Species name(s)")%>
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                <%
                  Iterator it = results.iterator();
                  int col = 0;
                  while (it.hasNext())
                  {
                    String bgColor = col++ % 2 == 0 ? "#EEEEEE" : "#FFFFFF";
                    SpeciesBooksPersist book = (SpeciesBooksPersist) it.next();
                    // Sort this common names in alphabetical order
                    String author = (book.getName() == null ? "" : book.getName());
                    int dc_id = book.getId();
                    String url = book.getUrl().replaceAll("#", "");
                %>
                    <tr>
                      <td>
                        <a target="_blank" href="references/<%=dc_id%>"><%=Utilities.treatURLSpecialCharacters(author)%></a>
                      </td>
                      <td>
                        <%=Utilities.formatString(Utilities.formatString(book.getDate()), "&nbsp;")%>
                      </td>
                      <td>
                        <%=Utilities.formatString(Utilities.treatURLSpecialCharacters(book.getTitle()), "&nbsp;")%>
                      </td>
                      <td>
                        <%=Utilities.formatString(Utilities.treatURLSpecialCharacters(book.getEditor()), "&nbsp;")%>
                      </td>
                      <td>
                        <%=Utilities.formatString(Utilities.treatURLSpecialCharacters(book.getPublisher()), "&nbsp;")%>
                      </td>
                      <td>
                        <%
                            SpeciesBooksDomain domain = new SpeciesBooksDomain(formBean.toSearchCriteria(), SessionManager.getShowEUNISInvalidatedSpecies());
                            // List of species attributes related to this reference
                            List resultsSpecies = new ArrayList();
                            try
                            {
                                resultsSpecies = domain.getSpeciesForAReference(book.getId().toString());
                            } catch(Exception e) { }

                            if (resultsSpecies != null && resultsSpecies.size() > 0)
                            {
                        %>
                        <%--<ul>--%>
                            <%
                                for (int ii = 0; ii < resultsSpecies.size(); ii++)
                                {
                                    SpeciesBooksPersist specie = (SpeciesBooksPersist) resultsSpecies.get(ii);
                                    String scientificName = Utilities.treatURLSpecialCharacters(specie.getName());
                                    Integer idSpecies = specie.getId();
                            %>
                            <p>
                              <a href="species/<%=idSpecies%>" title="<%=cm.cms("species_books-result_23_Title")%>"><%=scientificName%></a>
                              <%=cm.cmsTitle("species_books-result_23_Title")%>
                            </p>
                            <%
                                }
                            %>
                        <%--</ul>--%>
                        <%
                            }
                        %>
                      </td>
                    </tr>
                <%
                    }
                %>
                  </tbody>
                  <thead>
                    <tr>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cms("sort_by_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_AUTHOR%>&amp;ascendency=<%=formBean.changeAscendency(sortAuthor, (null == sortAuthor))%>"><%=Utilities.getSortImageTag(sortAuthor)%><%=cm.cmsPhrase("Author")%></a>
                        <%=cm.cmsTitle("sort_by_column")%>
                      </th>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cms("sort_by_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_DATE%>&amp;ascendency=<%=formBean.changeAscendency(sortDate, (null == sortDate))%>"><%=Utilities.getSortImageTag(sortDate)%><%=cm.cmsPhrase("Date")%></a>
                        <%=cm.cmsTitle("sort_by_column")%>
                      </th>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cms("sort_by_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_TITLE%>&amp;ascendency=<%=formBean.changeAscendency(sortTitle, (null == sortTitle))%>"><%=Utilities.getSortImageTag(sortTitle)%><%=cm.cmsPhrase("Title")%></a>
                        <%=cm.cmsTitle("sort_by_column")%>
                      </th>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cms("sort_by_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_EDITOR%>&amp;ascendency=<%=formBean.changeAscendency(sortEditor, (null == sortEditor))%>"><%=Utilities.getSortImageTag(sortEditor)%><%=cm.cmsPhrase("Editor")%></a>
                        <%=cm.cmsTitle("sort_by_column")%>
                      </th>
                      <th class="nosort" scope="col">
                        <a title="<%=cm.cms("sort_by_column")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=ReferencesSortCriteria.SORT_PUBLISHER%>&amp;ascendency=<%=formBean.changeAscendency(sortPublisher, (null == sortPublisher))%>"><%=Utilities.getSortImageTag(sortPublisher)%><%=cm.cmsPhrase("Publisher")%></a>
                        <%=cm.cmsTitle("sort_by_column")%>
                      </th>
                      <th class="nosort" scope="col">
                        <%=cm.cmsPhrase("Species name(s)")%>
                      </th>
                    </tr>
                  </thead>
                </table>
                <jsp:include page="navigator.jsp">
                    <jsp:param name="pagesCount" value="<%=pagesCount%>"/>
                    <jsp:param name="pageName" value="<%=pageName%>"/>
                    <jsp:param name="guid" value="<%=guid + 1%>"/>
                    <jsp:param name="currentPage" value="<%=formBean.getCurrentPage()%>"/>
                    <jsp:param name="toURLParam" value="<%=formBean.toURLParam(navigatorFormFields)%>"/>
                    <jsp:param name="toFORMParam" value="<%=formBean.toFORMParam(navigatorFormFields)%>"/>
                </jsp:include>
                </td></tr>
                </table>

                <%=cm.br()%>
                <%=cm.cmsMsg("species_books-result_title")%>
                <%=cm.br()%>
                <%=cm.cmsMsg("author")%>
                <%=cm.br()%>
                <%=cm.cmsMsg("date")%>
                <%=cm.br()%>
                <%=cm.cmsMsg("title")%>
                <%=cm.br()%>
                <%=cm.cmsMsg("editor")%>
                <%=cm.br()%>
                <%=cm.cmsMsg("publisher")%>
                <%=cm.br()%>
                <%=cm.cmsMsg("species_books-result_20_Sum")%>
                <%=cm.br()%>
                <%=cm.cmsMsg("list_species")%>
                <%=cm.br()%>
<!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>