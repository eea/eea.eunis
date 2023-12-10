<%@page contentType="text/html;charset=UTF-8"%>

<%@ include file="/stripes/common/taglibs.jsp"%>
<c:set var="title" value=""/>
<c:set var="notExistErrMsg" value="${eunis:cmsPhrase(actionBean.contentManagement, 'We are sorry, the requested species does not exist')}"/>
<c:choose>
    <c:when test="${eunis:exists(actionBean.factsheet)}">
        <c:set var="title" value="${actionBean.speciesTitle }"></c:set>
    </c:when>
    <c:otherwise>
        <c:set var="title" value="${notExistErrMsg}"></c:set>
    </c:otherwise>
</c:choose>
<%
String btrail = "eea#" + application.getInitParameter( "EEA_HOME" ) + ",home#index.jsp,species";
%>
<stripes:layout-render name="/stripes/common/template.jsp" hideMenu="true" pageTitle="${title}" btrail="<%= btrail %>">
    <stripes:layout-component name="head">
        <link rel="stylesheet" type="text/css" href="css/temp_gallery.css">
        <link rel="stylesheet" type="text/css" href="${actionBean.context.distributionArcgisCSS}"/>
        <!-- Custom js needed for Species page -->
        <script type="text/javascript" src="<%=request.getContextPath()%>/script/temp_gallery.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/script/init.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/script/map-utils.js"></script>

        <style>
        .cell-title {
          font-weight: bold;
        }

        .cell-effort-driven {
          text-align: center;
        }
        </style>

        <style>
            #content table {
                display: table;
            }
        </style>


        <link rel="alternate" type="application/rdf+xml" title="RDF"
            href="${pageContext.request.contextPath}/species/${actionBean.idSpecies}/rdf" />
    </stripes:layout-component>

    <stripes:layout-component name="contents">
        <!-- MAIN CONTENT -->
        <c:choose>
            <c:when test="${eunis:exists(actionBean.factsheet)}">

                <c:if test="${not empty actionBean.n2000id}">
                    <div class="advice-msg">
                    Natura 2000 related content is now shown on the Biodiversity Information System for Europe, BISE.
                    Please go to <a href="https://biodiversity.europa.eu/species/${actionBean.idSpecies}">BISE</a> or use the <a href="https://natura2000.eea.europa.eu/">Natura 2000 map viewer for the latest information</a>.
                    </div>
                </c:if>

                <c:choose>
                    <c:when test="${actionBean.seniorSpecies != null}">
                        <stripes:layout-render name="/stripes/species-factsheet/synonym.jsp"/>
                    </c:when>
                    <c:otherwise>

                        <%-- Species breadcrumb --%>
                        <stripes:layout-render name="/stripes/species-factsheet/species-breadcrumb.jsp"/>

                        <h1><c:if test="${not empty actionBean.englishName}">${actionBean.englishName} -</c:if> <span class="italics">${actionBean.scientificName}</span> ${actionBean.author}
                            <c:if test="${actionBean.seniorSpecies != null}">
                                <span class="redirection-msg">&#8213; Synonym of <a href="${pageContext.request.contextPath}/species/${actionBean.seniorIdSpecies}"><strong>${actionBean.seniorSpecies }</strong></a></span>
                            </c:if>
                        </h1>

                        <%-- Quick facts --%>
                        <div>
                            <stripes:layout-render name="/stripes/species-factsheet/species-quickfacts.jsp"/>
                        </div>

                        <div>
                                <%--Accordion for the other data--%>
                            <div class="eea-accordion-panels non-exclusive collapsed-by-default">

                                    <%-- Areas where this species has been reported --%>
                                <div class="eea-accordion-panel" style="clear: both;" id="reported-accordion">
                                    <h2 class="notoc eea-icon-right-container">Distribution</h2>
                                    <div class="pane" id="distributionPane">
                                        The distribution map is currently disabled. A new map solution will soon become available. In the meantime, please consult other species distribution map providers listed in the Other resources panel below.
                                        <script>
                                            $("#reported-accordion").addClass("nodata");
                                        </script>
                                        <%--<stripes:layout-render name="/stripes/species-factsheet/species-reported.jsp"/>--%>
                                        <%--<script>--%>
                                            <%--addReloadOnDisplay("distributionPane", null, null, init);--%>
                                        <%--</script>--%>
                                    </div>
                                </div>

                                    <%-- Threat status and conservation status --%>
                                <div class="eea-accordion-panel" style="clear: both;" id="threat-accordion">
                                    <a id="threat_status" ></a>
                                    <h2 class="notoc eea-icon-right-container">Threat and <c:choose><c:when test="${not empty actionBean.art12status}">EU population</c:when><c:otherwise>conservation</c:otherwise></c:choose> status</h2>
                                    <div class="pane" id="speciesStatusPane">
                                        <stripes:layout-render name="/stripes/species-factsheet/species-status.jsp"/>
                                    </div>
                                </div>

                                    <%-- This species is being protected in Europe --%>
                                <div class="eea-accordion-panel" style="clear: both;" id="sites-accordion">
                                    <a id="protected" ></a>
                                    <h2 class="notoc eea-icon-right-container">Natura 2000 sites</h2>
                                    <div class="pane" id="speciesSitesPane">
                                        <stripes:layout-render name="/stripes/species-factsheet/species-sites.jsp"/>
                                    </div>
                                </div>

                                    <%-- Species is mentioned by the following legal instruments --%>
                                <div class="eea-accordion-panel" style="clear: both;" id="references-accordion">
                                    <a id="legal_status" ></a>
                                    <h2 class="notoc eea-icon-right-container">Legal status</h2>
                                    <div class="pane">
                                        <stripes:layout-render name="/stripes/species-factsheet/species-references.jsp"/>
                                    </div>
                                </div>

                                    <%-- Synonyms and common names --%>
                                <div class="eea-accordion-panel" style="clear: both;" id="synonyms-accordion">
                                    <h2 class="notoc eea-icon-right-container">Common names and synonyms</h2>
                                    <div class="pane">
                                        <stripes:layout-render name="/stripes/species-factsheet/species-synonyms.jsp"/>
                                    </div>
                                </div>

                                    <%--Other resources--%>
                                <div class="eea-accordion-panel" style="clear: both;" id="other-accordion">
                                    <a id="other_resources" ></a>
                                    <h2 class="notoc eea-icon-right-container">Other resources</h2>
                                    <div class="pane">
                                        <stripes:layout-render name="/stripes/species-factsheet/species-other-resources.jsp"/>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>

            </c:when>
            <c:otherwise>
                <div class="error-msg">${notExistErrMsg}</div>
            </c:otherwise>
        </c:choose>
        <!-- END MAIN CONTENT -->

    </stripes:layout-component>
</stripes:layout-render>
