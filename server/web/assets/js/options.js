var pages = window.pages || {};
pages.options = {
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.dashboard;
        me.renderCompleteCallback = renderCompleteCallback;
        $('#options-page.page').css({
            display : 'block'
        });
    }
};