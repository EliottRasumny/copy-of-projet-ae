import {getSessionObject} from "../../utils/session";
import {
  SORT_OPTION,
  STORED_TOKEN,
  USER_STATE_VALUE_EN,
  USER_STATE_VALUE_FR,
  USER_VERSION
} from "../../utils/const";
import {
  displayError,
  displayErrorNoStatus,
  displayWarning
} from "../../utils/feedback";
import {createLoadAnimation} from "../../utils/loading";
import {isRequestEmpty, RequestError} from "../../utils/checkRequest";
import {createBackToTopButton} from "../../utils/buttons";
import noImageIcon from "../../img/no_image_icon.svg";
import {Redirect} from "../Router/Router";
import {displayStateInFrench} from "../../utils/offerState";

/**
 * Render the UserProfileAdminViewPage and display the profile of a user with
 * all his informations. Furthermore it will display all it offers and the offers
 * that he has been taken.
 */
const UserProfileAdminViewPage = () => {
  var splitUrl = window.location.href.split('?');
  var searchParams = new URLSearchParams(splitUrl[1]);
  const idUser = searchParams.get('id');
  const pageDiv = document.querySelector("#page");
  pageDiv.className = "container";
  pageDiv.innerHTML = `
    <!-- Error feedback -->
    <div id="user-profile-feedback" class="m-3"></div>
    <!-- Members' inscription card -->
    <div class="card shadow mt-5" id="card">
      <!-- Card header -->
      <div class="card-header" id="card-header"></div>
      <!-- Card body -->
      <div class="card-body row p-5">
        <!-- Header title -->
        <h2 class="fw-bold mb-5 text-center">
          PROFIL
        </h2>
        <div id="user-profile-content" class="row"></div>
        <div id="user-profile-buttons" class="row mt-5"></div>
      </div>
    </div>
    <!-- All objects of the user -->
    <div class="mt-5">
      <!-- Title -->
      <h2 class="fw-bold mb-5 text-center">SES OBJETS OFFERTS</h2>
      <!-- All offers -->
      <div id="user-profile-objects"
        class="row row-cols-1 row-cols-md-4 g-4"></div>
      <!-- Error feedback -->
    <div id="user-profile-objects-feedback" class="m-3"></div>
    </div>
    <!-- All objects of the user -->
    <div class="mt-5">
      <!-- Title -->
      <h2 class="fw-bold mb-5 text-center">SES OBJETS RECUS</h2>
      <!-- All offers -->
      <div id="user-profile-objects-received"
        class="row row-cols-1 row-cols-md-4 g-4"></div>
      <!-- Error feedback -->
    <div id="user-profile-objects-feedback" class="m-3"></div>
    </div>
  `;
  // Create a 'back to top' button
  createBackToTopButton(pageDiv);
  // Get the table that will contain all the information
  const content = document.getElementById("user-profile-content");
  // Display a load animation while waiting for elements
  createLoadAnimation(content);
  const options = {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      "Authorization": getSessionObject(STORED_TOKEN),
    },
  };
  fetch(`/api/users/${idUser}`,
      options) // fetch return a promise => we wait for the response
  .then((response) => {
    if (!response.ok) {
      // Reset the table to remove the animation
      document.getElementById("card").innerHTML = "";
      displayWarning("Impossible de r&#233;cup&#233;rer les donn&#233;es.",
          'user-profile-feedback');
      throw new Error(
          "fetch error : " + response.status + " : " + response.statusText
      );
    }
    return response.json(); // json() return a promise => we wait for the response
  })
  .then((data) => {
    if (!isRequestEmpty(data,
        document.getElementById('card'),
        `Impossible de r&eacute;cup&eacute;rer les donn&eacute;es.`,
        'user-profile-feedback')) {
      insertData(data, content);
      insertButtons(data, document.getElementById("user-profile-buttons"));
      displayOffers(data.idUser);
      displayReceivedObjects(data.idUser);
    }
  })
  .catch((error) => {
    if (error instanceof RequestError) {
      displayError(error.status, error.message,
          document.getElementById('user-profile-feedback'));
    } else {
      displayErrorNoStatus(error,
          document.getElementById('user-profile-feedback'));
    }
  });
}

export default UserProfileAdminViewPage;

/**
 * Insert the buttons
 *
 * @param data contains the information of the user.
 * @param content in which the header needs to be inserted.
 */
