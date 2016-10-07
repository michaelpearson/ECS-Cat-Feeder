self.addEventListener('push', function(event) {
    self.registration.showNotification("Things!");
});