<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
    <c:choose>
        <c:when test="${(not empty actionBean.diagnosticSpecies) or (not empty actionBean.constantSpecies) or (not empty actionBean.dominantSpecies)}">
            <div class="species-container">
                <div displayed="8" trigger_for="1" class="species-header tab-visible">Diagnostic species</div>
                <div displayed="8" trigger_for="2" class="species-header">Constant species</div>
                <div displayed="8" trigger_for="3" class="species-header">Dominant species</div>

                <div tab="1" id="pagination1" class="diagnostic-wrapper tab-body tab-visible">
                    <c:forEach items="${actionBean.diagnosticSpecies}" var="specie">
                        <div class="diagnostic-species species-item">
                        <span class="photoAlbumEntryWrapper">
                        <a href="/species/${specie.source.idSpecies}">
                        <img src="images/species/${specie.source.idSpecies}/thumbnail.jpg" onerror="this.src='images/species/${eunis:getDefaultPicture(specie.group)}';"/>
                        </a>
                        </span>
                            <span class="photoAlbumEntryTitle">
                                <span class="photo-subtitle">${specie.group}</span>
                                <span class="italics">${specie.scientificName} </span>
                            </span>
                        </div>
                    </c:forEach>
                    <span class="view-switch">
                        <button class="btn btn-table"></button>
                        <button class="btn btn-list selected"></button>
                    </span>
                    <table class="table-striped table-full display-hidden">
                        <thead>
                        <tr><th>Species scientific name</th>
                        <th>English common name</th>
                        <th>Species group</th></tr>
                        </thead>
                        <c:forEach items="${actionBean.diagnosticSpecies}" var="specie">
                            <tr>
                                <td>${specie.scientificName}</td>
                                <td><a href="/species/${specie.source.idSpecies}">${specie.commonName}</a></td>
                                <td>${specie.group}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
                <div tab="2" id="pagination2" class="constant-wrapper tab-body">
                    <c:forEach items="${actionBean.constantSpecies}" var="specie">
                        <div class="constant-species species-item">
                    <span class="photoAlbumEntryWrapper">
                    <a href="/species/${specie.source.idSpecies}">
                        <img src="images/species/${specie.source.idSpecies}/thumbnail.jpg" onerror="this.src='images/species/${eunis:getDefaultPicture(specie.group)}';"/>
                    </a>
                    </span>
                            <span class="photoAlbumEntryTitle">
                                <span class="photo-subtitle">${specie.group}</span>
                                <span class="italics">${specie.scientificName} </span>
                            </span>
                        </div>
                    </c:forEach>
                    <span class="view-switch">
                        <button class="btn btn-table"></button>
                        <button class="btn btn-list selected"></button>
                    </span>
                    <table class="table-striped table-full display-hidden">
                        <thead>
                        <tr><th>Species scientific name</th>
                        <th>English common name</th>
                        <th>Species group</th></tr>
                        </thead>
                        <c:forEach items="${actionBean.constantSpecies}" var="specie">
                            <tr>
                                <td>${specie.scientificName}</td>
                                <td><a href="/species/${specie.source.idSpecies}">${specie.commonName}</a></td>
                                <td>${specie.group}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
                <div tab="3" id="pagination3" class="dominant-wrapper tab-body">
                    <c:forEach items="${actionBean.dominantSpecies}" var="specie">
                        <div class="dominant-species species-item">
                        <span class="photoAlbumEntryWrapper">
                        <a href="/species/${specie.source.idSpecies}">
                            <img src="images/species/${specie.source.idSpecies}/thumbnail.jpg" onerror="this.src='images/species/${eunis:getDefaultPicture(specie.group)}';"/>
                        </a>
                        </span>
                            <span class="photoAlbumEntryTitle">
                                <span class="photo-subtitle">${specie.group}</span>
                                <span class="italics">${specie.scientificName} </span>
                            </span>
                        </div>
                    </c:forEach>
                    <span class="view-switch">
                        <button class="btn btn-table"></button>
                        <button class="btn btn-list selected"></button>
                    </span>
                    <table class="table-striped table-full display-hidden">
                        <thead>
                        <tr><th><a href="/species/${specie.source.idSpecies}">Species scientific name</a></th>
                        <th>English common name</th>
                        <th>Species group</th></tr>
                        </thead>
                        <c:forEach items="${actionBean.dominantSpecies}" var="specie">
                            <tr>
                                <td>${specie.scientificName}</td>
                                <td>${specie.commonName}</td>
                                <td>${specie.group}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
            </div>
            <script>
                var tabs = $('.tab-body');
                $('.species-header').click(function() {
                    var self = this
                    tabs.each(function (index,item) {
                        if($(item).attr('tab') == $(self).attr('trigger_for')) {
                            $(item).addClass('tab-visible')
                            $('.species-header').removeClass('tab-visible')
                            $(self).addClass('tab-visible')
                        }else {
                            $(item).removeClass('tab-visible')
                        }
                    })
                })
            </script>
            <script>
                $('.tab-body').each(function (index, item) {
                    var children = $(this).children('div')
                    var pages = Math.ceil(children.length / 8)
                    if (pages > 1) {
                        var current_page_number = 1
                        var next_button = "<button id='next' class='btn'>→</button>"
                        var prev_button = "<button id='prev' class='btn disabled'>←</button>"
                        var pages_number = "<span class='max_page'>"+ pages +"</span>"
                        var current_page ="<span class='current_page'>"+ current_page_number +"</span>"
                        var pages_wrapper = "<span class='pagination-controls'> "+prev_button+"<span class='page_number_wrapper'>"+ current_page +"/"+ pages_number +"</span>"+ next_button +"</span>"
                        $(item).prepend(pages_wrapper)
                    }
                })

                $('body').on('click', '#next', function () {
                    var children = $(this).parent().parent().find('.species-item')
                    var parent = $(this).parent().parent();
                    var count = parseInt($(parent).find('.current_page').text()) + 1
                    var tab_index = $(parent).attr('tab')
                    var tab_displayed = $('.species-header')
                    var current_displayed;
                    tab_displayed.each(function (index,item) {
                        if(tab_index === $(item).attr('trigger_for')) {
                            current_displayed = parseInt($(item).attr('displayed'))
                            $(item).attr('displayed', current_displayed + 8)
                        }
                    })
                    var count_wrapper = $(parent).find('.current_page')[0]
                    count_wrapper.innerHTML = count
                    if(count == parseInt($(parent).find('.max_page').text())) {
                        $(this).addClass('disabled')
                    }
                    $(parent).find('#prev').removeClass('disabled')
                    for (var i=0; i<current_displayed; i++) {
                        $(children[i]).css('display','none')
                    }
                    for (var i = current_displayed; i<current_displayed + 8; i++){
                        $(children[i]).css('display','block')
                    }
                })

                $('body').on('click', '#prev', function () {
                    var children = $(this).parent().parent().find('.species-item')
                    var parent = $(this).parent().parent();
                    var count = parseInt( $(parent).find('.current_page').text()) - 1
                    var tab_index = $(parent).attr('tab')
                    var tab_displayed = $('.species-header')
                    var current_displayed;
                    tab_displayed.each(function (index,item) {
                        if(tab_index === $(item).attr('trigger_for')) {
                            current_displayed = parseInt($(item).attr('displayed'))
                            $(item).attr('displayed', current_displayed - 8)
                        }
                    })
                    var count_wrapper = $(parent).find('.current_page')[0]
                    count_wrapper.innerHTML = count
                    if(count == 1) {
                        $(this).addClass('disabled')
                    }
                    $(parent).find('#next').removeClass('disabled')
                    for (var i=current_displayed; i > current_displayed - 8; i--) {
                        $(children[i]).css('display','none')
                    }
                    for (var i = current_displayed - 8; i > current_displayed - 16 ; i--){
                        $(children[i]).css('display','block')
                    }
                })
            </script>
            <script>
                var view_switch = $('.view-switch .btn')
                view_switch.click(function (e) {
                    var parent = $(e.target).parent();
                    var grandparent = $(parent).parent();
                    $(parent).find('.btn').removeClass('selected')
                    $(e.target).addClass('selected')
                    if($(e.target).hasClass('btn-list')){
                        $(grandparent).find('table').addClass('display-hidden');
                        $(grandparent).find('.species-item').removeClass('display-hidden')
                        $(grandparent).find('.pagination-controls').removeClass('display-hidden')
                    }
                    else {
                        $(grandparent).find('table').removeClass('display-hidden');
                        $(grandparent).find('.species-item').addClass('display-hidden')
                        $(grandparent).find('.pagination-controls').addClass('display-hidden')
                    }
                })
//                display-hidden
            </script>
        </c:when>
        <c:otherwise>
            ${eunis:cmsPhrase(actionBean.contentManagement, 'Not available')}
            <script>
                $("#indicator-species-accordion").addClass("nodata");
            </script>
        </c:otherwise>
    </c:choose>
</stripes:layout-definition>