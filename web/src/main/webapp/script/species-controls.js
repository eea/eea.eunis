/**
 * Created by mihai-macaneata on 1/23/2018.
 */

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

$('.tab-body').each(function (index, item) {
    var children = $(this).children('div')
    var pages = Math.ceil(children.length / 8)
    if(children.length > 4){
        $('.tab-body').addClass('big-container')
    }
    if(children.length == 0) {
        var tab_index = parseInt($(this).attr('tab'));
        $(this).parent().find('[trigger_for="'+tab_index+'"]').remove();
        $(this).remove();
        if(tab_index == 1) {
            $('.tab-body[tab="'+tab_index+1+'"]').addClass('tab-visible')
            $('.species-header[trigger_for="'+tab_index+1+'"]').addClass('tab-visible')
        }
    }
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
    for (var i=0; i<= current_displayed; i++) {
        $(children[i]).css('display','none')
    }
    for (var i = current_displayed + 1; i<= current_displayed + 8; i++){
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
    for (var i=current_displayed; i >= current_displayed - 8; i--) {
        $(children[i]).css('display','none')
    }
    for (var i = current_displayed - 8; i >= current_displayed - 15 ; i--){
        $(children[i]).css('display','block')
    }
})

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
