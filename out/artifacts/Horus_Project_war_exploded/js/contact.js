//Reseting the fields and hiding the button of contactModal whenever this script loads
$('#contact-subject').val('');
$('#contact-message').val('');
$('#contact-button').hide();

//The next 9 lines allow the button to appear while the user is typing and also when minimal conditions are met.
$('#contact-subject').on('input',function() {
  if ($('#contact-subject').val().length > 2 && $('#contact-message').val().length > 5) {
      $('#contact-button').show();
    } else $('#contact-button').hide();
});
$('#contact-message').on('input',function() {
  if ($('#contact-subject').val().length > 2 && $('#contact-message').val().length > 5) {
      $('#contact-button').show();
    } else $('#contact-button').hide();
});

// Open the "contact us" modal and send the message to the server.
$('#contact-button').off().on('click', function(event){
  event.preventDefault();
  var subject = $('#contact-subject').val();
  var body = $('#contact-message').val();
  var author = Cookies.getJSON('relevantData').email;
  $.ajax({
    url: '/horus/requests/support',
    type: 'POST',
    dataType: 'json',
    headers: {
      'Accept' : 'application/json',
      'Content-Type' : 'application/json',
      'email' : author,
      'head' : subject,
      'body' : body
    },
    complete: function(result){
      if(result.status == 200) {
        //alert('Thank you for contacting us!');
        location.reload();
      } else {
        console.log("error: " + result.status + ". " + result.errorMessage);
      }
    }
  })
});
