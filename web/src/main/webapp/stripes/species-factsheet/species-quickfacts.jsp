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
        <style>
            .quickfacts h4 {
                margin-top: 0;
            }
            .quickfacts table {
                width: 100%;
            }
            .quickfacts table td,
            .quickfacts table th {
                vertical-align: top;
                padding: .5em 0;
            }
            .quickfacts tbody {
                border-top: 1px solid #ececec;
            }
            .quickfacts table td {
                padding-left: 1em;
                padding-bottom: 0;
            }
            .quickfacts tr:last-child td {
                padding-bottom: .5em;
            }
            .quickfacts table th {
                width: 14em;
            }
            .quickfacts table h6 {
                margin: 0;
            }


            .quickfacts table small {
                font-size: inherit;
                font-style: italic;
                font-weight: normal;
                color: gray;
            }

            .quickfacts .list-inline {
                font-style: italic;
            }

            .quickfacts-list {
                margin: 0 0 0 1em;
                list-style: none!important;
            }
            .quickfacts-list li:before {
                content: "-";
                display: inline-block;
                width: 1em;
                margin-left: -1em;
                color: gray;
            }
            .list-inline {
                list-style: none!important;
                margin: 0;
            }
            .list-inline li {
                display: inline!important;
            }
            .list-inline li:before {
                content: ", ";
            }
            .list-inline li:first-child:before {
                content: "";
            }
            /* CONCLUSION */
            .conclusion {
                display: inline-block;
                *zoom: 1;
                padding: 1px 2px;
                margin: -2px 0;
                text-align: center;
                border-color: transparent;
                border-width: 1px;
                border-style: solid;
                border-radius: 2px;
                min-height: 1em;
                min-width: 1em;
            }
            /* CONCLUSION TYPES */
            .FV .conclusion,
            .FV.conclusion {
                color: #000;
                border-color: #639500;
                background-color: #9aca3b;
            }
            .FVU .conclusion,
            .FVU.conclusion {
                color: #000;
                border-color: #639500;
                background-color: #9aca3b;
            }
            .U1 .conclusion,
            .U1.conclusion {
                color: #000;
                border-color: #b05f00;
                background-color: #e69800;
            }
            .U1U .conclusion,
            .U1U.conclusion {
                color: #000;
                border-color: #b05f00;
                background-color: #e69800;
            }
            .U1M .conclusion,
            .U1M.conclusion {
                color: #000;
                border-color: #b05f00;
                background-color: #e69800;
            }
            .U1P .conclusion,
            .U1P.conclusion {
                color: #000;
                border-color: #b05f00;
                background-color: #e69800;
            }
            .U2 .conclusion,
            .U2.conclusion {
                color: #000;
                border-color: #742c2c;
                background-color: #eb6847;
            }
            .U2U .conclusion,
            .U2U.conclusion {
                color: #000;
                border-color: #742c2c;
                background-color: #eb6847;
            }
            .U2M .conclusion,
            .U2M.conclusion {
                color: #000;
                border-color: #742c2c;
                background-color: #eb6847;
            }
            .U2P .conclusion,
            .U2P.conclusion {
                color: #000;
                border-color: #742c2c;
                background-color: #eb6847;
            }
            .XX .conclusion,
            .XX.conclusion {
                color: #000;
                border-color: #888;
                background-color: #b4b4b4;
            }
            .XXU .conclusion,
            .XXU.conclusion {
                color: #000;
                border-color: #888;
                background-color: #b4b4b4;
            }
            .XU .conclusion,
            .XU.conclusion {
                color: #000;
                border-color: #888;
                background-color: #b4b4b4;
            }
            .XUU .conclusion,
            .XUU.conclusion {
                color: #000;
                border-color: #888;
                background-color: #b4b4b4;
            }
            .NA .conclusion,
            .NA.conclusion {
                color: #000;
                border-color: #4774c7;
                background-color: #78bbee;
            }
            .assesm {
                color: #666666 !important;
                font-style: italic;
            }

        </style>


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
                    EU conservation status <small>by biogeographical region</small>
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

            <tbody>
            <tr>
                <c:choose>
                <c:when test="${actionBean.protectedByEUDirectives or actionBean.otherAgreements gt 0}">
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
                </c:when>
                <c:otherwise>
                    <th>
                        ${eunis:cmsPhrase(actionBean.contentManagement, 'Not listed in legal texts')}
                    </th>
                </c:otherwise>
                </c:choose>
            </tr>
            </tbody>


            <c:if test="${actionBean.protectedByEUDirectives  or actionBean.otherAgreements gt 0}">
            <tbody>
            <tr>
                <th>Natura 2000 sites</th>
                <td>
                    <a href="${ actionBean.pageUrl }#protected" onclick="openSection('protected');"><span class="bold">${ actionBean.speciesSitesCount }</span></a>
                        ${eunis:cmsPhrase(actionBean.contentManagement, 'are designated for the species type')}
                </td>
            </tr>
            </tbody>
            </c:if>

            <c:if test="${not empty actionBean.nobanisFactsheetLink}">
            <tbody>
                <tr>
                    <th>
                        ${eunis:cmsPhrase(actionBean.contentManagement, 'Reported as invasive by')}
                    </th>
                    <td>
                        <span class="bold"><a href="${actionBean.nobanisLink.url}">NOBANIS</a></span>,
                        <small>see also
                        <span class="bold"><a href="${actionBean.nobanisFactsheetLink.url}">fact sheet</a></span></small>
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
                        <ul class="quickfacts-list">
                        <c:forEach var="ecosystem" items="${actionBean.breedingEcosystems}" varStatus="estatus">
                            <li>${ecosystem.ecoName}</li>
                        </c:forEach>
                        </ul>
                    </td>
                </tr>
                </c:if>
                <c:if test="${not empty actionBean.winteringEcosystems}">
                <tr>
                    <th>Wintering habitats</th>
                    <td>
                        <ul class="quickfacts-list">
                            <c:forEach var="ecosystem" items="${actionBean.winteringEcosystems}" varStatus="estatus">
                                <li>${ecosystem.ecoName}</li>
                            </c:forEach>
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
                            <ul class="quickfacts-list">
                                <c:forEach var="ecosystem" items="${actionBean.preferredEcosystems}" varStatus="estatus">
                                    <li>${ecosystem.ecoName}</li>
                                </c:forEach>
                            </ul>
                        </td>
                    </tr>
                </c:if>
                <c:if test="${not empty actionBean.suitableEcosystems}">
                    <tr>
                        <th><small>May also occur in</small></th>
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
                    <th>
                        ${eunis:cmsPhrase(actionBean.contentManagement, 'Natura 2000 species code')}
                    </th>
                    <td>${actionBean.n2000id}</td>
                </tr>
                </tbody>
            </c:if>
        </table>
    </div>


                <!-- END quick facts -->
</stripes:layout-definition>