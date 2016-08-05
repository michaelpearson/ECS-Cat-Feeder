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
function updateProfileInformation(name, password, successCallback, failCallback, finallyCallback) {
    $.ajax('/api/profile', {
        method : 'post',
        data : {
            name : name,
            password : password
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

function scheduleFoodDelivery(amount, type, timestamp, successCallback, failCallback, finallyCallback) {
    $.ajax('/api/schedule', {
        method : 'post',
        data : {
            gramAmount : amount,
            foodIndex : type,
            date : timestamp,
            recurring : false
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


function deleteScheduledFoodDelivery(id, successCallback, failCallback, finallyCallback) {
    $.ajax('/api/schedule?id=' + id, {
        method : 'delete',
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

function getAllScheduledDeliveries(month, year, successCallback, failCallback, finallyCallback) {
    $.ajax('/api/schedule', {
        method : 'get',
        data : {
            month : month,
            year : year
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

