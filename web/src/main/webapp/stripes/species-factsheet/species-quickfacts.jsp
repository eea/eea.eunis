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
                    <div>
                        <ul>
                            <li>
                                ${eunis:cmsPhrase(actionBean.contentManagement, 'Threat status Europe')}:
                                <a href="${ actionBean.pageUrl }#threat_status" onclick="openSection('threat_status');">
                                <span class="bold">
                                    <c:choose>
                                        <c:when test="${not empty actionBean.consStatusEU.statusName}">
                                            ${actionBean.consStatusEU.statusName}
                                        </c:when>
                                        <c:otherwise>${eunis:cmsPhrase(actionBean.contentManagement, 'Not evaluated')}</c:otherwise>
                                    </c:choose>
                                </span>
                                </a>
                                (IUCN)
                            </li>
                            <li>
                                <c:choose>
                                    <c:when test="${actionBean.protectedByEUDirectives or actionBean.otherAgreements > 0}">
                                        Protected by
                                        <c:if test="${actionBean.habitatsDirective}">
                                            <a href="${ actionBean.pageUrl }#legal_status" onclick="openSection('legal_status');"><span class="bold">EU Habitats Directive</span></a>
                                        </c:if>
                                        <c:if test="${actionBean.birdsDirective}">
                                            <a href="${ actionBean.pageUrl }#legal_status" onclick="openSection('legal_status');"><span class="bold">EU Birds Directive</span></a>
                                        </c:if>
                                        <c:if test="${actionBean.protectedByEUDirectives and actionBean.otherAgreements>0}">and</c:if>
                                        <c:if test="${actionBean.otherAgreements > 0}">
                                            <a href="${ actionBean.pageUrl }#legal_status" onclick="openSection('legal_status');"><span class="bold">${ actionBean.otherAgreements }</span></a>
                                            <c:if test="${actionBean.protectedByEUDirectives and actionBean.otherAgreements > 0}">other</c:if>
                                            <c:choose>
                                                <c:when test="${actionBean.otherAgreements > 1}">
                                                    ${eunis:cmsPhrase(actionBean.contentManagement, 'international agreements')}
                                                </c:when>
                                                <c:otherwise>
                                                    ${eunis:cmsPhrase(actionBean.contentManagement, 'international agreement')}
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>${eunis:cmsPhrase(actionBean.contentManagement, 'Not listed in legal texts')}</c:otherwise>
                                </c:choose>
                            </li>
                            <c:if test="${actionBean.protectedByEUDirectives  or actionBean.otherAgreements > 0}">
                            <li>
                                <a href="${ actionBean.pageUrl }#protected" onclick="openSection('protected');"><span class="bold">${ actionBean.speciesSitesCount }</span></a>
                                    ${eunis:cmsPhrase(actionBean.contentManagement, 'Natura 2000 sites are designated for the species')}.
                            </li>
                            </c:if>
                            <c:if test="${not empty actionBean.habitats}">
                            <li>
                                ${eunis:cmsPhrase(actionBean.contentManagement, 'Lives in ')}:
                                <span class="bold">
                                    <c:choose>
                                        <c:when test="${not empty actionBean.habitats}">
                                            <c:forEach items="${actionBean.habitats}" var="habitat" varStatus="loopStatus">
                                                ${habitat}<c:if test="${!loopStatus.last}">, </c:if>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>Not avaliable</c:otherwise>
                                    </c:choose>
                                </span>
                                ${eunis:cmsPhrase(actionBean.contentManagement, 'habitats')}
                            </li>
                            </c:if>
                            <c:if test="${not empty actionBean.nobanisFactsheetLink}">
                            <li>
                                ${eunis:cmsPhrase(actionBean.contentManagement, 'Reported as invasive by')}
                                <span class="bold"><a href="${actionBean.nobanisLink.url}">NOBANIS</a></span>,
                                see also
                                <span class="bold"><a href="${actionBean.nobanisFactsheetLink.url}">fact sheet</a></span>
                            </li>
                            </c:if>
                            <c:if test="${not empty actionBean.factsheet.validSpeciesId}">
                                <li>
                                    ${eunis:cmsPhrase(actionBean.contentManagement, 'Valid parent species: ')}
                                    <span class="bold italics"><a href="/species/${actionBean.factsheet.validSpeciesId}">${actionBean.factsheet.parentSpeciesName}</a></span>
                                </li>
                            </c:if>
                            <c:if test="${!empty actionBean.n2000id}">
                                <li class="discreet">
                                    ${eunis:cmsPhrase(actionBean.contentManagement, 'Natura 2000 code')}: ${actionBean.n2000id}
                                </li>
                            </c:if>
                        </ul>

                    </div>
                </div>


                <!-- END quick facts -->
</stripes:layout-definition>