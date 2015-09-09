<%--
  - Author(s)   : The EUNIS Database Team.
  - Date        :
  - Copyright   : (c) 2002-2005 EEA - European Environment Agency.
  - Description : About page for the application.x
  - Request params : -
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement"%>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session"/>

<%
  WebContentManagement cm = SessionManager.getWebContent();
  String eeaHome = application.getInitParameter( "EEA_HOME" );
  String btrail = "eea#" + eeaHome + ",home#index.jsp,about_EUNIS_database";
%>

<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cmsPhrase("About the European Nature Information System, EUNIS") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" pageTitle="${title}" btrail="<%= btrail%>">
    <stripes:layout-component name="head">
    </stripes:layout-component>
    <stripes:layout-component name="contents">

        <a name="documentContent"></a>
		<h1><%=cm.cmsPhrase("About the European Nature Information System, EUNIS")%></h1>
<!-- MAIN CONTENT -->
        <%--<%=cm.cmsText("generic_about_01")%>--%>

        <p>The European nature information system, EUNIS, brings together European data from several databases and organisations into three interlinked modules on sites, species and habitat types.</p>

        <p>The EUNIS information system is part of the European <a href="http://www.eea.europa.eu/themes/biodiversity/dc">Biodiversity data centre (BDC)</a> and it is a contribution to the knowledge base for implementing the EU and global biodiversity strategies and the <a href="http://ec.europa.eu/environment/newprg/">7th Environmental Action Programme</a>.</p>

        <p>The EUNIS information system provides access to the publicly available data in the EUNIS database. The information includes:</p>
        <ul><li>Data on species, habitat types and designated sites compiled in the framework of Natura 2000 (EU Habitats and Birds Directives);</li>
        <li>The EUNIS habitat classification;</li>
        <li>Data from material compiled by the <a href="http://bd.eionet.europa.eu/">European Topic Centre of Biological Diversity</a>;</li>
        <li>Information on species, habitat types and designated sites mentioned in relevant international conventions and in the IUCN Red Lists;</li>
        <li>Specific data collected in the framework of the EEA's reporting activities, which also constitute a core set of data to be updated periodically, e.g. Eionet priority dataflow Nationally designated areas (CDDA).</li>
        </ul>

        <h2>What are the purposes of the EUNIS information system?</h2>
        <p>EUNIS is a reference information system for anyone working in ecology and conservation or those with an interest in the natural world. It is also used for</p>
        <ul><li>
            assistance to the Natura 2000 process (EU Birds and Habitats Directives) and coordinated with the related EMERALD Network of the Bern Convention;
        </li><li>
            the development of indicators (EEA Core Set);
        </li><li>
            environmental reporting connected to EEA reporting activities.
        </li></ul>

        <br>

        <div class="eea-accordion-panels non-exclusive collapsed-by-default">
            <div class="eea-accordion-panel" style="clear: both;" id="about-accordion">
                <h2 class="notoc eea-icon-right-container">About the EUNIS database</h2>
                <div class="pane">

                    <p>The data that is integrated and visualised through the EUNIS information system are collected and maintained by the <a href="http://bd.eionet.europa.eu/">European Topic Centre on Biological Diversity</a>, the <a href="http://eea.europa.eu/">European Environment Agency</a> and the <a href="http://www.eionet.europa.eu/">European Environmental Information Observation Network</a> to be used for environmental reporting and for assistance to the Natura 2000 process (EU Birds and Habitats Directives) and coordinated to the related EMERALD Network of the Bern Convention.</p>
                    <p>The datasets consists of information on <a href="/species.jsp">Species</a>, <a href="/habitats.jsp">Habitat types</a> and <a href="/sites.jsp">Sites</a>.</p>

                    <h3><a href="/species.jsp">Species</a></h3>

                    <p>The species module contains information about more than 278 000 taxa occuring in Europe. However, the amount of information collected on each species varies in accordance with the potential use of the data:</p>
                    <ul><li>
                        <a href="http://eu-nomen.eu/portal">PESI</a> (http://eu-nomen.eu/portal) is the taxonomic reference for the species in EUNIS;
                    </li><li>
                        Spatial-temporal information (including species population size and trends) is available for species reported under the EU Habitats Directive and for the birds.
                    </li><li>
                        Data concerning the conservation status has been collected from the reporting of EU Member States under the EU Habitats Directive and from the IUCN Red List.
                    </li></ul>

                    <h3><a href="/habitats.jsp">Habitat types</a></h3>

                    <p>The habitat types module consist of the EUNIS habitat classification, the habitat types listed in Annex I of the EU Habitats Directive and of the habitat types in Resolution 4 of the Bern Convention.</p>
                    <p>The EUNIS habitat classification is a comprehensive pan-European system to facilitate the harmonised description and collection of data across Europe through the use of criteria for habitat identification; it covers all types of habitats from natural to artificial, from terrestrial to freshwater and marine.</p>
                    <p>Habitat type is defined for the purposes of the EUNIS habitat classification as follows: 'Plant and animal communities as the characterising elements of the biotic environment, together with abiotic factors operating together at a particular scale.' All factors included in the definition are addressed in the descriptive framework of the habitat classification. The scope of the EUNIS classification is limited to level 3 in its hierarchy (level 4 for Marine habitat types). At level 4 (5 for the Marine types) and below, the component units are drawn from other classification systems and combine these in the common framework.</p>
                    <p>A criteria-based key has been developed for all units to level 3 and in addition for salt marshes at level 4. The key takes the form of a sequential series of questions with additional detailed explanatory notes. Depending on the answer chosen, the user is directed to the next question in the series or to a habitat type identified by the parameters. The user may follow the key question by question, or view the criteria for each habitat level in a series of static diagrams.</p>
                    <p>The parameters used to form the framework of the classification may also be used in the search options in the web site.</p>

                    <h3><a href="/sites.jsp">Sites</a></h3>

                    The sites module of EUNIS uses data from the following databases:
                    <ul><li>
                        <a href="http://www.eea.europa.eu/data-and-maps/data/ds_resolveuid/52E54BF3-ACDB-4959-9165-F3E4469BE610">Natura 2000 data - the European network of protected sites</a>
                    </li><li>
                        <a href="http://www.eea.europa.eu/data-and-maps/data/ds_resolveuid/29430BDF-9AFB-4F1A-A529-7B4EF6D680D7">Nationally designated areas (protected areas) (CDDA)</a>
                    </li><li>
                        <a href="http://www.coe.int/t/dg4/cultureheritage/nature/diploma/default_en.asp">European Nature Diploma Areas (Council of Europe)</a>
                    </li></ul>
                </div>
            </div>
            <div class="eea-accordion-panel" style="clear: both;" id="rdf-accordion">
                <h2 class="notoc eea-icon-right-container">EUNIS database in RDF/XML</h2>
                <div class="pane">
                    <p>
                        In order to facilitate combination of EUNIS data with other datasets, the EUNIS database has been made available as RDF/XML. To cater to stakeholders who are interested in only a subset of the data, the dataset has been partitioned into several files:
                    </p>

                    <ul>
                    <c:forEach var="file" items="${actionBean.rdfFileList}">
                        <li><a href="${file.fileName}">${file.title}</a>
                            <c:if test="${not empty file.comment}"> - ${file.comment}</c:if>
                            <span class="discreet">(last updated: <fmt:formatDate value="${file.recordDate}" pattern="dd-MM-YYYY"/>)</span>
                        </li>
                    </c:forEach>
                    </ul>
                </div>
            </div>
        </div>

<!-- END MAIN CONTENT -->

    </stripes:layout-component>
</stripes:layout-render>