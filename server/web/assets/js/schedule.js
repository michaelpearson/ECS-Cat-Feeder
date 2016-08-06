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
        var schedule = event.schedule;
        var delivery = schedule.deliveries[event.deliveryIndex];
        if(!schedule.recurring) {
            if(confirm("Are you sure you would like to delete this event?")) {
                deleteScheduledFoodDelivery(schedule.id, function () {
                    $('.schedule-panel').fullCalendar('refetchEvents');
                });
            }
        } else {
            throw "Not implemented";
        }
    },
    dayClick : function (day) {
        var catFeederId = 1;
        var amount = parseInt(prompt("How many grams?"));
        var foodIndex = parseInt(prompt("Which type of food?"));
        day.set('hour', prompt("Hour of day?"));
        day.set('minute', 0);

        scheduleFoodDelivery(catFeederId, amount, foodIndex, day.toDate(), function () {
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
                response.schedules = response.schedules || [];
                for(var a = 0;a < response.schedules.length; a++) {
                    response.schedules[a].deliveries = response.schedules[a].deliveries || [];
                    for(var b = 0; b < response.schedules[a].deliveries.length;b++) {
                        build.push({
                            title : response.schedules[a].deliveries[b].gramAmount + " grams",
                            start : moment(response.schedules[a].deliveries[b].date),
                            color : response.schedules[a].deliveries[b].foodIndex == 0 ? "green" : "yellow",
                            schedule : response.schedules[a],
                            deliveryIndex : b
                        });
                    }
                }
                pending--;
                if(pending == 0) {
                    callback(build);
                }
            });
        }
    }
};