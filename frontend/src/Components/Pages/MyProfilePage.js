import {
  getSessionObject,
  getUserAddressId,
  getUserId,
  setSessionObject
} from "../../utils/session";
import {STORED_TOKEN, STORED_USER} from "../../utils/const";
import {Redirect} from "../Router/Router";
import {displayError, displayWarning} from "../../utils/feedback";
import {buttonStartLoading} from "../../utils/buttons";
import {isRequestEmpty, RequestError} from "../../utils/checkRequest";
import {createLoadAnimation} from "../../utils/loading";
import {verifyConfirmPassword, verifyPassword} from "../../utils/password";
import Navbar from "../Navbar/Navbar";

/**
 * Render MyProfilePage and display all information of the current user in a form.
 */
const MyProfilePage = () => {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = `
    <div class="container mt-5" id="myprofile">
      <!-- Card -->
      <div class="card shadow" id="card">
        <!-- Card header -->
        <div class="card-header" id="card-header"></div>
        <!-- Card body -->
        <div class="card-body row p-5" id="myprofile-card-body"></div>
      </div>
      <!-- Error feedback --> 
      <div id="myprofile-feedback" class="m-3"></div>
    </div>
  `;
  // Get the card body id to display animations
  const cardBody = document.getElementById('myprofile-card-body');
  // Display a load animation while waiting for elements
  createLoadAnimation(cardBody);
  // Make the request to the server to get all the data needed for the content
  (retrieveData)()
  .then((dataResponse) => {
    // if 'dataResponse' is empty, then, display a message to inform the user,
    // proceed otherwise.
    if (!isRequestEmpty(dataResponse,
        document.getElementById('myprofile'),
        "Impossible de r&#233;cup&#233;rer les donn&#233;es.",
        'myprofile-feedback')) {
      // Refresh stored data in the session storage
      setSessionObject(STORED_USER, dataResponse);
      // Insert useful values in input fields
      insertData(dataResponse, cardBody);
      // Set up the password verification
      passwordVerification();
      // Set up the submit action
      document.getElementById("myprofile-submit").addEventListener(
          "click", () => submitModifications(dataResponse.version,
              dataResponse.address.versionAddress))
    }
  })
  .catch((error) => {
    if (error instanceof RequestError) {
      displayError(error.status, error.message, 'user-profile-feedback');
    } else {
      displayWarning(error, "user-profile-feedback");
    }
  });
}

export default MyProfilePage;

/**
 * Prepare the request to the server to get all the data needed for this
 * page's content.
 *
 * @returns {Promise<void>} all the data received from the server.
 */
async function retrieveData() {
  // Options for the request to the database
  const options = {
    method: "GET", headers: {
      "Content-Type": "application/json",
      "Authorization": getSessionObject(STORED_TOKEN),
    },
  };
  // Request to get all information needed
  let res = await fetch(`/api/users/${getUserId()}`, options);
  // If the status code is not in range 200 to 299, create a message for the
  // user and throws an error.
  if (!res.ok) {
    throw new RequestError(res.status,
        "Impossible de r&#233;cup&#233;rer les donn&#233;es.");
  }
  return await res.json();
}

/**
 * Get all the data received from the server and insert it in the form input's
 * fields.
 *
 * @param data from the server.
 * @param cardBody the element that will contain all the data.
 */
