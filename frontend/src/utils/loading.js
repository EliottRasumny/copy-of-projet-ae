/**
 * Insert a loading animation in the element.
 *
 * @param element in which the animation is inserted.
 */
export function createLoadAnimation(element) {
  element.innerHTML = `
    <div class="text-center">
      <div class="spinner-border" role="status" id="spinner-loading">
        <span class="visually-hidden">Chargement...</span>
      </div>
    </div>
  `;
}