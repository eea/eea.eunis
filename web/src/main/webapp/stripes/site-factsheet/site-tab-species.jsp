<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>

<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />

<c:choose>
    <c:when test="${actionBean.totalSpeciesCount>0}">
    <div class="right-area" style="width:600px">
	<div class="species-change-view">
		<div onclick="changeSpeciesView(0);" class="species-list">
			<i class="eea-icon eea-icon-align-justify"></i><br/>List
		</div>
		<div onclick="changeSpeciesView(1);" class="species-gallery">
			<i class="eea-icon eea-icon-th"></i><br/>Gallery
		</div>
	</div>
	<br/>
	<!-- ---------------------------------- LIST VIEW ------------------------------- -->
	<div id="sites-species-list" style="display: none;">
            <c:if test="${actionBean.totalSpeciesCount>18}"><div class="scroll-auto" style="height: 567px; width: 100%; clear: both;"></c:if>
            <table summary="${eunis:cms(actionBean.contentManagement, 'ecological_information_fauna_flora')}" class="listing fullwidth table-inline">
                <thead>
                <tr>
                    <th scope="col">${eunis:cmsPhrase(actionBean.contentManagement, 'Natura 2000')}</th>
                    <th scope="col">${eunis:cmsPhrase(actionBean.contentManagement, 'Species scientific name')}</th>
                    <th scope="col">${eunis:cmsPhrase(actionBean.contentManagement, 'English common name')}</th>
                    <th scope="col">${eunis:cmsPhrase(actionBean.contentManagement, 'Species group')}</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${actionBean.allSiteSpecies}" var="specie">
                    <tr>
                        <td>${specie.natura2000Code}</td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty specie.url}"><a class="link-plain" href="${specie.url}">${specie.scientificName}</a></c:when>
                                <c:otherwise>${specie.scientificName}</c:otherwise>
                            </c:choose>
                        </td>
                        <td>${specie.commonName}</td>
                        <td align="center">
                                ${specie.group}
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        <c:if test="${actionBean.totalSpeciesCount>18}"></div></c:if>


	</div>
	<!-- ---------------------------------- GALERY VIEW ------------------------------- -->
	<div id="sites-species-gallery">
	<c:choose>
	<c:when test="${actionBean.totalSpeciesCount>0}">
		<div class="paginate">

            <c:forEach items="${actionBean.allSiteSpecies}" var="specie">
                <div class="photoAlbumEntry" style="padding-top: 0px;">
                    <a href="/species/${specie.source.idSpecies}">
		                <span class="photoAlbumEntryWrapper">
		                    <c:choose>
		                        <c:when test="${specie.speciesTypeId == 1 || specie.speciesTypeId == 3 || specie.speciesTypeId == 4}">
		                            <img src="images/species/${specie.source.idSpecies}/thumbnail.jpg" onerror="this.src='images/species/${eunis:getDefaultPicture(specie.group)}';"/>
                                </c:when>
                                <c:otherwise>
                                    <img src="images/sites/${specie.source.idSite}/thumbnail.jpg"/>
                                </c:otherwise>
                            </c:choose>
		                </span>
		                <span class="photoAlbumEntryTitle">
		                    ${specie.commonName}
		                	<span class="italics">${specie.scientificName} </span>
		                </span>
                    </a>
                </div>
            </c:forEach>

		</div>
    </c:when>
    <c:otherwise>
        ${eunis:cmsPhrase(actionBean.contentManagement, 'There are no species to be displayed')}
    </c:otherwise>
    </c:choose>
	</div>
</div>
	
<div class="right-area" style="width:300px">
    <div style="width: 100%">
    <h3>Species summary</h3>
    Nature directives' species in this site (${(actionBean.protectedSpeciesCount)})
		<table class="listing table-inline" width="100%">
            <thead>
            <tr>
                <th scope="col">${eunis:cmsPhrase(actionBean.contentManagement, 'Species group')}</th>
                <th scope="col">${eunis:cmsPhrase(actionBean.contentManagement, 'Number')}</th>
            </tr>
            </thead>
            <c:forEach items="${actionBean.speciesStatisticsSorted}" var="ss" varStatus="loop">
                <tr>
                    <td>${ss.key}</td>
                    <td>${ss.value}</td>
                </tr>
            </c:forEach>
		</table>
    </div>
</div>

    </c:when>
    <c:otherwise>
        ${eunis:cmsPhrase(actionBean.contentManagement, 'No information reported')}
        <script>
            $("#tab-species-accordion").addClass("nodata");
        </script>
    </c:otherwise>
</c:choose>
</stripes:layout-definition>