import {getSessionObject, getUserId} from "../../utils/session";
import {displayError, displayWarning} from "../../utils/feedback";
import {STORED_TOKEN, STORED_USER, VERSION} from "../../utils/const";
import {createLoadAnimation} from "../../utils/loading";
import {Redirect} from "../Router/Router";
import {buttonStartLoading, createBackToTopButton} from "../../utils/buttons";
import noImageIcon from "../../img/no_image_icon.svg";
import {displayStateInFrench} from "../../utils/offerState";
import {RequestError} from "../../utils/checkRequest";

/**
 * Render the OfferDetailsPage.
 *
 * Display the chosen object's informations.
 * If the user is the offeror he can modify the object's information and sees
 * the all the members that are interested in his offer. Choose one of them
 * to be the recipient and then mark if he has come or not.
 *
 * If the user is not the offeror he can mark his interest if not done yet.
 *
 */
const OfferDetailsPage = () => {
  // Get the id of the offer from the url
  var splitUrl = window.location.href.split('?');
  var searchParams = new URLSearchParams(splitUrl[1]);
  const idOffer = searchParams.get('id');
  // Get the id of the current user.
  const idMember = getSessionObject(STORED_USER).idUser;
  // Create all the structure of the page
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = `
    <div id="load-animation" class="mt-5"></div>
    <div class="container mt-5" id="offer-object">
      <!-- Error feedback -->
      <div id="offer-details-feedback" class="m-3"></div>
      <!-- Object card -->
      <div class="card shadow mb-3" id="card">
        <!-- Card header -->
        <div class="card-header" id="card-header"></div>
        <!-- Card body -->
        <div class="card-body row p-5" id="offer-object-card-body"></div>
      </div>
      <!-- Display interested members if there is at least one -->
      <div id="offer-details-interested-members-feedback"></div>
      <div id="offer-details-interested-members"></div>
    </div>
  `;
  // get the object card body
  let objectCardBody = document.getElementById('offer-object-card-body');
  // Create a 'back to top' button
  createBackToTopButton(pageDiv);
  // Display a load animation while waiting for elements
  createLoadAnimation(objectCardBody);
  // Value of the object's id
  let idObject;
  const options = {
    method: "GET", headers: {
      "Content-Type": "application/json",
      "Authorization": getSessionObject(STORED_TOKEN),
    },
  };
  fetch(`api/offers/${idOffer}`,
      options) // fetch return a promise => we wait for the response
  .then((response) => {
    if (!response.ok) {
      // Reset the table to remove the animation
      document.getElementById('load-animation').innerHTML = "";
      document.getElementById('card').innerHTML = '';
      throw new RequestError(response.status,
          "Impossible de récupérer l'offre");
    }
    return response.json(); // json() return a promise => we wait for the response

  })
  .then((request) => {
    try {
      // Load the object's card body
      loadObjectCard(objectCardBody, request);
      // Save the object's id
      idObject = request.object.idObject;
      // Get the elements that will contain the description and the time slot
      let objectTimeSlot = document.getElementById('offer-details-time-slot');
      let objectDescription = document.getElementById(
          'offer-details-description');
      // Display certain information if the current user IS NOT the offeror
      if (request.object.offeror.idUser !== getUserId()) {
        if (request.object.recipient != null
            && request.object.recipient.idUser === getUserId()) {
          displayRecipientEvaluationCard(request, objectTimeSlot,
              objectDescription);
        } else if (request.object.state !== "canceled") {
          displayInterestedMemberCard(request, objectTimeSlot,
              objectDescription);
        }
        // Display other information if the user IS the offeror.
      } else if (request.object.state !== "given" && request.object.state
          !== "canceled") {
        displayOfferorCard(request, objectTimeSlot, objectDescription,
            idObject);
        // Display all interested members
        if (request.object.state === "assignable") {
          displayInterestedMembers(idOffer, idMember);
          // Display the choosen recipient instead, if there is one.
        } else if (request.object.state === "assigned") {
          displayRecipient(idOffer, idMember);
        }
      } else if (request.object.state === "canceled") {
        displayCanceled(idOffer, request.object.version);
      }
    } catch (e) {
      document.getElementById(
          'offer-details-interested-members').innerHTML = '';
      displayError("Erreur lors du chargement des membres interessés.",
          'offer-details-interested-members-feedback')
    }
  }).catch((error) => {
        if (error instanceof RequestError) {
          document.getElementById('card').innerHTML = '';
          displayError(error.status, error.message, 'offer-details-feedback');
        } else {
          displayWarning(error, 'offer-details-feedback');
        }
      }
  );
}

