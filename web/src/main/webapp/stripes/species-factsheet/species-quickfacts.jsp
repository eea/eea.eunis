<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
               <!-- quick facts -->

               <script>
                   function openSection(sectionName) {
                       if($('#' + sectionName + ' ~ h2').attr('class').indexOf('current')==-1)
                           $('#' + sectionName + ' ~ h2').click();
                   }
               </script>


               <!--  Gallery on left -->
                <div class="left-area species">
                    <ul id="speciesGallery" class="galleryViewss" style="display: none;">
                            <c:forEach items="${actionBean.pics}" var="pic" varStatus="loop">
                                <li>
                                    <img src="${pic.path}/${pic.filename}"
                                    <c:if test="${not empty pic.source && fn:containsIgnoreCase(pic.description, 'no photo available')}">
                                    title="${pic.description}"
                                    </c:if>
                                     style="display: none;"
                                     <c:choose>
                                         <c:when test="${not empty pic.sourceUrl}">
                                         data-description="<a href='${pic.sourceUrl}'>${pic.source}</a>"
                                         </c:when>
                                         <c:otherwise>
                                             data-description="${pic.source}"
                                         </c:otherwise>
                                     </c:choose>
                                     />
                                </li>
                            </c:forEach>
                        </ul>
                    <p class="text-right">
                        <a href="http://images.google.com/images?q=${eunis:replaceTags(actionBean.scientificName)}">Images from the web</a>
                    </p>

                    <script>
                        $('.galleryViewss').galleryView({
                            enable_overlays: true,
                            panel_scale: 'fit',
                            show_filmstrip: false,
                            show_filmstrip_nav: false,
                            show_captions: true,
                            autoplay: ${fn:length(actionBean.pics)>1?"true":"false"},
                            frame_width: 50,
                            frame_height: 50,
                            frame_scale: 'fit',
                            frame_gap: 0,
                            show_infobar: false,
                        });
                    </script>
                </div>

                <!-- Textual facts on right -->
    <div class="right-area quickfacts">

        <h4>${eunis:cmsPhrase(actionBean.contentManagement, 'Quick facts')}</h4>

        <table class="table-quickfacts">
            <tbody>
            <tr>
                <th>${eunis:cmsPhrase(actionBean.contentManagement, 'Threat status Europe')}</th>
                <td>
                    <strong class="bold">
                        <a href="${ actionBean.pageUrl }#threat_status" onclick="openSection('threat_status');">
                            <c:choose>
                                <c:when test="${not empty actionBean.consStatusEU.statusName}">
                                    ${actionBean.consStatusEU.statusName}
                                </c:when>
                                <c:otherwise>${eunis:cmsPhrase(actionBean.contentManagement, 'Not evaluated')}</c:otherwise>
                            </c:choose>
                        </a>
                    </strong>
                    (IUCN)
                </td>
            </tr>
            </tbody>


            <c:if test="${not empty actionBean.biogeoAssessmentRows}">
            <tbody>
            <tr>
                <th rowspan="${eunis:getSize(actionBean.biogeoAssessmentRows)}">
                    <a href="${ actionBean.pageUrl }#threat_status" onclick="openSection('threat_status');">EU conservation status</a> <small>by biogeographical region</small>
                </th>
                <c:forEach items="${actionBean.biogeoAssessmentRows}" var="bg" varStatus="stat">
                    <c:if test="${not stat.first}"><tr></c:if>
                    <td>
                        <span class="conclusion ${bg['code']}"></span> ${bg['region']} - <small>${bg['assessment']}</small>
                    </td>
                    </tr>
                </c:forEach>
            </tbody>
            </c:if>

            <c:if test="${fn:length(actionBean.legalStatuses) gt 0}">
                <tbody>
                <tr>
                    <th>Protected by</th>
                    <td>
                        <c:if test="${actionBean.habitatsDirective}">
                            <a href="${ actionBean.pageUrl }#legal_status" onclick="openSection('legal_status');"><span class="bold">EU Habitats Directive</span></a>
                        </c:if>
                        <c:if test="${actionBean.birdsDirective}">
                            <a href="${ actionBean.pageUrl }#legal_status" onclick="openSection('legal_status');"><span class="bold">EU Birds Directive</span></a>
                        </c:if>
                        <c:if test="${actionBean.protectedByEUDirectives and actionBean.otherAgreements>0}">and</c:if>
                        <c:if test="${actionBean.otherAgreements > 0}">
                            <a href="${ actionBean.pageUrl }#legal_status" onclick="openSection('legal_status');"><span class="bold">${ actionBean.otherAgreements }</span></a>
                            <c:if test="${actionBean.protectedByEUDirectives and actionBean.otherAgreements gt 0}">other</c:if>
                            <c:choose>
                                <c:when test="${actionBean.otherAgreements gt 1}">
                                    ${eunis:cmsPhrase(actionBean.contentManagement, 'international agreements')}
                                </c:when>
                                <c:otherwise>
                                    ${eunis:cmsPhrase(actionBean.contentManagement, 'international agreement')}
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                    </td>
                </tr>
                </tbody>
            </c:if>


            <c:if test="${actionBean.habitatsDirectiveII}">
            <tbody>
            <tr>
                <th>Natura 2000 sites</th>
                <td>
                    <a href="${ actionBean.pageUrl }#protected" onclick="openSection('protected');"><span class="bold">${ actionBean.speciesSitesCount }</span></a>
                        ${eunis:cmsPhrase(actionBean.contentManagement, 'are designated for this species')}
                </td>
            </tr>
            </tbody>
            </c:if>

            <c:if test="${not empty actionBean.nobanisLink}">
            <tbody>
                <tr>
                    <th>
                        ${eunis:cmsPhrase(actionBean.contentManagement, 'Reported as invasive by')}
                    </th>
                    <td>
                        <span class="bold"><a href="${actionBean.nobanisLink.url}">NOBANIS</a></span><c:if test="${not empty actionBean.nobanisFactsheetLink}">,
                        <small>see also
                        <span class="bold"><a href="${actionBean.nobanisFactsheetLink.url}">fact sheet</a></span></small></c:if>
                    </td>
                </tr>
            </tbody>
            </c:if>


            <c:if test="${not empty actionBean.factsheet.validSpeciesId}">
                <tbody>
                <tr>
                    <th>
                        ${eunis:cmsPhrase(actionBean.contentManagement, 'Valid parent species')}
                    </th>
                    <td>
                        <span class="bold italics"><a href="/species/${actionBean.factsheet.validSpeciesId}">${actionBean.factsheet.parentSpeciesName}</a></span>
                    </td>
                </tr>
                </tbody>
            </c:if>
            
            

            <c:if test="${not empty actionBean.breedingEcosystems or not empty actionBean.winteringEcosystems}">
                <tbody>
                <c:if test="${not empty actionBean.breedingEcosystems}">
                <tr>
                    <th>Breeding habitats</th>
                    <td>
                        <ul class="list-inline">
                            <c:forEach var="ecosystem" items="${actionBean.breedingEcosystems}" varStatus="estatus"><li>${ecosystem.ecoName}</li></c:forEach>
                        </ul>
                    </td>
                </tr>
                </c:if>
                <c:if test="${not empty actionBean.winteringEcosystems}">
                <tr>
                    <th>Wintering habitats</th>
                    <td>
                        <ul class="list-inline">
                            <c:forEach var="ecosystem" items="${actionBean.winteringEcosystems}" varStatus="estatus"><li>${ecosystem.ecoName}</li></c:forEach>
                        </ul>
                    </td>
                </tr>
                </c:if>
                </tbody>
            </c:if>

            <c:if test="${not empty actionBean.preferredEcosystems or not empty actionBean.suitableEcosystems}">
                <tbody>
                <c:if test="${not empty actionBean.preferredEcosystems}">
                    <tr>
                        <th>Most preferred habitats</th>
                        <td>
                            <ul class="list-inline">
                                <c:forEach var="ecosystem" items="${actionBean.preferredEcosystems}" varStatus="estatus"><li>${ecosystem.ecoName}</li></c:forEach>
                            </ul>
                        </td>
                    </tr>
                </c:if>
                <c:if test="${not empty actionBean.suitableEcosystems}">
                    <tr>
                        <th class="normalfont">May also occur in</th>
                        <td>
                            <ul class="list-inline">
                                <c:forEach var="ecosystem" items="${actionBean.suitableEcosystems}" varStatus="estatus"><li>${ecosystem.ecoName}</li></c:forEach>
                            </ul>
                        </td>
                    </tr>
                </c:if>
                </tbody>
            </c:if>

            <c:if test="${!empty actionBean.n2000id}">
                <tbody>
                <tr>
                    <th class="normalfont">
                        ${eunis:cmsPhrase(actionBean.contentManagement, 'Natura 2000 species code')}
                    </th>
                    <td>${actionBean.n2000id}</td>
                </tr>
                </tbody>
            </c:if>

            <c:if test="${fn:length(actionBean.legalStatuses) eq 0}">
            <tbody>
                <tr>
                    <th colspan="2" class="normalfont">
                        <p>The EUNIS species component has very limited information about this species.</p>
                        <p>The main focus of the EUNIS species component is to provide relevant information about the European species protected
                            by Directives, Conventions and Agreements. The species assessed in the European Red Lists prepared by the IUCN for
                            the European Commission are also included.</p>
                        <p>
                            See <a style="border-bottom: 0px" href="http://bd.eionet.europa.eu/Reports/ETCBDTechnicalWorkingpapers/PDF/Geo_Scope_EUNIS_Species.pdf"><span class="bold"> here</span></a> what is Europe from a geographical point of view.
                        </p>
                        <p>
                            <a style="border-bottom: 0px" href="${ actionBean.pageUrl }#other_resources" onclick="openSection('other_resources');"><span class="bold">Other resources</span></a> available below may have more information.
                        </p>
                    </th>
                </tr>
            </tbody>
            </c:if>
        </table>
    </div>


                <!-- END quick facts -->
</stripes:layout-definition>