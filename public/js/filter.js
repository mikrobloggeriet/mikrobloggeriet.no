// Filtering approach inspired by https://diskprices.com/
//
// - HTML always contains all content
// - Javascript toggles visibility of elements.

const navigate = (element) => {
  history.pushState({}, "", element.href);

  for (const docSelector of document.querySelectorAll(".docSelector")) {
    docSelector.hidden = docSelector.dataset.cohort !== element.dataset.cohort
  }

  for (const docView of document.querySelectorAll(".docView")) {
    docView.hidden = docView.dataset.cohort !== element.dataset.cohort;
  }

}

export const init = () => {
  for (const cohortSelector of document.querySelectorAll(".cohortSelector")) {
    cohortSelector.addEventListener("click", function (event) {
      event.preventDefault()
      navigate(cohortSelector);
    });
  }
}

init();
