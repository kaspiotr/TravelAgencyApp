const {baseUrl, prodUrl} = require('./constants.js')

const axios = require('axios');
const fs = require('fs')
const random = require('random')

function generateRandomReservations(n, trip){
    let reservations = []
    for(let i = 0; i < n; i++){
        const upperLimit = Math.floor(trip.availablePlacesNo / 4)
        reservations.push({
            date: new Date(2021, trip.startDate[1] - 1, trip.startDate[2] + 1 - random.int(0, 60)),
            status: 'N',
            priceSnapshot: 0,
            numberOfParticipants: random.int(1, upperLimit <= 1 ? 1 : upperLimit)
        });
    }
    return reservations;
}

function postRandomReservations(n, reservations, userId, tripId){
    if (n < 0)
        return
    axios.post(`${prodUrl}/reservations?userId=${userId}&tripId=${tripId}`, {...reservations[n]}).then(result => {
        if(n > 0)
            postRandomReservations(n - 1, reservations, userId, tripId)
    }).catch(err => {
        console.log(err.response.data)
    })
}

function fetchData(){
    return Promise.all([
        axios.get(`${prodUrl}/users`)
            .then(res => res.data)
            .catch(err => undefined),
        axios.get(`${prodUrl}/trips`)
            .then(res => res.data)
            .catch(err => undefined)
    ])
}

fetchData().then(res => {
    const allUsers = res[0];
    const allTrips = res[1];
    console.log("Users fetched")
    console.log("Trips fetched")
    for(let i = 0; i < allUsers.length; i++){
        const n = random.int(0, 3);
        for(let j = 0; j < n; j++){
            const trip = allTrips[random.int(0, allTrips.length - 1)]
            const reservations = generateRandomReservations(1, trip);
            postRandomReservations(0, reservations, allUsers[i].id, trip.id)
        }
    }
})

