// Prevent users from accessing any unauthorized webpages.
if(!Cookies.get('relevantData')){
  url = './login.html';
  $(location).attr('href', url);
} else {
  if(!Cookies.getJSON('relevantData').isAdmin){
    url = './userView.html';
    $(location).attr('href', url);
  }
}

function gaze(requestID) {
  $.ajax({
    url: '/horus/requests/gazeIntoTheAbyss',
    type: 'GET',
    dataType: 'json',
    headers : {
      'Accept' : 'application/json',
      'Content-Type' : 'application/json',
      'requestID' : requestID
    }
  })
  .done(function(data) {
    console.log(data);
    $('#gaze-rooms').remove();
    $('#gaze-div').append('<select id="gaze-rooms" size="1"></select>')
    $('#gaze-rooms').append('<option value="99"></option>');
    for(i = 0; i < data.length; i++) {
      $('#gaze-rooms').append('<option>'+data[i]+'</option>');
    }
  })
  .fail(function(error) {
    console.log("error: " + error.status + " " + error.errorMessage);
  })
  .always(function() {
  });

}

// This template constructs a table row for a new request which will be latter shown in the dashboard.
function templateNew(teacherName, type, courseType, oldDate, newDate, id, teacherID, oldRoom, numberOfStudents, status, notes) {
  var html = '<tr class="tr-shadow" request-entry>' +
  '<td>'+teacherName+'</td>'+
  '<td>'+type+'</td>'+
  '<td>'+courseType+'</td>'+
  '<td>'+oldDate+'</td>'+
  '<td>'+newDate+'</td>'+
  '<td><button type="button" class="btn btn-secondary show-info"><i class="fa fa-angle-down"></i></button></td></tr>' +
  '<tr class="tr-shadow hidden-info" style="display:none">'+
  '<td colspan="6">'+
  '<div><ul>'+
  '<li>Request ID: '+id+'</li>'+
  '<li>Teacher ID: '+teacherID+'</li>'+
  '<li>Old Room: '+oldRoom+'</li>'+
  '<li>Number of students: '+numberOfStudents+'</li>'+
  '<li>Status: '+status+'</li>'+
  '<li>Other notes: '+notes+'</li></ul><br>'+
  '<div class = "text-center"><button type="button" class="btn btn-success btn-lg show-info accept-button" style="margin-right: 50px">Accept</button>'+
  '<button type="button" class="btn btn-danger btn-lg show-info decline-button">Decline</button></div>'+
  '</div></td></tr>';
  return html;
}

// This template contructs a table row for the history of requests table.
function templateHistory(teacherName, type, courseType, oldDate, newDate, id, teacherID, oldRoom, numberOfStudents, status, notes, newRoom, comments){
  var html = '<tr class="tr-shadow" request-entry>' +
  '<td>'+teacherName+'</td>'+
  '<td>'+type+'</td>'+
  '<td>'+courseType+'</td>'+
  '<td>'+oldDate+'</td>'+
  '<td>'+newDate+'</td>'+
  '<td><button type="button" class="btn btn-secondary show-info"><i class="fa fa-angle-down"></i></button></td></tr>' +
  '<tr class="tr-shadow hidden-info" style="display:none">'+
  '<td colspan="6">'+
  '<div><ul>'+
  '<li>Request ID: '+id+'</li>'+
  '<li>Teacher ID: '+teacherID+'</li>'+
  '<li>Old Room: '+oldRoom+'</li>'+
  '<li>New Room: '+newRoom+'</li>'+
  '<li>Number of students: '+numberOfStudents+'</li>'+
  '<li class="thisStatus">Status: '+status+'</li>'+
  '<li>Other notes: '+notes+'</li>'+
  '<li>Comments: '+comments+'</li></ul><br>'+
  '</div></td></tr>';
  return html;
}

