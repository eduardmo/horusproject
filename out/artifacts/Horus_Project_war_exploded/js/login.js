// This file handles the login.

$(document).ready(function() {

  // Check if there is a session cookie active and redirect to the proper webpage.
  if(Cookies.get('relevantData')){
    var isAdmin = Cookies.getJSON('relevantData').isAdmin;
    if(isAdmin){
      url='./admin.html';
      $(location).attr('href', url);
    } else {
      url = './userView.html';
      $(location).attr('href', url);
    }
  }

  // Trigger on form submission.
  $('.login-form').on('submit', function(event) {
    event.preventDefault();

    if(!Date.now){
      Date.now = function(){return new Date.getTime();}
    }

    var timestamp = Date.now();

    // Sends a GET request to the sever which authenticates the user.
    $.ajax({
      url: '/horus/requests/login', // de completat
      type: 'GET',
      dataType: 'json',
      headers : {
        'username' : $('#email').val(),
        'password' : $('#password').val(),
        'timestamp' : timestamp,
        'Accept' : 'application/json',
        'Content-Type' : 'application/json'
      },

      complete: function(result) {
        if(result.status == 200) {
          var responseJSON = result.responseJSON;
          var name = responseJSON.name;
          var teacherID = responseJSON.teacherID;
          var email = responseJSON.email;
          var isAdmin = responseJSON.isAdmin;
          var sessionID = responseJSON.sessionID;

          // Make a session cookie live longer if the button is checked.
          if($('#remember-me').is(':checked')){
            Cookies.set('relevantData', {name: name, teacherID: teacherID, email: email, sessionID: sessionID, isAdmin: isAdmin}, {expires: 10});
          } else {
            Cookies.set('relevantData', {name: name, teacherID: teacherID, email: email, sessionID: sessionID, isAdmin: isAdmin}, {expires: 1});
          }

          // Check if the user is an admin and redirects to the proper webpage.
          if(isAdmin){
            url='./admin.html';
            $(location).attr('href', url);
          } else {
            url = './userView.html';
            $(location).attr('href', url);
          }
        } else if(result.status == 500) {
          alert('Username or password incorrect!');
        }else {
          //alert('Failed!' + result.status + result.errorMessage);
          location.reload();
        }
      }

    });

  });

  // Redirects to the sign up webpage.
  $('#sign-up').click(function() {
    url = './register.html';
    $(location).attr('href', url);
  })
});
