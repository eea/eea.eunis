<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>

<div class="left-area protected-sites-map">
    <c:if test="${not actionBean.typeDiploma}">
        <img src="https://fme.discomap.eea.europa.eu/fmedatastreaming/ArcGisOnline/getStaticImage.fmw?webmap=0b2680c2bc544431a9a97119aa63d707&amp;width=480&amp;height=400&amp;SiteCode=${actionBean.idsite}" style="width: 480px; height: 400px;">
        <a class="interactive-map-more discreet" href="${ actionBean.pageUrl }#interactive_map" onclick="if($('#interactive_map ~ h2').attr('class').indexOf('current')==-1) $('#interactive_map ~ h2').click(); ">Interactive map</a>
        <div style="width:99.0%; height:20px;">
        </div>
        <a target="_blank" class="interactive-map-more" href="http://images.google.com/images?q=${actionBean.siteName}">More images</a>
    </c:if>
</div>

</stripes:layout-definition>
