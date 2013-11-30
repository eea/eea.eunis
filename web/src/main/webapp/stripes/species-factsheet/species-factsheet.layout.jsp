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
<stripes:layout-render name="/stripes/common/template.jsp" hideMenu="true" pageTitle="${title}">
    <stripes:layout-component name="head">
        <!-- Custom js needed for Species page -->
        <script type="text/javascript" src="<%=request.getContextPath()%>/script/init.js"></script>

        <style>
        .cell-title {
          font-weight: bold;
        }

        .cell-effort-driven {
          text-align: center;
        }
        </style>


        <link rel="alternate" type="application/rdf+xml" title="RDF"
            href="${pageContext.request.contextPath}/species/${actionBean.idSpecies}/rdf" />
    </stripes:layout-component>

    <stripes:layout-component name="contents">
        <!-- MAIN CONTENT -->
        <c:choose>
            <c:when test="${eunis:exists(actionBean.factsheet)}">

<!-- TODO documentActions - PDF link ?? -->
<!-- TODO the old template added "Upload pictures" menu item to the left menu for logged in users -->

                <%-- Species breadcrumb --%>
                <stripes:layout-render name="/stripes/species-factsheet/species-breadcrumb.jsp"/>

                <h1>Species: <c:if test="${not empty actionBean.englishName}">${actionBean.englishName} -</c:if> <span class="italics">${actionBean.scientificName}</span> ${actionBean.author}
                    <c:if test="${actionBean.seniorSpecies != null}">
                        <span class="redirection-msg">&#8213; Synonym of <a href="${pageContext.request.contextPath}/species/${actionBean.seniorIdSpecies}"><strong>${actionBean.seniorSpecies }</strong></a></span>
                    </c:if>
                </h1>

                <%-- Quick facts --%>
                <stripes:layout-render name="/stripes/species-factsheet/species-quickfacts.jsp"/>

                <%-- Areas where this species has been reported --%>
                <stripes:layout-render name="/stripes/species-factsheet/species-reported.jsp"/>

                <%-- Threat status and conservation status --%>
                <stripes:layout-render name="/stripes/species-factsheet/species-status.jsp"/>

                <%-- This species is being protected in Europe --%>
                <stripes:layout-render name="/stripes/species-factsheet/species-sites.jsp"/>

                <%-- Species is mentioned by the following legal instruments --%>
                <stripes:layout-render name="/stripes/species-factsheet/species-references.jsp"/>


            </c:when>
            <c:otherwise>
                <div class="error-msg">${notExistErrMsg}</div>
            </c:otherwise>
        </c:choose>
        <!-- END MAIN CONTENT -->

    </stripes:layout-component>
</stripes:layout-render>
