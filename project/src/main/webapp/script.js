// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// Retrieves account information from the database to display in the account
// dashboard of the user.
function retrieveAccountInfo() {
  fetch("/account").then(response => {
    if (response.redirected) return;
    else return response.json();
  }).then(account => {
    // Filling out the form with the account information.
    document.getElementById("logout").href = account.logoutUrl;
    if (account.isUserBusinessOwner) {
      document.getElementById("businessName").value = account.nickname;
    } else {
      document.getElementById("nickname").value = account.nickname;
    }
    document.getElementById("street").value = account.street;
    document.getElementById("city").value = account.city;
    document.getElementById("state").value = account.state;
    document.getElementById("zipCode").value = account.zipCode;
    document.getElementById("userGreeting").innerText = "Hello, " + account.nickname;
  });
}

// Get the product set names from the database to have as options in the create
// product set form.
function retrieveProductSetDisplayNames() {
  fetch("/createProductSet").then(response => response.json()).then(names => {
    const dropdownList = document.getElementById("productSetList");
    names.forEach(name => {
      let newOption = document.createElement("option");
      newOption.value = name;
      dropdownList.appendChild(newOption);
    });
  });
}

// Get the blobstore url to submit the image form to.
function getBlobstoreUrl() {
  fetch("/getBlobstoreUrl").then(response=> response.text()).then(url => {
    const myForm = document.getElementById("analyzeImageForm");
    myForm.action = url;
    myForm.classList.remove("hidden");
  });
  
}

// Fetch the cloud vision image annotation to auto fill the create product form
// with labels.
// TODO: add a loading animation when retrieving the json.
function retrieveProductFromInfo() {
  fetch("/cloudVision").then(response => response.json()).then(productInfo => {
    console.log(productInfo);
    // Set up input image display box.
    const imageBox = document.getElementById("inputImage");
    const imageText = document.createElement('h4');
    imageText.innerText = "No image has been uploaded yet.";
    const imageUrl = document.createElement('img');
    if (productInfo != null) {
      imageText.innerText = "Image that you uploaded:"
      imageUrl.src = productInfo.imageUrl;
      imageUrl.width = 300;
    }
    imageBox.appendChild(imageText);
    // If there is no product yet, return and don't attempt to autofill the form.
    if (productInfo == null) return;
    imageBox.appendChild(imageUrl);
    document.getElementById("mainImageUrl").value = productInfo.imageUrl;

    // Store the product info as a string in the form. (This will be hidden in
    // the html.)
    const hiddenAnnotation = document.getElementById("cloudVisionAnnotation");
    hiddenAnnotation.value = JSON.stringify(productInfo);

    // Fill the tags and description based on the cloud vision annotation.
    let formattedLabels = [];
    productInfo.labels.forEach(label => formattedLabels.push({value:label, text:label}));
    const tokenAutocomplete = new TokenAutocomplete({
                name: 'labels',
                selector: '#labelsBox',
                initialTokens: formattedLabels});
    const descriptionBox = document.getElementById("productDescription");
    descriptionBox.innerText = productInfo.description;
  });
}

function refreshCreateProductForm() {
  getBlobstoreUrl();
  retrieveProductSetDisplayNames();
  retrieveProductFromInfo();
}