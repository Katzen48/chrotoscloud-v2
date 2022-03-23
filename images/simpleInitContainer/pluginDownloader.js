const resolveMaven = require('./resolveMaven');
const download = require('./download');
const configDownload = require('./configurationDownloader');

module.exports = async function (pluginDirectory, gameMode, mavenUrl, mavenUser, mavenPassword) {
    let plugins = gameMode.body.spec.plugins;

    if (!plugins || plugins.length < 1) {
        return;
    }

    for (let i = 0; i < plugins.length; i++) {
        let plugin = plugins[i];
        let url;
        let fileName;
        let user = null;
        let password = null;
        if (plugin.dependency.maven) {
            let groupId = plugin.dependency.maven.groupId;
            let artifactId = plugin.dependency.maven.artifactId;
            let version = plugin.dependency.maven.version;

            console.log('GroupId: %s, ArtifactId: %s, Version: %s', groupId, artifactId, version);

            url = await resolveMaven(mavenUrl, groupId, artifactId, version, mavenUser, mavenPassword);
            url += '.jar'
            fileName = `${groupId}.${artifactId}.${version}.jar`;
            user = mavenUser;
            password = mavenPassword;
        } else if (plugin.dependency.url) {
            url = plugin.dependency.url;
            fileName = url.substring(url.lastIndexOf('/') + 1);
        }

        if (!url) {
            throw new Error('No Plugin URL found for entry ' + i);
        }

        console.log('Found Plugin URL: ' + url);

        await download(url, pluginDirectory + '/' + fileName, user, password);

        if (plugin.configuration) {
            console.log("Downloading Configuration");

            await configDownload(pluginDirectory, fileName, plugin.configuration, mavenUrl, mavenUser, mavenPassword);
        }
    }
};