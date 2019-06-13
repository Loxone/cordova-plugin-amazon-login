#!/usr/bin/env node
'use strict';
var fs = require('fs-extra');

var target = process.env.TARGET || 'debug';
var apiKeyFilePath = 'platforms/android/app/src/main/assets/api_key.txt';
var configobj = JSON.parse(fs.readFileSync('config/project.json', 'utf8'));

fs.writeFileSync(apiKeyFilePath, configobj[target].AMAZON_API_KEY, 'utf8');