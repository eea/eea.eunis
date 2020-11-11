<%@page contentType="text/html;charset=UTF-8" %>
<%@ include file="/stripes/common/taglibs.jsp" %>
<stripes:layout-definition>
    <div>
        <div>
            ${actionBean.englishDescription}
        </div>
        <div class="table-definition contain-float">
            <span class="table-definition-target standardButton float-left" style="margin-top: 20px;">
                    ${eunis:cmsPhrase(actionBean.contentManagement, 'Characteristic species')}
            </span>
            <div class="table-definition-body visualClear" style="display:none;">
                ${actionBean.descriptionSpecies}
            </div>

        </div>
    </div>
    <div class="discreet">For full habitat description, please download the habitat factsheet.</div>
</stripes:layout-definition>