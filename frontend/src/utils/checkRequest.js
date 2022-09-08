import {displayFeedback} from "./feedback";

/**
 * Check if a request from the database has empty return or not. If the return
 * is empty, display a message.
 *
 * If the request is empty, then it means there is no content to display.
 * Therefore, we display a message to explain it.
 *
 * @param request the request to check.
 * @param pageContentWrapper the content wrapper on the request page.
 * @param message to display if there is no content.
 * @param idWarningElement that will contain the message.
 * @returns {boolean} true if there was no content for the request and false
 *                    otherwise.
 */
function isRequestEmpty(request, pageContentWrapper, message,
  idWarningElement) {
  if (request.length === 0) {
    // Empty the content page wrapper to display the message only.
    pageContentWrapper.innerHTML = "";
    displayFeedback(message, idWarningElement);
    return true;
  }
  return false;
}

/**
 * Specific error to handle HTML request made to the server. The error will
 * save a message and the status code of the HTML request.
 */
class RequestError extends Error {
  // The status of the HTML request
  status = undefined;

  constructor(status, message) {
    super(message);
    this.name = "RequestError";
    this.status = status;
  }
}

/**
 * Specific error to handle HTML request made to the server that only need to
 * warn the user. Saves a message that can be read afterwards.
 */
class RequestWarning extends Error {
  constructor(message) {
    super(message);
    this.name = "RequestWarning";
  }
}

export {
  isRequestEmpty, RequestError, RequestWarning
}