function insertButtons(data, content) {
  // Insert a button to accept the inscription of a user
  if (data.state === USER_STATE_VALUE_EN.DENIED) {
    content.innerHTML = ` <button id="validate-member" type="submit"
      class="btn btn m-1 btn-secondary">
      Valider
    </button>`;
    let submit = document.getElementById("validate-member");
    submit.addEventListener("click", () =>
        validateMember(data.idUser, data.version));
  }
  // Set a button to allow the user being an administrator
  else if (data.role === "member") {
    // Display a button to set the user to 'unavailable'
    if (data.state !== USER_STATE_VALUE_EN.UNAVAILABLE) {
      content.innerHTML = `
        <button id="promote-member" type="submit"
          class="btn btn m-1 btn-secondary">
          Promouvoir
        </button>
        <button id="declare-unfit" type="submit"
          class="btn btn m-1 btn-secondary">
          D&#233;clarer indisponible
        </button>`;
      document.getElementById("declare-unfit").addEventListener("click", () =>
          declareUnavailable(data.idUser, data.version));
    } else {
      content.innerHTML = `<button id="promote-member" type="submit"
        class="btn btn m-1 btn-secondary">
        Promouvoir
      </button>`;
    }
    let promote = document.getElementById("promote-member");
    promote.addEventListener("click", () =>
        promoteMember(data.idUser, data.version));
  } else if (data.state !== USER_STATE_VALUE_EN.UNAVAILABLE) {
    content.innerHTML = `
    <button id="declare-unfit" type="submit"
            class="btn btn m-1 btn-secondary">
      D&#233;clarer indisponible
    </button>`;
    document.getElementById("declare-unfit").addEventListener("click", () =>
        declareUnavailable(data.idUser, data.version));
  }
}

/**
 * Declare that a user is unavailable.
 *
 * @param idUser the id of the user that is unavailable
 * @param version the version of the user
 */
async function declareUnavailable(idUser, version) {
  const options = {
    method: "PATCH", headers: {
      "Content-Type": "application/json",
      "Authorization": getSessionObject(STORED_TOKEN)
    },
  };
  await fetch(`/api/users/${idUser}/unavailable?${USER_VERSION}=${version}`,
      options)
  .then(() => {
    Redirect(`/users?id=${idUser}`);
  })
  .catch((error) => {
    if (error instanceof RequestError) {
      displayError(error.status, error.message,
          document.getElementById('user-profile-feedback'));
    } else {
      displayErrorNoStatus(error,
          document.getElementById('user-profile-feedback'));
    }
  });
}

/**
 * Promote a user.
 *
 * @param idUser the id of the user to be validate
 * @param version the version of the user
 */
async function promoteMember(idUser, version) {
  const options = {
    method: "PATCH", headers: {
      "Content-Type": "application/json",
      "Authorization": getSessionObject(STORED_TOKEN)
    },
  };
  await fetch(`/api/users/${idUser}/promote?${USER_VERSION}=${version}`,
      options)
  .then(() => {
    Redirect(`/users?id=${idUser}`);
  })
  .then((response) => {
    if (!response.ok) {
      throw new RequestError(response.status,
          "Impossible d'entreprendre l'action.");
    }
    Redirect(`/users?id=${idUser}`);
  })
  .catch((error) => {
    if (error instanceof RequestError) {
      displayError(error.status, error.message,
          document.getElementById('user-profile-feedback'));
    } else {
      displayWarning(error,
          document.getElementById('user-profile-feedback'));
    }
  });
}

/**
 * Validate a user.
 *
 * @param idUser the id of the user to be validate
 * @param version the version of the user
 */
