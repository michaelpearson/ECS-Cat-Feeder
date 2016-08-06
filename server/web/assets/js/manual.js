var pages = window.pages || {};
pages.manual = {
    foodTypeCount : 2,
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.manual;
        me.renderCompleteCallback = renderCompleteCallback;
        $('#manual-control-page.page').css({
            display : 'block'
        });
        var foodDeliveryHtml = "<div class=\"row food-type\">\n    <div class=\"col-md-4\">\n        <h3 style=\"margin: 0;\">Food Type <span class=\"index\"></span></h3>\n    </div>\n    <div class=\"col-md-3\">\n        <div class=\"input-group\" style=\"width: 80%\">\n            <input class=\"form-control\" type=\"number\" step=\"1.00\" placeholder=\"0.00\"/>\n            <span class=\"input-group-addon\">grams</span>\n        </div>\n    </div>\n    <div class=\"col-md-5\">\n        <button class=\"btn btn-default\">Deliver</button>\n    </div>\n</div>"

        getAllFeeders(function (response) {
            if(!response.success) {
                return;
            }
            var el = $('.food-delivery-panel');
            el.children().remove();

            var feeders = response.catFeeders || [];
            for(var b = 0;b < feeders.length;b++) {
                for(var a = 0;a < me.foodTypeCount;a++) {
                    var foodTypeEl = $(foodDeliveryHtml);
                    el.append(foodTypeEl);

                    foodTypeEl.find('.index').text(a);
                    foodTypeEl.find('.btn').click(function (index, hardware_id) {
                        me.deliverFood(this.find('input[type=number]').val(), index, hardware_id);
                    }.bind(foodTypeEl, a, feeders[b].hardwareId));
                }
            }
        });
    },
    deliverFood : function (amount, type, hardwareId) {
        deliverFood(amount, type, hardwareId, function () {
            
        });
    }
};