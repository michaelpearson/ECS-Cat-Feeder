var pages = window.pages || {};
pages.schedule = {
    highlightedDayEl : null,
    highlightedDay : null,
    rendered : false,
    pageArguments : null,
    schedulePanel : null,
    isFirstRender : true,
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.schedule;
        me.pageArguments = pageArguments;
        me.renderCompleteCallback = renderCompleteCallback;

        me.initControls();

        if(pageArguments.date) {
            me.renderSchedulePage({startDate : moment(pageArguments.date)});
        } else if(pageArguments.id) {
           me.renderSchedulePageFromId(pageArguments.id);
        } else {
            me.renderCalendarPage();
        }
    },
    renderCalendarPage : function () {
        $('#schedule-page.page').css({
            display : 'block'
        });
        $('.fc-today-button').click();
        if(!this.isFirstRender) {
            this.schedulePanel.fullCalendar('refetchEvents');
        }
        this.isFirstRender = false;
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
            foodTypeEl.append('<option value="' + element.id + '" data-default-amount="' + element.defaultGramAmount + '">' + element.name + '</option>')
        });


        var recurringCheckboxEl = $('#schedule-recurring');
        recurringCheckboxEl[0].checked = data.recurring || false;
        recurringCheckboxEl.change();

        $('#schedule-days-of-week').find('input[type=checkbox]').each(function (index, element) {
            element.checked = data.daysOfWeek[index];
        });

        $('#schedule-start-date').val(moment(data.startDate).format("YYYY-MM-DDTHH:mm:ss"));
        $('#schedule-end-date').val(moment(data.endDate || '').format("YYYY-MM-DDTHH:mm:ss"));


        foodTypeEl.change(function () {
            var defaultAmount = foodTypeEl.find(":selected").attr('data-default-amount');
            $('#schedule-amount').val(defaultAmount).trigger('input');
        });

        var amount = parseInt(data.gramAmount);
        if(!isNaN(amount)) {
            $('#schedule-amount').val(amount).trigger('input');
        } else {
            foodTypeEl.change();
        }

        foodTypeEl.val((data.foodType || {}).id);
        $('#schedule-notes').val(data.notes);

        this.renderCompleteCallback();
    },
    initControls : function () {
        if(this.rendered) {
            return;
        }
        this.rendered = true;

        this.schedulePanel = $('.schedule-panel');

        this.schedulePanel.fullCalendar({
            events: this.getEvents,
            eventClick : this.eventClick,
            dayClick : this.dayClick
        });

        var scheduleAmountIndicator = $('#schedule-amount-indicator');
        var scheduleAmount = $('#schedule-amount');

        scheduleAmount.on('input', function () {
            scheduleAmountIndicator.text(scheduleAmount.val() + ' grams');
        }).trigger('input');

        $('#schedule-add-new-event').click(this.saveSchedule);
        $('#schedule-update-event').click(this.updateSchedule);
        $('#schedule-cancel-event').click(function () {
            window.location.hash = "#/schedule";
        });
        $('#schedule-delete-event').click(function () {
            deleteScheduledFoodDelivery(this.pageArguments.id, function () {
                window.location.hash = "#/schedule";
                this.schedulePanel.fullCalendar('refetchEvents');
            }.bind(this));
        }.bind(this));

        $('#schedule-recurring').change(function () {
            if(!this.checked) {
                $('.recurring-options').hide();
            } else {
                $('.recurring-options').show();
            }
        });

        $(document).click(function (event) {
            if(!$.contains(this.schedulePanel[0], event.toElement)) {
                this.highlightDay(null);
            }
        }.bind(this));
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
        var days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
        $('#schedule-days-of-week').find('input[type=checkbox]').each(function (i, element) {
            if(!element.checked) {
                return;
            }
            daysOfWeek.push(days[i]);
        });
        var recurring = $('#schedule-recurring')[0].checked;
        var startDate = moment($('#schedule-start-date').val());
        var endDate = moment($('#schedule-end-date').val());
        var notes = $('#schedule-notes').val();
        var amount = parseInt($('#schedule-amount').val());
        var feederId = app.getCurrentFeederId();
        var type = $('#schedule-food-type').val();

        if(startDate.isValid()) {
            startDate = startDate.toDate().getTime();
        } else {
            startDate = 0;
        }

        if(endDate.isValid()) {
            endDate = endDate.toDate().getTime();
        } else {
            endDate = 0;
        }

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

        if(day != null && day.isSame(me.highlightedDay)) {
            window.location.href = "#/schedule/date/" + day.format("YYYY-MM-DD");
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
            element.append($('<div class="schedule-item-add"><i class="fa fa-plus"></i></div>'));
        }
    },
    dayClick : function (day) {
        var me = pages.schedule;
        var element = $(this);
        me.highlightDay(day, element);
    },
    getEvents : function (start, end, timezone, callback) {
        var me = pages.schedule;
        var startMonth = start.month();
        var endMonth = end.month();
        if(endMonth < startMonth) {
            endMonth += 12;
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
        $.when.apply($, ajax).done(function () {
            callback(build);
            if(me.renderCompleteCallback != null) {
                me.renderCompleteCallback();
                me.renderCompleteCallback = null;
            }
        });
    }
};