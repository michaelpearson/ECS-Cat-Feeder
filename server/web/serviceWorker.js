self.addEventListener('push', function(event) {
    self.registration.pushManager.getSubscription().then((subscription) => {
        var endpoint = subscription.endpoint.split('/');
        endpoint = endpoint[endpoint.length - 1];
        return fetch('/api/user/notifications?registration=' + endpoint);
    }).then((response) => {
        return response.text();
    }).then((text) => {
        var notifications = JSON.parse(text);
        for(var a = 0;a < notifications.unseenNotifications.length;a++) {
            showNotification(notifications.unseenNotifications[a]);
        }
    });
});

function showNotification(notification) {
    self.registration.showNotification(notification.notificationBody);
}