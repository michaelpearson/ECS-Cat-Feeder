function getProfileInformation(successCallback, failCallback, finallyCallback) {
    $.ajax('/api/profile', {
        method : 'get',
        data : {},
        success : function (response) {
            if(response.success) {
                if(successCallback) {
                    successCallback(response);
                }
            } else {
                if(failCallback) {
                    failCallback(response);
                }
            }
        },
        complete : finallyCallback || function () {}
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
                if(successCallback) {
                    successCallback(response);
                }
            } else {
                if(failCallback) {
                    failCallback(response);
                }
            }
        },
        complete : finallyCallback || function () {}
    });
}

function deliverFood(amount, type, successCallback, failCallback, finallyCallback) {
    $.ajax('/api/manual/deliverFood', {
        method : 'post',
        data : {
            amount : amount,
            type : type
        },
        success : function (response) {
            if(response.success) {
                if(successCallback) {
                    successCallback(response);
                }
            } else {
                if(failCallback) {
                    failCallback(response);
                }
            }
        },
        complete : finallyCallback || function () {}
    });
}