

var io = require('socket.io').listen(3000);
var users = [];

// when the user makaes a connection
io.sockets.on('connection', function (socket) {

  // When a new user connections
  socket.on('updateUsers', function(packet) {

    console.log("Updating the user list on the server.");

    // Set an intitial found variable
    var userFound = false;

    // See if the user exists already
    for (var userCount=0; userCount<users.length; userCount++) {
      if (packet.userid==users[userCount].userid) {
        userFound = true;
      }
    }

    // If the user has not been found, add them
    if (!userFound) {
      console.log("Adding new user "+packet.userid);
      users.push({
        "userid": packet.userid,
        "username": packet.username,
        "userbadge": packet.userbadge,
        "usercount": packet.usercount,
        "pingid": packet.pingid,
        "time": packet.time
      });
    }

    // If they have been found, then we update their details
    if (userFound) {
      console.log("Updating existing user "+packet.userid);
      for (var userCount=0; userCount<users.length; userCount++) {
        if (packet.userid==users[userCount].userid) {
          users[userCount].userid = packet.userid;
          users[userCount].username = packet.username;
          users[userCount].userbadge = packet.userbadge;
          users[userCount].usercount = packet.usercount,
          users[userCount].time = packet.time;
          break;
        }
      }
    }

    // Create array with just this ping's users
    var returnUserArray = [] ;
    for (var userCount=0; userCount<users.length; userCount++) {
      if (packet.pingid==users[userCount].pingid) {
        returnUserArray.push(users[userCount]);
      }
    }

    console.log("Send the server list back to the client.");

    // Emit the user list back to the user
    io.sockets.emit('updateUserList', returnUserArray) ;

    // After we emit this - we CULL the list
    var currentUserCounter = 0;
    for (var userCount=0; userCount<users.length; userCount++) {
      if (packet.pingid==users[userCount].pingid) {
        currentUserCounter++;
      }
    }
    if (currentUserCounter==packet.usercount) {
      var runTheLoop = true;
      while (runTheLoop) {
        console.log("Start single DELETE iteration");
        runTheLoop = false;
        for (var userCount=0; userCount<users.length; userCount++) {
          if (packet.pingid==users[userCount].pingid) {
            console.log("Deleting user with id "+users[userCount].userid+".");
            runTheLoop = true;
            users.splice(userCount, 1);
            break;
          }
        }
        console.log("End single DELETE iteration. User array size is "+users.length);
      }
    }



  });

});
