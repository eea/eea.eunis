<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>

    <c:choose>
        <c:when test="${not empty actionBean.consStatus}">
                <!-- species status -->
                <a name="species-status"></a>

                <div class="left-area iucn-red-list-area">
                  <h3>${eunis:cmsPhrase(actionBean.contentManagement, 'IUCN Red List status of threatened species')}</h3>
                  <p>The IUCN Red List threat status assesses the risk of extinction.</p>
                    <div class="threat-status-indicator width-14">
                        <c:if test="${not empty actionBean.consStatus}">

                            <c:set var="statusCodeWO" value="${(not empty actionBean.consStatusWO and not empty actionBean.consStatusWO.threatCode) ? fn:toLowerCase(actionBean.consStatusWO.threatCode) : 'ne'}"></c:set>
                            <c:set var="statusCodeEU" value="${(not empty actionBean.consStatusEU and not empty actionBean.consStatusEU.threatCode) ? fn:toLowerCase(actionBean.consStatusEU.threatCode) : 'ne' }"></c:set>
                            <c:set var="statusCodeE25" value="${(not empty actionBean.consStatusE25 and not empty actionBean.consStatusE25.threatCode) ? fn:toLowerCase(actionBean.consStatusE25.threatCode) : 'ne' }"></c:set>

                            <div class="threat-status-${statusCodeWO} roundedCorners">
                                <%--World status--%>
                                <c:choose>
                                    <c:when test="${!empty actionBean.consStatusWO and !(statusCodeWO eq 'ne')}">
                                        <c:choose>
                                            <c:when test="${!empty actionBean.redlistLink}">
                                                <a href="https://apiv3.iucnredlist.org/api/v3/taxonredirect/${actionBean.redlistLink}">
                                            </c:when>
                                            <c:otherwise>
                                                <a href="http://www.iucnredlist.org/apps/redlist/search/external?text=${eunis:treatURLSpecialCharacters(actionBean.specie.scientificName)}&amp;mode=">
                                            </c:otherwise>
                                        </c:choose>
                                            <div class="text-right">
                                                <p class="threat-status-region x-small-text">${eunis:cmsPhrase(actionBean.contentManagement, 'World')}</p>
                                                <p class="threat-status-label x-small-text">${actionBean.consStatusWO.statusName}</p>
                                            </div>
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-right">
                                            <p class="threat-status-region x-small-text">${eunis:cmsPhrase(actionBean.contentManagement, 'World')}</p>
                                            <p class="threat-status-label x-small-text">${eunis:cmsPhrase(actionBean.contentManagement, 'Not evaluated')}</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <%--EU status--%>
                                <div class="threat-status-${statusCodeEU} roundedCorners width-12">
                                    <c:choose>
                                        <c:when test="${not empty actionBean.consStatusEU and !(statusCodeEU eq 'ne')}">

                                            <c:choose>
                                                <c:when test="${!empty actionBean.redlistLink and !empty actionBean.redlistLinkEurope and !(statusCodeEU eq 'ne')}">
                                                    <a href="https://www.iucnredlist.org/species/${actionBean.redlistLink}/${actionBean.redlistLinkEurope}">
                                                </c:when>
                                                <c:when test="${!empty actionBean.redlistLink}">
                                                    <a href="https://apiv3.iucnredlist.org/api/v3/taxonredirect/${actionBean.redlistLink}">
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="http://www.iucnredlist.org/apps/redlist/search/external?text=${eunis:treatURLSpecialCharacters(actionBean.specie.scientificName)}&amp;mode=">
                                                </c:otherwise>
                                            </c:choose>
                                            <%--<a href="references/${actionBean.consStatusEU.idDc}">--%>
                                                <div class="text-right">
                                                    <p class="threat-status-region x-small-text">${eunis:cmsPhrase(actionBean.contentManagement, 'Europe')}</p>
                                                    <p class="threat-status-label x-small-text">${actionBean.consStatusEU.statusName}</p>
                                                </div>
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="text-right">
                                                <p class="threat-status-region x-small-text">${eunis:cmsPhrase(actionBean.contentManagement, 'Europe')}</p>
                                                <p class="threat-status-label x-small-text">${eunis:cmsPhrase(actionBean.contentManagement, 'Not evaluated')}</p>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>

                                    <%--E25 status--%>
                                    <div class="threat-status-${statusCodeE25} roundedCorners width-11">
                                        <c:choose>
                                            <c:when test="${not empty actionBean.consStatusE25 and !(statusCodeE25 eq 'ne')}">
                                                <c:choose>
                                                    <c:when test="${!empty actionBean.redlistLink and !empty actionBean.redlistLinkEurope}">
                                                        <a href="https://www.iucnredlist.org/species/${actionBean.redlistLink}/${actionBean.redlistLinkEurope}">
                                                    </c:when>
                                                    <c:when test="${!empty actionBean.redlistLink}">
                                                        <a href="https://apiv3.iucnredlist.org/api/v3/taxonredirect/${actionBean.redlistLink}">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="http://www.iucnredlist.org/apps/redlist/search/external?text=${eunis:treatURLSpecialCharacters(actionBean.specie.scientificName)}&amp;mode=">
                                                    </c:otherwise>
                                                </c:choose>
                                                <%--<a href="references/${actionBean.consStatusE25.idDc}">--%>
                                                    <div class="text-right">
                                                        <p class="threat-status-region x-small-text">${eunis:cmsPhrase(actionBean.contentManagement, 'EU')}</p>
                                                        <p class="threat-status-label x-small-text">${actionBean.consStatusE25.statusName}</p>
                                                    </div>
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="text-right">
                                                    <p class="threat-status-region x-small-text">${eunis:cmsPhrase(actionBean.contentManagement, 'EU')}</p>
                                                    <p class="threat-status-label x-small-text">${eunis:cmsPhrase(actionBean.contentManagement, 'Not evaluated')}</p>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>
                    <c:if test="${empty actionBean.art12status}">
                        <%--shows the legend underneath the status if there is no art 12 legend--%>
                        <stripes:layout-render name="/stripes/species-factsheet/species-status-rl-legend.jsp"/>
                    </c:if>
                </div>

                <div class="right-area conservation-status" style="width: 500px;">


                    <c:choose>
                    <c:when test="${actionBean.habitatsDirective}">
                        <h3>EU conservation status</h3>
                    <p>Conservation status assesses every six years and for each biogeographical
                        region the condition of habitats and species compared to the favourable status
                        as described in the Habitats Directive. The map shows the 2013-2018
                        assessments as reported by EU Member State. Assessments are further
                        detailed in the summary document available behind the link below.</p>
                        <iframe id="speciesStatusMap" class="map-border" src="" height="425px" width="100%"></iframe>

                    <script>
                        addReloadOnDisplay("speciesStatusPane", "speciesStatusMap", "https://maps.eea.europa.eu/EEAViewer/?appid=fcb24d064e7b4a2b83da7e4cabed3cce&showLayers=HabitatsDirective_ART_17_WMS_version_2020_08_public_6171;HabitatsDirective_ART_17_WMS_version_2020_08_public_6171_0;HabitatsDirective_ART_17_WMS_version_2020_08_public_6171_4&region=%25&zoomto=true&embed=true&speciescode=${actionBean.n2000id}");
                    </script>

                    <div class="footer">
                        <table>
                            <tr><td colspan="2" class="discreet"><div class="legend-color conservation-legend-favorable"> </div> <span class="bold">Good</span>: the species is viable and maintaining itself on a long-term basis, its natural range is not reduced, and it has a sufficient large habitat.</td></tr>
                            <tr><td colspan="2" class="discreet"><div class="legend-color conservation-legend-inadequate"> </div> <span class="bold">Poor</span>: the species is not as critical as being unfavourable-bad, but still requires significant conservation and restoration measure to make it viable in the long-term, or to enlarged its current range, or to improve the quality and availability of its habitat.</td></tr>
                            <tr><td colspan="2" class="discreet"><div class="legend-color conservation-legend-bad"> </div> <span class="bold">Bad</span>: the species is either not maintaining itself on a long-term basis and is not viable, or its natural range as been or is being drastically reduced, or its habitat is largely insufficient; the species requires major conservation and restoration measures.</td></tr>
                            <tr>
                                <td colspan="2" class="discreet"><div class="legend-color conservation-legend-unknown"> </div> <span class="bold">Unknown</span>: the information available for the species is scarce and does not allow a proper assessment of its conservation status.</td>
                                <%--<td class="discreet"><div class="legend-color conservation-legend-nodata"> </div> <span class="bold">No data</span></td>--%>
                            </tr>
                        </table>
                    </div>

                    <div class="footer">
                        <!-- Table definition dropdown example -->
                        <div class="table-definition contain-float">
                            <div class="discreet" style="float:right;">
                                <c:if test="${not empty actionBean.conservationStatusPDF or not empty actionBean.conservationStatus}">
                                Sources:
                                <ul>
