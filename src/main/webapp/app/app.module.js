(function() {
    'use strict';

    angular
        .module('soundxtream3App', [
            'ngStorage', 
            'tmh.dynamicLocale',
            'pascalprecht.translate', 
            'ngResource',
            'ngCookies',
            'ngAria',
            'ngCacheBuster',
            'ngFileUpload',
            'ui.bootstrap',
            'ui.bootstrap.datetimepicker',
            'ui.router',
            'infinite-scroll',
            'pubnub.angular.service',
            // jhipster-needle-angularjs-add-module JHipster will add new module here
            'angular-loading-bar'
        ])
        .run(run);

    run.$inject = ['$rootScope', 'stateHandler', 'translationHandler', 'Pubnub'];

    function run($rootScope, stateHandler, translationHandler, Pubnub) {
        stateHandler.initialize();
        translationHandler.initialize();

        $rootScope.uuid = _.random(1000000).toString();

        Pubnub.init({
            publish_key: 'pub-c-a1cd7ac1-585e-478e-925b-65d17ce62f7d',
            subscribe_key: 'sub-c-204f063e-c559-11e5-b764-02ee2ddab7fe',
            ssl: true,
            uuid: $rootScope.uuid
        });
    }
})();
