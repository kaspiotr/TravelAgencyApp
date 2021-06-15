const {baseUrl, prodUrl} = require('./constants.js')

const axios = require('axios');
const fs = require('fs')
const random = require('random')

function generateRandomParticipants(n){
    let participants = []
    let firstNames = fs.readFileSync('./participant/firstNames.txt', 'utf8')
    let lastNames = fs.readFileSync('./participant/lastNames.txt', 'utf8')
    firstNames = firstNames.split('\n')
    firstNames.pop();
    lastNames = lastNames.split('\n')
    lastNames.pop();
    for(let i = 0; i < n; i++){
        participants.push({
            firstName: firstNames[random.int(0, firstNames.length - 1)],
            lastName: lastNames[random.int(0, lastNames.length - 1)],
            birthDate: new Date(random.int(1900, 2020), random.int(0, 11), random.int(0, 32))
        });
    }
    return participants;
}

function postRandomParticipants(n, participants, reservation){
    if(n < 0)
        return new Promise(resolve => {resolve("Continue")})
    return axios.post(`${baseUrl}/participants?reservationId=${reservation.id}`, {...participants[n]}).then(result => {
        if(n > 0)
            return postRandomParticipants(n - 1, participants, reservation)
    }).catch(err => {
        console.log(err.response.data)
    })
}

function fetchData(){
    return axios.get(`${baseUrl}/reservations`)
        .then(res => res.data)
        .catch(err => undefined)
}

fetchData().then(res => {
    const allReservations = res;
    console.log("Reservations fetched")
    for (let i = 0; i < allReservations.length; i++){
        const isPaid = (random.int(0, 1) === 1);
        const reservation = allReservations[i];
        const n = isPaid ? reservation.numberOfParticipants : random.int(0, reservation.numberOfParticipants - 1)
        const participants = generateRandomParticipants(n)
        postRandomParticipants(n - 1, participants, reservation).then(res => {
            if(n === reservation.numberOfParticipants){
                axios.put(`${baseUrl}/reservations/${reservation.id}/changeStatus?status=P`)
                    .then(res => {})
                    .catch(err => { console.log(err); })
            }
        })
    }
})

// const participants = generateRandomParticipants(5);
// console.log(participants)
