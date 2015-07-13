<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>

<stripes:layout-definition>
    <!-- detailed reference -->
    <a name="legal-instruments"></a>
    <%--<h2 class="visualClear" id="legal-status">Legal status</h2>--%>

        <c:choose>
            <c:when test="${fn:length(actionBean.legalStatuses) gt 0}">
                <div class='detailed-reference'>
                    <h3 class="visualClear" id="legal-instruments">${eunis:cmsPhrase(actionBean.contentManagement, 'Mentioned in the following international legal instruments and agreements')}</h3>

                    <table summary="List of legal instruments"
                       class="listing fullwidth">
                        <thead>
                        <tr>
                            <th scope="col" style="cursor: pointer;" class="nosort">
                                ${eunis:cmsPhrase(actionBean.contentManagement, 'Legal text')}
                                </th>
                            <th scope="col" style="cursor: pointer;" class="nosort">
                                    ${eunis:cmsPhrase(actionBean.contentManagement, 'Annex')}
                              </th>
                            <th scope="col" style="cursor: pointer;" class="nosort">
                                ${eunis:cmsPhrase(actionBean.contentManagement, 'Geographical and other restrictions')}
                             </th>
                            <th scope="col" style="cursor: pointer;" class="nosort">
                                ${eunis:cmsPhrase(actionBean.contentManagement, 'More information')}
                            </th>
                        </tr>
                        </thead>
                        <tbody>

                        <c:set var="oldParent" value="${''}"/>
                        <c:set var="oldLink" value="${''}"/>
						<c:forEach items="${actionBean.legalStatuses}" var="legal" varStatus="loop">

							<tr>
		        				<td>
                                    <c:if test="${oldParent != legal.parentName}">
    		          					<a href="${legal.parentUrl}">${ legal.parentName }</a>
    		          					<c:if test="${not empty legal.parentAlternative}">(${legal.parentAlternative})</c:if>
                                    </c:if>
                                    <c:set var="oldParent" value="${legal.parentName}"/>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty legal.description}">
                                            <a href="/references/${ legal.idDc }/species">${ legal.description }</a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="/references/${ legal.idDc }/species">${legal.detailedReference}</a>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
		        				<td>
		              				${ eunis:bracketsToItalics(legal.comments) }
		        				</td>
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
                    
                  <%--<p>See also <a href="${ actionBean.unepWcmcPageLink }">${eunis:cmsPhrase(actionBean.contentManagement, 'UNEP-WCMC page')}</a>--%>
                  <%--</p>--%>
                </div>
                <!-- END detailed reference -->
            </c:when>
            <c:otherwise>
                ${eunis:cmsPhrase(actionBean.contentManagement, 'Not listed in legal texts')}
                <script>
                    $("#references-accordion").addClass("nodata");
                </script>
            </c:otherwise>
        </c:choose>
</stripes:layout-definition>
