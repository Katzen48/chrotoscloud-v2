const download = require('./download');

module.exports = async function (worldDirectory, gameMode, repoUrl, repoUser, repoPassword) {
    if (!gameMode.body.spec.maps) {
        return;
    }

    let maps = gameMode.body.spec.maps.pool;

    if (!maps || maps.length < 1) {
        return;
    }

    let mapsToDownload = [];

    // Random
    if (gameMode.body.spec.maps['random'] == true) {
        let selection = maps.filter(map => map.required == false);

        if (selection.length > 0) {
            mapsToDownload.push(selection[Math.floor(Math.random() * selection.length)])
        }
    }

    // Required
    let selection = maps.filter(map => map.required == true);
    if (selection.length > 0) {
        mapsToDownload = mapsToDownload.concat(selection);
    }

    if (mapsToDownload.length < 1) {
        return;
    }

    let mapNames = [];
    for (let i = 0; i < mapsToDownload.length; i++) {
        let mapName = mapsToDownload[i].name.toString();

        if (mapNames.includes(mapName)) {
            throw new Error('Map "' + mapName + '" is defined multiple times')
        }

        mapNames.push(mapName);
    }
    console.log('Maps to download:', mapNames.join(','));

    let mapCheck = mapsToDownload.filter(map => map.name == 'world');
    if (mapCheck.length < 1) {
        throw new Error('At least one map to download has to be named "world"');
    }

    for (let i = 0; i < mapsToDownload.length; i++) {
        let map = mapsToDownload[i];
        let url;
        let fileName = map.name;
        let user = null;
        let password = null;
        if (map.dependency.mavenLike) {
            let groupId = plugin.dependency.mavenLike.groupId;
            let artifactId = plugin.dependency.mavenLike.artifactId;
            let version = plugin.dependency.mavenLike.version;

            console.log('GroupId: %s, ArtifactId: %s, Version: %s', groupId, artifactId, version);

            url = repoUrl + '/' + groupId.replaceAll('.', '/');
            url += '/' + artifactId;
            url += '/' + version;

            user = repoUser;
            password = repoPassword;
        } else if (map.dependency.url) {
            url = map.dependency.url;
        }

        if (!url) {
            throw new Error('No World URL found for entry ' + i);
        }

        console.log('World', fileName, 'has URL:', url);

        await download(url, worldDirectory + '/' + fileName, user, password);
    }
};