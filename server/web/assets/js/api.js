function addRequestHeader(xhr) {
    xhr.setRequestHeader ("Authorization", "Bearer " + localStorage.getItem("token"));
}

function getProfileInformation(successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/user', {
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
    return $.ajax('/api/user', {
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
    return $.ajax('/api/feeder/list', {
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
    return $.ajax('/api/feeder/' + hardwareId + '/deliverFood', {
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

function scheduleFoodDelivery(options, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/schedule', {
        method : 'post',
        data : options,
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

function updateScheduledFoodDelivery(id, options, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/schedule/' + id, {
        method : 'post',
        data : options,
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
    return $.ajax('/api/schedule/' + id, {
        method : 'delete',
        beforeSend : addRequestHeader,
        success : function (response) {
            if (successCallback) {
                successCallback(response);
            }
        },
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
function updateFoodType(foodTypeId, defaultAmount, name, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/food/' + foodTypeId, {
        method : 'post',
        data : {
            defaultGramAmount : defaultAmount,
            name : name
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
function getLastCardId(hardwareId, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/feeder/' + hardwareId + '/tags/available', {
        method : 'get',
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

function getLogOfEvents(hardwareId, maxNumberOfItems, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/status/log/' + hardwareId + "/list?maxItems=" + maxNumberOfItems, {
        method : 'get',
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
function saveTag(tag, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/status/tag', {
        method : 'post',
        data : tag,
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
function deleteTag(tag, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/status/tag/' + tag.tagUID, {
        method : 'delete',
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
function listAllKnownTags(tag, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/status/tag/list', {
        method : 'get',
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
function setAuthenticatedTag(feederHardwareId, tagId, successCallback, failCallback, finallyCallback) {
    return $.ajax('/api/feeder/' + feederHardwareId + '/tag/setTrusted', {
        method : 'post',
        data : {
            tagId : tagId
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