#!/usr/bin/env node
'use strict';

var fs = require('fs-extra');
var getConfig = require('../get-lwa-config');

var config = getConfig();
var target = process.env.TARGET || 'debug';
var apiKey = config.android[target].api_key;
var apiKeyFilePath = 'platforms/android/app/src/main/assets/api_key.txt';

console.log('amazon login plugin copying ' + target + ' key to ' + apiKeyFilePath);
fs.writeFileSync(apiKeyFilePath, apiKey, 'utf8');