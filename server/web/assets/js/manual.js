var pages = window.pages || {};
pages.manual = {
    init : false,
    foodTypeEl : null,
    amountEl : null,
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.manual;
        me.renderCompleteCallback = renderCompleteCallback;
        $('#manual-control-page.page').css({
            display : 'block'
        });
        var foodDeliveryHtml = "<div class=\"row food-type\">\n    <div class=\"col-md-4\">\n        <h3 style=\"margin: 0;\"><span class=\"food-type-name\"></span></h3>\n    </div>\n    <div class=\"col-md-3\">\n        <div class=\"input-group\" style=\"width: 80%\">\n            <input class=\"form-control\" type=\"number\" step=\"1.00\" placeholder=\"0.00\"/>\n            <span class=\"input-group-addon\">grams</span>\n        </div>\n    </div>\n    <div class=\"col-md-5\">\n        <button class=\"btn btn-default\">Deliver</button>\n    </div>\n</div>"

        var foodTypeEl = $('#manual-food-type');
        foodTypeEl.children().remove();
        $(app.getFeederInfo().foodTypes).each(function (i, element) {
            foodTypeEl.append('<option value="' + element.id + '">' + element.name + '</option>')
        });

        me.initControls();
    },
    initControls : function () {
        var me = pages.manual;
        if(me.init) {
            return;
        }
        me.init = true;
        $('#manual-deliver').click(me.deliverFood);
        me.foodTypeEl = $('#manual-food-type');
        me.amountEl = $('#manual-deliver-amount');

        var amountIndicator = $('#manual-amount-indicator');
        me.amountEl.on('input', function () {
            amountIndicator.text(me.amountEl.val() + " grams");
        }).trigger('input');
    },
    deliverFood : function () {
        var me = pages.manual;
        var amount = me.amountEl.val();
        var type = me.foodTypeEl.val();
        var id = app.getCurrentFeederId();
        deliverFood(amount, type, id);
    }
};