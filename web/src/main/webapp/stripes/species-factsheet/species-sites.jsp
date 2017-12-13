<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>



    <!-- species protection -->
    <a name="protected"></a>

    <c:choose>
        <c:when test="${fn:length(actionBean.speciesSitesTable) gt 0}">

                <div id="protected_sites_table" class="left-area protected-sites" style="height:460px">

                    <h3>Protected in the following Natura 2000 sites</h3>
                    <div class="scroll-auto" style="height: 404px">

                    <table id="listItem" summary="List of sites" class="sortable listing" style="width: 450px;">
                        <thead>
                        <tr>
                            <th class="dt_sortable">Sitecode</th>
                            <th class="dt_sortable">Country</th>
                            <th class="dt_sortable">Site name</th>
                            <th class="dt_sortable" style="width: 105px">Action</th>
                        </tr>
                        </thead>
                        <tbody>

						<c:forEach items="${actionBean.speciesSitesTable}" var="site" varStatus="loop">
							<tr class="${loop.index % 2 == 0 ? 'zebraodd' : 'zebraeven'}">
								
								
								<td>${ site.IDSite }</td>
                            	<td>${ site.areaNameEn }</td>
                            	<td><a href="${ site.siteNameUrl }" title="Open site factsheet">${ site.name }</a></td>
                            	<td>
									<a href="javascript:void(0);" onclick="setMapSiteId('${ site.IDSite }');">Map</a>
								</td>
							</tr>
						</c:forEach>
						
                        </tbody>
                    </table>
                    </div>
                    
                </div>
                

                <div class="right-area protected-sites-map">
                    <h3>Protected sites</h3>
                    <span class="bold">Please note the site map takes a while to display.</span>
                    <div id="sites-map" class="map-border">
                    	<iframe id="protectionMap" src="" width="100%" height="400"></iframe>
                    </div>
                </div>
                <script>
                    addReloadOnDisplay("speciesSitesPane", "protectionMap", "https://maps.eea.europa.eu/Filtermap/v1/?webmap=${actionBean.context.natura2000MapId}&SpeciesCode=${actionBean.n2000id}&zoomTo=true");
                </script>


		        <script type="text/javascript">

		            function setMapSiteId(sitecode){
		            	document.getElementById('protectionMap').src='https://maps.eea.europa.eu/Filtermap/v1/?webmap=${actionBean.context.natura2000MapId}&SpeciesCode=${actionBean.n2000id}&zoomtofeat=SITECODE='+sitecode;
		            }

		        </script>


                <!-- END species protection -->


    </c:when>
        <c:otherwise>
            ${eunis:cmsPhrase(actionBean.contentManagement, 'Not available')}
            <script>
                $("#sites-accordion").addClass("nodata");
            </script>
        </c:otherwise>
    </c:choose>
                
                
                
                
</stripes:layout-definition>
