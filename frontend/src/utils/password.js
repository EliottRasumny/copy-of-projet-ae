/**
 * Verify the password and change the feedback accordingly;
 * while clicking on the input field of the password, clicking outside off it,
 * or while typing a character.
 * @param input element of the password input
 * @param feedback element of the feedback div
 */
function verifyPassword(input, feedback) {
  input.onfocus = () => {
    allChecks(input, feedback);
  };
  input.onblur = () => {
    allChecks(input, feedback);
  };
  input.onkeyup = () => {
    allChecks(input, feedback);
  };
}

/**
 * Launch every needed tests to check if the password is correct or not
 * @param input element of the password input
 * @param feedback element of the feedback div
 */
function allChecks(input, feedback) {
  feedback.innerHTML = ""; //reset everytime
  checkLowerCase(input, feedback);
  checkUpperCase(input, feedback);
  checkNumber(input, feedback);
  checkLength(input, feedback);
  validFeedback(input, feedback);
}

/**
 * Check if the password has at least 1 lower case character and give an invalid
 * feedback if it is not the case.
 * @param input element of the password input
 * @param feedback element of the feedback div
 */
function checkLowerCase(input, feedback) {
  if (!input.value.match(/[a-z]/g)) {
    input.className = "form-control is-invalid";
    feedback.className = "invalid-feedback text-start";
    feedback.innerHTML += "Au moins 1 lettre minuscule.<br>";
  }
}

/**
 * Check if the password has at least 1 upper case character and give an invalid
 * feedback if it is not the case.
 * @param input element of the password input
 * @param feedback element of the feedback div
 */
function checkUpperCase(input, feedback) {
  if (!input.value.match(/[A-Z]/g)) {
    input.className = "form-control is-invalid";
    feedback.className = "invalid-feedback text-start";
    feedback.innerHTML += "Au moins 1 lettre majuscule.<br>";
  }
}

/**
 * Check if the password has at least 1 number and give an invalid
 * feedback if it is not the case.
 * @param input element of the password input
 * @param feedback element of the feedback div
 */
function checkNumber(input, feedback) {
  if (!input.value.match(/[0-9]/g)) {
    input.className = "form-control is-invalid";
    feedback.className = "invalid-feedback text-start";
    feedback.innerHTML += "Au moins 1 chiffre.<br>";
  }
}

/**
 * Check if the password is at least 8 characters' length and give an invalid
 * feedback if it is not the case.
 * @param input element of the password input
 * @param feedback element of the feedback div
 */
function checkLength(input, feedback) {
  if (input.value.length < 8) {
    input.className = "form-control is-invalid";
    feedback.className = "invalid-feedback text-start";
    feedback.innerHTML += "Au moins 8 caract&egrave;res.";
  }
}

/**
 * Change the password's feedback as valid if there is no errors.
 * There is no errors when the length of the innerHTML's feedback is 0.
 * @param input element of the password input
 * @param feedback element of the feedback div
 */
function validFeedback(input, feedback) {
  if (feedback.innerHTML.length === 0) {
    input.className = "form-control is-valid";
    feedback.className = "valid-feedback text-start";
    feedback.innerHTML = "OK !";
  }
}

/**
 * Verify the confirmed password with the password and change the feedback accordingly;
 * while clicking on the input field of the confirmed password, clicking outside off
 * it, or while typing a character.
 * @param inputPwd element of the password input
 * @param inputConf element of the confirmed password input
 * @param feedback element of the feedback div
 */
function verifyConfirmPassword(inputPwd, inputConf, feedback) {
  feedback.innerHTML = "";
  inputConf.onfocus = () => {
    equalsPasswords(inputPwd, inputConf, feedback);
  };
  inputConf.onblur = () => {
    equalsPasswords(inputPwd, inputConf, feedback);
  }
  inputConf.onkeyup = () => {
    equalsPasswords(inputPwd, inputConf, feedback);
  };
}

/**
 * Check the confirmed password input and give a feedback depending on it.
 * 'No password' if the field is empty.
 * 'Not match' if the confirmed password is not the same as the password.
 * 'OK !' if both of them are the same.
 * @param inputPwd element of the password input
 * @param inputConf element of the confirmed password input
 * @param feedback element of the feedback div
 */
function equalsPasswords(inputPwd, inputConf, feedback) {
  if (inputConf.value.length === 0) {
    inputConf.className = "form-control is-invalid";
    feedback.className = "invalid-feedback text-start";
    feedback.innerHTML = "Aucun mot de passe entr&eacute;.";
  } else if (inputPwd.value !== inputConf.value) {
    inputConf.className = "form-control is-invalid";
    feedback.className = "invalid-feedback text-start";
    feedback.innerHTML = "Les mots de passe ne sont pas identiques.";
  } else {
    inputConf.className = "form-control is-valid";
    feedback.className = "valid-feedback text-start";
    feedback.innerHTML = "OK !";
  }
}

export {
  verifyPassword,
  verifyConfirmPassword,
}