function validateBooking() {
  var userID = document.getElementById("userID").value;
  var phone = document.getElementById("phone").value;
  var email = document.getElementById("email").value;
  var securityCode = document.getElementById("securityCode").value;
  var correctSecurityCode = document.getElementById("code").innerHTML;
  var resultStatus = true;
  var messageStr = "The following errors were detected:\n";

  var uidCheck = /\d/;
  var phoneCheck = /\D/;
  var emailCheck = /[@]/;

  if (!userID || userID == "") {
    resultStatus = false;
    messageStr += "UserID is blank\n";
  }

  if (uidCheck.test(userID)) {
    resultStatus = false;
    messageStr += "UserID can not contain numbers\n";
  }

  if (phoneCheck.test(phone)) {
    resultStatus = false;
    messageStr += "Phone can only contain numbers\n";
  }

  if (!emailCheck.test(email)) {
    resultStatus = false;
    messageStr += "Invalid email formatting\n";
  }

  if (securityCode != correctSecurityCode) {
    messageStr += "security Code does not match\n";
    resultStatus = false;
  }

  if (!resultStatus) {
    alert(messageStr);
  }
  return resultStatus;
}
