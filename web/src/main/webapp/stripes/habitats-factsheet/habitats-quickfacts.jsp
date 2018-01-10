<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
    <!-- quick facts -->
    <!--  Description on the left -->
    <div class="left-area">
        <div style="margin-left: 5px;">
            <c:if test="${not actionBean.factsheet.eunis2017}">
                    ${eunis:cmsPhrase(actionBean.contentManagement, 'English name')}:
                <span class="bold">${eunis:bracketsToItalics(eunis:treatURLSpecialCharacters(actionBean.factsheet.habitatDescription))}</span>
            </c:if>
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
                    <th>${eunis:cmsPhrase(actionBean.contentManagement, 'EUNIS habitat type')}</th>
                    <td>code <span class="bold">${eunis:formatString(actionBean.factsheet.eunisHabitatCode, '')}</span></td>
                </tr>
            </tbody>

            <c:if test="${actionBean.resolution4}">
                <tbody>
                <tr>
                    <th>Bern Convention</th>
                    <td>
                        <a href="${ actionBean.pageUrl }#legal" onclick="openSection('legal');"><span class="bold">Resolution 4 habitat type</span></a> (used for designation of Emerald sites)
                    </td>
                </tr>
                </tbody>
            </c:if>
            <c:if test="${not empty actionBean.resolution4Parent}">
                <tbody>
                    <tr>
                        <th>Bern Convention</th>
                        <td>
                            Included in a <span class="bold">Resolution 4 habitat type</span> at a higher level (<a href="/habitats/${actionBean.resolution4Parent.idHabitat}">${actionBean.resolution4Parent.eunisHabitatCode}</a>)
                        </td>
                    </tr>
                </tbody>
            </c:if>

            <c:if test="${actionBean.habitatsDirective}">
                <tbody>
                    <tr>
                        <th class="normalfont">
                            Relation to
                        </th>
                        <td>
                            <a href="${ actionBean.pageUrl }#legal" onclick="openSection('legal');"><span class="bold">Annex I habitat types</span></a> (EU Habitats Directive)
                        </td>
                    </tr>
                </tbody>
            </c:if>


            <c:if test="${actionBean.factsheet.eunis2017 and actionBean.resolution4Relation}">
                <tbody>
                <tr>
                    <th class="normalfont">Relation to</th>
                    <td>
                        <a href="${ actionBean.pageUrl }#legal" onclick="openSection('legal');"><span class="bold">Resolution 4 habitat type</span></a> (used for designation of Emerald sites)
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