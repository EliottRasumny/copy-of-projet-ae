import {Redirect} from "../Router/Router";
import brandWhite from "../../img/donnamis_logo_white.svg";
import brandBlack from "../../img/donnamis_logo_black.svg"
import {getSessionObject, getUserName, getUserRole} from "../../utils/session";
import {STORED_TOKEN} from "../../utils/const";

/**
 * Render the Navbar which is styled by using Bootstrap
 * Each item in the Navbar is tightly coupled with the Router configuration :
 */
const Navbar = () => {
  const navbar = document.querySelector("#navbar");
  navbar.innerHTML = `
    <nav class="navbar navbar-expand-md">
      <div class="container-fluid">
        <!-- Toggle -->
        <button id="button-inverse" class="navbar-toggler" type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbar-toggle-elements"
          aria-controls="navbar-toggle-elements" aria-expanded="false"
          aria-label="Toggle navigation">
          <i class="bi-list"></i>
        </button>
        <!-- Brand and toggle get grouped for better mobile display -->
        <a class="btn navbar-brand" id="navbar-brand-wrapper">
          <img id="navbar-brand" alt="Donnamis" type="image/svg+xml" src="">
        </a>
        <!-- Collect the nav links, forms, and other content for toggling -->
        <div id="navbar-toggle-elements" class="collapse navbar-collapse">
          <ul class="navbar-nav">
            <!-- My offers -->
            <li class="nav-item mx-1 mt-1">
              <a id="navbar-see-my-offers"
                class="btn btn-sm btn-outline-light nav-link">
                Voir mes offres
                <i class="bi-files ps-1"></i>
              </a>
            </li>
            <!-- Offer an Object -->
            <li class="nav-item mx-1 mt-1">
              <a id="navbar-offer-an-object"
                class="btn btn-sm btn-outline-light nav-link">
                Offrir un Objet
                <i class="bi-plus-square-fill ps-1"></i>
              </a>
            </li>
            <!-- Offers that are assigned to me -->
            <li class="nav-item mx-1 mt-1">
              <a id="navbar-offers-assigned-to-me"
                class="btn btn-sm btn-outline-light nav-link">
                Offres qui me sont attribuées
                <i class="bi-plus-square-fill ps-1"></i>
              </a>
            </li>
            <!-- Inscription requests -->
            <li class="nav-item mx-1 mt-1">
              <a id="navbar-inscription-request"
                class="btn btn-sm btn-outline-light nav-link">
                G&#233;rer les demandes
              </a>
            </li>
            <!-- Denied inscriptions -->
            <li class="nav-item mx-1 mt-1">
              <a id="navbar-denied-inscriptions"
                class="btn btn-sm btn-outline-light nav-link">
                Voir membres refus&#233;s
              </a>
            </li>
            <!-- Research members -->
            <li class="nav-item mx-1 mt-1">
              <a id="navbar-research-members"
                class="btn btn-sm btn-outline-light nav-link">
                Rechercher un membre
              </a>
            </li>
          </ul>
          <ul class="navbar-nav ms-auto">
            <!-- Login -->
            <li class="nav-item mx-1 mt-1">
              <a id="navbar-login"
                class="btn btn-sm btn-outline-light nav-link">
                Se connecter
                <i class="bi-box-arrow-in-right ps-1" role="img"
                aria-label="login icon"></i>
              </a>
            </li>
            <!-- Signup -->
            <li class="nav-item mx-1 mt-1">
              <a id="navbar-signup"
                class="btn btn-sm btn-outline-light nav-link">
                S'inscrire
                <i class="bi-clipboard-plus"></i>
              </a>
            </li>
            <!-- User -->
            <li class="nav-item mx-1 mt-1">
              <a id="navbar-user"
                class="btn btn-sm btn-outline-light nav-link">
              </a>
            </li>
            <!-- Logout -->
            <li class="nav-item mx-1 mt-1">
              <a id="navbar-logout"
                class="btn btn-sm btn-outline-light nav-link">
                Se d&#233;connecter
                <i class="bi-box-arrow-right ps-1" role="img"
                aria-label="sign out icon"></i>
              </a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  `;
  // Get all button's element to apply actions and visibility on them
  const login = document.getElementById("navbar-login");
  const signup = document.getElementById("navbar-signup");
  const user = document.getElementById("navbar-user");
  const inscriptionRequest = document.getElementById(
      "navbar-inscription-request");
  const deniedInscriptions = document.getElementById(
      "navbar-denied-inscriptions");
  const seeMyOffers = document.getElementById(
      "navbar-see-my-offers");
  const researchMembers = document.getElementById("navbar-research-members");
  const logout = document.getElementById("navbar-logout");
  const offerAnObject = document.getElementById("navbar-offer-an-object");
  const offersAssignedToMe = document.getElementById(
      "navbar-offers-assigned-to-me");
  // Create action on button clicks
  createAction(
      login,
      signup,
      user,
      inscriptionRequest,
      deniedInscriptions,
      seeMyOffers,
      researchMembers,
      logout,
      offerAnObject,
      offersAssignedToMe
  );
  //buttons' visibility depending on the user's connection
  buttonVisibility(
      login,
      signup,
      user,
      inscriptionRequest,
      deniedInscriptions,
      seeMyOffers,
      researchMembers,
      logout,
      offerAnObject,
      offersAssignedToMe
  );
};
export default Navbar;

