<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
    <!-- quick facts -->

    <style>
        .quickfacts table {
            width: 100%;
        }
        .quickfacts table td,
        .quickfacts table th {
            vertical-align: top;
            padding: .5em 0;
        }
        .quickfacts tbody {
            border-top: 1px solid #ececec;
        }
        .quickfacts table td {
            padding-left: 1em;
            padding-bottom: 0;
        }
        .quickfacts tr:last-child td {
            padding-bottom: .5em;
        }
        .quickfacts table th {
            width: 14em;
        }
        .quickfacts table h6 {
            margin: 0;
        }
        .quickfacts table ul {
            margin: 0;
            margin-left: .5em;
        }
        .quickfacts table small {
            font-size: inherit;
            font-style: italic;
            font-weight: normal;
            color: gray;
        }

        /* CONCLUSION */
        .conclusion {
            display: inline-block;
            *zoom: 1;
            padding: 1px 2px;
            margin: -2px 0;
            text-align: center;
            border-color: transparent;
            border-width: 1px;
            border-style: solid;
            border-radius: 2px;
            min-height: 1em;
            min-width: 1em;
        }
        /* CONCLUSION TYPES */
        .FV .conclusion,
        .FV.conclusion {
            color: #000;
            border-color: #639500;
            background-color: #9aca3b;
        }
        .FVU .conclusion,
        .FVU.conclusion {
            color: #000;
            border-color: #639500;
            background-color: #9aca3b;
        }
        .U1 .conclusion,
        .U1.conclusion {
            color: #000;
            border-color: #b05f00;
            background-color: #e69800;
        }
        .U1U .conclusion,
        .U1U.conclusion {
            color: #000;
            border-color: #b05f00;
            background-color: #e69800;
        }
        .U1M .conclusion,
        .U1M.conclusion {
            color: #000;
            border-color: #b05f00;
            background-color: #e69800;
        }
        .U1P .conclusion,
        .U1P.conclusion {
            color: #000;
            border-color: #b05f00;
            background-color: #e69800;
        }
        .U2 .conclusion,
        .U2.conclusion {
            color: #000;
            border-color: #742c2c;
            background-color: #eb6847;
        }
        .U2U .conclusion,
        .U2U.conclusion {
            color: #000;
            border-color: #742c2c;
            background-color: #eb6847;
        }
        .U2M .conclusion,
        .U2M.conclusion {
            color: #000;
            border-color: #742c2c;
            background-color: #eb6847;
        }
        .U2P .conclusion,
        .U2P.conclusion {
            color: #000;
            border-color: #742c2c;
            background-color: #eb6847;
        }
        .XX .conclusion,
        .XX.conclusion {
            color: #000;
            border-color: #888;
            background-color: #b4b4b4;
        }
        .XXU .conclusion,
        .XXU.conclusion {
            color: #000;
            border-color: #888;
            background-color: #b4b4b4;
        }
        .XU .conclusion,
        .XU.conclusion {
            color: #000;
            border-color: #888;
            background-color: #b4b4b4;
        }
        .XUU .conclusion,
        .XUU.conclusion {
            color: #000;
            border-color: #888;
            background-color: #b4b4b4;
        }
        .NA .conclusion,
        .NA.conclusion {
            color: #000;
            border-color: #4774c7;
            background-color: #78bbee;
        }
        .assesm {
            color: #666666 !important;
            font-style: italic;
        }

    </style>

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
                        designated for the habitat type
                    </td>
                </tr>
                </tbody>
            </c:if>
            <%--<c:if test="${not empty actionBean.uniqueEcosystems}">--%>
                <%--<tbody>--%>
                <%--<th>Occurs in</th>--%>
                <%--<td>--%>
                    <%--<c:forEach var="ecosystem" items="${actionBean.uniqueEcosystems}" varStatus="estatus"><c:if test="${not estatus.last and not estatus.first}">, </c:if><c:if test="${estatus.last and not estatus.first}"> and </c:if>${ecosystem.ecoName}</c:forEach>--%>
                    <%--ecosystems--%>
                <%--</td>--%>
                <%--</tbody>--%>
            <%--</c:if>--%>

        </table>
    </div>
    <c:if test="${fn:length(actionBean.englishDescription)>=actionBean.descriptionThreshold}">
        <stripes:layout-render name="/stripes/habitats-factsheet/habitats-quickfacts-description.jsp"/>
    </c:if>

    <!-- END quick facts -->
</stripes:layout-definition>