export default OfferDetailsPage;

/**
 * Create the structure that will contain the object information
 *
 * @param card the HTML element that will contain the card
 * @param request the request
 */
function loadObjectCard(card, request) {
  let state = displayStateInFrench(request.object.state);
  card.innerHTML = `
    <!-- Picture -->
    <div id="div-offer-details-picture" class="col-md-4">
      <img id="offer-details-picture" src="" alt="">
    </div>
    <!-- Details -->
    <div class="col-md-8">
      <div class="row">
        <!-- Type -->
        <div class="col-md-2">
          <strong>Type</strong>
          <P id="offer-details-type">${request.object.type.label}</P>
        </div>
        <!-- State -->
        <div class="col-md-2">
          <strong>Etat</strong>
          <P id="offer-details-state">${state}</P>
        </div>
        <!-- Interests -->
        <div class="col-md-3">
          <strong>Personnes int&eacute;ress&eacute;es</strong>
          <P id="offer-details-interests">${request.nbrInterests}</P>
        </div>
        <!-- Creation date -->
        <div class="col-md-3">
          <strong>Date de cr&eacute;ation</strong>
          <P id="offer-details-creation-date">${request.date}</P>
        </div>
        <div class="col-md-auto">
          <strong>Adresse</strong>
          <P id="offer-details-address"></P>
        </div>
        <div class="col-md-auto">
           <strong>Membre offreur</strong>
          <P id="offer-details-offeror"></P>
        </div>
        <!-- Time slot -->
        <div class="col-md-12 p-3" id="offer-details-time-slot"></div>
        <!-- Description -->
        <div class="col-md-12 p-3" id="offer-details-description"></div>
        <!-- User availability -->
        <div class="col-md-12 p-3" id="offer-details-availability"></div>
      </div>
    </div>
    <div class="col-md-12" id="not-the-offeror"></div>
    <!-- Offer's button -->
    <div class="col-md-12 text-center mt-4" id="offer-details-buttons"></div>
  `;
  // Display a warning message if the user that offered the object is not
  // available right now
  if (request.state === 'unavailable') {
    let availability = document.getElementById('offer-details-availability');
    availability.className = "card-text text-truncate ms-2 fst-italic text-danger";
    availability.innerHTML = `<strong>Offreur actuellement indisponible.</strong>`;
  }
  // Display correctly the address if there is a unit number or not
  if (request.object.offeror.address.unitNumber !== null) {
    document.getElementById('offer-details-address').innerHTML = `
      ${request.object.offeror.address.street + ` `
    + request.object.offeror.address.buildingNumber +
    ` bte ` + request.object.offeror.address.unitNumber + `, `
    + request.object.offeror.address.commune + ` `
    + request.object.offeror.address.postcode}
    `;
  } else {
    document.getElementById('offer-details-address').innerHTML = `
      ${request.object.offeror.address.street + ` `
    + request.object.offeror.address.buildingNumber + `, `
    + request.object.offeror.address.commune + ` `
    + request.object.offeror.address.postcode}
    `;
  }
  document.getElementById('offer-details-offeror').innerHTML = `
      ${request.object.offeror.surname + ' ' + request.object.offeror.lastname}
    `;

  // Insert the picture of the object, if there is one. Otherwise, insert
  // the 'no image', by default image.
  let img = document.getElementById('offer-details-picture');
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
      img.alt = `Image illustrant l'objet d&#233;crit.`;
    });
  }
  img.className = "card-img-top w-51";
}

/**
 * Display the evalutation card if the user is the recipient and that he went
 * take the object.
 *
 * @param request the request
 * @param objectTimeSlot the HTML element to fit the objectTimeSlot
 * @param objectDescription the HTML element to fit the objectDescription
 */
