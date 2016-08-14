var pages = window.pages || {};
pages.settings = {
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.settings;
        renderCompleteCallback();
        $('#settings-page.page').css({
            display : 'block'
        });
        me.type1 = app.getFeederInfo().foodTypes[0];
        me.type2 = app.getFeederInfo().foodTypes[1];
        me.tagList = $('#settings-tag-list');
        listAllKnownTags(1,
            function(data){
                me.knownTags = data;
                for(var i=0; i<me.knownTags.length; i++){
                    me.tagList.children().append();//option with known tag's value
                }
            },
            function(err){
                console.log(err);
            }
        );

        me.initControls();
    },
    initControls : function () {
        var me = pages.settings;
        if(me.init) {
            return;
        }
        me.init = true;

        var name1 = $('#settings-food-one-name');
        var name2 = $('#settings-food-two-name');
        var default1 = $('#settings-food-one-default');
        var default2 = $('#settings-food-two-default');
        var indicator1 = $('#settings-food-one-indicator');
        var indicator2 = $('#settings-food-two-indicator');

        var button1 = $('#settings-food-one-update');
        var button2 = $('#settings-food-two-update');
        var tagButton = $('#settings-tag-forget');

        name1.val(me.type1.name);
        name2.val(me.type2.name);
        default1.val(me.type1.defaultGramAmount);
        default2.val(me.type2.defaultGramAmount);

        button1.click(function(){
            me.updateFood(name1.val(), default1.val(), 1)
        });

        button2.click(function(){
            me.updateFood(name2.val(), default2.val(), 2)
        });

        tagButton.click(function(){
            me.forgetTag($('#settings-tag-list option:selected'));
        });

        default1.on('input', function(){
            indicator1.text(default1.val() + 'grams')
        }).trigger('input');
        default2.on('input', function(){
            indicator2.text(default2.val() + 'grams')
        }).trigger('input');


    },
    updateFood : function (name, def, typeId) {
        updateFoodType(typeId, def, name, app.invalidateFeederInfo);
    },
    forgetTag : function(tag){
        console.log(tag);
        //var val = tag.id //not sure about tag fields
        // deleteTag(tag, function(){
        //     $('#settings-tag-list option[value=val]').remove();//remove forgotten tag from list
        // });
    }
};