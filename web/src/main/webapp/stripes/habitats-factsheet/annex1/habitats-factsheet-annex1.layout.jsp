<%@page contentType="text/html;charset=UTF-8"%>

<%@ include file="/stripes/common/taglibs.jsp"%>
<%
    String btrail = "eea#" + application.getInitParameter( "EEA_HOME" ) + ",home#index.jsp,habitat_types";
%>
<stripes:layout-render name="/stripes/common/template.jsp" pageTitle="${actionBean.pageTitle }" bookmarkPageName="habitats" btrail="<%= btrail %>">
    <stripes:layout-component name="head">
        <c:if test="${!empty actionBean.factsheet}">
            <link rel="alternate" type="application/rdf+xml" title="RDF" href="${pageContext.request.contextPath}/habitats/${actionBean.idHabitat}/rdf" />
        </c:if>

        <script src="<%=request.getContextPath()%>/script/overlib.js" type="text/javascript"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/script/init.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/script/map-utils.js"></script>

        <script>
            function openSection(sectionName) {
                if($('#' + sectionName + ' ~ h2').attr('class').indexOf('current')==-1)
                    $('#' + sectionName + ' ~ h2').click();
            }
        </script>
        <%--for distribution section--%>
        <link rel="stylesheet" type="text/css" href="${actionBean.context.distributionArcgisCSS}"/>
        <script type="text/javascript" src="${actionBean.context.distributionArcgisScript}"></script>
    </stripes:layout-component>
    <stripes:layout-component name="contents">

        <!-- MAIN CONTENT -->
        <c:choose>
            <c:when test="${actionBean.factsheet.habitat == null}">
                <div class="error-msg">
                    ${eunis:cmsPhrase(actionBean.contentManagement, 'Sorry, no habitat type has been found in the database')}
                </div>
            </c:when>
            <c:otherwise>

                <stripes:layout-render name="/stripes/habitats-factsheet/habitats-breadcrumb.jsp"/>

                <h1>${eunis:bracketsToItalics(eunis:replaceTags(actionBean.factsheet.habitatScientificName))}</h1>

                <%--Quick facts--%>
                <stripes:layout-render name="/stripes/habitats-factsheet/annex1/habitats-quickfacts.jsp"/>

                <%--Accordion--%>
                <div>
                    <div class="eea-accordion-panels non-exclusive collapsed-by-default">
                        <div class="eea-accordion-panel" style="clear: both;" id="distribution-accordion">
                            <h2 class="notoc eea-icon-right-container">Distribution</h2>
                            <div class="pane" id="distributionPane">
                                The distribution map is currently disabled. A new map solution will soon become available. In the meantime, please consult other species distribution map providers listed in the Other resources panel below.
                                <script>
                                    $("#distribution-accordion").addClass("nodata");
                                </script>
                                <%--<stripes:layout-render name="/stripes/habitats-factsheet/annex1/habitats-distribution.jsp"/>--%>
                                <%--<script>--%>
                                    <%--addReloadOnDisplay("distributionPane", null, null, init);--%>
                                <%--</script>--%>
                            </div>
                        </div>
                        <div class="eea-accordion-panel" style="clear: both;" id="conservation-accordion">
                            <h2 class="notoc eea-icon-right-container">Conservation status</h2>
                            <div class="pane" id="conservationPane">
                                <stripes:layout-render name="/stripes/habitats-factsheet/annex1/habitats-conservation.jsp"/>
                            </div>
                        </div>
                        <div class="eea-accordion-panel" style="clear: both;" id="species-accordion">
                            <a id="species" ></a>
                            <h2 class="notoc eea-icon-right-container">Species mentioned in habitat description</h2>
                            <div class="pane">
                                <stripes:layout-render name="/stripes/habitats-factsheet/habitats-species.jsp"/>
                            </div>
                        </div>
                        <div class="eea-accordion-panel" style="clear: both;" id="sites-accordion">
                            <a id="sites" ></a>
                            <h2 class="notoc eea-icon-right-container">Natura 2000 sites</h2>
                            <div class="pane" id="habitatsSitesPane">
                                <stripes:layout-render name="/stripes/habitats-factsheet/annex1/habitats-sites.jsp"/>
                            </div>
                        </div>
                        <div class="eea-accordion-panel" style="clear: both;" id="legal-accordion">
                            <a id="legal" ></a>
                            <h2 class="notoc eea-icon-right-container">Legal status</h2>
                            <div class="pane">
                                <stripes:layout-render name="/stripes/habitats-factsheet/habitats-legal.jsp"/>
                            </div>
                        </div>
                        <div class="eea-accordion-panel" style="clear: both;" id="other-classifications-accordion">
                            <h2 class="notoc eea-icon-right-container">Relation to habitat classifications</h2>
                            <div class="pane">
                                <stripes:layout-render name="/stripes/habitats-factsheet/habitats-other-classifications.jsp"/>
                            </div>
                        </div>
                        <div class="eea-accordion-panel" style="clear: both;" id="other-resources-accordion">
                            <h2 class="notoc eea-icon-right-container">Other resources</h2>
                            <div class="pane">
                                <stripes:layout-render name="/stripes/habitats-factsheet/habitats-other-resources.jsp"/>
                            </div>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
        <!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>
