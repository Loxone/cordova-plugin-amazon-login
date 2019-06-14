#!/usr/bin/env node
'use strict';

var fs = require('fs-extra');
var glob = require('glob');
var plist = require('plist');
var path = require('path');
var getConfig = require('../get-lwa-config');

var cwd = process.cwd();
var config = getConfig();
var paths = glob.sync('platforms/ios/**/*-Info.plist') || [];

var encoding = 'utf8';
var iosApiKey = (config.ios.api_key + '');

paths.forEach(function (relativePath) {

    var absPath = path.resolve(cwd, relativePath);
    var rawContent = fs.readFileSync(absPath, encoding);
    var plistContent = plist.parse(rawContent);
    console.log('amazon login plugin updating plist file: ' + absPath);

    plistContent['APIKey'] = iosApiKey;
    fs.writeFileSync(absPath, plist.build(plistContent), encoding);
});