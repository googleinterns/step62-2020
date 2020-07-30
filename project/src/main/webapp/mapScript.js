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


/* 
Basic HTML:
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyC8PrUIlWAeCEONhP97HFLCFqB8R2dV8FU"></script>
    <body onload="createMap();">
    <div id="map"></div>
*/

//I'm doing all this assuming we'll call the map in html "map"

var geocoder;
var map;
var directionsService;
var directionsRenderer;

function createMap() {
    geocoder = new google.maps.Geocoder();

    map = new google.maps.Map(
        document.getElementById('map'),
        {center: {lat: 37.422, lng: -122.084}, zoom: 16});
}

function setAccountMap() {
    geocoder = new google.maps.Geocoder();

    var _lat = document.getElementById("lat").value;
    var _lng = document.getElementById("lng").value;

    map = new google.maps.Map(
        document.getElementById('map'),
        {center: {lat: _lat, lng: _lng}, zoom: 16});

    createMarker({lat: _lat, lng: _lng});
    document.getElementById("map").style.display = "block";
}

function productsMap(latLng) {
    geocoder = new google.maps.Geocoder();

    var _lat = latLng.latitude;
    var _lng = latLng.longitude;

    map = new google.maps.Map(
        document.getElementById('map'),
        {center: {lat: _lat, lng: _lng}, zoom: 16});

    createMarker({lat: _lat, lng: _lng});
    document.getElementById("map").style.display = "block";
}

function createMarker(coordinate) {
    var marker = new google.maps.Marker({position: coordinate, map: map});
}

function setLatLng() {
    const address = document.getElementById("street").value + ", " + document.getElementById("city").value +
      ", " + document.getElementById("state").value + ", " + document.getElementById("zipCode").value;

    function getLatLng(results, status) {
        if (status == 'OK') {
            document.getElementById("lat").value = results[0].geometry.location.lat();
            document.getElementById("lng").value = results[0].geometry.location.lng();
            createMarker(results[0].geometry.location);
            map.setCenter(results[0].geometry.location);
            document.getElementById("map").style.display = "block";
        } else {
            alert('Geocode was not successful for the following reason: ' + status);
        }
    }
    geocoder.geocode( { 'address': address}, getLatLng);
}


