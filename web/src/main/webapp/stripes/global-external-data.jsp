<%@page contentType="text/html;charset=UTF-8"%>

<%@ include file="/stripes/common/taglibs.jsp"%>

<%
    String btrail = "eea#" + application.getInitParameter("EEA_HOME") + ",home#index.jsp,externalglobal";
%>
<stripes:layout-render name="/stripes/common/template.jsp" bookmarkPageName="externalglobal" pageTitle="Global queries" btrail="<%=btrail%>">
    <stripes:layout-component name="head">
        <link rel="stylesheet" type="text/css" href="/css/eea_search.css"/>
    </stripes:layout-component>
    <stripes:layout-component name="contents">

        <!-- MAIN CONTENT -->

        <h2>Global queries of external data</h2>

        <c:choose>
            <c:when test="${!actionBean.querySelected}">
                <p>
                    ${eunis:cmsText(actionBean.contentManagement, 'globalqueries')}
                </p>
                <h3>Select a query:</h3>
                <c:if test="${not empty actionBean.queries}">
                    <dl>
                        <c:forEach items="${actionBean.queries}" var="query" varStatus="loop">
                            <dt>
                            <stripes:link beanclass="${actionBean['class'].name}" rel="nofollow">
                                <c:out value="${query.title}"/>
                                <stripes:param name="query" value="${query.id}"/>
                            </stripes:link>
                            </dt>
                            <dd>
                                ${query.summary}
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
                    <stripes:form beanclass="${actionBean['class'].name}" method="get">
                        <p>
                            <stripes:select name="query">
                                <stripes:options-collection collection="${actionBean.queries}" label="title" value="id"/>
                            </stripes:select>
                            <stripes:submit name="defaultEvent" value="Execute query"/>
                        </p>
                    </stripes:form>
                </c:if>
                <c:if test="${empty actionBean.queries}">
                    <p>No selectable queries were found! Please use the "Contact us" link if you think this is incorrect.</p>
                </c:if>
                <c:choose>
                    <c:when test="${not empty actionBean.queryResultCols && not empty actionBean.queryResultRows}">
                        <div style="overflow-x:auto ">
                            <display:table name="actionBean.queryResultRows" class="globalQuery ${actionBean.query} sortable listing" pagesize="100" sort="list" requestURI="${actionBean.urlBinding}">
                                <display:setProperty name="paging.banner.placement" value="both" />
                                <c:forEach var="cl" items="${actionBean.queryResultCols}">
                                    <display:column 
                                        class="${cl.property}" property="${cl.property}" title="${cl.title}" sortable="${cl.sortable}" headerClass="${cl.property} nosort" 
                                        decorator="eionet.eunis.util.decorators.ForeignDataColumnDecorator"/>
                                </c:forEach>
                            </display:table>
                        </div>
                        <c:if test="${not empty actionBean.attribution}">
                            <b>Source:</b> ${actionBean.attribution}
                        </c:if>
                    </c:when>
                    <c:when test="${not empty actionBean.jasperReportPage}">
                        <div>
                            <div style="height: 18px;">
                            <c:if test="${actionBean.paginable}">
                                <span style="margin-right: 10px;">Page ${actionBean.page+1} of ${actionBean.lastPage+1}</span>
                                <c:choose>
                                    <c:when test="${actionBean.page gt 0}"><span><a href="/externalglobal?query=${param["query"]}&page=${actionBean.page-1}" title="Previous page">Previous</a></span></c:when>
                                    <c:otherwise><span >Previous</span></c:otherwise>
                                </c:choose>
                                <c:set var="dots" value="0"/>
                                <c:forEach begin="0" end="${actionBean.lastPage}" var="i">
                                    <span>
                                    <c:choose>
                                        <c:when test="${i eq actionBean.page}">
                                            ${i+1}
                                        </c:when>
                                        <c:when test="${(i lt 3) or (i gt (actionBean.lastPage - 3)) or (i gt (actionBean.page-3) and i lt(actionBean.page+3))}">
                                            <a href="/externalglobal?query=${param["query"]}&page=${i}" title="Go to page ${i+1}">${i+1}</a>
                                            <c:set var="dots" value="0"/>
                                        </c:when>
                                        <%--<c:when test="${i gt (actionBean.lastPage - 3)}">--%>
                                            <%--<a href="/externalglobal?query=${param["query"]}&page=${i}" title="Go to page ${i+1}">${i+1}</a>--%>
                                        <%--</c:when>--%>
                                        <%--<c:when test="${i gt (actionBean.page-3) and i lt(actionBean.page+3)}">--%>
                                            <%--<a href="/externalglobal?query=${param["query"]}&page=${i}" title="Go to page ${i+1}">${i+1}</a>--%>
                                            <%--<c:set var="dots" value="0"/>--%>
                                        <%--</c:when>--%>
                                        <c:otherwise>
                                            <c:if test="${dots eq 0}">.....</c:if>
                                            <c:set var="dots" value="1"/>
                                        </c:otherwise>
                                    </c:choose>
                                    </span>
                                </c:forEach>
                                <c:choose>
                                    <c:when test="${actionBean.page lt actionBean.lastPage}"><span><a href="/externalglobal?query=${param["query"]}&page=${actionBean.page+1}" title="Next page">Next</a></span></span></c:when>
                                    <c:otherwise>Next</c:otherwise>
                                </c:choose>
                            </c:if>
                            <span style="float:right;">
                                <a href="/jreportdown?query=${param["query"]}&format=PDF">Download as PDF</a>
                            </span>
                            </div>
                        <div>
                            ${actionBean.jasperReportPage}
                        </div>

                            <script>
                                $(document).ready(function() {
                                    $('.jrPage').css('width','100%');
                                });
                            </script>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div style="font-weight:bold">Query didn't return any result!</div>
                    </c:otherwise>
                </c:choose>

            </c:otherwise>
        </c:choose>

        <!-- END MAIN CONTENT -->

    </stripes:layout-component>
    <stripes:layout-component name="foot">
    </stripes:layout-component>
</stripes:layout-render>
