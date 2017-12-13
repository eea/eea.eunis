<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=7,IE=9" />
		<!--The viewport meta tag is used to improve the presentation and behavior of the samples on iOS devices-->
		<meta name="viewport" content="initial-scale=1, maximum-scale=1,user-scalable=no"/>
		<title>${actionBean.pageTitle}</title>
		<link rel="stylesheet" type="text/css" href="https://serverapi.arcgisonline.com/jsapi/arcgis/2.3/js/dojo/dijit/themes/claro/claro.css"/>
		<style>
			html, body { height: 98%; width: 98%; margin: 0; padding: 5px; }
			#rightPane{
				width:20%;
			}
			#legendPane{
				border: solid #97DCF2 1px;
			}
		</style>
		<script type="text/javascript">var djConfig = {parseOnLoad: true};</script>
		<script type="text/javascript" src="https://serverapi.arcgisonline.com/jsapi/arcgis/?v=2.3"></script>
		<script type="text/javascript">
		//<![CDATA[
			dojo.require("dijit.layout.BorderContainer");
			dojo.require("dijit.layout.ContentPane");
			dojo.require("dijit.layout.AccordionContainer");
			dojo.require("esri.map");
			dojo.require("esri.dijit.Legend");
			dojo.require("esri.layers.FeatureLayer");
     
			var n2000layer, cddalayer, map;

			function init() {
				var initialExtent = new esri.geometry.Extent({"xmin":-3549139.09145218,"ymin":4871361.38436808,"xmax":5129676.02317436,"ymax":11227551.8207187,"spatialReference":{"wkid":102100}});
				map = new esri.Map("map", { extent: initialExtent});
				//Add the terrain service to the map. View the ArcGIS Online site for services http://arcgisonline/home/search.html?t=content&f=typekeywords:service    
				var basemap = new esri.layers.ArcGISTiledMapServiceLayer("https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer");
				map.addLayer(basemap);

				n2000layer = new esri.layers.ArcGISDynamicMapServiceLayer("https://bio.discomap.eea.europa.eu/ArcGIS/rest/services/Bio/Natura2000_Dyna_WM/MapServer");
				cddalayer = new esri.layers.ArcGISDynamicMapServiceLayer("https://bio.discomap.eea.europa.eu/ArcGIS/rest/services/Bio/CDDA_Dyna_WGS84/MapServer");
       
				//add the legend
				dojo.connect(map,'onLayersAddResult',function(results){
					var layerInfo = dojo.map(results, function(layer,index){
						return {layer:layer.layer,title:layer.layer.name};
					});
					if(layerInfo.length > 0){
						var legendDijit = new esri.dijit.Legend({
							map:map,
							layerInfos:layerInfo
						},"legendDiv");
						legendDijit.startup();
					}
				});
       
				map.addLayers([n2000layer,cddalayer]);

				//resize the map when the browser resizes - view the 'Resizing and repositioning the map' section in
				//the following help topic for more details http://help.esri.com/EN/webapi/javascript/arcgis/help/jshelp_start.htm#jshelp/inside_guidelines.htm
				var resizeTimer;
				dojo.connect(map, 'onLoad', function(theMap) {
					dojo.connect(dijit.byId('map'), 'resize', function() {  //resize the map if the div is resized
						clearTimeout(resizeTimer);
						resizeTimer = setTimeout( function() {
							map.resize();
							map.reposition();
						}, 500);
					});
				});
			}
			
			function updateLayerVisibility(id) {
				var input = document.getElementById(id);
				if (input.checked) {
					if (id == 'natura') {
						map.addLayer(n2000layer);
					} else if (id == 'cdda') {
						map.addLayer(cddalayer);
					}
				} else {
					if (id == 'natura') {
						map.removeLayer(n2000layer);
					} else if (id == 'cdda') {
						map.removeLayer(cddalayer);
					}
				}
			}
 
			dojo.addOnLoad(init);
		//]]>
		</script>
	</head>
 
	<body class="claro">
		<div id="content" dojotype="dijit.layout.BorderContainer" design="headline" gutters="true" style="width: 100%; height: 100%; margin: 0;">
			<div id="rightPane" dojotype="dijit.layout.ContentPane" region="right">
				<div dojoType="dijit.layout.AccordionContainer">
					<div dojoType="dijit.layout.ContentPane" id="legendPane" title="Legend">
						<div id="legendDiv"></div>
					</div>
					<div dojoType="dijit.layout.ContentPane" title="Layers" selected="true">
						<span id="layer_list">
							<input type="checkbox" name="natura" checked="checked" id="natura" onclick="updateLayerVisibility('natura');"/><label for="natura">Natura 2000 sites</label><br/>
							<div style="border: 1px solid #d1e3f5; padding: 4px; width: 97%; color: #666666; background: #e6f1fc">
								Natura 2000 is the key instrument to protect biodiversity in the European Union. It is an ecological network of protected areas, set up to ensure the survival of Europe's most valuable species and habitats. Natura 2000 is based on the 1979 Birds Directive and the 1992 Habitats Directive. The green infrastructure it provides safeguards numerous ecosystem services and ensures that Europe's natural systems remain healthy and resilient.
							</div>
							<input type="checkbox" name="cdda" checked="checked" id="cdda" onclick="updateLayerVisibility('cdda');"/><label for="cdda">CDDA sites</label>
							<div style="border: 1px solid #d1e3f5; padding: 4px; width: 97%; color: #666666; background: #e6f1fc">
								The European inventory of nationally designated areas holds information about protected sites and about the national legislative instruments. The inventory began under the CORINE programme. It is now maintained for EEA by the European Topic Centre on Biological Diversity and is annually updated through Eionet.
							</div>
						</span><br />
					</div>
				</div>
			</div>
			<div id="map" dojotype="dijit.layout.ContentPane" region="center" style="overflow:hidden;">
			</div>
		</div>
	</body>
 </html> 
