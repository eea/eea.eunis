<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
    <!-- quick facts -->
    <!--  Description on the left -->
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
                            <c:if test="${not empty pic.description}">
                                title="${pic.description}"
                            </c:if>
                    />
                </li>
            </c:forEach>
        </ul>

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
                    <th>${eunis:cmsPhrase(actionBean.contentManagement, 'Red List habitat type')}</th>
                    <td>code <span class="bold">${eunis:formatString(actionBean.factsheet.eeaCode, '')}</span></td>
                </tr>
            </tbody>

                <tbody>
                    <tr>
                        <th>Threat status</th>
                        <td></td>
                    </tr>
                    <tr>
                        <th class="normalfont">Europe</th>
                        <td>${actionBean.redlist.categoryEU28Plus}</td>
                    </tr>
                    <tr>
                        <th class="normalfont">EU</th>
                        <td>${actionBean.redlist.categoryEU28}</td>
                    </tr>
                </tbody>
            <tbody>
                <tr>
                    <th>Relation to</th>
                    <td>
                        <ul style="margin: 0 !important">
                        <c:if test="${actionBean.habitatsDirective}">
                            <li><a href="${ actionBean.pageUrl }#legal" onclick="openSection('legal');"><span class="bold">Annex I habitat types</span></a> (EU Habitats Directive)</li>
                        </c:if>
                        <c:if test="${actionBean.relResolution4}">
                            <li><a href="${ actionBean.pageUrl }#legal" onclick="openSection('legal');"><span class="bold">Resolution 4 habitat type</span></a> (Bern Convention)</li>
                        </c:if>
                        <c:if test="${not empty actionBean.relEunis2007}">
                            <li>${actionBean.relEunis2007} - <a href="${ actionBean.pageUrl }#legal" onclick="openSection('legal');"><span class="bold">EUNIS habitat classification (2007)</span></a></li>
                        </c:if>
                        </ul>
                    </td>
                </tr>
            </tbody>
            <tbody>
                <tr>
                    <th>Source</th>
                    <td><a href="https://forum.eionet.europa.eu/european-red-list-habitats/library/">European Red List habitat factsheet</a> </td>
                </tr>
                <tr>
                    <th class="normalfont"></th>
                    <td><a href="https://ec.europa.eu/environment/nature/knowledge/redlist_en.htm">European Red List of habitats reports</a></td>
                </tr>
                <tr>
                    <th class="normalfont"></th>
                    <td><a href="https://www.eea.europa.eu/data-and-maps/data/european-red-list-of-habitats">European Red List of habitats (Excel table)</a></td>
                </tr>
            </tbody>


            <c:if test="${actionBean.factsheet.eunis2017 and actionBean.resolution4Relation}">
                <tbody>
                <tr>
                    <th class="normalfont">Relation to</th>
                    <td>
                        <a href="${ actionBean.pageUrl }#legal" onclick="openSection('legal');"><span class="bold">Resolution 4 habitat type</span></a> (used for designation of Emerald sites)
                    </td>
                </tr>
                </tbody>
            </c:if>


        </table>

    </div>

    <!-- END quick facts -->
</stripes:layout-definition>