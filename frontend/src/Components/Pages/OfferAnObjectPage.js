import {displayWarning} from "../../utils/feedback";
import {Redirect} from "../Router/Router";
import {getSessionObject} from "../../utils/session";
import {STORED_TOKEN} from "../../utils/const";

/**
 * Render OfferAnObjectPage and display a form in order to create a new object.
 *
 * The user can enter a picture if he wants.
 */
const OfferAnObjectPage = () => {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = `
    <div class="container mt-5" id="offer-object">
      <!-- Error feedback --> 
      <div id="offer-object-feedback" class="m-3"></div>
      <!-- Card -->
      <div class="card shadow" id="card">
        <!-- Card header -->
        <div class="card-header" id="card-header"></div>
        <!-- Card body -->
        <div class="card-body row p-5" id="offer-object-card-body">
          <!-- Header title -->
          <h2 class="fw-bold mb-5 text-center" id="offer-object-load-animation">
            OFFRIR UN OBJET
          </h2>
          <!-- Type -->
          <div class="col-md-6 p-3">
            <label for="offer-object-type" class="form-label">
              <strong>Type</strong>
            </label>
            <input type="text" class="form-control" id="offer-object-type">
          </div>
          <!-- Time slot -->
          <div class="col-md-6 p-3">
            <label for="offer-object-time-slot" class="form-label">
              <strong>Plage horaire</strong>
            </label>
            <input type="text" class="form-control" id="offer-object-time-slot">
          </div>
          <!-- Description -->
          <div class="col-md-12 p-3">
            <label for="offer-object-description" class="form-label">
              <strong>Description</strong>
            </label>
            <textarea class="form-control" id="offer-object-description"></textarea>
          </div>
          <!-- Picture -->
          <div class="col-md-12 p-3">
            <label for="offer-object-picture" class="form-label">
              <strong>Ajouter une image</strong>
            </label>
            <input type="file" class="form-control" id="offer-object-picture"
              accept="image/png, image/jpeg, image/jpg">
          </div>
          <!-- Submit button -->
          <div class="col-md-12 text-center mt-4">
            <button id="offer-object-submit" type="submit" class="btn btn-lg">
              Valider
            </button>
          </div>
        </div>
      </div>
    </div>
  `;
  const submit = document.getElementById('offer-object-submit');
  submit.addEventListener("click", submitForm);
}

export default OfferAnObjectPage;

/**
 * Check the fields and create a new offer if all checks are correct.
 */
async function submitForm(e) {
  e.preventDefault();
  const type = document.getElementById("offer-object-type");
  const timeSlot = document.getElementById("offer-object-time-slot");
  const description = document.getElementById("offer-object-description");
  const fileInput = document.getElementById("offer-object-picture");
  const formData = new FormData();
  if (fileInput.value !== "") {
    formData.append('file', fileInput.files[0]);
  }
  formData.append('object', JSON.stringify({
    type: {
      label: type.value
    },
    timeSlot: timeSlot.value,
    description: description.value,
  }))

  if (type.value === "" || timeSlot.value === "" || description.value === "") {
    displayWarning("Veuillez completer tous les champs (sauf la photo).",
        'offer-feedback');
  } else {
    try {
      const options = {
        method: "POST",
        body: formData,
        headers: {
          "Authorization": getSessionObject(STORED_TOKEN)
        },
      };

      // Send the requests
      const response = await fetch("/api/offers/create", options);
      let data = await response.json();
      if (data.idOffer === undefined) {
        displayWarning(data.notification, 'offer-feedback');
      } else {
        // call the OfferDetailsPage via the Router
        Redirect(`/offer?id=${data.idOffer}`);
      }
    } catch (error) {
      displayWarning("Erreur.", 'offer-feedback');
      console.error("OfferAnObjectPage::error: ", error);
    }
  }
}