var pages = window.pages || {};
pages.dashboard = {
    documentId : null,
    initComplete : false,
    textAreaElement : null,
    renderCompleteCallback : null,
    codeMirrorObject : null,
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.dashboard;
        me.renderCompleteCallback = renderCompleteCallback;
        renderCompleteCallback();
        me.renderCompleteCallback = null;
        $('#dashboard-page.page').css({
            display : 'block'
        });
    }
};