function displayRecipientEvaluationCard(request, objectTimeSlot,
    objectDescription) {
  // Display the time slot
  objectTimeSlot.innerHTML = `
    <strong>Plage horaire</strong>
    <P>${request.object.timeSlot}</P>
  `;
  // Display the description
  objectDescription.innerHTML = `
    <strong>Description</strong>
    <P>${request.object.description}</P>
  `;
  // The user needs to be able to evaluate the object
  let divNotOfferor = document.getElementById('not-the-offeror');
  divNotOfferor.innerHTML = `
    <!-- Notation of the evaluation -->
    <div class="col-md-12 mb-3">
      <label for="offer-details-rating-note" class="form-label">
        <strong>Note sur 5 :</strong>
      </label>
      <input type="range" id="offer-details-rating-note" class="form-range" min="1" max="5"> 
    </div>
    <!-- Description of the evaluation -->
    <div class="col-md-12 mb-3">
      <label for="offer-details-rating-description" class="form-label">
        <strong>Description :</strong>
      </label>
      <input type="text" class="form-control"
        id="offer-details-rating-description">
    </div>
  `;
  // The user needs to validate its rating
  let buttonNotOfferor = document.getElementById('offer-details-buttons');
  buttonNotOfferor.innerHTML = `
    <button id="offer-details-rate" type="submit" class="btn btn-lg m-1">
      Evaluer
    </button>
  `;
  buttonNotOfferor.addEventListener("click", async () => {
    try {
      var note = document.getElementById(
          "offer-details-rating-note").value;
      var description = document.getElementById(
          "offer-details-rating-description").value;
      const options = {
        method: "POST", headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject(STORED_TOKEN)
        }, body: JSON.stringify({
          detail: description,
          value: note,
        }),
      };
      await fetch(`/api/offers/${request.idOffer}/rate`, options)
      .then((response) => {
        if (!response.ok) {
          if (response.status === 400) {
            displayWarning("Veuillez introduire une description.",
                "offer-details-feedback");
          } else {
            displayWarning("Il y a eu un problème.", 'offer-details-feedback');
          }
        } else {
          Redirect(`/`);
        }
      })
    } catch (error) {
      displayWarning("Il y a eu un problème.", 'offer-details-feedback');
    }
  });

}

/**
 * Insert all the useful element for a user that is not the offeror.
 *
 * @param request that contains all the information.
 * @param objectTimeSlot the element that will contain the time slot.
 * @param objectDescription the element that wil contain the description.
 */
async function displayInterestedMemberCard(request, objectTimeSlot,
    objectDescription) {
  // Display the time slot
  objectTimeSlot.innerHTML = `
    <strong>Plage horaire</strong>
    <P>${request.object.timeSlot}</P>
  `;
  // Display the description
  objectDescription.innerHTML = `
    <strong>Description</strong>
    <P>${request.object.description}</P>
  `;
  // Check if the user is already interested
  var interested = false;
  try {
    const options = {
      method: "GET", headers: {
        "Content-Type": "application/json",
        "Authorization": getSessionObject(STORED_TOKEN),
      },
    };
    // Send the requests
    await fetch(
        `/api/interests/${request.idOffer}/is-interested`,
        options).then((response) => {
      if (!response.ok) {
        displayError(response.status,
            "Il y a eu un problème.",
            'offer-details-feedback');
      }
      return response.json(); // json() return a promise => we wait for the response
    })
    .then((dataResponse) => {
      interested = dataResponse;
    });
  } catch (e) {

  }
  let divNotOfferor = document.getElementById('not-the-offeror');
  if (!interested) {
    // The user needs to be able to enter some information to mark its
    // interest
    divNotOfferor.innerHTML = `
    <!-- Retrieve date -->
    <div class="col-md-12 mb-3">
      <label for="offer-details-retrieved-date" class="form-label">
        <strong>Veuillez indiquer la date de retrait(et l'heure) :</strong>
      </label>
      <input type="text" class="form-control"
        id="offer-details-retrieved-date">
    </div>
    <!-- Phone number -->
    <div class="col-md-12 mb-3">
      <label for="offer-details-retrieved-phone-number" class="form-label">
        <strong>Si vous voulez &ecirc;tre appel&#233;, veuillez indiquer
          votre num&#233;ro de t&#233;l&#233;phone :</strong>
      </label>
      <input type="text" class="form-control"
        id="offer-details-retrieved-phone-number">
    </div>
  `;
    // The user needs to validate its interest
    let buttonNotOfferor = document.getElementById('offer-details-buttons');
    buttonNotOfferor.innerHTML = `
    <button id="offer-details-submit-interest" type="submit"
      class="btn btn-lg m-1">
      Marquer mon int&eacute;ret
    </button>
  `;
    buttonNotOfferor.addEventListener("click", async () => {
      var date = document.getElementById(
          "offer-details-retrieved-date").value;
      if (date == null || date === "") {
        displayWarning("Veuillez indiquer la date de retrait.",
            'offer-details-feedback');
      } else {
        var gsm = document.getElementById(
            "offer-details-retrieved-phone-number").value;
        const options = {
          method: "POST", headers: {
            "Content-Type": "application/json",
            "Authorization": getSessionObject(STORED_TOKEN)
          }, body: JSON.stringify({
            date: date,
            phoneNumber: gsm,
            version: request.object.version,
          }),
        };
        try {
          await fetch(`/api/offers/${request.idOffer}/interest`, options)
          .then((response) => {
            if (response.ok) {
              Redirect(`/`);
            } else {
              if (response.status === 400) {
                displayWarning("Veuillez indiquer la date de retrait.",
                    'offer-details-feedback');
              } else if (response.status === 412) {
                displayWarning(
                    "Vous avez déjà marqué votre interêt pour cette offre.",
                    'offer-details-feedback');
              } else {
                displayWarning("Il y a eu un problème.",
                    'offer-details-feedback');
              }
            }
          });
        } catch (error) {
          displayWarning("Il y a eu un problème.", 'offer-details-feedback');
        }

      }

    });
  } else {
    divNotOfferor.innerHTML = `
    <!-- Retrieve date -->
    <div class="col-md-12 mb-3">
        <strong>Vous avez d&eacute;j&agrave; marqu&eacute; votre int&eacute;r&ecirc;t sur cette offre.</strong>
    </div>`;
  }

}

