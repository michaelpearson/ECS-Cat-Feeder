function getProfileInformation(successCallback, failCallback, finallyCallback) {
    $.ajax('/api/profile', {
        method : 'get',
        data : {},
        success : function (response) {
            if(response.success) {
                successCallback(response);
            } else {
                failCallback(response);
            }
        },
        complete : finallyCallback
    });
}
function updateProfileInformation(name, email, successCallback, failCallback, finallyCallback) {
    $.ajax('/api/profile', {
        method : 'post',
        data : {
            name : name,
            email : email
        },
        success : function (response) {
            if(response.success) {
                successCallback(response);
            } else {
                failCallback(response);
            }
        },
        complete : finallyCallback
    });
}