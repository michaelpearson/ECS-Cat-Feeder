var pages = window.pages || {};
pages.schedule = {
    highlightedDayEl : null,
    highlightedDay : null,
    rendered : false,
    pageArguments : null,
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.schedule;
        me.pageArguments = pageArguments;
        me.renderCompleteCallback = renderCompleteCallback;

        if(pageArguments.date) {
            me.renderSchedulePage({startDate : pageArguments.date});
        } else if(pageArguments.id) {
           me.renderSchedulePageFromId(pageArguments.id);
        } else {
            me.renderCalendarPage();
        }

        me.initControls();
        renderCompleteCallback();
    },
    renderCalendarPage : function () {
        $('#schedule-page.page').css({
            display : 'block'
        });
        $('.fc-today-button').click();
    },
    renderSchedulePageFromId : function (id) {
        var me = pages.schedule;
        getScheduledDelivery(id, function (response) {
            me.renderSchedulePage(response.schedule);
        });
    },
    renderSchedulePage : function (data) {
        data = data || {};
        data.daysOfWeek = data.daysOfWeek || [];

        if(data.id) {
            $('#schedule-buttons-new').hide();
            $('#schedule-buttons-update').show();
        } else {
            $('#schedule-buttons-new').show();
            $('#schedule-buttons-update').hide();
        }

        $('#schedule-page-add-event.page').css({
            display : 'block'
        });
        var foodTypeEl = $('#schedule-food-type');
        foodTypeEl.children().remove();
        $(app.getFeederInfo().foodTypes).each(function (i, element) {
            foodTypeEl.append('<option value="' + element.id + '">' + element.name + '</option>')
        });

        $('#schedule-recurring')[0].checked = data.recurring || false;

        $('#schedule-days-of-week').find('input[type=checkbox]').each(function (index, element) {
            element.checked = data.daysOfWeek[index];
        });
        $('#schedule-start-date').val(moment(data.startDate).format("YYYY-MM-DDThh:ss"));
        $('#schedule-end-date').val(data.endDate || '');
        var amount = parseInt(data.gramAmount);
        $('#schedule-amount').val(isNaN(amount) ? 100 : amount).trigger('input');
        foodTypeEl.val((data.foodType || {}).id);
        $('#schedule-notes').val(data.notes);
    },
    initControls : function () {
        var me = pages.schedule;
        if(me.rendered) {
            return;
        }
        me.rendered = true;

        $('.schedule-panel').fullCalendar({
            events: me.getEvents,
            eventClick : me.eventClick,
            dayClick : me.dayClick
        });

        var scheduleAmountIndicator = $('#schedule-amount-indicator');
        var scheduleAmount = $('#schedule-amount');

        scheduleAmount.on('input', function () {
            scheduleAmountIndicator.text(scheduleAmount.val() + ' grams');
        }).trigger('input');

        $('#schedule-add-new-event').click(me.saveSchedule);
        $('#schedule-update-event').click(me.updateSchedule);
        $('#schedule-cancel-event').click(function () {
            window.location.hash = "#/schedule";
        });
        $('#schedule-delete-event').click(function () {
            deleteScheduledFoodDelivery(me.pageArguments.id, function () {
                window.location.hash = "#/schedule";
                $('.schedule-panel').fullCalendar('refetchEvents');
            });
        });
    },
    updateSchedule : function () {
        var me = pages.schedule;
        updateScheduledFoodDelivery(me.pageArguments.id, me.getScheduledData(), function () {
            window.location.hash = "#/schedule";
            $('.schedule-panel').fullCalendar('refetchEvents');
        });
    },
    saveSchedule : function () {
        var me = pages.schedule;
        scheduleFoodDelivery(me.getScheduledData(), function () {
            window.location.hash = "#/schedule";
            $('.schedule-panel').fullCalendar('refetchEvents');
        });
    },
    getScheduledData : function () {
        var daysOfWeek = [];
        $('#schedule-days-of-week').find('input[type=checkbox]').each(function (i, element) {
            daysOfWeek.push(element.checked);
        });
        var recurring = $('#schedule-recurring')[0].checked;
        var startDate = $('#schedule-start-date').val();
        var endDate = $('#schedule-end-date').val();
        var notes = $('#schedule-notes').val();
        var amount = parseInt($('#schedule-amount').val());
        var feederId = app.getCurrentFeederId();
        var type = $('#schedule-food-type').val();

        return {
            feederId : feederId,
            recurring : recurring,
            daysOfWeek : daysOfWeek,
            startDate : startDate,
            endDate : endDate,
            amountOfFood : amount,
            foodType : type,
            notes : notes
        };
    },
    eventClick : function (event) {
        var schedule = event.schedule;
        window.location.hash = "#/schedule/id/" + schedule.id;
    },
    highlightDay : function (day, element) {
        var me = pages.schedule;

        if(day.isSame(me.highlightedDay)) {
            window.location.href = "#/schedule/date/" + day;
            return;
        }

        if(me.highlightedDayEl) {
            me.highlightedDayEl.children().remove();
            me.highlightedDayEl = null;
            me.highlightedDay = null;
        }
        if(day) {
            me.highlightedDayEl = element;
            me.highlightedDay = day;
            var addButton = $('<div class="schedule-item-add"><i class="fa fa-plus"></i></div>');
            element.append(addButton);
        }
    },
    dayClick : function (day) {
        var me = pages.schedule;
        var element = $(this);
        me.highlightDay(day, element);
        return;
        var amount = parseInt(prompt("How many grams?"));
        var foodIndex = parseInt(prompt("Which type of food?"));
        day.set('hour', prompt("Hour of day?"));
        day.set('minute', 0);

        scheduleFoodDelivery(app.getCurrentFeederId(), amount, foodIndex, day.toDate(), function () {
            $('.schedule-panel').fullCalendar('refetchEvents');
        });
    },
    getEvents : function (start, end, timezone, callback) {
        var startMonth = start.month();
        var endMonth = end.month();
        if(endMonth < startMonth) {
            endMonth += 11;
        }

        var ajax = [], build = [];
        for(var a = startMonth; a <= endMonth;a++) {
            ajax.push(getAllScheduledDeliveries((a % 12) + 1, start.year() + ((a > 11) ? 1 : 0), function (response) {
                response.schedules = response.schedules || [];
                for(var a = 0;a < response.schedules.length; a++) {
                    response.schedules[a].deliveries = response.schedules[a].deliveries || [];
                    for(var b = 0; b < response.schedules[a].deliveries.length;b++) {
                        build.push({
                            title : response.schedules[a].deliveries[b].gramAmount + " grams",
                            start : moment(response.schedules[a].deliveries[b].date),
                            color : response.schedules[a].foodIndex == 0 ? "green" : "blue",
                            schedule : response.schedules[a],
                            deliveryIndex : b
                        });
                    }
                }
            }));
        }
        $.when.apply($, ajax).done(callback.bind(this, build));
    }
};