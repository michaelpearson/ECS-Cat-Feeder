var pages = window.pages || {};
pages.manual = {
    init : false,
    foodTypeEl : null,
    amountEl : null,
    tagListEl : null,
    weightDisplayEl : null,
    tagTimer : 0,
    weightTimer : 0,
    tagItemTemplate : '<div class="form-group">\n    <label class="col-sm-2 control-label">Tag</label>\n    <div class="input-group">\n        <span class="input-group-addon tag-uid"></span>\n        <input type="text" class="form-control tag-name" aria-label="New tag name" title="New tag name">\n        <div class="input-group-btn">\n            <button class="btn btn-warning auth">Set as authorised</button>\n            <button class="btn btn-primary save">Save</button>\n        </div>\n    </div>\n</div>',
    foundTags : [],
    renderCompleteCallback : null,
    renderPage : function (pageArguments, renderCompleteCallback) {
        this.initControls();
        this.renderCompleteCallback = renderCompleteCallback;
        $('#manual-control-page.page').css({
            display : 'block'
        });
        this.foodTypeEl.children().remove();
        $(app.getFeederInfo().foodTypes).each(function (i, element) {
            this.foodTypeEl.append('<option value="' + element.id + '" data-default="' + element.defaultGramAmount + '">' + element.name + '</option>')
        }.bind(this));
        this.foodTypeEl.change();
        this.tagTimer = setInterval(this.detectTags.bind(this), 3000);
        this.weightTimer = setInterval(this.updateWeightDisplay.bind(this), 1000);
        this.detectTags();
        this.updateWeightDisplay(0);
    },
    unload : function () {
        clearInterval(this.tagTimer);
        clearInterval(this.weightTimer);
    },
    updateWeightDisplay : function () {
        readWeight(app.getCurrentFeederId(), function (response) {
            var weight = response.weight;
            this.weightDisplayEl.text(weight + " gram" + (weight != 1 ? "s" : ""));
        }.bind(this));
    },
    detectTags : function () {
        getLastCardId(app.getCurrentFeederId(), function (response) {
            var tag = response.tag || {};
            if(!tag || !tag.tagUID || tag.tagUID == 0) {
                return;
            }
            if(this.foundTags.some(function (element) { return element.tagUID == tag.tagUID; })) {
                return;
            }
            var el = $(this.tagItemTemplate);
            var uidEl = el.find('.tag-uid');
            var nameEl = el.find('.tag-name');
            uidEl.text("#" + tag.tagUID);
            nameEl.val(tag.tagName || "New tag #" + tag.tagUID);

            el.find('button.save').click(this.updateCard.bind(this, tag, nameEl, null));
            el.find('button.auth').click(this.setAuthorised.bind(this, tag, nameEl));
            this.tagListEl.append(el);
            this.foundTags.push(tag);
            if(this.renderCompleteCallback == null) {
                this.renderCompleteCallback();
            }
        }.bind(this));
    },
    initControls : function () {
        if(this.init) {
            return;
        }
        this.init = true;

        this.tagListEl = $('#manual-found-tags');
        $('#manual-deliver').click(this.deliverFood.bind(this));
        this.foodTypeEl = $('#manual-food-type');
        this.amountEl = $('#manual-deliver-amount');

        var amountIndicator = $('#manual-amount-indicator');
        this.amountEl.on('input', function () {
            amountIndicator.text(this.amountEl.val() + " grams");
        }.bind(this)).trigger('input');

        var me = this;
        this.foodTypeEl.change(function () {
            var defaultAmount = ($(this).find('option:selected').attr('data-default'));
            me.amountEl.val(defaultAmount);
            me.amountEl.trigger('input');
        });

        this.weightDisplayEl = $('#weight-display');

        $('.tare-button').click(this.tareScale.bind(this));
        $('.run-button').click(this.run.bind(this));
        $('.stop-button').click(this.stop.bind(this));
    },
    run : function () {
        runConveyors(app.getCurrentFeederId(), true);
    },
    stop : function () {
        runConveyors(app.getCurrentFeederId(), false);
    },
    tareScale : function () {
        console.log('here');
        tareScale(app.getCurrentFeederId());
    },
    updateCard : function (tag, nameEl, callback) {
        tag.tagName = nameEl.val();
        saveTag(app.getCurrentFeederId(), tag, callback);
    },
    setAuthorised : function (tag, nameEl) {
        this.updateCard(tag, nameEl, function (response) {
            setAuthenticatedTag(app.getCurrentFeederId(), response.tag.id);
        });
    },
    deliverFood : function () {
        var amount = this.amountEl.val();
        var type = this.foodTypeEl.val();
        var id = app.getCurrentFeederId();
        deliverFood(amount, type, id);
    }
};