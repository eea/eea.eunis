<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
<c:choose>
    <c:when test="${(not empty actionBean.diagnosticSpecies) or (not empty actionBean.constantSpecies) or (not empty actionBean.dominantSpecies)}">
        <div class="diagnostic-wrapper">
            Diagnostic species
            <c:forEach items="${actionBean.diagnosticSpecies}" var="specie">
                <div class="diagnostic-species">
                    <span class="photoAlbumEntryWrapper">
                    <a href="/species/${specie.source.idSpecies}">
                    <img src="images/species/${specie.source.idSpecies}/thumbnail.jpg" onerror="this.src='images/species/${eunis:getDefaultPicture(specie.group)}';"/>
                    </a>
                    </span>
                    <span class="photoAlbumEntryTitle">
                        ${specie.commonName}

                        <span class="italics">${specie.scientificName} </span>
                    </span>
                </div>
            </c:forEach>
        </div>
        <div class="constant-wrapper">
        Constant species
        <c:forEach items="${actionBean.constantSpecies}" var="specie">
            <div class="constant-species">
                <span class="photoAlbumEntryWrapper">
                <a href="/species/${specie.source.idSpecies}">
                    <img src="images/species/${specie.source.idSpecies}/thumbnail.jpg" onerror="this.src='images/species/${eunis:getDefaultPicture(specie.group)}';"/>
                </a>
                </span>
                <span class="photoAlbumEntryTitle">
                    ${specie.commonName}

                    <span class="italics">${specie.scientificName} </span>
                </span>
            </div>
        </c:forEach>
        </div>
        <div class="dominant-wrapper">
            Dominant species
            <c:forEach items="${actionBean.dominantSpecies}" var="specie">
                <div class="dominant-species">
                    <span class="photoAlbumEntryWrapper">
                    <a href="/species/${specie.source.idSpecies}">
                        <img src="images/species/${specie.source.idSpecies}/thumbnail.jpg" onerror="this.src='images/species/${eunis:getDefaultPicture(specie.group)}';"/>
                    </a>
                    </span>
                    <span class="photoAlbumEntryTitle">
                        ${specie.commonName}

                        <span class="italics">${specie.scientificName} </span>
                    </span>
                </div>
            </c:forEach>
        </div>
    </c:when>
    <c:otherwise>
        ${eunis:cmsPhrase(actionBean.contentManagement, 'Not available')}
        <script>
            $("#indicator-species-accordion").addClass("nodata");
        </script>
    </c:otherwise>
</c:choose>
</stripes:layout-definition>