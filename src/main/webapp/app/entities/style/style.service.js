(function() {
    'use strict';
    angular
        .module('soundxtream3App')
        .factory('Style', Style);

    Style.$inject = ['$resource'];

    function Style ($resource) {
        var resourceUrl =  'api/styles/:id';

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
