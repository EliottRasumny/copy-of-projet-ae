import {Redirect} from "../Router/Router";
import {getSessionObject} from "../../utils/session";
import {STORED_TOKEN} from "../../utils/const";
import {displayError, displayErrorNoStatus} from "../../utils/feedback";
import {createLoadAnimation} from "../../utils/loading";
import {isRequestEmpty, RequestError} from "../../utils/checkRequest";

/**
 * Render the Inscriptions page and display all the users that are waiting for
 * an inscription's approval.
 *
 * Create a table that will display all the users that are under this condition.
 * Then fill it with all of them.
 * It is possible to click on the button on the right of each user to go to
 * another page that will display all information about this specific user and
 * allow approving or refusing its inscription.
 */
const InscriptionsPage = () => {
  // Create the div that will contain all the content for the page.
  const pageDiv = document.querySelector("#page");
  pageDiv.className = "container";
  pageDiv.innerHTML = `
    <!-- Error feedback --> 
    <div id="inscription-requests-feedback" class="m-3"></div>
    <!-- Members' inscription card -->
    <div class="card shadow mt-5" id="card">
      <!-- Card header -->
      <div class="card-header" id="card-header"></div>
      <!-- Card body -->
      <div class="card-body">
        <!-- Header title -->
        <h2 class="fw-bold mb-5 text-center">
          DEMANDES D'INSCRIPTION EN ATTENTE
        </h2>
        <table id="inscriptions-members-table" class="table table-hover"></table>
      </div>
    </div>
  `;
  const table = document.getElementById('inscriptions-members-table');
  // Display a load animation while waiting for elements
  createLoadAnimation(table);
  // Make the request to the server to get all the data needed for the content
  (retrieveData)()
  .then((dataResponse) => {
    // if 'dataResponse' is empty, then, display a message to inform the user,
    // proceed otherwise.
    if (!isRequestEmpty(
      dataResponse,
      document.getElementById('card'),
      `Aucun membre &agrave; afficher`,
      'inscription-requests-feedback'
    )) {
      // Create all the table's header
      createTableContent(table);
      // Get the body of the table
      let tbody = document.getElementById('inscriptions-members-table-body');
      // Insert all the users in the row of the table
      dataResponse.forEach((data) => {
        insertRow(data, tbody);
      });
    }
  })
  .catch((err) => {
    // Reset the table to remove the animation
    document.getElementById('card').innerHTML = "";
    if (err instanceof RequestError) {
      displayError(err.status, err.message,
        'inscription-requests-feedback');
    } else {
      displayErrorNoStatus(err, 'inscription-requests-feedback');
    }
  });
}

export default InscriptionsPage;

/**
 * Prepare the request to the server to get all the data needed for this
 * page's content.
 *
 * @returns {Promise<void>} all the data received from the server.
 */
async function retrieveData() {
  // Options for the request to the database
  const options = {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      "Authorization": getSessionObject(STORED_TOKEN),
    },
  };
  // Request to get all information needed
  let res = await fetch(`/api/users/inscriptions`, options);
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
 * 'prenom', 'nom'.
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
        <th scope="col" class="text-end">Voir l'inscription</th>
      </tr>
    </thead>
    <tbody id="inscriptions-members-table-body"></tbody>
  `;
}

/**
 * Create a row in the tbody with the data from the request.
 *
 * @param data that contains all the required data.
 * @param tbody body of the table
 */
function insertRow(data, tbody) {
  let line = document.createElement('tr');
  line.innerHTML = `
    <td class="flex-md-nowrap">${data.username}</td>
    <td class="flex-md-nowrap">${data.surname}</td>
    <td class="flex-md-nowrap">${data.lastname}</td>
    <td class="text-end">
      <button class="btn btn-secondary flex-row" id="inscription-submit">
        <i class="bi-clipboard-plus"></i></button>
    </td>
  `;
  // insert the new line to the body of the table
  tbody.appendChild(line);
  // Create the action on click for the button
  document.getElementById('inscription-submit').addEventListener("click",
    () => {
      const id = data.idUser;
      Redirect(`/inscriptions/user?id=${id}`
      );
    });
}