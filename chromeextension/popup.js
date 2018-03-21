document.addEventListener('DOMContentLoaded', function() {

  var submittedToken = "0";

  // First check if there is already a store
  chrome.storage.local.get('tok', function(data) {
    if (data.tok!==undefined) {
      if (data.tok!=="") {
        submittedToken = data.tok;
        getPingCount();
     }
    }
  });

  // Get the DOM elements
  var form = document.getElementById("form");
  var token = document.getElementById("token");
  var submit = document.getElementById("submit");
  var loading = document.getElementById("loading");
  var nextping = document.getElementById("nextping");
  var login = document.getElementById("login");

  // Global variables
  var globalId = "";
  var globalToken = "";
  var globalCount = "";

  // Hide the loading bar
  loading.style.display = "none";

  /**
  * When the user clicks on SUBMIT
  */
  form.onsubmit = function(e) {

    // Prevent the default behaviour
    e.preventDefault();

    // Get the user token
    submittedToken = token.value ;
    if (submittedToken=="") submittedToken = "0";

    // Get the ping count
    getPingCount();

  }

  /**
  * Get the Ping count
  */
  function getPingCount() {

    // Show and hide necessary things
    token.style.display = "none";
    submit.style.display = "none";
    nextping.style.display = "none";
    loading.style.display = "inline";

    // Make the AJAX request
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "http://192.168.0.100:8888/ping/"+submittedToken+"/getcount", true);
    xhr.onreadystatechange = function() {
      if (xhr.readyState == 4) {

        // Hide the relevant DOM
        loading.style.display = "none";

        // Get the response
        var jsonResponse = JSON.parse(xhr.responseText)[0];

        // Process it
        if (!jsonResponse.success) {

          // If it's invalid
          nextping.style.display = "block";
          nextping.innerText = "Sorry, invalid token.";
          token.style.display = "inline";
          submit.style.display = "inline";

        } else {

          // Hide the DOM
          login.style.display = "none";

          // Get the response data
          globalId = jsonResponse.user_id;
          globalToken = submittedToken;
          globalCount = jsonResponse.count;

          // Badge
          chrome.browserAction.setBadgeText({text: ""+globalCount});

          // Set the token in local storage
          chrome.storage.local.set({'tok': globalToken}, function() {
            console.log("Data saved.");
          });

          // Make a call to get the next Ping
          getNextPing();

        }

      }
    }
    xhr.send();

  }

  /**
  * Function for retreiving the next ping detail
  */
  function getNextPing() {

    var date = new Date();
    var weekday = date.getDay();
    var hour = date.getHours();
    var minute = date.getMinutes();

    // Because JS -> PHP
    if (weekday==0) weekday = 7;

    // Make the AJAX request
    var xhrNextPing = new XMLHttpRequest();
    xhrNextPing.open("GET", "http://192.168.0.100:8888/ping/"+globalId+"/get_next_ping?day="+weekday+"&hour="+hour+"&minute="+minute, true);
    xhrNextPing.onreadystatechange = function() {
      if (xhrNextPing.readyState == 4) {

          // If it's empty
          if (xhrNextPing.responseText === "[]") {
            nextping.innerText = "No upcoming Pings!"
          } else {
            // Get the response
            var jsonResponse = JSON.parse(xhrNextPing.responseText)[0];

            // Get our ping variables
            var title = jsonResponse.title;
            var time = jsonResponse.time;
            var timeRework = time.split(" ")[1];
            // Set the html
            nextping.style.display = "block";
            nextping.innerHTML = "<div class='padding'><img src='p.png' /><h1>"+title+"</h1><h2> Next Ping going off at: "+timeRework+"</h2></div><a href='#' id='logout'>Logout?</a>";

          }

      }
    }
    xhrNextPing.send();

  }


  /**
  * Logout
  */
  window.addEventListener('click', function(e){

    // If it's a logout
    if (e.target.id==="logout") {

      // Reset
      submittedToken = "0";

      // Badge
      chrome.browserAction.setBadgeText({text: ""});

      // Set the token in local storage
      chrome.storage.local.set({'tok': ''}, function() {
        console.log("Data cleared.");
      });

      // Show and hide necessary things
      nextping.style.display = "none";
      login.style.display = "block";
      token.style.display = "inline";
      submit.style.display = "inline";
      loading.style.display = "none";

    }

  });

});
