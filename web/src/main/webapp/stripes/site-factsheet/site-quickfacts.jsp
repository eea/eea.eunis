<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
	<div class="right-area quickfacts">
		<h4>${eunis:cmsPhrase(actionBean.contentManagement, 'Quick facts')}</h4>
		<div>
		    <ul>
            <li>
              <c:if test="${not actionBean.typeDiploma}">
                <a href="${ actionBean.pageUrl }#tab-designations" onclick="openSection('tab-designations');">
              </c:if>
                <span class="bold">${ actionBean.typeTitle }</span>
              <c:if test="${not actionBean.typeDiploma}">
                </a>
              </c:if>
                <span class="discreet">(code ${ actionBean.idsite })</span>
            </li>
            <c:if test="${ actionBean.typeNatura2000 and not empty actionBean.siteType}">
            <li>
                Under
              <c:if test="${actionBean.siteType eq 'A' or actionBean.siteType eq 'C'}">
                <a href="${ actionBean.pageUrl }#tab-designations" onclick="openSection('tab-designations');">
                <span class="bold">Birds Directive</span>
                </a>
              </c:if>
                <c:if test="${actionBean.siteType eq 'C'}">and</c:if>
              <c:if test="${actionBean.siteType eq 'B' or actionBean.siteType eq 'C'}">
                <a href="${ actionBean.pageUrl }#tab-designations" onclick="openSection('tab-designations');">
                <span class="bold">Habitats Directive</span>
                </a>
              </c:if>
            </li>
            </c:if>
            <li>
                ${eunis:cmsPhrase(actionBean.contentManagement, 'Since')}
              <span class="bold">
                  <c:choose>
                   <c:when test="${not empty actionBean.siteDesignationDateDisplayValue}">
	                 <c:if test="${not actionBean.typeDiploma}">
                      <a href="${ actionBean.pageUrl }#tab-designations" onclick="openSection('tab-designations');">
                     </c:if>
                       <fmt:formatDate value="${ (actionBean.siteDesignationDateDisplayValue) }" pattern="${actionBean.dateFormat}"/>
	                 <c:if test="${not actionBean.typeDiploma}">
                      </a>
                     </c:if>
                   </c:when>
                   <c:otherwise>${eunis:cmsPhrase(actionBean.contentManagement, 'Not available')}</c:otherwise>
                  </c:choose>
              </span>
            </li>
			<li>
			    ${eunis:cmsPhrase(actionBean.contentManagement, 'Country:')}
                <span class="bold">
                    <c:choose>
                      <c:when test="${not empty actionBean.country}">${ (actionBean.country) }</c:when>
                      <c:otherwise>${eunis:cmsPhrase(actionBean.contentManagement, 'Not available')}</c:otherwise>
                    </c:choose>
                </span>
            </li>
			<li>
			    ${eunis:cmsPhrase(actionBean.contentManagement, 'Administrative region:')}
			    <c:choose>
                <c:when test="${not empty actionBean.regions}">
                    <c:forEach var="region" items="${actionBean.regions}" varStatus="status">
                        <span class="bold">${ region.value }</span> <span class="discreet">(${ region.name })</span><c:if test="${not status.last}">,</c:if>
                    </c:forEach>
			    </c:when>
			    <c:otherwise><span class="bold">${eunis:cmsPhrase(actionBean.contentManagement, 'Not available')}</span></c:otherwise>
                </c:choose>
			</li>
            <li>
                ${eunis:cmsPhrase(actionBean.contentManagement, 'Surface area:')}
                <c:choose>
                    <c:when test="${not empty actionBean.surfaceAreaKm2}">
                        <span class="bold">${ actionBean.surfaceAreaKm2 } km<sup>2</sup></span> <span class="discreet">(${eunis:formatAreaExt(actionBean.factsheet.siteObject.area, 0, 2, '&nbsp;', null)} ha)</span>
                    </c:when>
                    <c:otherwise><span class="bold">${eunis:cmsPhrase(actionBean.contentManagement, 'Not available')}</span></c:otherwise>
                 </c:choose>
            </li>

            <li>
                ${eunis:cmsPhrase(actionBean.contentManagement, 'Marine area:')}
                <span class="bold">
                    <c:choose>
                        <c:when test="${not empty actionBean.marineAreaPercentage}">
                            ${actionBean.marineAreaPercentage}%
                        </c:when>
                        <c:otherwise>${eunis:cmsPhrase(actionBean.contentManagement, 'Not available')}</c:otherwise>
                    </c:choose>
                </span>
            </li>
            <c:if test="${ actionBean.typeNatura2000 }">
            <li>${eunis:cmsPhrase(actionBean.contentManagement, 'Located in ')} <span class="bold">
                <c:choose>
                <c:when test="${not empty actionBean.biogeographicRegion}">
                    ${actionBean.biogeographicRegionList}
                </c:when>
                <c:otherwise>${eunis:cmsPhrase(actionBean.contentManagement, 'Not available')}</c:otherwise>
                </c:choose>
              </span>
              <c:choose>
                <c:when test="${actionBean.biogeographicRegionsCount > 1}">${eunis:cmsPhrase(actionBean.contentManagement, 'biogeographical regions')}</c:when>
                <c:otherwise>${eunis:cmsPhrase(actionBean.contentManagement, 'biogeographical region')}</c:otherwise>
              </c:choose>
            </li>
            </c:if>

            <c:if test="${ actionBean.typeNatura2000 }">
            <li>
                ${eunis:cmsPhrase(actionBean.contentManagement, 'It protects')}
                <a href="${ actionBean.pageUrl }#tab-species" onclick="openSection('tab-species');">
                    <span class="bold">${ (actionBean.protectedSpeciesCount) }</span>
                </a>
                ${eunis:cmsPhrase(actionBean.contentManagement, 'species of the Nature Directives')}
            </li>
            <li>
                ${eunis:cmsPhrase(actionBean.contentManagement, 'It protects')}
                <a href="${ actionBean.pageUrl }#tab-habitats" onclick="openSection('tab-habitats');">
                    <span class="bold">${ (actionBean.habitatsCount) }</span>
                </a>
                ${eunis:cmsPhrase(actionBean.contentManagement, 'habitat types of the Habitats Directive')}
            </li>
            </c:if>
          </ul>
          <br>
			<c:if test="${ actionBean.typeNatura2000 }">
                <p class="discreet">${eunis:cmsPhrase(actionBean.contentManagement, 'Source and more information')}: <a href="http://natura2000.eea.europa.eu/Natura2000/SDF.aspx?site=${ actionBean.idsite }" target="_BLANK">${eunis:cmsPhrase(actionBean.contentManagement, 'Natura 2000 Standard Data Form')}</a></p>
                <c:if test="${ actionBean.context.sessionManager.authenticated && actionBean.context.sessionManager.importExportData_RIGHT}">
                  <p class="discreet" style="color:red;"><a href="/updatesite/${ actionBean.idsite }" title="Testing only">Force SDF update from Natura 2000 site</a></p>
                </c:if>
            </c:if>
            <c:if test="${ actionBean.typeCDDA}">
                <p class="discreet">${eunis:cmsPhrase(actionBean.contentManagement, 'Source and more information')}:
                <a href="http://www.eea.europa.eu/data-and-maps/data/ds_resolveuid/c24610c33ca343a8ad51d289ef4c9f4c">
                ${eunis:cmsPhrase(actionBean.contentManagement, 'Nationally designated areas (CDDA)')}
                </a></p>
            </c:if>
            <c:if test="${actionBean.typeDiploma}">
                <p class="discreet">${eunis:cmsPhrase(actionBean.contentManagement, 'Source and more information')}: <a href="http://www.coe.int/t/dg4/cultureheritage/nature/diploma/default_en.asp" target="_BLANK">${eunis:cmsPhrase(actionBean.contentManagement, 'Council of Europe')}</a></p>
            </c:if>
		</div>
	</div>
</stripes:layout-definition>