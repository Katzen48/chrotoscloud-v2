// Fetch newest paper version

const BASE_URL = 'https://papermc.io/api/v2/projects/paper/versions';
const path = '/workdir/server.jar'
const fs = require('fs');
const https = require('https');

(async () => {
    let version = process.env.PAPER_VERSION || '1.17.1';

    let versionResponse = await fetch(`${BASE_URL}/${version}`)
    let newestBuild = JSON.parse(versionResponse).builds.sort().reverse()[0];

    await new Promise((resolve, reject) => {
        https.get(`${BASE_URL}/${version}/builds/${newestBuild}/downloads/paper-${version}-${newestBuild}.jar`, (res) => {
            const stream = fs.createWriteStream(path);
            res.pipe(stream);

            stream.on('finish', () => {
                stream.close();
                resolve();
            });
        })
    })
})();