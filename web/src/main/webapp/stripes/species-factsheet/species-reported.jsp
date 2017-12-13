<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>

<script type="text/javascript">djConfig = { parseOnLoad:true };</script>
<script type="text/javascript" src="${actionBean.context.distributionArcgisScript}"></script>




<script type="text/javascript">
		//<![CDATA[
			dojo.require("esri.map");
			dojo.require("esri.tasks.geometry");

			dojo.declare("my.GBIFLayer", esri.layers.DynamicMapServiceLayer, {

				constructor: function() {
					this.initialExtent = this.fullExtent = new esri.geometry.Extent({"xmin":-3222779.52198856,"ymin": 2736409.05279762,"xmax":7105006.15070147,"ymax": 11615622.1895779,"spatialReference":{"wkid":3857}})
                    this.spatialReference = new esri.SpatialReference({wkid:3857});

					this.loaded = true;
					this.onLoad(this);
				},

				getImageUrl: function(extent, width, height, callback) {
					var params = {
						request:"GetMap",
						transparent:true,
						format:"image/png",
						version:"1.1.1",
						layers:"gbif:tabDensityLayer",
						styles: "",
						exceptions: "application%2Fvnd.ogc.se_inimage",
						filter: "<Filter><PropertyIsEqualTo><PropertyName>url</PropertyName><Literal><![CDATA[https://data.gbif.org/maplayer/taxon/${actionBean.gbifCode}]]></Literal></PropertyIsEqualTo></Filter>",

						//changing values
						bbox:extent.xmin + "," + extent.ymin + "," + extent.xmax + "," + extent.ymax,
						srs: "EPSG:" + extent.spatialReference.wkid,
						width: width,
						height: height
					};
					callback("https://ogc.gbif.org/wms?" + dojo.objectToQuery(params));
				}
			})

			dojo.declare("my.FAODistLayer", esri.layers.DynamicMapServiceLayer, {

				constructor: function() {
					this.initialExtent = this.fullExtent = new esri.geometry.Extent({"xmin":-3222779.52198856,"ymin": 2736409.05279762,"xmax":7105006.15070147,"ymax": 11615622.1895779,"spatialReference":{"wkid":3857}})
                    this.spatialReference = new esri.SpatialReference({wkid:3857});

					this.loaded = true;
					this.opacity = 0.4;
					this.onLoad(this);
				},

				getImageUrl: function(extent, width, height, callback) {
					var params = {
						request:"GetMap",
						transparent:true,
						format:"image/png",
						version:"1.1.1",
						layers:"fifao:SPECIES_DIST",
						styles: "",
						exceptions: "application/vnd.ogc.se_inimage",
						cql_filter: "ALPHACODE='${actionBean.faoCode}'",

						//changing values
						bbox:extent.xmin + "," + extent.ymin + "," + extent.xmax + "," + extent.ymax,
						srs: "EPSG:" + extent.spatialReference.wkid,
						width: width,
						height: height
					};

					callback("https://www.fao.org/figis/geoserver/wms?" + dojo.objectToQuery(params));
				}
			})

			var layer_dist, layer_range, report_map, n2000layer, cddalayer, bio_regions_layer, river_basin_districts_layer,visible = [];
			var faoDistLayer, gbifLayer;

			var symbol = new esri.symbol.SimpleFillSymbol();
		    symbol.setColor(new dojo.Color([150,150,150,1]));

			function init() {
				var initExtent =  new esri.geometry.Extent({"xmin":-3222779.52198856,"ymin": 2736409.05279762,"xmax":7105006.15070147,"ymax": 11615622.1895779,"spatialReference":{"wkid":3857}})
				report_map = new esri.Map("report-map",{extent:initExtent});

			    veTileLayer = new esri.virtualearth.VETiledLayer({
			    bingMapsKey: 'AngrFRWkKXOKP4DuIx_T3wGWalupu63oFfJcDJHqa5_QA34tELFodeuc97CMw5us',
			    mapStyle: esri.virtualearth.VETiledLayer.MAP_STYLE_ROAD});
			    report_map.addLayer(veTileLayer);

			    n2000layer = new esri.layers.GraphicsLayer();

                cddalayer = new esri.layers.ArcGISDynamicMapServiceLayer("${actionBean.context.distributionCDDALayer}");

                bio_regions_layer = new esri.layers.ArcGISDynamicMapServiceLayer("${actionBean.context.distributionBioRegionsLayer}");
                bio_regions_layer.opacity = 0.5;

                river_basin_districts_layer = new esri.layers.ArcGISDynamicMapServiceLayer("${actionBean.context.distributionRiverBasinLayer}");
                river_basin_districts_layer.opacity = 0.6;

                //Add species parameter to filter Natura 2000
                filterNatura2000('${actionBean.scientificName}')

                faoDistLayer = new my.FAODistLayer();
                gbifLayer = new my.GBIFLayer();
                gbifLayer.opacity = 0.5;

                // Species Distribution layer
                var imageParameters_dist = new esri.layers.ImageParameters();
                var layerDefs_dist = [];
                var isDistributionLayerExist = ${actionBean.distributionLayer};
                if(isDistributionLayerExist){
	                layerDefs_dist[4] = "Type = 'species' and Speciesname = '${actionBean.scientificName}'";
	                imageParameters_dist.layerDefinitions = layerDefs_dist;
	                imageParameters_dist.layerIds = [4];
	                imageParameters_dist.layerOption = esri.layers.ImageParameters.LAYER_OPTION_SHOW;
	                imageParameters_dist.transparent = true;
	                layer_dist = new esri.layers.ArcGISDynamicMapServiceLayer("${actionBean.context.distributionSpeciesLayer}", {"imageParameters":imageParameters_dist});
                }

                // Species Range layer
                var imageParameters_range = new esri.layers.ImageParameters();
                var layerDefs_range = [];
                var isRangeLayerExist = ${actionBean.rangeLayer};
                if (isRangeLayerExist){
	                layerDefs_range[1] = "Type = 'species' and Speciesname = '${actionBean.scientificName}'";
	                imageParameters_range.layerDefinitions = layerDefs_range;
	                imageParameters_range.layerIds = [1];
	                imageParameters_range.layerOption = esri.layers.ImageParameters.LAYER_OPTION_SHOW;
	                imageParameters_range.transparent = true;
	                layer_range = new esri.layers.ArcGISDynamicMapServiceLayer("${actionBean.context.distributionSpeciesLayer}", {"imageParameters":imageParameters_range});
                }
                if(isDistributionLayerExist) {
                    report_map.addLayer(layer_dist);
                } else {
                    report_map.addLayer(gbifLayer);
                }
			}

			///////////////////

            var objIDs;
		    function filterNatura2000(specie){
		        //build query task
                var queryTask = new esri.tasks.QueryTask("${actionBean.context.distributionSpeciesN2000Layer}");

                //build query filter
                var query = new esri.tasks.Query();
                query.returnGeometry = false;
                query.outFields = ["OBJECTID"];
                query.where = "LATINNAME = '" + specie + "'";

                //Can listen for onComplete event to process results or can use the callback option in the queryTask.execute method.
                dojo.connect(queryTask, "onComplete", function(featureSet) {
                    dojo.forEach(featureSet.features,function(feature){

                        var speciesTable = new esri.tasks.QueryTask("${actionBean.context.distributionSpeciesN2000Layer}");

                        var relatedQuery = new esri.tasks.RelationshipQuery();
                        relatedQuery.outFields = ["OBJECTID"];
                        relatedQuery.relationshipId = 5;
                        relatedQuery.objectIds = [featureSet.features[0].attributes.OBJECTID];
                        dojo.connect(speciesTable, "onExecuteRelationshipQueryComplete", function(relatedFeatureSets) {
                            objIDs = new Array();

                            dojo.forEach(relatedFeatureSets[featureSet.features[0].attributes.OBJECTID].features, function(feature){
                                objIDs.push(feature.attributes.OBJECTID);
                            });
                            processResults(objIDs);

                        });
                        speciesTable.executeRelationshipQuery(relatedQuery);
                    });
                });
                queryTask.execute(query);
            }

		    var i = 0;
		    function processResults(){
		        var arrayObjectIds = new Array();
		        var j=0;
		        for (i; j<4; i++){
		            if (i >= objIDs.length){
		                break;
		            }
		            arrayObjectIds.push(objIDs[i]);

		            j++;
		        }
		        if (arrayObjectIds.length > 0){
		            addGraphics(arrayObjectIds);
		        }else{
		            return;
		        }
		    }

		    var sfs = new esri.symbol.SimpleFillSymbol(esri.symbol.SimpleFillSymbol.STYLE_SOLID,
		    	    new esri.symbol.SimpleLineSymbol(esri.symbol.SimpleLineSymbol.STYLE_NULL, new dojo.Color([255,0,0]), 2),
		    	    new dojo.Color([255,0,0,0.75]));

		    function addGraphics(objIDs){
		        var queryTask = new esri.tasks.QueryTask("${actionBean.context.distributionSpeciesN2000QueryLayer}");

		        var relatedQuery = new esri.tasks.RelationshipQuery();
		        relatedQuery.outFields = ["SITECODE", "SITENAME"];
		        relatedQuery.returnGeometry = true;
		        relatedQuery.relationshipId = 6;

		        relatedQuery.objectIds = objIDs;
		        dojo.connect(queryTask, "onExecuteRelationshipQueryComplete", function(relatedFeatureSets) {
                    dojo.forEach(objIDs, function(id){
                        dojo.forEach(relatedFeatureSets[id].features, function(feature){
                            n2000layer.add(new esri.Graphic(feature.geometry, sfs, feature.attributes, new esri.InfoTemplate("Natura 2000 site", "<span style='font-weight:bold'>Site code</span>: ${SITECODE}<br><span style='font-weight:bold'>Site name</span>: ${SITENAME}")));
                        });
                    });
                    processResults();
                });
		        queryTask.executeRelationshipQuery(relatedQuery);
            }

			////////////////////

			function updateLayerVisibility(id) {
				var input = document.getElementById(id);
				if (input.checked) {
					if (id == 'distribution') {
						report_map.addLayer(layer_dist);
					} else if (id == 'range') {
						report_map.addLayer(layer_range);
					} else if (id == 'fao') {
						report_map.addLayer(faoDistLayer);
					} else if (id == 'natura') {
						report_map.addLayer(n2000layer);
					} else if (id == 'cdda') {
						report_map.addLayer(cddalayer);
					} else if (id == 'bio_regions') {
						report_map.addLayer(bio_regions_layer);
					} else if (id == 'river_basin') {
						report_map.addLayer(river_basin_districts_layer);
					} else if (id == 'gbif') {
						report_map.addLayer(gbifLayer);
					}
				} else {
					if (id == 'distribution') {
						report_map.removeLayer(layer_dist);
					} else if (id == 'range') {
						report_map.removeLayer(layer_range);
					} else if (id == 'fao') {
						report_map.removeLayer(faoDistLayer);
					} else if (id == 'natura') {
						report_map.removeLayer(n2000layer);
					} else if (id == 'cdda') {
						report_map.removeLayer(cddalayer);
					} else if (id == 'bio_regions') {
						report_map.removeLayer(bio_regions_layer);
					} else if (id == 'river_basin') {
						report_map.removeLayer(river_basin_districts_layer);
					} else if (id == 'gbif') {
						report_map.removeLayer(gbifLayer);
					}
				}
			}
