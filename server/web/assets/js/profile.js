var pages = window.pages || {};
pages.profile = {
    initialized : false,
    passwordElement : null,
    nameElement : null,
    emailElement : null,
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.profile;
        me.renderCompleteCallback = renderCompleteCallback;
        $('#profile-page.page').css({
            display : 'block'
        });
        if(!me.initialized) {
            me.initControls();
        }
        me.initPage();
    },
    initPage : function () {
        getProfileInformation(function (response) {
            this.passwordElement.val("");
            this.emailElement.val(response.user.email);
            this.nameElement.val(response.user.name);
            var notificationTypes = response.user.preferredNotificationTypes;


            var elements = $('.notification-checkbox');
            var email = elements[0];
            var browser = elements[1];

            notificationTypes.forEach(el => {
                switch(el) {
                    default:
                    case 0:
                        console.log(email);
                        email.checked = true;
                        break;
                    case 1:
                        browser.checked = true;
                        break;
                }
            });

        }.bind(this), function (response) {
            console.error("There was an error getting profile information; " + response.message);
        }, function () {
            if(this.renderCompleteCallback != null) {
                this.renderCompleteCallback();
                this.renderCompleteCallback = null;
            }
        }.bind(this));
        $('.notification-via-save').click(this.saveNotificationSettings.bind(this));

        var button = $('input.notification-checkbox[value=browser]');
        button.click(function () {
            if(button.prop('checked')) {
                this.registerNotifications();
            }
        }.bind(this))
    },
    initControls : function () {
        this.passwordElement = $('#profile-password');
        this.nameElement = $('#profile-name');
        this.emailElement = $('#profile-email');
        $('#profile-save').click(function () {
            this.save();
        }.bind(this));
    },
    save : function () {
        var name = this.nameElement.val();
        var password = this.passwordElement.val();
        this.renderCompleteCallback = app.beginNavigation();
        updateProfileInformation(name, password, function () {
            this.renderCompleteCallback();
            $('#user-name').text(name);
        }, function (response) {
            console.error("Could not update profile information; " + response.message);
            this.renderCompleteCallback();
        });
    },
    saveNotificationSettings : function () {
        var elements = $('.notification-checkbox');
        var types = [];
        //Email -> 0
        if(elements[0].checked) {
            types.push(0);
        }
        //Browser -> 1
        if(elements[1].checked) {
            types.push(1);
        }
        saveNotificationPreferences(types);
    },
    doesHaveNotificationPermissions : function () {
        return (window.Notification || {}).permission === 'granted';
    },
    registerNotifications : function (afterRequestPermissions) {
        var page = this;
        try {
            if(afterRequestPermissions && !this.doesHaveNotificationPermissions()) {
                $('input.notification-checkbox[value=browser]')[0].checked = false;
                return;
            } else if(!this.doesHaveNotificationPermissions()) {
                Notification.requestPermission(this.registerNotifications.bind(this, true));
                return;
            }
            navigator.serviceWorker.register('/serviceWorker.js');

            navigator.serviceWorker.ready.then(serviceWorkerRegistration => {
                return serviceWorkerRegistration.pushManager.getSubscription();
            }).then(subscription => {
                if(subscription) {
                    page.saveSubscription(subscription);
                } else {
                    page.subscribe();
                }
            });
        } catch(e) {
            this.showError(e.message);
        }
    },
    subscribe : function () {
        navigator.serviceWorker.ready.then(serviceWorkerRegistration => {
            serviceWorkerRegistration.pushManager.subscribe({
                userVisibleOnly: true
            }).then(subscription => {
                this.saveSubscription(subscription);
            });
        });
    },
    saveSubscription : function (subscription) {
        var endpoint = subscription.endpoint.split('/');
        var registrationId = (endpoint[endpoint.length - 1]);
        saveRegistrationId(registrationId);
    },
    showError : function (message) {
        alert(message);
    }
};