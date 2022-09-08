import {Redirect} from "../Router/Router";
import {displayError, displayWarning} from "../../utils/feedback";
import {buttonStartLoading} from "../../utils/buttons";
import {RequestError} from "../../utils/checkRequest";
import {verifyConfirmPassword, verifyPassword} from "../../utils/password";

/**
 * Render the RegisterPage page and display a form to signup on the website
 */
function RegisterPage() {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = `
    <div class="container py-5">
      <div class="row d-flex justify-content-center align-items-center h-100">
      <div class="col-12 col-md-8 col-lg-8 col-xl-8">
      <!-- Card -->
      <div id="card" class="card shadow pb-5 mb-3">
        <div class="card-header" id="card-header"></div>
        <!-- Form -->
        <form id="form" class="card-body p-5 text-center row gy-3">
          <!-- Header -->
          <h2 class="fw-bold mb-5">INSCRIPTION</h2>
          <!-- Username -->
          <div class="col-12 col-md-4 form-floating">
            <input type="text" class="form-control" id="signup-input-username" placeholder="Pseudonyme" required>
            <label for="signup-input-username">Pseudonyme</label>
          </div>
          <!-- Firstname -->
          <div class="col-12 col-md-4 form-floating">
            <input type="text" class="form-control" id="signup-input-firstname" placeholder="Prenom" required>
            <label for="signup-input-firstname">Prenom</label>
          </div>
          <!-- Lastname -->
          <div class="col-12 col-md-4 form-floating">
            <input type="text" class="form-control" id="signup-input-lastname" placeholder="Nom" required>
            <label for="signup-input-lastname">Nom</label>
          </div>
          <!-- Street -->
          <div class="col-12 col-md-8 form-floating">
            <input type="text" class="form-control" id="signup-input-street" placeholder="Rue" required>
            <label for="signup-input-street">Rue</label>
          </div>
          <!-- Building number -->
          <div class="col-6 col-md-2 col-sm-1 form-floating">
            <input type="text" class="form-control" id="signup-input-building-number" placeholder="Numero" required>
            <label for="signup-input-building-number">Numero</label>
          </div>
          <!-- box -->
          <div class="col-6 col-md-2 col-sm-1 form-floating">
            <input type="text" class="form-control" id="signup-input-box" placeholder="Boite" >
            <label for="signup-input-box">Boite</label>
          </div>
          <!-- Commune -->
          <div class="col-12 col-md-6 form-floating">
            <input type="text" class="form-control" id="signup-input-commune" placeholder="Commune" required>
            <label for="signup-input-commune">Commune</label>
          </div>
          <!-- Postal code -->
          <div class="col-12 col-md-4 form-floating">
            <input type="text" class="form-control" id="signup-input-postalcode" placeholder="Code postal" required>
            <label for="signup-input-postalcode">Code postal</label>
          </div>
          <!-- Password -->
          <div class="col-12 col-md-6 form-floating" id="signup-password">
            <input id="signup-input-password" type="password" class="form-control" placeholder="Mot de passe" required>
            <label for="signup-input-password">Mot de passe</label>
            <div id="signup-password-feedback"></div>
          </div>
          <!-- Confirm password -->
          <div class="col-12 col-md-6 form-floating">
            <input type="password" class="form-control" id="signup-input-confirm-password" placeholder="Confirmez mot de passe" required>
            <label for="signup-input-confirm-password">Confirmation</label>
            <div id="signup-confirm-password-feedback"></div>
          </div>
          <div class="col-12 text-center mt-5 mb-3">
            <button id="submit-signup" type="submit" class="btn btn-lg">S'inscrire</button>
          </div>
        </form>
      </div>
      <div id="signup-feedback"></div>
      </div>
      </div>
      </div>
    </div>
  `;
  // Password verification
  let inputPwd = document.getElementById('signup-input-password');
  let feedbackPwd = document.getElementById('signup-password-feedback');
  verifyPassword(inputPwd, feedbackPwd);
  // Confirm password verification
  let inputConf = document.getElementById('signup-input-confirm-password');
  let feedbackConf = document.getElementById(
    'signup-confirm-password-feedback');
  verifyConfirmPassword(inputPwd, inputConf, feedbackConf);
  // On submit
  const submit = document.getElementById('submit-signup');
  submit.addEventListener("click", submitForm);
}

export default RegisterPage;

/**
 * Launch all the tests required on the inputs of the form and validate it or not.
 * If the form is valid, the user is connected and redirected to the home page.
 * If the form is invalid, the user must modify its inputs to try to validate it.
 */
async function submitForm(e) {
  e.preventDefault();
  // Loading button
  buttonStartLoading(document.getElementById('submit-signup'));
  // Get all the form's data
  const username = document.getElementById("signup-input-username");
  const surname = document.getElementById("signup-input-firstname");
  const lastname = document.getElementById("signup-input-lastname");
  const street = document.getElementById("signup-input-street");
  const buildingNumber = document.getElementById(
    "signup-input-building-number");
  const unitNumber = document.getElementById("signup-input-box");
  const postcode = document.getElementById("signup-input-postalcode");
  const commune = document.getElementById("signup-input-commune");
  const password = document.getElementById("signup-input-password");

  if (username.value === "" || password.value === "" || surname.value === ""
    || lastname.value === "" || street.value === "" || buildingNumber.value
    === "" || postcode.value === "" || commune.value === "") {
    // Reset button
    document.getElementById('submit-signup').innerHTML = "S'inscrire";
    displayWarning("Veuillez compl&egrave;ter tous les champs.",
      "signup-feedback");
  } else {
    try {
      const options = {
        method: "POST", body: JSON.stringify({
          username: username.value,
          surname: surname.value,
          lastname: lastname.value,
          address: {
            street: street.value,
            buildingNumber: buildingNumber.value,
            unitNumber: unitNumber.value,
            postcode: postcode.value,
            commune: commune.value,
          },
          password: password.value,
        }), // body data type must match "Content-Type" header
        headers: {
          "Content-Type": "application/json",
        },
      };
      // Send the requests
      await fetch("/api/auths/register", options).then((response) => {
        if (!response.ok) {
          // Reset button
          document.getElementById('submit-signup').innerHTML = "S'inscrire";
          if (response.status >= 500) {
            throw new RequestError(response.status,
              "Impossible de r&#233;cup&#233;rer les donn&#233;es.");
          } else if (response.status === 409) {
            throw new Error("Il existe déjà un membre avec ce Pseudonyme.");
          } else {
            throw new Error("Un des champs n'est pas correcte.");
          }
        } else {
          Redirect('/register/done');
        }
      });
    } catch (error) {
      if (error instanceof RequestError) {
        displayError(error.status, error.message, 'signup-feedback');
      } else {
        displayWarning(error, "signup-feedback")
      }
    }
  }
}
