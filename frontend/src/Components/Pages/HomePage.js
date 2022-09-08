import noImageIcon from "../../img/no_image_icon.svg";
import {displayError, displayErrorNoStatus} from "../../utils/feedback";
import {getSessionObject, getUserId} from "../../utils/session";
import {OBJECT_FILTER_TYPE, SORT_OPTION, STORED_TOKEN} from "../../utils/const";
import {Redirect} from "../Router/Router";
import {createBackToTopButton} from "../../utils/buttons";
import {createLoadAnimation} from "../../utils/loading";
import {isRequestEmpty, RequestError} from "../../utils/checkRequest";
import {displayStateInFrench} from "../../utils/offerState";

// Create 3 arrays that will contain key values to make research
// on the offers. These arrays will keep every data from the start, but only
// once.
const dataOptionsLastnameList = [];
const dataOptionsTypeList = [];
const dataOptionsStateList = [];

/**
 * Render the HomePage and display all the current available offered objects.
 *
 * Those offers will be sorted depending on the authentication of the user.
 * If the user is authenticated, he will be able to navigate through
 * different sorts, by clicking on buttons.
 * If the user is not authenticated, he will only see the offers sorted by the
 * latest published.
 * If the user is authenticated, he will be allowed to click on the offers to
 * see their details. Furthermore, he will be able to see more information
 * directly on the home page for each offer such as : the published date and the
 * type of the offered object.
 */
const HomePage = () => {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = `
    <!-- All cards to display offer -->
    <!-- Research all members-->
    <div class="container mt-5 mb-5">
      <div id="home-notification" class="container"></div>
      <div class="input-group mb-3" id="home-research-objects">
        <datalist id="home-research-datalistOptions"></datalist>
        <input type="text" class="form-control" id="home-research-value"
          aria-label="Rechercher un object par"
          placeholder="Rechercher un object par"
          aria-describedby="button-addon2"
          list="home-research-datalistOptions">
        <select class="form-select" id="home-research-type" aria-label="Default select example">
          <option selected value="${OBJECT_FILTER_TYPE.BY_NAME}">
            Nom du donneur</option>
          <option value="${OBJECT_FILTER_TYPE.BY_TYPE}">Type</option>
          <option value="${OBJECT_FILTER_TYPE.BY_STATE}">&Eacute;tat</option>
          <option value="${OBJECT_FILTER_TYPE.BY_DATE}">Date</option>
        </select>
        <button class="btn btn-outline-secondary" type="button" id="home-submit-research-objects">Rechercher</button>
      </div>
      <div id="home-date-inputs" class="row mb-3">
        <div class="col-md-6">
          <label for="home-date-from"
            class="form-label">Post&eacute;es depuis : </label>
          <input class="form-control" type="date" id="home-research-date-from">
        </div>
        <div class="col-md-6">
          <label for="home-date-from"
            class="form-label">Post&eacute;es jusqu'&agrave; : </label>
          <input class="form-control col-md-5" type="date"
            id="home-research-date-to">
        </div>
      </div>
      <!-- Sort options -->
      <div class="mb-5" id="home-sort-options">
        <p><strong>Tri : </strong></p>
        <select class="form-select" id="home-sort-select">
          <option selected value="${SORT_OPTION.DATE_DESC}" class="option">
            Plus r&eacute;cent au plus ancien
          </option>
          <option value="${SORT_OPTION.DATE_ASC}">
            Plus ancien au plus r&eacute;cent
          </option>
          <option value="${SORT_OPTION.INTERESTS_DESC}">
            Plus d'int&eacute;rets &agrave; moins d'int&eacute;rets
          </option>
          <option value="${SORT_OPTION.INTERESTS_ASC}">
            Moins d'int&eacute;rets &agrave; plus d'int&eacute;rets
          </option>
          <option value="${SORT_OPTION.TYPE_ASC}">
            Type par ordre alphab&eacute;tique
          </option>
          <option value="${SORT_OPTION.TYPE_DESC}">
            Type par ordre alphab&eacute;tique inverse
          </option>
        </select>
      </div>
      <!-- Error feedback --> 
      <div id="home-feedback" class="m-3"></div>
      <!-- All cards to display offer -->
      <div id="home-card-holder" class="row row-cols-1 row-cols-md-4 g-4"></div>
    </div>
  `;
  //button 'backToTop'
  createBackToTopButton(pageDiv);
  // Load offers with sort option by default
  insertOffers(SORT_OPTION.DATE_DESC);
  // load notification
  if (getSessionObject(STORED_TOKEN)) {
    insertNotifications();
  }
  // Enable sort options if connected
  setUpSelectOptions(getSessionObject(STORED_TOKEN));
};

