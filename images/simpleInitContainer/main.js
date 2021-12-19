// Fetch newest paper version

const BASE_URL = 'https://papermc.io/api/v2/projects/';
const path = '/workdir'
const fetch = require('cross-fetch')
const fs = require('fs');
const https = require('https');

(async () => {
    const VERSIONS_URL = BASE_URL + process.env.SERVER_SOFTWARE + '/versions';
    const VERSION = process.env.SERVER_SOFTWARE_VERSION;

    let versionResponse = await fetch(`${VERSIONS_URL}/${VERSION}`)
    let json = await versionResponse.json();
    let newestBuild = json.builds[json.builds.length - 1];

    await new Promise((resolve) => {
        https.get(`${VERSIONS_URL}/${VERSION}/builds/${newestBuild}/downloads/paper-${VERSION}-${newestBuild}.jar`, (res) => {
            const stream = fs.createWriteStream(path + '/server.jar');
            res.pipe(stream);

            stream.on('finish', () => {
                stream.close();
                resolve();
            });
        })
    });
    
    fs.writeFileSync(path + '/eula.txt', 'eula=true');
})();