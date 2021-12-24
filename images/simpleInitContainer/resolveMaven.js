const fetch = require('cross-fetch')
const parser = require('pom-parser');

module.exports = async function(repository, groupId, artifactId, version, user, password) {
    await new Promise((resolve, reject) => {
        let options = {};

        if (user && password) {
            options.headers = {
                'Authorization': 'Basic ' + Buffer.from(user + ':' + password).toString('base64')
            }
        }
        let artifactUrl = repository + '/' + groupId.replaceAll('.', '/') + '/' + artifactId + '/' + version;
        console.log('Requesting maven-metadata from\'', artifactUrl, '\'');

        let metadataUrl = artifactUrl + '/maven-metadata.xml';
        let promise;
        if (user && password) {
            let headers = new Headers();
            headers.append('Authorization',
                            'Basic ' + Buffer.from(user + ':' + password).toString('base64'));

            promise = fetch(metadataUrl, {method: 'GET', headers});
        } else {
            promise = fetch(metadataUrl);
        }

        promise.then(xmlContent => {
            console.log('Parsing metadata')
            parser.parse({
                xmlContent
            }, function (err, pomResponse) {
                if (err) {
                    console.log("ERROR: " + err);
                    reject(err);
                }

                let versionString = 'core-';
                if (version.endsWith('-SNAPSHOT')) {
                    versionString += version.substr(0, version.lastIndexOf('-SNAPSHOT') + 2);
                    versionString += pomResponse.pomObject.versioning.snapshot.timestamp + '-' +
                        pomResponse.pomObject.versioning.snapshot.buildNumber;
                } else {
                    versionString += pomResponse.pomObject.release;
                }

                versionString += '-all.jar';

                console.log('Version String:', versionString);
                resolve(artifactUrl + '/' + versionString);
            })
        }).catch(error => reject(error));
    });
}