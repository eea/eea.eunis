<%@page contentType="text/html;charset=UTF-8"%>

<%@ include file="/stripes/common/taglibs.jsp"%>	

<stripes:layout-render name="/stripes/common/template.jsp" bookmarkPageName="dataimport/importcdda" pageTitle="Import National CDDA sites and designations">

	<stripes:layout-component name="contents">

	<c:choose>
		<c:when test="${actionBean.context.sessionManager.authenticated && actionBean.context.sessionManager.importExportData_RIGHT}">
			<h1>Import National CDDA sites and designations</h1>
			<stripes:form action="/dataimport/importcdda" method="post" name="f">
				<stripes:label for="file1">XML file for designations</stripes:label>
				<stripes:file name="fileDesignations" id="file1"/><br/>
				<stripes:label for="file2">XML file for sites</stripes:label>
				<stripes:file name="fileSites" id="file2"/><br/>
				<stripes:checkbox name="updateCountrySitesFactsheet" id="update"/>
				<stripes:label for="update"> - automatically update "chm62edt_country_sites_factsheet" table after import</stripes:label>
				<br/>
				<stripes:submit name="importCdda" value="Import"/>
			</stripes:form>
      <h2>Data formats</h2>
			<p>
			The format is XML as exported from the MDB-file published in the EEA data service.
			</p>
			<h3>Designations</h3>
			<p>A record in the designations file must have these fields. Verify and rename the fields in your import file if necessary.</p>
			<pre>
  &lt;CDDA_designations&gt;
    &lt;PARENT_ISO&gt;FRA&lt;/PARENT_ISO&gt;
    &lt;ISO3&gt;FRA&lt;/ISO3&gt;
    &lt;DESIG_ABBR&gt;FR21&lt;/DESIG_ABBR&gt;
    &lt;Category&gt;B&lt;/Category&gt;
    &lt;ODESIGNATE&gt;Réserve de pêche du domaine public fluvial&lt;/ODESIGNATE&gt;
    &lt;DESIGNATE&gt;State Riverine Fishing Reserve&lt;/DESIGNATE&gt;
    &lt;Law&gt;Origine : Décret 18 octobre 1968.
Code rural L. 236-12 et R. 236-84 et R. 236-95.&lt;/Law&gt;
    &lt;Lawreference&gt;Code de l’environnement : Articles L. 436-12 et R. 436-69 et R. 436-73 à R. 436-76.&lt;/Lawreference&gt;
    &lt;Agency&gt;Préfets de département (local State).&lt;/Agency&gt;
  &lt;/CDDA_designations&gt;
      </pre>
      <h3>Sites</h3>
			<pre>
	&lt;CDDA_sites&gt;
		&lt;SITE_CODE&gt;336692&lt;/SITE_CODE&gt;
		&lt;SITE_CODE_NAT&gt;282200&lt;/SITE_CODE_NAT&gt;
		&lt;PARENT_ISO&gt;DNK&lt;/PARENT_ISO&gt;
		&lt;ISO3&gt;DNK&lt;/ISO3&gt;
		&lt;DESIG_ABBR&gt;DK01&lt;/DESIG_ABBR&gt;
		&lt;SITE_NAME&gt;Møens Klint, Høje Møn&lt;/SITE_NAME&gt;
		&lt;SITE_AREA&gt;15&lt;/SITE_AREA&gt;
		&lt;IUCNCAT&gt;V&lt;/IUCNCAT&gt;
		&lt;NUTS&gt;DK00&lt;/NUTS&gt;
		&lt;YEAR&gt;1983&lt;/YEAR&gt;
		&lt;LAT&gt;54.95924&lt;/LAT&gt;
		&lt;LON&gt;12.52584&lt;/LON&gt;
		&lt;CDDA_Coordinates_code&gt;00&lt;/CDDA_Coordinates_code&gt;
		&lt;CDDA_Resolution_code&gt;01&lt;/CDDA_Resolution_code&gt;
		&lt;CDDA_Dissemination_code&gt;01&lt;/CDDA_Dissemination_code&gt;
		&lt;CDDA_Marine_percentage&gt;01&lt;/CDDA_Marine_percentage&gt;
	&lt;/CDDA_sites&gt;
      </pre>

		</c:when>
		<c:otherwise>
			<div class="error-msg">
				You are not logged in or you do not have enough privileges to view this page!
			</div>
		</c:otherwise>
	</c:choose>
	</stripes:layout-component>
	<stripes:layout-component name="foot">
	</stripes:layout-component>
</stripes:layout-render>
