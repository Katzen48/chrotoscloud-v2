const https = require('https');
const filePath = require('path');
const fs = require('fs');

module.exports = async function(url, path, user, password) {
    console.log('Downloading \'' + url + '\'');

    // Download Newest Server Software Version
    await new Promise((resolve, reject) => {
        let options = {};

        if (user && password) {
           options = {
               headers: {
                   'Authorization': 'Basic ' + Buffer.from(user + ':' + password).toString('base64')
               },
               method: 'GET'
            }
        }

        https.get(url, options, (res) => {
            let dir = filePath.dirname(path);

            if (!fs.existsSync(dir)) {
                fs.mkdirSync(dir, {recursive: true});
            }

            const stream = fs.createWriteStream(path);
            res.pipe(stream);

            stream.on('finish', () => {
                stream.close();
                console.log('Finished downloading');
                resolve();
            });

            stream.on('error', (e) => {
                stream.close();
                reject('Error whilst downloading from \'' + url + '\': ' + e);
            })
        })
    });
}