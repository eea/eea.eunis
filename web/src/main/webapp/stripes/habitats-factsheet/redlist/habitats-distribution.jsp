<%@page contentType="text/html;charset=UTF-8" %>
<%@ include file="/stripes/common/taglibs.jsp" %>
<stripes:layout-definition>
    <div>
        <div>
            <div>For each habitat a distribution map was produced from a wide variety of sources indicating known and potential occurrences of the habitat in 10x10 km grids within Europe. Occurrences in grid cells were given in two classes: actual distribution from relatively
                reliable sources (surveys, expert knowledge), and potential distribution based on models or less reliable indicators. Please download the fact sheet to see the map.
            </div>
            <div>

            </div>
        </div>
        <h3>Geographic occurrence and trends</h3>
        <div class="text-center" style="align-content: center; width: 50%; padding: 6px; float: left">

        <c:choose>
            <c:when test="${not empty actionBean.redlistOccurrencesSea}">

            </c:when>
            <c:otherwise>
                <table summary="EU28 Occurrence" class="listing fullwidth">
                    <thead>
                    <tr>
                        <th width="10%" style="text-align: left; white-space: normal;" class="nosort">
                            EU28
                        </th>
                        <th width="10%" style="text-align: left; white-space: normal;" class="nosort">
                            Present or presence uncertain
                        </th>
                        <th width="20%" style="text-align: left; white-space: normal;" class="nosort">
                            Current area of habitat (Km<sup>2</sup>)
                        </th>
                        <th width="20%" style="text-align: left; white-space: normal;" class="nosort">
                            Recent trend in quantity (last 50 years)
                        </th>
                        <th width="20%" style="text-align: left; white-space: normal;" class="nosort">
                            Recent trend in quality (last 50 years)
                        </th>
                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach items="${actionBean.redlistOccurrencesEU}" var="occ">
                        <tr>
                            <td>${occ.country}</td>
                            <td>${occ.present}</td>
                            <td>${occ.currentAreaValue}</td>
                            <td>${occ.trendQuantity}</td>
                            <td>${occ.trendQuality}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>



        </div>
        <div class="text-center" style="width: 50%; float: left; padding: 6px; text-align: center">
            <c:if test="${empty actionBean.redlistOccurrencesSea}">
            <table summary="EU28+ Occurrence" class="listing fullwidth">
                <thead>
                <tr>
                    <th width="10%" style="text-align: left; white-space: normal;" class="nosort">
                        EU28 +
                    </th>
                    <th width="10%" style="text-align: left; white-space: normal;" class="nosort">
                        Present or presence uncertain
                    </th>
                    <th width="20%" style="text-align: left; white-space: normal;" class="nosort">
                        Current area of habitat (Km<sup>2</sup>)
                    </th>
                    <th width="20%" style="text-align: left; white-space: normal;" class="nosort">
                        Recent trend in quantity (last 50 years)
                    </th>
                    <th width="20%" style="text-align: left; white-space: normal;" class="nosort">
                        Recent trend in quality (last 50 years)
                    </th>
                </tr>
                </thead>
                <tbody>

                <c:forEach items="${actionBean.redlistOccurrencesEurope}" var="occ">
                    <tr>
                        <td>${occ.country}</td>
                        <td>${occ.present}</td>
                        <td>${occ.currentAreaValue}</td>
                        <td>${occ.trendQuantity}</td>
                        <td>${occ.trendQuality}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            </c:if>
<%--        </div>--%>
<%--        <div>--%>

        </div>
        <div style="clear: left">
        <c:if test="${not empty actionBean.redlistOccurrencesSea}">
        <div>
                <table summary="Sea Occurrence" class="listing fullwidth">
                    <thead>
                    <tr>
                        <th width="30%" style="text-align: left; white-space: normal;" class="nosort">
                            Seas
                        </th>
                        <th width="15%" style="text-align: left; white-space: normal;" class="nosort">
                            Present or presence uncertain
                        </th>
                        <th width="15%" style="text-align: left; white-space: normal;" class="nosort">
                            Current area of habitat (Km<sup>2</sup>)
                        </th>
                        <th width="15%" style="text-align: left; white-space: normal;" class="nosort">
                            Recent trend in quantity (last 50 years)
                        </th>
                        <th width="15%" style="text-align: left; white-space: normal;" class="nosort">
                            Recent trend in quality (last 50 years)
                        </th>
                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach items="${actionBean.redlistOccurrencesSea}" var="occ" varStatus="stat">
                        <tr>
                            <td>${occ.sea}</td>
                            <c:if test="${stat.first}">
                                <td rowspan="${actionBean.redlistOccurrencesSea.size()}" style="vertical-align: middle">${occ.present}</td>
                                <td rowspan="${actionBean.redlistOccurrencesSea.size()}" style="vertical-align: middle">${occ.currentAreaValue}</td>
                                <td rowspan="${actionBean.redlistOccurrencesSea.size()}" style="vertical-align: middle">${occ.trendQuantity}</td>
                                <td rowspan="${actionBean.redlistOccurrencesSea.size()}" style="vertical-align: middle">${occ.trendQuality}</td>
                            </c:if>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>

        </div>
        </c:if>
        <div>
        <h4>Extent of Occurrence, Area of Occupancy and habitat area</h4>
        <table summary="Extent of Occurrence" class="listing fullwidth">
            <thead>
            <tr>
                <th width="15%" style="text-align: left; white-space: normal;" class="nosort"></th>
                <th width="15%" style="text-align: left; white-space: normal;" class="nosort">
                    Extent of Occurrence (EOO) (Km<sup>2</sup>)
                </th>
                <th width="15%" style="text-align: left; white-space: normal;" class="nosort">
                    Area of Occupancy (AOO)
                </th>
                <th width="15%" style="text-align: left; white-space: normal;" class="nosort">
                    Current estimated Total Area
                </th>
                <th width="40%" style="text-align: left; white-space: normal;" class="nosort">
                    Comment
                </th>
            </tr>
            </thead>
            <tbody>

            <tr>
                <td>EU28</td>
                <td>${actionBean.redlist.occurrenceEU28EOO}</td>
                <td>${actionBean.redlist.occurrenceEU28AOO}</td>
                <td>${actionBean.redlist.occurrenceEU28Area}</td>
                <td>${actionBean.redlist.occurrenceEU28Comment}</td>
            </tr>
            <tr>
                <td>EU28+</td>
                <td>${actionBean.redlist.occurrenceEU28PlusEOO}</td>
                <td>${actionBean.redlist.occurrenceEU28PlusAOO}</td>
                <td>${actionBean.redlist.occurrenceEU28PlusArea}</td>
                <td>${actionBean.redlist.occurrenceEU28PlusComment}</td>
            </tr>
            </tbody>
        </table>
        </div>
        </div>
    </div>
</stripes:layout-definition>