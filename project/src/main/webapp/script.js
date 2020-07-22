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
    const spinner = document.getElementById("spinner");
    spinner.classList.remove("is-active");
    spinner.classList.add("hidden");
  });
  
}

// Fetch the cloud vision image annotation to auto fill the create product form
// with labels.
// TODO: add a loading animation when retrieving the json.
function retrieveProductFormInfo() {
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
    if (productInfo == null) {
      imageBox.classList.remove("hidden");
      const spinner2 = document.getElementById("spinner2");
      spinner2.classList.remove("is-active");
      spinner2.classList.add("hidden");
      return;
    }
    imageBox.appendChild(imageUrl);
    document.getElementById("mainGcsUrl").value = productInfo.gcsUrl;
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

    // Clear loading symbol and show the form.
    const productForm = document.getElementById("productForm");
    const spinner2 = document.getElementById("spinner2");
    imageBox.classList.remove("hidden");
    productForm.classList.remove("hidden");
    spinner2.classList.remove("is-active");
    spinner2.classList.add("hidden");
  });
}

function refreshCreateProductForm() {
  getBlobstoreUrl();
  retrieveProductSetDisplayNames();
  retrieveProductFormInfo();
}

// Truncates the string and adds elipses to the desired length.
function truncateString(str, length) {
  const ending = '...';
  if (str.length > length) {
    return str.substring(0, length - ending.length) + ending;
  } else {
    return str;
  }
};
              
function retrieveProducts() {
  // TODO: implement filtering by product category, product set, sort by alphabet and price.
  // TODO: find a better way to put images in cards. Right now, they often get cut off.
  fetch("/viewProducts").then(response => response.json()).then(products => {
    const searchResults = document.getElementById("searchResults");
    if (products == null) {
      searchResults.innerText = "No products here!";
      return;
    }
    products.forEach(product => {
      const cardHtml = `<div class="product-card mdl-card mdl-shadow--2dp">
                          <div class="mdl-card__title" style="background-image: 
                            linear-gradient(to bottom, rgba(0,0,0,0) 80%, rgba(0,0,0,1)), 
                            url('${product.imageUrls[0]}');">
                            <h2 class="mdl-card__title-text">
                              ${product.productDisplayName}
                            </h2>
                          </div>
                          <div class="mdl-card__supporting-text">
                            ${'$' + product.price.toFixed(2) + ' - ' + truncateString(product.productDescription, 80)}
                          </div>
                          <div class="mdl-card__actions mdl-card--border">
                            <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect">
                              View
                            </a>
                            <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"
                               href="/editProduct.html?productId=${product.productId}">
                              Edit
                            </a>
                          </div>
                        </div>`;
      const card = document.createElement("div");
      card.classList.add("grid-item");
      card.innerHTML = cardHtml;
      searchResults.appendChild(card);
    });
  });
}

// Gets parameters passed in through the url, and formats them in a dictionary.
function getUrlParams() {
  var params = {};
  var parser = document.createElement('a');
  parser.href = window.location.href;
  var query = parser.search.substring(1);
  var vars = query.split('&');
  for (var i = 0; i < vars.length; i++) {
    var pair = vars[i].split('=');
    params[pair[0]] = decodeURIComponent(pair[1]);
  }
  return params;
};

function retrieveProductInfo() {
  const params = getUrlParams();
  const productId = params["productId"];
  if (productId == null) {
    document.getElementById("content").innerText = "Error: No product was selected";
    return;
  }
  const queryString = "/productInfo?productId=" + productId;
  fetch(queryString).then(response => response.json()).then(productInfo => {
    // Get the relavant data from the product info object.
    const product = productInfo.product;
    const productSet = productInfo.productSet;

    // Set up input image display box.
    const imageBox = document.getElementById("inputImage");
    const imageText = document.createElement('h4');
    const imageUrl = document.createElement('img');
    imageText.innerText = "No image was uploaded.";
    if (product != null) {
      imageText.innerText = "Image that was uploaded:"
      imageUrl.src = product.imageUrls[0];
      imageUrl.width = 300;
    }
    imageBox.appendChild(imageText);

    // If there is no product yet, return and don't attempt to autofill the form.
    if (product == null) {
      imageBox.classList.remove("hidden");
      const spinner2 = document.getElementById("spinner2");
      spinner2.classList.remove("is-active");
      spinner2.classList.add("hidden");
      return;
    }
    imageBox.appendChild(imageUrl);
    
    // Fill in the form information.
    document.getElementById("productId").value = product.productId;
    document.getElementById("mainGcsUrl").value = product.gcsUrls[0];
    document.getElementById("mainImageUrl").value = product.imageUrls[0];
    document.getElementById("productDisplayName").value = product.productDisplayName;
    document.getElementById("productSetDisplayName").value = productSet.productSetDisplayName;
    document.getElementById("productCategory").value = product.productCategory;
    document.getElementById("price").value = product.price.toFixed(2);
    document.getElementById("productDescription").value = product.productDescription;
    document.getElementById("cloudVisionAnnotation").value = product.cloudVisionAnnotation;

    // Fill the labels/tags.
    let formattedLabels = [];
    product.labels.forEach(label => formattedLabels.push({value:label, text:label}));
    const tokenAutocomplete = new TokenAutocomplete({
                name: 'labels',
                selector: '#labelsBox',
                initialTokens: formattedLabels});

    // Clear loading symbol and show the form.
    const productForm = document.getElementById("productForm");
    const spinner2 = document.getElementById("spinner2");
    imageBox.classList.remove("hidden");
    productForm.classList.remove("hidden");
    spinner2.classList.remove("is-active");
    spinner2.classList.add("hidden");
  });
}

function refreshProductInfoPage() {
  getBlobstoreUrl();
  retrieveProductInfo();
}

function loadProduct() {

  fetch("/product-list").then(response => response.json()).then((products) => {

    const productListElement = document.getElementById("product-list");
    productListElement.innerHTML = "";
    
 
    products.forEach((product) => {  
      productListElement.appendChild(createProductElement(product));
      productListElement.appendChild(document.createElement("br"));
    })
  });
}


function createProductElement(product) {
  const linebreak = document.createElement("br");

  const productElement = document.createElement("li");
  productElement.className = "product";

  const titleElement = document.createElement("span");
  titleElement.innerText = product.productName+": "+product.productId;

  productElement.appendChild(titleElement);
  productElement.appendChild(linebreak);

  return productElement;
}

function loadReferenceImages(){

  fetch("/view-reference-image").then(response => response.json()).then((referenceImages) => {

    console.log(referenceImages);
    const referenceImageElement = document.getElementById("reference-image-list");
    referenceImageElement.innerHTML = "";
    
 
    referenceImages.forEach((referenceImage) => {  
      referenceImageElement.appendChild(createReferenceImageElement(referenceImage));
      referenceImageElement.appendChild(document.createElement("br"));
    })
  });
}


function createReferenceImageElement(referenceImage) {
  const linebreak = document.createElement("br");

  const referenceImageElement = document.createElement("li");
  referenceImageElement.className = "referenceImage";

  const titleElement = document.createElement("span");
  titleElement.innerText = referenceImage;

  referenceImageElement.appendChild(titleElement);
  referenceImageElement.appendChild(linebreak);

  return referenceImageElement;
}