export default HomePage;

/**
 * Send a request to get all the offered objects in a list and displays them.
 *
 * @param sort the option to sort all the offers
 */
function insertOffers(sort) {
  // create a holder for all cards
  const cardHolder = document.getElementById('home-card-holder');
  // Display a load animation while waiting for elements
  createLoadAnimation(cardHolder);
  cardHolder.className = "mt-5";
  let researchValue = document.getElementById('home-research-value').value;
  let researchType = document.getElementById('home-research-type').value;
  createLoadAnimation(cardHolder);
  // Prepare the request
  const options = {
    method: "GET", headers: {
      "Authorization": getSessionObject(STORED_TOKEN),
    },
  };
  if (researchType === OBJECT_FILTER_TYPE.BY_DATE) {
    let dateFrom = document.getElementById("home-research-date-from").value;
    let dateTo = document.getElementById("home-research-date-to").value;
    researchValue = dateFrom + '_' + dateTo;
  }
  // Send the request
  fetch(
    `/api/offers?sort=${sort}&filter-value=${researchValue}&filter-type=${researchType}`,
    options)
  .then((response) => {
    if (!response.ok) {
      throw new RequestError(response.status,
        "Impossible de r&#233;cup&#233;rer les donn&#233;es.");
    }
    return response.json();
  })
  .then((requests) => {
    // reset the card holder
    cardHolder.innerHTML = "";
    // if 'dataResponse' is empty, then, display a message to inform the user,
    // proceed otherwise.
    if (!isRequestEmpty(requests,
      cardHolder,
      `Aucun objet &agrave; afficher`,
      'home-feedback')) {
      document.getElementById('home-feedback').innerHTML = "";
      cardHolder.className = "row row-cols-1 row-cols-md-4 g-4";
      requests.forEach((request) => {
        insertOffer(cardHolder, request, getSessionObject(STORED_TOKEN));
      });
      // Insert meaningful values to the datalistOption for the research
      insertDataOptionSearch();
    }
  })
  .catch((err) => {
    // reset the card holder
    cardHolder.innerHTML = "";
    if (err instanceof RequestError) {
      displayError(err.status, err.message, 'home-feedback');
    } else {
      displayErrorNoStatus(err, 'home-feedback');
    }
  });
}

/**
 * Create a card that contains all the information about the offer and object.
 *
 * @param cardHolder the div that will contain the card.
 * @param request the data of the offer and object.
 * @param authenticated true if actual user is connected, false otherwise.
 */
