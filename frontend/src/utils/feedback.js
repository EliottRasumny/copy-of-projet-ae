/**
 * Display a bootstrap warning alert that inform the user with a message.
 *
 * @param message text to display, describing the warning.
 * @param idDiv id of the parent div that will contain the feedback.
 */
function displayFeedback(message, idDiv) {
  // Create a wrapper element in the 'idDiv', that will contain the alert.
  let wrapper = document.getElementById(idDiv);
  // Reset the content of the wrapper if there was still a child in it. It can
  // happen when the alert is triggered more than once. By doing so, there will
  // never be more than one child.
  if (wrapper.hasChildNodes()) {
    wrapper.removeChild(wrapper.firstChild);
  }
  // Create the alert in itself and insert the message to it.
  let feedback = document.createElement('div');
  feedback.innerHTML = `
    <div class="alert alert-warning text-center shadow" role="alert">` +
    message + `
    </div>
  `;
  // Add the alert to the wrapper.
  wrapper.append(feedback);
}

/**
 * Display a bootstrap success alert that inform the user with a message with
 * a heading.
 *
 * @param message text to display, describing the success.
 * @param idDiv id of the parent div that will contain the feedback.
 */
function displaySuccess(message, idDiv) {
  // Create a wrapper element in the 'idDiv', that will contain the alert.
  let wrapper = document.getElementById(idDiv);
  // Reset the content of the wrapper if there was still a child in it. It can
  // happen when the alert is triggered more than once. By doing so, there will
  // never be more than one child.
  if (wrapper.hasChildNodes()) {
    wrapper.removeChild(wrapper.firstChild);
  }
  // Create the alert in itself and insert the message to it.
  let feedback = document.createElement('div');
  feedback.innerHTML = `
    <div class="alert alert-success text-center shadow" role="alert">` +
    message + `
    </div>`;
  // Add the alert to the wrapper.
  wrapper.append(feedback);
}

/**
 * Display a bootstrap warning alert that inform the user with a message with
 * a heading.
 *
 * @param message text to display, describing the warning.
 * @param idDiv id of the parent div that will contain the feedback.
 */
function displayWarning(message, idDiv) {
  // Create a wrapper element in the 'idDiv', that will contain the alert.
  let wrapper = document.getElementById(idDiv);
  // Reset the content of the wrapper if there was still a child in it. It can
  // happen when the alert is triggered more than once. By doing so, there will
  // never be more than one child.
  if (wrapper.hasChildNodes()) {
    wrapper.removeChild(wrapper.firstChild);
  }
  // Create the alert in itself and insert the message to it.
  let feedback = document.createElement('div');
  feedback.innerHTML = `
    <div class="alert alert-warning text-center shadow" role="alert">
      <i class="bi-exclamation-triangle-fill" role="img"
      aria-label="Icone alerte">
      <strong>Attention : </strong>
      </i>` +
    message + `
    </div>`;
  // Add the alert to the wrapper.
  wrapper.append(feedback);
}

/**
 * Display a bootstrap warning alert that inform the user with a message and the
 * status code of the error with a heading.
 *
 * @param status code of the error.
 * @param message text to display, describing the error.
 * @param idDiv id of the parent div that will contain the message.
 */
function displayError(status, message, idDiv) {
  // Create a wrapper element in the 'idDiv', that will contain the alert.
  let wrapper = document.getElementById(idDiv);
  // Reset the content of the wrapper if there was still a child in it. It can
  // happen when the alert is triggered more than once. By doing so, there will
  // never be more than one child.
  if (wrapper.hasChildNodes()) {
    wrapper.removeChild(wrapper.firstChild);
  }
  // Create the alert in itself and insert the message to it.
  let feedback = document.createElement('div');
  feedback.innerHTML = `
    <div class="alert alert-warning text-center shadow" role="alert">
      <i class="bi-x-octagon-fill" role="img" aria-label="Icone erreur">
      <strong>Erreur ` + status + ` : </strong>
      </i>` +
    message + `
    </div>`;
  // Add the alert to the wrapper.
  wrapper.append(feedback);
}

/**
 * Display a bootstrap warning alert that inform the user with a message with
 * a heading.
 *
 * @param message text to display, describing the error.
 * @param idDiv id of the parent div that will contain the message.
 */
function displayErrorNoStatus(message, idDiv) {
  // Create a wrapper element in the 'idDiv', that will contain the alert.
  let wrapper = document.getElementById(idDiv);
  // Reset the content of the wrapper if there was still a child in it. It can
  // happen when the alert is triggered more than once. By doing so, there will
  // never be more than one child.
  if (wrapper != null && wrapper.hasChildNodes()) {
    wrapper.removeChild(wrapper.firstChild);
  }
  // Create the alert in itself and insert the message to it.
  let feedback = document.createElement('div');
  feedback.innerHTML = `
    <div class="alert alert-warning text-center shadow" role="alert">
      <i class="bi-x-octagon-fill" role="img" aria-label="Icone erreur">
      <strong>Erreur : </strong>
      </i>` +
    message + `
    </div>`;
  // Add the alert to the wrapper.
  wrapper.append(feedback);
}

export {
  displayFeedback,
  displaySuccess,
  displayWarning,
  displayError,
  displayErrorNoStatus,
};