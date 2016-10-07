var pages = window.pages || {};
pages.dashboard = {
    documentId : null,
    initComplete : false,
    renderCompleteCallback : null,
    logTableEl : null,
    renderPage : function (pageArguments, renderCompleteCallback) {
        this.renderCompleteCallback = renderCompleteCallback;
        $('#dashboard-page.page').css({
            display : 'block'
        });
        this.init();
        getLogOfEvents(app.getCurrentFeederId(), 10, this.displayLog.bind(this));
        //getFutureEvents(app.getCurrentFeederId(), 10, this.displayUpcoming.bind(this));
        //getActivitySummary(app.getCurrentFeederId(), this.displayActivity.bind(this));
        getWeightGraph(app.getCurrentFeederId(), this.displayWeightGraph.bind(this));
    },
    init : function () {
        if(this.initComplete) {
            return;
        }
        this.initComplete = true;
        this.logTableEl = $('#log-body');
    },
    displayLog : function (log) {
        if(this.renderCompleteCallback != null) {
            this.renderCompleteCallback();
            this.renderCompleteCallback = null;
        }

        for(var a = 0; a < log.logEntries.length;a++) {
            var date = $('<td>' + moment(log.logEntries[a].eventGeneratedAt).fromNow() + '</td>');
            var type = $('<td>' + log.logEntries[a].eventType + '</td>');
            var number = $('<td>' + log.logEntries[a].eventId + '</td>');
            var foodType = $('<td>');
            var amount = $('<td>');
            switch(log.logEntries[a].eventType) {
                case 'One off delivery':
                    foodType.text(log.logEntries[a].event.foodType.name);
                    amount.text(log.logEntries[a].event.gramAmount + "g");
                    break;
            }
            var row = $('<tr>');
            row.append([number, date, type, foodType, amount]);
            this.logTableEl.append(row);
        }
    },
    displayWeightGraph : function (data) {
        $('#morris-area-chart').children().remove();
        Morris.Area({
            element: 'morris-area-chart',
            data: data.weights,
            resize: true,
            xkey: 'time',
            ykeys: ['weight'],
            labels: ['Remaining food'],
            pointSize: 2,
            hideHover: 'auto'
        });
    }
};