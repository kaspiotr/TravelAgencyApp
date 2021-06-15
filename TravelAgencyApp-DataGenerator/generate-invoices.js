const axios = require('axios');
const fs = require('fs')
const random = require('random')

function generateRandomInvoices(n = 10){
    let invoices = []
    for(let i = 0; i < n; i++){
        invoices.push({
            date: new Date(2021, random.int(0, 11), random.int(2, 32)),
            totalPrice: random.int(5, 2000) * 100,
        });
    }
    return invoices;
}

function postRandomParticipants(n){
    axios.post('http://localhost:8080/api/v1/invoices?id=someID', {...invoices[n]}).then(result => {
        if(n > 0)
            postRandomParticipants(n - 1)
    }).catch(err => {
        console.log(err)
    })
}

const invoices = generateRandomInvoices();
console.log(invoices)
