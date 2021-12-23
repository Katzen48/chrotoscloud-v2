const https = require('https');
const parser = require('pom-parser');

module.exports = async function (repository, groupId, artifactId, version, user, password) {
    await new Promise((resolve, reject) => {
        let options = {};

        if (user && password) {
            options.headers = {
                'Authorization': 'Basic ' + new Buffer(user + ':' + password).toString('base64')
            }
        }
        let artifactUrl = repository + '/' + groupId.replaceAll('.', '/') + '/' + artifactId + '/' + version;

        let xmlContent = '';
        https.get(artifactUrl + '/maven-metadata.xml', options, (res) => {
            res.on('data', chunk => xmlContent += chunk);
            res.on('finish', function () {
                parser.parse({
                    xmlContent
                }, function (err, pomResponse) {
                    if (err) {
                        console.log("ERROR: " + err);
                        process.exit(1);
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

                    resolve(artifactUrl + '/' + versionString);
                })
            });
        })
    });
}