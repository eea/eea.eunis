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
    <link rel="stylesheet" type="text/css" href="css/temp_gallery.css">
    <script type="text/javascript" src="<%=request.getContextPath()%>/script/temp_gallery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/script/init.js"></script>

    <script src="<%=request.getContextPath()%>/script/overlib.js" type="text/javascript"></script>
    <script>
        function openSection(sectionName) {
            if($('#' + sectionName + ' ~ h2').attr('class').indexOf('current')==-1)
                $('#' + sectionName + ' ~ h2').click();
        }
    </script>

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
            <stripes:layout-render name="/stripes/habitats-factsheet/redlist/habitats-quickfacts.jsp"/>


            <%--Accordion--%>
            <div>
                <div class="eea-accordion-panels non-exclusive collapsed-by-default">
                    <div class="eea-accordion-panel" style="clear: both;" id="summary-accordion">
                        <h2 class="notoc eea-icon-right-container">Summary</h2>
                        <div class="pane">
                            <stripes:layout-render name="/stripes/habitats-factsheet/redlist/habitats-summary.jsp"/>
                        </div>
                    </div>
                    <div class="eea-accordion-panel" style="clear: both;" id="threat-accordion">
                        <h2 class="notoc eea-icon-right-container">Threat status</h2>
                        <div class="pane">
                            <stripes:layout-render name="/stripes/habitats-factsheet/redlist/habitats-threat.jsp"/>
                        </div>
                    </div>
                    <div class="eea-accordion-panel" style="clear: both;" id="restoration-accordion">
                        <h2 class="notoc eea-icon-right-container">Habitat restoration potential</h2>
                        <div class="pane">
                            <stripes:layout-render name="/stripes/habitats-factsheet/redlist/habitats-restoration.jsp"/>
                        </div>
                    </div>
                    <div class="eea-accordion-panel" style="clear: both;" id="conservation-accordion">
                        <h2 class="notoc eea-icon-right-container">Conservation and management needs</h2>
                        <div class="pane">
                            <stripes:layout-render name="/stripes/habitats-factsheet/redlist/habitats-conservation.jsp"/>
                        </div>
                    </div>
                    <div class="eea-accordion-panel" style="clear: both;" id="distribution-accordion">
                        <h2 class="notoc eea-icon-right-container">Distribution</h2>
                        <div class="pane">
                            <stripes:layout-render name="/stripes/habitats-factsheet/redlist/habitats-distribution.jsp"/>
                        </div>
                    </div>
                    <div class="eea-accordion-panel" style="clear: both;" id="species-accordion">
                        <h2 class="notoc eea-icon-right-container">Characteristic species</h2>
                        <div class="pane">
                            <stripes:layout-render name="/stripes/habitats-factsheet/habitats-species.jsp"/>
                        </div>
                    </div>
                    <div class="eea-accordion-panel" style="clear: both;" id="legal-accordion">
                        <a id="legal"></a>
                        <h2 class="notoc eea-icon-right-container">Legal status</h2>
                        <div class="pane">
                            <stripes:layout-render name="/stripes/habitats-factsheet/habitats-legal.jsp"/>
                        </div>
                    </div>
                    <div class="eea-accordion-panel" style="clear: both;" id="vegetation-accordion">
                        <h2 class="notoc eea-icon-right-container">Vegetation types</h2>
                        <div class="pane">
                            <stripes:layout-render name="/stripes/habitats-factsheet/habitats-vegetation.jsp"/>
                        </div>
                    </div>
                    <div class="eea-accordion-panel" style="clear: both;" id="other-classifications-accordion">
                        <h2 class="notoc eea-icon-right-container">Other classifications</h2>
                        <div class="pane">
                            <stripes:layout-render name="/stripes/habitats-factsheet/redlist/habitats-other-classifications.jsp"/>
                        </div>
                    </div>

               </div>
           </div>
        </c:otherwise>
    </c:choose>
    <!-- END MAIN CONTENT -->
</stripes:layout-component>
<stripes:layout-component name="foot">
    <script src="<%=request.getContextPath()%>/script/species-controls.js" type="text/javascript"></script>
</stripes:layout-component>
</stripes:layout-render>
