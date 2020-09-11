// This file handles the request history of a certain user.

// Checks to see if there is an active session cookie and redirect to the proper webpage.
if(!Cookies.get('relevantData')){
  url = './login.html';
  $(location).attr('href', url);
} else{
  if(Cookies.getJSON('relevantData').isAdmin) {
    url = './admin.html';
    $(location).attr('href', url);
  }
}

// This template constructs a table row containing a pending request. This will be added to the user's dashboard.
function templatePending(oldRoom, oldDate, newDate, type, status, id, courseType, numberOfStudents, notes) {
  var html = '<tr class="tr-shadow request-entry">' +
  '<td>'+oldRoom+'</td>'+
  '<td>'+oldDate+'</td>'+
  '<td>'+newDate+'</td>'+
  '<td>'+type+'</td>'+
  '<td>'+status+'</td>'+
  '<td><button type="button" class="btn btn-secondary show-info"><i class="fa fa-angle-down"></i></button></td></tr>'+
  '<tr class="tr-shadow hidden-info" style="display: none">'+
  '<td colspan="6">'+
  '<div><ul>'+
  '<li>Requset ID: '+id+'</li>'+
  '<li>Course Type: '+courseType+'</li>'+
  '<li>Number of students: '+numberOfStudents+'</li>'+
  '<li>Other notes: '+notes+'</li>'+
  '</ul></div></td></tr>'
  return html;
}

// This template constructs a table row containing an accepted or declined request. This will be added to the history popup.
function templateHistory(oldRoom, oldDate, newDate, type, status, id, courseType, numberOfStudents, notes, newRoom, comments) {
  var html = '<tr class="tr-shadow request-entry">' +
  '<td class="h-old-room">'+oldRoom+'</td>'+
  '<td class="h-new-room">'+newRoom+'</td>'+
  '<td class="h-status">'+status+'</td>'+
  '<td><button type="button" class="btn btn-secondary show-info"><i class="fa fa-angle-down"></i></button></td></tr>'+
  '<tr class="tr-shadow hidden-info" style="display: none">'+
  '<td colspan="4">'+
  '<div><ul>'+
  '<li>Request ID: '+id+'</li>'+
  '<li>Type: '+type+'</li>'+
  '<li>Old date: '+oldDate+'</li>'+
  '<li>New date '+newDate+'</li>'+
  '<li>Course type: '+courseType+'</li>'+
  '<li>Number of students: '+numberOfStudents+'</li>'+
  '<li>Other notes: '+notes+'</li>'+
  '<li>Comments: '+comments+'</li>'+
  '</ul></div></td></tr>'
  return html;
}

$(document).ready(function() {

  var cookie = Cookies.getJSON('relevantData');
  var userid = cookie.teacherID;

  //Send a GET request to the server that retrieves all this user's requests.
  $.ajax({
    url: '/horus/requests/user',
    type: 'GET',
    dataType: 'json',
    headers: {
      'Accept' : 'application/json',
      'Content-Type' : 'application/json',
      'user' : userid
    },
  })
  .done(function(data) {
    for(i = 0; i < data.length; i++){
      var courseType = data[i].courseType;
      var faculty = data[i].faculty;
      var id = data[i].id;
      var newDate = data[i].newDate;
      var notes = data[i].notes;
      var numberOfStudents = data[i].numberOfStudents;
      var oldDate = data[i].oldDate;
      var oldRoom = data[i].oldRoom.roomNumber;
      var status = data[i].status;
      var teacherID = data[i].teacherID;
      var teacherName = data[i].teacherName;
      var type = data[i].type;
      var newRoom;
      var comments = data[i].comments;
      if(data[i].newRoom == null){
        newRoom = 'Not specified';
      } else{
        newRoom = data[i].newRoom;
      }

      var requestTableBody = $('#request-table').find('tbody');
      var historyTableBody = $('#history-table').find('tbody');


      // Check the status of this request and show it to the coresponding place (dashboard or history).
      if(status == 'pending') {
        var html = templatePending(oldRoom, oldDate, newDate, type, status, id, courseType, numberOfStudents, notes);
        requestTableBody.append(html);
      } else {
        var html = templateHistory(oldRoom, oldDate, newDate, type, status, id, courseType, numberOfStudents, notes, newRoom, comments);
        historyTableBody.append(html);
      }


    }

    // Trigger that expands a request's information on click.
    $('.show-info').off().on('click', function(event){
      event.stopPropagation();
      var closest_tr = $(this).closest('tr');
      var hiddent_tr = $(closest_tr).next('.hidden-info');
      hiddent_tr.slideToggle('fast');
    });

    var receivedJson = data;
  })
  .fail(function(data) {
    console.log(data.status + ' ' + data.errorMessage);
  })
  .always(function() {
  });

  // Reset the history popup to the default appearance every time it is not focused.
  $('#historyModal').on('hidden.bs.modal', function(){
    $(this).find('.request-entry').each(function(index){
      $(this).css('border', '0px');
    });
    $(this).find('.hidden-info').each(function(index){
      $(this).hide();
    });
    $(this).find('.show-info').each(function(index){
    //  $(this).append('<i class="fa fa-angle-down"></i>');
    });
  });



});
