<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>

    <c:choose>
        <c:when test="${(not empty actionBean.legalInfo) or (not empty actionBean.legalMentionedIn)}">
            <c:if test="${not empty actionBean.legalMentionedIn}">
                <h3>
                    ${eunis:cmsPhrase(actionBean.contentManagement, 'Mentioned in the following international legal instruments and agreements')}
                </h3>
                <table summary="${eunis:cms(actionBean.contentManagement, 'habitat_type_legal_instruments')}" class="listing fullwidth">
                    <thead>
                    <tr>
                        <th width="30%" style="text-align: left;" class="nosort">
                                ${eunis:cmsPhrase(actionBean.contentManagement, 'Legal text')}
                        </th>
                        <th width="30%" style="text-align: left;" class="nosort">
                                ${eunis:cmsPhrase(actionBean.contentManagement, 'Annex')}
                        </th>
                        <th width="20%" style="text-align: left;" class="nosort">
                                ${eunis:cmsPhrase(actionBean.contentManagement, 'More information')}
                        </th>
                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach items="${actionBean.legalMentionedIn}" var="legal">
                        <tr>
                            <td>
                            <c:choose>
                                <c:when test="${not empty legal.parent.url}"><a href="${legal.parent.url}"></c:when>
                                <c:otherwise><a href="/references/${legal.parent.idDc}"></c:otherwise>
                            </c:choose>


                            ${legal.parent.title}</a> <c:if test="${not empty legal.parent.alternative}">(${legal.parent.alternative})</c:if></td>
                            <td>
                                <a href="/references/${legal.annex.idDc}/habitats">
                                        ${legal.annex.title}
                                </a>
                            </td>
                            <td>
                                <c:forEach var="link" items="${legal.moreInfo}" varStatus="status">
                                    <c:choose>
                                        <c:when test="${empty link.linkText}">
                                            <a href="${ link.link }">${eunis:shortenURL(link.link)}</a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${ link.link }">${link.linkText}</a>
                                        </c:otherwise>
                                    </c:choose>
                                    <c:if test="${not status.last}"><br><br></c:if>
                                </c:forEach>

                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>

            <c:if test="${not empty actionBean.legalRelationTo}">
                <h3>
                        ${eunis:cmsPhrase(actionBean.contentManagement, 'Relation to other habitat types mentioned in legal instruments')}
                </h3>
                <table summary="${eunis:cms(actionBean.contentManagement, 'habitat_type_legal_instruments')}" class="listing fullwidth">
                    <thead>
                    <tr>
                        <th width="30%" style="text-align: left;" class="nosort">
                                ${eunis:cmsPhrase(actionBean.contentManagement, 'Legal text')}
                        </th>
                        <th width="30%" style="text-align: left;" class="nosort">
                                ${eunis:cmsPhrase(actionBean.contentManagement, 'Annex')}
                        </th>
                        <th width="50%" style="text-align: left;" class="nosort">
                                ${eunis:cmsPhrase(actionBean.contentManagement, 'Name in legal text')}
                        </th>
                        <th width="20%" style="text-align: left;" class="nosort">
                                ${eunis:cmsPhrase(actionBean.contentManagement, 'Code in legal text')}
                        </th>
                        <th width="20%" style="text-align: left;" class="nosort">
                                ${eunis:cmsPhrase(actionBean.contentManagement, 'Habitat type<br> relationship')}
                        </th>
                        <th width="20%" style="text-align: left;" class="nosort">
                                ${eunis:cmsPhrase(actionBean.contentManagement, 'More information')}
                        </th>
                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach items="${actionBean.legalRelationTo}" var="legal">
                        <tr>
                            <td><a href="${legal.parentLink}">${legal.parentTitle}</a></td>
                            <td>
                                <a href="/references/${legal.legalPersist.idDc}/habitats">
                                        ${legal.annexTitle}
                                </a>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty legal.legalPersist.relatedIdHabitat}">
                                        <a href="/habitats/${legal.legalPersist.relatedIdHabitat}">
                                            ${legal.legalPersist.title}
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        ${legal.legalPersist.title}
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty legal.legalPersist.relatedIdHabitat}">
                                        <a href="/habitats/${legal.legalPersist.relatedIdHabitat}">
                                            ${legal.legalPersist.code}
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        ${legal.legalPersist.code}
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${legal.relationTypeString}</td>
                            <td>
                                <c:forEach var="link" items="${legal.moreInfo}" varStatus="status">
                                    <c:if test="${not (oldLink eq link)}">
                                        <c:choose>
                                            <c:when test="${empty link.linkText}">
                                                <a href="${ link.link }">${eunis:shortenURL(link.link)}</a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${ link.link }">${link.linkText}</a>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${not status.last}"><br><br></c:if>
                                    </c:if>
                                    <c:set var="oldLink" value="${link}"/>
                                </c:forEach>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </c:when>
        <c:otherwise>
            ${eunis:cmsPhrase(actionBean.contentManagement, 'Not available')}
            <script>
                $("#legal-accordion").addClass("nodata");
            </script>
        </c:otherwise>
    </c:choose>

</stripes:layout-definition>