<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
    <c:choose>
        <c:when test="${not empty actionBean.species}">
            <div class="species-container">
                <div style="display: none" displayed="8" trigger_for="1" class="species-header tab-visible">Species</div>

                <div tab="1" id="pagination1" class="diagnostic-wrapper tab-body tab-visible">
                    <c:forEach items="${actionBean.species}" var="specie">
                        <div class="diagnostic-species species-item">
                       <span class="photoAlbumEntryWrapper">
                       <a href="/species/${specie.source.idSpecies}">
                       <img src="images/species/${specie.source.idSpecies}/thumbnail.jpg" onerror="this.src='images/species/${eunis:getDefaultPicture(specie.group)}';"/>
                       </a>
                       </span>
                            <span class="photoAlbumEntryTitle">
                               <span class="photo-subtitle">${specie.group}</span>
                               <span class="italics">${specie.scientificName} </span>
                           </span>
                        </div>
                    </c:forEach>
                    <span class="view-switch">
                       <button class="btn btn-table"></button>
                       <button class="btn btn-list selected"></button>
                   </span>
                    <table class="table-striped modified table-full display-hidden">
                        <thead>
                        <tr>
                            <th>Species scientific name</th>
                            <th>English common name</th>
                            <th>Species group</th>
                        </tr>
                        </thead>
                        <c:forEach items="${actionBean.species}" var="specie">
                            <tr>
                                <td><a href="/species/${specie.source.idSpecies}">${specie.scientificName}</a></td>
                                <td><a href="/species/${specie.source.idSpecies}">${specie.commonName}</a></td>
                                <td>${specie.group}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            ${eunis:cmsPhrase(actionBean.contentManagement, 'Not available')}
            <script>
                $("#species-accordion").addClass("nodata");
            </script>
        </c:otherwise>
    </c:choose>
</stripes:layout-definition>