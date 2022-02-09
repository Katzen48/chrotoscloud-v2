const fetch = require('cross-fetch')
const { XMLParser } = require("fast-xml-parser");
const parser = new XMLParser({preserveOrder: true, parseAttributeValue: false, parseTagValue: false});

module.exports = async function(repository, groupId, artifactId, version, user, password) {
        let artifactUrl = repository + '/' + groupId.replaceAll('.', '/') + '/' + artifactId + '/' + version;

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
        let metadata = pomResponse[0].metadata;
        let versioning = metadata[3].versioning;

        let versionString = artifactId + '-';
        if (version.endsWith('-SNAPSHOT')) {
            let snapshot = versioning[0].snapshot;
            versionString += version.substr(0, version.lastIndexOf('-SNAPSHOT') + 1);
            versionString += snapshot[0].timestamp[0]['#text'] + '-' +
                snapshot[1].buildNumber[0]['#text'];
        } else {
            versionString += metadata.release[0];
        }

        console.log('Version String:', versionString);

        return artifactUrl + '/' + versionString;
}