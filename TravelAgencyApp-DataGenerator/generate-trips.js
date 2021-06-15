const {baseUrl, prodUrl} = require('./constants.js')

const axios = require('axios');
const fs = require('fs')
const random = require('random')

function generateRandomTrips(){
    let trips = []
    let tripNames = fs.readFileSync('./trip/names.txt', 'utf8');
    let countries = fs.readFileSync('./trip/countries.txt', 'utf8');
    let descriptions = fs.readFileSync('./trip/description.txt', 'utf8');
    tripNames = tripNames.split('\n');
    tripNames.pop();
    countries = countries.split('\n');
    countries.pop();
    descriptions = descriptions.split('.');
    descriptions = descriptions.map(des => des[0] === ' ' ? des.substring(1) : des);
    descriptions = descriptions.map(des => des[0] === '\n' ? des.substring(1) : des);
    descriptions.pop();
    for(let tripName of tripNames){
        const startMonth = random.int(0, 11);
        const startDay = random.int(2, 32);
        const duration = random.int(3, 14)
        const startDate = new Date(2021, startMonth, startDay)
        const endDate = new Date(2021, startMonth, startDay + duration)
        trips.push({
            name: tripName,
            description: descriptions[random.int(0, descriptions.length - 1)],
            country: countries[random.int(0, countries.length - 1)],
            startDate: startDate,
            endDate: endDate,
            basePrice: random.int(5, 200) * 100,
            availablePlacesNo: random.int(1, 20) * 10
        });
    }
    return trips;
}

function postRandomTrips(n){
    axios.post(`${prodUrl}/trips`, {...trips[n]}).then(result => {
        if(n > 0)
            postRandomTrips(n - 1)
    }).catch(err => {
        console.log(err)
    })
}

const trips = generateRandomTrips();
console.log(trips)
postRandomTrips(trips.length - 1)
