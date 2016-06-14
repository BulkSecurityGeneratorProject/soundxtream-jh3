(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .factory('User_activitySearch', User_activitySearch);

    User_activitySearch.$inject = ['$resource'];

    function User_activitySearch($resource) {
        var resourceUrl =  'api/_search/user-activities/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
