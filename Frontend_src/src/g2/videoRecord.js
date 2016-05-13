/**
 * Props params:
 *
 * playCallback:    Function to be called with response from server. should take one parameter.
 * calc:            Only used when testing hardware.
 * postURL:         The url after the globally set url.
 * formDataBuilder  Function to create formdata containing information to send. Function takes:.
 *                                                                                     video(blob) as first param
 *                                                                                     filename as second param.
 * recButtonID:     [Optional] Button to start recording video. If left out it autorecords on render.
 * stopButtonID:    Id of the button to stop recording video. Might post video as well(read about postButtonID).
 * postButtonID:    [Optional] Id of the post video button. If left out the stop button posts the video to server.
 * replay:          replay="true" if you want the recording to be playable in the same video tag.
 * fileName:        The name the video recording should have.
 *
 *
 * watch HardwareTest.js for example of use.
 */



var Recorder = React.createClass({
  componentDidMount: function() {


      var props = this.props;

      //used for hw testing
      var blobsize;
      var sendTime;


      var autoRec = (typeof props.recButtonID === "undefined");

      var replay = props.replay == "true";
      function PostBlob(blob, siteView) {
          // FormData
          var formData = props.formDataBuilder(blob,props.fileName);

          //used for hw testing
          if(typeof props.calc !== "undefined") {
              blobsize = blob.size / 1048576;
              sendTime = Date.now();
          }

          //call xhr with full url, data and callback function
          if(siteView == "createAssignment" || siteView == "submission") {
              var xhReq = new XMLHttpRequest();

              xhReq.onreadystatechange = function () {
                  if (xhReq.readyState === 4 && xhReq.status == 200) {
                      var dataPOST = new FormData();

                      console.log(xhReq.responseText);


                      var xhrPOST = new XMLHttpRequest();

                      if ("withCredentials" in xhReq) { // Chrome, Firefox, Opera

                          xhr(window.globalURL + props.postURL + xhReq.responseText, formData, props.playCallback);


                      } else if (typeof XDomainRequest !== "undefined") { // IE
                          xhr(window.globalURL + props.postURL + xhReq.responseText, formData, props.playCallback);
                      }
                  }
              };

              var userID = "26";
              var courseID = "60";
              var assignmentID = "1000";

              var url = window.globalURL+"/video/inrequest?userID=" + userID + "&courseID=" + courseID +
                      "&assignmentID=" + assignmentID;
              var method = "GET";

              xhReq.open(method, url, true);
              xhReq.send();


          } else {
              xhr(window.globalURL + props.postURL, formData, props.playCallback);
         }
      }

      if(!autoRec) {
          var record = document.getElementById(props.recButtonID);
      }

      var stop = document.getElementById(props.stopButtonID);
      var preview;
      if(typeof props.calc !== "undefined")
          preview = document.getElementById('prev-test');
      else
          preview = document.getElementById('preview');
      //check webbrowse.
      //var isFirefox = !!navigator.mediaDevices.getUserMedia;

      navigator.getUserMedia = ( navigator.getUserMedia ||
                                  navigator.webkitGetUserMedia ||
      navigator.mediaDevices.getUserMedia ||
                                  navigator.msGetUserMedia);


      var recordAudio, recordVideo;
      var localStream;
      var startRecord = function () {
          if(!autoRec){
              record.disabled = true;
          }
          //Start recording with forced low settings for smaller files.
          navigator.getUserMedia({
              audio: true,
              video: {
                  mandatory: {
                      minWidth: 160,
                      maxWidth: 320,
                      minHeight: 120,
                      maxHeight: 240,
                      minFrameRate: 5,
                      maxFrameRate: 10
                  }
              }
          }, function (stream) {
              //start stream to record
              preview.src = window.URL.createObjectURL(stream);
              preview.play();
                localStream = stream;

              recordAudio = RecordRTC(stream, {

                  onAudioProcessStarted: function () {
                      //if (!isFirefox) {
                          recordVideo.startRecording();
                      //}
                  }
              });

             // if (isFirefox) {
               //   recordAudio.startRecording();
              //}

              //if (!isFirefox) {
                  recordVideo = RecordRTC(stream, {
                      type: 'video'
                  });
                  recordAudio.startRecording();
              //}

              stop.disabled = false;
              if(typeof props.calc !== "undefined")
                  document.getElementById('rec-text-test').innerHTML = "Recording";
              else
                  document.getElementById('rec-text').innerHTML = "Recording";
          }, function (error) {
              alert("Problem occured, make sure camera is not\nused elsewhere and that you are connected\nby https.");
          });
      };

      if(!autoRec){
          record.onclick = startRecord;
      }
      stop.onclick = function () {
          if(!autoRec){
              record.disabled = false;
          }
          
          stop.disabled = true;

          preview.src = '';
          
          var postbutton = null;
          if (typeof props.postButtonID !== "undefined"){
              postbutton = document.getElementById(props.postButtonID);
          }


        //  if (!isFirefox) {

              recordVideo.stopRecording(function (url) {
                  if(replay){
                      preview.src = url;
                      preview.setAttribute("controls","controls");
                      preview.removeAttribute("muted");
                  }

                  if(postbutton == null) {
                      if(props.siteView !== null) {
                          PostBlob(recordVideo.getBlob(), props.siteView);
                      } else {
                          PostBlob(recordVideo.getBlob());
                      }


                  }
                  else {
                      preview.src = url;
                      preview.setAttribute("controls","controls");
                      preview.removeAttribute("muted");
                      postbutton.disabled = false;
                      postbutton.onclick = function () {
                          
                          if(props.siteView !== null) {
                              PostBlob(recordVideo.getBlob(), props.siteView);
                          } else {
                              PostBlob(recordVideo.getBlob());
                          }
                      }
                  }
                  localStream.stop();
                  localStream = null;
                  if(typeof props.calc !== "undefined")
                      document.getElementById('rec-text-test').innerHTML = "";
                  else
                      document.getElementById('rec-text').innerHTML = "";

              });
          /*}else {

              recordAudio.stopRecording(function (url) {
                  if(replay){
                      preview.src = url;
                      preview.setAttribute("controls","controls");
                  }
                  if(postbutton == null) {

                      PostBlob(recordAudio.getBlob());
                  }
                  else {
                      preview.src = url;
                      preview.setAttribute("controls","controls");
                      postbutton.disabled = false;
                      postbutton.onclick = function () {
                         
                          PostBlob(recordAudio.getBlob());


                      }
                  }
              });
          }*/

      };



      function xhr(url, data, callback) {
          var request = new XMLHttpRequest();
          request.onreadystatechange = function () {
              if (request.readyState == 4 && request.status == 200) {
                  callback(request.responseText);
              } else if(request.readyState == 4 && request.status !== 200) {
                  alert(request.responseText);
              }
          };

          if(typeof props.calc !== "undefined") {
            request.upload.onloadstart = function () {
                $("#internet-speed").text("Uploading...");
            }
              request.onloadstart = function () {
                  $("#internet-speed").text("Uploading...");
              }
          }
          request.onload = function(){
              if(typeof props.calc !== "undefined"){
                  if(request.status == 404)
                      $("#internet-speed").text("Upload failed, no server connection.");
                  else if(request.status == 408)
                      $("#internet-speed").text("Connection timed out.");
                  else
                    props.calc(blobsize,sendTime);
              }
              else if(request.status == 404) {

                      alert("Upload failed, no server connection.");
              }
              else if(request.status == 408) {

                  alert("Connection timed out.");
              }


          }

          request.open('POST', url,true);

          request.send(data);

      }



      if(autoRec){
          startRecord();
      }
  },
  render: function() {
      var id;
      var pId;
    if(typeof this.props.calc !== "undefined"){
        id="prev-test";
        pId="rec-text-test";
    }

    else{
        id="preview"
        pId="rec-text";
    }
    return (
        <div>
            <div id="prev-container">
                <video id={id} muted height="100%" width="100%" ></video>
            </div>
            <p id={pId}></p>


        </div>
    );
  }
});

window.Recorder = Recorder;