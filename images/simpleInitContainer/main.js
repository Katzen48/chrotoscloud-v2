// Fetch newest paper version

const BASE_URL = 'https://papermc.io/api/v2/projects/';
const CLOUD_BASE_URL = 'https://maven.pkg.github.com/katzen48/chrotoscloud-v2'
const PATH = '/workdir'
const PropertiesReader = require('properties-reader');
const fetch = require('cross-fetch')
const fs = require('fs');
const k8s = require('@kubernetes/client-node');
const download = require('./download');
const resolveMaven = require('./resolveMaven');

await (async () => {
    const SOFTWARE = process.env.SERVER_SOFTWARE;
    let gameMode;

    if (SOFTWARE === 'paper') {
        const properties = PropertiesReader('/etc/podinfo/labels');
        const GAMEMODE = properties.get("net.chrotos.chrotoscloud.gameserver/gamemode").replaceAll('"', '');

        if (!GAMEMODE || GAMEMODE == '') {
            throw new Error('Game Mode was not defined!')
        }

        const kc = new k8s.KubeConfig();
        kc.loadFromDefault();
        const k8sApi = kc.makeApiClient(k8s.CustomObjectsApi);

        try {
            gameMode = await k8sApi.getNamespacedCustomObject('chrotoscloud.chrotos.net', 'v1',
                'servers', 'gamemodes', GAMEMODE);
        } catch (e) {
            throw new Error('Game Mode could not be resolved!');
        }

        if (!gameMode) {
            throw new Error('Game Mode could not be resolved!');
        }
    }

    const VERSIONS_URL = BASE_URL + SOFTWARE + '/versions';
    const VERSION = (SOFTWARE === 'paper') ? gameMode.body.spec.version : process.env.SERVER_SOFTWARE_VERSION;
    const CLOUD_VERSION = (SOFTWARE === 'paper') ? gameMode.body.spec.cloudVersion : process.env.CLOUD_VERSION;

    console.log('Fetching newest Build of', SOFTWARE, 'of version', VERSION);

    let versionResponse = await fetch(`${VERSIONS_URL}/${VERSION}`)
    let json = await versionResponse.json();
    let newestBuild = json.builds[json.builds.length - 1];

    console.log('Starting download of build', newestBuild);
    await download(`${VERSIONS_URL}/${VERSION}/builds/${newestBuild}/downloads/${SOFTWARE}-${VERSION}-${newestBuild}.jar`, PATH + '/server.jar');

    // Download ChrotosCloud-V2 implementation
    try {
        let cloudUrl = await resolveMaven(CLOUD_BASE_URL, 'net.chrotos.chrotoscloud',
            SOFTWARE, CLOUD_VERSION, process.env.GITHUB_USER,  process.env.GITHUB_TOKEN);

        await download(cloudUrl, PATH + '/plugins/chrotoscloud.jar', process.env.GITHUB_USER, process.env.GITHUB_TOKEN);
    } catch (e) {
        console.error('Could not download chrotoscloud: ' + e);
        process.exit(1);
    }
    
    fs.writeFileSync(PATH + '/eula.txt', 'eula=true');
})();