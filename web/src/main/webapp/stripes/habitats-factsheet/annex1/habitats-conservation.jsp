<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
<c:choose>
    <c:when test="${actionBean.factsheet.code2000 != 'na' and actionBean.factsheet.habitatLevel eq 3}">
    <div>
        <h3>EU conservation status</h3>
        <p>Conservation status assesses every six years and for each biogeographical region the condition of habitats and species compared to the favourable status as described in the Habitats Directive. The map shows the 2013 assessments.</p>

        <div class="left-area" style="width: 620px;">


        <div class="map-border" style="width: 600px;">
            <iframe id="habitatStatusMap" src="" height="400px" width="100%"></iframe>
        </div>

        <script>
            addReloadOnDisplay("conservationPane", "habitatStatusMap", "http://discomap.eea.europa.eu/map/Filtermap/?webmap=e7e4c618f20344a8a148c5f63ec8766b&Code=${actionBean.factsheet.code2000}");
        </script>


     </div>
     <div class="right-area" style="width: 350px;">
         <div>
             <table>
                 <tr><td class="discreet"><div class="legend-color conservation-legend-favorable"> </div> <span class="bold">Favourable</span>: A habitat is in a situation where it is prospering and with good prospects to do so in the future as well</td></tr>
                 <tr><td class="discreet"><div class="legend-color conservation-legend-inadequate"> </div> <span class="bold">Unfavourable-Inadequate</span>: A habitat is in a situation where a change in management or policy is required to return the habitat to favourable status but there is no danger of disappearance in the foreseeable future</td></tr>
                 <tr><td class="discreet"><div class="legend-color conservation-legend-bad"> </div> <span class="bold">Unfavourable-Bad</span>: A habitat is in serious danger of disappearing (at least regionally)</td></tr>
                 <tr><td class="discreet"><div class="legend-color conservation-legend-unknown"> </div> <span class="bold">Unknown</span>: There is insufficient information available to allow an assessment</td></tr>
             </table>
         </div>

         <div class="discreet">
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
             <div class="table-definition">
            <span class="table-definition-target standardButton float-left" style="margin-top: 20px;">
                    ${eunis:cmsPhrase(actionBean.contentManagement, 'Full table details')}
            </span>

                 <c:forEach items="${actionBean.conservationStatusQueries}" var="query">
                 <div class="table-definition-body visualClear float-right" style="display:none;">
                     <div style="margin-top:20px">
                         <p style="font-weight:bold">${eunis:cmsPhrase(actionBean.contentManagement, query.title)}:</p>
                         <c:set var="queryId" value="${query.id}"/>

                         <c:if test="${fn:length(actionBean.conservationStatusQueryResultRows[queryId]) gt 10}">
                         <div class="scroll-auto" style="height: 400px">
                             </c:if>
                             <div >
                                     <%--<span class="pagebanner">${fn:length(actionBean.conservationStatusQueryResultRows[queryId])} item<c:if test="${fn:length(actionBean.conservationStatusQueryResultRows[queryId]) != 1}">s</c:if> found.</span>--%>
                                 <table class="datatable listing inline-block">
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
                             <c:if test="${fn:length(actionBean.conservationStatusQueryResultRows[queryId]) gt 10}">
                         </div>
                         </c:if>
                     </div>
                     </c:forEach>
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
