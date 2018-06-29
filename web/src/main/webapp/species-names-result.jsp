<%--
  - Author(s)   : The EUNIS Database Team.
  - Date        :
  - Copyright   : (c) 2002-2005 EEA - European Environment Agency.
  - Description : 'Species names' function - results page.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="java.util.*,
                 ro.finsiel.eunis.search.species.names.NameSearchCriteria,
                 ro.finsiel.eunis.search.species.names.NamePaginator,
                 ro.finsiel.eunis.search.species.names.NameSortCriteria,
                 ro.finsiel.eunis.jrfTables.species.names.*,
                 ro.finsiel.eunis.search.species.VernacularNameWrapper,
                 ro.finsiel.eunis.search.species.SpeciesSearchUtility,
                 ro.finsiel.eunis.search.*,
                 ro.finsiel.eunis.WebContentManagement,
                 ro.finsiel.eunis.search.species.names.NameBean,
                 ro.finsiel.eunis.jrfTables.Chm62edtSoundexPersist,
                 ro.finsiel.eunis.jrfTables.Chm62edtSoundexDomain"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<jsp:useBean id="formBean" class="ro.finsiel.eunis.search.species.names.NameBean" scope="page">
  <jsp:setProperty name="formBean" property="*" />
</jsp:useBean>
<%
    // #18874: search all the genus when searching "spp."
    if(formBean.getScientificName()!= null && formBean.getScientificName().contains("spp.")){
        formBean.setScientificName(formBean.getScientificName().replace("spp.",""));
        formBean.setRelationOp(""+Utilities.OPERATOR_STARTS);
    }
    if(formBean.getScientificName()!= null && formBean.getScientificName().endsWith(" spp")){
        formBean.setScientificName(formBean.getScientificName().replace(" spp"," "));
        formBean.setRelationOp(""+Utilities.OPERATOR_STARTS);
    }

  	if (null != formBean.getRemoveFilterIndex()) { formBean.prepareFilterCriterias(); }
  	// Check columns to be displayed
  	boolean showGroup = Utilities.checkedStringToBoolean(formBean.getShowGroup(), NameBean.HIDE);
  	boolean showOrder = Utilities.checkedStringToBoolean(formBean.getShowOrder(), NameBean.HIDE);
  	boolean showFamily = Utilities.checkedStringToBoolean(formBean.getShowFamily(), NameBean.HIDE);
  	boolean showScientificName = Utilities.checkedStringToBoolean(formBean.getShowScientificName(), NameBean.HIDE);
  	boolean showValidName = Utilities.checkedStringToBoolean(formBean.getShowValidName(), NameBean.HIDE);
  	boolean showVernacularNames = Utilities.checkedStringToBoolean(formBean.getShowVernacularNames(), NameBean.HIDE);
  	boolean searchSynonyms = Utilities.checkedStringToBoolean( formBean.getSearchSynonyms(), false );
  	boolean newName = Utilities.checkedStringToBoolean( formBean.getNewName(), false );
  	boolean searchVernacular = formBean.getSearchVernacular();
  	boolean noSoundex = Utilities.checkedStringToBoolean( formBean.getNoSoundex(), false );
  	// Initialization
  	String languageReq = formBean.getLanguage();
  	boolean isAnyLanguage = null != languageReq && languageReq.equalsIgnoreCase("any");
  	int currentPage = Utilities.checkedStringToInt(formBean.getCurrentPage(), 0);
  	int typeForm = Utilities.checkedStringToInt(formBean.getTypeForm(), NameSearchCriteria.CRITERIA_SCIENTIFIC.intValue());
  	NamePaginator paginator = null;
  	// Coming from form 1
  	if (NameSearchCriteria.CRITERIA_SCIENTIFIC == typeForm){
      	String cqs = "" + formBean.getComeFromQuickSearch();
      	boolean showInvalidatedSpecies = SessionManager.getShowEUNISInvalidatedSpecies();
      	if(newName){
      		paginator = new NamePaginator(new SimilarNameDomain(formBean.toSearchCriteria(),
                    formBean.toSortCriteria(),
                    searchSynonyms,
                    showInvalidatedSpecies,
                    formBean.getSearchVernacular()));
      	} else {
      		paginator = new NamePaginator(new ScientificNameDomain(formBean.toSearchCriteria(),
                                                           formBean.toSortCriteria(),
                                                           searchSynonyms,
                                                           showInvalidatedSpecies,
                                                           formBean.getSearchVernacular()));
      	}
  	}
  	// Coming from form 2
  	if (NameSearchCriteria.CRITERIA_VERNACULAR == typeForm){
    	if (isAnyLanguage){
      		paginator = new NamePaginator(new VernacularNameAnyDomain(formBean.toSearchCriteria(),
                                                                formBean.toSortCriteria(),
                                                                SessionManager.getShowEUNISInvalidatedSpecies()));
    	} else {
      		paginator = new NamePaginator(new VernacularNameDomain(formBean.toSearchCriteria(),
                                                             formBean.toSortCriteria(),
                                                             SessionManager.getShowEUNISInvalidatedSpecies()));
    	}
  	}
  	paginator.setSortCriteria(formBean.toSortCriteria());
  	paginator.setPageSize(Utilities.checkedStringToInt(formBean.getPageSize(), AbstractPaginator.DEFAULT_PAGE_SIZE));
  	final String pageName = "species-names-result.jsp";
  	currentPage = paginator.setCurrentPage(currentPage);// Compute *REAL* current page (adjusted if user messes up)
  	int resultsCount = paginator.countResults();
  	int pagesCount = paginator.countPages();// This is used in included page...
  	int guid = 0;// This is used in included page...
  	// Now extract the results for the current page.
  	List results = paginator.getPage(currentPage);

  	// Prepare parameters for tsvlink
  	Vector reportFields = new Vector();
  	reportFields.addElement("sort");
  	reportFields.addElement("ascendency");
  	reportFields.addElement("criteriaSearch");
  	reportFields.addElement("oper");
  	reportFields.addElement("criteriaType");
  	reportFields.addElement("useVernacular");
  	if(newName)
  		reportFields.addElement("newName");
	
  	// Set number criteria for the search result
  	int noCriteria = (null == formBean.getCriteriaSearch() ? 0 : formBean.getCriteriaSearch().length);

	String tsvLink = "javascript:openTSVDownload('reports/species/tsv-species-names.jsp?" + formBean.toURLParam(reportFields) + "')";
	if( results.isEmpty() && !newName){
		String sname;
    	if(NameSearchCriteria.CRITERIA_SCIENTIFIC == typeForm){
       		sname = formBean.getScientificName();
    	} else {
	      	sname = formBean.getVernacularName();
    	}
    	
    	try {
    		String URL = "species-names-result.jsp?typeForm=0&showScientificName=true&showGroup=true&showOrder=true&showFamily=true&showVernacularNames=true&showValidName=true&relationOp=4&scientificName="+sname+"&searchSynonyms=true&newName=true";
    		URL += "&searchVernacular=" + searchVernacular;
    		response.sendRedirect(URL);
    		return;
  		} catch(Exception e) {
    		e.printStackTrace();
  		}
  	}
  	WebContentManagement cm = SessionManager.getWebContent();
  	String eeaHome = application.getInitParameter( "EEA_HOME" );
  	String location = "eea#" + eeaHome + ",home#index.jsp,species#species.jsp,species_names_location#species-names.jsp,results";
  	if (results.isEmpty()){
    	boolean fromRefine = formBean.getCriteriaSearch() != null && formBean.getCriteriaSearch().length > 0;
%>
      	<jsp:forward page="emptyresults.jsp">
	        <jsp:param name="location" value="<%=location%>" />
        	<jsp:param name="fromRefine" value="<%=fromRefine%>" />
      	</jsp:forward>
<%
  	}
