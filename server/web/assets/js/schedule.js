var pages = window.pages || {};
pages.schedule = {
    renderPage : function (pageArguments, renderCompleteCallback) {

        var me = pages.schedule;
        me.renderCompleteCallback = renderCompleteCallback;
        $('#schedule-page.page').css({
            display : 'block'
        });
        me.initControls();
        renderCompleteCallback();
    },
    initControls : function () {
        var me = pages.schedule;
        $('.schedule-panel').fullCalendar({
            events: me.getEvents,
            eventClick : me.eventClick,
            dayClick : me.dayClick
        });
    },
    eventClick : function (event) {
        if(confirm("Are you sure you would like to delete this event?")) {
            deleteScheduledFoodDelivery(event.data.scheduleId, function () {
                $('.schedule-panel').fullCalendar('refetchEvents');
            });
        }
    },
    dayClick : function (day) {
        var amount = parseInt(prompt("How many grams?"));
        var foodIndex = parseInt(prompt("Which type of food?"));
        day.set('hour', prompt("Hour of day?"));
        day.set('minute', 0);
        var date = day.unix() * 1000;

        scheduleFoodDelivery(amount, foodIndex, date, function () {
            $('.schedule-panel').fullCalendar('refetchEvents');
        });
    },
    getEvents : function (start, end, timezone, callback) {
        var startMonth = start.month();
        var endMonth = end.month();
        if(endMonth < startMonth) {
            endMonth += 11;
        }

        var pending = 0, build = [];
        for(var a = startMonth; a <= endMonth;a++) {
            pending++;
            getAllScheduledDeliveries((a % 12) + 1, start.year() + ((a > 11) ? 1 : 0), function (response) {
                response.deliveries = response.deliveries || [];
                for(var a = 0;a < response.deliveries.length; a++) {
                    build.push({
                        title : response.deliveries[a].gramAmount + " grams",
                        start : moment(response.deliveries[a].date),
                        color : response.deliveries[a].foodIndex == 0 ? "green" : "yellow",
                        data : response.deliveries[a]
                    });
                }
                pending--;
                if(pending == 0) {
                    callback(build);
                }
            });
        }
    }
};