function insertOffer(cardHolder, request, authenticated) {
  // Create the wrapper for the card that will contain all the information
  let wrapper = document.createElement('div');
  wrapper.className = "col";
  cardHolder.appendChild(wrapper);
  // Create the card's aesthetic. Putting shadow, round corners etc.
  let card = document.createElement('div');
  card.id = 'card' + request.idOffer;
  card.className = "card h-100 shadow rounded-3 position-relative";
  wrapper.appendChild(card);
  // Insert the picture of the object, if there is one. Otherwise, insert
  // the 'no image', by default image.
  let img = document.createElement('img');
  if (request.object.pictureName === null) {
    img.src = noImageIcon;
    img.alt = "Pas d'image pour cette offre."
  } else {
    const options = {
      method: "GET",
    };
    // Send the request
    fetch(`/api/objects/${request.object.pictureName}/picture`, options)
    .then(response => response.blob())
    .then(function (myBlob) {
      img.src = URL.createObjectURL(myBlob);
      img.alt = "Image illustrant l'objet d&#233;crit."
    });
  }
  img.className = "card-img-top w-51";
  card.appendChild(img);
  // Create the body of the card
  let body = document.createElement('div');
  body.className = "card-body";
  // Create the offer's description and insert it into the body
  let description = document.createElement('p');
  description.innerHTML = request.object.description;
  description.className = "card-text text-truncate ms-2 mb-5 mt-3";
  card.appendChild(description);
  // Options for authenticated users
  if (authenticated) {
    // Insert meaningful values for data research
    if (!dataOptionsStateList.includes(request.object.state)) {
      dataOptionsStateList.push(request.object.state);
    }
    if (!dataOptionsTypeList.includes(request.object.type.label)) {
      dataOptionsTypeList.push(request.object.type.label);
    }
    if (!dataOptionsLastnameList.includes(request.object.offeror.lastname)) {
      dataOptionsLastnameList.push(request.object.offeror.lastname);
    }
    // Let the card be completely clickable to be more user's friendly.
    card.addEventListener("click", () => {
      var id = request.idOffer;
      Redirect(`/offer?id=${id}`);
    });
    //change the mouse pointer while the cursor is on the element
    card.addEventListener("pointerover", () => {
      card.style = "cursor:pointer";
    });
    // Display if the user is interested or not by adding an icon on top off the
    // offer.
    // If interested in, the icon will be filled. Otherwise, it will be empty.
    let myInterest = document.createElement('span');
    myInterest.id = "home-my-interest-icon";
    myInterest.className = "position-absolute top-0 start-50"
      + " translate-middle";
    if (request.myInterest) {
      myInterest.className += " bi-bookmark-heart-fill";
    } else {
      myInterest.className += " bi-bookmark-heart";
    }
    myInterest.style.fontSize = "4vh";
    card.appendChild(myInterest);
    // Add the username of the offeror
    let lastnameOfferor = document.createElement('p');
    lastnameOfferor.innerHTML = `<strong>De : </strong>`
      + request.object.offeror.lastname;
    lastnameOfferor.className = "card-text text-truncate ms-2";
    card.appendChild(lastnameOfferor);
    // Add the offered date to the card description
    let date = document.createElement('p');
    date.innerHTML = `<strong>Date : </strong>` + request.date;
    date.className = "card-text text-truncate ms-2";
    card.appendChild(date);
    // Add the type of the object to the card description
    let type = document.createElement('p');
    type.innerHTML = `<strong>Type : </strong>` + request.object.type.label;
    type.className = "card-text text-truncate ms-2";
    card.appendChild(type);
    // Add the number of interests
    let nbInterest = document.createElement('p');
    nbInterest.innerHTML = `<strong>Membres interess&eacute;s : </strong>
      ${request.nbrInterests}`;
    nbInterest.className = "card-text text-truncate ms-2";
    card.appendChild(nbInterest);
    // Add the state of the object
    let stateObject = document.createElement('p');
    // Get the French translation of the state object
    let stateFr = displayStateInFrench(request.object.state);
    stateObject.innerHTML = `<strong>&Eacute;tat : </strong>${stateFr}`;
    stateObject.className = "card-text text-truncate ms-2";
    card.appendChild(stateObject);
    // display a message if the user of the offer is not available right now
    if (request.state === "unavailable") {
      let availability = document.createElement('p');
      availability.className = "card-text text-truncate ms-2 fst-italic text-danger";
      availability.innerHTML = `Offreur actuellement indisponible.`;
      card.appendChild(availability);
    }
    // Create a specific button to see the offer. Even if all the card is
    // clickable, it is more ergonomic this way.
    let buttonDiv = document.createElement("div");
    buttonDiv.className = "mt-3 text-center";
    buttonDiv.innerHTML = `
      <input id="button" class="btn btn-sm btn-secondary mb-3"
        value="Voir les d&#233;tails">
    `;
    card.appendChild(buttonDiv);
  }
  wrapper.appendChild(body);
}

/**
 * Insert in the datalist, all the options corresponding with the selected type
 * of research.
 */
function insertDataOptionSearch() {
  let datalist = document.getElementById('home-research-datalistOptions');
  datalist.innerHTML = ``;
  // by default
  dataOptionsLastnameList.forEach(option => {
    datalist.innerHTML += `<option value="${option}"></option>`;
  });
  // Create an event listener to change the datalist when the type of search
  // selected is different
  let selectResearchType = document.getElementById('home-research-type');
  selectResearchType.addEventListener("change", () => {
    // Reset the content
    datalist.innerHTML = ``
    // Insert option and their values
    if (selectResearchType.options[selectResearchType.selectedIndex].text
      === "Type") {
      dataOptionsTypeList.forEach(option => {
        datalist.innerHTML += `\n<option value="${option}"></option>`;
      });
    } else if (selectResearchType.options[selectResearchType.selectedIndex].text
      === "État") {
      dataOptionsStateList.forEach(option => {
        datalist.innerHTML += `<option value="${option}"></option>`;
      });
    } else if (selectResearchType.options[selectResearchType.selectedIndex].text
      === "Nom d'utilisateur du donneur") {
      dataOptionsLastnameList.forEach(option => {
        datalist.innerHTML += `<option value="${option}"></option>`;
      });
    }
  });
}

