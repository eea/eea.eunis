<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>

    <c:choose>
        <c:when test="${not empty actionBean.consStatus}">
                <!-- species status -->
                <a name="species-status"></a>
                <%--<h2 class="visualClear">How is this species doing?</h2>--%>

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
                                                <a href="http://www.iucnredlist.org/apps/redlist/details/${actionBean.redlistLink}/0">
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
                                                <c:when test="${!empty actionBean.redlistLink and !(statusCodeEU eq 'ne')}">
                                                    <a href="http://www.iucnredlist.org/apps/redlist/details/${actionBean.redlistLink}/1">
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
                                                    <c:when test="${!empty actionBean.redlistLink}">
                                                        <a href="http://www.iucnredlist.org/apps/redlist/details/${actionBean.redlistLink}/1">
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
                    <fieldset>
                        <legend><strong>IUCN Red List Category</strong></legend>
                        <table class="legend-table">
                            <tr class="discreet">
                                <td><div class="threat-status-ne legend-color" style="width: 8px; height: 8px;"> </div> Not Evaluated</td>
                                <td><div class="threat-status-nt legend-color"> </div> Near Threatened</td>
                                <td><div class="threat-status-re legend-color"> </div> Regionally Extinct</td>
                            </tr>
                            <tr class="discreet">
                                <td><div class="threat-status-na legend-color"> </div> Not Applicable</td>
                                <td><div class="threat-status-vu legend-color"> </div> Vulnerable</td>
                                <td><div class="threat-status-ew legend-color"> </div> Extinct in the Wild</td>
                            </tr>
                            <tr class="discreet">
                                <td><div class="threat-status-dd legend-color"> </div> Data Deficient</td>
                                <td><div class="threat-status-en legend-color"> </div> Endangered</td>
                                <td><div class="threat-status-ex legend-color"> </div> Extinct</td>
                            </tr>
                            <tr class="discreet">
                                <td><div class="threat-status-lc legend-color"> </div> Least Concern</td>
                                <td><div class="threat-status-cr legend-color"> </div> Critically Endangered</td>
                                <td> </td>
                            </tr>
                        </table>
                    </fieldset>
                    <div class="discreet">
                        Sources:
                        <ul>
                            <li>
                                <a href="http://ec.europa.eu/environment/nature/conservation/species/redlist/">European Red List</a>
                            </li>
                            <li>
                                <a href="http://www.iucnredlist.org/technical-documents/categories-and-criteria">IUCN Red List Categories and Criteria</a>
                            </li>
                        </ul>
                    </div>
                </div>

                <div class="right-area conservation-status">
                    <h3>EU conservation status</h3>
                    <p>Conservation status assesses every six years and for each biogeographical region the condition of habitats and species compared to the favourable status as described in the Habitats Directive. The map shows the 2013 assessments.</p>
                        <iframe id="speciesStatusMap" class="map-border" src="" height="420px" width="100%"></iframe>

                    <script>
                        addReloadOnDisplay("speciesStatusPane", "speciesStatusMap", "http://discomap.eea.europa.eu/map/Filtermap/?webmap=f50de51b438446499382664f9620081f&Code=${actionBean.n2000id}");
                    </script>

                    <div class="footer">
                        <table>
                            <tr><td colspan="2" class="discreet"><div class="legend-color conservation-legend-favorable"> </div> <span class="bold">Favourable</span>: A species is in a situation where it is prospering and with good prospects to do so in the future as well</td></tr>
                            <tr><td colspan="2" class="discreet"><div class="legend-color conservation-legend-inadequate"> </div> <span class="bold">Unfavourable-Inadequate</span>: A species is in a situation where a change in management or policy is required to return the species to favourable status but there is no danger of extinction in the foreseeable future</td></tr>
                            <tr><td colspan="2" class="discreet"><div class="legend-color conservation-legend-bad"> </div> <span class="bold">Unfavourable-Bad</span>: A species is in serious danger of becoming extinct (at least regionally)</td></tr>
                            <tr>
                                <td colspan="2" class="discreet"><div class="legend-color conservation-legend-unknown"> </div> <span class="bold">Unknown</span>: There is insufficient information available to allow an assessment</td>
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
                                    <c:if test="${not empty actionBean.conservationStatusPDF}">
                                        <li>
                                            <a href="${actionBean.conservationStatusPDF.url}">Conservation status 2006 – summary (pdf)</a>
                                        </li>
                                    </c:if>
                                    <c:if test="${not empty actionBean.conservationStatus}">
                                        <li>
                                            <a href="${actionBean.conservationStatus.url}">${actionBean.conservationStatus.name}</a>
                                        </li>
                                    </c:if>
                                </ul>
                                </c:if>
                            </div>
                            <c:if test="${not empty actionBean.conservationStatusQueryResultRows}">
                                <%-- Hidden by #19431 --%>
                                <%--<span class="table-definition-target standardButton float-left">--%>
                                    <%--${eunis:cmsPhrase(actionBean.contentManagement, 'See full table details')}--%>
                                <%--</span>--%>
                                <c:forEach items="${actionBean.conservationStatusQueries}" var="query">
                                    <div class="table-definition-body visualClear">
                                        <div style="margin-top:20px">
                                            <p style="font-weight:bold">${eunis:cmsPhrase(actionBean.contentManagement, query.title)}:</p>
                                            <c:set var="queryId" value="${query.id}"/>

                                            <div style="overflow-x:auto">
                                                <span class="pagebanner">${fn:length(actionBean.conservationStatusQueryResultRows[queryId])} item<c:if test="${fn:length(actionBean.conservationStatusQueryResultRows[queryId]) != 1}">s</c:if> found.</span>
                                                <table style="margin-top:20px" class="datatable listing inline-block">
                                                    <thead>
                                                        <tr>
                                                            <c:forEach var="col" items="${actionBean.conservationStatusQueryResultCols[queryId]}">
                                                                <th class="dt_sortable">
                                                                 ${col.property}
                                                                </th>
                                                            </c:forEach>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="row" items="${actionBean.conservationStatusQueryResultRows[queryId]}">
                                                            <tr>
                                                                <c:forEach var="col" items="${actionBean.conservationStatusQueryResultCols[queryId]}">
                                                                    <td>${row[col.property]}</td>
                                                                </c:forEach>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:if>
                        </div>
                        <!-- Conservation status other resources overlay -->

                    </div>
                </div>
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