/**
 * Insert all the useful element for the offeror.
 *
 * @param request that contains all the information.
 * @param objectTimeSlot the element that will contain the time slot.
 * @param objectDescription the element that will contain the description.
 * @param idObject id of the current object.
 */
function displayOfferorCard(request, objectTimeSlot, objectDescription,
    idObject) {
  // Add modification picture
  let div = document.getElementById("div-offer-details-picture");
  let fileModifier = document.createElement("input");
  fileModifier.type = "file"
  fileModifier.id = "offer-details-picture-modify";
  div.appendChild(fileModifier);
  // Display the time slot
  objectTimeSlot.innerHTML = `
    <label for="offer-details-time-slot-modify" class="form-label">
      <strong>Plage horaire</strong>
    </label>
    <input type="text" class="form-control"
      id="offer-details-time-slot-modify"
      value="${request.object.timeSlot}">
  `;
  //Display the description
  objectDescription.innerHTML = `
    <label for="offer-details-description-modify" class="form-label"><strong>Description</strong></label>
    <textarea class="form-control" id="offer-details-description-modify">${request.object.description}</textarea>`;
  // Display the buttons to cancel and approve changes
  let buttonOfferor = document.getElementById('offer-details-buttons');
  buttonOfferor.innerHTML = `
    <button id="offer-details-cancel" type="submit"
      class="btn btn-lg m-1">
      Annuler cette offre
    </button>
    <button id="offer-details-submit" type="submit"
      class="btn btn-lg m-1">
      Enregistrer les modifications
    </button>
  `;
  // Action to cancel
  document.getElementById('offer-details-cancel')
  .addEventListener("click", async () => {
    try {
      const options = {
        method: "PATCH", headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject(STORED_TOKEN)
        },
      };
      const response = await fetch(
          `/api/offers/${request.idOffer}/cancel?${VERSION}=${request.object.version}`,
          options)
      if (response.ok) {
        Redirect(`/`);
      } else {
        if (response.status === 412) {
          displayError(response.status,
              "Vous ne pouvez pas supprimer une offre qui ne vous appartient pas.",
              'interest-feedback');
        }
        if (response.status >= 500) {
          displayWarning(
              "Erreur serveur.", 'interest-feedback');
        } else {
          displayWarning("Il y a eu un problème.", 'interest-feedback');
        }
      }
    } catch (error) {
      displayWarning("Il y a eu un problème.", 'interest-feedback');
    }
  });
  // Action to modify
  document.getElementById('offer-details-submit')
  .addEventListener("click", () => (modifyInfo)(request));

}

