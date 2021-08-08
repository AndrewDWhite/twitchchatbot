var urlPrefix = window.location.origin;
var lastUUid=0;


function update() {
    console.log("update");
$.ajax({
        dataType: 'json',
        url: urlPrefix + '/chat',
        success: function(data) {
            if(data) {
                console.log("data");
                console.log(data);
                if (data.messages) {
                    var myLength = data.messages.length;
                    if (myLength>0){
                        if(data.messages[myLength-1].requestUUid !== 0 && data.messages[myLength-1].requestUUid !== lastUUid) {
                            console.log("chat update");
                            //TODO implement
                            var lastEndYet=false;
                            if (lastUUid ==0) {
                                lastEndYet=true;
                            }
                            $.each(data.messages, function(i, val) {
                                if (lastEndYet){
                                    $("#chat").append($("<p class='chatBox'></p>").text(val.requestor+": "+ val.message));
                                }
                                if (val.requestUUid == lastUUid) {
                                    lastEndYet = true;
                                }
                            });
                            lastUUid=data.messages[myLength-1].requestUUid;
                        }
                    }
                } else {
                    lastUUid =0;
                }
            }
        }


    })

}


//Done with definitions so run this
setInterval(update, 1000);