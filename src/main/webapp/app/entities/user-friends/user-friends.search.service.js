(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .factory('User_friendsSearch', User_friendsSearch);

    User_friendsSearch.$inject = ['$resource'];

    function User_friendsSearch($resource) {
        var resourceUrl =  'api/_search/user-friends/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
