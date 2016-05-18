var pages = window.pages || {};
pages.settings = {
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.dashboard;
        me.renderCompleteCallback = renderCompleteCallback;
        $('#settings-page.page').css({
            display : 'block'
        });
    }
};