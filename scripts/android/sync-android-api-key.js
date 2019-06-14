#!/usr/bin/env node
'use strict';

var fs = require('fs-extra');
var getConfig = require('../get-lwa-config');

var apiKeyFilePath = 'platforms/android/app/src/main/assets/api_key.txt';
var target = process.env.TARGET || 'debug';
var config = getConfig();

fs.writeFileSync(apiKeyFilePath, config.android[target].api_key, 'utf8');