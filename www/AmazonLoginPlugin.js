var exec = require('cordova/exec');
var PLUGIN_NAME = 'AmazonLoginPlugin';

var AmazonLoginPlugin = {
  authorizeAVS: function (options, successCallback, errorCallback) {
    exec(successCallback, errorCallback, PLUGIN_NAME, 'authorizeAVS', [options]);
  },
};

module.exports = AmazonLoginPlugin;