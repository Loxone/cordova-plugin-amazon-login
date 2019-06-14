#!/usr/bin/env node
'use strict';

var fs = require('fs-extra');
var configFilePath = './amazon-login.config.json';

module.exports = function () {

    if (!fs.existsSync(configFilePath)) {
        throw new Error('Missing LWA configuration file: ' + configFilePath);
    }

    return JSON.parse(fs.readFileSync(configFilePath, 'utf8'));
};