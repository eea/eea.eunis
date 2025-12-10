<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
<!-- Species synonyms and common names -->

<%--<h2 class="visualClear" id="synonyms">${eunis:cmsPhrase(actionBean.contentManagement, 'Common names and synonyms of ')} <span class="italics">${actionBean.scientificName}</span></h2>--%>

<div class="left-area species">
    <%--Common names--%>
    <c:choose>
    <c:when test="${actionBean.vernNamesCount>0}">
        <div class="scroll-auto" style="height: 300px">
            <table summary="Common names" class="listing fullwidth">
                <thead>
                <tr>
                    <th scope="col" style="cursor: pointer;"><img
                            src="/template/arrowBlank.gif"
                            height="6" width="9">
                            ${eunis:cmsPhrase(actionBean.contentManagement, 'Common Name')}
                            ${eunis:cmsTitle(actionBean.contentManagement, 'sort_results_on_this_column')}

                        <img src="/template/arrowUp.gif"
                             height="6" width="9"></th>
                    <th scope="col" style="cursor: pointer;"><img
                            src="/template/arrowBlank.gif"
                            height="6" width="9">
                            ${eunis:cmsPhrase(actionBean.contentManagement, 'Language')}
                            ${eunis:cmsTitle(actionBean.contentManagement, 'sort_results_on_this_column')}

                        <img src="/template/arrowBlank.gif"
                             height="6" width="9"></th>
                    <th scope="col" style="cursor: pointer;"><img
                            src="/template/arrowBlank.gif"
                            height="6" width="9">
                            ${eunis:cmsPhrase(actionBean.contentManagement, 'Reference')}
                            ${eunis:cmsTitle(actionBean.contentManagement, 'sort_results_on_this_column')}

                        <img src="/template/arrowBlank.gif"
                             height="6" width="9"></th>
                </tr>
                </thead>
                <tbody>

                <c:forEach items="${actionBean.vernNames}" var="vern" varStatus="loop">
                    <c:set var="ref" value="-1"></c:set>
                    <c:if test="${!empty vern.idDc}">
                        <c:set var="ref" value="${vern.idDc}"></c:set>
                    </c:if>
                    <tr>
                        <td xml:lang="${vern.languageCode}">
                                ${eunis:treatURLSpecialCharacters(vern.name)}
                        </td>
                        <td>
                                ${vern.language}
                        </td>
                        <td>
                            <a class="link-plain" href="references/${ref}">${eunis:getAuthorAndUrlByIdDc(ref)}</a>
                        </td>
                    </tr>
                </c:forEach>

                </tbody>
            </table>
    </div>
    </c:when>
        <c:otherwise>
            ${eunis:cmsPhrase(actionBean.contentManagement, 'The species has no common names')}
        </c:otherwise>
    </c:choose>
</div>

<div class="right-area quickfacts">

    <%--Synonyms--%>
    <c:choose>
    <c:when test="${actionBean.synonymsCount>0}">
        <div class="scroll-auto" style="height: 300px">
            <table summary="List of synonyms" class="listing fullwidth">
                <colgroup>
                    <col style="width:40%">
                    <col style="width:40%">
                </colgroup>
                <thead>
                <tr>
                    <th scope="col" style="cursor: pointer;"><img src="/template/arrowBlank.gif" height="6" width="9">
                            ${eunis:cmsPhrase(actionBean.contentManagement, 'Synonym')}
                            ${eunis:cmsTitle(actionBean.contentManagement, 'sort_by_column')}

                        <img src="/template/arrowUp.gif" height="6" width="9"></th>
                    <th scope="col" style="cursor: pointer;"><img src="/template/arrowBlank.gif" height="6" width="9">
                            ${eunis:cmsPhrase(actionBean.contentManagement, 'Author')}
                            ${eunis:cmsTitle(actionBean.contentManagement, 'sort_by_column')}
                        <img src="/template/arrowBlank.gif" height="6" width="9"></th>
                </tr>
                </thead>
                <tbody>

                <c:forEach items="${actionBean.factsheet.synonymsIterator}" var="synonym" varStatus="loop">
                    <tr>
                        <td>
                            <a href="${pageContext.request.contextPath}/species/${synonym.idSpecies}">${eunis:treatURLSpecialCharacters(synonym.scientificName)}</a>
                        </td>
                        <td>
                                ${eunis:treatURLSpecialCharacters(synonym.author)}
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:when>
        <c:otherwise>
            ${eunis:cmsPhrase(actionBean.contentManagement, 'No synonyms available.')}
        </c:otherwise>
    </c:choose>

</div>

<c:if test="${actionBean.synonymsCount eq 0 and actionBean.vernNamesCount eq 0}">
    <script>
        $("#synonyms-accordion").addClass("nodata");
    </script>
</c:if>

<!-- END synonyms -->
</stripes:layout-definition>