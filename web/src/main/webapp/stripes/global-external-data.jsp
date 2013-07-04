<%@page contentType="text/html;charset=UTF-8"%>

<%@ include file="/stripes/common/taglibs.jsp"%>

<stripes:layout-render name="/stripes/common/template.jsp" pageTitle="Glogabl queries of external data">

    <stripes:layout-component name="contents">

        <!-- MAIN CONTENT -->

            <h2>Glogabl queries of external data:</h2>
            
            <c:choose>
                <c:when test="${!actionBean.querySelected}">
                    <p>
                        This page contains reports that query foreign systems for structured data that <em>links</em> to the species, sites and other entities in EUNIS.<br/>
                        It is possible that there might be no relevant data in the foreign systems at a particular moment. In that case the submitted query shows nothing.<br/>
                        More queries will be added to this page as more relevant data at foreign sites becomes available. 
                    </p>
                    <h3>Select a query:</h3>
                    <c:if test="${not empty actionBean.queries}">
                        <dl>
                            <c:forEach items="${actionBean.queries}" var="query" varStatus="loop">
                                <dt>
                                    <stripes:link beanclass="${actionBean.class.name}" rel="nofollow">
                                        <c:out value="${query.title}"/>
                                        <stripes:param name="query" value="${query.id}"/>
                                        <stripes:param name="execute" value=""/>
                                    </stripes:link>
                                </dt>
                                <dd>
                                    <c:out value="${query.summary}"/>
                                </dd>
                            </c:forEach>
                        </dl>
                    </c:if>
                    <c:if test="${empty actionBean.queries}">
                        <p>No selectable queries were found! Please use the "Contact us" link if you think this is incorrect.</p>
                    </c:if>
                </c:when>
                <c:otherwise>
                
                    <div style="font-weight:bold">Select a query:</div>
                    <c:if test="${not empty actionBean.queries}">
                        <stripes:form beanclass="${actionBean.class.name}" method="post">
	                        <p>
	                            <stripes:select name="query">
	                                <stripes:options-collection collection="${actionBean.queries}" label="title" value="id"/>
	                            </stripes:select>
	                            <stripes:submit name="execute" value="Execute query"/>
	                        </p>
                        </stripes:form>
                    </c:if>
                    <c:if test="${empty actionBean.queries}">
                        <p>No selectable queries were found! Please use the "Contact us" link if you think this is incorrect.</p>
                    </c:if>
                    <div>
                        <p>Here come the query results...</p>
                    </div>
                </c:otherwise>
            </c:choose>

        <!-- END MAIN CONTENT -->

    </stripes:layout-component>
    <stripes:layout-component name="foot">
        <!-- start of the left (by default at least) column -->
            <div id="portal-column-one">
                <div class="visualPadding">
                    <jsp:include page="/inc_column_left.jsp">
                        <jsp:param name="page_name" value="countries" />
                    </jsp:include>
                </div>
            </div>
            <!-- end of the left (by default at least) column -->
    </stripes:layout-component>
</stripes:layout-render>
