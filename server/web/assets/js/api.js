function addRequestHeader(xhr) {
    xhr.setRequestHeader ("Authorization", "Bearer " + localStorage.getItem("token"));
}
function successHandler(successCallback, failCallback) {
    return function (response) {
        if(response.success) {
            if(successCallback) {
                successCallback(response);
            }
        } else {
            if(failCallback) {
                failCallback(response);
            }
        }
    }
}

function getProfileInformation(successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/user', {
        method : 'get',
        data : {},
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        error : failCallback,
        complete : finallyCallback || function () {}
    });
}
function updateProfileInformation(name, password, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/user/information', {
        method : 'post',
        data : {
            name : name,
            password : password
        },
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}

function getAllFeeders(successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/feeders/list', {
        method : 'get',
        data : {},
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        error : failCallback,
        complete : finallyCallback || function () {}
    });
}

function deliverFood(amount, type, hardwareId, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/feeder/' + hardwareId + '/deliverFood', {
        method : 'post',
        data : {
            amount : amount,
            type : type
        },
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}

function scheduleFoodDelivery(options, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/schedule', {
        method : 'post',
        data : options,
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}

function updateScheduledFoodDelivery(id, options, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/schedule/' + id, {
        method : 'post',
        data : options,
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}


function deleteScheduledFoodDelivery(id, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/schedule/' + id, {
        method : 'delete',
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        error : function (response) {
            if(failCallback) {
                failCallback(response);
            }
        },
        complete : finallyCallback || function () {}
    });
}

function getScheduledDelivery(id, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/schedule/' + id, {
        method : 'get',
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        error : failCallback || function () {},
        complete : finallyCallback || function () {}
    });
}

function getAllScheduledDeliveries(month, year, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/schedule/list', {
        method : 'get',
        data : {
            month : month,
            year : year
        },
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}
function updateFoodType(foodTypeId, defaultAmount, name, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/food/' + foodTypeId, {
        method : 'post',
        data : {
            defaultGramAmount : defaultAmount,
            name : name
        },
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}
function getLastCardId(hardwareId, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/feeder/' + hardwareId + '/tag/lastTag', {
        method : 'get',
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}

function getLogOfEvents(hardwareId, maxNumberOfItems, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/status/log/' + hardwareId + "/list?maxItems=" + maxNumberOfItems, {
        method : 'get',
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}
function saveTag(feederId, tag, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/feeder/' + feederId + '/tag', {
        method : 'post',
        data : tag,
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}
function deleteTag(feederId, tagId, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/feeder/' + feederId + '/tag/' + tagId, {
        method : 'delete',
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}
function listAllKnownTags(feederId, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/feeder/' + feederId + '/tag/list', {
        method : 'get',
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}
function setAuthenticatedTag(feederHardwareId, tagId, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/feeder/' + feederHardwareId + '/tag/setTrusted', {
        method : 'post',
        data : {
            tagId : tagId
        },
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}

function readWeight(feederHardwareId, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/feeder/' + feederHardwareId + '/weight', {
        method : 'get',
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}

function tareScale(feederHardwareId, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/feeder/' + feederHardwareId + '/tare', {
        method : 'put',
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}

function createNewFeeder(feederHardwareId, name, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/feeders/new', {
        method : 'post',
        data : {
            hardwareId : feederHardwareId,
            name : name
        },
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}

function createNewUser(feederHardwareId, email, name, password, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/user', {
        method : 'post',
        data : {
            feederId : feederHardwareId,
            email : email,
            name : name,
            password : password
        },
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}

function sendTestEmail(successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/test/email', {
        method : 'get',
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}
function setMaxFoodAmount(feederHardwareId, maxFoodAmount, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/feeder/' + feederHardwareId + '/foodLimit', {
        method : 'post',
        data : {
            maxFoodAmount : maxFoodAmount
        },
        beforeSend : addRequestHeader,
        success : successHandler(successCallback, failCallback),
        complete : finallyCallback || function () {}
    });
}