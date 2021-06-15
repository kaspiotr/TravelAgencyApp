const {baseUrl, prodUrl} = require('./constants.js')

const axios = require('axios');
const fs = require('fs')
const random = require('random')

function generateRandomUsers(){
    let users = []
    let userNames = fs.readFileSync('./user/names.txt', 'utf8')
    let emailDomains = fs.readFileSync('./user/domains.txt', 'utf8')
    userNames = userNames.split('\n')
    userNames.pop();
    emailDomains = emailDomains.split('\n')
    emailDomains.pop();
    for(let userName of userNames){
        const [firstName, lastName] = userName.split(' ');
        users.push({
            firstName: firstName,
            lastName: lastName,
            email: `${firstName.toLowerCase()}.${lastName.toLowerCase()}@${emailDomains[random.int(0, emailDomains.length - 1)]}`,
            role: 'R'
        });
    }
    return users;
}

function postRandomUsers(n){
    axios.post(`${prodUrl}/users`, {...users[n]}).then(result => {
        if(n > 0)
            postRandomUsers(n - 1)
    }).catch(err => {
        console.log(err)
    })
}

const users = generateRandomUsers();
postRandomUsers(users.length - 1)
