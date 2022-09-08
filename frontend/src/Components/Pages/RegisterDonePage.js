import {displaySuccess} from "../../utils/feedback";

/**
 * Render the RegisterDonePage and display the message to the user saying that
 * the inscription is done and needs an admin's approval
 */
function RegisterDonePage() {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = `
    <div class="container py-5">
      <div class="row d-flex justify-content-center align-items-center h-100">
        <div id="inscription-feedback" class="mt-3"></div>
      </div>
    </div>
  `;
  displaySuccess(
    `<i class="bi-stars"></i>`
    + " Merci de vous &ecirc;tre inscrit(e) ! "
    + `<i class="bi-stars"></i>`
    + "</br> Vous pourrez vous "
    + "connecter une fois qu'un administrateur aura accept&eacute; votre "
    + "inscription.",
    "inscription-feedback"
  )
}

export default RegisterDonePage;