//			dojo.addOnLoad(init);
		//]]>
		</script>





                <!-- species reported -->
                <div class="reported-areas">
		    <a name="species-reported"></a>
                    <%--<h2 class="visualClear">${eunis:cmsPhrase(actionBean.contentManagement, 'Where can the species be found?')}</h2>--%>
                    <div class="left-area reported-areas-fieldset">
                        <fieldset>
                            <legend><strong>Select layer</strong></legend>
			    
							<c:if test="${actionBean.distributionLayer}">
								<label for="distribution">
										<input type="checkbox" class="list_item" id="distribution" onclick="updateLayerVisibility('distribution');" checked="checked"/>
										Species distribution (2006, reported by EU Member States under Habitats Directive 92/43/EEC)
								</label>
								<p class="discreet">Layer shows this species distribution which marks roughly where the species is found. Data source: <a href="http://www.eea.europa.eu/data-and-maps/data/article-17-database-habitats-directive-92-43-eec">Article 17 database</a>.</p>
							</c:if>
							
							<c:if test="${not empty actionBean.faoCode}">
								<label for="fao">
									<input type="checkbox" class="list_item" id="fao" onclick="updateLayerVisibility('fao');"/>
									Species distribution (reported by FAO) <!-- TODO: To add indicative year of last update -->
								</label>
								<p class="discreet">Layer shows this species distribution which marks roughly where the species is found. Data source: <a href="http://www.fao.org/figis/geoserver/factsheets/species.html">FAO Aquatic Species Distribution Map Viewer</a> © FAO</p>
							</c:if>

                            <c:if test="${actionBean.rangeLayer}">
                                    <label for="range">
                                        <input type="checkbox" class="list_item" id="range" onclick="updateLayerVisibility('range');"/>
                                        Species range (2006, reported by EU Member States under Habitats Directive 92/43/EEC)
                                    </label>
                                <p class="discreet">
                                    Layer shows this species distribution which marks roughly where the species is found. Data source: <a href="http://www.eea.europa.eu/data-and-maps/data/article-17-database-habitats-directive-92-43-eec">Article 17 database</a>.</p>
                            </c:if>

                            <c:if test="${not empty actionBean.gbifCode}">
								<label for="gbif">
									<input type="checkbox" class="list_item" id="gbif" onclick="updateLayerVisibility('gbif');"/>
									Single observations (GBIF, Global Biodiversity Information Facility)
								</label>
								<p class="discreet">Layer show observations of this species are given by museum collections and other scientific databases. Data source: <a href="http://data.gbif.org/species/${actionBean.gbifCode}">GBIF</a></p>
							</c:if>

                            <%--Hidden by #19773--%>
							<%--<label for="natura">--%>
								<%--<input type="checkbox" class="list_item" id="natura" onclick="updateLayerVisibility('natura');"/>--%>
								<%--Protected sites (2011, Natura 2000 network)--%>
							<%--</label>--%>
							<%--<p class="discreet">Layer shows protected sites aiming to protect this species at the European level. Data source: <a href="http://www.eea.europa.eu/data-and-maps/data/natura-2">Natura 2000 database</a>.</p>--%>
							</fieldset>
							
							
							<fieldset>
	                            <legend><strong>Base map - not species related</strong></legend>
								<label for="cdda">
									<input type="checkbox" class="list_item" id="cdda" onclick="updateLayerVisibility('cdda');"/>
									Protected areas - all nationally designated areas (2011, CDDA)
								</label>
								<p class="discreet">Nationally designated areas are protected areas which directly or indirectly create protected areas. 
								Data source: <a href="http://www.eea.europa.eu/data-and-maps/data/nationally-designated-areas-national-cdda-7">CDDA database</a>.</p>

								<label for="bio_regions">
									<input type="checkbox" class="list_item" id="bio_regions" onclick="updateLayerVisibility('bio_regions');"/>
									Biogeographical regions (2012)
								</label>
								<p class="discreet">Biogeographical regions are defined by their <a href="http://www.eea.europa.eu/publications/report_2002_0524_1549099">ecological and geographical context</a>. 
								Data source: <a href="http://www.eea.europa.eu/data-and-maps/data/biogeographical-regions-europe-1"></a></p>

								<label for="river_basin">
									<input type="checkbox" class="list_item" id="river_basin" onclick="updateLayerVisibility('river_basin');"/>
									River basin districts <!-- TODO: To add year and source information. -->
								</label>
								<p class="discreet">&nbsp;</p>
								
								<!-- TODO: To add Corine Land Cover
								Corine land cover [http://www.eea.europa.eu/data-and-maps/data/corine-land-cover-2000-clc2000-seamless-vector-database-4]
								European land cover based on satellite imagery
								-->
								<!-- TODO: All Natura 2000 sites
								Protected areas - all Natura 2000 sites [Permalink to latest version 52E54BF3-ACDB-4959-9165-F3E4469BE610]
								-->

							</fieldset>
                    </div>

                    <div class="right-area reported-areas-map">
                        <div id="report-map" class="claro map-border" style="width:470px; height:400px; margin: 1em 0.5em 1em 0"></div>
                    </div>
                </div>
                <!-- END species reported -->
</stripes:layout-definition>
