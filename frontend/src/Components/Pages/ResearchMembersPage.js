import {createBackToTopButton} from "../../utils/buttons";
import {createLoadAnimation} from "../../utils/loading";
import {displayError, displayErrorNoStatus} from "../../utils/feedback";
import {getSessionObject} from "../../utils/session";
import {Redirect} from "../Router/Router";
import {MEMBER_FILTER_TYPE, STORED_TOKEN} from "../../utils/const";
import {isRequestEmpty, RequestError} from "../../utils/checkRequest";

/**
 * Render the ResearchMembersPage and display all the members on the website.
 * The admin can use filters to show some users
 */
const ResearchMembersPage = () => {
  const pageDiv = document.querySelector("#page");
  pageDiv.className = "container";
  pageDiv.innerHTML = `
    <!-- Title -->
    <h2 class="fw-bold text-center my-5">RECHERCHE D'UN MEMBRE</h2>
    <!-- Members research -->
    <div class="container mt-5 mb-5">
      <div class="input-group mb-3" id="member-research">
        <input type="text" class="form-control" id="member-research-value" aria-label="Rechercher un membre par" placeholder="Rechercher un membre par" aria-describedby="button-addon2">
        <select class="form-select" id="member-research-type" aria-label="Default select example">
          <option selected value="${MEMBER_FILTER_TYPE.BY_LASTNAME}">Nom</option>
          <option value="${MEMBER_FILTER_TYPE.BY_POSTCODE}">Code Postale</option>
          <option value="${MEMBER_FILTER_TYPE.BY_COMMUNE}">Commune</option>
          // TODO rechercher la date
        </select>
        <button class="btn btn-outline-secondary" type="button" id="member-research-submit">Rechercher</button>
      </div>
    <!-- Error feedback --> 
    <div id="research-members-feedback"></div>
    <!-- Research members' card -->
    <div class="card shadow" id="card">
      <!-- Card header -->
      <div class="card-header" id="card-header"></div>
      <!-- Card body -->
      <div class="card-body row p-5">
        <table id="research-members-table" class="table table-hover"></table>
      </div>
    </div>
  `;
  // Create a 'back to top' button
  createBackToTopButton(pageDiv);
  // Action on search
  const button = document.getElementById("member-research-submit");
  button.addEventListener("click",
    () => retrieveData());
  // Make the request to the server to get all the data needed for the content
  (retrieveData)();
}
export default ResearchMembersPage;

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
    }
  };
  let researchValue = document.getElementById("member-research-value").value;
  let researchType = document.getElementById("member-research-type").value;
  if (!document.getElementById("research-members-table")) {
    document.getElementById('card').innerHTML += `
      <!-- Card header -->
      <div class="card-header" id="card-header"></div>
      <!-- Card body -->
      <div class="card-body row p-5">
        <table id="research-members-table" class="table table-hover"></table>
      </div>
    `;
    document.getElementById('research-members-feedback').innerHTML = "";
  }
  // Get the table that will contain all the information
  let table = document.getElementById("research-members-table");
  // Display a load animation on the page while waiting for elements
  createLoadAnimation(table);
  // Request to get all information needed
  await fetch(
    `/api/users?filter-value=${researchValue}&filter-type=${researchType}`,
    options).then(
    (response) => {
      if (!response.ok) {
        throw new RequestError(response.status,
          "Impossible de r&#233;cup&#233;rer les donn&#233;es."
        );
      }
      return response.json();
    })
  .then((dataResponse) => {
    // if 'dataResponse' is empty, then, display a message to inform the user,
    // proceed otherwise.
    if (!isRequestEmpty(dataResponse,
      document.getElementById('card'),
      `Aucun membre &agrave; afficher`,
      'research-members-feedback')) {
      // Create all the table's header
      createTableContent(table);
      // deal with data rows for tbody
      const tbody = document.getElementById("research-members-table-body");
      // Insert all the users in the row of the table
      dataResponse.forEach((data) => {
        insertRow(data, tbody);
      });
    }
  })
  .catch((err) => {
    // Reset the table to remove the animation
    table.innerHTML = "";
    if (err instanceof RequestError) {
      displayError(err.status, err.message,
        'research-members-feedback');
    } else {
      displayErrorNoStatus(err, 'research-members-feedback');
    }
  });
}

/**
 * Create the header and the tbody element of the table with fields : 'pseudo',
 * 'prenom', 'nom', 'voir le profil'.
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
        <th scope="col" class="flex-md-nowrap">Commune</th>
        <th scope="col" class="flex-md-nowrap">Code postal</th>
        <th scope="col" class="text-end">Voir le profil</th>
      </tr>
    </thead>
    <tbody id="research-members-table-body"></tbody>
  `;
}

/**
 * Insert a row in the table body with the data from the data. Fields are :
 * 'pseudo', 'prenom', 'nom', 'Voir le profil'.
 *
 * @param tbody in which the row need to be inserted.
 * @param data that contains all the required data.
 */
function insertRow(data, tbody) {
  let line = document.createElement('tr');
  line.innerHTML = `
    <td class="flex-md-nowrap"><strong>${data.username}</strong></td>
    <td class="flex-md-nowrap">${data.surname}</td>
    <td class="flex-md-nowrap">${data.lastname}</td>
    <td class="flex-md-nowrap">${data.address.commune}</td>
    <td class="flex-md-nowrap">${data.address.postcode}</td>
    <td class="text-end">
      <button class="btn btn-secondary flex-row"
        id="submit-research-members${data.idUser}">
        <i class="bi-clipboard-plus" id="see-more"></i></button>
    </td>
  `;
  // insert the new line to the body of the table
  tbody.appendChild(line);
  // Create the action on click for the button
  let button = document.getElementById(`submit-research-members${data.idUser}`);
  button.addEventListener("click", () => submitForm(data));
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
 * Goes to the profile of the member
 *
 * Throws an exception and display an error message if there is a problem with
 * the request or the response.
 * Reload the page to see the changes if there is no problem.
 *
 * @param request the data that need to be sent to the database.
 */
async function submitForm(request) {
  var id = request.idUser
  if (request.state === "registered") {
    Redirect(`/inscriptions/user?id=${id}`);
  } else {
    Redirect(`/users?id=${id}`);
  }
}


