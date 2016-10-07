var pages = window.pages || {};
pages.settings = {
    editFoodTemplate : '<div class="form-horizontal">\n    <hr/>\n    <div class="form-group">\n        <label class="col-sm-2 control-label">Name</label>\n        <div class="col-sm-10">\n            <input class="form-control settings-food-name" type="text" />\n        </div>\n    </div>\n    <div class="form-group">\n        <label class="col-sm-2 control-label">Default Delivery Amount</label>\n        <div class="col-sm-10">\n            <input class="settings-food-default" type="range" value="" min="1" max="500" style="margin-top: 7px;">\n            <p class="settings-food-indicator"></p>\n        </div>\n    </div>\n    <div class="form-group">\n        <div class="col-sm-offset-2 col-sm-10">\n            <button type="submit" class="btn btn-default settings-food-update">Update</button>\n        </div>       \n    </div>\n</div>',
    init : false,
    knownTags : [],
    renderCompleteCallback : null,
    maxFoodSlider : null,
    renderPage : function (pageArguments, renderCompleteCallback) {
        $('#settings-page.page').css({
            display : 'block'
        });
        this.renderCompleteCallback = renderCompleteCallback;
        this.initControls();
        this.renderFoodElements();
        this.updateTagList();
        this.maxFoodSlider.val(app.getFeederInfo().foodLimit).trigger('input');
    },
    renderFoodElements : function () {
        var me = pages.settings;
        var knownFoodTypes = app.getFeederInfo().foodTypes;
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
            defaultAmountEl.on('input', function (indicator, slider) {
                indicator.text(slider.val() + " grams");
            }.bind(this, el.find('.settings-food-indicator'), defaultAmountEl)).trigger('input');
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
        this.knownTags = tags;
        var tagListEl = $('.settings-tag-list');

        tagListEl.children().remove();
        for(var a = 0; a < tags.length; a++) {
            var el = $('<option>');
            el.val(tags[a].id);
            el.text(tags[a].tagName);
            tagListEl.append(el);
        }

        var stage = app.getFeederInfo().learningStage;
        switch(stage) {
            case 'STAGE_ONE':
                stage = 1;
                break;
            case 'STAGE_TWO':
                stage = 2;
                break;
            case 'STAGE_THREE':
                stage = 3;
                break;
            default:
                stage = 0;
                break;
        }
        $('.settings-learning-form input[value=' + stage + ']').prop('checked', 'true');

    },
    initControls : function () {
        var me = this;
        if(this.init) {
            return;
        }
        this.init = true;
        $('.settings-tag-forget').click(this.forgetTag.bind(this));
        var maxFoodIndicator = $('.settings-max-food-indicator');
        this.maxFoodSlider = $('.max-food-amount');
        this.maxFoodSlider.on('input', function () {
            maxFoodIndicator.text(me.maxFoodSlider.val() + " grams");
        }).trigger('input');
        $('.save-max-food-amount').click(function () {
            var maxAmount = parseInt(me.maxFoodSlider.val());
            setMaxFoodAmount(app.getCurrentFeederId(), maxAmount, app.invalidateFeederInfo);
        });
        $('.settings-learning-form input').click(me.selectLearningStage.bind(this));
    },
    updateFood : function (element) {
        var id = element.id;
        var name = element.foodName.val();
        var amount = element.amount.val();
        updateFoodType(id, amount, name, app.invalidateFeederInfo);
    },
    forgetTag : function(tag) {
        var me = pages.settings;
        var id = $('.settings-tag-list').val();
        deleteTag(app.getCurrentFeederId(), id, me.updateTagList);
        this.updateTagList();
    },
    selectLearningStage : function() {
        var stage = parseInt($('.settings-learning-form input:checked').val());

        setLearningStage(app.getCurrentFeederId(), stage,
            function(data){
                console.log(data);
            },
            function(err){
                console.log(err);
            }
        );
    }
};