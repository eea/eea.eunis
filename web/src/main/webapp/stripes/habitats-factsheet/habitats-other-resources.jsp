<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
    <%--<h3>${eunis:cmsPhrase(actionBean.contentManagement, 'External links')}</h3>--%>
    <div id="linkcollection">
        <c:if test="${!empty actionBean.art17link}">
            <div>
                <a href="${actionBean.art17link}">${eunis:cmsPhrase(actionBean.contentManagement, 'Article 17 Summary')}</a>
            </div>
        </c:if>
        <c:if test="${!empty actionBean.factsheet.code2000}">
            <div>
                <a href="http://natura2000.eea.europa.eu/#annexICode=${actionBean.factsheet.code2000}">${eunis:cmsPhrase(actionBean.contentManagement, 'Natura2000 mapviewer')}</a>
            </div>
        </c:if>
        <c:forEach items="${actionBean.links}" var="link" varStatus="loop">
            <div>
                <c:choose>
                    <c:when test="${!empty link.url}">
                        <a href="${eunis:treatURLSpecialCharacters(link.url)}">
                            <c:out value="${link.name}"/>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <c:out value="${link.name}"/>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:forEach>
    </div>

    <div>
        <stripes:layout-render name="/stripes/species-factsheet/species-linkeddata.jsp"/>
    </div>
</stripes:layout-definition>