<%--                                    <c:if test="${not empty actionBean.conservationStatusPDF}">--%>
<%--                                        <li>--%>
<%--                                            <a href="${actionBean.conservationStatusPDF.url}">Conservation status 2007-2012 - summary</a>--%>
<%--                                        </li>--%>
<%--                                    </c:if>--%>
                                    <c:if test="${not empty actionBean.conservationStatus}">
                                        <li>
                                            <a href="${actionBean.conservationStatus.url}">${actionBean.conservationStatus.name}</a>
                                        </li>
                                    </c:if>
                                </ul>
                                </c:if>
                            </div>
                            <c:if test="${not empty actionBean.conservationByCountry}">
                                <span class="table-definition-target standardButton float-left" style="margin-top: 20px;">
                                    ${eunis:cmsPhrase(actionBean.contentManagement, 'Full table details')}
                                </span>
                                    <div class="table-definition-body visualClear" style="display:none;">
                                        <div style="margin-top:20px">
                                            <p style="font-weight:bold">Conservation status (2013-2018) of species (Habitats Directive, Article 17) by EU Member State:</p>

                                            <c:if test="${fn:length(actionBean.conservationByCountry) gt 10}">
                                                <div class="scroll-auto" style="height: 400px">
                                            </c:if>
                                                <div style="overflow-x:auto ">
                                                <table class="datatable listing inline-block">
                                                    <thead>
                                                        <tr>
                                                            <th class="dt_sortable">Country</th>
                                                            <th class="dt_sortable">Region</th>
                                                            <th class="dt_sortable">Conclusion</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="row" items="${actionBean.conservationByCountry}">
                                                            <tr>
                                                                <td>${row.get('area_name_en')}</td>
                                                                <td>${row.get('region_name')}</td>
                                                                <td>${row.get('assessment_label')}</td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                                </div>
                                            <c:if test="${fn:length(actionBean.conservationByCountry) gt 10}">
                                                </div>
                                            </c:if>
                                            </div>
                                </div>
                            </c:if>


                        </div>
                        <!-- Conservation status other resources overlay -->

                    </div>

                    </c:when>
                        <c:when test="${not empty actionBean.art12status}">
                            <h3>EU population status</h3>

                            <p>The status of the population at the EU level was evaluated at the species level;
                                this was based on the reports delivered by Member States under Article 12 of the
                                Birds Directive (see fact sheet below). The EU status assessment can cover several
                                subspecies/subspecific population units. For more information, please consult the
                                species fact sheet and link below.
                            </p>
                            <ul>
                                <c:forEach var="lnk" items="${actionBean.art12factsheet}">
                                    <li><a href="${lnk.url}">${lnk.name}</a></li>
                                </c:forEach>
                                <c:forEach var="lnk" items="${actionBean.art12expert}">
                                    <li><a href="${lnk.url}">${lnk.name}</a></li>
                                </c:forEach>
                            </ul>


                        </c:when>
                        <c:otherwise>
                            <h3>EU conservation status</h3>
                            <p>The EU conservation status is assessed for species mentioned in the EU Habitats Directive annexes. The EU Habitats Directive does not cover this species.</p>
                        </c:otherwise>
                    </c:choose>

                </div>
            <c:if test="${not empty actionBean.art12status}">
                <%--shows both legends aligned--%>
                <div style="width: 100%; float: left;">
                    <div class="left-area iucn-red-list-area">
                        <stripes:layout-render name="/stripes/species-factsheet/species-status-rl-legend.jsp"/>
                    </div>
                    <div class="right-area conservation-status">
                        <fieldset>
                            <legend><strong>Population status categories for bird species under the Birds Directive</strong></legend>
                            <table class="legend-table">
                                <tr class="discreet"><td><div class="a12threat-status-Secure legend-color"> </div> Secure</td> </tr>
                                <tr class="discreet"><td><div class="a12threat-status-NearThreatened legend-color"> </div> Near Threatened, declining or depleted</td></tr>
                                <tr class="discreet"><td><div class="a12threat-status-Threatened legend-color"> </div> Threatened (i.e. vulnerable, endangered, critically endangered, regionally extinct)</td></tr>
                                <tr class="discreet"><td><div class="a12threat-status-Unknown legend-color"> </div> Unknown or not evaluated</td></tr>
                            </table>
                        </fieldset>
                    </div>
                </div>
            </c:if>
        </c:when>
        <c:otherwise>
            ${eunis:cmsPhrase(actionBean.contentManagement, 'This species has not yet been assessed for the IUCN Red List')}
            <script>
                $("#threat-accordion").addClass("nodata");
            </script>
        </c:otherwise>
    </c:choose>

                <!-- END species status -->
</stripes:layout-definition>