async function validateMember(idUser, version) {
  const options = {
    method: "PATCH", headers: {
      "Content-Type": "application/json",
      "Authorization": getSessionObject(STORED_TOKEN)
    }, body: JSON.stringify({
      futurAdmin: false,
      userVersion: version,
    }),
  };
  await fetch(`/api/users/${idUser}/confirm`, options)
  .then(() => {
    Redirect(`/users?id=${idUser}`);
  })
  .then((response) => {
    if (!response.ok) {
      throw new RequestError(response.status,
          "Impossible d'entreprendre l'action.");
    }
    Redirect(`/users?id=${idUser}`);
  })
  .catch((error) => {
    if (error instanceof RequestError) {
      displayError(error.status, error.message,
          document.getElementById('user-profile-feedback'));
    } else {
      displayErrorNoStatus(error,
          document.getElementById('user-profile-feedback'));
    }
  });
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
    <div class="col-md-1">
      <strong>Pseudo</strong>
      <P>${data.username}</P>
    </div>
    <!-- Surname -->
    <div class="col-md-1">
      <strong>Pr&eacute;nom</strong>
      <P>${data.surname}</P>
    </div>
    <!-- Lastname -->
    <div class="col-md-1">
      <strong>Nom</strong>
      <P>${data.lastname}</P>
    </div>
    <!-- Phone Number -->
    <div class="col-md-2">
      <strong>Num&eacute;ro de t&eacute;l&eacute;phone</strong>
      <P id="user-profile-phone-number"></P>
    </div>
    <!-- Address -->
    <div class="col-md-auto">
      <strong>Adresse</strong>
      <P id="user-profile-address"></P>
    </div>
    <!-- Role -->
    <div class="col-md-auto">
      <strong>Role</strong>
      <P id="user-profile-role"></P>
    </div>
    <!-- State -->
    <div class="col-md-auto">
      <strong>&Eacute;tat</strong>
      <P id="user-profile-state"></P>
    </div>
    <!-- If the user is refused, display the refusal reason-->
    <div class="col-md-12" id="user-profile-refusal-reason"></div>
  `;
  // Display correctly the address if there is a unit number or not
  if (data.address.unitNumber !== null) {
    document.getElementById('user-profile-address').innerHTML = `
      ${data.address.street + ` ` + data.address.buildingNumber +
    ` bte ` + data.address.unitNumber + `, `
    + data.address.commune + ` ` + data.address.postcode}
    `;
  } else {
    document.getElementById('user-profile-address').innerHTML = `
      ${data.address.street + ` ` + data.address.buildingNumber + `, `
    + data.address.commune + ` ` + data.address.postcode}
    `;
  }
  // Display correctly the phone number if it is null or not
  if (data.phoneNumber === null) {
    document.getElementById('user-profile-phone-number')
        .innerText = "/";
  } else {
    document.getElementById('user-profile-phone-number')
        .innerText = data.phoneNumber;
  }
  // Display the role of the user in French
  if (data.role === "admin") {
    document.getElementById('user-profile-role').innerText = "Administrateur";
  } else if (data.role === "member") {
    document.getElementById('user-profile-role').innerText = "Membre";
  } else if (data.role === null) {
    document.getElementById('user-profile-role').innerText = "En attente";
  }
  // If the user is refused, display the reason
  if (data.refusalReason !== null) {
    document.getElementById('user-profile-refusal-reason').innerHTML = `
      <strong>Raison de refus : </strong>
      <P>${data.refusalReason}</P>
    `;
    document.getElementById('user-profile-role').innerText = "/";
  }
  // Display the state of the user in French
  if (data.state === USER_STATE_VALUE_EN.DENIED) {
    document.getElementById(
        'user-profile-state').innerHTML = USER_STATE_VALUE_FR.DENIED;
  } else if (data.state === USER_STATE_VALUE_EN.REGISTERED) {
    document.getElementById(
        'user-profile-state').innerHTML = USER_STATE_VALUE_FR.REGISTERED;
  } else if (data.state === USER_STATE_VALUE_EN.VALID) {
    document.getElementById(
        'user-profile-state').innerHTML = USER_STATE_VALUE_FR.VALID;
  } else if (data.state === USER_STATE_VALUE_EN.UNAVAILABLE) {
    document.getElementById(
        'user-profile-state').innerHTML = USER_STATE_VALUE_FR.UNAVAILABLE;
  }
}

/**
 * Send a request to get all the offered objects in a list and displays them.
 */
function displayOffers(idUser) {
  // create a holder for all cards
  const cardHolder = document.getElementById('user-profile-objects');
  // Display a loading spinner
  cardHolder.className = "mt-5";
  cardHolder.innerHTML = "";
  createLoadAnimation(cardHolder);
  // Prepare the request
  const options = {
    method: "GET",
    headers: {
      "Authorization": getSessionObject(STORED_TOKEN),
    },
  };
  // Send the request
  fetch(
      `/api/offers?sort=${SORT_OPTION.DATE_DESC}&filter-value=${idUser}&filter-type=user`,
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
    // reset the card holder
    cardHolder.innerHTML = "";
    cardHolder.className = "row row-cols-1 row-cols-md-4 g-4";
    requests.forEach((request) => {
      insertOffer(cardHolder, request, getSessionObject(STORED_TOKEN));
    });
  })
  .catch((err) => {
    // reset the card holder
    cardHolder.innerHTML = "";
    if (err instanceof RequestError) {
      displayError(err.status, err.message, 'user-profile-objects-feedback');
    } else {
      displayErrorNoStatus(err, 'user-profile-objects-feedback');
    }
  });
}

/**
 * Send a request to get all the offered objects in a list and displays them.
 */
function displayReceivedObjects(idUser) {
  // create a holder for all cards
  const cardHolder = document.getElementById('user-profile-objects-received');
  // Display a loading spinner
  cardHolder.className = "mt-5";
  cardHolder.innerHTML = "";
  createLoadAnimation(cardHolder);
  // Prepare the request
  const options = {
    method: "GET",
    headers: {
      "Authorization": getSessionObject(STORED_TOKEN),
    },
  };
  // Send the request
  fetch(
      `/api/offers?sort=${SORT_OPTION.DATE_DESC}&filter-value=${idUser}&filter-type=received`,
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
    // reset the card holder
    cardHolder.innerHTML = "";
    cardHolder.className = "row row-cols-1 row-cols-md-4 g-4";
    requests.forEach((request) => {
      insertOffer(cardHolder, request, getSessionObject(STORED_TOKEN));
    });
  })
  .catch((err) => {
    // reset the card holder
    cardHolder.innerHTML = "";
    if (err instanceof RequestError) {
      displayError(err.status, err.message, 'user-profile-objects-feedback');
    } else {
      displayErrorNoStatus(err, 'user-profile-objects-feedback');
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
