var fs = require('fs-extra');
var pkgData = require('../package.json');

var encoding = 'utf8';
var pluginXmlPath = './plugin.xml';
var pluginVersionRegex = /(<plugin[^>]+version=")(\S*)(">)/;

var pluginContent = fs.readFileSync(pluginXmlPath, encoding);
var pluginXmlResult = pluginContent.replace(pluginVersionRegex, '$1' + pkgData.version + '$3');

fs.writeFileSync(pluginXmlPath, pluginXmlResult, encoding);
