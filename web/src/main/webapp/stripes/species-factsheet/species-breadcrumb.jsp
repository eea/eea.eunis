<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
    <!-- breadcrumbs -->
    <div id="portal-breadcrumbs" class='species-taxonomy'>

        <c:forEach items="${actionBean.classifications}" var="classif" varStatus="loop">
            <span id="breadcrumbs-${loop.index + 1}" dir="ltr">
                ${classif.level}:
                <a href="species-taxonomic-browser.jsp?expand=${actionBean.breadcrumbClassificationExpands[loop.index]}#level_${classif.id}">${classif.name}</a>
                <span class="breadcrumbSeparator">
                    &gt;
                </span>
            </span>
        </c:forEach>

        <span id="breadcrumbs-${fn:length(actionBean.classifications)+1}" dir="ltr">
            ${eunis:cmsPhrase(actionBean.contentManagement, 'Genus')}:
            <a href="species-taxonomic-browser.jsp?expand=${actionBean.breadcrumbClassificationExpands[fn:length(actionBean.classifications)-1]}&genus=${actionBean.specie.genus}#level_${actionBean.specie.genus}">
                ${actionBean.specie.genus}
            </a>
            <span class="breadcrumbSeparator">
                &gt;
            </span>
        </span>

        <span id="breadcrumbs-current" dir="ltr">
            ${eunis:cmsPhrase(actionBean.contentManagement, 'Species')}:
            ${actionBean.scientificName }
        </span>

		<c:if test="${!empty actionBean.subSpecies}">
	        <span id="breadcrumbs-last" dir="ltr">
	        	<span class="breadcrumbSeparator">
	                &gt;
	            </span>
	            <a href="#subspecies-overlay" rel="#subspecies-overlay">See subspecies</a>
	        </span>
		</c:if>
    </div>
    
    
    <c:if test="${!empty actionBean.subSpecies}">
    	<div class='overlay' id="subspecies-overlay">
	 		<table summary="${eunis:cmsPhrase(actionBean.contentManagement, 'List of subspecies')}" class="listing fullwidth">
	   		<col style="width:39%"/>
	   		<col style="width:59%"/>
	   		
	   		
	   		<thead>
			    <tr>
			        <th scope="col" style="cursor: pointer;"><img
			                src="https://www.eea.europa.eu/arrowBlank.gif"
			                height="6" width="9">
			                ${eunis:cmsPhrase(actionBean.contentManagement, 'Scientific name')}
			                ${eunis:cmsTitle(actionBean.contentManagement, 'sort_results_on_this_column')}
							<img src="https://www.eea.europa.eu/arrowUp.gif" height="6" width="9"></th>
			        <th scope="col" style="cursor: pointer;"><img
			                src="https://www.eea.europa.eu/arrowBlank.gif"
			                height="6" width="9">
			                ${eunis:cmsPhrase(actionBean.contentManagement, 'Source')}
							${eunis:cmsTitle(actionBean.contentManagement, 'sort_results_on_this_column')}
					<img src="https://www.eea.europa.eu/arrowBlank.gif" height="6" width="9"></th>
			    </tr>
			</thead>
	   		

	   		<tbody>
	    		<c:forEach items="${actionBean.subSpecies}" var="subspecie" varStatus="loop">
	    			<tr ${loop.index % 2 == 0 ? '' : 'class="zebraeven"'}>
	    				<td>
	        				<a style="font-style : italic;" href="species/${subspecie.idSpecies}">${eunis:treatURLSpecialCharacters(subspecie.scientificName)}</a>
	         					${eunis:treatURLSpecialCharacters(subspecie.author)}
	      				</td>
	      				<td>
	          				${eunis:treatURLSpecialCharacters(subspecie.bookAuthorDate)}
	      				</td>
	    			</tr>
	    		</c:forEach>
	   		</tbody>
	 		</table>
 		</div>
	</c:if>
    
    <!-- END breadcrumbs -->
</stripes:layout-definition>