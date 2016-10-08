var pages = window.pages || {};
pages.activity = {
    renderPage : function (pageArguments, renderCompleteCallback) {
        renderCompleteCallback();
        $('#activity-page.page').css({
            display : 'block'
        });
        this.init();
    },
    init : function () {
        if(this.initComplete) {
            return;
        }
        $('#activity-log').bootgrid({
            ajax : true,
            url : '/api/status/log/' + app.getCurrentFeederId() + '/list',
            ajaxSettings : {
                beforeSend : addRequestHeader,
                method : "get"
            },
            sorting : false,
            rowCount : 50,
            requestHandler : function (request) {
                return {
                    maxItems : request.rowCount,
                    offset : (request.current - 1) * request.rowCount
                };
            },
            responseHandler : function (data) {
                return {
                    rows : data.logEntries,
                    total : data.totalSize,
                    current : parseInt((data.offset / data.logEntries.length) + 1)
                };
            },
            formatters : {
                date : function (column, row) {
                    return moment(row.eventGeneratedAt).fromNow();
                }
            }
        });
        this.initComplete = true;
    },
};