/**
 * Send a request to the server to modify the content of the offer.
 *
 * @param request The data of the offer.
 * @returns {Promise<void>}
 */
async function modifyInfo(request) {
  let idOffer = request.idOffer;
  // Get all the form's data
  const description = document.getElementById(
      "offer-details-description-modify");
  const timeslot = document.getElementById("offer-details-time-slot-modify");
  const uploader = document.getElementById("offer-details-picture-modify");
  if (description.value === "" || timeslot.value === "") {
    displayWarning("Veuillez compl&egrave;ter tous les champs.",
        "offer-details-feedback");
  } else {
    const formData = new FormData();
    if (uploader.value !== "") {
      formData.append('file', uploader.files[0]);
    }
    formData.append('idOffer', idOffer);
    formData.append('object', JSON.stringify({
      timeSlot: timeslot.value,
      description: description.value,
      version: request.object.version,
    }))
    try {
      const options = {
        method: "PUT",
        body: formData,
        headers: {
          "Authorization": getSessionObject(STORED_TOKEN),
        },
      };
      // Send the requests
      await fetch("/api/objects/modify", options)
      .then((response) => {
        if (!response.ok) {
          if (response.status >= 500) {
            displayError(response.status,
                "Impossible de r&#233;cup&#233;rer les donn&#233;es.",
                'offer-details-feedback');
          } else {
            displayWarning("Un des champs n'est pas correcte.",
                "offer-details-feedback");
          }
        }
        Redirect(`/offer?id=${idOffer}`);
      });

    } catch (error) {
      console.error("RegisterPage::error: ", error);
    }
  }
}

/**
 * Send a request to the server to get all the information about interested
 * members for this offer.
 *
 * @param idOffer the current offer.
 * @param idMember the current member.
 * @returns {Promise<void>}
 */
async function displayInterestedMembers(idOffer, idMember) {
  createInterestedMembersCard(
      document.getElementById('offer-details-interested-members'));
  const options = {
    method: "GET", headers: {
      "Content-Type": "application/json",
      "Authorization": getSessionObject(STORED_TOKEN),
    },
  };
  // Send the requests
  await fetch(`/api/interests?idOffer=${idOffer}&idMember=${idMember}`,
      options).then((response) => {
    if (!response.ok) {
      throw new RequestError(response.status,
          "Impossible de r&#233;cup&#233;rer les donn&#233;es.")
    }
    return response.json(); // json() return a promise => we wait for the response
  })
  .then((dataResponse) => {
    // for each user, insert in the table
    dataResponse.forEach((data) => {
      insertRow(
          document.getElementById('offer-details-interested-members-table'),
          data, idOffer);
    });
  })
  .catch((error) => {
    if (error instanceof RequestError) {
      displayError(error.status,
          error.message,
          'modify-feedback');
    } else {
      displayWarning(error,
          'modify-feedback');
    }

  });
}

/**
 * Create a card that contains a table with all the interested members.
 *
 * @param div the HTML element that contains the card.
 */
function createInterestedMembersCard(div) {
  div.innerHTML = `
    <!-- Interested members card -->
    <div class="card shadow mb-5" id="card">
      <!-- Card header -->
      <div class="card-header" id="card-header"></div>
      <!-- Card body -->
      <div class="card-body">
        <!-- Header title -->
        <h2 class="fw-bold mb-5 text-center">
          PERSONNES INT&Eacute;RESS&Eacute;S
        </h2>
        <table class="table table-hover">
          <thead>
            <tr>
              <th scope="col">Pseudo</th>
              <th scope="col">Pr&eacute;nom</th>
              <th scope="col">Nom</th>
              <th scope="col">Num&eacute;ro de t&eacute;l&eacute;phone</th>
              <th scope="col">Plage horaire</th>
              <th scope="col">Membre indisponible</th>
              <th scope="col"></th>
            </tr>
          </thead>
          <tbody id="offer-details-interested-members-table"></tbody>
        </table>
      </div>
    </div>
  `;
}

