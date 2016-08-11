var pages = window.pages || {};
pages.dashboard = {
    documentId : null,
    initComplete : false,
    renderCompleteCallback : null,
    logTableEl : null,
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.dashboard;
        me.renderCompleteCallback = renderCompleteCallback;
        $('#dashboard-page.page').css({
            display : 'block'
        });
        me.init();
        getLogOfEvents(app.getCurrentFeederId(), 10, me.displayLog);
    },
    init : function () {
        var me = pages.dashboard;
        if(me.initComplete) {
            return;
        }
        me.initComplete = true;
        me.logTableEl = $('#log-body');
    },
    displayLog : function (log) {
        var me = pages.dashboard;

        if(me.renderCompleteCallback != null) {
            me.renderCompleteCallback();
            me.renderCompleteCallback = null;
        }

        for(var a = 0; a < log.logEntries.length;a++) {
            var date = $('<td>' + moment(log.logEntries[a].eventGeneratedAt).fromNow() + '</td>');
            var type = $('<td>' + log.logEntries[a].eventType + '</td>');
            var number = $('<td>' + log.logEntries[a].eventId + '</td>');
            var foodType = $('<td>');
            var amount = $('<td>');
            switch(log.logEntries[a].eventType) {
                case 'FoodDelivery':
                    foodType.text(log.logEntries[a].event.foodType.name);
                    amount.text(log.logEntries[a].event.gramAmount + "g");
                    break;
            }
            var row = $('<tr>');
            row.append([number, date, type, foodType, amount]);
            me.logTableEl.append(row);
            console.log(log.logEntries[a]);
        }
    }
};