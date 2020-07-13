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

function loadProductSets() {

  fetch("/product-set-list").then(response => response.json()).then((productSets) => {
    console.log(productSets);
    const setListElement = document.getElementById("product-set-list");
    setListElement.innerHTML = "";
    
 
    productSets.forEach((productSet) => {  
      setListElement.appendChild(createProductSetElement(productSet));
      setListElement.appendChild(document.createElement("br"));
    })
  });
}


function createProductSetElement(productSet) {
  const linebreak = document.createElement("br");

  const productSetElement = document.createElement("li");
  productSetElement.className = "productSet";

  const titleElement = document.createElement("span");
  titleElement.innerText = productSet.setId+": "+productSet.setName;

  productSetElement.appendChild(titleElement);
  productSetElement.appendChild(linebreak);

  return productSetElement;
}

function deleteProductSet(){
    const productSet = document.getElementById("delete-product-set");
    fetch("/delete-product-set?productSet="+productSet, {method:"POST"}).then(() => loadProductSets());
}

function listProducts(){

    fetch("/product-list").then(response => response.json()).then((products) => {
        console.log(products);
    
    const productListElement = document.getElementById("products-list");
    productListElement.innerHTML = "";

    products.forEach((product) => {
        productListElement.appendChild(createProductElement(product));
        productListElement.appendChild(document.createElement("br"));
    })
  });
}

function createProductElement(product){
    const linebreak = document.createElement("br");

    const productElement = document.createElement("li");
    productElement.className = "product";

    const titleElement = document.createElement("span");
    titleElement.innerText = product.productName+": "+product.productCategory;

    productElement.appendChild(titleElement);
    productElement.appendChild(linebreak);

    return productElement;
}

