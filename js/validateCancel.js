function validateCancel() {
  var userID = document.getElementById("userID").value;
  var verification = document.getElementById("verification").value;
  var securityCode = document.getElementById("securityCode").value;
  var correctSecurityCode = document.getElementById("code").innerHTML;
  var resultStatus = true;
  var messageStr = "The following errors were detected:\n";

  var uidCheck = /\d/;

  if (!userID || userID == "") {
    resultStatus = false;
    messageStr += "UserID is blank\n";
  }

  if (uidCheck.test(userID)) {
    resultStatus = false;
    messageStr += "UserID can not contain numbers\n";
  }

  if (!verification || verification == "") {
    resultStatus = false;
    messageStr += "Blank verification field\n";
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
