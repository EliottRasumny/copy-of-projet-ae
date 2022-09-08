import {Redirect} from "../Router/Router";
import {getSessionObject} from "../../utils/session";
import {STORED_TOKEN} from "../../utils/const";
import {displayError, displayErrorNoStatus} from "../../utils/feedback";
import {createBackToTopButton} from "../../utils/buttons";
import {createLoadAnimation} from "../../utils/loading";
import {isRequestEmpty, RequestError} from "../../utils/checkRequest";

/**
 * Render the InscriptionDenied page and display all the users for which the
 * inscription has been denied.
 *
 * Create a table that will display all the users that are under this condition.
 * Then fill it with all of them.
 * It is possible to click on the button on the right to directly approve the
 * user and by checking the 'admin' box, this one will even become an admin.
 */
const InscriptionsDeniedPage = () => {
  // Create the div that will contain all the content for the page.
  const pageDiv = document.querySelector("#page");
  pageDiv.className = "container";
  pageDiv.innerHTML = `
    <!-- Error feedback --> 
    <div id="denied-inscription-requests-feedback" class="m-3"></div>
    <!-- Members' inscription card -->
    <div class="card shadow mt-5" id="card">
      <!-- Card header -->
      <div class="card-header" id="card-header"></div>
      <!-- Card body -->
      <div class="card-body">
        <!-- Header title -->
        <h2 class="fw-bold mb-5 text-center">
          DEMANDES D'INSCRIPTION REFUS&Eacute;ES
        </h2>
        <table id="denied-inscriptions-requests-table" class="table table-hover"></table>
      </div>
    </div>
  `;
  // Create a 'back to top' button
  createBackToTopButton(pageDiv);
  // Get the table that will contain all the information
  const table = document.getElementById('denied-inscriptions-requests-table');
  // Display a load animation on the page while waiting for elements
  createLoadAnimation(table);
  // Make the request to the server to get all the data needed for the content
  (retrieveData)()
  .then((dataResponse) => {
    // if 'dataResponse' is empty, then, display a message to inform the user,
    // proceed otherwise.
    if (!isRequestEmpty(dataResponse,
      document.getElementById('card'),
      `Aucun membre &agrave; afficher`,
      'denied-inscription-requests-feedback')) {
      // Create all the table's header
      createTableContent(table);
      // Get the body of the table
      let tbody = document.getElementById(
        'inscriptions-refused-members-table-body');
      // Insert all the users in the row of the table
      dataResponse.forEach((data) => {
        insertRow(data, tbody);
      });
    }
  })
  .catch((err) => {
    if (err instanceof RequestError) {
      displayError(err.status, err.message,
        'denied-inscription-requests-feedback');
    } else {
      displayErrorNoStatus(err, 'denied-inscription-requests-feedback');
    }
  });
}

export default InscriptionsDeniedPage;

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
  let res = await fetch(`/api/users/refusals`, options);
  // If the status code is not in range 200 to 299, create a message for the
  // user and throws an error.
  if (!res.ok) {
    throw new RequestError(res.status,
      "Impossible de r&#233;cup&#233;rer les donn&#233;es.");
  }
  return await res.json();
}

/**
 * Create the header and the tbody element of the table with fields : 'pseudo',
 * 'prenom', 'nom', 'raison du refus'.
 *
 * @param table in which the header needs to be inserted.
 */
function createTableContent(table) {
  table.innerHTML = `
    <thead>
      <tr>
        <th scope="col" class="flex-md-nowrap">Pseudo</th>
        <th scope="col" class="flex-md-nowrap">Pr&eacute;nom</th>
        <th scope="col" class="flex-md-nowrap">Nom</th>
        <th scope="col" class="flex-md-nowrap">Raison du refus</th>
        <th scope="col" class="flex-md-nowrap">Devenir admin ?</th>
        <th scope="col" class="text-end">Valider</th>
      </tr>
    </thead>
    <tbody id="inscriptions-refused-members-table-body"></tbody>
  `;
}

/**
 * Insert a row in the table body with the data from the request. Fields are :
 * 'pseudo', 'prenom', 'nom', 'raison de refus'.
 *
 * @param tbody in which the row need to be inserted.
 * @param data that contains all the required data.
 */
function insertRow(data, tbody) {
  let line = document.createElement('tr');
  line.innerHTML = `
    <td class="flex-md-nowrap">${data.username}</td>
    <td class="flex-md-nowrap">${data.surname}</td>
    <td class="flex-md-nowrap">${data.lastname}</td>
    <td class="flex-md-nowrap">${data.refusalReason}</td>
    <td class="flex-md-nowrap">
      <label>Futur admin : </label>
      <input type="checkbox" id="admin${data.idUser}" name="admin"
        value="admin">
    </td>
    <td class="text-end">
      <button class="btn btn-secondary flex-row"
        id="inscriptions-refused-submit${data.idUser}">
        <i class="bi-plus-lg"></i></button>
    </td>
  `;
  // insert the new line to the body of the table
  tbody.appendChild(line);
  // Create the action on click for the button and the style of it.
  let button = document.getElementById(
    `inscriptions-refused-submit${data.idUser}`);
  button.addEventListener("click", () => submitForm(data.idUser, data.version));
  button.style.color = "white";
  button.style.backgroundColor = "#293354";
  button.addEventListener("mouseenter", () => {
    button.style.color = "#293354";
    button.style.backgroundColor = "white";
  });
  button.addEventListener("mouseleave", () => {
    button.style.color = "white";
    button.style.backgroundColor = "#293354";
  });
}

/**
 * Create the request to confirm a user's inscription and then send it.
 *
 * Throws an exception and display an error message if there is a problem with
 * the request or the response.
 * Reload the page to see the changes if there is no problem.
 *
 * @param idUser the id of the user that needs to be accepted.
 * @param userVersion the version of the user
 */
async function submitForm(idUser, userVersion) {
  var admin = document.getElementById("admin" + idUser).checked;
  const options = {
    method: "PATCH", headers: {
      "Content-Type": "application/json",
      "Authorization": getSessionObject(STORED_TOKEN)
    }, body: JSON.stringify({
      futurAdmin: admin,
      userVersion: userVersion,
    }),
  };
  await fetch(`/api/users/${idUser}/confirm`, options)
  .then((response) => {
    if (!response.ok) {
      throw new RequestError(response.status,
        "Impossible d'entreprendre l'action.");
    }
    Redirect(`/inscriptions/refusals`);
  })
  .catch((err) => {
    if (err instanceof RequestError) {
      if (err.status === 412) {
        displayError(err.status, "Le membre est déjà accepté.",
          "denied-inscription-requests-feedback")
      } else {
        displayError(err.status, err.message,
          'denied-inscription-requests-feedback');

      }
    } else {
      displayErrorNoStatus(err, 'denied-inscription-requests-feedback');
    }

  });
}