/**
 * Insert a row in the table body with the data from the request. Fields are :
 * 'pseudo', 'prenom', 'nom', 'date et heure choisie'.
 *
 * @param tableBody in which the row need to be inserted.
 * @param data the data to insert
 * @param idOffer the offer related
 */
function insertRow(tableBody, data, idOffer) {
  const line = document.createElement("tr");
  // Create username column
  const usernameCell = document.createElement("th");
  usernameCell.scope = "row";
  usernameCell.innerText = data.interestedMember.username;
  line.appendChild(usernameCell);
  // Create firstname column
  const surnameCell = document.createElement("td");
  surnameCell.innerText = data.interestedMember.surname;
  line.appendChild(surnameCell);
  // Create lastname column
  const lastnameCell = document.createElement("td");
  lastnameCell.innerText = data.interestedMember.lastname;
  line.appendChild(lastnameCell);
  // Create the phone number cell
  const phoneCell = document.createElement("td");
  phoneCell.innerText = data.interestedMember.phoneNumber;
  line.appendChild(phoneCell);
  // Create date column
  const timeslotCell = document.createElement("td");
  timeslotCell.innerText = data.date;
  line.appendChild(timeslotCell);
  // Create disposable cell
  const disposableCell = document.createElement("th");
  if (data.interestedMember.state === "unavailable") {
    disposableCell.innerHTML = "indisponible";
  } else {
    disposableCell.innerHTML = "disponible";
  }
  line.appendChild(disposableCell)
  // Create the button to indicate a user as the receiver
  const buttonCell = document.createElement("td");
  const buttonCellInner = document.createElement('button');
  buttonCellInner.type = "button";
  buttonCellInner.className = "btn btn-secondary";
  buttonCellInner.innerText = "Choisir comme receveur";
  buttonCell.appendChild(buttonCellInner);
  buttonCellInner.addEventListener("click",
      () => chooseReceiver(data.interestedMember, idOffer, buttonCell,
          data.offer.object.version));
  line.appendChild(buttonCell);
  tableBody.appendChild(line);
}

/**
 * Send a request to the server to get all the information about the member that
 * is meant to receive the object.
 *
 * @param idOffer the current offer.
 * @param idMember the current member.
 * @returns {Promise<void>}
 */
async function displayRecipient(idOffer, idMember) {

  const options = {
    method: "GET", headers: {
      "Content-Type": "application/json",
      "Authorization": getSessionObject(STORED_TOKEN),
    },
  };
  // Send the requests
  await fetch(
      `/api/interests/get-recipient?idOffer=${idOffer}&idMember=${idMember}`,
      options).then((response) => {
    if (!response.ok) {
      throw new RequestError(response.status,
          "Impossible de récuperer les données")
    }
    return response.json(); // json() return a promise => we wait for the response
  })
  .then((dataResponse) => {
    insertRowRecipient(
        document.getElementById('offer-details-interested-members'),
        dataResponse, idOffer);
  })
  .catch((error) => {
    if (error instanceof RequestError) {
      displayError(error.status,
          error.message,
          'modify-feedback');
    } else {
      displayWarning(error,
          'modify-feedback');
    }
  });
}

/**
 * Insert a card that contains all recipient information.
 *
 * @param div that will contain the card
 * @param data the data to insert
 * @param idOffer the offer related
 */
