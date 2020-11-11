<%@page contentType="text/html;charset=UTF-8" %>
<%@ include file="/stripes/common/taglibs.jsp" %>
<stripes:layout-definition>
    <div>
        <div>${actionBean.redlist.conservationManagement}</div>
        <h4>
           List of conservation and management needs
        </h4>
        <div>
            <div>
                <c:set var="lastthreat" value="X"/>
                <ul>
                    <c:forEach items="${actionBean.redlistConservation}" var="threat">
                    <c:if test="${not (lastthreat eq threat.mainCategory)}">
                    <c:if test="${not (lastthreat eq 'X')}">
                </ul>
                </c:if>
                <li> ${threat.mainCategory} </li>
                <ul style="list-style-type: circle;">

                    </c:if>
                    <li>${threat.subcategory}</li>
                    <c:set var="lastthreat" value="${threat.mainCategory}"/>
                    </c:forEach>
                </ul>
                </ul>

            </div>
        </div>
    </div>
</stripes:layout-definition>