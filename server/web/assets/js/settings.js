var pages = window.pages || {};
pages.settings = {
    editFoodTemplate : '<div class="col-lg-8 col-md-12">\n    <div class="form-horizontal">\n        <hr/>\n        <div class="form-group">\n            <label class="col-sm-2 control-label">Name</label>\n            <div class="col-sm-10">\n                <input class="form-control settings-food-name" type="text" />\n            </div>\n        </div>\n        <div class="form-group">\n            <label class="col-sm-2 control-label">Default Delivery Amount</label>\n            <div class="col-sm-10">\n                <input class="settings-food-default" type="range" value="" min="1" max="500" style="margin-top: 7px;">\n                <p id="settings-food-indicator"></p>\n            </div>\n        </div>\n        <div class="form-group">\n            <div class="col-sm-offset-2 col-sm-10">\n                <button type="submit" class="btn btn-default settings-food-update">Update</button>\n            </div>       \n        </div>\n    </div>\n</div>',
    init : false,
    knownTags : [],
    renderCompleteCallback : null,
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.settings;

        $('#settings-page.page').css({
            display : 'block'
        });
        me.renderCompleteCallback = renderCompleteCallback;
        me.renderFoodElements();
        me.updateTagList();
        me.initControls();
    },
    renderFoodElements : function () {
        var me = pages.settings;
        var knownFoodTypes = app.getFeederInfo().foodTypes;
        var foodElements = [];
        var foodContainerEl = $('.food-container');
        foodContainerEl.children().remove();
        for(var a = 0;a < knownFoodTypes.length;a++) {
            var el = $(me.editFoodTemplate);
            var foodNameEl = el.find('.settings-food-name');
            var defaultAmountEl = el.find('.settings-food-default');
            el.find('.settings-food-update').click(me.updateFood.bind(me, {
                id : knownFoodTypes[a].id,
                foodName : foodNameEl,
                amount : defaultAmountEl
            }));
            foodNameEl.val(knownFoodTypes[a].name);
            defaultAmountEl.val(knownFoodTypes[a].defaultGramAmount);
            foodContainerEl.append(el);
        }
    },
    updateTagList : function () {
        var me = pages.settings;
        listAllKnownTags(app.getCurrentFeederId(), function(data) {
                me.renderTagElements(data.tags || []);
            }, function(err) {
            }, function () {
                if(me.renderCompleteCallback) {
                    me.renderCompleteCallback();
                    me.renderCompleteCallback = null;
                }
            }
        );
    },
    renderTagElements : function (tags) {
        var me = pages.settings;
        me.knownTags = tags;
        var tagListEl = $('.settings-tag-list');

        tagListEl.children().remove();
        for(var a = 0; a < tags.length; a++) {
            var el = $('<option>');
            el.val(tags[a].id);
            el.text(tags[a].tagName);
            tagListEl.append(el);
        }
    },
    initControls : function () {
        var me = pages.settings;
        if(me.init) {
            return;
        }
        me.init = true;
        $('.settings-tag-forget').click(me.forgetTag.bind(this));

    },
    updateFood : function (element) {
        var id = element.id;
        var name = element.foodName.val();
        var amount = element.amount.val();
        updateFoodType(id, amount, name, app.invalidateFeederInfo);
    },
    forgetTag : function(tag){
        var me = pages.settings;
        var id = $('.settings-tag-list').val();
        deleteTag(app.getCurrentFeederId(), id, me.updateTagList);
        this.updateTagList();
    },
    generateHandler: function(list, j){
        return function(){
            $('#settings-food-indicator'+list[j].id).text($('#settings-food-default'+list[j].id).val() + 'grams')
        }
    }
};