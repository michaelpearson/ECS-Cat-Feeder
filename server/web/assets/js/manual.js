var pages = window.pages || {};
pages.manual = {
    init : false,
    foodTypeEl : null,
    amountEl : null,
    timerId : 0,
    tagListEl : null,
    tagItemTemplate : '<div class="form-group">\n    <label class="col-sm-2 control-label">Tag</label>\n    <div class="input-group">\n        <span class="input-group-addon tag-uid"></span>\n        <input type="text" class="form-control tag-name" aria-label="New tag name" title="New tag name">\n        <div class="input-group-btn">\n            <button class="btn btn-warning auth">Set as authorised</button>\n            <button class="btn btn-primary save">Save</button>\n        </div>\n    </div>\n</div>',
    foundTags : [],
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.manual;
        renderCompleteCallback();
        $('#manual-control-page.page').css({
            display : 'block'
        });
        var foodTypeEl = $('#manual-food-type');
        foodTypeEl.children().remove();
        $(app.getFeederInfo().foodTypes).each(function (i, element) {
            foodTypeEl.append('<option value="' + element.id + '">' + element.name + '</option>')
        });
        me.timerId = setInterval(me.detectTags, 4000);
        me.detectTags();
        me.initControls();
    },
    unload : function () {
        clearInterval(pages.manual.timerId);
    },
    detectTags : function () {
        var me = pages.manual;
        getLastCardId(app.getCurrentFeederId(), function (response) {
            var tag = response.tag || {};
            var isPresent = response.isPresent || false;
            if(!tag || !tag.tagUID || tag.tagUID == 0) {
                return;
            }
            if(me.foundTags.some(function (element) { return element.tagUID == tag.tagUID; })) {
                return;
            }
            var el = $(me.tagItemTemplate);
            var uidEl = el.find('.tag-uid');
            var nameEl = el.find('.tag-name');
            uidEl.text("#" + tag.tagUID);
            nameEl.val(tag.tagName || "New tag #" + tag.tagUID);

            el.find('button.save').click(me.updateCard.bind(this, tag, nameEl, null));
            el.find('button.auth').click(me.setAuthorised.bind(this, tag, nameEl));
            me.tagListEl.append(el);
            me.foundTags.push(tag);
        });
    },
    initControls : function () {
        var me = pages.manual;
        if(me.init) {
            return;
        }
        me.init = true;

        me.tagListEl = $('#manual-found-tags');
        $('#manual-deliver').click(me.deliverFood);
        me.foodTypeEl = $('#manual-food-type');
        me.amountEl = $('#manual-deliver-amount');

        var amountIndicator = $('#manual-amount-indicator');
        me.amountEl.on('input', function () {
            amountIndicator.text(me.amountEl.val() + " grams");
        }).trigger('input');
    },
    updateCard : function (tag, nameEl, callback) {
        tag.tagName = nameEl.val();
        saveTag(tag, callback);
    },
    setAuthorised : function (tag, nameEl) {
        var me = pages.manual;
        me.updateCard(tag, nameEl, function (response) {
            setAuthenticatedTag(app.getCurrentFeederId(), response.tag.id);
        });
    },
    deliverFood : function () {
        var me = pages.manual;
        var amount = me.amountEl.val();
        var type = me.foodTypeEl.val();
        var id = app.getCurrentFeederId();
        deliverFood(amount, type, id);
    }
};