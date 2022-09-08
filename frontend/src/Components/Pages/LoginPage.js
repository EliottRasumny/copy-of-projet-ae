import {Redirect} from "../Router/Router";
import {setLocalStorage, setSessionObject,} from "../../utils/session";
import Navbar from "../Navbar/Navbar";
import {STORED_TOKEN, STORED_USER} from "../../utils/const";
import {
  displayError,
  displayErrorNoStatus,
  displayWarning
} from "../../utils/feedback";
import {buttonStartLoading} from "../../utils/buttons";
import {RequestError, RequestWarning} from "../../utils/checkRequest";

/**
 * Render the LoginPage and display the form to connect a user.
 *
 * Redirect to the home page once we are logged in or display a message if the
 * request to the API is not valid.
 */
function LoginPage() {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = `
    <div class="container py-5">
      <div class="row d-flex justify-content-center align-items-center h-100">
        <div class="col-12 col-md-8 col-lg-6 col-xl-5">
        <!-- Card -->
        <div id="card" class="card shadow mb-md-5 mt-md-4 pb-5">
          <div class="card-header" id="card-header"></div>
          <!-- Form -->
          <form id="form" class="card-body p-5">
            <!-- Header -->
            <h2 class="fw-bold mb-5 text-center">CONNEXION</h2>
            <!-- Username input -->
            <div class="form-floating mb-3">
              <input type="text" class="form-control form-control-lg"
                id="login-input-username" placeholder="Pseudonyme" required>
              <label for="login-input-username">Pseudonyme</label>
            </div>
            <!-- Password input -->
            <div class="form-floating mb-3">
              <input type="password" class="form-control"
                id="login-input-password" placeholder="Mot de passe" required>
              <label for="login-input-password">Mot de passe</label>
              <!-- Password switch view/hide -->
              <div class="form-check form-switch mt-2">
                <input class="form-check-input" type="checkbox" role="switch"
                  id="login-checkbox-password" />
                <label class="form-check-label" for="login-checkbox-password"
                  id="login-checkbox-password-label">Cach&#233;</label>
              </div>
            </div>
            <!-- Login options -->
            <div class="text-center">
              <!-- Stay connected checkbox -->
              <input type="checkbox" class="form-check-input mb-3"
                id="login-checkbox-connection"> Rester connect&#233;<br>
              <!-- Login button -->
              <button id="submit-login" type="submit" class="btn btn-lg">
                Se connecter
              </button>
            </div>
          </form>
        </div>
        <div id="login-feedback"></div>
      </div>
      </div>
    </div>
  `;
  // Handle the submit button
  const submit = document.getElementById('submit-login');
  submit.addEventListener("click", submitForm);
  // View/hide password options
  const switchHideViewPassword = document.getElementById(
    'login-checkbox-password');
  switchHideViewPassword.addEventListener("click", () => {
    viewHidePassword();
  })
}

export default LoginPage;

/**
 * Switch the visibility of the password. If the password is visible, it makes
 * it hidden and if the password is hidden, it makes it visible.
 */
function viewHidePassword() {
  let password = document.getElementById('login-input-password');
  let label = document.getElementById('login-checkbox-password-label');
  if (password.type === 'password') {
    password.type = 'text';
    label.innerHTML = 'Affich&#233;'
  } else {
    password.type = 'password';
    label.innerHTML = 'Cach&#233;';
  }
}

/**
 * Check the fields and log in the user if all checks are correct by redirecting
 * him to the home page for logged users. If not, the user need the try again.
 */
async function submitForm(e) {
  let submitButton = document.getElementById('submit-login');
  e.preventDefault();
  // Change the submit button to 'chargement'
  buttonStartLoading(submitButton);
  const username = document.getElementById("login-input-username");
  const password = document.getElementById("login-input-password");
  const checkbox = document
  .getElementById("login-checkbox-connection");
  if (username.value === "" || password.value === "") {
    // Reset submit button
    submitButton.innerHTML = "Se connecter";
    displayWarning("Veuillez compl&eacute;ter tous les champs.",
      'login-feedback');
  } else {
    // Prepare the request
    const options = {
      method: "POST",
      body: JSON.stringify({
        username: username.value,
        password: password.value,
      }),
      headers: {
        "Content-Type": "application/json",
      },
    };
    // Send the requests
    await fetch("/api/auths/login", options)
    // Check the response and throws exception if needed
    .then((response) => {
      if (!response.ok) {
        if (response.status >= 500) {
          throw new RequestError(
            response.status,
            "Impossible de r&#233;cup&#233;rer les donn&#233;es."
          );
        } else {
          throw new RequestError(
            response.status,
            "Il n'existe pas d'utilisateur avec ce mot de passe."
          );
        }
      }
      return response.json();
    })
    // Use the data received
    .then((data) => {
      if (data.user === undefined) {
        // Reset submit button
        submitButton.innerHTML = "Se connecter";
        throw new RequestWarning(data.notification);
      }
      var user = JSON.parse(data.user);
      if (data.token === undefined) {
        // Reset submit button
        submitButton.innerHTML = "Se connecter";
        if (user.refusalReason == null) {
          //the state of the user is registered
          throw new RequestWarning(
            "Un admin doit encore valider votre inscription.");
        } else {
          //the state of the user is denided
          throw new RequestWarning("Votre inscription à été refusée.\n"
            + "Raison du refu : " + user.refusalReason + ".");
        }
      } else {
        // Check if the user want a token to stay connected
        if (checkbox.checked) {
          // save the user's token into the localStorage
          setLocalStorage(STORED_TOKEN, data.token);
        }
        // save the user's token into the sessionStorage
        setSessionObject(STORED_TOKEN, data.token);
        // save user infos for the session
        setSessionObject(STORED_USER, user);
        // Rerender the navbar for an authenticated user
        Navbar();
        // call the HomePage via the Router
        Redirect("/");
      }
    }).catch((err) => {
      // Reset submit button.
      submitButton.innerHTML = "Se connecter";
      // Display the error message.
      if (err instanceof RequestError) {
        displayError(err.status, err.message, 'login-feedback');
      } else if (err instanceof RequestWarning) {
        displayWarning(err.message, 'login-feedback');
      } else {
        displayErrorNoStatus(err, 'user-inscription-request-feedback');
      }
    });
  }
}