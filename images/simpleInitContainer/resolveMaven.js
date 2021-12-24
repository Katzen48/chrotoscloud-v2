const fetch = require('cross-fetch')
const { XMLParser } = require("fast-xml-parser");
const parser = new XMLParser({ignoreAttributes: true, preserveOrder: true});

module.exports = async function(repository, groupId, artifactId, version, user, password) {
    await new Promise((resolve, reject) => {
        let artifactUrl = repository + '/' + groupId.replaceAll('.', '/') + '/' + artifactId + '/' + version;

        let metadataUrl = artifactUrl + '/maven-metadata.xml';
        console.log('Requesting maven-metadata from\'' + metadataUrl + '\'');
        let promise;
        if (user && password) {
            let headers = new fetch.Headers();
            headers.append('Authorization',
                            'Basic ' + Buffer.from(user + ':' + password).toString('base64'));

            promise = fetch(metadataUrl, {method: 'GET', headers});
        } else {
            promise = fetch(metadataUrl);
        }

        promise.then(response => response.text()).then(xmlContent => {
            console.log('Parsing metadata');
            let pomResponse = parser.parse(xmlContent);
            let metadata = pomResponse[0].metadata[0];

            let versionString = 'core-';
            if (version.endsWith('-SNAPSHOT')) {
                let versioning = metadata.versioning;
                let snapshot = versioning.snapshot;
                versionString += version.substr(0, version.lastIndexOf('-SNAPSHOT') + 2);
                console.log(snapshot.timestamp[0]);
                versionString += snapshot.timestamp[0] + '-' +
                    snapshot.buildNumber[0];
            } else {
                versionString += metadata.release[0];
            }

            versionString += '-all.jar';

            console.log('Version String:', versionString);
            resolve(artifactUrl + '/' + versionString);
        }).catch(error => reject(error));
    });
}