/**
 * Create all the options to properly sort and filter all the offers.
 *
 * Get the select elements created for the sort and filter choices, let them be
 * clickable.
 * When the value of the sort-select change, send a request to the server to
 * get all offers in a new sort.
 * This option is only available to authenticated users. Thus, selects options
 * won't be displayed when the user is not authenticated.
 *
 * @param authentication true if the user is authenticated, false otherwise.
 */
function setUpSelectOptions(authentication) {
  // Do not display sort and filter actions if not authenticated and do not
  // create actions for them.
  if (!authentication) {
    let research = document.getElementById("home-research-objects");
    research.style.display = "none";
    let all = document.getElementById("home-sort-options");
    all.style.display = "none";
    document.getElementById('home-date-inputs').style.display = "none";
  } else {
    // When there is a change of value for the sort-select element, make a
    // request to the server to get elements in a new sort.
    let submit = document.getElementById("home-sort-select");
    submit.addEventListener("change",
      () => {
        // Display a load animation while waiting for elements
        createLoadAnimation(document.getElementById('home-card-holder'));
        // make the request to the server
        insertOffers(submit.value)
      });
    // when the user click on the research button
    document.getElementById("home-submit-research-objects")
    .addEventListener("click", () => {
      // Display a load animation while waiting for elements
      createLoadAnimation(document.getElementById('home-card-holder'));
      // Make the request to the server
      insertOffers(submit.value);
    });
  }
}

/**
 * Insert a notification at the top of the page is there is one that has been
 * read yet.
 */
function insertNotifications() {
  //create notification holder
  const notifHolder = document.getElementById('home-notification');
  const options = {
    method: "GET",
    headers: {
      "Authorization": getSessionObject(STORED_TOKEN),
    },
  };
  // Send the request
  fetch(
    `/api/interests/notification`,
    options).then(
    (response) => {
      if (!response.ok) {
        throw new RequestError(response.status,
          "Impossible de r&#233;cup&#233;rer les donn&#233;es."
        );
      }
      return response.json();
    })
  .then((requests) => {
    if (requests.length !== 0) {
      createNotificationCard(notifHolder);
    }
    requests.forEach((request) => {
      insertRow(document.getElementById("home-notifications-table"), request)
    });
  })
  .catch((err) => {
    if (err instanceof RequestError) {
      displayError(err.status, err.message, 'home-feedback');
    } else {
      displayErrorNoStatus(err, 'home-feedback');
    }
  });

}

/**
 * Insert a row with a text in the table body with the data from the request.
 *
 * @param tableBody in which the row need to be inserted.
 * @param request the data to insert
 */
function insertRow(tableBody, request) {
  const line = document.createElement("tr");
  // Create username column
  const textCell = document.createElement("th");
  textCell.scope = "row";
  let text;
  if (request.interestedMember.idUser === getUserId()) {
    text = "Vous avez été choisi comme receveur pour l'object : "
  } else {
    text = "Quelqu'un est interessé par votre object : "
  }
  text += `${request.offer.object.description}`;
  textCell.innerText = text;
  line.appendChild(textCell);
  tableBody.appendChild(line);
}

/**
 * Create a card that contains a table with all the notifications.
 *
 * @param div the HTML element that contains the card.
 */
function createNotificationCard(div) {
  div.innerHTML = `
    <!-- Notifications card -->
    <div class="card shadow mb-5" id="card">
      <!-- Card header -->
      <div class="card-header" id="card-header"></div>
      <!-- Card body -->
      <div class="card-body">
        <!-- Header title -->
        <h2 class="fw-bold mb-5 text-center">
          NOTIFICATIONS
        </h2>
        <table class="table table-hover">
          <thead>
            <tr>
              <th scope="col"></th>
            </tr>
          </thead>
          <tbody id="home-notifications-table"></tbody>
        </table>
      </div>
    </div>
  `;
}