<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>


    <h3>
            ${eunis:cmsPhrase(actionBean.contentManagement, 'Relation to vegetation types (syntaxa)')}
    </h3>
    <%----%>
<%--<c:choose>--%>
    <%--<c:when test="${not empty actionBean.habitatSintaxa}">--%>

    <%--<table summary=" ${eunis:cms(actionBean.contentManagement, 'habitat_type_syntaxa')}" class="listing fullwidth">--%>
        <%--<col style="width:25%"/>--%>
        <%--<col style="width:6%"/>--%>
        <%--<col style="width:30%"/>--%>
        <%--<col style="width:20%"/>--%>
        <%--<col style="width:14%"/>--%>
        <%--<thead>--%>
        <%--<tr>--%>
            <%--<th scope="col">--%>
                <%--${eunis:cmsPhrase(actionBean.contentManagement, 'Name')}--%>
            <%--</th>--%>
            <%--<th scope="col">--%>
                <%--${eunis:cmsPhrase(actionBean.contentManagement, 'Relation')}--%>
            <%--</th>--%>
            <%--<th scope="col">--%>
                <%--${eunis:cmsPhrase(actionBean.contentManagement, 'Source (abbreviated)')}--%>
            <%--</th>--%>
            <%--<th scope="col">--%>
                <%--${eunis:cmsPhrase(actionBean.contentManagement, 'Author')}--%>
            <%--</th>--%>
            <%--<th scope="col">--%>
                <%--${eunis:cmsPhrase(actionBean.contentManagement, 'References')}--%>
            <%--</th>--%>
        <%--</tr>--%>
        <%--</thead>--%>
        <%--<tbody>--%>
        <%----%>
        <%--<c:forEach items="${actionBean.habitatSintaxa}" var="syntaxa">--%>
            <%--<tr>--%>
                <%--<td>--%>
                    <%--${syntaxa.name}--%>
                <%--</td>--%>
                <%--<td>--%>
                    <%--${eunis:mapHabitatsRelations(syntaxa.relation)}--%>
                <%--</td>--%>
                <%--<td>--%>
                    <%--${syntaxa.sourceAbbrev}--%>
                <%--</td>--%>
                <%--<td>--%>
                    <%--${syntaxa.author}--%>
                <%--</td>--%>

                <%--<td>--%>
                    <%--<c:if test="${not (syntaxa.idDc eq 0)}">--%>
                        <%--<a href="references/${syntaxa.idDc}">${eunis:getAuthorAndUrlByIdDc(syntaxa.idDc)}</a>--%>
                    <%--</c:if>--%>
                <%--</td>--%>
            <%--</tr>--%>
        <%--</c:forEach>--%>
        <%--</tbody>--%>
    <%--</table>--%>
    <%--</c:when>--%>
    <%--<c:otherwise>--%>
        <%--${eunis:cmsPhrase(actionBean.contentManagement, 'Not available')}--%>
        <%--<script>--%>
            <%--$("#vegetation-accordion").addClass("nodata");--%>
        <%--</script>--%>
    <%--</c:otherwise>--%>
<%--</c:choose>--%>











    <c:choose>
        <c:when test="${not empty actionBean.syntaxaQueryResultCols && not empty actionBean.syntaxaQueryResultRows}">
            <div style="overflow-x:auto ">
                <display:table name="actionBean.syntaxaQueryResultRows" class="globalQuery ${actionBean.query} sortable listing" pagesize="100" sort="list" requestURI="${actionBean.urlBinding}">
                    <display:setProperty name="paging.banner.placement" value="none" />
                    <c:forEach var="cl" items="${actionBean.syntaxaQueryResultCols}">
                        <display:column
                                class="${cl.property}" property="${cl.property}" title="${cl.title}" sortable="${cl.sortable}" headerClass="${cl.property} nosort"
                                decorator="eionet.eunis.util.decorators.ForeignDataColumnDecorator"/>
                    </c:forEach>
                </display:table>
            </div>
            <c:if test="${not empty actionBean.syntaxaAttribution}">
                <b>Source:</b> ${actionBean.syntaxaAttribution}
            </c:if>
        </c:when>
        <c:otherwise>
            ${eunis:cmsPhrase(actionBean.contentManagement, 'Not available')}
            <script>
            $("#vegetation-accordion").addClass("nodata");
            </script>
        </c:otherwise>
    </c:choose>

</stripes:layout-definition>