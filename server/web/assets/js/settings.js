var pages = window.pages || {};
pages.settings = {
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.settings;
        renderCompleteCallback();
        $('#settings-page.page').css({
            display : 'block'
        });
        var foodTypes = $('settings-edit-food-type');
        foodTypes.children().remove();
        $(app.getFeederInfo().foodTypes).each(function (i, element) {
            foodTypes.append('<option value="' + element.id + '">' + element.name + '</option>');
        });
        me.initControls();
    },
    initControls : function () {
        var me = pages.settings;
        if(me.init) {
            return;
        }
        me.init = true;

        me.foodTypeAl = $('#settings-add-food-type');
        me.amountAl = $('#settings-add-deliver-amount');
        me.foodTypeEl = $('#settings-edit-food-type');
        me.amountEl = $('#settings-edit-deliver-amount');

        var addAmountIndicator = $('#settings-add-amount-indicator');
        me.amountAl.on('input', function () {
            addAmountIndicator.text(me.amountAl.val() + " grams");
        }).trigger('input');

        var editAmountIndicator = $('#settings-edit-amount-indicator');
        me.amountEl.on('input', function () {
            editAmountIndicator.text(me.amountEl.val() + " grams");
        }).trigger('input');

        $('#settings-add-food').click(me.addFood);
        $('#settings-edit-food').click(me.updateFood);
    },
    addFood : function () {
        var me = pages.settings;
        var id = app.getCurrentFeederId();
        var amount = me.amountAl.val();
        var type = me.foodTypeAl.val();
        console.log(amount, type, id);
        //post new food type
    },
    updateFood : function () {
        var me = pages.settings;
        var defaultAmount = me.amountEl.val();
        var foodTypeId = me.foodTypeEl.val();
        foodTypeId = 1;
        updateFoodType(foodTypeId, defaultAmount, "New Name " + Math.random(), app.invalidateFeederInfo);
    }
};