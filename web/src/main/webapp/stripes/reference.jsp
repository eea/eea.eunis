<%@page contentType="text/html;charset=UTF-8" %>

<%@ include file="/stripes/common/taglibs.jsp" %>

<stripes:layout-render name="/stripes/common/template.jsp" btrail="${actionBean.btrail}"
                       pageTitle="Document - ${actionBean.dcIndex.title}" bookmarkPageName="references/${actionBean.idref}">
    <stripes:layout-component name="head">
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/eea_search.css">
    </stripes:layout-component>
    <stripes:layout-component name="contents">
        <!-- MAIN CONTENT -->
        <h1 class="documentFirstHeading">${actionBean.dcIndex.title}</h1>

        <div class="eea-accordion-panels non-exclusive">
            <div class="eea-accordion-panel" style="clear: both;">

                <h2 class="notoc eea-icon-right-container">Reference information</h2>

                <div class="pane">
                    <table width="90%" class="datatable">
                        <col style="width:20%"/>
                        <col style="width:80%"/>
                        <tr>
                            <th scope="row">Title</th>
                            <td>${eunis:replaceTags(actionBean.dcIndex.title)}</td>
                        </tr>
                        <tr class="zebraeven">
                            <th scope="row">Alternative title</th>
                            <td>${eunis:replaceTags(actionBean.dcIndex.alternative)}</td>
                        </tr>
                        <tr>
                            <th scope="row">Source</th>
                            <td>${eunis:replaceTags(actionBean.dcIndex.source)}</td>
                        </tr>
                        <tr class="zebraeven">
                            <th scope="row">Editor</th>
                            <td>${eunis:replaceTags(actionBean.dcIndex.editor)}</td>
                        </tr>
                        <tr>
                            <th scope="row">Journal Title</th>
                            <td>${eunis:replaceTags(actionBean.dcIndex.journalTitle)}</td>
                        </tr>
                        <tr class="zebraeven">
                            <th scope="row">Book Title</th>
                            <td>${eunis:replaceTags(actionBean.dcIndex.bookTitle)}</td>
                        </tr>
                        <tr>
                            <th scope="row">Journal Issue</th>
                            <td>${actionBean.dcIndex.journalIssue}</td>
                        </tr>
                        <tr class="zebraeven">
                            <th scope="row">ISBN</th>
                            <td>${actionBean.dcIndex.isbn}</td>
                        </tr>
                        <tr>
                            <th scope="row">URL</th>
                            <td>
                                <a href="${eunis:replaceTags(actionBean.dcIndex.url)}">${eunis:replaceTags(actionBean.dcIndex.url)}</a>
                            </td>
                        </tr>
                        <tr class="zebraeven">
                            <th scope="row">Created</th>
                            <td>${actionBean.dcIndex.created}</td>
                        </tr>
                        <tr>
                            <th scope="row">Publisher</th>
                            <td>${eunis:replaceTags(actionBean.dcIndex.publisher)}</td>
                        </tr>
                        <c:if test="${!empty actionBean.dcAttributes}">
                            <c:forEach items="${actionBean.dcAttributes}" var="attr" varStatus="loop">
                                <tr ${loop.index % 2 != 0 ? '' : 'class="zebraeven"'}>
                                    <th scope="row">${attr.label}</th>
                                    <c:choose>
                                        <c:when test="${attr.type == 'reference'}">
                                            <td>
                                                <a href="${eunis:replaceTags(attr.value)}">${eunis:replaceTags(attr.objectLabel)}</a>
                                            </td>
                                        </c:when>

                                        <c:when test="${attr.type == 'localref'}">
                                            <td>
                                                <a href="references/${eunis:replaceTags(attr.value)}">${eunis:replaceTags(attr.objectLabel)}</a>
                                            </td>
                                        </c:when>

                                        <c:otherwise>
                                            <td>${eunis:replaceTags(attr.objectLabel)}</td>
                                        </c:otherwise>
                                    </c:choose>
                                </tr>
                            </c:forEach>
                        </c:if>
                    </table>
                </div>
            </div>
            <c:if test="${not empty actionBean.speciesByName}">
                <div class="eea-accordion-panel" style="clear: both;">
                    <h2 class="notoc eea-icon-right-container">Species related to this reference</h2>

                    <div class="pane">

                        <table class="listing fullwidth table-inline">
                            <thead>
                            <tr>
                                <th title="${eunis:cmsPhrase(actionBean.contentManagement, 'Sort results on this column')}"
                                    style="text-align: left;">
                                        ${eunis:cmsPhrase(actionBean.contentManagement, 'Species name')}
                                </th>
                                <th title="${eunis:cmsPhrase(actionBean.contentManagement, 'Sort results on this column')}"
                                    style="text-align: left;">
                                        ${eunis:cmsPhrase(actionBean.contentManagement, 'Species group')}
                                </th>
                                <th title="${eunis:cmsPhrase(actionBean.contentManagement, 'Sort results on this column')}"
                                    style="text-align: left;">
                                        ${eunis:cmsPhrase(actionBean.contentManagement, 'Group scientific name')}
                                </th>
                            </tr>
                            </thead>
                            <tbody>

                            <c:forEach items="${actionBean.speciesByName}" var="spe" varStatus="loop">
                                <tr>
                                    <td>
                                        <a href="species/${spe.id}">${spe.name}<c:if test="${not empty spe.author}">, ${spe.author}</c:if></a>
                                    </td>
                                    <td>
                                            ${spe.groupCommonName}
                                    </td>
                                    <td>
                                            ${spe.groupScientificName}
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>
            <c:if test="${not empty actionBean.habitats}">
                <div class="eea-accordion-panel" style="clear: both;">
                    <h2 class="notoc eea-icon-right-container">Habitats related to this reference</h2>

                    <div class="pane">
                        <ol>
                            <c:forEach items="${actionBean.habitats}" var="habitat" varStatus="loop">
                                <li style="background-color: ${loop.index % 2 == 0 ? '#FFFFFF' : '#EEEEEE'}">
                                    <a href="habitats/${habitat.key}">${habitat.value}</a>
                                </li>
                            </c:forEach>
                        </ol>
                    </div>
                </div>
            </c:if>
        </div>

        <!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>