function insertRowRecipient(div, data, idOffer) {
  var phoneNumber = data.interestedMember.phoneNumber;
  if (phoneNumber === null) {
    phoneNumber = "";
  }
  div.innerHTML = `
    <!-- Recipient members card -->
    <div class="card shadow mb-5" id="card">
      <!-- Card header -->
      <div class="card-header" id="card-header"></div>
      <!-- Card body -->
      <div class="card-body">
        <!-- Header title -->
        <h2 class="fw-bold mb-5 text-center">
          RECEVEUR
        </h2>
        <table class="table table-hover">
          <thead>
            <tr>
              <th scope="col">Pseudo</th>
              <th scope="col">Pr&eacute;nom</th>
              <th scope="col">Nom</th>
              <th scope="col">Num&eacute;ro de t&eacute;l&eacute;phone</th>
              <th scope="col">Plage horaire</th>
              <th scope="col"></th>
              <th scope="col"></th>
            </tr>
          </thead>
          <tbody id="offer-details-interested-members-table">
            <tr>
              <th scope="row">${data.interestedMember.username}</th>
              <td>${data.interestedMember.surname}</td>
              <td>${data.interestedMember.lastname}</td>
              <td>${phoneNumber}</td>
              <td>${data.date}</td>
              <td>
                <!-- Button to indicate that the user came -->
                <button type="button" class="btn btn-secondary" id="offer-details-given">
                  Donn&eacute;
                </button>
              </td>
              <td>
                <!-- Button trigger modal when the user has not come -->
                <button type="button" class="btn btn-secondary"
                  data-bs-toggle="modal" data-bs-target="#notComeModal">
                  N'est pas venu
                </button>
                <!-- Modal -->
                <div class="modal fade" id="notComeModal" tabindex="-1" aria-labelledby="notComeModalLabel" aria-hidden="true">
                  <div class="modal-dialog">
                    <div class="modal-content">
                      <div class="modal-header">
                        <h5 class="modal-title" id="notComeModalLabel">Nous sommes d&eacute;sol&eacute;s d'apprendre que le receveur ne s'est pas pr&eacute;sent&eacute;</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                      </div>
                      <div class="modal-body">
                        <p>Que voulez-vous faire ?</p>
                        <div class="d-grid gap-3 text-center">
                          <div class="p-1">
                            <button type="button" class="btn btn-secondary p-2" 
                              id="offer-details-new-recipient"
                              data-bs-dismiss="modal">
                              Choisir un nouveau receveur
                            </button>
                          </div>
                          <div class="p-1">
                            <button type="button" class="btn btn-secondary p-2"
                            id="offer-details-new-offer"
                            data-bs-dismiss="modal">
                              Offrir &agrave; nouveau l'objet
                            </button>
                          </div>
                          <div class="p-1">
                            <button type="button" class="btn btn-secondary p-2" 
                            id="offer-details-annul"
                            data-bs-dismiss="modal">
                              Annuler l'offre
                            </button>
                          </div>
                        </div>
                      </div>
                     </div>
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `;
  // Action when clicking on button to say that the user came to get the object
  document.getElementById('offer-details-given').addEventListener("click",
      () => indicateRecipientHasCome(data.interestedMember, idOffer,
          data.offer.object.version));

  // Action when clicking on button new recipient
  document.getElementById('offer-details-new-recipient').addEventListener(
      "click",
      () => chooseNewRecipient(data.interestedMember.idUser, idOffer,
          data.offer.object.version));

  // Action when clicking on button new offer
  document.getElementById('offer-details-new-offer').addEventListener("click",
      () => offerAgain(data.interestedMember.idUser, idOffer,
          data.offer.object.version));

  // Action to cancel
  document.getElementById('offer-details-annul')
  .addEventListener("click",
      () => annulOffer(idOffer, data.offer.object.version));
}

/**
 * Create the request to add a member as the receiver of the offer.
 *
 * Throws an exception and display an error message if there is a problem with
 * the request or the response.
 * Reload the page to see the changes if there is no problem.
 *
 * @param user the chosen receiver.
 * @param idOffer the offer.
 * @param buttonCell the button cell.
 * @param version the version of the object
 */
async function chooseReceiver(user, idOffer, buttonCell, version) {
  // Change the submit button to 'chargement'
  buttonStartLoading(buttonCell);
  const options = {
    method: "PUT", headers: {
      "Content-Type": "application/json",
      "Authorization": getSessionObject(STORED_TOKEN)
    },
  };
  await fetch(
      `/api/interests/add-recipient?user=${user.idUser}&offer=${idOffer}&version=${version}`,
      options)
  .then(() => {
    Redirect(`/offer?id=${idOffer}`);
  });
}

/**
 * Indicate that the recipient has come.
 *
 * Throws an exception and display an error message if there is a problem with
 * the request or the response.
 * Reload the page to see the changes if there is no problem.
 *
 * @param user the recipient.
 * @param idOffer the offer.
 * @param version the version of the object
 */
