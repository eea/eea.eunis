<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>


    <c:choose>
        <c:when test="${not empty actionBean.mapUrl}">
            <div style="overflow-x:auto">
                <iframe id="interactive-map-iframe" class="map-border" height="650" width="100%" src="${actionBean.mapUrl}"></iframe>
            </div>
        </c:when>
        <c:otherwise>
            ${eunis:cmsPhrase(actionBean.contentManagement, 'No distribution map available')}
            <script>
            $("#map-accordion").addClass("nodata");
            </script>
        </c:otherwise>
    </c:choose>

</stripes:layout-definition>