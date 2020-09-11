// This files handles the register.

// Checks to see if both passwords are equal.
function checkPassword(password, confirmedPassword) {
  return password == confirmedPassword;
}

// Checks to see if user accepted the terms and conditions.
function termsAccepted() {
  return $('#terms').is(':checked');
}

$(document).ready(function() {

  // Redirect the user to the login page.
  $('#login').click(function() {
    url = './login.html';
    $(location).attr('href', url);
  });

  // Trigger on form submission.
  // Sends a POST request to the server adding a new user to the database.
  $('.register-form').on('submit', function(event){
    event.preventDefault();

    if(!checkPassword($('#password').val(), $('#confirm-password').val())){
      alert('Passwords do not match!');
    } else if($('#password').val().length <=5) {
      alert('Password must have at least 6 characters!');
    } else if(!termsAccepted()) {
      alert('Please accept the terms and conditions!');
    } else {
      dataToSend = JSON.stringify({
        'name' : $('#fullname').val(),
        'password' : $('#password').val(),
        'email' : $('#email').val()
      });
      //console.log(dataToSend);
      $.ajax({
        url: '/horus/requests/register',
        type: 'POST',
        dataType: 'json',
        data : dataToSend,
        headers: {
          'Accept' : 'application/json',
          'Content-Type' : 'application/json'
        },

        complete: function(result) {
          if(result.status == 200) {
            //alert('Account created!');
            url = './login.html';
            $(location).attr('href', url);
          } else {
            alert('Error!');
            location.reload();
          }
        }
      });
    }
  })
});