async function indicateRecipientHasCome(user, idOffer, version) {
  // Change the submit button to 'chargement'
  let submitButton = document.getElementById('offer-details-given');
  buttonStartLoading(submitButton);
  const options = {
    method: "PATCH", headers: {
      "Content-Type": "application/json",
      "Authorization": getSessionObject(STORED_TOKEN)
    },
  };
  await fetch(
      `/api/interests/indicate-has-come?user=${user.idUser}&offer=${idOffer}&version=${version}`,
      options)
  .then(() => {
    Redirect(`/offer?id=${idOffer}`);
  });
}

/**
 * Annul the offer
 *
 * Throws an exception and display an error message if there is a problem with
 * the request or the response.
 * Reload the page to see the changes if there is no problem.
 *
 * @param idOffer the offer.
 * @param version the version of the object
 */
async function annulOffer(idOffer, version) {
  try {
    const options = {
      method: "PATCH", headers: {
        "Content-Type": "application/json",
        "Authorization": getSessionObject(STORED_TOKEN)
      },
    };
    const response = await fetch(
        `/api/offers/${request.idOffer}/cancel?version=${version}`,
        options)
    if (response.ok) {
      Redirect(`/`);
    } else {
      if (response.status === 412) {
        displayWarning(
            "Vous ne pouvez pas supprimer une offre qui ne vous appartient pas.",
            'interest-feedback');
      }
      if (response.status >= 500) {
        displayWarning(
            "Erreur serveur.", 'interest-feedback');
      } else {
        displayWarning("Il y a eu un problème.", 'interest-feedback');
      }
    }
  } catch
      (error) {
    displayWarning("Il y a eu un problème.", 'interest-feedback');
  }
}

/**
 * Remove the previous recipient and put the objet to state "assignable" or "donated".
 * @param oldRecipient the old recipient
 * @param idOffer the id offer
 * @param version the version of the object
 */
async function chooseNewRecipient(oldRecipient, idOffer, version) {
  try {
    const options = {
      method: "POST", headers: {
        "Content-Type": "application/json",
        "Authorization": getSessionObject(STORED_TOKEN)
      }
    };
    const response = await fetch(
        `/api/offers/new-recipient?offer=${idOffer}&old-recipient=${oldRecipient}&version=${version}`,
        options)
    if (response.ok) {
      Redirect(`/offer?id=${idOffer}`);
    } else {
      displayWarning("Il y a eu un problème.", 'offer-details-feedback');
    }
  } catch (error) {
    displayWarning("Il y a eu un problème.", 'offer-details-feedback');
  }
}

/**
 * Create a new offer based on the old one and put the state of the old one to cancel
 * @param oldRecipient the old recipient
 * @param idOffer the id offer
 * @param version the version of the object
 */
async function offerAgain(oldRecipient, idOffer, version) {
  try {
    const options = {
      method: "POST", headers: {
        "Content-Type": "application/json",
        "Authorization": getSessionObject(STORED_TOKEN)
      }
    };
    const response = await fetch(
        `/api/offers/offer-again?offer=${idOffer}&old-recipient=${oldRecipient}&version=${version}`,
        options)
    if (response.ok) {
      Redirect(`/offer?id=${idOffer}`);
    } else {
      displayWarning("Il y a eu un problème.", 'offer-details-feedback');
    }
  } catch (error) {
    displayWarning("Il y a eu un problème.", 'offer-details-feedback');
  }
}

/**
 * Display the offer correctly if it was canceled
 *
 * @param idOffer the id of the offer
 * @param version the version of the object
 */
function displayCanceled(idOffer, version) {
  // Display the buttons to cancel and approve changes
  let cardBody = document.getElementById("offer-object-card-body");
  let button = document.createElement("button");
  button.className = "btn btn-lg m-1";
  button.type = "submit";
  button.id = "button-remake-offer";
  button.innerText = `Réoffrir l'offre`;
  cardBody.appendChild(button);
  button.addEventListener("click", async () => {
    try {
      const options = {
        method: "POST", headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject(STORED_TOKEN)
        },
      };
      const response = await fetch(
          `/api/offers/offer-again?offer=${idOffer}&old-recipient=-1&version=${version}`,
          options)
      if (response.ok) {
        Redirect(`/offers/my`);
      } else {
        displayWarning("Il y a eu un problème.", 'offer-details-feedback');
      }
    } catch (error) {
      displayWarning("Il y a eu un problème.", 'offer-details-feedback');
    }
  });
}


