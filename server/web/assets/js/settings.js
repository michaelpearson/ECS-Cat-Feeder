var pages = window.pages || {};
pages.settings = {
    renderPage : function (pageArguments, renderCompleteCallback) {
        var me = pages.settings;
        renderCompleteCallback();
        $('#settings-page.page').css({
            display : 'block'
        });
        me.foodList = app.getFeederInfo().foodTypes;
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
        
        console.log(me.foodList);
        for(var i=0; i<me.foodList.length; i++){
            $('#food-container').append('<div class="col-lg-8 col-md-12">\n    <div class="form-horizontal">\n        <hr/>\n        <div class="form-group">\n            <label for="settings-food-name" class="col-sm-2 control-label">Name</label>\n            <div class="col-sm-10">\n                <input id="settings-food-name" class="form-control" type=text value="'+me.foodList[i].name+'"/>\n            </div>\n        </div>\n        <div class="form-group">\n            <label for="settings-food-default'+me.foodList[i].id+'" class="col-sm-2 control-label">Default Delivery Amount</label>\n            <div class="col-sm-10">\n                <input type="range" value="'+me.foodList[i].defaultGramAmount+'" min="1" max="500" style="margin-top: 7px;" id="settings-food-default'+me.foodList[i].id+'">\n                <p id="settings-food-indicator'+me.foodList[i].id+'"></p>\n            </div>\n        </div>\n        <div class="form-group">\n            <div class="col-sm-offset-2 col-sm-10">\n                <button type="submit" onclick="pages.settings.updateFood(this.parentElement.parentElement.parentElement);" class="btn btn-default" id="settings-food-update">Update</button>\n            </div>\n            <input value="'+me.foodList[i].id+'" style="display: none;"/>\n        </div>\n    </div>\n</div>');
            var id = me.foodList[i].id;
            $('#settings-food-default'+id).on('input', me.generateHandler(me.foodList, i)).trigger('input');
        }
    },
    updateFood : function (element) {
        var name = $($(element).find('input')[0]).val();
        var def = $($(element).find('input')[1]).val();
        var typeId = $($(element).find('input')[2]).val();
        updateFoodType(typeId, def, name, app.invalidateFeederInfo);
    },
    forgetTag : function(tag){
        console.log(tag);
        //var val = tag.id //not sure about tag fields
        // deleteTag(tag, function(){
        //     $('#settings-tag-list option[value=val]').remove();//remove forgotten tag from list
        // });
    },
    generateHandler: function(list, j){
        return function(){
            $('#settings-food-indicator'+list[j].id).text($('#settings-food-default'+list[j].id).val() + 'grams')
        }
    }
};