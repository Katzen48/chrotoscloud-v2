const https = require('https');
const fs = require('fs');

module.exports = async function(url, path) {
    // Download Newest Server Software Version
    await new Promise((resolve, reject) => {
        https.get(url, (res) => {
            const stream = fs.createWriteStream(path + '/server.jar');
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