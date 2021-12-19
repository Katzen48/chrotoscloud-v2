// Fetch newest paper version

const BASE_URL = 'https://papermc.io/api/v2/projects/';
const path = '/workdir'
const fetch = require('cross-fetch')
const fs = require('fs');
const https = require('https');

(async () => {
    const SOFTWARE = process.env.SERVER_SOFTWARE;
    const VERSIONS_URL = BASE_URL + SOFTWARE + '/versions';
    const VERSION = process.env.SERVER_SOFTWARE_VERSION;

    console.log('Fetching newest Build of', SOFTWARE, 'of version', VERSION);

    let versionResponse = await fetch(`${VERSIONS_URL}/${VERSION}`)
    let json = await versionResponse.json();
    let newestBuild = json.builds[json.builds.length - 1];

    console.log('Starting download of build', newestBuild);

    await new Promise((resolve) => {
        https.get(`${VERSIONS_URL}/${VERSION}/builds/${newestBuild}/downloads/${SOFTWARE}-${VERSION}-${newestBuild}.jar`, (res) => {
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