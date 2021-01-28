<%@page contentType="text/html;charset=UTF-8" %>
<%@ include file="/stripes/common/taglibs.jsp" %>
<stripes:layout-definition>
    <div>
        <div style="padding: 10px">
        <h4 class="text-center">Synthesis of Red List assessment</h4>
        <div>${actionBean.redlist.synthesis}</div>
        </div>
        <div style="width: 100%; padding: 10px; text-align: center">
            <div class="text-center">

                <table class="redlist-table">
                    <tr>
                        <td colspan="2" style="font-weight: bold">EU</td>
                    </tr>
                    <tr>
                        <td>Red List Category</td>
                        <td>Red List Criteria</td>
                    </tr>
                    <tr>
                        <td class="rl-${actionBean.redlist.categoryCodeEU28}">${actionBean.redlist.categoryEU28}</td>
                        <td style="background: white">${actionBean.redlist.EU28OverallCriteria}</td>
                    </tr>
                </table>
                <table class="redlist-table">
                    <tr>
                        <td colspan="2" style="font-weight: bold">Europe</td>
                    </tr>
                    <tr>
                        <td>Red List Category</td>
                        <td>Red List Criteria</td>
                    </tr>
                    <tr>
                        <td class="rl-${actionBean.redlist.categoryCodeEU28Plus}">${actionBean.redlist.categoryEU28Plus}</td>
                        <td style="background: white">${actionBean.redlist.EU28PlusOverallCriteria}</td>
                    </tr>
                </table>
            </div>
        </div>
        <div>
            <h4 class="text-center">Confidence in the assessment</h4>
            <div class="text-center">${actionBean.redlist.confidence}</div>
        </div>
        <div>

            <div class="table-definition contain-float">
            <span class="table-definition-target standardButton float-right" style="margin-top: 20px;">
                    ${eunis:cmsPhrase(actionBean.contentManagement, 'Red List of habitat categories and criteria descriptions')}
            </span>
                <div class="table-definition-body visualClear" style="display:none;">

            <div class="text-center" style="align-content: center; width: 50%; padding: 6px; float: left">

                <div><h4>European Red List of Habitats categories</h4></div>
                <div><img src="images/habitat_redlist_categories.png"></div>
                <div class="discreet">Based on <a href="https://journals.plos.org/plosone/article?id=10.1371/journal.pone.0062111">Scientific Foundations for an IUCN Red List of Ecosystems</a></div>

            </div>
            <div class="text-center" style="width: 50%; float: left; padding: 6px; text-align: center">
                <div><h4>European Red List of Habitats criteria</h4></div>
                <div>
                    <table class="criteria-table">
                        <tr>
                            <td>Reduction in quantity<br/>(area of distribution)</td>
                            <td>A</td>
                            <td>
                                <div class="criteria-indent"><strong>A1</strong> Present decline (over last 50 years)</div>
                                <div class="criteria-indent"><strong>A2a</strong> Future decline (over the next 50 years)</div>
                                <div class="criteria-indent"><strong>A2b</strong> Future/present decline (over a 50-year period inclunding present and future)</div>
                                <div class="criteria-indent"><strong>A3</strong> Historic decline</div>
                            </td>
                        </tr>
                        <tr>
                            <td>Restricted geographic distribution</td>
                            <td>B</td>
                            <td>
                                <div class="criteria-indent"><strong>B1</strong> Restricted Extent of Occurrence (EOO)</div>
                                <div class="criteria-indent"><strong>B2</strong> Restricted Area of Occupancy (AOO)</div>
                                <div class="criteria-indent"><strong>B3</strong> Present at few locations</div>
                            </td>
                        </tr>
                        <tr>
                            <td>Reduction in abiotic quality</td>
                            <td>C</td>
                            <td rowspan="2">
                                <div class="criteria-indent"><strong>C/D1</strong> Reduction in quality over the last 50 years</div>
                                <div class="criteria-indent"><strong>C/D2</strong> Reduction in quality in the future or in a period including present and future</div>
                                <div class="criteria-indent"><strong>C/D3</strong> Historic reduction in quality</div>
                            </td>
                        </tr>
                        <tr>
                            <td>Reduction in biotic quality</td>
                            <td>D</td>
                        </tr>
                        <tr>
                            <td>Quantitative analysis of probability of collapse</td>
                            <td>E</td>
                        </tr>
                    </table>
                </div>
                <div class="discreet">From <a href="https://journals.plos.org/plosone/article?id=10.1371/journal.pone.0062111">Scientific Foundations for an IUCN Red List of Ecosystems</a></div>
            </div>
                </div>

            </div>
        </div>
        <div style="clear: left">
            <h4>Pressures and threats</h4>
            <div>
                <c:set var="lastthreat" value="X"/>
                <ul>
                <c:forEach items="${actionBean.redlistThreats}" var="threat">
                    <c:if test="${not (lastthreat eq threat.mainThreat)}">
                        <c:if test="${not (lastthreat eq 'X')}">
                        </ul>
                        </c:if>
                        <li> ${threat.mainThreat} </li>
                        <ul style="list-style-type: circle;">

                    </c:if>
                    <li>${threat.description}</li>
                    <c:set var="lastthreat" value="${threat.mainThreat}"/>
                </c:forEach>
                </ul>
                </ul>

            </div>
        </div>



    </div>
</stripes:layout-definition>