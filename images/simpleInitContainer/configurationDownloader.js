const resolveMaven = require('./resolveMaven');
const download = require('./download');
const ZipStream = require('node-stream-zip');
const YAML = require('js-yaml');

module.exports = async function(pluginDirectory, pluginFileName, pluginConfiguration, mavenUrl, mavenUser, mavenPassword) {
    let pluginPath = pluginDirectory + '/' + pluginFileName;
    let pluginFile = new ZipStream.async({file: pluginPath});
    let pluginYml = await pluginFile.entryData('plugin.yml');
    await pluginFile.close();

    let pluginName = YAML.load(pluginYml.toString('utf8')).name;

    let url;
    let fileName = pluginName + '.zip';
    let user = null;
    let password = null;
    if (pluginConfiguration.mavenLike) {
        let groupId = pluginConfiguration.mavenLike.groupId;
        let artifactId = pluginConfiguration.mavenLike.artifactId;
        let version = pluginConfiguration.mavenLike.version;

        console.log('GroupId: %s, ArtifactId: %s, Version: %s', groupId, artifactId, version);

        let groupPath = groupId.replaceAll('.', '/');
        url = `${mavenUrl}/${groupPath}/${artifactId}/${version}/${artifactId}-${version}.zip`
        user = mavenUser;
        password = mavenPassword;
    } else if (pluginConfiguration.url) {
        url = pluginConfiguration.url;
    }

    if (!url) {
        throw new Error('No Configuration URL found');
    }

    console.log('Found Configuration URL: ' + url);

    await download(url, pluginDirectory + '/' + fileName, user, password);
}