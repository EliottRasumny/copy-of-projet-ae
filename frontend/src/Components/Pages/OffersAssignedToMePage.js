import noImageIcon from "../../img/no_image_icon.svg";
import {displayError, displayWarning} from "../../utils/feedback";
import {getSessionObject} from "../../utils/session";
import {SORT_OPTION, STORED_TOKEN} from "../../utils/const";
import {Redirect} from "../Router/Router";
import {createBackToTopButton} from "../../utils/buttons";
import {createLoadAnimation} from "../../utils/loading";
import {isRequestEmpty} from "../../utils/checkRequest";

/**
 * Render the OffersAssignedPage and display all the offers that the current user
 * has been chosen as recipient or given
 */
const OffersAssignedToMePage = () => {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = `
    <!-- All cards to display offer -->
    <div class="container mt-5 mb-5">
      <!-- Sort options -->
     <div class="mb-5" id="myoffers-state-options">
        <p><strong>&Eacute;tat : </strong></p>
          <select class="form-select" id="myoffers-state-select">
            <option value="given" class="option">
              Recu
            </option>
            <option value="assigned" class="option">
              Assign&eacute;
            </option>
          </select>
      </div>
      <!-- No data feedback --> 
      <div id="offers-feedback-empty" class="m-3"></div>
      <div class="mb-5" id="myoffers-sort-options">
        <p><strong>Tri : </strong></p>
        <select class="form-select" id="myoffers-sort-select">
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
  // Load offers
  insertOffers(SORT_OPTION.DATE_DESC); // By default
  // Enable sort options if connected
  setUpSelectOptions(getSessionObject(STORED_TOKEN));
  //button 'backToTop'
  createBackToTopButton(pageDiv);
};

export default OffersAssignedToMePage;

/**
 * Send a request to get all the offered objects in a list and displays them.
 *
 * @param sort the option to sort all the offers
 */
function insertOffers(sort) {
  // Display a load animation while waiting for elements
  const cardHolder = document.getElementById('home-card-holder');
  const state = document.getElementById('myoffers-state-select');
  // Display a loading spinner
  cardHolder.className = "mt-5";
  cardHolder.innerHTML = "";
  createLoadAnimation(cardHolder);
  const options = {
    method: "GET",
    headers: {
      "Authorization": getSessionObject(STORED_TOKEN)
    },
  };
  fetch(
      `/api/offers?sort=${sort}&filter-type=given+assigned&filter-value=${state.value}`,
      options).then(
      (response) => {
        if (!response.ok) {
          // reset the card holder
          cardHolder.innerHTML = "";
          displayError(
              response.status,
              "Impossible de r&#233;cup&#233;rer les donn&#233;es.",
              'home-feedback'
          );
          throw new Error(
              "fetch error : " + response.status + " : " + response.statusText
          );
        }
        return response.json();
      })
  .then((requests) => {
    // reset all cards before creating new ones
    cardHolder.innerHTML = "";
    // if 'dataResponse' is empty, then, display a message to inform the user,
    // proceed otherwise.
    if (!isRequestEmpty(requests,
        document.getElementById('myoffers-sort-options'),
        `Aucun objet &agrave; afficher`,
        'offers-feedback-empty')) {
      document.getElementById('offers-feedback-empty').innerHTML = "";
      cardHolder.className = "row row-cols-1 row-cols-md-4 g-4";
      requests.forEach((request) => {
        insertOffer(cardHolder, request, getSessionObject(STORED_TOKEN));
      });
    }
  })
  .catch((error) => {
    // Reset the table to remove the animation
    document.getElementById('home-card-holder').innerHTML = "";
    displayWarning("Impossible de r&#233;cup&#233;rer les donn&#233;es.",
        'home-feedback');
    console.error("HomePage::error: ", error);
  });
}

/**
 * Create a card that contains all the information about the offer and object.
 *
 * @param cardHolder the div that contains the card.
 * @param request the data off the offer and object.
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
  img.className = "card-img-top w-60";
  card.appendChild(img);
  // Create the body of the card
  let body = document.createElement('div');
  body.className = "card-body";
  // Create the offer's description and insert it into the body
  let description = document.createElement('p');
  description.innerHTML = request.object.description;
  description.className = "card-text text-truncate ms-2 mt-3 mb-4";
  card.appendChild(description);
  // Options for authenticated users
  if (authenticated) {
    // Let the card be completely clickable to be more user's friendly.
    card.addEventListener("click", () => {
      var id = request.idOffer;
      Redirect(`/offer?id=${id}`);
    });
    //change the mouse pointer while the cursor is on the element
    card.addEventListener("pointerover", () => {
      card.style = "cursor:pointer";
    });
    // Add the offered date to the card description
    let date = document.createElement('p');
    date.innerHTML = `<strong>Date : </strong>${request.date}`;
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
    // Create a specific button to see the offer. Even if all the card is
    // clickable, it is more ergonomic this way.
    let buttonDiv = document.createElement("div");
    buttonDiv.className = "mt-3 text-center";
    buttonDiv.innerHTML = `
      <input id="button" class="btn btn-sm btn-secondary"
        value="Voir les d&#233;tails">
    `;
    card.appendChild(buttonDiv);
  }
  wrapper.appendChild(body);
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
    let all = document.getElementById("myoffers-sort-options");
    all.style.display = "none";
  } else {
    // When there is a change of value for the sort-select element, make a
    // request to the server to get elements in a new sort.
    let submit = document.getElementById("myoffers-sort-select");
    let submitState = document.getElementById("myoffers-state-select");
    submit.addEventListener("change", () => {
      insertOffers(submit.value,)
    });
    submitState.addEventListener("change", () => {
      insertOffers(submit.value,)
    });
  }
}
