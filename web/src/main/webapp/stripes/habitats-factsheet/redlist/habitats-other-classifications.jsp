<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
    <c:choose>
    <c:when test="${!empty actionBean.otherClassifications}">
        This habitat may be equivalent to, or broather than, or narrower than the habitats or ecosystems in the following typologies.
        <table class="listing fullwidth" style="display: table">
            <col style="width:30%"/>
            <col style="width:15%"/>
            <col style="width:40%"/>
            <col style="width:15%"/>
            <thead>
            <tr>
                <th scope="col">
                        ${eunis:cmsPhrase(actionBean.contentManagement, 'Classification')}
                </th>
                <th scope="col">
                        ${eunis:cmsPhrase(actionBean.contentManagement, 'Code')}
                </th>
                <th scope="col">
                        ${eunis:cmsPhrase(actionBean.contentManagement, 'Habitat type name')}
                </th>
                <th scope="col">
                        ${eunis:cmsPhrase(actionBean.contentManagement, 'Relationship type')}
                </th>
            </tr>
            </thead>
            <tbody>
                <c:forEach items="${actionBean.otherClassifications}" var="classif" varStatus="loop">
                    <tr>
                        <td>
                            <a href="/references/${classif.idDc}">
                                ${classif.name}
                            </a>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${classif.idDc eq 2356}">
                                    <a href="/habitats_codeEUNIS/${classif.code}">
                                            ${eunis:formatString(classif.code, '&nbsp;')}
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    ${eunis:formatString(classif.code, '&nbsp;')}
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                                ${eunis:bracketsToItalics(eunis:replaceTags(eunis:formatString(classif.title, '&nbsp;')))}
                        </td>
                        <td>
                                ${eunis:execMethodParamString('ro.finsiel.eunis.factsheet.habitats.HabitatsFactsheet', 'mapHabitatsRelations', classif.relationType)}
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        ${eunis:cmsPhrase(actionBean.contentManagement, 'Not available')}
        <script>
            $("#other-classifications-accordion").addClass("nodata");
        </script>
    </c:otherwise>
    </c:choose>

</stripes:layout-definition>