/**
 * Create action for all buttons while clicking on them.
 */
function createAction(
    login,
    signup,
    user,
    inscriptionRequest,
    deniedInscriptions,
    seeMyOffers,
    researchMembers,
    logout,
    offerAnObject,
    offersAssignedToMe
) {
  //Actions on the brand click
  const brandWrapper = document.getElementById('navbar-brand-wrapper');
  const brand = document.getElementById('navbar-brand');
  brand.src = brandWhite;
  brand.width = 80;
  brandWrapper.addEventListener("click", () => {
    Redirect("/");
  });
  //change the mouse pointer while the cursor is on the element
  brandWrapper.addEventListener("pointerover", () => {
    brand.src = brandBlack;
  });
  brandWrapper.addEventListener("pointerout", () => {
    brand.src = brandWhite;
  })
  //Action on login
  login.addEventListener("click", () => {
    Redirect('/login');
  })
  //Action on signup
  signup.addEventListener("click", () => {
    Redirect('/register');
  })
  //Action on user
  user.addEventListener("click", () => {
    Redirect(`/me`);
  });
  //Action on inscription request
  inscriptionRequest.addEventListener("click", () => {
    Redirect('/inscriptions');
  })
  //Action on denied inscriptions
  deniedInscriptions.addEventListener("click", () => {
    Redirect('/inscriptions/refusals');
  })
  //Action on see my offers
  seeMyOffers.addEventListener("click", () => {
    Redirect('/offers/my');
  })
  //Action on research members
  researchMembers.addEventListener("click", () => {
    Redirect('/users/research');
  })
  //Action on logout
  logout.addEventListener("click", () => {
    Redirect('/logout');
  })
  //Action on Offer an Object
  offerAnObject.addEventListener("click", () => {
    Redirect('/offer/create');
  })
  //Action on Offers that are assigned to me
  offersAssignedToMe.addEventListener("click", () => {
    Redirect('/offers/assigned/me');
  })
}

/**
 * Change the display property of the connection related buttons.
 * If the user is connected, display the "logout" button. Otherwise, display the
 * "login" and "signup" buttons.
 */
function buttonVisibility(
    login,
    signup,
    user,
    inscriptionRequest,
    deniedInscriptions,
    seeMyOffers,
    researchMembers,
    logout,
    offerAnObject,
    offersAssignedToMe
) {
  // A user is considered to be authenticated when there is a token stored in
  // the session storage.
  if (getSessionObject(STORED_TOKEN) != null) {
    // connected and admin
    login.style.display = "none";
    signup.style.display = "none";
    user.innerHTML = getUserName() + `
      <i class="bi-person-fill ps-1" role="img" aria-label="Person icon"></i>
    `;
    // Check if the role of the user stored in the session storage is 'admin'.
    // if not, it will not display admin actions' buttons.
    if (getUserRole() !== "admin") {
      inscriptionRequest.style.display = "none";
      deniedInscriptions.style.display = "none";
      researchMembers.style.display = "none";
    }
  } else { // Disconnected
    user.style.display = "none";
    logout.style.display = "none";
    inscriptionRequest.style.display = "none";
    deniedInscriptions.style.display = "none";
    researchMembers.style.display = "none";
    seeMyOffers.style.display = "none";
    offerAnObject.style.display = "none";
    offersAssignedToMe.style.display = "none";
  }
}
