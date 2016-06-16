(function() {
    'use strict';
    angular
        .module('soundxtream3App')
        .factory('Chat', Chat);

    Chat.$inject = ['$resource'];

    function Chat ($resource) {
        var resourceUrl =  'api/chats/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
