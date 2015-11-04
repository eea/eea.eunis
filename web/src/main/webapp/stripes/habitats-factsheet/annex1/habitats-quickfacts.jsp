<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
    <!-- quick facts -->

    <!--  Description on the left -->
    <div class="left-area">
        <div style="margin-left: 5px;">
            <c:if test="${fn:length(actionBean.englishDescription)<actionBean.descriptionThreshold}">
                <stripes:layout-render name="/stripes/habitats-factsheet/habitats-quickfacts-description.jsp"/>
            </c:if>
        </div>
    </div>

    <!-- Textual facts on right -->
    <div class="right-area">
        <h4>${eunis:cmsPhrase(actionBean.contentManagement, 'Quick facts')}</h4>
        <div>
            <ul>
                <li>
                    ${eunis:cmsPhrase(actionBean.contentManagement, 'EU Habitats Directive')}
                    <c:if test="${not empty actionBean.legalInfo}"><a href="${ actionBean.pageUrl }#legal" onclick="openSection('legal');"></c:if>
                        <span class="bold">${eunis:cmsPhrase(actionBean.contentManagement, 'Annex I habitat type')}</span>
                    <c:if test="${not empty actionBean.legalInfo}"></a></c:if>
                    <c:if test="${not empty actionBean.factsheet.code2000}">
                        (code <span class="bold">${eunis:formatString(actionBean.factsheet.code2000, '')}</span>)
                    </c:if>
                </li>
                <c:if test="${actionBean.factsheet.habitatLevel eq 3}">
                    <li>
                        Priority habitat type: <span class="bold"><c:choose><c:when test="${!empty actionBean.factsheet.priority && actionBean.factsheet.priority == 1}">Yes</c:when><c:otherwise>No</c:otherwise></c:choose></span>
                    </li>
                    <li>
                        Protected in
                        <a href="${ actionBean.pageUrl }#sites" onclick="openSection('sites');"><span class="bold">${fn:length(actionBean.sites)}</span></a>
                         Natura 2000 sites
                    </li>
                </c:if>
                <c:if test="${not empty actionBean.uniqueEcosystems}">
                    <li>Occurs in
                    <c:forEach var="ecosystem" items="${actionBean.uniqueEcosystems}" varStatus="estatus"><c:if test="${not estatus.last and not estatus.first}">, </c:if><c:if test="${estatus.last and not estatus.first}"> and </c:if>${ecosystem.ecoName}</c:forEach>
                    ecosystems</li>
                </c:if>
            </ul>
            <br/>
        </div>
    </div>
    <c:if test="${fn:length(actionBean.englishDescription)>=actionBean.descriptionThreshold}">
        <stripes:layout-render name="/stripes/habitats-factsheet/habitats-quickfacts-description.jsp"/>
    </c:if>

    <!-- END quick facts -->
</stripes:layout-definition>