<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
<c:choose>
    <c:when test="${actionBean.factsheet.code2000 != 'na' and actionBean.factsheet.habitatLevel eq 3}">
    <div>
        <h3>EU conservation status</h3>
        <p>Conservation status assesses every six years and for each biogeographical
            region the condition of habitats and species compared to the favourable status
            as described in the Habitats Directive. The map shows the 2007-2012
            assessments as reported by EU Member State. Assessments are further
            detailed in the summary document available behind the link below.</p>

        <div class="left-area" style="width: 620px;">


        <div class="map-border" style="width: 600px;">
            <iframe id="habitatStatusMap" src="" height="400px" width="100%"></iframe>
        </div>

        <script>
            addReloadOnDisplay("conservationPane", "habitatStatusMap", "https://maps.eea.europa.eu/EEAViewer/?appid=fcb24d064e7b4a2b83da7e4cabed3cce&showLayers=HabitatsDirective_ART_17_WMS_version_2020_08_public_6171;HabitatsDirective_ART_17_WMS_version_2020_08_public_6171_0;HabitatsDirective_ART_17_WMS_version_2020_08_public_6171_2&region=%25&zoomto=true&embed=true&habitatcode=${actionBean.factsheet.code2000}");
        </script>


     </div>
     <div class="right-area" style="width: 350px;">
         <div>
             <table>
                 <tr><td class="discreet"><div class="legend-color conservation-legend-favorable"> </div> <span class="bold">Favourable</span>: A habitat is in a situation where it is prospering and with good prospects to do so in the future as well</td></tr>
                 <tr><td class="discreet"><div class="legend-color conservation-legend-inadequate"> </div> <span class="bold">Unfavourable-Inadequate</span>: A habitat is in a situation where a change in management or policy is required to return the habitat to favourable status but there is no danger of disappearance in the foreseeable future</td></tr>
                 <tr><td class="discreet"><div class="legend-color conservation-legend-bad"> </div> <span class="bold">Unfavourable-Bad</span>: A habitat is in serious danger of disappearing (at least regionally)</td></tr>
                 <tr><td class="discreet"><div class="legend-color conservation-legend-unknown"> </div> <span class="bold">Unknown</span>: The information available for the habitat type is scarce and does not allow a proper assessment of its conservation status</td></tr>
             </table>
         </div>

         <div class="discreet">
             <c:if test="${not empty actionBean.conservationStatusPDF or not empty actionBean.conservationStatus}">
                 Sources:
                 <ul>
<%--                     <c:if test="${not empty actionBean.conservationStatusPDF}">--%>
<%--                         <li>--%>
<%--                             <a href="${actionBean.conservationStatusPDF.url}">Conservation Status 2007-2012 – summary</a>--%>
<%--                         </li>--%>
<%--                     </c:if>--%>
                     <c:if test="${not empty actionBean.conservationStatus}">
                         <li>
                             <a href="${actionBean.conservationStatus.url}">${actionBean.conservationStatus.name}</a>
                         </li>
                     </c:if>
                 </ul>
             </c:if>
         </div>

         <c:if test="${not empty actionBean.conservationByCountry}">
             <div class="table-definition">
            <span class="table-definition-target standardButton float-left" style="margin-top: 20px;">
                    ${eunis:cmsPhrase(actionBean.contentManagement, 'Full table details')}
            </span>

                 <div class="table-definition-body visualClear float-right" style="display:none;">
                     <div style="margin-top:20px">
                         <p style="font-weight:bold">Conservation status (2013-2018) of habitats (Habitats Directive, Article 17) by EU Member State::</p>

                         <c:if test="${fn:length(actionBean.conservationByCountry) gt 10}">
                            <div class="scroll-auto" style="height: 400px">
                         </c:if>
                             <div >
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
             </div>
         </c:if>

     </div>




    </div>
    </c:when>
    <c:otherwise>
        ${eunis:cmsPhrase(actionBean.contentManagement, 'Not available')}
        <script>
        $("#conservation-accordion").addClass("nodata");
        </script>
    </c:otherwise>
</c:choose>

</stripes:layout-definition>
