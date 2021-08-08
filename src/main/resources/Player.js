
var urlPrefix = window.location.origin;
var paused = true;
var currentlyPlayingRequestId = 0;
var waitingOnNext = false;
var currentVolume = 30;
var usingAuth = false;
var currentUser = null;
var userToken = null;
var player;
var activePlayer = "";

function setupYoutubeAPI() {
    var tag = document.createElement('script');
    tag.src = "https://www.youtube.com/player_api";
    var firstScriptTag = document.getElementsByTagName('script')[0];
    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
}

//youtube api requires a function with exactly this name. it calls this once after page load
function onYouTubeIframeAPIReady() {
    console.log("youtube api ready");
    // see https://developers.google.com/youtube/player_parameters?playerVersion=HTML5
    player = new YT.Player('youtubePlayer', {
        width: '600',
        height: '400',
        events: {
            'onError': onYoutubeError,
            'onReady': onPlayerReady,
            'onStateChange': onPlayerStateChange
        },
        playerVars: {
            fs: 0,
            iv_load_policy: 3,
            rel: 0
        }
    });
}

function onPlayerReady(event){
    console.log("player ready");
    if (justStarted) {
        playpause();
    }
}



function onPlayerStateChange(event){
    console.log("Change");
    console.log(JSON.stringify(event));
        if (event.data == YT.PlayerState.ENDED && !paused && !waitingOnNext) {
            console.log("onNextChange");
          nextSong(true);
        }
        lastState = event.data;
}

function onYoutubeError(event) {
    if(event.data == 100 || event.data == 101 || event.data == 150) {
        //not found, or not allowed to be embedded. hopefully the server checked for this, but if not, we skip it now.
        console.log("youtube error:");
        console.log(event);
        nextSong(true);
    }
}


function loadSong(songId, site, requestId, endSeconds) {
    console.log("load: "+ songId+" : " +site+" : " +requestId+" : " +endSeconds);
    //TODO remove hard coding
    site = 'yt';
    if (site == 'yt') {
        $("#youtubeHolder").css("display", "table-cell");

        player.loadVideoById({
            videoId: songId,
            startSeconds: 0,//TODO do we want to allow starting partially in
            suggestedQuality: 'large'
        });

	}  else {
	    console.log("error: loadSong passed an unsupported site id: '" + site +"'");
	    return;
	}

    activePlayer = site;
	currentlyPlayingRequestId = requestId;
	paused = false;
	$("#likeArea").hide();
}

function playpause() {
    console.log("play/pause");
	if (activePlayer === "yt") {
		if(paused) {
			player.playVideo();
		} else {
			player.pauseVideo();
		}
	}

	paused = !paused;

	if(paused) {
	    $("#playPauseButton").text('Play').button("refresh");
	} else {
	    $("#playPauseButton").text('Pause').button("refresh");
	}
}



function loadCurrentSong() {
    console.log("load");
    waitingOnNext = true;
    $.ajax({
        dataType: 'json',
        url: urlPrefix + '/current',
        success: function(data) {
            if(data && data.id && data.requestUUid) {
            try{
                console.log("load current song update data: ");
                console.log(JSON.stringify(data));
                loadSong(data.id, data.site, data.requestUUid, data.duration);
                document.getElementById('title').innerHTML=data.title;
                document.getElementById('requester').innerHTML=data.requester;
                justStarted = false;
                waitingOnNext = false;
                } catch (error) {
                console.log(error);
                }
            } else{
                console.log("invalid");
            }
        }, error: function(data) {
        waitingOnNext = false;
        }
    })
}

function nextSong(skip) {
    console.log("skip to next song");
    if(waitingOnNext)
        return;

    waitingOnNext = true;

    player.seekTo(9999999);


    if(skip) {
        var maybeSkipped = "skip=true&";
    } else {
        var maybeSkipped = "";
    }

    $.ajax({
        dataType: 'json',
        url: urlPrefix + '/next',
        success: function(data) {
            doingNext = false;
            waitingOnNext = false;
            var itWorked = false;
            console.log(JSON.stringify(data));
            if( data && !data.exception) {
                console.log("good here to go")
                var Songs = data.songEntries;
                console.log(JSON.stringify(Songs));
                var newSong = Songs[0];
                console.log(JSON.stringify(newSong));
                if(newSong && newSong.requestUUid !== currentlyPlayingRequestId && newSong.id) {
                    console.log("all set")
                    loadSong(newSong.id, newSong.site, newSong.requestedUUid, newSong.duration);
                    document.getElementById('title').innerHTML=newSong.title;
                    document.getElementById('requester').innerHTML=newSong.requester;
                    itWorked = true;
                }

            }
        }
    })
}

function reset(){
    console.log("reset");
    loadCurrentSong();
}


function update() {
    console.log("update");
    if(waitingOnNext) {
        console.log("waiting on next");
        return;
    }

    if(justStarted) {
        console.log("waiting on just started");
        loadCurrentSong();
        return;
    }
$.ajax({
        dataType: 'json',
        url: urlPrefix + '/current',
        success: function(data) {
            if(data) {
                console.log("data");

                if(data.requestUUid !== 0 && data.requestUUid !== currentlyPlayingRequestId) {
                    console.log("pause update");
                    player.pauseVideo();
                    waitingOnNext = true;
                    loadCurrentSong();
                }
            }
        }
    })

}

//Done function definitions so do stuff

setupYoutubeAPI();
justStarted = true;
setInterval(update, 1000);

