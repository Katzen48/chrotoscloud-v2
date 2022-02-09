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
const downloadPlugins = require('./pluginDownloader.js');
const downloadWorlds = require('./worldDownloader.js');

(async () => {
    const SOFTWARE = process.env.SERVER_SOFTWARE;
    const MAVEN_URL = process.env.MAVEN_URL;
    const MAVEN_USER = process.env.MAVEN_USER;
    const MAVEN_PASSWORD = process.env.MAVEN_PASSWORD;
    const WORLD_REPO_URL = process.env.WORLD_REPO_URL;
    const WORLD_REPO_USER = process.env.WORLD_REPO_USER;
    const WORLD_REPO_PASSWORD = process.env.WORLD_REPO_PASSWORD;

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

    console.log('Fetching newest Build of', SOFTWARE, 'of version', VERSION); // Maybe remove and include in image?

    let versionResponse = await fetch(`${VERSIONS_URL}/${VERSION}`)
    let json = await versionResponse.json();
    let newestBuild = json.builds[json.builds.length - 1];

    console.log('Starting download of build', newestBuild);
    await download(`${VERSIONS_URL}/${VERSION}/builds/${newestBuild}/downloads/${SOFTWARE}-${VERSION}-${newestBuild}.jar`, PATH + '/server.jar');

    // Download ChrotosCloud-V2 implementation
    try {
        console.log('Resolving newest build of ChrotosCloud-Plugin');
        let cloudUrl = await resolveMaven(CLOUD_BASE_URL, 'net.chrotos.chrotoscloud',
            SOFTWARE, CLOUD_VERSION, process.env.GITHUB_USER,  process.env.GITHUB_TOKEN);
        cloudUrl += '-all.jar';

        console.log('Found newest build url:', cloudUrl);

        await download(cloudUrl, PATH + '/plugins/chrotoscloud.jar', process.env.GITHUB_USER, process.env.GITHUB_TOKEN);
    } catch (e) {
        console.error('Could not download chrotoscloud: ' + e);
        process.exit(1);
    }

    // Download Plugins
    try {
        if (gameMode) {
            console.log('Starting download of plugins');
            await downloadPlugins(PATH + '/plugins', gameMode, MAVEN_URL, MAVEN_USER, MAVEN_PASSWORD);
        }
    } catch (e) {
        console.error('Could not download plugins: ' + e);
        process.exit(1);
    }

    // Download Worlds
    try {
        if (gameMode) {
            console.log('Starting download of worlds');
            await downloadWorlds(PATH + '/worlds', gameMode, WORLD_REPO_URL, WORLD_REPO_USER, WORLD_REPO_PASSWORD);
        }
    } catch (e) {
        console.error('Could not download worlds: ' + e);
        process.exit(1);
    }

    // Set EULA
    fs.writeFileSync(PATH + '/eula.txt', 'eula=true');
})();