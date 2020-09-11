  // Events that happen at page refresh.

$( document ).ready(function() {
  // Hide several buttons from the pop-ups.
  $("#settingsOptions").val("0").change();
        var isChange = !$('#change-radio').prop('checked');
        var isCancel = !$('#cancel-radio').prop('checked');

        if(!isChange){
          $('#cancel-button').hide();
            $('#change-form').show();
            $('#reschedule-button').show();
        }
         if(!isCancel){
          $('#change-form').hide();
          $('#reschedule-button').hide();
            $('#cancel-button').show();
        }
    });
