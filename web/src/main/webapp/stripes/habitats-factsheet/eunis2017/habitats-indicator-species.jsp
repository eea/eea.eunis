<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
    <c:choose>
        <c:when test="${(not empty actionBean.diagnosticSpecies) or (not empty actionBean.constantSpecies) or (not empty actionBean.dominantSpecies)}">
            <div class="species-container">
                <div displayed="7" trigger_for="1" class="species-header tab-visible">Diagnostic species</div>
                <div displayed="7" trigger_for="2" class="species-header">Constant species</div>
                <div displayed="7" trigger_for="3" class="species-header">Dominant species</div>

                <div tab="1" id="pagination1" class="diagnostic-wrapper tab-body tab-visible">
                    <c:forEach items="${actionBean.diagnosticSpecies}" var="specie">
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
                        <tr><th>Species scientific name</th>
                        <th>English common name</th>
                        <th>Species group</th></tr>
                        </thead>
                        <c:forEach items="${actionBean.diagnosticSpecies}" var="specie">
                            <tr>
                                <td><a href="/species/${specie.source.idSpecies}">${specie.scientificName}</a></td>
                                <td>${specie.commonName}</td>
                                <td>${specie.group}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
                <div tab="2" id="pagination2" class="constant-wrapper tab-body">
                    <c:forEach items="${actionBean.constantSpecies}" var="specie">
                        <div class="constant-species species-item">
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
                        <tr><th>Species scientific name</th>
                        <th>English common name</th>
                        <th>Species group</th></tr>
                        </thead>
                        <c:forEach items="${actionBean.constantSpecies}" var="specie">
                            <tr>
                                <td><a href="/species/${specie.source.idSpecies}">${specie.scientificName}</a></td>
                                <td>${specie.commonName}</td>
                                <td>${specie.group}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
                <div tab="3" id="pagination3" class="dominant-wrapper tab-body">
                    <c:forEach items="${actionBean.dominantSpecies}" var="specie">
                        <div class="dominant-species species-item">
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
                        <tr><th>Species scientific name</th>
                        <th>English common name</th>
                        <th>Species group</th></tr>
                        </thead>
                        <c:forEach items="${actionBean.dominantSpecies}" var="specie">
                            <tr>
                                <td><a href="/species/${specie.source.idSpecies}">${specie.scientificName}</a></td>
                                <td>${specie.commonName}</td>
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
                $("#indicator-species-accordion").addClass("nodata");
            </script>
        </c:otherwise>
    </c:choose>
</stripes:layout-definition>