<%--
  - Description : Resolution 4 habitat types browser/tree
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<%@ page import="ro.finsiel.eunis.WebContentManagement"%>
<%@ page import="ro.finsiel.eunis.search.Utilities"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.sql.DriverManager"%>
<%@ page import="ro.finsiel.eunis.utilities.SQLUtilities"%>
<%@ page import="eionet.eunis.util.JstlFunctions" %>
<%@ page import="ro.finsiel.eunis.jrfTables.habitats.factsheet.HabitatLegalPersist" %>
<%@ page import="java.util.List" %>
<%@ page import="ro.finsiel.eunis.jrfTables.habitats.factsheet.HabitatLegalDomain" %>
<%@ page import="ro.finsiel.eunis.jrfTables.Chm62edtHabitatDomain" %>
<%@ page import="ro.finsiel.eunis.jrfTables.Chm62edtHabitatPersist" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ro.finsiel.eunis.jrfTables.ReferencesDomain" %>
<%@ page import="ro.finsiel.eunis.search.species.references.ReferencesSearchCriteria" %>
<%@ page import="ro.finsiel.eunis.search.AbstractSortCriteria" %>
<%@ page import="eionet.eunis.util.Constants" %>
<%@ page import="eionet.eunis.dto.HabitatDTO" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<%
  WebContentManagement cm = SessionManager.getWebContent();
  String eeaHome = application.getInitParameter( "EEA_HOME" );
  String btrail = "eea#" + eeaHome + ",home#index.jsp,habitat_types#habitats.jsp,eunis_habitat_type_hierarchical_view";
%>
<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cmsPhrase("Bern Convention Resolution No 4 (1998) hierarchical view") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" pageTitle="${title}" btrail="<%= btrail%>">
    <stripes:layout-component name="head">
        <link rel="StyleSheet" href="css/eunistree.css" type="text/css" />
    </stripes:layout-component>
    <stripes:layout-component name="contents">
        <a name="documentContent"></a>
          <h1>
            <%=cm.cmsPhrase("Bern Convention Resolution No 4 (1998) hierarchical view")%>
          </h1>
          The top level is for grouping only
<!-- MAIN CONTENT -->
            <br/>
            <%
                String expand = Utilities.formatString( request.getParameter( "expand" ), "" );
                String hide = cm.cmsPhrase("Hide sublevel habitat types");
                String show = cm.cmsPhrase("Show sublevel habitat types");

                ReferencesDomain refDomain = new ReferencesDomain(new ReferencesSearchCriteria[0], new AbstractSortCriteria[0]);
                List<HabitatDTO> references = refDomain.getHabitatsForAReferences(Constants.RESOLUTION4.toString());



                boolean showExpanded = false;
                Chm62edtHabitatPersist topLevelHabitat = null;
            %>

                <ul class="eunistree">
                <%
                String lastTopLevel="ZZZ";
                for(HabitatDTO emerald : references) {
                    String topLevel = emerald.getCode().trim().substring(0,1);
                    if(!topLevel.equals(lastTopLevel)) {
                        lastTopLevel = topLevel;
                        if(!lastTopLevel.equals("ZZZ")){
                            if(showExpanded) {
                                %> </ul> <%
                            }
                        %> </li> <%
                        }

                        // get top level data
                        List topLevelList = new Chm62edtHabitatDomain().findWhere("EUNIS_HABITAT_CODE='" + topLevel + "'");

                        if (topLevelList.size() > 0) {
                            topLevelHabitat = (Chm62edtHabitatPersist) topLevelList.get(0);
                        } else {
                            topLevelHabitat = new Chm62edtHabitatPersist(); // should not be the case
                        }

                        showExpanded = Utilities.expandContains(expand, topLevel);

                        %>

                        <li>
                        <% if( showExpanded ){ %>
                        <a rel="nofollow" id="level_<%= topLevel %>" href="habitats-emerald-browser.jsp?expand=<%=Utilities.removeFromExpanded(expand, topLevel)%>#level_<%= topLevel %>"><img src="images/img_minus.gif" alt="<%=hide%>"/></a>
                        <% } else { %>
                        <a rel="nofollow" id="level_<%= topLevel %>" href="habitats-emerald-browser.jsp?expand=<%=Utilities.addToExpanded(expand, topLevel)%>#level_<%= topLevel %>"><img src="images/img_plus.gif" alt="<%=show%>"/></a>
                        <% } %>
                        <a title="<%= topLevelHabitat.getScientificName() %>" href="habitats/<%= topLevelHabitat.getIdHabitat() %>"><%= topLevelHabitat.getEunisHabitatCode() %> : <%=JstlFunctions.bracketsToItalics(topLevelHabitat.getScientificName())%></a><br/>
                        <%--Top level <%= topLevel %>--%>
                        <% if(showExpanded) { %>
                        <ul class="eunistree">
                        <% } %>
                    <%
                       }
                    %>
                        <% if(showExpanded) { %>
                            <li>
                                <img src="images/img_bullet.gif"/>&nbsp;<a title="<%= emerald.getName() %>" href="habitats/<%= emerald.getIdHabitat() %>"><%= emerald.getCode() %> : <%=JstlFunctions.bracketsToItalics(emerald.getName())%></a>
                            </li>
                        <%  } %>
               <%  } %>

               </ul>
                  <br/>
<!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>