function insertData(data, cardBody) {
  cardBody.innerHTML = `
    <!-- Header -->
    <h2 class="fw-bold mb-5" id="myprofile-load-animation">MON PROFIL</h2>
    <!-- Username -->
    <div class="col-md-4">
      <label for="myprofile-username" class="form-label">
        <strong>Pseudo</strong>
      </label>
      <input type="text" class="form-control" id="myprofile-username"
        placeholder="${data.username}">
    </div>
    <!-- Firstname -->
    <div class="col-md-4">
      <label for="myprofile-firstname" class="form-label">
        <strong>Pr&eacute;nom</strong>
      </label>
      <input type="text" class="form-control" id="myprofile-firstname"
        placeholder="${data.surname}">
    </div>
    <!-- Lastname -->
    <div class="col-md-4 mb-5">
      <label for="myprofile-lastname" class="form-label">
        <strong>Nom</strong>
      </label>
      <input type="text" class="form-control" id="myprofile-lastname"
        placeholder="${data.lastname}">
    </div>
    <!-- Street -->
    <div class="col-md-6">
      <label for="myprofile-street" class="form-label">
        <strong>Rue</strong>
      </label>
      <input type="text" class="form-control" id="myprofile-street"
        placeholder="${data.address.street}">
    </div>
    <!-- Building number -->
    <div class="col-md-3 col-6">
      <label for="myprofile-building" class="form-label">
        <strong>Num&eacute;ro</strong>
      </label>
      <input type="text" class="form-control" id="myprofile-building"
        placeholder="${data.address.buildingNumber}">
    </div>
    <!-- Box -->
    <div class="col-md-3 col-6">
      <label for="myprofile-box" class="form-label">
        <strong>Boite</strong>
      </label>
      <input type="text" class="form-control" id="myprofile-box">
    </div>
    <!-- Commune -->
    <div class="col-md-6">
      <label for="myprofile-commune" class="form-label">
        <strong>Commune</strong>
      </label>
      <input type="text" class="form-control" id="myprofile-commune"
        placeholder="${data.address.commune}">
    </div>
    <!-- Postal code -->
    <div class="col-md-3 col-6 mb-5">
      <label for="myprofile-postal-code" class="form-label">
        <strong>Code postal</strong>
      </label>
      <input type="text" class="form-control" id="myprofile-postal-code"
        placeholder="${data.address.postcode}">
    </div>
    <!-- Cellular -->
    <div class="col-md-5 mb-5">
      <label for="myprofile-cellular" class="form-label">
        <strong>Num&eacute;ro de t&eacute;l&eacute;phone</strong>
      </label>
      <input type="tel" class="form-control" id="myprofile-cellular">
      <div id="myprofile-cellular-feedback"></div>
    </div>
    <!-- Space for better display -->
    <div class="col-md-7"></div>
    <!-- Password -->
    <div class="col-md-6">
      <label for="myprofile-password" class="form-label">
        <strong>Mot de passe</strong>
      </label>
      <input type="password" class="form-control" id="myprofile-password">
      <div id="myprofile-password-feedback"></div>
    </div>
    <!-- Password confirmation -->
    <div class="col-md-6 mb-5">
      <label for="myprofile-password-confirmation" class="form-label">
        <strong>Confirmation du mot de passe</strong>
      </label>
      <input type="password" class="form-control"
        id="myprofile-password-confirmation">
      <div id="myprofile-password-confirmation-feedback"></div>
    </div>
    <!-- Submit button -->
    <div class="col-md-12 text-center mb-3">
      <button id="myprofile-submit" type="submit" class="btn btn-lg">
        Valider
      </button>
    </div>
  `;
  // Box
  if (data.address.unitNumber != null) {
    document.getElementById('myprofile-box')
        .placeholder = data.address.unitNumber;
  }
  // Cellular
  if (data.phoneNumber != null) {
    document.getElementById('myprofile-cellular')
        .placeholder = data.phoneNumber;
  }
}

/**
 * Verify if the password value and the confirmation password value. Give a
 * direct feedback to the user.
 */
function passwordVerification() {
  // Password verification
  let inputPwd = document.getElementById('myprofile-password');
  let feedbackPwd = document.getElementById('myprofile-password-feedback');
  verifyPassword(inputPwd, feedbackPwd);
  // Confirm password verification
  let inputConf = document.getElementById('myprofile-password-confirmation');
  let feedbackConf = document.getElementById(
      'myprofile-password-confirmation-feedback');
  verifyConfirmPassword(inputPwd, inputConf, feedbackConf);
}

/**
 * Submit the changes to the database by sending a request to the server.
 *
 * @param userVersion the version of the user
 * @param versionAddress the version of the address
 * @returns {Promise<void>} the response of the request.
 */
async function submitModifications(userVersion, versionAddress) {
  // Loading button
  buttonStartLoading(document.getElementById('myprofile-submit'));
  // Select all the input fields and get the modified value. If there is no
  // changes, get the default value.
  let username = getInputValue(document.getElementById("myprofile-username"));
  let firstname = getInputValue(document.getElementById("myprofile-firstname"));
  let lastname = getInputValue(document.getElementById("myprofile-lastname"));
  let street = getInputValue(document.getElementById("myprofile-street"));
  let buildingNumber = getInputValue(
      document.getElementById("myprofile-building"));
  let unitNumber = getInputValue(document.getElementById("myprofile-box"));
  let commune = getInputValue(document.getElementById("myprofile-commune"));
  let postalCode = getInputValue(
      document.getElementById("myprofile-postal-code"));
  let cellular = getInputValue(document.getElementById("myprofile-cellular"));
  let password = getInputValue(document.getElementById("myprofile-password"));
  // Prepare a request to send the modified values to the server.
  // Set the options required for the request
  const options = {
    method: "PUT",
    body: JSON.stringify({
      idUser: getUserId(),
      username: username,
      surname: firstname,
      lastname: lastname,
      address: {
        idAddress: getUserAddressId(),
        street: street,
        buildingNumber: buildingNumber,
        unitNumber: unitNumber,
        postcode: postalCode,
        commune: commune,
        versionAddress: versionAddress,
      },
      phoneNumber: cellular,
      password: password,
      version: userVersion,
    }), // body data type must match "Content-Type" header
    headers: {
      "Content-Type": "application/json",
      "Authorization": getSessionObject(STORED_TOKEN),
    },
  };
  // Send the request
  let response = await fetch(`/api/users/${getUserId()}/modify`, options);
  if (!response.ok) {
    Redirect('/me');
  }
  let data = await response.json();
  // Refresh stored data in the session storage
  setSessionObject(STORED_USER, data);
  // Reload the navbar with the new data
  Navbar();
  Redirect(`/me`);
}

/**
 * Check the input value of the element and return the default value if it is
 * empty.
 *
 * @param element input that needs to be checked.
 * @returns {*} the value contained in the value field of the input or the
 * default value of the element, contained in the placeholder field if there is
 * none.
 */
function getInputValue(element) {
  if (element.value === "") {
    return element.placeholder;
  } else {
    return element.value;
  }
}
