function addRequestHeader(xhr) {
    xhr.setRequestHeader ("Authorization", "Bearer " + localStorage.getItem("token"));
}

function getProfileInformation(successCallback, failCallback, finallyCallback) {
    $.ajax('/api/user', {
        method : 'get',
        data : {},
        beforeSend : addRequestHeader,
        success : function (response) {
            successCallback(response);
        },
        error : failCallback,
        complete : finallyCallback || function () {}
    });
}
function updateProfileInformation(name, password, successCallback, failCallback, finallyCallback) {
    $.ajax('/api/user', {
        method : 'post',
        data : {
            name : name,
            password : password
        },
        beforeSend : addRequestHeader,
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

function getAllFeeders(successCallback, failCallback, finallyCallback) {
    $.ajax('/api/feeder/list', {
        method : 'get',
        data : {},
        beforeSend : addRequestHeader,
        success : function (response) {
            successCallback(response);
        },
        error : failCallback,
        complete : finallyCallback || function () {}
    });
}

function deliverFood(amount, type, hardwareId, successCallback, failCallback, finallyCallback) {
    $.ajax('/api/feeder/' + hardwareId + '/deliverFood', {
        method : 'post',
        data : {
            amount : amount,
            type : type
        },
        beforeSend : addRequestHeader,
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

function scheduleFoodDelivery(catfeederId, amount, type, date, successCallback, failCallback, finallyCallback) {
    $.ajax('/api/schedule', {
        method : 'post',
        data : {
            feederId : catfeederId,
            gramAmount : amount,
            foodIndex : type,
            date : date,
            recurring : false
        },
        beforeSend : addRequestHeader,
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
    $.ajax('/api/schedule/' + id, {
        method : 'delete',
        beforeSend : addRequestHeader,
        success : function (response) {
            if (successCallback) {
                successCallback(response);
            }
        },
        error : function () {
            if(failCallback) {
                failCallback(response);
            }
        },
        complete : finallyCallback || function () {}
    });
}

function getAllScheduledDeliveries(month, year, successCallback, failCallback, finallyCallback) {
    $.ajax('/api/schedule/list', {
        method : 'get',
        data : {
            month : month,
            year : year
        },
        beforeSend : addRequestHeader,
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

