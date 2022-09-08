import {getSessionObject} from "../../utils/session";
import {STORED_TOKEN} from "../../utils/const";
import {Redirect} from "../Router/Router";
import {
  displayError,
  displayErrorNoStatus,
  displayWarning
} from "../../utils/feedback";
import {createLoadAnimation} from "../../utils/loading";
import {createBackToTopButton} from "../../utils/buttons";
import {isRequestEmpty, RequestError} from "../../utils/checkRequest";

/**
 * Render the UserInscription page for administrators and display all the user's
 * information. Allow the administrator to accept or refuse the user's
 * inscription.
 *
 * If the administrator want to refuse the user's inscription, he needs to
 * complete the 'refusal's reason'.
 */
const UserInscriptionPage = () => {
  // Retrieve the id of the seen user.
  var splitUrl = window.location.href.split('?');
  var searchParams = new URLSearchParams(splitUrl[1]);
  const idUser = searchParams.get('id');
  // Create the div that will contain all the content for the page.
  const pageDiv = document.querySelector("#page");
  pageDiv.className = "container";
  pageDiv.innerHTML = `
    <!-- Members' inscription card -->
    <div class="card shadow mt-5" id="card">
      <!-- Card header -->
      <div class="card-header" id="card-header"></div>
      <!-- Card body -->
      <div class="card-body row p-5">
        <!-- Header title -->
        <h2 class="fw-bold mb-5 text-center">
          DEMANDE EN ATTENTE
        </h2>
        <div id="user-inscription-request-content" class="row"></div>
      </div>
    </div>
    <!-- Error feedback --> 
    <div id="user-inscription-request-feedback" class="m-3"></div>
  `;
  // Create a 'back to top' button
  createBackToTopButton(pageDiv);
  // Get the table that will contain all the information
  const content = document.getElementById("user-inscription-request-content");
  // Display a load animation while waiting for elements
  createLoadAnimation(content);
  // Make the request to the server to get all the data needed for the content
  (retrieveData)(idUser)
  .then((data) => {
    if (!isRequestEmpty(data,
        document.getElementById('card'),
        `Aucun membre &agrave; afficher`,
        'user-inscription-request-feedback')) {
      insertData(data, content);
    }
  })
  .catch((err) => {
    // Reset the table to remove the animation
    document.getElementById('card').innerHTML = "";
    if (err instanceof RequestError) {
      displayError(err.status, err.message,
          'user-inscription-request-feedback');
    } else {
      displayErrorNoStatus(err, 'user-inscription-request-feedback');
    }
  });
}

export default UserInscriptionPage;

/**
 * Prepare the request to the server to get all the data needed for this
 * page's content.
 *
 * @param idUser the id off the user that we are reviewing inscription.
 * @returns {Promise<void>} all the data received from the server.
 */
async function retrieveData(idUser) {
  // Options for the request to the database
  const options = {
    method: "GET", headers: {
      "Content-Type": "application/json",
      "Authorization": getSessionObject(STORED_TOKEN),
    },
  };
  // Request to get all information needed
  let res = await fetch(`/api/users/${idUser}`, options);
  // If the status code is not in range 200 to 299, create a message for the
  // user and throws an error.
  if (!res.ok) {
    throw new RequestError(res.status,
        "Impossible de r&#233;cup&#233;rer les donn&#233;es.");
  }
  return await res.json();
}

/**
 * Insert all the information of the user and the options to accept or not its
 * inscription.
 *
 * @param data contains the information of the user.
 * @param content in which the header needs to be inserted.
 */
