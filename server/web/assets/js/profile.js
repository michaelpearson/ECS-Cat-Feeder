var pages = window.pages || {};
pages.profile = {
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.dashboard;
        me.renderCompleteCallback = renderCompleteCallback;
        $('#profile-page.page').css({
            display : 'block'
        });
    }
};