// Fetch newest paper version

const BASE_URL = 'https://papermc.io/api/v2/projects/';
const CLOUD_BASE_URL = 'https://maven.pkg.github.com/katzen48/chrotoscloud-v2/'
const PATH = '/workdir'
const PropertiesReader = require('properties-reader');
const fetch = require('cross-fetch')
const fs = require('fs');
const k8s = require('@kubernetes/client-node');
const download = require('./download');
const resolveMaven = require('./resolveMaven');

const properties = PropertiesReader('/etc/podinfo/labels');
const GAMEMODE = properties.get("net.chrotos.chrotoscloud.gameserver/gamemode").replaceAll('"', '');

if (!GAMEMODE || GAMEMODE == '') {
    throw new Error('Game Mode was not defined!')
}

const kc = new k8s.KubeConfig();
kc.loadFromDefault();
const k8sApi = kc.makeApiClient(k8s.CustomObjectsApi);

(async () => {
    let gameMode;
    try {
        gameMode = await k8sApi.getNamespacedCustomObject('chrotoscloud.chrotos.net', 'v1',
            'servers', 'gamemodes', GAMEMODE);
    } catch (e) {
        throw new Error('Game Mode could not be resolved!');
    }

    if (!gameMode) {
        throw new Error('Game Mode could not be resolved!');
    }

    const SOFTWARE = process.env.SERVER_SOFTWARE;
    const VERSIONS_URL = BASE_URL + SOFTWARE + '/versions';
    const VERSION = (SOFTWARE === 'paper') ? gameMode.body.spec.version : process.env.SERVER_SOFTWARE_VERSION;

    console.log('Fetching newest Build of', SOFTWARE, 'of version', VERSION);

    let versionResponse = await fetch(`${VERSIONS_URL}/${VERSION}`)
    let json = await versionResponse.json();
    let newestBuild = json.builds[json.builds.length - 1];

    console.log('Starting download of build', newestBuild);
    await download(`${VERSIONS_URL}/${VERSION}/builds/${newestBuild}/downloads/${SOFTWARE}-${VERSION}-${newestBuild}.jar`, PATH);

    // Download ChrotosCloud-V2 implementation (TODO: implement)
    //let cloudUrl = await resolveMaven(CLOUD_BASE_URL, 'net.chrotos.chrotoscloud-v2', SOFTWARE, gameMode.body.cloudVersion);
    //await download(cloudUrl, PATH + '/plugins');
    
    fs.writeFileSync(PATH + '/eula.txt', 'eula=true');
})();