function insertData(data, content) {
  content.innerHTML = `
    <!-- Username -->
    <div class="col-md-2">
      <strong>Pseudo</strong>
      <P>${data.username}</P>
    </div>
    <!-- Surname -->
    <div class="col-md-2">
      <strong>Pr&eacute;nom</strong>
      <P>${data.surname}</P>
    </div>
    <!-- Lastname -->
    <div class="col-md-2">
      <strong>Nom</strong>
      <P>${data.lastname}</P>
    </div>
    <!-- Phone Number -->
    <div class="col-md-2">
      <strong>Num&eacute;ro de t&eacute;l&eacute;phone</strong>
      <P id="user-inscription-phone-number"></P>
    </div>
    <!-- Address -->
    <div class="col-md-auto">
      <strong>Adresse</strong>
      <P id="user-inscription-address"></P>
    </div>
    <!-- Future admin -->
    <div class="mt-3 col-md-12">
      <label>Ce membre doit-il devenir un administrateur ?</label>
      <input type="checkbox" id="admin" name="admin" value="admin">
    </div>
    <!-- Username -->
    <div class="col-md-12 mt-3">
      <label for="user-inscription-refusal-reason" class="form-label">
        <strong>Raison de refus : </strong>
      </label>
      <textarea class="form-control" id="user-inscription-refusal-reason"
        placeholder="Compl&eacute;tez pour pouvoir refuser."></textarea>
    </div>
    <!-- Submit button -->
    <div class="col-md-12 text-center mt-4">
      <button id="user-inscription-refuse" type="submit" class="btn btn-lg">
        Refuser
      </button>
      <button id="user-inscription-confirm" type="submit" class="btn btn-lg">
        Confirmer
      </button>
    </div>
  `;
  // Display correctly the address if there is a unit number or not
  if (data.address.unitNumber !== null) {
    document.getElementById('user-inscription-address').innerHTML = `
      ${data.address.street + ` ` + data.address.buildingNumber +
    ` bte ` + data.address.unitNumber + `, `
    + data.address.commune + ` ` + data.address.postcode}
    `;
  } else {
    document.getElementById('user-inscription-address').innerHTML = `
      ${data.address.street + ` ` + data.address.buildingNumber + `, `
    + data.address.commune + ` ` + data.address.postcode}
    `;
  }
  // Display correctly the phone number if it is null or not
  if (data.phoneNumber === null) {
    document.getElementById('user-inscription-phone-number')
        .innerText = "/";
  } else {
    document.getElementById('user-inscription-phone-number')
        .innerText = data.phoneNumber;
  }
  // Add actions to the buttons
  document.getElementById('user-inscription-refuse').addEventListener("click",
      () => refuseInscription(data.idUser, data.version));
  document.getElementById('user-inscription-confirm').addEventListener("click",
      () => acceptInscription(data.idUser, data.version));
}

/**
 * Prepare the request to the server to validate the inscription of the user.
 *
 * @param idUser the id off the user to accept.
 * @param userVersion the version of the user
 * @returns {Promise<void>} all the data received from the server.
 */
async function acceptInscription(idUser, userVersion) {
  var admin = document.getElementById("admin").checked;
  const options = {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json",
      "Authorization": getSessionObject(STORED_TOKEN)
    },
    body: JSON.stringify({
      futurAdmin: admin,
      userVersion: userVersion,
    }),
  };
  await fetch(`/api/users/${idUser}/confirm`, options)
  .then((res) => {
    // If the status code is not in range 200 to 299, create a message for the
    // user and throws an error.
    if (!res.ok) {
      throw new RequestError(res.status,
          "Impossible d'entreprendre l'action.");
    }
    return res.json();
  })
  .then((data) => {
    // Check if the server's response says that the user's inscription was
    // refused
    if (data) {
      Redirect(`/inscriptions`)
    } else {
      // If not throws an exception to display a message.
      throw new Error("L'op&#233;ration n'a pas pu se faire.");
    }
  })
  .catch((err) => {
    if (err instanceof RequestError) {
      displayError(err.status, err.message,
          'user-inscription-request-feedback');
    } else {
      displayErrorNoStatus(err, 'user-inscription-request-feedback');
    }
  });
}

/**
 * Prepare the request to the server to refuse the inscription of the user.
 *
 * @param idUser the id off the user to refuse.
 * @param userVersion the version of the user
 * @returns {Promise<void>} all the data received from the server.
 */
async function refuseInscription(idUser, userVersion) {
  // Get the refusal reason's value.
  var refusalReasonText = document.getElementById(
      "user-inscription-refusal-reason").value;
  // If the reason was not completed, display a warning message to the admin.
  if (refusalReasonText === "") {
    displayWarning("Veuillez compl&#233;ter la raison du refus.",
        'user-inscription-request-feedback');
  } else {
    // Options for the request to the database
    const options = {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        "Authorization": getSessionObject(STORED_TOKEN)
      },
      body: JSON.stringify({
        refusalReason: refusalReasonText,
        userVersion: userVersion,
      }),
    };
    await fetch(`/api/users/${idUser}/refuse`, options)
    .then((res) => {
      // If the status code is not in range 200 to 299, create a message for the
      // user and throws an error.
      if (!res.ok) {
        throw new RequestError(res.status,
            "Impossible d'entreprendre l'action.");
      }
      return res.json();
    })
    .then((data) => {
      // Check if the server's response says that the user's inscription was
      // refused
      if (data) {
        Redirect(`/inscriptions`)
      } else {
        // If not throws an exception to display a message.
        throw new Error("L'op&#233;ration n'a pas pu se faire.");
      }
    })
    .catch((err) => {
      if (err instanceof RequestError) {
        displayError(err.status, err.message,
            'user-inscription-request-feedback');
      } else {
        displayErrorNoStatus(err, 'user-inscription-request-feedback');
      }
    });
  }
}