<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
    <fieldset>
        <legend><strong>IUCN Red List Category</strong></legend>
        <table class="legend-table">
            <tr class="discreet">
                <td><div class="threat-status-ne legend-color" style="width: 8px; height: 8px;"> </div> Not Evaluated</td>
                <td><div class="threat-status-nt legend-color"> </div> Near Threatened</td>
                <td><div class="threat-status-re legend-color"> </div> Regionally Extinct</td>
            </tr>
            <tr class="discreet">
                <td><div class="threat-status-na legend-color"> </div> Not Applicable</td>
                <td><div class="threat-status-vu legend-color"> </div> Vulnerable</td>
                <td><div class="threat-status-ew legend-color"> </div> Extinct in the Wild</td>
            </tr>
            <tr class="discreet">
                <td><div class="threat-status-dd legend-color"> </div> Data Deficient</td>
                <td><div class="threat-status-en legend-color"> </div> Endangered</td>
                <td><div class="threat-status-ex legend-color"> </div> Extinct</td>
            </tr>
            <tr class="discreet">
                <td><div class="threat-status-lc legend-color"> </div> Least Concern</td>
                <td><div class="threat-status-cr legend-color"> </div> Critically Endangered</td>
                <td> </td>
            </tr>
        </table>
    </fieldset>
    <div class="discreet">
        Sources:
        <ul>
            <li>
                <a href="http://ec.europa.eu/environment/nature/conservation/species/redlist/">European Red List</a>
            </li>
            <li>
                <a href="http://www.iucnredlist.org/technical-documents/categories-and-criteria">IUCN Red List Categories and Criteria</a>
            </li>
        </ul>
    </div>
</stripes:layout-definition>
