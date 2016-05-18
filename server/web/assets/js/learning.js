var pages = window.pages || {};
pages.learning = {
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.dashboard;
        me.renderCompleteCallback = renderCompleteCallback;
        $('#learning-page.page').css({
            display : 'block'
        });
    }
};