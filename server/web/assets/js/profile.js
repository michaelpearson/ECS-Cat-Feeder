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
        var me = pages.profile;
        getProfileInformation(function (response) {
            me.passwordElement.val("");
            me.emailElement.val(response.email);
            me.nameElement.val(response.name);
        }, function (response) {
            console.error("There was an error getting profile information; " + response.message);
        }, function () {
            if(me.renderCompleteCallback != null) {
                me.renderCompleteCallback();
                me.renderCompleteCallback = null;
            }
        });
    },
    initControls : function () {
        var me = pages.profile;
        me.passwordElement = $('#profile-password');
        me.nameElement = $('#profile-name');
        me.emailElement = $('#profile-email');
        $('#profile-save').click(function () {
            me.save();
        });
    },
    save : function () {
        var me = pages.profile;
        var name = me.nameElement.val();
        var password = me.passwordElement.val();
        me.renderCompleteCallback = app.beginNavigation();
        updateProfileInformation(name, password, function (response) {
            me.renderCompleteCallback();
            $('#user-name').text(name);
        }, function (response) {
            console.error("Could not update profile information; " + response.message);
            me.renderCompleteCallback();
        });
    }
};