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

        <div class="eea-accordion-panels non-exclusive collapsed-by-default">
            <div class="eea-accordion-panel" style="clear: both;">

                <h2 class="notoc eea-icon-right-container<c:if test="${empty actionBean.section}"> current</c:if>">Reference information</h2>

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
                        <c:set var="zebra" value="${'zebraeven'}"/>
                        <c:if test="${not empty actionBean.parent}">
                            <tr class="${zebra}">
                                <th scope="row">Is part of</th>
                                <td>
                                    <a href="references/${actionBean.parent.idDc}">${actionBean.parent.title}</a>
                                </td>
                            </tr>
                            <c:set var="zebra" value="${(zebra eq 'zebraeven')?'':'zebraeven' }"/>
                        </c:if>
                        <c:forEach var="child" items="${actionBean.children}">
                            <tr class="${zebra}">
                                <th scope="row">Has part</th>
                                <td>
                                    <a href="references/${child.idDc}">${child.title}</a><br>
                                </td>
                                <c:set var="zebra" value="${(zebra eq 'zebraeven')?'':'zebraeven' }"/>
                            </tr>
                        </c:forEach>
                        <c:forEach var="link" items="${actionBean.links}" varStatus="status">
                            <tr class="${zebra}">
                                <th scope="row">Web page</th>
                                <td>
                                    <c:choose>
                                        <c:when test="${empty link.linkText}">
                                            <a href="${ link.link }">${eunis:shortenURL(link.link)}</a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${ link.link }">${link.linkText}</a>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <c:set var="zebra" value="${(zebra eq 'zebraeven')?'':'zebraeven' }"/>
                            </tr>
                        </c:forEach>
                        <c:if test="${!empty actionBean.dcAttributes}">
                            <c:forEach items="${actionBean.dcAttributes}" var="attr" varStatus="loop">
                                <tr class="${zebra}">
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
                                <c:set var="zebra" value="${(zebra eq 'zebraeven')?'':'zebraeven' }"/>
                            </c:forEach>
                        </c:if>
                    </table>
                </div>
            </div>
            <c:if test="${not empty actionBean.speciesByName}">
                <div class="eea-accordion-panel" style="clear: both;">
                    <h2 class="notoc eea-icon-right-container<c:if test="${actionBean.section eq 'species'}"> current</c:if>">Species related to this reference</h2>

                    <div class="pane">

                        <table class="listing fullwidth table-inline">
                            <thead>
                            <tr>
                                <th title="${eunis:cmsPhrase(actionBean.contentManagement, 'Sort results on this column')}"
                                    style="text-align: left;">
                                        ${eunis:cmsPhrase(actionBean.contentManagement, 'Recommended species name')}
                                </th>
                                <th title="${eunis:cmsPhrase(actionBean.contentManagement, 'Sort results on this column')}"
                                    style="text-align: left;">
                                        ${eunis:cmsPhrase(actionBean.contentManagement, 'Name as used in text')}
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
                                        <a href="species/${spe.id}"><span class="italics">${spe.name}</span><c:if test="${not empty spe.author}"> ${spe.author}</c:if></a>
                                    </td>
                                    <td>
                                        <c:if test="${not empty spe.nameInDocument}"><span class="italics">${spe.nameInDocument}</span></c:if>
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
                    <h2 class="notoc eea-icon-right-container<c:if test="${actionBean.section eq 'habitats'}"> current</c:if>">Habitats related to this reference</h2>

                    <div class="pane">
                        <table class="listing fullwidth">
                            <colgroup><col><col></colgroup>

                            <c:forEach items="${actionBean.habitats}" var="habitat" varStatus="loop">
                                <tr>
                                    <td><span  class="bold"><a href="habitats/${habitat.idHabitat}">${habitat.code}</a></span></td>
                                    <td>
                                        <a href="habitats/${habitat.idHabitat}">${eunis:bracketsToItalics(habitat.name)}</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>
            </c:if>
        </div>

        <!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>
