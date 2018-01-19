<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<stripes:layout-definition>
<c:choose>
    <c:when test="${(not empty actionBean.diagnosticSpecies) or (not empty actionBean.constantSpecies) or (not empty actionBean.dominantSpecies)}">
           <div class="species-container">
               <div trigger_for="1" class="species-header tab-visible">Diagnostic species</div>
               <div trigger_for="2" class="species-header">Constant species</div>
               <div trigger_for="3" class="species-header">Dominant species</div>

               <div tab="1" id="pagination1" class="diagnostic-wrapper tab-body tab-visible">
                <c:forEach items="${actionBean.diagnosticSpecies}" var="specie">
                    <div class="diagnostic-species">
                        <span class="photoAlbumEntryWrapper">
                        <a href="/species/${specie.source.idSpecies}">
                        <img src="images/species/${specie.source.idSpecies}/thumbnail.jpg" onerror="this.src='images/species/${eunis:getDefaultPicture(specie.group)}';"/>
                        </a>
                        </span>
                        <span class="photoAlbumEntryTitle">
                            ${specie.commonName}

                            <span class="italics">${specie.scientificName} </span>
                        </span>
                    </div>
                </c:forEach>
            </div>
            <div tab="2" id="pagination2" class="constant-wrapper tab-body">
            <c:forEach items="${actionBean.constantSpecies}" var="specie">
                <div class="constant-species">
                    <span class="photoAlbumEntryWrapper">
                    <a href="/species/${specie.source.idSpecies}">
                        <img src="images/species/${specie.source.idSpecies}/thumbnail.jpg" onerror="this.src='images/species/${eunis:getDefaultPicture(specie.group)}';"/>
                    </a>
                    </span>
                    <span class="photoAlbumEntryTitle">
                        ${specie.commonName}

                        <span class="italics">${specie.scientificName} </span>
                    </span>
                </div>
            </c:forEach>
            </div>
            <div tab="3" id="pagination3" class="dominant-wrapper tab-body">
                <c:forEach items="${actionBean.dominantSpecies}" var="specie">
                    <div class="dominant-species">
                        <span class="photoAlbumEntryWrapper">
                        <a href="/species/${specie.source.idSpecies}">
                            <img src="images/species/${specie.source.idSpecies}/thumbnail.jpg" onerror="this.src='images/species/${eunis:getDefaultPicture(specie.group)}';"/>
                        </a>
                        </span>
                        <span class="photoAlbumEntryTitle">
                            ${specie.commonName}

                            <span class="italics">${specie.scientificName} </span>
                        </span>
                    </div>
                </c:forEach>
            </div>
        </div>
        <script>
            var tabs = $('.tab-body');
            $('.species-header').click(function() {
                var self = this
                tabs.each(function (index,item) {
                    if($(item).attr('tab') == $(self).attr('trigger_for')) {
                        $(item).animate({
                            'opacity': 'show'
                        })
                        $('.species-header').removeClass('tab-visible')
                        $(self).addClass('tab-visible')
                    }else {
                        $(item).css('display','none')
                    }
                })
            })
        </script>
        <script>
            $('.tab-body').each(function (index, item) {
                var children = $(this).children('div')
                var pages = Math.ceil(children.length / 6)
                if (pages > 1) {
                    var current_page_number = 1
                    var next_button = "<button id='next' class='btn'>→</button>"
                    var prev_button = "<button id='prev' class='btn'>←</button>"
                    var pages_number = "<span class='max_page'>"+ pages +"</span>"
                    var current_page ="<span class='current_page'>"+ current_page_number +"</span>"
                    var pages_wrapper = "<div class='pagination-controls'> "+prev_button+"<span class='page_number_wrapper'>"+ current_page +"/"+ pages_number +"<span>"+ next_button +"</div>"
                    $(item).prepend(pages_wrapper)
                }
            })
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