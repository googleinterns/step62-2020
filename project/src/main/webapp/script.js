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
function getBlobstoreUrl(isEditing) {
  let queryString = "/getBlobstoreUrl";
  if (isEditing) {
    const params = getUrlParams();
    const productId = params["productId"];
    queryString = queryString + "?edit=true&editProductId="+productId;
  } 
  fetch(queryString).then(response => response.text()).then(url => {
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
    hiddenAnnotation.value = JSON.stringify(productInfo.annotation);

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
  getBlobstoreUrl(false); // false indicates we are creating the product the first time.
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

// TODO: add text and image search as an option here.
// Display cards containing the products.
function retrieveProducts() {
  const searchResults = document.getElementById("searchResults");
  searchResults.innerHTML = "";
  const spinner = document.getElementById("spinner");
  spinner.classList.add("is-active");

  let productSetDisplayName = document.getElementById("productSetDisplayName").value;
  if (productSetDisplayName === "") productSetDisplayName = "none";
  const productCategory = document.getElementById("productCategory").value;
  const sortOrder = document.getElementById("sortOrder").value;
  let queryString = "/viewProducts?productSetDisplayName=" + productSetDisplayName + 
                    "&productCategory=" + productCategory + 
                    "&sortOrder=" + sortOrder + 
                    "&businessId=getFromDatabase";

  // Check if there is a search query, and add to the query string.
  const params = getUrlParams();
  const searchId = params["searchId"];
  if (searchId != null) queryString = queryString + "&searchId=" + searchId;

  fetch(queryString).then(response => response.json()).then(products => {
    if (products == null || products.length == 0) {
      searchResults.innerHTML = "<h4>No products here!</h4>";
      spinner.classList.remove("is-active");
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
                            <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"
                               href="/product.html?productId=${product.productId}">
                              View
                            </a>
                            <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"
                               href="/editProduct.html?productId=${product.productId}">
                              Edit
                            </a>
                            <a class="mdl-button mdl-button--accent mdl-js-button mdl-js-ripple-effect"
                               href="/deleteProduct.html?productId=${product.productId}">
                              Delete
                            </a>
                          </div>
                        </div>`;
      const card = document.createElement("div");
      card.classList.add("grid-item");
      card.innerHTML = cardHtml;
      searchResults.appendChild(card);
    });
    spinner.classList.remove("is-active");
  });
}

function refreshViewProductsPage() {
  retrieveProductSetDisplayNames();
  setBrowseInputs();
  retrieveProducts();
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

// Retrieves the information of a product from datastore, and autfills the
// edit product form with that info.
function retrieveProductInfo() {
  const params = getUrlParams();
  const productId = params["productId"];
  let refreshImage = params["refreshImage"];
  if (productId == null) {
    document.getElementById("content").innerText = "Error: No product was selected";
    return;
  }
  if (refreshImage == null) {
    refreshImage = false;
  } else {
    refreshImage = true;
  }
  if (refreshImage) retrieveProductFormInfo();
  const queryString = "/productInfo?productId=" + productId;
  fetch(queryString).then(response => response.json()).then(productInfo => {
    // Get the relavant data from the product info object.
    const product = productInfo.product;
    const productSet = productInfo.productSet;

    // If the user had submitted a new photo to be analyzed, only keep the following
    // information.
    if (refreshImage && product!=null && productSet!=null) {
      document.getElementById("productId").value = product.productId;
      document.getElementById("productDisplayName").value = product.productDisplayName;
      document.getElementById("productSetDisplayName").value = productSet.productSetDisplayName;
      document.getElementById("productCategory").value = product.productCategory;
      document.getElementById("price").value = product.price.toFixed(2);
      document.getElementById("productDescription").value = product.productDescription;
      return;
    }

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
    retrieveOptionalImages(product);
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

// Retrieves and displays the optional images in a table.
function retrieveOptionalImages(product) {
  const imageUrls = product.imageUrls.slice(1);
  const gcsUrls = product.gcsUrls.slice(1);
  document.getElementById("optionalGcsUrls").value = JSON.stringify(gcsUrls);
  document.getElementById("optionalImageUrls").value = JSON.stringify(imageUrls);
  const tableBody = document.getElementById("imageTableBody");

  // ImageUrls and GcsUrls are guaranteed to have the same length
  var i;
  for (i = 0; i < imageUrls.length; i++) {
    const row = document.createElement("tr");
    const imageCell = document.createElement("td");
    const imageLink = document.createElement("a");
    imageLink.href = imageUrls[i];
    imageLink.target = "_blank"; // Open image in a new tab.
    const image = document.createElement("img");
    image.src = imageUrls[i];
    image.style.maxHeight = "50px";
    imageLink.appendChild(image);
    imageCell.appendChild(imageLink);
    const buttonCell = document.createElement("td");
    const button = document.createElement("input");
    button.type = "button"
    button.setAttribute('onclick','deleteRow(this)');
    button.value = "Delete";
    buttonCell.appendChild(button);
    const imageUrl = document.createElement("td");
    imageUrl.innerText = imageUrls[i];
    imageUrl.style.display = "none";
    const gcsUrl = document.createElement("td");   
    gcsUrl.innerText = gcsUrls[i];
    gcsUrl.style.display = "none";
    row.appendChild(imageCell);
    row.appendChild(buttonCell);
    row.appendChild(gcsUrl);
    row.appendChild(imageUrl);
    tableBody.appendChild(row);
  }
}

function deleteRow(r) {
  var i = r.parentNode.parentNode.rowIndex;
  const table = document.getElementById("imageTable");
  table.deleteRow(i);
  // Loop through all the rows in the table and get the gcs and imageUrls.
  var j;
  const gcsUrls = [];
  const imageUrls = [];
  // First row ist just the headers of the columns, so we don't include that
  for (j = 1; j < table.rows.length; j++) {
    gcsUrls.push(table.rows[j].cells[2].innerText); // 2nd column corresponds to gcsUrl
    imageUrls.push(table.rows[j].cells[3].innerText); // 3rd column corresponds to imageUrl
  }
  document.getElementById("optionalGcsUrls").value = JSON.stringify(gcsUrls);
  document.getElementById("optionalImageUrls").value = JSON.stringify(imageUrls);
}

function refreshProductInfoPage() {
  getBlobstoreUrl(true); // True indicates we are editing the product.
  retrieveProductSetDisplayNames();
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

function loadProductSet(){

  fetch("/product-set-list").then(response => response.json()).then((productSets) => {

    console.log(productSets);
    const productSetElement = document.getElementById("product-set-list");
    productSetElement.innerHTML = "";
    
 
    productSets.forEach((productSet) => {  
      productSetElement.appendChild(createProductSetElement(productSet));
      productSetElement.appendChild(document.createElement("br"));
    })
  });
}


function createProductSetElement(productSet) {
  const linebreak = document.createElement("br");

  const productSetElement = document.createElement("li");
  productSetElement.className = "ProductSet";

  const titleElement = document.createElement("span");
  titleElement.innerText = productSet.setName;

  productSetElement.appendChild(titleElement);
  productSetElement.appendChild(linebreak);

  return productSetElement;
}

function deleteProduct() {
  const params = getUrlParams();
  const productId = params["productId"];
  document.getElementById("deleteProduct").href = "/deleteProduct?productId="+productId;
}

// Helper functions for the slideshow on the product page.
function plusDivs(n) {
  showDivs(slideIndex += n);
}
function currentDiv(n) {
  showDivs(slideIndex = n);
}
function showDivs(n) {
  var i;
  var x = document.getElementsByClassName("mySlides");
  var dots = document.getElementsByClassName("demo");
  if (n > x.length) {slideIndex = 1}    
  if (n < 1) {slideIndex = x.length}
  for (i = 0; i < x.length; i++) {
    x[i].style.display = "none";  
  }
  for (i = 0; i < dots.length; i++) {
    dots[i].className = dots[i].className.replace(" w3-red", "");
  }
  x[slideIndex-1].style.display = "block";  
  dots[slideIndex-1].className += " w3-red";
}

// Retrieve and display product on the view product page.
function viewProduct() {
  const params = getUrlParams();
  const productId = params["productId"];
  const queryString = "/productInfo?productId=" + productId;
  fetch(queryString).then(response => response.json()).then(productInfo => {
    const product = productInfo.product;
    const productSet = productInfo.productSet;
    const business = productInfo.business;
    // Fill out the appropriate places on the form.
    document.getElementById("productPath").innerText = 
      business.businessDisplayName + " / " + 
      product.productCategory.split("-")[0] + " / " + 
      productSet.productSetDisplayName;
    document.getElementById("productDisplayName").innerText = product.productDisplayName;

    // Add images to slideshow.
    const slideshowImages = document.getElementById("slideshowImages");
    const slideshowPane = document.getElementById("slideshowPane");
    let counter = 1;
    product.imageUrls.forEach(imageUrl => {
      const newImage = document.createElement("img");
      newImage.classList.add("mySlides");
      newImage.src = imageUrl;
      newImage.style = "margin: 0 auto; height: 100%; max-width:700px;";
      const newButton = document.createElement("button");
      newButton.classList.add("w3-button");
      newButton.classList.add("demo");
      newButton.innerText = counter;
      const inputCounter = counter;
      newButton.onclick = function () {currentDiv(inputCounter);};
      counter++;
      slideshowImages.appendChild(newImage);
      slideshowPane.appendChild(newButton);
    });
    showDivs(slideIndex);

    document.getElementById("price").innerText = "$" + product.price.toFixed(2);
    document.getElementById("productDescription").innerText = product.productDescription;
    const labels = document.getElementById("labels");
    product.labels.forEach(label => {
      const chip = document.createElement("span");
      chip.classList.add("mdl-chip");
      chip.innerHTML = `<span class="mdl-chip__text">${label}</span>`;
      labels.appendChild(chip);
    });
    document.getElementById("businessDisplayName").innerText = 
      "Sold by: " + business.businessDisplayName;
    document.getElementById("businessAddress").innerText = 
      `Business Address: ${business.street}, ${business.city} ${business.state}, ${business.zipCode}`;
    const similarWebsites = document.getElementById("similarWebsites");
    (JSON.parse(product.cloudVisionAnnotation)).webUrls.forEach(url => {
      const link = document.createElement("a");
      link.href = url;
      link.innerText = url;
      similarWebsites.appendChild(link);
      similarWebsites.appendChild(document.createElement("br"));
    });

    // Remove the loading spinner and load the page.
    const spinner = document.getElementById("spinner");
    spinner.classList.remove("is-active");
    spinner.classList.add("hidden");
    document.getElementById("productContent").classList.remove("hidden");
  });
}

// Gets a blobstore url.
function setupImageUpload(urlPath) {
  console.log("Setting up upload url.");
  const searchForm = document.getElementById("searchForm");
  const spinnerImage = document.getElementById("spinnerImage");
  const imageButton = document.getElementById("imageUpload");
  const productCategory = document.getElementById("productCategorySearch");
  spinnerImage.style.display = "block";
  fetch("/getBlobstoreUrlSearch?urlPath=" + urlPath)
    .then(response => response.text())
    .then(url => {
      searchForm.action = url;
      searchForm.enctype = "multipart/form-data";
      spinnerImage.style.display = "none";
      imageButton.required = true;
      imageButton.style.display = "block";
      productCategory.style.display = "block";
    });
}

// Toggles between showing and hiding the option to search by image.
function toggleImageUpload(urlPath) {
  const imageButton = document.getElementById("imageUpload");
  const searchForm = document.getElementById("searchForm");
  const productCategory = document.getElementById("productCategorySearch");
  if (imageButton.style.display === "block") {
    searchForm.action = urlPath;
    searchForm.enctype = "application/x-www-form-urlencoded";
    imageButton.required = false;
    imageButton.style.display = "none";
    productCategory.style.display = "none";
  } else {
    setupImageUpload(urlPath);
  }
}

// Validates the form to check there is a non empty input.
function checkSearchForm() {
  return document.getElementById("imageUpload").files.length > 0 ||
         // Remove all white space to check if text input is non empty.
         document.getElementById("textSearch").value.replace(/\s/g, '').length > 0;
}


// TODO: add textSearch as an option (eventually image search as well).
// Display cards on the browse page. These cards don't have edit or delete
// functionality.
function browseProducts() {
  const searchResults = document.getElementById("searchResults");
  searchResults.innerHTML = "";
  const spinner = document.getElementById("spinner");
  spinner.classList.add("is-active");

  let productSetDisplayName = document.getElementById("productSetDisplayName").value;
  if (productSetDisplayName === "") productSetDisplayName = "none";
  let businessId = document.getElementById("businessId").value;
  const productCategory = document.getElementById("productCategory").value;
  const sortOrder = document.getElementById("sortOrder").value;
  let queryString = "/browse?productSetDisplayName=" + productSetDisplayName + 
                    "&productCategory=" + productCategory + 
                    "&sortOrder=" + sortOrder + 
                    "&businessId=" + businessId;

  // Check if there is a search query, and add to the query string.
  const params = getUrlParams();
  const searchId = params["searchId"];
  if (searchId != null) queryString = queryString + "&searchId=" + searchId;

  fetch(queryString).then(response => response.json()).then(products => {
    if (products == null || products.length == 0) {
      searchResults.innerHTML = "<h4>No products here!</h4>";
      spinner.classList.remove("is-active");
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
                            <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"
                               href="/product.html?productId=${product.productId}">
                              View
                            </a>
                          </div>
                        </div>`;
      const card = document.createElement("div");
      card.classList.add("grid-item");
      card.innerHTML = cardHtml;
      searchResults.appendChild(card);
    });
    spinner.classList.remove("is-active");
  });
}

// Fill out the businesses dropdown menu with info from datastore.
function retrieveBusinesses() {
  fetch("/createBusinessAccount").then(response => response.json()).then(names => {
    const dropdownList = document.getElementById("businessId");
    names.forEach(name => {
      let newOption = document.createElement("option");
      newOption.value = name.businessId;
      newOption.innerText = name.businessDisplayName;
      dropdownList.appendChild(newOption);
    });
  });
}

function setBrowseInputs() {
  const params = getUrlParams();
  const searchId = params["searchId"];
  if (searchId != null) {
    fetch("/searchInfo?searchId="+searchId).then(response => response.json())
    .then(searchInfo => {
      if (searchInfo.textSearch != null) {
        document.getElementById("textSearch").value = searchInfo.textSearch;
      }
      if (searchInfo.imageUrl != null) {
        document.getElementById("uploadedImage").src = searchInfo.imageUrl;
        document.getElementById("uploadedImageBox").style.display = "block";
      }
    });
  }
}

function refreshBrowsePage() {
  retrieveProductSetDisplayNames();
  retrieveBusinesses();
  setBrowseInputs();
  browseProducts();
}

function updateCreateProductUrl() {
  console.log("Setting up upload url.");
  const images = document.getElementById("images");
  const productForm = document.getElementById("productForm");
  const spinner3 = document.getElementById("spinner3");
  const path = new URL(productForm.action);
  if (images.files.length > 0 && path.pathname === "/createProduct") {
    spinner3.style.display = "block";
    fetch("/getBlobstoreUrlSearch?urlPath=createProduct")
      .then(response => response.text())
      .then(url => {
        productForm.action = url;
        productForm.enctype = "multipart/form-data";
        spinner3.style.display = "none";
        images.required = true;
      });
  } else {
    productForm.action = "/createProduct";
    productForm.enctype = "application/x-www-form-urlencoded";
    images.required = false;
  }
}