$(document).ready(function() {

  // Retrieve all the requests.
  $.ajax({
    url: '/horus/requests',
    type: 'GET',
    dataType: 'json',
  })
  .done(function(data) {
  //  console.log(data);
    for(i = 0; i < data.length; i++) {
      var teacherName = data[i].teacherName;
      var type = data[i].type;
      var courseType = data[i].courseType;
      var oldDate = data[i].oldDate;
      var newDate = data[i].newDate;
      var id = data[i].id;
      var teacherID = data[i].teacherID;
      var oldRoom = data[i].oldRoom.roomNumber;
      var numberOfStudents = data[i].numberOfStudents;
      var notes = data[i].notes;
      var status = data[i].status;
      var newRoom = data[i].newRoom;
      var comments = data[i].comments;

      var requestTableBody = $('#request-table').find('tbody');
      var historyTableBody = $('#history-table').find('tbody');

      // Check if a request should be shown on the dashboard or on the history popup.
      if(status == 'pending'){
        var html = templateNew(teacherName, type, courseType, oldDate, newDate, id, teacherID, oldRoom, numberOfStudents, status, notes);
        requestTableBody.append(html);
      } else {
        var html = templateHistory(teacherName, type, courseType, oldDate, newDate, id, teacherID, oldRoom, numberOfStudents, status, notes, newRoom, comments);
        historyTableBody.append(html);
      }
}


      var thisID;
      var userID = Cookies.getJSON('relevantData').teacherID;

      // Button trigger which will expand the details of a request.
      $('.show-info').off().on('click', function(event){
        event.stopPropagation();
        var closest_tr = $(this).closest('tr');
        var hidden_tr = $(closest_tr).next('.hidden-info');
        hidden_tr.slideToggle('fast');
      });

      // Button trigger which will open a popup for accepting a request.
      $('.accept-button').on('click', function(event){
        thisID = $(this).closest('div').siblings('ul').find('li').first().text().substr(12);
        var thisType;
        for(i = 0; i < data.length; i++){
          if(data[i].id == thisID){
            thisType = data[i].type;
            break;
          }
        }

        // Display different modals if the request requires a new room or not.
        if(thisType == 'reschedule'){
          $('#accept-modal').modal('toggle');
          gaze(thisID);
        } else if(thisType == 'cancel'){
          $('#accept2-modal').modal('toggle');
        }
      });

      // Button trigger for declining a request.
      $('.decline-button').on('click', function(event){
        $('#cancel-modal').modal('toggle');
        thisID = $(this).closest('div').siblings('ul').find('li').first().text().substr(12, 2);
      });

      // Button trigger for sending a PUT request to the server, informing it that the request that requires a new room was accepted.
      $('.accept-request').off().on('click', function(event){
        event.stopPropagation();
        var newRoom = $(this).closest('.modal-content').find('.new-room option:selected').val();
        var gazeRoom = $('#gaze-rooms option:selected').val();
        if(gazeRoom != 99) {
          newRoom = gazeRoom;
        }
        var otherDetails = $(this).closest('.modal-content').find('.other-details').val();
        var userID = Cookies.getJSON('relevantData').teacherID;
        var teacherID;
        for(i = 0; i < data.length; i++) {
          if(data[i].id == thisID){
            teacherID = data[i].teacherID;
          }
        }

      //  console.log(newRoom);
        var changeStatus = JSON.stringify({
          'status' : 'accepted',
          'id' : thisID,
          'comments' : otherDetails,
          'newRoom' : newRoom,
          'userID' : userID,
          'teacherID' : teacherID
        });
            $.ajax({
              url: '/horus/requests/statusChange',
              type: 'PUT',
              dataType: 'json',
              data: changeStatus,
              headers: {
                'Accept': 'application/json',
                'Content-Type' : 'application/json'
              },
              complete: function(result){
                if(result.status == 200) {
                //  console.log("Status changed!");
                  location.reload();
                } else {
                  console.log(result.status + " " + result.errorMessage);
                }
              }
            });

      });

      // Button trigger for sending a PUT request to the server, informing it that the request that does not require a new room was accepted.
      $('.accept2-request').off().on('click', function(event){
        event.stopPropagation();
        var otherDetails = $(this).closest('.modal-content').find('.other-details').val();
        var userID = Cookies.getJSON('relevantData').teacherID;
        var teacherID;
        for(i = 0; i < data.length; i++) {
          if(data[i].id == thisID){
            teacherID = data[i].teacherID;
          }
        }
      //  console.log(teacherID);
        var changeStatus = JSON.stringify({
          'status' : 'accepted',
          'id' : thisID,
          'comments' : otherDetails,
          'newRoom' : 'Not specified!',
          'teacherID' : teacherID,
          'userID' : userID
        });
            $.ajax({
              url: '/horus/requests/statusChange',
              type: 'PUT',
              dataType: 'json',
              data: changeStatus,
              headers: {
                'Accept': 'application/json',
                'Content-Type' : 'application/json'
              },
              complete: function(result){
                if(result.status == 200) {
                  //console.log("Status changed!");
                  location.reload();
                } else {
                  console.log(result.status + " " + result.errorMessage);
                }
              }
            });

      });

      // Button trigger for sending a PUT request to the server, informing it that the request was not accepted.

      $('.cancel-request').off().on('click', function(event){
        event.stopPropagation();
        var otherDetails = $(this).closest('.modal-content').find('.other-details').val();
        var teacherID;
        for(i = 0; i < data.length; i++) {
          if(data[i].id == thisID){
            teacherID = data[i].teacherID;
          }
        }
        var changeStatus = JSON.stringify({
          'status' : 'cancelled',
          'id' : thisID,
          'comments' : otherDetails,
          'newRoom' : 'Not specified!',
          'userID' : userID,
          'teacherID' : teacherID
        });

            $.ajax({
              url: '/horus/requests/statusChange',
              type: 'PUT',
              dataType: 'json',
              data: changeStatus,
              headers: {
                'Accept' : 'application/json',
                'Content-Type' : 'application/json'
              },
              complete: function(result){
                if(result.status == 200) {
                //  console.log("accepted ok!");
                  location.reload();
                } else {
                  console.log(result.status + " " + result.errorMessage);
                }
              }
            });
      });

  })
  .fail(function(result) {
    console.log("error did not get requests");
    console.log(result.status + " " + result.errorMessage);
  })
  .always(function() {
    //console.log("complete");
  });
});
