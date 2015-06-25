<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>


    <h3>
            ${eunis:cmsPhrase(actionBean.contentManagement, 'Relation to vegetation types (syntaxa)')}
    </h3>

    <c:choose>
        <c:when test="${not empty actionBean.syntaxaQueryResultCols && not empty actionBean.syntaxaQueryResultRows}">
            <div style="overflow-x:auto ">
                <display:table name="actionBean.syntaxaQueryResultRows" class="globalQuery ${actionBean.query} sortable listing" pagesize="100" sort="list" requestURI="${actionBean.urlBinding}">
                    <display:setProperty name="paging.banner.placement" value="none" />
                    <c:forEach var="cl" items="${actionBean.syntaxaQueryResultCols}">
                        <display:column
                                class="${cl.property}" property="${cl.property}" title="${cl.title}" sortable="false" headerClass="${cl.property} nosort"
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