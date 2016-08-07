var pages = window.pages || {};
pages.manual = {
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.manual;
        me.renderCompleteCallback = renderCompleteCallback;
        $('#manual-control-page.page').css({
            display : 'block'
        });
        var foodDeliveryHtml = "<div class=\"row food-type\">\n    <div class=\"col-md-4\">\n        <h3 style=\"margin: 0;\"><span class=\"food-type-name\"></span></h3>\n    </div>\n    <div class=\"col-md-3\">\n        <div class=\"input-group\" style=\"width: 80%\">\n            <input class=\"form-control\" type=\"number\" step=\"1.00\" placeholder=\"0.00\"/>\n            <span class=\"input-group-addon\">grams</span>\n        </div>\n    </div>\n    <div class=\"col-md-5\">\n        <button class=\"btn btn-default\">Deliver</button>\n    </div>\n</div>"

        var el = $('.food-delivery-panel');
        el.children().remove();

        var feeder = app.getFeederInfo();
        for(var a = 0;a < feeder.foodTypes.length;a++) {
            var foodTypeEl = $(foodDeliveryHtml);
            el.append(foodTypeEl);

            foodTypeEl.find('.food-type-name').text(feeder.foodTypes[a].name);
            foodTypeEl.find('.btn').click(function (index, hardware_id) {
                me.deliverFood(this.find('input[type=number]').val(), index, hardware_id);
            }.bind(foodTypeEl, feeder.foodTypes[a].id, feeder.hardwareId));
        }
    },
    deliverFood : function (amount, type, hardwareId) {
        deliverFood(amount, type, hardwareId, function () {
            
        });
    }
};