%>

<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cms("species_names-result_pageTitle") %>'/>

<stripes:layout-render name="/stripes/common/template.jsp" helpLink="species-help.jsp" pageTitle="${title}" downloadLink="<%= tsvLink%>" btrail="<%= location%>">
    <stripes:layout-component name="head">
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/eea_search.css">
    	<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/script/species-result.js"></script>

    </stripes:layout-component>
    <stripes:layout-component name="contents">

                				<a name="documentContent"></a>
								<!-- MAIN CONTENT -->
                				<h1>
                   					<%
                      				if(newName) {
                      				%>
                        				<%=cm.cmsPhrase("Search by name results")%>
                      				<%
                      				} else {
                        				if (NameSearchCriteria.CRITERIA_SCIENTIFIC == typeForm) {
                       				%>
                             				<%=cm.cmsPhrase("Search by name results")%>
                       				<%
                          				}
                          				// Coming from form 2
                          				if (NameSearchCriteria.CRITERIA_VERNACULAR == typeForm) {
                        			%>
                             				<%=cm.cmsPhrase("Common names")%>
                        			<%
                          				}
                      				}
                      				%>
                				</h1>
                				<table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
                  					<tr>
                    					<td>
                      						<table width="100%" summary="layout" border="0" cellspacing="0" cellpadding="0">
                        						<tr>
                          							<td>
            											<%
              											if (!results.isEmpty()){
                      										if(newName){
              											%>
                         											<%=cm.cmsPhrase("No match was found for ")%>
                         											<strong><%=StringEscapeUtils.escapeXml(formBean.getScientificName())%></strong>.&nbsp;
                         											<%=cm.cmsPhrase("The closest matches are listed below: ")%>
              											<%
                    										}	
                      									} else {
            											%>
                 											<%=cm.cmsPhrase("No match was found for ")%>
                 											<strong>
    													<%
	                											String searchCriteria = "";
	                											String name = "";
	                											Integer oper = Utilities.checkedStringToInt(formBean.getRelationOp(), Utilities.OPERATOR_CONTAINS);
	                											if(typeForm == NameSearchCriteria.CRITERIA_SCIENTIFIC){
	                          										if(searchVernacular){
	                    												searchCriteria = "name";
	                  												} else {
	                    												searchCriteria = "scientific name";
	                  												}
	                  												name = formBean.getScientificName();
	                											}
	                											if(typeForm == NameSearchCriteria.CRITERIA_VERNACULAR){
				                          							searchCriteria = "common name";
				                          							name = formBean.getVernacularName();
	                											}
    													%>
                  												<%=Utilities.prepareHumanString(searchCriteria, name, oper)%>
                 											</strong>
    													<%
              											}
            											%>
                          							</td>
                        						</tr>
                      						</table>
                      						<br />
                        					<%=cm.cmsPhrase("Results found")%>:
                        					<strong><%=resultsCount%></strong>
            								<%
				                          	// Prepare parameters for pagesize.jsp
				                          	Vector pageSizeFormFields = new Vector();       /*  These fields are used by pagesize.jsp, included below.    */
				                          	pageSizeFormFields.addElement("sort");          /*  *NOTE* I didn't add currentPage & pageSize since pageSize */
				                          	pageSizeFormFields.addElement("ascendency");    /*   is overriden & also pageSize is set to default           */
				                          	pageSizeFormFields.addElement("criteriaSearch");/*   to page '0', first page. */
				                          	pageSizeFormFields.addElement("useVernacular"); /*   to page '0', first page. */
				                          	if(newName)
				                          		pageSizeFormFields.addElement("newName");
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
					                        filterSearch.addElement("pageSize");
					                        filterSearch.addElement("useVernacular");
					                        if(newName)
					                        	filterSearch.addElement("newName");
            								%>
                        					<br />
                        					<table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0" style="background-color:#EEEEEE">
                        						<tr>
                          							<td>
                              							<%=cm.cmsPhrase("Refine your search")%>
                          							</td>
                        						</tr>
                        						<tr>
                          							<td>
                            							<form name="refineSearch" method="get" onsubmit="return validateRefineForm(<%=noCriteria%>);" action="<%=pageName%>">
                              								<input type="hidden" name="noSoundex" value="true" />
                              								<%=formBean.toFORMParam(filterSearch)%>
                              								<label for="select1" class="noshow"><%=cm.cmsPhrase("Criteria")%></label>
            								<%
                              								if (!showGroup) {
            								%>
                               									<input type="hidden" name="criteriaType" value="<%=NameSearchCriteria.CRITERIA_SCIENTIFIC_NAME%>" />
            								<%
                              								}
            								%>
                              								<select id="select1" title="<%=cm.cmsPhrase("Criteria")%>" name="criteriaType" <%=(showGroup ? "" : "disabled=\"disabled\"")%>>
            								<%
	                              								if (showGroup) {
            								%>
	                                								<option value="<%=NameSearchCriteria.CRITERIA_GROUP%>">
	                                    								<%=cm.cmsPhrase("Group")%>
	                                								</option>
            								<%
	                              								}
	                              								if (showScientificName) {
            								%>
	                                								<option value="<%=NameSearchCriteria.CRITERIA_SCIENTIFIC_NAME%>" selected="selected">
	                                    								<%=cm.cmsPhrase("Scientific name")%>
	                                								</option>
            								<%
	                              								}
            								%>
                              								</select>
                              								<select id="select2" title="<%=cm.cmsPhrase("Operator")%>" name="oper">
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
                              								<input type="hidden" name="typeForm" value="<%=typeForm%>" />
                                							<label for="criteriaSearch" class="noshow">
                                  								<%=cm.cmsPhrase("Criteria")%>
                                							</label>
                              								<input id="criteriaSearch" title="<%=cm.cmsPhrase("Filter value")%>" name="criteriaSearch" type="text" size="30" />
                              								<input id="refine" title="<%=cm.cmsPhrase("Search")%>" class="submitSearchButton" type="submit" name="Submit" value="<%=cm.cmsPhrase("Search")%>" />
                            							</form>
                          							</td>
                        						</tr>
                        						<%-- This is the code which shows the search filters --%>
            								<%
                        						ro.finsiel.eunis.search.AbstractSearchCriteria[] criterias = formBean.toSearchCriteria();
                        						if (criterias.length > 1) {
            								%>
                          							<tr>
                            							<td>
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
                              								<td>
                                								<a title="<%=cm.cms("species_names-result_04_Title")%>" href="<%= pageName%>?<%=formBean.toURLParam(filterSearch)%>&amp;removeFilterIndex=<%=i%>">
                                  									<img alt="<%=cm.cms("delete")%>" src="images/mini/delete.jpg" border="0" style="vertical-align:middle" />
                                								</a>
                                								<%=cm.cmsTitle("species_names-result_04_Title")%>
                                								<%=cm.cmsAlt("delete")%>
                                								&nbsp;&nbsp;
                                								<strong class="linkDarkBg">
                                    								<%= i + ". " + criteria.toHumanString()%>
                                								</strong>
                              								</td>
                            							</tr>
            								<%
                          							}
                        						}	
            								%>
                      						</table>
                                              <%
                                              // Expand/Collapse common names
                                              Vector expand = new Vector();
                                              expand.addElement("sort");
                                              expand.addElement("ascendency");
                                              expand.addElement("criteriaSearch");
                                              expand.addElement("oper");
                                              expand.addElement("criteriaType");
                                              expand.addElement("pageSize");
                                              expand.addElement("currentPage");
                                              expand.addElement("useVernacular");
                                              if(newName)
                                              expand.addElement("newName");

                                              String toggleLink = formBean.toURLParam(expand);
                                              if(!toggleLink.contains("showVernacularNames"))
                                              toggleLink = toggleLink + "&showVernacularNames=" + showVernacularNames;
                                              toggleLink = toggleLink.replace("showVernacularNames=" + showVernacularNames,"showVernacularNames=" + (!showVernacularNames));

                                              %>

                                              <a rel="nofollow" href="<%=pageName + "?" + toggleLink%>" style="font-weight: bold;">
                                                  <% if(showVernacularNames){ %>
                                                  <%=cm.cmsPhrase("Display English common names only")%>
                                                  <% } else { %>
                                                  <%=cm.cmsPhrase("Display all available common names")%>
                                                  <% } %>
                                              </a>
                      						<br />
            								<%
					                        Vector navigatorFormFields = new Vector();  /*  The following fields are used by paginator.jsp, included below.      */
					                        navigatorFormFields.addElement("pageSize"); /* NOTE* that I didn't add here currentPage since it is overriden in the */
					                        navigatorFormFields.addElement("sort");     /* <form name='..."> in the navigator.jsp!                               */
					                        navigatorFormFields.addElement("ascendency");
					                        navigatorFormFields.addElement("criteriaSearch");
					                        navigatorFormFields.addElement("useVernacular");
					                        if(newName)
					                        	navigatorFormFields.addElement("newName");
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
                        					// Compute the sort criteria
					                        Vector sortURLFields = new Vector();      /* Used for sorting */
					                        sortURLFields.addElement("pageSize");
					                        sortURLFields.addElement("criteriaSearch");
					                        sortURLFields.addElement("useVernacular");
					                        if(newName)
					                        	sortURLFields.addElement("newName");
					                        String urlSortString = formBean.toURLParam(sortURLFields);
					                        AbstractSortCriteria sortGroup = formBean.lookupSortCriteria(NameSortCriteria.SORT_GROUP);
					                        AbstractSortCriteria sortOrder = formBean.lookupSortCriteria(NameSortCriteria.SORT_ORDER);
					                        AbstractSortCriteria sortFamily = formBean.lookupSortCriteria(NameSortCriteria.SORT_FAMILY);
					                        AbstractSortCriteria sortSciName = formBean.lookupSortCriteria(NameSortCriteria.SORT_SCIENTIFIC_NAME);
					                        AbstractSortCriteria sortValidName = formBean.lookupSortCriteria(NameSortCriteria.SORT_VALID_NAME);

                                               %>
                                              <%=cm.cmsTitle("show_vernacular_list")%>

                                              <table summary="<%=cm.cmsPhrase("Search results")%>" cellpadding="0" cellspacing="0" width="100%" class="sortable listing">
                        						<thead>
                        							<tr>
            								<%
                        								if (showGroup) {
                        									if(newName){
                        					%>
                        										<th scope="col" class="nosort">
	                                								<%=cm.cmsPhrase("Group")%>
	                          									</th>
                        					<%                        										
                        									} else {
            								%>
	                          									<th class="sorted" scope="col" class="nosort">
	                            									<a title="<%=cm.cms("species_names-result_07_Title")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_GROUP%>&amp;ascendency=<%=formBean.changeAscendency(sortGroup, (null == sortGroup) ? true : false)%>"><%=Utilities.getSortImageTag(sortGroup)%><%=cm.cmsPhrase("Group")%></a>
	                            									<%=cm.cmsTitle("species_names-result_07_Title")%>
	                          									</th>
            								<%
                        									}
                        								}
                        								if (showOrder) {
            								%>
                          									<th scope="col" class="nosort">
                                								<%=cm.cmsPhrase("Order")%>
                          									</th>
            								<%
                        								}
                        								if (showFamily) {
            								%>
                          									<th scope="col" class="nosort">
                            									<%=cm.cmsPhrase("Family")%>
                          									</th>
            								<%
                        								}
                        								if (showScientificName)	{
            								%>
                          									<th scope="col" class="nosort">
                            									<a title="<%=cm.cms("species_names-result_07_Title")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_SCIENTIFIC_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sortSciName, (null == sortSciName) ? true : false)%>"><%=Utilities.getSortImageTag(sortSciName)%><%=cm.cmsPhrase("Scientific name")%></a>
                            									<%=cm.cmsTitle("species_names-result_07_Title")%>
                          									</th>
            								<%
                        								}
                                                        if (showVernacularNames){
                                            %>
                                                            <th scope="col" class="nosort">
                                                                <%=cm.cmsPhrase("Common names")%>
                                                            </th>
                                                        <%
                                                        } else {
                                                        %>
                                                            <th scope="col" class="nosort">
                                                            <%=cm.cmsPhrase("English common name")%>
                                                            </th>
                                                        <%
                                                        }

                        								if (showValidName) {
                        									if(newName){
                        					%>
                        										<th scope="col" class="nosort">
	                            									<%=cm.cmsPhrase("Valid name")%>
	                          									</th>
                        					<%				
                        									} else {
            								%>
	                          									<th scope="col" class="nosort">
	                            									<a title="<%=cm.cms("species_names-result_07_Title")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_VALID_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sortValidName, (null == sortValidName) ? true : false)%>"><%=Utilities.getSortImageTag(sortValidName)%><%=cm.cmsPhrase("Valid name")%></a>
	                            									<%=cm.cmsTitle("species_names-result_07_Title")%>
	                          									</th>
            								<%
                        									}
                        								}
            								%>
                        							</tr>
                        						</thead>
            								<%
                        					Iterator it = results.iterator();
                        					// Coming from first form
                        					if (NameSearchCriteria.CRITERIA_SCIENTIFIC == typeForm){
                        						%>
                        						<tbody>
                        						<%
                          						int col = 0;
                          						while ( it.hasNext() ){
                            						ScientificNamePersist specie = ( ScientificNamePersist ) it.next();

                                                    Vector vernNamesList = SpeciesSearchUtility.findVernacularNames( specie.getIdNatureObject() );
                                                    // Sort this common names in alphabetical order
                                                    Vector sortVernList = new JavaSorter().sort( vernNamesList, JavaSorter.SORT_ALPHABETICAL );

                                                    String englishName = SpeciesSearchUtility.findEnglishName( specie.getIdNatureObject() );
						                            // Sort this common names in alphabetical order
						                            %>
                        							<tr>
                        					<%
                          								if ( showGroup ){
                        					%>
                        									<td>
                          										<%=Utilities.treatURLSpecialCharacters( specie.getCommonName() )%>
                        									</td>
                        					<%
                          								}
                          								if ( showOrder ){
                        					%>
                        									<td>
                          										<%=Utilities.formatString( Utilities.treatURLSpecialCharacters( specie.getTaxonomicNameOrder() ), "&nbsp;" )%>
                        									</td>
                        					<%
                          								}
                          								if ( showFamily ){
                        					%>
                        									<td>
                          										<%=Utilities.formatString( Utilities.treatURLSpecialCharacters( specie.getTaxonomicNameFamily() ), "&nbsp;" )%>
                        									</td>
                        					<%
                          								}
                          								if ( showScientificName ){
                        					%>
                        									<td>
                          										<a href="species/<%=specie.getIdSpecies()%>"><%=Utilities.treatURLSpecialCharacters( specie.getScientificName() )%></a>
                          					<%
                            									if ( !specie.getTypeRelatedSpecies().equalsIgnoreCase( "Species" ) ){
                          					%>
                          											(<%=specie.getTypeRelatedSpecies()%>)
                          					<%
                            									}
                          					%>
                        									</td>
                        					<%
                          								}
                          								boolean isValid;
                          								if ( specie.getValidName() == null ){
                            								isValid = false;
                          								} else {
                            								isValid = specie.getValidName().intValue() == 1;
                          								}
                          								%>
                                                        <td>
                                                            <%
                                                            if ( showVernacularNames ) {
                                                                if ( sortVernList != null && sortVernList.size() > 0 ){
                                                            %>
                                                                <%-- I display the common names within a table inside the cell, DON'T USE ROWSPAN, YOU'L REGRET IT --%>
                                                                <table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
                                                                    <col style="width:30%"/>
                                                                    <col style="width:70%"/>

                                                                    <%
                                                                        for ( int i = 0; i < sortVernList.size(); i++ ){
                                                                            VernacularNameWrapper aVernName = ( VernacularNameWrapper ) sortVernList.get( i );
                                                                            String vernacularName = aVernName.getName();
                                                                            String searchedName = formBean.getVernacularName();
                                                                            String bgColor1 = ( 0 == i % 2 ) ? "#EEEEEE" : "#FFFFFF";
                                                                            if ( null != searchedName && -1 != vernacularName.toLowerCase().lastIndexOf( searchedName.toLowerCase() ) ){
                                                                                bgColor1 = "#BBBBFF";
                                                                            }
                                                                    %>
                                                                    <tr>
                                                                        <td style="background-color:<%=bgColor1%>">
                                                                            <%=Utilities.treatURLSpecialCharacters( aVernName.getLanguage() )%>
                                                                        </td>
                                                                        <td style="background-color:<%=bgColor1%>">
                                                                            <%=Utilities.treatURLSpecialCharacters( vernacularName )%>
                                                                        </td>
                                                                    </tr>
                                                                    <% } %>
                                                                </table>
                                                            <% } %>

                                                            <%  } else { %>
                                                                <%= englishName %>
                                                            <%
                                                            }
                                                            %>
                                                        </td>
                                                        <%
                          								if ( showValidName ){
	                            							if ( isValid ){
	                        				%>
	                        									<td>
	                          										<img alt="Valid species name" src="images/mini/check_green.gif" width="15" height="16" style="vertical-align:middle" title="Yes"/>
	                        									</td>
	                        				<%
	                        								} else {
	                        				%>
	                        									<td>
	                          										<img alt="Invalid species name" src="images/mini/invalid.gif" width="15" height="16" style="vertical-align:middle" title="No"/>
	                        									</td>
	                        				<%
	                            							}
                          								}
                        					%>
                        							</tr>
                        					<%
	                          						}
                        					%>
                        						</tbody>
                        					<%
                        					}
                        					// Coming from second form - ANY LANGUAGE
                        					if (NameSearchCriteria.CRITERIA_VERNACULAR == typeForm && isAnyLanguage){
                        						%>
                        						<tbody>
                        						<%
                          						int col = 0;
                          						while (it.hasNext()){
						                            VernacularNameAnyPersist specie = (VernacularNameAnyPersist)it.next();
						                            String englishName = SpeciesSearchUtility.findEnglishName(specie.getIdNatureObject());


                                                      Vector vernNamesList = SpeciesSearchUtility.findVernacularNames( specie.getIdNatureObject() );
                                                      // Sort this common names in alphabetical order
                                                      Vector sortVernList = new JavaSorter().sort( vernNamesList, JavaSorter.SORT_ALPHABETICAL );



            								%>
                          								<tr>
            								<%
                          								if (showGroup){
            								%>
                            								<td>
                              									<%=Utilities.treatURLSpecialCharacters(specie.getCommonName())%>
                            								</td>
            								<%
                          								}
                          								if (showOrder){
            								%>
                            								<td>
                              									<%=Utilities.treatURLSpecialCharacters(Utilities.formatString(specie.getTaxonomicNameOrder()))%>
                            								</td>
            								<%
                          								}
                          								if (showFamily){
            								%>
                            								<td>
                              									<%=Utilities.treatURLSpecialCharacters(Utilities.formatString(specie.getTaxonomicNameFamily()))%>
                            								</td>
            								<%
                          								}
                          								if (showScientificName){
            								%>
                            								<td>
                              									<a href="species/<%=specie.getIdSpecies()%>"><%=Utilities.treatURLSpecialCharacters(specie.getScientificName())%></a>
                            								</td>
            								<%
                          								}
                            							boolean isValid = specie.getValidName() != null && specie.getValidName().intValue() == 1;
                                            %>
                                                        <td>
                                                                  <%
                                                                      if ( showVernacularNames ) {
                                                                          if ( sortVernList != null && sortVernList.size() > 0 ){
                                                                  %>
                                                                      <%-- I display the common names within a table inside the cell, DON'T USE ROWSPAN, YOU'L REGRET IT --%>
                                                                  <table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
                                                                      <col style="width:30%"/>
                                                                      <col style="width:70%"/>

                                                                      <%
                                                                          for ( int i = 0; i < sortVernList.size(); i++ ){
                                                                              VernacularNameWrapper aVernName = ( VernacularNameWrapper ) sortVernList.get( i );
                                                                              String vernacularName = aVernName.getName();
                                                                              String searchedName = formBean.getVernacularName();
                                                                              String bgColor1 = ( 0 == i % 2 ) ? "#EEEEEE" : "#FFFFFF";
                                                                              if ( null != searchedName && -1 != vernacularName.toLowerCase().lastIndexOf( searchedName.toLowerCase() ) ){
                                                                                  bgColor1 = "#BBBBFF";
                                                                              }
                                                                      %>
                                                                      <tr>
                                                                          <td style="background-color:<%=bgColor1%>">
                                                                              <%=Utilities.treatURLSpecialCharacters( aVernName.getLanguage() )%>
                                                                          </td>
                                                                          <td style="background-color:<%=bgColor1%>">
                                                                              <%=Utilities.treatURLSpecialCharacters( vernacularName )%>
                                                                          </td>
                                                                      </tr>
                                                                      <% } %>
                                                                  </table>
                                                                  <% } %>

                                                                  <%  } else { %>
                                                                  <%= englishName %>
                                                                  <%
                                                                      }
                                                                  %>
                                                              </td>
                                            <%
                            							if ( showValidName ){
                              								if ( isValid ){
            								%>
                                								<td>
                                  									<img alt="Valid species name" src="images/mini/check_green.gif" width="15" height="16" style="vertical-align:middle" title="Yes" />
                                								</td>
            								<%
                              								} else {
            								%>
                                								<td>
                                  									<img alt="Invalid species name" src="images/mini/invalid.gif" width="15" height="16" style="vertical-align:middle" title="No" />
                                								</td>
            								<%
                              								}
                            							}

            								%>
                          								</tr>
            								<%
                            						}
	        								%>
                      								</tbody>
        									<%
                        						}
                          						// Coming from second form - A LANGUAGE
                          						if (NameSearchCriteria.CRITERIA_VERNACULAR == typeForm && !isAnyLanguage){
                            						int col = 0;
            								%>
                          							<tbody>
                        					<%
                            						while (it.hasNext()){
                              							VernacularNamePersist specie = (VernacularNamePersist)it.next();
                              							String englishName = SpeciesSearchUtility.findEnglishName(specie.getIdNatureObject());

                                                        Vector vernNamesList = SpeciesSearchUtility.findVernacularNames( specie.getIdNatureObject() );
                                                        // Sort this common names in alphabetical order
                                                        Vector sortVernList = new JavaSorter().sort( vernNamesList, JavaSorter.SORT_ALPHABETICAL );
                                            %>
                                                    <tr>
            								<%
                              								if (showGroup){
            								%>
                                								<td>
                                  									<%=Utilities.treatURLSpecialCharacters(specie.getCommonName())%>
                                								</td>
            								<%
                              								}
                              								if (showOrder){
            								%>
                                								<td>
                                  									<%=Utilities.treatURLSpecialCharacters(Utilities.formatString(specie.getTaxonomicNameOrder()))%>
                                								</td>
            								<%
                              								}
                              								if (showFamily){
            								%>
                                								<td>
                                  									<%=Utilities.treatURLSpecialCharacters(specie.getTaxonomicNameFamily())%>
                                								</td>
            								<%
                              								}
                              								if (showScientificName){
            								%>
                                								<td>
                                  									<a href="species/<%=specie.getIdSpecies()%>"><%=Utilities.treatURLSpecialCharacters(specie.getScientificName())%></a>
                                								</td>
            								<%
                              								}
                            								boolean isValid = specie.getValidName() != null && specie.getValidName().intValue() == 1;
                            								if ( showValidName ){
                              									if ( isValid ){
            								%>
                                									<td>
                                  										<img alt="Valid species name" src="images/mini/check_green.gif" width="15" height="16" style="vertical-align:middle" title="Yes" />
                                									</td>
            								<%
                              									} else {
            								%>
                                									<td>
                                  										<img alt="Invalid species name" src="images/mini/invalid.gif" width="15" height="16" style="vertical-align:middle" title="No" />
                                									</td>
            								<%
                              									}
                            								}
                          								%>
                                                <td>
                                                    <%
                                                        if ( showVernacularNames ) {
                                                            if ( sortVernList != null && sortVernList.size() > 0 ){
                                                    %>
                                                        <%-- I display the common names within a table inside the cell, DON'T USE ROWSPAN, YOU'L REGRET IT --%>
                                                    <table summary="layout" width="100%" border="0" cellspacing="0" cellpadding="0">
                                                        <col style="width:30%"/>
                                                        <col style="width:70%"/>

                                                        <%
                                                            for ( int i = 0; i < sortVernList.size(); i++ ){
                                                                VernacularNameWrapper aVernName = ( VernacularNameWrapper ) sortVernList.get( i );
                                                                String vernacularName = aVernName.getName();
                                                                String searchedName = formBean.getVernacularName();
                                                                String bgColor1 = ( 0 == i % 2 ) ? "#EEEEEE" : "#FFFFFF";
                                                                if ( null != searchedName && -1 != vernacularName.toLowerCase().lastIndexOf( searchedName.toLowerCase() ) ){
                                                                    bgColor1 = "#BBBBFF";
                                                                }
                                                        %>
                                                        <tr>
                                                            <td style="background-color:<%=bgColor1%>">
                                                                <%=Utilities.treatURLSpecialCharacters( aVernName.getLanguage() )%>
                                                            </td>
                                                            <td style="background-color:<%=bgColor1%>">
                                                                <%=Utilities.treatURLSpecialCharacters( vernacularName )%>
                                                            </td>
                                                        </tr>
                                                        <% } %>
                                                    </table>
                                                    <% } %>

                                                    <%  } else { %>
                                                    <%= englishName %>
                                                    <%
                                                        }
                                                    %>
                                                </td>
                              							</tr>
            								<%	} 	%>
                        							</tbody>
            								<%
                          						}
            								%>
                        						<thead>
                          							<tr>
            								<%
                        								if (showGroup){
                        									if(newName){
                        					%>
                        										<th scope="col" class="nosort">
	                                								<%=cm.cmsPhrase("Group")%>
	                          									</th>
                        					<%				
                        									} else {
            								%>
	                          									<th scope="col" class="nosort">
	                            									<a title="<%=cm.cms("species_names-result_07_Title")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_GROUP%>&amp;ascendency=<%=formBean.changeAscendency(sortGroup, (null == sortGroup) ? true : false)%>"><%=Utilities.getSortImageTag(sortGroup)%><%=cm.cmsPhrase("Group")%></a>
	                            									<%=cm.cmsTitle("species_names-result_07_Title")%>
	                          									</th>
            								<%
                        									}
                        								}
                        								if (showOrder){
            								%>
                          									<th scope="col" class="nosort">
                                								<%=cm.cmsPhrase("Order")%>
                          									</th>
            								<%
                        								}
                        								if (showFamily){
            								%>
                          									<th scope="col" class="nosort">
                            									<%=cm.cmsPhrase("Family")%>
                          									</th>
            								<%
                        								}
                        								if (showScientificName){
            								%>	
                          									<th scope="col" class="nosort">
                            									<a title="<%=cm.cms("species_names-result_07_Title")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_SCIENTIFIC_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sortSciName, (null == sortSciName) ? true : false)%>"><%=Utilities.getSortImageTag(sortSciName)%><%=cm.cmsPhrase("Scientific name")%></a>
									                            <%=cm.cmsTitle("species_names-result_07_Title")%>
                          									</th>
            								<%
                        								}

                                                        if (showVernacularNames){
                                            %>
                                                          <th scope="col" class="nosort">
                                                              <%=cm.cmsPhrase("Common names")%>
                                                          </th>
                                                          <%
                                                          } else {
                                                          %>
                                                          <th scope="col" class="nosort">
                                                              <%=cm.cmsPhrase("English common name")%>
                                                          </th>
                                                          <%
                                                        }

                        								if (showValidName){
                        									if(newName){
                        					%>
                        										<th scope="col" class="nosort">
	                            									<%=cm.cmsPhrase("Valid name")%>
	                          									</th>
                        					<%				
                        									} else {
            								%>
	                          									<th scope="col" class="nosort">
	                            									<a title="<%=cm.cms("species_names-result_07_Title")%>" href="<%=pageName + "?" + urlSortString%>&amp;sort=<%=NameSortCriteria.SORT_VALID_NAME%>&amp;ascendency=<%=formBean.changeAscendency(sortValidName, (null == sortValidName) ? true : false)%>"><%=Utilities.getSortImageTag(sortValidName)%><%=cm.cmsPhrase("Valid name")%></a>
	                            									<%=cm.cmsTitle("species_names-result_07_Title")%>
	                          									</th>
            								<%
                        									}
                        								}

            								%>
                        							</tr>
                        						</thead>
                      						</table>
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
            					<%=cm.cmsMsg("species_names-result_pageTitle")%>
            					<%=cm.br()%>
							<!-- END MAIN CONTENT -->

    	<script type="text/javascript">
	    	//<![CDATA[
	        // Writes a warning if the page is called as a popup. Works only in IE
	        if ( history.length == 0 && document.referrer != '') {
	            c = document.getElementById('content');
	            w = document.createElement('div');
	            w.className = "note-msg";
	            w.innerHTML = "<strong>Notice</strong> <p>This page was opened as a new window. The back button has been disabled by the referring page. Close the window to exit.</p>";
	            c.insertBefore(w, c.firstChild);
	        }
      		//]]>
    	</script>

    </stripes:layout-component>
</stripes:layout-render>

