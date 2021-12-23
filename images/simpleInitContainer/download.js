const https = require('https');
const fs = require('fs');

module.exports = async function(url, path, user, password) {
    console.log('Downloading \'' + url + '\'');

    // Download Newest Server Software Version
    await new Promise((resolve, reject) => {
        let options = {};

        if (user && password) {
            options.headers = {
                'Authorization': 'Basic ' + new Buffer(user + ':' + password).toString('base64')
            }
        }

        https.get(url, options, (res) => {
            const stream = fs.createWriteStream(path);
            res.pipe(stream);

            stream.on('finish', () => {
                stream.close();
                resolve();
            });

            stream.on('error', () => {
                stream.close();
                reject('Error whilst downloading from \'' + url + '\'');
            })
        })
    });
}