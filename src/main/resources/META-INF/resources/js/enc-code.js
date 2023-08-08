var encEmail = "a2FydGVuQHRoZWF0ZXItb3NuYWJydWVjay5kZQ==";
const form = document.getElementById("contact-mail");
form.setAttribute("href", "mailto:".concat(atob(encEmail)));
var encTel = "KzQ5NTQxNzYwMDA3Ng==";
const form2 = document.getElementById("contact-phone");
form2.setAttribute("href", "tel:".concat(atob(encTel)));