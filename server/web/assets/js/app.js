// Array Remove - By John Resig (MIT Licensed)
Array.prototype.remove = function(from, to) {
    var rest = this.slice((to || from) + 1 || this.length);
    this.length = from < 0 ? this.length + from : from;
    return this.push.apply(this, rest);
};

$(function () {
    app.init();
});

var pages = window.pages || {};
var app = {
    disableNavigation : false,
    currentHash : "",
    currentPage : null,
    currentFeeder : null,
    init : function () {
        $('.page').css({
            display : 'none'
        });
        var profileInfo = getProfileInformation(function (response) {
            $('#user-name').text(response.name);
        }, function () {
            window.location.href = "/";
        });
        var feederInfo = app.invalidateFeederInfo();
        $.when(profileInfo, feederInfo).done(app.start);
    },
    start : function () {
        $(window).on('hashchange', function (e) {
            app.renderPage();
        }).trigger('hashchange');
        $(window).on('resize', function () {
            if(pages[app.currentPage] && pages[app.currentPage].resize) {
                pages[app.currentPage].resize();
            }
        });
    },
    updateCurrentFeeder : function (feeder) {
        var me = app;
        me.currentFeeder = feeder;
        $('#feeder-name').text(me.currentFeeder.name);
    },
    getCurrentFeederId : function () {
        var me = app;
        return me.currentFeeder.hardwareId;
    },
    getFeederInfo : function () {
        return app.currentFeeder;
    },
    decodeHash : function() {
        var me = app;
        try {
            var hash = window.location.hash.match(/^#?(.+)$/)[1];
        } catch (e) {
            hash = "/dashboard"
        }
        var arguments = hash.split("/");
        var argumentMap = {};
        var pageName = "dashboard";
        for (var a = 1; a < arguments.length; a++) {
            if (a == 1) {
                pageName = decodeURIComponent(arguments[a]);
                continue;
            }
            if (a < arguments.length - 1) {
                var key = decodeURIComponent(arguments[a]);
                var value = decodeURIComponent(arguments[a + 1]);
                if(argumentMap[key] !== undefined) {
                    if(!Array.isArray(argumentMap[key])) {
                        argumentMap[key] = [argumentMap[key]];
                    }
                    argumentMap[key].push(value);
                } else {
                    argumentMap[key] = value;
                }
                a++;
            }
        }
        var build = {
            arguments : argumentMap,
            pageName : pageName == "" ? "dashboard" : pageName,
            previousHash : me.currentHash
        };
        me.currentHash = hash;
        return build;
    },
    encodeHash : function(pageName, pageArguments, setHash) {
        setHash = setHash || false;
        var hash = "#/" + pageName;
        for (var k in pageArguments) {
            if (!pageArguments.hasOwnProperty(k)) {
                continue;
            }
            if(Array.isArray(pageArguments[k])) {
                for(var a = 0;a < pageArguments[k].length;a++) {
                    hash += '/' + k + '/' + encodeURIComponent(pageArguments[k][a]);
                }
            } else {
                hash += '/' + k + '/' + encodeURIComponent(pageArguments[k]);
            }
        }
        if (setHash) {
            window.location.hash = hash;
        }
        return hash;
    },
    renderPage : function() {
        if(app.disableNavigation) {
            return;
        }
        var pageState = app.decodeHash();
        $('.nav-link').removeClass("active");
        $('.nav-link.' + pageState.pageName).addClass("active");
        $('.page').css({
            display: 'none'
        });
        if(pages[app.currentPage] && pages[app.currentPage].unload) {
            pages[app.currentPage].unload();
        }
        if (pages[pageState.pageName] && pages[pageState.pageName].renderPage) {
            app.currentPage = pageState.pageName;
            app.beginNavigation();
            pages[pageState.pageName].renderPage(pageState.arguments, app.endNavigation.bind(this), pageState.previousHash);
            if(pages[pageState.pageName].resize) {
                pages[pageState.pageName].resize();
            }
        }
    },
    beginNavigation : function() {
        $('.loadingbar').remove();
        var bar = $('<div class="loadingbar"></div>');
        bar.animate({
            width: "90%"
        }, {
            duration: 1500
        });
        $('body').append(bar);
        return app.endNavigation.bind(this, false);
    },
    endNavigation : function(final) {
        var bar = $('.loadingbar');
        final = final || false;
        if (final) {
            bar.remove();
        } else {
            bar.animate({
                width: "100%",
                opacity: 0
            }, {
                duration: 500,
                queue: false,
                complete: app.endNavigation.bind(this, true)
            });
        }
    },
    invalidateFeederInfo : function () {
        return getAllFeeders(function (response) {
            response = response || {};
            var feeders = response.catFeeders || [];
            var menuEl = $('.feeder-menu');
            menuEl.children().remove();
            $(feeders).each(function (index, element) {
                var el = $('<li><a>' + this.name + '</a></li>');
                el.click(function (element) {
                    app.updateCurrentFeeder(element);
                }.bind(this, element));
                menuEl.append(el);
                return true;
            });
            menuEl.find('li').first().click();
        });
    }
};