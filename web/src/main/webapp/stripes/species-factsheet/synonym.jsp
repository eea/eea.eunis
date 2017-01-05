<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>


    <h1>
        <c:if test="${not empty actionBean.englishName}">${actionBean.englishName} -</c:if> <span class="italics">${actionBean.scientificName}</span> ${actionBean.author}
        <span class="redirection-msg">&#8213; Synonym of <a href="${pageContext.request.contextPath}/species/${actionBean.seniorIdSpecies}"><strong>${actionBean.seniorSpecies }</strong></a></span>
    </h1>

    <c:if test="${fn:length(actionBean.parentLegal) gt 0}">
        Mentioned in the following international legal instruments and agreements:
        <ul>
            <c:set var="lastLabel" value="${''}"/>
            <c:forEach var="legal" items="${actionBean.parentLegal}">
                <c:choose>
                    <c:when test="${not empty legal.parentAlternative}">
                        <c:set var="currentLabel" value="${legal.parentAlternative}"/>
                    </c:when>
                    <c:otherwise><c:set var="currentLabel" value="${legal.parentName}"/></c:otherwise>
                </c:choose>

                    <c:if test="${lastLabel != currentLabel}">
                        <c:if test="${not empty lastLabel}">
                            </ul></li>
                        </c:if>
                        <li>
                        ${currentLabel}
                        <ul>
                        <c:set var="lastLabel" value="${currentLabel}"/>
                    </c:if>


                    <c:if test="${not empty legal.detailedReference}">
                        <li>${legal.detailedReference}</li>
                    </c:if>
                </li>
            </c:forEach>
        </ul></li></ul>

    </c:if>
    <c:if test="${not empty actionBean.n2000id}">
        <p>Natura 2000 code: <span class="bold">${actionBean.n2000id}</span></p>
    </c:if>

    All the information associated to this invalid name can be found on the page of the species or subspecies with the valid name.
</stripes:layout-definition>