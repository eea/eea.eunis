<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
<c:choose>
    <c:when test="${not empty actionBean.species}">


        <style>
            .cell-title {
                font-weight: bold;
            }

            .cell-effort-driven {
                text-align: center;
            }
            .table-inline {
                display: inline-table !important;
            }

            .species-change-view {
                text-align: right;
            }

            .species-list {
                background: url("sprite-eeaimages.png") no-repeat scroll left center rgba(0, 0, 0, 0);
                background-position: -447px -163px;

            }

            .species-gallery {
                background: url("sprite-eeaimages.png") no-repeat scroll left center rgba(0, 0, 0, 0);
                background-position: -427px -163px;
            }

            .species-gallery, .species-list {
                cursor: pointer;
                width: 10%;
                float:right;
                text-align: center;
            }

            .species-list:hover {
                background-position: -447px -145px;
            }

            .species-gallery:hover {
                background-position: -427px -145px;
            }

        </style>
        <script>
            /*
             @type,0 - list 1 - gallery
             */
            function changeSpeciesView(type) {
                var gallery = jQuery("#sites-species-gallery");
                var list    = jQuery("#sites-species-list");
                if( (gallery.is(":visible") && type==0) || (list.is(":visible") && type==1) ) {
                    jQuery("#sites-species-gallery").toggle();
                    jQuery("#sites-species-list").toggle();
                }
            }

        </script>

        <div class="right-area" style="width:860px">
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
                <c:if test="${fn:length(actionBean.species)>12}"><div class="scroll-auto" style="height: 380px; width: 100%; clear: both;"></c:if>
                <table summary="${eunis:cms(actionBean.contentManagement, 'ecological_information_fauna_flora')}" class="listing fullwidth table-inline">
                    <thead>
                    <tr>
                        <%--<th scope="col">${eunis:cmsPhrase(actionBean.contentManagement, 'Natura 2000')}</th>--%>
                        <th scope="col">${eunis:cmsPhrase(actionBean.contentManagement, 'Species scientific name')}</th>
                        <th scope="col">${eunis:cmsPhrase(actionBean.contentManagement, 'English common name')}</th>
                        <th scope="col">${eunis:cmsPhrase(actionBean.contentManagement, 'Species group')}</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${actionBean.species}" var="specie">
                        <tr>
                            <%--<td>${specie.natura2000Code}</td>--%>
                            <td>
                                <a class="link-plain" href="/species/${specie.source.idSpecies}">${specie.scientificName}</a>
                            </td>
                            <td>${specie.commonName}</td>
                            <td align="center">
                                ${specie.group}
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <c:if test="${fn:length(actionBean.species)>12}"></div></c:if>


            </div>
            <!-- ---------------------------------- GALERY VIEW ------------------------------- -->
            <div id="sites-species-gallery">
                <div class="paginate">
                    <c:forEach items="${actionBean.species}" var="specie">
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