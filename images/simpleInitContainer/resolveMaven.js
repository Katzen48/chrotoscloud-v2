const fetch = require('cross-fetch')
const { XMLParser } = require("fast-xml-parser");
const parser = new XMLParser({ parseTagValue: false });

module.exports = async function(repository, groupId, artifactId, version, user, password) {
        let artifactUrl = repository + '/' + groupId.replaceAll('.', '/') + '/' + artifactId + '/' + version;

        let versionString = artifactId + '-';
        if (version.endsWith('-SNAPSHOT')) {
            let metadataUrl = artifactUrl + '/maven-metadata.xml';
            console.log('Requesting maven-metadata from \'' + metadataUrl + '\'');
            let promise;
            if (user && password) {
                let headers = new fetch.Headers();
                headers.append('Authorization',
                    'Basic ' + Buffer.from(user + ':' + password).toString('base64'));

                promise = fetch(metadataUrl, {method: 'GET', headers});
            } else {
                promise = fetch(metadataUrl);
            }

            let response = await promise;
            let xmlContent = await response.text();

            console.log('Parsing metadata');
            let pomResponse = parser.parse(xmlContent);
            let metadata = pomResponse.metadata;
            let versioning = metadata.versioning;

            let snapshot = versioning.snapshot;

            if (!snapshot) {
                versionString += version;
            } else {
                versionString += version.substr(0, version.lastIndexOf('-SNAPSHOT') + 1);
                versionString += snapshot.timestamp + '-' +
                    snapshot.buildNumber;
            }
        } else {
            versionString += version;
        }

        console.log('Version String:', versionString);

        return artifactUrl + '/' + versionString;
}