/**
 * Create a 'back to top' button. Define when it is visible or hidden and what
 * happen when clicking on it.
 * Button is visible at the instant the user begin to scroll on the page and is
 * hidden if the user is at the top off the page.
 * Clicking on the button takes the user back to the top off the page.
 *
 * @param pageDiv the element of the page in which the button need to be
 * created.
 */
function createBackToTopButton(pageDiv) {
  // Create the wrapper in which the button will be inserted.
  const buttonWrapper = document.createElement('div');
  buttonWrapper.id = "backToTopButtonWrapper";
  // Create the aesthetic of the button.
  const button = document.createElement('i');
  button.id = "backToTopButton";
  button.className = "bi-arrow-up-circle-fill";
  // Change the button form while the cursor is over it. The cursor will also
  // look like it is pointing something.
  button.addEventListener("mouseover", () => {
    button.className = "bi-arrow-up-circle";
    button.style = "cursor:pointer";
    buttonWrapper.style.display = "block";
  });
  // Reset button if cursor is no longer over it.
  button.addEventListener("mouseleave", () => {
    button.className = "bi-arrow-up-circle-fill";
  });
  // Create the action to go back to the top off the page.
  button.addEventListener("click", () => {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
  });
  // Append the button to the wrapper and the wrapper to the page.
  buttonWrapper.appendChild(button);
  pageDiv.appendChild(buttonWrapper);
  // If the user scroll on the page, makes the 'backToTop' button visible.
  window.onscroll = () => {
    if (document.body.scrollTop > 20 || document.documentElement.scrollTop
      > 20) {
      buttonWrapper.style.display = "block";
    } else {
      buttonWrapper.style.display = "none";
    }
  }
}

/**
 * Create a loading animation inside off the specified button element.
 *
 * @param element in which we want an animation.
 */
function buttonStartLoading(element) {
  element.innerHTML = `
    <span class="spinner-border spinner-border-sm" role="status" 
      aria-hidden="true"></span>
      Chargement...
  `;
}

export {createBackToTopButton, buttonStartLoading};