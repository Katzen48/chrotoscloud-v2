// Fetch newest paper version

const BASE_URL = 'https://papermc.io/api/v2/projects/velocity/versions';
const path = '/workdir'
const fetch = require('cross-fetch')
const fs = require('fs');
const https = require('https');

(async () => {
    let version = process.env.PAPER_VERSION || '3.1.0';

    let versionResponse = await fetch(`${BASE_URL}/${version}`)
    let json = await versionResponse.json();
    let newestBuild = json.builds[json.builds.length - 1];

    await new Promise((resolve, reject) => {
        https.get(`${BASE_URL}/${version}/builds/${newestBuild}/downloads/velocity-${version}-${newestBuild}.jar`, (res) => {
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