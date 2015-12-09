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
    <div class="right-area quickfacts">
        <h4>${eunis:cmsPhrase(actionBean.contentManagement, 'Quick facts')}</h4>

        <table class="table-quickfacts">
            <tbody>
            <tr>
                <th>${eunis:cmsPhrase(actionBean.contentManagement, 'EU Habitats Directive')}</th>
                <td>
                    <c:if test="${not empty actionBean.legalInfo}"><a href="${ actionBean.pageUrl }#legal" onclick="openSection('legal');"></c:if>
                    <span class="bold">${eunis:cmsPhrase(actionBean.contentManagement, 'Annex I habitat type')}</span>
                    <c:if test="${not empty actionBean.legalInfo}"></a></c:if>
                    <c:if test="${not empty actionBean.factsheet.code2000}">
                        (code <span class="bold">${eunis:formatString(actionBean.factsheet.code2000, '')}</span>)
                    </c:if>
                </td>
            </tr>
            </tbody>

            <c:if test="${not empty actionBean.biogeoAssessmentRows}">
                <tbody>
                <tr>
                    <th rowspan="${eunis:getSize(actionBean.biogeoAssessmentRows)}">
                        EU conservation status <small>by biogeographical region</small>
                    </th>
                    <c:forEach items="${actionBean.biogeoAssessmentRows}" var="bg" varStatus="stat">
                <c:if test="${not stat.first}"><tr></c:if>
                    <td><span class="conclusion ${bg['code']}"></span> ${bg['region']} - <small>${bg['assessment']}</small></td>
                </tr>
                </c:forEach>
                </tbody>
            </c:if>
            <c:if test="${actionBean.factsheet.habitatLevel eq 3}">
                <tbody>
                <tr>
                    <th>Habitat type</th>
                    <td>
                        <span class="bold"><c:choose><c:when test="${!empty actionBean.factsheet.priority && actionBean.factsheet.priority == 1}">Priority</c:when><c:otherwise>Not priority</c:otherwise></c:choose></span>
                    </td>
                </tr>
                </tbody>
                <tbody>
                <tr>
                    <th>Natura 2000 sites</th>
                    <td>
                        <a href="${ actionBean.pageUrl }#sites" onclick="openSection('sites');"><span class="bold">${fn:length(actionBean.sites)}</span></a>
                        are designated for the habitat type
                    </td>
                </tr>
                </tbody>
            </c:if>

        </table>
    </div>
    <c:if test="${fn:length(actionBean.englishDescription)>=actionBean.descriptionThreshold}">
        <stripes:layout-render name="/stripes/habitats-factsheet/habitats-quickfacts-description.jsp"/>
    </c:if>

    <!-- END quick facts -->
</stripes:layout-definition>